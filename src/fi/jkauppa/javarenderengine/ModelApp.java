package fi.jkauppa.javarenderengine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.Arrays;
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
	private Triangle[] trianglelist = null;
	private Material[] materiallist = null;
	private Position campos = new Position(0,0,0);
	private Rotation camrot = new Rotation(0,0,0);
	private Direction lookdir = new Direction(0,0,-1);
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
		g.setComposite(AlphaComposite.Src);
		g.setColor(Color.BLACK);
		g.setPaint(null);
		g.fillRect(0, 0, renderwidth, renderheight);
		if ((model!=null)&&(trianglelist!=null)&&(materiallist!=null)) {
			Position renderpos = new Position(-campos.x,-campos.y,-campos.z);
			Matrix rendermat = MathLib.rotationMatrix(-this.camrot.x, -this.camrot.y, -this.camrot.z);
			TreeSet<Triangle> transformedtriangletree = new TreeSet<Triangle>(Arrays.asList(MathLib.matrixMultiply(MathLib.translate(this.trianglelist, renderpos), rendermat)));
			Triangle[] transformedtrianglelist = transformedtriangletree.toArray(new Triangle[transformedtriangletree.size()]);
			Plane[] triangleplanes = MathLib.planeFromPoints(transformedtrianglelist);
			Direction[] trianglenormals = MathLib.planeNormals(triangleplanes);
			double[] triangleviewangles = MathLib.vectorAngle(lookdir, trianglenormals);
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
					Color tricolor = this.model.materials[transformedtrianglelist[i].mind].facecolor;
					if (tricolor==null) {tricolor = Color.WHITE;}
					float[] tricolorcomp = tricolor.getRGBColorComponents(new float[3]);
					g.setColor(new Color(tricolorcomp[0]*shadingmultiplier, tricolorcomp[1]*shadingmultiplier, tricolorcomp[2]*shadingmultiplier));
					g.fill(trianglepolygon);
				}
			}
		}
	}

	@Override public void actionPerformed(ActionEvent e) {
		if (this.leftkeydown) {
			this.campos.x -= 20.0f;
		} else if (this.rightkeydown) {
			this.campos.x += 20.0f;
		}
		if (this.upwardkeydown) {
			this.campos.y -= 20.0f;
		} else if (this.downwardkeydown) {
			this.campos.y += 20.0f;
		}
		if (this.forwardkeydown) {
			this.campos.z -= 20.0f;
		} else if (this.backwardkeydown) {
			this.campos.z += 20.0f;
		}
	}

	@Override public void keyReleased(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_D) {
			this.rightkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_A) {
			this.leftkeydown = false;
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
			this.model = null;
			this.campos = new Position(0,0,0);
			this.camrot = new Rotation(0,0,0);
		} else if (e.getKeyCode()==KeyEvent.VK_D) {
			this.rightkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_A) {
			this.leftkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_W) {
			this.upwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_S) {
			this.downwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_SUBTRACT) {
			this.backwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_ADD) {
			this.forwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_Z) {
			this.camrot.x -= 1; if (this.camrot.x<0) {this.camrot.x = 360;}
		} else if (e.getKeyCode()==KeyEvent.VK_X) {
			this.camrot.x += 1; if (this.camrot.x>360) {this.camrot.x = 0;}
		} else if (e.getKeyCode()==KeyEvent.VK_C) {
			this.camrot.y -= 1; if (this.camrot.y<0) {this.camrot.y = 360;}
		} else if (e.getKeyCode()==KeyEvent.VK_V) {
			this.camrot.y += 1; if (this.camrot.y>360) {this.camrot.y = 0;}
		} else if (e.getKeyCode()==KeyEvent.VK_B) {
			this.camrot.z -= 1; if (this.camrot.z<0) {this.camrot.z = 360;}
		} else if (e.getKeyCode()==KeyEvent.VK_N) {
			this.camrot.z += 1; if (this.camrot.z>360) {this.camrot.z = 0;}
		} else if (e.getKeyCode()==KeyEvent.VK_F3) {
			this.filechooser.setDialogTitle("Load File");
			this.filechooser.setApproveButtonText("Load");
			if (this.filechooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				File loadfile = this.filechooser.getSelectedFile();
				this.model = ModelLib.loadWaveFrontOBJFile(loadfile.getPath(), false);
				TreeSet<Material> materialarray = new TreeSet<Material>(); 
				for (int i=0;i<model.materials.length;i++) {
					Material material = model.materials[i];
					material.mind = i;
					materialarray.add(material);
				}
				this.materiallist = materialarray.toArray(new Material[materialarray.size()]);
				TreeSet<Triangle> trianglearray = new TreeSet<Triangle>(); 
				for (int j=0;j<model.objects.length;j++) {
					Material searchmat = new Material(model.objects[j].usemtl);
					int matind = this.materiallist[Arrays.binarySearch(this.materiallist, searchmat)].mind;
					for (int i=0;i<model.objects[j].faceindex.length;i++) {
						Position pos1 = model.vertexlist[model.objects[j].faceindex[i].facevertexindex[0].vertexindex-1];
						Position pos2 = model.vertexlist[model.objects[j].faceindex[i].facevertexindex[1].vertexindex-1];
						Position pos3 = model.vertexlist[model.objects[j].faceindex[i].facevertexindex[2].vertexindex-1];
						Triangle tri = new Triangle(new Position(pos1.x,pos1.y,pos1.z),new Position(pos2.x,pos2.y,pos2.z),new Position(pos3.x,pos3.y,pos3.z));
						tri.oind = j;
						tri.tind = i;
						tri.mind = matind; 
						trianglearray.add(tri);
					}
				}
				this.trianglelist = trianglearray.toArray(new Triangle[trianglearray.size()]);
			}
		}
	}
	
	@Override public void keyTyped(KeyEvent e) {}
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mouseDragged(MouseEvent e) {}
	@Override public void mouseMoved(MouseEvent e) {}
	
	@Override public void mouseWheelMoved(MouseWheelEvent e) {
		this.campos.z += 200.0f*e.getWheelRotation();
	}
	
	@Override public void drop(DropTargetDropEvent dtde) {}

}