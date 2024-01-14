package fi.jkauppa.javarenderengine;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.Transparency;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import fi.jkauppa.javarenderengine.JavaRenderEngine.AppHandlerPanel;
import fi.jkauppa.javarenderengine.MathLib.Coordinate;
import fi.jkauppa.javarenderengine.MathLib.Direction;
import fi.jkauppa.javarenderengine.MathLib.Entity;
import fi.jkauppa.javarenderengine.MathLib.Line;
import fi.jkauppa.javarenderengine.MathLib.Matrix;
import fi.jkauppa.javarenderengine.MathLib.Plane;
import fi.jkauppa.javarenderengine.MathLib.Position;
import fi.jkauppa.javarenderengine.MathLib.Rotation;
import fi.jkauppa.javarenderengine.MathLib.Sphere;
import fi.jkauppa.javarenderengine.MathLib.Sphere.SphereRenderComparator;
import fi.jkauppa.javarenderengine.MathLib.Tetrahedron;
import fi.jkauppa.javarenderengine.MathLib.Triangle;
import fi.jkauppa.javarenderengine.UtilLib.ModelFileFilters.OBJFileFilter;
import fi.jkauppa.javarenderengine.UtilLib.ModelFileFilters.STLFileFilter;
import fi.jkauppa.javarenderengine.ModelLib.Material;
import fi.jkauppa.javarenderengine.ModelLib.Model;
import fi.jkauppa.javarenderengine.ModelLib.ModelFaceIndex;
import fi.jkauppa.javarenderengine.ModelLib.ModelFaceVertexIndex;
import fi.jkauppa.javarenderengine.ModelLib.ModelLineIndex;
import fi.jkauppa.javarenderengine.ModelLib.ModelObject;

public class CADApp extends AppHandlerPanel {
	private static final long serialVersionUID = 1L;
	private Direction lookdir = new Direction(0,0,-1);
	private TexturePaint bgpattern = null;
	private boolean draglinemode = false;
	private boolean snaplinemode = false;
	private Color drawcolor = Color.BLACK;
	private float[] drawcolorhsb = {0.0f, 1.0f, 0.0f};
	private float penciltransparency = 1.0f;
	private int polygonfillmode = 1;
	private int selecteddragvertex = 0;
	private Triangle mouseovertriangle = null;
	private int mousestartlocationx = 0, mousestartlocationy = 0;  
	private int mouselocationx = 0, mouselocationy = 0;
	private int cameralocationx = 0, cameralocationy = 0;
	private int drawdepth = 0; 
	private int drawstartdepth = 0;
	private Rotation camrot = new Rotation(0,0,0);
	private final double drawdepthscale = 0.00035f;
	private int origindeltax = 0, origindeltay = 0;
	private final int originlinewidth = 100;
	private final int originlineheight = 100;
	private final int originlinedepth = 100;
	private final int vertexradius = 2;
	private final int axisstroke = 2;
	private final int vertexstroke = 2;
	private final int vertexfocus = 3;
	private final int sketchlinestroke = 2;
	private final int flatlinestroke = 1;
	private final int gridstep = 20;
	private BufferedImage bgpatternimage = gc.createCompatibleImage(gridstep, gridstep, Transparency.OPAQUE);
	private ArrayList<Line> linelistarray = new ArrayList<Line>();
	private Entity[] entitylist = null;
	private JFileChooser filechooser = new JFileChooser();
	private OBJFileFilter objfilefilter = new OBJFileFilter();
	private STLFileFilter stlfilefilter = new STLFileFilter();
	private boolean leftkeydown = false;
	private boolean rightkeydown = false;
	private boolean upwardkeydown = false;
	private boolean downwardkeydown = false;
	private boolean forwardkeydown = false;
	private boolean backwardkeydown = false;
	private boolean updatetrianglelist = true;
	
