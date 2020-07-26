package mainPackage;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Elastic;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;


public class TestAnimation {

	private static JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		
		

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestAnimation window = new TestAnimation();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TestAnimation() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1750, 900);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setBackground(new Color(10,10,10));

		jb = new JPanel() {
			private static final long serialVersionUID = 1L;
			protected void paintComponent(Graphics g)
		    {
		        g.setColor( getBackground() );
		        g.fillRect(0, 0, getWidth(), getHeight());
		        super.paintComponent(g);
		    }
		};
		jb.setBounds(700,500,300,300);
		jb.setOpaque(false);
		jb.setBackground(new Color(200,100,0,20));
		frame.getContentPane().add(jb);
		jb.setLayout(null);
		
		JPanel panel = new JPanel();
//		panel.setOpaque(false);
		panel.setBackground(new Color(0,50,0,20));
		panel.setBounds(38, 51, 175, 158);
		jb.add(panel);
//		jb.add(new JLabel("abc"));
		jb.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
//				animate(jb, new Point(700,500), new Point(-600,-400), 35, 16);
//				System.out.println(jb.getLocation().x);
				if(jb.getLocation().x == 700)
					move(jb, new Point(700,500), new Point(-600,-400), 120, 2);
				else move(jb, new Point(100,100), new Point(600,400), 120, 2);
					
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				fadeAll(jb, 125, -105, 60, 2);
				scaleAll(jb,320,-20,60,2,jb.getLocation());
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				fadeAll(jb, 20, 105, 60, 2);
				scaleAll(jb,300,20,60,2,jb.getLocation());
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
//		System.out.println(System.currentTimeMillis());


	JPanel jb;
	int x = 0;
	private void scale(Component component, int oldValue, int newValue, int frames, int interval, Point oldCoordinate)
	{
		
		new Timer(interval, new ActionListener() {
	        int t = 0;
	        public void actionPerformed(ActionEvent e) {
	        	int abc = (int)easeout(t, oldValue, newValue, frames);
	            component.setSize(abc,abc);
	            component.setLocation(oldCoordinate.x - ((abc - oldValue)/2), oldCoordinate.y - ((abc - oldValue)/2));
	            if (t != frames)
	                t++;
	            else
	                ((Timer)e.getSource()).stop();
	        }
	    }).start();
	}
	private void scaleAll(JComponent component, int oldValue, int newValue, int frames, int interval, Point oldCoordinate)
	{
		Component[] components = component.getComponents();
		for (Component comp : components)  {
			scale(comp,oldValue,newValue,frames,interval,oldCoordinate);
	    }
		scale(component,oldValue,newValue,frames,interval,oldCoordinate);
	}
	private void move(JComponent component, Point oldPoint, Point newPoint, int frames, int interval) {
	    Rectangle compBounds = component.getBounds();
	    
	    new Timer(interval, new ActionListener() {
	        int t = 0;
	        public void actionPerformed(ActionEvent e) {
	            component.setBounds((int)easeout(t, oldPoint.x, newPoint.x, frames),
	            					(int)easeout(t, oldPoint.y, newPoint.y, frames),
	                                compBounds.width,
	                                compBounds.height);
	            
	            if (t != frames)
	                t++;
	            else
	                ((Timer)e.getSource()).stop();
	        }
	    }).start();
	}
	
	private double easeout (double t2, double b2, double c2, double d2) {
		t2 /= d2/2;
		if (t2 < 1) return c2/2*t2*t2*t2*t2*t2 + b2;
		t2 -= 2;
		return c2/2*(t2*t2*t2*t2*t2 + 2) + b2;
	}

	private void fadeAll(JComponent component, int startValue, int endValue, int frames, int interval)
	{
		Component[] components = component.getComponents();
//		System.out.println(components.length);
		
		for (Component comp : components)  {
			fade(comp, startValue, endValue, frames, interval);
	    }
		fade(component, startValue, endValue, frames, interval);
	}

	private void fade(Component component, int startValue, int endValue, int frames, int interval)
	{
		new Timer(interval, new ActionListener() {
	        int t = 0;
	        public void actionPerformed(ActionEvent e) {
        		x = (int)easeout(t,startValue, endValue, frames);
        		component.setBackground(new Color(component.getBackground().getRed(),component.getBackground().getGreen(),component.getBackground().getGreen(),x));
	        	component.repaint();
	            if (t != frames)
	            {
	            	t++;
	            }
	            else
	            {
	            	((Timer)e.getSource()).stop();
	            }
	        }
	    }).start();
	}
	int map(int x, int in_min, int in_max, int out_min, int out_max) {

		  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
}











