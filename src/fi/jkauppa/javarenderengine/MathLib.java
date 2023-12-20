package fi.jkauppa.javarenderengine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;

public class MathLib {
	public static class Position implements Comparable<Position> {public double x,y,z; public Position(double xi,double yi,double zi){this.x=xi;this.y=yi;this.z=zi;} @Override public int compareTo(Position o){int k=-1;if(this.x==o.x){if(this.y==o.y){if(this.z==o.z){k=0;}else if(this.z>o.z){k=1;}}else if(this.y>o.y){k=1;}}else if(this.x>o.x){k=1;}return k;} public Position copy(){return new Position(this.x,this.y,this.z);}}
	public static class Direction {public double dx,dy,dz; public Direction(double dxi,double dyi,double dzi){this.dx=dxi;this.dy=dyi;this.dz=dzi;}}
	public static class Coordinate {public double u,v; public Coordinate(double ui,double vi){this.u=ui;this.v=vi;}}
	public static class Rotation {public double x,y,z; public Rotation(double xi,double yi,double zi){this.x=xi;this.y=yi;this.z=zi;}}
	public static class Sphere {public double x,y,z,r; public Sphere(double xi,double yi,double zi,double ri){this.x=xi;this.y=yi;this.z=zi;this.r=ri;}}
	public static class Plane {public double a,b,c,d; public Plane(double ai,double bi,double ci,double di){this.a=ai;this.b=bi;this.c=ci;this.d=di;}}
	public static class Position2 implements Comparable<Position2> {public Position pos1,pos2; public Position2(Position pos1i,Position pos2i){this.pos1=pos1i;this.pos2=pos2i;} @Override public int compareTo(Position2 o){int k=-1;Position2 ts=this.sort();Position2 os=o.sort();if(ts.pos1.x>os.pos1.x){k=1;}else if(ts.pos1.x==os.pos1.x){if(ts.pos1.y>os.pos1.y){k=1;}else if(ts.pos1.y==os.pos1.y){if(ts.pos1.z>os.pos1.z){k=1;}else if(ts.pos1.z==os.pos1.z){if(ts.pos2.x>os.pos2.x){k=1;}else if(ts.pos2.x==os.pos2.x){if(ts.pos2.y>os.pos2.y){k=1;}else if(ts.pos2.y==os.pos2.y){if(ts.pos2.z>os.pos2.z){k=1;}else if(ts.pos2.z==os.pos2.z){k=0;}}}}}}return k;} public Position2 copy(){return new Position2(new Position(this.pos1.x,this.pos1.y,this.pos1.z),new Position(this.pos2.x,this.pos2.y,this.pos2.z));} public Position2 swap(){return new Position2(this.pos2,this.pos1);} public Position2 sort(){Position2 k=this;boolean keeporder=true;if(this.pos1.x>this.pos2.x){keeporder=false;}else if(this.pos1.x==this.pos2.x){if(this.pos1.y>this.pos2.y){keeporder=false;}else if (this.pos1.y==this.pos2.y){if(this.pos1.z>this.pos2.z){keeporder=false;}}}if(!keeporder){k=this.swap();}return k;}}
	public static class Triangle implements Comparable<Triangle> {public Position pos1,pos2,pos3; public int oind,tind,mind; public Triangle(Position pos1i,Position pos2i,Position pos3i){this.pos1=pos1i;this.pos2=pos2i;this.pos3=pos3i;} public Triangle copy(){Triangle k=new Triangle(new Position(this.pos1.x,this.pos1.y,this.pos1.z),new Position(this.pos2.x,this.pos2.y,this.pos2.z),new Position(this.pos3.x,this.pos3.y,this.pos3.z));k.oind=this.oind;k.tind=this.tind;k.mind=this.mind; return k;}
	@Override public int compareTo(Triangle o) {
		int k = -1;
		Position[] tposarray = {this.pos1,this.pos2,this.pos3};
		Position[] oposarray = {o.pos1,o.pos2,o.pos3};
		Arrays.sort(tposarray);
		Arrays.sort(oposarray);
		if ((tposarray[0].compareTo(oposarray[0])==0)&&(tposarray[1].compareTo(oposarray[1])==0)&&(tposarray[2].compareTo(oposarray[2])==0)) {
			k = 0;
		} else {
			double zposaver1 = (this.pos1.z+this.pos2.z+this.pos3.z)/3.0f;
			double zposaver2 = (o.pos1.z+o.pos2.z+o.pos3.z)/3.0f;
			if (zposaver1>=zposaver2) {
				k = 1;
			}
		}
		return k;
	}}
	public static class Polyangle {public Position[] poslist; public Polyangle(Position[] poslisti){this.poslist=poslisti;}}
	public static class Matrix {public double a11,a12,a13,a21,a22,a23,a31,a32,a33;public Matrix(double a11i,double a12i,double a13i,double a21i,double a22i,double a23i,double a31i,double a32i,double a33i){this.a11=a11i;this.a12=a12i;this.a13=a13i;this.a21=a21i;this.a22=a22i;this.a23=a23i;this.a31=a31i;this.a32=a32i;this.a33=a33i;}}
	
