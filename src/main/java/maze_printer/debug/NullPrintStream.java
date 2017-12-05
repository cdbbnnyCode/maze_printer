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
package maze_printer.debug;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;
/**
 * NullPrintStream - the PrintStream that does nothing <br>
 * All methods in PrintStream are overridden so that none of them do anything at all. <br>
 * Therefore, anything passed to the stream will be destroyed. <br>
 * It is a good replacement for System.out if you want it to output to nowhere. <br>
 * AKA a /dev/null for Java.
 * @author Aidan Yaklin
 * @version 1.0.1
 */
public class NullPrintStream extends PrintStream{
	public NullPrintStream() {
		super(new FilterOutputStream(null));
	}
	@Override
	public void flush() {}

	@Override
	public void close() {}

	@Override
	public boolean checkError() {
		return false;
	}

	@Override
	protected void setError() {}

	@Override
	protected void clearError() {}
	@Override
	public void write(int b) {}
	@Override
	public void write(byte[] buf, int off, int len) {}
	@Override
	public void write(byte[] b) throws IOException {}
	
	

}
