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
import java.util.TreeSet;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import fi.jkauppa.javarenderengine.JavaRenderEngine.AppHandler;
import fi.jkauppa.javarenderengine.MathLib.Coordinate;
import fi.jkauppa.javarenderengine.MathLib.Direction;
import fi.jkauppa.javarenderengine.MathLib.Polyangle;
import fi.jkauppa.javarenderengine.MathLib.Position;
import fi.jkauppa.javarenderengine.MathLib.Position2;
import fi.jkauppa.javarenderengine.MathLib.Sphere;
import fi.jkauppa.javarenderengine.ModelLib.Material;
import fi.jkauppa.javarenderengine.ModelLib.Model;
import fi.jkauppa.javarenderengine.ModelLib.ModelFaceIndex;
import fi.jkauppa.javarenderengine.ModelLib.ModelFaceVertexIndex;
import fi.jkauppa.javarenderengine.ModelLib.ModelObject;

public class CADApp implements AppHandler {
	private GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment ();
	private GraphicsDevice gd = ge.getDefaultScreenDevice ();
	private GraphicsConfiguration gc = gd.getDefaultConfiguration ();
	private int lastrenderwidth = 0;
	private int lastrenderheight = 0;
	private TexturePaint bgpattern = null;
	private boolean draglinemode = false;
	private boolean snaplinemode = false;
	private int polygonfillmode = 1;
	private int selecteddragvertex = -1;
	private int mousestartlocationx = -1, mousestartlocationy = -1;  
	private int mouselastlocationx = -1, mouselastlocationy = -1;  
	private int mouselocationx = -1, mouselocationy = -1;
	private int drawdepth = 0; 
	private int drawstartdepth = 0; 
	private final double drawdepthscale = 0.001f;
	private int origindeltax = 0, origindeltay = 0; 
	private final int originlinewidth = 100;
	private final int originlineheight = 100;
	private final int vertexradius = 5;
	private final int vertexstroke = 2;
	private final int linestroke = 5;
	private final int gridstep = 20;
	private BufferedImage bgpatternimage = gc.createCompatibleImage(gridstep, gridstep, Transparency.OPAQUE);
	private ArrayList<Position2> linelist = new ArrayList<Position2>(); 
	private JFileChooser filechooser = new JFileChooser();
	private ImageFileFilters.OBJFileFilter objfilefilter = new ImageFileFilters.OBJFileFilter();
	
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
		if ((this.lastrenderwidth!=renderwidth)||(this.lastrenderheight!=renderheight)) {
			this.lastrenderwidth = renderwidth;
			this.lastrenderheight = renderheight;
			this.bgpattern = new TexturePaint(this.bgpatternimage,new Rectangle(this.origindeltax, this.origindeltay, gridstep, gridstep));
		}
		g.setComposite(AlphaComposite.Src);
		g.setColor(null);
		g.setPaint(bgpattern);
		g.fillRect(0, 0, renderwidth*2, renderheight*2);
		g.setPaint(null);
		g.setStroke(new BasicStroke(this.vertexstroke));
		g.setColor(Color.RED);
		g.drawLine(this.origindeltax, this.origindeltay, this.origindeltax+this.originlinewidth, this.origindeltay);
		g.setColor(Color.GREEN);
		g.drawLine(this.origindeltax, this.origindeltay, this.origindeltax, this.origindeltay+this.originlineheight);
		g.setColor(Color.BLACK);
		g.fillOval(this.origindeltax-this.vertexradius, this.origindeltay-this.vertexradius, this.vertexradius*2, this.vertexradius*2);
		g.setColor(null);
		for (int i=0;i<linelist.size();i++) {
			if ((linelist.get(i).pos1.z>=this.drawdepth)||(linelist.get(i).pos2.z>=this.drawdepth)) {
				double pos1s = (linelist.get(i).pos1.z-this.drawdepth)*this.drawdepthscale+1;
				int pos1x = (int)Math.round(linelist.get(i).pos1.x/pos1s)+this.origindeltax;
				int pos1y = (int)Math.round(linelist.get(i).pos1.y/pos1s)+this.origindeltay;
				double pos2s = (linelist.get(i).pos2.z-this.drawdepth)*this.drawdepthscale+1;
				int pos2x = (int)Math.round(linelist.get(i).pos2.x/pos2s)+this.origindeltax;
				int pos2y = (int)Math.round(linelist.get(i).pos2.y/pos2s)+this.origindeltay;
				g.setColor(Color.BLACK);
				if (linelist.get(i).pos1.z==this.drawdepth){g.setStroke(new BasicStroke(this.vertexstroke+1));}else{g.setStroke(new BasicStroke(this.vertexstroke));}
				g.drawOval(pos1x-this.vertexradius, pos1y-this.vertexradius, this.vertexradius*2, this.vertexradius*2);
				if (linelist.get(i).pos2.z==this.drawdepth){g.setStroke(new BasicStroke(this.vertexstroke+1));}else{g.setStroke(new BasicStroke(this.vertexstroke));}
				g.drawOval(pos2x-this.vertexradius, pos2y-this.vertexradius, this.vertexradius*2, this.vertexradius*2);
				g.setColor(Color.BLUE);
				g.setStroke(new BasicStroke(this.linestroke));
				g.drawLine(pos1x, pos1y, pos2x, pos2y);
			}
		}
	}

	private int snapToGrid(int coordinate) {
		return this.gridstep*Math.floorDiv(coordinate, this.gridstep);
	}
	
	private int getVertexAtMouse() {
		int k = -1;
		Sphere[] vsphere1 = new Sphere[1]; vsphere1[0] = new Sphere(this.mouselocationx-this.origindeltax, this.mouselocationy-this.origindeltay, this.drawdepth, 0); 
		Sphere[] vsphere2 = new Sphere[2*this.linelist.size()];
		for (int i=0;i<linelist.size();i++) {
			vsphere2[2*i] = new Sphere(linelist.get(i).pos1.x, linelist.get(i).pos1.y, linelist.get(i).pos1.z, this.vertexradius);
			vsphere2[2*i+1] = new Sphere(linelist.get(i).pos2.x, linelist.get(i).pos2.y, linelist.get(i).pos2.z, this.vertexradius);
		}
		boolean[][] ssint = MathLib.sphereSphereIntersection(vsphere1, vsphere2);
		if (ssint!=null) {
    		for (int i=0;i<ssint[0].length;i++) {
    			if ((ssint[0][i])&&(vsphere2[i].z==this.drawdepth)) {
    				k = i;
    			}
    		}
		}
		return k;
	}
	
	private Position[] generateVertexList() {
		TreeSet<Position> uniquevertexlist = new TreeSet<Position>();
		for (int i=0;i<linelist.size();i++) {
			uniquevertexlist.add(linelist.get(i).pos1);
			uniquevertexlist.add(linelist.get(i).pos2);
		}
		return uniquevertexlist.toArray(new Position[uniquevertexlist.size()]);
	}
	private Polyangle[] generatePolygonList() {
		Position[] vertexlist = generateVertexList();
		ArrayList<Polyangle> uniquepolygonlist = new ArrayList<Polyangle>();
		return uniquepolygonlist.toArray(new Polyangle[uniquepolygonlist.size()]);
	}
	
	@Override public void actionPerformed(ActionEvent e) {}
	@Override public void keyTyped(KeyEvent e) {}
	@Override public void keyReleased(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_SHIFT) {
			this.snaplinemode = false;
		}
	}
	@Override public void keyPressed(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_BACK_SPACE) {
			this.linelist.clear();
		} else if (e.getKeyCode()==KeyEvent.VK_ENTER) {
			this.polygonfillmode += 1;
			if (this.polygonfillmode>3) {
				this.polygonfillmode = 1;
			}
		} else if (e.getKeyCode()==KeyEvent.VK_SHIFT) {
			this.snaplinemode = true;
		} else if (e.getKeyCode()==KeyEvent.VK_ADD) {
			if (this.snaplinemode) {
				this.drawdepth = snapToGrid(this.drawdepth+this.gridstep);
			} else {
				this.drawdepth += 1;
			}
		} else if (e.getKeyCode()==KeyEvent.VK_SUBTRACT) {
			if (this.snaplinemode) {
				this.drawdepth = snapToGrid(this.drawdepth-this.gridstep);
			} else {
				this.drawdepth -= 1;
			}
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
				savemodel.objects[0].faceindex = new ModelFaceIndex[this.linelist.size()];
				savemodel.vertexlist = generateVertexList();
				System.out.println("vertexlist.length="+savemodel.vertexlist.length);
				Polyangle[] polygonlist = generatePolygonList();
				System.out.println("polygonlist.length="+polygonlist.length);
				/*
				savemodel.vertexlist = new Position[this.linelist.size()*2];
				for (int i=0;i<this.linelist.size();i++) {
					savemodel.vertexlist[i*2] = new Position(linelist.get(i).pos1.x, linelist.get(i).pos1.y, linelist.get(i).pos1.z);
					savemodel.vertexlist[i*2+1] = new Position(linelist.get(i).pos2.x, linelist.get(i).pos2.y, linelist.get(i).pos2.z);
					ModelFaceVertexIndex[] linefacevertex = new ModelFaceVertexIndex[2];
					linefacevertex[0] = new ModelFaceVertexIndex(i*2+1, 1, 1);
					linefacevertex[1] = new ModelFaceVertexIndex(i*2+2, 1, 1);
					savemodel.objects[0].faceindex[i] = new ModelFaceIndex(linefacevertex);
				}
				*/
				ModelLib.saveWaveFrontOBJFile(saveobjfile, savemodel);
			}
		} else if (e.getKeyCode()==KeyEvent.VK_F3) {
			this.filechooser.setDialogTitle("Load File");
			this.filechooser.setApproveButtonText("Load");
			if (this.filechooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				File loadfile = this.filechooser.getSelectedFile();
				Model loadmodel = ModelLib.loadWaveFrontOBJFile(loadfile.getPath(), false);
				for (int k=0;k<loadmodel.objects.length;k++) {
					for (int j=0;j<loadmodel.objects[k].faceindex.length;j++) {
						Position[] loadvertex = new Position[loadmodel.objects[k].faceindex[j].facevertexindex.length];
						for (int i=0;i<loadmodel.objects[k].faceindex[j].facevertexindex.length;i++) {
							loadvertex[i] = loadmodel.vertexlist[loadmodel.objects[k].faceindex[j].facevertexindex[i].vertexindex-1];
							if (i>0) {
								this.linelist.add(new Position2(loadvertex[i-1],loadvertex[i]));
							}
						}
					}
				}
			}
		}
	}
	
	@Override public void mouseMoved(MouseEvent e) {this.mouselocationx=e.getX();this.mouselocationy=e.getY();}
	@Override public void mousePressed(MouseEvent e) {
		this.mouselocationx=e.getX();this.mouselocationy=e.getY();
		this.mousestartlocationx=this.mouselocationx;this.mousestartlocationy=this.mouselocationy;
		this.drawstartdepth = this.drawdepth;
	    int onmask1 = MouseEvent.BUTTON1_DOWN_MASK;
	    int offmask1 = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK; //MouseEvent.SHIFT_DOWN_MASK|
	    boolean mouse1down = ((e.getModifiersEx() & (onmask1 | offmask1)) == onmask1);
    	if (mouse1down) {
    		int vertexatmouse = getVertexAtMouse();
			if (vertexatmouse!=-1) {
				this.draglinemode = true;
				this.selecteddragvertex = vertexatmouse;
			}
    	}
	    int onmask1alt = MouseEvent.BUTTON1_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    int offmask1alt = MouseEvent.CTRL_DOWN_MASK; //MouseEvent.SHIFT_DOWN_MASK|
	    boolean mouse1altdown = ((e.getModifiersEx() & (onmask1alt | offmask1alt)) == onmask1alt);
    	if (mouse1altdown) {
			int drawstartlocationx = this.mousestartlocationx-this.origindeltax;
			int drawstartlocationy = this.mousestartlocationy-this.origindeltay;
			int drawlocationx = this.mouselocationx-this.origindeltax;
			int drawlocationy = this.mouselocationy-this.origindeltay;
			if (this.snaplinemode) {
				drawstartlocationx = snapToGrid(drawstartlocationx);
				drawstartlocationy = snapToGrid(drawstartlocationy);
				drawlocationx = snapToGrid(drawlocationx);
				drawlocationy = snapToGrid(drawlocationy);
			}
			this.linelist.add(new Position2(new Position(drawstartlocationx, drawstartlocationy, this.drawstartdepth), new Position(drawlocationx, drawlocationy, this.drawdepth)));
			this.draglinemode = true;
			this.selecteddragvertex = (this.linelist.size()-1)*2+1;
    	}
	    int onmask3altdown = MouseEvent.BUTTON3_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    int offmask3altdown = MouseEvent.CTRL_DOWN_MASK; //MouseEvent.SHIFT_DOWN_MASK|
	    boolean mouse3altdown = ((e.getModifiersEx() & (onmask3altdown | offmask3altdown)) == onmask3altdown);
    	if (mouse3altdown) {
    		int vertexatmouse = getVertexAtMouse();
			if (vertexatmouse!=-1) {
				int linenum = Math.floorDiv(vertexatmouse,2);
				this.linelist.remove(linenum);
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
		this.mouselastlocationx=this.mouselocationx;this.mouselastlocationy=this.mouselocationy;
		this.mouselocationx=e.getX();this.mouselocationy=e.getY();
    	int mousedeltax = this.mouselocationx - this.mouselastlocationx; 
    	int mousedeltay = this.mouselocationy - this.mouselastlocationy;
    	
	    int onmask1 = MouseEvent.BUTTON1_DOWN_MASK;
	    int offmask1 = MouseEvent.CTRL_DOWN_MASK; //MouseEvent.SHIFT_DOWN_MASK| //|MouseEvent.ALT_DOWN_MASK
	    int onmask3 = MouseEvent.BUTTON3_DOWN_MASK;
	    int offmask3 = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK; //MouseEvent.SHIFT_DOWN_MASK|
	    boolean mouse1down = ((e.getModifiersEx() & (onmask1 | offmask1)) == onmask1);
	    boolean mouse3down = ((e.getModifiersEx() & (onmask3 | offmask3)) == onmask3);
	    if (mouse1down||mouse3down) {
	    	if (mouse1down) {
	    		if (this.draglinemode) {
    				int linenum = Math.floorDiv(this.selecteddragvertex,2);
    				boolean firstvertex = Math.floorMod(this.selecteddragvertex,2)==0;
    				int drawlocationx = this.mouselocationx-this.origindeltax;
    				int drawlocationy = this.mouselocationy-this.origindeltay;
    				if (this.snaplinemode) {
    					drawlocationx = snapToGrid(drawlocationx);
    					drawlocationy = snapToGrid(drawlocationy);
    				}
    				if (firstvertex) {
    					this.linelist.get(linenum).pos1.x = drawlocationx;
    					this.linelist.get(linenum).pos1.y = drawlocationy;
    					this.linelist.get(linenum).pos1.z = this.drawdepth;
	    			} else {
    					this.linelist.get(linenum).pos2.x = drawlocationx;
    					this.linelist.get(linenum).pos2.y = drawlocationy;
    					this.linelist.get(linenum).pos2.z = this.drawdepth;
	    			}
	    		}
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
