package fi.jkauppa.javarenderengine;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import fi.jkauppa.javarenderengine.JavaRenderEngine.AppHandler;
import fi.jkauppa.javarenderengine.MathLib.Plane;
import fi.jkauppa.javarenderengine.MathLib.Position;
import fi.jkauppa.javarenderengine.MathLib.Position2;
import fi.jkauppa.javarenderengine.MathLib.Rotation;
import fi.jkauppa.javarenderengine.MathLib.Triangle;
import fi.jkauppa.javarenderengine.ModelLib.Model;

public class ModelApp implements AppHandler {
	private Model model = null;
	private Triangle[] trianglelist = null;
	private Plane[] camplanes = null;
	private Position campos = new Position(0,270,0);
	private int vfov = 70;
	private Rotation vrot = new Rotation(0,90,0);
	private final double drawdepthscale = 0.0003f;
	private int origindeltax = 0, origindeltay = 0; 
	private int lastrenderwidth = 0, lastrenderheight = 0;
	private final int linestroke = 1;
	
	public ModelApp() {
		String modelfilename = "res/models/testcubemodel4.obj";
		this.model = ModelLib.loadWaveFrontOBJFile(modelfilename,true);
		ArrayList<Triangle> trianglearray = new ArrayList<Triangle>(); 
		for (int j=0;j<model.objects.length;j++) {
			for (int i=0;i<model.objects[j].faceindex.length;i++) {
				Position pos1 = model.vertexlist[model.objects[j].faceindex[i].facevertexindex[0].vertexindex-1];
				Position pos2 = model.vertexlist[model.objects[j].faceindex[i].facevertexindex[1].vertexindex-1];
				Position pos3 = model.vertexlist[model.objects[j].faceindex[i].facevertexindex[2].vertexindex-1];
				trianglearray.add(new Triangle(new Position(pos1.x*100,pos1.y*100,pos1.z*100),new Position(pos2.x*100,pos2.y*100,pos2.z*100),new Position(pos3.x*100,pos3.y*100,pos3.z*100)));
			}
		}
		trianglelist = trianglearray.toArray(new Triangle[trianglearray.size()]);
	}
	
	@Override public void renderWindow(Graphics2D g, int renderwidth, int renderheight, double deltatimesec, double deltatimefps) {
		this.origindeltax = (int)Math.floor(((double)renderwidth)/2.0f);
		this.origindeltay = (int)Math.floor(((double)renderheight)/2.0f);
		if ((this.camplanes==null)||(this.lastrenderwidth!=renderwidth)||(this.lastrenderheight!=renderheight)) {
			this.lastrenderwidth = renderwidth;
			this.lastrenderheight = renderheight;
			this.camplanes = MathLib.projectedPlanes(this.campos, renderwidth, this.vfov, this.vrot);
		}
		g.setComposite(AlphaComposite.Src);
		g.setColor(Color.BLACK);
		g.setPaint(null);
		g.fillRect(0, 0, renderwidth, renderheight);
		Position2[][] planetriangleint = MathLib.planeTriangleIntersection(camplanes, this.trianglelist);
		for (int j=0;j<planetriangleint.length;j++) {
			for (int i=0;i<planetriangleint[j].length;i++) {
				Position2 intersectionline = planetriangleint[j][i];
				if (intersectionline!=null) {
					if ((intersectionline.pos1.z>=0)&&(intersectionline.pos2.z>=0)) {
						double pos1s = intersectionline.pos1.z*this.drawdepthscale+1;
						int pos1x = (int)Math.round(intersectionline.pos1.x/pos1s)+this.origindeltax;
						int pos1y = (int)Math.round(intersectionline.pos1.y/pos1s)+this.origindeltay;
						double pos2s = intersectionline.pos2.z*this.drawdepthscale+1;
						int pos2x = (int)Math.round(intersectionline.pos2.x/pos2s)+this.origindeltax;
						int pos2y = (int)Math.round(intersectionline.pos2.y/pos2s)+this.origindeltay;
						g.setColor(Color.WHITE);
						g.setStroke(new BasicStroke(this.linestroke));
						g.drawLine(pos1x, pos1y, pos2x, pos2y);
					}
				}
			}
		}
	}

	@Override public void actionPerformed(ActionEvent e) {}
	@Override public void keyTyped(KeyEvent e) {}
	@Override public void keyPressed(KeyEvent e) {}
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
}