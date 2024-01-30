package fi.jkauppa.javarenderengine;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import fi.jkauppa.javarenderengine.ModelLib.AxisAlignedBoundingBox;
import fi.jkauppa.javarenderengine.ModelLib.Coordinate;
import fi.jkauppa.javarenderengine.ModelLib.Cuboid;
import fi.jkauppa.javarenderengine.ModelLib.Direction;
import fi.jkauppa.javarenderengine.ModelLib.Entity;
import fi.jkauppa.javarenderengine.ModelLib.Line;
import fi.jkauppa.javarenderengine.ModelLib.Matrix;
import fi.jkauppa.javarenderengine.ModelLib.Plane;
import fi.jkauppa.javarenderengine.ModelLib.Position;
import fi.jkauppa.javarenderengine.ModelLib.Quad;
import fi.jkauppa.javarenderengine.ModelLib.Sphere;
import fi.jkauppa.javarenderengine.ModelLib.Tetrahedron;
import fi.jkauppa.javarenderengine.ModelLib.Triangle;

public class MathLib {
	public static double sind(double value) {return Math.sin((Math.PI/180.0f)*value);}
	public static double asind(double value) {return (180.0f/Math.PI)*Math.asin(value);}
	public static double cosd(double value) {return Math.cos((Math.PI/180.0f)*value);}
	public static double acosd(double value) {return (180.0f/Math.PI)*Math.acos(value);}
	public static double tand(double value) {return Math.tan((Math.PI/180.0f)*value);}
	public static double atand(double value) {return (180.0f/Math.PI)*Math.atan(value);}
	
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
	public static double[] vectorLength(Position[] vpos1,Position[] vpos2) {
		Direction[] lengthvector = MathLib.vectorFromPoints(vpos1, vpos2);
		return vectorLength(lengthvector);
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
				k[n] = acosd(vdir12dot[n]/(vdir1length[0]*vdir2length[n]));
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
				k[n] = acosd(vdir12dot[n]/(vdir1length[n]*vdir2length[n]));
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
	public static double[][] planePointDistance(Position[] vpoint, Plane[] vplane) {
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
			Direction[] raypospointdir = vectorFromPoints(vpos, vpoint);
			double[] vdirlength = vectorLength(vdir);
			for (int n=0;n<vdir.length;n++) {
				Direction[] vdircross = vectorCross(vdir[n], raypospointdir);
				double[] vdircrosslen = vectorLength(vdircross); 
				for (int m=0;m<vpoint.length;m++) {
					k[n][m] = vdircrosslen[m]/vdirlength[n];
				}
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
					if ((Double.isFinite(tpdist[n][m]))&&(tpdist[n][m]>=0)) {
						Position[] p4 = {new Position(vpos.x+vdir[n].dx*tpdist[n][m],vpos.y+vdir[n].dy*tpdist[n][m],vpos.z+vdir[n].dz*tpdist[n][m])};
						Position[] p1 = {vtri[m].pos1};
						Position[] p2 = {vtri[m].pos2};
						Position[] p3 = {vtri[m].pos3};
						Direction[] v12 = vectorFromPoints(p1, p2); Direction[] v21 = vectorFromPoints(p2, p1);
						Direction[] v13 = vectorFromPoints(p1, p3); Direction[] v31 = vectorFromPoints(p3, p1);
						Direction[] v23 = vectorFromPoints(p2, p3); Direction[] v32 = vectorFromPoints(p3, p2);
						double[] vl12 = vectorLength(v12);
						double[] vl13 = vectorLength(v13);
						double[] a1 = vectorAngle(v12,v13);
						double[] a2 = vectorAngle(v21,v23);
						double[] a3 = vectorAngle(v31,v32);
						double[] ai1 = vectorAngle(v21,v13);
						Direction[] t1 = vectorFromPoints(p1, p4);
						Direction[] t2 = vectorFromPoints(p2, p4);
						Direction[] t3 = vectorFromPoints(p3, p4);
						double[] tl1 = vectorLength(t1);
						double[] h12 = vectorAngle(v12,t1); double[] h13 = vectorAngle(v13,t1);
						double[] h21 = vectorAngle(v21,t2); double[] h23 = vectorAngle(v23,t2);
						double[] h31 = vectorAngle(v31,t3); double[] h32 = vectorAngle(v32,t3);
						boolean isatpoint1 = (t1[0].dx==0)&&(t1[0].dy==0)&&(t1[0].dz==0);
						boolean isatpoint2 = (t2[0].dx==0)&&(t2[0].dy==0)&&(t2[0].dz==0);
						boolean isatpoint3 = (t3[0].dx==0)&&(t3[0].dy==0)&&(t3[0].dz==0);
						boolean withinangles = (h12[0]<=a1[0])&&(h13[0]<=a1[0])&&(h21[0]<=a2[0])&&(h23[0]<=a2[0])&&(h31[0]<=a3[0])&&(h32[0]<=a3[0]);
						if(isatpoint1||isatpoint2||isatpoint3||withinangles) {
							if ((p1[0].tex!=null)&&(p2[0].tex!=null)&&(p3[0].tex!=null)) {
								if (isatpoint1) {
									p4[0].tex = p1[0].tex.copy();
								} else if (isatpoint2) {
									p4[0].tex = p2[0].tex.copy();
								} else if (isatpoint3) {
									p4[0].tex = p3[0].tex.copy();
								} else {
									double n12len = tl1[0]*(sind(h13[0])/sind(ai1[0]));
									double n13len = tl1[0]*(sind(h12[0])/sind(ai1[0]));
									double n12mult = n12len/vl12[0];
									double n13mult = n13len/vl13[0];
									double u12delta = p2[0].tex.u-p1[0].tex.u;
									double v12delta = p2[0].tex.v-p1[0].tex.v;
									double u13delta = p3[0].tex.u-p1[0].tex.u;
									double v13delta = p3[0].tex.v-p1[0].tex.v;
									p4[0].tex = new Coordinate(p1[0].tex.u+u12delta*n12mult+u13delta*n13mult,p1[0].tex.v+v12delta*n12mult+v13delta*n13mult);
								}
							}
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
			for (int m=0;m<vquad.length;m++) {
				Triangle vaabbtri1 = new Triangle(vquad[m].pos1,vquad[m].pos2,vquad[m].pos3);
				Triangle vaabbtri2 = new Triangle(vquad[m].pos2,vquad[m].pos3,vquad[m].pos4);
				Triangle[] vaabbtrilist = {vaabbtri1,vaabbtri2};
				Position[][] vaabbtrirayint = rayTriangleIntersection(vpos, vdir, vaabbtrilist);
				for (int n=0;n<vaabbtrirayint.length;n++) {
					Position vaabbtrirayhitpos1 = null; 
					for (int i=0;i<vaabbtrirayint[0].length;i++) {
						if (vaabbtrirayint[n][i]!=null) {
							if (vaabbtrirayhitpos1==null) {
								vaabbtrirayhitpos1 = vaabbtrirayint[n][i];
							}
						}
					}
					if (vaabbtrirayhitpos1!=null) {
						k[n][m] = vaabbtrirayhitpos1;
					}
				}
			}
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
					boolean ptlhit12 = (ptd12[0][n]>=0)&(ptd12[0][n]<=1);
					boolean ptlhit13 = (ptd13[0][n]>=0)&(ptd13[0][n]<=1);
					boolean ptlhit23 = (ptd23[0][n]>=0)&(ptd23[0][n]<=1);
					if (ptlhit12|ptlhit13|ptlhit23) {
						Position ptlint12 = new Position(vtri[m].pos1.x+ptd12[0][n]*vtri12[0].dx,vtri[m].pos1.y+ptd12[0][n]*vtri12[0].dy,vtri[m].pos1.z+ptd12[0][n]*vtri12[0].dz);
						Position ptlint13 = new Position(vtri[m].pos1.x+ptd13[0][n]*vtri13[0].dx,vtri[m].pos1.y+ptd13[0][n]*vtri13[0].dy,vtri[m].pos1.z+ptd13[0][n]*vtri13[0].dz);
						Position ptlint23 = new Position(vtri[m].pos2.x+ptd23[0][n]*vtri23[0].dx,vtri[m].pos2.y+ptd23[0][n]*vtri23[0].dy,vtri[m].pos2.z+ptd23[0][n]*vtri23[0].dz);
						if ((vtri[m].pos1.tex!=null)&&(vtri[m].pos2.tex!=null)&&(vtri[m].pos3.tex!=null)) {
							double udelta12 = vtri[m].pos2.tex.u-vtri[m].pos1.tex.u;
							double vdelta12 = vtri[m].pos2.tex.v-vtri[m].pos1.tex.v;
							double udelta13 = vtri[m].pos3.tex.u-vtri[m].pos1.tex.u;
							double vdelta13 = vtri[m].pos3.tex.v-vtri[m].pos1.tex.v;
							double udelta23 = vtri[m].pos3.tex.u-vtri[m].pos2.tex.u;
							double vdelta23 = vtri[m].pos3.tex.v-vtri[m].pos2.tex.v;
							ptlint12.tex = new Coordinate(vtri[m].pos1.tex.u+ptd12[0][n]*udelta12,vtri[m].pos1.tex.v+ptd12[0][n]*vdelta12);
							ptlint13.tex = new Coordinate(vtri[m].pos1.tex.u+ptd13[0][n]*udelta13,vtri[m].pos1.tex.v+ptd13[0][n]*vdelta13);
							ptlint23.tex = new Coordinate(vtri[m].pos2.tex.u+ptd23[0][n]*udelta23,vtri[m].pos2.tex.v+ptd23[0][n]*vdelta23);
						}
						if (ptlhit12&&ptlhit13) {
							k[n][m] = new Line(ptlint12,ptlint13);
							k[n][m].ind = 0;
						} else if (ptlhit12&&ptlhit23) {
							k[n][m] = new Line(ptlint12,ptlint23);
							k[n][m].ind = 1;
						} else if (ptlhit13&&ptlhit23) {
							k[n][m] = new Line(ptlint13,ptlint23);
							k[n][m].ind = 2;
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
					if ((ptd1[0][n]>=0)&(ptd1[0][n]<=1)) {
						Position ptlint = new Position(vline[m].pos1.x+ptd1[0][n]*vline12[0].dx,vline[m].pos1.y+ptd1[0][n]*vline12[0].dy,vline[m].pos1.z+ptd1[0][n]*vline12[0].dz);
						if ((vline[m].pos1.tex!=null)&&(vline[m].pos2.tex!=null)) {
							double udelta = vline[m].pos2.tex.u-vline[m].pos1.tex.u;
							double vdelta = vline[m].pos2.tex.v-vline[m].pos1.tex.v;
							ptlint.tex = new Coordinate(vline[m].pos1.tex.u+ptd1[0][n]*udelta,vline[m].pos1.tex.v+ptd1[0][n]*vdelta);
						}
						k[n][m] = ptlint;
					}
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
		//TODO include intersection lines that completely cross over the axis aligned bounding box, not only those lines that have vertices inside the bounding box 
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
	public static Sphere[] matrixMultiply(Sphere[] vsph, Matrix vmat) {
		Sphere[] k = null;
		if ((vsph!=null)&&(vmat!=null)) {
			k = new Sphere[vsph.length];
			for (int n=0;n<vsph.length;n++) {
				k[n] = vsph[n].copy();
				k[n].x = vsph[n].x*vmat.a11+vsph[n].y*vmat.a12+vsph[n].z*vmat.a13;
				k[n].y = vsph[n].x*vmat.a21+vsph[n].y*vmat.a22+vsph[n].z*vmat.a23;
				k[n].z = vsph[n].x*vmat.a31+vsph[n].y*vmat.a32+vsph[n].z*vmat.a33;
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
	public static Quad[] matrixMultiply(Quad[] vquad, Matrix vmat) {
		Quad[] k = null;
		if ((vquad!=null)&&(vmat!=null)) {
			k = new Quad[vquad.length];
			for (int n=0;n<vquad.length;n++) {
				k[n] = vquad[n].copy();
				k[n].pos1.x = vquad[n].pos1.x*vmat.a11+vquad[n].pos1.y*vmat.a12+vquad[n].pos1.z*vmat.a13;
				k[n].pos1.y = vquad[n].pos1.x*vmat.a21+vquad[n].pos1.y*vmat.a22+vquad[n].pos1.z*vmat.a23;
				k[n].pos1.z = vquad[n].pos1.x*vmat.a31+vquad[n].pos1.y*vmat.a32+vquad[n].pos1.z*vmat.a33;
				k[n].pos2.x = vquad[n].pos2.x*vmat.a11+vquad[n].pos2.y*vmat.a12+vquad[n].pos2.z*vmat.a13;
				k[n].pos2.y = vquad[n].pos2.x*vmat.a21+vquad[n].pos2.y*vmat.a22+vquad[n].pos2.z*vmat.a23;
				k[n].pos2.z = vquad[n].pos2.x*vmat.a31+vquad[n].pos2.y*vmat.a32+vquad[n].pos2.z*vmat.a33;
				k[n].pos3.x = vquad[n].pos3.x*vmat.a11+vquad[n].pos3.y*vmat.a12+vquad[n].pos3.z*vmat.a13;
				k[n].pos3.y = vquad[n].pos3.x*vmat.a21+vquad[n].pos3.y*vmat.a22+vquad[n].pos3.z*vmat.a23;
				k[n].pos3.z = vquad[n].pos3.x*vmat.a31+vquad[n].pos3.y*vmat.a32+vquad[n].pos3.z*vmat.a33;
				k[n].pos4.x = vquad[n].pos4.x*vmat.a11+vquad[n].pos4.y*vmat.a12+vquad[n].pos4.z*vmat.a13;
				k[n].pos4.y = vquad[n].pos4.x*vmat.a21+vquad[n].pos4.y*vmat.a22+vquad[n].pos4.z*vmat.a23;
				k[n].pos4.z = vquad[n].pos4.x*vmat.a31+vquad[n].pos4.y*vmat.a32+vquad[n].pos4.z*vmat.a33;
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
				k[n].x = vpoint[n].x+vpos.x;
				k[n].y = vpoint[n].y+vpos.y;
				k[n].z = vpoint[n].z+vpos.z;
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
				k[n].dx = vdir[n].dx+vpos.x;
				k[n].dy = vdir[n].dy+vpos.y;
				k[n].dz = vdir[n].dz+vpos.z;
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
				k[n].pos1.x = vline[n].pos1.x+vpos.x;
				k[n].pos1.y = vline[n].pos1.y+vpos.y;
				k[n].pos1.z = vline[n].pos1.z+vpos.z;
				k[n].pos2.x = vline[n].pos2.x+vpos.x;
				k[n].pos2.y = vline[n].pos2.y+vpos.y;
				k[n].pos2.z = vline[n].pos2.z+vpos.z;
			}
		}
		return k;
	}
	public static Sphere[] translate(Sphere[] vsph, Position vpos) {
		Sphere[] k = null;
		if ((vsph!=null)&&(vpos!=null)) {
			k = new Sphere[vsph.length];
			for (int n=0;n<vsph.length;n++) {
				k[n] = vsph[n].copy();
				k[n].x = vsph[n].x+vpos.x;
				k[n].y = vsph[n].y+vpos.y;
				k[n].z = vsph[n].z+vpos.z;
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
				k[n].pos1.x = vtri[n].pos1.x+vpos.x;
				k[n].pos1.y = vtri[n].pos1.y+vpos.y;
				k[n].pos1.z = vtri[n].pos1.z+vpos.z;
				k[n].pos2.x = vtri[n].pos2.x+vpos.x;
				k[n].pos2.y = vtri[n].pos2.y+vpos.y;
				k[n].pos2.z = vtri[n].pos2.z+vpos.z;
				k[n].pos3.x = vtri[n].pos3.x+vpos.x;
				k[n].pos3.y = vtri[n].pos3.y+vpos.y;
				k[n].pos3.z = vtri[n].pos3.z+vpos.z;
			}
		}
		return k;
	}
	public static Quad[] translate(Quad[] vquad, Position vpos) {
		Quad[] k = null;
		if ((vquad!=null)&&(vpos!=null)) {
			k = new Quad[vquad.length];
			for (int n=0;n<vquad.length;n++) {
				k[n] = vquad[n].copy();
				k[n].pos1.x = vquad[n].pos1.x+vpos.x;
				k[n].pos1.y = vquad[n].pos1.y+vpos.y;
				k[n].pos1.z = vquad[n].pos1.z+vpos.z;
				k[n].pos2.x = vquad[n].pos2.x+vpos.x;
				k[n].pos2.y = vquad[n].pos2.y+vpos.y;
				k[n].pos2.z = vquad[n].pos2.z+vpos.z;
				k[n].pos3.x = vquad[n].pos3.x+vpos.x;
				k[n].pos3.y = vquad[n].pos3.y+vpos.y;
				k[n].pos3.z = vquad[n].pos3.z+vpos.z;
				k[n].pos4.x = vquad[n].pos4.x+vpos.x;
				k[n].pos4.y = vquad[n].pos4.y+vpos.y;
				k[n].pos4.z = vquad[n].pos4.z+vpos.z;
			}
		}
		return k;
	}
	public static Position[] translate(Position[] vpoint, Direction vdir, double mult) {
		Position[] k = null;
		if ((vpoint!=null)&&(vdir!=null)) {
			k = new Position[vpoint.length];
			for (int n=0;n<vpoint.length;n++) {
				k[n] = vpoint[n].copy();
				k[n].x = vpoint[n].x+mult*vdir.dx;
				k[n].y = vpoint[n].y+mult*vdir.dy;
				k[n].z = vpoint[n].z+mult*vdir.dz;
			}
		}
		return k;
	}
	public static Direction[] translate(Direction[] vvec, Direction vdir, double mult) {
		Direction[] k = null;
		if ((vvec!=null)&&(vdir!=null)) {
			k = new Direction[vvec.length];
			for (int n=0;n<vvec.length;n++) {
				k[n] = vvec[n].copy();
				k[n].dx = vvec[n].dx+mult*vdir.dx;
				k[n].dy = vvec[n].dy+mult*vdir.dy;
				k[n].dz = vvec[n].dz+mult*vdir.dz;
			}
		}
		return k;
	}
	public static Line[] translate(Line[] vline, Direction vdir, double mult) {
		Line[] k = null;
		if ((vline!=null)&&(vdir!=null)) {
			k = new Line[vline.length];
			for (int n=0;n<vline.length;n++) {
				k[n] = vline[n].copy();
				k[n].pos1.x = vline[n].pos1.x+mult*vdir.dx;
				k[n].pos1.y = vline[n].pos1.y+mult*vdir.dy;
				k[n].pos1.z = vline[n].pos1.z+mult*vdir.dz;
				k[n].pos2.x = vline[n].pos2.x+mult*vdir.dx;
				k[n].pos2.y = vline[n].pos2.y+mult*vdir.dy;
				k[n].pos2.z = vline[n].pos2.z+mult*vdir.dz;
			}
		}
		return k;
	}
	public static Sphere[] translate(Sphere[] vsph, Direction vdir, double mult) {
		Sphere[] k = null;
		if ((vsph!=null)&&(vdir!=null)) {
			k = new Sphere[vsph.length];
			for (int n=0;n<vsph.length;n++) {
				k[n] = vsph[n].copy();
				k[n].x = vsph[n].x+mult*vdir.dx;
				k[n].y = vsph[n].y+mult*vdir.dy;
				k[n].z = vsph[n].z+mult*vdir.dz;
			}
		}
		return k;
	}
	public static Triangle[] translate(Triangle[] vtri, Direction vdir, double mult) {
		Triangle[] k = null;
		if ((vtri!=null)&&(vdir!=null)) {
			k = new Triangle[vtri.length];
			for (int n=0;n<vtri.length;n++) {
				k[n] = vtri[n].copy();
				k[n].pos1.x = vtri[n].pos1.x+mult*vdir.dx;
				k[n].pos1.y = vtri[n].pos1.y+mult*vdir.dy;
				k[n].pos1.z = vtri[n].pos1.z+mult*vdir.dz;
				k[n].pos2.x = vtri[n].pos2.x+mult*vdir.dx;
				k[n].pos2.y = vtri[n].pos2.y+mult*vdir.dy;
				k[n].pos2.z = vtri[n].pos2.z+mult*vdir.dz;
				k[n].pos3.x = vtri[n].pos3.x+mult*vdir.dx;
				k[n].pos3.y = vtri[n].pos3.y+mult*vdir.dy;
				k[n].pos3.z = vtri[n].pos3.z+mult*vdir.dz;
			}
		}
		return k;
	}
	public static Quad[] translate(Quad[] vquad, Direction vdir, double mult) {
		Quad[] k = null;
		if ((vquad!=null)&&(vdir!=null)) {
			k = new Quad[vquad.length];
			for (int n=0;n<vquad.length;n++) {
				k[n] = vquad[n].copy();
				k[n].pos1.x = vquad[n].pos1.x+mult*vdir.dx;
				k[n].pos1.y = vquad[n].pos1.y+mult*vdir.dy;
				k[n].pos1.z = vquad[n].pos1.z+mult*vdir.dz;
				k[n].pos2.x = vquad[n].pos2.x+mult*vdir.dx;
				k[n].pos2.y = vquad[n].pos2.y+mult*vdir.dy;
				k[n].pos2.z = vquad[n].pos2.z+mult*vdir.dz;
				k[n].pos3.x = vquad[n].pos3.x+mult*vdir.dx;
				k[n].pos3.y = vquad[n].pos3.y+mult*vdir.dy;
				k[n].pos3.z = vquad[n].pos3.z+mult*vdir.dz;
				k[n].pos4.x = vquad[n].pos4.x+mult*vdir.dx;
				k[n].pos4.y = vquad[n].pos4.y+mult*vdir.dy;
				k[n].pos4.z = vquad[n].pos4.z+mult*vdir.dz;
			}
		}
		return k;
	}
	public static Matrix rotationMatrix(double xaxisr, double yaxisr, double zaxisr) {
		Matrix xrot = new Matrix(1,0,0,0,cosd(xaxisr),-sind(xaxisr),0,sind(xaxisr),cosd(xaxisr));
		Matrix yrot = new Matrix(cosd(yaxisr),0,sind(yaxisr),0,1,0,-sind(yaxisr),0,cosd(yaxisr));
		Matrix zrot = new Matrix(cosd(zaxisr),-sind(zaxisr),0,sind(zaxisr),cosd(zaxisr),0,0,0,1);
		return matrixMultiply(zrot,matrixMultiply(yrot, xrot));
	}

	public static Position[] sphereVertexList(Sphere[] spherelist) {
		Position[] k = null;
		if (spherelist!=null) {
			k = new Position[spherelist.length];
			for (int i=0;i<spherelist.length;i++) {
				k[i] = new Position(spherelist[i].x,spherelist[i].y,spherelist[i].z);
			}
		}
		return k;
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

	public static Quad[] generateQuadList(Line[] linelist) {
		//TODO generate quad list
		return null;
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
				Direction tetrahedronsideplanenormal = new Direction(tetrahedronsideplanes[i].a,tetrahedronsideplanes[i].b,tetrahedronsideplanes[i].c);
				Position[] tetrahedronsidepoint = {tetrahedronsidepoints[i]};
				Plane[] tetrahedronsideplane = {tetrahedronsideplanes[i]};
				double[][] tetrahedronsideplanepointdist = planePointDistance(tetrahedronsidepoint, tetrahedronsideplane);
				if (tetrahedronsideplanepointdist[0][0]>0) {
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
			processent.vertexlist = generateVertexList(processent.linelist);
			processent.linelist = generateNonTriangleLineList(processent.linelist);
			processent.aabbboundaryvolume = axisAlignedBoundingBox(processent.vertexlist);
			processent.sphereboundaryvolume = pointCloudCircumSphere(processent.vertexlist);
		}
		Entity[] entitylist = newentitylistarray.toArray(new Entity[newentitylistarray.size()]); 
		return entitylist;
	}
	
	public static void generateEntityListOctree(Entity[] entitylist) {
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
						double aabbxcenter = (newchildren[n].aabbboundaryvolume.x1+newchildren[n].aabbboundaryvolume.x2)/2.0f;
						double aabbycenter = (newchildren[n].aabbboundaryvolume.y1+newchildren[n].aabbboundaryvolume.y2)/2.0f;
						double aabbzcenter = (newchildren[n].aabbboundaryvolume.z1+newchildren[n].aabbboundaryvolume.z2)/2.0f;
						Position[] aabbcenter = {new Position(aabbxcenter,aabbycenter,aabbzcenter)};
						Position[] aabbedge = {new Position(newchildren[n].aabbboundaryvolume.x1,newchildren[n].aabbboundaryvolume.y1,newchildren[n].aabbboundaryvolume.z1)};
						Direction[] aabbdir = vectorFromPoints(aabbcenter, aabbedge);
						double[] aabbradius = vectorLength(aabbdir);
						newchildren[n].sphereboundaryvolume = new Sphere(aabbxcenter,aabbycenter,aabbzcenter,aabbradius[0]);
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
	
	public static Plane[] spheremapPlanes(Position vpos, int vhres, double vhfov, Matrix vmat) {
		return null;
	}
	public static Direction[][] spheremapRays(int vhres, int vvres, double vhfov, double vvfov, Matrix vmat) {
		return null;
	}
	
	public static double[] projectedStep(int vres, double vfov) {
		double[] k = new double[vres];
		double halfvfov = vfov/2.0f;
		double stepmax = Math.abs(tand(halfvfov));
		double stepmin = -stepmax;
		double step = 2.0f/((double)(vres-1))*stepmax;
		for (int i=0;i<vres;i++){k[i]=stepmin+step*i;}
		return k;
	}
	public static double[] projectedAngles(int vres, double vfov) {
		double[] k = new double[vres];
		double[] hd = projectedStep(vres, vfov);
		for (int i=0;i<vres;i++){k[i]=atand(hd[i]);}
		return k;
	}
	public static Direction[] projectedCameraDirections(Matrix vmat) {
		Direction[] rightdirupvectors = new Direction[3];
		Direction dirvector = new Direction(0,0,-1);
		Direction rightvector = new Direction(1,0,0);
		Direction upvector = new Direction(0,-1,0);
		rightdirupvectors[0] = dirvector;
		rightdirupvectors[1] = rightvector;
		rightdirupvectors[2] = upvector;
	    return matrixMultiply(rightdirupvectors, vmat);
	}
	public static Direction[] projectedPlaneDirections(Matrix vmat) {
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
		Direction[] dirrightupvectors = projectedPlaneDirections(vmat);
		Direction rightvector = dirrightupvectors[1];
		Direction[] planenormalvectors = vectorCross(fwdvectors,rightvector);
		planenormalvectors = normalizeVector(planenormalvectors);
	    return planeFromNormalAtPoint(vpos, planenormalvectors);
	}
	public static Direction[][] projectedRays(int vhres, int vvres, double vhfov, double vvfov, Matrix vmat) {
		Direction[][] k = new Direction[vvres][vhres];
		double[] hstep = projectedStep(vhres, vhfov);
		double[] vstep = projectedStep(vvres, vvfov);
		for (int j=0;j<vvres;j++) {
			for (int i=0;i<vhres;i++) {
				k[j][i] = new Direction(hstep[i],-vstep[j],-1);
			}
			k[j] = normalizeVector(k[j]);
			k[j] = matrixMultiply(k[j], vmat);
		}
		return k;
	}
	public static Coordinate[] projectedPoints(Position vpos, Position[] vpoint, int hres, double hfov, int vres, double vfov, Matrix vmat) {
		Coordinate[] k = null;
		if ((vpos!=null)&&(vpoint!=null)&&(vmat!=null)) {
			k = new Coordinate[vpoint.length];
			double halfhfovmult = (1.0f/tand(hfov/2.0f));
			double halfvfovmult = (1.0f/tand(vfov/2.0f));
			double origindeltax = ((double)(hres-1))/2.0f;
			double origindeltay = ((double)(vres-1))/2.0f;
			double halfhres = ((double)hres)/2.0f;
			double halfvres = ((double)vres)/2.0f;
			Direction[] dirrightupvectors = projectedCameraDirections(vmat);
			Plane[] dirrightupplanes = planeFromNormalAtPoint(vpos, dirrightupvectors);
			double[][] fwdintpointsdist = planePointDistance(vpoint, dirrightupplanes);
			for (int i=0;i<vpoint.length;i++) {
				if (fwdintpointsdist[i][0]>0) {
					double hind = halfhfovmult*halfhres*(fwdintpointsdist[i][1]/fwdintpointsdist[i][0])+origindeltax;
					double vind = halfvfovmult*halfvres*(fwdintpointsdist[i][2]/fwdintpointsdist[i][0])+origindeltay;
					k[i] = new Coordinate(hind,vind);
				}
			}
		}
		return k;
	}
	public static Coordinate[][] projectedLines(Position vpos, Line[] vline, int hres, double hfov, int vres, double vfov, Matrix vmat) {
		Coordinate[][] k = null;
		if ((vpos!=null)&&(vline!=null)&&(vmat!=null)) {
			k = new Coordinate[vline.length][2];
			double halfhfovmult = (1.0f/tand(hfov/2.0f));
			double halfvfovmult = (1.0f/tand(vfov/2.0f));
			double origindeltax = ((double)(hres-1))/2.0f;
			double origindeltay = ((double)(vres-1))/2.0f;
			double halfhres = ((double)hres)/2.0f;
			double halfvres = ((double)vres)/2.0f;
			Direction[] dirrightupvectors = projectedCameraDirections(vmat);
			Plane[] dirrightupplanes = planeFromNormalAtPoint(vpos, dirrightupvectors);
			Position[] vlinepoint1 = new Position[vline.length];
			Position[] vlinepoint2 = new Position[vline.length];
			for (int i=0;i<vline.length;i++) {
				vlinepoint1[i]=vline[i].pos1;
				vlinepoint2[i]=vline[i].pos2;
			}
			double[][] fwdintpointsdist1 = planePointDistance(vlinepoint1, dirrightupplanes);
			double[][] fwdintpointsdist2 = planePointDistance(vlinepoint2, dirrightupplanes);
			for (int i=0;i<vline.length;i++) {
				if (fwdintpointsdist1[i][0]>0) {
					double hind = halfhfovmult*halfhres*fwdintpointsdist1[i][1]/fwdintpointsdist1[i][0]+origindeltax;
					double vind = halfvfovmult*halfvres*fwdintpointsdist1[i][2]/fwdintpointsdist1[i][0]+origindeltay;
					k[i][0] = new Coordinate(hind,vind);
				}
				if (fwdintpointsdist2[i][0]>0) {
					double hind = halfhfovmult*halfhres*fwdintpointsdist2[i][1]/fwdintpointsdist2[i][0]+origindeltax;
					double vind = halfvfovmult*halfvres*fwdintpointsdist2[i][2]/fwdintpointsdist2[i][0]+origindeltay;
					k[i][1] = new Coordinate(hind,vind);
				}
			}
		}
		return k;
	}
	public static Coordinate[][] projectedTriangles(Position vpos, Triangle[] vtri, int hres, double hfov, int vres, double vfov, Matrix vmat) {
		Coordinate[][] k = null;
		if ((vpos!=null)&&(vtri!=null)&&(vmat!=null)) {
			k = new Coordinate[vtri.length][3];
			double halfhfovmult = (1.0f/tand(hfov/2.0f));
			double halfvfovmult = (1.0f/tand(vfov/2.0f));
			double origindeltax = ((double)(hres-1))/2.0f;
			double origindeltay = ((double)(vres-1))/2.0f;
			double halfhres = ((double)hres)/2.0f;
			double halfvres = ((double)vres)/2.0f;
			Direction[] dirrightupvectors = projectedCameraDirections(vmat);
			Plane[] dirrightupplanes = planeFromNormalAtPoint(vpos, dirrightupvectors);
			Position[] vtripoint1 = new Position[vtri.length];
			Position[] vtripoint2 = new Position[vtri.length];
			Position[] vtripoint3 = new Position[vtri.length];
			for (int i=0;i<vtri.length;i++) {
				vtripoint1[i]=vtri[i].pos1;
				vtripoint2[i]=vtri[i].pos2;
				vtripoint3[i]=vtri[i].pos3;
			}
			double[][] fwdintpointsdist1 = planePointDistance(vtripoint1, dirrightupplanes);
			double[][] fwdintpointsdist2 = planePointDistance(vtripoint2, dirrightupplanes);
			double[][] fwdintpointsdist3 = planePointDistance(vtripoint3, dirrightupplanes);
			for (int i=0;i<vtri.length;i++) {
				if (fwdintpointsdist1[i][0]>0) {
					double hind = halfhfovmult*halfhres*fwdintpointsdist1[i][1]/fwdintpointsdist1[i][0]+origindeltax;
					double vind = halfvfovmult*halfvres*fwdintpointsdist1[i][2]/fwdintpointsdist1[i][0]+origindeltay;
					k[i][0] = new Coordinate(hind,vind);
				}
				if (fwdintpointsdist2[i][0]>0) {
					double hind = halfhfovmult*halfhres*fwdintpointsdist2[i][1]/fwdintpointsdist2[i][0]+origindeltax;
					double vind = halfvfovmult*halfvres*fwdintpointsdist2[i][2]/fwdintpointsdist2[i][0]+origindeltay;
					k[i][1] = new Coordinate(hind,vind);
				}
				if (fwdintpointsdist3[i][0]>0) {
					double hind = halfhfovmult*halfhres*fwdintpointsdist3[i][1]/fwdintpointsdist3[i][0]+origindeltax;
					double vind = halfvfovmult*halfvres*fwdintpointsdist3[i][2]/fwdintpointsdist3[i][0]+origindeltay;
					k[i][2] = new Coordinate(hind,vind);
				}
			}
		}
		return k;
	}
	public static Coordinate[][] projectedQuads(Position vpos, Quad[] vquad, int hres, double hfov, int vres, double vfov, Matrix vmat) {
		Coordinate[][] k = null;
		if ((vpos!=null)&&(vquad!=null)&&(vmat!=null)) {
			k = new Coordinate[vquad.length][4];
			double halfhfovmult = (1.0f/tand(hfov/2.0f));
			double halfvfovmult = (1.0f/tand(vfov/2.0f));
			double origindeltax = ((double)(hres-1))/2.0f;
			double origindeltay = ((double)(vres-1))/2.0f;
			double halfhres = ((double)hres)/2.0f;
			double halfvres = ((double)vres)/2.0f;
			Direction[] dirrightupvectors = projectedCameraDirections(vmat);
			Plane[] dirrightupplanes = planeFromNormalAtPoint(vpos, dirrightupvectors);
			Position[] vquadpoint1 = new Position[vquad.length];
			Position[] vquadpoint2 = new Position[vquad.length];
			Position[] vquadpoint3 = new Position[vquad.length];
			Position[] vquadpoint4 = new Position[vquad.length];
			for (int i=0;i<vquad.length;i++) {
				vquadpoint1[i]=vquad[i].pos1;
				vquadpoint2[i]=vquad[i].pos2;
				vquadpoint3[i]=vquad[i].pos3;
				vquadpoint4[i]=vquad[i].pos4;
			}
			double[][] fwdintpointsdist1 = planePointDistance(vquadpoint1, dirrightupplanes);
			double[][] fwdintpointsdist2 = planePointDistance(vquadpoint2, dirrightupplanes);
			double[][] fwdintpointsdist3 = planePointDistance(vquadpoint3, dirrightupplanes);
			double[][] fwdintpointsdist4 = planePointDistance(vquadpoint4, dirrightupplanes);
			for (int i=0;i<vquad.length;i++) {
				if (fwdintpointsdist1[i][0]>0) {
					double hind = halfhfovmult*halfhres*fwdintpointsdist1[i][1]/fwdintpointsdist1[i][0]+origindeltax;
					double vind = halfvfovmult*halfvres*fwdintpointsdist1[i][2]/fwdintpointsdist1[i][0]+origindeltay;
					k[i][0] = new Coordinate(hind,vind);
				}
				if (fwdintpointsdist2[i][0]>0) {
					double hind = halfhfovmult*halfhres*fwdintpointsdist2[i][1]/fwdintpointsdist2[i][0]+origindeltax;
					double vind = halfvfovmult*halfvres*fwdintpointsdist2[i][2]/fwdintpointsdist2[i][0]+origindeltay;
					k[i][1] = new Coordinate(hind,vind);
				}
				if (fwdintpointsdist3[i][0]>0) {
					double hind = halfhfovmult*halfhres*fwdintpointsdist3[i][1]/fwdintpointsdist3[i][0]+origindeltax;
					double vind = halfvfovmult*halfvres*fwdintpointsdist3[i][2]/fwdintpointsdist3[i][0]+origindeltay;
					k[i][2] = new Coordinate(hind,vind);
				}
				if (fwdintpointsdist4[i][0]>0) {
					double hind = halfhfovmult*halfhres*fwdintpointsdist4[i][1]/fwdintpointsdist4[i][0]+origindeltax;
					double vind = halfvfovmult*halfvres*fwdintpointsdist4[i][2]/fwdintpointsdist4[i][0]+origindeltay;
					k[i][3] = new Coordinate(hind,vind);
				}
			}
		}
		return k;
	}

	public static Rectangle[] projectedSphereIntersection(Position vpos, Sphere[] vsphere, int hres, int vres, double hfov, double vfov, Matrix vmat) {
		Rectangle[] k = new Rectangle[vsphere.length];
		if ((vpos!=null)&&(vsphere!=null)&&(vmat!=null)) {
			Position[] vpoint = sphereVertexList(vsphere);
			Direction[] lvec = vectorFromPoints(vpos, vsphere);
			double[] lvecl = vectorLength(lvec);
			double halfhfovmult = (1.0f/tand(hfov/2.0f));
			double halfvfovmult = (1.0f/tand(vfov/2.0f));
			double origindeltax = ((double)(hres-1))/2.0f;
			double origindeltay = ((double)(vres-1))/2.0f;
			double halfhres = ((double)hres)/2.0f;
			double halfvres = ((double)vres)/2.0f;
			Direction[] dirrightupvectors = projectedCameraDirections(vmat);
			Plane[] dirrightupplanes = planeFromNormalAtPoint(vpos, dirrightupvectors);
			double[][] fwdintpointsdist = planePointDistance(vpoint, dirrightupplanes);
			for (int i=0;i<vpoint.length;i++) {
				if (fwdintpointsdist[i][0]>0) {
					double cmradang = asind(vsphere[i].r/lvecl[i]);
					if (!Double.isFinite(cmradang)) {cmradang = 180.0f;}
					double hangle = atand(fwdintpointsdist[i][1]/fwdintpointsdist[i][0]);
					double vangle = atand(fwdintpointsdist[i][2]/fwdintpointsdist[i][0]);
					double hangle1 = hangle-cmradang;
					double hangle2 = hangle+cmradang;
					double vangle1 = vangle-cmradang;
					double vangle2 = vangle+cmradang;
					if (hangle1<-90.0f) {hangle1=-90.0f;}
					if (hangle2>90.0f) {hangle2=90.0f;}
					if (vangle1<-90.0f) {vangle1=-90.0f;}
					if (vangle2>90.0f) {vangle2=90.0f;}
					int hcenterind1 = (int)Math.ceil(halfhfovmult*halfhres*(tand(hangle1))+origindeltax);
					int hcenterind2 = (int)Math.floor(halfhfovmult*halfhres*(tand(hangle2))+origindeltax);
					int vcenterind1 = (int)Math.ceil(halfvfovmult*halfvres*(tand(vangle1))+origindeltay);
					int vcenterind2 = (int)Math.floor(halfvfovmult*halfvres*(tand(vangle2))+origindeltay);
					if (hcenterind1<0) {hcenterind1=0;}
					if (hcenterind2>=hres) {hcenterind2=hres-1;}
					if (vcenterind1<0) {vcenterind1=0;}
					if (vcenterind2>=vres) {vcenterind2=vres-1;}
					int spherewidth = hcenterind2-hcenterind1+1;
					int sphereheight = vcenterind2-vcenterind1+1;
					if ((hcenterind1<hres)&&(hcenterind2>=0)&&(vcenterind1<vres)&&(vcenterind2>=0)&&(spherewidth>0)&&(sphereheight>0)) {
						k[i] = new Rectangle(hcenterind1,vcenterind1,spherewidth,sphereheight);
					}
				}
			}
		}
		return k;
	}
	public static Rectangle[][] cubemapSphereIntersection(Position vpos, Sphere[] vsphere, int vres) {
		Rectangle[][] k = new Rectangle[6][vsphere.length];
		Matrix rotxp0 = MathLib.rotationMatrix(0.0f, 0.0f, 0.0f);
		Matrix rotxp90 = MathLib.rotationMatrix(90.0f, 0.0f, 0.0f);
		Matrix rotxp180 = MathLib.rotationMatrix(180.0f, 0.0f, 0.0f);
		Matrix rotzn90 = MathLib.rotationMatrix(0.0f, 0.0f, -90.0f);
		Matrix rotzp90 = MathLib.rotationMatrix(0.0f, 0.0f, 90.0f);
		Matrix rotzp180 = MathLib.rotationMatrix(0.0f, 0.0f, 180.0f);
		Matrix rotxp90zn90 = MathLib.matrixMultiply(rotzn90, rotxp90);
		Matrix rotxp90zp90 = MathLib.matrixMultiply(rotzp90, rotxp90);
		Matrix rotxp90zp180 = MathLib.matrixMultiply(rotzp180, rotxp90);
		k[0] = projectedSphereIntersection(vpos, vsphere, vres, vres, 90, 90, rotxp90zn90);
		k[1] = projectedSphereIntersection(vpos, vsphere, vres, vres, 90, 90, rotxp90);
		k[2] = projectedSphereIntersection(vpos, vsphere, vres, vres, 90, 90, rotxp90zp90);
		k[3] = projectedSphereIntersection(vpos, vsphere, vres, vres, 90, 90, rotxp90zp180);
		k[4] = projectedSphereIntersection(vpos, vsphere, vres, vres, 90, 90, rotxp180);
		k[5] = projectedSphereIntersection(vpos, vsphere, vres, vres, 90, 90, rotxp0);
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
			double[] v12D = vectorDot(v12);
			double[] v13D = vectorDot(v13);
			Direction[] cv12v13 = vectorCross(v12,v13);
			double[] cv12v13D = vectorDot(cv12v13);
			Direction[] toparg = {new Direction(v12D[0]*v13[0].dx-v13D[0]*v12[0].dx,v12D[0]*v13[0].dy-v13D[0]*v12[0].dy,v12D[0]*v13[0].dz-v13D[0]*v12[0].dz)};
			Direction[] top = vectorCross(toparg,cv12v13);
			double bottom = 2.0f*cv12v13D[0];
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
