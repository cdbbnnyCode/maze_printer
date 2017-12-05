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

public class Cache<K, V> {
	private Object[] keys;
	private Object[] values;
	private final int capacity;
	private int pointer;
	public Cache(int capacity) {
		this.capacity = capacity;
		this.keys = new Object[capacity];
		this.values = new Object[capacity];
	}
	
	public void put(K key, V value) {
		keys[pointer] = key;
		values[pointer++] = value;
		pointer %= capacity;
	}
	
	@SuppressWarnings("unchecked")
	public V get(K key) {
		for (int i = 0; i < capacity; i++) {
			if (keys[i] != null && keys[i].equals(key)) {
				return (V)values[i];
			}
		}
		return null;
	}
}
