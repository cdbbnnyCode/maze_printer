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

package maze_printer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import maze_printer.export.Print;
import maze_printer.export.SchematicExport;
import maze_printer.util.AboutDialog;

@SuppressWarnings("serial")
public class MazePrinter extends JFrame {

	private JPanel contentPane;
	private final ButtonGroup mazeType = new ButtonGroup();
	private PreviewPanel preview;
	private BufferedImage img;
	private JCheckBox slow;
	private JSpinner spinner;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MazePrinter frame = new MazePrinter();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MazePrinter() {
		try {
			String[] sizes = {
				"16", "32", "48", "64", "128", "256"
			};
			List<BufferedImage> imgs = new ArrayList<>();
			for (String sz : sizes) {
				imgs.add(ImageIO.read(MazePrinter.class.getResourceAsStream("/img/icon_"+sz+".png")));
			}
			setIconImages(imgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		setTitle("Maze Generator/Printer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 793, 614);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmSaveAsImage = new JMenuItem("Save as image...");
		mntmSaveAsImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refresh();
				JFileChooser jfc = new JFileChooser(System.getProperty("user.home"));
				jfc.showSaveDialog(MazePrinter.this);
				File res = jfc.getSelectedFile();
				if (res == null || res.isDirectory()) return;
				if (!ImageSizeDialog.run()) return;
				Dimension size = ImageSizeDialog.getOutput();
				BufferedImage img = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
				preview.setSlow(false);
				preview.updateTo(img);
				if (noExt(res))
					writeAs("png", new File(res.getAbsolutePath() + ".png"), img);
				else {
					String ext = ext(res);
					String[] names = ImageIO.getWriterFormatNames();
					boolean success = false;
					for (String s : names) {
						if (s.equals(ext)) {
							success = true;
							break;
						}
					}
					if (success) {
						writeAs(ext, res, img);
					} else {
						writeAs("png", new File(res.getAbsolutePath() + ".png"), img);
					}
				}
			}
			private void writeAs(String format, File f, BufferedImage img) {
				System.out.println("Writing image...");
				try {
					ImageIO.write(img, format, f);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("Done writing image");
			}
			private boolean noExt(File f) {
				return (f.getName().indexOf('.') < 0);
			}
			private String ext(File f) {
				return f.getName().substring(f.getName().lastIndexOf('.')+1);
			}
		});
		mnFile.add(mntmSaveAsImage);
		
		JMenuItem mntmSaveAsSchematic = new JMenuItem("Save as MCEdit schematic...");
		mntmSaveAsSchematic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refresh();
				SchematicExport.export(preview);
			}
		});
		mnFile.add(mntmSaveAsSchematic);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnFile.add(mntmExit);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AboutDialog.run(MazePrinter.this);
			}
			
		});
		mnHelp.add(mntmAbout);
		
		contentPane = (JPanel) getContentPane();
		
		JPanel header = new JPanel();
		contentPane.add(header, BorderLayout.NORTH);
		GridBagLayout gbl_header = new GridBagLayout();
		gbl_header.columnWidths = new int[]{56, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_header.rowHeights = new int[]{14, 0};
		gbl_header.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_header.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		header.setLayout(gbl_header);
		
		JLabel lblMazeType = new JLabel("Maze Type:");
		GridBagConstraints gbc_lblMazeType = new GridBagConstraints();
		gbc_lblMazeType.insets = new Insets(0, 0, 0, 5);
		gbc_lblMazeType.anchor = GridBagConstraints.WEST;
		gbc_lblMazeType.gridx = 0;
		gbc_lblMazeType.gridy = 0;
		header.add(lblMazeType, gbc_lblMazeType);
		
		JRadioButton rect_opt = new JRadioButton("Rectangle");
		rect_opt.setSelected(true);
		rect_opt.setActionCommand("use rect");
		mazeType.add(rect_opt);
		GridBagConstraints gbc_rect_opt = new GridBagConstraints();
		gbc_rect_opt.insets = new Insets(0, 0, 0, 5);
		gbc_rect_opt.gridx = 1;
		gbc_rect_opt.gridy = 0;
		header.add(rect_opt, gbc_rect_opt);
		
		JRadioButton circular_opt = new JRadioButton("Circle");
		circular_opt.setEnabled(false);
		circular_opt.setActionCommand("use circle");
		mazeType.add(circular_opt);
		GridBagConstraints gbc_circular_opt = new GridBagConstraints();
		gbc_circular_opt.insets = new Insets(0, 0, 0, 5);
		gbc_circular_opt.gridx = 2;
		gbc_circular_opt.gridy = 0;
		header.add(circular_opt, gbc_circular_opt);
		
		JRadioButton image_opt = new JRadioButton("Image");
		image_opt.setEnabled(false);
		image_opt.setActionCommand("use img");
		mazeType.add(image_opt);
		GridBagConstraints gbc_image_opt = new GridBagConstraints();
		gbc_image_opt.insets = new Insets(0, 0, 0, 5);
		gbc_image_opt.gridx = 3;
		gbc_image_opt.gridy = 0;
		header.add(image_opt, gbc_image_opt);
		
		JButton btnSetImage = new JButton("Set Image...");
		btnSetImage.setEnabled(false);
		btnSetImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser(System.getProperty("user.home"));
				jfc.setFileFilter(new FileFilter() {

					@Override
					public boolean accept(File f) {
						String ext = ext(f);
						return f.isDirectory() || ext.equals("png") || ext.equals("gif");
					}

					@Override
					public String getDescription() {
						return "Images (PNG/GIF)";
					}
					
				});
				jfc.showOpenDialog(MazePrinter.this);
				File res = jfc.getSelectedFile();
				if (res == null) return;
				
				try {
					img = ImageIO.read(res);
				} catch (IOException e1) {}
			}
			private String ext(File f) {
				return f.getName().substring(f.getName().lastIndexOf('.')+1);
			}
		});
		GridBagConstraints gbc_btnSetImage = new GridBagConstraints();
		gbc_btnSetImage.insets = new Insets(0, 0, 0, 5);
		gbc_btnSetImage.gridx = 4;
		gbc_btnSetImage.gridy = 0;
		header.add(btnSetImage, gbc_btnSetImage);
		
		JLabel lblPathSizepx = new JLabel("Path Size (px)");
		GridBagConstraints gbc_lblPathSizepx = new GridBagConstraints();
		gbc_lblPathSizepx.insets = new Insets(0, 0, 0, 5);
		gbc_lblPathSizepx.gridx = 5;
		gbc_lblPathSizepx.gridy = 0;
		header.add(lblPathSizepx, gbc_lblPathSizepx);
		
		spinner = new JSpinner();
		spinner.addChangeListener(new ChangeListener() {
			private int lastValue = 10;
			public void stateChanged(ChangeEvent e) {
				preview.path_size = (int) spinner.getValue();
				preview.calc_pathsize();
				if ((int)spinner.getValue() > lastValue) {
					while (preview.path_size < (int)spinner.getValue()) {
						spinner.setValue(((int)spinner.getValue())+1);
						preview.path_size = (int)spinner.getValue();
						preview.calc_pathsize();
					}
				} else {
					spinner.setValue(preview.path_size);
				}
				if ((int)spinner.getValue() < 4 || (int)spinner.getValue() > 100)
					spinner.setValue(lastValue);
				lastValue = (int) spinner.getValue();
			}
		});
		spinner.setModel(new SpinnerNumberModel(18, 4, 100, 1));
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.insets = new Insets(0, 0, 0, 5);
		gbc_spinner.gridx = 6;
		gbc_spinner.gridy = 0;
		header.add(spinner, gbc_spinner);
		
		slow = new JCheckBox("Slow Generation");
		GridBagConstraints gbc_slow = new GridBagConstraints();
		gbc_slow.gridx = 7;
		gbc_slow.gridy = 0;
		header.add(slow, gbc_slow);
		
		JPanel center = new JPanel();
		center.setBorder(new TitledBorder(null, "Preview", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(center, BorderLayout.CENTER);
		GridBagLayout gbl_center = new GridBagLayout();
		gbl_center.columnWidths = new int[]{0, 0};
		gbl_center.rowHeights = new int[]{0, 0};
		gbl_center.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_center.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		center.setLayout(gbl_center);
		
		preview = new PreviewPanel();
		GridBagConstraints gbc_preview = new GridBagConstraints();
		gbc_preview.fill = GridBagConstraints.BOTH;
		gbc_preview.gridx = 0;
		gbc_preview.gridy = 0;
		center.add(preview, gbc_preview);
		
		JPanel footer = new JPanel();
		contentPane.add(footer, BorderLayout.SOUTH);
		GridBagLayout gbl_footer = new GridBagLayout();
		gbl_footer.columnWidths = new int[]{695, 67, 0};
		gbl_footer.rowHeights = new int[]{23, 0};
		gbl_footer.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_footer.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		footer.setLayout(gbl_footer);
		
		JButton btnRefreshPreview = new JButton("Refresh Preview");
		btnRefreshPreview.addActionListener(new ActionListener() {
			private boolean doCancel;
			public void actionPerformed(ActionEvent e) {
				if (doCancel) {
					preview.cancel();
					spinner.setEnabled(true);
					doCancel = false;
					btnRefreshPreview.setText("Refresh Preview");
				} else {
					refresh();
					doCancel = true;
					btnRefreshPreview.setText("Cancel");
				}
			}
		});
		preview.addFinishListener(() -> {
			btnRefreshPreview.doClick();
			spinner.setEnabled(true);
		});
		GridBagConstraints gbc_btnRefreshPreview = new GridBagConstraints();
		gbc_btnRefreshPreview.anchor = GridBagConstraints.EAST;
		gbc_btnRefreshPreview.insets = new Insets(0, 0, 0, 5);
		gbc_btnRefreshPreview.gridx = 0;
		gbc_btnRefreshPreview.gridy = 0;
		footer.add(btnRefreshPreview, gbc_btnRefreshPreview);
		
		JButton btnPrint = new JButton("Print...");
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Print.print(preview);
			}
		});
		GridBagConstraints gbc_btnPrint = new GridBagConstraints();
		gbc_btnPrint.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnPrint.gridx = 1;
		gbc_btnPrint.gridy = 0;
		footer.add(btnPrint, gbc_btnPrint);
	}

	private void refresh() {
		boolean useImg = mazeType.getSelection().getActionCommand().equals("use img");
		boolean circular = mazeType.getSelection().getActionCommand().equals("use circle");
		int path_size = (int) spinner.getValue();
		spinner.setEnabled(false);
		if (useImg && img == null) {
			JOptionPane.showMessageDialog(MazePrinter.this, "Please select an image");
			return;
		}
		preview.path_size = path_size;
		preview.setSlow(slow.isSelected());
		if (useImg) preview.setImage(img);
		else if (circular) preview.circularMode();
		else preview.rectMode();
	}
}
