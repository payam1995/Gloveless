package mainPackage;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.leapmotion.leap.*;

public class MappingProperties extends JPanel {

	
	
	private static final long serialVersionUID = 1L;
	private int id;
	private String title;
	public boolean runInBackground = false;
	private JTextField textFieldNameOfProperty;
	/** Holds the possible notes.*/
	public static String[] notes = {"C","D","E","F","G","A","B"};
	/** Holds the possible sharps.*/
	public static String[] sharps = {"C#","D#","F#","G#","A#"};
	/** Holds the octave numbers.*/
	public static String[] octave = {"-2","-1", "0", "1", "2", "3","4","5","6","7","8"};
	int decimalOfNote = -1;
	JLabel lbldecimalOfNote = null, mappedMidiData = null, qualifiedOrNot = null;
	JPanel demoPanel = null;
	MappingThumbnails thumbnail = null;
	MappingProperties thisProperty = null;
	boolean monitor = false, pipeline = false, nullifyQualifiers = false;
	JButton btnTestOutput = null, btnDelete = null, btnClose = null;
	JComboBox<Integer> comboBoxChannel = null; 
	JComboBox<String> comboBoxCommand = null;
	JPanel qualifiedOrNotPanel;
	JCheckBox relativeMIDICheckbox;
	
	boolean displayMonitor = false;
	MainWindow theMainWindow = null;
	JProgressBar midiProgressBar;
	
	final ListItems[] items = new ListItems[7];
	/**
	 * Launch the application.
	 */
	String list1selected = "", list2selected = "", list3selected = ""; 
//	public static void main(String[] args) throws MidiUnavailableException {
//		loadMyStuff();
//		
//	}
	
	public int convertNoteToDecimal (String note)
	{
		int counterToCheckNote = 0, counterToCheckOctave = 0;
//		String note = "D#-3";
		String detectedNote = "", detectedOctave = "";
		for(int i = 0; i<sharps.length;i++)
		{
			if(note.contains(sharps[i]))
			{
				counterToCheckNote++;
				note = note.replaceAll(sharps[i], "");
				detectedNote = sharps[i];
//				System.out.println("detected sharp: " + detectedNote);
			}
		}
		for(int i = 0; i<notes.length;i++)
		{
			if(note.contains(notes[i]))
			{
				counterToCheckNote++;
				note = note.replaceAll(notes[i], "");
				detectedNote = notes[i];
//				System.out.println("detected note: " + detectedNote);
			}
		}
		for(int i = 0; i<octave.length;i++)
		{
			if(note.equals(octave[i]))
			{
				counterToCheckOctave++;
				note = note.replaceAll(octave[i], "");
				detectedOctave = octave[i];
//				System.out.println("detected octave: " + detectedOctave);
			}
		}
		int detectedOctaveInt = 0;
		int detectedNoteInt = 0;
		int finalthing = -1;
		if(counterToCheckNote==1 && counterToCheckOctave ==1)
		{
			switch(detectedNote)
			{
			case "C":
				detectedNoteInt = 0;
				break;
			case "C#":
				detectedNoteInt = 1;
				break;
			case "D":
				detectedNoteInt = 2;
				break;
			case "D#":
				detectedNoteInt = 3;
				break;
			case "E":
				detectedNoteInt = 4;
				break;
			case "F":
				detectedNoteInt = 5;
				break;
			case "F#":
				detectedNoteInt = 6;
				break;
			case "G":
				detectedNoteInt = 7;
				break;
			case "G#":
				detectedNoteInt = 8;
				break;
			case "A":
				detectedNoteInt = 9;
				break;
			case "A#":
				detectedNoteInt = 10;
				break;
			case "B":
				detectedNoteInt = 11;
				break;
			}
			
			detectedOctaveInt = (Integer.parseInt(detectedOctave) + 2);
			
			finalthing = detectedOctaveInt * 12 + detectedNoteInt;
			System.out.println(finalthing);
			lbldecimalOfNote.setText(Integer.toString(finalthing));
		}
		
//		System.out.println("checknote: " + counterToCheckNote);
		
		
		
		if(counterToCheckNote==1 && counterToCheckOctave ==1)
			return finalthing;
		else return -1;
	}
	static Receiver rcvr = MainWindow.rcvr;	
	
	public void sendThumbnail(MappingThumbnails mt)
	{
		thumbnail = mt;
	}
	
	public MappingProperties(MainWindow mw) {
		theMainWindow = mw;
		btnDelete = new JButton("Delete");
		btnClose = new JButton("Save/Close");
		setSize(600, 690);
//		setBackground(new Color(107, 114, 114));
		setBackground(new Color(51,82,135));
//		setBounds(0, 0, 600, 690);
		setLayout(null);
//		revalidate();
		initialize();
//		revalidate();
		
		thisProperty = this;
		
//		setVisible(true);
//		System.out.println("i'm visible");
		
		items[0] = new ListItems("X Position", true, true);
		items[1] = new ListItems("Y Position", true, true);
		items[2] = new ListItems("Z Position", true, true);
		items[3] = new ListItems("X Rotation", true, true);
		items[4] = new ListItems("Y Rotation", true, true);
		items[5] = new ListItems("Z Rotation", true, true);
		items[6] = new ListItems("Ball", true, false);
	}
	
	
	DefaultListModel<String> list3Items = null;
	JList<String> list1 = null, list2 = null, list3 = null, list4 = null, list5 = null, list6 = null, listQualifier;
	JButton btnDel = null, btnAdd = null;
	private JTextField textFieldNote;
	JTextField textFieldRangeStart;
	JTextField textFieldRangeEnd;
	
