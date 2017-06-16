package maze_printer.debug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * Java implementations of various C library functions. Please note that char[]s are used
 * instead of Strings, mostly since char[]s are mutable objects.
 * Feel free to use these in your own code.
 */
public class CUtils {
	/**
	 * Reads a file until either the end of the line/file is reached or <code>len</code>
	 * characters are read
	 * @param buf Character buffer
	 * @param len Maximum number of characters to read
	 * @param reader File reader to get the bytes from
	 * @return true on success, false if EOF is reached or an error occurs.
	 */
	public static boolean fgets(char[] buf, int len, BufferedReader reader) {
		char c;
		int cnt = 0;
		for (int i = 0; i < buf.length; i++) {
			buf[i] = '\0';
		}
		try {
			do {
				c = (char) reader.read();
				if (c == 65535) return false;
				buf[cnt] = c;
				cnt++;
			} while (c != '\n' && c != '\r' && cnt < len);
			if (c == '\r') reader.read();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	//Treat char[]s as Strings
	/**
	 * Concatenate two <code>char[]</code>s. Works like the <code>+</code> operator on Strings.
	 * @param dest The object that <code>src</code> will be appended to
	 * @param src The object to append to <code>dest</code>
	 * @return <code>dest</code>
	 */
	public static char[] strcat(char[] dest, char[] src) {
		int start = new String(dest).indexOf('\0');
		for (int i = start, j = 0; j < src.length; i++, j++) {
			dest[i] = src[j];
		}
		return dest;
	}
	/**
	 * Copies <code>n</code> characters of <code>src</code> into <code>dest</code>.
	 * @param dest Destination array
	 * @param src Source array
	 * @param n Number of characters to copy
	 * @return <code>dest</code>
	 */
	public static char[] strncpy(char[] dest, char[] src, int n) {
		for (int i = 0; i < n; i++) {
			char c;
			if (i >= dest.length)
				break;
			if (i >= src.length) {
				c = '\0';
			} else {
				c = src[i];
			}
			dest[i] = c;
		}
		for (int i = n; i < dest.length; i++) {
			dest[i] = '\0';
		}
		return dest;
	}
	/**
	 * Copies all of the characters of <code>src</code> into <code>dest</code>
	 * @param dest Destination array
	 * @param src Source array
	 * @return <code>dest</code>
	 */
	public static char[] strcpy(char[] dest, char[] src) {
		return strncpy(dest, src, src.length);
	}
	/**
	 * See {@link #strcpy(char[], char[])}
	 * @param dest Destination
	 * @param src Source
	 * @return <code>dest</code> (as a <code>char</code> array)
	 * @see #strcpy(char[], char[])
	 */
	public static char[] strcpy(char[] dest, String src) {
		return strcpy(dest, src == null ? new char[0] : src.toCharArray());
	}
	/**
	 * Get length of string after null characters have been removed
	 * @param obj Input
	 * @return Length of string
	 */
	public static int strlen(char[] obj) {
		return tostring(obj).length();
	}
	//Remove blanks from strings
	/**
	 * Convert a <code>char[]</code> to a <code>String</code>, removing trailing null characters
	 * @param src Input
	 * @return A <code>String</code> object
	 */
	public static String tostring(char[] src) {
		if (new String(src).indexOf('\0') == -1) return new String(src); //The string is full
		return new String(src).substring(0, new String(src).indexOf('\0'));
	}
	
	/**
	 * Prints a formatted string to a file
	 * @param file The file to write to
	 * @param format A format string (see {@link #sprintf(char[], String, Object...)} for syntax)
	 * @param args Arguments used by the format string
	 * @return 0 on success, -1 if there is an error (usually bad arguments)
	 * @see #sprintf(char[], String, Object...)
	 */
	public static int fprintf(OutputStream file, String format, Object... args) {
		try {
			char[] str = new char[1024];
			int i = sprintf(str, format, args);
			if (i < 0) return i;
			PrintStream ps = new PrintStream(file);
			for (int j = 0; j < str.length && str[j] != '\0'; j++) {
				ps.print(str[j]);
			}
			return i;
		} catch (Exception e) {
			return -1;
		}
	}
	/**
	 * Prints a formatted string to a file
	 * @param file The file to write to
	 * @param format A format string (see {@link #sprintf(char[], String, Object...)} for syntax)
	 * @param args Arguments used by the format string
	 * @return 0 on success, -1 if there is an error (usually bad arguments)
	 * @see #sprintf(char[], String, Object...)
	 * @see #fprintf(OutputStream, String, Object...)
	 */
	public static int fprintf(RandomAccessFile file, String format, Object... args) {
		try {
			char[] str = new char[1024];
			int i = sprintf(str, format, args);
			if (i < 0) return i;
			for (int j = 0; j < str.length && str[j] != '\0'; j++) {
				file.writeChar(str[j]);
			}
			return i;
		} catch (Exception e) {
			return -1;
		}
	}
	/**
	 * Prints a formatted string to standard output
	 * @param format A format string (see {@link #sprintf(char[], String, Object...)} for syntax)
	 * @param args Arguments used by the format string
	 * @return 0 on success, -1 if there is an error (usually bad arguments)
	 * @see #sprintf(char[], String, Object...)
	 */
	public static int printf(String format, Object... args) {
		int i;
		char[] str = new char[1024];
		i = sprintf(str, format, args);
		if (i < 0) return i;
		System.out.print(tostring(str));
		return i;
	}
	/**
	 * C's sprintf function translated into Java.                   <p/><p/>
	 * 
	 * <i>Format specifiers</i> are replaced by the values of 
	 * subsequent arguments (from the <code>args</code> list)
	 * and formatted as requested.                                  <p/><p/>
	 * 
	 * The syntax of the format specifiers is:<code>                <p/>
	 * %[flags][width][.&lt;precision&gt;][length]&lt;specifier&gt; <p/></code>
	 * Only <i>specifier</i> is required. All other fields are
	 * optional.                                                    <p/>
	 * <i>specifier</i> can be one of the following:          <p/><code>
	 * |Specifier|Output____________________________________|Example__|   <br>
	 * |d or i___|Integer___________________________________|-42______|   <br>
	 * |u________|Unsigned integer__________________________|74595____|   <br>
	 * |o________|Integer - Octal___________________________|731______|   <br>
	 * |x________|Integer - Hexadecimal_____________________|fa31_____|   <br>
	 * |X________|Integer - Hexadecimal - Uppercase_________|FA31_____|   <br>
	 * |f________|Floating point____________________________|1.5625___|   <br>
	 * |e________|Floating point - Scientific notation______|1.2345e+2|   <br>
	 * |E________|Floating point - Sci. notation - uppercase|1.2345E+2|   <br>
	 * |g________|Shorter of %e and %f______________________|123.45___|   <br>
	 * |G________|Shorter of %e and %f - uppercase__________|3.6231E+6|   <br>
	 * |c________|Character_________________________________|j________|   <br>
	 * |s________|String____________________________________|A String_|   <br>
	 * |p________|Pointer address (prints hash code instead)|c63cb61d_|   <br>
	 * |_________|Nothing - stores # of chars printed in____|_________|   <br>
	 * |n________|the next argument (interpreted as a_______|_________|   <br>
	 * |_________|Pointer&lt;Integer&gt;)_________________________|_________|   <br>
	 * |%________|Prints a '%' symbol_______________________|%________|   <p/><p/></code>
	 * 
	 * There can be various <i>flags</i>:                                 <p/><p/><code>
	 * 
	 * |flag_|Description_____________________________________________|   <br>
	 * |-____|Force left-justification (right is default)_____________|   <br>
	 * |+____|Forces '+' to be added to positive numbers______________|   <br>
	 * |space|Puts a space before positive numbers____________________|   <br>
	 * |#____|When used with o, x, or X, begins with the proper prefix|   <br>
	 * |_____|(0, 0x, or 0X, respectively) unless the number is 0_____|   <br>
	 * |0____|Pads the value with zeroes instead of spaces____________|   <p/><p/></code>
	 * 
	 * The <i>width</i> field determines the minimum number of characters
	 * to be printed. It is denoted by a number (for constant width) or
	 * '*' (width is read in as an argument).                             <p/><p/>
	 * 
	 * the <i>precision</i> field determines how precise the number is.
	 * For integers (<code>d, i, o, u, x, X</code>), it specifies the 
	 * minimum number of digits to be written. If the length of the
	 * number is smaller than this, it will be padded with zeroes.
	 * For floating-point numbers (<code>e, E, f, F</code>), it specifies
	 * how many decimal places to add after the decimal point (default is
	 * 6). For <code>g</code> and <code>G</code>, it is the number of
	 * significant digits to be printed.                                 <p/>
	 * <i>precision</i> is denoted by a dot ('.') followed by either a
	 * number (constant precision) or '*' (precision is read as an
	 * argument).                                                        <p/><p/>
	 * 
	 * The <i>length</i> specifier used to determine the length of the
	 * data type. In this version, <i>length</i> is ignored and the data
	 * type is automatically detected.
	 * 
	 * 
	 * @param str String buffer to store result in
	 * @param format Format 
	 * @param args Arguments for the format specifiers
	 * @return 0 on normal exit, -1 if an error occurred (overflow, invalid arguments, etc.)
	 */
	public static int sprintf(char[] str, String format, Object... args) {
		return sprintf(str, format, false, args);
	}
	
	private static int sprintf(char[] str, String format, boolean force_debug, Object... args) {
		String debug = "";
		boolean formatting = false;
		int i = 0;
		Arrays.fill(str, '\0');
		ArrayList<Character> flags = new ArrayList<Character>();
		int argn = 0;
		boolean has_dot = false;
		char[] specifiers = {'d', 'i', 'u', 'o', 'x', 'X', 'f', 'e', 'E', 'g', 'G', 'c', 's', 'p', 'n', '%'};
		String digits = "";
		int width = 1;
		int precision = -1;
		char length = '\0';
		try {
			for (char c : format.toCharArray()) {
				debug += c;
				if (!formatting) {
					if (c == '%') {
						debug += " - format\n";
						formatting = true;
						continue;
					} else {
						str[i++] = c;
					}
				} else {
					if (Search.has(specifiers, c)) {
						debug += " - specifier";
						formatting = false;
						if (!digits.equals("")) {
							if (has_dot) {
								precision = Integer.parseInt(digits);
								debug += "\nprecision=" + precision + "\n";
								has_dot = false;
							} else {
								width = Integer.parseInt(digits);
								debug += "\nwidth=" + width + "\n";
							}
							digits = "";
						}
					}
					switch (c) {
					//Specifiers (end format string)
					case 'd':
					case 'i': //int
					case 'u': //unsigned int; treat it as an int
						debug += " - int\n";
						debug += "given: " + args[argn].getClass().getSimpleName() + "\n";
						if (precision == -1) precision = 1;
						i = add_int(str, flags, width, precision, length, args[argn++], i);
						break;
					case 'o': //octal
						debug += " - oct\n";
						debug += "given: " + args[argn].getClass().getSimpleName() + "\n";
						if (precision == -1) precision = 1;
						i = add_int(str, flags, width, precision, length, args[argn++], i, 8);
						break;
					case 'x': //hex - lowercase
						debug += " - hex\n";
						debug += "given: " + args[argn].getClass().getSimpleName() + "\n";
						if (precision == -1) precision = 1;
						i = add_int(str, flags, width, precision, length, args[argn++], i, 16);
						break;
					case 'X': //hex - uppercase
						debug += " - Hex\n";
						debug += "given: " + args[argn].getClass().getSimpleName() + "\n";
						if (precision == -1) precision = 1;
						i = add_int(str, flags, width, precision, length, args[argn++], i, 16, true);
						break;
					case 'f': //float
						debug += " - flt\n";
						debug += "given: " + args[argn].getClass().getSimpleName() + "\n";
						if (precision == -1) precision = 6;
						i = add_float(str, flags, width, precision, length, args[argn++], i);
						break;
					case 'e': //float - scientific notation - lowercase
						debug += " - sci\n";
						debug += "given: " + args[argn].getClass().getSimpleName() + "\n";
						if (precision == -1) precision = 6;
						i = add_sci_float(str, flags, width, precision, length, args[argn++], i, false);
						break;
					case 'E': //float - scientific notation - uppercase
						debug += " - Sci\n";
						debug += "given: " + args[argn].getClass().getSimpleName() + "\n";
						if (precision == -1) precision = 6;
						i = add_sci_float(str, flags, width, precision, length, args[argn++], i, true);
						break;
					case 'g':
						debug += " - best\n";
						debug += "given: " + args[argn].getClass().getSimpleName() + "\n";
						if (precision == -1) precision = 6;
						i = add_best_float(str, flags, width, precision, length, args[argn++], i, false);
						break;
					case 'G':
						debug += " - Best\n";
						debug += "given: " + args[argn].getClass().getSimpleName() + "\n";
						if (precision == -1) precision = 6;
						i = add_best_float(str, flags, width, precision, length, args[argn++], i, true);
						break;
					case 'c':
						debug += " - chr\n";
						debug += "given: " + args[argn].getClass().getSimpleName() + "\n";
						Object val = args[argn++];
						if (val instanceof Character) {
							str[i++] = (char) val;
						} else {
							str[i++] = (char) (int) val;
						}
						break;
					case 's':
						debug += " - str\n";
						debug += "given: " + args[argn].getClass().getSimpleName() + "\n";
						i = add_string(str, flags, width, precision, length, args[argn++], i);
						break;
					case 'p':
						debug += " - ptr\n";
						debug += "given: " + args[argn].getClass().getSimpleName() + "\n";
						//hmmm.... pointer addresses??? We'll print hash code just to print something...
						i = add_string(str, flags, width, precision, length, Integer.toString(args[argn++].hashCode()), i);
						break;
					case 'n':
						debug += " - nul\n";
						debug += "given: " + args[argn].getClass().getSimpleName() + "\n";
						@SuppressWarnings("unchecked") 
						Pointer<Integer> pointer = (Pointer<Integer>)args[argn++];
						pointer.value = i-1;
						break;
					case '%':
						debug += " - per\n";
						str[i++] = '%';
						break;
					case '-':
					case '+':
					case '#':
					case ' ':
						debug += " - flag:" + c + "\n";
						flags.add(c);
						break;
					case '.':
						debug += " - dot\n";
						has_dot = true;
						if (!digits.equals("")) {
							//We have found a width specifier; add it
							width = Integer.parseInt(digits);
							debug += "width=" + width + "\n";
							digits = "";
						}
						break;
					case 'h':
					case 'l':
					case 'L':
						debug += " - length:" + c + "\n";
						length = c;
						if (!digits.equals("")) {
							if (has_dot) {
								precision = Integer.parseInt(digits);
								debug += "precision=" + precision + "\n"; 
								has_dot = false;
							} else {
								width = Integer.parseInt(digits);
								debug += "width=" + width + "\n";
							}
							digits = "";
						}
						break;
					case '*':
						debug += " - star - ";
						if (has_dot) {
							precision = (int)args[argn++];
							debug += "precision=" + precision + "\n"; 
						} else {
							width = (int)args[argn++];
							debug += "width=" + width + "\n";
						}
						break;
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						if (c == '0' && digits.equals("") && /*but*/ !has_dot) {
							debug += " - flag:" + c + "\n";
							flags.add('0');
						}
						else {
							debug += " - digit:" + c + "\n";
							digits += c;
						}
						break;
					}
					if (formatting == false) {
						width = 1;
						precision = -1;
						length = '\0';
						flags.clear();
					}
				}
			}
			debug += "done - " + tostring(str) + "\n";
		} catch (Exception e) {
			System.err.println(debug);
			return -1;
		}
		if (force_debug) System.out.println(debug);
		return 0;
	}
	private static int add_string(char[] buf, List<Character> flags, int width, int precision, char length, Object arg, int idx) {
		String val;
		if (arg instanceof String) val = (String)arg;
		else val = tostring((char[])arg);
		String s;
		if (precision == -1) s = val;
		else s = val.substring(0, precision);
		s = pad(s, flags, width);
		idx = add_str(buf, s, idx);
		return idx;
	}
	private static int add_str(char[] buf, String s, int idx) {
		for (int i = 0; i < s.length(); idx++, i++) {
			buf[idx] = s.charAt(i);
		}
		return idx;
	}
	private static int add_best_float(char[] buf, List<Character> flags, int width, int precision, char length, Object arg, int idx, boolean uppercase) {
		char[] b1 = new char[buf.length];
		char[] b2 = new char[buf.length];
		add_sci_float(b1, flags, width, precision, length, arg, 0, uppercase);
		add_float(b2, flags, width, precision, length, arg, 0);
		int l1 = Search.index(b1, '\0');
		int l2 = Search.index(b2, '\0');
		if (l1 > l2) return add_sci_float(buf, flags, width, precision, length, arg, idx, uppercase);
		else return add_float(buf, flags, width, precision, length, arg, idx);
	}
	private static int add_sci_float(char[] buf, List<Character> flags, int width, int precision, char length, Object arg, int idx, boolean uppercase) {
		//Use Java formatters
		double val = (double)arg;
		String s = "";
		String sci = String.format("%"+width+"."+precision+"e", val);
		String mant = sci.substring(0, sci.indexOf('e'));
		String expon= sci.substring(sci.indexOf('e'), sci.length());
		if (!mant.contains(".") && flags.contains('#')) mant += '.';
		if (!mant.contains("-") && flags.contains('+')) mant = "+" + mant;
		s = mant + expon;
		s = pad(s, flags, width);
		idx = add_str(buf, s, idx);
		return idx;
	}
	private static int add_float(char[] buf, List<Character> flags, int width, int precision, char length, Object arg, int idx) {
		String s = get_float(flags, width, precision, length, arg);
		s = pad(s, flags, width);
		idx = add_str(buf, s, idx);
		return idx;
	}
	private static String get_float(List<Character> flags, int width, int precision, char length, Object arg) {
		double val = (double)arg;
		String s = Integer.toString((int)val);
		boolean needs_decimal = false;
		int digits_after = 0;
		if (precision > 0) {
			needs_decimal = true;
			digits_after = precision;
		}
		if (flags.contains('#')) needs_decimal = true; //No matter what.
		if (flags.contains('+') && val >= 0) s = "+" + s;
		else if (flags.contains(' ') && val >= 0) s = " " + s;
		if (needs_decimal) {
			s += '.';
			String digits = Double.toString(val); //Easy way
			int min = digits.indexOf('.')+1;
			int max = digits.length()-min;
			for (int i = 0; i < Math.min(digits_after, max); i++) {
				s += digits.charAt(min+i);
			}
		}
		return s;
	}
	private static int add_int(char[] buf, List<Character> flags, int width, int precision, char length, Object arg, int idx) {
		return add_int(buf, flags, width, precision, length, arg, idx, 10);
	}
	private static int add_int(char[] buf, List<Character> flags, int width, int precision, char length, Object arg, int idx, int radix) {
		return add_int(buf, flags, width, precision, length, arg, idx, radix, false);
	}
	private static int add_int(char[] buf, List<Character> flags, int width, int precision, char length, Object arg, int idx, int radix, boolean uppercase) {
		long val;
		if (arg instanceof Short) val = (short)arg;
		else if (arg instanceof Long) val = (long)arg;
		else val = (int)arg;
		String s = Long.toString(val, radix);
		if (precision == 0 && val == 0) s = "";
		else if (precision > 0) {
			for (int i = s.length(); i < precision; i++) {
				s = "0" + s;
			}
		}
		if (flags.contains('#') && val != 0) {
			if (radix == 16) s = "0x" + s;
			if (radix == 8)  s = "0"  + s;
		}
		if (flags.contains('+') && val >= 0) s = "+" + s;
		else if (flags.contains(' ') && val >= 0) s = " " + s;
		s = pad(s, flags, width);
		if (uppercase) s = s.toUpperCase();
		idx = add_str(buf, s, idx);
		return idx;
	}
	private static String pad(String in, List<Character> flags, int width) {
		char padding = (flags.contains('0') ? '0' : ' ');
		if (flags.contains('-')) {
			for (int i = in.length(); i < width; i++) {
				in += padding;
			}
		} else {
			for (int i = in.length(); i < width; i++) {
				in = padding + in;
			}
		}
		return in;
	}
	// We don't use very many of these and I don't char whether they are used or not (bad pun)
	@SuppressWarnings("unused")
	private static class Search {
		public static int index(byte[] a, byte q) {       for (int i = 0; i < a.length; i++) if (a[i] == q) return i; return -1;}
		public static int index(short[] a, short q) {     for (int i = 0; i < a.length; i++) if (a[i] == q) return i; return -1;}
		public static int index(int[] a, int q) {         for (int i = 0; i < a.length; i++) if (a[i] == q) return i; return -1;}
		public static int index(long[] a, long q) {       for (int i = 0; i < a.length; i++) if (a[i] == q) return i; return -1;}
		public static int index(float[] a, float q) {     for (int i = 0; i < a.length; i++) if (a[i] == q) return i; return -1;}
		public static int index(double[] a, double q) {   for (int i = 0; i < a.length; i++) if (a[i] == q) return i; return -1;}
		public static int index(boolean[] a, boolean q) { for (int i = 0; i < a.length; i++) if (a[i] == q) return i; return -1;}
		public static int index(char[] a, char q) {       for (int i = 0; i < a.length; i++) if (a[i] == q) return i; return -1;}
		public static int index(Object[] a, Object q) {   for (int i = 0; i < a.length; i++) if (a[i].equals(q)) return i; return -1;}
		public static boolean has(byte[] a, byte q) {       return index(a,q) >= 0;} 
	    public static boolean has(short[] a, short q) {     return index(a,q) >= 0;} 
	    public static boolean has(int[] a, int q) {         return index(a,q) >= 0;} 
	    public static boolean has(long[] a, long q) {       return index(a,q) >= 0;} 
	    public static boolean has(float[] a, float q) {     return index(a,q) >= 0;} 
	    public static boolean has(double[] a, double q) {   return index(a,q) >= 0;} 
	    public static boolean has(boolean[] a, boolean q) { return index(a,q) >= 0;} 
	    public static boolean has(char[] a, char q) {       return index(a,q) >= 0;}
	    public static boolean has(Object[] a, Object q) {   return index(a,q) >= 0;}
	}
}
