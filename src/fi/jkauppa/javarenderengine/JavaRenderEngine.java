package fi.jkauppa.javarenderengine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import fi.jkauppa.javarenderengine.MathLib.Direction;
import fi.jkauppa.javarenderengine.MathLib.Plane;
import fi.jkauppa.javarenderengine.MathLib.Position;

public class JavaRenderEngine extends JFrame implements KeyListener,MouseListener,MouseMotionListener,MouseWheelListener {
	private static final long serialVersionUID = 1L;
	private RenderPanel renderpanel = new RenderPanel();
	private boolean windowedmode = false;
	private Color drawcolor = Color.BLACK;
	private float[] drawcolorhsb = {0.0f, 1.0f, 0.0f};
	private int pencilsize = 1;
	private int pencilshape = 1;
	private BufferedImage loadimage = null;
	private JFileChooser filechooser = new JFileChooser();
	private ImageFileFilters.PNGFileFilter pngfilefilter = new ImageFileFilters.PNGFileFilter();
	private ImageFileFilters.JPGFileFilter jpgfilefilter = new ImageFileFilters.JPGFileFilter();
	private ImageFileFilters.GIFFileFilter giffilefilter = new ImageFileFilters.GIFFileFilter();
	private ImageFileFilters.BMPFileFilter bmpfilefilter = new ImageFileFilters.BMPFileFilter();
	private ImageFileFilters.WBMPFileFilter wbmpfilefilter = new ImageFileFilters.WBMPFileFilter();
	
