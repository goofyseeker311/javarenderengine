package fi.jkauppa.javarenderengine;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
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
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import fi.jkauppa.javarenderengine.JavaRenderEngine.AppHandler;
import fi.jkauppa.javarenderengine.MathLib.Position;
import fi.jkauppa.javarenderengine.MathLib.Position2;
import fi.jkauppa.javarenderengine.MathLib.Sphere;
import fi.jkauppa.javarenderengine.ModelLib.Model;

public class CADApp implements AppHandler {
	private GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment ();
	private GraphicsDevice gd = ge.getDefaultScreenDevice ();
	private GraphicsConfiguration gc = gd.getDefaultConfiguration ();
	private TexturePaint bgpattern = null;
	private boolean drawlinemode = false;
	private boolean draglinemode = false;
	private boolean snaplinemode = false;
	private int selecteddragvertex = -1;
	private int mousestartlocationx = -1, mousestartlocationy = -1;  
	private int mouselastlocationx = -1, mouselastlocationy = -1;  
	private int mouselocationx = -1, mouselocationy = -1;
	private int drawheight = 0; 
	private final int vertexradius = 5;
	private final int vertexstroke = 2;
	private final int linestroke = 5;
	private final int gridstep = 16;
	private final int gridsteph = gridstep/2;
	private final int griddelta = gridsteph;
	private ArrayList<Position2> linelist = new ArrayList<Position2>(); 
	private JFileChooser filechooser = new JFileChooser();
	private ImageFileFilters.OBJFileFilter objfilefilter = new ImageFileFilters.OBJFileFilter();
	
	public CADApp() {
		BufferedImage bgpatternimage = gc.createCompatibleImage(64, 64, Transparency.OPAQUE);
		Graphics2D pgfx = bgpatternimage.createGraphics();
		pgfx.setColor(Color.WHITE);
		pgfx.fillRect(0, 0, bgpatternimage.getWidth(), bgpatternimage.getHeight());
		pgfx.setColor(Color.BLACK);
		pgfx.drawLine(gridsteph-1, 0, gridsteph-1, gridstep-1);
		pgfx.drawLine(0, gridsteph-1, gridstep-1, gridsteph-1);
		pgfx.dispose();
		this.bgpattern = new TexturePaint(bgpatternimage,new Rectangle(0, 0, 64, 64));
		this.filechooser.addChoosableFileFilter(this.objfilefilter);
		this.filechooser.setFileFilter(this.objfilefilter);
		this.filechooser.setAcceptAllFileFilterUsed(false);
	}
	@Override
	public void renderWindow(Graphics2D g, int renderwidth, int renderheight, double deltatimesec, double deltatimefps) {
		g.setComposite(AlphaComposite.Src);
		g.setColor(null);
		g.setPaint(bgpattern);
		g.fillRect(0, 0, renderwidth, renderheight);
		g.setPaint(null);
		for (int i=0;i<linelist.size();i++) {
			g.setColor(Color.BLACK);
			g.setStroke(new BasicStroke(this.vertexstroke));
			g.drawOval((int)Math.round(linelist.get(i).pos1.x-this.vertexradius), (int)Math.round(linelist.get(i).pos1.y-this.vertexradius), this.vertexradius*2, this.vertexradius*2);
			g.drawOval((int)Math.round(linelist.get(i).pos2.x-this.vertexradius), (int)Math.round(linelist.get(i).pos2.y-this.vertexradius), this.vertexradius*2, this.vertexradius*2);
			g.setColor(Color.BLUE);
			g.setStroke(new BasicStroke(this.linestroke));
			g.drawLine((int)Math.round(linelist.get(i).pos1.x), (int)Math.round(linelist.get(i).pos1.y), (int)Math.round(linelist.get(i).pos2.x), (int)Math.round(linelist.get(i).pos2.y));
		}
		if (this.drawlinemode) {
			int drawstartlocationx = this.mousestartlocationx;
			int drawstartlocationy = this.mousestartlocationy;
			int drawlocationx = this.mouselocationx;
			int drawlocationy = this.mouselocationy;
			if (this.snaplinemode) {
				drawstartlocationx = snapToGrid(drawstartlocationx);
				drawstartlocationy = snapToGrid(drawstartlocationy);
				drawlocationx = snapToGrid(drawlocationx);
				drawlocationy = snapToGrid(drawlocationy);
			}
			g.setColor(Color.BLACK);
			g.setStroke(new BasicStroke(this.vertexstroke));
			g.drawOval(drawstartlocationx-this.vertexradius, drawstartlocationy-this.vertexradius, this.vertexradius*2, this.vertexradius*2);
			g.drawOval(drawlocationx-this.vertexradius, drawlocationy-this.vertexradius, this.vertexradius*2, this.vertexradius*2);
			g.setColor(Color.BLUE);
			g.setStroke(new BasicStroke(this.linestroke));
			g.drawLine(drawstartlocationx, drawstartlocationy, drawlocationx, drawlocationy);
		}
	}

	private int snapToGrid(int coordinate) {
		return this.gridstep*Math.floorDiv(coordinate, this.gridstep)+this.griddelta;
	}
	
