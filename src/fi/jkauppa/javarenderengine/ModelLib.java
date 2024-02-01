package fi.jkauppa.javarenderengine;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.Transparency;
import java.awt.geom.Line2D;
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

import fi.jkauppa.javarenderengine.ModelLib.Sphere.SphereDistanceComparator;

public class ModelLib {
	private static GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment ();
	private static GraphicsDevice gd = ge.getDefaultScreenDevice ();
	private static GraphicsConfiguration gc = gd.getDefaultConfiguration ();

	public static class Material implements Comparable<Material> {
		public String materialname = null;
		public VolatileImage fileimage = null;
		public BufferedImage snapimage = null;
		public String filename = null;
		public Color facecolor = null;
		public float transparency = 1.0f;
		public Material() {}
		public Material(Color facecolori, float transparencyi) {this.facecolor=facecolori;this.transparency=transparencyi;}
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
							if (this.fileimage==o.fileimage) {
								k=0;
							} else if ((this.fileimage!=null)&&(o.fileimage!=null)) {
								k=this.fileimage.toString().compareTo(o.fileimage.toString());
							} else if (this.fileimage!=null) {
								k=1;
							}
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
		public Material copy(){
			Material k=new Material(this.facecolor,this.transparency);
			k.fileimage = null;
			if (this.fileimage!=null) {
				k.fileimage = gc.createCompatibleVolatileImage(this.fileimage.getWidth(),this.fileimage.getHeight(),Transparency.TRANSLUCENT);
				Graphics2D cgfx=k.fileimage.createGraphics();
				cgfx.drawImage(this.fileimage, 0, 0, null);
				cgfx.dispose();
			}
			return k;
		}
	}

	public static class RenderView {
		public VolatileImage renderimage = null;
		public BufferedImage snapimage = null;
		public Cubemap cubemap = null;
		public Spheremap spheremap = null;
		public double[][] sbuffer = null;
		public double[][] zbuffer = null;
		public Entity[][] ebuffer = null;
		public Triangle[][] tbuffer = null;
		public Coordinate[][] cbuffer = null;
		public Triangle[] mouseovertriangle = null;
		public Position[] mouseoververtex = null;
		public Line[] mouseoverline = null;
		public int mouselocationx=0,mouselocationy=0; 
		public Position pos;
		public Matrix rot;
		public int renderwidth=0, renderheight=0;
		public int rendersize=0;
		public double hfov=0.0f, vfov=43.0f;
		public boolean rendered = false;
		public boolean unlit = false;
		public Direction[] dirs;
		public Direction[][] rays;
		public Plane[] planes;
	}
	
	public static class Cubemap {
		public RenderView topview=null,bottomview=null,leftview=null,rightview=null,forwardview=null,backwardview=null;
	}
	public static class Spheremap {
		public RenderView sphereview=null;
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
		public Position invert(){Position k=this.copy(); k.x=-k.x;k.y=-k.y;k.z=-k.z; return k;}
		public boolean isZero(){return (this.x==0.0f)&&(this.y==0.0f)&&(this.z==0.0f);}
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
		public Direction invert(){return new Direction(-this.dx,-this.dy,-this.dz);}
		public boolean isZero(){return (this.dx==0.0f)&&(this.dy==0.0f)&&(this.dz==0.0f);}
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
		public Coordinate invert(){return new Coordinate(-this.u,-this.v);}
		public boolean isZero(){return (this.u==0.0f)&&(this.v==0.0f);}
	}
	public static class Rotation {public double x,y,z; public Rotation(double xi,double yi,double zi){this.x=xi;this.y=yi;this.z=zi;}
		@Override public boolean equals(Object o) {
			boolean k = false;
			if (o.getClass().equals(this.getClass())) {
				Rotation os = (Rotation)o;
				if ((this.x==os.x)&&(this.y==os.y)&&(this.z==os.z)) {
					k = true;
				}
			}
			return k;
		}
		public Rotation copy(){return new Rotation(this.x,this.y,this.z);}
	}
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
				int k = -1;
				Sphere[] spheres = {o1,o2};
				Direction[] spheredir = MathLib.vectorFromPoints(this.origin, spheres);
				double[] spheredist = MathLib.vectorLength(spheredir);				
				if (spheredist[0]>spheredist[1]) {
					k = 1;
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
		public Position[] vertexlist = null;
		public Sphere sphereboundaryvolume = null;
		public AxisAlignedBoundingBox aabbboundaryvolume = null;
		public Matrix transform = null;
		public Position translation = null;
		@Override public int compareTo(Entity o) {return this.sphereboundaryvolume.compareTo(o.sphereboundaryvolume);}
	}
	public static class Matrix {public double a11,a12,a13,a21,a22,a23,a31,a32,a33; public Matrix(double a11i,double a12i,double a13i,double a21i,double a22i,double a23i,double a31i,double a32i,double a33i){this.a11=a11i;this.a12=a12i;this.a13=a13i;this.a21=a21i;this.a22=a22i;this.a23=a23i;this.a31=a31i;this.a32=a32i;this.a33=a33i;}
		@Override public boolean equals(Object o) {
			boolean k = false;
			if (o.getClass().equals(this.getClass())) {
				Matrix co = (Matrix)o;
				if ((this.a11==co.a11)&&(this.a12==co.a12)&&(this.a13==co.a13)&&(this.a21==co.a21)&&(this.a22==co.a22)&&(this.a23==co.a23)&&(this.a31==co.a31)&&(this.a32==co.a32)&&(this.a33==co.a33)) {
					k = true;
				}
			}
			return k;
		}
		public Matrix copy(){Matrix k=new Matrix(this.a11,this.a12,this.a13,this.a21,this.a22,this.a23,this.a31,this.a32,this.a33);return k;}
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
						File imagefile = new File(savemtlfile.getParent(), model.materials[i].filename);
						ImageIO.write(model.materials[i].fileimage.getSnapshot(), "PNG", imagefile);
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

