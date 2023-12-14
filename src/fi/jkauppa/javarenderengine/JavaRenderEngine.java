package fi.jkauppa.javarenderengine;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
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
import java.awt.image.VolatileImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;

import fi.jkauppa.javarenderengine.MathLib.Direction;
import fi.jkauppa.javarenderengine.MathLib.Matrix;
import fi.jkauppa.javarenderengine.MathLib.Plane;
import fi.jkauppa.javarenderengine.MathLib.Position;
import fi.jkauppa.javarenderengine.MathLib.Position2;
import fi.jkauppa.javarenderengine.MathLib.Sphere;
import fi.jkauppa.javarenderengine.MathLib.Triangle;
import fi.jkauppa.javarenderengine.ModelLib.Model;

public class JavaRenderEngine extends JFrame implements KeyListener,MouseListener,MouseMotionListener,MouseWheelListener {
	private static final long serialVersionUID = 1L;
	private DrawApp drawapp = new DrawApp();
	private CADApp cadapp = new CADApp();
	private ModelApp modelapp = new ModelApp();
	private EditorApp editorapp = new EditorApp();
	private AppHandler activeapp = null;
	private GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment ();
	private GraphicsDevice gd = ge.getDefaultScreenDevice ();
	private GraphicsConfiguration gc = gd.getDefaultConfiguration ();
	private int imagecanvaswidth = 1920;
	private int imagecanvasheight= 1080;
	private RenderPanel renderpanel = new RenderPanel();
	private boolean windowedmode = true;
	private DropTargetHandler droptargethandler = new DropTargetHandler();
	
