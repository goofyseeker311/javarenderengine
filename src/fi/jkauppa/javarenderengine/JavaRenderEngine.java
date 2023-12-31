package fi.jkauppa.javarenderengine;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
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
import java.awt.image.VolatileImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;

import fi.jkauppa.javarenderengine.MathLib.Cuboid;
import fi.jkauppa.javarenderengine.MathLib.Direction;
import fi.jkauppa.javarenderengine.MathLib.Matrix;
import fi.jkauppa.javarenderengine.MathLib.Plane;
import fi.jkauppa.javarenderengine.MathLib.Position;
import fi.jkauppa.javarenderengine.MathLib.Position2;
import fi.jkauppa.javarenderengine.MathLib.Sphere;
import fi.jkauppa.javarenderengine.MathLib.Triangle;

public class JavaRenderEngine extends JFrame implements KeyListener,MouseListener,MouseMotionListener,MouseWheelListener {
	private static final long serialVersionUID = 1L;
	private DrawApp drawapp = new DrawApp();
	private CADApp cadapp = new CADApp();
	private ModelApp modelapp = new ModelApp();
	private EditorApp editorapp = new EditorApp();
	private GameApp gameapp = new GameApp();
	private AppHandler activeapp = null;
	private GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment ();
	private GraphicsDevice gd = ge.getDefaultScreenDevice ();
	private GraphicsConfiguration gc = gd.getDefaultConfiguration ();
	private int imagecanvaswidth = 1920;
	private int imagecanvasheight= 1080;
	private RenderPanel renderpanel = new RenderPanel();
	private boolean windowedmode = true;
	private DropTargetHandler droptargethandler = new DropTargetHandler();
	private final int fpstarget = 120;
	private final int fpstargetdelay = (int)Math.floor(1000.0f/(2.0f*(double)fpstarget));
	private VolatileImage logoimage = UtilLib.loadImage("res/icons/logo.png", true);
	
