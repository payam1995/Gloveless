package mainPackage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class MappingThumbnails extends JPanel {
	
	private static final long serialVersionUID = 1285823728180791556L;
	public String text = "Untitled";
	JLabel lbl;
	public MappingThumbnails()
	{
		
		
		setSize(130, 100);
		Border border = BorderFactory.createDashedBorder(null, 5.0f, 2.0f, 2.5f, true);
		setBorder(border);
		setBackground(new Color(107, 114, 114));
		setLayout(new BorderLayout());
		
		lbl = new JLabel(text);
		lbl.setFont(new Font("Arcon",Font.PLAIN, 15));
		lbl.setHorizontalAlignment(JLabel.CENTER);
		lbl.setVerticalAlignment(JLabel.CENTER);
		lbl.setForeground(new Color(240, 240, 240));
		
		add(lbl);
		setVisible(true);
		
	}
	
	public void changeText(String t)
	{
		lbl.setText("<html><div style='text-align: center;'>"+ t + "<html>");
		lbl.setFont(new Font("Arcon",Font.PLAIN, 15));
		lbl.setHorizontalAlignment(JLabel.CENTER);
		lbl.setVerticalAlignment(JLabel.CENTER);
		repaint();
	}
}
