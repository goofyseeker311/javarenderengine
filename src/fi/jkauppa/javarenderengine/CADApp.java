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
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
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
import fi.jkauppa.javarenderengine.JavaRenderEngine.AppHandler;
import fi.jkauppa.javarenderengine.MathLib.Coordinate;
import fi.jkauppa.javarenderengine.MathLib.Direction;
import fi.jkauppa.javarenderengine.MathLib.Plane;
import fi.jkauppa.javarenderengine.MathLib.Polyangle;
import fi.jkauppa.javarenderengine.MathLib.Position;
import fi.jkauppa.javarenderengine.MathLib.Position2;
import fi.jkauppa.javarenderengine.MathLib.Sphere;
import fi.jkauppa.javarenderengine.MathLib.Triangle;
import fi.jkauppa.javarenderengine.ModelLib.Material;
import fi.jkauppa.javarenderengine.ModelLib.Model;
import fi.jkauppa.javarenderengine.ModelLib.ModelFaceIndex;
import fi.jkauppa.javarenderengine.ModelLib.ModelFaceVertexIndex;
import fi.jkauppa.javarenderengine.ModelLib.ModelLineIndex;
import fi.jkauppa.javarenderengine.ModelLib.ModelObject;

public class CADApp implements AppHandler {
	private GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment ();
	private GraphicsDevice gd = ge.getDefaultScreenDevice ();
	private GraphicsConfiguration gc = gd.getDefaultConfiguration ();
	private Direction lookdir = new Direction(0,0,-1);
	private TexturePaint bgpattern = null;
	private boolean draglinemode = false;
	private boolean snaplinemode = false;
	private int polygonfillmode = 1;
	private int selecteddragvertex = 0;
	private int mousestartlocationx = 0, mousestartlocationy = 0;  
	private int mouselocationx = 0, mouselocationy = 0;
	private int cameralocationx = 0, cameralocationy = 0;
	private int drawdepth = 0; 
	private int drawstartdepth = 0; 
	private final double drawdepthscale = 0.00035f;
	private int origindeltax = 0, origindeltay = 0; 
	private final int originlinewidth = 100;
	private final int originlineheight = 100;
	private final int vertexradius = 2;
	private final int axisstroke = 2;
	private final int vertexstroke = 2;
	private final int vertexfocus = 3;
	private final int sketchlinestroke = 2;
	private final int flatlinestroke = 1;
	private final int gridstep = 20;
	private BufferedImage bgpatternimage = gc.createCompatibleImage(gridstep, gridstep, Transparency.OPAQUE);
	private ArrayList<Position2> linelistarray = new ArrayList<Position2>();
	private Triangle[] trianglelist = null;
	private JFileChooser filechooser = new JFileChooser();
	private ImageFileFilters.OBJFileFilter objfilefilter = new ImageFileFilters.OBJFileFilter();
	private boolean leftkeydown = false;
	private boolean rightkeydown = false;
	private boolean upwardkeydown = false;
	private boolean downwardkeydown = false;
	private boolean forwardkeydown = false;
	private boolean backwardkeydown = false;
	
