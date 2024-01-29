package fi.jkauppa.javarenderengine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
import fi.jkauppa.javarenderengine.UtilLib.ImageFileFilters.BMPFileFilter;
import fi.jkauppa.javarenderengine.UtilLib.ImageFileFilters.GIFFileFilter;
import fi.jkauppa.javarenderengine.UtilLib.ImageFileFilters.JPGFileFilter;
import fi.jkauppa.javarenderengine.UtilLib.ImageFileFilters.PNGFileFilter;
import fi.jkauppa.javarenderengine.UtilLib.ImageFileFilters.WBMPFileFilter;
import fi.jkauppa.javarenderengine.UtilLib.ModelFileFilters.OBJFileFilter;
import fi.jkauppa.javarenderengine.UtilLib.ModelFileFilters.STLFileFilter;
import fi.jkauppa.javarenderengine.ModelLib.Position;
import fi.jkauppa.javarenderengine.ModelLib.RenderView;
import fi.jkauppa.javarenderengine.ModelLib.Rotation;
import fi.jkauppa.javarenderengine.ModelLib.Triangle;
import fi.jkauppa.javarenderengine.ModelLib.Model;
import fi.jkauppa.javarenderengine.ModelLib.ModelFaceIndex;
import fi.jkauppa.javarenderengine.ModelLib.ModelFaceVertexIndex;
import fi.jkauppa.javarenderengine.ModelLib.ModelLineIndex;
import fi.jkauppa.javarenderengine.ModelLib.ModelObject;
import fi.jkauppa.javarenderengine.ModelLib.Plane;

public class CADApp extends AppHandlerPanel {
	private static final long serialVersionUID = 1L;
	private boolean draglinemode = false;
	private boolean snaplinemode = false;
	private Material drawmat = new Material(Color.getHSBColor(0.0f, 0.0f, 1.0f),1.0f);
	private int polygonfillmode = 1;
	private Position[] selecteddragvertex = null;
	private Triangle[] mouseovertriangle = null;
	private Position[] mouseoververtex = null;
	private Line[] mouseoverline = null;
	private int mouselocationx = 0, mouselocationy = 0;
	private int mouselastlocationx = -1, mouselastlocationy = -1; 
	private int origindeltax = 0, origindeltay = 0;
	private double editplanedistance = 1371.023f;
	private Position drawstartpos = new Position(0,0,0);
	private Position editpos = new Position(0.0f,0.0f,0.0f);
	private Position campos = new Position(0.0f,0.0f,this.editplanedistance);
	private Rotation camrot = new Rotation(0.0f, 0.0f, 0.0f);
	private Matrix cameramat = MathLib.rotationMatrix(0.0f, 0.0f, 0.0f);
	private final Direction[] lookdirs = MathLib.projectedCameraDirections(cameramat);
	private Direction[] camdirs = lookdirs;
	private double hfov = 70.0f;
	private double vfov = 43.0f;
	private int gridstep = 20;
	private TreeSet<Line> linelisttree = new TreeSet<Line>();
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
	private boolean pitchupkeydown = false;
	private boolean pitchdownkeydown = false;
	private boolean yawleftkeydown = false;
	private boolean yawrightkeydown = false;
	private RenderView hardwarerenderview = null;
	private RenderView softwarerenderview = null;
	private JFileChooser imagechooser = new JFileChooser();
	private PNGFileFilter pngfilefilter = new PNGFileFilter();
	private JPGFileFilter jpgfilefilter = new JPGFileFilter();
	private GIFFileFilter giffilefilter = new GIFFileFilter();
	private BMPFileFilter bmpfilefilter = new BMPFileFilter();
	private WBMPFileFilter wbmpfilefilter = new WBMPFileFilter();
	
	public CADApp() {
		this.filechooser.addChoosableFileFilter(this.objfilefilter);
		this.filechooser.addChoosableFileFilter(this.stlfilefilter);
		this.filechooser.setFileFilter(this.objfilefilter);
		this.filechooser.setAcceptAllFileFilterUsed(false);
		this.imagechooser.addChoosableFileFilter(this.pngfilefilter);
		this.imagechooser.addChoosableFileFilter(this.jpgfilefilter);
		this.imagechooser.addChoosableFileFilter(this.giffilefilter);
		this.imagechooser.addChoosableFileFilter(this.bmpfilefilter);
		this.imagechooser.addChoosableFileFilter(this.wbmpfilefilter);
		this.imagechooser.setFileFilter(this.pngfilefilter);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	}
	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setComposite(AlphaComposite.Src);
		g2.setColor(Color.BLACK);
		g2.setPaint(null);
		g2.setClip(null);
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		this.origindeltax = (int)Math.floor(((double)this.getWidth())/2.0f);
		this.origindeltay = (int)Math.floor(((double)this.getHeight())/2.0f);
		this.vfov = 2.0f*MathLib.atand((((double)this.getHeight())/((double)this.getWidth()))*MathLib.tand(this.hfov/2.0f));
		this.editplanedistance = (((double)this.getWidth())/2.0f)/MathLib.tand(hfov/2.0f);
		if ((this.polygonfillmode==3)&&(this.softwarerenderview!=null)) {
			g2.drawImage(this.softwarerenderview.renderimage, 0, 0, null);
		} else if (this.hardwarerenderview!=null) {
			CADApp.this.softwarerenderview = null;
			g2.drawImage(this.hardwarerenderview.renderimage, 0, 0, null);
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
		Position[] editposarray = {this.editpos};
		Position[] camposarray = MathLib.translate(editposarray, camlookdirs[0], -this.editplanedistance);
		this.campos = camposarray[0];
		this.cameramat = camrotmat;
		this.camdirs = camlookdirs;
	}
	
