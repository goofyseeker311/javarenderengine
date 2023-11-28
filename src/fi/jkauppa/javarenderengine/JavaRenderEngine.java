package fi.jkauppa.javarenderengine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import fi.jkauppa.javarenderengine.MathLib.Direction;
import fi.jkauppa.javarenderengine.MathLib.Plane;
import fi.jkauppa.javarenderengine.MathLib.Position;

public class JavaRenderEngine extends JFrame implements KeyListener,MouseListener,MouseMotionListener,MouseWheelListener {
	private static final long serialVersionUID = 1L;
	private RenderPanel renderpanel = new RenderPanel();
	private boolean windowedmode = false;
	private Color drawcolor = Color.BLACK;
	private float[] drawcolorhsb = {0.0f, 0.0f, 0.0f};
	private int pencilsize = 1;
	private int pencilshape = 1;
	private BufferedImage loadimage = null;

	public JavaRenderEngine() {
		this.addKeyListener(this);
		renderpanel.addMouseListener(this);
		renderpanel.addMouseMotionListener(this);
		renderpanel.addMouseWheelListener(this);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH);
		this.setUndecorated(true);
		this.add(renderpanel);
		this.setVisible(true);
	}

	public static void main(String[] args) {
		Position campos = new Position(0.0f,0.0f,0.0f);
		Direction[] camdir = new Direction[1]; camdir[0] = new Direction(1.0f,0.0f,0.0f);
		camdir = MathLib.NormalizeVector(camdir);
		System.out.println("camdir: "+camdir[0].dx+" "+camdir[0].dy+" "+camdir[0].dz);
		Plane[] tplane = new Plane[1]; tplane[0] = new Plane(1.0f,0.0f,0.0f,-2.0f);
		double[][] dist = MathLib.RayPlaneDistance(campos, camdir, tplane);
		System.out.println("dist["+dist.length+"]["+dist[0].length+"]: "+dist[0][0]);
		JavaRenderEngine app = new JavaRenderEngine();
	}
	
	private class RenderPanel extends JPanel implements ActionListener,ComponentListener {
		private static final long serialVersionUID = 1L;
		private final int fpstarget = 60;
		private final int fpstargetdelay = (int)Math.floor(1000.0f/(2.0f*(double)fpstarget));
		private final Timer timer = new Timer(fpstargetdelay,this);
		private long lastupdate = System.currentTimeMillis();
		private BufferedImage renderbuffer = null;
		public RenderPanel() {
			this.addComponentListener(this);
			timer.start();
		}
		@Override
		public void paintComponent(Graphics g) {
			long newupdate = System.currentTimeMillis();
			long ticktime = newupdate-lastupdate;
			double ticktimefps = 1000.0f/(double)ticktime;
			lastupdate = newupdate; 
			Graphics2D g2 = (Graphics2D)g;
			if (renderbuffer!=null) {
				g2.drawImage(renderbuffer, 0, 0, null);
			}
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			this.repaint();
		}
		public BufferedImage getRenderBuffer() {
			return renderbuffer;
		}
		
		@Override public void componentMoved(ComponentEvent e) {}
		@Override public void componentShown(ComponentEvent e) {}
		@Override public void componentHidden(ComponentEvent e) {}
		
		@Override
		public void componentResized(ComponentEvent e) {
			BufferedImage oldimage = renderbuffer; 
			renderbuffer = new BufferedImage(this.getWidth(),this.getWidth(),BufferedImage.TYPE_INT_ARGB);
			Graphics2D gfx = (Graphics2D)renderbuffer.getGraphics();
			gfx.setColor(Color.WHITE);
			gfx.fillRect(0, 0, renderbuffer.getWidth(), renderbuffer.getHeight());
			if (oldimage!=null) {
				gfx.drawImage(oldimage, 0, 0, null);
			}
		}
	}

	@Override public void keyTyped(KeyEvent e) {}
	@Override public void keyReleased(KeyEvent e) {}
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mouseMoved(MouseEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}
		if (e.getKeyCode()==KeyEvent.VK_ENTER) {
		    int onmask = KeyEvent.ALT_DOWN_MASK;
		    int offmask = 0;
		    if ((e.getModifiersEx() & (onmask | offmask)) == onmask) {
		    	JavaRenderEngine.this.dispose();
		    	if (!windowedmode) {
		    		windowedmode = true;
		    		JavaRenderEngine.this.setExtendedState(JavaRenderEngine.this.getExtendedState()&~JFrame.MAXIMIZED_BOTH);
		    		JavaRenderEngine.this.setUndecorated(false);
		    	}else {
		    		windowedmode = false;
		    		JavaRenderEngine.this.setExtendedState(JavaRenderEngine.this.getExtendedState()|JFrame.MAXIMIZED_BOTH);
		    		JavaRenderEngine.this.setUndecorated(true);
		    	}
		    	JavaRenderEngine.this.setVisible(true);
		    }
		}
		if (e.getKeyCode()==KeyEvent.VK_BACK_SPACE) {
			BufferedImage renderbufferhandle = renderpanel.getRenderBuffer();
			if (renderbufferhandle!=null) {
				Graphics2D gfx = (Graphics2D)renderbufferhandle.getGraphics();
				gfx.setColor(Color.WHITE);
				gfx.fillRect(0, 0, renderbufferhandle.getWidth(), renderbufferhandle.getHeight());
			}
		}
		if (e.getKeyCode()==KeyEvent.VK_INSERT) {
			this.drawcolorhsb[0] += 0.01f;
			if (this.drawcolorhsb[0]>1.0f) {this.drawcolorhsb[0] = 0.0f;}
			this.drawcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
		}
		if (e.getKeyCode()==KeyEvent.VK_DELETE) {
			this.drawcolorhsb[0] -= 0.01f;
			if (this.drawcolorhsb[0]<0.0f) {this.drawcolorhsb[0] = 1.0f;}
			this.drawcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
		}
		if (e.getKeyCode()==KeyEvent.VK_HOME) {
			this.drawcolorhsb[1] += 0.01f;
			if (this.drawcolorhsb[1]>1.0f) {this.drawcolorhsb[1] = 1.0f;}
			this.drawcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
		}
		if (e.getKeyCode()==KeyEvent.VK_END) {
			this.drawcolorhsb[1] -= 0.01f;
			if (this.drawcolorhsb[1]<0.0f) {this.drawcolorhsb[1] = 0.0f;}
			this.drawcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
		}
		if (e.getKeyCode()==KeyEvent.VK_PAGE_UP) {
			this.drawcolorhsb[2] += 0.01f;
			if (this.drawcolorhsb[2]>1.0f) {this.drawcolorhsb[2] = 1.0f;}
			this.drawcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
		}
		if (e.getKeyCode()==KeyEvent.VK_PAGE_DOWN) {
			this.drawcolorhsb[2] -= 0.01f;
			if (this.drawcolorhsb[2]<0.0f) {this.drawcolorhsb[2] = 0.0f;}
			this.drawcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
		}
		if (e.getKeyCode()==KeyEvent.VK_F1) {
			//TODO help pop-up window
		}
		if (e.getKeyCode()==KeyEvent.VK_F2) {
			//TODO save pop-up window
		}
		if (e.getKeyCode()==KeyEvent.VK_F3) {
			JFileChooser chooser = new JFileChooser();
			if (chooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				File loadfile = chooser.getSelectedFile() ;
				try {loadimage = ImageIO.read(loadfile);} catch (IOException ex) {}
				if (loadimage!=null) {
					Graphics2D gfx = (Graphics2D)renderpanel.getRenderBuffer().getGraphics();
					gfx.drawImage(loadimage, 0, 0, null);
				}
			}
		}
		if (e.getKeyCode()==KeyEvent.VK_F4) {
			//TODO tools pop-up window
		}
		if (e.getKeyCode()==KeyEvent.VK_F5) {
			//TODO color pop-up window
		}
	}

	@Override public void mousePressed(MouseEvent e) {mouseDragged(e);}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		BufferedImage renderbufferhandle = renderpanel.getRenderBuffer();
		if (renderbufferhandle!=null) {
			int pencilwidth = (int)Math.ceil((double)(this.pencilsize-1)/2.0f);
			Graphics2D renderbuffergfx = (Graphics2D)renderbufferhandle.getGraphics();
		    int onmask1 = MouseEvent.BUTTON1_DOWN_MASK;
		    int offmask1 = 0;
		    if ((e.getModifiersEx() & (onmask1 | offmask1)) == onmask1) {
					renderbuffergfx.setColor(this.drawcolor);
					if (this.pencilshape==1) {
						renderbuffergfx.fillRect(e.getX()-pencilwidth, e.getY()-pencilwidth, this.pencilsize, this.pencilsize);
					} else if (this.pencilshape==2) {
						renderbuffergfx.fillOval(e.getX()-pencilwidth, e.getY()-pencilwidth, this.pencilsize, this.pencilsize);
					} else if (this.pencilshape==3) {
						renderbuffergfx.drawRect(e.getX()-pencilwidth, e.getY()-pencilwidth, this.pencilsize, this.pencilsize);
					} else if (this.pencilshape==4) {
						renderbuffergfx.drawOval(e.getX()-pencilwidth, e.getY()-pencilwidth, this.pencilsize, this.pencilsize);
					}else {
						renderbuffergfx.fillRect(e.getX()-pencilwidth, e.getY()-pencilwidth, this.pencilsize, this.pencilsize);
					}
			}			
		    int onmask2 = MouseEvent.BUTTON2_DOWN_MASK;
		    int offmask2 = MouseEvent.SHIFT_DOWN_MASK;
		    if ((e.getModifiersEx() & (onmask2 | offmask2)) == onmask2) {
		    	this.pencilshape += 1;
		    	if (this.pencilshape>4) {
		    		this.pencilshape = 1;
		    	}
		    }
		    int onmask3 = MouseEvent.BUTTON3_DOWN_MASK;
		    int offmask3 = 0;
		    if ((e.getModifiersEx() & (onmask3 | offmask3)) == onmask3) {
					renderbuffergfx.setColor(Color.WHITE);
					if (this.pencilshape==1) {
						renderbuffergfx.fillRect(e.getX()-pencilwidth, e.getY()-pencilwidth, this.pencilsize, this.pencilsize);
					} else if (this.pencilshape==2) {
						renderbuffergfx.fillOval(e.getX()-pencilwidth, e.getY()-pencilwidth, this.pencilsize, this.pencilsize);
					} else if (this.pencilshape==3) {
						renderbuffergfx.drawRect(e.getX()-pencilwidth, e.getY()-pencilwidth, this.pencilsize, this.pencilsize);
					} else if (this.pencilshape==4) {
						renderbuffergfx.drawOval(e.getX()-pencilwidth, e.getY()-pencilwidth, this.pencilsize, this.pencilsize);
					}else {
						renderbuffergfx.fillRect(e.getX()-pencilwidth, e.getY()-pencilwidth, this.pencilsize, this.pencilsize);
					}
			}			
		    int onmask4 = MouseEvent.BUTTON2_DOWN_MASK|MouseEvent.SHIFT_DOWN_MASK;
		    int offmask4 = 0;
		    if ((e.getModifiersEx() & (onmask4 | offmask4)) == onmask4) {
		    	//TODO zoom into canvas
		    }
		}
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		this.pencilsize += e.getWheelRotation();
		if (this.pencilsize<1) {
			this.pencilsize = 1;
		}
	}
}