	public void iJustAppeared()
	{
		
	}
	
	private void initialize()
	{
		DefaultListModel<String> list1Items = new DefaultListModel<String>();
		list1Items.addElement("Left Hand");
		list1Items.addElement("Right Hand");
		list1 = new JList<String>(list1Items);
		list1.setBounds(30, 65, 99, 110);
		add(list1);
		
		DefaultListModel<String> list4Items = new DefaultListModel<String>();
		list4Items.addElement("Left Hand");
		list4Items.addElement("Right Hand");
		
		DefaultListModel<String> list2Items = new DefaultListModel<String>();
		list2Items.addElement("Palm");
		list2Items.addElement("Thumb Finger");
		list2Items.addElement("Index Finger");
		list2Items.addElement("Middle Finger");
		list2Items.addElement("Ring Finger");
		list2Items.addElement("Pinky Finger");
		list2 = new JList<String>();
		list2.setBounds(130, 65, 99, 110);
		add(list2);
		
		DefaultListModel<String> list5Items = new DefaultListModel<String>();
		list5Items.addElement("Fist");
		list5Items.addElement("Open");
		list5Items.addElement("Rock Sign");
		list5Items.addElement("Spiderman");
		list5Items.addElement("Pinch");
		list5Items.addElement("Thumb Finger");
		list5Items.addElement("Index Finger");
		list5Items.addElement("Middle Finger");
		list5Items.addElement("Ring Finger");
		list5Items.addElement("Pinky Finger");
		list5Items.addElement("2 - Thumb Index");
		list5Items.addElement("2 - Index Middle");
		list5Items.addElement("2 - Thumb Pinky");
		list5Items.addElement("2 - Ring Pinky");
		list5Items.addElement("3 - Thumb Index Middle");
		list5Items.addElement("3 - Middle Ring Pinky");
		list5Items.addElement("3 - Thumb Ring Pinky");
		list5Items.addElement("4 - Thumb Middle Ring Pinky");
		list5Items.addElement("4 - Index Middle Ring Pinky");
		
		DefaultListModel<String> list6Items = new DefaultListModel<String>();
		list6Items.addElement("Visible");
		list6Items.addElement("Invisible");
		list6Items.addElement("Face Up");
		list6Items.addElement("Face Down");
		
		list3Items = new DefaultListModel<String>();
		
		
		DefaultListModel<Qualifier> qualifiers = new DefaultListModel<Qualifier>();
		DefaultListModel<String> qualifiersList = new DefaultListModel<String>();
				
		list3 = new JList<String>();
		list3.setBorder(null);
		list3.setBounds(390, 65, 150, 140);
//		add(list3);
		
		JScrollPane scroll = new JScrollPane(list3, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setViewportBorder(null);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.setBounds(230, 65, 100, 110);
		add(scroll);
		list1.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting())
				{
					refreshList2(list2Items);
					list3.clearSelection();
				}
					
			}
		});
		
		list2.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting())
				{
//					System.out.println(list2.getSelectedValue().toString());
					refreshList3(list2.getSelectedValue().toString());
					list3.clearSelection();
				}
			}
		});
		
		
		textFieldNameOfProperty = new JTextField();
		textFieldNameOfProperty.setBounds(159, 11, 258, 26);
		textFieldNameOfProperty.setBorder(BorderFactory.createEmptyBorder());
		textFieldNameOfProperty.setFont(new Font("Arcon", Font.PLAIN, 18));
		add(textFieldNameOfProperty);
		textFieldNameOfProperty.setColumns(10);
		
		JLabel lblMovement = new JLabel("Movement");
		lblMovement.setForeground(Color.WHITE);
		lblMovement.setFont(new Font("Arcon", Font.PLAIN, 14));
		lblMovement.setBounds(30, 40, 76, 26);
		add(lblMovement);
		
		PositionXPanel xPositionPanel = new PositionXPanel(this, theMainWindow);
		xPositionPanel.setBounds(340, 65, 235, 140);
		add(xPositionPanel);