class animation extends JPanel implements ActionListener 
{
	PaintUtils paintUtility = new PaintUtils();
	private static final long serialVersionUID = 1L;
	Timer tm;
	Rectangle rec = new Rectangle(0, 0, 100,100);
	double t=0, b = 5, c=250, d = 60; 	//d is the total frames here. 
										//t is the start frame which increments every millisecond or Timer(16,this) 16 here
										//b is the first value and c is the last value 
										//the function just returns a value every time (the only variable changing is t
	int x = 5, velx = 2, i =0;
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(x);
		if(x <= 250 && x >= 5)
		{
			t++;
			x = (int) easeout(t, b, c, d);
			setBackground(new Color(20,20,24,x));
		}
		else 
		{
			System.out.println(System.currentTimeMillis());
			tm.stop();
		}
	
		repaint();
		
	}
	
	private double easeout (double t2, double b2, double c2, double d2) {
		return c2*(t2/=d2)*t2*t2 + b2;
		
	}
	
	public animation()
	{
		setVisible(true);
//		setSize(20,20);
		tm = new Timer(8,this);
	}

	protected void paintComponent(Graphics g)
	{
		g.setColor( getBackground() );
		g.fillRect(0, 0, getWidth(), getHeight());
		super.paintComponent(g);
		setBackground(new Color(20,20,24,x));
		if(x <= 250 && x >= 5)
			tm.start();
		else 
		{
			System.out.println(System.currentTimeMillis());
			tm.stop();
		}
	}
	
}











class animate extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	String type = "";
	double t=0, b = 0, c=0, d = 60, tpf = 10;
	int x = 0;
	Timer tm;
	int[] startValue, endValue;
	JPanel panel;
	public animate(JPanel panel, String type, int totalFrames, int timePerFrame, int[] startValue, int[] endValue)
	{
		this.type = type;
		d = totalFrames;
		tpf = timePerFrame;
		this.startValue = startValue;
		this.endValue = endValue;
		this.panel = panel;
		tm = new Timer(timePerFrame,this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch(type)
		{
		case "fadeIn":
			if(x >= startValue[0] && x <= endValue[0])
			{
				t++;
				x = (int)easeout(t, b, c, d);
			}
			else tm.stop();
			panel.repaint();
			break;
		case "fadeOut":
			if(x >= startValue[0] && x <= endValue[0])
			{
				t++;
				x = (int)easeout(t, b, c, d);
			}
			else tm.stop();
			panel.repaint();
			break;
		case "move":
			
			break;
		default:		
			break;
		}
		
	}
	private double easeout (double t2, double b2, double c2, double d2) {
		return c2*(t2/=d2)*t2*t2 + b2;
	}
	protected void paintComponent(Graphics g)
	{
		
		switch(type)
		{
		case "fadeIn":
			g.setColor( getBackground() );
			g.fillRect(0, 0, getWidth(), getHeight());
			super.paintComponent(g);
			setBackground(new Color(20,20,24,x));
			if(x >= startValue[0] && x <= endValue[0])
				tm.start();
			else 
			{
				System.out.println(System.currentTimeMillis());
				tm.stop();
			}
			break;
		case "fadeOut":
			
			break;
		case "move":
			
			break;
		default:		
			break;
		}
	}
}


class PaintUtils {

