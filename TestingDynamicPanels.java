package mainPackage;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

public class TestingDynamicPanels {

	private int i;
    private JPanel listContainer;

    private void initUI() {
        final JFrame frame = new JFrame();
        JPanel mainPanel = new JPanel();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        List<JPanel> maps = new LinkedList<JPanel>();
        frame.add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        JButton button = new JButton("Add");
        JButton rbutton = new JButton("remove");
        
    
        
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel newPanel = new JPanel();
                newPanel.add(new JLabel("Label " + i++));
                maps.add(newPanel);
                
                mainPanel.add(newPanel);
                mainPanel.revalidate();
                // Scroll down to last added panel
//                SwingUtilities.invokeLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        newPanel.scrollRectToVisible(newPanel.getBounds());
//                    }
//                });
            }
        });
        
        rbutton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				mainPanel.remove(maps.get(0));
				mainPanel.revalidate();
				maps.remove(0);
				
			}
		});
        frame.add(button, BorderLayout.PAGE_END);
        frame.add(rbutton, BorderLayout.PAGE_START);
        frame.setSize(400, 500);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new TestingDynamicPanels().initUI();
            }
        });
    }
}