//		xPositionPanel.setVisible(false);
//		xPositionPanel.addComponentListener ( new ComponentAdapter ()
//	    {
//	        public void componentShown ( ComponentEvent e )
//	        {
////	        	System.out.println("showing stats");
////	        	monitor = true;
////	        	xPositionPanel.monitor = true;
////	        	xPositionPanel.displayStats();
////	        	xPositionPanel.monitor = true;
////	        	monitor = true;
//	        }
//
//	        public void componentHidden ( ComponentEvent e )
//	        {
////	        	monitor = false;
////	        	xPositionPanel.monitor = false;
////	        	xPositionPanel.hideStats();
////	        	xPositionPanel.monitor = false;
////	        	monitor = false;
////	        	if(!runInBackground)
////	        	{
////	        		xPositionPanel.breakPipeline = true;
////	        		xPositionPanel.pipeline = false;
////	        		xPositionPanel.breakPipeline = true;
////	        	}
//	        }
//	    });
		
		PositionYPanel yPositionPanel = new PositionYPanel(this);
		yPositionPanel.setBounds(340, 65, 235, 140);
		add(yPositionPanel);
		yPositionPanel.setVisible(false);
		yPositionPanel.addComponentListener ( new ComponentAdapter ()
	    {
	        public void componentShown ( ComponentEvent e )
	        {
	        	monitor = true;
	        	yPositionPanel.monitor = true;
	        	yPositionPanel.displayStats();
	        	yPositionPanel.monitor = true;
	        	monitor = true;
	        }

	        public void componentHidden ( ComponentEvent e )
	        {
	        	monitor = false;
	        	yPositionPanel.monitor = false;
	        	yPositionPanel.hideStats();
	        	yPositionPanel.monitor = false;
	        	monitor = false;
	        	if(!runInBackground)
	        	{
	        		yPositionPanel.breakPipeline = true;
	        		yPositionPanel.pipeline = false;
	        		yPositionPanel.breakPipeline = true;
	        	}
	        }
	    });
		
		PositionZPanel zPositionPanel = new PositionZPanel(this);
		zPositionPanel.setBounds(340, 65, 235, 140);
		add(zPositionPanel);
		zPositionPanel.setVisible(false);
		zPositionPanel.addComponentListener ( new ComponentAdapter ()
	    {
	        public void componentShown ( ComponentEvent e )
	        {
	        	monitor = true;
	        	zPositionPanel.monitor = true;
	        	zPositionPanel.displayStats();
	        	zPositionPanel.monitor = true;
	        	monitor = true;
	        }

	        public void componentHidden ( ComponentEvent e )
	        {
	        	monitor = false;
	        	zPositionPanel.monitor = false;
	        	zPositionPanel.hideStats();
	        	zPositionPanel.monitor = false;
	        	monitor = false;
	        }
	    });
		
		RotationXPanel xRotationPanel = new RotationXPanel(this);
		xRotationPanel.setBounds(340, 65, 235, 140);
		add(xRotationPanel);
		xRotationPanel.setVisible(false);
		xRotationPanel.addComponentListener ( new ComponentAdapter ()
	    {
	        public void componentShown ( ComponentEvent e )
	        {
	        	monitor = true;
	        	xRotationPanel.monitor = true;
	        	xRotationPanel.displayStats();
	        	xRotationPanel.monitor = true;
	        	monitor = true;
	        }

	        public void componentHidden ( ComponentEvent e )
	        {
	        	monitor = false;
	        	xRotationPanel.monitor = false;
	        	xRotationPanel.hideStats();
	        	xRotationPanel.monitor = false;
	        	monitor = false;
	        }
	    });
		
		RotationYPanel yRotationPanel = new RotationYPanel();
		yRotationPanel.setBounds(340, 65, 235, 140);
		add(yRotationPanel);
		yRotationPanel.setVisible(false);
		
		RotationZPanel zRotationPanel = new RotationZPanel();
		zRotationPanel.setBounds(340, 65, 235, 140);
		add(zRotationPanel);
		zRotationPanel.setVisible(false);
		
		BallPanel ballPanel = new BallPanel();
		ballPanel.setBounds(340, 65, 235, 140);
		add(ballPanel);
		
		JLabel lblEvent = new JLabel("Event");
		lblEvent.setForeground(Color.WHITE);
		lblEvent.setFont(new Font("Arcon", Font.PLAIN, 14));
		lblEvent.setBounds(130, 40, 49, 26);
		add(lblEvent);
		
		JRadioButton radioButtonMovement = new JRadioButton("");
		radioButtonMovement.setBackground(new Color(107, 114, 114));
		radioButtonMovement.setBounds(6, 43, 21, 23);
		radioButtonMovement.setSelected(true);
		add(radioButtonMovement);
		
		JRadioButton radioButtonEvent = new JRadioButton("");
		radioButtonEvent.setBounds(106, 43, 21, 21);
		radioButtonEvent.setBackground(new Color(107, 114, 114));
		add(radioButtonEvent);
		ballPanel.setVisible(false);
		

		
		
		
		ButtonGroup btngroup = new ButtonGroup();
		btngroup.add(radioButtonMovement);
		btngroup.add(radioButtonEvent);
		
		JLabel lblQualifier = new JLabel("Qualifier");
		lblQualifier.setFont(new Font("Arcon", Font.PLAIN, 14));
		lblQualifier.setForeground(Color.WHITE);
		lblQualifier.setBounds(30, 179, 129, 26);
		add(lblQualifier);
		
		list4 = new JList<String>(list4Items);
		list4.setBounds(30, 205, 99, 110);
		add(list4);
		
		list5 = new JList<String>();
