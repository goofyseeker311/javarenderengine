package fi.jkauppa.javarenderengine;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;

public class UtilLib {
	private static GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment ();
	private static GraphicsDevice gd = ge.getDefaultScreenDevice ();
	private static GraphicsConfiguration gc = gd.getDefaultConfiguration ();
	
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

}
