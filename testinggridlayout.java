package mainPackage;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;

public class testinggridlayout extends JFrame {

	public static void main(String[] args) {
		JFrame testframe = new JFrame("sfdkjl");
		testframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		testframe.setVisible(true);
		testframe.setSize(600, 690);
		testframe.setResizable(false);
		
		JPanel parentPanel = new JPanel();
		parentPanel.setBackground(new Color(107, 114, 114));
		parentPanel.setBounds(0, 0, 600, 690);
		parentPanel.setVisible(true);
		parentPanel.setLayout(null);
		
		testframe.add(parentPanel);
		
		
		
	}

}
