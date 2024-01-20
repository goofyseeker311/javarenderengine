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
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeSet;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import fi.jkauppa.javarenderengine.JavaRenderEngine.AppHandlerPanel;
import fi.jkauppa.javarenderengine.ModelLib.Coordinate;
import fi.jkauppa.javarenderengine.ModelLib.Direction;
import fi.jkauppa.javarenderengine.ModelLib.Entity;
import fi.jkauppa.javarenderengine.ModelLib.Line;
import fi.jkauppa.javarenderengine.ModelLib.Material;
import fi.jkauppa.javarenderengine.ModelLib.Matrix;
import fi.jkauppa.javarenderengine.UtilLib.ModelFileFilters.OBJFileFilter;
import fi.jkauppa.javarenderengine.UtilLib.ModelFileFilters.STLFileFilter;
import fi.jkauppa.javarenderengine.ModelLib.Plane;
import fi.jkauppa.javarenderengine.ModelLib.Position;
import fi.jkauppa.javarenderengine.ModelLib.Rotation;
import fi.jkauppa.javarenderengine.ModelLib.Sphere;
import fi.jkauppa.javarenderengine.ModelLib.Sphere.SphereDistanceComparator;
import fi.jkauppa.javarenderengine.ModelLib.Tetrahedron;
import fi.jkauppa.javarenderengine.ModelLib.Triangle;
import fi.jkauppa.javarenderengine.ModelLib.Model;
import fi.jkauppa.javarenderengine.ModelLib.ModelFaceIndex;
import fi.jkauppa.javarenderengine.ModelLib.ModelFaceVertexIndex;
import fi.jkauppa.javarenderengine.ModelLib.ModelLineIndex;
import fi.jkauppa.javarenderengine.ModelLib.ModelObject;

