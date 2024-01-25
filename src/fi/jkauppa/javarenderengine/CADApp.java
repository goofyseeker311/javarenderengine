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
	private Color drawcolor = Color.WHITE;
	private float[] drawcolorhsb = {0.0f, 1.0f, 1.0f};
	private float penciltransparency = 1.0f;
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
	private Position campos = new Position(0.0f,-this.editplanedistance,0.0f);
	private Rotation camrot = new Rotation(90.0f, 0.0f, 0.0f);
	private Matrix cameramat = MathLib.rotationMatrix(90.0f, 0.0f, 0.0f);
	private final Direction[] lookdirs = MathLib.projectedCameraDirections(MathLib.rotationMatrix(0.0f, 0.0f, 0.0f));
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
	
	public CADApp() {
		this.filechooser.addChoosableFileFilter(this.objfilefilter);
		this.filechooser.addChoosableFileFilter(this.stlfilefilter);
		this.filechooser.setFileFilter(this.objfilefilter);
		this.filechooser.setAcceptAllFileFilterUsed(false);
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
		(new SoftwareRenderViewUpdater()).start();
		(new HardwareRenderViewUpdater()).start();
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
			this.campos = new Position(0.0f,-this.editplanedistance,0.0f);
			this.camrot = new Rotation(90.0f, 0.0f, 0.0f);
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
			//TODO triangle mesh sub-divide at cursor
		} else if (e.getKeyCode()==KeyEvent.VK_ENTER) {
			this.polygonfillmode += 1;
			if (this.polygonfillmode>3) {
				this.polygonfillmode = 1;
			}
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD0) {
			(new EntityListUpdater()).start();
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
					    vertexlistarray.addAll(Arrays.asList(this.entitylist[j].vertexlist));
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
    	if (mouse1altdown) {
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
    	}
	    int onmask1shiftdown = MouseEvent.BUTTON1_DOWN_MASK|MouseEvent.SHIFT_DOWN_MASK;
	    int offmask1shiftdown = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    boolean mouse1shiftdown = ((e.getModifiersEx() & (onmask1shiftdown | offmask1shiftdown)) == onmask1shiftdown);
	    if (mouse1shiftdown) {
    		if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
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
	    int onmask2down = MouseEvent.BUTTON2_DOWN_MASK;
	    int offmask2down = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    boolean mouse2down = ((e.getModifiersEx() & (onmask2down | offmask2down)) == onmask2down);
    	if (mouse2down) {
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
        	this.camrot = this.camrot.copy();
        	this.camrot.z -= mousedeltax*(movementstep/((double)this.gridstep))*0.1f;
        	this.camrot.x -= mousedeltay*(movementstep/((double)this.gridstep))*0.1f;
        	updateCameraDirections();
    	}
	    int onmask3down = MouseEvent.BUTTON3_DOWN_MASK;
	    int offmask3down = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    boolean mouse3down = ((e.getModifiersEx() & (onmask3down | offmask3down)) == onmask3down);
    	if (mouse3down) {
    		if ((this.mouseovertriangle!=null)&&(this.mouseovertriangle.length>0)) {
				Material newmaterial = new Material();
				newmaterial.facecolor = this.drawcolor;
				newmaterial.transparency = this.penciltransparency;
				this.mouseovertriangle[this.mouseovertriangle.length-1].mat = newmaterial;
    		}
    	}
	    int onmask3altdown = MouseEvent.BUTTON3_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    int offmask3altdown = MouseEvent.CTRL_DOWN_MASK;
	    boolean mouse3altdown = ((e.getModifiersEx() & (onmask3altdown | offmask3altdown)) == onmask3altdown);
    	if (mouse3altdown) {
    		if ((this.mouseoverline!=null)&&(this.mouseoverline.length>0)) {
				this.linelisttree.removeAll(Arrays.asList(this.mouseoverline));
				(new EntityListUpdater()).start();
			}
    	}
	    int onmask3ctrldown = MouseEvent.BUTTON3_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
	    int offmask3ctrldown = MouseEvent.ALT_DOWN_MASK;
	    boolean mouse3ctrldown = ((e.getModifiersEx() & (onmask3ctrldown | offmask3ctrldown)) == onmask3ctrldown);
    	if (mouse3ctrldown) {
    		//TODO move entity
    	}
	}
	@Override public void mouseWheelMoved(MouseWheelEvent e) {
		double movementstep = 200.0f*e.getWheelRotation();
		if (this.snaplinemode) {
			movementstep *= this.gridstep;
		}
		this.editpos = this.editpos.copy();
		this.editpos.x -= movementstep*this.camdirs[0].dx;
		this.editpos.y -= movementstep*this.camdirs[0].dy;
		this.editpos.z -= movementstep*this.camdirs[0].dz;
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
				Material newmat = new Material();
				newmat.facecolor = CADApp.this.drawcolor;
				newmat.transparency = CADApp.this.penciltransparency;
				for (int j=0;j<newentitylist.length;j++) {
					for (int i=0;i<newentitylist[j].trianglelist.length;i++) {
						newentitylist[j].trianglelist[i].mat = newmat;
						newentitylist[j].trianglelist[i].norm = new Direction(0.0f,0.0f,0.0f);
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
					CADApp.this.hardwarerenderview = ModelLib.renderProjectedPolygonViewHardware(CADApp.this.campos, CADApp.this.entitylist, CADApp.this.getWidth(), CADApp.this.hfov, CADApp.this.getHeight(), CADApp.this.vfov, CADApp.this.cameramat, CADApp.this.mouselocationx, CADApp.this.mouselocationy);
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
					CADApp.this.softwarerenderview = ModelLib.renderProjectedTextureViewSoftware(CADApp.this.campos, CADApp.this.entitylist, CADApp.this.getWidth(), CADApp.this.hfov, CADApp.this.getHeight(), CADApp.this.vfov, CADApp.this.cameramat, CADApp.this.mouselocationx, CADApp.this.mouselocationy);
				}
				SoftwareRenderViewUpdater.renderupdaterrunning = false;
			}
		}
	}
	
}
