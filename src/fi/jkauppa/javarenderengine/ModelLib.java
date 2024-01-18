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
import java.util.Arrays;
import java.util.Comparator;

import javax.imageio.ImageIO;

public class ModelLib {
	private static GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment ();
	private static GraphicsDevice gd = ge.getDefaultScreenDevice ();
	private static GraphicsConfiguration gc = gd.getDefaultConfiguration ();

	public static class Material implements Comparable<Material> {
		public String materialname;
		public VolatileImage fileimage;
		public BufferedImage snapimage;
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
	public static class Cubemap {
		public VolatileImage top=null,bottom=null,left=null,right=null,forward=null,backward=null;
		public int vres = 0; 
		public Cubemap(int vresi) {
			this.vres = vresi;
			this.top = gc.createCompatibleVolatileImage(this.vres,this.vres,Transparency.TRANSLUCENT);
			this.bottom = gc.createCompatibleVolatileImage(this.vres,this.vres,Transparency.TRANSLUCENT);
			this.left = gc.createCompatibleVolatileImage(this.vres,this.vres,Transparency.TRANSLUCENT);
			this.right = gc.createCompatibleVolatileImage(this.vres,this.vres,Transparency.TRANSLUCENT);
			this.forward = gc.createCompatibleVolatileImage(this.vres,this.vres,Transparency.TRANSLUCENT);
			this.backward = gc.createCompatibleVolatileImage(this.vres,this.vres,Transparency.TRANSLUCENT);
		}
	}
	public static class Spheremap {
		public VolatileImage equirectangular = null;
		public int hres = 0, vres = 0; 
		public Spheremap(int hresi, int vresi) {
			this.hres = hresi;
			this.vres = vresi;
			this.equirectangular = gc.createCompatibleVolatileImage(this.hres,this.vres,Transparency.TRANSLUCENT);
		}
	}
	
