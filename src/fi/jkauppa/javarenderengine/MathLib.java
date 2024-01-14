package fi.jkauppa.javarenderengine;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import fi.jkauppa.javarenderengine.ModelLib.Material;

public class MathLib {
	public static class Position implements Comparable<Position> {public double x,y,z; public Coordinate tex; public Position(double xi,double yi,double zi){this.x=xi;this.y=yi;this.z=zi;}
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
		public static class SphereRenderComparator implements Comparator<Sphere> {
			@Override public int compare(Sphere o1, Sphere o2) {
				int k = -1;
				if (o1.z>o2.z) {
					k = 1;
				} else if (o1.z==o2.z) {
					double ydiff = o1.y-o2.y;
					double xdiff = o1.x-o2.x;
					if (Math.abs(ydiff)>Math.abs(xdiff)) {
						if (Math.abs(o1.y)>Math.abs(o2.y)) {
							k = 1;
						} else if (o1.y==o2.y) {
							if (Math.abs(o1.x)>Math.abs(o2.x)) {
								k = 1;
							} else if (o1.x==o2.x) {
								k = 0;
							}
						}
					} else {
						if (Math.abs(o1.x)>Math.abs(o2.x)) {
							k = 1;
						} else if (o1.x==o2.x) {
							if (Math.abs(o1.y)>Math.abs(o2.y)) {
								k = 1;
							} else if (o1.y==o2.y) {
								k = 0;
							}
						}
					}
				}
				return k;
			}
			
		}
		public static class SphereDistanceComparator implements Comparator<Sphere> {
			public Position origin;
			public SphereDistanceComparator(Position origini) {this.origin = origini;}
			@Override public int compare(Sphere o1, Sphere o2) {
				int k = 1;
				Sphere[] spheres = {o1,o2};
				Direction[] spheredir = vectorFromPoints(this.origin, spheres);
				double[] spheredist = vectorLength(spheredir);				
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
	public static class Quad {public Position pos1,pos2,pos3,pos4; public Quad(Position pos1i,Position pos2i,Position pos3i,Position pos4i){this.pos1=pos1i;this.pos2=pos2i;this.pos3=pos3i;this.pos4=pos4i;}}
	public static class Arc {public Position origin; public double r,ang1,ang2; public Arc(Position origini, double ri, double ang1i, double ang2i){this.origin=origini;this.r=ri;this.ang1=ang1i;this.ang2=ang2i;}}
	public static class Ray {public Position pos; public Direction dir; public Ray(Position posi, Direction diri){this.pos=posi;this.dir=diri;}}
	public static class Plane {public double a,b,c,d; public Plane(double ai,double bi,double ci,double di){this.a=ai;this.b=bi;this.c=ci;this.d=di;}}
	public static class Line implements Comparable<Line> {public Position pos1,pos2; public int hitind=-1; public Line(Position pos1i,Position pos2i){this.pos1=pos1i;this.pos2=pos2i;}
		@Override public int compareTo(Line o){
			int k=-1;
			Line ts=this.sort();
			Line os=o.sort();
			if (ts.pos1.z>os.pos1.z) {
				k=1;
			}else if (ts.pos1.z==os.pos1.z) {
				if(ts.pos1.y>os.pos1.y){
					k=1;
				}else if (ts.pos1.y==os.pos1.y) {
					if(ts.pos1.x>os.pos1.x) {
						k=1;
					} else if (ts.pos1.x==os.pos1.x) {
						if(ts.pos2.z>os.pos2.z) {
							k=1;
						} else if (ts.pos2.z==os.pos2.z) {
							if(ts.pos2.y>os.pos2.y) {
								k=1;
							} else if (ts.pos2.y==os.pos2.y) {
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
				if ((ts.pos1.x==os.pos1.x)&&(this.pos1.y==os.pos1.y)&&(this.pos1.z==os.pos1.z)&&(ts.pos2.x==os.pos2.x)&&(this.pos2.y==os.pos2.y)&&(this.pos2.z==os.pos2.z)) {
					k = true;
				}
			}
			return k;
		}
		public Line copy(){Line k = new Line(new Position(this.pos1.x,this.pos1.y,this.pos1.z),new Position(this.pos2.x,this.pos2.y,this.pos2.z)); k.hitind=this.hitind; return k;}
		public Line swap(){return new Line(this.pos2,this.pos1);}
		public Line sort(){Line k=this;boolean keeporder=true;if(this.pos1.x>this.pos2.x){keeporder=false;}else if(this.pos1.x==this.pos2.x){if(this.pos1.y>this.pos2.y){keeporder=false;}else if (this.pos1.y==this.pos2.y){if(this.pos1.z>this.pos2.z){keeporder=false;}}}if(!keeporder){k=this.swap();}return k;}
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
				Position[] tposarray = {this.pos1,this.pos2,this.pos3,this.pos4};
				Position[] oposarray = {co.pos1,co.pos2,co.pos3,this.pos4};
				Arrays.sort(tposarray);
				Arrays.sort(oposarray);
				if ((tposarray[0].compareTo(oposarray[0])==0)&&(tposarray[1].compareTo(oposarray[1])==0)&&(tposarray[2].compareTo(oposarray[2])==0)&&(tposarray[3].compareTo(oposarray[3])==0)) {
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
	
	public static double[] vectorDot(Direction[] vdir, Position vpoint){double[] k=null; if((vdir!=null)&&(vpoint!=null)){k=new double[vdir.length];for(int n=0;n<vdir.length;n++){k[n] = vdir[n].dx*vpoint.x+vdir[n].dy*vpoint.y+vdir[n].dz*vpoint.z;}}return k;}
	public static double[] vectorDot(Direction[] vdir, Position[] vpoint){double[] k=null; if((vdir!=null)&&(vpoint!=null)&&(vdir.length==vpoint.length)){k=new double[vdir.length];for(int n=0;n<vdir.length;n++){k[n] = vdir[n].dx*vpoint[n].x+vdir[n].dy*vpoint[n].y+vdir[n].dz*vpoint[n].z;}}return k;}
	public static double[] vectorDot(Direction vdir1, Direction[] vdir2){double[] k=null; if((vdir1!=null)&&(vdir2!=null)){k=new double[vdir2.length];for(int n=0;n<vdir2.length;n++){k[n] = vdir1.dx*vdir2[n].dx+vdir1.dy*vdir2[n].dy+vdir1.dz*vdir2[n].dz;}}return k;}
	public static double vectorDot(Direction vdir1, Direction vdir2){return vdir1.dx*vdir2.dx+vdir1.dy*vdir2.dy+vdir1.dz*vdir2.dz;}
	public static double[] vectorDot(Direction[] vdir1, Direction[] vdir2){double[] k=null; if((vdir1!=null)&&(vdir2!=null)&&(vdir1.length==vdir2.length)){k=new double[vdir1.length];for(int n=0;n<vdir1.length;n++){k[n] = vdir1[n].dx*vdir2[n].dx+vdir1[n].dy*vdir2[n].dy+vdir1[n].dz*vdir2[n].dz;}}return k;}
	public static double[] vectorDot(Direction[] vdir){double[] k=null; if(vdir!=null){k=new double[vdir.length];for(int n=0;n<vdir.length;n++){k[n] = vdir[n].dx*vdir[n].dx+vdir[n].dy*vdir[n].dy+vdir[n].dz*vdir[n].dz;}}return k;}
	public static double vectorDot(Direction vdir){return vdir.dx*vdir.dx+vdir.dy*vdir.dy+vdir.dz*vdir.dz;}
	public static double[] vectorDot(Plane[] vplane, Position vpoint){double[] k=null; if((vplane!=null)&&(vpoint!=null)){k=new double[vplane.length];for(int n=0;n<vplane.length;n++){k[n] = vplane[n].a*vpoint.x+vplane[n].b*vpoint.y+vplane[n].c*vpoint.z+vplane[n].d;}}return k;}
	public static double[] vectorDot(Plane[] vplane, Direction vdir){double[] k=null; if((vplane!=null)&&(vdir!=null)){k=new double[vplane.length];for(int n=0;n<vplane.length;n++){k[n] = vplane[n].a*vdir.dx+vplane[n].b*vdir.dy+vplane[n].c*vdir.dz;}}return k;}

	public static Direction[] vectorCross(Direction vdir1, Direction[] vdir2) {
		Direction[] k=null;
		if ((vdir1!=null)&&(vdir2!=null)) {
			k=new Direction[vdir2.length];
			for (int n=0;n<vdir2.length;n++) {
				k[n] = new Direction(vdir1.dy*vdir2[n].dz-vdir1.dz*vdir2[n].dy,-(vdir1.dx*vdir2[n].dz-vdir1.dz*vdir2[n].dx),vdir1.dx*vdir2[n].dy-vdir1.dy*vdir2[n].dx);
			}
		}
		return k;
	}
	public static Direction[] vectorCross(Direction[] vdir1, Direction vdir2) {
		Direction[] k=null;
		if ((vdir1!=null)&&(vdir2!=null)) {
			k=new Direction[vdir1.length];
			for (int n=0;n<vdir1.length;n++) {
				k[n] = new Direction(vdir1[n].dy*vdir2.dz-vdir1[n].dz*vdir2.dy,-(vdir1[n].dx*vdir2.dz-vdir1[n].dz*vdir2.dx),vdir1[n].dx*vdir2.dy-vdir1[n].dy*vdir2.dx);
			}
		}
		return k;
	}
	public static Direction[] vectorCross(Direction[] vdir1, Direction[] vdir2) {
		Direction[] k=null;
		if ((vdir1!=null)&&(vdir2!=null)&&(vdir1.length==vdir2.length)) {
			k=new Direction[vdir1.length];
			for (int n=0;n<vdir1.length;n++) {
				k[n] = new Direction(vdir1[n].dy*vdir2[n].dz-vdir1[n].dz*vdir2[n].dy,-(vdir1[n].dx*vdir2[n].dz-vdir1[n].dz*vdir2[n].dx),vdir1[n].dx*vdir2[n].dy-vdir1[n].dy*vdir2[n].dx);
			}
		}
		return k;
	}
	public static double[] vectorLength(Direction[] vdir) {
		double[] k = null;
		if (vdir!=null) {
			k = new double[vdir.length];
			double[] vdirlength = vectorDot(vdir);
			for (int n=0;n<vdir.length;n++) {
				k[n] = Math.sqrt(vdirlength[n]);
			}
		}
		return k;
	}
	public static double[] vectorLengthMax(Direction[] vdir) {
		double[] k = null;
		if (vdir!=null) {
			k = new double[vdir.length];
			for (int n=0;n<vdir.length;n++) {
				if (vdir[n]!=null) {
					double vdirlength = vectorDot(vdir[n]);
					k[n] = Math.sqrt(vdirlength);
				} else {
					k[n] = Double.MAX_VALUE;
				}
			}
		}
		return k;
	}
	public static double[] vectorAngle(Direction vdir1, Direction[] vdir2) {
		double[] k = null;
		if ((vdir1!=null)&&(vdir2!=null)) {
			k = new double[vdir2.length];
			Direction[] vdir1ar = new Direction[1]; vdir1ar[0] = vdir1; 
			double[] vdir1length = vectorLength(vdir1ar);
			double[] vdir2length = vectorLength(vdir2);
			double[] vdir12dot = vectorDot(vdir1,vdir2);
			for (int n=0;n<vdir2.length;n++) {
				k[n] = (180.0f/Math.PI)*Math.acos(vdir12dot[n]/(vdir1length[0]*vdir2length[n]));
			}
		}
		return k;
	}
	public static double[] vectorAngle(Direction[] vdir1, Direction[] vdir2) {
		double[] k = null;
		if ((vdir1!=null)&&(vdir2!=null)&&(vdir1.length==vdir2.length)) {
			k = new double[vdir1.length];
			double[] vdir1length = vectorLength(vdir1);
			double[] vdir2length = vectorLength(vdir2);
			double[] vdir12dot = vectorDot(vdir1,vdir2);
			for (int n=0;n<vdir1.length;n++) {
				k[n] = (180.0f/Math.PI)*Math.acos(vdir12dot[n]/(vdir1length[n]*vdir2length[n]));
			}
		}
		return k;
	}
	public static Direction[] normalizeVector(Direction[] vdir) {
		Direction[] k = null;
		if (vdir!=null) {
			k = new Direction[vdir.length];
			double[] vdirlength =  vectorLength(vdir);
			for (int n=0;n<vdir.length;n++) {
				k[n] = new Direction(vdir[n].dx/vdirlength[n], vdir[n].dy/vdirlength[n], vdir[n].dz/vdirlength[n]);
			}
		}
		return k;
	}
	public static Direction[] planeNormals(Plane[] vplane) {
		Direction[] k = null;
		if (vplane!=null) {
			k = new Direction[vplane.length];
			for (int n=0;n<vplane.length;n++) {
				k[n] = new Direction(vplane[n].a,vplane[n].b,vplane[n].c);
			}
			k = normalizeVector(k);
		}
		return k;
	}
	public static Plane[] planeFromNormalAtPoint(Position vpoint, Direction[] vnormal) {
		Plane[] k = null;
		if ((vpoint!=null)&&(vnormal!=null)) {
			k = new Plane[vnormal.length];
			Direction[] nm = normalizeVector(vnormal);
			double[] dv = vectorDot(nm,vpoint);
			for (int n=0;n<vnormal.length;n++) {
				k[n] = new Plane(nm[n].dx,nm[n].dy,nm[n].dz,-dv[n]);
			}
		}
		return k;
	}
	public static Plane[] planeFromNormalAtPoint(Position[] vpoint, Direction[] vnormal) {
		Plane[] k = null;
		if ((vpoint!=null)&&(vnormal!=null)&&(vpoint.length==vnormal.length)) {
			k = new Plane[vpoint.length];
			Direction[] nm = normalizeVector(vnormal);
			double[] dv = vectorDot(nm,vpoint);
			for (int n=0;n<vpoint.length;n++) {
				k[n] = new Plane(nm[n].dx,nm[n].dy,nm[n].dz,-dv[n]);
			}
		}
		return k;
	}
	public static Direction[] vectorFromPoints(Position vpoint1, Position[] vpoint2) {
		Direction[] k = null;
		if ((vpoint1!=null)&&(vpoint2!=null)) {
			k = new Direction[vpoint2.length];
			if (vpoint1!=null) {
				for (int n=0;n<vpoint2.length;n++) {
					if (vpoint2[n]!=null) {
						k[n] = new Direction(vpoint2[n].x-vpoint1.x, vpoint2[n].y-vpoint1.y, vpoint2[n].z-vpoint1.z);
					}
				}
			}
		}
		return k;
	}
	public static Direction[] vectorFromPoints(Position[] vpoint1, Position[] vpoint2) {
		Direction[] k = null;
		if ((vpoint1!=null)&&(vpoint2!=null)&&(vpoint1.length==vpoint2.length)) {
			k = new Direction[vpoint1.length];
			for (int n=0;n<vpoint1.length;n++) {
				if ((vpoint1[n]!=null)&&(vpoint2[n]!=null)) {
					k[n] = new Direction(vpoint2[n].x-vpoint1[n].x, vpoint2[n].y-vpoint1[n].y, vpoint2[n].z-vpoint1[n].z);
				}
			}
		}
		return k;
	}
	public static Direction[] vectorFromPoints(Position vpoint1, Sphere[] vsphere) {
		Direction[] k = null;
		if ((vpoint1!=null)&&(vsphere!=null)) {
			k = new Direction[vsphere.length];
			if (vpoint1!=null) {
				for (int n=0;n<vsphere.length;n++) {
					if (vsphere[n]!=null) {
						k[n] = new Direction(vsphere[n].x-vpoint1.x, vsphere[n].y-vpoint1.y, vsphere[n].z-vpoint1.z);
					}
				}
			}
		}
		return k;
	}
	public static Plane[] planeFromPoints(Triangle[] vtri) {
		Plane[] k = null;
		if (vtri!=null) {
			k = new Plane[vtri.length];
			for (int n=0;n<vtri.length;n++) {
				Position[] p1 = new Position[1]; p1[0] = vtri[n].pos1;
				Position[] p2 = new Position[1]; p2[0] = vtri[n].pos2;
				Position[] p3 = new Position[1]; p3[0] = vtri[n].pos3;
				Direction[] v1 = vectorFromPoints(p1, p2);
				Direction[] v2 = vectorFromPoints(p1, p3);
				Direction[] nm = normalizeVector(vectorCross(v1, v2));
				Plane[] pplane = planeFromNormalAtPoint(p1, nm);
				k[n] = pplane[0];
			}
		}
		return k;
	}
	public static double[][] pointPlaneDistance(Position[] vpoint, Plane[] vplane) {
		double[][] k = null;
		if ((vpoint!=null)&&(vplane!=null)) {
			Direction[] vplanedir = new Direction[vplane.length];
			for (int n=0;n<vplanedir.length;n++) {
				vplanedir[n] = new Direction(vplane[n].a, vplane[n].b, vplane[n].c);
			}
			double[] vplanelen = vectorLength(vplanedir);
			k = new double[vpoint.length][vplane.length];
			for (int n=0;n<vpoint.length;n++) {
				double[] top = vectorDot(vplane, vpoint[n]);
				for (int m=0;m<vplane.length;m++) {
					k[n][m] = top[m]/vplanelen[m];
				}
			}
		}
		return k;
	}
	public static double[][] rayPointDistance(Position vpos, Direction[] vdir, Position[] vpoint) {
		double[][] k = null;
		if ((vpos!=null)&&(vdir!=null)&&(vpoint!=null)) {
			k = new double[vdir.length][vpoint.length];
			for (int n=0;n<vdir.length;n++) {
				//TODO closest distance from point to a line
				//t0 = (ldir . (point - lpos)) / (ldir . ldir) 
			}
		}
		return k;
	}
	public static double[][] rayPlaneDistance(Position vpos, Direction[] vdir, Plane[] vplane) {
		double[][] k = null;
		if ((vpos!=null)&&(vdir!=null)&&(vplane!=null)) {
			k = new double[vdir.length][vplane.length];
			for (int n=0;n<vdir.length;n++) {
				double[] top = vectorDot(vplane, vpos);
				double[] bottom = vectorDot(vplane, vdir[n]);
				for (int m=0;m<vplane.length;m++) {
					k[n][m] = -top[m]/bottom[m];
				}
			}
		}
		return k;
	}
	public static Position[][] rayRayIntersection(Position[] vpos1, Direction[] vdir1, Position[] vpos2, Direction[] vdir2) {
		Position[][] k = null;
		if ((vpos1!=null)&&(vdir1!=null)&&(vpos2!=null)&&(vdir2!=null)&&(vpos1.length==vdir1.length)&&(vpos2.length==vdir2.length)) {
			k = new Position[vdir1.length][vdir2.length];
			//TODO ray-ray intersection 
		}
		return k;
	}
	public static Position[][] rayTriangleIntersection(Position vpos, Direction[] vdir, Triangle[] vtri) {
		Position[][] k = null;
		if ((vpos!=null)&&(vdir!=null)&&(vtri!=null)) {
			k = new Position[vdir.length][vtri.length];
			Plane[] tplanes = planeFromPoints(vtri);
			double[][] tpdist = rayPlaneDistance(vpos, vdir, tplanes);
			for (int n=0;n<vdir.length;n++) {
				for (int m=0;m<vtri.length;m++) {
					if ((!Double.isInfinite(tpdist[n][m]))&&(tpdist[n][m]>=0)) {
						Position[] p4 = new Position[1]; p4[0] = new Position(vpos.x+vdir[n].dx*tpdist[n][m],vpos.y+vdir[n].dy*tpdist[n][m],vpos.z+vdir[n].dz*tpdist[n][m]);
						Position[] p1 = new Position[1]; p1[0] = vtri[m].pos1;
						Position[] p2 = new Position[1]; p2[0] = vtri[m].pos2;
						Position[] p3 = new Position[1]; p3[0] = vtri[m].pos3;
						Direction[] v12 = vectorFromPoints(p1, p2); Direction[] v21 = vectorFromPoints(p2, p1);
						Direction[] v13 = vectorFromPoints(p1, p3); Direction[] v31 = vectorFromPoints(p3, p1);
						Direction[] v23 = vectorFromPoints(p2, p3); Direction[] v32 = vectorFromPoints(p3, p2);
						double[] a1 = vectorAngle(v12,v13);
						double[] a2 = vectorAngle(v21,v23);
						double[] a3 = vectorAngle(v31,v32);
						Direction[] t1 = vectorFromPoints(p1, p4);
						Direction[] t2 = vectorFromPoints(p2, p4);
						Direction[] t3 = vectorFromPoints(p3, p4);
						double[] h12 = vectorAngle(v12,t1); double[] h13 = vectorAngle(v13,t1);
						double[] h21 = vectorAngle(v21,t2); double[] h23 = vectorAngle(v23,t2);
						double[] h31 = vectorAngle(v31,t3); double[] h32 = vectorAngle(v32,t3);
						boolean isatpoint = ((t1[0].dx==0)&&(t1[0].dy==0)&&(t1[0].dz==0))||((t2[0].dx==0)&&(t2[0].dy==0)&&(t2[0].dz==0))||((t3[0].dx==0)&&(t3[0].dy==0)&&(t3[0].dz==0));
						boolean withinangles = (h12[0]<=a1[0])&&(h13[0]<=a1[0])&&(h21[0]<=a2[0])&&(h23[0]<=a2[0])&&(h31[0]<=a3[0])&&(h32[0]<=a3[0]);
						if(isatpoint||withinangles) {
							k[n][m] = p4[0];
						}
					}
				}
			}
		}
		return k;
	}
	public static Position[][] rayQuadIntersection(Position vpos, Direction[] vdir, Quad[] vquad) {
		Position[][] k = null;
		if ((vpos!=null)&&(vdir!=null)&&(vquad!=null)) {
			k = new Position[vdir.length][vquad.length];
			//TODO ray quad intersection
		}
		return k;
	}
	public static Line[][] rayAxisAlignedBoundingBoxIntersection(Position vpos, Direction[] vdir, AxisAlignedBoundingBox[] vaabb) {
		Line[][] k = null;
		if ((vpos!=null)&&(vdir!=null)&&(vaabb!=null)) {
			k = new Line[vdir.length][vaabb.length];
			for (int m=0;m<vaabb.length;m++) {
				double vaabbxmin=vaabb[m].x1; double vaabbxmax=vaabb[m].x2;
				if (vaabbxmin>vaabbxmax) {vaabbxmin=vaabb[m].x2;vaabbxmax=vaabb[m].x1;}
				double vaabbymin=vaabb[m].y1; double vaabbymax=vaabb[m].y2;
				if (vaabbymin>vaabbymax) {vaabbymin=vaabb[m].y2;vaabbymax=vaabb[m].y1;}
				double vaabbzmin=vaabb[m].z1;double vaabbzmax=vaabb[m].z2;
				if (vaabbzmin>vaabbzmax) {vaabbzmin=vaabb[m].z2;vaabbzmax=vaabb[m].z1;}
				Position vaabbpos1 = new Position(vaabbxmin,vaabbymin,vaabbzmin);
				Position vaabbpos2 = new Position(vaabbxmax,vaabbymax,vaabbzmax);
				Direction[] xyzplanenormals = {new Direction(1,0,0),new Direction(0,1,0),new Direction(0,0,1)};
				Plane[] xyzplanes1 = planeFromNormalAtPoint(vaabbpos1, xyzplanenormals);
				Plane[] xyzplanes2 = planeFromNormalAtPoint(vaabbpos2, xyzplanenormals);
				double[][] xyzcp1dist = rayPlaneDistance(vpos, vdir, xyzplanes1);
				double[][] xyzcp2dist = rayPlaneDistance(vpos, vdir, xyzplanes2);
				for (int n=0;n<vdir.length;n++) {
					Position[] xyzplanesint1 = new Position[3]; 
					Position[] xyzplanesint2 = new Position[3]; 
					for (int i=0;i<3;i++) {xyzplanesint1[i] = new Position(vpos.x+xyzcp1dist[n][i]*vdir[n].dx,vpos.y+xyzcp1dist[n][i]*vdir[n].dy,vpos.z+xyzcp1dist[n][i]*vdir[n].dz);}
					for (int i=0;i<3;i++) {xyzplanesint2[i] = new Position(vpos.x+xyzcp2dist[n][i]*vdir[n].dx,vpos.y+xyzcp2dist[n][i]*vdir[n].dy,vpos.z+xyzcp2dist[n][i]*vdir[n].dz);}
					TreeSet<Position> vaabbplanehitpositions = new TreeSet<Position>(); 
					if ((xyzplanesint1[0].y>=vaabbpos1.y)&&(xyzplanesint1[0].y<=vaabbpos2.y)&&(xyzplanesint1[0].z>=vaabbpos1.z)&&(xyzplanesint1[0].z<=vaabbpos2.z)) {
						vaabbplanehitpositions.add(xyzplanesint1[0]);
					}
					if ((xyzplanesint1[1].x>=vaabbpos1.x)&&(xyzplanesint1[1].x<=vaabbpos2.x)&&(xyzplanesint1[1].z>=vaabbpos1.z)&&(xyzplanesint1[1].z<=vaabbpos2.z)) {
						vaabbplanehitpositions.add(xyzplanesint1[1]);
					}
					if ((xyzplanesint1[2].x>=vaabbpos1.x)&&(xyzplanesint1[2].x<=vaabbpos2.x)&&(xyzplanesint1[2].y>=vaabbpos1.y)&&(xyzplanesint1[2].y<=vaabbpos2.y)) {
						vaabbplanehitpositions.add(xyzplanesint1[2]);
					}
					if ((xyzplanesint2[0].y>=vaabbpos1.y)&&(xyzplanesint2[0].y<=vaabbpos2.y)&&(xyzplanesint2[0].z>=vaabbpos1.z)&&(xyzplanesint2[0].z<=vaabbpos2.z)) {
						vaabbplanehitpositions.add(xyzplanesint2[0]);
					}
					if ((xyzplanesint2[1].x>=vaabbpos1.x)&&(xyzplanesint2[1].x<=vaabbpos2.x)&&(xyzplanesint2[1].z>=vaabbpos1.z)&&(xyzplanesint2[1].z<=vaabbpos2.z)) {
						vaabbplanehitpositions.add(xyzplanesint2[1]);
					}
					if ((xyzplanesint2[2].x>=vaabbpos1.x)&&(xyzplanesint2[2].x<=vaabbpos2.x)&&(xyzplanesint2[2].y>=vaabbpos1.y)&&(xyzplanesint2[2].y<=vaabbpos2.y)) {
						vaabbplanehitpositions.add(xyzplanesint2[2]);
					}
					Position[] vaabbplanerayhitposlist = vaabbplanehitpositions.toArray(new Position[vaabbplanehitpositions.size()]);
					if (vaabbplanerayhitposlist.length>1) {
						k[n][m] = new Line(vaabbplanerayhitposlist[0],vaabbplanerayhitposlist[1]);
					} else if (vaabbplanerayhitposlist.length>0) {
						k[n][m] = new Line(vaabbplanerayhitposlist[0],vaabbplanerayhitposlist[0]);
					}
				}
			}
		}
		return k;
	}
	public static Line[][] rayCuboidIntersection(Position vpos, Direction[] vdir, Cuboid[] vcub) {
		Line[][] k = null;
		if ((vpos!=null)&&(vdir!=null)&&(vcub!=null)) {
			k = new Line[vdir.length][vcub.length];
			for (int m=0;m<vcub.length;m++) {
				Triangle vaabbtri1 = new Triangle(vcub[m].poslft,vcub[m].poslbt,vcub[m].posrft);
				Triangle vaabbtri2 = new Triangle(vcub[m].posrbt,vcub[m].poslbt,vcub[m].posrft);
				Triangle vaabbtri3 = new Triangle(vcub[m].poslfb,vcub[m].poslbb,vcub[m].posrfb);
				Triangle vaabbtri4 = new Triangle(vcub[m].posrbb,vcub[m].poslbb,vcub[m].posrfb);
				Triangle vaabbtri5 = new Triangle(vcub[m].poslft,vcub[m].poslbt,vcub[m].poslfb);
				Triangle vaabbtri6 = new Triangle(vcub[m].poslbb,vcub[m].poslbt,vcub[m].poslfb);
				Triangle vaabbtri7 = new Triangle(vcub[m].posrft,vcub[m].posrbt,vcub[m].posrfb);
				Triangle vaabbtri8 = new Triangle(vcub[m].posrbb,vcub[m].posrbt,vcub[m].posrfb);
				Triangle vaabbtri9 = new Triangle(vcub[m].poslft,vcub[m].posrft,vcub[m].poslfb);
				Triangle vaabbtri10 = new Triangle(vcub[m].posrfb,vcub[m].posrft,vcub[m].poslfb);
				Triangle vaabbtri11 = new Triangle(vcub[m].poslbt,vcub[m].posrbt,vcub[m].poslbb);
				Triangle vaabbtri12 = new Triangle(vcub[m].posrbb,vcub[m].posrbt,vcub[m].poslbb);
				Triangle[] vaabbtrilist = {vaabbtri1,vaabbtri2,vaabbtri3,vaabbtri4,vaabbtri5,vaabbtri6,vaabbtri7,vaabbtri8,vaabbtri9,vaabbtri10,vaabbtri11,vaabbtri12};
				Position[][] vaabbtrirayint = rayTriangleIntersection(vpos, vdir, vaabbtrilist);
				for (int n=0;n<vaabbtrirayint.length;n++) {
					Position vaabbtrirayhitpos1 = null; 
					Position vaabbtrirayhitpos2 = null;
					for (int i=0;i<vaabbtrirayint[0].length;i++) {
						if (vaabbtrirayint[n][i]!=null) {
							if (vaabbtrirayhitpos1==null) {
								vaabbtrirayhitpos1 = vaabbtrirayint[n][i];
							} else if (vaabbtrirayhitpos1!=vaabbtrirayint[n][i]) {
								vaabbtrirayhitpos2 = vaabbtrirayint[n][i];
							}
						}
					}
					if ((vaabbtrirayhitpos1!=null)&&(vaabbtrirayhitpos2==null)) {
						k[n][m] = new Line(vaabbtrirayhitpos1,vaabbtrirayhitpos1);
					} else if ((vaabbtrirayhitpos1!=null)&&(vaabbtrirayhitpos2!=null)) {
						k[n][m] = new Line(vaabbtrirayhitpos1,vaabbtrirayhitpos2);
					}
				}
			}
		}
		return k;
	}
	public static Line[][] planeTriangleIntersection(Plane[] vplane, Triangle[] vtri) {
		Line[][] k = null;
		if ((vplane!=null)&&(vtri!=null)) {
			k = new Line[vplane.length][vtri.length]; 
			for (int m=0;m<vtri.length;m++) {
				Position[] p1 = new Position[1]; p1[0] = vtri[m].pos1;
				Position[] p2 = new Position[1]; p2[0] = vtri[m].pos2;
				Position[] p3 = new Position[1]; p3[0] = vtri[m].pos3;
				Direction[] vtri12 = vectorFromPoints(p1, p2);
				Direction[] vtri13 = vectorFromPoints(p1, p3);
				Direction[] vtri23 = vectorFromPoints(p2, p3);
				double[][] ptd12 = rayPlaneDistance(vtri[m].pos1, vtri12, vplane);
				double[][] ptd13 = rayPlaneDistance(vtri[m].pos1, vtri13, vplane);
				double[][] ptd23 = rayPlaneDistance(vtri[m].pos2, vtri23, vplane);
				for (int n=0;n<vplane.length;n++) {
					Position[] ptlint12 = new Position[1]; ptlint12[0] = new Position(vtri[m].pos1.x+ptd12[0][n]*vtri12[0].dx,vtri[m].pos1.y+ptd12[0][n]*vtri12[0].dy,vtri[m].pos1.z+ptd12[0][n]*vtri12[0].dz);
					Position[] ptlint13 = new Position[1]; ptlint13[0] = new Position(vtri[m].pos1.x+ptd13[0][n]*vtri13[0].dx,vtri[m].pos1.y+ptd13[0][n]*vtri13[0].dy,vtri[m].pos1.z+ptd13[0][n]*vtri13[0].dz);
					Position[] ptlint23 = new Position[1]; ptlint23[0] = new Position(vtri[m].pos2.x+ptd23[0][n]*vtri23[0].dx,vtri[m].pos2.y+ptd23[0][n]*vtri23[0].dy,vtri[m].pos2.z+ptd23[0][n]*vtri23[0].dz);
					boolean ptlhit12 = (ptd12[0][n]>=0)&(ptd12[0][n]<=1);
					boolean ptlhit13 = (ptd13[0][n]>=0)&(ptd13[0][n]<=1);
					boolean ptlhit23 = (ptd23[0][n]>=0)&(ptd23[0][n]<=1);
					if (ptlhit12|ptlhit13|ptlhit23) {
						if (ptlhit12&&ptlhit13) {
							k[n][m] = new Line(ptlint12[0],ptlint13[0]);
							k[n][m].hitind = 0;
						} else if (ptlhit12&&ptlhit23) {
							k[n][m] = new Line(ptlint12[0],ptlint23[0]);
							k[n][m].hitind = 1;
						} else if (ptlhit13&&ptlhit23) {
							k[n][m] = new Line(ptlint13[0],ptlint23[0]);
							k[n][m].hitind = 2;
						}
					}
				}
			}
		}
		return k;
	}
	public static Position[][] planeLineIntersection(Plane[] vplane, Line[] vline) {
		Position[][] k = null;
		if ((vplane!=null)&&(vline!=null)) {
			k = new Position[vplane.length][vline.length]; 
			for (int m=0;m<vline.length;m++) {
				Position[] p1 = new Position[1]; p1[0] = vline[m].pos1;
				Position[] p2 = new Position[1]; p2[0] = vline[m].pos2;
				Direction[] vline12 = vectorFromPoints(p1, p2);
				double[][] ptd1 = rayPlaneDistance(vline[m].pos1, vline12, vplane);
				for (int n=0;n<vplane.length;n++) {
					Position[] ptlint1 = new Position[1]; ptlint1[0] = new Position(vline[m].pos1.x+ptd1[0][n]*vline12[0].dx,vline[m].pos1.y+ptd1[0][n]*vline12[0].dy,vline[m].pos1.z+ptd1[0][n]*vline12[0].dz);
					boolean ptlhit1 = (ptd1[0][n]>=0)&(ptd1[0][n]<=1);
					if (ptlhit1) {
						k[n][m] = ptlint1[0];
					}
				}
			}
		}
		return k;
	}

	public static Rectangle[][] cubemapSphereIntersection(Position vpos, Sphere[] vsphere, int vres) {
		Rectangle[][] k = new Rectangle[vsphere.length][6];
		double[] cmangles = projectedAngles(vres, 90);
		Arrays.sort(cmangles);
		Direction[] cubedir = {new Direction(1.0f,0.0f,0.0f),new Direction(0.0f,1.0f,0.0f),new Direction(-1.0f,0.0f,0.0f),new Direction(0.0f,-1.0f,0.0f),new Direction(0.0f,0.0f,1.0f),new Direction(0.0f,0.0f,-1.0f)};
		int[] cubeind1 = {3,3,3,3,2,2};
		int[] cubeind2 = {2,1,2,1,1,1};
		Direction[] lvec = vectorFromPoints(vpos, vsphere);
		double[] lvecl = vectorLength(lvec);
		for (int j=0;j<vsphere.length;j++) {
			double cmradang = (180.0f/Math.PI)*Math.asin(vsphere[j].r/lvecl[j]);
			if (!Double.isFinite(cmradang)) {cmradang = 180.0f;}
			for (int i=0;i<cubedir.length;i++) {
				Direction[] lvecx = new Direction[2];
				double sign1 = 1.0f;
				double sign2 = 1.0f;
				if (cubeind1[i]==1) {
					lvecx[0] = new Direction(0.0f,lvec[j].dy,lvec[j].dz);
					sign2 = lvec[j].dx<0.0f?-1.0f:1.0f;
				} else if (cubeind1[i]==2) {
					lvecx[0] = new Direction(lvec[j].dx,0.0f,lvec[j].dz);
					sign2 = lvec[j].dy<0.0f?-1.0f:1.0f;
				} else {
					lvecx[0] = new Direction(lvec[j].dx,lvec[j].dy,0.0f);
					sign2 = lvec[j].dz<0.0f?-1.0f:1.0f;
				}
				if (cubeind2[i]==1) {
					lvecx[1] = new Direction(0.0f,lvec[j].dy,lvec[j].dz);
					sign1 = lvec[j].dx<0.0f?-1.0f:1.0f;
				} else if (cubeind2[i]==2) {
					lvecx[1] = new Direction(lvec[j].dx,0.0f,lvec[j].dz);
					sign1 = lvec[j].dy<0.0f?-1.0f:1.0f;
				} else {
					lvecx[1] = new Direction(lvec[j].dx,lvec[j].dy,0.0f);
					sign1 = lvec[j].dz<0.0f?-1.0f:1.0f;
				}
				Direction[] lvecxn = normalizeVector(lvecx);
				double lvecxa1 = sign1*(180.0f/Math.PI)*Math.acos(vectorDot(lvecxn[0],cubedir[i])); 
				double lvecxa2 = sign2*(180.0f/Math.PI)*Math.acos(vectorDot(lvecxn[1],cubedir[i]));
				double lvecxa1min = lvecxa1 - cmradang;
				double lvecxa1max = lvecxa1 + cmradang;
				double lvecxa2min = lvecxa2 - cmradang;
				double lvecxa2max = lvecxa2 + cmradang;
				int startind1 = Arrays.binarySearch(cmangles, lvecxa1min);
				int endind1 = Arrays.binarySearch(cmangles, lvecxa1max);
				int startind2 = Arrays.binarySearch(cmangles, lvecxa2min);
				int endind2 = Arrays.binarySearch(cmangles, lvecxa2max);
				if ((startind1!=-(cmangles.length+1))&&(endind1!=-1)&&(startind2!=-(cmangles.length+1))&&(endind2!=-1)) {
					if (startind1<0) {startind1 = -startind1-1;}
					if (endind1<0) {endind1 = -endind1-1;}
					if (startind1>=cmangles.length) {startind1 = cmangles.length-1;}
					if (endind1>=cmangles.length) {endind1 = cmangles.length-1;}
					if (startind2<0) {startind2 = -startind2-1;}
					if (endind2<0) {endind2 = -endind2-1;}
					if (startind2>=cmangles.length) {startind2 = cmangles.length-1;}
					if (endind2>=cmangles.length) {endind2 = cmangles.length-1;}
					k[j][i] = new Rectangle(startind1,startind2,endind1-startind1+1,endind2-startind2+1);
				}
			}
		}
		return k;
	}
	
	public static boolean[][] sphereSphereIntersection(Sphere[] vsphere1, Sphere[] vsphere2) {
		boolean[][] k = null;
		if ((vsphere1.length>0)&&(vsphere2.length>0)) {
			k = new boolean[vsphere1.length][vsphere2.length];
			for (int j=0;j<vsphere1.length;j++) {
				for (int i=0;i<vsphere2.length;i++) {
					k[j][i] = Math.sqrt(Math.pow(vsphere2[i].x-vsphere1[j].x,2)+Math.pow(vsphere2[i].y-vsphere1[j].y,2)+Math.pow(vsphere2[i].z-vsphere1[j].z,2))<=(vsphere2[i].r+vsphere1[j].r); 
				}
			}
		}
		return k;
	}
	
	public static Integer[][] mutualSphereIntersection(Sphere[] vsphere) {
		Integer[][] k = new Integer[vsphere.length][0];
		double[] xlist = new double[2*vsphere.length];
		double[] ylist = new double[2*vsphere.length];
		double[] zlist = new double[2*vsphere.length];
		for (int i=0; i<vsphere.length; i++) {
			xlist[2*i] = vsphere[i].x-vsphere[i].r;
			xlist[2*i+1] = vsphere[i].x+vsphere[i].r;
			ylist[2*i] = vsphere[i].y-vsphere[i].r;
			ylist[2*i+1] = vsphere[i].y+vsphere[i].r;
			zlist[2*i] = vsphere[i].z-vsphere[i].r;
			zlist[2*i+1] = vsphere[i].z+vsphere[i].r;
		}
		int[] xlistsidx = UtilLib.indexSort(xlist);
		int[] ylistsidx = UtilLib.indexSort(ylist);
		int[] zlistsidx = UtilLib.indexSort(zlist);
		double[] xlistsval = UtilLib.indexValues(xlist,xlistsidx);
		double[] ylistsval = UtilLib.indexValues(ylist,ylistsidx);
		double[] zlistsval = UtilLib.indexValues(zlist,zlistsidx);
		for (int j=0;j<vsphere.length;j++) {
			int xlistidx1 = Arrays.binarySearch(xlistsval,xlist[j*2]);
			int xlistidx2 = Arrays.binarySearch(xlistsval,xlist[j*2+1]);
			int ylistidx1 = Arrays.binarySearch(ylistsval,ylist[j*2]);
			int ylistidx2 = Arrays.binarySearch(ylistsval,ylist[j*2+1]);
			int zlistidx1 = Arrays.binarySearch(zlistsval,zlist[j*2]);
			int zlistidx2 = Arrays.binarySearch(zlistsval,zlist[j*2+1]);
			HashSet<Integer> mutualxindex = new HashSet<Integer>();
			HashSet<Integer> mutualyindex = new HashSet<Integer>();
			HashSet<Integer> mutualzindex = new HashSet<Integer>();
			for (int i=xlistidx1;i<xlistidx2;i++) {
				mutualxindex.add(Math.floorDiv(xlistsidx[i],2));
			}
			for (int i=ylistidx1;i<ylistidx2;i++) {
				mutualyindex.add(Math.floorDiv(ylistsidx[i],2));
			}
			for (int i=zlistidx1;i<zlistidx2;i++) {
				mutualzindex.add(Math.floorDiv(zlistsidx[i],2));
			}
			HashSet<Integer> mutualindex = new HashSet<Integer>(Arrays.asList(mutualxindex.toArray(new Integer[mutualxindex.size()])));
			mutualindex.retainAll(mutualyindex);
			mutualindex.retainAll(mutualzindex);
			mutualindex.remove(j);
			Integer[] intersectionlist = mutualindex.toArray(new Integer[mutualindex.size()]);
			Sphere[] intersectionspheres = new Sphere[intersectionlist.length];
			for (int i=0;i<intersectionlist.length;i++) {
				intersectionspheres[i] = vsphere[intersectionlist[i]];
			}
			Sphere[] vspherei = {vsphere[j]}; 
			boolean[][] sphereintersection = sphereSphereIntersection(vspherei, intersectionspheres);
			ArrayList<Integer> intersectinglist = new ArrayList<Integer>();
			for (int i=0;i<intersectionlist.length;i++) {
				if (sphereintersection[0][i]) {
					intersectinglist.add(intersectionlist[i]);
				}
			}
			k[j] = intersectinglist.toArray(new Integer[intersectinglist.size()]);
		}
		for (int j=0;j<k.length;j++) {
			for (int i=0;i<k[j].length;i++) {
				HashSet<Integer> newlist = new HashSet<Integer>(Arrays.asList(k[k[j][i]]));
				newlist.add(j);
				k[k[j][i]] = newlist.toArray(new Integer[newlist.size()]);
			}
		}
		return k;
	}
	
	public static boolean[] vertexAxisAlignedBoundingBoxIntersection(AxisAlignedBoundingBox vaabb, Position[] vpoint) {
		boolean[] k = null;
		if ((vaabb!=null)&&(vpoint!=null)) {
			k = new boolean[vpoint.length];
			for (int i=0;i<vpoint.length;i++) {
				k[i] = false;
				if ((vpoint[i].x>=vaabb.x1)&&(vpoint[i].x<=vaabb.x2)&&(vpoint[i].y>=vaabb.y1)&&(vpoint[i].y<=vaabb.y2)&&(vpoint[i].z>=vaabb.z1)&&(vpoint[i].z<=vaabb.z2)) {
					k[i] = true;
				}
			}
		}
		return k;
	}
	public static boolean[] lineAxisAlignedBoundingBoxIntersection(AxisAlignedBoundingBox vaabb, Line[] vline) {
		boolean[] k = null;
		if ((vaabb!=null)&&(vline!=null)) {
			k = new boolean[vline.length];
			for (int i=0;i<vline.length;i++) {
				k[i] = false;
				Position[] vpoint = {vline[i].pos1,vline[i].pos2};
				boolean[] vertexint = vertexAxisAlignedBoundingBoxIntersection(vaabb, vpoint);
				if (vertexint[0]||vertexint[1]) {
					k[i] = true;
				}
			}
		}
		return k;
	}
	public static boolean[] triangleAxisAlignedBoundingBoxIntersection(AxisAlignedBoundingBox vaabb, Triangle[] vtri) {
		boolean[] k = null;
		if ((vaabb!=null)&&(vtri!=null)) {
			k = new boolean[vtri.length];
			for (int i=0;i<vtri.length;i++) {
				k[i] = false;
				Position[] vpoint = {vtri[i].pos1,vtri[i].pos2,vtri[i].pos3};
				boolean[] vertexint = vertexAxisAlignedBoundingBoxIntersection(vaabb, vpoint);
				if (vertexint[0]||vertexint[1]||vertexint[2]) {
					k[i] = true;
				}
			}
		}
		return k;
	}
	public static boolean[] tetrahedronAxisAlignedBoundingBoxIntersection(AxisAlignedBoundingBox vaabb, Tetrahedron[] vtet) {
		boolean[] k = null;
		if ((vaabb!=null)&&(vtet!=null)) {
			k = new boolean[vtet.length];
			for (int i=0;i<vtet.length;i++) {
				k[i] = false;
				Position[] vpoint = {vtet[i].pos1,vtet[i].pos2,vtet[i].pos3,vtet[i].pos4};
				boolean[] vertexint = vertexAxisAlignedBoundingBoxIntersection(vaabb, vpoint);
				if (vertexint[0]||vertexint[1]||vertexint[2]||vertexint[3]) {
					k[i] = true;
				}
			}
		}
		return k;
	}
	
	public static Matrix matrixMultiply(Matrix vmat1, Matrix vmat2) {
		Matrix k = null;
		if ((vmat1!=null)&&(vmat2!=null)) {
			k = new Matrix(
					vmat1.a11*vmat2.a11+vmat1.a12*vmat2.a21+vmat1.a13*vmat2.a31,
					vmat1.a11*vmat2.a12+vmat1.a12*vmat2.a22+vmat1.a13*vmat2.a32,
					vmat1.a11*vmat2.a13+vmat1.a12*vmat2.a23+vmat1.a13*vmat2.a33,
					vmat1.a21*vmat2.a11+vmat1.a22*vmat2.a21+vmat1.a23*vmat2.a31,
					vmat1.a21*vmat2.a12+vmat1.a22*vmat2.a22+vmat1.a23*vmat2.a32,
					vmat1.a21*vmat2.a13+vmat1.a22*vmat2.a23+vmat1.a23*vmat2.a33,
					vmat1.a31*vmat2.a11+vmat1.a32*vmat2.a21+vmat1.a33*vmat2.a31,
					vmat1.a31*vmat2.a12+vmat1.a32*vmat2.a22+vmat1.a33*vmat2.a32,
					vmat1.a31*vmat2.a13+vmat1.a32*vmat2.a23+vmat1.a33*vmat2.a33
					);
		}
		return k;
	}
	public static Position[] matrixMultiply(Position[] vpoint, Matrix vmat) {
		Position[] k = null;
		if ((vpoint!=null)&&(vmat!=null)) {
			k = new Position[vpoint.length];
			for (int n=0;n<vpoint.length;n++) {
				k[n] = vpoint[n].copy();
				k[n].x = vpoint[n].x*vmat.a11+vpoint[n].y*vmat.a12+vpoint[n].z*vmat.a13;
				k[n].y = vpoint[n].x*vmat.a21+vpoint[n].y*vmat.a22+vpoint[n].z*vmat.a23;
				k[n].z = vpoint[n].x*vmat.a31+vpoint[n].y*vmat.a32+vpoint[n].z*vmat.a33;
			}
		}
		return k;
	}
	public static Direction[] matrixMultiply(Direction[] vdir, Matrix vmat) {
		Direction[] k = null;
		if ((vdir!=null)&&(vmat!=null)) {
			k = new Direction[vdir.length];
			for (int n=0;n<vdir.length;n++) {
				k[n] = vdir[n].copy();
				k[n].dx = vdir[n].dx*vmat.a11+vdir[n].dy*vmat.a12+vdir[n].dz*vmat.a13;
				k[n].dy = vdir[n].dx*vmat.a21+vdir[n].dy*vmat.a22+vdir[n].dz*vmat.a23;
				k[n].dz = vdir[n].dx*vmat.a31+vdir[n].dy*vmat.a32+vdir[n].dz*vmat.a33;
			}
		}
		return k;
	}
	public static Line[] matrixMultiply(Line[] vline, Matrix vmat) {
		Line[] k = null;
		if ((vline!=null)&&(vmat!=null)) {
			k = new Line[vline.length];
			for (int n=0;n<vline.length;n++) {
				k[n] = vline[n].copy();
				k[n].pos1.x = vline[n].pos1.x*vmat.a11+vline[n].pos1.y*vmat.a12+vline[n].pos1.z*vmat.a13;
				k[n].pos1.y = vline[n].pos1.x*vmat.a21+vline[n].pos1.y*vmat.a22+vline[n].pos1.z*vmat.a23;
				k[n].pos1.z = vline[n].pos1.x*vmat.a31+vline[n].pos1.y*vmat.a32+vline[n].pos1.z*vmat.a33;
				k[n].pos2.x = vline[n].pos2.x*vmat.a11+vline[n].pos2.y*vmat.a12+vline[n].pos2.z*vmat.a13;
				k[n].pos2.y = vline[n].pos2.x*vmat.a21+vline[n].pos2.y*vmat.a22+vline[n].pos2.z*vmat.a23;
				k[n].pos2.z = vline[n].pos2.x*vmat.a31+vline[n].pos2.y*vmat.a32+vline[n].pos2.z*vmat.a33;
			}
		}
		return k;
	}
	public static Triangle[] matrixMultiply(Triangle[] vtri, Matrix vmat) {
		Triangle[] k = null;
		if ((vtri!=null)&&(vmat!=null)) {
			k = new Triangle[vtri.length];
			for (int n=0;n<vtri.length;n++) {
				k[n] = vtri[n].copy();
				k[n].pos1.x = vtri[n].pos1.x*vmat.a11+vtri[n].pos1.y*vmat.a12+vtri[n].pos1.z*vmat.a13;
				k[n].pos1.y = vtri[n].pos1.x*vmat.a21+vtri[n].pos1.y*vmat.a22+vtri[n].pos1.z*vmat.a23;
				k[n].pos1.z = vtri[n].pos1.x*vmat.a31+vtri[n].pos1.y*vmat.a32+vtri[n].pos1.z*vmat.a33;
				k[n].pos2.x = vtri[n].pos2.x*vmat.a11+vtri[n].pos2.y*vmat.a12+vtri[n].pos2.z*vmat.a13;
				k[n].pos2.y = vtri[n].pos2.x*vmat.a21+vtri[n].pos2.y*vmat.a22+vtri[n].pos2.z*vmat.a23;
				k[n].pos2.z = vtri[n].pos2.x*vmat.a31+vtri[n].pos2.y*vmat.a32+vtri[n].pos2.z*vmat.a33;
				k[n].pos3.x = vtri[n].pos3.x*vmat.a11+vtri[n].pos3.y*vmat.a12+vtri[n].pos3.z*vmat.a13;
				k[n].pos3.y = vtri[n].pos3.x*vmat.a21+vtri[n].pos3.y*vmat.a22+vtri[n].pos3.z*vmat.a23;
				k[n].pos3.z = vtri[n].pos3.x*vmat.a31+vtri[n].pos3.y*vmat.a32+vtri[n].pos3.z*vmat.a33;
			}
		}
		return k;
	}
	public static Position[] translate(Position[] vpoint, Position vpos) {
		Position[] k = null;
		if ((vpoint!=null)&&(vpos!=null)) {
			k = new Position[vpoint.length];
			for (int n=0;n<vpoint.length;n++) {
				k[n] = vpoint[n].copy();
				k[n].x = k[n].x+vpos.x;
				k[n].y = k[n].y+vpos.y;
				k[n].z = k[n].z+vpos.z;
			}
		}
		return k;
	}
	public static Direction[] translate(Direction[] vdir, Position vpos) {
		Direction[] k = null;
		if ((vdir!=null)&&(vpos!=null)) {
			k = new Direction[vdir.length];
			for (int n=0;n<vdir.length;n++) {
				k[n] = vdir[n].copy();
				k[n].dx = k[n].dx+vpos.x;
				k[n].dy = k[n].dy+vpos.y;
				k[n].dz = k[n].dz+vpos.z;
			}
		}
		return k;
	}
	public static Line[] translate(Line[] vline, Position vpos) {
		Line[] k = null;
		if ((vline!=null)&&(vpos!=null)) {
			k = new Line[vline.length];
			for (int n=0;n<vline.length;n++) {
				k[n] = vline[n].copy();
				k[n].pos1.x = k[n].pos1.x+vpos.x;
				k[n].pos1.y = k[n].pos1.y+vpos.y;
				k[n].pos1.z = k[n].pos1.z+vpos.z;
				k[n].pos2.x = k[n].pos2.x+vpos.x;
				k[n].pos2.y = k[n].pos2.y+vpos.y;
				k[n].pos2.z = k[n].pos2.z+vpos.z;
			}
		}
		return k;
	}
	public static Triangle[] translate(Triangle[] vtri, Position vpos) {
		Triangle[] k = null;
		if ((vtri!=null)&&(vpos!=null)) {
			k = new Triangle[vtri.length];
			for (int n=0;n<vtri.length;n++) {
				k[n] = vtri[n].copy();
				k[n].pos1.x = k[n].pos1.x+vpos.x;
				k[n].pos1.y = k[n].pos1.y+vpos.y;
				k[n].pos1.z = k[n].pos1.z+vpos.z;
				k[n].pos2.x = k[n].pos2.x+vpos.x;
				k[n].pos2.y = k[n].pos2.y+vpos.y;
				k[n].pos2.z = k[n].pos2.z+vpos.z;
				k[n].pos3.x = k[n].pos3.x+vpos.x;
				k[n].pos3.y = k[n].pos3.y+vpos.y;
				k[n].pos3.z = k[n].pos3.z+vpos.z;
			}
		}
		return k;
	}
	public static Matrix rotationMatrix(double xaxisr, double yaxisr, double zaxisr) {
		double xaxisrrad=xaxisr*(Math.PI/180.0f); double yaxisrrad=yaxisr*(Math.PI/180.0f); double zaxisrrad=zaxisr*(Math.PI/180.0f);
		Matrix xrot = new Matrix(1,0,0,0,Math.cos(xaxisrrad),-Math.sin(xaxisrrad),0,Math.sin(xaxisrrad),Math.cos(xaxisrrad));
		Matrix yrot = new Matrix(Math.cos(yaxisrrad),0,Math.sin(yaxisrrad),0,1,0,-Math.sin(yaxisrrad),0,Math.cos(yaxisrrad));
		Matrix zrot = new Matrix(Math.cos(zaxisrrad),-Math.sin(zaxisrrad),0,Math.sin(zaxisrrad),Math.cos(zaxisrrad),0,0,0,1);
		return matrixMultiply(zrot,matrixMultiply(yrot, xrot));
	}
	
	public static Position[] generateVertexList(Line[] linelist) {
		TreeSet<Position> vertexlist = new TreeSet<Position>();
		for (int i=0;i<linelist.length;i++) {
			vertexlist.add(linelist[i].pos1);
			vertexlist.add(linelist[i].pos2);
		}
		return vertexlist.toArray(new Position[vertexlist.size()]);
	}
	public static Position[] generateVertexList(Triangle[] trianglelist) {
		TreeSet<Position> vertexlist = new TreeSet<Position>();
		for (int i=0;i<trianglelist.length;i++) {
			vertexlist.add(trianglelist[i].pos1);
			vertexlist.add(trianglelist[i].pos2);
			vertexlist.add(trianglelist[i].pos3);
		}
		return vertexlist.toArray(new Position[vertexlist.size()]);
	}
	public static Line[] generateLineList(Triangle[] trianglelist) {
		TreeSet<Line> linelist = new TreeSet<Line>();
		for (int i=0;i<trianglelist.length;i++) {
			linelist.add(new Line(trianglelist[i].pos1,trianglelist[i].pos2));
			linelist.add(new Line(trianglelist[i].pos1,trianglelist[i].pos3));
			linelist.add(new Line(trianglelist[i].pos2,trianglelist[i].pos3));
		}
		return linelist.toArray(new Line[linelist.size()]);
	}
	public static Triangle[] generateTriangleList(Line[] linelist) {
		ArrayList<Triangle> trianglelist = new ArrayList<Triangle>();
		for (int k=0;k<linelist.length;k++) {
			Line kline = linelist[k];
			for (int j=k+1;j<linelist.length;j++) {
				Line jline = linelist[j];
				boolean kjlineconnected11 = kline.pos1.compareTo(jline.pos1)==0;
				boolean kjlineconnected12 = kline.pos1.compareTo(jline.pos2)==0;
				boolean kjlineconnected21 = kline.pos2.compareTo(jline.pos1)==0;
				boolean kjlineconnected22 = kline.pos2.compareTo(jline.pos2)==0;
				boolean kjlineconnected = kjlineconnected11||kjlineconnected12||kjlineconnected21||kjlineconnected22;
				if (kjlineconnected) {
					boolean klinefirst = (kjlineconnected11||kjlineconnected12)?true:false;
					boolean jlinefirst = (kjlineconnected11||kjlineconnected21)?true:false;
					for (int i=j+1;i<linelist.length;i++) {
						Line iline = linelist[i];
						Position klinefreevertex = klinefirst?kline.pos2:kline.pos1;
						Position jlinefreevertex = jlinefirst?jline.pos2:jline.pos1;
						Position connectedvertex = klinefirst?kline.pos1:kline.pos2;
						boolean ilinecoonnected = (klinefreevertex.compareTo(iline.pos1)==0)&&(jlinefreevertex.compareTo(iline.pos2)==0);
						boolean ilinecoonnectedr = (klinefreevertex.compareTo(iline.pos2)==0)&&(jlinefreevertex.compareTo(iline.pos1)==0);
						if (ilinecoonnected||ilinecoonnectedr) {
							trianglelist.add(new Triangle(connectedvertex,klinefreevertex,jlinefreevertex));
						}
					}
				}
			}
		}
		return trianglelist.toArray(new Triangle[trianglelist.size()]);
	}
	public static Line[] generateNonTriangleLineList(Line[] linelist) {
		Triangle[] trianglelist = generateTriangleList(linelist);
		TreeSet<Line> trianglelinelistarray = new TreeSet<Line>(Arrays.asList(generateLineList(trianglelist)));
		TreeSet<Line> linelistarray = new TreeSet<Line>(Arrays.asList(linelist));
		linelistarray.removeAll(trianglelinelistarray);
		return linelistarray.toArray(new Line[linelistarray.size()]);
	}

	public static Tetrahedron[] generateTetrahedronList(Line[] linelist) {
		Triangle[] uniquetrianglelist = generateTriangleList(linelist);
		TreeSet<Tetrahedron> tetrahedronlist = new TreeSet<Tetrahedron>();
		for (int k=0;k<uniquetrianglelist.length;k++) {
			Triangle trianglek = uniquetrianglelist[k]; 
			for (int j=k+1;j<uniquetrianglelist.length;j++) {
				Triangle trianglej = uniquetrianglelist[j];
				Position[] trianglevertexkj = {trianglek.pos1,trianglek.pos2,trianglek.pos3,trianglej.pos1,trianglej.pos2,trianglej.pos3};
				TreeSet<Position> uniquetrianglevertexkj = new TreeSet<Position>(Arrays.asList(trianglevertexkj));
				if (uniquetrianglevertexkj.size()==4) {
					for (int i=j+1;i<uniquetrianglelist.length;i++) {
						Triangle trianglei = uniquetrianglelist[i];
						Position[] trianglevertexkji = {trianglek.pos1,trianglek.pos2,trianglek.pos3,trianglej.pos1,trianglej.pos2,trianglej.pos3,trianglei.pos1,trianglei.pos2,trianglei.pos3};
						TreeSet<Position> uniquetrianglevertexkjiset = new TreeSet<Position>(Arrays.asList(trianglevertexkji));
						Position[] uniquetrianglevertexkji = uniquetrianglevertexkjiset.toArray(new Position[uniquetrianglevertexkjiset.size()]);
						if (uniquetrianglevertexkji.length==4) {
							Triangle triangle1 = new Triangle(uniquetrianglevertexkji[0],uniquetrianglevertexkji[1],uniquetrianglevertexkji[2]);
							Triangle triangle2 = new Triangle(uniquetrianglevertexkji[1],uniquetrianglevertexkji[2],uniquetrianglevertexkji[3]);
							Triangle[] triangles12 = {triangle1,triangle2};
							Plane[] triangle1plane = planeFromPoints(triangles12);
							if ((!((triangle1plane[0].a==triangle1plane[1].a)&&(triangle1plane[0].b==triangle1plane[1].b)&&(triangle1plane[0].c==triangle1plane[1].c)))&&(!((triangle1plane[0].a==-triangle1plane[1].a)&&(triangle1plane[0].b==-triangle1plane[1].b)&&(triangle1plane[0].c==-triangle1plane[1].c)))) {
								tetrahedronlist.add(new Tetrahedron(uniquetrianglevertexkji[0],uniquetrianglevertexkji[1],uniquetrianglevertexkji[2],uniquetrianglevertexkji[3]));
							}
						}
					}
				}
			}
		}
		return tetrahedronlist.toArray(new Tetrahedron[tetrahedronlist.size()]);
	}

	public static Cuboid[] generateCuboidList(Line[] linelist) {
		//TODO generate cuboid volume list
		return null;
	}
	
	public static Triangle[] generateSurfaceList(Line[] linelist) {
		TreeSet<Triangle> surfacelistarray = new TreeSet<Triangle>(); 
		Tetrahedron[] newtetrahedronlist = generateTetrahedronList(linelist);
		for (int j=0;j<newtetrahedronlist.length;j++) {
			Triangle tetrahedronside1 = new Triangle(newtetrahedronlist[j].pos1, newtetrahedronlist[j].pos2, newtetrahedronlist[j].pos3); 
			Triangle tetrahedronside2 = new Triangle(newtetrahedronlist[j].pos1, newtetrahedronlist[j].pos2, newtetrahedronlist[j].pos4); 
			Triangle tetrahedronside3 = new Triangle(newtetrahedronlist[j].pos1, newtetrahedronlist[j].pos3, newtetrahedronlist[j].pos4); 
			Triangle tetrahedronside4 = new Triangle(newtetrahedronlist[j].pos2, newtetrahedronlist[j].pos3, newtetrahedronlist[j].pos4);
			Triangle[] tetrahedronsides = {tetrahedronside1,tetrahedronside2,tetrahedronside3,tetrahedronside4};
			Plane[] tetrahedronsideplanes = planeFromPoints(tetrahedronsides);
			Position[] tetrahedronsidepoints = {newtetrahedronlist[j].pos4,newtetrahedronlist[j].pos3,newtetrahedronlist[j].pos2,newtetrahedronlist[j].pos1};
			for (int i=0;i<tetrahedronsides.length;i++) {
				Plane[] tetrahedronsideplane = {tetrahedronsideplanes[i]};
				Direction tetrahedronsideplanenormal = new Direction(tetrahedronsideplanes[i].a,tetrahedronsideplanes[i].b,tetrahedronsideplanes[i].c);
				Direction[] tetrahedronsideplanenormalarray = {tetrahedronsideplanenormal};
				double[][] tetrahedronsideplanepointdist = rayPlaneDistance(tetrahedronsidepoints[i], tetrahedronsideplanenormalarray, tetrahedronsideplane);
				if (tetrahedronsideplanepointdist[0][0]<0) {
					tetrahedronsideplanenormal = new Direction(-tetrahedronsideplanes[i].a,-tetrahedronsideplanes[i].b,-tetrahedronsideplanes[i].c);
				}
				tetrahedronsides[i].norm = tetrahedronsideplanenormal;
				if (surfacelistarray.contains(tetrahedronsides[i])) {
					surfacelistarray.remove(tetrahedronsides[i]);
				} else {
					surfacelistarray.add(tetrahedronsides[i]);
				}
			}
		}
		return surfacelistarray.toArray(new Triangle[surfacelistarray.size()]);
	}
	
	public static Entity[] generateEntityList(Line[] linelist) {
		ArrayList<Entity> newentitylistarray = new ArrayList<Entity>();
		for (int j=0;j<linelist.length;j++) {
			Entity foundent1 = null;
			Entity foundent2 = null;
			for (Iterator<Entity> i=newentitylistarray.iterator();(i.hasNext())&&((foundent1==null)||(foundent2==null));) {
				Entity searchent = i.next();
				if (searchent.linelist!=null) {
					Position[] searchvert = generateVertexList(searchent.linelist);
					TreeSet<Position> searchvertarray = new TreeSet<Position>(Arrays.asList(searchvert));
					if (searchvertarray.contains(linelist[j].pos1)) {
						foundent1 = searchent; 
					} else if (searchvertarray.contains(linelist[j].pos2)) {
						foundent2 = searchent; 
					}
				}
			}
			if ((foundent1!=null)&&(foundent2!=null)&&(!foundent1.equals(foundent2))) {
				TreeSet<Line> newlinelistarray = new TreeSet<Line>(Arrays.asList(foundent1.linelist));
				newlinelistarray.addAll(Arrays.asList(foundent2.linelist));
				newlinelistarray.add(linelist[j]);
				foundent1.linelist = newlinelistarray.toArray(new Line[newlinelistarray.size()]);
				newentitylistarray.remove(foundent2);
			} else if ((foundent1!=null)||(foundent2!=null)) {
				Entity foundent = foundent1; 
				if (foundent2!=null) {foundent = foundent2;}
				TreeSet<Line> newlinelistarray = new TreeSet<Line>(Arrays.asList(foundent.linelist));
				newlinelistarray.add(linelist[j]);
				foundent.linelist = newlinelistarray.toArray(new Line[newlinelistarray.size()]);
			} else {
				Entity newentity = new Entity();
				Line[] newlinelist = {linelist[j]}; 
				newentity.linelist = newlinelist; 
				newentitylistarray.add(newentity);
			}
		}
		for (Iterator<Entity> i=newentitylistarray.iterator();i.hasNext();) {
			Entity processent = i.next();
			processent.trianglelist = generateTriangleList(processent.linelist);
			processent.surfacelist = generateSurfaceList(processent.linelist);
			processent.tetrahedronlist = generateTetrahedronList(processent.linelist);
			processent.vertexlist = generateVertexList(processent.linelist);
			processent.linelist = generateNonTriangleLineList(processent.linelist);
			processent.aabbboundaryvolume = axisAlignedBoundingBox(processent.vertexlist);
			processent.sphereboundaryvolume = pointCloudCircumSphere(processent.vertexlist);
		}
		Entity[] entitylist = newentitylistarray.toArray(new Entity[newentitylistarray.size()]); 
		octreeEntityList(entitylist);
		return entitylist;
	}
	
	public static void octreeEntityList(Entity[] entitylist) {
		if (entitylist!=null) {
			for (int j=0;j<entitylist.length;j++) {
				Entity[] newchildren = {new Entity(),new Entity(),new Entity(),new Entity(),new Entity(),new Entity(),new Entity(),new Entity()}; 
				double xlimit = (entitylist[j].aabbboundaryvolume.x1+entitylist[j].aabbboundaryvolume.x2)/2.0f;
				double ylimit = (entitylist[j].aabbboundaryvolume.y1+entitylist[j].aabbboundaryvolume.y2)/2.0f;
				double zlimit = (entitylist[j].aabbboundaryvolume.z1+entitylist[j].aabbboundaryvolume.z2)/2.0f;
				newchildren[0].aabbboundaryvolume = new AxisAlignedBoundingBox(entitylist[j].aabbboundaryvolume.x1,entitylist[j].aabbboundaryvolume.y1,entitylist[j].aabbboundaryvolume.z1,xlimit,ylimit,zlimit);
				newchildren[1].aabbboundaryvolume = new AxisAlignedBoundingBox(xlimit,entitylist[j].aabbboundaryvolume.y1,entitylist[j].aabbboundaryvolume.z1,entitylist[j].aabbboundaryvolume.x2,ylimit,zlimit);
				newchildren[2].aabbboundaryvolume = new AxisAlignedBoundingBox(entitylist[j].aabbboundaryvolume.x1,ylimit,entitylist[j].aabbboundaryvolume.z1,xlimit,entitylist[j].aabbboundaryvolume.y2,zlimit);
				newchildren[3].aabbboundaryvolume = new AxisAlignedBoundingBox(xlimit,ylimit,entitylist[j].aabbboundaryvolume.z1,entitylist[j].aabbboundaryvolume.x2,entitylist[j].aabbboundaryvolume.y2,zlimit);
				newchildren[4].aabbboundaryvolume = new AxisAlignedBoundingBox(entitylist[j].aabbboundaryvolume.x1,entitylist[j].aabbboundaryvolume.y1,zlimit,xlimit,ylimit,entitylist[j].aabbboundaryvolume.z2);
				newchildren[5].aabbboundaryvolume = new AxisAlignedBoundingBox(xlimit,entitylist[j].aabbboundaryvolume.y1,zlimit,entitylist[j].aabbboundaryvolume.x2,ylimit,entitylist[j].aabbboundaryvolume.z2);
				newchildren[6].aabbboundaryvolume = new AxisAlignedBoundingBox(entitylist[j].aabbboundaryvolume.x1,ylimit,zlimit,xlimit,entitylist[j].aabbboundaryvolume.y2,entitylist[j].aabbboundaryvolume.z2);
				newchildren[7].aabbboundaryvolume = new AxisAlignedBoundingBox(xlimit,ylimit,zlimit,entitylist[j].aabbboundaryvolume.x2,entitylist[j].aabbboundaryvolume.y2,entitylist[j].aabbboundaryvolume.z2);
				ArrayList<Entity> newchildlistarray = new ArrayList<Entity>();
				for (int n=0;n<newchildren.length;n++) {
					if (entitylist[j].trianglelist!=null) {
						boolean[] taabbint = triangleAxisAlignedBoundingBoxIntersection(newchildren[n].aabbboundaryvolume, entitylist[j].trianglelist);
						ArrayList<Triangle> triintarray = new ArrayList<Triangle>();
						for (int i=0;i<taabbint.length;i++) {if (taabbint[i]) {triintarray.add(entitylist[j].trianglelist[i]);}}
						newchildren[n].trianglelist = triintarray.toArray(new Triangle[triintarray.size()]);
					}
					if (entitylist[j].surfacelist!=null) {
						boolean[] saabbint = triangleAxisAlignedBoundingBoxIntersection(newchildren[n].aabbboundaryvolume, entitylist[j].surfacelist);
						ArrayList<Triangle> surfintarray = new ArrayList<Triangle>();
						for (int i=0;i<saabbint.length;i++) {if (saabbint[i]) {surfintarray.add(entitylist[j].surfacelist[i]);}}
						newchildren[n].surfacelist = surfintarray.toArray(new Triangle[surfintarray.size()]);
					}
					if (entitylist[j].tetrahedronlist!=null) {
						boolean[] teaabbint = tetrahedronAxisAlignedBoundingBoxIntersection(newchildren[n].aabbboundaryvolume, entitylist[j].tetrahedronlist);
						ArrayList<Tetrahedron> tetrintarray = new ArrayList<Tetrahedron>();
						for (int i=0;i<teaabbint.length;i++) {if (teaabbint[i]) {tetrintarray.add(entitylist[j].tetrahedronlist[i]);}}
						newchildren[n].tetrahedronlist= tetrintarray.toArray(new Tetrahedron[tetrintarray.size()]);
					}
					if (entitylist[j].linelist!=null) {
						boolean[] laabbint = lineAxisAlignedBoundingBoxIntersection(newchildren[n].aabbboundaryvolume, entitylist[j].linelist);
						ArrayList<Line> lineintarray = new ArrayList<Line>();
						for (int i=0;i<laabbint.length;i++) {if (laabbint[i]) {lineintarray.add(entitylist[j].linelist[i]);}}
						newchildren[n].linelist = lineintarray.toArray(new Line[lineintarray.size()]);
					}
					if (entitylist[j].vertexlist!=null) {
						boolean[] vaabbint = vertexAxisAlignedBoundingBoxIntersection(newchildren[n].aabbboundaryvolume, entitylist[j].vertexlist);
						ArrayList<Position> vertintarray = new ArrayList<Position>();
						for (int i=0;i<vaabbint.length;i++) {if (vaabbint[i]) {vertintarray.add(entitylist[j].vertexlist[i]);}}
						newchildren[n].vertexlist = vertintarray.toArray(new Position[vertintarray.size()]);
					}
					if ((newchildren[n].vertexlist!=null)&&(newchildren[n].vertexlist.length>0)) {
						newchildren[n].aabbboundaryvolume = axisAlignedBoundingBox(newchildren[n].vertexlist);
						newchildren[n].sphereboundaryvolume = pointCloudCircumSphere(newchildren[n].vertexlist);
						newchildlistarray.add(newchildren[n]);
					}
				}
				entitylist[j].childlist = newchildlistarray.toArray(new Entity[newchildlistarray.size()]);
			}
		}
	}
	
	public static Triangle[] subDivideTriangle(Triangle[] vtri) {
		Triangle[] k = null;
		if (vtri!=null) {
			k = new Triangle[2*vtri.length];
			for (int i=0;i<vtri.length;i++) {
				Position[] trianglepos1 = {vtri[i].pos1,vtri[i].pos1,vtri[i].pos2};
				Position[] trianglepos2 = {vtri[i].pos2,vtri[i].pos3,vtri[i].pos3};
				Direction[] trianglelines = vectorFromPoints(trianglepos1, trianglepos2);
				double[] trianglelineslen = vectorLength(trianglelines);
				int[] lineindex = UtilLib.indexSort(trianglelineslen);
				Position newtrianglepoint = new Position(trianglepos1[lineindex[2]].x+0.5f*trianglelines[lineindex[2]].dx, trianglepos1[lineindex[2]].y+0.5f*trianglelines[lineindex[2]].dy, trianglepos1[lineindex[2]].z+0.5f*trianglelines[lineindex[2]].dz);
				if ((trianglepos1[lineindex[2]].tex!=null)&&(trianglepos2[lineindex[2]].tex!=null)) {
					newtrianglepoint.tex = new Coordinate(trianglepos1[lineindex[2]].tex.u+0.5f*(trianglepos2[lineindex[2]].tex.u-trianglepos1[lineindex[2]].tex.u),trianglepos1[lineindex[2]].tex.v+0.5f*(trianglepos2[lineindex[2]].tex.v-trianglepos1[lineindex[2]].tex.v));
				}
				Triangle newtriangle1 = null;
				Triangle newtriangle2 = null;
				if (lineindex[2]==0) {
					newtriangle1 = new Triangle(newtrianglepoint,vtri[i].pos1,vtri[i].pos3);
					newtriangle2 = new Triangle(newtrianglepoint,vtri[i].pos2,vtri[i].pos3);
				} else if (lineindex[2]==1) {
					newtriangle1 = new Triangle(newtrianglepoint,vtri[i].pos1,vtri[i].pos2);
					newtriangle2 = new Triangle(newtrianglepoint,vtri[i].pos3,vtri[i].pos2);
				} else {
					newtriangle1 = new Triangle(newtrianglepoint,vtri[i].pos2,vtri[i].pos1);
					newtriangle2 = new Triangle(newtrianglepoint,vtri[i].pos3,vtri[i].pos1);
				}
				newtriangle1.ind = vtri[i].ind;
				newtriangle2.ind = vtri[i].ind;
				newtriangle1.mat = vtri[i].mat;
				newtriangle2.mat = vtri[i].mat;
				newtriangle1.norm = vtri[i].norm;
				newtriangle2.norm = vtri[i].norm;
				k[i*2] = newtriangle1;
				k[i*2+1] = newtriangle2;
			}
		}
		return k;
	}
	
	public static AffineTransform[] textureTransform(VolatileImage[] vtexture, Triangle[] vtri, Polygon[] vpoly) {
		AffineTransform[] k = null;
		//TODO triangle texture coordinates transform into display polygon triangle
		if ((vtexture!=null)&&(vtri!=null)&&(vpoly!=null)&&(vtri.length==vpoly.length)&&(vtri.length==vtexture.length)) {
			k = new AffineTransform[vtri.length];
			for (int i=0;i<vtri.length;i++) {
				if (vpoly[i].npoints==3) {
					Direction[] polyvec12 = {new Direction(vpoly[i].xpoints[1]-vpoly[i].xpoints[0],vpoly[i].ypoints[1]-vpoly[i].ypoints[0],0.0f)};
					Direction[] polyvec13 = {new Direction(vpoly[i].xpoints[2]-vpoly[i].xpoints[0],vpoly[i].ypoints[2]-vpoly[i].ypoints[0],0.0f)};
					Direction[] trivec12 = {new Direction(vtri[i].pos2.tex.u-vtri[i].pos1.tex.u,vtri[i].pos2.tex.v-vtri[i].pos1.tex.v,0.0f)};
					Direction[] trivec13 = {new Direction(vtri[i].pos3.tex.u-vtri[i].pos1.tex.u,vtri[i].pos3.tex.v-vtri[i].pos1.tex.v,0.0f)};
					Direction[] deltavec1 = {new Direction(vpoly[i].xpoints[0]-vtri[i].pos1.tex.u,vpoly[i].ypoints[0]-vtri[i].pos1.tex.v,0.0f)};
					double[] polyvec12len = vectorLength(polyvec12);
					double[] polyvec13len = vectorLength(polyvec13);
					double[] trivec12len = vectorLength(trivec12);
					double[] trivec13len = vectorLength(trivec13);
					double[] polyvecangles = vectorAngle(polyvec12, polyvec13);
					double[] trivecangles = vectorAngle(trivec12, trivec13);
					Triangle[] vtriangle = {vtri[i]}; 
					AxisAlignedBoundingBox tribounds = axisAlignedBoundingBox(generateVertexList(vtriangle));
					Rectangle polybounds = vpoly[i].getBounds();
					AffineTransform newtransform = new AffineTransform();
					double scalefactorx = polybounds.getWidth()/((double)vtexture[i].getWidth());
					double scalefactory = polybounds.getHeight()/((double)vtexture[i].getHeight());
					newtransform.translate(polybounds.x, polybounds.y);
					newtransform.scale(scalefactorx, scalefactory);
					k[i] = newtransform;
				}
			}
		}
		return k;
	}
	
	public static double[] projectedStep(int vres, double vfov) {
		double[] k = new double[vres];
		double halfvfov = vfov/2.0f;
		double stepmax = Math.abs(Math.tan(halfvfov*(Math.PI/180.0f)));
		double stepmin = -stepmax;
		double step = 2.0f/((double)(vres-1))*stepmax;
		for (int i=0;i<vres;i++){k[i]=stepmin+step*i;}
		return k;
	}
	public static double[] projectedAngles(int vres, double vfov) {
		double[] k = new double[vres];
		double[] hd = projectedStep(vres, vfov);
		for (int i=0;i<vres;i++){k[i]=(180.0f/Math.PI)*Math.atan(hd[i]);}
		return k;
	}
	public static Direction[] projectedDirections(Matrix vmat) {
		Direction[] rightdirupvectors = new Direction[3];
		Direction dirvector = new Direction(0,0,-1);
		Direction rightvector = new Direction(0,1,0);
		Direction upvector = new Direction(1,0,0);
		rightdirupvectors[0] = dirvector;
		rightdirupvectors[1] = rightvector;
		rightdirupvectors[2] = upvector;
	    return matrixMultiply(rightdirupvectors, vmat);
	}
	public static Direction[] projectedVectors(int vres, double vfov, Matrix vmat) {
	    double[] steps = projectedStep(vres,vfov);
		Direction[] fwdvectors = new Direction[vres];
	    for (int i=0;i<vres;i++) {
	    	fwdvectors[i] = new Direction(steps[i], 0, -1);
	    }
	    fwdvectors = normalizeVector(fwdvectors);
	    return matrixMultiply(fwdvectors, vmat);
	}
	public static Plane[] projectedPlanes(Position vpos, int vres, double vfov, Matrix vmat) {
		Direction[] fwdvectors = projectedVectors(vres, vfov, vmat);
		Direction[] dirrightupvectors = projectedDirections(vmat);
		Direction rightvector = dirrightupvectors[1];
		Direction[] planenormalvectors = vectorCross(fwdvectors,rightvector);
		planenormalvectors = normalizeVector(planenormalvectors);
	    return planeFromNormalAtPoint(vpos, planenormalvectors);
	}
	public static Direction[][] projectedRays(Position vpos, int vhres, int vvres, double vhfov, double vvfov, Matrix vmat) {
		Direction[][] k = new Direction[vvres][vhres];
		double[] hstep = projectedStep(vhres, vhfov);
		double[] vstep = projectedStep(vvres, vvfov);
		for (int j=0;j<vvres;j++) {
			k[j] = new Direction[vvres];
			for (int i=0;i<vvres;i++) {
				k[j][i] = new Direction(1,hstep[i],vstep[j]);
			}
			k[j] = normalizeVector(k[j]);
			k[j] = matrixMultiply(k[j], vmat);
			k[j] = translate(k[j], vpos);
		}
		return k;
	}
	
	public static AxisAlignedBoundingBox axisAlignedBoundingBox(Position[] vertexlist) {
		double xmin=Double.POSITIVE_INFINITY, ymin=Double.POSITIVE_INFINITY, zmin=Double.POSITIVE_INFINITY;
		double xmax=Double.NEGATIVE_INFINITY, ymax=Double.NEGATIVE_INFINITY, zmax=Double.NEGATIVE_INFINITY;
		for (int i=0;i<vertexlist.length;i++) {
			if (vertexlist[i].x<xmin) {xmin=vertexlist[i].x;}
			if (vertexlist[i].x>xmax) {xmax=vertexlist[i].x;}
			if (vertexlist[i].y<ymin) {ymin=vertexlist[i].y;}
			if (vertexlist[i].y>ymax) {ymax=vertexlist[i].y;}
			if (vertexlist[i].z<zmin) {zmin=vertexlist[i].z;}
			if (vertexlist[i].z>zmax) {zmax=vertexlist[i].z;}
		}
		return new AxisAlignedBoundingBox(xmin,ymin,zmin,xmax,ymax,zmax);
	}
	public static Sphere pointCloudCircumSphere(Position[] vertexlist) {
		AxisAlignedBoundingBox pointcloudlimits = axisAlignedBoundingBox(vertexlist);
		Position pointcloudcenter = new Position((pointcloudlimits.x1+pointcloudlimits.x2)/2.0f,(pointcloudlimits.y1+pointcloudlimits.y2)/2.0f,(pointcloudlimits.z1+pointcloudlimits.z2)/2.0f);
		Direction[] pointvectors = vectorFromPoints(pointcloudcenter, vertexlist);
		double[] pointdistances = vectorLength(pointvectors);
		double maxradius = -1;
		for (int i=0;i<pointdistances.length;i++) {
			if (pointdistances[i]>maxradius) {
				maxradius = pointdistances[i]; 
			}
		}
		return new Sphere(pointcloudcenter.x,pointcloudcenter.y,pointcloudcenter.z,maxradius);
	}
	public static Sphere[] triangleCircumSphere(Triangle[] trianglelist) {
		Sphere[] k = new Sphere[trianglelist.length];
		for (int i=0;i<trianglelist.length;i++) {
			Direction[] v12 = {new Direction(trianglelist[i].pos2.x-trianglelist[i].pos1.x,trianglelist[i].pos2.y-trianglelist[i].pos1.y,trianglelist[i].pos2.z-trianglelist[i].pos1.z)};
			Direction[] v13 = {new Direction(trianglelist[i].pos3.x-trianglelist[i].pos1.x,trianglelist[i].pos3.y-trianglelist[i].pos1.y,trianglelist[i].pos3.z-trianglelist[i].pos1.z)};
			double[] v12L = vectorLength(v12);
			double[] v13L = vectorLength(v13);
			Direction[] cv12v13 = vectorCross(v12,v13);
			double[] cv12v13L = vectorLength(cv12v13);
			Direction[] toparg = {new Direction(v12L[0]*v13[0].dx-v13L[0]*v12[0].dx,v12L[0]*v13[0].dy-v13L[0]*v12[0].dy,v12L[0]*v13[0].dz-v13L[0]*v12[0].dz)};
			Direction[] top = vectorCross(toparg,cv12v13);
			double bottom = 2.0f*Math.pow(cv12v13L[0],2);
			Position p1 = trianglelist[i].pos1;
			if (bottom!=0) {
				Position spherecenter = new Position(top[0].dx/bottom+p1.x,top[0].dy/bottom+p1.y,top[0].dz/bottom+p1.z);
				Direction[] sphereradiusvector = {new Direction(spherecenter.x-p1.x,spherecenter.y-p1.y,spherecenter.z-p1.z)};
				double[] sphereradius = vectorLength(sphereradiusvector);
				k[i] = new Sphere(spherecenter.x,spherecenter.y,spherecenter.z,sphereradius[0]);
			}
		}
		return k;
	}
}