public class CADApp extends AppHandlerPanel {
	private static final long serialVersionUID = 1L;
	private TexturePaint bgpattern = null;
	private boolean draglinemode = false;
	private boolean snaplinemode = false;
	private Color drawcolor = Color.BLACK;
	private float[] drawcolorhsb = {0.0f, 1.0f, 0.0f};
	private float penciltransparency = 1.0f;
	private int polygonfillmode = 1;
	private Position[] selecteddragvertex = null;
	private Triangle[] mouseovertriangle = null;
	private Position[] mouseoververtex = null;
	private Line[] mouseoverline = null;
	private int mouselocationx = 0, mouselocationy = 0;
	private int mouselastlocationx = -1, mouselastlocationy = -1; 
	private int origindeltax = 0, origindeltay = 0;
	private double editplanedistance = 1371.0f;
	private Position drawstartpos = new Position(0,0,0);
	private Position editpos = new Position(0.0f,0.0f,0.0f);
	private Position campos = new Position(0,0,this.editplanedistance);
	private Rotation camrot = new Rotation(0.0f, 0.0f, 0.0f);
	private Matrix cameramat = MathLib.rotationMatrix(0.0f, 0.0f, 0.0f);
	private final Direction[] lookdirs = MathLib.projectedCameraDirections(cameramat);
	private Direction[] camdirs = lookdirs;
	private Plane[] editplanes = MathLib.planeFromNormalAtPoint(this.editpos, this.camdirs); 
	private double hfov = 70.0f;
	private double vfov = 43.0f;
	private final int originlinewidth = 100;
	private final int originlineheight = 100;
	private final int originlinedepth = 100;
	private final int vertexradius = 2;
	private final int axisstroke = 2;
	private final int vertexstroke = 2;
	private final int vertexfocus = 3;
	private final int sketchlinestroke = 2;
	private final int gridstep = 20;
	private final double pointdist = 1.0f;
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
	private boolean rollrightkeydown = false;
	private boolean rollleftkeydown = false;
	
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
		this.origindeltax = (int)Math.floor(((double)this.getWidth())/2.0f);
		this.origindeltay = (int)Math.floor(((double)this.getHeight())/2.0f);
		this.bgpattern = new TexturePaint(this.bgpatternimage,new Rectangle(this.origindeltax, this.origindeltay, gridstep, gridstep));
		g2.setComposite(AlphaComposite.Src);
		g2.setColor(null);
		g2.setPaint(bgpattern);
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		g2.setPaint(null);
		g2.setColor(null);
		g2.setComposite(AlphaComposite.SrcOver);
		this.vfov = 2.0f*MathLib.atand((((double)this.getHeight())/((double)this.getWidth()))*MathLib.tand((this.hfov/2.0f)));
		this.editplanedistance = (((double)this.getWidth())/2.0f)/MathLib.tand(this.hfov/2.0f);
		ArrayList<Triangle> mouseoverhittriangle = new ArrayList<Triangle>();
		ArrayList<Position> mouseoverhitvertex = new ArrayList<Position>(); 
		ArrayList<Line> mouseoverhitline = new ArrayList<Line>();
		Entity[] entitylistmaphandle = this.entitylist;
		if (this.polygonfillmode==2) {
			if (entitylistmaphandle!=null) {
				Sphere[] entityspherelist = new Sphere[entitylistmaphandle.length]; 
				for (int k=0;k<entitylistmaphandle.length;k++) {
					entityspherelist[k] = entitylistmaphandle[k].sphereboundaryvolume;
					entityspherelist[k].ind = k;
				}
				TreeSet<Sphere> sortedentityspheretree = new TreeSet<Sphere>(new SphereDistanceComparator(this.campos));
				sortedentityspheretree.addAll(Arrays.asList(entityspherelist));
				Sphere[] sortedentityspherelist = sortedentityspheretree.toArray(new Sphere[sortedentityspheretree.size()]);
				for (int k=0;k<sortedentityspherelist.length;k++) {
					Triangle[] entitytrianglelist = entitylistmaphandle[sortedentityspherelist[k].ind].trianglelist;
					if (entitytrianglelist!=null) {
						if (entitytrianglelist.length>0) {
							Triangle[] copytrianglelist = MathLib.subDivideTriangle(entitytrianglelist);
							for (int i=0;i<copytrianglelist.length;i++) {copytrianglelist[i].ind = i;}
							Sphere[] copytrianglespherelist = MathLib.triangleCircumSphere(copytrianglelist);
							for (int i=0;i<copytrianglespherelist.length;i++) {copytrianglespherelist[i].ind = i;}
							TreeSet<Sphere> sortedtrianglespheretree = new TreeSet<Sphere>(new SphereDistanceComparator(this.campos));
							sortedtrianglespheretree.addAll(Arrays.asList(copytrianglespherelist));
							Sphere[] sortedtrianglespherelist = sortedtrianglespheretree.toArray(new Sphere[sortedtrianglespheretree.size()]);
							Plane[] triangleplanes = MathLib.planeFromPoints(copytrianglelist);
							Direction[] trianglenormals = MathLib.planeNormals(triangleplanes);
							double[] triangleviewangles = MathLib.vectorAngle(this.camdirs[0], trianglenormals);
							Coordinate[][] copytrianglelistcoords = MathLib.projectedTriangles(this.campos, copytrianglelist, this.getWidth(), this.hfov, this.getHeight(), this.vfov, this.cameramat);
							for (int j=0;j<sortedtrianglespherelist.length;j++) {
								Coordinate coord1 = copytrianglelistcoords[sortedtrianglespherelist[j].ind][0];
								Coordinate coord2 = copytrianglelistcoords[sortedtrianglespherelist[j].ind][1];
								Coordinate coord3 = copytrianglelistcoords[sortedtrianglespherelist[j].ind][2];
								int i = sortedtrianglespherelist[j].ind;
								Triangle copytriangle = copytrianglelist[i];
								double triangleviewangle = triangleviewangles[i];
								if (triangleviewangle>90.0f) {triangleviewangle = 180-triangleviewangle;}
								float shadingmultiplier = (90.0f-(((float)triangleviewangle))/1.5f)/90.0f;
								Material copymaterial = copytriangle.mat;
								Color tricolor = copymaterial.facecolor;
								float alphacolor = copymaterial.transparency;
								if (tricolor==null) {tricolor = Color.WHITE;}
								float[] tricolorcomp = tricolor.getRGBComponents(new float[4]);
								g2.setColor(new Color(tricolorcomp[0]*shadingmultiplier, tricolorcomp[1]*shadingmultiplier, tricolorcomp[2]*shadingmultiplier, alphacolor));
								if ((coord1!=null)&&(coord2!=null)&&(coord3!=null)) {
									Polygon trianglepolygon = new Polygon();
									trianglepolygon.addPoint((int)Math.round(coord1.u), (int)Math.round(coord1.v));
									trianglepolygon.addPoint((int)Math.round(coord2.u), (int)Math.round(coord2.v));
									trianglepolygon.addPoint((int)Math.round(coord3.u), (int)Math.round(coord3.v));
									g2.fill(trianglepolygon);
									boolean mouseoverhit = g2.hit(new Rectangle(this.mouselocationx-this.vertexradius,this.mouselocationy-this.vertexradius,3,3), trianglepolygon, false);
									if (mouseoverhit) {
										int entitytriangleind = Math.floorDiv(i, 2);
										mouseoverhittriangle.add(entitytrianglelist[entitytriangleind]);
									}
								}
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
						for (int j=0;j<tetrahedronlist.length;j++) {
							Triangle[] tetrahedrontrianglelist = new Triangle[4];
							tetrahedrontrianglelist[0] = new Triangle(tetrahedronlist[j].pos1,tetrahedronlist[j].pos2,tetrahedronlist[j].pos3); 
							tetrahedrontrianglelist[1] = new Triangle(tetrahedronlist[j].pos1,tetrahedronlist[j].pos2,tetrahedronlist[j].pos4); 
							tetrahedrontrianglelist[2] = new Triangle(tetrahedronlist[j].pos1,tetrahedronlist[j].pos3,tetrahedronlist[j].pos4); 
							tetrahedrontrianglelist[3] = new Triangle(tetrahedronlist[j].pos2,tetrahedronlist[j].pos3,tetrahedronlist[j].pos4);
							Coordinate[][] copytrianglelistcoords = MathLib.projectedTriangles(this.campos, tetrahedrontrianglelist, this.getWidth(), this.hfov, this.getHeight(), this.vfov, this.cameramat);
							for (int i=0;i<tetrahedrontrianglelist.length;i++) {
								Coordinate coord1 = copytrianglelistcoords[i][0];
								Coordinate coord2 = copytrianglelistcoords[i][1];
								Coordinate coord3 = copytrianglelistcoords[i][2];
								if ((coord1!=null)&&(coord2!=null)&&(coord3!=null)) {
									Polygon trianglepolygon = new Polygon();
									trianglepolygon.addPoint((int)Math.round(coord1.u), (int)Math.round(coord1.v));
									trianglepolygon.addPoint((int)Math.round(coord2.u), (int)Math.round(coord2.v));
									trianglepolygon.addPoint((int)Math.round(coord3.u), (int)Math.round(coord3.v));
									g2.fill(trianglepolygon);
								}
							}
						}
					}
				}
			}
			Line[] copylinelist = linelistarray.toArray(new Line[linelistarray.size()]);
			Coordinate[][] copylinelistcoords = MathLib.projectedLines(this.campos, copylinelist, this.getWidth(), this.hfov, this.getHeight(), this.vfov, this.cameramat);
			Plane[] editdirplane = {this.editplanes[0]};
			for (int i=0;i<copylinelist.length;i++) {
				Position[] linepoints = {copylinelist[i].pos1, copylinelist[i].pos2};
				double[][] linepointdists = MathLib.planePointDistance(linepoints, editdirplane);
				if ((linepointdists[0][0]>=0)||(linepointdists[1][0]>=0)) {
					Coordinate coord1 = copylinelistcoords[i][0];
					Coordinate coord2 = copylinelistcoords[i][1];
					if ((coord1!=null)&&(coord2!=null)) {
						g2.setColor(Color.BLACK);
						if (Math.abs(linepointdists[0][0])<this.pointdist){g2.setStroke(new BasicStroke(this.vertexstroke+this.vertexfocus));}else{g2.setStroke(new BasicStroke(this.vertexstroke));}
						g2.drawOval((int)Math.round(coord1.u)-this.vertexradius, (int)Math.round(coord1.v)-this.vertexradius, this.vertexradius*2, this.vertexradius*2);
						if (Math.abs(linepointdists[1][0])<this.pointdist){g2.setStroke(new BasicStroke(this.vertexstroke+this.vertexfocus));}else{g2.setStroke(new BasicStroke(this.vertexstroke));}
						g2.drawOval((int)Math.round(coord2.u)-this.vertexradius, (int)Math.round(coord2.v)-this.vertexradius, this.vertexradius*2, this.vertexradius*2);
						g2.setColor(Color.BLUE);
						g2.setStroke(new BasicStroke(this.sketchlinestroke));
						g2.drawLine((int)Math.round(coord1.u), (int)Math.round(coord1.v), (int)Math.round(coord2.u), (int)Math.round(coord2.v));
						boolean mouseoverhit1 = g2.hit(new Rectangle(this.mouselocationx-this.vertexradius,this.mouselocationy-this.vertexradius,3,3), new Rectangle((int)Math.round(coord1.u)-this.vertexradius,(int)Math.round(coord1.v)-this.vertexradius,3,3), false);
						boolean mouseoverhit2 = g2.hit(new Rectangle(this.mouselocationx-this.vertexradius,this.mouselocationy-this.vertexradius,3,3), new Rectangle((int)Math.round(coord2.u)-this.vertexradius,(int)Math.round(coord2.v)-this.vertexradius,3,3), false);
						boolean mouseoverhitL = g2.hit(new Rectangle(this.mouselocationx-this.vertexradius,this.mouselocationy-this.vertexradius,3,3), new Line2D.Double((int)Math.round(coord1.u),(int)Math.round(coord1.v),(int)Math.round(coord2.u),(int)Math.round(coord2.v)), false);
						if (mouseoverhit1) {
							mouseoverhitvertex.add(copylinelist[i].pos1);
						}
						if (mouseoverhit2) {
							mouseoverhitvertex.add(copylinelist[i].pos2);
						}
						if (mouseoverhitL) {
							mouseoverhitline.add(copylinelist[i]);
						}
					}
				}
			}
		}
		Position[] originpoints = {new Position(0,0,0),new Position(this.originlinewidth,0,0),new Position(0,this.originlineheight,0),new Position(0,0,this.originlinedepth)}; 
		Coordinate[] originpointscoords = MathLib.projectedPoints(this.campos, originpoints, this.getWidth(), this.hfov, this.getHeight(), this.vfov, this.cameramat);
		Coordinate coord1 = originpointscoords[0];
		Coordinate coord2 = originpointscoords[1];
		Coordinate coord3 = originpointscoords[2];
		Coordinate coord4 = originpointscoords[3];
		if ((coord1!=null)&&(coord2!=null)&&(coord3!=null)&&(coord4!=null)) {
			g2.setStroke(new BasicStroke(this.axisstroke));
			g2.setColor(Color.RED);
			g2.drawLine((int)Math.round(coord1.u), (int)Math.round(coord1.v), (int)Math.round(coord2.u), (int)Math.round(coord2.v));
			g2.setColor(Color.GREEN);
			g2.drawLine((int)Math.round(coord1.u), (int)Math.round(coord1.v), (int)Math.round(coord3.u), (int)Math.round(coord3.v));
			g2.setColor(Color.BLUE);
			g2.drawLine((int)Math.round(coord1.u), (int)Math.round(coord1.v), (int)Math.round(coord4.u), (int)Math.round(coord4.v));
			g2.setColor(Color.BLACK);
			g2.fillOval((int)Math.round(coord1.u)-this.vertexradius, (int)Math.round(coord1.v)-this.vertexradius, this.vertexradius*2, this.vertexradius*2);
		}
		this.mouseovertriangle = mouseoverhittriangle.toArray(new Triangle[mouseoverhittriangle.size()]);
		this.mouseoververtex = mouseoverhitvertex.toArray(new Position[mouseoverhitvertex.size()]);
		this.mouseoverline = mouseoverhitline.toArray(new Line[mouseoverhitline.size()]);
	}

	private int snapToGrid(int coordinate) {
		return this.gridstep*(int)Math.round(((double)coordinate)/((double)this.gridstep));
	}

	private void updateCameraDirections() {
		Matrix camrotmatz = MathLib.rotationMatrix(0.0f, 0.0f, this.camrot.z);
		Matrix camrotmaty = MathLib.rotationMatrix(0.0f, this.camrot.y, 0.0f);
		Matrix camrotmatx = MathLib.rotationMatrix(this.camrot.x, 0.0f, 0.0f);
		Matrix eyeonemat = MathLib.rotationMatrix(0.0f, 0.0f, 0.0f);
		Matrix camrotmat = MathLib.matrixMultiply(eyeonemat, camrotmatz);
		camrotmat = MathLib.matrixMultiply(camrotmat, camrotmaty);
		camrotmat = MathLib.matrixMultiply(camrotmat, camrotmatx);
		Direction[] camlookdirs = MathLib.matrixMultiply(this.lookdirs, camrotmat);
		Position[] camposarray = {this.campos};
		Position[] editposarray = MathLib.translate(camposarray, camlookdirs[0], this.editplanedistance);
		Plane[] editdirplanes = MathLib.planeFromNormalAtPoint(editposarray[0], this.camdirs);
		this.cameramat = camrotmat;
		this.camdirs = camlookdirs;
		this.editpos = editposarray[0];
		this.editplanes = editdirplanes; 
	}

	@Override public void timerTick() {
		double movementstep = 1.0f;
		if (this.snaplinemode) {
			movementstep = this.gridstep;
		}
		if (this.leftkeydown) {
			this.campos.x -= movementstep*this.camdirs[1].dx;
			this.campos.y -= movementstep*this.camdirs[1].dy;
			this.campos.z -= movementstep*this.camdirs[1].dz;
		} else if (this.rightkeydown) {
			this.campos.x += movementstep*this.camdirs[1].dx;
			this.campos.y += movementstep*this.camdirs[1].dy;
			this.campos.z += movementstep*this.camdirs[1].dz;
		}
		if (this.forwardkeydown) {
			this.campos.x += movementstep*this.camdirs[0].dx;
			this.campos.y += movementstep*this.camdirs[0].dy;
			this.campos.z += movementstep*this.camdirs[0].dz;
		} else if (this.backwardkeydown) {
			this.campos.x -= movementstep*this.camdirs[0].dx;
			this.campos.y -= movementstep*this.camdirs[0].dy;
			this.campos.z -= movementstep*this.camdirs[0].dz;
		}
		if (this.upwardkeydown) {
			this.campos.x -= movementstep*this.camdirs[2].dx;
			this.campos.y -= movementstep*this.camdirs[2].dy;
			this.campos.z -= movementstep*this.camdirs[2].dz;
		} else if (this.downwardkeydown) {
			this.campos.x += movementstep*this.camdirs[2].dx;
			this.campos.y += movementstep*this.camdirs[2].dy;
			this.campos.z += movementstep*this.camdirs[2].dz;
		}
		if (this.rollleftkeydown) {
			this.camrot.y -= (movementstep/((double)this.gridstep));
		} else if (this.rollrightkeydown) {
			this.camrot.y += (movementstep/((double)this.gridstep));
		}
		updateCameraDirections();
	}
	
	@Override public void keyTyped(KeyEvent e) {}
	@Override public void keyReleased(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_SHIFT) {
			this.snaplinemode = false;
		} else if (e.getKeyCode()==KeyEvent.VK_W) {
			this.forwardkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_S) {
			this.backwardkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_A) {
			this.leftkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_D) {
			this.rightkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_SPACE) {
			this.upwardkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_C) {
			this.downwardkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_Q) {
			this.rollleftkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_E) {
			this.rollrightkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_ADD) {
			this.forwardkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_SUBTRACT) {
			this.backwardkeydown = false;
		}
	}
	@Override public void keyPressed(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_BACK_SPACE) {
			this.linelistarray.clear();
			this.entitylist = null;
			this.campos = new Position(0.0f,0.0f,this.editplanedistance);
			this.camrot = new Rotation(0.0f,0.0f,0.0f);
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
		} else if (e.getKeyCode()==KeyEvent.VK_W) {
			this.forwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_S) {
			this.backwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_A) {
			this.leftkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_D) {
			this.rightkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_SPACE) {
			this.upwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_C) {
			this.downwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_Q) {
			this.rollleftkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_E) {
			this.rollrightkeydown = true;
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
					Line[] linelist = linelistarray.toArray(new Line[linelistarray.size()]);
					newentitylist[0].vertexlist = MathLib.generateVertexList(linelist);
					newentitylist[0].aabbboundaryvolume = MathLib.axisAlignedBoundingBox(newentitylist[0].vertexlist);
					newentitylist[0].sphereboundaryvolume = MathLib.pointCloudCircumSphere(newentitylist[0].vertexlist);
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
					Line[] linelist = linelistarray.toArray(new Line[linelistarray.size()]);
					for (Iterator<Entity> i=newentitylistarray.iterator();i.hasNext();) {
						Entity nextentity = i.next();
						nextentity.vertexlist = MathLib.generateVertexList(linelist);
						nextentity.aabbboundaryvolume = MathLib.axisAlignedBoundingBox(nextentity.vertexlist);
						nextentity.sphereboundaryvolume = MathLib.pointCloudCircumSphere(nextentity.vertexlist);
					}
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
	    int onmask1ctrldown = MouseEvent.BUTTON1_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
	    int offmask1ctrldown = MouseEvent.ALT_DOWN_MASK;
	    boolean mouse1ctrldown = ((e.getModifiersEx() & (onmask1ctrldown | offmask1ctrldown)) == onmask1ctrldown);
    	if (mouse1ctrldown) {
    		if (this.mouseoververtex.length>0) {
	    		if (this.snaplinemode) {
	    			this.draglinemode = true;
	    			this.selecteddragvertex = this.mouseoververtex;
	    		} else {
	    			this.draglinemode = true;
	    			Position[] selectedvertex = {this.mouseoververtex[this.mouseoververtex.length-1]};
	    			this.selecteddragvertex = selectedvertex; 
	    		}
	    	}
    	}
	    int onmask1alt = MouseEvent.BUTTON1_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    int offmask1alt = MouseEvent.CTRL_DOWN_MASK;
	    boolean mouse1altdown = ((e.getModifiersEx() & (onmask1alt | offmask1alt)) == onmask1alt);
    	if (mouse1altdown) {
    		this.drawstartpos = null;
    		double mouserelativelocationx = this.mouselocationx-this.origindeltax;
    		double mouserelativelocationy = this.mouselocationy-this.origindeltay;
			if (this.snaplinemode) {
	    		mouserelativelocationx = snapToGrid(this.mouselocationx-this.origindeltax);
	    		mouserelativelocationy = snapToGrid(this.mouselocationy-this.origindeltay);
				if (this.mouseoververtex.length>0) {
					this.drawstartpos = this.mouseoververtex[this.mouseoververtex.length-1].copy(); 
				}
			}
			Position[] editposarray = {this.editpos};
			Position[] drawposarray = MathLib.translate(editposarray, this.camdirs[1], mouserelativelocationx);
			drawposarray = MathLib.translate(drawposarray, this.camdirs[2], mouserelativelocationy);
			if (this.drawstartpos==null) {
				this.drawstartpos = drawposarray[0].copy();
			}
			this.linelistarray.add(new Line(this.drawstartpos, drawposarray[0]));
			this.draglinemode = true;
			this.selecteddragvertex = drawposarray;
			(new EntityListUpdater()).start();
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
	}
	
	public void mouseDragged(MouseEvent e) {
		this.mouselastlocationx=this.mouselocationx;this.mouselastlocationy=this.mouselocationy;
		this.mouselocationx=e.getX();this.mouselocationy=e.getY();
	    int onmask1down = MouseEvent.BUTTON1_DOWN_MASK;
	    int offmask1down = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK|MouseEvent.SHIFT_DOWN_MASK;
	    boolean mouse1down = ((e.getModifiersEx() & (onmask1down | offmask1down)) == onmask1down);
    	if (mouse1down) {
    		if (this.mouseovertriangle.length>0) {
				Material newmaterial = new Material();
				newmaterial.facecolor = this.drawcolor;
				newmaterial.transparency = this.penciltransparency;
				this.mouseovertriangle[this.mouseovertriangle.length-1].mat = newmaterial;
    		}
    	}
	    int onmask1shiftdown = MouseEvent.BUTTON1_DOWN_MASK|MouseEvent.SHIFT_DOWN_MASK;
	    int offmask1shiftdown = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    boolean mouse1shiftdown = ((e.getModifiersEx() & (onmask1shiftdown | offmask1shiftdown)) == onmask1shiftdown);
	    if (mouse1shiftdown) {
    		if (this.mouseovertriangle.length>0) {
    			this.penciltransparency = this.mouseovertriangle[this.mouseovertriangle.length-1].mat.transparency;
    			int colorvalue = this.mouseovertriangle[this.mouseovertriangle.length-1].mat.facecolor.getRGB();
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
    			Position drawlocation = null;
	    		double mouserelativelocationx = this.mouselocationx-this.origindeltax;
	    		double mouserelativelocationy = this.mouselocationy-this.origindeltay;
				if (this.snaplinemode) {
		    		mouserelativelocationx = snapToGrid(this.mouselocationx-this.origindeltax);
		    		mouserelativelocationy = snapToGrid(this.mouselocationy-this.origindeltay);
					if (this.mouseoververtex.length>0) {
						drawlocation = this.mouseoververtex[this.mouseoververtex.length-1].copy(); 
					}
				}
				if (drawlocation==null) {
					Position[] editposarray = {this.editpos};
					Position[] drawposarray = MathLib.translate(editposarray, this.camdirs[1], mouserelativelocationx);
					drawposarray = MathLib.translate(drawposarray, this.camdirs[2], mouserelativelocationy);
	    			drawlocation = drawposarray[0];
				}
				for (int i=0;i<this.selecteddragvertex.length;i++) {
					this.selecteddragvertex[i].x = drawlocation.x;
					this.selecteddragvertex[i].y = drawlocation.y;
					this.selecteddragvertex[i].z = drawlocation.z;
				}
				(new EntityListUpdater()).start();
    		}
		}
	    int onmask3down = MouseEvent.BUTTON3_DOWN_MASK;
	    int offmask3down = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    boolean mouse3down = ((e.getModifiersEx() & (onmask3down | offmask3down)) == onmask3down);
    	if (mouse3down) {
    		double movementstep = 1.0f;
    		if (this.snaplinemode) {
    			movementstep = this.gridstep;
    		}
        	int mousedeltax = this.mouselocationx - this.mouselastlocationx; 
        	int mousedeltay = this.mouselocationy - this.mouselastlocationy;
        	this.camrot.z -= mousedeltax*(movementstep/((double)this.gridstep))*0.1f;
        	this.camrot.x -= mousedeltay*(movementstep/((double)this.gridstep))*0.1f;
        	updateCameraDirections();
    	}
	    int onmask3altdown = MouseEvent.BUTTON3_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    int offmask3altdown = MouseEvent.CTRL_DOWN_MASK;
	    boolean mouse3altdown = ((e.getModifiersEx() & (onmask3altdown | offmask3altdown)) == onmask3altdown);
    	if (mouse3altdown) {
    		if (this.mouseoverline.length>0) {
				this.linelistarray.removeAll(Arrays.asList(this.mouseoverline));
				(new EntityListUpdater()).start();
			}
    	}
	    int onmask2down = MouseEvent.BUTTON2_DOWN_MASK;
	    int offmask2down = MouseEvent.ALT_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
	    boolean mouse2down = ((e.getModifiersEx() & (onmask2down | offmask2down)) == onmask2down);
    	if (mouse2down) {
    		double movementstep = 1.0f;
    		if (this.snaplinemode) {
    			movementstep = this.gridstep;
    		}
        	int mousedeltax = this.mouselocationx - this.mouselastlocationx; 
        	int mousedeltay = this.mouselocationy - this.mouselastlocationy;
    		this.campos.x -= mousedeltax*movementstep*this.camdirs[1].dx;
    		this.campos.y -= mousedeltax*movementstep*this.camdirs[1].dy;
    		this.campos.z -= mousedeltax*movementstep*this.camdirs[1].dz;
    		this.campos.x -= mousedeltay*movementstep*this.camdirs[2].dx;
    		this.campos.y -= mousedeltay*movementstep*this.camdirs[2].dy;
    		this.campos.z -= mousedeltay*movementstep*this.camdirs[2].dz;
    	}
	    int onmask2ctrldown = MouseEvent.BUTTON2_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
	    int offmask2ctrldown = MouseEvent.ALT_DOWN_MASK;
	    boolean mouse2ctrldown = ((e.getModifiersEx() & (onmask2ctrldown | offmask2ctrldown)) == onmask2ctrldown);
    	if (mouse2ctrldown) {
    		//TODO move entity
    	}
	}
	@Override public void mouseWheelMoved(MouseWheelEvent e) {
		double movementstep = 200.0f*e.getWheelRotation();
		if (this.snaplinemode) {
			movementstep *= this.gridstep;
		}
		this.campos.x -= movementstep*this.camdirs[0].dx;
		this.campos.y -= movementstep*this.camdirs[0].dy;
		this.campos.z -= movementstep*this.camdirs[0].dz;
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