//		list5.setBounds(130, 205, 99, 110);
//		add(list5);
		
		list6 = new JList<String>();
		list6.setBounds(230, 205, 99, 110);
		add(list6);
		
		JScrollPane scroll2 = new JScrollPane(list5, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll2.setViewportBorder(null);
		scroll2.setBorder(BorderFactory.createEmptyBorder());
		scroll2.setBounds(130, 205, 99, 110);
		add(scroll2);
		
		btnAdd = new JButton("Add");
		btnAdd.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnAdd.setBounds(340, 220, 55, 35);
		add(btnAdd);
		
		btnDel = new JButton("Del");
		btnDel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnDel.setBounds(340, 266, 55, 35);
		add(btnDel);
		
		btnAdd.setEnabled(false);
		btnDel.setEnabled(false);
		
		listQualifier = new JList<String>();
		
		JScrollPane scroll3 = new JScrollPane(listQualifier, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll3.setBorder(BorderFactory.createEmptyBorder());
		scroll3.setBounds(404, 216, 170, 99);
		add(scroll3);
		
		btnAdd.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(btnAdd.isEnabled())
				{
					nullifyQualifiers = true;
					String temp = list4.getSelectedValue().toString() + " " + list5.getSelectedValue().toString() + " is " + list6.getSelectedValue().toString();
					temp = temp.replaceAll("Finger ", "");
					qualifiersList.addElement(temp);
					qualifiers.addElement(new Qualifier(list4.getSelectedValue().toString(),list5.getSelectedValue().toString(),list6.getSelectedValue().toString()));
					listQualifier.setModel(qualifiersList);
					xPositionPanel.refreshLists();
					yPositionPanel.refreshLists();
					zPositionPanel.refreshLists();
					xRotationPanel.refreshLists();
				}
				if(listQualifier.getModel().getSize()>0)
				{
					list4.setEnabled(false);
					list5.setEnabled(false);
					list6.setEnabled(false);
					scroll2.setEnabled(false);
					btnAdd.setEnabled(false);
					btnDel.setEnabled(false);
					
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		btnDel.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(btnDel.isEnabled())
				{
					int tempIndex = listQualifier.getSelectedIndex();
					qualifiers.remove(tempIndex);
					qualifiersList.removeElementAt(tempIndex);
					listQualifier.setModel(qualifiersList);
					btnDel.setEnabled(false);	
					xPositionPanel.refreshLists();
					yPositionPanel.refreshLists();
					zPositionPanel.refreshLists();
					xRotationPanel.refreshLists();
				}
				if(listQualifier.getModel().getSize()==0)
				{
					list4.setEnabled(true);
					list5.setEnabled(true);
					list6.setEnabled(true);
					scroll2.setEnabled(true);
					btnAdd.setEnabled(true);
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
		
		list4.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting())
				{
					if(!list4.isSelectionEmpty())
					{
						refreshList5(list5Items);
					}
				}
				
			}
		});
		
		list5.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting())
				{
					if(!list5.isSelectionEmpty())
					{
						refreshList6(list6Items);
					}
				}
			}
		});
		
		list6.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting())
				{
					btnAdd.setEnabled(true);
//					btnDel.setEnabled(true);
				}	
				if(list6.isSelectionEmpty())
					btnAdd.setEnabled(false);
			}
		});
		
		
		
		list4.setEnabled(false);
		list5.setEnabled(false);
		list6.setEnabled(false);
		scroll2.setEnabled(false);
		
		JLabel lblOutput = new JLabel("Output");
		lblOutput.setForeground(Color.WHITE);
		lblOutput.setFont(new Font("Arcon", Font.PLAIN, 14));
		lblOutput.setBounds(32, 326, 97, 23);
		add(lblOutput);
		
		String[] eventComboBoxList = new String[] {"Note ON"};
		String[] movementComboBoxList = new String[] {"CC", "Pitch Bend"};
		
		comboBoxCommand = new JComboBox<String>();
		comboBoxCommand.setBounds(116, 360, 130, 22);
		add(comboBoxCommand);
		
		JLabel lblCommand = new JLabel("Command");
		lblCommand.setHorizontalAlignment(SwingConstants.LEFT);
		lblCommand.setForeground(Color.WHITE);
		lblCommand.setFont(new Font("Arcon", Font.PLAIN, 14));
		lblCommand.setBounds(30, 360, 76, 18);
		add(lblCommand);
		
		comboBoxChannel = new JComboBox<Integer>();
		comboBoxChannel.setModel(new DefaultComboBoxModel<Integer>(new Integer[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16}));
		comboBoxChannel.setBounds(116, 393, 130, 22);
		add(comboBoxChannel);
		
		JLabel lblChannel = new JLabel("Channel");
		lblChannel.setHorizontalAlignment(SwingConstants.LEFT);
		lblChannel.setForeground(Color.WHITE);
		lblChannel.setFont(new Font("Arcon", Font.PLAIN, 14));
		lblChannel.setBounds(30, 393, 76, 18);
		add(lblChannel);
		
		JLabel lblNote = new JLabel("Note");
		lblNote.setHorizontalAlignment(SwingConstants.LEFT);
		lblNote.setForeground(Color.WHITE);
		lblNote.setFont(new Font("Arcon", Font.PLAIN, 14));
		lblNote.setBounds(30, 427, 76, 18);
		add(lblNote);
		
		textFieldNote = new JTextField();
		textFieldNote.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldNote.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldNote.setBounds(116, 427, 50, 22);
		add(textFieldNote);
		textFieldNote.setBorder(BorderFactory.createEmptyBorder());
		textFieldNote.setColumns(10);
		
		JLabel lblRange = new JLabel("Range");
		lblRange.setHorizontalAlignment(SwingConstants.LEFT);
		lblRange.setForeground(Color.WHITE);
		lblRange.setFont(new Font("Arcon", Font.PLAIN, 14));
		lblRange.setBounds(30, 461, 76, 18);
		add(lblRange);
		
		textFieldRangeStart = new JTextField();
		textFieldRangeStart.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldRangeStart.setText("0");
		textFieldRangeStart.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldRangeStart.setColumns(10);
		textFieldRangeStart.setBorder(BorderFactory.createEmptyBorder());
		textFieldRangeStart.setBounds(116, 460, 50, 22);
		add(textFieldRangeStart);
		
		textFieldRangeEnd = new JTextField();
		textFieldRangeEnd.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldRangeEnd.setText("127");
		textFieldRangeEnd.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldRangeEnd.setColumns(10);
		textFieldRangeEnd.setBorder(BorderFactory.createEmptyBorder());
		textFieldRangeEnd.setBounds(196, 460, 50, 22);
		add(textFieldRangeEnd);
		
		JLabel label = new JLabel("-");
		label.setForeground(Color.WHITE);
		label.setFont(new Font("Arcon", Font.PLAIN, 24));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBounds(171, 462, 21, 14);
		add(label);
		
		comboBoxCommand.setModel(new DefaultComboBoxModel<String>(movementComboBoxList));
		
		btnTestOutput = new JButton("Test Output");
		btnTestOutput.setEnabled(false);
		btnTestOutput.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnTestOutput.setBounds(116, 493, 130, 26);
		add(btnTestOutput);
