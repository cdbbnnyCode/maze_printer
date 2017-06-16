package maze_printer.debug;

import static maze_printer.debug.CUtils.*;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * Basic debug log that can be disabled.
 * Uses the {@link CUtils#sprintf(char[], String, Object...) CUtils.sprintf} function
 * for formatted output.
 */
public class Debugger {
	//Set this to false in release builds.
	private static final boolean DEBUG = false;
	private String cl; //Class name
	private PrintStream out, err; //Output/error streams
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS"); //Date format
	
	/*
	 * Print text to a file. Supports prefixes (normally INFO and ERROR).
	 */
	private static class FilePrintStream extends PrintStream {
		
		String prefix;
		private boolean nl = true;

		public FilePrintStream(File file, String prefix) throws FileNotFoundException {
			super(file);
			this.prefix = prefix;
		}

		@Override
		public void print(boolean b) {
			_print((nl?prefix:"")+b);
		}

		@Override
		public void print(char c) {
			_print((nl?prefix:"")+c);
		}

		@Override
		public void print(int i) {
			_print((nl?prefix:"")+i);
		}

		@Override
		public void print(long l) {
			_print((nl?prefix:"")+l);
		}

		@Override
		public void print(float f) {
			_print((nl?prefix:"")+f);
		}

		@Override
		public void print(double d) {
			_print((nl?prefix:"")+d);
		}

		@Override
		public void print(char[] s) {
			_print((nl?prefix:"")+new String(s));
		}

		public void _print(String s) {
			super.print(s);
			if (s.endsWith("\n")) nl = true;
			else nl = false;
		}
		
		@Override
		public void print(String s) {
			_print((nl?prefix:"") + s);
		}

		@Override
		public void print(Object obj) {
			_print((nl?prefix:"")+obj);
		}

		@Override
		public void println(boolean x) {
			_println((nl?prefix:"")+x);
		}

		@Override
		public void println(char x) {
			_println((nl?prefix:"")+x);
		}

		@Override
		public void println(int x) {
			_println((nl?prefix:"")+x);
		}

		@Override
		public void println(long x) {
			_println((nl?prefix:"")+x);
		}

		@Override
		public void println(float x) {
			_println((nl?prefix:"")+x);
		}

		@Override
		public void println(double x) {
			_println((nl?prefix:"")+x);
		}

		@Override
		public void println(char[] x) {
			_println((nl?prefix:"")+new String(x));
		}

		@Override
		public void println(String x) {
			_println((nl?prefix:"")+x);
		}

		@Override
		public void println(Object x) {
			_println((nl?prefix:"")+x);
		}
		
		private void _println(String s) {
			nl = true;
			super.println(s);
		}

		@SuppressWarnings("resource")
		@Override
		public PrintStream printf(String format, Object... args) {
			if (new Formatter().format(format, args).toString().endsWith("\n"))
				nl = true;
			else
				nl = false;
			return super.printf((nl?prefix:"") + format, args);
		}

		@SuppressWarnings("resource")
		@Override
		public PrintStream printf(Locale l, String format, Object... args) {
			if (new Formatter().format(l, format, args).toString().endsWith("\n"))
				nl = true;
			else
				nl = false;
			return super.printf(l, (nl?prefix:"") + format, args);
		}
		
	}
	private boolean newline = true; //Only print prefixes on new lines
	static {
//		try {
//			syserr = new FilePrintStream(new File("err.txt"), "");
//		} catch (FileNotFoundException e1) {
//			syserr = new NullPrintStream();
//		}
//		System.setErr(syserr);
//		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				Debugger dbg = new Debugger();
//				dbg.errln("Errors:");
//				try {
//					Scanner scan = new Scanner(new FileReader(new File("err.txt")));
//					while (scan.hasNextLine()) {
//						dbg.errln(scan.nextLine());
//					}
//					scan.close();
//				} catch (IOException e) {
//					e.printStackTrace(new PrintStream(new FileOutputStream(FileDescriptor.err)));
//				}
//			}
//			
//		}));
	}
	/*
	 * Create a debugger from a file (null if stdout/err is to be used)
	 * and a depth into the stack trace to look for the name of the class
	 * that created it.
	 */
	private Debugger(File f, int depth) {
		cl = Thread.currentThread().getStackTrace()[depth+1].getClassName();
		cl = cl.substring(cl.lastIndexOf('.')+1); //We just want the class name
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				out.close();
				err.close();
			}
		}));
		if (DEBUG) {
			if (f != null) {
				try {
					out = new FilePrintStream(f, "[INFO] ");
					err = new FilePrintStream(f, "[ERROR] ");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				out = System.out;
				err = new PrintStream(new FileOutputStream(FileDescriptor.err)); //Re-create System.err, which we have just assigned null to
			}
		} else {
			out = new NullPrintStream();
			err = new NullPrintStream();
		}
	}
	
	/**
	 * Create a default debugger that uses stdout and stderr
	 */
	public Debugger() {
		this(null, 2);
	}
	
	/**
	 * Create a debugger that outputs to a file.
	 * @param f File to print to.
	 */
	public Debugger(File f) {
		this(f, 2);
	}
	
	/**
	 * Write an object and a newline to the output stream.
	 * @param s The object to write
	 */
	public void outln(Object s) {
		if (!DEBUG) return;
		if (newline)
			out.println("[" + cl + "][" + dateFormat.format(new Date()) + "] " + s);
		else
			out.println(s);
		newline = true;
	}
	
	/**
	 * Write formatted text and a newline to the output stream. 
	 * See {@link CUtils#sprintf(char[], String, Object...) CUtils.sprintf}
	 * for formatting syntax.
	 * @param s Format string
	 * @param args Arguments
	 */
	public void outln(String s, Object...args) {
		if (!DEBUG) return;
		char[] buf = new char[1024];
		sprintf(buf, s, args);
		if (newline)
			out.println("[" + cl + "][" + dateFormat.format(new Date()) + "] " + tostring(buf));
		else
			out.println(tostring(buf));
		newline = true;
	}
	
	/**
	 * Write an object and a newline to the error stream.
	 * @param s The object to write
	 */
	public void errln(Object s) {
		if (!DEBUG) return;
		if (newline)
			err.println("[" + cl + "][" + dateFormat.format(new Date()) + "] " + s);
		else
			err.println(s);
		newline = true;
	}
	/**
	 * Write formatted text and a newline to the error stream.
	 * See {@link CUtils#sprintf(char[], String, Object...) CUtils.sprintf}
	 * for formatting syntax.
	 * @param s Format string
	 * @param args Arguments
	 */
	public void errln(String s, Object...args) {
		if (!DEBUG) return;
		char[] buf = new char[1024];
		sprintf(buf, s, args);
		if (newline)
			err.println("[" + cl + "][" + dateFormat.format(new Date()) + "] " + tostring(buf));
		else
			err.println(tostring(buf));
		newline = true;
	}
	/**
	 * Write an object (without a newline) to the output stream.
	 * @param s The object to write
	 */
	public void out(Object s) {
		if (!DEBUG) return;
		if (newline)
			out.print("[" + cl + "][" + dateFormat.format(new Date()) + "] " + s);
		else
			out.print(s);
		newline = ("" + s).endsWith("\n");
	}
	/**
	 * Write formatted text to the output stream. 
	 * See {@link CUtils#sprintf(char[], String, Object...) CUtils.sprintf}
	 * for formatting syntax.
	 * @param s Format string
	 * @param args Arguments
	 */
	public void out(String s, Object...args) {
		if (!DEBUG) return;
		char[] buf = new char[1024];
		sprintf(buf, s, args);
		if (newline)
			out.print("[" + cl + "][" + dateFormat.format(new Date()) + "] " + tostring(buf));
		else
			out.print(tostring(buf));
		newline = tostring(buf).endsWith("\n");
	}
	/**
	 * Write an object (without a newline) to the error stream.
	 * @param s The object to write
	 */
	public void err(Object s) {
		if (!DEBUG) return;
		if (newline)
			err.print("[" + cl + "][" + dateFormat.format(new Date()) + "] " + s);
		else
			err.print(s);
		newline = ("" + s).endsWith("\n");
	}
	/**
	 * Write formatted text to the error stream. 
	 * See {@link CUtils#sprintf(char[], String, Object...) CUtils.sprintf}
	 * for formatting syntax.
	 * @param s Format string
	 * @param args Arguments
	 */
	public void err(String s, Object...args) {
		if (!DEBUG) return;
		char[] buf = new char[1024];
		sprintf(buf, s, args);
		if (newline)
			err.print("[" + cl + "][" + dateFormat.format(new Date()) + "] " + tostring(buf));
		else
			err.print(tostring(buf));
		newline = tostring(buf).endsWith("\n");
	}
	/**
	 * Close any files that were open when the debugger was created.
	 * Does nothing if stdout/stderr were used.
	 * @throws IOException if an I/O error occurred closing the file.
	 */
	public void close() throws IOException {
		if (out == System.out) return; //Don't close system output. Things might still need it.
		out.close();
		err.close();
	}
}