	  /** Four shades of white, each with increasing opacity. */
	  public final static Color[] whites = new Color[] {
	      new Color(255,255,255,50),
	      new Color(255,255,255,100),
	      new Color(255,255,255,150)
	  };
	  
	  /** Four shades of black, each with increasing opacity. */
	  public final static Color[] blacks = new Color[] {
	      new Color(0,0,0,50),
	      new Color(0,0,0,100),
	      new Color(0,0,0,150)
	  };
	  
	  /** @return the color used to indicate when a component has
	   * focus.  By default this uses the color (64,113,167), but you can
	   * override this by calling:
	   * <BR><code>UIManager.put("focusRing",customColor);</code>
	   */
	  public static Color getFocusRingColor() {
	    Object obj = UIManager.getColor("focusRing");
	    if(obj instanceof Color)
	      return (Color)obj;
	    return new Color(64,113,167);
	  }
	  
	  /** Paints 3 different strokes around a shape to indicate focus.
	   * The widest stroke is the most transparent, so this achieves a nice
	   * "glow" effect.
	   * <P>The catch is that you have to render this underneath the shape,
	   * and the shape should be filled completely.
	   * 
	   * @param g the graphics to paint to
	   * @param shape the shape to outline
	   * @param biggestStroke the widest stroke to use.
	   */
	  public static void paintFocus(Graphics2D g,Shape shape,int biggestStroke) {
	    Color focusColor = getFocusRingColor();
	    Color[] focusArray = new Color[] {
	      new Color(focusColor.getRed(), focusColor.getGreen(), focusColor.getBlue(),255),
	      new Color(focusColor.getRed(), focusColor.getGreen(), focusColor.getBlue(),170),
	      new Color(focusColor.getRed(), focusColor.getGreen(), focusColor.getBlue(),110) 
	    };
	    g.setStroke(new BasicStroke(biggestStroke));
	    g.setColor(focusArray[2]);
	    g.draw(shape);
	    g.setStroke(new BasicStroke(biggestStroke-1));
	    g.setColor(focusArray[1]);
	    g.draw(shape);
	    g.setStroke(new BasicStroke(biggestStroke-2));
	    g.setColor(focusArray[0]);
	    g.draw(shape);
	    g.setStroke(new BasicStroke(1));
	  }
	  
	  /** Uses translucent shades of white and black to draw highlights
	   * and shadows around a rectangle, and then frames the rectangle
	   * with a shade of gray (120).
	   * <P>This should be called to add a finishing touch on top of
	   * existing graphics.
	   * @param g the graphics to paint to.
	   * @param r the rectangle to paint.
	   */
	  public static void drawBevel(Graphics g,Rectangle r) {
	    drawColors(blacks,g, r.x, r.y+r.height, r.x+r.width, r.y+r.height, SwingConstants.SOUTH);
	    drawColors(blacks,g, r.x+r.width, r.y, r.x+r.width, r.y+r.height, SwingConstants.EAST);

	    drawColors(whites,g, r.x, r.y, r.x+r.width, r.y, SwingConstants.NORTH);
	    drawColors(whites,g, r.x, r.y, r.x, r.y+r.height, SwingConstants.WEST);
	    
	    g.setColor(new Color(120, 120, 120));
	    g.drawRect(r.x, r.y, r.width, r.height);
	  }
	  
	  private static void drawColors(Color[] colors,Graphics g,int x1,int y1,int x2,int y2,int direction) {
	    for(int a = 0; a<colors.length; a++) {
	      g.setColor(colors[colors.length-a-1]);
	      if(direction==SwingConstants.SOUTH) {
	        g.drawLine(x1, y1-a, x2, y2-a);
	      } else if(direction==SwingConstants.NORTH) {
	        g.drawLine(x1, y1+a, x2, y2+a);
	      } else if(direction==SwingConstants.EAST) {
	        g.drawLine(x1-a, y1, x2-a, y2);
	      } else if(direction==SwingConstants.WEST) {
	        g.drawLine(x1+a, y1, x2+a, y2);
	      }
	    }
	  }
	}