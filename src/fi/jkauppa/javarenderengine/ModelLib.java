package fi.jkauppa.javarenderengine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import fi.jkauppa.javarenderengine.MathLib.Coordinate;
import fi.jkauppa.javarenderengine.MathLib.Direction;
import fi.jkauppa.javarenderengine.MathLib.Position;
import fi.jkauppa.javarenderengine.MathLib.Triangle;

public class ModelLib {
	private static GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment ();
	private static GraphicsDevice gd = ge.getDefaultScreenDevice ();
	private static GraphicsConfiguration gc = gd.getDefaultConfiguration ();
	
	public static class Material implements Comparable<Material> {
		public String materialname;
		public VolatileImage fileimage;
		public String filename;
		public Color facecolor = null;
		public float transparency = 1.0f;
		public Material() {}
		@Override public int compareTo(Material o) {
			int k=-1;
			if(this.facecolor.getRed()>o.facecolor.getRed()) {
				k=1;
			} else if(this.facecolor.getRed()==o.facecolor.getRed()) {
				if (this.facecolor.getGreen()>o.facecolor.getGreen()) {
					k=1;
				} else if (this.facecolor.getGreen()==o.facecolor.getGreen()) {
					if (this.facecolor.getBlue()>o.facecolor.getBlue()) {
						k=1;
					} else if (this.facecolor.getBlue()==o.facecolor.getBlue()) {
						if(this.facecolor.getAlpha()>o.facecolor.getAlpha()) {
							k=1;
						} else if (this.facecolor.getAlpha()==o.facecolor.getAlpha()) {
							k=0;
						}}}}
			return k;}
		@Override public boolean equals(Object o) {
			boolean k=false;
			if (o.getClass().equals(this.getClass())){
				Material co=(Material)o;
				if(this.compareTo(co)==0){
					k=true;
				}
			}
			return k;
		}
	}
	public static class ModelFaceVertexIndex {
		public int vertexindex; 
		public int textureindex; 
		public int normalindex; 
		public ModelFaceVertexIndex(int vertexindexi, int textureindexi, int normalindexi) {this.vertexindex = vertexindexi; this.textureindex = textureindexi; this.normalindex = normalindexi;}
	}
	public static class ModelFaceIndex {
		public ModelFaceVertexIndex[] facevertexindex;
		public String usemtl;
		public ModelFaceIndex(ModelFaceVertexIndex[] facevertexindexi){this.facevertexindex=facevertexindexi;}
	}
	public static class ModelLineIndex {
		public int[] linevertexindex;
		public ModelLineIndex(int[] linevertexindexi){this.linevertexindex=linevertexindexi;}
	}
	public static class ModelObject {
		public String objectname;
		public ModelFaceIndex[] faceindex;
		public ModelLineIndex[] lineindex;
		public ModelObject(String objectnamei) {this.objectname = objectnamei;}
	}
	public static class Model {
		public String filename = null;
		public String mtllib = null;
		public Position[] vertexlist;
		public Direction[] facenormals;
		public Coordinate[] texturecoords;
		public Material[] materials;
		public ModelObject[] objects;
		public Model(String filenamei) {this.filename = filenamei;}
	}
	
