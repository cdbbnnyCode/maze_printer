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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.flowpowered.nbt.ByteArrayTag;
import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.StringTag;
import com.flowpowered.nbt.stream.NBTOutputStream;

import maze_printer.PreviewPanel;
import maze_printer.export.SchematicOptionsDialog.Result;

public class SchematicExport {
	
	public static void export(PreviewPanel preview) {
		if (SchematicOptionsDialog.run()) {
			JFileChooser jfc = new JFileChooser(System.getProperty("user.home"));
			jfc.showSaveDialog(preview);
			File res = jfc.getSelectedFile();
			if (res == null || res.isDirectory()) return;
			if (!res.getName().endsWith(".schematic"))
				res = new File(res.getAbsolutePath() + ".schematic");
			CompoundMap schematic = new CompoundMap();
			Result r = SchematicOptionsDialog.getResult();
			schematic.put(new IntTag("Width", r.x));
			schematic.put(new IntTag("Height", r.y));
			schematic.put(new IntTag("Length", r.z));
			schematic.put(new StringTag("Materials", "Alpha"));
			byte[] ids = new byte[r.x * r.y * r.z];
			byte[] meta = new byte[r.x * r.y * r.z];
			BufferedImage img = new BufferedImage(r.x, r.z, BufferedImage.TYPE_INT_ARGB);
			preview.updateTo(img);
			for (int x = 0; x < r.x; x++) {
				for (int y = 0; y < r.y; y++) {
					for (int z = 0; z < r.z; z++) {
						int i = (y*r.z+z)*r.x+x;
						if (new Color(img.getRGB(x, z), true).equals(Color.BLACK)) {
							ids[i] = (byte) r.block.getId();
							meta[i] = (byte) r.block.getMeta();
						}
					}
				}
			}
			schematic.put(new ByteArrayTag("Blocks", ids));
			schematic.put(new ByteArrayTag("Data", meta));
			schematic.put(new ListTag<CompoundTag>("Entities", CompoundTag.class, new ArrayList<CompoundTag>()));
			schematic.put(new ListTag<CompoundTag>("TileEntities", CompoundTag.class, new ArrayList<CompoundTag>()));
			CompoundTag tag = new CompoundTag("Schematic", schematic);
			try {
				NBTOutputStream out = new NBTOutputStream(new FileOutputStream(res));
				out.writeTag(tag);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(preview, 
						"Unable to write file (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")",
						"An error has occurred", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
