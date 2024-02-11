package fi.jkauppa.javarenderengine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;

public class UtilLib {
	private static GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	private static GraphicsDevice gd = ge.getDefaultScreenDevice();
	private static GraphicsConfiguration gc = gd.getDefaultConfiguration();
	
	public static class ImageFileFilters  {
		public static class PNGFileFilter extends FileFilter {
			@Override public boolean accept(File f) {return (f.isDirectory())||(f.getName().endsWith(".png"));}
			@Override public String getDescription() {return "PNG Image file";}
		}
		public static class JPGFileFilter extends FileFilter {
			@Override public boolean accept(File f) {return (f.isDirectory())||(f.getName().endsWith(".jpg"))||(f.getName().endsWith(".jpeg"));}
			@Override public String getDescription() {return "JPG Image file";}
		}
		public static class GIFFileFilter extends FileFilter {
			@Override public boolean accept(File f) {return (f.isDirectory())||(f.getName().endsWith(".gif"));}
			@Override public String getDescription() {return "GIF Image file";}
		}
		public static class BMPFileFilter extends FileFilter {
			@Override public boolean accept(File f) {return (f.isDirectory())||(f.getName().endsWith(".bmp"));}
			@Override public String getDescription() {return "BMP Image file";}
		}
		public static class WBMPFileFilter extends FileFilter {
			@Override public boolean accept(File f) {return (f.isDirectory())||(f.getName().endsWith(".wbmp"));}
			@Override public String getDescription() {return "WBMP Image file";}
		}
	}
	public static class ModelFileFilters  {
		public static class OBJFileFilter extends FileFilter {
			@Override public boolean accept(File f) {return (f.isDirectory())||(f.getName().endsWith(".obj"));}
			@Override public String getDescription() {return "OBJ Model file";}
		}
		public static class STLFileFilter extends FileFilter {
			@Override public boolean accept(File f) {return (f.isDirectory())||(f.getName().endsWith(".stl"));}
			@Override public String getDescription() {return "STL Model file";}
		}
	}
	public static VolatileImage loadImage(String filename, boolean loadresourcefromjar) {
		VolatileImage k = null;
		if (filename!=null) {
			try {
				File imagefile = new File(filename);
				BufferedInputStream imagefilestream = null;
				if (loadresourcefromjar) {
					imagefilestream = new BufferedInputStream(ClassLoader.getSystemClassLoader().getResourceAsStream(imagefile.getPath().replace(File.separatorChar, '/')));
				}else {
					imagefilestream = new BufferedInputStream(new FileInputStream(imagefile));
				}
				BufferedImage loadimage = ImageIO.read(imagefilestream);
				if (loadimage!=null) {
					VolatileImage loadimagevolatile = gc.createCompatibleVolatileImage(loadimage.getWidth(), loadimage.getHeight(), Transparency.TRANSLUCENT);
					Graphics2D loadimagevolatilegfx = loadimagevolatile.createGraphics();
					loadimagevolatilegfx.setComposite(AlphaComposite.Src);
					loadimagevolatilegfx.drawImage(loadimage, 0, 0, null);
					loadimagevolatilegfx.dispose();
					k = loadimagevolatile;
				}
				imagefilestream.close();
			} catch (Exception ex) {ex.printStackTrace();}
		}
		return k;
	}
	
	public static VolatileImage flipImage(VolatileImage image, boolean horizontal, boolean vertical) {
		VolatileImage k = gc.createCompatibleVolatileImage(image.getWidth(), image.getHeight(), Transparency.TRANSLUCENT);
		Graphics2D rigfx = k.createGraphics();
		rigfx.setComposite(AlphaComposite.Src);
		rigfx.setColor(new Color(0.0f,0.0f,0.0f,0.0f));
		rigfx.setPaint(null);
		rigfx.setClip(null);
		rigfx.fillRect(0, 0, image.getWidth(), image.getHeight());
		if (horizontal&&vertical) {
			rigfx.scale(-1, -1);
			rigfx.translate(-image.getWidth(), -image.getHeight());
		} else if (horizontal) {
			rigfx.scale(-1, 1);
			rigfx.translate(-image.getWidth(), 0);
		} else if (vertical) {
			rigfx.scale(1, -1);
			rigfx.translate(0, -image.getHeight());
		}
		rigfx.drawImage(image, 0, 0, null);
		rigfx.dispose();
		return k;
	}

	public static class DoubleSort implements Comparable<DoubleSort> {
		public Double value;
		public int ind;
		public DoubleSort(double valuei) {this.value = valuei;}
		@Override public int compareTo(DoubleSort o) {
			return this.value.compareTo(o.value);
		}
	}
	public static int[] indexSort(double[] data) {
		int[] k = null;
		if ((data!=null)&&(data.length>0)) {
			k = new int[data.length];
			DoubleSort[] sorteddouble = new DoubleSort[data.length];
			for (int i=0;i<sorteddouble.length;i++) {
				sorteddouble[i] = new DoubleSort(data[i]);
				sorteddouble[i].ind = i;
			}
			Arrays.sort(sorteddouble);
			for (int i=0;i<sorteddouble.length;i++) {
				k[i] = sorteddouble[i].ind;
			}
		}
		return k;
	}
	public static double[] indexValues(double[] data, int[] idx) {
		double[] k = null;
		if ((data!=null)&&(idx!=null)&&(data.length>0)&&(idx.length>0)&&(data.length==idx.length)) {
			k = new double[data.length];
			for (int i=0;i<data.length;i++) {
				k[i] = data[idx[i]];
			}
		}
		return k;
	}

	public static int snapToGrid(int coordinate, int gridstep) {
		return gridstep*(int)Math.round(((double)coordinate)/((double)gridstep));
	}

    static class ImageTransferable implements Transferable {
        private Image image;
        public ImageTransferable (Image imagei) {this.image=imagei;}
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {if (isDataFlavorSupported(flavor)) {return image;}else{throw new UnsupportedFlavorException(flavor);}}
        public boolean isDataFlavorSupported (DataFlavor flavor) {return flavor==DataFlavor.imageFlavor;}
        public DataFlavor[] getTransferDataFlavors () {return new DataFlavor[] {DataFlavor.imageFlavor};}
    }	
}
