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
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import fi.jkauppa.javarenderengine.JavaRenderEngine.AppHandlerPanel;
import fi.jkauppa.javarenderengine.ModelLib.Coordinate;
import fi.jkauppa.javarenderengine.ModelLib.Direction;
import fi.jkauppa.javarenderengine.ModelLib.Entity;
import fi.jkauppa.javarenderengine.ModelLib.Material;
import fi.jkauppa.javarenderengine.ModelLib.Matrix;
import fi.jkauppa.javarenderengine.UtilLib.ModelFileFilters.OBJFileFilter;
import fi.jkauppa.javarenderengine.ModelLib.Position;
import fi.jkauppa.javarenderengine.ModelLib.RenderView;
import fi.jkauppa.javarenderengine.ModelLib.Rotation;
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
	private int polygonfillmode = 1;
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
	private BufferedImage cursorimage = gc.createCompatibleImage(1, 1, Transparency.TRANSLUCENT);
	private Cursor customcursor = null;
	private RenderView renderview = null;
	
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
		this.vfov = 2.0f*MathLib.atand((((double)this.getHeight())/((double)this.getWidth()))*MathLib.tand(this.hfov/2.0f));
		if (this.renderview!=null) {
			g2.drawImage(renderview.renderimage, 0, 0, null);
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
			this.campos = this.campos.copy();
			this.campos.x -= 20.0f*this.camdirs[1].dx;
			this.campos.y -= 20.0f*this.camdirs[1].dy;
			this.campos.z -= 20.0f*this.camdirs[1].dz;
		} else if (this.rightkeydown) {
			this.campos = this.campos.copy();
			this.campos.x += 20.0f*this.camdirs[1].dx;
			this.campos.y += 20.0f*this.camdirs[1].dy;
			this.campos.z += 20.0f*this.camdirs[1].dz;
		}
		if (this.forwardkeydown) {
			this.campos = this.campos.copy();
			this.campos.x += 20.0f*this.camdirs[0].dx;
			this.campos.y += 20.0f*this.camdirs[0].dy;
			this.campos.z += 20.0f*this.camdirs[0].dz;
		} else if (this.backwardkeydown) {
			this.campos = this.campos.copy();
			this.campos.x -= 20.0f*this.camdirs[0].dx;
			this.campos.y -= 20.0f*this.camdirs[0].dy;
			this.campos.z -= 20.0f*this.camdirs[0].dz;
		}
		if (this.upwardkeydown) {
			this.campos = this.campos.copy();
			this.campos.x -= 20.0f*this.camdirs[2].dx;
			this.campos.y -= 20.0f*this.camdirs[2].dy;
			this.campos.z -= 20.0f*this.camdirs[2].dz;
		} else if (this.downwardkeydown) {
			this.campos = this.campos.copy();
			this.campos.x += 20.0f*this.camdirs[2].dx;
			this.campos.y += 20.0f*this.camdirs[2].dy;
			this.campos.z += 20.0f*this.camdirs[2].dz;
		}
		if (this.rollleftkeydown) {
			this.camrot = this.camrot.copy();
			this.camrot.y -= 1.0f;
			updateCameraDirections();
		} else if (this.rollrightkeydown) {
			this.camrot = this.camrot.copy();
			this.camrot.y += 1.0f;
			updateCameraDirections();
		}
		(new RenderViewUpdater()).start();
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
			if (this.polygonfillmode>3) {
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
							tri.mat = foundmat;
							newtrianglelistarray.add(tri);
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
		int origindeltax = (int)Math.floor(((double)(this.getWidth()-1))/2.0f);
		int origindeltay = (int)Math.floor(((double)(this.getHeight()-1))/2.0f);
		int windowcenterx = windowscreenlocation.x + origindeltax;
		int windowcentery = windowscreenlocation.y + origindeltay;
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
		this.camrot = this.camrot.copy();
    	this.camrot.z -= mousedeltax*0.1f;
    	this.camrot.x -= mousedeltay*0.1f;
    	updateCameraDirections();
		if ((this.mouselocationx<=0)||(this.mouselocationy<=0)||(this.mouselocationx>=(this.lastrenderwidth-1))||(this.mouselocationy>=(this.lastrenderheight-1))) {
			mouseExited(e);
		}
	}
	@Override public void mouseWheelMoved(MouseWheelEvent e) {}
	@Override public void drop(DropTargetDropEvent dtde) {}

	private class RenderViewUpdater extends Thread {
		private static boolean renderupdaterrunning = false;
		public void run() {
			if (!RenderViewUpdater.renderupdaterrunning) {
				RenderViewUpdater.renderupdaterrunning = true;
				if (ModelApp.this.polygonfillmode==1) {
					ModelApp.this.renderview = ModelLib.renderProjectedPlaneViewSoftware(ModelApp.this.campos, ModelApp.this.entitylist, ModelApp.this.getWidth(), ModelApp.this.hfov, ModelApp.this.getHeight(), ModelApp.this.vfov, ModelApp.this.cameramat, false, ModelApp.this.mouselocationx, ModelApp.this.mouselocationy);
				} else if (ModelApp.this.polygonfillmode==2) {
					ModelApp.this.renderview = ModelLib.renderSpheremapPlaneViewSoftware(ModelApp.this.campos, ModelApp.this.entitylist, ModelApp.this.getWidth(), ModelApp.this.getHeight(), ModelApp.this.cameramat, false, ModelApp.this.mouselocationx, ModelApp.this.mouselocationy);
				} else if (ModelApp.this.polygonfillmode==3) {
					ModelApp.this.renderview = ModelLib.renderCubemapPlaneViewSoftware(ModelApp.this.campos, ModelApp.this.entitylist, ModelApp.this.getWidth(), ModelApp.this.getHeight(), ModelApp.this.cameramat, false, ModelApp.this.mouselocationx, ModelApp.this.mouselocationy);
				}
				RenderViewUpdater.renderupdaterrunning = false;
			}
		}
	}
	
}