//		try {
//			rcvr.send(new ShortMessage(ShortMessage.NOTE_ON ,1,24, 127), -1);
//		} catch (InvalidMidiDataException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
		btnTestOutput.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(comboBoxCommand.getSelectedItem().equals("Note ON"))
				{
					try {
						System.out.println(decimalOfNote);
						rcvr.send(new ShortMessage(ShortMessage.NOTE_ON ,(int)comboBoxChannel.getSelectedItem(),decimalOfNote, 64), -1);
						Thread.sleep(2000);
						rcvr.send(new ShortMessage(ShortMessage.NOTE_OFF ,(int)comboBoxChannel.getSelectedItem(),decimalOfNote, 64), -1);
					} catch (InvalidMidiDataException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}	
				}
				else if(comboBoxCommand.getSelectedItem().equals("CC"))
				{
					try {
						System.out.println(decimalOfNote);
						rcvr.send(new ShortMessage(ShortMessage.NOTE_ON ,(int)comboBoxChannel.getSelectedItem(),decimalOfNote, 64), -1);
						Thread.sleep(100);
						for(int i=0;i<128;i++)
						{
							rcvr.send(new ShortMessage(ShortMessage.CONTROL_CHANGE ,(int)comboBoxChannel.getSelectedItem(),decimalOfNote, i), -1);	
							Thread.sleep(5);
						}
						for(int i=127;i>-1;i--)
						{
							rcvr.send(new ShortMessage(ShortMessage.CONTROL_CHANGE ,(int)comboBoxChannel.getSelectedItem(),decimalOfNote, i), -1);	
							Thread.sleep(5);
						}
						Thread.sleep(100);
						rcvr.send(new ShortMessage(ShortMessage.NOTE_OFF ,(int)comboBoxChannel.getSelectedItem(),decimalOfNote, 64), -1);
					} catch (InvalidMidiDataException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}	
				}
				else if(comboBoxCommand.getSelectedItem().equals("Pitch Bend"))
				{
					try {
						System.out.println(decimalOfNote);
						rcvr.send(new ShortMessage(ShortMessage.NOTE_ON ,(int)comboBoxChannel.getSelectedItem(),decimalOfNote, 64), -1);
						Thread.sleep(100);
						for(int i=0;i<128;i++)
						{
							rcvr.send(new ShortMessage(ShortMessage.PITCH_BEND ,(int)comboBoxChannel.getSelectedItem(),decimalOfNote, i), -1);	
							Thread.sleep(5);
						}
						for(int i=127;i>-1;i--)
						{
							rcvr.send(new ShortMessage(ShortMessage.PITCH_BEND ,(int)comboBoxChannel.getSelectedItem(),decimalOfNote, i), -1);	
							Thread.sleep(5);
						}
						Thread.sleep(100);
						rcvr.send(new ShortMessage(ShortMessage.NOTE_OFF ,(int)comboBoxChannel.getSelectedItem(),decimalOfNote, 64), -1);
					} catch (InvalidMidiDataException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}	
				}
				
				
			}
		});
		
		btnTestOutput.addPropertyChangeListener("enabled", new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				
				if(evt.getNewValue().toString().equals("true"))
				{
					pipeline = true;
				}
				else if(evt.getNewValue().toString().equals("false"))
				{
					pipeline = false;
				}
				
			}
		});
		
		
		JButton btnAutoName = new JButton("Auto Name");
		btnAutoName.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnAutoName.setBounds(427, 11, 89, 26);
		add(btnAutoName);
		
