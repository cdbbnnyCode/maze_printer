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
package maze_printer.export;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.ListCellRenderer;

import maze_printer.mc.Block;
import maze_printer.mc.BlockIcons;
import maze_printer.mc.BlockList;
import javax.swing.SpinnerNumberModel;

@SuppressWarnings("serial")
public class SchematicOptionsDialog extends JDialog {
	private JComboBox<Block> blockTypeSel;

	/**
	 * Launch the application.
	 */
	public static boolean run() {
		try {
			SchematicOptionsDialog dialog = new SchematicOptionsDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result != null;
	}

	/**
	 * Create the dialog.
	 */
	public SchematicOptionsDialog() {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setBounds(100, 100, 450, 182);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{50, 150, 0};
		gbl_panel.rowHeights = new int[] {0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{1.0, 1.0, 0.0, 0.0};
		panel.setLayout(gbl_panel);
		
		JLabel lblLength = new JLabel("Length");
		GridBagConstraints gbc_lblLength = new GridBagConstraints();
		gbc_lblLength.insets = new Insets(0, 0, 5, 5);
		gbc_lblLength.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblLength.gridx = 0;
		gbc_lblLength.gridy = 0;
		panel.add(lblLength, gbc_lblLength);
		
		JSpinner sx = new JSpinner();
		sx.setModel(new SpinnerNumberModel(new Integer(20), new Integer(1), null, new Integer(1)));
		GridBagConstraints gbc_sx = new GridBagConstraints();
		gbc_sx.fill = GridBagConstraints.HORIZONTAL;
		gbc_sx.insets = new Insets(0, 0, 5, 0);
		gbc_sx.gridx = 1;
		gbc_sx.gridy = 0;
		panel.add(sx, gbc_sx);
		
		JLabel lblWidth = new JLabel("Width");
		GridBagConstraints gbc_lblWidth = new GridBagConstraints();
		gbc_lblWidth.anchor = GridBagConstraints.WEST;
		gbc_lblWidth.insets = new Insets(0, 0, 5, 5);
		gbc_lblWidth.gridx = 0;
		gbc_lblWidth.gridy = 1;
		panel.add(lblWidth, gbc_lblWidth);
		
		JSpinner sz = new JSpinner();
		sz.setModel(new SpinnerNumberModel(new Integer(20), new Integer(1), null, new Integer(1)));
		GridBagConstraints gbc_sz = new GridBagConstraints();
		gbc_sz.fill = GridBagConstraints.HORIZONTAL;
		gbc_sz.insets = new Insets(0, 0, 5, 0);
		gbc_sz.gridx = 1;
		gbc_sz.gridy = 1;
		panel.add(sz, gbc_sz);
		
		JLabel lblHeight = new JLabel("Height");
		GridBagConstraints gbc_lblHeight = new GridBagConstraints();
		gbc_lblHeight.anchor = GridBagConstraints.WEST;
		gbc_lblHeight.insets = new Insets(0, 0, 5, 5);
		gbc_lblHeight.gridx = 0;
		gbc_lblHeight.gridy = 2;
		panel.add(lblHeight, gbc_lblHeight);
		
		JSpinner sy = new JSpinner();
		sy.setModel(new SpinnerNumberModel(new Integer(3), new Integer(1), null, new Integer(1)));
		GridBagConstraints gbc_sy = new GridBagConstraints();
		gbc_sy.fill = GridBagConstraints.HORIZONTAL;
		gbc_sy.insets = new Insets(0, 0, 5, 0);
		gbc_sy.gridx = 1;
		gbc_sy.gridy = 2;
		panel.add(sy, gbc_sy);
		
		JLabel lblBlock = new JLabel("Block type");
		GridBagConstraints gbc_lblBlock = new GridBagConstraints();
		gbc_lblBlock.anchor = GridBagConstraints.EAST;
		gbc_lblBlock.insets = new Insets(0, 0, 0, 5);
		gbc_lblBlock.gridx = 0;
		gbc_lblBlock.gridy = 3;
		panel.add(lblBlock, gbc_lblBlock);
		
		blockTypeSel = new JComboBox<>();
		GridBagConstraints gbc_blockTypeSel = new GridBagConstraints();
		gbc_blockTypeSel.fill = GridBagConstraints.HORIZONTAL;
		gbc_blockTypeSel.gridx = 1;
		gbc_blockTypeSel.gridy = 3;
		panel.add(blockTypeSel, gbc_blockTypeSel);
		
		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		
		JButton btnOk = new JButton("OK");
		panel_1.add(btnOk);
		btnOk.addActionListener((e) -> {
			result = new Result();
			result.x = (int)sx.getValue();
			result.y = (int)sy.getValue();
			result.z = (int)sz.getValue();
			result.block = (Block)blockTypeSel.getSelectedItem();
			dispose();
		});
		
		JButton btnCancel = new JButton("Cancel");
		panel_1.add(btnCancel);
		btnCancel.addActionListener((e) -> {
			result = null;
			dispose();
		});
		populateComboBox();
	}
	
	public static class Result {
		public int x, y, z;
		public Block block;
	}
	
	private static Result result;
	
	public static Result getResult() {
		return result;
	}
	
	private void populateComboBox() {
		blockTypeSel.setRenderer(new ListCellRenderer<Block>() {

			@Override
			public Component getListCellRendererComponent(
					JList<? extends Block> list, Block value, int index,
					boolean isSelected, boolean cellHasFocus) {
				JPanel panel = new JPanel();
				panel.setLayout(new BorderLayout());
				JPanel imagePanel = new JPanel() {
					private BufferedImage img;
					/* Constructor */
					{
						try {
							img = BlockIcons.instance().getImage(value);
						} catch (IOException e) {
							e.printStackTrace();
						}
						setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
						setMinimumSize(getPreferredSize());
					}
					
					@Override
					public void paintComponent(Graphics g) {
						super.paintComponent(g);
						g.drawImage(img, 0, 0, null);
					}
				};
				panel.add(imagePanel, BorderLayout.WEST);
				panel.add(new JLabel(value.getDisplayName()), BorderLayout.EAST);
				return panel;
			}
			
		});
		DefaultComboBoxModel<Block> model = new DefaultComboBoxModel<>();
		blockTypeSel.setModel(model);
		for (Block b : BlockList.instance()) {
			model.addElement(b);
		}
	}
}
