package mainPackage;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



public class BallPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JLabel currentPositionXlabel = new JLabel("");
	private final JLabel lblO1 = new JLabel("o");
	private int fontSize = 15;
	private JLabel rangeSliderLabel1 = new JLabel();
    private JLabel rangeSliderValue1 = new JLabel();
    private JLabel rangeSliderLabel2 = new JLabel();
    private JLabel rangeSliderValue2 = new JLabel();
    private RangeSlider rangeSlider = new RangeSlider();
    
	public BallPanel()
	{
		setBounds(0, 0, 235, 140);
		setLayout(null);
		
		lblO1.setFont(new Font("Tahoma", Font.PLAIN, fontSize));
        lblO1.setHorizontalAlignment(SwingConstants.CENTER);
        lblO1.setBounds(95, 40, 30, 27);
        add(lblO1);
        
        
        
        currentPositionXlabel.setFont(new Font("Arial", Font.PLAIN, 17));
        currentPositionXlabel.setHorizontalAlignment(SwingConstants.CENTER);
        currentPositionXlabel.setBounds(77, 11, 63, 27);
        
        add(currentPositionXlabel);
        
        rangeSlider.setBounds(0, 79, 235, 38);
        rangeSlider.setPreferredSize(new Dimension(235, 38));
        rangeSlider.setMinimum(-200);
        rangeSlider.setMaximum(200);
        
        // Add listener to update display.
        rangeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                RangeSlider slider = (RangeSlider) e.getSource();
                rangeSliderValue1.setText(String.valueOf(slider.getValue()));
                rangeSliderValue2.setText(String.valueOf(slider.getUpperValue()));
            }
        });
        
        
        
        rangeSliderLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        rangeSliderLabel1.setFont(new Font("Arial", Font.PLAIN, 14));
        rangeSliderLabel1.setSize(40, 27);
        rangeSliderLabel1.setLocation(0, 113);

        rangeSliderLabel1.setText("Min:");
        add(rangeSliderLabel1);
        rangeSliderLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        rangeSliderLabel2.setFont(new Font("Arial", Font.PLAIN, 14));
        rangeSliderLabel2.setSize(40, 28);
        rangeSliderLabel2.setLocation(140, 112);
        rangeSliderLabel2.setText("Max:");
        add(rangeSliderLabel2);
        rangeSliderValue1.setFont(new Font("Arial", Font.PLAIN, 14));
        rangeSliderValue1.setSize(63, 27);
        rangeSliderValue1.setLocation(37, 113);
        rangeSliderValue1.setHorizontalAlignment(SwingConstants.LEFT);
        add(rangeSliderValue1);
        
        // Initialize value display.
        rangeSliderValue1.setText(String.valueOf(rangeSlider.getValue()));
        rangeSliderValue2.setFont(new Font("Arial", Font.PLAIN, 14));
        rangeSliderValue2.setSize(50, 27);
        rangeSliderValue2.setLocation(180, 113);
        rangeSliderValue2.setHorizontalAlignment(JLabel.LEFT);
        add(rangeSliderValue2);
        rangeSliderValue2.setText(String.valueOf(rangeSlider.getUpperValue()));

        add(rangeSlider);
        

        rangeSlider.setValue(-200);
        rangeSlider.setUpperValue(200);
        
        setVisible(false);
	}
}