//		JCheckBox chckbxDemoIo = new JCheckBox("Demo I/O");
//		chckbxDemoIo.setForeground(Color.WHITE);
//		chckbxDemoIo.setFont(new Font("Arcon", Font.PLAIN, 14));
//		chckbxDemoIo.setBackground(new Color(107, 114, 114));
//		chckbxDemoIo.setBounds(382, 326, 89, 23);
//		add(chckbxDemoIo);
		
		
		mappedMidiData = new JLabel("0");
		mappedMidiData.setFont(new Font("Arcon", Font.BOLD, 20));
		mappedMidiData.setForeground(new Color(240,240,240));
		mappedMidiData.setBounds(480, 485, 109, 35);
		add(mappedMidiData);
		
		qualifiedOrNot = new JLabel("Unqualified");
		qualifiedOrNot.setFont(new Font("Arcon", Font.PLAIN, 15));
		qualifiedOrNot.setForeground(new Color(240,240,240));
		qualifiedOrNot.setBounds(260, 422, 109, 35);
		add(qualifiedOrNot);
		
		qualifiedOrNotPanel = new JPanel();
		qualifiedOrNotPanel.setBackground(new Color(204, 51, 0));
		qualifiedOrNotPanel.setBounds(404, 360, 170, 85);
		qualifiedOrNotPanel.setLayout(null);
		add(qualifiedOrNotPanel);
		
		relativeMIDICheckbox = new JCheckBox("Relative MIDI");
		relativeMIDICheckbox.setForeground(Color.WHITE);
		relativeMIDICheckbox.setFont(new Font("Arcon", Font.PLAIN, 14));
		relativeMIDICheckbox.setBackground(new Color(107, 114, 114));
		relativeMIDICheckbox.setBounds(255, 494, 130, 23);
		add(relativeMIDICheckbox);
		
		midiProgressBar = new JProgressBar();
		midiProgressBar.setForeground(new Color(0, 204, 204));
		midiProgressBar.setMaximum(127);
		midiProgressBar.setBounds(404, 460, 171, 22);
		add(midiProgressBar);
		
		btnClose.setEnabled(false);
		btnClose.setFont(new Font("Arcon", Font.PLAIN, 13));
		btnClose.setBounds(20, 560, 109, 35);
		add(btnClose);
		
		btnClose.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				setVisible2(false);
				if(!runInBackground)
				{
					
					//stop everything
				}
				else
				{
					///stop leap background monitoring display, but keep data flowing through midi	
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
		
		lbldecimalOfNote = new JLabel("0");
		lbldecimalOfNote.setHorizontalAlignment(SwingConstants.CENTER);
		lbldecimalOfNote.setForeground(Color.WHITE);
		lbldecimalOfNote.setFont(new Font("Arcon", Font.PLAIN, 14));
		lbldecimalOfNote.setBounds(196, 427, 50, 22);
		add(lbldecimalOfNote);
		
		textFieldNote.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
//				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				textFieldNote.setText(textFieldNote.getText().toUpperCase());
				decimalOfNote = convertNoteToDecimal(textFieldNote.getText().toString());
				if(textFieldNote.getText().length()>0 && 
						!textFieldNote.getText().toString().contains(" ") && 
						textFieldNote.getText().length() <= 4 &&
						decimalOfNote <= 127 &&
						decimalOfNote >= 0
//						Integer.parseInt(textFieldNote.getText(),16) > 127
						)
				{
					btnTestOutput.setEnabled(true);
				}
				else
				{
					btnTestOutput.setEnabled(false);
				}
				if(shouldEnableSaveButton(list3, btnTestOutput, textFieldNameOfProperty))
				{
					btnClose.setEnabled(true);
				}
				else
				{
					btnClose.setEnabled(false);
				}
				
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
//				AbstractDocument document = (AbstractDocument) textFieldNote
//		                .getDocument();
//		        document.setDocumentFilter(new DocumentFilter() {
//		            @Override
//		            public void insertString(FilterBypass fb, int offset,
//		                    String string, AttributeSet attr)
//		                    throws BadLocationException {
//		                super.insertString(fb, offset, string.toUpperCase(), attr);
//		            }
//
//		            @Override
//		            public void replace(FilterBypass fb, int offset, int length,
//		                    String text, AttributeSet attrs)
//		                    throws BadLocationException {
//		                super.insertString(fb, offset, text.toUpperCase(), attrs);
//		            }
//
//		        });
//				textFieldNote.setText(textFieldNote.getText().toUpperCase());
			}
		});
		
		
		btnDelete.setFont(new Font("Arcon", Font.PLAIN, 24));
		btnDelete.setBounds(137, 560, 109, 35);
		add(btnDelete);
		
		btnDelete.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
//				System.out.println("this listener just ran");
				runInBackground = false;
				setVisible2(false);
				
				MainWindow.deleteProperty(thisProperty, thumbnail);
				
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
		
		
		JCheckBox chckbxbackground = new JCheckBox("Run in Background");
		chckbxbackground.setForeground(Color.WHITE);
		chckbxbackground.setFont(new Font("Arcon", Font.PLAIN, 14));
		chckbxbackground.setBackground(new Color(107, 114, 114));
		chckbxbackground.setBounds(20, 610, 200, 23);
		add(chckbxbackground);
		
		chckbxbackground.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==1)
				{
					runInBackground = true;
				}
				else
				{
					runInBackground = false;
				}
				
			}
		});
		
