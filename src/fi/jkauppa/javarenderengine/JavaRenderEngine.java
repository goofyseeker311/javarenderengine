package fi.jkauppa.javarenderengine;

//TODO texture handling

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
import java.awt.image.VolatileImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;

import fi.jkauppa.javarenderengine.ModelLib.AxisAlignedBoundingBox;
import fi.jkauppa.javarenderengine.ModelLib.Coordinate;
import fi.jkauppa.javarenderengine.ModelLib.Cuboid;
import fi.jkauppa.javarenderengine.ModelLib.Direction;
import fi.jkauppa.javarenderengine.ModelLib.Line;
import fi.jkauppa.javarenderengine.ModelLib.Matrix;
import fi.jkauppa.javarenderengine.ModelLib.Plane;
import fi.jkauppa.javarenderengine.ModelLib.Position;
import fi.jkauppa.javarenderengine.ModelLib.Quad;
import fi.jkauppa.javarenderengine.ModelLib.Sphere;
import fi.jkauppa.javarenderengine.ModelLib.Triangle;

public class JavaRenderEngine extends JFrame implements ActionListener,KeyListener,MouseListener,MouseMotionListener,MouseWheelListener {
	private static final long serialVersionUID = 1L;
	private DrawApp drawapp = new DrawApp();
	private CADApp cadapp = new CADApp();
	private ModelApp modelapp = new ModelApp();
	private EditorApp editorapp = new EditorApp();
	private GameApp gameapp = new GameApp();
	private AppHandlerPanel activeapp = null;
	private DropTargetHandler droptargethandler = new DropTargetHandler();
	private double fpstarget = 120.0f;
	private int fpstargetdelay = (int)Math.floor(1000.0f/fpstarget);
	private Timer timer = new Timer(this.fpstargetdelay,this);
	private final int defaultimagecanvaswidth = 1920;
	private final int defaultimagecanvasheight= 1080;
	private boolean windowedmode = true;
	private VolatileImage logoimage = UtilLib.loadImage("res/icons/logo.png", true);
	
