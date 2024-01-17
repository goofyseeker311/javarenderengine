package fi.jkauppa.javarenderengine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Robot;
import java.awt.Transparency;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import javax.swing.JFileChooser;

import fi.jkauppa.javarenderengine.JavaRenderEngine.AppHandlerPanel;
import fi.jkauppa.javarenderengine.ModelLib.Coordinate;
import fi.jkauppa.javarenderengine.ModelLib.Direction;
import fi.jkauppa.javarenderengine.ModelLib.Entity;
import fi.jkauppa.javarenderengine.ModelLib.Line;
import fi.jkauppa.javarenderengine.ModelLib.Material;
import fi.jkauppa.javarenderengine.ModelLib.Matrix;
import fi.jkauppa.javarenderengine.UtilLib.ModelFileFilters.OBJFileFilter;
import fi.jkauppa.javarenderengine.ModelLib.Plane;
import fi.jkauppa.javarenderengine.ModelLib.Position;
import fi.jkauppa.javarenderengine.ModelLib.Rotation;
import fi.jkauppa.javarenderengine.ModelLib.Sphere;
import fi.jkauppa.javarenderengine.ModelLib.Sphere.SphereDistanceComparator;
import fi.jkauppa.javarenderengine.ModelLib.Triangle;
import fi.jkauppa.javarenderengine.ModelLib.Model;

public class ModelApp extends AppHandlerPanel {
	private static final long serialVersionUID = 1L;
	private Entity[] entitylist = null;
	private Position campos = new Position(0,0,0);
	private Rotation camrot = new Rotation(0.0f, 0.0f, 0.0f);
	private Matrix cameramat = MathLib.rotationMatrix(0.0f, 0.0f, 0.0f);
	private final Direction[] lookdirs = MathLib.projectedCameraDirections(cameramat);
	private Direction[] camdirs = lookdirs;
	private double hfov = 70.0f;
	private double vfov = 43.0f;
	private JFileChooser filechooser = new JFileChooser();
	private OBJFileFilter objfilefilter = new OBJFileFilter();
	private boolean leftkeydown = false;
	private boolean rightkeydown = false;
	private boolean upwardkeydown = false;
	private boolean downwardkeydown = false;
	private boolean forwardkeydown = false;
	private boolean backwardkeydown = false;
	private boolean rollrightkeydown = false;
	private boolean rollleftkeydown = false;
	private int mouselastlocationx = -1, mouselastlocationy = -1; 
	private int mouselocationx = -1, mouselocationy = -1;
	private double[][] zbuffer = null;
	private int polygonfillmode = 1;
	private int lastrenderwidth = 0, lastrenderheight = 0;
	private Cursor customcursor = null;
	