//		demoPanel = new JPanel();
//		demoPanel.setBounds(295, 360, 280, 280);
//		add(demoPanel);
//		
//		
//		demoPanel.setVisible(false);
//		
//		chckbxDemoIo.addItemListener(new ItemListener() {
//			
//			@Override
//			public void itemStateChanged(ItemEvent e) {
//				if(e.getStateChange()==1)
//				{
//					demoPanel.setVisible(true);
//				}
//				else
//				{
//					demoPanel.setVisible(false);
//				}
//				
//			}
//		});
		
		radioButtonMovement.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!radioButtonEvent.isSelected())
				{
					comboBoxCommand.setModel(new DefaultComboBoxModel<String>(movementComboBoxList));
					textFieldRangeStart.setEnabled(true);
					textFieldRangeEnd.setEnabled(true);
					relativeMIDICheckbox.setEnabled(true);
				}
			}
		});
		
		radioButtonEvent.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!radioButtonMovement.isSelected())
				{
					comboBoxCommand.setModel(new DefaultComboBoxModel<String>(eventComboBoxList));
					textFieldRangeStart.setEnabled(false);
					textFieldRangeEnd.setEnabled(false);
					relativeMIDICheckbox.setEnabled(false);
				}
				
			}
		});
		
		lblMovement.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				radioButtonMovement.setSelected(true);
				if(!radioButtonEvent.isSelected())
				{
					comboBoxCommand.setModel(new DefaultComboBoxModel<String>(movementComboBoxList));
					textFieldRangeStart.setEnabled(true);
					textFieldRangeEnd.setEnabled(true);
					relativeMIDICheckbox.setEnabled(true);
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		
		lblEvent.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				radioButtonEvent.setSelected(true);
				if(!radioButtonMovement.isSelected())
				{
					comboBoxCommand.setModel(new DefaultComboBoxModel<String>(eventComboBoxList));
					textFieldRangeStart.setEnabled(false);
					textFieldRangeEnd.setEnabled(false);
					relativeMIDICheckbox.setEnabled(false);
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
			}
			
			@Override
			public void mouseExited(MouseEvent e) {		
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		
		listQualifier.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting())
				{
					btnDel.setEnabled(true);
				}
				
			}
		});
		listQualifier.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub
				System.out.println("fjlsdk");
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		list3.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting())
				{
					if(!list3.isSelectionEmpty() && list3.getSelectedValue().toString().equals("X Position"))
					{
//						add(xPositionPanel);
//						remove(yPositionPanel);
						xPositionPanel.setVisible(true);
						yPositionPanel.setVisible(false);
						zPositionPanel.setVisible(false);
						xRotationPanel.setVisible(false);
						yRotationPanel.setVisible(false);
						zRotationPanel.setVisible(false);
						ballPanel.setVisible(false);
					}
					if(!list3.isSelectionEmpty() && list3.getSelectedValue().toString().equals("Y Position"))
					{
//						add(yPositionPanel);
//						remove(xPositionPanel);
						xPositionPanel.setVisible(false);
						yPositionPanel.setVisible(true);
						zPositionPanel.setVisible(false);
						xRotationPanel.setVisible(false);
						yRotationPanel.setVisible(false);
						zRotationPanel.setVisible(false);
						ballPanel.setVisible(false);
					}
					if(!list3.isSelectionEmpty() && list3.getSelectedValue().toString().equals("Z Position"))
					{
						xPositionPanel.setVisible(false);
						yPositionPanel.setVisible(false);
						zPositionPanel.setVisible(true);
						xRotationPanel.setVisible(false);
						yRotationPanel.setVisible(false);
						zRotationPanel.setVisible(false);
						ballPanel.setVisible(false);
					}
					if(!list3.isSelectionEmpty() && list3.getSelectedValue().toString().equals("X Rotation"))
					{
						xPositionPanel.setVisible(false);
						yPositionPanel.setVisible(false);
						zPositionPanel.setVisible(false);
						xRotationPanel.setVisible(true);
						yRotationPanel.setVisible(false);
						zRotationPanel.setVisible(false);
						ballPanel.setVisible(false);
					}
					if(!list3.isSelectionEmpty() && list3.getSelectedValue().toString().equals("Y Rotation"))
					{
						xPositionPanel.setVisible(false);
						yPositionPanel.setVisible(false);
						zPositionPanel.setVisible(false);
						xRotationPanel.setVisible(false);
						yRotationPanel.setVisible(true);
						zRotationPanel.setVisible(false);
						ballPanel.setVisible(false);
					}
					if(!list3.isSelectionEmpty() && list3.getSelectedValue().toString().equals("Z Rotation"))
					{
						xPositionPanel.setVisible(false);
						yPositionPanel.setVisible(false);
						zPositionPanel.setVisible(false);
						xRotationPanel.setVisible(false);
						yRotationPanel.setVisible(false);
						zRotationPanel.setVisible(true);
						ballPanel.setVisible(false);
					}
					if(!list3.isSelectionEmpty() && list3.getSelectedValue().toString().equals("Ball"))
					{
						xPositionPanel.setVisible(false);
						yPositionPanel.setVisible(false);
						zPositionPanel.setVisible(false);
						xRotationPanel.setVisible(false);
						yRotationPanel.setVisible(false);
						zRotationPanel.setVisible(false);
						ballPanel.setVisible(true);
					}
					if(list3.isSelectionEmpty())
					{
						xPositionPanel.setVisible(false);
						yPositionPanel.setVisible(false);
						zPositionPanel.setVisible(false);
						xRotationPanel.setVisible(false);
						yRotationPanel.setVisible(false);
						zRotationPanel.setVisible(false);
						ballPanel.setVisible(false);
						list4.setEnabled(false);
						list5.setEnabled(false);
						list6.setEnabled(false);
						scroll3.setEnabled(false);
//						btnAdd.setEnabled(false);
//						list6.clearSelection();
//						btnDel.setEnabled(false);
					}
					if(!list3.isSelectionEmpty() && listQualifier.getModel().getSize()==0)
					{
						list4.setEnabled(true);
						list5.setEnabled(true);
						list6.setEnabled(true);
						scroll2.setEnabled(true);
//						btnAdd.setEnabled(true);
//						btnDel.setEnabled(true);
					}
					if(shouldEnableSaveButton(list3, btnTestOutput, textFieldNameOfProperty))
					{
						btnClose.setEnabled(true);
					}
					else
					{
						btnClose.setEnabled(false);
					}
					if(!list3.isSelectionEmpty())
					{
						startLeapMonitoringAndPipeLine();
					}
						
				}
			}
		});
		textFieldNameOfProperty.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if(shouldEnableSaveButton(list3, btnTestOutput, textFieldNameOfProperty))
				{
					btnClose.setEnabled(true);
				}
				else
				{
					btnClose.setEnabled(false);
				}
				thumbnail.changeText(textFieldNameOfProperty.getText());
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		btnAutoName.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!list3.isSelectionEmpty())
				{
					String temp = list1.getSelectedValue().toString() + 
							" " + list2.getSelectedValue().toString() +
							" " + list3.getSelectedValue().toString();
//					temp.replace("Hand", "");
					temp = temp.replaceAll(" Hand", "");
					temp = temp.replaceAll(" Finger", "");
					textFieldNameOfProperty.setText(temp);
					thumbnail.changeText(temp);
				}
				else 
				{
					textFieldNameOfProperty.setText("Default name");
					thumbnail.changeText("Default name");
				}
				if(shouldEnableSaveButton(list3, btnTestOutput, textFieldNameOfProperty))
				{
					btnClose.setEnabled(true);
				}
				else
				{
					btnClose.setEnabled(false);
				}
			}
		});
	}
	
	
	
	public void setVisible2(boolean b)
	{
		setVisible(b);
		if(!b)
		{
			if(runInBackground)
			{
				pipeline = true;
			}
			else 
			{
				pipeline = false;
			}
			monitor = false;
		}
		else
		{
			if(btnTestOutput.isEnabled())
				pipeline = true;
			monitor = true;								///run threads here - option
		}
	}
	
	public void startLeapMonitoringAndPipeLine()				///called every time panel becomes visible
	{
		
//		getOrgans();
//		monitorLeap.start();
//		pipelineLeap.start();
		//get organs
		//start monitoring
		
	}

	
	
	private boolean shouldEnableSaveButton(JList list3, JButton btnTestOutput, JTextField textField)
	{
		if(!list3.isSelectionEmpty() && btnTestOutput.isEnabled() && !textField.getText().isEmpty())
			return true;
		else return false;
	}
	
	private void refreshList2(DefaultListModel<String> list2Items)
	{
		if(list2.getModel().getSize()<1)
		{
			list2.setModel(list2Items);
			list1selected = list1.getSelectedValue().toString();
		}
	}
	
	private void refreshList3(String selectedValue)
	{
		
		if(selectedValue.equalsIgnoreCase("Palm"))
		{
			list3Items.clear();
			for(int i=0; i<7;i++)
			{
				if(items[i].handCompatible)
					list3Items.addElement(items[i].name);
			}
			list3.setModel(list3Items);
		}
		else
		{
			list3Items.clear();
			for(int i=0; i<7;i++)
			{
				if(items[i].fingerCompatible)
					list3Items.addElement(items[i].name);
			}
			list3.setModel(list3Items);
		}
	}
	private void refreshList5(DefaultListModel<String> list5Items)
	{
		if(list5.getModel().getSize()<1)
			list5.setModel(list5Items);
	}
	private void refreshList6(DefaultListModel<String> list6Items)
	{
		if(list6.getModel().getSize()<1)
		{
			list6.setModel(list6Items);
			
		}
			
	}
	
	public void setTitle(String a)
	{
		title = a;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void setId(int i)
	{
		id = i;
	}
	
	public int getId()
	{
		return id;
	}
	
	public MappingProperties(LayoutManager layout) {
		super(layout);
		// TODO Auto-generated constructor stub
	}

	public MappingProperties(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	public MappingProperties(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

}