	public static class Position implements Comparable<Position> {public double x,y,z; public Coordinate tex; public Material mat; public int ind=-1; public Position(double xi,double yi,double zi){this.x=xi;this.y=yi;this.z=zi;}
		@Override public int compareTo(Position o){
			int k = -1;
			if (this.z>o.z) {
				k = 1;
			} else if (this.z==o.z) {
				if (this.y>o.y) {
					k = 1;
				} else if (this.y==o.y) {
					if (this.x>o.x) {
						k = 1;
					} else if (this.x==o.x) {
						k = 0;
					}
				}
			}
			return k;
		}
		@Override public boolean equals(Object o) {
			boolean k = false;
			if (o.getClass().equals(this.getClass())) {
				Position os = (Position)o;
				if ((this.x==os.x)&&(this.y==os.y)&&(this.z==os.z)) {
					k = true;
				}
			}
			return k;
		}
		public Position copy(){Position k=new Position(this.x,this.y,this.z); k.tex=this.tex; return k;}
	}
	public static class Direction implements Comparable<Direction> {public double dx,dy,dz; public Direction(double dxi,double dyi,double dzi){this.dx=dxi;this.dy=dyi;this.dz=dzi;}
		@Override public int compareTo(Direction o){
			int k = -1;
			if (this.dz>o.dz) {
				k = 1;
			} else if (this.dz==o.dz) {
				if (this.dy>o.dy) {
					k = 1;
				} else if (this.dy==o.dy) {
					if (this.dx>o.dx) {
						k = 1;
					} else if (this.dx==o.dx) {
						k = 0;
					}
				}
			}
			return k;
		}
		@Override public boolean equals(Object o) {
			boolean k = false;
			if (o.getClass().equals(this.getClass())) {
				Direction os = (Direction)o;
				if ((this.dx==os.dx)&&(this.dy==os.dy)&&(this.dz==os.dz)) {
					k = true;
				}
			}
			return k;
		}
		public Direction copy(){return new Direction(this.dx,this.dy,this.dz);}
	}
	public static class Coordinate implements Comparable<Coordinate> {public double u,v; public Coordinate(double ui,double vi){this.u=ui;this.v=vi;}
	@Override public int compareTo(Coordinate o){
		int k = -1;
		if (this.u>o.u) {
			k = 1;
		} else if (this.u==o.u) {
			if (this.v>o.v) {
				k = 1;
			} else if (this.v==o.v) {
				k = 0;
			}
		}
		return k;
	}
	@Override public boolean equals(Object o) {
		boolean k = false;
		if (o.getClass().equals(this.getClass())) {
			Coordinate os = (Coordinate)o;
			if ((this.u==os.u)&&(this.v==os.v)) {
				k = true;
			}
		}
		return k;
	}
		public Coordinate copy(){return new Coordinate(this.u,this.v);}
	}
	public static class Rotation {public double x,y,z; public Rotation(double xi,double yi,double zi){this.x=xi;this.y=yi;this.z=zi;}}
	public static class Sphere implements Comparable<Sphere> {public double x,y,z,r; public int ind=-1; public Sphere(double xi,double yi,double zi,double ri){this.x=xi;this.y=yi;this.z=zi;this.r=ri;}
		@Override public int compareTo(Sphere o) {
			int k = -1;
			if (this.z>o.z) {
				k = 1;
			} else if (this.z==o.z) {
				if (this.y>o.y) {
					k = 1;
				} else if (this.y==o.y) {
					if (this.x>o.x) {
						k = 1;
					} else if (this.x==o.x) {
						if (this.r>o.r) {
							k = 1;
						} else if (this.r==o.r) {
							k = 0;
						}
					}
				}
			}
			return k;
		}
		@Override public boolean equals(Object o) {
			boolean k = false;
			if (o.getClass().equals(this.getClass())) {
				Sphere os = (Sphere)o;
				if ((this.x==os.x)&&(this.y==os.y)&&(this.z==os.z)&&(this.r==os.r)) {
					k = true;
				}
			}
			return k;
		}
		public Sphere copy(){Sphere k = new Sphere(this.x,this.y,this.z,this.r); k.ind=this.ind; return k;}
		public static class SphereDistanceComparator implements Comparator<Sphere> {
			public Position origin;
			public SphereDistanceComparator(Position origini) {this.origin = origini;}
			@Override public int compare(Sphere o1, Sphere o2) {
				int k = 1;
				Sphere[] spheres = {o1,o2};
				Direction[] spheredir = MathLib.vectorFromPoints(this.origin, spheres);
				double[] spheredist = MathLib.vectorLength(spheredir);				
				if (spheredist[0]>spheredist[1]) {
					k = -1;
				} else if (spheredist[0]==spheredist[1]) {
					k = 0;
				}
				return k;
			}
			
		}
	}
	public static class AxisAlignedBoundingBox {public double x1,y1,z1,x2,y2,z2; public AxisAlignedBoundingBox(double x1i,double y1i,double z1i,double x2i,double y2i,double z2i){this.x1=x1i;this.y1=y1i;this.z1=z1i;this.x2=x2i;this.y2=y2i;this.z2=z2i;}}
	public static class Cuboid {public Position poslft,poslbt,posrft,posrbt,poslfb,poslbb,posrfb,posrbb; public Cuboid(Position poslfti,Position poslbti,Position posrfti,Position posrbti,Position poslfbi,Position poslbbi,Position posrfbi,Position posrbbi){this.poslft=poslfti;this.poslbt=poslbti;this.posrft=posrfti;this.posrbt=posrbti;this.poslfb=poslfbi;this.poslbb=poslbbi;this.posrfb=posrfbi;this.posrbb=posrbbi;}}
	public static class Quad {public Position pos1,pos2,pos3,pos4; public Quad(Position pos1i,Position pos2i,Position pos3i,Position pos4i){this.pos1=pos1i;this.pos2=pos2i;this.pos3=pos3i;this.pos4=pos4i;} public Quad copy(){Quad k = new Quad(this.pos1.copy(),this.pos2.copy(),this.pos3.copy(),this.pos4.copy()); return k;}}
	public static class Arc {public Position origin; public double r,ang1,ang2; public Arc(Position origini, double ri, double ang1i, double ang2i){this.origin=origini;this.r=ri;this.ang1=ang1i;this.ang2=ang2i;}}
	public static class Circle {public Position origin; public double r; public Circle(Position origini, double ri){this.origin=origini;this.r=ri;}}
	public static class Ray {public Position pos; public Direction dir; public Ray(Position posi, Direction diri){this.pos=posi;this.dir=diri;}}
	public static class Plane {public double a,b,c,d; public Plane(double ai,double bi,double ci,double di){this.a=ai;this.b=bi;this.c=ci;this.d=di;}}
	public static class Line implements Comparable<Line> {public Position pos1,pos2; public Material mat; public int ind=-1; public Line(Position pos1i,Position pos2i){this.pos1=pos1i;this.pos2=pos2i;}
		@Override public int compareTo(Line o){
			int k=-1;
			Line ts=this.sort();
			Line os=o.sort();
			if (ts.pos1.z>os.pos1.z) {
				k=1;
			}else if (ts.pos1.z==os.pos1.z) {
				if(ts.pos2.z>os.pos2.z) {
					k=1;
				} else if (ts.pos2.z==os.pos2.z) {
					if(ts.pos1.y>os.pos1.y){
						k=1;
					}else if (ts.pos1.y==os.pos1.y) {
						if(ts.pos2.y>os.pos2.y) {
							k=1;
						} else if (ts.pos2.y==os.pos2.y) {
							if(ts.pos1.x>os.pos1.x) {
								k=1;
							} else if (ts.pos1.x==os.pos1.x) {
								if (ts.pos2.x>os.pos2.x) {
									k=1;
								} else if (ts.pos2.x==os.pos2.x) {
									k=0;
								}}}}}}
			return k;
		}
		@Override public boolean equals(Object o) {
			boolean k = false;
			if (o.getClass().equals(this.getClass())) {
				Line os = ((Line)o).sort();
				Line ts=this.sort();
				if ((ts.pos1.x==os.pos1.x)&&(ts.pos1.y==os.pos1.y)&&(ts.pos1.z==os.pos1.z)&&(ts.pos2.x==os.pos2.x)&&(ts.pos2.y==os.pos2.y)&&(ts.pos2.z==os.pos2.z)) {
					k = true;
				}
			}
			return k;
		}
		public Line copy(){Line k = new Line(new Position(this.pos1.x,this.pos1.y,this.pos1.z),new Position(this.pos2.x,this.pos2.y,this.pos2.z)); k.ind=this.ind; return k;}
		public Line swap(){return new Line(this.pos2,this.pos1);}
		public Line sort(){Line k=this;if (this.pos1.compareTo(this.pos2)==1) {k=this.swap();}return k;}
	}
	public static class Tetrahedron implements Comparable<Tetrahedron> {public Position pos1,pos2,pos3,pos4; public Tetrahedron(Position pos1i,Position pos2i, Position pos3i,Position pos4i){this.pos1=pos1i;this.pos2=pos2i;this.pos3=pos3i;this.pos4=pos4i;}
		@Override public int compareTo(Tetrahedron o) {
			int k = -1;
			Position[] tposarray = {this.pos1,this.pos2,this.pos3,this.pos4};
			Position[] oposarray = {o.pos1,o.pos2,o.pos3,o.pos4};
			Arrays.sort(tposarray);
			Arrays.sort(oposarray);
			if (tposarray[0].z>oposarray[0].z) {
				k = 1;
			} else if (tposarray[0].z==oposarray[0].z) {
				if (tposarray[1].z>oposarray[1].z) {
					k = 1;
				} else if (tposarray[1].z==oposarray[1].z) {
					if (tposarray[2].z>oposarray[2].z) {
						k = 1;
					} else if (tposarray[2].z==oposarray[2].z) {
						if (tposarray[3].z>oposarray[3].z) {
							k = 1;
						} else if (tposarray[3].z==oposarray[3].z) {
							if (tposarray[0].y>oposarray[0].y) {
								k = 1;
							} else if (tposarray[0].y==oposarray[0].y) {
								if (tposarray[1].y>oposarray[1].y) {
									k = 1;
								} else if (tposarray[1].y==oposarray[1].y) {
									if (tposarray[2].y>oposarray[2].y) {
										k = 1;
									} else if (tposarray[2].y==oposarray[2].y) {
										if (tposarray[3].y>oposarray[3].y) {
											k = 1;
										} else if (tposarray[3].y==oposarray[3].y) {
											if (tposarray[0].x>oposarray[0].x) {
												k = 1;
											} else if (tposarray[0].x==oposarray[0].x) {
												if (tposarray[1].x>oposarray[1].x) {
													k = 1;
												} else if (tposarray[1].x==oposarray[1].x) {
													if (tposarray[2].x>oposarray[2].x) {
														k = 1;
													} else if (tposarray[2].x==oposarray[2].x) {
														if (tposarray[3].x>oposarray[3].x) {
															k = 1;
														} else if (tposarray[3].x==oposarray[3].x) {
															k = 0;
														}}}}}}}}}}}}
			return k;
		}
		@Override public boolean equals(Object o) {
			boolean k = false;
			if (o.getClass().equals(this.getClass())) {
				Tetrahedron co = (Tetrahedron)o;
				Position[] tp = {this.pos1,this.pos2,this.pos3,this.pos4};
				Position[] op = {co.pos1,co.pos2,co.pos3,this.pos4};
				Arrays.sort(tp);
				Arrays.sort(op);
				boolean arraytest1 = (tp[0].x==op[0].x)&&(tp[0].y==op[0].y)&&(tp[0].z==op[0].z);
				boolean arraytest2 = (tp[1].x==op[1].x)&&(tp[1].y==op[1].y)&&(tp[1].z==op[1].z);
				boolean arraytest3 = (tp[2].x==op[2].x)&&(tp[2].y==op[2].y)&&(tp[2].z==op[2].z);
				boolean arraytest4 = (tp[3].x==op[3].x)&&(tp[3].y==op[3].y)&&(tp[3].z==op[3].z);
				if ((arraytest1)&&(arraytest2)&&(arraytest3)&&(arraytest4)) {
					k = true;
				}
			}
			return k;
		}
	}
	public static class Triangle implements Comparable<Triangle> {public Position pos1,pos2,pos3; public Direction norm; public Material mat; public int ind=-1; public Triangle(Position pos1i,Position pos2i,Position pos3i){this.pos1=pos1i;this.pos2=pos2i;this.pos3=pos3i;}
		@Override public int compareTo(Triangle o) {
			int k = -1;
			Position[] tposarray = {this.pos1,this.pos2,this.pos3};
			Position[] oposarray = {o.pos1,o.pos2,o.pos3};
			Arrays.sort(tposarray);
			Arrays.sort(oposarray);
			if (tposarray[0].z>oposarray[0].z) {
				k = 1;
			} else if (tposarray[0].z==oposarray[0].z) {
				if (tposarray[1].z>oposarray[1].z) {
					k = 1;
				} else if (tposarray[1].z==oposarray[1].z) {
					if (tposarray[2].z>oposarray[2].z) {
						k = 1;
					} else if (tposarray[2].z==oposarray[2].z) {
						if (tposarray[0].y>oposarray[0].y) {
							k = 1;
						} else if (tposarray[0].y==oposarray[0].y) {
							if (tposarray[1].y>oposarray[1].y) {
								k = 1;
							} else if (tposarray[1].y==oposarray[1].y) {
								if (tposarray[2].y>oposarray[2].y) {
									k = 1;
								} else if (tposarray[2].y==oposarray[2].y) {
									if (tposarray[0].x>oposarray[0].x) {
										k = 1;
									} else if (tposarray[0].x==oposarray[0].x) {
										if (tposarray[1].x>oposarray[1].x) {
											k = 1;
										} else if (tposarray[1].x==oposarray[1].x) {
											if (tposarray[2].x>oposarray[2].x) {
												k = 1;
											} else if (tposarray[2].x==oposarray[2].x) {
												k = 0;
											}}}}}}}}}
			return k;
		}
		@Override public boolean equals(Object o) {
			boolean k = false;
			if (o.getClass().equals(this.getClass())) {
				Triangle co = (Triangle)o;
				Position[] tposarray = {this.pos1,this.pos2,this.pos3};
				Position[] oposarray = {co.pos1,co.pos2,co.pos3};
				Arrays.sort(tposarray);
				Arrays.sort(oposarray);
				if ((tposarray[0].compareTo(oposarray[0])==0)&&(tposarray[1].compareTo(oposarray[1])==0)&&(tposarray[2].compareTo(oposarray[2])==0)) {
					k = true;
				}
			}
			return k;
		}
		public Triangle copy(){Triangle k=new Triangle(this.pos1.copy(),this.pos2.copy(),this.pos3.copy());k.norm=this.norm;k.mat=this.mat;k.ind=this.ind;return k;}
	}
	public static class Entity implements Comparable<Entity> {
		public Entity[] childlist = null;
		public Triangle[] trianglelist = null;
		public Line[] linelist = null;
		public Tetrahedron[] tetrahedronlist = null;
		public Triangle[] surfacelist = null;
		public Position[] vertexlist = null;
		public Sphere sphereboundaryvolume = null;
		public AxisAlignedBoundingBox aabbboundaryvolume = null;
		public Matrix transform = null;
		public Position translation = null;
		@Override public int compareTo(Entity o) {return this.sphereboundaryvolume.compareTo(o.sphereboundaryvolume);}
	}
	public static class Matrix {public double a11,a12,a13,a21,a22,a23,a31,a32,a33;public Matrix(double a11i,double a12i,double a13i,double a21i,double a22i,double a23i,double a31i,double a32i,double a33i){this.a11=a11i;this.a12=a12i;this.a13=a13i;this.a21=a21i;this.a22=a22i;this.a23=a23i;this.a31=a31i;this.a32=a32i;this.a33=a33i;}}
	
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