	private int getVertexAtMouse() {
		int k = -1;
		Sphere[] vsphere1 = new Sphere[1]; vsphere1[0] = new Sphere(this.mouselocationx,this.mouselocationy,0,0); 
		Sphere[] vsphere2 = new Sphere[2*this.linelist.size()];
		for (int i=0;i<linelist.size();i++) {
			vsphere2[2*i] = new Sphere(linelist.get(i).pos1.x, linelist.get(i).pos1.y, 0.0f, this.vertexradius);
			vsphere2[2*i+1] = new Sphere(linelist.get(i).pos2.x, linelist.get(i).pos2.y, 0.0f, this.vertexradius);
		}
		boolean[][] ssint = MathLib.sphereSphereIntersection(vsphere1, vsphere2);
		if (ssint!=null) {
    		for (int i=0;(!this.draglinemode)&&(i<ssint[0].length);i++) {
    			if (ssint[0][i]) {
    				k = i;
    			}
    		}
		}
		return k;
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
		} else if (e.getKeyCode()==KeyEvent.VK_SHIFT) {
			this.snaplinemode = true;
		} else if (e.getKeyCode()==KeyEvent.VK_ADD) {
			this.drawheight += 1;
			if (this.snaplinemode) {
				this.drawheight = snapToGrid(this.drawheight);
			}
		} else if (e.getKeyCode()==KeyEvent.VK_SUBTRACT) {
			this.drawheight -= 1;
			if (this.snaplinemode) {
				this.drawheight = snapToGrid(this.drawheight);
			}
		}else if (e.getKeyCode()==KeyEvent.VK_F2) {
			this.filechooser.setDialogTitle("Save File");
			this.filechooser.setApproveButtonText("Save");
			if (this.filechooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				File savefile = this.filechooser.getSelectedFile();
				Model savemodel = new Model(savefile.getPath());
			}
		} else if (e.getKeyCode()==KeyEvent.VK_F3) {
			this.filechooser.setDialogTitle("Load File");
			this.filechooser.setApproveButtonText("Load");
			if (this.filechooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				File loadfile = this.filechooser.getSelectedFile();
				Model loadmodel = ModelLib.loadWaveFrontOBJFile(loadfile.getPath(), false);
			}
		}
	}
	
	@Override public void mouseMoved(MouseEvent e) {this.mouselocationx=e.getX();this.mouselocationy=e.getY();}
	@Override public void mousePressed(MouseEvent e) {
		this.mouselocationx=e.getX();this.mouselocationy=e.getY();
		this.mousestartlocationx=this.mouselocationx;this.mousestartlocationy=this.mouselocationy;
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
				if (this.drawlinemode) {
					this.drawlinemode = false;
					int drawstartlocationx = this.mousestartlocationx;
					int drawstartlocationy = this.mousestartlocationy;
					int drawlocationx = this.mouselocationx;
					int drawlocationy = this.mouselocationy;
					if (this.snaplinemode) {
						drawstartlocationx = snapToGrid(drawstartlocationx);
						drawstartlocationy = snapToGrid(drawstartlocationy);
						drawlocationx = snapToGrid(drawlocationx);
						drawlocationy = snapToGrid(drawlocationy);
					}
					this.linelist.add(new Position2(new Position(drawstartlocationx, drawstartlocationy,0), new Position(drawlocationx, drawlocationy,0)));
				}
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
	    int offmask1 = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK; //MouseEvent.SHIFT_DOWN_MASK|
	    int onmask3 = MouseEvent.BUTTON3_DOWN_MASK;
	    int offmask3 = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK; //MouseEvent.SHIFT_DOWN_MASK|
	    boolean mouse1down = ((e.getModifiersEx() & (onmask1 | offmask1)) == onmask1);
	    boolean mouse3down = ((e.getModifiersEx() & (onmask3 | offmask3)) == onmask3);
	    if (mouse1down||mouse3down) {
	    	if (mouse1down) {
	    		if (this.draglinemode) {
    				int linenum = Math.floorDiv(this.selecteddragvertex,2);
    				boolean firstvertex = Math.floorMod(this.selecteddragvertex,2)==0;
    				int drawlocationx = this.mouselocationx;
    				int drawlocationy = this.mouselocationy;
    				if (this.snaplinemode) {
    					drawlocationx = snapToGrid(drawlocationx);
    					drawlocationy = snapToGrid(drawlocationy);
    				}
    				if (firstvertex) {
    					this.linelist.get(linenum).pos1.x = drawlocationx;
    					this.linelist.get(linenum).pos1.y = drawlocationy;
	    			} else {
    					this.linelist.get(linenum).pos2.x = drawlocationx;
    					this.linelist.get(linenum).pos2.y = drawlocationy;
	    			}
	    		}
    		}
		}
	    int onmask1c = MouseEvent.BUTTON1_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    int offmask1c = MouseEvent.CTRL_DOWN_MASK; //MouseEvent.SHIFT_DOWN_MASK|
	    int onmask3c = MouseEvent.BUTTON3_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    int offmask3c = MouseEvent.CTRL_DOWN_MASK; //MouseEvent.SHIFT_DOWN_MASK|
	    boolean mouse1altdown = ((e.getModifiersEx() & (onmask1c | offmask1c)) == onmask1c);
	    boolean mouse3altdown = ((e.getModifiersEx() & (onmask3c | offmask3c)) == onmask3c);
	    if (mouse1altdown||mouse3altdown) {
	    	if (mouse1altdown) {
	    		this.drawlinemode = true;
	    	}
	    }
	}
	
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mouseWheelMoved(MouseWheelEvent e) {}
	@Override public void drop(DropTargetDropEvent dtde) {}

	private class ImageFileFilters  {
		public static class OBJFileFilter extends FileFilter {
			@Override public boolean accept(File f) {return (f.isDirectory())||(f.getName().endsWith(".obj"));}
			@Override public String getDescription() {return "OBJ Model file";}
		}
	}
	
}
