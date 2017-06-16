package maze_printer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JOptionPane;

import maze_printer.debug.CUtils;

public class Print {
	private static int nPages;
	public static void print(PreviewPanel preview) {
		preview.setSlow(false);
		
		PrinterJob job = PrinterJob.getPrinterJob();
		
		job.setPrintable(new Printable() {

			@Override
			public int print(Graphics g, PageFormat fmt,
					int pageIndex) throws PrinterException {
				if (pageIndex >= nPages)
					return NO_SUCH_PAGE;
				
				CUtils.printf("Printing %d/%d page(s)...", pageIndex, nPages);
				BufferedImage buffer = new BufferedImage((int)(fmt.getImageableWidth() - 48), (int)(fmt.getImageableHeight()), BufferedImage.TYPE_INT_ARGB);
				preview.updateTo(buffer);
				g.drawImage(buffer, (int)fmt.getImageableX() + 48, (int)fmt.getImageableY(), null);
				
				return PAGE_EXISTS;
			}
			
		});
		
		boolean print = job.printDialog();
		boolean success = false;
		if (!print) return;
		while (!success) {
			try {
				nPages = Integer.valueOf(JOptionPane.showInputDialog("Enter number of pages to print with this configuration"));
				success = true;
			} catch (NumberFormatException e) {
				success = false;
			}
		}
		
		try {
			job.print();
		} catch (PrinterException e) {
			System.err.println("Error occured while printing");
		}
	}
}
