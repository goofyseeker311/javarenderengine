package fi.jkauppa.javarenderengine;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
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
	public static class Material implements Comparable<Material> {
		public String materialname = null;
		public VolatileImage fileimage = null;
		public BufferedImage snapimage = null;
		public VolatileImage ambientfileimage = null;
		public BufferedImage ambientsnapimage = null;
		public VolatileImage specularfileimage = null;
		public BufferedImage specularsnapimage = null;
		public VolatileImage specularhighfileimage = null;
		public BufferedImage specularhighsnapimage = null;
		public VolatileImage emissivefileimage = null;
		public BufferedImage emissivesnapimage = null;
		public VolatileImage alphafileimage = null;
		public BufferedImage alphasnapimage = null;
		public VolatileImage roughnessfileimage = null;
		public BufferedImage roughnesssnapimage = null;
		public VolatileImage metallicfileimage = null;
		public BufferedImage metallicsnapimage = null;
		public VolatileImage sheenfileimage = null;
		public BufferedImage sheensnapimage = null;
		public VolatileImage bumpfileimage = null;
		public BufferedImage bumpsnapimage = null;
		public VolatileImage dispfileimage = null;
		public BufferedImage dispsnapimage = null;
		public VolatileImage decalfileimage = null;
		public BufferedImage decalsnapimage = null;
		public String filename = null;
		public Color facecolor = null;
		public Color ambientcolor = null;
		public Color specularcolor = null;
		public Color emissivecolor = null;
		public float specularexp = 250.0f;
		public float emissivity = 0.0f;
		public float transparency = 1.0f;
		public float roughness = 1.0f;
		public float metallic = 0.0f;
		public float sheen = 0.0f;
		public float coatthickness = 0.0f;
		public float coatroughtness = 0.0f;
		public float anisotropy = 0.0f;
		public float anisotropyrot = 0.0f;
		public float refraction = 1.45f;
		public Material() {}
		public Material(Color facecolori, float transparencyi, VolatileImage fileimagei) {this.facecolor=facecolori;this.transparency=transparencyi;this.fileimage=fileimagei;}
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
			Material k = new Material();
			k.materialname = this.materialname;
			k.fileimage = this.fileimage;
			k.snapimage = this.snapimage;
			k.ambientfileimage = this.ambientfileimage;
			k.ambientsnapimage = this.ambientsnapimage;
			k.specularfileimage = this.specularfileimage;
			k.specularsnapimage = this.specularsnapimage;
			k.specularhighfileimage = this.specularhighfileimage;
			k.specularhighsnapimage = this.specularhighsnapimage;
			k.emissivefileimage = this.emissivefileimage;
			k.emissivesnapimage = this.emissivesnapimage;
			k.alphafileimage = this.alphafileimage;
			k.alphasnapimage = this.alphasnapimage;
			k.roughnessfileimage = this.roughnessfileimage;
			k.roughnesssnapimage = this.roughnesssnapimage;
			k.metallicfileimage = this.metallicfileimage;
			k.metallicsnapimage = this.metallicsnapimage;
			k.sheenfileimage = this.sheenfileimage;
			k.sheensnapimage = this.sheensnapimage;
			k.bumpfileimage = this.bumpfileimage;
			k.bumpsnapimage = this.bumpsnapimage;
			k.dispfileimage = this.dispfileimage;
			k.dispsnapimage = this.dispsnapimage;
			k.decalfileimage = this.decalfileimage;
			k.decalsnapimage = this.decalsnapimage;
			k.filename = this.filename;
			k.facecolor = this.facecolor;
			k.ambientcolor = this.ambientcolor;
			k.specularcolor = this.specularcolor;
			k.emissivecolor = this.emissivecolor;
			k.specularexp = this.specularexp;
			k.emissivity = this.emissivity;
			k.transparency = this.transparency;
			k.roughness = this.roughness;
			k.metallic = this.metallic;
			k.sheen = this.sheen;
			k.coatthickness = this.coatthickness;
			k.coatroughtness = this.coatroughtness;
			k.anisotropy = this.anisotropy;
			k.anisotropyrot = this.anisotropyrot;
			k.refraction = this.refraction;
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
		public Direction[][] nbuffer = null;
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
		public Direction[] fwddirs;
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
	public static class Plane {public double a,b,c,d; public Plane(double ai,double bi,double ci,double di){this.a=ai;this.b=bi;this.c=ci;this.d=di;}
		public Plane invert(){return new Plane(-this.a,-this.b,-this.c,-this.d);}
	}
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
	public static class Triangle implements Comparable<Triangle> {public Position pos1,pos2,pos3; public Direction norm; public Material mat = null; public Material[] lmatl = null; public int ind=-1; public Triangle(Position pos1i,Position pos2i,Position pos3i){this.pos1=pos1i;this.pos2=pos2i;this.pos3=pos3i;}
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
		public Triangle copy(){Triangle k=new Triangle(this.pos1.copy(),this.pos2.copy(),this.pos3.copy());k.norm=this.norm;k.mat=this.mat;k.lmatl=this.lmatl;k.ind=this.ind;return k;}
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
								if ((model.objects[k].faceindex[j].facevertexindex[i].textureindex>0)&&(model.objects[k].faceindex[j].facevertexindex[i].normalindex>0)) {
									modelobjfile.write("/");
									modelobjfile.write(""+model.objects[k].faceindex[j].facevertexindex[i].textureindex);
									modelobjfile.write("/");
									modelobjfile.write(""+model.objects[k].faceindex[j].facevertexindex[i].normalindex);
								} else if (model.objects[k].faceindex[j].facevertexindex[i].textureindex>0) {
									modelobjfile.write("/");
									modelobjfile.write(""+model.objects[k].faceindex[j].facevertexindex[i].textureindex);
								} else if (model.objects[k].faceindex[j].facevertexindex[i].normalindex>0) {
									modelobjfile.write("/");
									modelobjfile.write("/");
									modelobjfile.write(""+model.objects[k].faceindex[j].facevertexindex[i].normalindex);
								}
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
					modelobjfile.write("Ns "+model.materials[i].specularexp);
					modelobjfile.newLine();
					if (model.materials[i].ambientcolor!=null) {
						float[] rgbcolor = model.materials[i].ambientcolor.getRGBColorComponents(new float[3]);
						modelobjfile.write("Ka "+rgbcolor[0]+" "+rgbcolor[1]+" "+rgbcolor[2]);
						modelobjfile.newLine();
					}
					if (model.materials[i].facecolor!=null) {
						float[] rgbcolor = model.materials[i].facecolor.getRGBColorComponents(new float[3]);
						modelobjfile.write("Kd "+rgbcolor[0]+" "+rgbcolor[1]+" "+rgbcolor[2]);
						modelobjfile.newLine();
					}
					if (model.materials[i].specularcolor!=null) {
						float[] rgbcolor = model.materials[i].specularcolor.getRGBColorComponents(new float[3]);
						modelobjfile.write("Ks "+rgbcolor[0]+" "+rgbcolor[1]+" "+rgbcolor[2]);
						modelobjfile.newLine();
					}
					if (model.materials[i].emissivecolor!=null) {
						float[] rgbcolor = model.materials[i].emissivecolor.getRGBColorComponents(new float[3]);
						modelobjfile.write("Ke "+rgbcolor[0]+" "+rgbcolor[1]+" "+rgbcolor[2]);
						modelobjfile.newLine();
					}
					modelobjfile.write("Ni "+model.materials[i].refraction);
					modelobjfile.newLine();
					modelobjfile.write("d "+model.materials[i].transparency);
					modelobjfile.newLine();
					modelobjfile.write("illum 2");
					modelobjfile.newLine();
					modelobjfile.write("Pr "+model.materials[i].roughness);
					modelobjfile.newLine();
					modelobjfile.write("Pm "+model.materials[i].metallic);
					modelobjfile.newLine();
					modelobjfile.write("Ps "+model.materials[i].sheen);
					modelobjfile.newLine();
					modelobjfile.write("Pc "+model.materials[i].coatthickness);
					modelobjfile.newLine();
					modelobjfile.write("Pcr "+model.materials[i].coatroughtness);
					modelobjfile.newLine();
					modelobjfile.write("aniso "+model.materials[i].anisotropy);
					modelobjfile.newLine();
					modelobjfile.write("anisor "+model.materials[i].anisotropyrot);
					modelobjfile.newLine();
					if (model.materials[i].fileimage!=null) {
						String savefilename = model.materials[i].filename;
						modelobjfile.write("map_Kd "+savefilename);
						modelobjfile.newLine();
						File imagefile = new File(savemtlfile.getParent(), savefilename);
						ImageIO.write(model.materials[i].fileimage.getSnapshot(), "PNG", imagefile);
					}
					if (model.materials[i].ambientfileimage!=null) {
						String savefilename = model.materials[i].filename;
						savefilename = savefilename.substring(0, savefilename.length()-4)+"_ambient.png";
						modelobjfile.write("map_Ka "+savefilename);
						modelobjfile.newLine();
						File imagefile = new File(savemtlfile.getParent(), savefilename);
						ImageIO.write(model.materials[i].ambientfileimage.getSnapshot(), "PNG", imagefile);
					}
					if (model.materials[i].specularfileimage!=null) {
						String savefilename = model.materials[i].filename;
						savefilename = savefilename.substring(0, savefilename.length()-4)+"_spec.png";
						modelobjfile.write("map_Ks "+savefilename);
						modelobjfile.newLine();
						File imagefile = new File(savemtlfile.getParent(), savefilename);
						ImageIO.write(model.materials[i].specularfileimage.getSnapshot(), "PNG", imagefile);
					}
					if (model.materials[i].specularhighfileimage!=null) {
						String savefilename = model.materials[i].filename;
						savefilename = savefilename.substring(0, savefilename.length()-4)+"_spechigh.png";
						modelobjfile.write("map_Ns "+savefilename);
						modelobjfile.newLine();
						File imagefile = new File(savemtlfile.getParent(), savefilename);
						ImageIO.write(model.materials[i].specularhighfileimage.getSnapshot(), "PNG", imagefile);
					}
					if (model.materials[i].emissivefileimage!=null) {
						String savefilename = model.materials[i].filename;
						savefilename = savefilename.substring(0, savefilename.length()-4)+"_emissive.png";
						modelobjfile.write("map_Ke "+savefilename);
						modelobjfile.newLine();
						File imagefile = new File(savemtlfile.getParent(), savefilename);
						ImageIO.write(model.materials[i].emissivefileimage.getSnapshot(), "PNG", imagefile);
					}
					if (model.materials[i].roughnessfileimage!=null) {
						String savefilename = model.materials[i].filename;
						savefilename = savefilename.substring(0, savefilename.length()-4)+"_roughness.png";
						modelobjfile.write("map_Pr "+savefilename);
						modelobjfile.newLine();
						File imagefile = new File(savemtlfile.getParent(), savefilename);
						ImageIO.write(model.materials[i].roughnessfileimage.getSnapshot(), "PNG", imagefile);
					}
					if (model.materials[i].metallicfileimage!=null) {
						String savefilename = model.materials[i].filename;
						savefilename = savefilename.substring(0, savefilename.length()-4)+"_metallic.png";
						modelobjfile.write("map_Pm "+savefilename);
						modelobjfile.newLine();
						File imagefile = new File(savemtlfile.getParent(), savefilename);
						ImageIO.write(model.materials[i].metallicfileimage.getSnapshot(), "PNG", imagefile);
					}
					if (model.materials[i].sheenfileimage!=null) {
						String savefilename = model.materials[i].filename;
						savefilename = savefilename.substring(0, savefilename.length()-4)+"_sheen.png";
						modelobjfile.write("map_Ps "+savefilename);
						modelobjfile.newLine();
						File imagefile = new File(savemtlfile.getParent(), savefilename);
						ImageIO.write(model.materials[i].sheenfileimage.getSnapshot(), "PNG", imagefile);
					}
					if (model.materials[i].alphafileimage!=null) {
						String savefilename = model.materials[i].filename;
						savefilename = savefilename.substring(0, savefilename.length()-4)+"_alpha.png";
						modelobjfile.write("map_d "+savefilename);
						modelobjfile.newLine();
						File imagefile = new File(savemtlfile.getParent(), savefilename);
						ImageIO.write(model.materials[i].alphafileimage.getSnapshot(), "PNG", imagefile);
					}
					if (model.materials[i].bumpfileimage!=null) {
						String savefilename = model.materials[i].filename;
						savefilename = savefilename.substring(0, savefilename.length()-4)+"_bump.png";
						modelobjfile.write("bump "+savefilename);
						modelobjfile.newLine();
						File imagefile = new File(savemtlfile.getParent(), savefilename);
						ImageIO.write(model.materials[i].bumpfileimage.getSnapshot(), "PNG", imagefile);
					}
					if (model.materials[i].dispfileimage!=null) {
						String savefilename = model.materials[i].filename;
						savefilename = savefilename.substring(0, savefilename.length()-4)+"_disp.png";
						modelobjfile.write("disp "+savefilename);
						modelobjfile.newLine();
						File imagefile = new File(savemtlfile.getParent(), savefilename);
						ImageIO.write(model.materials[i].dispfileimage.getSnapshot(), "PNG", imagefile);
					}
					if (model.materials[i].decalfileimage!=null) {
						String savefilename = model.materials[i].filename;
						savefilename = savefilename.substring(0, savefilename.length()-4)+"_decal.png";
						modelobjfile.write("decal "+savefilename);
						modelobjfile.newLine();
						File imagefile = new File(savemtlfile.getParent(), savefilename);
						ImageIO.write(model.materials[i].decalfileimage.getSnapshot(), "PNG", imagefile);
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
					    	for (int j=0;j<fargsplit.length;j++) {
						    	String[] fargsplit2 = fargsplit[j].split("/");
						    	for (int i=0;i<fargsplit2.length;i++) {
						    		if (fargsplit2[i].isBlank()) {
						    			fargsplit2[i] = "0";
						    		}
						    	}
						    	if (fargsplit2.length==1) {
						    		modelfacevertexindex.add(new ModelFaceVertexIndex(Integer.parseInt(fargsplit2[0]),0,0));
						    	} else if (fargsplit2.length==2) {
						    		modelfacevertexindex.add(new ModelFaceVertexIndex(Integer.parseInt(fargsplit2[0]),Integer.parseInt(fargsplit2[1]),0));
						    	} else {
						    		modelfacevertexindex.add(new ModelFaceVertexIndex(Integer.parseInt(fargsplit2[0]),Integer.parseInt(fargsplit2[1]),Integer.parseInt(fargsplit2[2])));
						    	}
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
					    	modelmaterials.add(new Material(Color.WHITE, 1.0f, null));
					    	modelmaterials.get(modelmaterials.size()-1).materialname = farg;
					    }else if (fline.toLowerCase().startsWith("map_kd ")) {
					    	String farg = fline.substring(7).trim();
					    	File loadimgfile = new File(loadmtlfile.getParent(),farg);
					    	modelmaterials.get(modelmaterials.size()-1).filename = farg;
					    	modelmaterials.get(modelmaterials.size()-1).fileimage = UtilLib.loadImage(loadimgfile.getPath(), loadresourcefromjar);
					    }else if (fline.toLowerCase().startsWith("map_ka ")) {
					    	String farg = fline.substring(7).trim();
					    	File loadimgfile = new File(loadmtlfile.getParent(),farg);
					    	modelmaterials.get(modelmaterials.size()-1).filename = farg;
					    	modelmaterials.get(modelmaterials.size()-1).ambientfileimage = UtilLib.loadImage(loadimgfile.getPath(), loadresourcefromjar);
					    }else if (fline.toLowerCase().startsWith("map_ks ")) {
					    	String farg = fline.substring(7).trim();
					    	File loadimgfile = new File(loadmtlfile.getParent(),farg);
					    	modelmaterials.get(modelmaterials.size()-1).filename = farg;
					    	modelmaterials.get(modelmaterials.size()-1).specularfileimage = UtilLib.loadImage(loadimgfile.getPath(), loadresourcefromjar);
					    }else if (fline.toLowerCase().startsWith("map_ke ")) {
					    	String farg = fline.substring(7).trim();
					    	File loadimgfile = new File(loadmtlfile.getParent(),farg);
					    	modelmaterials.get(modelmaterials.size()-1).filename = farg;
					    	modelmaterials.get(modelmaterials.size()-1).emissivefileimage = UtilLib.loadImage(loadimgfile.getPath(), loadresourcefromjar);
					    }else if (fline.toLowerCase().startsWith("map_pr ")) {
					    	String farg = fline.substring(7).trim();
					    	File loadimgfile = new File(loadmtlfile.getParent(),farg);
					    	modelmaterials.get(modelmaterials.size()-1).filename = farg;
					    	modelmaterials.get(modelmaterials.size()-1).roughnessfileimage = UtilLib.loadImage(loadimgfile.getPath(), loadresourcefromjar);
					    }else if (fline.toLowerCase().startsWith("map_pm ")) {
					    	String farg = fline.substring(7).trim();
					    	File loadimgfile = new File(loadmtlfile.getParent(),farg);
					    	modelmaterials.get(modelmaterials.size()-1).filename = farg;
					    	modelmaterials.get(modelmaterials.size()-1).metallicfileimage = UtilLib.loadImage(loadimgfile.getPath(), loadresourcefromjar);
					    }else if (fline.toLowerCase().startsWith("map_ps ")) {
					    	String farg = fline.substring(7).trim();
					    	File loadimgfile = new File(loadmtlfile.getParent(),farg);
					    	modelmaterials.get(modelmaterials.size()-1).filename = farg;
					    	modelmaterials.get(modelmaterials.size()-1).sheenfileimage = UtilLib.loadImage(loadimgfile.getPath(), loadresourcefromjar);
					    }else if (fline.toLowerCase().startsWith("map_ns ")) {
					    	String farg = fline.substring(7).trim();
					    	File loadimgfile = new File(loadmtlfile.getParent(),farg);
					    	modelmaterials.get(modelmaterials.size()-1).filename = farg;
					    	modelmaterials.get(modelmaterials.size()-1).specularhighfileimage = UtilLib.loadImage(loadimgfile.getPath(), loadresourcefromjar);
					    }else if (fline.toLowerCase().startsWith("map_d ")) {
					    	String farg = fline.substring(6).trim();
					    	File loadimgfile = new File(loadmtlfile.getParent(),farg);
					    	modelmaterials.get(modelmaterials.size()-1).filename = farg;
					    	modelmaterials.get(modelmaterials.size()-1).alphafileimage = UtilLib.loadImage(loadimgfile.getPath(), loadresourcefromjar);
					    }else if (fline.toLowerCase().startsWith("bump ")) {
					    	String farg = fline.substring(5).trim();
					    	File loadimgfile = new File(loadmtlfile.getParent(),farg);
					    	modelmaterials.get(modelmaterials.size()-1).filename = farg;
					    	modelmaterials.get(modelmaterials.size()-1).bumpfileimage = UtilLib.loadImage(loadimgfile.getPath(), loadresourcefromjar);
					    }else if (fline.toLowerCase().startsWith("disp ")) {
					    	String farg = fline.substring(5).trim();
					    	File loadimgfile = new File(loadmtlfile.getParent(),farg);
					    	modelmaterials.get(modelmaterials.size()-1).filename = farg;
					    	modelmaterials.get(modelmaterials.size()-1).dispfileimage = UtilLib.loadImage(loadimgfile.getPath(), loadresourcefromjar);
					    }else if (fline.toLowerCase().startsWith("decal ")) {
					    	String farg = fline.substring(5).trim();
					    	File loadimgfile = new File(loadmtlfile.getParent(),farg);
					    	modelmaterials.get(modelmaterials.size()-1).filename = farg;
					    	modelmaterials.get(modelmaterials.size()-1).decalfileimage = UtilLib.loadImage(loadimgfile.getPath(), loadresourcefromjar);
					    }else if (fline.toLowerCase().startsWith("kd ")) {
					    	String farg = fline.substring(3).trim();
					    	String[] fargsplit = farg.split(" ");
					    	modelmaterials.get(modelmaterials.size()-1).facecolor = new Color(Float.parseFloat(fargsplit[0]), Float.parseFloat(fargsplit[1]), Float.parseFloat(fargsplit[2]));
					    }else if (fline.toLowerCase().startsWith("ka ")) {
					    	String farg = fline.substring(3).trim();
					    	String[] fargsplit = farg.split(" ");
					    	modelmaterials.get(modelmaterials.size()-1).ambientcolor = new Color(Float.parseFloat(fargsplit[0]), Float.parseFloat(fargsplit[1]), Float.parseFloat(fargsplit[2]));
					    }else if (fline.toLowerCase().startsWith("ke ")) {
					    	String farg = fline.substring(3).trim();
					    	String[] fargsplit = farg.split(" ");
					    	modelmaterials.get(modelmaterials.size()-1).emissivecolor = new Color(Float.parseFloat(fargsplit[0]), Float.parseFloat(fargsplit[1]), Float.parseFloat(fargsplit[2]));
					    }else if (fline.toLowerCase().startsWith("ks ")) {
					    	String farg = fline.substring(3).trim();
					    	String[] fargsplit = farg.split(" ");
					    	modelmaterials.get(modelmaterials.size()-1).specularcolor = new Color(Float.parseFloat(fargsplit[0]), Float.parseFloat(fargsplit[1]), Float.parseFloat(fargsplit[2]));
					    }else if (fline.toLowerCase().startsWith("Ns ")) {
					    	String farg = fline.substring(2).trim();
					    	modelmaterials.get(modelmaterials.size()-1).specularexp = Float.parseFloat(farg);
					    }else if (fline.toLowerCase().startsWith("d ")) {
					    	String farg = fline.substring(2).trim();
					    	modelmaterials.get(modelmaterials.size()-1).transparency = Float.parseFloat(farg);
					    }else if (fline.toLowerCase().startsWith("ni ")) {
					    	String farg = fline.substring(3).trim();
					    	modelmaterials.get(modelmaterials.size()-1).refraction = Float.parseFloat(farg);
					    }else if (fline.toLowerCase().startsWith("pr ")) {
					    	String farg = fline.substring(3).trim();
					    	modelmaterials.get(modelmaterials.size()-1).roughness = Float.parseFloat(farg);
					    }else if (fline.toLowerCase().startsWith("pm ")) {
					    	String farg = fline.substring(3).trim();
					    	modelmaterials.get(modelmaterials.size()-1).metallic = Float.parseFloat(farg);
					    }else if (fline.toLowerCase().startsWith("pc ")) {
					    	String farg = fline.substring(3).trim();
					    	modelmaterials.get(modelmaterials.size()-1).coatthickness = Float.parseFloat(farg);
					    }else if (fline.toLowerCase().startsWith("pcr ")) {
					    	String farg = fline.substring(4).trim();
					    	modelmaterials.get(modelmaterials.size()-1).coatroughtness = Float.parseFloat(farg);
					    }else if (fline.toLowerCase().startsWith("aniso ")) {
					    	String farg = fline.substring(6).trim();
					    	modelmaterials.get(modelmaterials.size()-1).anisotropy = Float.parseFloat(farg);
					    }else if (fline.toLowerCase().startsWith("anisor ")) {
					    	String farg = fline.substring(7).trim();
					    	modelmaterials.get(modelmaterials.size()-1).anisotropyrot = Float.parseFloat(farg);
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
