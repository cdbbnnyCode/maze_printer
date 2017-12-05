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

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

public class BlockList implements Iterable<Block> {
	private Block[][] blocks = new Block[256][16];
	
	private static BlockList instance;
	public static BlockList instance() {
		try {
			if (instance == null) 
				instance = new BlockList(new URL("http://minecraft-ids.grahamedgecombe.com/items.json"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return instance;
	}
	
	private BlockList(URL file) throws IOException {
		JsonStreamParser reader = new JsonStreamParser(new InputStreamReader(file.openStream()));
		JsonArray array = reader.next().getAsJsonArray();
		readBlocks(array);
	}
	
	private void readBlocks(JsonArray list) {
		for (int i = 0; i < list.size(); i++) {
			JsonObject obj = list.get(i).getAsJsonObject();
			int id = obj.get("type").getAsInt();
			int meta = obj.get("meta").getAsInt();
			String name = obj.get("text_type").getAsString();
			String displayName = obj.get("name").getAsString();
			if (id < 256) {
				blocks[id][meta] = new Block(id, meta, name, displayName);
			}
		}
	}
	
	public Block getBlock(int id, int meta) {
		return blocks[id][meta];
	}

	@Override
	public Iterator<Block> iterator() {
		return new Iterator<Block>() {
			private int id = 0;
			private int meta = 0;
			private boolean more = true;

			@Override
			public boolean hasNext() {
				return more;
			}

			@Override
			public Block next() {
				Block next = blocks[id][meta];
				Block nextNext;
				do {
					meta++;
					if (meta == 16) {
						meta = 0;
						id++;
						if (id == 256) {
							//No more
							more = false;
							break;
						}
					}
					nextNext = blocks[id][meta];
				} while (nextNext == null);
				return next;
			}
			
		};
	}
	
}