	public JavaRenderEngine() {
		if (this.logoimage!=null) {this.setIconImage(this.logoimage);}
		this.setTitle("Java Render Engine v1.6.4");
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
		String userdir = System.getProperty("user.dir");
		System.out.println("userdir="+userdir);
		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());} catch (Exception ex) {}
        
		Position campos=new Position(0.0f,0.0f,0.0f);
		Position[] camposa=new Position[1]; camposa[0]=campos;
		Position[] campos2 = {new Position(1.0f,2.0f,3.0f), new Position(1.0f,2.0f,3.0f)};
		Position[] campos3 = {new Position(1.0f,0.0f,0.0f), new Position(1.0f,0.0f,0.0f)};
		Position[] campos4 = {new Position(1.0f,0.0f,0.0f), new Position(1.0f,1.0f,0.0f), new Position(1.0f,0.0f,1.0f)};
		System.out.println("campos: "+campos.x+" "+campos.y+" "+campos.z);
		Direction[] camvec = MathLib.vectorFromPoints(campos2, campos3);
		for (int i=0;i<camvec.length;i++) {System.out.println("camvec: "+camvec[i].dx+" "+camvec[i].dy+" "+camvec[i].dz);}
		Direction[] camdir = {new Direction(1.0f,0.0f,0.0f)};
		Direction[] camdir2 = {new Direction(1.0f,-1.0f,1.0f), new Direction(1.0f,-1.0f,1.0f)};
		Direction[] camdir3 = {new Direction(1.0f,0.0f,0.0f), new Direction(1.0f,0.0f,0.0f)};
		Direction[] camdir4 = {new Direction(0.0f,0.0f,1.0f)};
		Direction[] camcross = MathLib.vectorCross(camdir2, camdir3);
		for (int i=0;i<camcross.length;i++) {System.out.println("camcross: "+camcross[i].dx+" "+camcross[i].dy+" "+camcross[i].dz);}
		double[] camdir2len = MathLib.vectorLength(camdir2);
		for (int i=0;i<camdir2len.length;i++) {System.out.println("camdir2len: "+camdir2len[i]);}
		Triangle[] ptri = {new Triangle(campos4[0],campos4[1],campos4[2]), new Triangle(campos4[0],campos4[1],campos4[2]), new Triangle(campos4[0],campos4[1],campos4[2])};
		Plane[] tpplane = MathLib.planeFromPoints(ptri);
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
		Plane[] tplane = {new Plane(1.0f,0.0f,0.0f,-2.0f)};
		Plane[] tplane2 = {new Plane(1.0f,0.0f,0.0f,-2.0f), new Plane(1.0f,0.0f,0.0f,-2.0f), new Plane(1.0f,0.0f,0.0f,-2.0f)};
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
		Position[][] camrtint = MathLib.rayTriangleIntersection(campos, camdir3, ptri);
		for (int i=0;i<camrtint.length;i++) {for (int j=0;j<camrtint[i].length;j++) {if(camrtint[i][j]!=null) {System.out.println("camrtint["+i+"]["+j+"]: "+camrtint[i][j].x+" "+camrtint[i][j].y+" "+camrtint[i][j].z);}else{System.out.println("camrtint["+i+"]["+j+"]: no hit.");}}}
		Position2[][] camptint = MathLib.planeTriangleIntersection(camplane, ptri);
		for (int i=0;i<camptint.length;i++) {for (int j=0;j<camptint[i].length;j++) {if(camptint[i][j]!=null) {System.out.println("camptint["+i+"]["+j+"]: "+camptint[i][j].pos1.x+" "+camptint[i][j].pos1.y+" "+camptint[i][j].pos1.z+", "+camptint[i][j].pos2.x+" "+camptint[i][j].pos2.y+" "+camptint[i][j].pos2.z);}else{System.out.println("camptint["+i+"]["+j+"]: no hit.");}}}
		Matrix mat1 = new Matrix(1,0,0,0,1,0,0,0,1);
		Matrix mat2 = new Matrix(0.6124,0.6124,0.5000,0.3536,0.3536,-0.8660,-0.7071,0.7071,0);
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
		Sphere[] vsphere1 = {new Sphere(0,0,0,2)}; 
		Sphere[] vsphere2 = {new Sphere(0,0,2,2), new Sphere(0,4,0,2), new Sphere(4,4,0,2)};
		Sphere[] vsphere3 = {new Sphere(0,0,0,2), new Sphere(0,0,2,2), new Sphere(0,3.9,0,2), new Sphere(3.9,0,0,2)};
		boolean[][] ssint = MathLib.sphereSphereIntersection(vsphere1, vsphere2);
		Integer[][] ssint2 = MathLib.mutualSphereIntersection(vsphere3);
		System.out.println("ssint["+ssint.length+"]["+ssint[0].length+"]="); for (int j=0;j<ssint.length;j++) {for (int i=0;i<ssint[0].length;i++) {System.out.print(" "+ssint[j][i]);}System.out.println();}
		for (int j=0;j<ssint2.length;j++) {System.out.print("ssint2["+j+"]=");for (int i=0;i<ssint2[j].length;i++){System.out.print(" "+ssint2[j][i]);}System.out.println();}
		Position2[] pline = new Position2[3]; pline[0]=new Position2(new Position(1,0,-1),new Position(1,0,1)); pline[1]=new Position2(new Position(-1,0,1),new Position(1,0,1)); pline[2]=new Position2(new Position(1,0,1),new Position(1,0,3));
		Position[][] camplint = MathLib.planeLineIntersection(camplane, pline);
		for (int i=0;i<camplint.length;i++) {for (int j=0;j<camplint[i].length;j++) {if(camplint[i][j]!=null) {System.out.println("camplint["+i+"]["+j+"]: "+camplint[i][j].x+" "+camplint[i][j].y+" "+camplint[i][j].z);}else{System.out.println("camplint["+i+"]["+j+"]: no hit.");}}}
		double[] pang = MathLib.projectedAngles(64, 70.0f);
		for (int i=0;i<pang.length;i++) {System.out.println("pang["+i+"]="+pang[i]);}
		double[] prjstep = MathLib.projectedStep(64, 70.0f);
		double[] prjangles = MathLib.projectedAngles(64, 70.0f);
		Direction[] prjdirs = MathLib.projectedDirections(matrot1);
		Direction[] prjdirs2 = MathLib.projectedDirections(matrot2);
		Direction[] prjvectors = MathLib.projectedVectors(64, 70.0f, matrot1);
		Plane[] prjplane = MathLib.projectedPlanes(campos, 64, 70.0f, matrot1);
		Plane[] prjplane2 = MathLib.projectedPlanes(campos2[0], 64, 70.0f, matrot2);
		for (int i=0;i<prjstep.length;i++) {System.out.println("prjstep["+i+"]: "+prjstep[i]);}
		for (int i=0;i<prjangles.length;i++) {System.out.println("prjangles["+i+"]: "+prjangles[i]);}
		System.out.println("prjdirs[0]="+prjdirs[0].dx+","+prjdirs[0].dy+","+prjdirs[0].dz); System.out.println("prjdirs[1]="+prjdirs[1].dx+","+prjdirs[1].dy+","+prjdirs[1].dz); System.out.println("prjdirs[2]="+prjdirs[2].dx+","+prjdirs[2].dy+","+prjdirs[2].dz);
		System.out.println("prjdirs2[0]="+prjdirs2[0].dx+","+prjdirs2[0].dy+","+prjdirs2[0].dz); System.out.println("prjdirs2[1]="+prjdirs2[1].dx+","+prjdirs2[1].dy+","+prjdirs2[1].dz); System.out.println("prjdirs2[2]="+prjdirs2[2].dx+","+prjdirs2[2].dy+","+prjdirs2[2].dz);
		for (int i=0;i<prjvectors.length;i++) {System.out.println("prjvectors["+i+"]: "+prjvectors[i].dx+" "+prjvectors[i].dy+" "+prjvectors[i].dz);}
		for (int i=0;i<prjplane.length;i++) {System.out.println("prjplane["+i+"]: "+prjplane[i].a+" "+prjplane[i].b+" "+prjplane[i].c+" "+prjplane[i].d);}
		for (int i=0;i<prjplane2.length;i++) {System.out.println("prjplane2["+i+"]: "+prjplane2[i].a+" "+prjplane2[i].b+" "+prjplane2[i].c+" "+prjplane2[i].d);}
		Direction[][] prjrays = MathLib.projectedRays(campos,48, 27, 70, 39, matrot1);
		for (int j=0;j<prjrays.length;j++) {System.out.print("prjrays["+j+"]=");for (int i=0;i<prjrays[j].length;i++) {System.out.print(" ["+prjrays[j][i].dx+","+prjrays[j][i].dy+","+prjrays[j][i].dz+"]");}System.out.println();}
		Direction[] camfwd = {new Direction(1,0,0)};
		Direction[] camrgt = {new Direction(0,1,0)};
		Direction[] camup = MathLib.vectorCross(camfwd[0],camrgt);
		System.out.println("camup="+camup[0].dx+" "+camup[0].dy+" "+camup[0].dz);
		Position[] vpoint = {new Position(0,0,0)};
		Position[] vplanepoint = {new Position(1,1,0)};
		Direction[] vplanenormal = {new Direction(-1,-1,0)};
		Plane[] vplane = MathLib.planeFromNormalAtPoint(vplanepoint, vplanenormal);
		double[][] vppdist = MathLib.pointPlaneDistance(vpoint, vplane);
		double[][] vppdist2 = MathLib.pointPlaneDistance(campos2, tplane2);
		System.out.println("vppdist="+vppdist[0][0]);
		System.out.println("vppdist2["+vppdist2.length+"]["+vppdist2[0].length+"]="); for (int j=0;j<vppdist2.length;j++) {for (int i=0;i<vppdist2[0].length;i++) {System.out.print(" "+vppdist2[j][i]);}System.out.println();}
		Position[] vertexlist = {new Position(-5,3,9),new Position(-7,-3,-1),new Position(4,-6,-7),new Position(2,4,11)};
		Cuboid aaboundingbox = MathLib.axisAlignedBoundingBox(vertexlist);
		Sphere pointcloudsphere = MathLib.pointCloudCircumSphere(vertexlist);
		Sphere[] trianglesphere = MathLib.triangleCircumSphere(ptri);
		System.out.println("aaboundingbox="+aaboundingbox.x1+","+aaboundingbox.y1+","+aaboundingbox.z1+" "+aaboundingbox.x2+" "+aaboundingbox.y2+" "+aaboundingbox.z2);
		System.out.println("boundingsphere="+pointcloudsphere.x+","+pointcloudsphere.y+","+pointcloudsphere.z+" "+pointcloudsphere.r);
		for (int i=0;i<trianglesphere.length;i++) {System.out.println("trianglesphere["+i+"]="+trianglesphere[i].x+" "+trianglesphere[i].y+" "+trianglesphere[i].z+" "+trianglesphere[i].r);}
		
		new JavaRenderEngine();
	}
	
	private class RenderPanel extends JPanel implements ActionListener {
		private static final long serialVersionUID = 1L;
		private final Timer timer = new Timer(JavaRenderEngine.this.fpstargetdelay,this);
		private long lastupdate = System.currentTimeMillis();
		public VolatileImage doublebuffer = null;
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
			if ((this.doublebuffer==null)||((this.doublebuffer.getWidth()!=this.getWidth())&&(this.doublebuffer.getHeight()!=this.getHeight()))) {
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
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment ();
		GraphicsDevice gd = ge.getDefaultScreenDevice ();
		GraphicsConfiguration gc = gd.getDefaultConfiguration ();
		Toolkit tk = Toolkit.getDefaultToolkit();
		Clipboard cb = tk.getSystemClipboard();
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
		}else if (e.getKeyCode()==KeyEvent.VK_F9) {
			System.out.println("keyPressed: VK_F9");
			this.setActiveApp(gameapp);
		}else if (e.getKeyCode()==KeyEvent.VK_F10) {
			System.out.println("keyPressed: VK_F10");
			//TODO <tbd>
			e.consume();
		}else if (e.getKeyCode()==KeyEvent.VK_F11) {
			System.out.println("keyPressed: VK_F11");
			//TODO <tbd>
		}else if (e.getKeyCode()==KeyEvent.VK_F12) {
			System.out.println("keyPressed: VK_F12");
			File screenshotfile = new File("screenshot1.png");
			int screenshotnum = 1;
			while (screenshotfile.exists()) {
				screenshotnum += 1;
				screenshotfile = new File("screenshot"+screenshotnum+".png");
			}
			try {
				ImageIO.write(this.renderpanel.doublebuffer.getSnapshot(), "PNG", screenshotfile);
			} catch (Exception ex) {ex.printStackTrace();}
		}else {
			if (this.activeapp!=null) {this.activeapp.keyPressed(e);}
		}
	}

}
