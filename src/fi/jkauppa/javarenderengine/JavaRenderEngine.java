package fi.jkauppa.javarenderengine;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;

public class JavaRenderEngine extends JFrame implements ActionListener,KeyListener,MouseListener,MouseMotionListener,MouseWheelListener {
	private static final long serialVersionUID = 1L;
	private static int defaultimagecanvaswidth = 1920;
	private static int defaultimagecanvasheight = 1080;
	private DrawApp drawapp = new DrawApp();
	private CADApp cadapp = new CADApp();
	private ModelApp modelapp = new ModelApp();
	private EditorApp editorapp = new EditorApp();
	private GameApp gameapp = new GameApp();
	private AppHandlerPanel activeapp = null;
	private DropTargetHandler droptargethandler = new DropTargetHandler();
	private double fpstarget = 288.0f;
	private int fpstargetdelay = (int)Math.floor(1000.0f/fpstarget);
	private Timer timer = new Timer(this.fpstargetdelay,this);
	private boolean windowedmode = true;
	private BufferedImage logoimage = UtilLib.loadImage("res/icons/logo.png", true);
	
	public JavaRenderEngine() {
		if (this.logoimage!=null) {this.setIconImage(this.logoimage);}
		this.setTitle("Java Render Engine v3.3.2");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setJMenuBar(null);
		if (!windowedmode) {
			this.setUndecorated(true);
			this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH);
		}else {
			this.setLocationByPlatform(true);
		}
		this.setFocusTraversalKeysEnabled(false);
		this.setDropTarget(this.droptargethandler);
		this.setActiveApp(drawapp);
		this.setVisible(true);
		timer.start();
	}

	public static void main(String[] args) {
		System.setProperty("sun.java2d.opengl", "true");
		String userdir = System.getProperty("user.dir");
		System.out.println("JavaRenderEngine: main: userdir="+userdir);
		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());} catch (Exception ex) {}
		new JavaRenderEngine();
	}
	
	public static abstract class AppHandlerPanel extends JPanel implements KeyListener,MouseListener,MouseMotionListener,MouseWheelListener {
		private static final long serialVersionUID = 1L;
    	public String userdir = System.getProperty("user.dir");
    	public int renderwidth = defaultimagecanvaswidth;
    	public int renderheight = defaultimagecanvasheight;
    	public long lastticktime = System.currentTimeMillis();
    	public long nowticktime = this.lastticktime;
		public double diffticktime = (double)(this.nowticktime - this.lastticktime);
		public double diffticktimesec = this.diffticktime/1000.0f;
		public GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		public GraphicsDevice gd = ge.getDefaultScreenDevice();
		public GraphicsConfiguration gc = gd.getDefaultConfiguration();
		public Toolkit tk = Toolkit.getDefaultToolkit();
		public Clipboard cb = tk.getSystemClipboard();

		@Override public void paintComponent(Graphics g) {super.paintComponent(g);}
		public abstract void drop(DropTargetDropEvent dtde);
		public abstract void tick();
	}
	
	private class DropTargetHandler extends DropTarget {
		private static final long serialVersionUID = 1L;
		@Override public synchronized void drop(DropTargetDropEvent dtde) {System.out.println("JavaRenderEngine: main: DropTarget: drop");if (JavaRenderEngine.this.activeapp!=null) {JavaRenderEngine.this.activeapp.drop(dtde);}}
	}

	private void setActiveApp(AppHandlerPanel activeappi) {
		int appcanvaswidth = defaultimagecanvaswidth; 
		int appcanvasheight = defaultimagecanvasheight; 
		if (this.activeapp!=null) {
			this.activeapp.removeMouseListener(this);
			this.activeapp.removeMouseMotionListener(this);
			this.activeapp.removeMouseWheelListener(this);
			this.activeapp.removeKeyListener(this);
			appcanvaswidth = this.activeapp.getWidth();
			appcanvasheight = this.activeapp.getHeight();
		}
		this.activeapp = activeappi;
		this.activeapp.addMouseListener(this);
		this.activeapp.addMouseMotionListener(this);
		this.activeapp.addMouseWheelListener(this);
		this.activeapp.addKeyListener(this);
		this.activeapp.setPreferredSize(new Dimension(appcanvaswidth,appcanvasheight));
		this.activeapp.setSize(appcanvaswidth,appcanvasheight);
		this.setContentPane(this.activeapp);
		this.pack();
		this.activeapp.requestFocusInWindow();
	}
	
	@Override public void actionPerformed(ActionEvent e) {
		if (this.activeapp!=null) {
			activeapp.lastticktime = activeapp.nowticktime;
			activeapp.nowticktime = System.currentTimeMillis();
			activeapp.diffticktime = (double)(activeapp.nowticktime - activeapp.lastticktime);
			activeapp.diffticktimesec = activeapp.diffticktime/1000.0f;
			this.activeapp.tick();
			Dimension componentsize=this.activeapp.getSize();
			Rectangle paintregion=new Rectangle(0,0,componentsize.width,componentsize.height);
			this.activeapp.paintImmediately(paintregion);
		}
	}
	@Override public void mouseWheelMoved(MouseWheelEvent e) {if (this.activeapp!=null) {this.activeapp.mouseWheelMoved(e);}}
	@Override public void mouseDragged(MouseEvent e) {if (this.activeapp!=null) {this.activeapp.mouseDragged(e);}}
	@Override public void mouseMoved(MouseEvent e) {if (this.activeapp!=null) {this.activeapp.mouseMoved(e);}}
	@Override public void mouseClicked(MouseEvent e) {if (this.activeapp!=null) {this.activeapp.mouseClicked(e);}}
	@Override public void mousePressed(MouseEvent e) {if (this.activeapp!=null) {this.activeapp.mousePressed(e);}}
	@Override public void mouseReleased(MouseEvent e) {if (this.activeapp!=null) {this.activeapp.mouseReleased(e);}}
	@Override public void mouseEntered(MouseEvent e) {if (this.activeapp!=null) {this.activeapp.mouseEntered(e);}}
	@Override public void mouseExited(MouseEvent e) {if (this.activeapp!=null) {this.activeapp.mouseExited(e);}}
	@Override public void keyTyped(KeyEvent e) {if (this.activeapp!=null) {this.activeapp.keyTyped(e);}}
	@Override public void keyReleased(KeyEvent e) {if (this.activeapp!=null) {this.activeapp.keyReleased(e);}}

	@Override public void keyPressed(KeyEvent e) {
	    int altonmask = KeyEvent.ALT_DOWN_MASK;
	    int altoffmask = KeyEvent.SHIFT_DOWN_MASK|KeyEvent.CTRL_DOWN_MASK;
	    boolean altdownmask = (e.getModifiersEx() & (altonmask | altoffmask)) == altonmask;
		if ((e.getKeyCode()==KeyEvent.VK_ALT)) {
			if (this.activeapp!=null) {this.activeapp.keyPressed(e);}
			e.consume();
		} else if ((e.getKeyCode()==KeyEvent.VK_F4)&&(altdownmask)) {
			System.out.println("JavaRenderEngine: main: keyPressed: ALT+VK_F4: closing software");
		} else if ((e.getKeyCode()==KeyEvent.VK_ENTER)&&(altdownmask)) {
			System.out.println("JavaRenderEngine: main: keyPressed: ALT+VK_ENTER");
	    	this.dispose();
	    	if (!windowedmode) {
	    		windowedmode = true;
	    		this.setExtendedState(this.getExtendedState()&~JFrame.MAXIMIZED_BOTH);
	    		this.setUndecorated(false);
	    		this.activeapp.setPreferredSize(new Dimension(defaultimagecanvaswidth,defaultimagecanvasheight));
	    		this.activeapp.setSize(defaultimagecanvaswidth,defaultimagecanvasheight);
	    		this.pack();
	    	}else {
	    		windowedmode = false;
	    		this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH);
	    		this.setUndecorated(true);
	    	}
	    	this.setVisible(true);
			this.activeapp.requestFocusInWindow();
		}else if (e.getKeyCode()==KeyEvent.VK_F5) {
			System.out.println("JavaRenderEngine: main: keyPressed: VK_F5");
			this.setActiveApp(this.drawapp);
		}else if (e.getKeyCode()==KeyEvent.VK_F6) {
			System.out.println("JavaRenderEngine: main: keyPressed: VK_F6");
			this.setActiveApp(this.cadapp);
		}else if (e.getKeyCode()==KeyEvent.VK_F7) {
			System.out.println("JavaRenderEngine: main: keyPressed: VK_F7");
			this.setActiveApp(this.modelapp);
		}else if (e.getKeyCode()==KeyEvent.VK_F8) {
			System.out.println("JavaRenderEngine: main: keyPressed: VK_F8");
			this.setActiveApp(this.editorapp);
		}else if (e.getKeyCode()==KeyEvent.VK_F9) {
			System.out.println("JavaRenderEngine: main: keyPressed: VK_F9");
			this.setActiveApp(this.gameapp);
		}else if (e.getKeyCode()==KeyEvent.VK_F10) {
			System.out.println("JavaRenderEngine: main: keyPressed: VK_F10");
			//TODO <tbd>
			e.consume();
		}else if (e.getKeyCode()==KeyEvent.VK_F11) {
			System.out.println("JavaRenderEngine: main: keyPressed: VK_F11");
			//TODO <tbd>
		}else if (e.getKeyCode()==KeyEvent.VK_F12) {
			System.out.println("JavaRenderEngine: main: keyPressed: VK_F12");
			BufferedImage componentimage = this.activeapp.gc.createCompatibleImage(this.activeapp.getWidth(),this.activeapp.getHeight(), Transparency.OPAQUE);
			Graphics2D gfx = componentimage.createGraphics();
			this.activeapp.paintComponent(gfx);
			gfx.dispose();
			File screenshotfile = new File("screenshot1.png");
			int screenshotnum = 1;
			while (screenshotfile.exists()) {
				screenshotnum += 1;
				screenshotfile = new File("screenshot"+screenshotnum+".png");
			}
			UtilLib.saveImageFormat(screenshotfile.getPath(), componentimage, new UtilLib.ImageFileFilters.PNGFileFilter());
		}else {
			if (this.activeapp!=null) {this.activeapp.keyPressed(e);}
		}
	}

}