	public CADApp() {
		Graphics2D pgfx = this.bgpatternimage.createGraphics();
		pgfx.setColor(Color.WHITE);
		pgfx.fillRect(0, 0, this.bgpatternimage.getWidth(), this.bgpatternimage.getHeight());
		pgfx.setColor(Color.BLACK);
		pgfx.drawLine(0, 0, 0, gridstep-1);
		pgfx.drawLine(0, 0, gridstep-1, 0);
		pgfx.dispose();
		this.filechooser.addChoosableFileFilter(this.objfilefilter);
		this.filechooser.addChoosableFileFilter(this.stlfilefilter);
		this.filechooser.setFileFilter(this.objfilefilter);
		this.filechooser.setAcceptAllFileFilterUsed(false);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	}
	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		Position renderpos = new Position(-this.cameralocationx,-this.cameralocationy,-this.drawdepth);
		Matrix rendermat = MathLib.rotationMatrix(-this.camrot.x, -this.camrot.y, -this.camrot.z);
		this.origindeltax = (int)Math.floor(((double)this.getWidth())/2.0f);
		this.origindeltay = (int)Math.floor(((double)this.getHeight())/2.0f);
		this.bgpattern = new TexturePaint(this.bgpatternimage,new Rectangle(this.origindeltax-this.cameralocationx, this.origindeltay-this.cameralocationy, gridstep, gridstep));
		g2.setComposite(AlphaComposite.SrcOver);
		g2.setColor(null);
		g2.setPaint(bgpattern);
		g2.fillRect(0, 0, this.getWidth()*2, this.getHeight()*2);
		g2.setPaint(null);
		g2.setColor(null);
		Triangle mouseoverhittriangle = null;
		Entity[] entitylistmaphandle = this.entitylist;
		if (this.polygonfillmode==2) {
			if (entitylistmaphandle!=null) {
				for (int k=0;k<entitylistmaphandle.length;k++) {
					Triangle[] copytrianglelist = entitylistmaphandle[k].trianglelist;
					if (copytrianglelist!=null) {
						for (int i=0;i<copytrianglelist.length;i++) {copytrianglelist[i].ind = i;}
						Triangle[] transformedtrianglelist = MathLib.translate(copytrianglelist, renderpos);
						transformedtrianglelist = MathLib.matrixMultiply(transformedtrianglelist, rendermat);
						Sphere[] transformedtrianglespherelist = MathLib.triangleCircumSphere(transformedtrianglelist);
						for (int i=0;i<transformedtrianglespherelist.length;i++) {transformedtrianglespherelist[i].ind = i;}
						TreeSet<Sphere> sortedtrianglespheretree = new TreeSet<Sphere>(new SphereRenderComparator());
						sortedtrianglespheretree.addAll(Arrays.asList(transformedtrianglespherelist));
						Sphere[] sortedtrianglespherelist = sortedtrianglespheretree.toArray(new Sphere[sortedtrianglespheretree.size()]);
						Direction[] lookdirarray = {lookdir};
						Plane[] lookdirplane = MathLib.planeFromNormalAtPoint(new Position(0,0,0), lookdirarray);
						Line[][] clipplaneint = MathLib.planeTriangleIntersection(lookdirplane, transformedtrianglelist);
						Plane[] triangleplanes = MathLib.planeFromPoints(transformedtrianglelist);
						Direction[] trianglenormals = MathLib.planeNormals(triangleplanes);
						double[] triangleviewangles = MathLib.vectorAngle(lookdir, trianglenormals);
						for (int j=0;j<sortedtrianglespherelist.length;j++) {
							int i = sortedtrianglespherelist[j].ind;
							double triangleviewangle = triangleviewangles[i];
							if (triangleviewangle>90.0f) {triangleviewangle = 180-triangleviewangle;}
							float shadingmultiplier = (90.0f-(((float)triangleviewangle))/1.5f)/90.0f;
							Material copymaterial = copytrianglelist[transformedtrianglelist[i].ind].mat;
							Color tricolor = copymaterial.facecolor;
							float alphacolor = copymaterial.transparency;
							if (tricolor==null) {tricolor = Color.WHITE;}
							float[] tricolorcomp = tricolor.getRGBComponents(new float[4]);
							g2.setColor(new Color(tricolorcomp[0]*shadingmultiplier, tricolorcomp[1]*shadingmultiplier, tricolorcomp[2]*shadingmultiplier, alphacolor));
							if ((transformedtrianglelist[i].pos1.z<=0.0f)||(transformedtrianglelist[i].pos2.z<=0.0f)||(transformedtrianglelist[i].pos3.z<=0.0f)) {
								double pos1s = (-transformedtrianglelist[i].pos1.z)*this.drawdepthscale+1;
								int pos1x = (int)Math.round(transformedtrianglelist[i].pos1.x/pos1s)+this.origindeltax;
								int pos1y = (int)Math.round(transformedtrianglelist[i].pos1.y/pos1s)+this.origindeltay;
								double pos2s = (-transformedtrianglelist[i].pos2.z)*this.drawdepthscale+1;
								int pos2x = (int)Math.round(transformedtrianglelist[i].pos2.x/pos2s)+this.origindeltax;
								int pos2y = (int)Math.round(transformedtrianglelist[i].pos2.y/pos2s)+this.origindeltay;
								double pos3s = (-transformedtrianglelist[i].pos3.z)*this.drawdepthscale+1;
								int pos3x = (int)Math.round(transformedtrianglelist[i].pos3.x/pos3s)+this.origindeltax;
								int pos3y = (int)Math.round(transformedtrianglelist[i].pos3.y/pos3s)+this.origindeltay;
								Polygon trianglepolygon = new Polygon();
								if (clipplaneint[0][i]!=null) {
									Line lineint = clipplaneint[0][i];
									double posi1s = (-lineint.pos1.z)*this.drawdepthscale+1;
									int posi1x = (int)Math.round(lineint.pos1.x/posi1s)+this.origindeltax;
									int posi1y = (int)Math.round(lineint.pos1.y/posi1s)+this.origindeltay;
									double posi2s = (-lineint.pos2.z)*this.drawdepthscale+1;
									int posi2x = (int)Math.round(lineint.pos2.x/posi2s)+this.origindeltax;
									int posi2y = (int)Math.round(lineint.pos2.y/posi2s)+this.origindeltay;
									if (transformedtrianglelist[i].pos1.z<0) {
										trianglepolygon.addPoint(pos1x, pos1y);
									}
									if (lineint.hitind==0) {
										trianglepolygon.addPoint(posi2x, posi2y);
										trianglepolygon.addPoint(posi1x, posi1y);
									}
									if (transformedtrianglelist[i].pos2.z<0) {
										trianglepolygon.addPoint(pos2x, pos2y);
									}
									if (lineint.hitind==1) {
										trianglepolygon.addPoint(posi1x, posi1y);
										trianglepolygon.addPoint(posi2x, posi2y);
									}
									if (transformedtrianglelist[i].pos3.z<0) {
										trianglepolygon.addPoint(pos3x, pos3y);
									}
									if (lineint.hitind==2) {
										trianglepolygon.addPoint(posi2x, posi2y);
										trianglepolygon.addPoint(posi1x, posi1y);
									}
								} else {
									trianglepolygon.addPoint(pos1x, pos1y);
									trianglepolygon.addPoint(pos2x, pos2y);
									trianglepolygon.addPoint(pos3x, pos3y);
								}
								g2.fill(trianglepolygon);
								boolean mouseoverhit = g2.hit(new Rectangle(this.mouselocationx,this.mouselocationy,1,1), trianglepolygon, false);
								if (mouseoverhit) {
									mouseoverhittriangle = copytrianglelist[i];
								}
								g2.setColor(Color.BLACK);
								g2.setStroke(new BasicStroke(this.flatlinestroke));
								g2.draw(trianglepolygon);
							}
						}
					}
				}
			}
		} else {
			g2.setColor(new Color(0.5f, 0.5f, 0.5f, 0.1f));
			if (entitylistmaphandle!=null) {
				for (int k=0;k<entitylistmaphandle.length;k++) {
					Tetrahedron[] tetrahedronlist = entitylistmaphandle[k].tetrahedronlist;
					if (tetrahedronlist!=null) {
						for (int j=0;j<entitylistmaphandle[k].tetrahedronlist.length;j++) {
							Triangle[] tetrahedrontrianglelist = new Triangle[4];
							tetrahedrontrianglelist[0] = new Triangle(tetrahedronlist[j].pos1,tetrahedronlist[j].pos2,tetrahedronlist[j].pos3); 
							tetrahedrontrianglelist[1] = new Triangle(tetrahedronlist[j].pos1,tetrahedronlist[j].pos2,tetrahedronlist[j].pos4); 
							tetrahedrontrianglelist[2] = new Triangle(tetrahedronlist[j].pos1,tetrahedronlist[j].pos3,tetrahedronlist[j].pos4); 
							tetrahedrontrianglelist[3] = new Triangle(tetrahedronlist[j].pos2,tetrahedronlist[j].pos3,tetrahedronlist[j].pos4);
							Triangle[] transformedtetrahedrontrianglelist = MathLib.matrixMultiply(MathLib.translate(tetrahedrontrianglelist, renderpos), rendermat);
							for (int i=0;i<transformedtetrahedrontrianglelist.length;i++) {
								if ((transformedtetrahedrontrianglelist[i].pos1.z<=0.0f)||(transformedtetrahedrontrianglelist[i].pos2.z<=0.0f)||(transformedtetrahedrontrianglelist[i].pos3.z<=0.0f)) {
									double pos1s = (-transformedtetrahedrontrianglelist[i].pos1.z)*this.drawdepthscale+1;
									int pos1x = (int)Math.round(transformedtetrahedrontrianglelist[i].pos1.x/pos1s)+this.origindeltax;
									int pos1y = (int)Math.round(transformedtetrahedrontrianglelist[i].pos1.y/pos1s)+this.origindeltay;
									double pos2s = (-transformedtetrahedrontrianglelist[i].pos2.z)*this.drawdepthscale+1;
									int pos2x = (int)Math.round(transformedtetrahedrontrianglelist[i].pos2.x/pos2s)+this.origindeltax;
									int pos2y = (int)Math.round(transformedtetrahedrontrianglelist[i].pos2.y/pos2s)+this.origindeltay;
									double pos3s = (-transformedtetrahedrontrianglelist[i].pos3.z)*this.drawdepthscale+1;
									int pos3x = (int)Math.round(transformedtetrahedrontrianglelist[i].pos3.x/pos3s)+this.origindeltax;
									int pos3y = (int)Math.round(transformedtetrahedrontrianglelist[i].pos3.y/pos3s)+this.origindeltay;
									Polygon trianglepolygon = new Polygon();
									trianglepolygon.addPoint(pos1x, pos1y);
									trianglepolygon.addPoint(pos2x, pos2y);
									trianglepolygon.addPoint(pos3x, pos3y);
									g2.fill(trianglepolygon);
								}
							}
						}
					}
				}
			}
			Line[] copylinelist = linelistarray.toArray(new Line[linelistarray.size()]);
			Line[] transformedlinelist = MathLib.matrixMultiply(MathLib.translate(copylinelist, renderpos), rendermat);
			for (int i=0;i<transformedlinelist.length;i++) {
				if ((transformedlinelist[i].pos1.z<=0.0f)||(transformedlinelist[i].pos2.z<=0.0f)) {
					double pos1s = (-transformedlinelist[i].pos1.z)*this.drawdepthscale+1;
					int pos1x = (int)Math.round(transformedlinelist[i].pos1.x/pos1s)+this.origindeltax;
					int pos1y = (int)Math.round(transformedlinelist[i].pos1.y/pos1s)+this.origindeltay;
					double pos2s = (-transformedlinelist[i].pos2.z)*this.drawdepthscale+1;
					int pos2x = (int)Math.round(transformedlinelist[i].pos2.x/pos2s)+this.origindeltax;
					int pos2y = (int)Math.round(transformedlinelist[i].pos2.y/pos2s)+this.origindeltay;
					g2.setColor(Color.BLACK);
					if (Math.abs(transformedlinelist[i].pos1.z)<0.5f){g2.setStroke(new BasicStroke(this.vertexstroke+this.vertexfocus));}else{g2.setStroke(new BasicStroke(this.vertexstroke));}
					g2.drawOval(pos1x-this.vertexradius, pos1y-this.vertexradius, this.vertexradius*2, this.vertexradius*2);
					if (Math.abs(transformedlinelist[i].pos2.z)<0.5f){g2.setStroke(new BasicStroke(this.vertexstroke+this.vertexfocus));}else{g2.setStroke(new BasicStroke(this.vertexstroke));}
					g2.drawOval(pos2x-this.vertexradius, pos2y-this.vertexradius, this.vertexradius*2, this.vertexradius*2);
					g2.setColor(Color.BLUE);
					g2.setStroke(new BasicStroke(this.sketchlinestroke));
					g2.drawLine(pos1x, pos1y, pos2x, pos2y);
				}
			}
		}
		Position[] originpoints = {new Position(0,0,0),new Position(this.originlinewidth,0,0),new Position(0,this.originlineheight,0),new Position(0,0,this.originlinedepth)}; 
		Position[] transformedoriginpoints = MathLib.matrixMultiply(MathLib.translate(originpoints, renderpos), rendermat);
		double posas = -transformedoriginpoints[0].z*this.drawdepthscale+1;
		double posasz = -transformedoriginpoints[3].z*this.drawdepthscale+1;
		if (posasz>0) {
			g2.setStroke(new BasicStroke(this.axisstroke));
			g2.setColor(Color.RED);
			g2.drawLine((int)Math.round(transformedoriginpoints[0].x/posas)+this.origindeltax, (int)Math.round(transformedoriginpoints[0].y/posas)+this.origindeltay, (int)Math.round(transformedoriginpoints[1].x/posas)+this.origindeltax, (int)Math.round(transformedoriginpoints[1].y/posas)+this.origindeltay);
			g2.setColor(Color.GREEN);
			g2.drawLine((int)Math.round(transformedoriginpoints[0].x/posas)+this.origindeltax, (int)Math.round(transformedoriginpoints[0].y/posas)+this.origindeltay, (int)Math.round(transformedoriginpoints[2].x/posas)+this.origindeltax, (int)Math.round(transformedoriginpoints[2].y/posas)+this.origindeltay);
			g2.setColor(Color.BLUE);
			g2.drawLine((int)Math.round(transformedoriginpoints[0].x/posas)+this.origindeltax, (int)Math.round(transformedoriginpoints[0].y/posas)+this.origindeltay, (int)Math.round(transformedoriginpoints[3].x/posasz)+this.origindeltax, (int)Math.round(transformedoriginpoints[3].y/posasz)+this.origindeltay);
			g2.setColor(Color.BLACK);
			g2.fillOval((int)Math.round(transformedoriginpoints[0].x/posas)+this.origindeltax-this.vertexradius, (int)Math.round(transformedoriginpoints[0].y/posas)+this.origindeltay-this.vertexradius, this.vertexradius*2, this.vertexradius*2);
		}
		this.mouseovertriangle = mouseoverhittriangle;
	}

	private int snapToGrid(int coordinate) {
		return this.gridstep*(int)Math.round(((double)coordinate)/((double)this.gridstep));
	}
	private int getVertexAtMouse() {
		int k = -1;
		Sphere[] vsphere1 = new Sphere[1]; vsphere1[0] = new Sphere(this.mouselocationx-this.origindeltax+this.cameralocationx, this.mouselocationy-this.origindeltay+this.cameralocationy, this.drawdepth, 0); 
		Sphere[] vsphere2 = new Sphere[2*this.linelistarray.size()];
		for (int i=0;i<linelistarray.size();i++) {
			vsphere2[2*i] = new Sphere(linelistarray.get(i).pos1.x, linelistarray.get(i).pos1.y, linelistarray.get(i).pos1.z, this.vertexradius);
			vsphere2[2*i+1] = new Sphere(linelistarray.get(i).pos2.x, linelistarray.get(i).pos2.y, linelistarray.get(i).pos2.z, this.vertexradius);
		}
		boolean[][] ssint = MathLib.sphereSphereIntersection(vsphere1, vsphere2);
		if (ssint!=null) {
    		for (int i=0;i<ssint[0].length;i++) {
    			if ((ssint[0][i])&&(Math.abs(vsphere2[i].z-this.drawdepth)<0.5f)) {
    				k = i;
    			}
    		}
		}
		return k;
	}
	
	@Override public void timerTick() {
		if (this.leftkeydown) {
			this.cameralocationx -= this.gridstep;
		} else if (this.rightkeydown) {
			this.cameralocationx += this.gridstep;
		}
		if (this.upwardkeydown) {
			this.cameralocationy -= this.gridstep;
		} else if (this.downwardkeydown) {
			this.cameralocationy += this.gridstep;
		}
		if (this.forwardkeydown) {
			if (this.snaplinemode) {
				this.drawdepth = snapToGrid(this.drawdepth+this.gridstep);
			} else {
				this.drawdepth += 1;
			}
		} else if (this.backwardkeydown) {
			if (this.snaplinemode) {
				this.drawdepth = snapToGrid(this.drawdepth-this.gridstep);
			} else {
				this.drawdepth -= 1;
			}
		}
	}
	
	@Override public void keyTyped(KeyEvent e) {}
	@Override public void keyReleased(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_SHIFT) {
			this.snaplinemode = false;
		} else if (e.getKeyCode()==KeyEvent.VK_A) {
			this.leftkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_D) {
			this.rightkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_W) {
			this.upwardkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_S) {
			this.downwardkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_SUBTRACT) {
			this.backwardkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_ADD) {
			this.forwardkeydown = false;
		}
	}
	@Override public void keyPressed(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_BACK_SPACE) {
			this.linelistarray.clear();
			this.entitylist = null;
			this.drawdepth = 0;
			this.cameralocationx = 0;
			this.cameralocationy = 0;
			this.camrot.x = 0;
			this.camrot.y = 0;
			this.camrot.z = 0;
		} else if (e.getKeyCode()==KeyEvent.VK_INSERT) {
			this.drawcolorhsb[0] += 0.01f;
			if (this.drawcolorhsb[0]>1.0f) {this.drawcolorhsb[0] = 0.0f;}
			Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		} else if (e.getKeyCode()==KeyEvent.VK_DELETE) {
			this.drawcolorhsb[0] -= 0.01f;
			if (this.drawcolorhsb[0]<0.0f) {this.drawcolorhsb[0] = 1.0f;}
			Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		} else if (e.getKeyCode()==KeyEvent.VK_HOME) {
			this.drawcolorhsb[1] += 0.01f;
			if (this.drawcolorhsb[1]>1.0f) {this.drawcolorhsb[1] = 1.0f;}
			Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		} else if (e.getKeyCode()==KeyEvent.VK_END) {
			this.drawcolorhsb[1] -= 0.01f;
			if (this.drawcolorhsb[1]<0.0f) {this.drawcolorhsb[1] = 0.0f;}
			Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		} else if (e.getKeyCode()==KeyEvent.VK_PAGE_UP) {
			this.drawcolorhsb[2] += 0.01f;
			if (this.drawcolorhsb[2]>1.0f) {this.drawcolorhsb[2] = 1.0f;}
			Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		} else if (e.getKeyCode()==KeyEvent.VK_PAGE_DOWN) {
			this.drawcolorhsb[2] -= 0.01f;
			if (this.drawcolorhsb[2]<0.0f) {this.drawcolorhsb[2] = 0.0f;}
			Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD9) {
			this.penciltransparency += 0.01f;
			if (this.penciltransparency>1.0f) {this.penciltransparency = 1.0f;}
			float[] colorvalues = this.drawcolor.getRGBColorComponents(new float[3]);
			this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD8) {
			this.penciltransparency -= 0.01f;
			if (this.penciltransparency<0.0f) {this.penciltransparency = 0.0f;}
			float[] colorvalues = this.drawcolor.getRGBColorComponents(new float[3]);
			this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		} else if (e.getKeyCode()==KeyEvent.VK_ENTER) {
			this.polygonfillmode += 1;
			if (this.polygonfillmode>2) {
				this.polygonfillmode = 1;
			}
		} else if (e.getKeyCode()==KeyEvent.VK_SHIFT) {
			this.snaplinemode = true;
		} else if (e.getKeyCode()==KeyEvent.VK_A) {
			this.leftkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_D) {
			this.rightkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_W) {
			this.upwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_S) {
			this.downwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_ADD) {
			this.forwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_SUBTRACT) {
			this.backwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_F2) {
			this.filechooser.setDialogTitle("Save File");
			this.filechooser.setApproveButtonText("Save");
			if (this.filechooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
			    int onmask = KeyEvent.SHIFT_DOWN_MASK;
			    int offmask = KeyEvent.ALT_DOWN_MASK|KeyEvent.CTRL_DOWN_MASK;
			    boolean f2shiftdown = (e.getModifiersEx() & (onmask | offmask)) == onmask;
				File savefile = this.filechooser.getSelectedFile();
				FileFilter savefileformat = this.filechooser.getFileFilter();
				if (savefileformat.equals(this.stlfilefilter)) {
					ArrayList<Triangle> savemodeltrianglearray = new ArrayList<Triangle>();  
					for (int i=0;i<this.entitylist.length;i++) {
						Triangle[] copytrianglelist = this.entitylist[i].trianglelist;
					    if (f2shiftdown) {
					    	copytrianglelist = this.entitylist[i].surfacelist;
					    }
					    savemodeltrianglearray.addAll(Arrays.asList(copytrianglelist));
					}
					Triangle[] savemodel = savemodeltrianglearray.toArray(new Triangle[savemodeltrianglearray.size()]);
					String savestlfile = savefile.getPath();
					if (savestlfile.toLowerCase().endsWith(".stl")) {
						savestlfile = savestlfile.substring(0, savestlfile.length()-4).concat(".stl");
					} else {
						savestlfile = savestlfile.concat(".stl");
					}
					ModelLib.saveSTLFile(savestlfile, savemodel, "JREOBJ");
				} else {
					Model savemodel = new Model(savefile.getPath());
					String saveobjfile = savefile.getPath();
					String savemtlfile = savefile.getName();
					if (savemtlfile.toLowerCase().endsWith(".obj")) {
						savemtlfile = savemtlfile.substring(0, savemtlfile.length()-4).concat(".mtl");
					} else {
						saveobjfile = saveobjfile.concat(".obj");
						savemtlfile = savemtlfile.concat(".mtl");
					}
					savemodel.mtllib = savemtlfile;
					TreeSet<Material> materiallistarray = new TreeSet<Material>();
					TreeSet<Position> vertexlistarray = new TreeSet<Position>();
					TreeSet<Direction> normallistarray = new TreeSet<Direction>();
					savemodel.objects = new ModelObject[this.entitylist.length];
					normallistarray.add(new Direction(0, 0, 0));
					for (int j=0;j<this.entitylist.length;j++) {
						savemodel.objects[j] = new ModelObject("JREOBJ"+(j+1));
						Triangle[] copytrianglelist = this.entitylist[j].trianglelist;
					    if (f2shiftdown) {
					    	copytrianglelist = this.entitylist[j].surfacelist;
					    } else {
					    	vertexlistarray.addAll(Arrays.asList(this.entitylist[j].vertexlist));
					    }
						for (int i=0;i<copytrianglelist.length;i++) {
							materiallistarray.add(copytrianglelist[i].mat);
							vertexlistarray.add(copytrianglelist[i].pos1);
							vertexlistarray.add(copytrianglelist[i].pos2);
							vertexlistarray.add(copytrianglelist[i].pos3);
							normallistarray.add(copytrianglelist[i].norm);
						}
					}
					savemodel.materials = materiallistarray.toArray(new Material[materiallistarray.size()]);
					savemodel.vertexlist = vertexlistarray.toArray(new Position[vertexlistarray.size()]);
					savemodel.facenormals = normallistarray.toArray(new Direction[normallistarray.size()]);
					savemodel.texturecoords = new Coordinate[1];
					savemodel.texturecoords[0] = new Coordinate(0, 0);
					for (int i=0;i<savemodel.materials.length;i++) {
						savemodel.materials[i].materialname = "JREMAT"+(i+1);
					}
					for (int j=0;j<this.entitylist.length;j++) {
						Triangle[] copytrianglelist = this.entitylist[j].trianglelist;
					    if (f2shiftdown) {
					    	copytrianglelist = this.entitylist[j].surfacelist;
					    }
						for (int i=0;i<copytrianglelist.length;i++) {
							ModelFaceVertexIndex[] trianglevertex = new ModelFaceVertexIndex[3];
							int trianglefacenormalind = Arrays.binarySearch(savemodel.facenormals, copytrianglelist[i].norm)+1;
							trianglevertex[0] = new ModelFaceVertexIndex(Arrays.binarySearch(savemodel.vertexlist, copytrianglelist[i].pos1)+1,1,trianglefacenormalind);
							trianglevertex[1] = new ModelFaceVertexIndex(Arrays.binarySearch(savemodel.vertexlist, copytrianglelist[i].pos2)+1,1,trianglefacenormalind);
							trianglevertex[2] = new ModelFaceVertexIndex(Arrays.binarySearch(savemodel.vertexlist, copytrianglelist[i].pos3)+1,1,trianglefacenormalind);
							Material copymaterial = copytrianglelist[i].mat;
							int searchmatindex = Arrays.binarySearch(savemodel.materials, copymaterial);
							ModelFaceIndex[] objectfaceindex = savemodel.objects[j].faceindex;
							ArrayList<ModelFaceIndex> faceindexarray = (objectfaceindex!=null)?(new ArrayList<ModelFaceIndex>(Arrays.asList(objectfaceindex))):(new ArrayList<ModelFaceIndex>());
							ModelFaceIndex newmodelfaceindex = new ModelFaceIndex(trianglevertex);
							newmodelfaceindex.usemtl = savemodel.materials[searchmatindex].materialname;
							faceindexarray.add(newmodelfaceindex);
							savemodel.objects[j].faceindex = faceindexarray.toArray(new ModelFaceIndex[faceindexarray.size()]);
						}
						if (this.entitylist[j].linelist!=null) {
							Line[] uniquelinelist = this.entitylist[j].linelist;
							for (int i=0;i<uniquelinelist.length;i++) {
								if (uniquelinelist[i].pos1.compareTo(uniquelinelist[i].pos2)!=0) {
									int[] linevertex = new int[2];
									linevertex[0] = Arrays.binarySearch(savemodel.vertexlist, uniquelinelist[i].pos1)+1;
									linevertex[1] = Arrays.binarySearch(savemodel.vertexlist, uniquelinelist[i].pos2)+1;
									ArrayList<ModelLineIndex> lineindexarray = (savemodel.objects[j].lineindex!=null)?(new ArrayList<ModelLineIndex>(Arrays.asList(savemodel.objects[j].lineindex))):(new ArrayList<ModelLineIndex>());
									lineindexarray.add(new ModelLineIndex(linevertex));
									savemodel.objects[j].lineindex = lineindexarray.toArray(new ModelLineIndex[lineindexarray.size()]);
								} else {
									int[] linevertex = new int[1];
									linevertex[0] = Arrays.binarySearch(savemodel.vertexlist, uniquelinelist[i].pos1)+1;
									ArrayList<ModelLineIndex> lineindexarray = (savemodel.objects[j].lineindex!=null)?(new ArrayList<ModelLineIndex>(Arrays.asList(savemodel.objects[j].lineindex))):(new ArrayList<ModelLineIndex>());
									lineindexarray.add(new ModelLineIndex(linevertex));
									savemodel.objects[j].lineindex = lineindexarray.toArray(new ModelLineIndex[lineindexarray.size()]);
								}
							}
						}
					}
					ModelLib.saveWaveFrontOBJFile(saveobjfile, savemodel);
				}
			}
		} else if (e.getKeyCode()==KeyEvent.VK_F3) {
			this.filechooser.setDialogTitle("Load File");
			this.filechooser.setApproveButtonText("Load");
			if (this.filechooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				File loadfile = this.filechooser.getSelectedFile();
				FileFilter loadfileformat = this.filechooser.getFileFilter();
				if (loadfileformat.equals(this.stlfilefilter)) {
					Entity[] newentitylist = {new Entity()};
					newentitylist[0].trianglelist = ModelLib.loadSTLFile(loadfile.getPath(), false);
					for (int i=0;i<newentitylist[0].trianglelist.length;i++) {
						if (newentitylist[0].trianglelist[i].mat==null) {
							Material newmat = new Material();
							newmat.facecolor = Color.WHITE;
							newentitylist[0].trianglelist[i].mat = newmat;
						}
					}
					this.linelistarray.addAll(Arrays.asList(MathLib.generateLineList(newentitylist[0].trianglelist)));
					this.entitylist = newentitylist;
				} else {
					ArrayList<Entity> newentitylistarray = new ArrayList<Entity>(); 
					Model loadmodel = ModelLib.loadWaveFrontOBJFile(loadfile.getPath(), false);
					TreeSet<Line> uniquelinetree = new TreeSet<Line>();
					ArrayList<Material> materiallisttree = new ArrayList<Material>(Arrays.asList(loadmodel.materials));
					Material[] copymateriallist = materiallisttree.toArray(new Material[materiallisttree.size()]);
					for (int i=0;i<copymateriallist.length;i++) {
						if (copymateriallist[i].facecolor==null) {
							copymateriallist[i].facecolor = Color.WHITE;
						}
					}
					for (int k=0;k<loadmodel.objects.length;k++) {
						newentitylistarray.add(new Entity());
						for (int j=0;j<loadmodel.objects[k].faceindex.length;j++) {
							Material foundmat = null;
							for (int i=0;(i<copymateriallist.length)&&(foundmat==null);i++) {
								if (loadmodel.objects[k].faceindex[j].usemtl.equals(copymateriallist[i].materialname)) {
									foundmat = copymateriallist[i];
								}
							}
							Position[] loadvertex = new Position[loadmodel.objects[k].faceindex[j].facevertexindex.length];
							for (int i=0;i<loadmodel.objects[k].faceindex[j].facevertexindex.length;i++) {
								loadvertex[i] = loadmodel.vertexlist[loadmodel.objects[k].faceindex[j].facevertexindex[i].vertexindex-1];
								if (i>0) {
									uniquelinetree.add(new Line(loadvertex[i-1].copy(),loadvertex[i].copy()));
								}
							}
							if (loadmodel.objects[k].faceindex[j].facevertexindex.length>2) {
								uniquelinetree.add(new Line(loadvertex[loadmodel.objects[k].faceindex[j].facevertexindex.length-1].copy(),loadvertex[0].copy()));
							} else if (loadmodel.objects[k].faceindex[j].facevertexindex.length==1) {
								uniquelinetree.add(new Line(loadvertex[0].copy(),loadvertex[0].copy()));
							}
							if (loadmodel.objects[k].faceindex[j].facevertexindex.length==3) {
								ArrayList<Triangle> newtrianglelistarray = new ArrayList<Triangle>();
								if (newentitylistarray.get(newentitylistarray.size()-1).trianglelist!=null) {newtrianglelistarray.addAll(Arrays.asList(newentitylistarray.get(newentitylistarray.size()-1).trianglelist));}
								Triangle newtriangle = new Triangle(loadvertex[0],loadvertex[1],loadvertex[2]);
								newtriangle.mat = foundmat;
								newtriangle.norm = loadmodel.facenormals[loadmodel.objects[k].faceindex[j].facevertexindex[0].normalindex-1];
								newtrianglelistarray.add(newtriangle);
								newentitylistarray.get(newentitylistarray.size()-1).trianglelist = newtrianglelistarray.toArray(new Triangle[newtrianglelistarray.size()]);
							}
						}
						for (int j=0;j<loadmodel.objects[k].lineindex.length;j++) {
							Position[] loadvertex = new Position[loadmodel.objects[k].lineindex[j].linevertexindex.length];
							for (int i=0;i<loadmodel.objects[k].lineindex[j].linevertexindex.length;i++) {
								loadvertex[i] = loadmodel.vertexlist[loadmodel.objects[k].lineindex[j].linevertexindex[i]-1];
								if (i>0) {
									uniquelinetree.add(new Line(loadvertex[i-1].copy(),loadvertex[i].copy()));
								}
							}
							if (loadmodel.objects[k].lineindex[j].linevertexindex.length==1) {
								uniquelinetree.add(new Line(loadvertex[0].copy(),loadvertex[0].copy()));
							}
						}
					}
					this.linelistarray.addAll(uniquelinetree);
					this.entitylist = newentitylistarray.toArray(new Entity[newentitylistarray.size()]);
				}
				(new EntityListUpdater()).start();
			}
		}
	}
	
	@Override public void mouseMoved(MouseEvent e) {
		this.mouselocationx=e.getX();this.mouselocationy=e.getY();
		
	}
	@Override public void mousePressed(MouseEvent e) {
		this.mouselocationx=e.getX();this.mouselocationy=e.getY();
		this.mousestartlocationx=this.mouselocationx;this.mousestartlocationy=this.mouselocationy;
		this.drawstartdepth = this.drawdepth;
	    int onmask1ctrldown = MouseEvent.BUTTON1_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
	    int offmask1ctrldown = MouseEvent.ALT_DOWN_MASK;
	    boolean mouse1ctrldown = ((e.getModifiersEx() & (onmask1ctrldown | offmask1ctrldown)) == onmask1ctrldown);
    	if (mouse1ctrldown) {
    		int vertexatmouse = getVertexAtMouse();
			if (vertexatmouse!=-1) {
				this.draglinemode = true;
				this.selecteddragvertex = vertexatmouse;
			}
    	}
	    int onmask1alt = MouseEvent.BUTTON1_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    int offmask1alt = MouseEvent.CTRL_DOWN_MASK;
	    boolean mouse1altdown = ((e.getModifiersEx() & (onmask1alt | offmask1alt)) == onmask1alt);
    	if (mouse1altdown) {
			int drawstartlocationx = this.mousestartlocationx-this.origindeltax+this.cameralocationx;
			int drawstartlocationy = this.mousestartlocationy-this.origindeltay+this.cameralocationy;
			int drawlocationx = this.mouselocationx-this.origindeltax+this.cameralocationx;
			int drawlocationy = this.mouselocationy-this.origindeltay+this.cameralocationy;
			if (this.snaplinemode) {
				drawstartlocationx = snapToGrid(drawstartlocationx);
				drawstartlocationy = snapToGrid(drawstartlocationy);
				drawlocationx = snapToGrid(drawlocationx);
				drawlocationy = snapToGrid(drawlocationy);
				int snaptovertex = getVertexAtMouse();
				if (snaptovertex!=-1) {
    				int snaptovertexlinenum = Math.floorDiv(snaptovertex,2);
    				boolean snaptovertexfirstvertex = Math.floorMod(snaptovertex,2)==0;
    				if (snaptovertexfirstvertex) {
    					drawstartlocationx = (int)this.linelistarray.get(snaptovertexlinenum).pos1.x;
    					drawstartlocationy = (int)this.linelistarray.get(snaptovertexlinenum).pos1.y;
	    			} else {
	    				drawstartlocationx = (int)this.linelistarray.get(snaptovertexlinenum).pos2.x;
	    				drawstartlocationy = (int)this.linelistarray.get(snaptovertexlinenum).pos2.y;
	    			}
				}
			}
			this.linelistarray.add(new Line(new Position(drawstartlocationx, drawstartlocationy, this.drawstartdepth), new Position(drawlocationx, drawlocationy, this.drawdepth)));
			this.draglinemode = true;
			this.selecteddragvertex = (this.linelistarray.size()-1)*2+1;
			this.updatetrianglelist = true;
    	}
	    int onmask3altdown = MouseEvent.BUTTON3_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    int offmask3altdown = MouseEvent.CTRL_DOWN_MASK;
	    boolean mouse3altdown = ((e.getModifiersEx() & (onmask3altdown | offmask3altdown)) == onmask3altdown);
    	if (mouse3altdown) {
    		int vertexatmouse = getVertexAtMouse();
			if (vertexatmouse!=-1) {
				int linenum = Math.floorDiv(vertexatmouse,2);
				this.linelistarray.remove(linenum);
				this.updatetrianglelist = true;
			}
    	}
		mouseDragged(e);
	}
	@Override public void mouseReleased(MouseEvent e) {
	    boolean mouse1up = e.getButton()==MouseEvent.BUTTON1;
	    boolean mouse3up = e.getButton()==MouseEvent.BUTTON3;
		if (mouse1up||mouse3up) {
			if (mouse1up) {
				if (this.draglinemode) {
					this.draglinemode = false;
				}
			}
		}
		if (this.updatetrianglelist) {
			this.updatetrianglelist = false;
			(new EntityListUpdater()).start(); 
		}
	}
	
	public void mouseDragged(MouseEvent e) {
		this.mouselocationx=e.getX();this.mouselocationy=e.getY();
	    int onmask1down = MouseEvent.BUTTON1_DOWN_MASK;
	    int offmask1down = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK|MouseEvent.SHIFT_DOWN_MASK;
	    boolean mouse1down = ((e.getModifiersEx() & (onmask1down | offmask1down)) == onmask1down);
    	if (mouse1down) {
    		if (this.mouseovertriangle!=null) {
				Material newmaterial = new Material();
				newmaterial.facecolor = this.drawcolor;
				newmaterial.transparency = this.penciltransparency;
				this.mouseovertriangle.mat = newmaterial;
    		}
    	}
	    int onmask1shiftdown = MouseEvent.BUTTON1_DOWN_MASK|MouseEvent.SHIFT_DOWN_MASK;
	    int offmask1shiftdown = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    boolean mouse1shiftdown = ((e.getModifiersEx() & (onmask1shiftdown | offmask1shiftdown)) == onmask1shiftdown);
	    if (mouse1shiftdown) {
    		if (this.mouseovertriangle!=null) {
    			this.penciltransparency = this.mouseovertriangle.mat.transparency;
    			int colorvalue = this.mouseovertriangle.mat.facecolor.getRGB();
				Color pickeddrawcolor = new Color(colorvalue);
				this.drawcolorhsb = Color.RGBtoHSB(pickeddrawcolor.getRed(), pickeddrawcolor.getGreen(), pickeddrawcolor.getBlue(), new float[3]);
				float[] colorvalues = pickeddrawcolor.getRGBColorComponents(new float[3]);
				this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
    		}
	    }
	    int onmask1ctrldown = MouseEvent.BUTTON1_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
	    int offmask1ctrldown = MouseEvent.ALT_DOWN_MASK;
	    boolean mouse1ctrldown = ((e.getModifiersEx() & (onmask1ctrldown | offmask1ctrldown)) == onmask1ctrldown);
	    int onmask1altdown = MouseEvent.BUTTON1_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    int offmask1altdown = MouseEvent.CTRL_DOWN_MASK;
	    boolean mouse1altdown = ((e.getModifiersEx() & (onmask1altdown | offmask1altdown)) == onmask1altdown);
    	if (mouse1ctrldown||mouse1altdown) {
    		if (this.draglinemode) {
				int linenum = Math.floorDiv(this.selecteddragvertex,2);
				boolean firstvertex = Math.floorMod(this.selecteddragvertex,2)==0;
				int drawlocationx = this.mouselocationx-this.origindeltax+this.cameralocationx;
				int drawlocationy = this.mouselocationy-this.origindeltay+this.cameralocationy;
				if (this.snaplinemode) {
					drawlocationx = snapToGrid(drawlocationx);
					drawlocationy = snapToGrid(drawlocationy);
					int snaptovertex = getVertexAtMouse();
					if (snaptovertex!=-1) {
	    				int snaptovertexlinenum = Math.floorDiv(snaptovertex,2);
	    				boolean snaptovertexfirstvertex = Math.floorMod(snaptovertex,2)==0;
	    				if (snaptovertexfirstvertex) {
	    					drawlocationx = (int)this.linelistarray.get(snaptovertexlinenum).pos1.x;
	    					drawlocationy = (int)this.linelistarray.get(snaptovertexlinenum).pos1.y;
		    			} else {
		    				drawlocationx = (int)this.linelistarray.get(snaptovertexlinenum).pos2.x;
		    				drawlocationy = (int)this.linelistarray.get(snaptovertexlinenum).pos2.y;
		    			}
					}
				}
				if (firstvertex) {
					this.linelistarray.get(linenum).pos1.x = drawlocationx;
					this.linelistarray.get(linenum).pos1.y = drawlocationy;
					this.linelistarray.get(linenum).pos1.z = this.drawdepth;
    			} else {
					this.linelistarray.get(linenum).pos2.x = drawlocationx;
					this.linelistarray.get(linenum).pos2.y = drawlocationy;
					this.linelistarray.get(linenum).pos2.z = this.drawdepth;
    			}
				this.updatetrianglelist = true;
    		}
		}
	}
	@Override public void mouseWheelMoved(MouseWheelEvent e) {
		if (this.snaplinemode) {
			this.drawdepth = snapToGrid(this.drawdepth+this.gridstep*e.getWheelRotation());
		} else {
			this.drawdepth += e.getWheelRotation();
		}
	}
	
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void drop(DropTargetDropEvent dtde) {}

	private synchronized void updateEntityList() {
		ArrayList<Triangle> entitylisttrianglearray = new ArrayList<Triangle>();
		if (this.entitylist!=null) {
			for (int i=0;i<this.entitylist.length;i++) {
				if (this.entitylist[i].trianglelist!=null) {
					entitylisttrianglearray.addAll(Arrays.asList(this.entitylist[i].trianglelist));
				}
			}
		}
		Line[] copylinelist = linelistarray.toArray(new Line[linelistarray.size()]);
		Entity[] newentitylist = MathLib.generateEntityList(copylinelist);
		Material newmat = new Material();
		newmat.facecolor = this.drawcolor;
		newmat.transparency = this.penciltransparency;
		for (int j=0;j<newentitylist.length;j++) {
			for (int i=0;i<newentitylist[j].trianglelist.length;i++) {
				Material foundmat = newmat;
				int searchindex = entitylisttrianglearray.indexOf(newentitylist[j].trianglelist[i]);
				if (searchindex>=0) {
					Material searchmat = entitylisttrianglearray.get(searchindex).mat;
					if (searchmat!=null) {
						foundmat = searchmat;
					}
				}
				newentitylist[j].trianglelist[i].mat = foundmat;
				newentitylist[j].trianglelist[i].norm = new Direction(0.0f,0.0f,0.0f);
			}
			for (int i=0;i<newentitylist[j].surfacelist.length;i++) {
				Material foundmat = newmat;
				int searchindex = entitylisttrianglearray.indexOf(newentitylist[j].surfacelist[i]);
				if (searchindex>=0) {
					Material searchmat = entitylisttrianglearray.get(searchindex).mat;
					if (searchmat!=null) {
						foundmat = searchmat;
					}
				}
				newentitylist[j].surfacelist[i].mat = foundmat;
			}
		}
		this.entitylist = newentitylist;
	}
	
	private class EntityListUpdater extends Thread {
		public void run() {
			updateEntityList();
		}
	}
	
}