	public CADApp() {
		Graphics2D pgfx = this.bgpatternimage.createGraphics();
		pgfx.setColor(Color.WHITE);
		pgfx.fillRect(0, 0, this.bgpatternimage.getWidth(), this.bgpatternimage.getHeight());
		pgfx.setColor(Color.BLACK);
		pgfx.drawLine(0, 0, 0, gridstep-1);
		pgfx.drawLine(0, 0, gridstep-1, 0);
		pgfx.dispose();
		this.filechooser.addChoosableFileFilter(this.objfilefilter);
		this.filechooser.setFileFilter(this.objfilefilter);
		this.filechooser.setAcceptAllFileFilterUsed(false);
	}
	@Override
	public void renderWindow(Graphics2D g, int renderwidth, int renderheight, double deltatimesec, double deltatimefps) {
		this.origindeltax = (int)Math.floor(((double)renderwidth)/2.0f);
		this.origindeltay = (int)Math.floor(((double)renderheight)/2.0f);
		this.bgpattern = new TexturePaint(this.bgpatternimage,new Rectangle(this.origindeltax-this.cameralocationx, this.origindeltay-this.cameralocationy, gridstep, gridstep));
		g.setComposite(AlphaComposite.Src);
		g.setColor(null);
		g.setPaint(bgpattern);
		g.fillRect(0, 0, renderwidth*2, renderheight*2);
		g.setPaint(null);
		g.setColor(null);
		if (this.leftkeydown) {
			this.cameralocationx -= this.gridstep*deltatimesec*100.0f;
		} else if (this.rightkeydown) {
			this.cameralocationx += this.gridstep*deltatimesec*100.0f;
		}
		if (this.upwardkeydown) {
			this.cameralocationy -= this.gridstep*deltatimesec*100.0f;
		} else if (this.downwardkeydown) {
			this.cameralocationy += this.gridstep*deltatimesec*100.0f;
		}
		if (this.forwardkeydown) {
			if (this.snaplinemode) {
				this.drawdepth = snapToGrid(this.drawdepth-this.gridstep);
			} else {
				this.drawdepth -= 1;
			}
		} else if (this.backwardkeydown) {
			if (this.snaplinemode) {
				this.drawdepth = snapToGrid(this.drawdepth+this.gridstep);
			} else {
				this.drawdepth += 1;
			}
		}
		if (this.polygonfillmode==2) {
			Plane[] triangleplanes = MathLib.planeFromPoints(trianglelist);
			Direction[] trianglenormals = MathLib.planeNormals(triangleplanes);
			double[] triangleviewangles = MathLib.vectorAngle(lookdir, trianglenormals);
			for (int i=0;i<trianglelist.length;i++) {
				if ((trianglelist[i].pos1.z<=this.drawdepth)||(trianglelist[i].pos2.z<=this.drawdepth)||(trianglelist[i].pos3.z<=this.drawdepth)) {
					double pos1s = (this.drawdepth-trianglelist[i].pos1.z)*this.drawdepthscale+1;
					int pos1x = (int)Math.round((trianglelist[i].pos1.x-this.cameralocationx)/pos1s)+this.origindeltax;
					int pos1y = (int)Math.round((trianglelist[i].pos1.y-this.cameralocationy)/pos1s)+this.origindeltay;
					double pos2s = (this.drawdepth-trianglelist[i].pos2.z)*this.drawdepthscale+1;
					int pos2x = (int)Math.round((trianglelist[i].pos2.x-this.cameralocationx)/pos2s)+this.origindeltax;
					int pos2y = (int)Math.round((trianglelist[i].pos2.y-this.cameralocationy)/pos2s)+this.origindeltay;
					double pos3s = (this.drawdepth-trianglelist[i].pos3.z)*this.drawdepthscale+1;
					int pos3x = (int)Math.round((trianglelist[i].pos3.x-this.cameralocationx)/pos3s)+this.origindeltax;
					int pos3y = (int)Math.round((trianglelist[i].pos3.y-this.cameralocationy)/pos3s)+this.origindeltay;
					Polygon trianglepolygon = new Polygon();
					trianglepolygon.addPoint(pos1x, pos1y);
					trianglepolygon.addPoint(pos2x, pos2y);
					trianglepolygon.addPoint(pos3x, pos3y);
					double triangleviewangle = triangleviewangles[i];
					if (triangleviewangle>90.0f) {
						triangleviewangle = 180-triangleviewangle;
					}
					float shadingmultiplier = (90.0f-(((float)triangleviewangle))/1.5f)/90.0f;
					g.setColor(new Color(shadingmultiplier,shadingmultiplier,shadingmultiplier));
					g.fill(trianglepolygon);
					g.setColor(Color.BLACK);
					g.setStroke(new BasicStroke(this.flatlinestroke));
					g.drawLine(pos1x, pos1y, pos2x, pos2y);
					g.drawLine(pos1x, pos1y, pos3x, pos3y);
					g.drawLine(pos2x, pos2y, pos3x, pos3y);
				}
			}
		} else {
			Position2[] linelist = linelistarray.toArray(new Position2[linelistarray.size()]);
			for (int i=0;i<linelist.length;i++) {
				if ((linelist[i].pos1.z<=this.drawdepth)||(linelist[i].pos2.z<=this.drawdepth)) {
					double pos1s = (this.drawdepth-linelist[i].pos1.z)*this.drawdepthscale+1;
					int pos1x = (int)Math.round((linelist[i].pos1.x-this.cameralocationx)/pos1s)+this.origindeltax;
					int pos1y = (int)Math.round((linelist[i].pos1.y-this.cameralocationy)/pos1s)+this.origindeltay;
					double pos2s = (this.drawdepth-linelist[i].pos2.z)*this.drawdepthscale+1;
					int pos2x = (int)Math.round((linelist[i].pos2.x-this.cameralocationx)/pos2s)+this.origindeltax;
					int pos2y = (int)Math.round((linelist[i].pos2.y-this.cameralocationy)/pos2s)+this.origindeltay;
					g.setColor(Color.BLACK);
					if (Math.abs(linelist[i].pos1.z-this.drawdepth)<0.4f){g.setStroke(new BasicStroke(this.vertexstroke+this.vertexfocus));}else{g.setStroke(new BasicStroke(this.vertexstroke));}
					g.drawOval(pos1x-this.vertexradius, pos1y-this.vertexradius, this.vertexradius*2, this.vertexradius*2);
					if (Math.abs(linelist[i].pos2.z-this.drawdepth)<0.4f){g.setStroke(new BasicStroke(this.vertexstroke+this.vertexfocus));}else{g.setStroke(new BasicStroke(this.vertexstroke));}
					g.drawOval(pos2x-this.vertexradius, pos2y-this.vertexradius, this.vertexradius*2, this.vertexradius*2);
					g.setColor(Color.BLUE);
					g.setStroke(new BasicStroke(this.sketchlinestroke));
					g.drawLine(pos1x, pos1y, pos2x, pos2y);
				}
			}
		}
		int axislocationx = this.origindeltax-this.cameralocationx;
		int axislocationy = this.origindeltay-this.cameralocationy;
		g.setStroke(new BasicStroke(this.axisstroke));
		g.setColor(Color.RED);
		g.drawLine(axislocationx, axislocationy, axislocationx+this.originlinewidth, axislocationy);
		g.setColor(Color.GREEN);
		g.drawLine(axislocationx, axislocationy, axislocationx, axislocationy+this.originlineheight);
		g.setColor(Color.BLACK);
		g.fillOval(axislocationx-this.vertexradius, axislocationy-this.vertexradius, this.vertexradius*2, this.vertexradius*2);
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
    			if ((ssint[0][i])&&(Math.abs(vsphere2[i].z-this.drawdepth)<0.4f)) {
    				k = i;
    			}
    		}
		}
		return k;
	}
	private void updateTriangleList() {
		Triangle[] unsortedtrianglelist = MathLib.generateTriangleList(linelistarray.toArray(new Position2[linelistarray.size()]));
		TreeSet<Triangle> triangletree = new TreeSet<Triangle>(Arrays.asList(unsortedtrianglelist));
		this.trianglelist = triangletree.toArray(new Triangle[triangletree.size()]);
	}
	
	@Override public void actionPerformed(ActionEvent e) {}
	@Override public void keyTyped(KeyEvent e) {}
	@Override public void keyReleased(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_SHIFT) {
			this.snaplinemode = false;
		} else if (e.getKeyCode()==KeyEvent.VK_D) {
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
			this.linelistarray.clear();
			this.drawdepth = 0;
			this.cameralocationx = 0;
			this.cameralocationy = 0;
			updateTriangleList();
		} else if (e.getKeyCode()==KeyEvent.VK_ENTER) {
			this.polygonfillmode += 1;
			if (this.polygonfillmode>2) {
				this.polygonfillmode = 1;
			}
		} else if (e.getKeyCode()==KeyEvent.VK_SHIFT) {
			this.snaplinemode = true;
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
		} else if (e.getKeyCode()==KeyEvent.VK_F2) {
			this.filechooser.setDialogTitle("Save File");
			this.filechooser.setApproveButtonText("Save");
			if (this.filechooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				File savefile = this.filechooser.getSelectedFile();
				Model savemodel = new Model(savefile.getPath());
				String saveobjfile = savefile.getPath();
				String savemtlfile = savefile.getName();
				if (savemtlfile.toLowerCase().endsWith(".obj")) {
					savemtlfile = savemtlfile.substring(0, savemtlfile.length()-4).concat(".mtl");
				} else {
					savemtlfile = savemtlfile.concat(".mtl");
				}
				savemodel.mtllib = savemtlfile;
				savemodel.texturecoords = new Coordinate[1];
				savemodel.texturecoords[0] = new Coordinate(0, 0);
				savemodel.facenormals = new Direction[1];
				savemodel.facenormals[0] = new Direction(0, 0, 0);
				savemodel.materials = new Material[1];
				savemodel.materials[0] = new Material("JREMAT");
				savemodel.materials[0].facecolor = Color.YELLOW;
				savemodel.objects = new ModelObject[1];
				savemodel.objects[0] = new ModelObject("JREOBJ");
				savemodel.objects[0].usemtl = savemodel.materials[0].materialname;
				savemodel.vertexlist = MathLib.generateVertexList(this.linelistarray.toArray(new Position2[this.linelistarray.size()]));
				Polyangle[] polygonlist = MathLib.generatePolygonList(this.linelistarray.toArray(new Position2[this.linelistarray.size()]));
				ArrayList<ModelFaceIndex> faceindexarray = new ArrayList<ModelFaceIndex>();
				ArrayList<ModelLineIndex> lineindexarray = new ArrayList<ModelLineIndex>();
				for (int j=0;j<polygonlist.length;j++) {
					if (polygonlist[j].poslist.length>=3) {
						ModelFaceVertexIndex[] trianglevertex = new ModelFaceVertexIndex[polygonlist[j].poslist.length];
						for (int i=0;i<polygonlist[j].poslist.length;i++) {
							int vertexindex = Arrays.binarySearch(savemodel.vertexlist, polygonlist[j].poslist[i])+1;
							trianglevertex[i] = new ModelFaceVertexIndex(vertexindex,1,1);
						}
						faceindexarray.add(new ModelFaceIndex(trianglevertex));
					} else {
						int[] linevertex = new int[polygonlist[j].poslist.length];
						for (int i=0;i<polygonlist[j].poslist.length;i++) {
							int vertexindex = Arrays.binarySearch(savemodel.vertexlist, polygonlist[j].poslist[i])+1;
							linevertex[i] = vertexindex;
						}
						lineindexarray.add(new ModelLineIndex(linevertex));
					}
				}
				savemodel.objects[0].faceindex = faceindexarray.toArray(new ModelFaceIndex[faceindexarray.size()]);
				savemodel.objects[0].lineindex = lineindexarray.toArray(new ModelLineIndex[lineindexarray.size()]);
				ModelLib.saveWaveFrontOBJFile(saveobjfile, savemodel);
			}
		} else if (e.getKeyCode()==KeyEvent.VK_F3) {
			this.filechooser.setDialogTitle("Load File");
			this.filechooser.setApproveButtonText("Load");
			if (this.filechooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				TreeSet<Position2> uniquelinetree = new TreeSet<Position2>();
				File loadfile = this.filechooser.getSelectedFile();
				Model loadmodel = ModelLib.loadWaveFrontOBJFile(loadfile.getPath(), false);
				for (int k=0;k<loadmodel.objects.length;k++) {
					for (int j=0;j<loadmodel.objects[k].faceindex.length;j++) {
						Position[] loadvertex = new Position[loadmodel.objects[k].faceindex[j].facevertexindex.length];
						for (int i=0;i<loadmodel.objects[k].faceindex[j].facevertexindex.length;i++) {
							loadvertex[i] = loadmodel.vertexlist[loadmodel.objects[k].faceindex[j].facevertexindex[i].vertexindex-1];
							if (i>0) {
								uniquelinetree.add(new Position2(loadvertex[i-1].copy(),loadvertex[i].copy()));
							}
						}
						if (loadmodel.objects[k].faceindex[j].facevertexindex.length>2) {
							uniquelinetree.add(new Position2(loadvertex[loadmodel.objects[k].faceindex[j].facevertexindex.length-1].copy(),loadvertex[0].copy()));
						} else if (loadmodel.objects[k].faceindex[j].facevertexindex.length==1) {
							uniquelinetree.add(new Position2(loadvertex[0].copy(),loadvertex[0].copy()));
						}
					}
					for (int j=0;j<loadmodel.objects[k].lineindex.length;j++) {
						Position[] loadvertex = new Position[loadmodel.objects[k].lineindex[j].linevertexindex.length];
						for (int i=0;i<loadmodel.objects[k].lineindex[j].linevertexindex.length;i++) {
							loadvertex[i] = loadmodel.vertexlist[loadmodel.objects[k].lineindex[j].linevertexindex[i]-1];
							if (i>0) {
								uniquelinetree.add(new Position2(loadvertex[i-1].copy(),loadvertex[i].copy()));
							}
						}
						if (loadmodel.objects[k].lineindex[j].linevertexindex.length==1) {
							uniquelinetree.add(new Position2(loadvertex[0].copy(),loadvertex[0].copy()));
						}
					}
				}
				linelistarray.addAll(uniquelinetree);
				updateTriangleList();
			}
		}
	}
	
	@Override public void mouseMoved(MouseEvent e) {this.mouselocationx=e.getX();this.mouselocationy=e.getY();}
	@Override public void mousePressed(MouseEvent e) {
		this.mouselocationx=e.getX();this.mouselocationy=e.getY();
		this.mousestartlocationx=this.mouselocationx;this.mousestartlocationy=this.mouselocationy;
		this.drawstartdepth = this.drawdepth;
	    int onmask1 = MouseEvent.BUTTON1_DOWN_MASK;
	    int offmask1 = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    boolean mouse1down = ((e.getModifiersEx() & (onmask1 | offmask1)) == onmask1);
    	if (mouse1down) {
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
			this.linelistarray.add(new Position2(new Position(drawstartlocationx, drawstartlocationy, this.drawstartdepth), new Position(drawlocationx, drawlocationy, this.drawdepth)));
			this.draglinemode = true;
			this.selecteddragvertex = (this.linelistarray.size()-1)*2+1;
			updateTriangleList();
    	}
	    int onmask3altdown = MouseEvent.BUTTON3_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    int offmask3altdown = MouseEvent.CTRL_DOWN_MASK;
	    boolean mouse3altdown = ((e.getModifiersEx() & (onmask3altdown | offmask3altdown)) == onmask3altdown);
    	if (mouse3altdown) {
    		int vertexatmouse = getVertexAtMouse();
			if (vertexatmouse!=-1) {
				int linenum = Math.floorDiv(vertexatmouse,2);
				this.linelistarray.remove(linenum);
				updateTriangleList();
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
	}
	
	public void mouseDragged(MouseEvent e) {
		this.mouselocationx=e.getX();this.mouselocationy=e.getY();
    	
	    int onmask1 = MouseEvent.BUTTON1_DOWN_MASK;
	    int offmask1 = MouseEvent.CTRL_DOWN_MASK;
	    boolean mouse1down = ((e.getModifiersEx() & (onmask1 | offmask1)) == onmask1);
    	if (mouse1down) {
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
				updateTriangleList();
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

	private class ImageFileFilters  {
		public static class OBJFileFilter extends FileFilter {
			@Override public boolean accept(File f) {return (f.isDirectory())||(f.getName().endsWith(".obj"));}
			@Override public String getDescription() {return "OBJ Model file";}
		}
	}
	
}