	public static void saveWaveFrontOBJFile(String filename, Model model) {
		if ((filename!=null)&&(model!=null)) {
			try {
				File saveobjfile = new File(filename);
				BufferedWriter modelobjfile = new BufferedWriter(new FileWriter(saveobjfile, false));
				modelobjfile.write("mtllib "+model.mtllib);
				modelobjfile.newLine();
				for (int i=0;i<model.vertexlist.length;i++) {
					modelobjfile.write("v "+model.vertexlist[i].x+" "+model.vertexlist[i].y+" "+model.vertexlist[i].z);
					modelobjfile.newLine();
				}
				for (int i=0;i<model.facenormals.length;i++) {
					modelobjfile.write("vn "+model.facenormals[i].dx+" "+model.facenormals[i].dy+" "+model.facenormals[i].dz);
					modelobjfile.newLine();
				}
				for (int i=0;i<model.texturecoords.length;i++) {
					modelobjfile.write("vt "+model.texturecoords[i].u+" "+model.texturecoords[i].v);
					modelobjfile.newLine();
				}
		    	String lastusemtl = null;
				for (int k=0;k<model.objects.length;k++) {
					modelobjfile.newLine();
					modelobjfile.write("o "+model.objects[k].objectname);
					modelobjfile.newLine();
					if ((model.objects[k].faceindex!=null)&&(model.objects[k].faceindex.length>0)) {
						for (int j=0;j<model.objects[k].faceindex.length;j++) {
							if ((j==0)||(!model.objects[k].faceindex[j].usemtl.equals(lastusemtl))) {
								modelobjfile.write("usemtl "+model.objects[k].faceindex[j].usemtl);
								modelobjfile.newLine();
								lastusemtl = model.objects[k].faceindex[j].usemtl;
							}
							modelobjfile.write("f ");
							for (int i=0;i<model.objects[k].faceindex[j].facevertexindex.length;i++) {
								if (i>0) {modelobjfile.write(" ");}
								modelobjfile.write(""+model.objects[k].faceindex[j].facevertexindex[i].vertexindex);
								modelobjfile.write("/");
								modelobjfile.write(""+model.objects[k].faceindex[j].facevertexindex[i].textureindex);
								modelobjfile.write("/");
								modelobjfile.write(""+model.objects[k].faceindex[j].facevertexindex[i].normalindex);
							}
							modelobjfile.newLine();
						}
					}
					if ((model.objects[k].lineindex!=null)&&(model.objects[k].lineindex.length>0)) {
						for (int j=0;j<model.objects[k].lineindex.length;j++) {
							modelobjfile.write("l");
							for (int i=0;i<model.objects[k].lineindex[j].linevertexindex.length;i++) {
								modelobjfile.write(" "+model.objects[k].lineindex[j].linevertexindex[i]);
							}
							modelobjfile.newLine();
						}
					}
				}
				modelobjfile.close();
				saveWaveFrontMTLFile(new File(saveobjfile.getParent(), model.mtllib).getPath(), model);
			} catch(Exception ex){ex.printStackTrace();}
		}
	}
	
	public static void saveWaveFrontMTLFile(String filename, Model model) {
		if ((filename!=null)&&(model!=null)) {
			try {
				File savemtlfile = new File(filename);
				BufferedWriter modelobjfile = new BufferedWriter(new FileWriter(savemtlfile, false));
				for (int i=0;i<model.materials.length;i++) {
					modelobjfile.write("newmtl "+model.materials[i].materialname);
					modelobjfile.newLine();
					modelobjfile.write("Ns 250.000000");
					modelobjfile.newLine();
					modelobjfile.write("Ka 1.000000 1.000000 1.000000");
					modelobjfile.newLine();
					if (model.materials[i].facecolor!=null) {
						float[] rgbcolor = model.materials[i].facecolor.getRGBColorComponents(new float[3]);
						modelobjfile.write("Kd "+rgbcolor[0]+" "+rgbcolor[1]+" "+rgbcolor[2]);
						modelobjfile.newLine();
					}
					modelobjfile.write("Ks 0.500000 0.500000 0.500000");
					modelobjfile.newLine();
					modelobjfile.write("Ke 0.000000 0.000000 0.000000");
					modelobjfile.newLine();
					modelobjfile.write("Ni 1.450000");
					modelobjfile.newLine();
					modelobjfile.write("d "+model.materials[i].transparency);
					modelobjfile.newLine();
					modelobjfile.write("illum 2");
					modelobjfile.newLine();
					if (model.materials[i].fileimage!=null) {
						modelobjfile.write("map_Kd "+model.materials[i].filename);
						modelobjfile.newLine();
					}
					modelobjfile.newLine();
				}
				modelobjfile.close();
			} catch(Exception ex){ex.printStackTrace();}
		}
	}

