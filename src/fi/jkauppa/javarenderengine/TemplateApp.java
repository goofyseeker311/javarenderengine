package fi.jkauppa.javarenderengine;

import java.awt.Graphics2D;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import fi.jkauppa.javarenderengine.JavaRenderEngine.AppHandler;

public class TemplateApp implements AppHandler {
	@Override public void renderWindow(Graphics2D g, int renderwidth, int renderheight, double deltatimesec, double deltatimefps) {}
	@Override public void actionPerformed(ActionEvent e) {}
	@Override public void componentResized(ComponentEvent e) {}
	@Override public void componentMoved(ComponentEvent e) {}
	@Override public void componentShown(ComponentEvent e) {}
	@Override public void componentHidden(ComponentEvent e) {}
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