package mainPackage;

import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.*;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.ComponentOrientation;

import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;

import javax.swing.UIManager;
import javax.swing.border.Border;

import com.leapmotion.leap.Controller;


//import testingMidi.SampleListener;

public class MainWindow {

	private JFrame frame;
	private JTextField fileNameTextField;
	private boolean leapStatusOn = false;
	public static Thread mainLeapThread = null;
	public static SampleListener listener = null;
    static Controller controller = null;
    static Receiver rcvr = null;	
    static JPanel mappingInnerPanel = null;
    static JPanel BackgroundPanel = null;
    static JLabel plusButton = null;
    static JPanel glassPanelMapping, glassPanelSidebar = null;
	private boolean instrumentPanelActive = false, 
			mappingPanelActive = false, 
			createInstrumentPanelActive = false,
			miscPanelActive = false;
	MainWindow thisMainWindow = null;
	JPanel mappingButton;
	public static boolean hmdMode;
	/**
	 * Launch the application.
	 * @throws MidiUnavailableException 
	 */
	public static void main(String[] args) throws MidiUnavailableException {
		loadMyStuff();
		
		listener = new SampleListener();
        controller = new Controller();
//        controller.setPolicy(Controller.PolicyFlag.POLICY_OPTIMIZE_HMD);hmdMode=true;				//	VRRR mode
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	        public void run() {
	            System.out.println("In shutdown hook");
	            controller.removeListener(listener);
	        }
	    }, "Shutdown-thread"));
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public static List<MappingProperties> maps;
	public static List<MappingThumbnails> mapThumbnails;
	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public MainWindow() throws IOException {
		initialize();
	}

	
	//130x100
	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 */
	private void initialize() throws IOException {
		thisMainWindow = this;
		frame = new JFrame();
//		frame.getContentPane().setBackground(new Color(85, 107, 47));
		frame.getContentPane().setBackground(new Color(70,70,70));
		frame.setResizable(false);
		frame.setBounds(10, 10, 1280, 720);
		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		//make the window movable
		FrameDragListener frameDragListener = new FrameDragListener(frame);
		frame.addMouseListener(frameDragListener);
        frame.addMouseMotionListener(frameDragListener);

        //frame.pack();
        frame.setLocationRelativeTo(null);
	
		

		
		JPanel mappingPanel = new JPanel();
//		mappingPanel.setBackground(new Color(47, 114, 114));
		mappingPanel.setBackground(new Color(20,78,96));
		mappingPanel.setBounds(130, 30, 550, 690);
		frame.getContentPane().add(mappingPanel);
		mappingPanel.setVisible(false);				//-----------------------------------make this false
		
		mappingInnerPanel = new JPanel();
//		mappingInnerPanel.setBackground(new Color(47, 114, 114));
		mappingInnerPanel.setBackground(new Color(20,78,96));
		mappingInnerPanel.setBounds(0, 0, 550, 105);
		
		mappingInnerPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		GridLayout gbl_mappingPanel = new GridLayout(0,4,5,5);
		mappingInnerPanel.setLayout(gbl_mappingPanel);
		
		maps = new LinkedList<MappingProperties>();
		mapThumbnails = new LinkedList<MappingThumbnails>();
		
		
		final String plusSymbolString = "<html><body><font face='STIXGeneral'>\uFF0B</font></body></html>";
		plusButton = new JLabel(plusSymbolString, SwingConstants.CENTER);
		
		
		plusButton.setHorizontalAlignment(SwingConstants.CENTER);
		plusButton.setVerticalAlignment(SwingConstants.CENTER);
		plusButton.setFont(new Font("Arcon",Font.BOLD, 100));
		Border border = BorderFactory.createDashedBorder(null, 5.0f, 2.0f, 2.5f, true);
		plusButton.setBorder(border);
		mappingPanel.setLayout(null);
		
//		refreshMappingInnerPanel(maps, plusButton, mappingInnerPanel);
		mappingInnerPanel.add(plusButton);
		
//		mappingInnerPanel.setBounds(130, 30, 295, 100);
		mappingPanel.add(mappingInnerPanel);

//		abc.setVisible(true);

		
		
		
		
		JPanel instrumentPanel = new JPanel();
		instrumentPanel.setLayout(null);
		instrumentPanel.setBackground(new Color(47, 114, 114));
		instrumentPanel.setBounds(130, 30, 1150, 690);
		frame.getContentPane().add(instrumentPanel);
		instrumentPanel.setVisible(false);
		
		JPanel createNewInstrumentPanel = new JPanel();
		createNewInstrumentPanel.setBackground(new Color(47, 114, 114));
		createNewInstrumentPanel.setBounds(130, 30, 1150, 690);
		frame.getContentPane().add(createNewInstrumentPanel);
		createNewInstrumentPanel.setLayout(null);
		createNewInstrumentPanel.setVisible(false);
		
		JPanel miscPanel = new JPanel();
		miscPanel.setBackground(new Color(47, 114, 114));
//		miscPanel.setBounds(130, 30, 1150, 690);
		miscPanel.setBounds(130, 30, 0, 0);
		frame.getContentPane().add(miscPanel);
		miscPanel.setLayout(null);
		miscPanel.setVisible(false);
		
		
		
		
		JPanel leapSwitchPanel = new JPanel();
		leapSwitchPanel.setBounds(950, 600, 130, 40);
		frame.getContentPane().add(leapSwitchPanel);
		leapSwitchPanel.setBackground(new Color(204, 102, 102));
		leapSwitchPanel.addMouseListener(new MouseListener() {
			

			public void mouseReleased(MouseEvent e) {
				leapStatusOn = !leapStatusOn;
				if(leapStatusOn)
				{
					leapSwitchPanel.setBackground(new Color(51, 204, 153));
					if(controller.isPaused())
					{
						System.out.println("resume");
						controller.setPaused(false);
//						mainLeapThread.start();
					}
					else 
					{

						controller.addListener(listener);
						System.out.println("start");
//						mainLeapThread.resume();
					}
				}
					
				else
				{
					leapSwitchPanel.setBackground(new Color(204, 102, 102));
					controller.setPaused(true);
					System.out.println("pause");
//					mainLeapThread.suspend();
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		BackgroundPanel = new JPanel();
//		BackgroundPanel.setBackground(new Color(47, 74, 104));
		BackgroundPanel.setBackground(new Color(25, 46, 68));
		
		BackgroundPanel.setBounds(130, 30, 1150, 690);
//		BackgroundPanel.setBounds(130, 30, 0, 0);
		BackgroundPanel.setLayout(null);
		frame.getContentPane().add(BackgroundPanel);
		
		
		
		
		
		JPanel openButton = new JPanel();
		openButton.setBounds(0, 680, 130, 40);
		frame.getContentPane().add(openButton);
		openButton.setBackground(new Color(57, 64, 74));
		
		JPanel saveButton = new JPanel();
		saveButton.setBounds(0, 629, 130, 40);
		frame.getContentPane().add(saveButton);
		saveButton.setBackground(new Color(57, 64, 74));
		
		JPanel instrumentButton = new JPanel();
		instrumentButton.setBounds(0, 0, 130, 110);
		frame.getContentPane().add(instrumentButton);
		instrumentButton.setBackground(new Color(47, 54, 64));
		instrumentButton.setLayout(null);
		
		
		JLabel lblInstruments = new JLabel("Instruments");
		lblInstruments.setHorizontalAlignment(SwingConstants.CENTER);
		lblInstruments.setBounds(0, 72, 130, 38);
		lblInstruments.setFont(new Font("Arcon", Font.PLAIN, 24));
		lblInstruments.setForeground(Color.LIGHT_GRAY);
		instrumentButton.add(lblInstruments);
		
		
		
		instrumentButton.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				instrumentButton.setBackground(new Color(47, 114, 114));
				instrumentPanelActive = !instrumentPanelActive;
				
				mappingPanelActive = false;
				mappingPanel.setVisible(false);
				mappingButton.setBackground(new Color(47, 54, 64));
				
				createInstrumentPanelActive = false;
				createNewInstrumentPanel.setVisible(false);
				
				miscPanelActive = false;
				miscPanel.setVisible(false);
				
				if(instrumentPanelActive)
				{
					instrumentPanel.setVisible(true);
				}
				else
				{
					instrumentPanel.setVisible(false);
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				instrumentButton.setBackground(new Color(47, 114, 114));
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				if(!instrumentPanelActive)
					instrumentButton.setBackground(new Color(47, 54, 64));
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				if(!instrumentPanelActive)
					instrumentButton.setBackground(new Color(114, 114, 64));
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		
		mappingButton = new JPanel();
		mappingButton.setBounds(0, 0, 130, 110);
		frame.getContentPane().add(mappingButton);
		mappingButton.setBackground(new Color(47, 54, 64));
		mappingButton.setLayout(null);
		
		
		JLabel lblMappings = new JLabel("Mappings");
		lblMappings.setBounds(0, 81, 128, 29);
		lblMappings.setHorizontalAlignment(SwingConstants.CENTER);
		lblMappings.setForeground(Color.LIGHT_GRAY);
		lblMappings.setFont(new Font("Arcon", Font.PLAIN, 24));
		mappingButton.add(lblMappings);
		
		mappingButton.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				mappingButton.setBackground(new Color(47, 114, 114));
				mappingPanelActive = !mappingPanelActive;
				
				instrumentPanelActive = false;
				instrumentPanel.setVisible(false);
				instrumentButton.setBackground(new Color(47, 54, 64));
				
				createInstrumentPanelActive = false;
				createNewInstrumentPanel.setVisible(false);
				
				
				miscPanelActive = false;
				miscPanel.setVisible(false);
				
				
				if(mappingPanelActive)
					mappingPanel.setVisible(true);
				else
					mappingPanel.setVisible(false);
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				mappingButton.setBackground(new Color(47, 114, 114));
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				if(!mappingPanelActive)
					mappingButton.setBackground(new Color(47, 54, 64));
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				if(!mappingPanelActive)
					mappingButton.setBackground(new Color(114, 114, 64));
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		JPanel dummyButton = new JPanel();
		dummyButton.setBounds(0, 0, 130, 110);
		frame.getContentPane().add(dummyButton);
		dummyButton.setBackground(new Color(47, 54, 64));
		dummyButton.setLayout(null);
		dummyButton.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				dummyButton.setBackground(new Color(47, 114, 114));
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				dummyButton.setBackground(new Color(47, 114, 114));
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				dummyButton.setBackground(new Color(47, 54, 64));
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				dummyButton.setBackground(new Color(114, 114, 64));
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		JLabel lblCreateInst = new JLabel("Create Inst.");
		lblCreateInst.setHorizontalAlignment(SwingConstants.CENTER);
		lblCreateInst.setForeground(Color.LIGHT_GRAY);
		lblCreateInst.setFont(new Font("Arcon", Font.PLAIN, 24));
		lblCreateInst.setBounds(0, 81, 128, 29);
		dummyButton.add(lblCreateInst);
		
		JPanel miscButton = new JPanel();
		miscButton.setBounds(0, 0, 130, 110);
		frame.getContentPane().add(miscButton);
		miscButton.setBackground(new Color(47, 54, 64));
		miscButton.setLayout(null);
		miscButton.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				miscButton.setBackground(new Color(47, 114, 114));
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				miscButton.setBackground(new Color(47, 114, 114));
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				miscButton.setBackground(new Color(47, 54, 64));
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				miscButton.setBackground(new Color(114, 114, 64));
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				
				
			}
		});
		
		JLabel lblMisc = new JLabel("Misc.");
		lblMisc.setHorizontalAlignment(SwingConstants.CENTER);
		lblMisc.setForeground(Color.LIGHT_GRAY);
		lblMisc.setFont(new Font("Arcon", Font.PLAIN, 24));
		lblMisc.setBounds(0, 81, 128, 29);
		miscButton.add(lblMisc);
		
		JPanel SideBarPanel = new JPanel();
//		SideBarPanel.setBackground(new Color(25, 46, 68));
		SideBarPanel.setBackground(new Color(25, 76, 98));
//		SideBarPanel.setBounds(0, 0, 130, 720);
		SideBarPanel.setBounds(0, 0, 0, 0);
		frame.getContentPane().add(SideBarPanel);
		SideBarPanel.setLayout(null);
		
		
		JLabel closeButton = new JLabel("X ");
		closeButton.setHorizontalAlignment(SwingConstants.CENTER);
		closeButton.setFont(new Font("Arcon", Font.BOLD, 30));
		closeButton.setToolTipText("Close Program");
		closeButton.setBackground(new Color(150, 17, 47));
		closeButton.setForeground(new Color(150, 17, 47));
		closeButton.setBounds(1250, 0, 30, 30);
		closeButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseExited(MouseEvent e) {
				closeButton.setForeground(new Color(150, 17, 47));
			}			
			@Override
			public void mouseEntered(MouseEvent e) {
				closeButton.setForeground(new Color(240, 30, 40));
			}			
			@Override
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
		});
		frame.getContentPane().add(closeButton);
		
		fileNameTextField = new JTextField();
		fileNameTextField.setFont(new Font("Arcon", Font.PLAIN, 17));
		fileNameTextField.setHorizontalAlignment(SwingConstants.CENTER);
		fileNameTextField.setText("default scene");
		fileNameTextField.setBackground(new Color(85, 107, 47));
		fileNameTextField.setBounds(410, 0, 460, 30);
		frame.getContentPane().add(fileNameTextField);
		fileNameTextField.setColumns(10);
		
		JLabel goToAbletonButton = new JLabel(new ImageIcon("./Images/AbletonLogo.jpg"));
		goToAbletonButton.setBounds(1096, 0, 132, 30);
		goToAbletonButton.setToolTipText("Jump to ableton");
		frame.getContentPane().add(goToAbletonButton);
		
			glassPanelMapping = new JPanel();
			glassPanelMapping.setBounds(130, 30, 550, 690);
			glassPanelMapping.setBackground(new Color(0,0,0,50));
			glassPanelMapping.setVisible(false);
			frame.getContentPane().add(glassPanelMapping);
			
			glassPanelSidebar = new JPanel();
			glassPanelSidebar.setBounds(0, 0, 130, 720);
			glassPanelSidebar.setBackground(new Color(0,0,0,50));
			glassPanelSidebar.setVisible(false);
			frame.getContentPane().add(glassPanelSidebar);
			glassPanelSidebar.addMouseListener(new MouseListener() {
				
				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
			glassPanelMapping.addMouseListener(new MouseListener() {
				
				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
		goToAbletonButton.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		plusButton.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				System.out.println("pressed on plus button");
				//add
				MappingProperties property = new MappingProperties(thisMainWindow);
				property.setBounds(550, 0, 600, 690);
				BackgroundPanel.add(property);
				property.setVisible(false);
				property.setVisible2(true);
				maps.add(property);
				
				property.addComponentListener(new ComponentListener() {
					
					@Override
					public void componentShown(ComponentEvent e) {
						System.out.println("shown");
						property.iJustAppeared();
						glassPanelMapping.setVisible(true);
						glassPanelMapping.requestFocus();
						
//						glassPanelSidebar.setVisible(true);
//						glassPanelSidebar.requestFocus();
					}
					
					@Override
					public void componentResized(ComponentEvent e) {
						
						
					}
					
					@Override
					public void componentMoved(ComponentEvent e) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void componentHidden(ComponentEvent e) {
						// TODO Auto-generated method stub
						System.out.println("hidden");
						
						
						refreshMappingInnerPanel();
						
						glassPanelMapping.setVisible(false);
						glassPanelMapping.requestFocus();
						
//						glassPanelSidebar.setVisible(false);
//						glassPanelSidebar.requestFocus();
					}
				});
				
				
				
				//refresh
				mapThumbnails.add(addToMappingInnerPanel(maps, plusButton, mappingInnerPanel, property));
				
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
		
	}
	
	
	public static void deleteProperty(MappingProperties mp, MappingThumbnails mt)
	{
		mappingInnerPanel.remove(mt);
		BackgroundPanel.remove(mp);
		maps.remove(mp);
		mapThumbnails.remove(mt);
		maps.clear();
		mapThumbnails.clear();
		glassPanelMapping.setVisible(false);
		glassPanelSidebar.setVisible(false);
		mappingInnerPanel.setSize(550, 115*((maps.size()/4)+1));
	}
	
	private void refreshMappingInnerPanel()
	{
		
//		for(int i=0; i<maps.size();i++)
//		{
////			mappingInnerPanel.add(maps.get(i));
//			BackgroundPanel.add(mapThumbnails.get(i));
//		}
//		mappingInnerPanel.add(plusButton);
//		BackgroundPanel.repaint();
//		mappingInnerPanel.repaint();
	}
	
	private MappingThumbnails addToMappingInnerPanel(List<MappingProperties> maps, JLabel plusButton, JPanel mappingInnerPanel, MappingProperties mp)
	{

		MappingThumbnails xyz = new MappingThumbnails();
		
		xyz.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				mp.setVisible2(true);
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mp.sendThumbnail(xyz);
		mappingInnerPanel.add(xyz);
		mappingInnerPanel.add(plusButton);
		
		mappingInnerPanel.setSize(550, 115*((maps.size()/4)+1));
		
		return xyz;
	}
	
	public static class FrameDragListener extends MouseAdapter {

        private final JFrame frame;
        private Point mouseDownCompCoords = null;

        public FrameDragListener(JFrame frame) {
            this.frame = frame;
        }

        public void mouseReleased(MouseEvent e) {
            mouseDownCompCoords = null;
        }

        public void mousePressed(MouseEvent e) {
            mouseDownCompCoords = e.getPoint();
        }

        public void mouseDragged(MouseEvent e) {
            Point currCoords = e.getLocationOnScreen();
            frame.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
        }
    }
	
	public static void loadMyStuff() throws MidiUnavailableException
	{
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		MidiDevice device = null;
		int j=0;
		for(int i=0; i<infos.length; i++)
		{
			try {
				device = MidiSystem.getMidiDevice(infos[i]);
				System.out.println(device.getDeviceInfo());
				if(device.getDeviceInfo().toString().equals("LoopBe Internal MIDI"))
				{
					System.out.println("found ya");
					//if(j>0)
						break;
					//j++;
				}
			} catch (MidiUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		device.open();
		rcvr = device.getReceiver();
	}
}
