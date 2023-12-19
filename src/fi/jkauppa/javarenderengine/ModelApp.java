package fi.jkauppa.javarenderengine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.Arrays;
import java.util.TreeSet;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import fi.jkauppa.javarenderengine.JavaRenderEngine.AppHandler;
import fi.jkauppa.javarenderengine.MathLib.Direction;
import fi.jkauppa.javarenderengine.MathLib.Plane;
import fi.jkauppa.javarenderengine.MathLib.Position;
import fi.jkauppa.javarenderengine.MathLib.Triangle;
import fi.jkauppa.javarenderengine.ModelLib.Material;
import fi.jkauppa.javarenderengine.ModelLib.Model;

public class ModelApp implements AppHandler {
	private Model model = null;
	private Triangle[] trianglelist = null;
	private Material[] materiallist = null;
	private int drawdepth = 6000; 
	private final double drawdepthscale = 0.00035f;
	private int origindeltax = 0, origindeltay = 0; 
	private int lastrenderwidth = 0, lastrenderheight = 0;
	private Direction lookdir = new Direction(0,0,-1);
	private JFileChooser filechooser = new JFileChooser();
	private ImageFileFilters.OBJFileFilter objfilefilter = new ImageFileFilters.OBJFileFilter();
	
	public ModelApp() {
		this.filechooser.addChoosableFileFilter(this.objfilefilter);
		this.filechooser.setFileFilter(this.objfilefilter);
		this.filechooser.setAcceptAllFileFilterUsed(false);
	}
	
	@Override public void renderWindow(Graphics2D g, int renderwidth, int renderheight, double deltatimesec, double deltatimefps) {
		this.origindeltax = (int)Math.floor(((double)renderwidth)/2.0f);
		this.origindeltay = (int)Math.floor(((double)renderheight)/2.0f);
		if ((this.lastrenderwidth!=renderwidth)||(this.lastrenderheight!=renderheight)) {
			this.lastrenderwidth = renderwidth;
			this.lastrenderheight = renderheight;
		}
		g.setComposite(AlphaComposite.Src);
		g.setColor(Color.BLACK);
		g.setPaint(null);
		g.fillRect(0, 0, renderwidth, renderheight);
		if (trianglelist!=null) {
			Plane[] triangleplanes = MathLib.planeFromPoints(trianglelist);
			Direction[] trianglenormals = MathLib.planeNormals(triangleplanes);
			double[] triangleviewangles = MathLib.vectorAngle(lookdir, trianglenormals);
			for (int i=0;i<trianglelist.length;i++) {
				if ((trianglelist[i].pos1.z<=this.drawdepth)||(trianglelist[i].pos2.z<=this.drawdepth)||(trianglelist[i].pos3.z<=this.drawdepth)) {
					double pos1s = (this.drawdepth-trianglelist[i].pos1.z)*this.drawdepthscale+1;
					int pos1x = (int)Math.round(trianglelist[i].pos1.x/pos1s)+this.origindeltax;
					int pos1y = (int)Math.round(trianglelist[i].pos1.y/pos1s)+this.origindeltay;
					double pos2s = (this.drawdepth-trianglelist[i].pos2.z)*this.drawdepthscale+1;
					int pos2x = (int)Math.round(trianglelist[i].pos2.x/pos2s)+this.origindeltax;
					int pos2y = (int)Math.round(trianglelist[i].pos2.y/pos2s)+this.origindeltay;
					double pos3s = (this.drawdepth-trianglelist[i].pos3.z)*this.drawdepthscale+1;
					int pos3x = (int)Math.round(trianglelist[i].pos3.x/pos3s)+this.origindeltax;
					int pos3y = (int)Math.round(trianglelist[i].pos3.y/pos3s)+this.origindeltay;
					Polygon trianglepolygon = new Polygon();
					trianglepolygon.addPoint(pos1x, pos1y);
					trianglepolygon.addPoint(pos2x, pos2y);
					trianglepolygon.addPoint(pos3x, pos3y);
					double triangleviewangle = triangleviewangles[i];
					if (triangleviewangle>90.0f) {
						triangleviewangle = 180-triangleviewangle;
					}
					float shadingmultiplier = (90.0f-(((float)triangleviewangle))/1.5f)/90.0f;
					Color tricolor = model.materials[trianglelist[i].mind].facecolor;
					if (tricolor==null) {tricolor = Color.WHITE;}
					float[] tricolorcomp = tricolor.getRGBColorComponents(new float[3]);
					g.setColor(new Color(tricolorcomp[0]*shadingmultiplier, tricolorcomp[1]*shadingmultiplier, tricolorcomp[2]*shadingmultiplier));
					g.fill(trianglepolygon);
				}
			}
		}
	}

	private void processModel() {
		TreeSet<Material> materialarray = new TreeSet<Material>(); 
		for (int i=0;i<model.materials.length;i++) {
			Material material = model.materials[i];
			material.mind = i;
			materialarray.add(material);
		}
		this.materiallist = materialarray.toArray(new Material[materialarray.size()]);
		TreeSet<Triangle> trianglearray = new TreeSet<Triangle>(); 
		for (int j=0;j<model.objects.length;j++) {
			Material searchmat = new Material(model.objects[j].usemtl);
			int matind = this.materiallist[Arrays.binarySearch(this.materiallist, searchmat)].mind;
			for (int i=0;i<model.objects[j].faceindex.length;i++) {
				Position pos1 = model.vertexlist[model.objects[j].faceindex[i].facevertexindex[0].vertexindex-1];
				Position pos2 = model.vertexlist[model.objects[j].faceindex[i].facevertexindex[1].vertexindex-1];
				Position pos3 = model.vertexlist[model.objects[j].faceindex[i].facevertexindex[2].vertexindex-1];
				Triangle tri = new Triangle(new Position(pos1.x,pos1.y,pos1.z),new Position(pos2.x,pos2.y,pos2.z),new Position(pos3.x,pos3.y,pos3.z));
				tri.oind = j;
				tri.tind = i;
				tri.mind = matind; 
				trianglearray.add(tri);
			}
		}
		this.trianglelist = trianglearray.toArray(new Triangle[trianglearray.size()]);
	}
	
	@Override public void actionPerformed(ActionEvent e) {}
	
	@Override public void keyPressed(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_F3) {
			this.filechooser.setDialogTitle("Load File");
			this.filechooser.setApproveButtonText("Load");
			if (this.filechooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				File loadfile = this.filechooser.getSelectedFile();
				this.model = ModelLib.loadWaveFrontOBJFile(loadfile.getPath(), false);
				processModel();
			}
		}
	}
	
	@Override public void keyTyped(KeyEvent e) {}
	@Override public void keyReleased(KeyEvent e) {}
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mouseDragged(MouseEvent e) {}
	@Override public void mouseMoved(MouseEvent e) {}
	@Override public void mouseWheelMoved(MouseWheelEvent e) {}
	@Override public void drop(DropTargetDropEvent dtde) {}

	private class ImageFileFilters  {
		public static class OBJFileFilter extends FileFilter {
			@Override public boolean accept(File f) {return (f.isDirectory())||(f.getName().endsWith(".obj"));}
			@Override public String getDescription() {return "OBJ Model file";}
		}
	}

}