package fi.jkauppa.javarenderengine;

public class MathLib {
	public static class Position {public double x,y,z; public Position(double xi,double yi,double zi){this.x=xi;this.y=yi;this.z=zi;}}
	public static class Direction {public double dx,dy,dz; public Direction(double dxi,double dyi,double dzi){this.dx=dxi;this.dy=dyi;this.dz=dzi;}}
	public static class Sphere {public double x,y,z,r; public Sphere(double xi,double yi,double zi,double ri){this.x=xi;this.y=yi;this.z=zi;this.r=ri;}}
	public static class Plane {public double a,b,c,d; public Plane(double ai,double bi,double ci,double di){this.a=ai;this.b=bi;this.c=ci;this.d=di;}}
	public static class Position2 {public Position pos1,pos2; public Position2(Position pos1i,Position pos2i){this.pos1=pos1i;this.pos2=pos2i;}}
	public static class Ray {public Position pos; public Direction dir; public Ray(Position posi,Direction diri){this.pos=posi;this.dir=diri;}}
	public static class Triangle {public Position pos1,pos2,pos3; public Triangle(Position pos1i,Position pos2i,Position pos3i){this.pos1=pos1i;this.pos2=pos2i;this.pos3=pos3i;}}
	
	public static Direction[] NormalizeVector(Direction[] vdir) {
		Direction[] k = null;
		if (vdir!=null) {
			k = new Direction[vdir.length];
			for (int n=0;n<vdir.length;n++) {
				double bottom = Math.sqrt(vdir[n].dx*vdir[n].dx+vdir[n].dy*vdir[n].dy+vdir[n].dz*vdir[n].dz);
				k[n] = new Direction(vdir[n].dx/bottom, vdir[n].dy/bottom, vdir[n].dz/bottom);
			}
		}
		return k;
	}
	
	public static double[][] RayPlaneDistance(Position vpos, Direction[] vdir, Plane[] vplane) {
		double[][] k = null;
		if ((vdir!=null)&&(vplane!=null)) {
			k = new double[vdir.length][vplane.length];
			for (int n=0;n<vdir.length;n++) {
				for (int m=0;m<vplane.length;m++) {
					double top = -(vpos.x*vplane[m].a+vpos.y*vplane[m].b+vpos.z*vplane[m].c+vplane[m].d);
					double bottom = vdir[n].dx*vplane[m].a+vdir[n].dy*vplane[m].b+vdir[n].dz*vplane[m].c;
					k[n][m] = top/bottom;
				}
			}
		}
		return k;
	}
}
