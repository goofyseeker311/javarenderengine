package fi.jkauppa.javarenderengine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.VolatileImage;
import java.io.File;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JFileChooser;
import fi.jkauppa.javarenderengine.JavaRenderEngine.AppHandler;
import fi.jkauppa.javarenderengine.MathLib.Direction;
import fi.jkauppa.javarenderengine.MathLib.Matrix;
import fi.jkauppa.javarenderengine.MathLib.Plane;
import fi.jkauppa.javarenderengine.MathLib.Position;
import fi.jkauppa.javarenderengine.MathLib.Rotation;
import fi.jkauppa.javarenderengine.MathLib.Triangle;
import fi.jkauppa.javarenderengine.UtilLib.ModelFileFilters.OBJFileFilter;
import fi.jkauppa.javarenderengine.ModelLib.Material;
import fi.jkauppa.javarenderengine.ModelLib.Model;

public class ModelApp implements AppHandler {
	private Model model = null;
	private TreeMap<Triangle,Material> trianglematerialmap = new TreeMap<Triangle,Material>();
	private Position campos = new Position(0,0,0);
	private Rotation camrot = new Rotation(0.0f, 0.0f, 0.0f);
	private Matrix rendermat = MathLib.rotationMatrix(0.0f, 0.0f, 0.0f);
	private final Direction lookdir = new Direction(0,0,-1);
	private final Direction[] lookdirs = {new Direction(0,0,-1),new Direction(1,0,0),new Direction(0,-1,0)};
	private Direction[] camdirs = lookdirs;
	private final double drawdepthscale = 0.00035f;
	private int origindeltax = 0, origindeltay = 0; 
	private int lastrenderwidth = 0, lastrenderheight = 0;
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
	
	public ModelApp() {
		this.filechooser.addChoosableFileFilter(this.objfilefilter);
		this.filechooser.setFileFilter(this.objfilefilter);
		this.filechooser.setAcceptAllFileFilterUsed(false);
	}
	
	@Override public void renderWindow(Graphics2D g, int renderwidth, int renderheight, double deltatimesec, double deltatimefps) {
		this.origindeltax = (int)Math.floor(((double)renderwidth)/2.0f);
		this.origindeltay = (int)Math.floor(((double)renderheight)/2.0f);
		if ((this.lastrenderwidth!=renderwidth)||(this.lastrenderheight!=renderheight)) {
			this.lastrenderwidth = renderwidth;
			this.lastrenderheight = renderheight;
		}
		g.setComposite(AlphaComposite.SrcOver);
		g.setColor(Color.BLACK);
		g.setPaint(null);
		g.fillRect(0, 0, renderwidth, renderheight);
		Position renderpos = new Position(-campos.x,-campos.y,-campos.z);
		Triangle[] copytrianglelist = this.trianglematerialmap.keySet().toArray(new Triangle[this.trianglematerialmap.size()]);
		for (int i=0;i<copytrianglelist.length;i++) {copytrianglelist[i].sind = i;}
		Triangle[] transformedtriangles = MathLib.translate(copytrianglelist, renderpos);
		transformedtriangles = MathLib.matrixMultiply(transformedtriangles, rendermat);
		TreeSet<Triangle> transformedtrianglearray = new TreeSet<Triangle>(Arrays.asList(transformedtriangles));
		Triangle[] transformedtrianglelist = transformedtrianglearray.toArray(new Triangle[transformedtrianglearray.size()]);
		Plane[] triangleplanes = MathLib.planeFromPoints(transformedtrianglelist);
		Direction[] trianglenormals = MathLib.planeNormals(triangleplanes);
		double[] triangleviewangles = MathLib.vectorAngle(this.lookdir, trianglenormals);
		for (int i=0;i<transformedtrianglelist.length;i++) {
			double pos1s = (-transformedtrianglelist[i].pos1.z)*this.drawdepthscale+1;
			double pos2s = (-transformedtrianglelist[i].pos2.z)*this.drawdepthscale+1;
			double pos3s = (-transformedtrianglelist[i].pos3.z)*this.drawdepthscale+1;
			if ((pos1s>0)&&(pos2s>0)&&(pos3s>0)) {
				int pos1x = (int)Math.round(transformedtrianglelist[i].pos1.x/pos1s)+this.origindeltax;
				int pos1y = (int)Math.round(transformedtrianglelist[i].pos1.y/pos1s)+this.origindeltay;
				int pos2x = (int)Math.round(transformedtrianglelist[i].pos2.x/pos2s)+this.origindeltax;
				int pos2y = (int)Math.round(transformedtrianglelist[i].pos2.y/pos2s)+this.origindeltay;
				int pos3x = (int)Math.round(transformedtrianglelist[i].pos3.x/pos3s)+this.origindeltax;
				int pos3y = (int)Math.round(transformedtrianglelist[i].pos3.y/pos3s)+this.origindeltay;
				Polygon trianglepolygon = new Polygon();
				trianglepolygon.addPoint(pos1x, pos1y);
				trianglepolygon.addPoint(pos2x, pos2y);
				trianglepolygon.addPoint(pos3x, pos3y);
				double triangleviewangle = triangleviewangles[i];
				if (triangleviewangle>90.0f) {
					triangleviewangle = 180-triangleviewangle;
				}
				float shadingmultiplier = (90.0f-(((float)triangleviewangle))/1.5f)/90.0f;
				Material copymaterial = this.trianglematerialmap.get(copytrianglelist[transformedtrianglelist[i].sind]);
				Color tricolor = copymaterial.facecolor;
				float alphacolor = copymaterial.transparency;
				if (tricolor==null) {tricolor = Color.WHITE;}
				float[] tricolorcomp = tricolor.getRGBComponents(new float[4]);
				g.setColor(new Color(tricolorcomp[0]*shadingmultiplier, tricolorcomp[1]*shadingmultiplier, tricolorcomp[2]*shadingmultiplier, alphacolor));
				VolatileImage tritexture = copymaterial.fileimage;
				if (tritexture==null) {
					g.fill(trianglepolygon);
				} else { 
					g.clip(trianglepolygon);
					Rectangle polygonarea = trianglepolygon.getBounds();
					g.drawImage(tritexture, polygonarea.x, polygonarea.y, polygonarea.x+polygonarea.width-1, polygonarea.y+polygonarea.height-1, 0, 0, tritexture.getWidth()-1, tritexture.getHeight()-1, null);
					g.setClip(null);
				}
			}
		}
	}

