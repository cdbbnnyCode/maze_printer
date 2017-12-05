/*******************************************************************************
 *        Copyright 2017 cdbbnny
 *
 *        Licensed under the Apache License, Version 2.0 (the "License");
 *        you may not use this file except in compliance with the License.
 *        You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *        Unless required by applicable law or agreed to in writing, software
 *        distributed under the License is distributed on an "AS IS" BASIS,
 *        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *        See the License for the specific language governing permissions and
 *        limitations under the License.
 *******************************************************************************/
package maze_printer.util;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import maze_printer.Version;

@SuppressWarnings("serial")
public class AboutDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void run(Frame parent) {
		try {
			AboutDialog dialog = new AboutDialog(parent);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			if (!(e instanceof NullPointerException ))
				e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public AboutDialog(Frame parent) {
		super(parent);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setBounds(100, 100, 550, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		JFXPanel fxpanel = new JFXPanel();
		Platform.runLater(() -> {
			WebView wv = new WebView();
			fxpanel.setScene(new Scene(wv));
			wv.getEngine().loadContent(readFile("about.html"));
		});
		contentPanel.add(fxpanel, BorderLayout.CENTER);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton licenseButton = new JButton("License...");
				licenseButton.addActionListener((e) -> {
					JDialog d2 = new JDialog(AboutDialog.this);
					d2.setModalityType(ModalityType.APPLICATION_MODAL);
					d2.setSize(400, 200);
					
					JPanel panel = new JPanel();
					panel.setLayout(new BorderLayout());
					d2.setContentPane(panel);
					
					JScrollPane scroll = new JScrollPane();
					panel.add(scroll, BorderLayout.CENTER);
					
					JTextPane text = new JTextPane();
					text.setText(readFile("LICENSE"));
					text.setEditable(false);
					scroll.setViewportView(text);
					
					JButton btn = new JButton("OK");
					btn.addActionListener((e1) -> {
						d2.dispose();
					});
					panel.add(btn, BorderLayout.SOUTH);
					
					text.setCaretPosition(0);
					
					d2.setVisible(true);
				}); 
				buttonPane.add(licenseButton);
			}
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener((e) -> {
					dispose();
				}); 
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}
	
	private String readFile(String loc) {
		try {
			InputStream in = ClassLoader.getSystemResourceAsStream(loc);
			Reader r = new InputStreamReader(in);
			char[] chars = new char[1024];
			int len;
			String out = "";
			while ((len = r.read(chars)) >= 0) {
				out += new String(chars).substring(0, len);
			}
			out = out.replaceAll("%v", Version.VER);
			return out;
		} catch (IOException e) {
			e.printStackTrace();
			return "Error reading file";
		}
	}
}
