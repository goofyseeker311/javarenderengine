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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import fi.jkauppa.javarenderengine.ModelLib.Entity;

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
	
    public static void loadNativeLibrary(String[] filename, boolean loadresourcefromjar) {
		try {
			Path tempdir = Files.createTempDirectory("loadlib");
			String[] temppath = new String[filename.length];
			for (int i=0;i<filename.length;i++) {
				if (loadresourcefromjar) {
					File libraryfile = new File(filename[i]);
					String libraryfilename = libraryfile.getName();
					File tempfile = new File(tempdir.toAbsolutePath().toString(),libraryfilename);
					BufferedInputStream libraryfilestream = null;
					libraryfilestream = new BufferedInputStream(ClassLoader.getSystemClassLoader().getResourceAsStream(libraryfile.getPath().replace(File.separatorChar, '/')));
					Files.copy(libraryfilestream, tempfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			    	libraryfilestream.close();
			    	temppath[i] = tempfile.toPath().toAbsolutePath().toString();
				}
			}
			for (int i=0;i<filename.length;i++) {
				System.load(temppath[i]);
			}
		} catch (Exception ex) {ex.printStackTrace();}
    }

    public static JFileChooser createModelFileChooser() {
    	JFileChooser filechooser = new JFileChooser();
    	ModelFileFilters.OBJFileFilter objfilefilter = new ModelFileFilters.OBJFileFilter();
		filechooser.addChoosableFileFilter(objfilefilter);
		filechooser.addChoosableFileFilter(new ModelFileFilters.STLFileFilter());
		filechooser.setFileFilter(objfilefilter);
		filechooser.setAcceptAllFileFilterUsed(false);
		return filechooser;
    }
    
    public static void saveModelFormat(String filename, Entity[] entitylist, FileFilter savefileformat, boolean savesurfaceonly) {
    	String savefilename = filename;
		if (savefileformat.getClass().equals(ModelFileFilters.STLFileFilter.class)) {
			if (!savefilename.toLowerCase().endsWith(".stl")) {savefilename = savefilename.concat(".stl");}
			Entity saveentity = new Entity();
			saveentity.childlist = entitylist;
			ModelLib.saveSTLFileEntity(savefilename, saveentity, "JREOBJ");
		} else {
			if (!savefilename.toLowerCase().endsWith(".obj")) {savefilename = savefilename.concat(".obj");}
			Entity saveentity = new Entity();
			saveentity.childlist = entitylist;
			ModelLib.saveOBJFileEntity(savefilename, saveentity, savesurfaceonly);
		}
    }
    public static Entity loadModelFormat(String filename, FileFilter loadfileformat, boolean loadresourcefromjar) {
    	Entity loadentity = null;
		if (loadfileformat.getClass().equals(ModelFileFilters.STLFileFilter.class)) {
			loadentity = ModelLib.loadSTLFileEntity(filename, loadresourcefromjar);
		} else {
			loadentity = ModelLib.loadOBJFileEntity(filename, loadresourcefromjar);
		}
		return loadentity;
    }
    
    public static JFileChooser createImageFileChooser() {
    	JFileChooser imagechooser = new JFileChooser();
    	ImageFileFilters.PNGFileFilter pngfilefilter = new ImageFileFilters.PNGFileFilter();
		imagechooser.addChoosableFileFilter(pngfilefilter);
		imagechooser.addChoosableFileFilter(new ImageFileFilters.JPGFileFilter());
		imagechooser.addChoosableFileFilter(new ImageFileFilters.GIFFileFilter());
		imagechooser.addChoosableFileFilter(new ImageFileFilters.BMPFileFilter());
		imagechooser.addChoosableFileFilter(new ImageFileFilters.WBMPFileFilter());
		imagechooser.setFileFilter(pngfilefilter);
		imagechooser.setAcceptAllFileFilterUsed(false);
		return imagechooser;
    }

    public static void saveImageFormat(String filename, VolatileImage image, FileFilter savefileformat) {
    	String savefilename = filename;
		if (savefileformat.getClass().equals(ImageFileFilters.JPGFileFilter.class)) {
			if ((!savefilename.toLowerCase().endsWith(".jpg"))&&(!savefilename.toLowerCase().endsWith(".jpeg"))) {savefilename = savefilename.concat(".jpg");}
			UtilLib.saveImage(savefilename, image, "JPG");
		} else if (savefileformat.getClass().equals(ImageFileFilters.GIFFileFilter.class)) {
			if (!savefilename.toLowerCase().endsWith(".gif")) {savefilename = savefilename.concat(".gif");}
			UtilLib.saveImage(savefilename, image, "GIF");
		} else if (savefileformat.getClass().equals(ImageFileFilters.BMPFileFilter.class)) {
			if (!savefilename.toLowerCase().endsWith(".bmp")) {savefilename = savefilename.concat(".bmp");}
			UtilLib.saveImage(savefilename, image, "BMP");
		} else if (savefileformat.getClass().equals(ImageFileFilters.WBMPFileFilter.class)) {
			if (!savefilename.toLowerCase().endsWith(".wbmp")) {savefilename = savefilename.concat(".wbmp");}
			UtilLib.saveImage(savefilename, image, "WBMP");
		} else {
			if (!savefilename.toLowerCase().endsWith(".png")) {savefilename = savefilename.concat(".png");}
			UtilLib.saveImage(savefilename, image, "PNG");
		}
    }
    
	public static void saveImage(String filename, VolatileImage image, String format) {
		File savefile = new File(filename);
		try {ImageIO.write(image.getSnapshot(), format, savefile);} catch (Exception ex) {ex.printStackTrace();}
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

	public static class ObjectSort<T> implements Comparator<Integer> {
		private Comparable<T>[] data;
		private Comparator<T> comp;
		public ObjectSort(Comparable<T>[] datai, Comparator<T> compi) {this.data=datai;this.comp=compi;}
		@SuppressWarnings("unchecked")
		@Override public int compare(Integer o1, Integer o2) {
			int compval = -1;
			if (this.comp!=null) {
				compval = this.comp.compare((T)data[o1],(T)data[o2]);
			} else {
				compval = data[o1].compareTo((T)data[o2]);
			}
	        return compval;
		}
	}
	public static <T> Integer[] objectIndexSort(Comparable<T>[] data, Comparator<T> comp) {
		Integer[] k = null;
		if ((data!=null)&&(data.length>0)) {
			Integer[] indices = new Integer[data.length];
			for (int i = 0; i < indices.length; i++) {
			    indices[i] = i;
			}
			ObjectSort<T> comparator = new ObjectSort<T>(data, comp);
			Arrays.sort(indices, comparator);
			k = indices;
		}
		return k;
	}

	static class ImageTransferable implements Transferable {
        private Image image;
        public ImageTransferable (Image imagei) {this.image=imagei;}
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {if (isDataFlavorSupported(flavor)) {return image;}else{throw new UnsupportedFlavorException(flavor);}}
        public boolean isDataFlavorSupported (DataFlavor flavor) {return flavor==DataFlavor.imageFlavor;}
        public DataFlavor[] getTransferDataFlavors () {return new DataFlavor[] {DataFlavor.imageFlavor};}
    }	
}
