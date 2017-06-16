package maze_printer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

import javax.swing.JPanel;

import maze_printer.debug.CUtils;
import maze_printer.debug.Debugger;


/**
 * Preview panel. Also renders the maze when it is printed.
 */
@SuppressWarnings("serial")
public class PreviewPanel extends JPanel {
	//Background/foreground colors
	private static final Color bg = Color.BLACK;
	private static final Color fg = Color.WHITE;
	//Buffer image that the maze is rendered onto
	private BufferedImage buffer;
	//Image to fit the maze into (unsupported)
	@SuppressWarnings("unused")
	private BufferedImage img;
	//Flags for real-time rendering of the maze
	private volatile boolean needsUpdate = false;
	private volatile boolean updating = false;
	//Whether to use the (unsupported) image
	private boolean useImg = false;
	//Path size (default = 18)
	public int path_size = 18;
	//Maximum x and y coordinates (in path_size units)
	private int maxx, maxy;
	//Random number generator
	private Random rand;
	//Whether to generate slowly
	private boolean slow;
	//Debugger
	private Debugger debug = new Debugger();
	/**
	 * Create the panel.
	 */
	public PreviewPanel() {
		//Set up variables
		super();
		setBackground(bg);
		rand = new Random();
		//Create updater thread
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					if (needsUpdate) {
						sleep(10);
						updating = true;
						update();
						updating = false;
					}
					sleep(10);
				}
			}
			
		}, "Update Thread").start();
		//Create paint thread
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					sleep(5);
					if (needsUpdate) {
						repaint();
					}
				}
			}
			
		}, "Paint Thread").start();
	}
	
	private void sleep(long millis) {
		//Sleeps for millis milliseconds
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() < start + millis) {
			try {
				Thread.sleep(5); //To keep from using 100% cpu while doing nothing
			} catch (Exception e) {}
		};
	}
	/**
	 * Paint the preview window
	 */
	@Override
	public void paintComponent(Graphics g) {
//		debug.outln("Draw");
		super.paintComponent(g); //Fill background
		Graphics2D g2d = (Graphics2D)g;
		//Wait for the width and height to be more than 0 to start rendering the maze
		//Allows the rest of the layout manager to load
		if ((buffer == null && getWidth() > 1 && getHeight() > 1)) {
			debug.outln("Creating buffer image of size %d x %d", getWidth(), getHeight());
			needsUpdate = true;
			buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		}
		//Resize buffer
		if (needsUpdate && !updating && buffer != null && (getWidth() != buffer.getWidth() || getHeight() != buffer.getHeight())) {
			debug.outln("Resizing buffer image to %d x %d", getWidth(), getHeight());
			needsUpdate = false;
			sleep(100);
			buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
			needsUpdate = true;
		}
		//Render buffer
		g2d.drawImage(buffer, 0, 0, null);
		//Show window size if resized
		if (getWidth() != buffer.getWidth() || getHeight() != buffer.getHeight()) {
			g2d.setColor(Color.GREEN);
			g2d.drawString(getWidth() + "x" + getHeight() + " (refresh to hide)", 20, 20);
		}
	}
	/*
	 * Update to the default buffer
	 */
	private void update() {
		update(buffer);
	}
	
	/*
	 * Redraw the maze
	 */
	private void update(BufferedImage buffer) {
		Graphics2D g = buffer.createGraphics();
		//Use buffer size so that it can fill the buffer
		int w = buffer.getWidth();
		int h = buffer.getHeight();
		g.setColor(fg);
		g.fillRect(0, 0, w, h); //Fill with white
		Dimension d = calc_pathsize(buffer); //Adjust size to make square paths on the edges
		w = d.width;
		h = d.height;
		int wall_size = path_size / 4; //Wall size is 1/4 path size so that paths can be 2x as wide as the walls
		maxx = w / path_size;
		maxy = h / path_size;
		g.setColor(bg);
		g.fillRect(0, 0, w, h); //Fill with black
		g.setColor(fg);
		//Create fill arrays
		boolean[][] fill = new boolean[maxy+1][maxx+1];
		boolean[][] autofill = new boolean[maxy+1][maxx+1];
		int x, y, x2, y2;
		//Coords array is for points to go back to if the current trail ends
		Stack<Point> coords = new Stack<Point>();
		//Push starting point (top left corner)
		coords.push(getStart(fill, autofill));
		while (!full(fill)) {
			Point p = coords.pop();
//			debug.outln("Starting at (%d,%d)", p.x, p.y);
			x = p.x;
			y = p.y;
			int max = (rand.nextInt((w * h) / 10) * 2);
			for (int i = 0; i < max && canMove(x, y, fill) != 0; i++) {
				x2 = x;
				y2 = y;
				fill[y][x] = true;
				boolean succ = false;
				byte move = canMove(x, y, fill);
//				debug.outln("(udlr)");
//				debug.outln(" %04s ", Integer.toBinaryString(move));
				while (canMove(x, y, fill) != 0 && !succ) {
					int tr = rand.nextInt(4);
					if (((move & 0b1000) != 0) && tr == 0){
//						debug.outln("up");
						succ = true;
						y2--;
					} else if (((move & 0b0100) != 0) && tr == 1){
//						debug.outln("down");
						succ = true;
						y2++;
					} else if (((move & 0b0010) != 0) && tr == 2){
//						debug.outln("left");
						succ = true;
						x2--;
					} else if (((move & 0b0001) != 0) && tr == 3){
//						debug.outln("right");
						succ = true;
						x2++;
					}
				}
				if (rand.nextInt(3) == 0) coords.push(new Point(x, y));
				//Draw line
				draw_path(g, wall_size, x, y, x2, y2);
				x = x2;
				y = y2;
				//Print current progress; \r returns to the same line
				CUtils.printf("%d%% complete - %d path(s) to resolve                               \r", (int)(fullness(fill)*100), coords.size());
				if (slow)
					try {
						Thread.sleep(70);
					} catch (Exception e) {}
			}
			if (coords.empty())
				coords.push(rand_path(fill, autofill)); // Add random place if list is empty
			fill[y][x] = true; //Fill the end of the path too
			//Debugging overlays
//			if (overlay == null) 
//				overlay = new BufferedImage(buffer.getWidth(), buffer.getHeight(), BufferedImage.TYPE_INT_ARGB);
//			else
//				overlay.setRGB(0, 0, overlay.getWidth(), overlay.getHeight(), new int[overlay.getWidth() * overlay.getHeight()], 0, 1);
//			draw_fullness(overlay.createGraphics(), fill);
			
		}
		//Print current progress; \r returns to the same line
		CUtils.printf("%d%% complete - %d path(s) to resolve                               \r", (int)(fullness(fill)*100), coords.size());
		g.setColor(bg);
		//Draw edges
		g.setStroke(new BasicStroke(wall_size, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
		g.drawLine(path_size, wall_size / 2, w, wall_size / 2);
		g.drawLine(w, 0, w, h);
		g.drawLine(0, h, w - path_size + 1, h);
		g.drawLine(wall_size / 2, 0, wall_size / 2, h);
		//Put holes for entrance and exit
		g.setColor(fg);
		g.drawLine(wall_size + wall_size / 2, wall_size / 2, path_size - wall_size, wall_size / 2);
		g.drawLine(w - path_size + wall_size + 1, h, w - wall_size, h);

		System.out.println("\nDone");
		needsUpdate = false;
		repaint(); //Final repaint so that holes aren't left in the preview
	}
	/*
	 * Draw a path from (x1, y1) to (x2, y2).
	 */
	private void draw_path(Graphics2D g, int wall_size, int x1, int y1, int x2, int y2) {
		int offset = wall_size / 2;
		int size = path_size - wall_size;
		g.setStroke(new BasicStroke(size, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
		g.drawLine(x1 * path_size + size / 2 + offset, y1 * path_size + size / 2 + offset, x2 * path_size + size / 2 + offset, y2 * path_size + size / 2 + offset);
	}
	/*
	 * Get starting position (usually (0,0) for non-images)
	 * Images don't currently work, so much of this code is not used.
	 */
	private Point getStart(boolean[][] fill, boolean[][] autofill) {
		int[] test = new int[path_size * path_size];
		Arrays.fill(test, new Color(0, 0, 0, 255).getRGB());
		Point p = null;
		if (useImg) {
			for (int i = 0; i < maxy; i++) {
				for (int j = 0; j < maxx; j++) {
					if (checkRect(j, i, buffer)
						&& (j == 0 || checkRect(j-1, i, buffer))
						&& (j == fill.length-1 || checkRect(j+1, i, buffer))
						&& (i == 0 || checkRect(j, i-1, buffer))
						&& (i == fill.length-1 || checkRect(j, i+1, buffer))) {
						if (p == null) p = new Point(j, i);
					} else {
						fill[i][j] = true;
						autofill[i][j] = true;
					}
				}
			}
			if (canMove(p.x, p.y, fill) == 0) {
				for (int i = 0; i < maxy; i++) {
					for (int j = 0; j < maxx; j++) {
						if (canMove(i, j, fill) != 0) return new Point(j, i);
						else {
							fill[i][j] = true;
							autofill[i][j] = true;
						}
					}
				}
			}
			return p;
		}
		useImg = false;
		return new Point(0, 0);
	}
	/*
	 * Used in image mode (unsupported) for checking if a
	 * path_size by path_size square chunk of the image is
	 * completely black
	 */
	private boolean checkRect(int x, int y, BufferedImage img) {
		int test = new Color(0, 0, 0).getRGB();
		for (int i = 0; i < path_size; i++) {
			for (int j = 0; j < path_size; j++) {
				if (img.getRGB(j + x * path_size, i + y * path_size) != test)
					return false;
			}
		}
		return true;
	}
	
	/*
	 * Returns true when all items of fill are
	 * true.
	 */
	private boolean full(boolean[][] fill) {
		for (int i = 0; i < fill.length; i++) {
			for (int j = 0; j < fill[0].length; j++) {
				if (!fill[i][j]) return false;
			}
		}
		return true;
	}
	
	/*
	 * Returns a number from 0 to 1 that shows
	 * what percent of fill is true
	 */
	private double fullness(boolean[][] fill) {
		int max = fill.length * fill[0].length;
		int val = 0;
		for (int i = 0; i < fill.length; i++) {
			for (int j = 0; j < fill[0].length; j++) {
				if (fill[i][j]) val++;
			}
		}
		return (double)val / (double)max;
	}
	
	/*
	 * Get a random point on the drawn path to 
	 * attempt to create more paths on.
	 */
	private Point rand_path(boolean[][] fill, boolean[][] autofill) {
		Point[] available = new Point[fill.length * fill[0].length];
		int c = 0;
		for (int i = 0; i < fill.length; i++) {
			for (int j = 0; j < fill[0].length; j++) {
				if (fill[i][j] && !autofill[i][j]) available[c++] = new Point(j, i);
			}
		}
		if (c == 0) return getStart(fill, autofill);
		Point p;
		do {
			int rnd = rand.nextInt(c);
			p = available[rnd];
		} while (canMove(p.x, p.y, fill) == 0);
		return p;
	}
	
	/*
	 * Returns a byte showing valid moves from a certain place.
	 * Bit 4 is up, bit 3 is down, bit 2 is left, and bit 1 is right.
	 * Returns 0 if there are no valid moves.
	 */
	private byte canMove(int x, int y, boolean[][] fill) {
		//                                          udlr
		byte flags =                              0b1111;
		if (y == 0 || fill[y-1][x])    flags &=   0b0111;
		if (y == fill.length-1 || fill[y+1][x]) flags &= 0b1011;
		if (x == 0 || fill[y][x-1])    flags &=   0b1101;
		if (x == fill[0].length-1 || fill[y][x+1]) flags &= 0b1110;
		return flags;
	}
	/**
	 * Set the image to be used in image mode.
	 * Sets useImg to true.
	 * @param img the image to be used in image mode.
	 */
	public void setImage(BufferedImage img) {
		this.img = img;
		needsUpdate = true;
		useImg = true;
	}
	/**
	 * Sets useImg to false, but doesn't
	 * delete img.
	 */
	public void rectMode() {
		useImg = false;
		needsUpdate = true;
	}
	/**
	 * Set path size.
	 * @param sz Size
	 */
	public void setSize(int sz) {
		this.path_size = sz;
	}
	/**
	 * Set slow mode
	 * @param b Slow flag
	 */
	public void setSlow(boolean b) {
		this.slow = b;
	}
	/**
	 * Render the maze on to a custom buffer.
	 * Used in printing.
	 * @param buffer The image to render the maze onto
	 */
	public void updateTo(BufferedImage buffer) {
		update(buffer);
	}
	/**
	 * Calculate optimal path/image size so as to not
	 * chop off any of the paths on the edges.
	 * @param buffer Buffer image to use for sizing
	 * @return New buffer size to use
	 */
	public Dimension calc_pathsize(BufferedImage buffer) {
		int w = buffer.getWidth();
		int h = buffer.getHeight();
		int last_size = path_size;
		boolean incr = false;
		int tries = 0;
		while (w % path_size < path_size-1) {
			if (incr) {
				w++;
			} else {
				w--;
			}
			if (w == 0) {
				w = buffer.getWidth();
				if (incr) {
					path_size++;
					if (path_size == 100) {
						path_size = last_size;
						incr = false;
						tries++;
						if (tries > 2)
							throw new IllegalArgumentException(buffer.getWidth() + "," + buffer.getHeight());
					}
				} else {
					path_size--;
					if (path_size == 4) {
						path_size = last_size;
						incr = true;
						tries++;
					}
				}
			}
		}
		while (h % path_size < path_size-1) {
			if (incr) {
				h++;
			} else {
				h--;
			}
			if (h == 0) {
				h = buffer.getHeight();
				if (incr) {
					path_size++;
					if (path_size == 100) {
						path_size = last_size;
						incr = false;
						tries++;
						if (tries > 2)
							throw new IllegalArgumentException(buffer.getWidth() + "," + buffer.getHeight());
					}
				} else {
					path_size--;
					if (path_size == 4) {
						path_size = last_size;
						incr = true;
						tries++;
					}
				}
			}
		}
		return new Dimension(w, h);
	}
	/**
	 * Calculate path/image size on the default buffer
	 * @return See {@link #calc_pathsize(BufferedImage)}
	 * @see #calc_pathsize(BufferedImage)
	 */
	public Dimension calc_pathsize() {
		return calc_pathsize(buffer);
	}
}
