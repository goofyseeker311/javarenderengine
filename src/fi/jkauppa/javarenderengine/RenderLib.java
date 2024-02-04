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
import java.util.ArrayList;
import java.util.Arrays;

import fi.jkauppa.javarenderengine.ModelLib.Coordinate;
import fi.jkauppa.javarenderengine.ModelLib.Cubemap;
import fi.jkauppa.javarenderengine.ModelLib.Direction;
import fi.jkauppa.javarenderengine.ModelLib.Entity;
import fi.jkauppa.javarenderengine.ModelLib.Line;
import fi.jkauppa.javarenderengine.ModelLib.Material;
import fi.jkauppa.javarenderengine.ModelLib.Matrix;
import fi.jkauppa.javarenderengine.ModelLib.Plane;
import fi.jkauppa.javarenderengine.ModelLib.Position;
import fi.jkauppa.javarenderengine.ModelLib.RenderView;
import fi.jkauppa.javarenderengine.ModelLib.Sphere;
import fi.jkauppa.javarenderengine.ModelLib.Spheremap;
import fi.jkauppa.javarenderengine.ModelLib.Triangle;
import fi.jkauppa.javarenderengine.ModelLib.Sphere.SphereDistanceComparator;

public class RenderLib {
	private static GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment ();
	private static GraphicsDevice gd = ge.getDefaultScreenDevice ();
	private static GraphicsConfiguration gc = gd.getDefaultConfiguration ();

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

