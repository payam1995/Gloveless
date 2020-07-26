package mainPackage;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class FadeAnimation {//extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

//	Timer tm;
//	int t, b = 0, c, d = 2000; 
//	
//	public void fadeIn (JPanel panel)
//	{
//		
//	}
//	
//	public void fadeOut ()
//	{
//		
//	}
//	
//	public void paintComponent(Graphics g)
//	{
//		super.paintComponent(g);
//		
//		tm.start();
//	}
//	
//	@Override
//	public void actionPerformed(ActionEvent e) {
//		t /= d;
//		t--;
//		c = c*(t*t*t + 1) + b;
//		
//	}
//	
//	public FadeAnimation ()
//	{
//		tm = new Timer(10, this);
//		
//		
//	}
	public static void main(String[] args) {
		JFrame frame = new JFrame("abc");
		frame.setBounds(300,200,500,500);
		frame.setLayout(null);
		frame.setBackground(new Color(51,82,30));
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		JPanel button = new JPanel();
		button.setBounds(250,250,20,20);
		button.setBackground(new Color(20,20,20));
		
		
		
		button.setVisible(true);
		
		
		
		frame.getContentPane().add((button));
//		for(int i = 255; i> 0; i--)
//		{
//			button.setBackground(new Color(20,20,20,i));
//		}
		
	}
	
	
	
	
	

}