	public ModelApp() {
		BufferedImage cursorimage = gc.createCompatibleImage(1, 1, Transparency.TRANSLUCENT);
		Graphics2D cgfx = cursorimage.createGraphics();
		cgfx.setColor(new Color(0.0f,0.0f,0.0f,0.0f));
		cgfx.drawLine(0, 0, 0, 0);
		cgfx.dispose();
		this.customcursor = tk.createCustomCursor(cursorimage, new Point(0,0), "customcursor");
		this.filechooser.addChoosableFileFilter(this.objfilefilter);
		this.filechooser.setFileFilter(this.objfilefilter);
		this.filechooser.setAcceptAllFileFilterUsed(false);
		this.setCursor(this.customcursor);
	}

	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if ((this.lastrenderwidth!=this.getWidth())||(this.lastrenderheight!=this.getHeight())) {
			this.lastrenderwidth = this.getWidth();
			this.lastrenderheight = this.getHeight();
		}
		Graphics2D g2 = (Graphics2D)g;
		g2.setComposite(AlphaComposite.Src);
		g2.setColor(Color.BLACK);
		g2.setPaint(null);
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		g2.setComposite(AlphaComposite.SrcOver);
		this.vfov = 2.0f*(180.0f/Math.PI)*Math.atan((((double)this.getHeight())/((double)this.getWidth()))*Math.tan((this.hfov/2.0f)*(Math.PI/180.0f)));
		if (this.polygonfillmode==1) {
			renderWindow(g2);
		} else {
			renderWindowSoftware(g2);
		}
	}

	private void renderWindow(Graphics2D g) {
		if (this.entitylist!=null) {
			Sphere[] entityspherelist = new Sphere[this.entitylist.length]; 
			for (int k=0;k<this.entitylist.length;k++) {
				entityspherelist[k] = this.entitylist[k].sphereboundaryvolume;
				entityspherelist[k].ind = k;
			}
			TreeSet<Sphere> sortedentityspheretree = new TreeSet<Sphere>(new SphereDistanceComparator(this.campos));
			sortedentityspheretree.addAll(Arrays.asList(entityspherelist));
			Sphere[] sortedentityspherelist = sortedentityspheretree.toArray(new Sphere[sortedentityspheretree.size()]);
			for (int k=0;k<sortedentityspherelist.length;k++) {
				Triangle[] copytrianglelist = this.entitylist[sortedentityspherelist[k].ind].trianglelist;
				if (copytrianglelist.length>0) {
					Plane[] triangleplanes = MathLib.planeFromPoints(copytrianglelist);
					Direction[] trianglenormals = MathLib.planeNormals(triangleplanes);
					double[] triangleviewangles = MathLib.vectorAngle(this.camdirs[0], trianglenormals);
					Color[] trianglecolor = new Color[copytrianglelist.length];
					for (int i=0;i<copytrianglelist.length;i++) {
						double triangleviewangle = triangleviewangles[i];
						if (triangleviewangle>90.0f) {triangleviewangle = 180.0f-triangleviewangle;}
						float shadingmultiplier = (90.0f-(((float)triangleviewangle))/1.5f)/90.0f;
						Material copymaterial = copytrianglelist[i].mat;
						Color tricolor = copymaterial.facecolor;
						float alphacolor = copymaterial.transparency;
						if (tricolor==null) {tricolor = Color.WHITE;}
						float[] tricolorcomp = tricolor.getRGBComponents(new float[4]);
						trianglecolor[i] = new Color(tricolorcomp[0]*shadingmultiplier, tricolorcomp[1]*shadingmultiplier, tricolorcomp[2]*shadingmultiplier, alphacolor);
					}
					Sphere[] copytrianglepherelist = MathLib.triangleCircumSphere(copytrianglelist);
					for (int i=0;i<copytrianglepherelist.length;i++) {copytrianglepherelist[i].ind = i;}
					TreeSet<Sphere> sortedtrianglespheretree = new TreeSet<Sphere>(new SphereDistanceComparator(this.campos));
					sortedtrianglespheretree.addAll(Arrays.asList(copytrianglepherelist));
					Sphere[] sortedtrianglespherelist = sortedtrianglespheretree.toArray(new Sphere[sortedtrianglespheretree.size()]);
					Coordinate[][] copytrianglelistcoords = MathLib.projectedTriangles(this.campos, copytrianglelist, this.getWidth(), this.hfov, this.getHeight(), this.vfov, this.cameramat);
					for (int i=0;i<sortedtrianglespherelist.length;i++) {
						Coordinate coord1 = copytrianglelistcoords[sortedtrianglespherelist[i].ind][0];
						Coordinate coord2 = copytrianglelistcoords[sortedtrianglespherelist[i].ind][1];
						Coordinate coord3 = copytrianglelistcoords[sortedtrianglespherelist[i].ind][2];
						if ((coord1!=null)&&(coord2!=null)&&(coord3!=null)) {
							int it = sortedtrianglespherelist[i].ind;
							Triangle copytriangle = copytrianglelist[it];
							Polygon trianglepolygon = new Polygon();
							trianglepolygon.addPoint((int)Math.round(coord1.u), (int)Math.round(coord1.v));
							trianglepolygon.addPoint((int)Math.round(coord2.u), (int)Math.round(coord2.v));
							trianglepolygon.addPoint((int)Math.round(coord3.u), (int)Math.round(coord3.v));
							Material copymaterial = copytriangle.mat;
							g.setColor(trianglecolor[it]);
							VolatileImage tritexture = copymaterial.fileimage;
							if (tritexture==null) {
								g.fill(trianglepolygon);
							} else {
								g.clip(trianglepolygon);
								Triangle[] sortedtriangle = {copytriangle};
								Polygon[] drawpolygon = {trianglepolygon};
								VolatileImage[] drawtexture = {tritexture};
								AffineTransform[] texturetransform = MathLib.textureTransform(drawtexture, sortedtriangle, drawpolygon);
								g.drawImage(tritexture, texturetransform[0], null);
								g.setClip(null);
							}
						}
					}
				}
			}
		}
	}
	
	private void renderWindowSoftware(Graphics2D g) {
		if ((this.zbuffer==null)||(this.zbuffer.length!=this.getHeight())||(this.zbuffer[0].length!=this.getWidth())) {
			this.zbuffer = new double[this.getHeight()][this.getWidth()];
		}
		for (int i=0;i<this.zbuffer.length;i++) {Arrays.fill(this.zbuffer[i],Double.POSITIVE_INFINITY);}
		if (this.entitylist!=null) {
			Plane[] verticalplanes = MathLib.projectedPlanes(this.campos, this.getWidth(), hfov, this.cameramat);
			double[] verticalangles = MathLib.projectedAngles(this.getHeight(), vfov);
			Arrays.sort(verticalangles);
			Sphere[] entityspherelist = new Sphere[this.entitylist.length]; 
			for (int k=0;k<this.entitylist.length;k++) {
				entityspherelist[k] = this.entitylist[k].sphereboundaryvolume;
				entityspherelist[k].ind = k;
			}
			TreeSet<Sphere> sortedentityspheretree = new TreeSet<Sphere>(new SphereDistanceComparator(this.campos));
			sortedentityspheretree.addAll(Arrays.asList(entityspherelist));
			Sphere[] sortedentityspherelist = sortedentityspheretree.toArray(new Sphere[sortedentityspheretree.size()]);
			for (int k=0;k<sortedentityspherelist.length;k++) {
				Triangle[] copytrianglelist = this.entitylist[sortedentityspherelist[k].ind].trianglelist;
				if (copytrianglelist.length>0) {
					Line[][] vertplanetriangleint = MathLib.planeTriangleIntersection(verticalplanes, copytrianglelist);		
					Plane[] triangleplanes = MathLib.planeFromPoints(copytrianglelist);
					Direction[] trianglenormals = MathLib.planeNormals(triangleplanes);
					double[] triangleviewangles = MathLib.vectorAngle(this.camdirs[0], trianglenormals);
					Direction[] camfwddir = {this.camdirs[0]};
					Direction[] camupdir = {this.camdirs[2]};
					Plane[] camfwdplane = MathLib.planeFromNormalAtPoint(this.campos, camfwddir);
					Plane[] camupplane = MathLib.planeFromNormalAtPoint(this.campos, camupdir);
					Color[] trianglecolor = new Color[copytrianglelist.length];
					for (int i=0;i<copytrianglelist.length;i++) {
						double triangleviewangle = triangleviewangles[i];
						if (triangleviewangle>90.0f) {triangleviewangle = 180.0f-triangleviewangle;}
						float shadingmultiplier = (90.0f-(((float)triangleviewangle))/1.5f)/90.0f;
						Material copymaterial = copytrianglelist[i].mat;
						Color tricolor = copymaterial.facecolor;
						float alphacolor = copymaterial.transparency;
						if (tricolor==null) {tricolor = Color.WHITE;}
						float[] tricolorcomp = tricolor.getRGBComponents(new float[4]);
						trianglecolor[i] = new Color(tricolorcomp[0]*shadingmultiplier, tricolorcomp[1]*shadingmultiplier, tricolorcomp[2]*shadingmultiplier, alphacolor);
					}
					Sphere[] copytrianglepherelist = MathLib.triangleCircumSphere(copytrianglelist);
					for (int i=0;i<copytrianglepherelist.length;i++) {copytrianglepherelist[i].ind = i;}
					TreeSet<Sphere> sortedtrianglespheretree = new TreeSet<Sphere>(new SphereDistanceComparator(this.campos));
					sortedtrianglespheretree.addAll(Arrays.asList(copytrianglepherelist));
					Sphere[] sortedtrianglespherelist = sortedtrianglespheretree.toArray(new Sphere[sortedtrianglespheretree.size()]);
					for (int j=0;j<vertplanetriangleint.length;j++) {
						for (int i=0;i<sortedtrianglespherelist.length;i++) {
							int it = sortedtrianglespherelist[i].ind;
							Line triangleint = vertplanetriangleint[j][it];
							if (triangleint!=null) {
								g.setColor(trianglecolor[it]);
								Position[] triangleintpoints = {triangleint.pos1, triangleint.pos2};
								double[][] trianglefwdintpointsdist = MathLib.pointPlaneDistance(triangleintpoints, camfwdplane);
								if ((trianglefwdintpointsdist[0][0]>0)||(trianglefwdintpointsdist[1][0]>0)) {
									Line drawline = triangleint;
									Line[] triangleintarray = {triangleint};
									Position[][] lineviewint = MathLib.planeLineIntersection(camfwdplane, triangleintarray);
									if (lineviewint[0][0]!=null) {
										if (trianglefwdintpointsdist[0][0]>0) {
											drawline = new Line(triangleint.pos1, lineviewint[0][0]);
										} else if (trianglefwdintpointsdist[1][0]>0) {
											drawline = new Line(triangleint.pos2, lineviewint[0][0]);
										}
									}
									Position[] drawlinepoints = {drawline.pos1, drawline.pos2};
									double[][] drawlinefwdintpointsdist = MathLib.pointPlaneDistance(drawlinepoints, camfwdplane);
									double[][] drawlineupintpointsdist = MathLib.pointPlaneDistance(drawlinepoints, camupplane);
									double drawangle1 = (180.0f/Math.PI)*Math.atan(drawlineupintpointsdist[0][0]/Math.abs(drawlinefwdintpointsdist[0][0]));
									double drawangle2 = (180.0f/Math.PI)*Math.atan(drawlineupintpointsdist[1][0]/Math.abs(drawlinefwdintpointsdist[1][0]));
									double[] angles = {drawangle1, drawangle2};
									int[] anglesind = UtilLib.indexSort(angles);
									double[] anglessort = UtilLib.indexValues(angles, anglesind);
									Position[] sortlinepoints = {drawlinepoints[anglesind[0]], drawlinepoints[anglesind[1]]}; 
									if (!Double.isFinite(anglessort[0])) {anglessort[0] = -180.0f;}
									if (!Double.isFinite(anglessort[1])) {anglessort[1] = 180.0f;}
									Direction[] drawvector = MathLib.vectorFromPoints(this.campos, sortlinepoints);
									double[] drawdistance = MathLib.vectorLength(drawvector);
									double drawdistancedelta = drawdistance[1]-drawdistance[0];
									int startind = Arrays.binarySearch(verticalangles, anglessort[0]);
									int endind = Arrays.binarySearch(verticalangles, anglessort[1]);
									if ((startind!=-(verticalangles.length+1))&&(endind!=-1)) {
										if (startind<0) {startind = -startind-1; }
										if (endind<0) {endind = -endind-1;}
										if (startind>=verticalangles.length) {startind = verticalangles.length-1; }
										if (endind>=verticalangles.length) {endind = verticalangles.length-1; }
										int indcount = endind - startind + 1;
										double drawstep = drawdistancedelta/((double)indcount);
										for (int n=startind;n<=endind;n++) {
											double stepdistance = drawdistance[0] + drawstep*(n-startind);  
											if (stepdistance<this.zbuffer[n][j]) {
												this.zbuffer[n][j] = stepdistance;
												g.drawLine(j, n, j, n);
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

	private void updateCameraDirections() {
		Matrix camrotmatz = MathLib.rotationMatrix(0.0f, 0.0f, this.camrot.z);
		Matrix camrotmaty = MathLib.rotationMatrix(0.0f, this.camrot.y, 0.0f);
		Matrix camrotmatx = MathLib.rotationMatrix(this.camrot.x, 0.0f, 0.0f);
		Matrix eyeonemat = MathLib.rotationMatrix(0.0f, 0.0f, 0.0f);
		Matrix camrotmat = MathLib.matrixMultiply(eyeonemat, camrotmatz);
		camrotmat = MathLib.matrixMultiply(camrotmat, camrotmaty);
		camrotmat = MathLib.matrixMultiply(camrotmat, camrotmatx);
		Direction[] camlookdirs = MathLib.matrixMultiply(this.lookdirs, camrotmat);
		this.cameramat = camrotmat;
		this.camdirs = camlookdirs;
	}

	@Override public void timerTick() {
		if (this.leftkeydown) {
			this.campos.x -= 20.0f*this.camdirs[1].dx;
			this.campos.y -= 20.0f*this.camdirs[1].dy;
			this.campos.z -= 20.0f*this.camdirs[1].dz;
		} else if (this.rightkeydown) {
			this.campos.x += 20.0f*this.camdirs[1].dx;
			this.campos.y += 20.0f*this.camdirs[1].dy;
			this.campos.z += 20.0f*this.camdirs[1].dz;
		}
		if (this.forwardkeydown) {
			this.campos.x += 20.0f*this.camdirs[0].dx;
			this.campos.y += 20.0f*this.camdirs[0].dy;
			this.campos.z += 20.0f*this.camdirs[0].dz;
		} else if (this.backwardkeydown) {
			this.campos.x -= 20.0f*this.camdirs[0].dx;
			this.campos.y -= 20.0f*this.camdirs[0].dy;
			this.campos.z -= 20.0f*this.camdirs[0].dz;
		}
		if (this.upwardkeydown) {
			this.campos.x -= 20.0f*this.camdirs[2].dx;
			this.campos.y -= 20.0f*this.camdirs[2].dy;
			this.campos.z -= 20.0f*this.camdirs[2].dz;
		} else if (this.downwardkeydown) {
			this.campos.x += 20.0f*this.camdirs[2].dx;
			this.campos.y += 20.0f*this.camdirs[2].dy;
			this.campos.z += 20.0f*this.camdirs[2].dz;
		}
		if (this.rollleftkeydown) {
			this.camrot.y -= 1.0f;
			updateCameraDirections();
		} else if (this.rollrightkeydown) {
			this.camrot.y += 1.0f;
			updateCameraDirections();
		}
	}

	@Override public void keyReleased(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_A) {
		this.leftkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_D) {
			this.rightkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_W) {
			this.forwardkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_S) {
			this.backwardkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_SPACE) {
			this.upwardkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_C) {
			this.downwardkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_Q) {
			this.rollleftkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_E) {
			this.rollrightkeydown = false;
		}
	}
	
	@Override public void keyPressed(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_BACK_SPACE) {
			this.entitylist = null;
			this.campos = new Position(0,0,0);
			this.camrot = new Rotation(0,0,0);
			updateCameraDirections();
		} else if (e.getKeyCode()==KeyEvent.VK_A) {
			this.leftkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_D) {
			this.rightkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_SPACE) {
			this.upwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_C) {
			this.downwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_W) {
			this.forwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_S) {
			this.backwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_Q) {
			this.rollleftkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_E) {
			this.rollrightkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_ENTER) {
			this.polygonfillmode += 1;
			if (this.polygonfillmode>2) {
				this.polygonfillmode = 1;
			}
		} else if (e.getKeyCode()==KeyEvent.VK_F3) {
			this.filechooser.setDialogTitle("Load File");
			this.filechooser.setApproveButtonText("Load");
			if (this.filechooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				File loadfile = this.filechooser.getSelectedFile();
				Model loadmodel = ModelLib.loadWaveFrontOBJFile(loadfile.getPath(), false);
				ArrayList<Entity> newentitylist = new ArrayList<Entity>();
				for (int j=0;j<loadmodel.objects.length;j++) {
					Entity newentity = new Entity();
					ArrayList<Triangle> newtrianglelistarray = new ArrayList<Triangle>();
					for (int i=0;i<loadmodel.objects[j].faceindex.length;i++) {
						if (loadmodel.objects[j].faceindex[i].facevertexindex.length==3) {
							Material foundmat = null;
							for (int n=0;(n<loadmodel.materials.length)&&(foundmat==null);n++) {
								if (loadmodel.objects[j].faceindex[i].usemtl.equals(loadmodel.materials[n].materialname)) {
									foundmat = loadmodel.materials[n];
								}
							}
							if (foundmat==null) {
								foundmat = new Material();
								foundmat.facecolor = Color.WHITE;
							}
							Position pos1 = loadmodel.vertexlist[loadmodel.objects[j].faceindex[i].facevertexindex[0].vertexindex-1];
							Position pos2 = loadmodel.vertexlist[loadmodel.objects[j].faceindex[i].facevertexindex[1].vertexindex-1];
							Position pos3 = loadmodel.vertexlist[loadmodel.objects[j].faceindex[i].facevertexindex[2].vertexindex-1];
							Direction norm = loadmodel.facenormals[loadmodel.objects[j].faceindex[i].facevertexindex[0].normalindex-1];
							Coordinate tex1 = loadmodel.texturecoords[loadmodel.objects[j].faceindex[i].facevertexindex[0].textureindex-1];
							Coordinate tex2 = loadmodel.texturecoords[loadmodel.objects[j].faceindex[i].facevertexindex[1].textureindex-1];
							Coordinate tex3 = loadmodel.texturecoords[loadmodel.objects[j].faceindex[i].facevertexindex[2].textureindex-1];
							Triangle tri = new Triangle(new Position(pos1.x,pos1.y,pos1.z),new Position(pos2.x,pos2.y,pos2.z),new Position(pos3.x,pos3.y,pos3.z));
							tri.norm = norm;
							tri.pos1.tex = tex1;
							tri.pos2.tex = tex2;
							tri.pos3.tex = tex3;
							Triangle[] stri = {tri};
							stri = MathLib.subDivideTriangle(stri);
							for (int n=0;n<stri.length;n++ ) {
								stri[n].mat = foundmat;
								newtrianglelistarray.add(stri[n]);
							}
						}
					}
					newentity.trianglelist = newtrianglelistarray.toArray(new Triangle[newtrianglelistarray.size()]);
					if (newentity.trianglelist.length>0) {
						newentity.vertexlist = MathLib.generateVertexList(newentity.trianglelist);
						newentity.aabbboundaryvolume = MathLib.axisAlignedBoundingBox(newentity.vertexlist);
						newentity.sphereboundaryvolume = MathLib.pointCloudCircumSphere(newentity.vertexlist);
						newentitylist.add(newentity);
					}
				}
				this.entitylist = newentitylist.toArray(new Entity[newentitylist.size()]);
			}
		}
	}
	
	@Override public void keyTyped(KeyEvent e) {}
	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseDragged(MouseEvent e) {}
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {
		Point windowscreenlocation = this.getLocationOnScreen();
		int windowhalfwidth = this.getWidth()/2;
		int windowhalfheight = this.getHeight()/2;
		int windowcenterx = windowscreenlocation.x + windowhalfwidth;
		int windowcentery = windowscreenlocation.y + windowhalfheight;
		this.mouselocationx = this.lastrenderwidth/2; 
		this.mouselocationy = this.lastrenderheight/2; 
		try {
			Robot mouserobot = new Robot();
			mouserobot.mouseMove(windowcenterx, windowcentery);
		} catch (Exception ex) {ex.printStackTrace();}
	}
	@Override public void mouseMoved(MouseEvent e) {
		this.mouselastlocationx=this.mouselocationx;this.mouselastlocationy=this.mouselocationy;
		this.mouselocationx=e.getX();this.mouselocationy=e.getY();
    	int mousedeltax = this.mouselocationx - this.mouselastlocationx; 
    	int mousedeltay = this.mouselocationy - this.mouselastlocationy;
    	this.camrot.z -= mousedeltax*0.1f;
    	this.camrot.x -= mousedeltay*0.1f;
    	updateCameraDirections();
		if ((this.mouselocationx<=0)||(this.mouselocationy<=0)||(this.mouselocationx>=(this.lastrenderwidth-1))||(this.mouselocationy>=(this.lastrenderheight-1))) {
			mouseExited(e);
		}
	}
	@Override public void mouseWheelMoved(MouseWheelEvent e) {}
	@Override public void drop(DropTargetDropEvent dtde) {}

}