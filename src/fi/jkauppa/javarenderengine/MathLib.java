package fi.jkauppa.javarenderengine;

public class MathLib {
	public static class Position {public double x,y,z; public Position(double xi,double yi,double zi){this.x=xi;this.y=yi;this.z=zi;}}
	public static class Direction {public double dx,dy,dz; public Direction(double dxi,double dyi,double dzi){this.dx=dxi;this.dy=dyi;this.dz=dzi;}}
	public static class Sphere {public double x,y,z,r; public Sphere(double xi,double yi,double zi,double ri){this.x=xi;this.y=yi;this.z=zi;this.r=ri;}}
	public static class Plane {public double a,b,c,d; public Plane(double ai,double bi,double ci,double di){this.a=ai;this.b=bi;this.c=ci;this.d=di;}}
	public static class Position2 {public Position pos1,pos2; public Position2(Position pos1i,Position pos2i){this.pos1=pos1i;this.pos2=pos2i;}}
	public static class Ray {public Position pos; public Direction dir; public Ray(Position posi,Direction diri){this.pos=posi;this.dir=diri;}}
	public static class Triangle {public Position pos1,pos2,pos3; public Triangle(Position pos1i,Position pos2i,Position pos3i){this.pos1=pos1i;this.pos2=pos2i;this.pos3=pos3i;}}
	
	public static double[] vectorDot(Direction[] vdir, Position[] vpoint){double[] k=null; if((vdir!=null)&&(vpoint!=null)&&(vdir.length==vpoint.length)){k=new double[vdir.length];for(int n=0;n<vdir.length;n++){k[n] = vdir[n].dx*vpoint[n].x+vdir[n].dy*vpoint[n].y+vdir[n].dz*vpoint[n].z;}}return k;}
	public static double[] vectorDot(Direction[] vdir1, Direction[] vdir2){double[] k=null; if((vdir1!=null)&&(vdir2!=null)&&(vdir1.length==vdir2.length)){k=new double[vdir1.length];for(int n=0;n<vdir1.length;n++){k[n] = vdir1[n].dx*vdir2[n].dx+vdir1[n].dy*vdir2[n].dy+vdir1[n].dz*vdir2[n].dz;}}return k;}
	public static double[] vectorDot(Direction[] vdir){double[] k=null; if(vdir!=null){k=new double[vdir.length];for(int n=0;n<vdir.length;n++){k[n] = vdir[n].dx*vdir[n].dx+vdir[n].dy*vdir[n].dy+vdir[n].dz*vdir[n].dz;}}return k;}
	public static double[] vectorDot(Plane[] vplane, Position vpoint){double[] k=null; if((vplane!=null)&&(vpoint!=null)){k=new double[vplane.length];for(int n=0;n<vplane.length;n++){k[n] = vplane[n].a*vpoint.x+vplane[n].b*vpoint.y+vplane[n].c*vpoint.z+vplane[n].d;}}return k;}
	public static double[] vectorDot(Plane[] vplane, Direction vdir){double[] k=null; if((vplane!=null)&&(vdir!=null)){k=new double[vplane.length];for(int n=0;n<vplane.length;n++){k[n] = vplane[n].a*vdir.dx+vplane[n].b*vdir.dy+vplane[n].c*vdir.dz;}}return k;}
	
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

	public static double[] vectorAngle(Direction[] vdir1, Direction[] vdir2) {
		double[] k = null;
		if ((vdir1!=null)&&(vdir2!=null)&&(vdir1.length==vdir2.length)) {
			k = new double[vdir1.length];
			double[] vdir1length = vectorLength(vdir1);
			double[] vdir2length = vectorLength(vdir2);
			double[] vdir12dot = vectorDot(vdir1,vdir2);
			for (int n=0;n<vdir1.length;n++) {
				k[n] = (180/Math.PI)*Math.acos(vdir12dot[n])/(vdir1length[n]*vdir2length[n]);
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
						Direction[] v12n = normalizeVector(v12);
						Direction[] v13n = normalizeVector(v13);
						Direction[] v23n = normalizeVector(v23);
						double[] v12L = vectorLength(v12);
						double[] v13L = vectorLength(v13);
						double[] v23L = vectorLength(v23);
						double[] a1 = vectorAngle(v12,v13);
						double[] a2 = vectorAngle(v21,v23);
						double[] a3 = vectorAngle(v31,v32);
						Direction[] t1 = vectorFromPoints(p1, p4);
						Direction[] t2 = vectorFromPoints(p2, p4);
						Direction[] t3 = vectorFromPoints(p3, p4);
						double[] h12 = vectorAngle(v12,t1); double[] h13 = vectorAngle(v13,t1);
						double[] h21 = vectorAngle(v21,t2); double[] h23 = vectorAngle(v23,t2);
						double[] h31 = vectorAngle(v31,t3); double[] h32 = vectorAngle(v32,t3);
						if((h12[0]<=a1[0])&&(h13[0]<=a1[0])&&(h21[0]<=a2[0])&&(h23[0]<=a2[0])&&(h31[0]<=a3[0])&&(h32[0]<=a3[0])) {
							k[n][m] = p4[0];
						}
					}
				}
			}
		}
		return k;
	}

	public static Position2[] planeTriangleIntersection(Plane[] vplane, Triangle[] vtri) {
		Position2[] k = null;
		return k;
	}
}
