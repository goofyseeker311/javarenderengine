package fi.jkauppa.javarenderengine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.Transparency;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import fi.jkauppa.javarenderengine.JavaRenderEngine.AppHandlerPanel;
import fi.jkauppa.javarenderengine.UtilLib.ImageTransferable;

public class DrawApp extends AppHandlerPanel {
	private static final long serialVersionUID = 1L;
	private float[] hsbdrawcolor = {0.0f,1.0f,0.0f,1.0f};
	private float penciltransparency = 1.0f;
	private Color drawcolor = UtilLib.getHSBTransparencyColor(hsbdrawcolor[0],hsbdrawcolor[1],hsbdrawcolor[2],this.penciltransparency);
	private int pencilsize = 1;
	private int pencilshape = 1;
	private double pencilangle = 0;
	private int oldpencilsize = 1;
	private boolean drawlinemode = false;
	private boolean rotatemode = false;
	private int mousestartlocationx = -1, mousestartlocationy = -1;  
	private int mouselastlocationx = -1, mouselastlocationy = -1;  
	private int mouselocationx = -1, mouselocationy = -1;
	private BufferedImage renderbuffer = null;
	private BufferedImage dragbuffer = null;
	private TexturePaint bgpattern = null;
	private BufferedImage pencilbuffer = null;
	
