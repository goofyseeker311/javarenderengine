package fi.jkauppa.javarenderengine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class JavaRenderEngine extends JFrame implements KeyListener {
	private static final long serialVersionUID = 1L;
	private RenderPanel renderpanel = new RenderPanel();
	
	private class RenderPanel extends JPanel implements ActionListener {
		private static final long serialVersionUID = 1L;
		private final int fpstarget = 60;
		private final int fpstargetdelay = (int)Math.floor(1000.0f/(2.0f*(double)fpstarget));
		private final Timer timer = new Timer(fpstargetdelay,this);
		private long lastupdate = System.currentTimeMillis();
		public RenderPanel() {
			timer.start();
		}
		@Override public void paintComponent(Graphics g) {
			long newupdate = System.currentTimeMillis();
			long ticktime = newupdate-lastupdate;
			double ticktimefps = 1000.0f/(double)ticktime;
			lastupdate = newupdate; 
			System.out.println("ticktime: "+ticktime+", fps: "+ticktimefps);
			Graphics2D g2 = (Graphics2D)g;
			g2.setColor(Color.GREEN);
			g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			this.repaint();
		}
	}

	public JavaRenderEngine() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addKeyListener(this);
		this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH);
		this.setUndecorated(true);
		this.setResizable(false);
		this.setVisible(true);
		this.add(renderpanel);
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
	}
}