	public static RenderView renderProjectedPolygonViewHardware(Position campos, Entity[] entitylist, int renderwidth, double hfov, int renderheight, double vfov, Matrix viewrot, boolean unlit, int mouselocationx, int mouselocationy) {
		RenderView renderview = new RenderView();
		int vertexradius = 2;
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
		Graphics2D g2 = renderview.renderimage.createGraphics();
		g2.setComposite(AlphaComposite.Src);
		g2.setColor(new Color(0.0f,0.0f,0.0f,0.0f));
		g2.setPaint(null);
		g2.setClip(null);
		g2.fillRect(0, 0, renderwidth, renderheight);
		g2.setComposite(AlphaComposite.SrcOver);
		ArrayList<Triangle> mouseoverhittriangle = new ArrayList<Triangle>();
		if (entitylist!=null) {
			Direction[] camray = {renderview.dirs[0]};
			Sphere[] entityspherelist = new Sphere[entitylist.length]; 
			for (int k=0;k<entitylist.length;k++) {
				entityspherelist[k] = entitylist[k].sphereboundaryvolume;
				entityspherelist[k].ind = k;
			}
			SphereDistanceComparator distcomp = new SphereDistanceComparator(renderview.pos);
			Sphere[] sortedentityspherelist = Arrays.copyOf(entityspherelist, entityspherelist.length);
			Arrays.sort(sortedentityspherelist, distcomp);
			Rectangle[] sortedentityspherelistint = MathLib.projectedSphereIntersection(renderview.pos, sortedentityspherelist, renderwidth, renderheight, hfov, vfov, renderview.rot);
			for (int k=sortedentityspherelist.length-1;k>=0;k--) {
				if (sortedentityspherelistint[k]!=null) {
					Triangle[] copytrianglelist = entitylist[sortedentityspherelist[k].ind].trianglelist;
					if (copytrianglelist.length>0) {
						Direction[] trianglenormallist = new Direction[copytrianglelist.length];
						for (int i=0;i<copytrianglelist.length;i++) {
							trianglenormallist[i] = copytrianglelist[i].norm;
							if (copytrianglelist[i].norm.isZero()) {
								Triangle[] copyplanetriangle = {copytrianglelist[i]};
								Plane[] triangleplanes = MathLib.planeFromPoints(copyplanetriangle);
								Direction[] trianglenormal = MathLib.planeNormals(triangleplanes);
								trianglenormallist[i] = trianglenormal[0];
							}
						}
						Sphere[] copytrianglespherelist = MathLib.triangleCircumSphere(copytrianglelist);
						for (int i=0;i<copytrianglespherelist.length;i++) {copytrianglespherelist[i].ind = i;}
						Sphere[] sortedtrianglespherelist = Arrays.copyOf(copytrianglespherelist, copytrianglespherelist.length);
						Arrays.sort(sortedtrianglespherelist, distcomp);
						Rectangle[] copytrianglelistint = MathLib.projectedTrianglesIntersection(renderview.pos, copytrianglelist, renderwidth, renderheight, hfov, vfov, renderview.rot);
						Coordinate[][] copytrianglelistcoords = MathLib.projectedTriangles(renderview.pos, copytrianglelist, renderwidth, hfov, renderheight, vfov, viewrot);
						for (int i=sortedtrianglespherelist.length-1;i>=0;i--) {
							int it = sortedtrianglespherelist[i].ind;
							if (copytrianglelistint[it]!=null) {
								Triangle[] copytriangle = {copytrianglelist[it]};
								Material copymaterial = copytriangle[0].mat;
								Direction copytrianglenormal = trianglenormallist[it];
								Color trianglecolor = copymaterial.facecolor;
								float alphacolor = copymaterial.transparency;
								if (trianglecolor==null) {trianglecolor = Color.WHITE;}
								float[] trianglecolorcomp = trianglecolor.getRGBComponents(new float[4]);
								trianglecolor = new Color(trianglecolorcomp[0], trianglecolorcomp[1], trianglecolorcomp[2], alphacolor);
								trianglecolorcomp = trianglecolor.getRGBComponents(new float[4]);
								Color emissivecolor = copymaterial.emissivecolor;
								float[] emissivecolorcomp = null;
								if (emissivecolor!=null) {emissivecolorcomp = emissivecolor.getRGBComponents(new float[4]);}
								Color lightmapcolor = copymaterial.ambientcolor;
								if (lightmapcolor==null) {lightmapcolor = Color.BLACK;}
								float[] lightmapcolorcomp = lightmapcolor.getRGBComponents(new float[4]);
								double[] triangleviewangle = MathLib.vectorAngle(copytrianglenormal, camray);
								if ((copytriangle[0].norm.isZero())&&(triangleviewangle[0]<90.0f)) {
									triangleviewangle[0] = 180.0f - triangleviewangle[0];
								}
								triangleviewangle[0] -= 90.0f;
								if (triangleviewangle[0]<0.0f) {triangleviewangle[0] = 0.0f;}
								float shadingmultiplier = ((((float)triangleviewangle[0])/1.5f)+30.0f)/90.0f;
								if (!unlit) {
									trianglecolor = new Color(trianglecolorcomp[0]*shadingmultiplier, trianglecolorcomp[1]*shadingmultiplier, trianglecolorcomp[2]*shadingmultiplier, alphacolor);
								} else {
									float texr = trianglecolorcomp[0]*lightmapcolorcomp[0];
									float texg = trianglecolorcomp[1]*lightmapcolorcomp[1];
									float texb = trianglecolorcomp[2]*lightmapcolorcomp[2];
									if (emissivecolor!=null) {
										texr += emissivecolorcomp[0];
										texg += emissivecolorcomp[1];
										texb += emissivecolorcomp[2];
									}
									if (texr>1.0f) {texr=1.0f;}
									if (texg>1.0f) {texg=1.0f;}
									if (texb>1.0f) {texb=1.0f;}
									trianglecolor = new Color(texr, texg, texb, alphacolor);
								}
								Coordinate coord1 = copytrianglelistcoords[it][0];
								Coordinate coord2 = copytrianglelistcoords[it][1];
								Coordinate coord3 = copytrianglelistcoords[it][2];
								if ((coord1!=null)&&(coord2!=null)&&(coord3!=null)) {
									Polygon trianglepolygon = new Polygon();
									trianglepolygon.addPoint((int)Math.round(coord1.u), (int)Math.round(coord1.v));
									trianglepolygon.addPoint((int)Math.round(coord2.u), (int)Math.round(coord2.v));
									trianglepolygon.addPoint((int)Math.round(coord3.u), (int)Math.round(coord3.v));
									g2.setColor(trianglecolor);
									g2.fill(trianglepolygon);
									boolean mouseoverhit = g2.hit(new Rectangle(mouselocationx-vertexradius,mouselocationy-vertexradius,3,3), trianglepolygon, false);
									if (mouseoverhit) {
										mouseoverhittriangle.add(copytrianglelist[it]);
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

	public static RenderView renderCubemapPolygonViewHardware(Position campos, Entity[] entitylist, int renderwidth, int renderheight, int rendersize, Matrix viewrot, boolean unlit, int mouselocationx, int mouselocationy) {
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
		renderview.cubemap.topview = renderProjectedPolygonViewHardware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, topmatrix, renderview.unlit, mouselocationx, mouselocationy);
		renderview.cubemap.bottomview = renderProjectedPolygonViewHardware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, bottommatrix, renderview.unlit, mouselocationx, mouselocationy); 
		renderview.cubemap.forwardview = renderProjectedPolygonViewHardware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, forwardmatrix, renderview.unlit, mouselocationx, mouselocationy);
		renderview.cubemap.rightview = renderProjectedPolygonViewHardware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, rightmatrix, renderview.unlit, mouselocationx, mouselocationy);
		renderview.cubemap.backwardview = renderProjectedPolygonViewHardware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, backwardmatrix, renderview.unlit, mouselocationx, mouselocationy);
		renderview.cubemap.leftview = renderProjectedPolygonViewHardware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, leftmatrix, renderview.unlit, mouselocationx, mouselocationy);
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
			Rectangle[] sortedentityspherelistint = MathLib.projectedSphereIntersection(renderview.pos, sortedentityspherelist, renderwidth, renderheight, hfov, vfov, renderview.rot);
			for (int k=sortedentityspherelist.length-1;k>=0;k--) {
				if (sortedentityspherelistint[k]!=null) {
					Triangle[] copytrianglelist = entitylist[sortedentityspherelist[k].ind].trianglelist;
					if (copytrianglelist.length>0) {
						Direction[] trianglenormallist = new Direction[copytrianglelist.length];
						for (int i=0;i<copytrianglelist.length;i++) {
							trianglenormallist[i] = copytrianglelist[i].norm;
							if (copytrianglelist[i].norm.isZero()) {
								Triangle[] copyplanetriangle = {copytrianglelist[i]};
								Plane[] triangleplanes = MathLib.planeFromPoints(copyplanetriangle);
								Direction[] trianglenormal = MathLib.planeNormals(triangleplanes);
								trianglenormallist[i] = trianglenormal[0];
							}
						}
						Sphere[] copytrianglespherelist = MathLib.triangleCircumSphere(copytrianglelist);
						for (int i=0;i<copytrianglespherelist.length;i++) {copytrianglespherelist[i].ind = i;}
						Sphere[] sortedtrianglespherelist = Arrays.copyOf(copytrianglespherelist, copytrianglespherelist.length);
						Arrays.sort(sortedtrianglespherelist, distcomp);
						Rectangle[] copytrianglelistint = MathLib.projectedTrianglesIntersection(renderview.pos, copytrianglelist, renderwidth, renderheight, hfov, vfov, renderview.rot);
						for (int i=sortedtrianglespherelist.length-1;i>=0;i--) {
							int it = sortedtrianglespherelist[i].ind;
							if (copytrianglelistint[it]!=null) {
								Triangle[] copytriangle = {copytrianglelist[it]};
								int jstart = copytrianglelistint[it].x;
								int jend = copytrianglelistint[it].x+copytrianglelistint[it].width-1;
								Plane[] renderviewplanes = Arrays.copyOfRange(renderview.planes, jstart, jend+1);
								Line[][] vertplanetriangleint = MathLib.planeTriangleIntersection(renderviewplanes, copytriangle);
								Material copymaterial = copytriangle[0].mat;
								Direction copytrianglenormal = trianglenormallist[it];
								Color trianglecolor = copymaterial.facecolor;
								float alphacolor = copymaterial.transparency;
								if (trianglecolor==null) {trianglecolor = Color.WHITE;}
								float[] trianglecolorcomp = trianglecolor.getRGBComponents(new float[4]);
								trianglecolor = new Color(trianglecolorcomp[0], trianglecolorcomp[1], trianglecolorcomp[2], alphacolor);
								trianglecolorcomp = trianglecolor.getRGBComponents(new float[4]);
								VolatileImage triangletexture = copymaterial.fileimage;
								BufferedImage triangletextureimage = copymaterial.snapimage;
								if ((triangletexture!=null)&&(triangletextureimage==null)) {
									copymaterial.snapimage = copymaterial.fileimage.getSnapshot();
									triangletextureimage = copymaterial.snapimage;
								}
								Color emissivecolor = copymaterial.emissivecolor;
								float[] emissivecolorcomp = null;
								if (emissivecolor!=null) {emissivecolorcomp = emissivecolor.getRGBComponents(new float[4]);}
								VolatileImage emissivetexture = copymaterial.emissivefileimage;
								BufferedImage emissivetextureimage = copymaterial.emissivesnapimage;
								if ((emissivetexture!=null)&&(emissivetextureimage==null)) {
									copymaterial.emissivesnapimage = copymaterial.emissivefileimage.getSnapshot();
									emissivetextureimage = copymaterial.emissivesnapimage;
								}
								Color lightmapcolor = copymaterial.ambientcolor;
								if (lightmapcolor==null) {lightmapcolor = Color.BLACK;}
								float[] lightmapcolorcomp = lightmapcolor.getRGBComponents(new float[4]);
								VolatileImage lightmaptexture = copymaterial.ambientfileimage;
								BufferedImage lightmaptextureimage = copymaterial.ambientsnapimage;
								if ((lightmaptexture!=null)&&(lightmaptextureimage==null)) {
									copymaterial.ambientsnapimage = copymaterial.ambientfileimage.getSnapshot();
									lightmaptextureimage = copymaterial.ambientsnapimage;
								}
								for (int j=jstart;j<=jend;j++) {
									Line drawline = vertplanetriangleint[j-jstart][0];
									if (drawline!=null) {
										Position[] drawlinepoints = {drawline.pos1, drawline.pos2};
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
														if ((copytriangle[0].norm.isZero())&&(triangleviewangle[0]<90.0f)) {
															triangleviewangle[0] = 180.0f - triangleviewangle[0];
														}
														triangleviewangle[0] -= 90.0f;
														if (triangleviewangle[0]<0.0f) {triangleviewangle[0] = 0.0f;}
														float shadingmultiplier = ((((float)triangleviewangle[0])/1.5f)+30.0f)/90.0f;
														Coordinate tex1 = vpixelpoints[0].tex;
														Coordinate tex2 = vpixelpoints[1].tex;
														if ((tex1!=null)&&(tex2!=null)) {
															if (lightmaptexture!=null) {
																Position[] lineuvpoint1 = {new Position(tex1.u*(lightmaptexture.getWidth()-1),(1.0f-tex1.v)*(lightmaptexture.getHeight()-1),0.0f)};
																Position[] lineuvpoint2 = {new Position(tex2.u*(lightmaptexture.getWidth()-1),(1.0f-tex2.v)*(lightmaptexture.getHeight()-1),0.0f)};
																Direction[] vpixelpointdir12uv = MathLib.vectorFromPoints(lineuvpoint1, lineuvpoint2);
																Position[] lineuvpos = MathLib.translate(lineuvpoint1, vpixelpointdir12uv[0], vpixelpointlenfrac);
																int lineuvx = (int)Math.round(lineuvpos[0].x);
																int lineuvy = (int)Math.round(lineuvpos[0].y);
																if ((lineuvx>=0)&&(lineuvx<lightmaptexture.getWidth())&&(lineuvy>=0)&&(lineuvy<lightmaptexture.getHeight())) {
																	lightmapcolor = new Color(lightmaptextureimage.getRGB(lineuvx, lineuvy));
																	lightmapcolorcomp = lightmapcolor.getRGBComponents(new float[4]);
																}
															}
															if (emissivetexture!=null) {
																Position[] lineuvpoint1 = {new Position(tex1.u*(emissivetexture.getWidth()-1),(1.0f-tex1.v)*(emissivetexture.getHeight()-1),0.0f)};
																Position[] lineuvpoint2 = {new Position(tex2.u*(emissivetexture.getWidth()-1),(1.0f-tex2.v)*(emissivetexture.getHeight()-1),0.0f)};
																Direction[] vpixelpointdir12uv = MathLib.vectorFromPoints(lineuvpoint1, lineuvpoint2);
																Position[] lineuvpos = MathLib.translate(lineuvpoint1, vpixelpointdir12uv[0], vpixelpointlenfrac);
																int lineuvx = (int)Math.round(lineuvpos[0].x);
																int lineuvy = (int)Math.round(lineuvpos[0].y);
																if ((lineuvx>=0)&&(lineuvx<emissivetexture.getWidth())&&(lineuvy>=0)&&(lineuvy<emissivetexture.getHeight())) {
																	emissivecolor = new Color(emissivetextureimage.getRGB(lineuvx, lineuvy));
																	emissivecolorcomp = emissivecolor.getRGBComponents(new float[4]);
																}
															}
															if (triangletexture!=null) {
																Position[] lineuvpoint1 = {new Position(tex1.u*(triangletexture.getWidth()-1),(1.0f-tex1.v)*(triangletexture.getHeight()-1),0.0f)};
																Position[] lineuvpoint2 = {new Position(tex2.u*(triangletexture.getWidth()-1),(1.0f-tex2.v)*(triangletexture.getHeight()-1),0.0f)};
																Direction[] vpixelpointdir12uv = MathLib.vectorFromPoints(lineuvpoint1, lineuvpoint2);
																Position[] lineuvpos = MathLib.translate(lineuvpoint1, vpixelpointdir12uv[0], vpixelpointlenfrac);
																int lineuvx = (int)Math.round(lineuvpos[0].x);
																int lineuvy = (int)Math.round(lineuvpos[0].y);
																if ((lineuvx>=0)&&(lineuvx<triangletexture.getWidth())&&(lineuvy>=0)&&(lineuvy<triangletexture.getHeight())) {
																	renderview.cbuffer[n][j] = new Coordinate(lineuvx,lineuvy);
																	trianglecolor = new Color(triangletextureimage.getRGB(lineuvx, lineuvy));
																	trianglecolorcomp = trianglecolor.getRGBComponents(new float[4]);
																}
															}
														}
														if (!unlit) {
															trianglecolor = new Color(trianglecolorcomp[0]*shadingmultiplier, trianglecolorcomp[1]*shadingmultiplier, trianglecolorcomp[2]*shadingmultiplier, alphacolor);
														} else {
															float texr = trianglecolorcomp[0]*lightmapcolorcomp[0];
															float texg = trianglecolorcomp[1]*lightmapcolorcomp[1];
															float texb = trianglecolorcomp[2]*lightmapcolorcomp[2];
															if (emissivecolor!=null) {
																texr += emissivecolorcomp[0];
																texg += emissivecolorcomp[1];
																texb += emissivecolorcomp[2];
															}
															if (texr>1.0f) {texr=1.0f;}
															if (texg>1.0f) {texg=1.0f;}
															if (texb>1.0f) {texb=1.0f;}
															trianglecolor = new Color(texr, texg, texb, alphacolor);
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
		}
		renderview.mouseovertriangle = mouseoverhittriangle.toArray(new Triangle[mouseoverhittriangle.size()]);
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
			Rectangle[] sortedentityspherelistint = MathLib.spheremapSphereIntersection(renderview.pos, sortedentityspherelist, renderwidth, renderheight, renderview.rot);
			for (int k=sortedentityspherelist.length-1;k>=0;k--) {
				if (sortedentityspherelistint[k]!=null) {
					Triangle[] copytrianglelist = entitylist[sortedentityspherelist[k].ind].trianglelist;
					if (copytrianglelist.length>0) {
						Direction[] trianglenormallist = new Direction[copytrianglelist.length];
						for (int i=0;i<copytrianglelist.length;i++) {
							trianglenormallist[i] = copytrianglelist[i].norm;
							if (copytrianglelist[i].norm.isZero()) {
								Triangle[] copyplanetriangle = {copytrianglelist[i]};
								Plane[] triangleplanes = MathLib.planeFromPoints(copyplanetriangle);
								Direction[] trianglenormal = MathLib.planeNormals(triangleplanes);
								trianglenormallist[i] = trianglenormal[0];
							}
						}
						Sphere[] copytrianglespherelist = MathLib.triangleCircumSphere(copytrianglelist);
						for (int i=0;i<copytrianglespherelist.length;i++) {copytrianglespherelist[i].ind = i;}
						Sphere[] sortedtrianglespherelist = Arrays.copyOf(copytrianglespherelist, copytrianglespherelist.length);
						Arrays.sort(sortedtrianglespherelist, distcomp);
						Rectangle[] copytrianglelistint = MathLib.spheremapTrianglesIntersection(renderview.pos, copytrianglelist, renderwidth, renderheight, renderview.rot);
						for (int i=sortedtrianglespherelist.length-1;i>=0;i--) {
							int it = sortedtrianglespherelist[i].ind;
							if (copytrianglelistint[it]!=null) {
								Triangle[] copytriangle = {copytrianglelist[it]};
								int jstart = copytrianglelistint[it].x;
								int jend = copytrianglelistint[it].x+copytrianglelistint[it].width-1;
								Plane[] renderviewplanes = Arrays.copyOfRange(renderview.planes, jstart, jend+1);
								Line[][] vertplanetriangleint = MathLib.planeTriangleIntersection(renderviewplanes, copytriangle);
								Material copymaterial = copytriangle[0].mat;
								Direction copytrianglenormal = trianglenormallist[it];
								Color trianglecolor = copymaterial.facecolor;
								float alphacolor = copymaterial.transparency;
								if (trianglecolor==null) {trianglecolor = Color.WHITE;}
								float[] trianglecolorcomp = trianglecolor.getRGBComponents(new float[4]);
								trianglecolor = new Color(trianglecolorcomp[0], trianglecolorcomp[1], trianglecolorcomp[2], alphacolor);
								trianglecolorcomp = trianglecolor.getRGBComponents(new float[4]);
								VolatileImage triangletexture = copymaterial.fileimage;
								BufferedImage triangletextureimage = copymaterial.snapimage;
								if ((triangletexture!=null)&&(triangletextureimage==null)) {
									copymaterial.snapimage = copymaterial.fileimage.getSnapshot();
									triangletextureimage = copymaterial.snapimage;
								}
								Color emissivecolor = copymaterial.emissivecolor;
								float[] emissivecolorcomp = null;
								if (emissivecolor!=null) {emissivecolorcomp = emissivecolor.getRGBComponents(new float[4]);}
								VolatileImage emissivetexture = copymaterial.emissivefileimage;
								BufferedImage emissivetextureimage = copymaterial.emissivesnapimage;
								if ((emissivetexture!=null)&&(emissivetextureimage==null)) {
									copymaterial.emissivesnapimage = copymaterial.emissivefileimage.getSnapshot();
									emissivetextureimage = copymaterial.emissivesnapimage;
								}
								Color lightmapcolor = copymaterial.ambientcolor;
								if (lightmapcolor==null) {lightmapcolor = Color.BLACK;}
								float[] lightmapcolorcomp = lightmapcolor.getRGBComponents(new float[4]);
								VolatileImage lightmaptexture = copymaterial.ambientfileimage;
								BufferedImage lightmaptextureimage = copymaterial.ambientsnapimage;
								if ((lightmaptexture!=null)&&(lightmaptextureimage==null)) {
									copymaterial.ambientsnapimage = copymaterial.ambientfileimage.getSnapshot();
									lightmaptextureimage = copymaterial.ambientsnapimage;
								}
								for (int j=jstart;j<=jend;j++) {
									Line drawline = vertplanetriangleint[j-jstart][0];
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
														if ((copytriangle[0].norm.isZero())&&(triangleviewangle[0]<90.0f)) {
															triangleviewangle[0] = 180.0f - triangleviewangle[0];
														}
														triangleviewangle[0] -= 90.0f;
														if (triangleviewangle[0]<0.0f) {triangleviewangle[0] = 0.0f;}
														float shadingmultiplier = ((((float)triangleviewangle[0])/1.5f)+30.0f)/90.0f;
														Coordinate tex1 = vpixelpoints[0].tex;
														Coordinate tex2 = vpixelpoints[1].tex;
														if ((tex1!=null)&&(tex2!=null)) {
															if (lightmaptexture!=null) {
																Position[] lineuvpoint1 = {new Position(tex1.u*(lightmaptexture.getWidth()-1),(1.0f-tex1.v)*(lightmaptexture.getHeight()-1),0.0f)};
																Position[] lineuvpoint2 = {new Position(tex2.u*(lightmaptexture.getWidth()-1),(1.0f-tex2.v)*(lightmaptexture.getHeight()-1),0.0f)};
																Direction[] vpixelpointdir12uv = MathLib.vectorFromPoints(lineuvpoint1, lineuvpoint2);
																Position[] lineuvpos = MathLib.translate(lineuvpoint1, vpixelpointdir12uv[0], vpixelpointlenfrac);
																int lineuvx = (int)Math.round(lineuvpos[0].x);
																int lineuvy = (int)Math.round(lineuvpos[0].y);
																if ((lineuvx>=0)&&(lineuvx<lightmaptexture.getWidth())&&(lineuvy>=0)&&(lineuvy<lightmaptexture.getHeight())) {
																	lightmapcolor = new Color(lightmaptextureimage.getRGB(lineuvx, lineuvy));
																	lightmapcolorcomp = lightmapcolor.getRGBComponents(new float[4]);
																}
															}
															if (emissivetexture!=null) {
																Position[] lineuvpoint1 = {new Position(tex1.u*(emissivetexture.getWidth()-1),(1.0f-tex1.v)*(emissivetexture.getHeight()-1),0.0f)};
																Position[] lineuvpoint2 = {new Position(tex2.u*(emissivetexture.getWidth()-1),(1.0f-tex2.v)*(emissivetexture.getHeight()-1),0.0f)};
																Direction[] vpixelpointdir12uv = MathLib.vectorFromPoints(lineuvpoint1, lineuvpoint2);
																Position[] lineuvpos = MathLib.translate(lineuvpoint1, vpixelpointdir12uv[0], vpixelpointlenfrac);
																int lineuvx = (int)Math.round(lineuvpos[0].x);
																int lineuvy = (int)Math.round(lineuvpos[0].y);
																if ((lineuvx>=0)&&(lineuvx<emissivetexture.getWidth())&&(lineuvy>=0)&&(lineuvy<emissivetexture.getHeight())) {
																	emissivecolor = new Color(emissivetextureimage.getRGB(lineuvx, lineuvy));
																	emissivecolorcomp = emissivecolor.getRGBComponents(new float[4]);
																}
															}
															if (triangletexture!=null) {
																Position[] lineuvpoint1 = {new Position(tex1.u*(triangletexture.getWidth()-1),(1.0f-tex1.v)*(triangletexture.getHeight()-1),0.0f)};
																Position[] lineuvpoint2 = {new Position(tex2.u*(triangletexture.getWidth()-1),(1.0f-tex2.v)*(triangletexture.getHeight()-1),0.0f)};
																Direction[] vpixelpointdir12uv = MathLib.vectorFromPoints(lineuvpoint1, lineuvpoint2);
																Position[] lineuvpos = MathLib.translate(lineuvpoint1, vpixelpointdir12uv[0], vpixelpointlenfrac);
																int lineuvx = (int)Math.round(lineuvpos[0].x);
																int lineuvy = (int)Math.round(lineuvpos[0].y);
																if ((lineuvx>=0)&&(lineuvx<triangletexture.getWidth())&&(lineuvy>=0)&&(lineuvy<triangletexture.getHeight())) {
																	renderview.cbuffer[n][j] = new Coordinate(lineuvx,lineuvy);
																	trianglecolor = new Color(triangletextureimage.getRGB(lineuvx, lineuvy));
																	trianglecolorcomp = trianglecolor.getRGBComponents(new float[4]);
																}
															}
														}
														if (!unlit) {
															trianglecolor = new Color(trianglecolorcomp[0]*shadingmultiplier, trianglecolorcomp[1]*shadingmultiplier, trianglecolorcomp[2]*shadingmultiplier, alphacolor);
														} else {
															float texr = trianglecolorcomp[0]*lightmapcolorcomp[0];
															float texg = trianglecolorcomp[1]*lightmapcolorcomp[1];
															float texb = trianglecolorcomp[2]*lightmapcolorcomp[2];
															if (emissivecolor!=null) {
																texr += emissivecolorcomp[0];
																texg += emissivecolorcomp[1];
																texb += emissivecolorcomp[2];
															}
															if (texr>1.0f) {texr=1.0f;}
															if (texg>1.0f) {texg=1.0f;}
															if (texb>1.0f) {texb=1.0f;}
															trianglecolor = new Color(texr, texg, texb, alphacolor);
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
		renderview.cubemap.topview = renderProjectedPlaneViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, topmatrix, renderview.unlit, mouselocationx, mouselocationy);
		renderview.cubemap.bottomview = renderProjectedPlaneViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, bottommatrix, renderview.unlit, mouselocationx, mouselocationy); 
		renderview.cubemap.forwardview = renderProjectedPlaneViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, forwardmatrix, renderview.unlit, mouselocationx, mouselocationy);
		renderview.cubemap.rightview = renderProjectedPlaneViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, rightmatrix, renderview.unlit, mouselocationx, mouselocationy);
		renderview.cubemap.backwardview = renderProjectedPlaneViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, backwardmatrix, renderview.unlit, mouselocationx, mouselocationy);
		renderview.cubemap.leftview = renderProjectedPlaneViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, leftmatrix, renderview.unlit, mouselocationx, mouselocationy);
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
			Rectangle[] sortedentityspherelistint = MathLib.projectedSphereIntersection(renderview.pos, sortedentityspherelist, renderwidth, renderheight, hfov, vfov, renderview.rot);
			for (int k=sortedentityspherelist.length-1;k>=0;k--) {
				if (sortedentityspherelistint[k]!=null) {
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
								trianglenormallist[i] = trianglenormal[0];
							}
						}
						Sphere[] copytrianglespherelist = MathLib.triangleCircumSphere(copytrianglelist);
						for (int i=0;i<copytrianglespherelist.length;i++) {copytrianglespherelist[i].ind = i;}
						Sphere[] sortedtrianglespherelist = Arrays.copyOf(copytrianglespherelist, copytrianglespherelist.length);
						Arrays.sort(sortedtrianglespherelist, distcomp);
						Rectangle[] copytrianglelistint = MathLib.projectedTrianglesIntersection(renderview.pos, copytrianglelist, renderwidth, renderheight, hfov, vfov, renderview.rot);
						for (int n=sortedtrianglespherelist.length-1;n>=0;n--) {
							int it = sortedtrianglespherelist[n].ind;
							if (copytrianglelistint[it]!=null) {
								Triangle[] copytriangle = {copytrianglelist[it]};
								Material copymaterial = copytriangle[0].mat;
								Direction copytrianglenormal = trianglenormallist[it];
								Color trianglecolor = copymaterial.facecolor;
								float alphacolor = copymaterial.transparency;
								if (trianglecolor==null) {trianglecolor = Color.WHITE;}
								float[] trianglecolorcomp = trianglecolor.getRGBComponents(new float[4]);
								trianglecolor = new Color(trianglecolorcomp[0], trianglecolorcomp[1], trianglecolorcomp[2], alphacolor);
								trianglecolorcomp = trianglecolor.getRGBComponents(new float[4]);
								VolatileImage triangletexture = copymaterial.fileimage;
								BufferedImage triangletextureimage = copymaterial.snapimage;
								if ((triangletexture!=null)&&(triangletextureimage==null)) {
									copymaterial.snapimage = copymaterial.fileimage.getSnapshot();
									triangletextureimage = copymaterial.snapimage;
								}
								Color emissivecolor = copymaterial.emissivecolor;
								float[] emissivecolorcomp = null;
								if (emissivecolor!=null) {emissivecolorcomp = emissivecolor.getRGBComponents(new float[4]);}
								VolatileImage emissivetexture = copymaterial.emissivefileimage;
								BufferedImage emissivetextureimage = copymaterial.emissivesnapimage;
								if ((emissivetexture!=null)&&(emissivetextureimage==null)) {
									copymaterial.emissivesnapimage = copymaterial.emissivefileimage.getSnapshot();
									emissivetextureimage = copymaterial.emissivesnapimage;
								}
								Color lightmapcolor = copymaterial.ambientcolor;
								if (lightmapcolor==null) {lightmapcolor = Color.BLACK;}
								float[] lightmapcolorcomp = lightmapcolor.getRGBComponents(new float[4]);
								VolatileImage lightmaptexture = copymaterial.ambientfileimage;
								BufferedImage lightmaptextureimage = copymaterial.ambientsnapimage;
								if ((lightmaptexture!=null)&&(lightmaptextureimage==null)) {
									copymaterial.ambientsnapimage = copymaterial.ambientfileimage.getSnapshot();
									lightmaptextureimage = copymaterial.ambientsnapimage;
								}
								int jstart = copytrianglelistint[it].y;
								int jend = copytrianglelistint[it].y+copytrianglelistint[it].height-1;
								int istart = copytrianglelistint[it].x;
								int iend = copytrianglelistint[it].x+copytrianglelistint[it].width-1;
								for (int j=jstart;j<=jend;j++) {
									for (int i=istart;i<=iend;i++) {
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
												if ((copytriangle[0].norm.isZero())&&(triangleviewangle[0]<90.0f)) {
													triangleviewangle[0] = 180.0f - triangleviewangle[0];
												}
												triangleviewangle[0] -= 90.0f;
												if (triangleviewangle[0]<0.0f) {triangleviewangle[0] = 0.0f;}
												float shadingmultiplier = ((((float)triangleviewangle[0])/1.5f)+30.0f)/90.0f;
												Coordinate tex = camrayint[0][0].tex;
												if (tex!=null) {
													if (lightmaptexture!=null) {
														Position[] lineuvpoint = {new Position(tex.u*(lightmaptexture.getWidth()-1),(1.0f-tex.v)*(lightmaptexture.getHeight()-1),0.0f)};
														int lineuvx = (int)Math.round(lineuvpoint[0].x);
														int lineuvy = (int)Math.round(lineuvpoint[0].y);
														if ((lineuvx>=0)&&(lineuvx<lightmaptexture.getWidth())&&(lineuvy>=0)&&(lineuvy<lightmaptexture.getHeight())) {
															lightmapcolor = new Color(lightmaptextureimage.getRGB(lineuvx, lineuvy));
															lightmapcolorcomp = lightmapcolor.getRGBComponents(new float[4]);
														}
													}
													if (emissivetexture!=null) {
														Position[] lineuvpoint = {new Position(tex.u*(emissivetexture.getWidth()-1),(1.0f-tex.v)*(emissivetexture.getHeight()-1),0.0f)};
														int lineuvx = (int)Math.round(lineuvpoint[0].x);
														int lineuvy = (int)Math.round(lineuvpoint[0].y);
														if ((lineuvx>=0)&&(lineuvx<emissivetexture.getWidth())&&(lineuvy>=0)&&(lineuvy<emissivetexture.getHeight())) {
															emissivecolor = new Color(emissivetextureimage.getRGB(lineuvx, lineuvy));
															emissivecolorcomp = emissivecolor.getRGBComponents(new float[4]);
														}
													}
													if (triangletexture!=null) {
														Position[] lineuvpoint = {new Position(tex.u*(triangletexture.getWidth()-1),(1.0f-tex.v)*(triangletexture.getHeight()-1),0.0f)};
														int lineuvx = (int)Math.round(lineuvpoint[0].x);
														int lineuvy = (int)Math.round(lineuvpoint[0].y);
														if ((lineuvx>=0)&&(lineuvx<triangletexture.getWidth())&&(lineuvy>=0)&&(lineuvy<triangletexture.getHeight())) {
															renderview.cbuffer[n][j] = new Coordinate(lineuvx,lineuvy);
															trianglecolor = new Color(triangletextureimage.getRGB(lineuvx, lineuvy));
															trianglecolorcomp = trianglecolor.getRGBComponents(new float[4]);
														}
													}
												}
												if (!unlit) {
													trianglecolor = new Color(trianglecolorcomp[0]*shadingmultiplier, trianglecolorcomp[1]*shadingmultiplier, trianglecolorcomp[2]*shadingmultiplier, alphacolor);
												} else {
													float texr = trianglecolorcomp[0]*lightmapcolorcomp[0];
													float texg = trianglecolorcomp[1]*lightmapcolorcomp[1];
													float texb = trianglecolorcomp[2]*lightmapcolorcomp[2];
													if (emissivecolor!=null) {
														texr += emissivecolorcomp[0];
														texg += emissivecolorcomp[1];
														texb += emissivecolorcomp[2];
													}
													if (texr>1.0f) {texr=1.0f;}
													if (texg>1.0f) {texg=1.0f;}
													if (texb>1.0f) {texb=1.0f;}
													trianglecolor = new Color(texr, texg, texb, alphacolor);
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
		}
		renderview.mouseovertriangle = mouseoverhittriangle.toArray(new Triangle[mouseoverhittriangle.size()]);
		renderview.snapimage = renderview.renderimage.getSnapshot();
		return renderview;
	}

	public static RenderView renderSpheremapRayViewSoftware(Position campos, Entity[] entitylist, int renderwidth, int renderheight, Matrix viewrot, boolean unlit, int mouselocationx, int mouselocationy) {
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
		renderview.rays = MathLib.spheremapRays(renderwidth, renderheight, viewrot);
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
			Rectangle[] sortedentityspherelistint = MathLib.spheremapSphereIntersection(renderview.pos, sortedentityspherelist, renderwidth, renderheight, renderview.rot);
			for (int k=sortedentityspherelist.length-1;k>=0;k--) {
				if (sortedentityspherelistint[k]!=null) {
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
								trianglenormallist[i] = trianglenormal[0];
							}
						}
						Sphere[] copytrianglespherelist = MathLib.triangleCircumSphere(copytrianglelist);
						for (int i=0;i<copytrianglespherelist.length;i++) {copytrianglespherelist[i].ind = i;}
						Sphere[] sortedtrianglespherelist = Arrays.copyOf(copytrianglespherelist, copytrianglespherelist.length);
						Arrays.sort(sortedtrianglespherelist, distcomp);
						Rectangle[] copytrianglelistint = MathLib.spheremapTrianglesIntersection(renderview.pos, copytrianglelist, renderwidth, renderheight, renderview.rot);
						for (int n=sortedtrianglespherelist.length-1;n>=0;n--) {
							int it = sortedtrianglespherelist[n].ind;
							if (copytrianglelistint[it]!=null) {
								Triangle[] copytriangle = {copytrianglelist[it]};
								Material copymaterial = copytriangle[0].mat;
								Direction copytrianglenormal = trianglenormallist[it];
								Color trianglecolor = copymaterial.facecolor;
								float alphacolor = copymaterial.transparency;
								if (trianglecolor==null) {trianglecolor = Color.WHITE;}
								float[] trianglecolorcomp = trianglecolor.getRGBComponents(new float[4]);
								trianglecolor = new Color(trianglecolorcomp[0], trianglecolorcomp[1], trianglecolorcomp[2], alphacolor);
								trianglecolorcomp = trianglecolor.getRGBComponents(new float[4]);
								VolatileImage triangletexture = copymaterial.fileimage;
								BufferedImage triangletextureimage = copymaterial.snapimage;
								if ((triangletexture!=null)&&(triangletextureimage==null)) {
									copymaterial.snapimage = copymaterial.fileimage.getSnapshot();
									triangletextureimage = copymaterial.snapimage;
								}
								Color emissivecolor = copymaterial.emissivecolor;
								float[] emissivecolorcomp = null;
								if (emissivecolor!=null) {emissivecolorcomp = emissivecolor.getRGBComponents(new float[4]);}
								VolatileImage emissivetexture = copymaterial.emissivefileimage;
								BufferedImage emissivetextureimage = copymaterial.emissivesnapimage;
								if ((emissivetexture!=null)&&(emissivetextureimage==null)) {
									copymaterial.emissivesnapimage = copymaterial.emissivefileimage.getSnapshot();
									emissivetextureimage = copymaterial.emissivesnapimage;
								}
								Color lightmapcolor = copymaterial.ambientcolor;
								if (lightmapcolor==null) {lightmapcolor = Color.BLACK;}
								float[] lightmapcolorcomp = lightmapcolor.getRGBComponents(new float[4]);
								VolatileImage lightmaptexture = copymaterial.ambientfileimage;
								BufferedImage lightmaptextureimage = copymaterial.ambientsnapimage;
								if ((lightmaptexture!=null)&&(lightmaptextureimage==null)) {
									copymaterial.ambientsnapimage = copymaterial.ambientfileimage.getSnapshot();
									lightmaptextureimage = copymaterial.ambientsnapimage;
								}
								int jstart = copytrianglelistint[it].y;
								int jend = copytrianglelistint[it].y+copytrianglelistint[it].height-1;
								int istart = copytrianglelistint[it].x;
								int iend = copytrianglelistint[it].x+copytrianglelistint[it].width-1;
								for (int j=jstart;j<=jend;j++) {
									for (int i=istart;i<=iend;i++) {
										Direction[] camray = {renderview.rays[i][j]};
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
												if ((copytriangle[0].norm.isZero())&&(triangleviewangle[0]<90.0f)) {
													triangleviewangle[0] = 180.0f - triangleviewangle[0];
												}
												triangleviewangle[0] -= 90.0f;
												if (triangleviewangle[0]<0.0f) {triangleviewangle[0] = 0.0f;}
												float shadingmultiplier = ((((float)triangleviewangle[0])/1.5f)+30.0f)/90.0f;
												Coordinate tex = camrayint[0][0].tex;
												if (tex!=null) {
													if (lightmaptexture!=null) {
														Position[] lineuvpoint = {new Position(tex.u*(lightmaptexture.getWidth()-1),(1.0f-tex.v)*(lightmaptexture.getHeight()-1),0.0f)};
														int lineuvx = (int)Math.round(lineuvpoint[0].x);
														int lineuvy = (int)Math.round(lineuvpoint[0].y);
														if ((lineuvx>=0)&&(lineuvx<lightmaptexture.getWidth())&&(lineuvy>=0)&&(lineuvy<lightmaptexture.getHeight())) {
															lightmapcolor = new Color(lightmaptextureimage.getRGB(lineuvx, lineuvy));
															lightmapcolorcomp = lightmapcolor.getRGBComponents(new float[4]);
														}
													}
													if (emissivetexture!=null) {
														Position[] lineuvpoint = {new Position(tex.u*(emissivetexture.getWidth()-1),(1.0f-tex.v)*(emissivetexture.getHeight()-1),0.0f)};
														int lineuvx = (int)Math.round(lineuvpoint[0].x);
														int lineuvy = (int)Math.round(lineuvpoint[0].y);
														if ((lineuvx>=0)&&(lineuvx<emissivetexture.getWidth())&&(lineuvy>=0)&&(lineuvy<emissivetexture.getHeight())) {
															emissivecolor = new Color(emissivetextureimage.getRGB(lineuvx, lineuvy));
															emissivecolorcomp = emissivecolor.getRGBComponents(new float[4]);
														}
													}
													if (triangletexture!=null) {
														Position[] lineuvpoint = {new Position(tex.u*(triangletexture.getWidth()-1),(1.0f-tex.v)*(triangletexture.getHeight()-1),0.0f)};
														int lineuvx = (int)Math.round(lineuvpoint[0].x);
														int lineuvy = (int)Math.round(lineuvpoint[0].y);
														if ((lineuvx>=0)&&(lineuvx<triangletexture.getWidth())&&(lineuvy>=0)&&(lineuvy<triangletexture.getHeight())) {
															renderview.cbuffer[n][j] = new Coordinate(lineuvx,lineuvy);
															trianglecolor = new Color(triangletextureimage.getRGB(lineuvx, lineuvy));
															trianglecolorcomp = trianglecolor.getRGBComponents(new float[4]);
														}
													}
												}
												if (!unlit) {
													trianglecolor = new Color(trianglecolorcomp[0]*shadingmultiplier, trianglecolorcomp[1]*shadingmultiplier, trianglecolorcomp[2]*shadingmultiplier, alphacolor);
												} else {
													float texr = trianglecolorcomp[0]*lightmapcolorcomp[0];
													float texg = trianglecolorcomp[1]*lightmapcolorcomp[1];
													float texb = trianglecolorcomp[2]*lightmapcolorcomp[2];
													if (emissivecolor!=null) {
														texr += emissivecolorcomp[0];
														texg += emissivecolorcomp[1];
														texb += emissivecolorcomp[2];
													}
													if (texr>1.0f) {texr=1.0f;}
													if (texg>1.0f) {texg=1.0f;}
													if (texb>1.0f) {texb=1.0f;}
													trianglecolor = new Color(texr, texg, texb, alphacolor);
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
		}
		renderview.mouseovertriangle = mouseoverhittriangle.toArray(new Triangle[mouseoverhittriangle.size()]);
		renderview.snapimage = renderview.renderimage.getSnapshot();
		return renderview;
	}

	public static RenderView renderCubemapRayViewSoftware(Position campos, Entity[] entitylist, int renderwidth, int renderheight, int rendersize, Matrix viewrot, boolean unlit, int mouselocationx, int mouselocationy) {
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
		renderview.cubemap.topview = renderProjectedRayViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, topmatrix, renderview.unlit, mouselocationx, mouselocationy);
		renderview.cubemap.bottomview = renderProjectedRayViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, bottommatrix, renderview.unlit, mouselocationx, mouselocationy); 
		renderview.cubemap.forwardview = renderProjectedRayViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, forwardmatrix, renderview.unlit, mouselocationx, mouselocationy);
		renderview.cubemap.rightview = renderProjectedRayViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, rightmatrix, renderview.unlit, mouselocationx, mouselocationy);
		renderview.cubemap.backwardview = renderProjectedRayViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, backwardmatrix, renderview.unlit, mouselocationx, mouselocationy);
		renderview.cubemap.leftview = renderProjectedRayViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, leftmatrix, renderview.unlit, mouselocationx, mouselocationy);
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

	public static void renderSurfaceFaceCubemapPlaneViewHardware(Entity[] entitylist, int rendersize) {
		float multiplier = 10000.0f;
		if (entitylist!=null) {
			for (int j=0;j<entitylist.length;j++) {
				if (entitylist[j]!=null) {
					if (entitylist[j].trianglelist!=null) {
						Sphere[] trianglespherelist = MathLib.triangleCircumSphere(entitylist[j].trianglelist);
						for (int i=0;i<entitylist[j].trianglelist.length;i++) {
							if (entitylist[j].trianglelist[i]!=null) {
								Sphere[] trianglesphere = {trianglespherelist[i]};
								Position[] trianglespherepoint = MathLib.sphereVertexList(trianglesphere);
								RenderView p4pixelview = renderCubemapPolygonViewHardware(trianglespherepoint[0], entitylist, rendersize*3, rendersize*2, rendersize, MathLib.rotationMatrix(0, 0, 0), true, 0, 0);
								BufferedImage[] cubemapimages = new BufferedImage[6];
								cubemapimages[0] = p4pixelview.cubemap.backwardview.snapimage;
								cubemapimages[1] = p4pixelview.cubemap.bottomview.snapimage;
								cubemapimages[2] = p4pixelview.cubemap.forwardview.snapimage;
								cubemapimages[3] = p4pixelview.cubemap.leftview.snapimage;
								cubemapimages[4] = p4pixelview.cubemap.rightview.snapimage;
								cubemapimages[5] = p4pixelview.cubemap.topview.snapimage;
								float p4pixelr = 0.0f;
								float p4pixelg = 0.0f;
								float p4pixelb = 0.0f;
								for (int k=0;k<cubemapimages.length;k++) {
									for (int ky=0;ky<cubemapimages[k].getHeight();ky++) {
										for (int kx=0;kx<cubemapimages[k].getWidth();kx++) {
											Color p4pixelcolor = new Color(cubemapimages[k].getRGB(kx, ky));
											float[] p4pixelcolorcomp = p4pixelcolor.getRGBComponents(new float[4]);
											p4pixelr += p4pixelcolorcomp[0];
											p4pixelg += p4pixelcolorcomp[1];
											p4pixelb += p4pixelcolorcomp[2];
										}
									}
								}
								float pixelcount = 6*rendersize*rendersize;
								float p4pixelrt = multiplier*p4pixelr/pixelcount;
								float p4pixelgt = multiplier*p4pixelg/pixelcount;
								float p4pixelbt = multiplier*p4pixelb/pixelcount;
								if (p4pixelrt>1.0f) {p4pixelrt=1.0f;}
								if (p4pixelgt>1.0f) {p4pixelgt=1.0f;}
								if (p4pixelbt>1.0f) {p4pixelbt=1.0f;}
								Color p4pixelcolor = new Color(p4pixelrt, p4pixelgt, p4pixelbt, 1.0f);
								System.out.println("entitylist["+j+"]="+trianglespherepoint[0].x+","+trianglespherepoint[0].y+","+trianglespherepoint[0].z);
								Material[] newlmatl = {new Material(p4pixelcolor, 1.0f, null)};
								entitylist[j].trianglelist[i].lmatl = newlmatl;
							}
						}
					}
				}
			}
		}
		if (entitylist!=null) {
			for (int j=0;j<entitylist.length;j++) {
				if (entitylist[j]!=null) {
					if (entitylist[j].trianglelist!=null) {
						for (int i=0;i<entitylist[j].trianglelist.length;i++) {
							if (entitylist[j].trianglelist[i]!=null) {
								if ((entitylist[j].trianglelist[i].mat!=null)&&(entitylist[j].trianglelist[i].lmatl!=null)) {
									entitylist[j].trianglelist[i].mat = entitylist[j].trianglelist[i].mat.copy();
									entitylist[j].trianglelist[i].mat.ambientcolor = entitylist[j].trianglelist[i].lmatl[0].facecolor;
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static void renderSurfaceFaceCubemapPlaneViewSoftware(Entity[] entitylist, int rendersize) {
		float multiplier = 10000.0f;
		if (entitylist!=null) {
			for (int j=0;j<entitylist.length;j++) {
				if (entitylist[j]!=null) {
					if (entitylist[j].trianglelist!=null) {
						Sphere[] trianglespherelist = MathLib.triangleCircumSphere(entitylist[j].trianglelist);
						for (int i=0;i<entitylist[j].trianglelist.length;i++) {
							if (entitylist[j].trianglelist[i]!=null) {
								Sphere[] trianglesphere = {trianglespherelist[i]};
								Position[] trianglespherepoint = MathLib.sphereVertexList(trianglesphere);
								RenderView p4pixelview = renderCubemapPlaneViewSoftware(trianglespherepoint[0], entitylist, rendersize*3, rendersize*2, rendersize, MathLib.rotationMatrix(0, 0, 0), true, 0, 0);
								BufferedImage[] cubemapimages = new BufferedImage[6];
								cubemapimages[0] = p4pixelview.cubemap.backwardview.snapimage;
								cubemapimages[1] = p4pixelview.cubemap.bottomview.snapimage;
								cubemapimages[2] = p4pixelview.cubemap.forwardview.snapimage;
								cubemapimages[3] = p4pixelview.cubemap.leftview.snapimage;
								cubemapimages[4] = p4pixelview.cubemap.rightview.snapimage;
								cubemapimages[5] = p4pixelview.cubemap.topview.snapimage;
								float p4pixelr = 0.0f;
								float p4pixelg = 0.0f;
								float p4pixelb = 0.0f;
								for (int k=0;k<cubemapimages.length;k++) {
									for (int ky=0;ky<cubemapimages[k].getHeight();ky++) {
										for (int kx=0;kx<cubemapimages[k].getWidth();kx++) {
											Color p4pixelcolor = new Color(cubemapimages[k].getRGB(kx, ky));
											float[] p4pixelcolorcomp = p4pixelcolor.getRGBComponents(new float[4]);
											p4pixelr += p4pixelcolorcomp[0];
											p4pixelg += p4pixelcolorcomp[1];
											p4pixelb += p4pixelcolorcomp[2];
										}
									}
								}
								float pixelcount = 6*rendersize*rendersize;
								float p4pixelrt = multiplier*p4pixelr/pixelcount;
								float p4pixelgt = multiplier*p4pixelg/pixelcount;
								float p4pixelbt = multiplier*p4pixelb/pixelcount;
								if (p4pixelrt>1.0f) {p4pixelrt=1.0f;}
								if (p4pixelgt>1.0f) {p4pixelgt=1.0f;}
								if (p4pixelbt>1.0f) {p4pixelbt=1.0f;}
								Color p4pixelcolor = new Color(p4pixelrt, p4pixelgt, p4pixelbt, 1.0f);
								System.out.println("entitylist["+j+"]="+trianglespherepoint[0].x+","+trianglespherepoint[0].y+","+trianglespherepoint[0].z);
								Material[] newlmatl = {new Material(p4pixelcolor, 1.0f, null)};
								entitylist[j].trianglelist[i].lmatl = newlmatl;
							}
						}
					}
				}
			}
		}
		if (entitylist!=null) {
			for (int j=0;j<entitylist.length;j++) {
				if (entitylist[j]!=null) {
					if (entitylist[j].trianglelist!=null) {
						for (int i=0;i<entitylist[j].trianglelist.length;i++) {
							if (entitylist[j].trianglelist[i]!=null) {
								if ((entitylist[j].trianglelist[i].mat!=null)&&(entitylist[j].trianglelist[i].lmatl!=null)) {
									entitylist[j].trianglelist[i].mat = entitylist[j].trianglelist[i].mat.copy();
									entitylist[j].trianglelist[i].mat.ambientcolor = entitylist[j].trianglelist[i].lmatl[0].facecolor;
								}
							}
						}
					}
				}
			}
		}
	}

	public static void renderSurfacePixelCubemapPlaneViewSoftware(Entity[] entitylist, int rendersize, int texturesize) {
		float multiplier = 10000.0f;
		if (entitylist!=null) {
			for (int j=0;j<entitylist.length;j++) {
				if (entitylist[j]!=null) {
					if (entitylist[j].trianglelist!=null) {
						Sphere[] trianglespherelist = MathLib.triangleCircumSphere(entitylist[j].trianglelist);
						for (int i=0;i<entitylist[j].trianglelist.length;i++) {
							if (entitylist[j].trianglelist[i]!=null) {
								Sphere[] trianglesphere = {trianglespherelist[i]};
								Position[] trianglespherepoint = MathLib.sphereVertexList(trianglesphere);
								RenderView p4pixelview = renderCubemapPlaneViewSoftware(trianglespherepoint[0], entitylist, rendersize*3, rendersize*2, rendersize, MathLib.rotationMatrix(0, 0, 0), true, 0, 0);
								BufferedImage[] cubemapimages = new BufferedImage[6];
								cubemapimages[0] = p4pixelview.cubemap.backwardview.snapimage;
								cubemapimages[1] = p4pixelview.cubemap.bottomview.snapimage;
								cubemapimages[2] = p4pixelview.cubemap.forwardview.snapimage;
								cubemapimages[3] = p4pixelview.cubemap.leftview.snapimage;
								cubemapimages[4] = p4pixelview.cubemap.rightview.snapimage;
								cubemapimages[5] = p4pixelview.cubemap.topview.snapimage;
								float p4pixelr = 0.0f;
								float p4pixelg = 0.0f;
								float p4pixelb = 0.0f;
								for (int k=0;k<cubemapimages.length;k++) {
									for (int ky=0;ky<cubemapimages[k].getHeight();ky++) {
										for (int kx=0;kx<cubemapimages[k].getWidth();kx++) {
											Color p4pixelcolor = new Color(cubemapimages[k].getRGB(kx, ky));
											float[] p4pixelcolorcomp = p4pixelcolor.getRGBComponents(new float[4]);
											p4pixelr += p4pixelcolorcomp[0];
											p4pixelg += p4pixelcolorcomp[1];
											p4pixelb += p4pixelcolorcomp[2];
										}
									}
								}
								float pixelcount = 6*rendersize*rendersize;
								float p4pixelrt = multiplier*p4pixelr/pixelcount;
								float p4pixelgt = multiplier*p4pixelg/pixelcount;
								float p4pixelbt = multiplier*p4pixelb/pixelcount;
								if (p4pixelrt>1.0f) {p4pixelrt=1.0f;}
								if (p4pixelgt>1.0f) {p4pixelgt=1.0f;}
								if (p4pixelbt>1.0f) {p4pixelbt=1.0f;}
								Color p4pixelcolor = new Color(p4pixelrt, p4pixelgt, p4pixelbt, 1.0f);
								System.out.println("entitylist["+j+"]="+trianglespherepoint[0].x+","+trianglespherepoint[0].y+","+trianglespherepoint[0].z);
								VolatileImage lightmaptexture = gc.createCompatibleVolatileImage(texturesize, texturesize, Transparency.TRANSLUCENT);
								Graphics2D lmgfx = lightmaptexture.createGraphics();
								lmgfx.setComposite(AlphaComposite.Src);
								lmgfx.setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
								lmgfx.fillRect(0, 0, texturesize, texturesize);
								lmgfx.setComposite(AlphaComposite.SrcOver);
								lmgfx.setColor(p4pixelcolor);
								lmgfx.fillRect(0, 0, texturesize, texturesize);
								lmgfx.dispose();
								Material[] newlmatl = {new Material(Color.WHITE, 1.0f, lightmaptexture)};
								newlmatl[0].snapimage = lightmaptexture.getSnapshot(); 
								entitylist[j].trianglelist[i].lmatl = newlmatl;
							}
						}
					}
				}
			}
		}
		if (entitylist!=null) {
			for (int j=0;j<entitylist.length;j++) {
				if (entitylist[j]!=null) {
					if (entitylist[j].trianglelist!=null) {
						for (int i=0;i<entitylist[j].trianglelist.length;i++) {
							if (entitylist[j].trianglelist[i]!=null) {
								if ((entitylist[j].trianglelist[i].mat!=null)&&(entitylist[j].trianglelist[i].lmatl!=null)) {
									entitylist[j].trianglelist[i].mat = entitylist[j].trianglelist[i].mat.copy();
									entitylist[j].trianglelist[i].mat.ambientfileimage = entitylist[j].trianglelist[i].lmatl[0].fileimage;
									entitylist[j].trianglelist[i].mat.ambientsnapimage = entitylist[j].trianglelist[i].lmatl[0].snapimage;
								}
							}
						}
					}
				}
			}
		}
	}
	
}