	public JavaRenderEngine() {
		if (this.logoimage!=null) {this.setIconImage(this.logoimage);}
		this.setTitle("Java Render Engine v1.8.44");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setJMenuBar(null);
		if (!windowedmode) {
			this.setUndecorated(true);
			this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH);
		}else {
			this.setLocationByPlatform(true);
		}
		this.addKeyListener(this);
		this.setDropTarget(this.droptargethandler);
		this.setActiveApp(drawapp);
		this.setVisible(true);
		timer.start();
	}

	public static void main(String[] args) {
		System.setProperty("sun.java2d.opengl", "true");
		String userdir = System.getProperty("user.dir");
		System.out.println("userdir="+userdir);
		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());} catch (Exception ex) {}
        
		Position campos=new Position(0.0f,0.0f,0.0f);
		Position[] camposa = {campos};
		Position[] campos2 = {new Position(1.0f,2.0f,3.0f), new Position(1.0f,2.0f,3.0f)};
		Position[] campos3 = {new Position(1.0f,0.0f,0.0f), new Position(1.0f,0.0f,0.0f)};
		Position[] campos4 = {new Position(1.0f,0.0f,0.0f), new Position(1.0f,1.0f,0.0f), new Position(1.0f,0.0f,1.0f)};
		campos4[0].tex = new Coordinate(0.0f,0.0f);
		campos4[1].tex = new Coordinate(1.0f,0.0f);
		campos4[2].tex = new Coordinate(0.0f,1.0f);
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
		for (int i=0;i<camrtint.length;i++) {for (int j=0;j<camrtint[i].length;j++) {if(camrtint[i][j]!=null) {System.out.println("camrtint["+i+"]["+j+"]: "+camrtint[i][j].x+" "+camrtint[i][j].y+" "+camrtint[i][j].z+" ("+camrtint[i][j].tex.u+" "+camrtint[i][j].tex.v+")");}else{System.out.println("camrtint["+i+"]["+j+"]: no hit.");}}}
		Line[][] camptint = MathLib.planeTriangleIntersection(camplane, ptri);
		for (int i=0;i<camptint.length;i++) {for (int j=0;j<camptint[i].length;j++) {if(camptint[i][j]!=null) {System.out.println("camptint["+i+"]["+j+"]: "+camptint[i][j].pos1.x+" "+camptint[i][j].pos1.y+" "+camptint[i][j].pos1.z+" ("+camptint[i][j].pos1.tex.u+" "+camptint[i][j].pos1.tex.v+"), "+camptint[i][j].pos2.x+" "+camptint[i][j].pos2.y+" "+camptint[i][j].pos2.z+" ("+camptint[i][j].pos2.tex.u+" "+camptint[i][j].pos2.tex.v+")");}else{System.out.println("camptint["+i+"]["+j+"]: no hit.");}}}
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
		int[] sortedlistidx = UtilLib.indexSort(unsortedlist);
		double[] sortedlist = UtilLib.indexValues(unsortedlist,sortedlistidx);
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
		Line[] pline = new Line[3]; pline[0]=new Line(new Position(1,0,-1),new Position(1,0,1)); pline[1]=new Line(new Position(-1,0,1),new Position(1,0,1)); pline[2]=new Line(new Position(1,0,1),new Position(1,0,3));
		Position[][] camplint = MathLib.planeLineIntersection(camplane, pline);
		for (int i=0;i<camplint.length;i++) {for (int j=0;j<camplint[i].length;j++) {if(camplint[i][j]!=null) {System.out.println("camplint["+i+"]["+j+"]: "+camplint[i][j].x+" "+camplint[i][j].y+" "+camplint[i][j].z);}else{System.out.println("camplint["+i+"]["+j+"]: no hit.");}}}
		double[] pang = MathLib.projectedAngles(64, 70.0f);
		for (int i=0;i<pang.length;i++) {System.out.println("pang["+i+"]="+pang[i]);}
		double[] prjstep = MathLib.projectedStep(64, 70.0f);
		double[] prjangles = MathLib.projectedAngles(64, 70.0f);
		Direction[] prjdirs = MathLib.projectedPlaneDirections(matrot1);
		Direction[] prjdirs2 = MathLib.projectedPlaneDirections(matrot2);
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
		Direction[][] prjrays = MathLib.projectedRays(48, 27, 70, 39, matrot1);
		for (int j=0;j<prjrays.length;j++) {System.out.print("prjrays["+j+"]=");for (int i=0;i<prjrays[j].length;i++) {System.out.print(" ["+prjrays[j][i].dx+","+prjrays[j][i].dy+","+prjrays[j][i].dz+"]");}System.out.println();}
		Direction[] camfwd = {new Direction(1,0,0)};
		Direction[] camrgt = {new Direction(0,1,0)};
		Direction[] camup = MathLib.vectorCross(camfwd[0],camrgt);
		System.out.println("camup="+camup[0].dx+" "+camup[0].dy+" "+camup[0].dz);
		Position[] vpoint = {new Position(0,0,0)};
		Position[] vplanepoint = {new Position(1,1,0)};
		Direction[] vplanenormal = {new Direction(-1,-1,0)};
		Plane[] vplane = MathLib.planeFromNormalAtPoint(vplanepoint, vplanenormal);
		double[][] vppdist = MathLib.planePointDistance(vpoint, vplane);
		double[][] vppdist2 = MathLib.planePointDistance(campos2, tplane2);
		System.out.println("vppdist="+vppdist[0][0]);
		System.out.println("vppdist2["+vppdist2.length+"]["+vppdist2[0].length+"]="); for (int j=0;j<vppdist2.length;j++) {for (int i=0;i<vppdist2[0].length;i++) {System.out.print(" "+vppdist2[j][i]);}System.out.println();}
		Position[] vertexlist = {new Position(-5,3,9),new Position(-7,-3,-1),new Position(4,-6,-7),new Position(2,4,11)};
		AxisAlignedBoundingBox aabb = MathLib.axisAlignedBoundingBox(vertexlist);
		Sphere pointcloudsphere = MathLib.pointCloudCircumSphere(vertexlist);
		Sphere[] trianglesphere = MathLib.triangleCircumSphere(ptri);
		System.out.println("aabb="+aabb.x1+","+aabb.y1+","+aabb.z1+" "+aabb.x2+" "+aabb.y2+" "+aabb.z2);
		System.out.println("boundingsphere="+pointcloudsphere.x+","+pointcloudsphere.y+","+pointcloudsphere.z+" "+pointcloudsphere.r);
		for (int i=0;i<trianglesphere.length;i++) {System.out.println("trianglesphere["+i+"]="+trianglesphere[i].x+" "+trianglesphere[i].y+" "+trianglesphere[i].z+" "+trianglesphere[i].r);}
		Position vpos = new Position(0,0,0);
		Rectangle[][] cmsint = MathLib.cubemapSphereIntersection(vpos, vsphere3, 64);
		for (int j=0;j<cmsint.length;j++) {for (int i=0;i<cmsint[0].length;i++){if(cmsint[j][i]==null){cmsint[j][i]=new Rectangle(-1,-1,1,1);}}}
		for (int j=0;j<cmsint.length;j++) {System.out.print("cmsint["+j+"]="); for (int i=0;i<cmsint[0].length;i++) {System.out.print(" "+cmsint[j][i].x+","+cmsint[j][i].y+","+(cmsint[j][i].x+cmsint[j][i].width-1)+","+(cmsint[j][i].y+cmsint[j][i].height-1));} System.out.println();}
		Triangle[] sdtri = MathLib.subDivideTriangle(ptri);
		for (int i=0;i<sdtri.length;i++) {System.out.println("sdtri["+i+"]="+sdtri[i].pos1.x+","+sdtri[i].pos1.y+","+sdtri[i].pos1.z+" "+sdtri[i].pos2.x+","+sdtri[i].pos2.y+","+sdtri[i].pos2.z+" "+sdtri[i].pos3.x+","+sdtri[i].pos3.y+","+sdtri[i].pos3.z);}
		Position[] tpoint = {new Position(0.0f,0.0f,0.0f),new Position(-50.0f,30.0f,45.0f),new Position(-8.0f,3.0f,9.0f),new Position(-6.0f,1.0f,4.0f)};
		boolean[] aabbpint = MathLib.vertexAxisAlignedBoundingBoxIntersection(aabb, tpoint);
		for (int i=0;i<aabbpint.length;i++) {System.out.println("aabbpint[i]="+aabbpint[i]);}
		Direction[] aabbdirlist = {new Direction(1,0,0),new Direction(0,1,0),new Direction(1,1,0)};
		AxisAlignedBoundingBox[] aabblist = {aabb,aabb};
		Position cubcam1 = new Position(-10.0f,1.0f,0.0f);
		Position cubcam2 = new Position(1.0f,-10.0f,0.0f);
		Cuboid cuboid = new Cuboid(new Position(aabb.x1,aabb.y1,aabb.z1),new Position(aabb.x2,aabb.y1,aabb.z1),new Position(aabb.x1,aabb.y2,aabb.z1),new Position(aabb.x2,aabb.y2,aabb.z1),new Position(aabb.x1,aabb.y1,aabb.z2),new Position(aabb.x2,aabb.y1,aabb.z2),new Position(aabb.x1,aabb.y2,aabb.z2),new Position(aabb.x2,aabb.y2,aabb.z2));
		Cuboid[] cuboidlist = {cuboid,cuboid};
		Line[][] raabbint = MathLib.rayAxisAlignedBoundingBoxIntersection(cubcam1, aabbdirlist, aabblist);
		Line[][] raabbint2 = MathLib.rayAxisAlignedBoundingBoxIntersection(cubcam2, aabbdirlist, aabblist);
		Line[][] rcubint = MathLib.rayCuboidIntersection(cubcam1, aabbdirlist, cuboidlist);
		Line[][] rcubint2 = MathLib.rayCuboidIntersection(cubcam2, aabbdirlist, cuboidlist);
		for (int j=0;j<raabbint.length;j++) {for (int i=0;i<raabbint[0].length;i++) {if (raabbint[j][i]!=null) {System.out.println("raabbint["+j+"]["+i+"]="+raabbint[j][i].pos1.x+","+raabbint[j][i].pos1.y+","+raabbint[j][i].pos1.z+" "+raabbint[j][i].pos2.x+","+raabbint[j][i].pos2.y+","+raabbint[j][i].pos2.z);} else {System.out.println("raabbint["+j+"]["+i+"]=no hit.");}}}
		for (int j=0;j<raabbint2.length;j++) {for (int i=0;i<raabbint2[0].length;i++) {if (raabbint2[j][i]!=null) {System.out.println("raabbint2["+j+"]["+i+"]="+raabbint2[j][i].pos1.x+","+raabbint2[j][i].pos1.y+","+raabbint2[j][i].pos1.z+" "+raabbint2[j][i].pos2.x+","+raabbint2[j][i].pos2.y+","+raabbint2[j][i].pos2.z);} else {System.out.println("raabbint2["+j+"]["+i+"]=no hit.");}}}
		for (int j=0;j<rcubint.length;j++) {for (int i=0;i<rcubint[0].length;i++) {if (rcubint[j][i]!=null) {System.out.println("rcubint["+j+"]["+i+"]="+rcubint[j][i].pos1.x+","+rcubint[j][i].pos1.y+","+rcubint[j][i].pos1.z+" "+rcubint[j][i].pos2.x+","+rcubint[j][i].pos2.y+","+rcubint[j][i].pos2.z);} else {System.out.println("rcubint["+j+"]["+i+"]=no hit.");}}}
		for (int j=0;j<rcubint2.length;j++) {for (int i=0;i<rcubint2[0].length;i++) {if (rcubint2[j][i]!=null) {System.out.println("rcubint2["+j+"]["+i+"]="+rcubint2[j][i].pos1.x+","+rcubint2[j][i].pos1.y+","+rcubint2[j][i].pos1.z+" "+rcubint2[j][i].pos2.x+","+rcubint2[j][i].pos2.y+","+rcubint2[j][i].pos2.z);} else {System.out.println("rcubint2["+j+"]["+i+"]=no hit.");}}}
		Position prjpoint = new Position(0.0f,0.0f,0.0f);
		Position[] prjpoints = {new Position(5.0f,0.0f,0.0f),new Position(5.0f,0.0f,-5.0f),new Position(0.0f,0.0f,-5.0f)};
		Matrix prjmat = MathLib.rotationMatrix(0.0f, 0.0f, 0.0f);
		Coordinate[] prjcoords = MathLib.projectedPoints(prjpoint, prjpoints, 64, 90.0f, 64, 90.0f, prjmat);
		for (int i=0;i<prjcoords.length;i++) { if(prjcoords[i]!=null){System.out.println("prjcoords[i]="+prjcoords[i].u+" "+prjcoords[i].v);}else{System.out.println("prjcoords[i]=not visible.");}}
		Position rpdpos = new Position(0.0f,0.0f,0.0f); 
		Position rpdpos2 = new Position(0.0f,0.0f,1.0f); 
		Direction[] rpddir = {new Direction(1.0f,0.0f,0.0f),new Direction(1.0f,0.0f,1.0f)};
		Position[] rpdpoint = {new Position(1.0f,0.0f,0.0f),new Position(1.0f,0.0f,0.0f),new Position(1.0f,0.0f,0.0f)};
		double[][] rptdist = MathLib.rayPointDistance(rpdpos, rpddir, rpdpoint);
		double[][] rptdist2 = MathLib.rayPointDistance(rpdpos2, rpddir, rpdpoint);
		for (int j=0;j<rptdist.length;j++) {for (int i=0;i<rptdist[0].length;i++) {System.out.println("rptdist["+j+"]["+i+"]= "+rptdist[j][i]);}}
		for (int j=0;j<rptdist2.length;j++) {for (int i=0;i<rptdist2[0].length;i++) {System.out.println("rptdist2["+j+"]["+i+"]= "+rptdist2[j][i]);}}
		double vsindn45 = MathLib.sind(-45.0f); System.out.println("vsindn45="+vsindn45);
		double vsind0 = MathLib.sind(0.0f); System.out.println("vsind0="+vsind0);
		double vsind45 = MathLib.sind(45.0f); System.out.println("vsind45="+vsind45);
		double vsind90 = MathLib.sind(90.0f); System.out.println("vsind90="+vsind90);
		double vsind180 = MathLib.sind(180.0f); System.out.println("vsind180="+vsind180);
		double vsind270 = MathLib.sind(270.0f); System.out.println("vsind270="+vsind270);
		double vsind360 = MathLib.sind(360.0f); System.out.println("vsind360="+vsind360);
		double vasind0 = MathLib.asind(0.0f); System.out.println("vasind0="+vasind0);
		double vasindn1 = MathLib.asind(-1.0f); System.out.println("vasindn1="+vasindn1);
		double vasindnis2 = MathLib.asind(-1.0f/Math.sqrt(2.0f)); System.out.println("vasindnis2="+vasindnis2);
		double vasindp1 = MathLib.asind(1.0f); System.out.println("vasindp1="+vasindp1);
		double vasindpis2 = MathLib.asind(1.0f/Math.sqrt(2.0f)); System.out.println("vasindpis2="+vasindpis2);
		double vcosdn45 = MathLib.cosd(-45.0f); System.out.println("vcosdn45="+vcosdn45);
		double vcosd0 = MathLib.cosd(0.0f); System.out.println("vcosd0="+vcosd0);
		double vcosd45 = MathLib.cosd(45.0f); System.out.println("vcosd45="+vcosd45);
		double vcosd90 = MathLib.cosd(90.0f); System.out.println("vcosd90="+vcosd90);
		double vcosd180 = MathLib.cosd(180.0f); System.out.println("vcosd180="+vcosd180);
		double vcosd270 = MathLib.cosd(270.0f); System.out.println("vcosd270="+vcosd270);
		double vcosd360 = MathLib.cosd(360.0f); System.out.println("vcosd360="+vcosd360);
		double vacosd0 = MathLib.acosd(0.0f); System.out.println("vacosd0="+vacosd0);
		double vacosdn1 = MathLib.acosd(-1.0f); System.out.println("vacosdn1="+vacosdn1);
		double vacosdnis2 = MathLib.acosd(-1.0f/Math.sqrt(2.0f)); System.out.println("vacosdnis2="+vacosdnis2);
		double vacosdp1 = MathLib.acosd(1.0f); System.out.println("vacosdp1="+vacosdp1);
		double vacosdpis2 = MathLib.acosd(1.0f/Math.sqrt(2.0f)); System.out.println("vacosdpis2="+vacosdpis2);
		double vtandn45 = MathLib.tand(-45.0f); System.out.println("vtandn45="+vtandn45);
		double vtand0 = MathLib.tand(0.0f); System.out.println("vtand0="+vtand0);
		double vtand45 = MathLib.tand(45.0f); System.out.println("vtand45="+vtand45);
		double vtand90 = MathLib.tand(90.0f); System.out.println("vtand90="+vtand90);
		double vtand180 = MathLib.tand(180.0f); System.out.println("vtand180="+vtand180);
		double vtand270 = MathLib.tand(270.0f); System.out.println("vtand270="+vtand270);
		double vtand360 = MathLib.tand(360.0f); System.out.println("vtand360="+vtand360);
		double vatand0 = MathLib.atand(0.0f); System.out.println("vatand0="+vatand0);
		double vatandn1 = MathLib.atand(-1.0f); System.out.println("vatandn1="+vatandn1);
		double vatandnis2 = MathLib.atand(-1.0f/Math.sqrt(2.0f)); System.out.println("vatandnis2="+vatandnis2);
		double vatandp1 = MathLib.atand(1.0f); System.out.println("vatandp1="+vatandp1);
		double vatandpis2 = MathLib.atand(1.0f/Math.sqrt(2.0f)); System.out.println("vatandpis2="+vatandpis2);
		Position vrqipos = new Position(0.0f,0.0f,0.0f);
		Direction[] vrqidir = {new Direction(1.0f,0.0f,0.0f),new Direction(1.0f,0.0f,1.0f)};
		Quad vrqiquad = new Quad(new Position(1.0f,0.0f,0.0f),new Position(1.0f,0.0f,1.0f),new Position(1.0f,1.0f,1.0f),new Position(1.0f,1.0f,0.0f));
		Quad[] vrqiquads = {vrqiquad, vrqiquad, vrqiquad};
		Position[][] rqint = MathLib.rayQuadIntersection(vrqipos, vrqidir, vrqiquads);
		for (int j=0;j<rqint.length;j++) {for (int i=0;i<rqint[0].length;i++) {if(rqint[j][i]!=null){System.out.println("rqint["+i+"]["+j+"]="+rqint[j][i].x+" "+rqint[j][i].y+" "+rqint[j][i].z);}else{System.out.println("rqint["+i+"]["+j+"]=no hit.");}}}
		
		new JavaRenderEngine();
	}
	
	public static abstract class AppHandlerPanel extends JPanel implements KeyListener,MouseListener,MouseMotionListener,MouseWheelListener {
		private static final long serialVersionUID = 1L;
		public int lastrenderwidth = 0, lastrenderheight = 0;
		public long newupdate = System.currentTimeMillis();
		public long lastupdate = newupdate;
		public long ticktime = 0;
		public double ticktimesec = 0.0f;
		public double ticktimefps = 0.0f;
		public GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment ();
		public GraphicsDevice gd = ge.getDefaultScreenDevice ();
		public GraphicsConfiguration gc = gd.getDefaultConfiguration ();
		public Toolkit tk = Toolkit.getDefaultToolkit();
		public Clipboard cb = tk.getSystemClipboard();
		
		@Override public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if ((this.lastrenderwidth!=this.getWidth())||(this.lastrenderheight!=this.getHeight())) {
				this.lastrenderwidth = this.getWidth();
				this.lastrenderheight = this.getHeight();
				System.out.println("Window: Resolution "+this.getWidth()+"x"+this.getHeight());
			}
			this.newupdate = System.currentTimeMillis();
			this.ticktime = this.newupdate-this.lastupdate;
			this.ticktimesec = ((double)this.ticktime)/1000.0f;
			this.ticktimefps = 1000.0f/((double)this.ticktime);
		}
		public abstract void drop(DropTargetDropEvent dtde);
		public abstract void timerTick();
	}
	
	private class DropTargetHandler extends DropTarget {
		private static final long serialVersionUID = 1L;
		@Override public synchronized void drop(DropTargetDropEvent dtde) {System.out.println("DropTarget: drop");if (JavaRenderEngine.this.activeapp!=null) {JavaRenderEngine.this.activeapp.drop(dtde);}}
	}

	private void setActiveApp(AppHandlerPanel activeappi) {
		int appcanvaswidth = this.defaultimagecanvaswidth; 
		int appcanvasheight = this.defaultimagecanvasheight; 
		if (this.activeapp!=null) {
			this.activeapp.removeMouseListener(this);
			this.activeapp.removeMouseMotionListener(this);
			this.activeapp.removeMouseWheelListener(this);
			appcanvaswidth = this.activeapp.getWidth();
			appcanvasheight = this.activeapp.getHeight();
		}
		this.activeapp = activeappi;
		this.activeapp.addMouseListener(this);
		this.activeapp.addMouseMotionListener(this);
		this.activeapp.addMouseWheelListener(this);
		this.activeapp.setPreferredSize(new Dimension(appcanvaswidth,appcanvasheight));
		this.activeapp.setSize(appcanvaswidth,appcanvasheight);
		this.setContentPane(this.activeapp);
		this.pack();
	}
	
	@Override public void actionPerformed(ActionEvent e) {
		if (this.activeapp!=null) {this.activeapp.timerTick();}
		Dimension componentsize=this.activeapp.getSize();
		Rectangle paintregion=new Rectangle(0,0,componentsize.width,componentsize.height);
		this.activeapp.paintImmediately(paintregion);
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
	    	this.dispose();
	    	if (!windowedmode) {
	    		windowedmode = true;
	    		this.setExtendedState(this.getExtendedState()&~JFrame.MAXIMIZED_BOTH);
	    		this.setUndecorated(false);
	    		this.activeapp.setPreferredSize(new Dimension(this.defaultimagecanvaswidth,this.defaultimagecanvasheight));
	    		this.activeapp.setSize(this.defaultimagecanvaswidth,this.defaultimagecanvasheight);
	    		this.pack();
	    	}else {
	    		windowedmode = false;
	    		this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH);
	    		this.setUndecorated(true);
	    	}
	    	this.setVisible(true);
		}else if (e.getKeyCode()==KeyEvent.VK_F5) {
			System.out.println("keyPressed: VK_F5");
			this.setActiveApp(this.drawapp);
		}else if (e.getKeyCode()==KeyEvent.VK_F6) {
			System.out.println("keyPressed: VK_F6");
			this.setActiveApp(this.cadapp);
		}else if (e.getKeyCode()==KeyEvent.VK_F7) {
			System.out.println("keyPressed: VK_F7");
			this.setActiveApp(this.modelapp);
		}else if (e.getKeyCode()==KeyEvent.VK_F8) {
			System.out.println("keyPressed: VK_F8");
			this.setActiveApp(this.editorapp);
		}else if (e.getKeyCode()==KeyEvent.VK_F9) {
			System.out.println("keyPressed: VK_F9");
			this.setActiveApp(this.gameapp);
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
			VolatileImage componentimage = this.activeapp.gc.createCompatibleVolatileImage(this.activeapp.getWidth(),this.activeapp.getHeight(), Transparency.OPAQUE);
			Graphics2D gfx = componentimage.createGraphics();
			this.activeapp.paintComponent(gfx);
			gfx.dispose();
			try {
				ImageIO.write(componentimage.getSnapshot(), "PNG", screenshotfile);
			} catch (Exception ex) {ex.printStackTrace();}
		}else {
			if (this.activeapp!=null) {this.activeapp.keyPressed(e);}
		}
	}

}