	public static RenderView renderProjectedRayViewSoftware(Position campos, Entity[] entitylist, int renderwidth, double hfov, int renderheight, double vfov, Matrix viewrot, boolean unlit, int mouselocationx, int mouselocationy) {
		RenderView renderview = new RenderView();
		renderview.pos = campos.copy();
		renderview.rot = viewrot.copy();
		renderview.renderwidth = renderwidth;
		renderview.renderheight = renderheight;
		renderview.hfov = hfov;
		renderview.vfov = 2.0f*MathLib.atand((((double)renderheight)/((double)renderwidth))*MathLib.tand(renderview.hfov/2.0f));
		renderview.unlit = unlit;
		renderview.mouselocationx = mouselocationx; 
		renderview.mouselocationy = mouselocationy; 
		renderview.dirs = MathLib.projectedCameraDirections(renderview.rot);
		renderview.rays = MathLib.projectedRays(renderwidth, renderheight, hfov, vfov, viewrot);
		renderview.renderimage = gc.createCompatibleVolatileImage(renderwidth, renderheight, Transparency.TRANSLUCENT);
		renderview.zbuffer = new double[renderheight][renderwidth];
		renderview.tbuffer = new Triangle[renderheight][renderwidth];
		renderview.cbuffer = new Coordinate[renderheight][renderwidth];
		for (int i=0;i<renderview.zbuffer.length;i++) {Arrays.fill(renderview.zbuffer[i],Double.POSITIVE_INFINITY);}
		Graphics2D g2 = renderview.renderimage.createGraphics();
		g2.setComposite(AlphaComposite.Src);
		g2.setColor(new Color(0.0f,0.0f,0.0f,0.0f));
		g2.setPaint(null);
		g2.setClip(null);
		g2.fillRect(0, 0, renderwidth, renderheight);
		g2.setComposite(AlphaComposite.SrcOver);
		ArrayList<Triangle> mouseoverhittriangle = new ArrayList<Triangle>();
		if (entitylist!=null) {
			Sphere[] entityspherelist = new Sphere[entitylist.length]; 
			for (int k=0;k<entitylist.length;k++) {
				entityspherelist[k] = entitylist[k].sphereboundaryvolume;
				entityspherelist[k].ind = k;
			}
			SphereDistanceComparator distcomp = new SphereDistanceComparator(renderview.pos);
			Sphere[] sortedentityspherelist = Arrays.copyOf(entityspherelist, entityspherelist.length);
			Arrays.sort(sortedentityspherelist, distcomp);
			for (int k=sortedentityspherelist.length-1;k>=0;k--) {
				Entity sortedentity = entitylist[sortedentityspherelist[k].ind];
				Triangle[] copytrianglelist = sortedentity.trianglelist;
				if (copytrianglelist.length>0) {
					Direction[] trianglenormallist = new Direction[copytrianglelist.length];
					for (int i=0;i<copytrianglelist.length;i++) {
						trianglenormallist[i] = copytrianglelist[i].norm;
						if (copytrianglelist[i].norm.isZero()) {
							Triangle[] copyplanetriangle = {copytrianglelist[i]};
							Plane[] triangleplanes = MathLib.planeFromPoints(copyplanetriangle);
							Direction[] trianglenormal = MathLib.planeNormals(triangleplanes);
							double[] triangleviewangles = MathLib.vectorAngle(renderview.dirs[0], trianglenormal);
							if (triangleviewangles[0]<90.0f) {trianglenormal[0]=trianglenormal[0].invert();}
							trianglenormallist[i] = trianglenormal[0];
						}
					}
					Sphere[] copytrianglespherelist = MathLib.triangleCircumSphere(copytrianglelist);
					for (int i=0;i<copytrianglespherelist.length;i++) {copytrianglespherelist[i].ind = i;}
					Sphere[] sortedtrianglespherelist = Arrays.copyOf(copytrianglespherelist, copytrianglespherelist.length);
					Arrays.sort(sortedtrianglespherelist, distcomp);
					for (int n=sortedtrianglespherelist.length-1;n>=0;n--) {
						int it = sortedtrianglespherelist[n].ind;
						Triangle[] copytriangle = {copytrianglelist[it]};
						Material copymaterial = copytriangle[0].mat;
						Direction copytrianglenormal = trianglenormallist[it];
						Color tricolor = copymaterial.facecolor;
						float alphacolor = copymaterial.transparency;
						if (tricolor==null) {tricolor = Color.WHITE;}
						float[] tricolorcomp = tricolor.getRGBComponents(new float[4]);
						Color trianglecolor = new Color(tricolorcomp[0], tricolorcomp[1], tricolorcomp[2], alphacolor);
						VolatileImage tritexture = copymaterial.fileimage;
						BufferedImage tritextureimage = copymaterial.snapimage;
						if ((tritexture!=null)&&(tritextureimage==null)) {
							copymaterial.snapimage = copymaterial.fileimage.getSnapshot();
							tritextureimage = copymaterial.snapimage;
						}
						for (int j=0;j<renderheight;j++) {
							for (int i=0;i<renderwidth;i++) {
								Direction[] camray = {renderview.rays[j][i]};
								Position[][] camrayint = MathLib.rayTriangleIntersection(renderview.pos, camray, copytriangle);
								Position[] camrayintpos = {camrayint[0][0]};
								if (camrayintpos[0]!=null) {
									Direction[] linepointdir = MathLib.vectorFromPoints(renderview.pos, camrayintpos);
									double[] linepointdirlen = MathLib.vectorLength(linepointdir);
									double drawdistance = linepointdirlen[0];
									if (drawdistance<renderview.zbuffer[j][i]) {
										renderview.zbuffer[j][i] = drawdistance;
										renderview.tbuffer[j][i] = copytriangle[0];
										if ((mouselocationx==i)&&(mouselocationy==j)) {
											mouseoverhittriangle.add(copytriangle[0]);
										}
										double[] triangleviewangle = MathLib.vectorAngle(copytrianglenormal, camray);
										triangleviewangle[0] -= 90.0f;
										if (triangleviewangle[0]<0.0f) {triangleviewangle[0] = 0.0f;}
										float shadingmultiplier = ((((float)triangleviewangle[0])/1.5f)+30.0f)/90.0f;
										Coordinate tex = camrayint[0][0].tex;
										if ((tritexture!=null)&&(tex!=null)) {
											Position[] lineuvpoint = {new Position(tex.u*(tritexture.getWidth()-1),(1.0f-tex.v)*(tritexture.getHeight()-1),0.0f)};
											int lineuvx = (int)Math.round(lineuvpoint[0].x);
											int lineuvy = (int)Math.round(lineuvpoint[0].y);
											if ((lineuvx>=0)&&(lineuvx<tritexture.getWidth())&&(lineuvy>=0)&&(lineuvy<tritexture.getHeight())) {
												renderview.cbuffer[j][i] = new Coordinate(lineuvx,lineuvy);
												Color texcolor = new Color(tritextureimage.getRGB(lineuvx, lineuvy));
												float[] texcolorcomp = texcolor.getRGBComponents(new float[4]);
												Color texcolorshade = new Color(texcolorcomp[0], texcolorcomp[1], texcolorcomp[2], alphacolor);
												if (!unlit) {
													texcolorshade = new Color(texcolorcomp[0]*shadingmultiplier, texcolorcomp[1]*shadingmultiplier, texcolorcomp[2]*shadingmultiplier, alphacolor);
												}
												g2.setColor(texcolorshade);
												g2.drawLine(i, j, i, j);
											}
										} else {
											if (!unlit) {
												trianglecolor = new Color(tricolorcomp[0]*shadingmultiplier, tricolorcomp[1]*shadingmultiplier, tricolorcomp[2]*shadingmultiplier, alphacolor);
											}
											g2.setColor(trianglecolor);
											g2.drawLine(i, j, i, j);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		renderview.mouseovertriangle = mouseoverhittriangle.toArray(new Triangle[mouseoverhittriangle.size()]);
		renderview.snapimage = renderview.renderimage.getSnapshot();
		return renderview;
	}
	
	public static RenderView renderProjectedPlaneViewSoftware(Position campos, Entity[] entitylist, int renderwidth, double hfov, int renderheight, double vfov, Matrix viewrot, boolean unlit, int mouselocationx, int mouselocationy) {
		RenderView renderview = new RenderView();
		renderview.pos = campos.copy();
		renderview.rot = viewrot.copy();
		renderview.renderwidth = renderwidth;
		renderview.renderheight = renderheight;
		renderview.hfov = hfov;
		renderview.vfov = 2.0f*MathLib.atand((((double)renderheight)/((double)renderwidth))*MathLib.tand(renderview.hfov/2.0f));
		renderview.unlit = unlit;
		renderview.mouselocationx = mouselocationx;
		renderview.mouselocationy = mouselocationy;
		renderview.dirs = MathLib.projectedCameraDirections(renderview.rot);
		renderview.planes = MathLib.projectedPlanes(renderview.pos, renderwidth, hfov, renderview.rot); 
		renderview.renderimage = gc.createCompatibleVolatileImage(renderwidth, renderheight, Transparency.TRANSLUCENT);
		renderview.zbuffer = new double[renderheight][renderwidth];
		renderview.tbuffer = new Triangle[renderheight][renderwidth];
		renderview.cbuffer = new Coordinate[renderheight][renderwidth];
		for (int i=0;i<renderview.zbuffer.length;i++) {Arrays.fill(renderview.zbuffer[i],Double.POSITIVE_INFINITY);}
		Graphics2D g2 = renderview.renderimage.createGraphics();
		g2.setComposite(AlphaComposite.Src);
		g2.setColor(new Color(0.0f,0.0f,0.0f,0.0f));
		g2.setPaint(null);
		g2.setClip(null);
		g2.fillRect(0, 0, renderwidth, renderheight);
		g2.setComposite(AlphaComposite.SrcOver);
		ArrayList<Triangle> mouseoverhittriangle = new ArrayList<Triangle>();
		if (entitylist!=null) {
			double[] verticalangles = MathLib.projectedAngles(renderheight, vfov);
			double halfvfovmult = (1.0f/MathLib.tand(vfov/2.0f));
			double origindeltay = ((double)(renderheight-1))/2.0f;
			double halfvres = ((double)renderheight)/2.0f;
			Plane[] camdirrightupplanes = MathLib.planeFromNormalAtPoint(renderview.pos, renderview.dirs);
			Plane[] camfwdplane = {camdirrightupplanes[0]};
			Plane[] camupplane = {camdirrightupplanes[2]};
			Sphere[] entityspherelist = new Sphere[entitylist.length]; 
			for (int k=0;k<entitylist.length;k++) {
				entityspherelist[k] = entitylist[k].sphereboundaryvolume;
				entityspherelist[k].ind = k;
			}
			SphereDistanceComparator distcomp = new SphereDistanceComparator(renderview.pos);
			Sphere[] sortedentityspherelist = Arrays.copyOf(entityspherelist, entityspherelist.length);
			Arrays.sort(sortedentityspherelist, distcomp);
			for (int k=sortedentityspherelist.length-1;k>=0;k--) {
					Triangle[] copytrianglelist = entitylist[sortedentityspherelist[k].ind].trianglelist;
					if (copytrianglelist.length>0) {
						Direction[] trianglenormallist = new Direction[copytrianglelist.length];
						for (int i=0;i<copytrianglelist.length;i++) {
							trianglenormallist[i] = copytrianglelist[i].norm;
							if (copytrianglelist[i].norm.isZero()) {
								Triangle[] copyplanetriangle = {copytrianglelist[i]};
								Plane[] triangleplanes = MathLib.planeFromPoints(copyplanetriangle);
								Direction[] trianglenormal = MathLib.planeNormals(triangleplanes);
								double[] triangleviewangles = MathLib.vectorAngle(renderview.dirs[0], trianglenormal);
								if (triangleviewangles[0]<90.0f) {trianglenormal[0]=trianglenormal[0].invert();}
								trianglenormallist[i] = trianglenormal[0];
							}
						}
						Sphere[] copytrianglespherelist = MathLib.triangleCircumSphere(copytrianglelist);
						for (int i=0;i<copytrianglespherelist.length;i++) {copytrianglespherelist[i].ind = i;}
						Sphere[] sortedtrianglespherelist = Arrays.copyOf(copytrianglespherelist, copytrianglespherelist.length);
						Arrays.sort(sortedtrianglespherelist, distcomp);
						for (int i=sortedtrianglespherelist.length-1;i>=0;i--) {
							int it = sortedtrianglespherelist[i].ind;
							Triangle[] copytriangle = {copytrianglelist[it]};
							Line[][] vertplanetriangleint = MathLib.planeTriangleIntersection(renderview.planes, copytriangle);
							Material copymaterial = copytriangle[0].mat;
							Direction copytrianglenormal = trianglenormallist[it];
							Color tricolor = copymaterial.facecolor;
							float alphacolor = copymaterial.transparency;
							if (tricolor==null) {tricolor = Color.WHITE;}
							float[] tricolorcomp = tricolor.getRGBComponents(new float[4]);
							Color trianglecolor = new Color(tricolorcomp[0], tricolorcomp[1], tricolorcomp[2], alphacolor);
							VolatileImage tritexture = copymaterial.fileimage;
							BufferedImage tritextureimage = copymaterial.snapimage;
							if ((tritexture!=null)&&(tritextureimage==null)) {
								copymaterial.snapimage = copymaterial.fileimage.getSnapshot();
								tritextureimage = copymaterial.snapimage;
							}
							for (int j=0;j<renderwidth;j++) {
							Line[] drawline = {vertplanetriangleint[j][0]};
							if (drawline[0]!=null) {
								Position[] drawlinepoints = {drawline[0].pos1, drawline[0].pos2};
								double[][] fwdintpointsdist = MathLib.planePointDistance(drawlinepoints, camfwdplane);
								double[][] upintpointsdist = MathLib.planePointDistance(drawlinepoints, camupplane);
								if ((fwdintpointsdist[0][0]>=1.0f)&&(fwdintpointsdist[1][0]>=1.0f)) {
									double vpixely1 = halfvfovmult*halfvres*(upintpointsdist[0][0]/fwdintpointsdist[0][0])+origindeltay;
									double vpixely2 = halfvfovmult*halfvres*(upintpointsdist[1][0]/fwdintpointsdist[1][0])+origindeltay;
									double vpixelyang1 = MathLib.atand(upintpointsdist[0][0]/fwdintpointsdist[0][0]);
									double vpixelyang2 = MathLib.atand(upintpointsdist[1][0]/fwdintpointsdist[1][0]);
									double[] vpixelys = {vpixely1, vpixely2};
									double[] vpixelyangs = {vpixelyang1, vpixelyang2};
									int[] vpixelyinds = UtilLib.indexSort(vpixelys);
									double[] vpixelysort = UtilLib.indexValues(vpixelys, vpixelyinds);
									Position[] vpixelpoints = {drawlinepoints[vpixelyinds[0]], drawlinepoints[vpixelyinds[1]]};
									Position[] vpixelpoint1 = {vpixelpoints[0]};
									Position[] vpixelpoint2 = {vpixelpoints[1]};
									Position[] vcamposd = {new Position(0.0f,0.0f,0.0f)};
									Position[] vpixelpoint1d = {new Position(fwdintpointsdist[vpixelyinds[0]][0],upintpointsdist[vpixelyinds[0]][0],0.0f)};
									Position[] vpixelpoint2d = {new Position(fwdintpointsdist[vpixelyinds[1]][0],upintpointsdist[vpixelyinds[1]][0],0.0f)};
									Direction[] vpixelpointdir1d = MathLib.vectorFromPoints(vcamposd, vpixelpoint1d);
									double[] vpixelpointdirlen1d = MathLib.vectorLength(vpixelpointdir1d);
									Direction[] vpixelpointdir1invd = {vpixelpointdir1d[0].invert()};
									Direction[] vpixelpointdir12d = MathLib.vectorFromPoints(vpixelpoint1d, vpixelpoint2d);
									double[] vpixelpointdir12lend = MathLib.vectorLength(vpixelpointdir12d);
									double[] vpixelpoint1angled = MathLib.vectorAngle(vpixelpointdir1invd, vpixelpointdir12d);
									double vpixelyangsort1 = vpixelyangs[vpixelyinds[0]]; 
									int vpixelyind1 = (int)Math.ceil(vpixelysort[0]); 
									int vpixelyind2 = (int)Math.floor(vpixelysort[1]); 
									int vpixelystart = vpixelyind1;
									int vpixelyend = vpixelyind2;
									Direction[] vpixelpointdir12 = MathLib.vectorFromPoints(vpixelpoint1, vpixelpoint2);
									if ((vpixelyend>=0)&&(vpixelystart<=renderheight)) {
										if (vpixelystart<0) {vpixelystart=0;}
										if (vpixelyend>=renderheight) {vpixelyend=renderheight-1;}
										for (int n=vpixelystart;n<=vpixelyend;n++) {
											double vpixelcampointangle = verticalangles[n]-vpixelyangsort1;
											double vpixelpointangle = 180.0f-vpixelpoint1angled[0]-vpixelcampointangle;
											double vpixelpointlen = vpixelpointdirlen1d[0]*(MathLib.sind(vpixelcampointangle)/MathLib.sind(vpixelpointangle));
											double vpixelpointlenfrac = vpixelpointlen/vpixelpointdir12lend[0];
											Position[] linepoint = MathLib.translate(vpixelpoint1, vpixelpointdir12[0], vpixelpointlenfrac);
											Direction[] linepointdir = MathLib.vectorFromPoints(renderview.pos, linepoint);
											double[] linepointdirlen = MathLib.vectorLength(linepointdir);
											Direction[] camray = linepointdir;
											double drawdistance = Math.abs(linepointdirlen[0]);
											if (drawdistance<renderview.zbuffer[n][j]) {
												renderview.zbuffer[n][j] = drawdistance;
												renderview.tbuffer[n][j] = copytriangle[0];
												if ((mouselocationx==j)&&(mouselocationy==n)) {
													mouseoverhittriangle.add(copytriangle[0]);
												}
												double[] triangleviewangle = MathLib.vectorAngle(copytrianglenormal, camray);
												triangleviewangle[0] -= 90.0f;
												if (triangleviewangle[0]<0.0f) {triangleviewangle[0] = 0.0f;}
												float shadingmultiplier = ((((float)triangleviewangle[0])/1.5f)+30.0f)/90.0f;
												Coordinate tex1 = vpixelpoints[0].tex;
												Coordinate tex2 = vpixelpoints[1].tex;
												if ((tritexture!=null)&&(tex1!=null)&&(tex2!=null)) {
													Position[] lineuvpoint1 = {new Position(tex1.u*(tritexture.getWidth()-1),(1.0f-tex1.v)*(tritexture.getHeight()-1),0.0f)};
													Position[] lineuvpoint2 = {new Position(tex2.u*(tritexture.getWidth()-1),(1.0f-tex2.v)*(tritexture.getHeight()-1),0.0f)};
													Direction[] vpixelpointdir12uv = MathLib.vectorFromPoints(lineuvpoint1, lineuvpoint2);
													Position[] lineuvpos = MathLib.translate(lineuvpoint1, vpixelpointdir12uv[0], vpixelpointlenfrac);
													int lineuvx = (int)Math.round(lineuvpos[0].x);
													int lineuvy = (int)Math.round(lineuvpos[0].y);
													if ((lineuvx>=0)&&(lineuvx<tritexture.getWidth())&&(lineuvy>=0)&&(lineuvy<tritexture.getHeight())) {
														renderview.cbuffer[n][j] = new Coordinate(lineuvx,lineuvy);
														Color texcolor = new Color(tritextureimage.getRGB(lineuvx, lineuvy));
														float[] texcolorcomp = texcolor.getRGBComponents(new float[4]);
														Color texcolorshade = new Color(texcolorcomp[0], texcolorcomp[1], texcolorcomp[2], alphacolor);
														if (!unlit) {
															texcolorshade = new Color(texcolorcomp[0]*shadingmultiplier, texcolorcomp[1]*shadingmultiplier, texcolorcomp[2]*shadingmultiplier, alphacolor);
														}
														g2.setColor(texcolorshade);
														g2.drawLine(j, n, j, n);
													}
												} else {
													if (!unlit) {
														trianglecolor = new Color(tricolorcomp[0]*shadingmultiplier, tricolorcomp[1]*shadingmultiplier, tricolorcomp[2]*shadingmultiplier, alphacolor);
													}
													g2.setColor(trianglecolor);
													g2.drawLine(j, n, j, n);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		renderview.mouseovertriangle = mouseoverhittriangle.toArray(new Triangle[mouseoverhittriangle.size()]);
		renderview.snapimage = renderview.renderimage.getSnapshot();
		return renderview;
	}

	public static RenderView renderProjectedPolygonViewHardware(Position campos, Entity[] entitylist, int renderwidth, double hfov, int renderheight, double vfov, Matrix viewrot, boolean unlit, int mouselocationx, int mouselocationy) {
		int vertexradius = 2;
		RenderView renderview = new RenderView();
		renderview.mouselocationx = mouselocationx;
		renderview.mouselocationy = mouselocationy;
		renderview.pos = campos.copy();
		renderview.rot = viewrot.copy();
		renderview.renderwidth = renderwidth;
		renderview.renderheight = renderheight;
		renderview.hfov = hfov;
		renderview.vfov = vfov;
		renderview.unlit = unlit;
		renderview.mouselocationx = mouselocationx; 
		renderview.mouselocationy = mouselocationy; 
		renderview.dirs = MathLib.projectedCameraDirections(viewrot);
		renderview.renderimage = gc.createCompatibleVolatileImage(renderwidth, renderheight, Transparency.TRANSLUCENT);
		renderview.zbuffer = new double[renderheight][renderwidth];
		for (int i=0;i<renderview.zbuffer.length;i++) {Arrays.fill(renderview.zbuffer[i],Double.POSITIVE_INFINITY);}
		Graphics2D g2 = renderview.renderimage.createGraphics();
		g2.setComposite(AlphaComposite.Src);
		g2.setColor(new Color(0.0f,0.0f,0.0f,0.0f));
		g2.setPaint(null);
		g2.setClip(null);
		g2.fillRect(0, 0, renderwidth, renderheight);
		g2.setComposite(AlphaComposite.SrcOver);
		ArrayList<Triangle> mouseoverhittriangle = new ArrayList<Triangle>();
		g2.setComposite(AlphaComposite.Src);
		g2.setColor(Color.BLACK);
		g2.setPaint(null);
		g2.fillRect(0, 0, renderwidth, renderheight);
		g2.setComposite(AlphaComposite.SrcOver);
		if (entitylist!=null) {
			Sphere[] entityspherelist = new Sphere[entitylist.length]; 
			for (int k=0;k<entitylist.length;k++) {
				entityspherelist[k] = entitylist[k].sphereboundaryvolume;
				entityspherelist[k].ind = k;
			}
			SphereDistanceComparator distcomp = new SphereDistanceComparator(renderview.pos);
			Sphere[] sortedentityspherelist = Arrays.copyOf(entityspherelist, entityspherelist.length);
			Arrays.sort(sortedentityspherelist, distcomp);
			for (int k=sortedentityspherelist.length-1;k>=0;k--) {
				Triangle[] copytrianglelist = entitylist[sortedentityspherelist[k].ind].trianglelist;
				if (copytrianglelist!=null) {
					if (copytrianglelist.length>0) {
						for (int i=0;i<copytrianglelist.length;i++) {copytrianglelist[i].ind = i;}
						Sphere[] copytrianglespherelist = MathLib.triangleCircumSphere(copytrianglelist);
						for (int i=0;i<copytrianglespherelist.length;i++) {copytrianglespherelist[i].ind = i;}
						Sphere[] sortedtrianglespherelist = Arrays.copyOf(copytrianglespherelist, copytrianglespherelist.length);
						Arrays.sort(sortedtrianglespherelist, distcomp);
						float[] triangleshadingmultipliers = new float[copytrianglelist.length];
						for (int i=0;i<copytrianglelist.length;i++) {
							Direction[] trianglenormal = {copytrianglelist[i].norm};
							double[] triangleviewangles = MathLib.vectorAngle(renderview.dirs[0], trianglenormal);
							if (!Double.isFinite(triangleviewangles[0])) {
								Triangle[] copyplanetriangle = {copytrianglelist[i]};
								Plane[] triangleplanes = MathLib.planeFromPoints(copyplanetriangle);
								Direction[] trianglenormals = MathLib.planeNormals(triangleplanes);
								triangleviewangles = MathLib.vectorAngle(renderview.dirs[0], trianglenormals);
								if (triangleviewangles[0]<90.0f) {triangleviewangles[0]=180.0f-triangleviewangles[0];}
							}
							double triangleviewangle = triangleviewangles[0];
							triangleviewangle -= 90.0f;
							if (triangleviewangle<0.0f) {triangleviewangle = 0.0f;}
							triangleshadingmultipliers[i] = ((((float)triangleviewangle)/1.5f)+30.0f)/90.0f;
						}
						Coordinate[][] copytrianglelistcoords = MathLib.projectedTriangles(renderview.pos, copytrianglelist, renderwidth, hfov, renderheight, vfov, viewrot);
						for (int j=sortedtrianglespherelist.length-1;j>=0;j--) {
							Coordinate coord1 = copytrianglelistcoords[sortedtrianglespherelist[j].ind][0];
							Coordinate coord2 = copytrianglelistcoords[sortedtrianglespherelist[j].ind][1];
							Coordinate coord3 = copytrianglelistcoords[sortedtrianglespherelist[j].ind][2];
							if ((coord1!=null)&&(coord2!=null)&&(coord3!=null)) {
								int i = sortedtrianglespherelist[j].ind;
								Triangle copytriangle = copytrianglelist[i];
								float shadingmultiplier = triangleshadingmultipliers[i];
								Material copymaterial = copytriangle.mat;
								Color tricolor = copymaterial.facecolor;
								float alphacolor = copymaterial.transparency;
								if (tricolor==null) {tricolor = Color.WHITE;}
								float[] tricolorcomp = tricolor.getRGBComponents(new float[4]);
								Color trianglecolor = new Color(tricolorcomp[0], tricolorcomp[1], tricolorcomp[2], alphacolor);
								if (!unlit) {
									trianglecolor = new Color(tricolorcomp[0]*shadingmultiplier, tricolorcomp[1]*shadingmultiplier, tricolorcomp[2]*shadingmultiplier, alphacolor);
								}
								g2.setColor(trianglecolor);
								Polygon trianglepolygon = new Polygon();
								trianglepolygon.addPoint((int)Math.round(coord1.u), (int)Math.round(coord1.v));
								trianglepolygon.addPoint((int)Math.round(coord2.u), (int)Math.round(coord2.v));
								trianglepolygon.addPoint((int)Math.round(coord3.u), (int)Math.round(coord3.v));
								g2.fill(trianglepolygon);
								boolean mouseoverhit = g2.hit(new Rectangle(mouselocationx-vertexradius,mouselocationy-vertexradius,3,3), trianglepolygon, false);
								if (mouseoverhit) {
									mouseoverhittriangle.add(copytrianglelist[i]);
								}
							}
						}
					}
				}
			}
		}
		renderview.mouseovertriangle = mouseoverhittriangle.toArray(new Triangle[mouseoverhittriangle.size()]);
		renderview.snapimage = renderview.renderimage.getSnapshot();
		return renderview;
	}
	
	public static RenderView renderProjectedLineViewHardware(Position campos, Line[] linelist, int renderwidth, double hfov, int renderheight, double vfov, Matrix viewrot, int mouselocationx, int mouselocationy) {
		int originlinelength = 100;
		int vertexradius = 2;
		int axisstroke = 2;
		int vertexstroke = 2;
		int vertexfocus = 3;
		int sketchlinestroke = 2;
		double pointdist = 1.0f;
		int gridstep = 20;
		RenderView renderview = new RenderView();
		renderview.mouselocationx = mouselocationx;
		renderview.mouselocationy = mouselocationy;
		renderview.pos = campos.copy();
		renderview.rot = viewrot.copy();
		renderview.renderwidth = renderwidth;
		renderview.renderheight = renderheight;
		renderview.hfov = hfov;
		renderview.vfov = vfov;
		renderview.mouselocationx = mouselocationx; 
		renderview.mouselocationy = mouselocationy; 
		renderview.dirs = MathLib.projectedCameraDirections(viewrot);
		renderview.renderimage = gc.createCompatibleVolatileImage(renderwidth, renderheight, Transparency.TRANSLUCENT);
		renderview.zbuffer = new double[renderheight][renderwidth];
		for (int i=0;i<renderview.zbuffer.length;i++) {Arrays.fill(renderview.zbuffer[i],Double.POSITIVE_INFINITY);}
		double editplanedistance = (((double)renderwidth)/2.0f)/MathLib.tand(hfov/2.0f);
		Graphics2D g2 = renderview.renderimage.createGraphics();
		g2.setComposite(AlphaComposite.Src);
		g2.setColor(new Color(0.0f,0.0f,0.0f,0.0f));
		g2.setPaint(null);
		g2.setClip(null);
		g2.fillRect(0, 0, renderwidth, renderheight);
		g2.setComposite(AlphaComposite.SrcOver);
		ArrayList<Position> mouseoverhitvertex = new ArrayList<Position>(); 
		ArrayList<Line> mouseoverhitline = new ArrayList<Line>();
		BufferedImage bgpatternimage = gc.createCompatibleImage(gridstep, gridstep, Transparency.OPAQUE);
		TexturePaint bgpattern = null;
		Graphics2D pgfx = bgpatternimage.createGraphics();
		pgfx.setColor(Color.WHITE);
		pgfx.fillRect(0, 0, bgpatternimage.getWidth(), bgpatternimage.getHeight());
		pgfx.setColor(Color.BLACK);
		pgfx.drawLine(0, 0, 0, gridstep-1);
		pgfx.drawLine(0, 0, gridstep-1, 0);
		pgfx.dispose();
		int origindeltax = (int)Math.floor(((double)(renderwidth-1))/2.0f);
		int origindeltay = (int)Math.floor(((double)(renderheight-1))/2.0f);
		bgpattern = new TexturePaint(bgpatternimage,new Rectangle(origindeltax, origindeltay, gridstep, gridstep));
		g2.setComposite(AlphaComposite.Src);
		g2.setColor(null);
		g2.setPaint(bgpattern);
		g2.fillRect(0, 0, renderwidth, renderheight);
		g2.setPaint(null);
		g2.setComposite(AlphaComposite.SrcOver);
		Coordinate[][] linelistcoords = MathLib.projectedLines(renderview.pos, linelist, renderwidth, hfov, renderheight, vfov, viewrot);
		Position[] camposarray = {renderview.pos};
		Position[] editposarray = MathLib.translate(camposarray, renderview.dirs[0], editplanedistance);
		Plane[] editplanes = MathLib.planeFromNormalAtPoint(editposarray[0], renderview.dirs);
		Plane[] editplane = {editplanes[0]};
		for (int i=0;i<linelist.length;i++) {
			Position[] linepoints = {linelist[i].pos1, linelist[i].pos2};
			double[][] linepointdists = MathLib.planePointDistance(linepoints, editplane);
			if ((linepointdists[0][0]>=0)||(linepointdists[1][0]>=0)) {
				Coordinate coord1 = linelistcoords[i][0];
				Coordinate coord2 = linelistcoords[i][1];
				if ((coord1!=null)&&(coord2!=null)) {
					g2.setColor(Color.BLACK);
					if (Math.abs(linepointdists[0][0])<pointdist){g2.setStroke(new BasicStroke(vertexstroke+vertexfocus));}else{g2.setStroke(new BasicStroke(vertexstroke));}
					g2.drawOval((int)Math.round(coord1.u)-vertexradius, (int)Math.round(coord1.v)-vertexradius, vertexradius*2, vertexradius*2);
					if (Math.abs(linepointdists[1][0])<pointdist){g2.setStroke(new BasicStroke(vertexstroke+vertexfocus));}else{g2.setStroke(new BasicStroke(vertexstroke));}
					g2.drawOval((int)Math.round(coord2.u)-vertexradius, (int)Math.round(coord2.v)-vertexradius, vertexradius*2, vertexradius*2);
					g2.setStroke(new BasicStroke(sketchlinestroke));
					g2.setColor(Color.BLUE);
					g2.drawLine((int)Math.round(coord1.u), (int)Math.round(coord1.v), (int)Math.round(coord2.u), (int)Math.round(coord2.v));
					boolean mouseoverhit1 = g2.hit(new Rectangle(mouselocationx-vertexradius,mouselocationy-vertexradius,3,3), new Rectangle((int)Math.round(coord1.u)-vertexradius,(int)Math.round(coord1.v)-vertexradius,3,3), false);
					boolean mouseoverhit2 = g2.hit(new Rectangle(mouselocationx-vertexradius,mouselocationy-vertexradius,3,3), new Rectangle((int)Math.round(coord2.u)-vertexradius,(int)Math.round(coord2.v)-vertexradius,3,3), false);
					boolean mouseoverhitL = g2.hit(new Rectangle(mouselocationx-vertexradius,mouselocationy-vertexradius,3,3), new Line2D.Double((int)Math.round(coord1.u),(int)Math.round(coord1.v),(int)Math.round(coord2.u),(int)Math.round(coord2.v)), false);
					if (mouseoverhit1) {
						mouseoverhitvertex.add(linelist[i].pos1);
					}
					if (mouseoverhit2) {
						mouseoverhitvertex.add(linelist[i].pos2);
					}
					if (mouseoverhitL) {
						mouseoverhitline.add(linelist[i]);
					}
				}
			}
		}
		Position[] originpoints = {new Position(0.0f,0.0f,0.0f),new Position(originlinelength,0.0f,0.0f),new Position(0.0f,originlinelength,0.0f),new Position(0.0f,0.0f,originlinelength)}; 
		Coordinate[] originpointscoords = MathLib.projectedPoints(renderview.pos, originpoints, renderwidth, hfov, renderheight, vfov, viewrot);
		Coordinate coord1 = originpointscoords[0];
		Coordinate coord2 = originpointscoords[1];
		Coordinate coord3 = originpointscoords[2];
		Coordinate coord4 = originpointscoords[3];
		if ((coord1!=null)&&(coord2!=null)&&(coord3!=null)&&(coord4!=null)) {
			g2.setStroke(new BasicStroke(axisstroke));
			g2.setColor(Color.RED);
			g2.drawLine((int)Math.round(coord1.u), (int)Math.round(coord1.v), (int)Math.round(coord2.u), (int)Math.round(coord2.v));
			g2.setColor(Color.GREEN);
			g2.drawLine((int)Math.round(coord1.u), (int)Math.round(coord1.v), (int)Math.round(coord3.u), (int)Math.round(coord3.v));
			g2.setColor(Color.BLUE);
			g2.drawLine((int)Math.round(coord1.u), (int)Math.round(coord1.v), (int)Math.round(coord4.u), (int)Math.round(coord4.v));
			g2.setColor(Color.BLACK);
			g2.fillOval((int)Math.round(coord1.u)-vertexradius, (int)Math.round(coord1.v)-vertexradius, vertexradius*2, vertexradius*2);
		}
		renderview.mouseoververtex = mouseoverhitvertex.toArray(new Position[mouseoverhitvertex.size()]);
		renderview.mouseoverline = mouseoverhitline.toArray(new Line[mouseoverhitline.size()]);
		renderview.snapimage = renderview.renderimage.getSnapshot();
		return renderview;
	}
	
	public static RenderView renderSpheremapPlaneViewSoftware(Position campos, Entity[] entitylist, int renderwidth, int renderheight, Matrix viewrot, boolean unlit, int mouselocationx, int mouselocationy) {
		RenderView renderview = new RenderView();
		renderview.pos = campos.copy();
		renderview.rot = viewrot.copy();
		renderview.renderwidth = renderwidth;
		renderview.renderheight = renderheight;
		renderview.hfov = 360.0f;
		renderview.vfov = 180.0f;
		renderview.unlit = unlit;
		renderview.mouselocationx = mouselocationx; 
		renderview.mouselocationy = mouselocationy; 
		renderview.dirs = MathLib.projectedCameraDirections(renderview.rot);
		renderview.planes = MathLib.spheremapPlanes(renderview.pos, renderwidth, renderview.rot);
		renderview.renderimage = gc.createCompatibleVolatileImage(renderwidth, renderheight, Transparency.TRANSLUCENT);
		renderview.zbuffer = new double[renderheight][renderwidth];
		renderview.tbuffer = new Triangle[renderheight][renderwidth];
		renderview.cbuffer = new Coordinate[renderheight][renderwidth];
		for (int i=0;i<renderview.zbuffer.length;i++) {Arrays.fill(renderview.zbuffer[i],Double.POSITIVE_INFINITY);}
		Graphics2D g2 = renderview.renderimage.createGraphics();
		g2.setComposite(AlphaComposite.Src);
		g2.setColor(new Color(0.0f,0.0f,0.0f,0.0f));
		g2.setPaint(null);
		g2.setClip(null);
		g2.fillRect(0, 0, renderwidth, renderheight);
		g2.setComposite(AlphaComposite.SrcOver);
		ArrayList<Triangle> mouseoverhittriangle = new ArrayList<Triangle>();
		if (entitylist!=null) {
			double[] verticalangles = MathLib.spheremapAngles(renderheight, 180.0f);
			double halfvfovmult = (1.0f/(renderview.vfov/2.0f));
			double origindeltay = ((double)(renderheight-1))/2.0f;
			double halfvres = ((double)renderheight)/2.0f;
			Plane[] camdirrightupplanes = MathLib.planeFromNormalAtPoint(renderview.pos, renderview.dirs);
			Plane[] camupplane = {camdirrightupplanes[2]};
			Direction[] camupplanenormal = MathLib.planeNormals(camupplane);
			Direction[] camplanenormals = MathLib.planeNormals(renderview.planes);
			Direction[] camfwdplanenormals = MathLib.vectorCross(camupplanenormal[0], camplanenormals);
			Plane[] camfwdplanes = MathLib.planeFromNormalAtPoint(renderview.pos, camfwdplanenormals);
			Sphere[] entityspherelist = new Sphere[entitylist.length]; 
			for (int k=0;k<entitylist.length;k++) {
				entityspherelist[k] = entitylist[k].sphereboundaryvolume;
				entityspherelist[k].ind = k;
			}
			SphereDistanceComparator distcomp = new SphereDistanceComparator(renderview.pos);
			Sphere[] sortedentityspherelist = Arrays.copyOf(entityspherelist, entityspherelist.length);
			Arrays.sort(sortedentityspherelist, distcomp);
			for (int k=sortedentityspherelist.length-1;k>=0;k--) {
				Triangle[] copytrianglelist = entitylist[sortedentityspherelist[k].ind].trianglelist;
				if (copytrianglelist.length>0) {
					Direction[] trianglenormallist = new Direction[copytrianglelist.length];
					for (int i=0;i<copytrianglelist.length;i++) {
						trianglenormallist[i] = copytrianglelist[i].norm;
						if (copytrianglelist[i].norm.isZero()) {
							Triangle[] copyplanetriangle = {copytrianglelist[i]};
							Plane[] triangleplanes = MathLib.planeFromPoints(copyplanetriangle);
							Direction[] trianglenormal = MathLib.planeNormals(triangleplanes);
							double[] triangleviewangles = MathLib.vectorAngle(renderview.dirs[0], trianglenormal);
							if (triangleviewangles[0]<90.0f) {trianglenormal[0]=trianglenormal[0].invert();}
							trianglenormallist[i] = trianglenormal[0];
						}
					}
					Sphere[] copytrianglespherelist = MathLib.triangleCircumSphere(copytrianglelist);
					for (int i=0;i<copytrianglespherelist.length;i++) {copytrianglespherelist[i].ind = i;}
					Sphere[] sortedtrianglespherelist = Arrays.copyOf(copytrianglespherelist, copytrianglespherelist.length);
					Arrays.sort(sortedtrianglespherelist, distcomp);
					for (int i=sortedtrianglespherelist.length-1;i>=0;i--) {
						int it = sortedtrianglespherelist[i].ind;
						Triangle[] copytriangle = {copytrianglelist[it]};
						Line[][] vertplanetriangleint = MathLib.planeTriangleIntersection(renderview.planes, copytriangle);
						Material copymaterial = copytriangle[0].mat;
						Direction copytrianglenormal = trianglenormallist[it];
						Color tricolor = copymaterial.facecolor;
						float alphacolor = copymaterial.transparency;
						if (tricolor==null) {tricolor = Color.WHITE;}
						float[] tricolorcomp = tricolor.getRGBComponents(new float[4]);
						Color trianglecolor = new Color(tricolorcomp[0], tricolorcomp[1], tricolorcomp[2], alphacolor);
						VolatileImage tritexture = copymaterial.fileimage;
						BufferedImage tritextureimage = copymaterial.snapimage;
						if ((tritexture!=null)&&(tritextureimage==null)) {
							copymaterial.snapimage = copymaterial.fileimage.getSnapshot();
							tritextureimage = copymaterial.snapimage;
						}
						for (int j=0;j<renderwidth;j++) {
							Line drawline = vertplanetriangleint[j][0];
							if (drawline!=null) {
								Position[] drawlinepoints = {drawline.pos1, drawline.pos2};
								Plane[] camfwdplane = {camfwdplanes[j]};
								double[][] trianglefwdintpointsdist = MathLib.planePointDistance(drawlinepoints, camfwdplane);
								if ((trianglefwdintpointsdist[0][0]>=1.0f)&&(trianglefwdintpointsdist[1][0]>=1.0f)) {
									double[][] fwdintpointsdist = MathLib.planePointDistance(drawlinepoints, camfwdplane);
									double[][] upintpointsdist = MathLib.planePointDistance(drawlinepoints, camupplane);
									double vpixelyang1 = MathLib.atand(upintpointsdist[0][0]/fwdintpointsdist[0][0]);
									double vpixelyang2 = MathLib.atand(upintpointsdist[1][0]/fwdintpointsdist[1][0]);
									double vpixely1 = halfvres*halfvfovmult*vpixelyang1+origindeltay;
									double vpixely2 = halfvres*halfvfovmult*vpixelyang2+origindeltay;
									double[] vpixelys = {vpixely1, vpixely2};
									double[] vpixelyangs = {vpixelyang1, vpixelyang2};
									int[] vpixelyinds = UtilLib.indexSort(vpixelys);
									double[] vpixelysort = UtilLib.indexValues(vpixelys, vpixelyinds);
									Position[] vpixelpoints = {drawlinepoints[vpixelyinds[0]], drawlinepoints[vpixelyinds[1]]};
									Position[] vpixelpoint1 = {vpixelpoints[0]};
									Position[] vpixelpoint2 = {vpixelpoints[1]};
									Position[] vcamposd = {new Position(0.0f,0.0f,0.0f)};
									Position[] vpixelpoint1d = {new Position(fwdintpointsdist[vpixelyinds[0]][0],upintpointsdist[vpixelyinds[0]][0],0.0f)};
									Position[] vpixelpoint2d = {new Position(fwdintpointsdist[vpixelyinds[1]][0],upintpointsdist[vpixelyinds[1]][0],0.0f)};
									Direction[] vpixelpointdir1d = MathLib.vectorFromPoints(vcamposd, vpixelpoint1d);
									double[] vpixelpointdirlen1d = MathLib.vectorLength(vpixelpointdir1d);
									Direction[] vpixelpointdir1invd = {vpixelpointdir1d[0].invert()};
									Direction[] vpixelpointdir12d = MathLib.vectorFromPoints(vpixelpoint1d, vpixelpoint2d);
									double[] vpixelpointdir12lend = MathLib.vectorLength(vpixelpointdir12d);
									double[] vpixelpoint1angled = MathLib.vectorAngle(vpixelpointdir1invd, vpixelpointdir12d);
									double vpixelyangsort1 = vpixelyangs[vpixelyinds[0]]; 
									int vpixelyind1 = (int)Math.ceil(vpixelysort[0]); 
									int vpixelyind2 = (int)Math.floor(vpixelysort[1]); 
									int vpixelystart = vpixelyind1;
									int vpixelyend = vpixelyind2;
									Direction[] vpixelpointdir12 = MathLib.vectorFromPoints(vpixelpoint1, vpixelpoint2);
									if ((vpixelyend>=0)&&(vpixelystart<=renderheight)) {
										if (vpixelystart<0) {vpixelystart=0;}
										if (vpixelyend>=renderheight) {vpixelyend=renderheight-1;}
										for (int n=vpixelystart;n<=vpixelyend;n++) {
											double vpixelcampointangle = verticalangles[n]-vpixelyangsort1;
											double vpixelpointangle = 180.0f-vpixelpoint1angled[0]-vpixelcampointangle;
											double vpixelpointlen = vpixelpointdirlen1d[0]*(MathLib.sind(vpixelcampointangle)/MathLib.sind(vpixelpointangle));
											double vpixelpointlenfrac = vpixelpointlen/vpixelpointdir12lend[0];
											Position[] linepoint = MathLib.translate(vpixelpoint1, vpixelpointdir12[0], vpixelpointlenfrac);
											Direction[] linepointdir = MathLib.vectorFromPoints(renderview.pos, linepoint);
											double[] linepointdirlen = MathLib.vectorLength(linepointdir);
											Direction[] camray = linepointdir;
											double drawdistance = Math.abs(linepointdirlen[0]);
											if (drawdistance<renderview.zbuffer[n][j]) {
												renderview.zbuffer[n][j] = drawdistance;
												renderview.tbuffer[n][j] = copytriangle[0];
												if ((mouselocationx==j)&&(mouselocationy==n)) {
													mouseoverhittriangle.add(copytriangle[0]);
												}
												double[] triangleviewangle = MathLib.vectorAngle(copytrianglenormal, camray);
												triangleviewangle[0] -= 90.0f;
												if (triangleviewangle[0]<0.0f) {triangleviewangle[0] = 0.0f;}
												float shadingmultiplier = ((((float)triangleviewangle[0])/1.5f)+30.0f)/90.0f;
												Coordinate tex1 = vpixelpoints[0].tex;
												Coordinate tex2 = vpixelpoints[1].tex;
												if ((tritexture!=null)&&(tex1!=null)&&(tex2!=null)) {
													Position[] lineuvpoint1 = {new Position(tex1.u*(tritexture.getWidth()-1),(1.0f-tex1.v)*(tritexture.getHeight()-1),0.0f)};
													Position[] lineuvpoint2 = {new Position(tex2.u*(tritexture.getWidth()-1),(1.0f-tex2.v)*(tritexture.getHeight()-1),0.0f)};
													Direction[] vpixelpointdir12uv = MathLib.vectorFromPoints(lineuvpoint1, lineuvpoint2);
													Position[] lineuvpos = MathLib.translate(lineuvpoint1, vpixelpointdir12uv[0], vpixelpointlenfrac);
													int lineuvx = (int)Math.round(lineuvpos[0].x);
													int lineuvy = (int)Math.round(lineuvpos[0].y);
													if ((lineuvx>=0)&&(lineuvx<tritexture.getWidth())&&(lineuvy>=0)&&(lineuvy<tritexture.getHeight())) {
														renderview.cbuffer[n][j] = new Coordinate(lineuvx,lineuvy);
														Color texcolor = new Color(tritextureimage.getRGB(lineuvx, lineuvy));
														float[] texcolorcomp = texcolor.getRGBComponents(new float[4]);
														Color texcolorshade = new Color(texcolorcomp[0], texcolorcomp[1], texcolorcomp[2], alphacolor);
														if (!unlit) {
															texcolorshade = new Color(texcolorcomp[0]*shadingmultiplier, texcolorcomp[1]*shadingmultiplier, texcolorcomp[2]*shadingmultiplier, alphacolor);
														}
														g2.setColor(texcolorshade);
														g2.drawLine(j, n, j, n);
													}
												} else {
													if (!unlit) {
														trianglecolor = new Color(tricolorcomp[0]*shadingmultiplier, tricolorcomp[1]*shadingmultiplier, tricolorcomp[2]*shadingmultiplier, alphacolor);
													}
													g2.setColor(trianglecolor);
													g2.drawLine(j, n, j, n);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		renderview.mouseovertriangle = mouseoverhittriangle.toArray(new Triangle[mouseoverhittriangle.size()]);
		renderview.snapimage = renderview.renderimage.getSnapshot();
		renderview.spheremap = new Spheremap();
		renderview.spheremap.sphereview = renderview; 
		return renderview;
	}
	
	public static RenderView renderCubemapPlaneViewSoftware(Position campos, Entity[] entitylist, int renderwidth, int renderheight, int rendersize, Matrix viewrot, boolean unlit, int mouselocationx, int mouselocationy) {
		RenderView renderview = new RenderView();
		renderview.pos = campos.copy();
		renderview.rot = viewrot.copy();
		renderview.renderwidth = renderwidth;
		renderview.renderheight = renderheight;
		renderview.rendersize = rendersize;
		renderview.hfov = 90.0f;
		renderview.vfov = 90.0f;
		renderview.unlit = unlit;
		renderview.mouselocationx = mouselocationx;
		renderview.mouselocationy = mouselocationy;
		int renderposx1start = 0;
		int renderposx1end = rendersize-1;
		int renderposx2start = rendersize;
		int renderposx2end = 2*rendersize-1;
		int renderposx3start = 2*rendersize;
		int renderposx3end = 3*rendersize-1;
		int renderposy1start = 0;
		int renderposy1end = rendersize-1;
		int renderposy2start = rendersize;
		int renderposy2end = 2*rendersize-1;
		Matrix topmatrix = MathLib.rotationMatrix(-180.0f, 0.0f, 0.0f);
		Matrix bottommatrix = MathLib.rotationMatrix(0.0f, 0.0f, 0.0f);
		Matrix forwardmatrix = MathLib.rotationMatrix(-90.0f, 0.0f, 0.0f);
		Matrix rightmatrix = MathLib.matrixMultiply(MathLib.rotationMatrix(0.0f, 0.0f, 90.0f), forwardmatrix);
		Matrix backwardmatrix = MathLib.matrixMultiply(MathLib.rotationMatrix(0.0f, 0.0f, 180.0f), forwardmatrix);
		Matrix leftmatrix = MathLib.matrixMultiply(MathLib.rotationMatrix(0.0f, 0.0f, 270.0f), forwardmatrix);
		topmatrix = MathLib.matrixMultiply(renderview.rot, topmatrix);
		bottommatrix = MathLib.matrixMultiply(renderview.rot, bottommatrix);
		forwardmatrix = MathLib.matrixMultiply(renderview.rot, forwardmatrix);
		rightmatrix = MathLib.matrixMultiply(renderview.rot, rightmatrix);
		backwardmatrix = MathLib.matrixMultiply(renderview.rot, backwardmatrix);
		leftmatrix = MathLib.matrixMultiply(renderview.rot, leftmatrix);
		renderview.cubemap = new Cubemap();
		renderview.cubemap.topview = ModelLib.renderProjectedPlaneViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, topmatrix, renderview.unlit, mouselocationx, mouselocationy);
		renderview.cubemap.bottomview = ModelLib.renderProjectedPlaneViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, bottommatrix, renderview.unlit, mouselocationx, mouselocationy); 
		renderview.cubemap.forwardview = ModelLib.renderProjectedPlaneViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, forwardmatrix, renderview.unlit, mouselocationx, mouselocationy);
		renderview.cubemap.rightview = ModelLib.renderProjectedPlaneViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, rightmatrix, renderview.unlit, mouselocationx, mouselocationy);
		renderview.cubemap.backwardview = ModelLib.renderProjectedPlaneViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, backwardmatrix, renderview.unlit, mouselocationx, mouselocationy);
		renderview.cubemap.leftview = ModelLib.renderProjectedPlaneViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, leftmatrix, renderview.unlit, mouselocationx, mouselocationy);
		renderview.renderimage = gc.createCompatibleVolatileImage(renderwidth, renderheight, Transparency.TRANSLUCENT);
		Graphics2D rigfx = renderview.renderimage.createGraphics();
		rigfx.setComposite(AlphaComposite.Src);
		rigfx.setColor(new Color(0.0f,0.0f,0.0f,0.0f));
		rigfx.setPaint(null);
		rigfx.setClip(null);
		rigfx.fillRect(0, 0, renderwidth, renderheight);
		rigfx.drawImage(renderview.cubemap.forwardview.renderimage, renderposx1start, renderposy1start, renderposx1end, renderposy1end, 0, 0, renderview.rendersize-1, renderview.rendersize-1, null);
		rigfx.drawImage(renderview.cubemap.backwardview.renderimage, renderposx2start, renderposy1start, renderposx2end, renderposy1end, 0, 0, renderview.rendersize-1, renderview.rendersize-1, null);
		rigfx.drawImage(renderview.cubemap.topview.renderimage, renderposx3start, renderposy1start, renderposx3end, renderposy1end, 0, 0, renderview.rendersize-1, renderview.rendersize-1, null);
		rigfx.drawImage(renderview.cubemap.leftview.renderimage, renderposx1start, renderposy2start, renderposx1end, renderposy2end, 0, 0, renderview.rendersize-1, renderview.rendersize-1, null);
		rigfx.drawImage(renderview.cubemap.bottomview.renderimage, renderposx2start, renderposy2start, renderposx2end, renderposy2end, 0, 0, renderview.rendersize-1, renderview.rendersize-1, null);
		rigfx.drawImage(renderview.cubemap.rightview.renderimage, renderposx3start, renderposy2start, renderposx3end, renderposy2end, 0, 0, renderview.rendersize-1, renderview.rendersize-1, null);
		rigfx.dispose();
		renderview.snapimage = renderview.renderimage.getSnapshot();
		return renderview;
	}
	
}
