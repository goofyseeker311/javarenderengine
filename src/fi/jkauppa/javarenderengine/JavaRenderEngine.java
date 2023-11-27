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
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class JavaRenderEngine extends JFrame implements KeyListener {
	private static final long serialVersionUID = 1L;
	private RenderPanel renderpanel = new RenderPanel();
	private boolean windowedmode = false;
	
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
				Graphics2D gfx = (Graphics2D)renderbuffer.getGraphics();
				gfx.setColor(Color.GREEN);
				gfx.fillRect(0, 0, this.getWidth(), this.getHeight());
				g2.drawImage(renderbuffer, 0, 0, null);
			}
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			this.repaint();
		}
		
		@Override public void componentMoved(ComponentEvent e) {}
		@Override public void componentShown(ComponentEvent e) {}
		@Override public void componentHidden(ComponentEvent e) {}
		
		@Override
		public void componentResized(ComponentEvent e) {
			renderbuffer = new BufferedImage(this.getWidth(),this.getWidth(),BufferedImage.TYPE_INT_ARGB);
		}
	}

	public JavaRenderEngine() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addKeyListener(this);
		this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH);
		this.setUndecorated(true);
		this.add(renderpanel);
		this.setVisible(true);
	}

	public static void main(String[] args) {
		JavaRenderEngine app = new JavaRenderEngine();
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
		    int offmask = 0;
		    if ((e.getModifiersEx() & (onmask | offmask)) == onmask) {
	    		this.dispose();
		    	if (!windowedmode) {
		    		windowedmode = true;
		    		this.setExtendedState(this.getExtendedState()&~JFrame.MAXIMIZED_BOTH);
		    		this.setUndecorated(false);
		    	}else {
		    		windowedmode = false;
		    		this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH);
		    		this.setUndecorated(true);
		    	}
	    		this.setVisible(true);
		    }
		}
	}
}
