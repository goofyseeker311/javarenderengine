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
		double editplanedistance = (((double)renderwidth)/2.0f)/MathLib.tand(renderview.hfov/2.0f);
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
		Coordinate[][] linelistcoords = MathLib.projectedLine(renderview.pos, linelist, renderwidth, renderview.hfov, renderheight, renderview.vfov, viewrot);
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
		Coordinate[] originpointscoords = MathLib.projectedPoint(renderview.pos, originpoints, renderwidth, renderview.hfov, renderheight, renderview.vfov, viewrot);
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
		renderview.planes = MathLib.projectedPlanes(renderview.pos, renderwidth, renderview.hfov, renderview.rot); 
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
					Direction[] copytrianglenormallist = MathLib.triangleNormal(copytrianglelist);
					Sphere[] copytrianglespherelist = MathLib.triangleCircumSphere(copytrianglelist);
					for (int i=0;i<copytrianglespherelist.length;i++) {copytrianglespherelist[i].ind = i;}
					Direction[] copyviewtrianglespheredir = MathLib.vectorFromPoints(campos, copytrianglespherelist);
					Sphere[] sortedtrianglespherelist = Arrays.copyOf(copytrianglespherelist, copytrianglespherelist.length);
					Arrays.sort(sortedtrianglespherelist, distcomp);
					Coordinate[][] copytrianglelistcoords = MathLib.projectedTriangle(renderview.pos, copytrianglelist, renderwidth, renderview.hfov, renderheight, renderview.vfov, viewrot);
					for (int i=sortedtrianglespherelist.length-1;i>=0;i--) {
						int it = sortedtrianglespherelist[i].ind;
						Triangle[] copytriangle = {copytrianglelist[it]};
						Direction copytrianglenormal = copytrianglenormallist[it];
						Direction copytriangledir = copyviewtrianglespheredir[it];
						Coordinate coord1 = copytrianglelistcoords[it][0];
						Coordinate coord2 = copytrianglelistcoords[it][1];
						Coordinate coord3 = copytrianglelistcoords[it][2];
						Coordinate coord4 = copytrianglelistcoords[it][3];
						Coordinate coord5 = copytrianglelistcoords[it][4];
						if ((coord1!=null)||(coord2!=null)||(coord3!=null)) {
							Polygon trianglepolygon = new Polygon();
							if ((coord1!=null)&&(coord2!=null)&&(coord3!=null)) {
								trianglepolygon.addPoint((int)Math.round(coord1.u), (int)Math.round(coord1.v));
								trianglepolygon.addPoint((int)Math.round(coord2.u), (int)Math.round(coord2.v));
								trianglepolygon.addPoint((int)Math.round(coord3.u), (int)Math.round(coord3.v));
							} else {
								if (coord1!=null) {trianglepolygon.addPoint((int)Math.round(coord1.u), (int)Math.round(coord1.v));}
								if (coord2!=null) {trianglepolygon.addPoint((int)Math.round(coord2.u), (int)Math.round(coord2.v));}
								if (coord3!=null) {trianglepolygon.addPoint((int)Math.round(coord3.u), (int)Math.round(coord3.v));}
								trianglepolygon.addPoint((int)Math.round(coord5.u), (int)Math.round(coord5.v));
								trianglepolygon.addPoint((int)Math.round(coord4.u), (int)Math.round(coord4.v));
							}
							boolean mouseoverhit = g2.hit(new Rectangle(mouselocationx-vertexradius,mouselocationy-vertexradius,3,3), trianglepolygon, false);
							if (mouseoverhit) {
								mouseoverhittriangle.add(copytrianglelist[it]);
							}
							Color trianglecolor = trianglePixelShader(copytriangle[0], copytrianglenormal, null, copytriangledir, unlit);
							if (trianglecolor!=null) {
								g2.setColor(trianglecolor);
								g2.fill(trianglepolygon);
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
		renderview.planes = MathLib.projectedPlanes(renderview.pos, renderwidth, renderview.hfov, renderview.rot); 
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
			Direction[] camdir = {renderview.dirs[0]};
			Position[] camposa = {renderview.pos};
			Position[] rendercutpos = MathLib.translate(camposa, renderview.dirs[0], 1.1d);
			Plane[] rendercutplane = MathLib.planeFromNormalAtPoint(rendercutpos, camdir);
			double[] verticalangles = MathLib.projectedAngles(renderheight, renderview.vfov);
			double halfvfovmult = (1.0f/MathLib.tand(renderview.vfov/2.0f));
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
					Direction[] copytrianglenormallist = MathLib.triangleNormal(copytrianglelist);
					Sphere[] copytrianglespherelist = MathLib.triangleCircumSphere(copytrianglelist);
					for (int i=0;i<copytrianglespherelist.length;i++) {copytrianglespherelist[i].ind = i;}
					Sphere[] sortedtrianglespherelist = Arrays.copyOf(copytrianglespherelist, copytrianglespherelist.length);
					Arrays.sort(sortedtrianglespherelist, distcomp);
					Rectangle[] copytrianglelistint = MathLib.projectedTriangleIntersection(renderview.pos, copytrianglelist, renderwidth, renderheight, renderview.hfov, renderview.vfov, viewrot);
					for (int i=sortedtrianglespherelist.length-1;i>=0;i--) {
						int it = sortedtrianglespherelist[i].ind;
						if (copytrianglelistint[it]!=null) {
							Triangle[] copytriangle = {copytrianglelist[it]};
							Direction copytrianglenormal = copytrianglenormallist[it];
							int jstart = copytrianglelistint[it].x;
							int jend = copytrianglelistint[it].x+copytrianglelistint[it].width-1;
							Plane[] renderviewplanes = Arrays.copyOfRange(renderview.planes, jstart, jend+1);
							Line[][] vertplanetriangleint = MathLib.planeTriangleIntersection(renderviewplanes, copytriangle);
							for (int j=jstart;j<=jend;j++) {
								Line drawline = vertplanetriangleint[j-jstart][0];
								if (drawline!=null) {
									Position[] drawlinepoints = {drawline.pos1, drawline.pos2};
									double[][] fwdintpointsdist = MathLib.planePointDistance(drawlinepoints, camfwdplane);
									double[][] upintpointsdist = MathLib.planePointDistance(drawlinepoints, camupplane);
									if ((fwdintpointsdist[0][0]>=1.0f)||(fwdintpointsdist[1][0]>=1.0f)) {
										if (!((fwdintpointsdist[0][0]>=1.0f)&&(fwdintpointsdist[1][0]>=1.0f))) {
											Position[] drawlinepos1 = {drawline.pos1};
											Position[] drawlinepos2 = {drawline.pos2};
											Direction[] drawlinedir12 = MathLib.vectorFromPoints(drawlinepos1, drawlinepos2);
											double[][] drawlinedir12dist = MathLib.rayPlaneDistance(drawlinepos1[0], drawlinedir12, rendercutplane);
											Position[] drawlinepos3 = MathLib.translate(drawlinepos1, drawlinedir12[0], drawlinedir12dist[0][0]);
											if (fwdintpointsdist[0][0]>=1.0f) {
												Position[] newdrawlinepoints = {drawlinepos1[0], drawlinepos3[0]};
												drawlinepoints = newdrawlinepoints;
											} else {
												Position[] newdrawlinepoints = {drawlinepos2[0], drawlinepos3[0]};
												drawlinepoints = newdrawlinepoints;
											}
											fwdintpointsdist = MathLib.planePointDistance(drawlinepoints, camfwdplane);
											upintpointsdist = MathLib.planePointDistance(drawlinepoints, camupplane);
										}
										double vpixelyang1 = MathLib.atand(upintpointsdist[0][0]/fwdintpointsdist[0][0]);
										double vpixelyang2 = MathLib.atand(upintpointsdist[1][0]/fwdintpointsdist[1][0]);
										double vpixely1 = halfvfovmult*halfvres*(upintpointsdist[0][0]/fwdintpointsdist[0][0])+origindeltay;
										double vpixely2 = halfvfovmult*halfvres*(upintpointsdist[1][0]/fwdintpointsdist[1][0])+origindeltay;
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
												Coordinate tex1 = vpixelpoints[0].tex;
												Coordinate tex2 = vpixelpoints[1].tex;
												Coordinate lineuv = null;
												if ((tex1!=null)&&(tex2!=null)) {
													Position[] lineuvpoint1 = {new Position(tex1.u,1.0f-tex1.v,0.0f)};
													Position[] lineuvpoint2 = {new Position(tex2.u,1.0f-tex2.v,0.0f)};
													Direction[] vpixelpointdir12uv = MathLib.vectorFromPoints(lineuvpoint1, lineuvpoint2);
													Position[] lineuvpos = MathLib.translate(lineuvpoint1, vpixelpointdir12uv[0], vpixelpointlenfrac);
													lineuv = new Coordinate(lineuvpos[0].x, lineuvpos[0].y);
													renderview.cbuffer[n][j] = lineuv;
												}
												Position[] linepoint = MathLib.translate(vpixelpoint1, vpixelpointdir12[0], vpixelpointlenfrac);
												Direction[] linepointdir = MathLib.vectorFromPoints(renderview.pos, linepoint);
												double[] linepointdirlen = MathLib.vectorLength(linepointdir);
												Direction[] camray = linepointdir;
												double drawdistance = linepointdirlen[0];
												double[][] linepointdist = MathLib.planePointDistance(linepoint, camfwdplane);
												if ((linepointdist[0][0]>1.0f)&&(drawdistance<renderview.zbuffer[n][j])) {
													renderview.zbuffer[n][j] = drawdistance;
													renderview.tbuffer[n][j] = copytriangle[0];
													if ((mouselocationx==j)&&(mouselocationy==n)) {
														mouseoverhittriangle.add(copytriangle[0]);
													}
													Color trianglecolor = trianglePixelShader(copytriangle[0], copytrianglenormal, lineuv, camray[0], unlit);
													if (trianglecolor!=null) {
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
		renderview.fwddirs = MathLib.spheremapVectors(renderwidth, renderview.rot);
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
			Plane[] camfwdplanes = MathLib.planeFromNormalAtPoint(renderview.pos, renderview.fwddirs);
			Position[] camposa = {renderview.pos};
			Plane[] rendercutplanes = new Plane[renderwidth];
			for (int i=0;i<renderwidth;i++) {
				Direction[] renderfwddir = {renderview.fwddirs[i]};
				Position[] rendercutpos = MathLib.translate(camposa, renderfwddir[0], 1.1d);
				Plane[] renderfwdcutplane = MathLib.planeFromNormalAtPoint(rendercutpos[0], renderfwddir);
				rendercutplanes[i] = renderfwdcutplane[0]; 
			}
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
					Direction[] copytrianglenormallist = MathLib.triangleNormal(copytrianglelist);
					Sphere[] copytrianglespherelist = MathLib.triangleCircumSphere(copytrianglelist);
					for (int i=0;i<copytrianglespherelist.length;i++) {copytrianglespherelist[i].ind = i;}
					Sphere[] sortedtrianglespherelist = Arrays.copyOf(copytrianglespherelist, copytrianglespherelist.length);
					Arrays.sort(sortedtrianglespherelist, distcomp);
					Rectangle[] copytrianglelistint = MathLib.spheremapTriangleIntersection(renderview.pos, copytrianglelist, renderwidth, renderheight, viewrot);
					for (int i=sortedtrianglespherelist.length-1;i>=0;i--) {
						int it = sortedtrianglespherelist[i].ind;
						if (copytrianglelistint[it]!=null) {
							Triangle[] copytriangle = {copytrianglelist[it]};
							Direction copytrianglenormal = copytrianglenormallist[it];
							int jstart = copytrianglelistint[it].x;
							int jend = copytrianglelistint[it].x+copytrianglelistint[it].width-1;
							int[] planeindex = null;
							Plane[] renderviewplanes = null;
							if (copytrianglelistint[it].width>0) {
								planeindex = new int[copytrianglelistint[it].width];
								renderviewplanes = new Plane[copytrianglelistint[it].width];
								for (int n=jstart;n<=jend;n++) {
									planeindex[n-jstart] = n;
									renderviewplanes[n-jstart] = renderview.planes[n];
								}
							} else {
								int planeindexwidth = renderwidth-jstart+jend+1;
								planeindex = new int[planeindexwidth];
								renderviewplanes = new Plane[planeindexwidth];
								for (int n=0;n<=jend;n++) {
									planeindex[n] = n;
									renderviewplanes[n] = renderview.planes[n];
								}
								int n2 = jend+1;
								for (int n=jstart;n<renderwidth;n++) {
									planeindex[n2] = n;
									renderviewplanes[n2] = renderview.planes[n];
									n2++;
								}
							}
							Line[][] vertplanetriangleint = MathLib.planeTriangleIntersection(renderviewplanes, copytriangle);
							for (int l=0;l<planeindex.length;l++) {
								int j = planeindex[l];
								Line drawline = vertplanetriangleint[l][0];
								if (drawline!=null) {
									Position[] drawlinepoints = {drawline.pos1, drawline.pos2};
									Plane[] camfwdplane = {camfwdplanes[j]};
									Plane[] rendercutplane = {rendercutplanes[j]};
									double[][] fwdintpointsdist = MathLib.planePointDistance(drawlinepoints, camfwdplane);
									double[][] upintpointsdist = MathLib.planePointDistance(drawlinepoints, camupplane);
									if ((fwdintpointsdist[0][0]>=1.0f)||(fwdintpointsdist[1][0]>=1.0f)) {
										if (!((fwdintpointsdist[0][0]>=1.0f)&&(fwdintpointsdist[1][0]>=1.0f))) {
											Position[] drawlinepos1 = {drawline.pos1};
											Position[] drawlinepos2 = {drawline.pos2};
											Direction[] drawlinedir12 = MathLib.vectorFromPoints(drawlinepos1, drawlinepos2);
											double[][] drawlinedir12dist = MathLib.rayPlaneDistance(drawlinepos1[0], drawlinedir12, rendercutplane);
											Position[] drawlinepos3 = MathLib.translate(drawlinepos1, drawlinedir12[0], drawlinedir12dist[0][0]);
											if (fwdintpointsdist[0][0]>=1.0f) {
												Position[] newdrawlinepoints = {drawlinepos1[0], drawlinepos3[0]};
												drawlinepoints = newdrawlinepoints;
											} else {
												Position[] newdrawlinepoints = {drawlinepos2[0], drawlinepos3[0]};
												drawlinepoints = newdrawlinepoints;
											}
											fwdintpointsdist = MathLib.planePointDistance(drawlinepoints, camfwdplane);
											upintpointsdist = MathLib.planePointDistance(drawlinepoints, camupplane);
										}
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
												Coordinate tex1 = vpixelpoints[0].tex;
												Coordinate tex2 = vpixelpoints[1].tex;
												Coordinate lineuv = null;
												if ((tex1!=null)&&(tex2!=null)) {
													Position[] lineuvpoint1 = {new Position(tex1.u,1.0f-tex1.v,0.0f)};
													Position[] lineuvpoint2 = {new Position(tex2.u,1.0f-tex2.v,0.0f)};
													Direction[] vpixelpointdir12uv = MathLib.vectorFromPoints(lineuvpoint1, lineuvpoint2);
													Position[] lineuvpos = MathLib.translate(lineuvpoint1, vpixelpointdir12uv[0], vpixelpointlenfrac);
													lineuv = new Coordinate(lineuvpos[0].x, lineuvpos[0].y);
													renderview.cbuffer[n][j] = lineuv;
												}
												Position[] linepoint = MathLib.translate(vpixelpoint1, vpixelpointdir12[0], vpixelpointlenfrac);
												Direction[] linepointdir = MathLib.vectorFromPoints(renderview.pos, linepoint);
												double[] linepointdirlen = MathLib.vectorLength(linepointdir);
												Direction[] camray = linepointdir;
												double drawdistance = linepointdirlen[0];
												double[][] linepointdist = MathLib.planePointDistance(linepoint, camfwdplane);
												if ((linepointdist[0][0]>1.0f)&&(drawdistance<renderview.zbuffer[n][j])) {
													renderview.zbuffer[n][j] = drawdistance;
													renderview.tbuffer[n][j] = copytriangle[0];
													if ((mouselocationx==j)&&(mouselocationy==n)) {
														mouseoverhittriangle.add(copytriangle[0]);
													}
													Color trianglecolor = trianglePixelShader(copytriangle[0], copytrianglenormal, lineuv, camray[0], unlit);
													if (trianglecolor!=null) {
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
		renderview.rays = MathLib.projectedRays(renderwidth, renderheight, renderview.hfov, renderview.vfov, renderview.rot);
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
			Plane[] camdirrightupplanes = MathLib.planeFromNormalAtPoint(renderview.pos, renderview.dirs);
			Plane[] camfwdplane = {camdirrightupplanes[0]};
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
					Direction[] copytrianglenormallist = MathLib.triangleNormal(copytrianglelist);
					Sphere[] copytrianglespherelist = MathLib.triangleCircumSphere(copytrianglelist);
					for (int i=0;i<copytrianglespherelist.length;i++) {copytrianglespherelist[i].ind = i;}
					Sphere[] sortedtrianglespherelist = Arrays.copyOf(copytrianglespherelist, copytrianglespherelist.length);
					Arrays.sort(sortedtrianglespherelist, distcomp);
					Rectangle[] copytrianglelistint = MathLib.projectedTriangleIntersection(renderview.pos, copytrianglelist, renderwidth, renderheight, renderview.hfov, renderview.vfov, viewrot);
					for (int n=sortedtrianglespherelist.length-1;n>=0;n--) {
						int it = sortedtrianglespherelist[n].ind;
						if (copytrianglelistint[it]!=null) {
							Triangle[] copytriangle = {copytrianglelist[it]};
							Direction copytrianglenormal = copytrianglenormallist[it];
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
										Coordinate tex = camrayint[0][0].tex;
										Coordinate pointuv = null;
										if (tex!=null) {
											pointuv = new Coordinate(tex.u,1.0f-tex.v);
											renderview.cbuffer[n][j] = pointuv;
										}
										Direction[] linepointdir = MathLib.vectorFromPoints(renderview.pos, camrayintpos);
										double[] linepointdirlen = MathLib.vectorLength(linepointdir);
										double drawdistance = linepointdirlen[0];
										double[][] camrayintposdist = MathLib.planePointDistance(camrayintpos, camfwdplane);
										if ((camrayintposdist[0][0]>1.0f)&&(drawdistance<renderview.zbuffer[j][i])) {
											renderview.zbuffer[j][i] = drawdistance;
											renderview.tbuffer[j][i] = copytriangle[0];
											if ((mouselocationx==i)&&(mouselocationy==j)) {
												mouseoverhittriangle.add(copytriangle[0]);
											}
											Color trianglecolor = trianglePixelShader(copytriangle[0], copytrianglenormal, pointuv, camray[0], unlit);
											if (trianglecolor!=null) {
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
		renderview.rays = MathLib.spheremapRays(renderwidth, renderheight, renderview.rot);
		renderview.fwddirs = MathLib.spheremapVectors(renderwidth, renderview.rot);
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
			Plane[] camfwdplanes = MathLib.planeFromNormalAtPoint(renderview.pos, renderview.fwddirs);
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
					Direction[] copytrianglenormallist = MathLib.triangleNormal(copytrianglelist);
					Sphere[] copytrianglespherelist = MathLib.triangleCircumSphere(copytrianglelist);
					for (int i=0;i<copytrianglespherelist.length;i++) {copytrianglespherelist[i].ind = i;}
					Sphere[] sortedtrianglespherelist = Arrays.copyOf(copytrianglespherelist, copytrianglespherelist.length);
					Arrays.sort(sortedtrianglespherelist, distcomp);
					Rectangle[] copytrianglelistint = MathLib.spheremapTriangleIntersection(renderview.pos, copytrianglelist, renderwidth, renderheight, viewrot);
					for (int n=sortedtrianglespherelist.length-1;n>=0;n--) {
						int it = sortedtrianglespherelist[n].ind;
						if (copytrianglelistint[it]!=null) {
							Triangle[] copytriangle = {copytrianglelist[it]};
							Direction copytrianglenormal = copytrianglenormallist[it];
							int jstart = copytrianglelistint[it].y;
							int jend = copytrianglelistint[it].y+copytrianglelistint[it].height-1;
							int istart = copytrianglelistint[it].x;
							int iend = copytrianglelistint[it].x+copytrianglelistint[it].width-1;
							int[] rayindex = null;
							if (copytrianglelistint[it].width>0) {
								rayindex = new int[copytrianglelistint[it].width];
								for (int i=istart;i<=iend;i++) {
									rayindex[i-istart] = i;
								}
							} else {
								rayindex = new int[renderwidth-istart+iend+1];
								for (int i=0;i<=iend;i++) {
									rayindex[i] = i;
								}
								int i2 = iend+1;
								for (int i=istart;i<renderwidth;i++) {
									rayindex[i2] = i;
									i2++;
								}
							}
							for (int j=jstart;j<=jend;j++) {
								for (int l=0;l<rayindex.length;l++) {
									int i = rayindex[l];
									Direction[] camray = {renderview.rays[i][j]};
									Plane[] camfwdplane = {camfwdplanes[i]};
									Position[][] camrayint = MathLib.rayTriangleIntersection(renderview.pos, camray, copytriangle);
									Position[] camrayintpos = {camrayint[0][0]};
									if (camrayintpos[0]!=null) {
										Coordinate tex = camrayint[0][0].tex;
										Coordinate pointuv = null;
										if (tex!=null) {
											pointuv = new Coordinate(tex.u,1.0f-tex.v);
											renderview.cbuffer[n][j] = pointuv;
										}
										Direction[] linepointdir = MathLib.vectorFromPoints(renderview.pos, camrayintpos);
										double[] linepointdirlen = MathLib.vectorLength(linepointdir);
										double drawdistance = linepointdirlen[0];
										double[][] camrayintposdist = MathLib.planePointDistance(camrayintpos, camfwdplane);
										if ((camrayintposdist[0][0]>1.0f)&&(drawdistance<renderview.zbuffer[j][i])) {
											renderview.zbuffer[j][i] = drawdistance;
											renderview.tbuffer[j][i] = copytriangle[0];
											if ((mouselocationx==i)&&(mouselocationy==j)) {
												mouseoverhittriangle.add(copytriangle[0]);
											}
											Color trianglecolor = trianglePixelShader(copytriangle[0], copytrianglenormal, pointuv, camray[0], unlit);
											if (trianglecolor!=null) {
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

	public static RenderView renderCubemapView(Position campos, Entity[] entitylist, int renderwidth, int renderheight, int rendersize, Matrix viewrot, boolean unlit, int mode, int mouselocationx, int mouselocationy) {
		RenderView renderview = new RenderView();
		renderview.mode = (mode>0)?mode:1;
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
		if (renderview.mode==2) {
			renderview.cubemap.topview = renderProjectedPlaneViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, topmatrix, renderview.unlit, mouselocationx, mouselocationy);
			renderview.cubemap.bottomview = renderProjectedPlaneViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, bottommatrix, renderview.unlit, mouselocationx, mouselocationy); 
			renderview.cubemap.forwardview = renderProjectedPlaneViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, forwardmatrix, renderview.unlit, mouselocationx, mouselocationy);
			renderview.cubemap.rightview = renderProjectedPlaneViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, rightmatrix, renderview.unlit, mouselocationx, mouselocationy);
			renderview.cubemap.backwardview = renderProjectedPlaneViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, backwardmatrix, renderview.unlit, mouselocationx, mouselocationy);
			renderview.cubemap.leftview = renderProjectedPlaneViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, leftmatrix, renderview.unlit, mouselocationx, mouselocationy);
		} else if (renderview.mode==3) {
			renderview.cubemap.topview = renderProjectedRayViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, topmatrix, renderview.unlit, mouselocationx, mouselocationy);
			renderview.cubemap.bottomview = renderProjectedRayViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, bottommatrix, renderview.unlit, mouselocationx, mouselocationy); 
			renderview.cubemap.forwardview = renderProjectedRayViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, forwardmatrix, renderview.unlit, mouselocationx, mouselocationy);
			renderview.cubemap.rightview = renderProjectedRayViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, rightmatrix, renderview.unlit, mouselocationx, mouselocationy);
			renderview.cubemap.backwardview = renderProjectedRayViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, backwardmatrix, renderview.unlit, mouselocationx, mouselocationy);
			renderview.cubemap.leftview = renderProjectedRayViewSoftware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, leftmatrix, renderview.unlit, mouselocationx, mouselocationy);
		} else {
			renderview.cubemap.topview = renderProjectedPolygonViewHardware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, topmatrix, renderview.unlit, mouselocationx, mouselocationy);
			renderview.cubemap.bottomview = renderProjectedPolygonViewHardware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, bottommatrix, renderview.unlit, mouselocationx, mouselocationy); 
			renderview.cubemap.forwardview = renderProjectedPolygonViewHardware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, forwardmatrix, renderview.unlit, mouselocationx, mouselocationy);
			renderview.cubemap.rightview = renderProjectedPolygonViewHardware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, rightmatrix, renderview.unlit, mouselocationx, mouselocationy);
			renderview.cubemap.backwardview = renderProjectedPolygonViewHardware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, backwardmatrix, renderview.unlit, mouselocationx, mouselocationy);
			renderview.cubemap.leftview = renderProjectedPolygonViewHardware(renderview.pos, entitylist, renderview.rendersize, renderview.vfov, renderview.rendersize, renderview.vfov, leftmatrix, renderview.unlit, mouselocationx, mouselocationy);
		}
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


	public static Color trianglePixelShader(Triangle triangle, Direction trianglenormal, Coordinate texuv, Direction camray, boolean unlit) {
		Triangle[] copytriangle = {triangle};
		Direction[] copytrianglenormal = {trianglenormal};
		Material copymaterial = copytriangle[0].mat;
		Color trianglecolor = copymaterial.facecolor;
		float alphacolor = copymaterial.transparency;
		float[] trianglecolorcomp = null;
		if (trianglecolor!=null) {
			trianglecolorcomp = trianglecolor.getRGBComponents(new float[4]);
			trianglecolor = new Color(trianglecolorcomp[0], trianglecolorcomp[1], trianglecolorcomp[2], alphacolor);
		}
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
		float[] lightmapcolorcomp =  null;
		if (lightmapcolor!=null) {
			lightmapcolorcomp = lightmapcolor.getRGBComponents(new float[4]);
		}
		VolatileImage lightmaptexture = copymaterial.ambientfileimage;
		BufferedImage lightmaptextureimage = copymaterial.ambientsnapimage;
		if ((lightmaptexture!=null)&&(lightmaptextureimage==null)) {
			copymaterial.ambientsnapimage = copymaterial.ambientfileimage.getSnapshot();
			lightmaptextureimage = copymaterial.ambientsnapimage;
		}
		double[] triangleviewangle = MathLib.vectorAngle(camray, copytrianglenormal);
		if ((copytriangle[0].norm.isZero())&&(triangleviewangle[0]<90.0f)) {
			triangleviewangle[0] = 180.0f - triangleviewangle[0];
		}
		triangleviewangle[0] -= 90.0f;
		boolean frontsidevisible = true;
		if (triangleviewangle[0]<0.0f) {triangleviewangle[0]=0.0f;frontsidevisible=false;}
		float shadingmultiplier = ((((float)triangleviewangle[0])/1.5f)+30.0f)/90.0f;
		if (texuv!=null) {
			if (lightmaptexture!=null) {
				lightmapcolor = null;
				lightmapcolorcomp = null;
				if ((texuv.u>=0.0f)&&(texuv.u<=1.0f)&&(texuv.v>=0.0f)&&(texuv.v<=1.0f)) {
					int lineuvx = (int)Math.round(texuv.u*(lightmaptexture.getWidth()-1));
					int lineuvy = (int)Math.round(texuv.v*(lightmaptexture.getHeight()-1));
					lightmapcolor = new Color(lightmaptextureimage.getRGB(lineuvx, lineuvy));
					lightmapcolorcomp = lightmapcolor.getRGBComponents(new float[4]);
				}
			}
			if (emissivetexture!=null) {
				emissivecolor = null;
				emissivecolorcomp = null;
				if ((texuv.u>=0.0f)&&(texuv.u<=1.0f)&&(texuv.v>=0.0f)&&(texuv.v<=1.0f)) {
					int lineuvx = (int)Math.round(texuv.u*(emissivetexture.getWidth()-1));
					int lineuvy = (int)Math.round(texuv.v*(emissivetexture.getHeight()-1));
					emissivecolor = new Color(emissivetextureimage.getRGB(lineuvx, lineuvy));
					emissivecolorcomp = emissivecolor.getRGBComponents(new float[4]);
				}
			}
			if (triangletexture!=null) {
				trianglecolor = null;
				trianglecolorcomp = null;
				if ((texuv.u>=0.0f)&&(texuv.u<=1.0f)&&(texuv.v>=0.0f)&&(texuv.v<=1.0f)) {
					int lineuvx = (int)Math.round(texuv.u*(triangletexture.getWidth()-1));
					int lineuvy = (int)Math.round(texuv.v*(triangletexture.getHeight()-1));
					Color triangletexturecolor = new Color(triangletextureimage.getRGB(lineuvx, lineuvy));
					trianglecolorcomp = triangletexturecolor.getRGBComponents(new float[4]);
					trianglecolor = new Color(trianglecolorcomp[0], trianglecolorcomp[1], trianglecolorcomp[2], alphacolor);
				}
			}
		}
		if (trianglecolor!=null) {
			float texr = trianglecolorcomp[0];
			float texg = trianglecolorcomp[1];
			float texb = trianglecolorcomp[2];
			float multiplier = 10.0f;
			if (!unlit) {
				texr *= shadingmultiplier;
				texg *= shadingmultiplier;
				texb *= shadingmultiplier;
			}
			if ((frontsidevisible)&&(lightmapcolor!=null)) {
				texr *= lightmapcolorcomp[0]*multiplier;
				texg *= lightmapcolorcomp[1]*multiplier;
				texb *= lightmapcolorcomp[2]*multiplier;
			} else if (unlit) {
				texr = 0.0f;
				texg = 0.0f;
				texb = 0.0f;
			}
			if ((frontsidevisible)&&(emissivecolor!=null)) {
				texr += emissivecolorcomp[0]*multiplier;
				texg += emissivecolorcomp[1]*multiplier;
				texb += emissivecolorcomp[2]*multiplier;
			}
			if (texr>1.0f) {texr=1.0f;}
			if (texg>1.0f) {texg=1.0f;}
			if (texb>1.0f) {texb=1.0f;}
			trianglecolor = new Color(texr, texg, texb, alphacolor);
		}
		return trianglecolor;
	}
	
	public static void renderSurfaceFaceLightmapCubemapView(Entity[] entitylist, int rendersize, int bounces, int mode) {
		float multiplier = 1000.0f;
		Direction[][] cubemaprays = MathLib.projectedRays(rendersize, rendersize, 90.0f, 90.0f, MathLib.rotationMatrix(0.0f, 0.0f, 0.0f));
		double[][] cubemapraylen = new double[rendersize][rendersize];
		for (int i=0;i<cubemaprays.length;i++) {
			cubemapraylen[i] = MathLib.vectorLength(cubemaprays[i]);
		}
		int rendermode = (mode>0)?mode:1;
		int lightbounces = (bounces>0)?bounces:1;
		for (int l=0;l<lightbounces;l++) {
			if (entitylist!=null) {
				for (int j=0;j<entitylist.length;j++) {
					if (entitylist[j]!=null) {
						if (entitylist[j].trianglelist!=null) {
							Sphere[] trianglespherelist = MathLib.triangleInSphere(entitylist[j].trianglelist);
							for (int i=0;i<entitylist[j].trianglelist.length;i++) {
								if (entitylist[j].trianglelist[i]!=null) {
									Sphere[] trianglesphere = {trianglespherelist[i]};
									Position[] trianglespherepoint = MathLib.sphereVertexList(trianglesphere);
									RenderView p4pixelview = renderCubemapView(trianglespherepoint[0], entitylist, rendersize*3, rendersize*2, rendersize, MathLib.rotationMatrix(0, 0, 0), true, rendermode, 0, 0);
									RenderView[] cubemapviews = new RenderView[6];
									cubemapviews[0] = p4pixelview.cubemap.backwardview;
									cubemapviews[1] = p4pixelview.cubemap.bottomview;
									cubemapviews[2] = p4pixelview.cubemap.forwardview;
									cubemapviews[3] = p4pixelview.cubemap.leftview;
									cubemapviews[4] = p4pixelview.cubemap.rightview;
									cubemapviews[5] = p4pixelview.cubemap.topview;
									float p4pixelr = 0.0f;
									float p4pixelg = 0.0f;
									float p4pixelb = 0.0f;
									float pixelcount = 6*rendersize*rendersize;
									if (entitylist[j].trianglelist[i].mat.emissivecolor!=null) {
										float[] triangleemissivecolorcomp = entitylist[j].trianglelist[i].mat.emissivecolor.getRGBComponents(new float[4]);
										p4pixelr = triangleemissivecolorcomp[0]*pixelcount;
										p4pixelg = triangleemissivecolorcomp[1]*pixelcount;
										p4pixelb = triangleemissivecolorcomp[2]*pixelcount;
									}
									for (int k=0;k<cubemapviews.length;k++) {
										for (int ky=0;ky<cubemapviews[k].snapimage.getHeight();ky++) {
											for (int kx=0;kx<cubemapviews[k].snapimage.getWidth();kx++) {
												Color p4pixelcolor = new Color(cubemapviews[k].snapimage.getRGB(kx, ky));
												float[] p4pixelcolorcomp = p4pixelcolor.getRGBComponents(new float[4]);
												p4pixelr += p4pixelcolorcomp[0]/cubemapraylen[ky][kx];
												p4pixelg += p4pixelcolorcomp[1]/cubemapraylen[ky][kx];
												p4pixelb += p4pixelcolorcomp[2]/cubemapraylen[ky][kx];
											}
										}
									}
									float p4pixelrt = multiplier*p4pixelr/(float)Math.pow(pixelcount,l+1);
									float p4pixelgt = multiplier*p4pixelg/(float)Math.pow(pixelcount,l+1);
									float p4pixelbt = multiplier*p4pixelb/(float)Math.pow(pixelcount,l+1);
									if (p4pixelrt>1.0f) {p4pixelrt=1.0f;}
									if (p4pixelgt>1.0f) {p4pixelgt=1.0f;}
									if (p4pixelbt>1.0f) {p4pixelbt=1.0f;}
									Color p4pixelcolor = new Color(p4pixelrt, p4pixelgt, p4pixelbt, 1.0f);
									System.out.println("RenderLib: renderSurfaceLightmapFaceCubemapView: mode["+mode+"] bounce["+l+"] entitylist["+j+"]["+i+"]="+trianglespherepoint[0].x+","+trianglespherepoint[0].y+","+trianglespherepoint[0].z);
									if ((entitylist[j].trianglelist[i].lmatl==null)||(entitylist[j].trianglelist[i].lmatl.length!=lightbounces)) {
										entitylist[j].trianglelist[i].lmatl = new Material[lightbounces];
									}
									entitylist[j].trianglelist[i].lmatl[l] = new Material(p4pixelcolor, 1.0f, null);
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
										if (entitylist[j].trianglelist[i].mat.ambientcolor==null) {
											entitylist[j].trianglelist[i].mat.ambientcolor = Color.BLACK;
										}
										float[] ambcolorcomp = entitylist[j].trianglelist[i].mat.ambientcolor.getRGBComponents(new float[4]);
										float[] lightcolorcomp = entitylist[j].trianglelist[i].lmatl[l].facecolor.getRGBComponents(new float[4]);
										float newr = ambcolorcomp[0]+lightcolorcomp[0];
										float newg = ambcolorcomp[1]+lightcolorcomp[1];
										float newb = ambcolorcomp[2]+lightcolorcomp[2];
										if (newr>1.0f) {newr=1.0f;}
										if (newg>1.0f) {newg=1.0f;}
										if (newb>1.0f) {newb=1.0f;}
										entitylist[j].trianglelist[i].mat.ambientcolor = new Color(newr,newg,newb,1.0f);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static void renderSurfaceTextureLightmapCubemapView(Entity[] entitylist, int rendersize, int texturesize, int bounces, int mode) {
		//TODO pixel lightmap texture output
		float multiplier = 1000.0f;
		Direction[][] cubemaprays = MathLib.projectedRays(rendersize, rendersize, 90.0f, 90.0f, MathLib.rotationMatrix(0.0f, 0.0f, 0.0f));
		double[][] cubemapraylen = new double[rendersize][rendersize];
		for (int i=0;i<cubemaprays.length;i++) {
			cubemapraylen[i] = MathLib.vectorLength(cubemaprays[i]);
		}
		int rendermode = (mode>0)?mode:1;
		int lightbounces = (bounces>0)?bounces:1;
		for (int l=0;l<lightbounces;l++) {
			if (entitylist!=null) {
				for (int j=0;j<entitylist.length;j++) {
					if (entitylist[j]!=null) {
						if (entitylist[j].trianglelist!=null) {
							Sphere[] trianglespherelist = MathLib.triangleInSphere(entitylist[j].trianglelist);
							for (int i=0;i<entitylist[j].trianglelist.length;i++) {
								if (entitylist[j].trianglelist[i]!=null) {
									Sphere[] trianglesphere = {trianglespherelist[i]};
									Position[] trianglespherepoint = MathLib.sphereVertexList(trianglesphere);
									RenderView p4pixelview = renderCubemapView(trianglespherepoint[0], entitylist, rendersize*3, rendersize*2, rendersize, MathLib.rotationMatrix(0, 0, 0), true, rendermode, 0, 0);
									RenderView[] cubemapviews = new RenderView[6];
									cubemapviews[0] = p4pixelview.cubemap.backwardview;
									cubemapviews[1] = p4pixelview.cubemap.bottomview;
									cubemapviews[2] = p4pixelview.cubemap.forwardview;
									cubemapviews[3] = p4pixelview.cubemap.leftview;
									cubemapviews[4] = p4pixelview.cubemap.rightview;
									cubemapviews[5] = p4pixelview.cubemap.topview;
									float p4pixelr = 0.0f;
									float p4pixelg = 0.0f;
									float p4pixelb = 0.0f;
									float pixelcount = 6*rendersize*rendersize;
									if (entitylist[j].trianglelist[i].mat.emissivecolor!=null) {
										float[] triangleemissivecolorcomp = entitylist[j].trianglelist[i].mat.emissivecolor.getRGBComponents(new float[4]);
										p4pixelr = triangleemissivecolorcomp[0]*pixelcount;
										p4pixelg = triangleemissivecolorcomp[1]*pixelcount;
										p4pixelb = triangleemissivecolorcomp[2]*pixelcount;
									}
									for (int k=0;k<cubemapviews.length;k++) {
										for (int ky=0;ky<cubemapviews[k].snapimage.getHeight();ky++) {
											for (int kx=0;kx<cubemapviews[k].snapimage.getWidth();kx++) {
												Color p4pixelcolor = new Color(cubemapviews[k].snapimage.getRGB(kx, ky));
												float[] p4pixelcolorcomp = p4pixelcolor.getRGBComponents(new float[4]);
												p4pixelr += p4pixelcolorcomp[0]/cubemapraylen[ky][kx];
												p4pixelg += p4pixelcolorcomp[1]/cubemapraylen[ky][kx];
												p4pixelb += p4pixelcolorcomp[2]/cubemapraylen[ky][kx];
											}
										}
									}
									float p4pixelrt = multiplier*p4pixelr/(float)Math.pow(pixelcount,l+1);
									float p4pixelgt = multiplier*p4pixelg/(float)Math.pow(pixelcount,l+1);
									float p4pixelbt = multiplier*p4pixelb/(float)Math.pow(pixelcount,l+1);
									if (p4pixelrt>1.0f) {p4pixelrt=1.0f;}
									if (p4pixelgt>1.0f) {p4pixelgt=1.0f;}
									if (p4pixelbt>1.0f) {p4pixelbt=1.0f;}
									Color p4pixelcolor = new Color(p4pixelrt, p4pixelgt, p4pixelbt, 1.0f);
									System.out.println("RenderLib: renderSurfaceLightmapTextureCubemapView: mode["+mode+"] bounce["+l+"] entitylist["+j+"]["+i+"]="+trianglespherepoint[0].x+","+trianglespherepoint[0].y+","+trianglespherepoint[0].z);
									if ((entitylist[j].trianglelist[i].lmatl==null)||(entitylist[j].trianglelist[i].lmatl.length!=lightbounces)) {
										entitylist[j].trianglelist[i].lmatl = new Material[lightbounces];
									}
									VolatileImage lightmaptexture = gc.createCompatibleVolatileImage(texturesize, texturesize, Transparency.TRANSLUCENT);
									Graphics2D lmgfx = lightmaptexture.createGraphics();
									lmgfx.setComposite(AlphaComposite.Src);
									lmgfx.setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
									lmgfx.fillRect(0, 0, texturesize, texturesize);
									lmgfx.setComposite(AlphaComposite.SrcOver);
									lmgfx.setColor(p4pixelcolor);
									lmgfx.fillRect(0, 0, texturesize, texturesize);
									lmgfx.dispose();
									Material newlmatl = new Material(Color.WHITE, 1.0f, lightmaptexture);
									newlmatl.snapimage = lightmaptexture.getSnapshot(); 
									entitylist[j].trianglelist[i].lmatl[l] = newlmatl;
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
										entitylist[j].trianglelist[i].mat.ambientfileimage = entitylist[j].trianglelist[i].lmatl[l].fileimage;
										entitylist[j].trianglelist[i].mat.ambientsnapimage = entitylist[j].trianglelist[i].lmatl[l].snapimage;
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