	public JavaRenderEngine() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setJMenuBar(null);
		if (!windowedmode) {
			this.setUndecorated(true);
			this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH);
		}else {
			this.setLocationByPlatform(true);
		}
		this.addKeyListener(this);
		this.renderpanel.addMouseListener(this);
		this.renderpanel.addMouseMotionListener(this);
		this.renderpanel.addMouseWheelListener(this);
		this.renderpanel.setDropTarget(droptargethandler);
		this.renderpanel.setPreferredSize(new Dimension(this.imagecanvaswidth,this.imagecanvasheight));
		this.renderpanel.setSize(this.imagecanvaswidth,this.imagecanvasheight);
		this.setContentPane(renderpanel);
		this.pack();
		this.setVisible(true);
		this.setActiveApp(drawapp);
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
		System.out.println("camdir2norm: "+camdir2norm[0].dx+" "+camdir2norm[0].dy+" "+camdir2norm[0].dz);
		System.out.println("camdir3norm: "+camdir3norm[0].dx+" "+camdir3norm[0].dy+" "+camdir3norm[0].dz);
		System.out.println("camdir4norm: "+camdir4norm[0].dx+" "+camdir4norm[0].dy+" "+camdir4norm[0].dz);
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
		double[] unsortedlist = {5, -2, 7, 15, 3, -2, 0, 2, 7};
		int[] sortedlistidx = MathLib.indexSort(unsortedlist);
		double[] sortedlist = MathLib.indexValues(unsortedlist,sortedlistidx);
		System.out.print("unsortedlist:"); for (int i=0;i<unsortedlist.length;i++) {System.out.print(" "+unsortedlist[i]);} System.out.println();
		System.out.print("sortedlistidx:"); for (int i=0;i<sortedlistidx.length;i++) {System.out.print(" "+sortedlistidx[i]);} System.out.println();
		System.out.print("sortedlist:"); for (int i=0;i<sortedlist.length;i++) {System.out.print(" "+sortedlist[i]);} System.out.println();
		Sphere[] vsphere1 = new Sphere[1]; vsphere1[0] = new Sphere(0,0,0,2); 
		Sphere[] vsphere2 = new Sphere[3]; vsphere2[0] = new Sphere(0,0,2,2); vsphere2[1] = new Sphere(0,4,0,2); vsphere2[2] = new Sphere(4,4,0,2);
		boolean[][] ssint = MathLib.sphereSphereIntersection(vsphere1, vsphere2);
		System.out.println("ssint["+ssint.length+"]["+ssint[0].length+"]="); for (int j=0;j<ssint.length;j++) {for (int i=0;i<ssint[0].length;i++) {System.out.print(" "+ssint[j][i]);}System.out.println();}
		
		String modelfilename = "res/models/testcubemodel4.obj";
		Model loadmodel = ModelLib.loadWaveFrontOBJFile(modelfilename);
		loadmodel.getClass();
		
		JavaRenderEngine app = new JavaRenderEngine();
		app.isVisible();
	}
	
	private class RenderPanel extends JPanel implements ActionListener {
		private static final long serialVersionUID = 1L;
		private final int fpstarget = 60;
		private final int fpstargetdelay = (int)Math.floor(1000.0f/(2.0f*(double)fpstarget));
		private final Timer timer = new Timer(fpstargetdelay,this);
		private long lastupdate = System.currentTimeMillis();
		private VolatileImage doublebuffer = null;
		public RenderPanel() {
			timer.start();
		}
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			long newupdate = System.currentTimeMillis();
			long ticktime = newupdate-lastupdate;
			double ticktimesec = (double)ticktime / 1000.0f;
			double ticktimefps = 1000.0f/(double)ticktime;
			lastupdate = newupdate;
			if ((doublebuffer==null)||((doublebuffer.getWidth()!=this.getWidth())&&(doublebuffer.getHeight()!=this.getHeight()))) {
				System.out.println("Window: Resolution "+this.getWidth()+"x"+this.getHeight());
				VolatileImage oldimage = this.doublebuffer;
				this.doublebuffer = gc.createCompatibleVolatileImage(this.getWidth(),this.getHeight(), Transparency.OPAQUE);
				Graphics2D gfx = this.doublebuffer.createGraphics();
				gfx.setComposite(AlphaComposite.Clear);
				gfx.fillRect(0, 0, this.getWidth(),this.getHeight());
				if (oldimage!=null) {
					gfx.setComposite(AlphaComposite.Src);
					gfx.drawImage(oldimage, 0, 0, null);
				}
				gfx.dispose();
			}
			if (this.doublebuffer!=null) {
				Graphics2D doublebuffergfx = this.doublebuffer.createGraphics();
				if (JavaRenderEngine.this.activeapp!=null) {
					JavaRenderEngine.this.activeapp.renderWindow(doublebuffergfx, doublebuffer.getWidth(), doublebuffer.getHeight(), ticktimesec, ticktimefps);
				}
				Graphics2D g2 = (Graphics2D)g;
				g2.drawImage(this.doublebuffer,0,0,null);
			}
		}
		@Override public void actionPerformed(ActionEvent e) {this.repaint();if (JavaRenderEngine.this.activeapp!=null) {JavaRenderEngine.this.activeapp.actionPerformed(e);}}
	}
	
	private class DropTargetHandler extends DropTarget {
		private static final long serialVersionUID = 1L;
		@Override public synchronized void drop(DropTargetDropEvent dtde) {System.out.println("DropTarget: drop");if (JavaRenderEngine.this.activeapp!=null) {JavaRenderEngine.this.activeapp.drop(dtde);}}
	}

	private void setActiveApp(AppHandler activeappi) {
		this.activeapp = activeappi;
	}
	public interface AppHandler extends ActionListener,KeyListener,MouseListener,MouseMotionListener,MouseWheelListener {
		public void renderWindow(Graphics2D g, int renderwidth, int renderheight, double deltatimesec, double deltatimefps);
		public void drop(DropTargetDropEvent dtde);
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
		}else if ((e.getKeyCode()==KeyEvent.VK_ENTER)&&(altdownmask)) {
			System.out.println("keyPressed: ALT+VK_ENTER");
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
		}else if (e.getKeyCode()==KeyEvent.VK_F5) {
			System.out.println("keyPressed: VK_F5");
			this.setActiveApp(drawapp);
		}else if (e.getKeyCode()==KeyEvent.VK_F6) {
			System.out.println("keyPressed: VK_F6");
			this.setActiveApp(cadapp);
		}else if (e.getKeyCode()==KeyEvent.VK_F7) {
			System.out.println("keyPressed: VK_F7");
			this.setActiveApp(modelapp);
		}else if (e.getKeyCode()==KeyEvent.VK_F8) {
			System.out.println("keyPressed: VK_F8");
			this.setActiveApp(editorapp);
		}else {
			if (this.activeapp!=null) {this.activeapp.keyPressed(e);}
		}
	}
}
