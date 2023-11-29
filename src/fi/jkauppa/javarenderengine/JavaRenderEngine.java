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
import fi.jkauppa.javarenderengine.MathLib.Matrix;
import fi.jkauppa.javarenderengine.MathLib.Plane;
import fi.jkauppa.javarenderengine.MathLib.Position;
import fi.jkauppa.javarenderengine.MathLib.Position2;
import fi.jkauppa.javarenderengine.MathLib.Triangle;
import fi.jkauppa.javarenderengine.ModelLib.Model;

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
		Position[] camposa=new Position[1]; camposa[0]=campos;
		Position[] campos2=new Position[2]; campos2[0]=new Position(1.0f,2.0f,3.0f); campos2[1]=new Position(1.0f,2.0f,3.0f);
		Position[] campos3=new Position[2]; campos3[0]=new Position(1.0f,0.0f,0.0f); campos3[1]=new Position(1.0f,0.0f,0.0f);
		Position[] campos4=new Position[3]; campos4[0]=new Position(1.0f,0.0f,0.0f); campos4[1]=new Position(1.0f,1.0f,0.0f); campos4[2]=new Position(1.0f,0.0f,1.0f);
		System.out.println("campos: "+campos.x+" "+campos.y+" "+campos.z);
		Direction[] camvec = MathLib.vectorFromPoints(campos2, campos3);
		for (int i=0;i<camvec.length;i++) {System.out.println("camvec: "+camvec[i].dx+" "+camvec[i].dy+" "+camvec[i].dz);}
		Direction[] camdir=new Direction[1]; camdir[0]=new Direction(1.0f,0.0f,0.0f);
		Direction[] camdir2=new Direction[2]; camdir2[0]=new Direction(1.0f,-1.0f,1.0f); camdir2[1]=new Direction(1.0f,-1.0f,1.0f);
		Direction[] camdir3=new Direction[2]; camdir3[0]=new Direction(1.0f,0.0f,0.0f); camdir3[1]=new Direction(1.0f,0.0f,0.0f);
		Direction[] camdir4=new Direction[1]; camdir4[0]=new Direction(0.0f,0.0f,1.0f);
		Direction[] camcross = MathLib.vectorCross(camdir2, camdir3);
		for (int i=0;i<camcross.length;i++) {System.out.println("camcross: "+camcross[i].dx+" "+camcross[i].dy+" "+camcross[i].dz);}
		double[] camdir2len = MathLib.vectorLength(camdir2);
		for (int i=0;i<camdir2len.length;i++) {System.out.println("camdir2len: "+camdir2len[i]);}
		Triangle[] ptri = new Triangle[1]; ptri[0]=new Triangle(campos,campos2[0],campos3[0]);
		Triangle[] ptri2 = new Triangle[3]; ptri2[0]=ptri[0]; ptri2[1]=ptri[0]; ptri2[2]=ptri[0];
		Triangle[] ptri3 = new Triangle[3]; ptri3[0]=new Triangle(campos4[0],campos4[1],campos4[2]); ptri3[1]=ptri3[0]; ptri3[2]=ptri3[0];
		Plane[] tpplane = MathLib.planeFromPoints(ptri3);
		System.out.println("tpplane: "+tpplane[0].a+" "+tpplane[0].b+" "+tpplane[0].c+" "+tpplane[0].d);
		double[] camdot = MathLib.vectorDot(camdir2, campos2);
		for (int i=0;i<camdot.length;i++) {System.out.println("camdot: "+camdot[i]+" "+camdot[i]+" "+camdot[i]);}
		Direction[] camdirnorm = MathLib.normalizeVector(camdir);
		Direction[] camdir2norm = MathLib.normalizeVector(camdir2);
		Direction[] camdir3norm = MathLib.normalizeVector(camdir3);
		Direction[] camdir4norm = MathLib.normalizeVector(camdir4);
		System.out.println("camdirnorm: "+camdirnorm[0].dx+" "+camdirnorm[0].dy+" "+camdirnorm[0].dz);
		for (int i=0;i<camdir2norm.length;i++) {System.out.println("camdir2norm: "+camdir2norm[i].dx+" "+camdir2norm[i].dy+" "+camdir2norm[i].dz);}
		Plane[] tplane=new Plane[1]; tplane[0]=new Plane(1.0f,0.0f,0.0f,-2.0f);
		Plane[] tplane2=new Plane[3]; tplane2[0]=new Plane(1.0f,0.0f,0.0f,-2.0f);tplane2[1]=new Plane(1.0f,0.0f,0.0f,-2.0f);tplane2[2]=new Plane(1.0f,0.0f,0.0f,-2.0f);
		double[][] cpdist = MathLib.rayPlaneDistance(campos, camdirnorm, tplane);
		double[][] cpdist2 = MathLib.rayPlaneDistance(campos, camdir2norm, tplane2);
		double[][] cpdist3 = MathLib.rayPlaneDistance(campos, camdirnorm, tpplane);
		System.out.println("cpdist[0][0]: "+cpdist[0][0]);
		for (int i=0;i<cpdist2.length;i++) {for (int j=0;j<cpdist2[i].length;j++) {System.out.println("cpdist2["+i+"]["+j+"]: "+cpdist2[i][j]);}}
		for (int i=0;i<cpdist3.length;i++) {for (int j=0;j<cpdist3[i].length;j++) {System.out.println("cpdist3["+i+"]["+j+"]: "+cpdist3[i][j]);}}
		Plane[] pplane = MathLib.planeFromNormalAtPoint(campos2, camdir2norm);
		Plane[] camplane = MathLib.planeFromNormalAtPoint(camposa, camdir4norm);
		for (int i=0;i<pplane.length;i++) {System.out.println("pplane: "+pplane[i].a+" "+pplane[i].b+" "+pplane[i].c+" "+pplane[i].d);}
		for (int i=0;i<camplane.length;i++) {System.out.println("camplane: "+camplane[i].a+" "+camplane[i].b+" "+camplane[i].c+" "+camplane[i].d);}
		double[] camdirang = MathLib.vectorAngle(camdir2, camdir3);
		for (int i=0;i<camdirang.length;i++) {System.out.println("camdirang: "+camdirang[i]);}
		Position[][] camrtint = MathLib.rayTriangleIntersection(campos, camdir3, ptri3);
		for (int i=0;i<camrtint.length;i++) {for (int j=0;j<camrtint[i].length;j++) {if(camrtint[i][j]!=null) {System.out.println("camrtint["+i+"]["+j+"]: "+camrtint[i][j].x+" "+camrtint[i][j].y+" "+camrtint[i][j].z);}else{System.out.println("camrtint["+i+"]["+j+"]: no hit.");}}}
		Position2[][] camptint = MathLib.planeTriangleIntersection(camplane, ptri3);
		for (int i=0;i<camptint.length;i++) {for (int j=0;j<camptint[i].length;j++) {if(camptint[i][j]!=null) {System.out.println("camptint["+i+"]["+j+"]: "+camptint[i][j].pos1.x+" "+camptint[i][j].pos1.y+" "+camptint[i][j].pos1.z+", "+camptint[i][j].pos2.x+" "+camptint[i][j].pos2.y+" "+camptint[i][j].pos2.z);}else{System.out.println("camptint["+i+"]["+j+"]: no hit.");}}}
		Matrix mat1 = new Matrix(1,0,0,0,1,0,0,0,1); Matrix mat2 = new Matrix(0.6124,0.6124,0.5000,0.3536,0.3536,-0.8660,-0.7071,0.7071,0);
		Matrix matout1 = MathLib.matrixMultiply(mat1, mat2);
		Matrix matout2 = MathLib.matrixMultiply(mat2, mat1);
		Matrix matout3 = MathLib.matrixMultiply(mat2, mat2);
		System.out.println("matout1: "+matout1.a11+" "+matout1.a12+" "+matout1.a13); System.out.println("matout1: "+matout1.a21+" "+matout1.a22+" "+matout1.a23); System.out.println("matout1: "+matout1.a31+" "+matout1.a32+" "+matout1.a33);
		System.out.println("matout2: "+matout2.a11+" "+matout2.a12+" "+matout2.a13); System.out.println("matout2: "+matout2.a21+" "+matout2.a22+" "+matout2.a23); System.out.println("matout2: "+matout2.a31+" "+matout2.a32+" "+matout2.a33);
		System.out.println("matout3: "+matout3.a11+" "+matout3.a12+" "+matout3.a13); System.out.println("matout3: "+matout3.a21+" "+matout3.a22+" "+matout3.a23); System.out.println("matout3: "+matout3.a31+" "+matout3.a32+" "+matout3.a33);
		Position[] campos4rotmat1 = MathLib.matrixMultiply(campos4, mat1);
		Position[] campos4rotmat2 = MathLib.matrixMultiply(campos4, mat2);
		for (int i=0;i<campos4rotmat1.length;i++) {System.out.println("campos4rotmat1: "+campos4rotmat1[i].x+" "+campos4rotmat1[i].y+" "+campos4rotmat1[i].z);}
		for (int i=0;i<campos4rotmat2.length;i++) {System.out.println("campos4rotmat2: "+campos4rotmat2[i].x+" "+campos4rotmat2[i].y+" "+campos4rotmat2[i].z);}
		Direction[] camdir2normrotmat1 = MathLib.matrixMultiply(camdir2norm, mat1);
		Direction[] camdir2normrotmat2 = MathLib.matrixMultiply(camdir2norm, mat2);
		for (int i=0;i<camdir2normrotmat1.length;i++) {System.out.println("camdir2normrotmat1: "+camdir2normrotmat1[i].dx+" "+camdir2normrotmat1[i].dy+" "+camdir2normrotmat1[i].dz);}
		for (int i=0;i<camdir2normrotmat2.length;i++) {System.out.println("camdir2normrotmat2: "+camdir2normrotmat2[i].dx+" "+camdir2normrotmat2[i].dy+" "+camdir2normrotmat2[i].dz);}
		Matrix matrot1 = MathLib.rotationMatrix(0, 0, 0);
		Matrix matrot2 = MathLib.rotationMatrix(90, 45, 30);
		System.out.println("matrot1: "+matrot1.a11+" "+matrot1.a12+" "+matrot1.a13); System.out.println("matrot1: "+matrot1.a21+" "+matrot1.a22+" "+matrot1.a23); System.out.println("matrot1: "+matrot1.a31+" "+matrot1.a32+" "+matrot1.a33);
		System.out.println("matrot2: "+matrot2.a11+" "+matrot2.a12+" "+matrot2.a13); System.out.println("matrot2: "+matrot2.a21+" "+matrot2.a22+" "+matrot2.a23); System.out.println("matrot2: "+matrot2.a31+" "+matrot2.a32+" "+matrot2.a33);
		
		String modelfilename = "res/models/testcubemodel4.obj";
		Model loadmodel = ModelLib.loadWaveFrontOBJFile(modelfilename);
		
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
			//TODO tools/color pop-up window
		}
		if (e.getKeyCode()==KeyEvent.VK_F5) {
			//TODO 2D image edit mode
		}
		if (e.getKeyCode()==KeyEvent.VK_F6) {
			//TODO CAD 2D edit mode
		}
		if (e.getKeyCode()==KeyEvent.VK_F7) {
			//TODO CAD 3D edit mode
		}
		if (e.getKeyCode()==KeyEvent.VK_F8) {
			//TODO Game edit mode
		}
		if (e.getKeyCode()==KeyEvent.VK_F9) {
			//TODO Game run mode
		}
		if (e.getKeyCode()==KeyEvent.VK_ADD) {
			this.pencilsize += 1;
			if (this.pencilsize<1) {
				this.pencilsize = 1;
			}
		}
		if (e.getKeyCode()==KeyEvent.VK_SUBTRACT) {
			this.pencilsize -= 1;
			if (this.pencilsize<1) {
				this.pencilsize = 1;
			}
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
					renderbuffergfx.fillRect(e.getX()-pencilwidth, e.getY()-pencilwidth, this.pencilsize, this.pencilsize);
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
