package fi.jkauppa.javarenderengine;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class UtilLib {
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
}
