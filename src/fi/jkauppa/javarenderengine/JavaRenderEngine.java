package fi.jkauppa.javarenderengine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.Transparency;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
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
import java.awt.image.VolatileImage;
import java.io.File;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.TransferHandler;
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
	private AppHandler activeapp = null;
	private String userlocalpath = Paths.get("").toAbsolutePath().toString();
	private String userlocaldir = System.getProperty("user.dir");
	private String[] writeformatnames = ImageIO.getWriterFormatNames();
	private String[] readformatnames = ImageIO.getReaderFormatNames();
	private GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment ();
	private GraphicsDevice gd = ge.getDefaultScreenDevice ();
	private GraphicsConfiguration gc = gd.getDefaultConfiguration ();
	private int imagecanvaswidth = 1920;
	private int imagecanvasheight= 1080;
	private RenderPanel renderpanel = new RenderPanel(imagecanvaswidth,imagecanvasheight);
	private boolean windowedmode = true;
	private Color drawcolor = Color.BLACK;
	private float[] drawcolorhsb = {0.0f, 1.0f, 0.0f};
	private Color erasecolor = new Color(1.0f,1.0f,1.0f,0.0f);
	private int pencilsize = 1;
	private int pencilshape = 1;
	private boolean penciloverridemode = false;
	private float penciltransparency = 1.0f;
	private VolatileImage pencilbuffer = null;
	private int oldpencilsize = 1;
	private int mousestartlocationx = -1, mousestartlocationy = -1;  
	private int mouselastlocationx = -1, mouselastlocationy = -1;  
	private int mouselocationx = -1, mouselocationy = -1;
	private boolean drawlinemode = false;
	private JFileChooser filechooser = new JFileChooser();
	private ImageFileFilters.PNGFileFilter pngfilefilter = new ImageFileFilters.PNGFileFilter();
	private ImageFileFilters.JPGFileFilter jpgfilefilter = new ImageFileFilters.JPGFileFilter();
	private ImageFileFilters.GIFFileFilter giffilefilter = new ImageFileFilters.GIFFileFilter();
	private ImageFileFilters.BMPFileFilter bmpfilefilter = new ImageFileFilters.BMPFileFilter();
	private ImageFileFilters.WBMPFileFilter wbmpfilefilter = new ImageFileFilters.WBMPFileFilter();
	private DragAndDropClipBoardHandler dndcbhandler = new DragAndDropClipBoardHandler();
	private DropTargetHandler droptargethandler = new DropTargetHandler();
	
	public JavaRenderEngine() {
		this.setActiveApp(new DrawApp());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		if (!windowedmode) {
			this.setUndecorated(true);
			this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH);
		}else {
			this.setLocationByPlatform(true);
		}
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
		this.renderpanel.setTransferHandler(dndcbhandler);
		this.renderpanel.setDropTarget(droptargethandler);
		this.renderpanel.setPreferredSize(new Dimension(this.imagecanvaswidth,this.imagecanvasheight));
		this.renderpanel.setSize(this.imagecanvaswidth,this.imagecanvasheight);
		this.setContentPane(renderpanel);
		this.pack();
		this.setVisible(true);
	}

	public static void main(String[] args) {
		System.setProperty("sun.java2d.opengl", "true");
		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());} catch (Exception ex) {}
		
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
		private VolatileImage renderbuffer = null;
		private VolatileImage dragbuffer = null;
		private TexturePaint bgpattern = null;
		public RenderPanel(int imagewidth, int imageheight) {
			this.renderbuffer = gc.createCompatibleVolatileImage(imagewidth, imageheight, Transparency.TRANSLUCENT);
			this.dragbuffer = gc.createCompatibleVolatileImage(imagewidth,imageheight,Transparency.TRANSLUCENT);
			Graphics2D gfx = this.renderbuffer.createGraphics();
			gfx.setComposite(AlphaComposite.Clear);
			gfx.fillRect(0, 0, imagewidth, imageheight);
			BufferedImage bgpatternimage = gc.createCompatibleImage(64, 64, Transparency.OPAQUE);
			Graphics2D pgfx = bgpatternimage.createGraphics();
			pgfx.setColor(Color.WHITE);
			pgfx.fillRect(0, 0, bgpatternimage.getWidth(), bgpatternimage.getHeight());
			pgfx.setColor(Color.BLACK);
			pgfx.drawLine(31, 0, 31, 63);
			pgfx.drawLine(0, 31, 63, 31);
			pgfx.dispose();
			this.bgpattern = new TexturePaint(bgpatternimage,new Rectangle(0, 0, 64, 64));
			this.addComponentListener(this);
			timer.start();
		}
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			long newupdate = System.currentTimeMillis();
			long ticktime = newupdate-lastupdate;
			double ticktimefps = 1000.0f/(double)ticktime;
			lastupdate = newupdate;
			Graphics2D g2 = (Graphics2D)g;
			if (renderbuffer!=null) {
				g2.setPaint(this.bgpattern);
				g2.fillRect(0, 0, renderbuffer.getWidth(), renderbuffer.getHeight());
				g2.setPaint(null);
				g2.drawImage(renderbuffer, 0, 0, null);
	    		if ((JavaRenderEngine.this.penciloverridemode)&&(JavaRenderEngine.this.pencilbuffer!=null)) {
	        		double pencilsizescalefactor = ((double)JavaRenderEngine.this.pencilsize)/((double)JavaRenderEngine.this.pencilbuffer.getWidth());
	    			g2.setComposite(AlphaComposite.Src);
					g2.setPaint(this.bgpattern);
					g2.fillRect(JavaRenderEngine.this.mouselocationx-(int)Math.round((double)JavaRenderEngine.this.pencilbuffer.getWidth()*pencilsizescalefactor/2.0f), JavaRenderEngine.this.mouselocationy-(int)Math.round((double)JavaRenderEngine.this.pencilbuffer.getHeight()*pencilsizescalefactor/2.0f),(int)Math.round(JavaRenderEngine.this.pencilbuffer.getWidth()*pencilsizescalefactor),(int)Math.round(JavaRenderEngine.this.pencilbuffer.getHeight()*pencilsizescalefactor));
	    		}
    			if (JavaRenderEngine.this.drawlinemode) {
    				JavaRenderEngine.this.drawPencilLine(g2, JavaRenderEngine.this.mousestartlocationx, JavaRenderEngine.this.mousestartlocationy, JavaRenderEngine.this.mouselocationx, JavaRenderEngine.this.mouselocationy, false, false);
    			} else {
    				JavaRenderEngine.this.drawPencil(g2, JavaRenderEngine.this.mouselocationx, JavaRenderEngine.this.mouselocationy, false, false);
				}
			}
		}
		@Override
		public void actionPerformed(ActionEvent e) {this.repaint();}
		public VolatileImage getRenderBuffer() {return renderbuffer;}
		public VolatileImage getDragBuffer() {return dragbuffer;}
		public void setRenderBuffer(VolatileImage renderbufferi) {this.renderbuffer=renderbufferi;}
		
		@Override public void componentMoved(ComponentEvent e) {}
		@Override public void componentShown(ComponentEvent e) {}
		@Override public void componentHidden(ComponentEvent e) {}
		@Override public void componentResized(ComponentEvent e) {
			VolatileImage oldimage = this.renderbuffer;
			this.renderbuffer = gc.createCompatibleVolatileImage(this.getWidth(),this.getHeight(), Transparency.TRANSLUCENT);
			this.dragbuffer = gc.createCompatibleVolatileImage(this.getWidth(),this.getHeight(), Transparency.TRANSLUCENT);
			Graphics2D gfx = this.renderbuffer.createGraphics();
			gfx.setComposite(AlphaComposite.Clear);
			gfx.fillRect(0, 0, this.getWidth(),this.getHeight());
			if (oldimage!=null) {
				gfx.setComposite(AlphaComposite.Src);
				gfx.drawImage(oldimage, 0, 0, null);
			}
			gfx.dispose();
		}
	}
	
	private void setActiveApp(AppHandler activeappi) {this.activeapp = activeappi;}
	
	private interface AppHandler extends ActionListener,ComponentListener,KeyListener,MouseListener,MouseMotionListener,MouseWheelListener {}
	private class DrawApp implements AppHandler {

		@Override
		public void actionPerformed(ActionEvent e) {
		}

		@Override
		public void componentResized(ComponentEvent e) {
		}

		@Override
		public void componentMoved(ComponentEvent e) {
		}

		@Override
		public void componentShown(ComponentEvent e) {
		}

		@Override
		public void componentHidden(ComponentEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mouseDragged(MouseEvent e) {
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
		}
	}
	
	private class ImageFileFilters  {
		public static class PNGFileFilter extends FileFilter {
			@Override public boolean accept(File f) {return (f.isDirectory())||(f.getName().endsWith(".png"));}
			@Override public String getDescription() {return "PNG Image file";}
		}
		public static class JPGFileFilter extends FileFilter {
			@Override public boolean accept(File f) {return (f.isDirectory())||(f.getName().endsWith(".jpg"))||(f.getName().endsWith(".jpeg"));}
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
	private class DragAndDropClipBoardHandler extends TransferHandler {
		private static final long serialVersionUID = 1L;
        public boolean canImport(TransferHandler.TransferSupport info) {System.out.println("canImport");return false;}
        public boolean importData(TransferHandler.TransferSupport info) {System.out.println("importData");return false;}
        public int getSourceActions(JComponent c) {System.out.println("getSourceActions");return COPY;}
        protected Transferable createTransferable(JComponent c) {System.out.println("createTransferable");return null;}
	}
	private class DropTargetHandler extends DropTarget {
		private static final long serialVersionUID = 1L;
		@Override public synchronized void drop(DropTargetDropEvent dtde) {System.out.println("drop");dtde.rejectDrop();}
	}

	private void drawPencil(Graphics2D g, int mousex, int mousey, boolean erasemode, boolean overridemode) {
		g.setComposite(AlphaComposite.SrcOver);
		g.setPaint(null);
		g.setColor(null);
		int pencilwidth = (int)Math.ceil((double)(this.pencilsize-1)/2.0f);
    	if (this.pencilbuffer!=null) {
	    	if (erasemode) {
	    		if (overridemode) {
	    			g.setComposite(AlphaComposite.Clear);
	    		} else {
	    			g.setComposite(AlphaComposite.DstOut);
	    		}
	    	} else {
	    		if (overridemode) {
	    			g.setComposite(AlphaComposite.Src);
	    		}
    		}
    		double pencilsizescalefactor = ((double)this.pencilsize)/((double)this.pencilbuffer.getWidth());
    		g.drawImage(JavaRenderEngine.this.pencilbuffer, mousex-(int)Math.round((double)JavaRenderEngine.this.pencilbuffer.getWidth()*pencilsizescalefactor/2.0f), mousey-(int)Math.round((double)JavaRenderEngine.this.pencilbuffer.getHeight()*pencilsizescalefactor/2.0f),(int)Math.round(JavaRenderEngine.this.pencilbuffer.getWidth()*pencilsizescalefactor),(int)Math.round(JavaRenderEngine.this.pencilbuffer.getHeight()*pencilsizescalefactor),null);
    	} else {
	    	if (erasemode) {
	    		g.setComposite(AlphaComposite.Src);
	    		g.setColor(this.erasecolor);
	    	} else {
	    		if (overridemode) {
		    		g.setComposite(AlphaComposite.Src);
	    		}
    			g.setColor(this.drawcolor);
	    	}
			if (this.pencilshape==2) {
				g.fillRoundRect(mousex-pencilwidth, mousey-pencilwidth, this.pencilsize, this.pencilsize, 5, 5);
			} else if (this.pencilshape==3) {
				g.fillOval(mousex-pencilwidth, mousey-pencilwidth, this.pencilsize, this.pencilsize);
			} else if (this.pencilshape==4) {
				g.drawRect(mousex-pencilwidth, mousey-pencilwidth, this.pencilsize, this.pencilsize);
			} else if (this.pencilshape==5) {
				g.drawRoundRect(mousex-pencilwidth, mousey-pencilwidth, this.pencilsize, this.pencilsize, 5, 5);
			} else if (this.pencilshape==6) {
				g.drawOval(mousex-pencilwidth, mousey-pencilwidth, this.pencilsize, this.pencilsize);
			}else {
				g.fillRect(mousex-pencilwidth, mousey-pencilwidth, this.pencilsize, this.pencilsize);
			}
    	}
	}
	private void drawPencilLine(Graphics2D g, int mousestartx, int mousestarty, int mousex, int mousey, boolean erasemode, boolean overridemode) {
		double linedistx = mousex-mousestartx;
		double linedisty = mousey-mousestarty;
		int linestepnum = (int)Math.ceil(Math.sqrt(linedistx*linedistx+linedisty*linedisty))+1;
		double linestepx = linedistx/linestepnum;
		double linestepy = linedisty/linestepnum;
		for (int i=0;i<linestepnum;i++) {
			int drawposx = (int)Math.round(this.mousestartlocationx + i*linestepx);
			int drawposy = (int)Math.round(this.mousestartlocationy + i*linestepy);
	    	this.drawPencil(g, drawposx, drawposy, erasemode, overridemode);
		}
	}
	
	@Override public void keyTyped(KeyEvent e) {}
	@Override public void keyReleased(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}
		if (e.getKeyCode()==KeyEvent.VK_ENTER) {
		    int onmask = KeyEvent.ALT_DOWN_MASK;
		    int offmask = KeyEvent.SHIFT_DOWN_MASK|KeyEvent.CTRL_DOWN_MASK;
		    boolean enteraltdown = (e.getModifiersEx() & (onmask | offmask)) == onmask;
		    if (enteraltdown) {
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
		    int onmaska = 0;
		    int offmaska = KeyEvent.ALT_DOWN_MASK|KeyEvent.SHIFT_DOWN_MASK|KeyEvent.CTRL_DOWN_MASK;
		    boolean enterdown = (e.getModifiersEx() & (onmaska | offmaska)) == onmaska;
		    if(enterdown) {
		    	this.penciloverridemode = !this.penciloverridemode;
		    }
		}
		if (e.getKeyCode()==KeyEvent.VK_BACK_SPACE) {
			VolatileImage renderbufferhandle = renderpanel.getRenderBuffer();
			if (renderbufferhandle!=null) {
				Graphics2D gfx = renderbufferhandle.createGraphics();
				gfx.setComposite(AlphaComposite.Src);
				gfx.setColor(this.erasecolor);
				gfx.fillRect(0, 0, renderbufferhandle.getWidth(), renderbufferhandle.getHeight());
				gfx.dispose();
			}
		}
		if (e.getKeyCode()==KeyEvent.VK_INSERT) {
			this.drawcolorhsb[0] += 0.01f;
			if (this.drawcolorhsb[0]>1.0f) {this.drawcolorhsb[0] = 0.0f;}
			Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		}
		if (e.getKeyCode()==KeyEvent.VK_DELETE) {
			this.drawcolorhsb[0] -= 0.01f;
			if (this.drawcolorhsb[0]<0.0f) {this.drawcolorhsb[0] = 1.0f;}
			Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		}
		if (e.getKeyCode()==KeyEvent.VK_HOME) {
			this.drawcolorhsb[1] += 0.01f;
			if (this.drawcolorhsb[1]>1.0f) {this.drawcolorhsb[1] = 1.0f;}
			Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		}
		if (e.getKeyCode()==KeyEvent.VK_END) {
			this.drawcolorhsb[1] -= 0.01f;
			if (this.drawcolorhsb[1]<0.0f) {this.drawcolorhsb[1] = 0.0f;}
			Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		}
		if (e.getKeyCode()==KeyEvent.VK_PAGE_UP) {
			this.drawcolorhsb[2] += 0.01f;
			if (this.drawcolorhsb[2]>1.0f) {this.drawcolorhsb[2] = 1.0f;}
			Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		}
		if (e.getKeyCode()==KeyEvent.VK_PAGE_DOWN) {
			this.drawcolorhsb[2] -= 0.01f;
			if (this.drawcolorhsb[2]<0.0f) {this.drawcolorhsb[2] = 0.0f;}
			Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
			float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
			this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		}
		if (e.getKeyCode()==KeyEvent.VK_ADD) {
			this.pencilsize += 1;
		}
		if (e.getKeyCode()==KeyEvent.VK_SUBTRACT) {
			this.pencilsize -= 1;
			if (this.pencilsize<1) {this.pencilsize = 1;}
		}
		if (e.getKeyCode()==KeyEvent.VK_DIVIDE) {
	    	this.pencilshape -= 1;
	    	if (this.pencilshape<1) {this.pencilshape = 6;}
	    	if (this.pencilbuffer!=null) {
	    		this.pencilbuffer = null;
	    		this.pencilsize = this.oldpencilsize;
	    	}
		}
		if (e.getKeyCode()==KeyEvent.VK_MULTIPLY) {
	    	this.pencilshape += 1;
	    	if (this.pencilshape>6) {this.pencilshape = 1;}
	    	if (this.pencilbuffer!=null) {
	    		this.pencilbuffer = null;
	    		this.pencilsize = this.oldpencilsize;
	    	}
		}
		if (e.getKeyCode()==KeyEvent.VK_NUMPAD9) {
			this.penciltransparency += 0.01f;
			if (this.penciltransparency>1.0f) {this.penciltransparency = 1.0f;}
			float[] colorvalues = this.drawcolor.getRGBColorComponents(new float[3]);
			this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		}
		if (e.getKeyCode()==KeyEvent.VK_NUMPAD8) {
			this.penciltransparency -= 0.01f;
			if (this.penciltransparency<0.0f) {this.penciltransparency = 0.0f;}
			float[] colorvalues = this.drawcolor.getRGBColorComponents(new float[3]);
			this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
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
				if (savefileformat.equals(this.jpgfilefilter)) {
					try {ImageIO.write(renderpanel.getRenderBuffer().getSnapshot(), "JPG", savefile);} catch (Exception ex) {ex.printStackTrace();}
				} else if (savefileformat.equals(this.giffilefilter)) {
					try {ImageIO.write(renderpanel.getRenderBuffer().getSnapshot(), "GIF", savefile);} catch (Exception ex) {ex.printStackTrace();}
				} else if (savefileformat.equals(this.bmpfilefilter)) {
					try {ImageIO.write(renderpanel.getRenderBuffer().getSnapshot(), "BMP", savefile);} catch (Exception ex) {ex.printStackTrace();}
				} else if (savefileformat.equals(this.wbmpfilefilter)) {
					try {ImageIO.write(renderpanel.getRenderBuffer().getSnapshot(), "WBMP", savefile);} catch (Exception ex) {ex.printStackTrace();}
				} else {
					try {ImageIO.write(renderpanel.getRenderBuffer().getSnapshot(), "PNG", savefile);} catch (Exception ex) {ex.printStackTrace();}
				}
			}
		}
		if (e.getKeyCode()==KeyEvent.VK_F3) {
			this.filechooser.setDialogTitle("Load File");
			this.filechooser.setApproveButtonText("Load");
			if (this.filechooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION) {
				File loadfile = this.filechooser.getSelectedFile();
				BufferedImage loadimage = null;
				try {loadimage=ImageIO.read(loadfile);} catch (Exception ex) {ex.printStackTrace();}
				if (loadimage!=null) {
				    int onmask = KeyEvent.SHIFT_DOWN_MASK;
				    int offmask = KeyEvent.ALT_DOWN_MASK|KeyEvent.CTRL_DOWN_MASK;
				    boolean f3shiftdown = (e.getModifiersEx() & (onmask | offmask)) == onmask;
				    if (f3shiftdown) {
				    	this.oldpencilsize = this.pencilsize;
						this.pencilsize = loadimage.getWidth();
						VolatileImage loadimagevolatile = gc.createCompatibleVolatileImage(loadimage.getWidth(), loadimage.getHeight(), Transparency.TRANSLUCENT);
						Graphics2D loadimagevolatilegfx = loadimagevolatile.createGraphics();
						loadimagevolatilegfx.setComposite(AlphaComposite.Src);
						loadimagevolatilegfx.drawImage(loadimage, 0, 0, null);
						loadimagevolatilegfx.dispose();
				    	this.pencilbuffer = loadimagevolatile;
				    	this.pencilbuffer.setAccelerationPriority(1.0f);
				    }else{
						if (this.windowedmode) {
							VolatileImage loadimagevolatile = gc.createCompatibleVolatileImage(loadimage.getWidth(), loadimage.getHeight(), Transparency.TRANSLUCENT);
							Graphics2D loadimagevolatilegfx = loadimagevolatile.createGraphics();
							loadimagevolatilegfx.setComposite(AlphaComposite.Src);
							loadimagevolatilegfx.drawImage(loadimage, 0, 0, null);
							loadimagevolatilegfx.dispose();
					    	this.renderpanel.setRenderBuffer(loadimagevolatile);
							this.renderpanel.setPreferredSize(new Dimension(loadimage.getWidth(),loadimage.getHeight()));
							this.renderpanel.setSize(loadimage.getWidth(),loadimage.getHeight());
							this.pack();
						}else {
							VolatileImage loadimagevolatile = gc.createCompatibleVolatileImage(this.renderpanel.getWidth(), this.renderpanel.getHeight(), Transparency.TRANSLUCENT);
							Graphics2D loadimagevolatilegfx = loadimagevolatile.createGraphics();
							loadimagevolatilegfx.drawImage(loadimage, 0, 0, null);
							loadimagevolatilegfx.dispose();
					    	this.renderpanel.setRenderBuffer(loadimagevolatile);
						}
				    }
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
			//TODO Java code edit mode
		}
		if (e.getKeyCode()==KeyEvent.VK_F9) {
			//TODO Game run mode
		}
		if (e.getKeyCode()==KeyEvent.VK_F10) {
			//TODO <tbd>
		}
		if (e.getKeyCode()==KeyEvent.VK_F11) {
			//TODO <tbd>
		}
		if (e.getKeyCode()==KeyEvent.VK_F12) {
			//TODO Save screen shot
		}
	}

	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	
	@Override public void mouseMoved(MouseEvent e) {this.mouselocationx=e.getX();this.mouselocationy=e.getY();}
	@Override public void mousePressed(MouseEvent e) {this.mouselocationx=e.getX();this.mouselocationy=e.getY();this.mousestartlocationx=this.mouselocationx;this.mousestartlocationy=this.mouselocationy;mouseDragged(e);}
	@Override public void mouseReleased(MouseEvent e) {
		VolatileImage renderbufferhandle = this.renderpanel.getRenderBuffer();
		if (renderbufferhandle!=null) {
			Graphics2D renderbuffergfx = renderbufferhandle.createGraphics();
			renderbuffergfx.setColor(JavaRenderEngine.this.drawcolor);
		    boolean mouse1up = e.getButton()==MouseEvent.BUTTON1;
		    boolean mouse3up = e.getButton()==MouseEvent.BUTTON3;
			if (mouse1up||mouse3up) {
				if (this.drawlinemode) {
					this.drawlinemode=false;
					drawPencilLine(renderbuffergfx, this.mousestartlocationx, this.mousestartlocationy, this.mouselocationx, this.mouselocationy, mouse3up, this.penciloverridemode);
			    	renderbufferhandle.contentsLost();
				}
			}
			renderbuffergfx.dispose();
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		this.mouselastlocationx=this.mouselocationx;this.mouselastlocationy=this.mouselocationy;
		this.mouselocationx=e.getX();this.mouselocationy=e.getY();
    	int mousedeltax = this.mouselocationx - this.mouselastlocationx; 
    	int mousedeltay = this.mouselocationy - this.mouselastlocationy;
		VolatileImage renderbufferhandle = renderpanel.getRenderBuffer();
		if (renderbufferhandle!=null) {
			Graphics2D renderbuffergfx = renderbufferhandle.createGraphics();
		    int onmask1 = MouseEvent.BUTTON1_DOWN_MASK;
		    int offmask1 = MouseEvent.SHIFT_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
		    int onmask3 = MouseEvent.BUTTON3_DOWN_MASK;
		    int offmask3 = MouseEvent.SHIFT_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
		    boolean mouse1down = ((e.getModifiersEx() & (onmask1 | offmask1)) == onmask1);
		    boolean mouse3down = ((e.getModifiersEx() & (onmask3 | offmask3)) == onmask3);
		    if (mouse1down||mouse3down) {
		    	this.drawPencil(renderbuffergfx, e.getX(), e.getY(), mouse3down, this.penciloverridemode);
		    	renderbufferhandle.contentsLost();
			}			
		    int onmask1c = MouseEvent.BUTTON1_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
		    int offmask1c = MouseEvent.SHIFT_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
		    int onmask3c = MouseEvent.BUTTON3_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
		    int offmask3c = MouseEvent.SHIFT_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
		    boolean mouse1altdown = ((e.getModifiersEx() & (onmask1c | offmask1c)) == onmask1c);
		    boolean mouse3altdown = ((e.getModifiersEx() & (onmask3c | offmask3c)) == onmask3c);
		    if (mouse1altdown||mouse3altdown) {
		    	this.drawlinemode = true;
		    }
		    int onmask1a = MouseEvent.BUTTON1_DOWN_MASK|MouseEvent.SHIFT_DOWN_MASK;
		    int offmask1a = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
		    boolean mouse1shiftdown = ((e.getModifiersEx() & (onmask1a | offmask1a)) == onmask1a);
		    if (mouse1shiftdown) {
				int colorvalue = renderbufferhandle.getSnapshot().getRGB(e.getX(), e.getY());
				Color pickeddrawcolor = new Color(colorvalue);
				this.drawcolorhsb = Color.RGBtoHSB(pickeddrawcolor.getRed(), pickeddrawcolor.getGreen(), pickeddrawcolor.getBlue(), new float[3]);
				float[] colorvalues = pickeddrawcolor.getRGBColorComponents(new float[3]);
				this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
		    }
		    int onmask1b = MouseEvent.BUTTON1_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
		    int offmask1b = MouseEvent.SHIFT_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
		    boolean mouse1controldown = ((e.getModifiersEx() & (onmask1b | offmask1b)) == onmask1b);
		    if (mouse1controldown) {
		    	//TODO select canvas region
		    }
		    int onmask2 = MouseEvent.BUTTON2_DOWN_MASK;
		    int offmask2 = MouseEvent.SHIFT_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
		    boolean mouse2down = ((e.getModifiersEx() & (onmask2 | offmask2)) == onmask2);
		    if (mouse2down) {
		    	//TODO <tbd>
		    }
		    int onmask2a = MouseEvent.BUTTON2_DOWN_MASK|MouseEvent.SHIFT_DOWN_MASK;
		    int offmask2a = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
		    boolean mouse2shiftdown = ((e.getModifiersEx() & (onmask2a | offmask2a)) == onmask2a);
		    if (mouse2shiftdown) {
		    	VolatileImage dragimage = JavaRenderEngine.this.renderpanel.getDragBuffer();
		    	Graphics2D dragimagegfx = dragimage.createGraphics();
		    	dragimagegfx.setComposite(AlphaComposite.Clear);
		    	dragimagegfx.fillRect(0, 0, renderbufferhandle.getWidth(), renderbufferhandle.getHeight());
		    	dragimagegfx.setComposite(AlphaComposite.Src);
		    	dragimagegfx.drawImage(renderbufferhandle, mousedeltax, mousedeltay, null);
		    	dragimagegfx.dispose();
		    	renderbuffergfx.setComposite(AlphaComposite.Clear);
		    	renderbuffergfx.fillRect(0, 0, renderbufferhandle.getWidth(), renderbufferhandle.getHeight());
		    	renderbuffergfx.setComposite(AlphaComposite.Src);
		    	renderbuffergfx.drawImage(dragimage, 0, 0, null);
		    	renderbufferhandle.contentsLost();
		    }
	    	renderbuffergfx.dispose();
		}
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
	    int onmask4 = 0;
	    int offmask4 = MouseEvent.SHIFT_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    boolean mousewheeldown = ((e.getModifiersEx() & (onmask4 | offmask4)) == onmask4);
	    if (mousewheeldown) {
			this.pencilsize += e.getWheelRotation();
			if (this.pencilsize<1) {
				this.pencilsize = 1;
			}
	    }
	    int onmask4a = MouseEvent.CTRL_DOWN_MASK;
	    int offmask4a = MouseEvent.SHIFT_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    boolean mousewheelctrldown = ((e.getModifiersEx() & (onmask4a | offmask4a)) == onmask4a);
	    if (mousewheelctrldown) {
	    	this.drawcolorhsb[0] += 0.01f*e.getWheelRotation();
	    	if (this.drawcolorhsb[0]>1.0f) {this.drawcolorhsb[0] = 0.0f;}
	    	else if (this.drawcolorhsb[0]<0.0f) {this.drawcolorhsb[0] = 1.0f;}
	    	Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
	    	float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
	    	this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
	    }
	    int onmask4b = MouseEvent.ALT_DOWN_MASK;
	    int offmask4b = MouseEvent.SHIFT_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
	    boolean mousewheelaltdown = ((e.getModifiersEx() & (onmask4b | offmask4b)) == onmask4b);
	    if (mousewheelaltdown) {
	    	this.drawcolorhsb[2] += 0.01f*e.getWheelRotation();
	    	if (this.drawcolorhsb[2]>1.0f) {this.drawcolorhsb[2] = 1.0f;}
	    	else if (this.drawcolorhsb[2]<0.0f) {this.drawcolorhsb[2] = 0.0f;}
	    	Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
	    	float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
	    	this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
	    }
	    int onmask4c = MouseEvent.ALT_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK;
	    int offmask4c = MouseEvent.SHIFT_DOWN_MASK;
	    boolean mousewheelctrlaltdown = ((e.getModifiersEx() & (onmask4c | offmask4c)) == onmask4c);
	    if (mousewheelctrlaltdown) {
	    	this.drawcolorhsb[1] += 0.01f*e.getWheelRotation();
	    	if (this.drawcolorhsb[1]>1.0f) {this.drawcolorhsb[1] = 1.0f;}
	    	else if (this.drawcolorhsb[1]<0.0f) {this.drawcolorhsb[1] = 0.0f;}
	    	Color hsbcolor = Color.getHSBColor(this.drawcolorhsb[0], this.drawcolorhsb[1], this.drawcolorhsb[2]);
	    	float[] colorvalues = hsbcolor.getRGBColorComponents(new float[3]);
	    	this.drawcolor = new Color(colorvalues[0],colorvalues[1],colorvalues[2],this.penciltransparency);
	    }
	    int onmask4d = MouseEvent.SHIFT_DOWN_MASK;
	    int offmask4d = MouseEvent.CTRL_DOWN_MASK|MouseEvent.ALT_DOWN_MASK;
	    boolean mousewheelshiftdown = ((e.getModifiersEx() & (onmask4d | offmask4d)) == onmask4d);
	    if (mousewheelshiftdown) {
	    	//TODO <tbd>
	    }
	}
}