	public DrawApp() {
		BufferedImage bgpatternimage = this.gc.createCompatibleImage(64,64,Transparency.OPAQUE);
		Graphics2D pgfx = bgpatternimage.createGraphics();
		pgfx.setColor(Color.WHITE);
		pgfx.fillRect(0, 0, bgpatternimage.getWidth(), bgpatternimage.getHeight());
		pgfx.setColor(Color.BLACK);
		pgfx.drawLine(31, 0, 31, 63);
		pgfx.drawLine(0, 31, 63, 31);
		this.bgpattern = new TexturePaint(bgpatternimage, new Rectangle(0, 0, bgpatternimage.getWidth(), bgpatternimage.getHeight()));
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		this.setFocusTraversalKeysEnabled(false);
	}
	
	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		this.renderwidth = this.getWidth();
		this.renderheight = this.getHeight();
		if ((renderbuffer==null)||(renderbuffer.getWidth()!=this.getWidth())||(renderbuffer.getHeight()!=this.getHeight())) {
			BufferedImage oldimage = this.renderbuffer;
			this.renderbuffer = gc.createCompatibleImage(this.getWidth(),this.getHeight(), Transparency.TRANSLUCENT);
			this.dragbuffer = gc.createCompatibleImage(this.getWidth(),this.getHeight(), Transparency.TRANSLUCENT);
			Graphics2D gfx = this.renderbuffer.createGraphics();
			gfx.setComposite(AlphaComposite.Clear);
			gfx.fillRect(0, 0, this.getWidth(),this.getHeight());
			if (oldimage!=null) {
				gfx.setComposite(AlphaComposite.Src);
				gfx.drawImage(oldimage, 0, 0, null);
			}
		}
		g2.setPaint(this.bgpattern);
		g2.fillRect(0, 0, renderbuffer.getWidth(), renderbuffer.getHeight());
		g2.setPaint(null);
		g2.drawImage(renderbuffer, 0, 0, null);
		if (this.drawlinemode) {
			this.drawPencilLine(g2, this.mousestartlocationx, this.mousestartlocationy, this.mouselocationx, this.mouselocationy, false);
		} else {
			this.drawPencil(g2, this.mouselocationx, this.mouselocationy, false);
		}
	}
	
	@Override public void tick() {}
	@Override public void keyTyped(KeyEvent e) {}
	
	@Override public void keyPressed(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_ESCAPE) {
			//TODO options menu
		} else if (e.getKeyCode()==KeyEvent.VK_ENTER) {
			//TODO <tbd>
		} else if (e.getKeyCode()==KeyEvent.VK_TAB) {
			this.rotatemode = true;
		} else if (e.getKeyCode()==KeyEvent.VK_BACK_SPACE) {
			this.renderbuffer = null;
		} else if (e.getKeyCode()==KeyEvent.VK_C) {
			if (e.isControlDown()) {
				ImageTransferable cbimage =  new ImageTransferable(UtilLib.flipImage(this.renderbuffer, false, false));
				this.cb.setContents(cbimage, null);
			}
		} else if (e.getKeyCode()==KeyEvent.VK_V) {
			if (e.isControlDown()) {
				try {
				if (this.cb.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
					Image cbimage = (Image)this.cb.getData(DataFlavor.imageFlavor);
					Graphics2D gfx = this.renderbuffer.createGraphics();
					gfx.setComposite(AlphaComposite.Src);
					gfx.drawImage(cbimage, 0, 0, null);
				}
				} catch(Exception ex) {ex.printStackTrace();}
			}
		} else if (e.getKeyCode()==KeyEvent.VK_INSERT) {
	    	if (e.isShiftDown()) {
	    		this.hsbdrawcolor[0] = this.hsbdrawcolor[0] + 0.20f;
	    	} else {
	    		this.hsbdrawcolor[0] = this.hsbdrawcolor[0] + 0.01f;
	    	}
			if (this.hsbdrawcolor[0]>1.0f) {this.hsbdrawcolor[0] = 0.0f;}
			this.drawcolor = UtilLib.getHSBTransparencyColor(hsbdrawcolor[0],hsbdrawcolor[1],hsbdrawcolor[2],this.penciltransparency);
	    	if (this.pencilbuffer!=null) {
	    		this.pencilbuffer = null;
	    		this.pencilsize = this.oldpencilsize;
	    	}
		} else if (e.getKeyCode()==KeyEvent.VK_DELETE) {
	    	if (e.isShiftDown()) {
	    		this.hsbdrawcolor[0] = this.hsbdrawcolor[0] - 0.20f;
	    	} else {
	    		this.hsbdrawcolor[0] = this.hsbdrawcolor[0] - 0.01f;
	    	}
			if (this.hsbdrawcolor[0]<0.0f) {this.hsbdrawcolor[0] = 1.0f;}
			this.drawcolor = UtilLib.getHSBTransparencyColor(hsbdrawcolor[0],hsbdrawcolor[1],hsbdrawcolor[2],this.penciltransparency);
	    	if (this.pencilbuffer!=null) {
	    		this.pencilbuffer = null;
	    		this.pencilsize = this.oldpencilsize;
	    	}
		} else if (e.getKeyCode()==KeyEvent.VK_HOME) {
	    	if (e.isShiftDown()) {
	    		this.hsbdrawcolor[1] = this.hsbdrawcolor[1] + 0.20f;
	    	} else {
				this.hsbdrawcolor[1] = this.hsbdrawcolor[1] + 0.01f;
	    	}
			if (this.hsbdrawcolor[1]>1.0f) {this.hsbdrawcolor[1] = 1.0f;}
			this.drawcolor = UtilLib.getHSBTransparencyColor(hsbdrawcolor[0],hsbdrawcolor[1],hsbdrawcolor[2],this.penciltransparency);
	    	if (this.pencilbuffer!=null) {
	    		this.pencilbuffer = null;
	    		this.pencilsize = this.oldpencilsize;
	    	}
		} else if (e.getKeyCode()==KeyEvent.VK_END) {
	    	if (e.isShiftDown()) {
	    		this.hsbdrawcolor[1] = this.hsbdrawcolor[1] - 0.20f;
	    	} else {
				this.hsbdrawcolor[1] = this.hsbdrawcolor[1] - 0.01f;
	    	}
			if (this.hsbdrawcolor[1]<0.0f) {this.hsbdrawcolor[1] = 0.0f;}
			this.drawcolor = UtilLib.getHSBTransparencyColor(hsbdrawcolor[0],hsbdrawcolor[1],hsbdrawcolor[2],this.penciltransparency);
	    	if (this.pencilbuffer!=null) {
	    		this.pencilbuffer = null;
	    		this.pencilsize = this.oldpencilsize;
	    	}
		} else if (e.getKeyCode()==KeyEvent.VK_PAGE_UP) {
	    	if (e.isShiftDown()) {
	    		this.hsbdrawcolor[2] = this.hsbdrawcolor[2] + 0.20f;
	    	} else {
				this.hsbdrawcolor[2] = this.hsbdrawcolor[2] + 0.01f;
	    	}
			if (this.hsbdrawcolor[2]>1.0f) {this.hsbdrawcolor[2] = 1.0f;}
			this.drawcolor = UtilLib.getHSBTransparencyColor(hsbdrawcolor[0],hsbdrawcolor[1],hsbdrawcolor[2],this.penciltransparency);
	    	if (this.pencilbuffer!=null) {
	    		this.pencilbuffer = null;
	    		this.pencilsize = this.oldpencilsize;
	    	}
		} else if (e.getKeyCode()==KeyEvent.VK_PAGE_DOWN) {
	    	if (e.isShiftDown()) {
	    		this.hsbdrawcolor[2] = this.hsbdrawcolor[2] - 0.20f;
	    	} else {
				this.hsbdrawcolor[2] = this.hsbdrawcolor[2] - 0.01f;
	    	}
			if (this.hsbdrawcolor[2]<0.0f) {this.hsbdrawcolor[2] = 0.0f;}
			this.drawcolor = UtilLib.getHSBTransparencyColor(hsbdrawcolor[0],hsbdrawcolor[1],hsbdrawcolor[2],this.penciltransparency);
	    	if (this.pencilbuffer!=null) {
	    		this.pencilbuffer = null;
	    		this.pencilsize = this.oldpencilsize;
	    	}
		} else if (e.getKeyCode()==KeyEvent.VK_ADD) {
			this.pencilsize += 1;
		} else if (e.getKeyCode()==KeyEvent.VK_SUBTRACT) {
			this.pencilsize -= 1;
			if (this.pencilsize<1) {this.pencilsize = 1;}
		} else if (e.getKeyCode()==KeyEvent.VK_DIVIDE) {
	    	this.pencilshape -= 1;
	    	if (this.pencilshape<1) {this.pencilshape = 6;}
	    	if (this.pencilbuffer!=null) {
	    		this.pencilbuffer = null;
	    		this.pencilsize = this.oldpencilsize;
	    	}
		} else if (e.getKeyCode()==KeyEvent.VK_MULTIPLY) {
	    	this.pencilshape += 1;
	    	if (this.pencilshape>6) {this.pencilshape = 1;}
	    	if (this.pencilbuffer!=null) {
	    		this.pencilbuffer = null;
	    		this.pencilsize = this.oldpencilsize;
	    	}
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD9) {
			this.penciltransparency += 0.01f;
			if (this.penciltransparency>1.0f) {this.penciltransparency = 1.0f;}
			this.drawcolor = UtilLib.getHSBTransparencyColor(hsbdrawcolor[0],hsbdrawcolor[1],hsbdrawcolor[2],this.penciltransparency);
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD8) {
			this.penciltransparency -= 0.01f;
			if (this.penciltransparency<0.0f) {this.penciltransparency = 0.0f;}
			this.drawcolor = UtilLib.getHSBTransparencyColor(hsbdrawcolor[0],hsbdrawcolor[1],hsbdrawcolor[2],this.penciltransparency);
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD6) {
	    	if (e.isShiftDown()) {
	    		this.pencilangle += 20.0f*0.05f;
	    	} else {
	    		this.pencilangle += 1.0f*0.05f;
	    	}
			if (this.pencilangle>360.0f) {
				this.pencilangle = 0.0f;
			}
		} else if (e.getKeyCode()==KeyEvent.VK_NUMPAD5) {
	    	if (e.isShiftDown()) {
	    		this.pencilangle -= 20.0f*0.05f;
	    	} else {
	    		this.pencilangle -= 1.0f*0.05f;
	    	}
			if (this.pencilangle<0.0f) {
				this.pencilangle = 360.0f;
			}
		} else if (e.getKeyCode()==KeyEvent.VK_F1) {
			//TODO help pop-up window
		} else if (e.getKeyCode()==KeyEvent.VK_F2) {
        	JFileChooser filechooser = UtilLib.createImageFileChooser();
	    	filechooser.setDialogTitle("Save Image");
	    	filechooser.setApproveButtonText("Save");
        	filechooser.setCurrentDirectory(new File(this.userdir));
        	if (filechooser.showSaveDialog(null)==JFileChooser.APPROVE_OPTION) {
        		File savefile = filechooser.getSelectedFile();
				if (savefile.getParent()!=null) {this.userdir = savefile.getParent();}
	    		FileFilter savefileextension = filechooser.getFileFilter();
	    		BufferedImage saveimage = this.renderbuffer;
				UtilLib.saveImageFormat(savefile.getPath(), saveimage, savefileextension);
        	}
		} else if (e.getKeyCode()==KeyEvent.VK_F3) {
        	JFileChooser filechooser = UtilLib.createAllImageFileChooser();
	    	filechooser.setDialogTitle("Load Image");
	    	filechooser.setApproveButtonText("Load");
        	filechooser.setCurrentDirectory(new File(this.userdir));
        	if (filechooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
        		File loadfile = filechooser.getSelectedFile();
				if (loadfile.getParent()!=null) {this.userdir = loadfile.getParent();}
				BufferedImage loadimage = UtilLib.loadImage(loadfile.getPath(), false);
				if (loadimage!=null) {
				    boolean f3shiftdown = ((!e.isControlDown())&&(!e.isAltDown())&&(e.isShiftDown())&&(!e.isMetaDown()));
				    if (f3shiftdown) {
				    	this.oldpencilsize = this.pencilsize;
						this.pencilsize = loadimage.getWidth();
				    	this.pencilbuffer = loadimage;
				    }else{
				    	Graphics2D dragimagegfx = this.renderbuffer.createGraphics();
				    	dragimagegfx.setComposite(AlphaComposite.Src);
				    	dragimagegfx.drawImage(loadimage, 0, 0, null);
				    }
				}
			}
		} else if (e.getKeyCode()==KeyEvent.VK_F4) {
			//TODO tools/color pop-up window
		}
	}
	
	@Override public void keyReleased(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_TAB) {
			this.rotatemode = false;
		}
	}
	
	@Override public void mouseClicked(MouseEvent e) {}
	
	@Override public void mousePressed(MouseEvent e) {
		this.mouselocationx=e.getX();
		this.mouselocationy=e.getY();
		this.mousestartlocationx=this.mouselocationx;
		this.mousestartlocationy=this.mouselocationy;
		mouseDragged(e);
	}
	
	@Override public void mouseReleased(MouseEvent e) {
		if (this.renderbuffer!=null) {
			Graphics2D renderbuffergfx = this.renderbuffer.createGraphics();
		    boolean mouse1up = e.getButton()==MouseEvent.BUTTON1;
		    boolean mouse3up = e.getButton()==MouseEvent.BUTTON3;
			if (mouse1up||mouse3up) {
				if (this.drawlinemode) {
					this.drawlinemode=false;
					drawPencilLine(renderbuffergfx, this.mousestartlocationx, this.mousestartlocationy, this.mouselocationx, this.mouselocationy, mouse3up);
				}
			}
		}
	}
	
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	
	@Override public void mouseDragged(MouseEvent e) {
		this.mouselastlocationx=this.mouselocationx;
		this.mouselastlocationy=this.mouselocationy;
		this.mouselocationx=e.getX();
		this.mouselocationy=e.getY();
    	int mousedeltax = this.mouselocationx - this.mouselastlocationx; 
    	int mousedeltay = this.mouselocationy - this.mouselastlocationy;
		if (this.renderbuffer!=null) {
			Graphics2D renderbuffergfx = this.renderbuffer.createGraphics();
		    boolean mouse1down = ((e.getModifiersEx()&MouseEvent.BUTTON1_DOWN_MASK)!=0)&&((!e.isControlDown())&&(!e.isAltDown())&&(!e.isMetaDown()));
		    boolean mouse3down = ((e.getModifiersEx()&MouseEvent.BUTTON3_DOWN_MASK)!=0)&&((!e.isControlDown())&&(!e.isAltDown())&&(!e.isMetaDown()));
		    if (mouse1down||mouse3down) {
		    	this.drawPencil(renderbuffergfx, this.mouselocationx, this.mouselocationy, mouse3down);
			}
		    boolean mouse1altdown = ((e.getModifiersEx()&MouseEvent.BUTTON1_DOWN_MASK)!=0)&&((!e.isControlDown())&&(e.isAltDown())&&(!e.isMetaDown()));
		    boolean mouse3altdown = ((e.getModifiersEx()&MouseEvent.BUTTON3_DOWN_MASK)!=0)&&((!e.isControlDown())&&(e.isAltDown())&&(!e.isMetaDown()));
		    if (mouse1altdown||mouse3altdown) {
			    this.drawlinemode = true;
		    }
		    boolean mouse1ctrldown = ((e.getModifiersEx()&MouseEvent.BUTTON1_DOWN_MASK)!=0)&&((e.isControlDown())&&(!e.isAltDown())&&(!e.isMetaDown()));
		    if (mouse1ctrldown) {
		    	this.drawcolor = new Color(this.renderbuffer.getRGB(this.mouselocationx, this.mouselocationy));
		    	this.hsbdrawcolor = UtilLib.getHSBTransparencyColorComponents(this.drawcolor, this.penciltransparency);
		    }
		    boolean mouse1controldown = ((e.getModifiersEx()&MouseEvent.BUTTON1_DOWN_MASK)!=0)&&((e.isControlDown())&&(!e.isAltDown())&&(!e.isMetaDown()));
		    if (mouse1controldown) {
		    	//TODO select canvas region
		    }
		    boolean mouse2down = ((e.getModifiersEx()&MouseEvent.BUTTON2_DOWN_MASK)!=0)&&((!e.isControlDown())&&(!e.isAltDown())&&(!e.isMetaDown()));
		    if (mouse2down) {
		    	Graphics2D dragimagegfx = this.dragbuffer.createGraphics();
		    	dragimagegfx.setColor(new Color(0.0f,0.0f,0.0f,0.0f));
		    	dragimagegfx.setComposite(AlphaComposite.Src);
		    	dragimagegfx.fillRect(0, 0, this.renderbuffer.getWidth(), this.renderbuffer.getHeight());
		    	dragimagegfx.drawImage(this.renderbuffer, mousedeltax, mousedeltay, null);
		    	renderbuffergfx.setColor(new Color(0.0f,0.0f,0.0f,0.0f));
		    	renderbuffergfx.setComposite(AlphaComposite.Src);
		    	renderbuffergfx.fillRect(0, 0, this.renderbuffer.getWidth(), this.renderbuffer.getHeight());
		    	renderbuffergfx.drawImage(this.dragbuffer, 0, 0, null);
		    }
		}
	}
	
	@Override public void mouseMoved(MouseEvent e) {
		this.mouselocationx=e.getX();
		this.mouselocationy=e.getY();
	}
	
	@Override public void mouseWheelMoved(MouseWheelEvent e) {
		float scrollticks = e.getWheelRotation();
	    boolean mousewheeldown = ((!e.isControlDown())&&(!e.isAltDown())&&(!e.isMetaDown()));
	    if (mousewheeldown) {
	    	if (this.rotatemode) {
		    	if (e.isShiftDown()) {
		    		this.pencilangle -= 20.0f*0.05f*scrollticks;
		    	} else {
		    		this.pencilangle -= 1.0f*0.05f*scrollticks;
		    	}
				if (this.pencilangle>360.0f) {
					this.pencilangle = 0.0f;
				} else if (this.pencilangle<0.0f) {
					this.pencilangle = 360.0f;
				}
	    	} else {
		    	if (e.isShiftDown()) {
		    		this.pencilsize -= 20.0f*scrollticks*((this.pencilsize>16)?this.pencilsize/16:1);
		    	} else {
		    		this.pencilsize -= 1.0f*scrollticks*((this.pencilsize>16)?this.pencilsize/16:1);
		    	}
				if (this.pencilsize<1) {
					this.pencilsize = 1;
				}
	    	}
	    }
	    boolean mousewheelctrldown = ((e.isControlDown())&&(!e.isAltDown())&&(!e.isMetaDown()));
	    if (mousewheelctrldown) {
	    	if (e.isShiftDown()) {
	    		this.hsbdrawcolor[0] = this.hsbdrawcolor[0] + 0.20f*scrollticks;
	    	} else {
	    		this.hsbdrawcolor[0] = this.hsbdrawcolor[0] + 0.01f*scrollticks;
	    	}
			if (this.hsbdrawcolor[0]>1.0f) {this.hsbdrawcolor[0] = 0.0f;}
			if (this.hsbdrawcolor[0]<0.0f) {this.hsbdrawcolor[0] = 1.0f;}
			this.drawcolor = UtilLib.getHSBTransparencyColor(this.hsbdrawcolor[0],this.hsbdrawcolor[1],this.hsbdrawcolor[2],this.penciltransparency);
	    }
	    boolean mousewheelaltdown = ((!e.isControlDown())&&(e.isAltDown())&&(!e.isMetaDown()));
	    if (mousewheelaltdown) {
	    	if (e.isShiftDown()) {
	    		this.hsbdrawcolor[1] = this.hsbdrawcolor[1] - 0.20f*scrollticks;
	    	} else {
	    		this.hsbdrawcolor[1] = this.hsbdrawcolor[1] - 0.01f*scrollticks;
	    	}
			if (this.hsbdrawcolor[1]>1.0f) {this.hsbdrawcolor[1] = 1.0f;}
			if (this.hsbdrawcolor[1]<0.0f) {this.hsbdrawcolor[1] = 0.0f;}
			this.drawcolor = UtilLib.getHSBTransparencyColor(this.hsbdrawcolor[0],this.hsbdrawcolor[1],this.hsbdrawcolor[2],this.penciltransparency);
	    }
	    boolean mousewheelctrlaltdown = ((e.isControlDown())&&(e.isAltDown())&&(!e.isMetaDown()));
	    if (mousewheelctrlaltdown) {
	    	if (e.isShiftDown()) {
	    		this.hsbdrawcolor[2] = this.hsbdrawcolor[2] - 0.20f*scrollticks;
	    	} else {
	    		this.hsbdrawcolor[2] = this.hsbdrawcolor[2] - 0.01f*scrollticks;
	    	}
			if (this.hsbdrawcolor[2]>1.0f) {this.hsbdrawcolor[2] = 1.0f;}
			if (this.hsbdrawcolor[2]<0.0f) {this.hsbdrawcolor[2] = 0.0f;}
			this.drawcolor = UtilLib.getHSBTransparencyColor(this.hsbdrawcolor[0],this.hsbdrawcolor[1],this.hsbdrawcolor[2],this.penciltransparency);
	    }
	}

	@SuppressWarnings("unchecked")
	@Override public void drop(DropTargetDropEvent dtde) {
		dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
		boolean success = false;
		Transferable dtt = dtde.getTransferable();
        try {
            if ((dtt!=null)&&(dtt.isDataFlavorSupported(DataFlavor.javaFileListFlavor))) {
				List<File> files = (List<File>)dtt.getTransferData(DataFlavor.javaFileListFlavor);
                for (Iterator<File> i=files.iterator();i.hasNext();) {
                	File file = i.next();
                    BufferedImage loadimage = UtilLib.loadImage(file.getPath(), false);
			    	this.oldpencilsize = this.pencilsize;
					this.pencilsize = loadimage.getWidth();
					this.pencilbuffer = loadimage;
                }
            	success = true;
            }
        } catch (Exception ex){ex.printStackTrace();}
		dtde.dropComplete(success);
	}

	private void drawPencil(Graphics2D g, int mousex, int mousey, boolean erasemode) {
		g.setComposite(AlphaComposite.SrcOver);
		g.setPaint(null);
    	if (this.pencilbuffer!=null) {
    		double pencilsizescalefactor = ((double)this.pencilsize)/((double)this.pencilbuffer.getWidth());
    		int scalewidth = (int)Math.floor(((double)this.pencilbuffer.getWidth())*pencilsizescalefactor);
    		int scaleheight = (int)Math.floor(((double)this.pencilbuffer.getHeight())*pencilsizescalefactor);
    		int halfscalewidth = (int)(scalewidth/2.0f);
    		int halfscaleheight = (int)(scaleheight/2.0f);
    		int drawlocationx = mousex - halfscalewidth;
    		int drawlocationy = mousey - halfscaleheight;
    		AffineTransform penciltransform = new AffineTransform();
    		penciltransform.translate(drawlocationx, drawlocationy);
    		penciltransform.rotate(Math.toRadians(this.pencilangle),halfscalewidth,halfscaleheight);
    		AffineTransform otr = g.getTransform();
    		g.transform(penciltransform);
    		if (erasemode) {
		    	g.setColor(new Color(0.0f,0.0f,0.0f,0.0f));
		    	g.setComposite(AlphaComposite.Src);
    			g.fillRect(0, 0, scalewidth, scaleheight);
    		} else {
    			g.drawImage(this.pencilbuffer, 0, 0, scalewidth, scaleheight, 0, 0, this.pencilbuffer.getWidth(), this.pencilbuffer.getHeight(), null);
    		}
    		g.setTransform(otr);
    	} else {
    		int pencilwidth = (int)Math.ceil((double)(this.pencilsize-1)/2.0f);
    		int drawlocationx = mousex - pencilwidth;
    		int drawlocationy = mousey - pencilwidth;
    		AffineTransform penciltransform = new AffineTransform();
    		penciltransform.translate(drawlocationx, drawlocationy);
    		penciltransform.rotate(Math.toRadians(this.pencilangle),pencilwidth,pencilwidth);
    		AffineTransform otr = g.getTransform();
    		g.transform(penciltransform);
    		if (erasemode) {
		    	g.setColor(new Color(0.0f,0.0f,0.0f,0.0f));
		    	g.setComposite(AlphaComposite.Src);
	    		g.fillRect(0, 0, this.pencilsize, this.pencilsize);
    		} else {
    			g.setColor(this.drawcolor);
    			if (this.pencilshape==2) {
    				g.fillRoundRect(0, 0, this.pencilsize, this.pencilsize, 5, 5);
    			} else if (this.pencilshape==3) {
    				g.fillOval(0, 0, this.pencilsize, this.pencilsize);
    			} else if (this.pencilshape==4) {
    				g.drawRect(0, 0, this.pencilsize, this.pencilsize);
    			} else if (this.pencilshape==5) {
    				g.drawRoundRect(0, 0, this.pencilsize, this.pencilsize, 5, 5);
    			} else if (this.pencilshape==6) {
    				g.drawOval(0, 0, this.pencilsize, this.pencilsize);
    			}else {
    				g.fillRect(0, 0, this.pencilsize, this.pencilsize);
    			}
    		}
    		g.setTransform(otr);
    	}
	}
	private void drawPencilLine(Graphics2D g, int mousestartx, int mousestarty, int mousex, int mousey, boolean erasemode) {
		double linedistx = mousex-mousestartx;
		double linedisty = mousey-mousestarty;
		int linestepnum = (int)Math.ceil(Math.sqrt(linedistx*linedistx+linedisty*linedisty))+1;
		double linestepx = linedistx/linestepnum;
		double linestepy = linedisty/linestepnum;
		for (int i=0;i<linestepnum;i++) {
			int drawposx = (int)Math.round(this.mousestartlocationx + i*linestepx);
			int drawposy = (int)Math.round(this.mousestartlocationy + i*linestepy);
	    	this.drawPencil(g, drawposx, drawposy, erasemode);
		}
	}
	
}
