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
package maze_printer.mc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import maze_printer.util.Cache;
import maze_printer.util.StreamUtils;

public class BlockIcons {
	
	private final File zip = new File("icons.zip");
	
	private String tmpdir = System.getProperty("java.io.tmpdir") + "/block_icons";
	private String zipdir = null;
	private Cache<Block, BufferedImage> cache = new Cache<>(10);
	
	private static BlockIcons instance;
	public static BlockIcons instance() {
		if (instance == null)
			try {
				instance = new BlockIcons(new URL("http://minecraft-ids.grahamedgecombe.com/items.zip"),
							BlockList.instance());
			} catch (IOException e) {
				e.printStackTrace();
			}
		return instance;
	}
	
	private BlockIcons(URL url, BlockList list) throws IOException {
		System.out.println("Loading block icons...");
		boolean download = false;
		if (zip.exists()) {
			unzip();
			if (!checkZip(list)) {
				System.out.println("New blocks detected; need to re-download icons");
				download = true;
			}
		} else {
			System.out.println("No block zip found; downloading icons");
			download = true;
		}
		if (download) {
			System.out.println("Downloading icon archive...");
			InputStream in = url.openStream();
			OutputStream out = new FileOutputStream(zip);
			StreamUtils.writeFromStream(in, out);
			in.close();
			out.close();
			unzip();
		}
		System.out.println("Load complete");
	}
	
	private void unzip() throws IOException {
		System.out.println("Unzipping icons...");
		ZipFile zip = new ZipFile(this.zip);
		Enumeration<? extends ZipEntry> entries = zip.entries();
		while (entries.hasMoreElements()) {
			ZipEntry next = entries.nextElement();
			if (!next.isDirectory()) {
				InputStream in = zip.getInputStream(next);
				File f = new File(tmpdir + "/" + next.getName());
				f.getParentFile().mkdirs();
				OutputStream out = new FileOutputStream(f);
				StreamUtils.writeFromStream(in, out);
				out.close();
				in.close();
			}
		}
		zip.close();
		System.out.println("Unzipping complete");
		zipdir = tmpdir;
	}
	
	private boolean checkZip(BlockList list) {
		for (Block b : list) {
			if (!new File(zipdir + "/" + b.getId() + "-0.png").exists()) {
				System.out.println("Unable to find " + zipdir + "/" + b.getId() + "-0.png");
				return false;
			}
		}
		return true;
	}
	
	private BufferedImage cacheImage(Block block) throws IOException {
		File f = new File(zipdir + "/" + block.getId() + "-" + block.getMeta() + ".png");
		if (!f.exists()) {
			f = new File(zipdir + "/" + block.getId() + "-0.png");
		}
		BufferedImage im = ImageIO.read(f);
		cache.put(block, im);
		return im;
	}
	
	public BufferedImage getImage(Block block) throws IOException {
		BufferedImage img;
		if ((img = cache.get(block)) == null) {
			img = cacheImage(block);
		}
		return img;
	}
}
