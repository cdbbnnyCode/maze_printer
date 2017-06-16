package maze_printer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class ImageSizeDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private boolean success = false;
	private static Dimension val;
	private JSpinner h_spinner;
	private JSpinner w_spinner;

	/**
	 * Launch the application.
	 */
	public static boolean run() {
		try {
			ImageSizeDialog dialog = new ImageSizeDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setModalityType(ModalityType.APPLICATION_MODAL);
			dialog.setVisible(true);
			return dialog.success;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static Dimension getOutput() {
		return val;
	}

	/**
	 * Create the dialog.
	 */
	public ImageSizeDialog() {
		setTitle("Select Image Size");
		setBounds(100, 100, 335, 159);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{32, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{14, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblWidth = new JLabel("Width:");
			GridBagConstraints gbc_lblWidth = new GridBagConstraints();
			gbc_lblWidth.insets = new Insets(0, 0, 5, 5);
			gbc_lblWidth.anchor = GridBagConstraints.NORTHWEST;
			gbc_lblWidth.gridx = 0;
			gbc_lblWidth.gridy = 0;
			contentPanel.add(lblWidth, gbc_lblWidth);
		}
		{
			w_spinner = new JSpinner();
			w_spinner.setModel(new SpinnerNumberModel(new Integer(800), new Integer(8), null, new Integer(10)));
			GridBagConstraints gbc_w_spinner = new GridBagConstraints();
			gbc_w_spinner.fill = GridBagConstraints.HORIZONTAL;
			gbc_w_spinner.insets = new Insets(0, 0, 5, 0);
			gbc_w_spinner.gridx = 1;
			gbc_w_spinner.gridy = 0;
			contentPanel.add(w_spinner, gbc_w_spinner);
		}
		{
			JLabel lblHeight = new JLabel("Height:");
			GridBagConstraints gbc_lblHeight = new GridBagConstraints();
			gbc_lblHeight.insets = new Insets(0, 0, 0, 5);
			gbc_lblHeight.gridx = 0;
			gbc_lblHeight.gridy = 1;
			contentPanel.add(lblHeight, gbc_lblHeight);
		}
		{
			h_spinner = new JSpinner();
			h_spinner.setModel(new SpinnerNumberModel(new Integer(600), new Integer(8), null, new Integer(10)));
			GridBagConstraints gbc_h_spinner = new GridBagConstraints();
			gbc_h_spinner.fill = GridBagConstraints.HORIZONTAL;
			gbc_h_spinner.gridx = 1;
			gbc_h_spinner.gridy = 1;
			contentPanel.add(h_spinner, gbc_h_spinner);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						success = true;
						val = new Dimension((int)w_spinner.getValue(), (int)h_spinner.getValue());
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