	@Override public void timerTick() {
		double movementstep = 1.0f;
		if (this.snaplinemode) {
			movementstep = this.gridstep;
		}
		if (this.leftkeydown) {
			this.editpos = this.editpos.copy();
			this.editpos.x -= movementstep*this.camdirs[1].dx;
			this.editpos.y -= movementstep*this.camdirs[1].dy;
			this.editpos.z -= movementstep*this.camdirs[1].dz;		} else if (this.rightkeydown) {
			this.editpos = this.editpos.copy();
			this.editpos.x += movementstep*this.camdirs[1].dx;
			this.editpos.y += movementstep*this.camdirs[1].dy;
			this.editpos.z += movementstep*this.camdirs[1].dz;
		}
		if (this.forwardkeydown) {
			this.editpos = this.editpos.copy();
			this.editpos.x += movementstep*this.camdirs[0].dx;
			this.editpos.y += movementstep*this.camdirs[0].dy;
			this.editpos.z += movementstep*this.camdirs[0].dz;
		} else if (this.backwardkeydown) {
			this.editpos = this.editpos.copy();
			this.editpos.x -= movementstep*this.camdirs[0].dx;
			this.editpos.y -= movementstep*this.camdirs[0].dy;
			this.editpos.z -= movementstep*this.camdirs[0].dz;
		}
		if (this.upwardkeydown) {
			this.editpos = this.editpos.copy();
			this.editpos.x -= movementstep*this.camdirs[2].dx;
			this.editpos.y -= movementstep*this.camdirs[2].dy;
			this.editpos.z -= movementstep*this.camdirs[2].dz;
		} else if (this.downwardkeydown) {
			this.editpos = this.editpos.copy();
			this.editpos.x += movementstep*this.camdirs[2].dx;
			this.editpos.y += movementstep*this.camdirs[2].dy;
			this.editpos.z += movementstep*this.camdirs[2].dz;
		}
		if (this.rollleftkeydown) {
			this.camrot = this.camrot.copy();
			this.camrot.y -= (movementstep/((double)this.gridstep));
		} else if (this.rollrightkeydown) {
			this.camrot = this.camrot.copy();
			this.camrot.y += (movementstep/((double)this.gridstep));
		}
		if (this.yawleftkeydown) {
			this.camrot = this.camrot.copy();
        	this.camrot.z += (movementstep/((double)this.gridstep))*1.0f;
		} else if (this.yawrightkeydown) {
			this.camrot = this.camrot.copy();
        	this.camrot.z -= (movementstep/((double)this.gridstep))*1.0f;
		}
		if (this.pitchupkeydown) {
			this.camrot = this.camrot.copy();
        	this.camrot.x += (movementstep/((double)this.gridstep))*1.0f;
		} else if (this.pitchdownkeydown) {
			this.camrot = this.camrot.copy();
        	this.camrot.x -= (movementstep/((double)this.gridstep))*1.0f;
		}
		updateCameraDirections();
		(new HardwareRenderViewUpdater()).start();
		(new SoftwareRenderViewUpdater()).start();
	}
	
