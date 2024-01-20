package fi.jkauppa.javarenderengine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Transparency;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
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
	private BufferedImage cursorimage = gc.createCompatibleImage(1, 1, Transparency.TRANSLUCENT);
	private Cursor customcursor = null;
	
	public ModelApp() {
		cursorimage.setRGB(0, 0, (new Color(0.0f,0.0f,0.0f,0.0f)).getRGB());
		this.customcursor = tk.createCustomCursor(cursorimage, new Point(0,0), "customcursor");
		this.filechooser.addChoosableFileFilter(this.objfilefilter);
		this.filechooser.setFileFilter(this.objfilefilter);
		this.filechooser.setAcceptAllFileFilterUsed(false);
		this.setCursor(this.customcursor);
	}

	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setComposite(AlphaComposite.Src);
		g2.setColor(Color.BLACK);
		g2.setPaint(null);
		g2.setClip(null);
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		g2.setComposite(AlphaComposite.SrcOver);
		this.vfov = 2.0f*(180.0f/Math.PI)*Math.atan((((double)this.getHeight())/((double)this.getWidth()))*Math.tan((this.hfov/2.0f)*(Math.PI/180.0f)));
		if ((this.zbuffer==null)||(this.zbuffer.length!=this.getHeight())||(this.zbuffer[0].length!=this.getWidth())) {
			this.zbuffer = new double[this.getHeight()][this.getWidth()];
		}
		for (int i=0;i<this.zbuffer.length;i++) {Arrays.fill(this.zbuffer[i],Double.POSITIVE_INFINITY);}
		if (this.entitylist!=null) {
			Plane[] verticalplanes = MathLib.projectedPlanes(this.campos, this.getWidth(), hfov, this.cameramat);
			double[] verticalangles = MathLib.projectedAngles(this.getHeight(), vfov);
			Direction[][] projectedrays = MathLib.projectedRays(this.getWidth(), this.getHeight(), this.hfov, this.vfov, this.cameramat);
			double halfvfovmult = (1.0f/Math.tan((Math.PI/180.0f)*(vfov/2.0f)));
			int halfvres = (int)Math.round(((double)this.getHeight())/2.0f);
			Plane[] camdirrightupplanes = MathLib.planeFromNormalAtPoint(this.campos, this.camdirs);
			Plane[] camfwdplane = {camdirrightupplanes[0]};
			Plane[] camupplane = {camdirrightupplanes[2]};
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
					float[] triangleshadingmultipliers = new float[copytrianglelist.length];
					for (int i=0;i<copytrianglelist.length;i++) {
						double triangleviewangle = triangleviewangles[i];
						if (triangleviewangle>90.0f) {triangleviewangle = 180.0f-triangleviewangle;}
						triangleshadingmultipliers[i] = (90.0f-(((float)triangleviewangle))/1.5f)/90.0f;
					}
					Sphere[] copytrianglepherelist = MathLib.triangleCircumSphere(copytrianglelist);
					for (int i=0;i<copytrianglepherelist.length;i++) {copytrianglepherelist[i].ind = i;}
					TreeSet<Sphere> sortedtrianglespheretree = new TreeSet<Sphere>(new SphereDistanceComparator(this.campos));
					sortedtrianglespheretree.addAll(Arrays.asList(copytrianglepherelist));
					Sphere[] sortedtrianglespherelist = sortedtrianglespheretree.toArray(new Sphere[sortedtrianglespheretree.size()]);
					for (int j=0;j<vertplanetriangleint.length;j++) {
						for (int i=0;i<sortedtrianglespherelist.length;i++) {
							int it = sortedtrianglespherelist[i].ind;
							Line drawline = vertplanetriangleint[j][it];
							if (drawline!=null) {
								Position[] triangleintpoints = {drawline.pos1, drawline.pos2};
								double[][] trianglefwdintpointsdist = MathLib.planePointDistance(triangleintpoints, camfwdplane);
								if ((trianglefwdintpointsdist[0][0]>0)&&(trianglefwdintpointsdist[1][0]>0)) {
									Triangle[] copytriangle = {copytrianglelist[it]};
									Plane[] copytriangleplane = {triangleplanes[it]};
									Material copymaterial = copytriangle[0].mat;
									float shadingmultiplier = triangleshadingmultipliers[it];
									Color tricolor = copymaterial.facecolor;
									float alphacolor = copymaterial.transparency;
									if (tricolor==null) {tricolor = Color.WHITE;}
									float[] tricolorcomp = tricolor.getRGBComponents(new float[4]);
									Color trianglecolor = new Color(tricolorcomp[0]*shadingmultiplier, tricolorcomp[1]*shadingmultiplier, tricolorcomp[2]*shadingmultiplier, alphacolor);
									VolatileImage tritexture = copymaterial.fileimage;
									BufferedImage tritextureimage = copymaterial.snapimage;
									Position[] drawlinepoints = {drawline.pos1, drawline.pos2};
									double[][] fwdintpointsdist = MathLib.planePointDistance(drawlinepoints, camfwdplane);
									double[][] upintpointsdist = MathLib.planePointDistance(drawlinepoints, camupplane);
									double vpixely1 = halfvfovmult*halfvres*(upintpointsdist[0][0]/fwdintpointsdist[0][0])+halfvres;
									double vpixely2 = halfvfovmult*halfvres*(upintpointsdist[1][0]/fwdintpointsdist[1][0])+halfvres;
									double vpixelyang1 = (180.0f/Math.PI)*Math.atan(upintpointsdist[0][0]/fwdintpointsdist[0][0]);
									double vpixelyang2 = (180.0f/Math.PI)*Math.atan(upintpointsdist[1][0]/fwdintpointsdist[1][0]);
									double[] vpixelys = {vpixely1, vpixely2};
									double[] vpixelyangs = {vpixelyang1, vpixelyang2};
									int[] vpixelyinds = UtilLib.indexSort(vpixelys);
									double[] vpixelysort = UtilLib.indexValues(vpixelys, vpixelyinds);
									Position[] vpixelpoints = {drawlinepoints[vpixelyinds[0]], drawlinepoints[vpixelyinds[1]]};
									Position[] vpixelpoint1 = {vpixelpoints[0]};
									Position[] vpixelpoint2 = {vpixelpoints[1]};
									double vpixelyangsort1 = vpixelyangs[vpixelyinds[0]]; 
									int vpixelyind1 = (int)Math.ceil(vpixelysort[0]); 
									int vpixelyind2 = (int)Math.floor(vpixelysort[1]); 
									int vpixelystart = vpixelyind1;
									int vpixelyend = vpixelyind2;
									Direction[] vpixelpointdir1 = MathLib.vectorFromPoints(this.campos, vpixelpoint1);
									double[] vpixelpointdirlen1 = MathLib.vectorLength(vpixelpointdir1);
									Direction[] vpixelpointdir1inv = {vpixelpointdir1[0].invert()};
									Direction[] vpixelpointdir12 = MathLib.vectorFromPoints(vpixelpoint1, vpixelpoint2);
									double[] vpixelpointdir12len = MathLib.vectorLength(vpixelpointdir12);
									double[] vpixelpoint1angle = MathLib.vectorAngle(vpixelpointdir1inv, vpixelpointdir12);
									if ((vpixelyend>=0)&&(vpixelystart<=this.getHeight())) {
										if (vpixelystart<0) {vpixelystart=0;}
										if (vpixelyend>=this.getHeight()) {vpixelyend=this.getHeight()-1;}
										for (int n=vpixelystart;n<=vpixelyend;n++) {
											double vpixelcampointangle = verticalangles[n]-vpixelyangsort1;
											double vpixelpointangle = 180.0f-vpixelpoint1angle[0]-vpixelcampointangle;
											double vpixelpointlen = vpixelpointdirlen1[0]*(Math.sin((Math.PI/180.0f)*vpixelcampointangle)/Math.sin((Math.PI/180.0f)*vpixelpointangle));
											double vpixelpointlenfrac = vpixelpointlen/vpixelpointdir12len[0];
											Direction[] pixelray = {projectedrays[n][j]};
											double[][] copytriangleplanedist = MathLib.rayPlaneDistance(this.campos, pixelray, copytriangleplane);
											double drawdistance = Math.abs(copytriangleplanedist[0][0]);
											if (drawdistance<this.zbuffer[n][j]) {
												this.zbuffer[n][j] = drawdistance;
												if (tritexture!=null) {
													Position[] lineuvpoint1 = {new Position(vpixelpoints[0].tex.u*(tritexture.getWidth()-1),(1.0f-vpixelpoints[0].tex.v)*(tritexture.getHeight()-1),0.0f)};
													Position[] lineuvpoint2 = {new Position(vpixelpoints[1].tex.u*(tritexture.getWidth()-1),(1.0f-vpixelpoints[1].tex.v)*(tritexture.getHeight()-1),0.0f)};
													Direction[] vpixelpointdir12uv = MathLib.vectorFromPoints(lineuvpoint1, lineuvpoint2);
													Position[] lineuv = MathLib.translate(lineuvpoint1, vpixelpointdir12uv[0], vpixelpointlenfrac);
													int lineuvx = (int)Math.round(lineuv[0].x);
													int lineuvy = (int)Math.round(lineuv[0].y);
													if ((lineuvx>=0)&&(lineuvx<tritexture.getWidth())&&(lineuvy>=0)&&(lineuvy<tritexture.getHeight())) {
														Color texcolor = new Color(tritextureimage.getRGB(lineuvx, lineuvy));
														g2.setColor(texcolor);
														g2.drawLine(j, n, j, n);
													}
												} else {
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
									if (foundmat.fileimage!=null) {
										foundmat.snapimage = foundmat.fileimage.getSnapshot();
									}
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