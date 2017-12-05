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

public class Block {
	private final int id;
	private final int meta;
	private final String name;
	private final String displayName;
	
	public Block(int id, int meta, String name, String displayName) {
		this.id = id;
		this.meta = meta;
		this.name = name;
		this.displayName = displayName;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public int getMeta() {
		return meta;
	}
	
	public String toString() {
		return name + " (" + id + "-" + meta + ")";
	}
	
}