	@Override public void keyTyped(KeyEvent e) {}
	@Override public void keyReleased(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_UP) {
			this.pitchupkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_DOWN) {
			this.pitchdownkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_LEFT) {
			this.yawleftkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_RIGHT) {
			this.yawrightkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_SHIFT) {
			this.snaplinemode = false;
		} else if (e.getKeyCode()==KeyEvent.VK_W) {
			this.upwardkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_S) {
			this.downwardkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_A) {
			this.leftkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_D) {
			this.rightkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_SPACE) {
			this.forwardkeydown = false;
		} else if (e.getKeyCode()==KeyEvent.VK_C) {
			this.backwardkeydown = false;
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
			this.linelisttree.clear();
			this.entitylist = null;
			this.editpos = new Position(0.0f, 0.0f, 0.0f);
			this.campos = new Position(0.0f,0.0f,this.editplanedistance);
			this.camrot = new Rotation(0.0f, 0.0f, 0.0f);
		} else if (e.getKeyCode()==KeyEvent.VK_INSERT) {
			float[] drawcolorhsb = Color.RGBtoHSB(this.drawmat.facecolor.getRed(), this.drawmat.facecolor.getGreen(), this.drawmat.facecolor.getBlue(), new float[3]);
			drawcolorhsb[0] += 0.01f; if (drawcolorhsb[0]>1.0f) {drawcolorhsb[0] = 0.0f;}
			Color hsbcolor = Color.getHSBColor(drawcolorhsb[0], drawcolorhsb[1], drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			Color newfacecolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.drawmat.transparency);
			this.drawmat = new Material(newfacecolor,this.drawmat.transparency);
		} else if (e.getKeyCode()==KeyEvent.VK_DELETE) {
			float[] drawcolorhsb = Color.RGBtoHSB(this.drawmat.facecolor.getRed(), this.drawmat.facecolor.getGreen(), this.drawmat.facecolor.getBlue(), new float[3]);
			drawcolorhsb[0] -= 0.01f; if (drawcolorhsb[0]<0.0f) {drawcolorhsb[0] = 1.0f;}
			Color hsbcolor = Color.getHSBColor(drawcolorhsb[0], drawcolorhsb[1], drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			Color newfacecolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.drawmat.transparency);
			this.drawmat = new Material(newfacecolor,this.drawmat.transparency);
		} else if (e.getKeyCode()==KeyEvent.VK_HOME) {
			float[] drawcolorhsb = Color.RGBtoHSB(this.drawmat.facecolor.getRed(), this.drawmat.facecolor.getGreen(), this.drawmat.facecolor.getBlue(), new float[3]);
			drawcolorhsb[1] += 0.01f; if (drawcolorhsb[1]>1.0f) {drawcolorhsb[1] = 1.0f;}
			Color hsbcolor = Color.getHSBColor(drawcolorhsb[0], drawcolorhsb[1], drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			Color newfacecolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.drawmat.transparency);
			this.drawmat = new Material(newfacecolor,this.drawmat.transparency);
		} else if (e.getKeyCode()==KeyEvent.VK_END) {
			float[] drawcolorhsb = Color.RGBtoHSB(this.drawmat.facecolor.getRed(), this.drawmat.facecolor.getGreen(), this.drawmat.facecolor.getBlue(), new float[3]);
			drawcolorhsb[1] -= 0.01f; if (drawcolorhsb[1]<0.0f) {drawcolorhsb[1] = 0.0f;}
			Color hsbcolor = Color.getHSBColor(drawcolorhsb[0], drawcolorhsb[1], drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			Color newfacecolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.drawmat.transparency);
			this.drawmat = new Material(newfacecolor,this.drawmat.transparency);
		} else if (e.getKeyCode()==KeyEvent.VK_PAGE_UP) {
			float[] drawcolorhsb = Color.RGBtoHSB(this.drawmat.facecolor.getRed(), this.drawmat.facecolor.getGreen(), this.drawmat.facecolor.getBlue(), new float[3]);
			drawcolorhsb[2] += 0.01f; if (drawcolorhsb[2]>1.0f) {drawcolorhsb[2] = 1.0f;}
			Color hsbcolor = Color.getHSBColor(drawcolorhsb[0], drawcolorhsb[1], drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			Color newfacecolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.drawmat.transparency);
			this.drawmat = new Material(newfacecolor,this.drawmat.transparency);
		} else if (e.getKeyCode()==KeyEvent.VK_PAGE_DOWN) {
			float[] drawcolorhsb = Color.RGBtoHSB(this.drawmat.facecolor.getRed(), this.drawmat.facecolor.getGreen(), this.drawmat.facecolor.getBlue(), new float[3]);
			drawcolorhsb[2] -= 0.01f; if (drawcolorhsb[2]<0.0f) {drawcolorhsb[2] = 0.0f;}
			Color hsbcolor = Color.getHSBColor(drawcolorhsb[0], drawcolorhsb[1], drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			Color newfacecolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.drawmat.transparency);
			this.drawmat = new Material(newfacecolor,this.drawmat.transparency);
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD9) {
			float newtransparency = this.drawmat.transparency+0.01f; if (newtransparency>1.0f) {newtransparency = 1.0f;}
			float[] colorvalues = this.drawmat.facecolor.getRGBColorComponents(new float[3]);
			Color newfacecolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.drawmat.transparency);
			this.drawmat = new Material(newfacecolor,newtransparency);
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD8) {
			float newtransparency = this.drawmat.transparency-0.01f; if (newtransparency<0.0f) {newtransparency = 0.0f;}
			float[] colorvalues = this.drawmat.facecolor.getRGBColorComponents(new float[3]);
			Color newfacecolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.drawmat.transparency);
			this.drawmat = new Material(newfacecolor,newtransparency);
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD7) {
			if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
				Triangle[] stri = {this.mouseovertriangle[this.mouseovertriangle.length-1]};
				if ((stri[0].norm==null)||(stri[0].norm.isZero())) {
					Plane[] striplane = MathLib.planeFromPoints(stri);
					Direction[] strinorm = MathLib.planeNormals(striplane);
					stri[0].norm = strinorm[0];
				} else {
					stri[0].norm = stri[0].norm.invert();
				}
			}
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD4) {
			if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
				Triangle[] stri = {this.mouseovertriangle[this.mouseovertriangle.length-1]};
				stri[0].norm = new Direction(0.0f,0.0f,0.0f);
			}
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD1) {
	    	Triangle mousetriangle = null;
    		if (this.softwarerenderview!=null) {
    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.getWidth())&&(this.mouselocationy>=0)&&(this.mouselocationy<this.getHeight())) {
	    			mousetriangle = this.softwarerenderview.tbuffer[this.mouselocationy][this.mouselocationx];
    			}
    		} else if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
    			mousetriangle = this.mouseovertriangle[this.mouseovertriangle.length-1];
    		}
			if (mousetriangle!=null) {
	        	mousetriangle.pos1.tex = mousetriangle.pos1.tex.copy();
	        	mousetriangle.pos2.tex = mousetriangle.pos2.tex.copy();
	        	mousetriangle.pos3.tex = mousetriangle.pos3.tex.copy();
	    		mousetriangle.pos1.tex.u = -mousetriangle.pos1.tex.u;
	    		mousetriangle.pos2.tex.u = -mousetriangle.pos2.tex.u;
	    		mousetriangle.pos3.tex.u = -mousetriangle.pos3.tex.u;
			}
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD0) {
			(new EntityListUpdater()).start();
		} else if (e.getKeyCode()==KeyEvent.VK_ENTER) {
			this.polygonfillmode += 1;
			if (this.polygonfillmode>3) {
				this.polygonfillmode = 1;
			}
		} else if (e.getKeyCode()==KeyEvent.VK_UP) {
			this.pitchupkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_DOWN) {
			this.pitchdownkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_LEFT) {
			this.yawleftkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_RIGHT) {
			this.yawrightkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_SHIFT) {
			this.snaplinemode = true;
		} else if (e.getKeyCode()==KeyEvent.VK_W) {
			this.upwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_S) {
			this.downwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_A) {
			this.leftkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_D) {
			this.rightkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_SPACE) {
			this.forwardkeydown = true;
		} else if (e.getKeyCode()==KeyEvent.VK_C) {
			this.backwardkeydown = true;
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
					String saveimgfile = savefile.getName();
					if (savemtlfile.toLowerCase().endsWith(".obj")) {
						savemtlfile = savemtlfile.substring(0, savemtlfile.length()-4).concat(".mtl");
						saveimgfile = savemtlfile.substring(0, savemtlfile.length()-4);
					} else {
						saveobjfile = saveobjfile.concat(".obj");
						savemtlfile = savemtlfile.concat(".mtl");
					}
					savemodel.mtllib = savemtlfile;
					TreeSet<Material> materiallistarray = new TreeSet<Material>();
					TreeSet<Position> vertexlistarray = new TreeSet<Position>();
					TreeSet<Direction> normallistarray = new TreeSet<Direction>();
					TreeSet<Coordinate> texcoordlistarray = new TreeSet<Coordinate>();
					savemodel.objects = new ModelObject[this.entitylist.length];
					normallistarray.add(new Direction(0, 0, 0));
					texcoordlistarray.add(new Coordinate(0.0f,0.0f));
					for (int j=0;j<this.entitylist.length;j++) {
						savemodel.objects[j] = new ModelObject("JREOBJ"+(j+1));
						Triangle[] copytrianglelist = this.entitylist[j].trianglelist;
					    vertexlistarray.addAll(Arrays.asList(this.entitylist[j].vertexlist));
						for (int i=0;i<copytrianglelist.length;i++) {
							vertexlistarray.add(copytrianglelist[i].pos1);
							vertexlistarray.add(copytrianglelist[i].pos2);
							vertexlistarray.add(copytrianglelist[i].pos3);
							normallistarray.add(copytrianglelist[i].norm);
							texcoordlistarray.add(copytrianglelist[i].pos1.tex);
							texcoordlistarray.add(copytrianglelist[i].pos2.tex);
							texcoordlistarray.add(copytrianglelist[i].pos3.tex);
							if (copytrianglelist[i].mat.facecolor==null) {copytrianglelist[i].mat.facecolor = Color.WHITE;}
							materiallistarray.add(copytrianglelist[i].mat);
						}
					}
					savemodel.materials = materiallistarray.toArray(new Material[materiallistarray.size()]);
					savemodel.vertexlist = vertexlistarray.toArray(new Position[vertexlistarray.size()]);
					savemodel.facenormals = normallistarray.toArray(new Direction[normallistarray.size()]);
					savemodel.texturecoords = texcoordlistarray.toArray(new Coordinate[texcoordlistarray.size()]);
					int imagenum = 0;
					for (int i=0;i<savemodel.materials.length;i++) {
						savemodel.materials[i].materialname = "JREMAT"+(i+1);
						if (savemodel.materials[i].fileimage!=null) {
							imagenum += 1;
							savemodel.materials[i].filename = saveimgfile+"_"+imagenum+".png";
						}
					}
					for (int j=0;j<this.entitylist.length;j++) {
						Triangle[] copytrianglelist = this.entitylist[j].trianglelist;
						for (int i=0;i<copytrianglelist.length;i++) {
							ModelFaceVertexIndex[] trianglevertex = new ModelFaceVertexIndex[3];
							int trianglefacenormalind = Arrays.binarySearch(savemodel.facenormals, copytrianglelist[i].norm)+1;
							trianglevertex[0] = new ModelFaceVertexIndex(Arrays.binarySearch(savemodel.vertexlist,copytrianglelist[i].pos1)+1,Arrays.binarySearch(savemodel.texturecoords,copytrianglelist[i].pos1.tex)+1,trianglefacenormalind);
							trianglevertex[1] = new ModelFaceVertexIndex(Arrays.binarySearch(savemodel.vertexlist,copytrianglelist[i].pos2)+1,Arrays.binarySearch(savemodel.texturecoords,copytrianglelist[i].pos2.tex)+1,trianglefacenormalind);
							trianglevertex[2] = new ModelFaceVertexIndex(Arrays.binarySearch(savemodel.vertexlist,copytrianglelist[i].pos3)+1,Arrays.binarySearch(savemodel.texturecoords,copytrianglelist[i].pos3.tex)+1,trianglefacenormalind);
							Material copymaterial = copytrianglelist[i].mat;
							int searchmatindex = Arrays.binarySearch(savemodel.materials, copymaterial);
							ModelFaceIndex[] objectfaceindex = savemodel.objects[j].faceindex;
							ArrayList<ModelFaceIndex> faceindexarray = (objectfaceindex!=null)?(new ArrayList<ModelFaceIndex>(Arrays.asList(objectfaceindex))):(new ArrayList<ModelFaceIndex>());
							ModelFaceIndex newmodelfaceindex = new ModelFaceIndex(trianglevertex);
							newmodelfaceindex.usemtl = savemodel.materials[searchmatindex].materialname;
							faceindexarray.add(newmodelfaceindex);
							savemodel.objects[j].faceindex = faceindexarray.toArray(new ModelFaceIndex[faceindexarray.size()]);
						}
						if (!f2shiftdown) {
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
					}
					ModelLib.saveWaveFrontOBJFile(saveobjfile, savemodel);
				}
			}
		} else if (e.getKeyCode()==KeyEvent.VK_F3) {
		    int onmaskf3shiftdown = KeyEvent.SHIFT_DOWN_MASK;
		    int offmaskf3shiftdown = KeyEvent.CTRL_DOWN_MASK|KeyEvent.ALT_DOWN_MASK;
		    boolean f3shiftdown = (e.getModifiersEx() & (onmaskf3shiftdown | offmaskf3shiftdown)) == onmaskf3shiftdown;
		    int onmaskf3ctrldown = KeyEvent.CTRL_DOWN_MASK;
		    int offmaskf3ctrldown = KeyEvent.SHIFT_DOWN_MASK|KeyEvent.ALT_DOWN_MASK;
		    boolean f3ctrldown = (e.getModifiersEx() & (onmaskf3ctrldown | offmaskf3ctrldown)) == onmaskf3ctrldown;
		    if (f3shiftdown) {
				this.imagechooser.setDialogTitle("Load File");
				this.imagechooser.setApproveButtonText("Load");
				if (this.imagechooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
					File loadfile = this.imagechooser.getSelectedFile();
					Material newmat = new Material(Color.WHITE,1.0f);
					newmat.fileimage = UtilLib.loadImage(loadfile.getPath(), false);
					this.drawmat = newmat;
				}
		    } else if (f3ctrldown) {
		    	//TODO load insert object
		    } else {
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
								newentitylist[0].trianglelist[i].mat = new Material(Color.WHITE,1.0f);
							}
						}
						this.linelisttree.addAll(Arrays.asList(MathLib.generateLineList(newentitylist[0].trianglelist)));
						Line[] linelist = this.linelisttree.toArray(new Line[this.linelisttree.size()]);
						newentitylist[0].vertexlist = MathLib.generateVertexList(linelist);
						newentitylist[0].aabbboundaryvolume = MathLib.axisAlignedBoundingBox(newentitylist[0].vertexlist);
						newentitylist[0].sphereboundaryvolume = MathLib.pointCloudCircumSphere(newentitylist[0].vertexlist);
						this.entitylist = newentitylist;
					} else {
						Model loadmodel = ModelLib.loadWaveFrontOBJFile(loadfile.getPath(), false);
						ArrayList<Entity> newentitylist = new ArrayList<Entity>();
						for (int j=0;j<loadmodel.objects.length;j++) {
							Entity newentity = new Entity();
							TreeSet<Line> newlinelisttree = new TreeSet<Line>();
							TreeSet<Line> newnontrianglelinelisttree = new TreeSet<Line>();
							ArrayList<Triangle> newtrianglelistarray = new ArrayList<Triangle>();
							for (int i=0;i<loadmodel.objects[j].faceindex.length;i++) {
								if (loadmodel.objects[j].faceindex[i].facevertexindex.length==1) {
									Position pos1 = loadmodel.vertexlist[loadmodel.objects[j].faceindex[i].facevertexindex[0].vertexindex-1];
									Line newline = new Line(pos1.copy(), pos1.copy());
									newlinelisttree.add(newline);
									this.linelisttree.add(newline);
									newnontrianglelinelisttree.add(newline);
								} else if (loadmodel.objects[j].faceindex[i].facevertexindex.length==2) {
									Position pos1 = loadmodel.vertexlist[loadmodel.objects[j].faceindex[i].facevertexindex[0].vertexindex-1];
									Position pos2 = loadmodel.vertexlist[loadmodel.objects[j].faceindex[i].facevertexindex[1].vertexindex-1];
									Line newline = new Line(pos1.copy(), pos2.copy());
									newlinelisttree.add(newline);
									this.linelisttree.add(newline);
									newnontrianglelinelisttree.add(newline);
								} else if (loadmodel.objects[j].faceindex[i].facevertexindex.length==3) {
									Material foundmat = null;
									for (int n=0;(n<loadmodel.materials.length)&&(foundmat==null);n++) {
										if (loadmodel.objects[j].faceindex[i].usemtl.equals(loadmodel.materials[n].materialname)) {
											foundmat = loadmodel.materials[n];
										}
									}
									if (foundmat==null) {
										foundmat = new Material(Color.WHITE,1.0f);
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
									Line newline1 = new Line(pos1.copy(), pos2.copy());
									Line newline2 = new Line(pos1.copy(), pos3.copy());
									Line newline3 = new Line(pos2.copy(), pos3.copy());
									newlinelisttree.add(newline1);
									newlinelisttree.add(newline2);
									newlinelisttree.add(newline3);
									this.linelisttree.add(newline1);
									this.linelisttree.add(newline2);
									this.linelisttree.add(newline3);
								} else {
									Position[] pos = new Position[loadmodel.objects[j].faceindex[i].facevertexindex.length];
									for (int m=0;m<loadmodel.objects[j].faceindex[i].facevertexindex.length;m++) {
										pos[m] = loadmodel.vertexlist[loadmodel.objects[j].faceindex[i].facevertexindex[m].vertexindex-1];
										if (m>0) {
											Line newline = new Line(pos[m].copy(), pos[m-1].copy());
											newlinelisttree.add(newline);
											this.linelisttree.add(newline);
											newnontrianglelinelisttree.add(newline);
										}
									}
									Line newline = new Line(pos[0].copy(), pos[loadmodel.objects[j].faceindex[i].facevertexindex.length-1].copy());
									newlinelisttree.add(newline);
									this.linelisttree.add(newline);
									newnontrianglelinelisttree.add(newline);
								}
							}
							for (int i=0;i<loadmodel.objects[j].lineindex.length;i++) {
								if (loadmodel.objects[j].lineindex[i].linevertexindex.length==1) {
									Position pos = loadmodel.vertexlist[loadmodel.objects[j].lineindex[i].linevertexindex[0]-1];
									Line newline = new Line(pos.copy(), pos.copy());
									newlinelisttree.add(newline);
									this.linelisttree.add(newline);
									newnontrianglelinelisttree.add(newline);
								} else {
									Position[] pos = new Position[loadmodel.objects[j].lineindex[i].linevertexindex.length];
									for (int m=0;m<loadmodel.objects[j].lineindex[i].linevertexindex.length;m++) {
										pos[m] = loadmodel.vertexlist[loadmodel.objects[j].lineindex[i].linevertexindex[m]-1];
										if (m>0) {
											Line newline = new Line(pos[m].copy(), pos[m-1].copy());
											newlinelisttree.add(newline);
											this.linelisttree.add(newline);
											newnontrianglelinelisttree.add(newline);
										}
									}
								}
							}
							newentity.trianglelist = newtrianglelistarray.toArray(new Triangle[newtrianglelistarray.size()]);
							newentity.linelist = newnontrianglelinelisttree.toArray(new Line[newnontrianglelinelisttree.size()]);
							Line[] newlinelist = newlinelisttree.toArray(new Line[newlinelisttree.size()]);
							if (newlinelist.length>0) {
								newentity.vertexlist = MathLib.generateVertexList(newlinelist);
								newentity.aabbboundaryvolume = MathLib.axisAlignedBoundingBox(newentity.vertexlist);
								newentity.sphereboundaryvolume = MathLib.pointCloudCircumSphere(newentity.vertexlist);
								newentitylist.add(newentity);
							}
						}
						this.entitylist = newentitylist.toArray(new Entity[newentitylist.size()]);
					}
				}
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
			if ((this.mouseoververtex!=null)&&(this.mouseoververtex.length>0)) {
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
    	if ((mouse1altdown)&&(this.polygonfillmode==1)) {
    		this.drawstartpos = null;
    		double mouserelativelocationx = this.mouselocationx-this.origindeltax;
    		double mouserelativelocationy = this.mouselocationy-this.origindeltay;
			if (this.snaplinemode) {
	    		mouserelativelocationx = UtilLib.snapToGrid(this.mouselocationx-this.origindeltax,this.gridstep);
	    		mouserelativelocationy = UtilLib.snapToGrid(this.mouselocationy-this.origindeltay,this.gridstep);
				if ((this.mouseoververtex!=null)&&(this.mouseoververtex.length>0)) {
					this.drawstartpos = this.mouseoververtex[this.mouseoververtex.length-1].copy(); 
				}
			}
			Position[] editposarray = {this.editpos};
			Position[] drawposarray = MathLib.translate(editposarray, this.camdirs[1], mouserelativelocationx);
			drawposarray = MathLib.translate(drawposarray, this.camdirs[2], mouserelativelocationy);
			if (this.drawstartpos==null) {
				this.drawstartpos = drawposarray[0].copy();
			}
			this.linelisttree.add(new Line(this.drawstartpos, drawposarray[0]));
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
	    	Triangle mousetriangle = null;
    		if (this.softwarerenderview!=null) {
    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.getWidth())&&(this.mouselocationy>=0)&&(this.mouselocationy<this.getHeight())) {
	    			mousetriangle = this.softwarerenderview.tbuffer[this.mouselocationy][this.mouselocationx];
    			}
    		} else if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
    			mousetriangle = this.mouseovertriangle[this.mouseovertriangle.length-1];
    		}
			if (mousetriangle!=null) {
				mousetriangle.mat = this.drawmat;
			}
    	}
	    int onmask1shiftdown = MouseEvent.BUTTON1_DOWN_MASK|MouseEvent.SHIFT_DOWN_MASK;
	    int offmask1shiftdown = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    boolean mouse1shiftdown = ((e.getModifiersEx() & (onmask1shiftdown | offmask1shiftdown)) == onmask1shiftdown);
	    if (mouse1shiftdown) {
	    	Triangle mousetriangle = null;
    		if (this.softwarerenderview!=null) {
    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.getWidth())&&(this.mouselocationy>=0)&&(this.mouselocationy<this.getHeight())) {
	    			mousetriangle = this.softwarerenderview.tbuffer[this.mouselocationy][this.mouselocationx];
    			}
    		} else if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
    			mousetriangle = this.mouseovertriangle[this.mouseovertriangle.length-1];
    		}
			if (mousetriangle!=null) {
				this.drawmat = mousetriangle.mat;
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
		    		mouserelativelocationx = UtilLib.snapToGrid(this.mouselocationx-this.origindeltax,this.gridstep);
		    		mouserelativelocationy = UtilLib.snapToGrid(this.mouselocationy-this.origindeltay,this.gridstep);
					if ((this.mouseoververtex!=null)&&(this.mouseoververtex.length>0)) {
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
	    int onmask1ctrlaltdown = MouseEvent.BUTTON1_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    int offmask1ctrlaltdown = 0;
	    boolean mouse1ctrlaltdown = ((e.getModifiersEx() & (onmask1ctrlaltdown | offmask1ctrlaltdown)) == onmask1ctrlaltdown);
	    if (mouse1ctrlaltdown) {
    		if ((this.mouseoverline!=null)&&(this.mouseoverline.length>0)) {
				this.linelisttree.removeAll(Arrays.asList(this.mouseoverline));
				(new EntityListUpdater()).start();
			}
	    }
	    int onmask2down = MouseEvent.BUTTON2_DOWN_MASK;
	    int offmask2down = MouseEvent.ALT_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
	    boolean mouse2down = ((e.getModifiersEx() & (onmask2down | offmask2down)) == onmask2down);
    	if (mouse2down) {
	    	Triangle mousetriangle = null;
    		if (this.softwarerenderview!=null) {
    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.getWidth())&&(this.mouselocationy>=0)&&(this.mouselocationy<this.getHeight())) {
	    			mousetriangle = this.softwarerenderview.tbuffer[this.mouselocationy][this.mouselocationx];
    			}
    		} else if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
    			mousetriangle = this.mouseovertriangle[this.mouseovertriangle.length-1];
    		}
			if (mousetriangle!=null) {
	        	int mousedeltax = this.mouselocationx - this.mouselastlocationx; 
	        	int mousedeltay = this.mouselocationy - this.mouselastlocationy;
	    		double movementstep = 1.0f;
	    		if (this.snaplinemode) {
	    			movementstep = this.gridstep;
	    		}
	        	double texdeltau = movementstep*mousedeltax*0.001f;
	        	double texdeltav = movementstep*mousedeltay*0.001f;
	        	mousetriangle.pos1.tex = mousetriangle.pos1.tex.copy();
	        	mousetriangle.pos2.tex = mousetriangle.pos2.tex.copy();
	        	mousetriangle.pos3.tex = mousetriangle.pos3.tex.copy();
	    		mousetriangle.pos1.tex.u -= texdeltau;
	    		mousetriangle.pos1.tex.v += texdeltav;
	    		mousetriangle.pos2.tex.u -= texdeltau;
	    		mousetriangle.pos2.tex.v += texdeltav;
	    		mousetriangle.pos3.tex.u -= texdeltau;
	    		mousetriangle.pos3.tex.v += texdeltav;
			}
    	}
	    int onmask2ctrldown = MouseEvent.BUTTON2_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
	    int offmask2ctrldown = MouseEvent.ALT_DOWN_MASK;
	    boolean mouse2ctrldown = ((e.getModifiersEx() & (onmask2ctrldown | offmask2ctrldown)) == onmask2ctrldown);
    	if (mouse2ctrldown) {
    		double movementstep = 1.0f;
    		if (this.snaplinemode) {
    			movementstep = this.gridstep;
    		}
        	int mousedeltax = this.mouselocationx - this.mouselastlocationx; 
        	int mousedeltay = this.mouselocationy - this.mouselastlocationy;
        	this.editpos = this.editpos.copy();
    		this.editpos.x -= mousedeltax*movementstep*this.camdirs[1].dx;
    		this.editpos.y -= mousedeltax*movementstep*this.camdirs[1].dy;
    		this.editpos.z -= mousedeltax*movementstep*this.camdirs[1].dz;
    		this.editpos.x -= mousedeltay*movementstep*this.camdirs[2].dx;
    		this.editpos.y -= mousedeltay*movementstep*this.camdirs[2].dy;
    		this.editpos.z -= mousedeltay*movementstep*this.camdirs[2].dz;
    	}
	    int onmask2ctrlaltdown = MouseEvent.BUTTON2_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    int offmask2ctrlaltdown = 0;
	    boolean mouse2ctrlaltdown = ((e.getModifiersEx() & (onmask2ctrlaltdown | offmask2ctrlaltdown)) == onmask2ctrlaltdown);
    	if (mouse2ctrlaltdown) {
    		double movementstep = 1.0f;
    		if (this.snaplinemode) {
    			movementstep = this.gridstep;
    		}
        	int mousedeltax = this.mouselocationx - this.mouselastlocationx; 
        	int mousedeltay = this.mouselocationy - this.mouselastlocationy;
        	this.camrot = this.camrot.copy();
        	this.camrot.z -= mousedeltax*(movementstep/((double)this.gridstep))*0.1f;
        	this.camrot.x -= mousedeltay*(movementstep/((double)this.gridstep))*0.1f;
        	updateCameraDirections();
    	}
	    int onmask3down = MouseEvent.BUTTON3_DOWN_MASK;
	    int offmask3down = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK|MouseEvent.SHIFT_DOWN_MASK;
	    boolean mouse3down = ((e.getModifiersEx() & (onmask3down | offmask3down)) == onmask3down);
    	if (mouse3down) {
    		//TODO <tbd>
    	}
	    int onmask3shiftdown = MouseEvent.BUTTON3_DOWN_MASK|MouseEvent.SHIFT_DOWN_MASK;
	    int offmask3shiftdown = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    boolean mouse3shiftdown = ((e.getModifiersEx() & (onmask3shiftdown | offmask3shiftdown)) == onmask3shiftdown);
	    if (mouse3shiftdown) {
	    	//TODO <tbd>
	    }
	    int onmask3altdown = MouseEvent.BUTTON3_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    int offmask3altdown = MouseEvent.CTRL_DOWN_MASK;
	    boolean mouse3altdown = ((e.getModifiersEx() & (onmask3altdown | offmask3altdown)) == onmask3altdown);
    	if (mouse3altdown) {
    		//TODO <tbd>
    	}
	    int onmask3ctrldown = MouseEvent.BUTTON3_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
	    int offmask3ctrldown = MouseEvent.ALT_DOWN_MASK;
	    boolean mouse3ctrldown = ((e.getModifiersEx() & (onmask3ctrldown | offmask3ctrldown)) == onmask3ctrldown);
    	if (mouse3ctrldown) {
    		//TODO move entity
    	}
	}
	@Override public void mouseWheelMoved(MouseWheelEvent e) {
	    int onmask = 0;
	    int offmask = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK|MouseEvent.SHIFT_DOWN_MASK;
	    boolean mousewheeldown = ((e.getModifiersEx() & (onmask | offmask)) == onmask);
	    if (mousewheeldown) {
	    	Triangle mousetriangle = null;
    		if (this.softwarerenderview!=null) {
    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.getWidth())&&(this.mouselocationy>=0)&&(this.mouselocationy<this.getHeight())) {
	    			mousetriangle = this.softwarerenderview.tbuffer[this.mouselocationy][this.mouselocationx];
    			}
    		} else if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
    			mousetriangle = this.mouseovertriangle[this.mouseovertriangle.length-1];
    		}
			if (mousetriangle!=null) {
	        	mousetriangle.pos1.tex = mousetriangle.pos1.tex.copy();
	        	mousetriangle.pos2.tex = mousetriangle.pos2.tex.copy();
	        	mousetriangle.pos3.tex = mousetriangle.pos3.tex.copy();
	    		mousetriangle.pos1.tex.u *= 1-(0.01f*e.getWheelRotation());
	    		mousetriangle.pos1.tex.v *= 1-(0.01f*e.getWheelRotation());
	    		mousetriangle.pos2.tex.u *= 1-(0.01f*e.getWheelRotation());
	    		mousetriangle.pos2.tex.v *= 1-(0.01f*e.getWheelRotation());
	    		mousetriangle.pos3.tex.u *= 1-(0.01f*e.getWheelRotation());
	    		mousetriangle.pos3.tex.v *= 1-(0.01f*e.getWheelRotation());
			}
	    }
	    int onmaskshiftdown = MouseEvent.SHIFT_DOWN_MASK;
	    int offmaskshiftdown = MouseEvent.ALT_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
	    boolean mousewheelshiftdown = ((e.getModifiersEx() & (onmaskshiftdown | offmaskshiftdown)) == onmaskshiftdown);
	    if (mousewheelshiftdown) {
	    	Triangle mousetriangle = null;
    		if (this.softwarerenderview!=null) {
    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.getWidth())&&(this.mouselocationy>=0)&&(this.mouselocationy<this.getHeight())) {
	    			mousetriangle = this.softwarerenderview.tbuffer[this.mouselocationy][this.mouselocationx];
    			}
    		} else if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
    			mousetriangle = this.mouseovertriangle[this.mouseovertriangle.length-1];
    		}
			if (mousetriangle!=null) {
	        	AffineTransform textr = new AffineTransform();
	        	textr.rotate(0.01f*e.getWheelRotation());
	        	Point2D pos1tex = new Point2D.Double(mousetriangle.pos1.tex.u,mousetriangle.pos1.tex.v); 
	        	Point2D pos2tex = new Point2D.Double(mousetriangle.pos2.tex.u,mousetriangle.pos2.tex.v); 
	        	Point2D pos3tex = new Point2D.Double(mousetriangle.pos3.tex.u,mousetriangle.pos3.tex.v); 
	        	textr.transform(pos1tex, pos1tex);
	        	textr.transform(pos2tex, pos2tex);
	        	textr.transform(pos3tex, pos3tex);
	        	mousetriangle.pos1.tex = new Coordinate(pos1tex.getX(),pos1tex.getY());
	        	mousetriangle.pos2.tex = new Coordinate(pos2tex.getX(),pos2tex.getY());
	        	mousetriangle.pos3.tex = new Coordinate(pos3tex.getX(),pos3tex.getY());
			}
	    }
	    int onmaskaltdown = MouseEvent.ALT_DOWN_MASK;
	    int offmaskaltdown = MouseEvent.SHIFT_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
	    boolean mousewheelaltdown = ((e.getModifiersEx() & (onmaskaltdown | offmaskaltdown)) == onmaskaltdown);
	    if (mousewheelaltdown) {
	    	Triangle mousetriangle = null;
    		if (this.softwarerenderview!=null) {
    			if ((this.mouselocationx>=0)&&(this.mouselocationx<this.getWidth())&&(this.mouselocationy>=0)&&(this.mouselocationy<this.getHeight())) {
	    			mousetriangle = this.softwarerenderview.tbuffer[this.mouselocationy][this.mouselocationx];
    			}
    		} else if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
    			mousetriangle = this.mouseovertriangle[this.mouseovertriangle.length-1];
    		}
			if (mousetriangle!=null) {
	        	AffineTransform textr = new AffineTransform();
	        	textr.scale(1+0.01f*e.getWheelRotation(),1.0f);
	        	Point2D pos1tex = new Point2D.Double(mousetriangle.pos1.tex.u,mousetriangle.pos1.tex.v); 
	        	Point2D pos2tex = new Point2D.Double(mousetriangle.pos2.tex.u,mousetriangle.pos2.tex.v); 
	        	Point2D pos3tex = new Point2D.Double(mousetriangle.pos3.tex.u,mousetriangle.pos3.tex.v); 
	        	textr.transform(pos1tex, pos1tex);
	        	textr.transform(pos2tex, pos2tex);
	        	textr.transform(pos3tex, pos3tex);
	        	mousetriangle.pos1.tex = new Coordinate(pos1tex.getX(),pos1tex.getY());
	        	mousetriangle.pos2.tex = new Coordinate(pos2tex.getX(),pos2tex.getY());
	        	mousetriangle.pos3.tex = new Coordinate(pos3tex.getX(),pos3tex.getY());
			}
	    }
	    int onmaskctrldown = MouseEvent.CTRL_DOWN_MASK;
	    int offmaskctrldown = 0;
	    boolean mousewheelctrldown = ((e.getModifiersEx() & (onmaskctrldown | offmaskctrldown)) == onmaskctrldown);
	    if (mousewheelctrldown) {
			double movementstep = 200.0f*e.getWheelRotation();
			if (this.snaplinemode) {
				movementstep *= this.gridstep;
			}
			this.editpos = this.editpos.copy();
			this.editpos.x -= movementstep*this.camdirs[0].dx;
			this.editpos.y -= movementstep*this.camdirs[0].dy;
			this.editpos.z -= movementstep*this.camdirs[0].dz;
	    }
	}
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void drop(DropTargetDropEvent dtde) {}

	private class EntityListUpdater extends Thread {
		private static boolean entitylistupdaterrunning = false;
		public void run() {
			if (!EntityListUpdater.entitylistupdaterrunning) {
				EntityListUpdater.entitylistupdaterrunning = true;
				ArrayList<Triangle> entitylisttrianglearray = new ArrayList<Triangle>();
				if (CADApp.this.entitylist!=null) {
					for (int i=0;i<CADApp.this.entitylist.length;i++) {
						if (CADApp.this.entitylist[i].trianglelist!=null) {
							entitylisttrianglearray.addAll(Arrays.asList(CADApp.this.entitylist[i].trianglelist));
						}
					}
				}
				Line[] copylinelist = CADApp.this.linelisttree.toArray(new Line[CADApp.this.linelisttree.size()]);
				Entity[] newentitylist = MathLib.generateEntityList(copylinelist);
				Material newmat = CADApp.this.drawmat;
				for (int j=0;j<newentitylist.length;j++) {
					for (int i=0;i<newentitylist[j].trianglelist.length;i++) {
						newentitylist[j].trianglelist[i].mat = newmat;
						newentitylist[j].trianglelist[i].norm = new Direction(0.0f,0.0f,0.0f);
						newentitylist[j].trianglelist[i].pos1.tex = new Coordinate(0.0f,0.0f);
						newentitylist[j].trianglelist[i].pos2.tex = new Coordinate(1.0f,0.0f);
						newentitylist[j].trianglelist[i].pos3.tex = new Coordinate(0.0f,1.0f);
						int searchindex = entitylisttrianglearray.indexOf(newentitylist[j].trianglelist[i]);
						if (searchindex>=0) {
							newentitylist[j].trianglelist[i] = entitylisttrianglearray.get(searchindex).copy(); 
						}
					}
				}
				CADApp.this.entitylist = newentitylist;
				EntityListUpdater.entitylistupdaterrunning = false;
			}
		}
	}

	private class HardwareRenderViewUpdater extends Thread {
		private static boolean renderupdaterrunning = false;
		public void run() {
			if (!HardwareRenderViewUpdater.renderupdaterrunning) {
				HardwareRenderViewUpdater.renderupdaterrunning = true;
				if (CADApp.this.polygonfillmode==1) {
					Line[] linelist = CADApp.this.linelisttree.toArray(new Line[CADApp.this.linelisttree.size()]);
					CADApp.this.hardwarerenderview = ModelLib.renderProjectedLineViewHardware(CADApp.this.campos, linelist, CADApp.this.getWidth(), CADApp.this.hfov, CADApp.this.getHeight(), CADApp.this.vfov, CADApp.this.cameramat, CADApp.this.mouselocationx, CADApp.this.mouselocationy);
					CADApp.this.mouseoverline = CADApp.this.hardwarerenderview.mouseoverline;
					CADApp.this.mouseoververtex = CADApp.this.hardwarerenderview.mouseoververtex;
				} else if (CADApp.this.polygonfillmode==2) { 
					CADApp.this.hardwarerenderview = ModelLib.renderProjectedPolygonViewHardware(CADApp.this.campos, CADApp.this.entitylist, CADApp.this.getWidth(), CADApp.this.hfov, CADApp.this.getHeight(), CADApp.this.vfov, CADApp.this.cameramat, false, CADApp.this.mouselocationx, CADApp.this.mouselocationy);
					CADApp.this.mouseovertriangle = CADApp.this.hardwarerenderview.mouseovertriangle;
				}
				HardwareRenderViewUpdater.renderupdaterrunning = false;
			}
		}
	}

	private class SoftwareRenderViewUpdater extends Thread {
		private static boolean renderupdaterrunning = false;
		public void run() {
			if (!SoftwareRenderViewUpdater.renderupdaterrunning) {
				SoftwareRenderViewUpdater.renderupdaterrunning = true;
				if (CADApp.this.polygonfillmode==3) {
					CADApp.this.softwarerenderview = ModelLib.renderProjectedPlaneViewSoftware(CADApp.this.campos, CADApp.this.entitylist, CADApp.this.getWidth(), CADApp.this.hfov, CADApp.this.getHeight(), CADApp.this.vfov, CADApp.this.cameramat, false, CADApp.this.mouselocationx, CADApp.this.mouselocationy);
					CADApp.this.mouseovertriangle = CADApp.this.softwarerenderview.mouseovertriangle;
				}
				SoftwareRenderViewUpdater.renderupdaterrunning = false;
			}
		}
	}
	
}