	public static double[] vectorDot(Direction[] vdir, Position vpoint){double[] k=null; if((vdir!=null)&&(vpoint!=null)){k=new double[vdir.length];for(int n=0;n<vdir.length;n++){k[n] = vdir[n].dx*vpoint.x+vdir[n].dy*vpoint.y+vdir[n].dz*vpoint.z;}}return k;}
	public static double[] vectorDot(Direction[] vdir, Position[] vpoint){double[] k=null; if((vdir!=null)&&(vpoint!=null)&&(vdir.length==vpoint.length)){k=new double[vdir.length];for(int n=0;n<vdir.length;n++){k[n] = vdir[n].dx*vpoint[n].x+vdir[n].dy*vpoint[n].y+vdir[n].dz*vpoint[n].z;}}return k;}
	public static double[] vectorDot(Direction vdir1, Direction[] vdir2){double[] k=null; if((vdir1!=null)&&(vdir2!=null)){k=new double[vdir2.length];for(int n=0;n<vdir2.length;n++){k[n] = vdir1.dx*vdir2[n].dx+vdir1.dy*vdir2[n].dy+vdir1.dz*vdir2[n].dz;}}return k;}
	public static double[] vectorDot(Direction[] vdir1, Direction[] vdir2){double[] k=null; if((vdir1!=null)&&(vdir2!=null)&&(vdir1.length==vdir2.length)){k=new double[vdir1.length];for(int n=0;n<vdir1.length;n++){k[n] = vdir1[n].dx*vdir2[n].dx+vdir1[n].dy*vdir2[n].dy+vdir1[n].dz*vdir2[n].dz;}}return k;}
	public static double[] vectorDot(Direction[] vdir){double[] k=null; if(vdir!=null){k=new double[vdir.length];for(int n=0;n<vdir.length;n++){k[n] = vdir[n].dx*vdir[n].dx+vdir[n].dy*vdir[n].dy+vdir[n].dz*vdir[n].dz;}}return k;}
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
	public static Direction[] vectorFromPoints(Position[] vpoint1, Position[] vpoint2) {
		Direction[] k = null;
		if ((vpoint1!=null)&&(vpoint2!=null)&&(vpoint1.length==vpoint2.length)) {
			k = new Direction[vpoint1.length];
			for (int n=0;n<vpoint1.length;n++) {
				k[n] = new Direction(vpoint2[n].x-vpoint1[n].x, vpoint2[n].y-vpoint1[n].y, vpoint2[n].z-vpoint1[n].z);
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
	public static Position2[][] planeTriangleIntersection(Plane[] vplane, Triangle[] vtri) {
		Position2[][] k = null;
		if ((vplane!=null)&&(vtri!=null)) {
			k = new Position2[vplane.length][vtri.length]; 
			for (int m=0;m<vtri.length;m++) {
				Position[] p1 = new Position[1]; p1[0] = vtri[m].pos1;
				Position[] p2 = new Position[1]; p2[0] = vtri[m].pos2;
				Position[] p3 = new Position[1]; p3[0] = vtri[m].pos3;
				Direction[] vtri12 = vectorFromPoints(p1, p2);
				Direction[] vtri13 = vectorFromPoints(p1, p3);
				Direction[] vtri23 = vectorFromPoints(p2, p3);
				double[][] ptd1 = rayPlaneDistance(vtri[m].pos1, vtri12, vplane);
				double[][] ptd2 = rayPlaneDistance(vtri[m].pos1, vtri13, vplane);
				double[][] ptd3 = rayPlaneDistance(vtri[m].pos2, vtri23, vplane);
				for (int n=0;n<vplane.length;n++) {
					Position[] ptlint1 = new Position[1]; ptlint1[0] = new Position(vtri[m].pos1.x+ptd1[0][n]*vtri12[0].dx,vtri[m].pos1.y+ptd1[0][n]*vtri12[0].dy,vtri[m].pos1.z+ptd1[0][n]*vtri12[0].dz);
					Position[] ptlint2 = new Position[1]; ptlint2[0] = new Position(vtri[m].pos1.x+ptd2[0][n]*vtri13[0].dx,vtri[m].pos1.y+ptd2[0][n]*vtri13[0].dy,vtri[m].pos1.z+ptd2[0][n]*vtri13[0].dz);
					Position[] ptlint3 = new Position[1]; ptlint3[0] = new Position(vtri[m].pos2.x+ptd3[0][n]*vtri23[0].dx,vtri[m].pos2.y+ptd3[0][n]*vtri23[0].dy,vtri[m].pos2.z+ptd3[0][n]*vtri23[0].dz);
					boolean ptlhit1 = (ptd1[0][n]>=0)&(ptd1[0][n]<=1);
					boolean ptlhit2 = (ptd2[0][n]>=0)&(ptd2[0][n]<=1);
					boolean ptlhit3 = (ptd3[0][n]>=0)&(ptd3[0][n]<=1);
					if (ptlhit1|ptlhit2|ptlhit3) {
						if (ptlhit1&&ptlhit2) {
							k[n][m] = new Position2(ptlint1[0],ptlint2[0]);
						} else if (ptlhit1&&ptlhit3) {
							k[n][m] = new Position2(ptlint1[0],ptlint3[0]);
						} else if (ptlhit2&&ptlhit3) {
							k[n][m] = new Position2(ptlint2[0],ptlint3[0]);
						}
					}
				}
			}
		}
		return k;
	}
	public static Position[][] planeLineIntersection(Plane[] vplane, Position2[] vline) {
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
	
	public static boolean[][] mutualSphereIntersection(Sphere[] vsphere) {
		boolean[][] k = null;
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
		int[] xlistsidx = indexSort(xlist);
		int[] ylistsidx = indexSort(ylist);
		int[] zlistsidx = indexSort(zlist);
		double[] xlistsval = indexValues(xlist,xlistsidx);
		double[] ylistsval = indexValues(ylist,ylistsidx);
		double[] zlistsval = indexValues(zlist,zlistsidx);
		for (int i=0; i<vsphere.length; i++) {
			int xlistidx1 = Arrays.binarySearch(xlist,xlist[i*2]);
			int xlistidx2 = Arrays.binarySearch(xlist,xlist[i*2+1]);
			int ylistidx1 = Arrays.binarySearch(ylist,ylist[i*2]);
			int ylistidx2 = Arrays.binarySearch(ylist,ylist[i*2+1]);
			int zlistidx1 = Arrays.binarySearch(zlist,zlist[i*2]);
			int zlistidx2 = Arrays.binarySearch(zlist,zlist[i*2+1]);
			//TODO limit sort intersection
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
				k[n] = new Position(
						vpoint[n].x*vmat.a11+vpoint[n].y*vmat.a12+vpoint[n].z*vmat.a13,
						vpoint[n].x*vmat.a21+vpoint[n].y*vmat.a22+vpoint[n].z*vmat.a23,
						vpoint[n].x*vmat.a31+vpoint[n].y*vmat.a32+vpoint[n].z*vmat.a33
						);
			}
		}
		return k;
	}
	public static Direction[] matrixMultiply(Direction[] vdir, Matrix vmat) {
		Direction[] k = null;
		if ((vdir!=null)&&(vmat!=null)) {
			k = new Direction[vdir.length];
			for (int n=0;n<vdir.length;n++) {
				k[n] = new Direction(
						vdir[n].dx*vmat.a11+vdir[n].dy*vmat.a12+vdir[n].dz*vmat.a13,
						vdir[n].dx*vmat.a21+vdir[n].dy*vmat.a22+vdir[n].dz*vmat.a23,
						vdir[n].dx*vmat.a31+vdir[n].dy*vmat.a32+vdir[n].dz*vmat.a33
						);
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
	public static Position2[] translate(Position2[] vline, Position vpos) {
		Position2[] k = null;
		if ((vline!=null)&&(vpos!=null)) {
			k = new Position2[vline.length];
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
	
	public static int[] indexSort(double[] data) {
		int[] k = null;
		if ((data!=null)&&(data.length>0)) {
			k = new int[data.length];
			Integer[] idx = new Integer[data.length];
			for (int i=0;i<data.length;i++) {
				idx[i] = i; 
			}
			Arrays.sort(idx, new Comparator<Integer>() {
			    @Override public int compare(final Integer o1, final Integer o2) {
			        return Double.compare(data[o1], data[o2]);
			    }
			});
			for (int i=0;i<data.length;i++) {
				k[i] = idx[i].intValue(); 
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
	
	public static Position[] generateVertexList(Position[] vertexlist) {
		TreeSet<Position> uniquevertexlist = new TreeSet<Position>(Arrays.asList(vertexlist));
		return uniquevertexlist.toArray(new Position[uniquevertexlist.size()]);
	}
	public static Position[] generateVertexList(Position2[] linelist) {
		ArrayList<Position> arrayvertexlist = new ArrayList<Position>(); 
		for (int i=0;i<linelist.length;i++) {
			arrayvertexlist.add(linelist[i].pos1);
			arrayvertexlist.add(linelist[i].pos2);
		}
		TreeSet<Position> uniquevertexlist = new TreeSet<Position>(arrayvertexlist);
		return uniquevertexlist.toArray(new Position[uniquevertexlist.size()]);
	}
	public static Triangle[] generateTriangleList(Position2[] linelist) {
		TreeSet<Position2> uniquelinetree = new TreeSet<Position2>(Arrays.asList(linelist));
		Position2[] uniquelinelist = uniquelinetree.toArray(new Position2[uniquelinetree.size()]);
		ArrayList<Triangle> trianglelist = new ArrayList<Triangle>();
		for (int k=0;k<uniquelinelist.length;k++) {
			Position2 kline = uniquelinelist[k];
			for (int j=k+1;j<uniquelinelist.length;j++) {
				Position2 jline = uniquelinelist[j];
				boolean kjlineconnected11 = kline.pos1.compareTo(jline.pos1)==0;
				boolean kjlineconnected12 = kline.pos1.compareTo(jline.pos2)==0;
				boolean kjlineconnected21 = kline.pos2.compareTo(jline.pos1)==0;
				boolean kjlineconnected22 = kline.pos2.compareTo(jline.pos2)==0;
				boolean kjlineconnected = kjlineconnected11||kjlineconnected12||kjlineconnected21||kjlineconnected22;
				if (kjlineconnected) {
					boolean klinefirst = (kjlineconnected11||kjlineconnected12)?true:false;
					boolean jlinefirst = (kjlineconnected11||kjlineconnected21)?true:false;
					for (int i=j+1;i<uniquelinelist.length;i++) {
						Position2 iline = uniquelinelist[i];
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
	public static Polyangle[] generatePolygonList(Position2[] linelist) {
		TreeSet<Position2> uniquelinetree = new TreeSet<Position2>(Arrays.asList(linelist));
		Position2[] uniquelinelist = uniquelinetree.toArray(new Position2[uniquelinetree.size()]);
		TreeSet<Position2> trianglelinetree = new TreeSet<Position2>();
		ArrayList<Polyangle> uniquepolygonlist = new ArrayList<Polyangle>();
		for (int k=0;k<uniquelinelist.length;k++) {
			Position2 kline = uniquelinelist[k];
			for (int j=k+1;j<uniquelinelist.length;j++) {
				Position2 jline = uniquelinelist[j];
				boolean kjlineconnected11 = kline.pos1.compareTo(jline.pos1)==0;
				boolean kjlineconnected12 = kline.pos1.compareTo(jline.pos2)==0;
				boolean kjlineconnected21 = kline.pos2.compareTo(jline.pos1)==0;
				boolean kjlineconnected22 = kline.pos2.compareTo(jline.pos2)==0;
				boolean kjlineconnected = kjlineconnected11||kjlineconnected12||kjlineconnected21||kjlineconnected22;
				if (kjlineconnected) {
					boolean klinefirst = (kjlineconnected11||kjlineconnected12)?true:false;
					boolean jlinefirst = (kjlineconnected11||kjlineconnected21)?true:false;
					for (int i=j+1;i<uniquelinelist.length;i++) {
						Position2 iline = uniquelinelist[i];
						Position klinefreevertex = klinefirst?kline.pos2:kline.pos1;
						Position jlinefreevertex = jlinefirst?jline.pos2:jline.pos1;
						Position connectedvertex = klinefirst?kline.pos1:kline.pos2;
						boolean ilinecoonnected = (klinefreevertex.compareTo(iline.pos1)==0)&&(jlinefreevertex.compareTo(iline.pos2)==0);
						boolean ilinecoonnectedr = (klinefreevertex.compareTo(iline.pos2)==0)&&(jlinefreevertex.compareTo(iline.pos1)==0);
						if (ilinecoonnected||ilinecoonnectedr) {
							Position[] linevertexlist = new Position[3];
							linevertexlist[0] = connectedvertex;
							linevertexlist[1] = klinefreevertex;
							linevertexlist[2] = jlinefreevertex;
							uniquepolygonlist.add(new Polyangle(linevertexlist));
							trianglelinetree.add(kline);
							trianglelinetree.add(jline);
							trianglelinetree.add(iline);
						}
					}
				}
			}
		}
		TreeSet<Position2> nontrianglelinetree = new TreeSet<Position2>(Arrays.asList(linelist));
		nontrianglelinetree.removeAll(trianglelinetree);  
		Position2[] nontrianglelinelist = nontrianglelinetree.toArray(new Position2[nontrianglelinetree.size()]);
		for (int k=0;k<nontrianglelinelist.length;k++) {
			if (nontrianglelinelist[k].pos1.compareTo(nontrianglelinelist[k].pos2)==0) {
				Position[] linevertexlist = new Position[1];
				linevertexlist[0] = nontrianglelinelist[k].pos1;
				uniquepolygonlist.add(new Polyangle(linevertexlist));
			} else {
				Position[] linevertexlist = new Position[2];
				linevertexlist[0] = nontrianglelinelist[k].pos1;
				linevertexlist[1] = nontrianglelinelist[k].pos2;
				uniquepolygonlist.add(new Polyangle(linevertexlist));
			}
		}
		return uniquepolygonlist.toArray(new Polyangle[uniquepolygonlist.size()]);
	}
	
	public static double[] projectedStep(int vres, int vfov) {
		double[] k = new double[vres];
		double halfvfov = ((double)vfov)/2.0f;
		double stepmax = Math.abs(Math.tan(halfvfov*(Math.PI/180.0f)));
		double stepmin = -stepmax;
		double step = 2.0f/((double)(vres-1))*stepmax;
		for (int i=0;i<vres;i++){k[i]=stepmin+step*i;}
		return k;
	}
	public static double[] projectedAngles(int vres, int vfov) {
		double[] k = new double[vres];
		double[] hd = projectedStep(vres, vfov);
		for (int i=0;i<vres;i++){k[i]=(180.0f/Math.PI)*Math.atan(hd[i]);}
		return k;
	}
	public static Direction[] projectedDirections(Rotation vrot) {
		Direction[] rightdirupvectors = new Direction[3];
		Direction rightvector = new Direction(0,0,1);
		Direction dirvector = new Direction(1,0,0);
		Direction upvector = new Direction(0,1,0);
		rightdirupvectors[0] = rightvector;
		rightdirupvectors[1] = dirvector;
		rightdirupvectors[2] = upvector;
	    Matrix rotmat = rotationMatrix(vrot.x, vrot.y, vrot.z);
	    return matrixMultiply(rightdirupvectors, rotmat);
	}
	public static Direction[] projectedVectors(int vres, int vfov, Rotation vrot) {
	    double[] steps = projectedStep(vres,vfov);
	    Matrix rotmat = rotationMatrix(vrot.x, vrot.y, vrot.z);
		Direction[] fwdvectors = new Direction[vres];
	    for (int i=0;i<vres;i++) {
	    	fwdvectors[i] = new Direction(1, steps[i], 0);
	    }
	    return matrixMultiply(fwdvectors, rotmat);
	}
	public static Plane[] projectedPlanes(Position vpos, int vres, int vfov, Rotation vrot) {
		Direction[] fwdvectors = projectedVectors(vres, vfov, vrot);
		Direction[] rightdirupvectors = projectedDirections(vrot);
		Direction rightvector = rightdirupvectors[0];
		Direction[] planenormalvectors = vectorCross(rightvector, fwdvectors);
		planenormalvectors = normalizeVector(planenormalvectors);
	    return planeFromNormalAtPoint(vpos, planenormalvectors);
	}
	public static Direction[] projectedRays(int vhres, int vvres, int vhfov, int vvfov) {
		Direction[] k = null;
		return k;
	}
	
}