	private void updateCameraDirections() {
		Matrix camrotmatz = MathLib.rotationMatrix(0.0f, 0.0f, this.camrot.z);
		Matrix camrotmaty = MathLib.rotationMatrix(0.0f, this.camrot.y, 0.0f);
		Matrix camrotmatx = MathLib.rotationMatrix(this.camrot.x, 0.0f, 0.0f);
		Matrix renderrotmatx = MathLib.rotationMatrix(-this.camrot.x, 0.0f, 0.0f);
		Matrix renderrotmaty = MathLib.rotationMatrix(0.0f, -this.camrot.y, 0.0f);
		Matrix renderrotmatz = MathLib.rotationMatrix(0.0f, 0.0f, -this.camrot.z);
		Matrix eyeonemat = MathLib.rotationMatrix(0.0f, 0.0f, 0.0f);
		Matrix camrotmat = MathLib.matrixMultiply(eyeonemat, camrotmatz);
		camrotmat = MathLib.matrixMultiply(camrotmat, camrotmaty);
		camrotmat = MathLib.matrixMultiply(camrotmat, camrotmatx);
		Matrix renderrotmat = MathLib.matrixMultiply(eyeonemat, renderrotmatx);
		renderrotmat = MathLib.matrixMultiply(renderrotmat, renderrotmaty);
		renderrotmat = MathLib.matrixMultiply(renderrotmat, renderrotmatz);
		Direction[] camlookdirs = MathLib.matrixMultiply(this.lookdirs, camrotmat);
		this.rendermat = renderrotmat;
		this.camdirs = camlookdirs;
	}
	
	@Override public void actionPerformed(ActionEvent e) {
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
			this.campos.x += 20.0f*this.camdirs[2].dx;
			this.campos.y += 20.0f*this.camdirs[2].dy;
			this.campos.z += 20.0f*this.camdirs[2].dz;
		} else if (this.downwardkeydown) {
			this.campos.x -= 20.0f*this.camdirs[2].dx;
			this.campos.y -= 20.0f*this.camdirs[2].dy;
			this.campos.z -= 20.0f*this.camdirs[2].dz;
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
			this.model = null;
			this.trianglematerialmap.clear();
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
				this.model = ModelLib.loadWaveFrontOBJFile(loadfile.getPath(), false);
				for (int j=0;j<model.objects.length;j++) {
					Material foundmat = null;
					for (int i=0;(i<this.model.materials.length)&&(foundmat==null);i++) {
						if (this.model.objects[j].usemtl.equals(this.model.materials[i].materialname)) {
							foundmat = this.model.materials[i];
						}
					}
					if (foundmat==null) {
						foundmat = new Material();
						foundmat.facecolor = Color.WHITE;
					}
					for (int i=0;i<model.objects[j].faceindex.length;i++) {
						Position pos1 = model.vertexlist[model.objects[j].faceindex[i].facevertexindex[0].vertexindex-1];
						Position pos2 = model.vertexlist[model.objects[j].faceindex[i].facevertexindex[1].vertexindex-1];
						Position pos3 = model.vertexlist[model.objects[j].faceindex[i].facevertexindex[2].vertexindex-1];
						Triangle tri = new Triangle(new Position(pos1.x,pos1.y,pos1.z),new Position(pos2.x,pos2.y,pos2.z),new Position(pos3.x,pos3.y,pos3.z));
						this.trianglematerialmap.put(tri, foundmat);
					}
				}
			}
		}
	}
	
	@Override public void keyTyped(KeyEvent e) {}

	@Override public void mousePressed(MouseEvent e) {this.mouselocationx=e.getX();this.mouselocationy=e.getY();mouseDragged(e);}
	@Override public void mouseDragged(MouseEvent e) {
		this.mouselastlocationx=this.mouselocationx;this.mouselastlocationy=this.mouselocationy;
		this.mouselocationx=e.getX();this.mouselocationy=e.getY();
    	int mousedeltax = this.mouselocationx - this.mouselastlocationx; 
    	int mousedeltay = this.mouselocationy - this.mouselastlocationy;
    	this.camrot.z += mousedeltax*0.1f;
    	this.camrot.x += mousedeltay*0.1f;
    	updateCameraDirections();
	}
	
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mouseMoved(MouseEvent e) {}
	@Override public void mouseWheelMoved(MouseWheelEvent e) {}
	@Override public void drop(DropTargetDropEvent dtde) {}

}