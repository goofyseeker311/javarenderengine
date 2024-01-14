package fi.jkauppa.javarenderengine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import fi.jkauppa.javarenderengine.JavaRenderEngine.AppHandlerPanel;

public class GameApp extends AppHandlerPanel {
	private static final long serialVersionUID = 1L;
	public GameApp() {}
	@Override public void renderWindow(Graphics2D g, int renderwidth, int renderheight, double deltatimesec, double deltatimefps) {
		g.setComposite(AlphaComposite.Src);
		g.setColor(Color.RED);
		g.setPaint(null);
		g.fillRect(0, 0, renderwidth, renderheight);
	}
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
	@Override public void timerTick() {}
}