	public static Model loadWaveFrontOBJFile(String filename, boolean loadresourcefromjar) {
		Model k = null;
		if (filename!=null) {
			BufferedReader modelobjfile = null;
			try {
				File loadobjfile = new File(filename);
				if (loadresourcefromjar) {
					modelobjfile = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(loadobjfile.getPath().replace(File.separatorChar, '/'))));
				}else {
					modelobjfile = new BufferedReader(new FileReader(loadobjfile));
				}
				if (modelobjfile!=null) {
					k = new Model(filename);
					ArrayList<ModelObject> modelobjects = new ArrayList<ModelObject>();
					ArrayList<Position> modelvertexlist = new ArrayList<Position>();
					ArrayList<Direction> modelfacenormals = new ArrayList<Direction>();
					ArrayList<Coordinate> modeltexturecoords = new ArrayList<Coordinate>();
			    	ArrayList<ModelFaceIndex> modelfaceindex = new ArrayList<ModelFaceIndex>(); 
			    	ArrayList<ModelLineIndex> modellineindex = new ArrayList<ModelLineIndex>();
			    	String lastusemtl = null;
					String fline = null;
					while((fline=modelobjfile.readLine())!=null) {
						fline = fline.trim();
					    if (fline.toLowerCase().startsWith("#")) {
					    }else if (fline.toLowerCase().startsWith("mtllib ")) {
					    	String farg = fline.substring(7).trim();
					    	File loadmtlfile = new File(loadobjfile.getParent(),farg);
					    	k.mtllib = farg;
					    	k.materials = loadWaveFrontMTLFile(loadmtlfile.getPath(), loadresourcefromjar);
					    }else if (fline.toLowerCase().startsWith("o ")) {
					    	if (modelobjects.size()>0) {
					    		modelobjects.get(modelobjects.size()-1).faceindex = modelfaceindex.toArray(new ModelFaceIndex[modelfaceindex.size()]);
					    		modelobjects.get(modelobjects.size()-1).lineindex = modellineindex.toArray(new ModelLineIndex[modellineindex.size()]);
					    	}
					    	String farg = fline.substring(2).trim();
					    	modelobjects.add(new ModelObject(farg));
					    	modelfaceindex = new ArrayList<ModelFaceIndex>();
					    	modellineindex = new ArrayList<ModelLineIndex>();
					    }else if (fline.toLowerCase().startsWith("v ")) {
					    	String farg = fline.substring(2).trim();
					    	String[] fargsplit = farg.split(" ");
					    	modelvertexlist.add(new Position(Double.parseDouble(fargsplit[0]), Double.parseDouble(fargsplit[1]), Double.parseDouble(fargsplit[2])));
					    }else if (fline.toLowerCase().startsWith("vn ")) {
					    	String farg = fline.substring(3).trim();
					    	String[] fargsplit = farg.split(" ");
					    	modelfacenormals.add(new Direction(Double.parseDouble(fargsplit[0]), Double.parseDouble(fargsplit[1]), Double.parseDouble(fargsplit[2])));
					    }else if (fline.toLowerCase().startsWith("vt ")) {
					    	String farg = fline.substring(3).trim();
					    	String[] fargsplit = farg.split(" ");
					    	modeltexturecoords.add(new Coordinate(Double.parseDouble(fargsplit[0]), Double.parseDouble(fargsplit[1])));
					    }else if (fline.toLowerCase().startsWith("usemtl ")) {
					    	String farg = fline.substring(7).trim();
					    	lastusemtl = farg;
					    }else if (fline.toLowerCase().startsWith("l ")) {
					    	String farg = fline.substring(2).trim();
					    	String[] fargsplit = farg.split(" ");
					    	int[] modellinevertexindex = new int[fargsplit.length]; 
					    	for (int i=0;i<fargsplit.length;i++) {
					    		modellinevertexindex[i] = Integer.parseInt(fargsplit[i]);
					    	}
					    	modellineindex.add(new ModelLineIndex(modellinevertexindex));
					    }else if (fline.toLowerCase().startsWith("f ")) {
					    	String farg = fline.substring(2).trim();
					    	String[] fargsplit = farg.split(" ");
					    	ArrayList<ModelFaceVertexIndex> modelfacevertexindex = new ArrayList<ModelFaceVertexIndex>(); 
					    	for (int i=0;i<fargsplit.length;i++) {
						    	String[] fargsplit2 = fargsplit[i].split("/");
						    	modelfacevertexindex.add(new ModelFaceVertexIndex(Integer.parseInt(fargsplit2[0]),Integer.parseInt(fargsplit2[1]),Integer.parseInt(fargsplit2[2])));
					    	}
					    	ModelFaceIndex newmodelfaceindex = new ModelFaceIndex(modelfacevertexindex.toArray(new ModelFaceVertexIndex[modelfacevertexindex.size()]));
					    	newmodelfaceindex.usemtl = lastusemtl;
					    	modelfaceindex.add(newmodelfaceindex);
					    }
					}
			    	if (modelobjects.size()>0) {
			    		modelobjects.get(modelobjects.size()-1).faceindex = modelfaceindex.toArray(new ModelFaceIndex[modelfaceindex.size()]);
			    		modelobjects.get(modelobjects.size()-1).lineindex = modellineindex.toArray(new ModelLineIndex[modellineindex.size()]);
			    	}
					k.objects = modelobjects.toArray(new ModelObject[modelobjects.size()]);
					k.vertexlist = modelvertexlist.toArray(new Position[modelvertexlist.size()]);
					k.facenormals = modelfacenormals.toArray(new Direction[modelfacenormals.size()]);
					k.texturecoords = modeltexturecoords.toArray(new Coordinate[modeltexturecoords.size()]);
				}
				modelobjfile.close();
			} catch(Exception ex) {ex.printStackTrace();}
		}
		return k;
	}
	
	public static Material[] loadWaveFrontMTLFile(String filename, boolean loadresourcefromjar) {
		Material[] k = null;
		if (filename!=null) {
			BufferedReader modelmtlfile = null;
			try {
				File loadmtlfile = new File(filename);
				if (loadresourcefromjar) {
					modelmtlfile = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(loadmtlfile.getPath().replace(File.separatorChar, '/'))));
				}else {
					modelmtlfile = new BufferedReader(new FileReader(loadmtlfile));
				}
				if (modelmtlfile!=null) {
					ArrayList<Material> modelmaterials = new ArrayList<Material>();
					String fline = null;
					while((fline=modelmtlfile.readLine())!=null) {
						fline = fline.trim();
					    if (fline.toLowerCase().startsWith("#")) {
					    }else if (fline.toLowerCase().startsWith("newmtl ")) {
					    	String farg = fline.substring(7).trim();
					    	modelmaterials.add(new Material());
					    	modelmaterials.get(modelmaterials.size()-1).materialname = farg;
					    }else if (fline.toLowerCase().startsWith("map_kd ")) {
					    	String farg = fline.substring(7).trim();
					    	File loadimgfile = new File(loadmtlfile.getParent(),farg);
					    	modelmaterials.get(modelmaterials.size()-1).filename = farg;
					    	BufferedImage fileloadimage = null;
							if (loadresourcefromjar) {
								fileloadimage = ImageIO.read(new BufferedInputStream(ClassLoader.getSystemClassLoader().getResourceAsStream(loadimgfile.getPath().replace(File.separatorChar, '/'))));
							}else {
								fileloadimage = ImageIO.read(loadimgfile);
							}
					    	VolatileImage filestoreimage = gc.createCompatibleVolatileImage(fileloadimage.getWidth(),fileloadimage.getHeight(), Transparency.TRANSLUCENT);
					    	Graphics2D filestoreimagegfx = filestoreimage.createGraphics();
					    	filestoreimagegfx.drawImage(fileloadimage, 0, 0, null);
					    	modelmaterials.get(modelmaterials.size()-1).fileimage = filestoreimage;
					    }else if (fline.toLowerCase().startsWith("kd ")) {
					    	String farg = fline.substring(3).trim();
					    	String[] fargsplit = farg.split(" ");
					    	modelmaterials.get(modelmaterials.size()-1).facecolor = new Color(Float.parseFloat(fargsplit[0]), Float.parseFloat(fargsplit[1]), Float.parseFloat(fargsplit[2]));
					    }else if (fline.toLowerCase().startsWith("d ")) {
					    	String farg = fline.substring(2).trim();
					    	modelmaterials.get(modelmaterials.size()-1).transparency = Float.parseFloat(farg);
					    }
					}
					k = modelmaterials.toArray(new Material[modelmaterials.size()]);
				}
				modelmtlfile.close();
			} catch(Exception ex) {ex.printStackTrace();}
		}
		return k;
	}

	public static void saveSTLFile(String filename, Triangle[] model, String objectname) {
		if ((filename!=null)&&(model!=null)) {
			try {
				File savestlfile = new File(filename);
				BufferedWriter modelstlfile = new BufferedWriter(new FileWriter(savestlfile, false));
				modelstlfile.write("solid "+objectname);
				modelstlfile.newLine();
				for (int i=0;i<model.length;i++) {
					modelstlfile.write("facet normal "+String.format("%1.4e",model[i].norm.dx).replace(',','.')+" "+String.format("%1.4e",model[i].norm.dy).replace(',','.')+" "+String.format("%1.4e",model[i].norm.dz).replace(',','.'));
					modelstlfile.newLine();
					modelstlfile.write("\touter loop");
					modelstlfile.newLine();
					modelstlfile.write("\t\tvertex "+String.format("%1.4e",model[i].pos1.x).replace(',','.')+" "+String.format("%1.4e",model[i].pos1.y).replace(',','.')+" "+String.format("%1.4e",model[i].pos1.z).replace(',','.'));
					modelstlfile.newLine();
					modelstlfile.write("\t\tvertex "+String.format("%1.4e",model[i].pos2.x).replace(',','.')+" "+String.format("%1.4e",model[i].pos2.y).replace(',','.')+" "+String.format("%1.4e",model[i].pos2.z).replace(',','.'));
					modelstlfile.newLine();
					modelstlfile.write("\t\tvertex "+String.format("%1.4e",model[i].pos3.x).replace(',','.')+" "+String.format("%1.4e",model[i].pos3.y).replace(',','.')+" "+String.format("%1.4e",model[i].pos3.z).replace(',','.'));
					modelstlfile.newLine();
					modelstlfile.write("\tendloop");
					modelstlfile.newLine();
					modelstlfile.write("endfacet");
					modelstlfile.newLine();
				}
				modelstlfile.write("endsolid "+objectname);
				modelstlfile.newLine();
				modelstlfile.close();
			} catch(Exception ex){ex.printStackTrace();}
		}
	}
	
	public static Triangle[] loadSTLFile(String filename, boolean loadresourcefromjar) {
		Triangle[] k = null;
		if (filename!=null) {
			BufferedReader modelstlfile = null;
			try {
				File loadstlfile = new File(filename);
				if (loadresourcefromjar) {
					modelstlfile = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(loadstlfile.getPath().replace(File.separatorChar, '/'))));
				}else {
					modelstlfile = new BufferedReader(new FileReader(loadstlfile));
				}
				if (modelstlfile!=null) {
					ArrayList<Triangle> modeltriangles = new ArrayList<Triangle>();
					String fline = null;
					String objectname = null;
					Triangle lasttri = null;
					Direction lastnorm = null;
					Position lastpos1 = null;
					Position lastpos2 = null;
					Position lastpos3 = null;
					while((fline=modelstlfile.readLine())!=null) {
						fline = fline.trim();
					    if (fline.toLowerCase().startsWith("#")) {
					    }else if (fline.toLowerCase().startsWith("solid")) {
					    	String farg = fline.substring(5).trim();
					    	objectname = farg;
					    	objectname = objectname.trim(); 
					    }else if (fline.toLowerCase().startsWith("facet normal ")) {
					    	String farg = fline.substring(13).trim();
					    	String[] fargsplit = farg.split(" ");
					    	lastnorm = new Direction(Double.parseDouble(fargsplit[0]),Double.parseDouble(fargsplit[1]),Double.parseDouble(fargsplit[2]));
					    }else if (fline.toLowerCase().startsWith("outer loop")) {
					    }else if (fline.toLowerCase().startsWith("vertex ")) {
					    	String farg = fline.substring(7).trim();
					    	String[] fargsplit = farg.split(" ");
					    	Position newpos = new Position(Double.parseDouble(fargsplit[0]),Double.parseDouble(fargsplit[1]),Double.parseDouble(fargsplit[2]));
					    	if (lastpos1==null) {
					    		lastpos1 = newpos; 
					    	} else if (lastpos2==null) {
					    		lastpos2 = newpos;
					    	} else if (lastpos3==null) {
					    		lastpos3 = newpos;
						    }
					    }else if (fline.toLowerCase().startsWith("endloop")) {
					    	lasttri = new Triangle(lastpos1,lastpos2,lastpos3);
							lastpos1 = null;
							lastpos2 = null;
							lastpos3 = null;
					    }else if (fline.toLowerCase().startsWith("endfacet")) {
					    	lasttri.norm = lastnorm;
					    	modeltriangles.add(lasttri);
							lastnorm = null;
					    }else if (fline.toLowerCase().startsWith("endsolid")) {
					    }
					}
					k = modeltriangles.toArray(new Triangle[modeltriangles.size()]);
				}
				modelstlfile.close();
			} catch(Exception ex) {ex.printStackTrace();}
		}
		return k;
	}
}