	public JavaRenderEngine() {
		this.filechooser.addChoosableFileFilter(this.pngfilefilter);
		this.filechooser.addChoosableFileFilter(this.jpgfilefilter);
		this.filechooser.addChoosableFileFilter(this.giffilefilter);
		this.filechooser.addChoosableFileFilter(this.bmpfilefilter);
		this.filechooser.addChoosableFileFilter(this.wbmpfilefilter);
		this.filechooser.setFileFilter(pngfilefilter);
		
		this.addKeyListener(this);
		this.renderpanel.addMouseListener(this);
		this.renderpanel.addMouseMotionListener(this);
		this.renderpanel.addMouseWheelListener(this);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH);
		this.setUndecorated(true);
		this.setContentPane(this.renderpanel);
		this.setVisible(true);
	}

	public static void main(String[] args) {
		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());} catch (Exception ex) {}
		String userlocalpath = Paths.get("").toAbsolutePath().toString();
		String userlocaldir = System.getProperty("user.dir");
		String[] writeformatnames = ImageIO.getWriterFormatNames();
		String[] readformatnames = ImageIO.getReaderFormatNames();
		
		Position campos=new Position(0.0f,0.0f,0.0f);
		Position[] campos2=new Position[2]; campos2[0]=new Position(1.0f,2.0f,3.0f); campos2[1]=new Position(1.0f,2.0f,3.0f);
		System.out.println("campos: "+campos.x+" "+campos.y+" "+campos.z);
		Direction[] camdir=new Direction[1]; camdir[0]=new Direction(1.0f,0.0f,0.0f);
		Direction[] camdir2=new Direction[2]; camdir2[0]=new Direction(1.0f,-1.0f,1.0f); camdir2[1]=new Direction(1.0f,-1.0f,1.0f);
		double[] camdir2len = MathLib.vectorLength(camdir2);
		for (int i=0;i<camdir2len.length;i++) {System.out.println("camdir2len: "+camdir2len[i]);}
		double[] camdot = MathLib.vectorDot(camdir2, campos2);
		for (int i=0;i<camdot.length;i++) {System.out.println("camdot: "+camdot[i]+" "+camdot[i]+" "+camdot[i]);}
		camdir = MathLib.normalizeVector(camdir);
		camdir2 = MathLib.normalizeVector(camdir2);
		System.out.println("camdir: "+camdir[0].dx+" "+camdir[0].dy+" "+camdir[0].dz);
		for (int i=0;i<camdir2.length;i++) {System.out.println("camdir2: "+camdir2[i].dx+" "+camdir2[i].dy+" "+camdir2[i].dz);}
		Plane[] tplane=new Plane[1]; tplane[0]=new Plane(1.0f,0.0f,0.0f,-2.0f);
		Plane[] tplane2=new Plane[3]; tplane2[0]=new Plane(1.0f,0.0f,0.0f,-2.0f);tplane2[1]=new Plane(1.0f,0.0f,0.0f,-2.0f);tplane2[2]=new Plane(1.0f,0.0f,0.0f,-2.0f);
		double[][] cpdist = MathLib.rayPlaneDistance(campos, camdir, tplane);
		double[][] cpdist2 = MathLib.rayPlaneDistance(campos, camdir2, tplane2);
		System.out.println("cpdist["+cpdist.length+"]["+cpdist[0].length+"]: "+cpdist[0][0]);
		System.out.println("cpdist2["+cpdist2.length+"]["+cpdist2[0].length+"]: "+cpdist2[0][0]);
		for (int i=0;i<cpdist2.length;i++) {for (int j=0;j<cpdist2[i].length;j++) {System.out.println("cpdist2["+i+"]["+j+"]: "+cpdist2[i][j]);}}
		Plane[] pplane = MathLib.planeFromNormalAtPoint(campos2, camdir2);
		for (int i=0;i<pplane.length;i++) {System.out.println("pplane: "+pplane[i].a+" "+pplane[i].b+" "+pplane[i].c+" "+pplane[i].d);}
		
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
			BufferedImage oldimage = this.renderbuffer; 
			this.renderbuffer = new BufferedImage(this.getWidth(),this.getHeight(),BufferedImage.TYPE_INT_ARGB);
			Graphics2D gfx = (Graphics2D)this.renderbuffer.getGraphics();
			gfx.setColor(Color.WHITE);
			gfx.fillRect(0, 0, this.renderbuffer.getWidth(), this.renderbuffer.getHeight());
			if (oldimage!=null) {
				gfx.drawImage(oldimage, 0, 0, null);
			}
		}
	}
	
	public class ImageFileFilters  {
		public static class PNGFileFilter extends FileFilter {
			@Override public boolean accept(File f) {return (f.isDirectory())||(f.getName().endsWith(".png"));}
			@Override public String getDescription() {return "PNG Image file";}
		}
		public static class JPGFileFilter extends FileFilter {
			@Override public boolean accept(File f) {return (f.isDirectory())||(f.getName().endsWith(".jpg"));}
			@Override public String getDescription() {return "JPG Image file";}
		}
		public static class GIFFileFilter extends FileFilter {
			@Override public boolean accept(File f) {return (f.isDirectory())||(f.getName().endsWith(".gif"));}
			@Override public String getDescription() {return "GIF Image file";}
		}
		public static class BMPFileFilter extends FileFilter {
			@Override public boolean accept(File f) {return (f.isDirectory())||(f.getName().endsWith(".bmp"));}
			@Override public String getDescription() {return "BMP Image file";}
		}
		public static class WBMPFileFilter extends FileFilter {
			@Override public boolean accept(File f) {return (f.isDirectory())||(f.getName().endsWith(".wbmp"));}
			@Override public String getDescription() {return "WBMP Image file";}
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
			this.filechooser.setDialogTitle("Save File");
			this.filechooser.setApproveButtonText("Save");
			if (this.filechooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION) {
				File savefile = this.filechooser.getSelectedFile();
				FileFilter savefileformat = this.filechooser.getFileFilter();
				if (savefileformat.equals(this.pngfilefilter)) {
					try {ImageIO.write(renderpanel.getRenderBuffer(), "PNG", savefile);} catch (Exception ex) {ex.printStackTrace();}
				} else if (savefileformat.equals(this.jpgfilefilter)) {
					try {ImageIO.write(renderpanel.getRenderBuffer(), "JPG", savefile);} catch (Exception ex) {ex.printStackTrace();}
				} else if (savefileformat.equals(this.giffilefilter)) {
					try {ImageIO.write(renderpanel.getRenderBuffer(), "GIF", savefile);} catch (Exception ex) {ex.printStackTrace();}
				} else if (savefileformat.equals(this.bmpfilefilter)) {
					try {ImageIO.write(renderpanel.getRenderBuffer(), "BMP", savefile);} catch (Exception ex) {ex.printStackTrace();}
				} else if (savefileformat.equals(this.wbmpfilefilter)) {
					try {ImageIO.write(renderpanel.getRenderBuffer(), "WBMP", savefile);} catch (Exception ex) {ex.printStackTrace();}
				} else {
					try {ImageIO.write(renderpanel.getRenderBuffer(), "PNG", savefile);} catch (Exception ex) {ex.printStackTrace();}
				}
			}
		}
		if (e.getKeyCode()==KeyEvent.VK_F3) {
			this.filechooser.setDialogTitle("Load File");
			this.filechooser.setApproveButtonText("Load");
			if (this.filechooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION) {
				File loadfile = this.filechooser.getSelectedFile() ;
				try {loadimage = ImageIO.read(loadfile);} catch (Exception ex) {ex.printStackTrace();}
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
		    int onmask3 = MouseEvent.BUTTON3_DOWN_MASK;
		    int offmask3 = 0;
		    boolean mouse1down = ((e.getModifiersEx() & (onmask1 | offmask1)) == onmask1);
		    boolean mouse3down = ((e.getModifiersEx() & (onmask3 | offmask3)) == onmask3);
		    if (mouse1down||mouse3down) {
		    	if (mouse3down) {
					renderbuffergfx.setColor(Color.WHITE);
		    	} else {
					renderbuffergfx.setColor(this.drawcolor);
		    	}
				if (this.pencilshape==1) {
					renderbuffergfx.fillRect(e.getX()-pencilwidth, e.getY()-pencilwidth, this.pencilsize, this.pencilsize);
				} else if (this.pencilshape==2) {
					renderbuffergfx.fillOval(e.getX()-pencilwidth, e.getY()-pencilwidth, this.pencilsize, this.pencilsize);
				} else if (this.pencilshape==3) {
					renderbuffergfx.drawRect(e.getX()-pencilwidth, e.getY()-pencilwidth, this.pencilsize, this.pencilsize);
				} else if (this.pencilshape==4) {
					renderbuffergfx.drawOval(e.getX()-pencilwidth, e.getY()-pencilwidth, this.pencilsize, this.pencilsize);
				}else {
					renderbuffergfx.fillOval(e.getX()-pencilwidth, e.getY()-pencilwidth, this.pencilsize, this.pencilsize);
				}
			}			
		    int onmask2 = MouseEvent.BUTTON2_DOWN_MASK;
		    int offmask2 = MouseEvent.SHIFT_DOWN_MASK;
		    boolean mouse2down = ((e.getModifiersEx() & (onmask2 | offmask2)) == onmask2);
		    if (mouse2down) {
		    	this.pencilshape += 1;
		    	if (this.pencilshape>4) {
		    		this.pencilshape = 1;
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
