package fi.jkauppa.javarenderengine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import fi.jkauppa.javarenderengine.JavaRenderEngine.AppHandlerPanel;

public class EditorApp extends AppHandlerPanel {
	private static final long serialVersionUID = 1L;
	public EditorApp() {}
	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		this.renderwidth = this.getWidth();
		this.renderheight = this.getHeight();
		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(0, 0, this.renderwidth, this.renderheight);
	}
	@Override public void tick() {}
	
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
