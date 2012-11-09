import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;

public class Curves extends Applet implements Runnable, MouseListener, KeyListener
{
	public static final int width = 640, height = 640, refreshRate = 20;
	private static final long serialVersionUID = 1L;
	private boolean running;
	private BufferedImage buffer;
	private Graphics2D bufferGraphics;
	private Thread thread;
	private ArrayList<Point> points, curve;
	private boolean animate;
	private int frame;
	private Calculations calc;
	private double length;
	
	public Curves()
	{
		addMouseListener(this);
		addKeyListener(this);
		buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		bufferGraphics = buffer.createGraphics();
		points = new ArrayList<Point>();
		curve = new ArrayList<Point>();
		calc = new Calculations();
		calc.start();
	}
	
	public static void main(String args[])
	{ 
		JFrame frame = new JFrame("Curves");
		Curves game = new Curves();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setBackground(Color.black);
		frame.setLayout(new BorderLayout());
		frame.setSize(width, height);
		frame.setResizable(false);
		frame.add(game, BorderLayout.CENTER);
		frame.setVisible(true);
		
		game.start();
	}
	public void start()
	{
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public void run()
	{
		while(running)
		{
			logic();
			repaint();
			try
			{
				Thread.sleep(refreshRate);
			}
			catch(Exception e){}
		}
	}
	public void logic()
	{
		/*
		time += 0.01;
		time %= 1;
		if(points.size() >= 2)
		{
			previousX = x;
			previousY = y;
			x = (int) calcX(time, points, 0, points.size()-1);
			y = (int) calcY(time, points, 0, points.size()-1);
		}
		*/
	}
	public void paint(Graphics g)
	{
		//bufferGraphics.clearRect(0, 0, width, height);
		if(animate)
		{
			if(curve.size()!=0)
			{
				bufferGraphics.setColor(Color.white);
				if(frame != curve.size()-1 && frame != 0)
					bufferGraphics.drawLine(curve.get(frame-1).x, curve.get(frame-1).y, curve.get(frame).x, curve.get(frame).y);
				frame++;
				frame %= curve.size();
				if(frame == 0)
				{
					bufferGraphics.clearRect(0, 0, width, height);
				}
				bufferGraphics.clearRect(width/2 - 30, height - 75, 65, 15);
				bufferGraphics.drawString("t = " + ((double)frame+1)/curve.size(), width/2-20, height-65);
			}
		}
		else
		{
			bufferGraphics.setColor(Color.white);
			for(int i = 1; i < curve.size(); i++)
			{
				bufferGraphics.drawLine(curve.get(i-1).x, curve.get(i-1).y, curve.get(i).x, curve.get(i).y);
			}
			if(points.size()>2)
			{
				int color = 0x0000ff;
				for(int i = 1; i < points.size(); i++)
				{
					bufferGraphics.setColor(new Color(color));
					bufferGraphics.drawLine(points.get(i-1).x, points.get(i-1).y, points.get(i).x, points.get(i).y);
				}
			}
		}
		bufferGraphics.drawString("Length: "+length, width/2-20, 20);
		g.drawImage(buffer, 0, 0, this);
	}
	public void update(Graphics g)
    {
         paint(g);
    }

	public void mouseClicked(MouseEvent arg0) 
	{
		
	}
	public void mousePressed(MouseEvent e) 
	{
		if(points.size()>1)
		{
			insert(new Point(e.getX(), e.getY()));
		}
		else
		{
			points.add(new Point(e.getX(), e.getY()));
		}
		if(points.size()>1)
		{
			curve.clear();
			calc.startUpdate();
		}
		frame = 0;
		bufferGraphics.clearRect(0, 0, width, height);
	}
	public void insert(Point p)
	{
		for(int i = 1; i < points.size(); i++)
		{
			Point a = points.get(i-1);
			Point b = points.get(i);
			if(p.x > a.x && p.x < b.x || p.y > a.y && p.y < b.y)
			{
				points.add(i, p);
				return;
			}
		}
	}
	public void mouseReleased(MouseEvent arg0) 
	{
		
	}
	public void mouseEntered(MouseEvent arg0){}
	public void mouseExited(MouseEvent arg0){}
	public void keyPressed(KeyEvent e)
	{
		int keyCode = e.getKeyCode();
		if(keyCode == KeyEvent.VK_A)
		{
			animate = !animate;
			frame = 0;
			bufferGraphics.clearRect(0, 0, width, height);
		}
		if(keyCode == KeyEvent.VK_R)
		{
			points.clear();
			curve.clear();
			bufferGraphics.clearRect(0, 0, width, height);
		}
	}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {} 
	
	class Calculations implements Runnable
	{
		private boolean update;
		
		public void start()
		{
			new Thread(this).start();
		}
		public void run() 
		{
			while(true)
			{
				if(update)
				{
					update();
				}
				System.out.print("");
			}
		}
		public void startUpdate()
		{
			update = true;
		}
		public void update()
		{
			for(double t = 0; t <= 1; t += 0.01)
			{
				int x = (int) calcX(t, points, 0, points.size()-1);
				int y = (int) calcY(t, points, 0, points.size()-1);
				curve.add(new Point(x, y));
			}
			length = 0;
			for(int i = 0; i < curve.size()-1; i++)
			{
				Point p1 = curve.get(i);
				Point p2 = curve.get(i+1);
				length += Math.sqrt(Math.pow(p2.x-p1.x, 2) + Math.pow(p2.y-p1.y, 2));
			}
			update = false;
		}
		public double calcX(double t, ArrayList<Point> p, int start, int finish)
		{
			if(start == finish)
			{
				return p.get(start).x;
			}
			return (1-t)*calcX(t, p, start, finish-1)+t*calcX(t, p, start+1, finish);
		}
		public double calcY(double t, ArrayList<Point> p, int start, int finish)
		{
			if(start == finish)
			{
				return p.get(start).y;
			}
			return (1-t)*calcY(t, p, start, finish-1)+t*calcY(t, p, start+1, finish);
		}
	}
}