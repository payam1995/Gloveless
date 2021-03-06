package mainPackage;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Hand;



public class RotationXPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JLabel currentPositionXlabel = new JLabel("");
	private final JLabel lblO1 = new JLabel("o");
	private final JLabel lblO2 = new JLabel("o");
	private JLabel rangeSliderLabel1 = new JLabel();
    private JLabel rangeSliderValue1 = new JLabel();
    private JLabel rangeSliderLabel2 = new JLabel();
    private JLabel rangeSliderValue2 = new JLabel();
    private RangeSlider rangeSlider = new RangeSlider();
    public Hand mainHand = null, qualifiedHand = null;
	public Finger mainFinger = null, qualifiedThumb= null, qualifiedIndex= null, qualifiedMiddle= null, qualifiedRing= null, qualifiedPinky= null;
	boolean monitor = false, pipeline = false;
	boolean conditionVisible = false, conditionInvisible = false, conditionFaceup = false, conditionFacedown = false;
	boolean noteOff = true, breakLoop = false;
	int monitorSleepTime = 15, pipelineSleepTime = 15;
	MappingProperties parent = null;
	JList list1, list2, list3, list4, list5, list6, listQualifier;
	static Receiver rcvr = MainWindow.rcvr;
//	Thread monitorLeap = null;
	int previousX = 0, preprevious = 0;
    
	public void displayStats()
    {
    	monitor = true;
    	refreshLists();
    	breakLoop = false;
    	System.out.println("display stats called");

    	
    	
    	monitorLeap.start();
    }
    
	Thread monitorLeap = new Thread() {
		public void run()
		{
//			System.out.println("starting monitor monitor status: "+ monitor);
			while(monitor)
			{
				refreshLists();
				if(!SampleListener.currentFrame.hands().isEmpty())
				{
					getOrgans();
//					System.out.println(mainHand != null && mainFinger != null);
					if(mainHand != null && mainFinger != null)
					{
						if(list2.getSelectedValue().toString().equals("Palm"))
						{
//							System.out.println("monitor rotation x still on");
							int tempX = (int)(mainHand.palmNormal().getX()*100);
							if(tempX != previousX)
							{
								int tempAngle = map(tempX, -99, 99, 0, 180);
								Point p1 = getCircleXY(95, 50, 25, tempAngle); 
								lblO1.setLocation(p1.x, p1.y);
								Point p2 = getCircleXY(95, 50, 25, tempAngle-180);
								lblO2.setLocation(p2.x, p2.y);
    							currentPositionXlabel.setText(Integer.toString(((int)tempX)));
    							previousX = tempX;
							}    							
						}
						else
						{
							int tempX = (int)mainFinger.tipPosition().getX();
							if(tempX != previousX)
							{
								int tempAngle = map(tempX, -99, 99, 0, 180);
								Point p1 = getCircleXY(95, 50, 25, tempAngle); 
								lblO1.setLocation(p1.x, p1.y);
								Point p2 = getCircleXY(95, 50, 25, tempAngle-180);
								lblO2.setLocation(p2.x, p2.y);
    							currentPositionXlabel.setText(Integer.toString(((int)tempX)));
    							previousX = tempX;
							}
						}

					}
				}
				try {
					Thread.sleep(monitorSleepTime);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				pipeline = parent.pipeline;
				
				if(pipeline && !pipelineLeap.isAlive())
				{
					
					pipelineLeap.start();
				}
				if(breakLoop)
				{
					breakLoop = false;
					break;
					
				}
			}
			System.out.println("leaving monitor leap thread rotation x");
		}
		
	};
	
    @SuppressWarnings("deprecation")
	public void hideStats()
    {
    	monitor = false;
    	parent.monitor = false;
//    	monitorLeap.stop();
    	breakLoop = true;
    	if(!parent.runInBackground)
    	{
    		pipeline = false;
    		parent.pipeline = false;
    	}
    		
    }
    
    public boolean isQualified()
    {
    	if(!conditionInvisible)
    	{
//    		System.out.println("condition visible: " + conditionVisible);
	    	if(qualifiedHand != null && qualifiedThumb != null)
	    	{
//	    		System.out.println("qualified hand null: "+ QualifierHand == null);
	    		if(conditionVisible)
	    		{
	    			FingerList fl = qualifiedHand.fingers().extended();
	    			if(list5.getSelectedValue().toString().contains("Finger"))
		    		{
			    		if(fl.count()==1)	
	    				{
	    					switch(list5.getSelectedValue().toString())
			    			{
			    			case "Index Finger":														//rest fingers closed, only this open
			    				if(fl.get(0).equals(qualifiedIndex))
			    					return true;
			    				break;
			    			case "Thumb Finger":
			    				if(fl.get(0).equals(qualifiedThumb))
			    					return true;
			    				break;
			    			case "Middle Finger":														//rest fingers closed, only this open
			    				if(fl.get(0).equals(qualifiedMiddle))
			    					return true;
			    				break;
			    			case "Ring Finger":
			    				if(fl.get(0).equals(qualifiedRing))
			    					return true;
			    				break;
			    			case "Pinky Finger":														//rest fingers closed, only this open
			    				if(fl.get(0).equals(qualifiedPinky))
			    					return true;
			    				break;
			    			default:
			    				break;
			    			}
	    				}
		    		}
	    			else if(list5.getSelectedValue().toString().equals("Spiderman") && fl.count()==3)
	    			{
	    				if(qualifiedThumb.isExtended() && qualifiedIndex.isExtended() && qualifiedPinky.isExtended())
	    					return true;
	    			}
	    			else if(list5.getSelectedValue().toString().equals("Rock Sign") && fl.count()==2)
	    			{
	    				if(qualifiedIndex.isExtended() && qualifiedPinky.isExtended())
	    					return true;
	    			}
	    			else if(list5.getSelectedValue().toString().equals("Pinch"))
	    			{
	    				if(qualifiedHand.pinchDistance() < 30)
	    				{
//	    					System.out.println("pinched11");
	    					return true;
	    				}
	    			}
	    			else if(list5.getSelectedValue().toString().equals("Fist"))
	    			{
//	    				System.out.println(qualifiedHand.grabAngle());
	    				if(qualifiedHand.grabAngle() > 3)
	    				{
//	    					System.out.println("grabbed");
	    					return true;
	    				}
	    			}
	    			else if(list5.getSelectedValue().toString().equals("Open"))
	    			{
	    				if(qualifiedHand.grabAngle() < 2)
	    				{
//	    					System.out.println("ungrabbed");
	    					return true;
	    				}
	    			}
	    			else if(list5.getSelectedValue().toString().equals("2 - Thumb Index"))
	    			{
	    				if(qualifiedThumb.isExtended() && qualifiedIndex.isExtended() && !qualifiedMiddle.isExtended() && !qualifiedRing.isExtended() && !qualifiedPinky.isExtended())
	    					return true;
	    			}
	    			else if(list5.getSelectedValue().toString().equals("2 - Index Middle"))
	    			{
	    				if(qualifiedIndex.isExtended() && qualifiedMiddle.isExtended() && !qualifiedRing.isExtended() && !qualifiedPinky.isExtended() && !qualifiedThumb.isExtended())
	    					return true;
	    			}
	    			else if(list5.getSelectedValue().toString().equals("2 - Thumb Pinky"))
	    			{
	    				if(qualifiedThumb.isExtended() && qualifiedPinky.isExtended() && !qualifiedMiddle.isExtended() && !qualifiedRing.isExtended() && !qualifiedIndex.isExtended())
	    					return true;
	    			}
	    			else if(list5.getSelectedValue().toString().equals("2 - Ring Pinky"))
	    			{
	    				if(qualifiedRing.isExtended() && qualifiedPinky.isExtended() && !qualifiedMiddle.isExtended() && !qualifiedThumb.isExtended() && !qualifiedIndex.isExtended())
	    					return true;
	    			}
	    			else if(list5.getSelectedValue().toString().equals("3 - Thumb Index Middle"))
	    			{
	    				if(qualifiedThumb.isExtended() && qualifiedIndex.isExtended() && qualifiedMiddle.isExtended() && !qualifiedRing.isExtended() && !qualifiedPinky.isExtended())
	    					return true;
	    			}
	    			else if(list5.getSelectedValue().toString().equals("3 - Middle Ring Pinky"))
	    			{
	    				if(qualifiedMiddle.isExtended() && qualifiedRing.isExtended() && qualifiedPinky.isExtended() && !qualifiedThumb.isExtended() && !qualifiedIndex.isExtended())
	    					return true;
	    			}
	    			else if(list5.getSelectedValue().toString().equals("3 - Thumb Ring Pinky"))
	    			{
	    				if(qualifiedThumb.isExtended() && qualifiedRing.isExtended() && qualifiedPinky.isExtended() && !qualifiedMiddle.isExtended() && !qualifiedIndex.isExtended())
	    					return true;
	    			}
	    			else if(list5.getSelectedValue().toString().equals("4 - Thumb Middle Ring Pinky"))
	    			{
	    				if(qualifiedThumb.isExtended() && qualifiedMiddle.isExtended() && qualifiedRing.isExtended() && qualifiedPinky.isExtended() && !qualifiedIndex.isExtended())
	    					return true;
	    			}
	    			else if(list5.getSelectedValue().toString().equals("4 - Index Middle Ring Pinky"))
	    			{
	    				if(qualifiedIndex.isExtended() && qualifiedMiddle.isExtended() && qualifiedRing.isExtended() && qualifiedPinky.isExtended() && !qualifiedThumb.isExtended())
	    					return true;
	    			}
	    		}
	    		else if(conditionFaceup)
	    		{
	    			if(qualifiedHand.palmNormal().getY() > 0)
	    			{
	    				FingerList fl = qualifiedHand.fingers().extended();
		    			if(list5.getSelectedValue().toString().contains("Finger"))
			    		{
				    		if(fl.count()==1)	
		    				{
		    					switch(list5.getSelectedValue().toString())
				    			{
				    			case "Index Finger":														//rest fingers closed, only this open
				    				if(fl.get(0).equals(qualifiedIndex))
				    					return true;
				    				break;
				    			case "Thumb Finger":
				    				if(fl.get(0).equals(qualifiedThumb))
				    					return true;
				    				break;
				    			case "Middle Finger":														//rest fingers closed, only this open
				    				if(fl.get(0).equals(qualifiedMiddle))
				    					return true;
				    				break;
				    			case "Ring Finger":
				    				if(fl.get(0).equals(qualifiedRing))
				    					return true;
				    				break;
				    			case "Pinky Finger":														//rest fingers closed, only this open
				    				if(fl.get(0).equals(qualifiedPinky))
				    					return true;
				    				break;
				    			default:
				    				break;
				    			}
		    				}
			    		}
		    			else if(list5.getSelectedValue().toString().equals("Spiderman") && fl.count()==3)
		    			{
		    				if(qualifiedThumb.isExtended() && qualifiedIndex.isExtended() && qualifiedPinky.isExtended())
		    					return true;
		    			}
		    			else if(list5.getSelectedValue().toString().equals("Rock Sign") && fl.count()==2)
		    			{
		    				if(qualifiedIndex.isExtended() && qualifiedPinky.isExtended())
		    					return true;
		    			}
		    			else if(list5.getSelectedValue().toString().equals("Pinch"))
		    			{
		    				if(qualifiedHand.pinchDistance() < 30)
		    				{
//		    					System.out.println("pinched");
		    					return true;
		    				}
		    			}
		    			else if(list5.getSelectedValue().toString().equals("Fist"))
		    			{
		    				if(qualifiedHand.grabAngle() > 3)
		    				{
//		    					System.out.println("grabbed");
		    					return true;
		    				}
		    			}
		    			else if(list5.getSelectedValue().toString().equals("Open"))
		    			{
		    				if(qualifiedHand.grabAngle() < 2)
		    				{
//		    					System.out.println("ungrabbed");
		    					return true;
		    				}
		    			}
		    			else if(list5.getSelectedValue().toString().equals("2 - Thumb Index"))
		    			{
		    				if(qualifiedThumb.isExtended() && qualifiedIndex.isExtended() && !qualifiedMiddle.isExtended() && !qualifiedRing.isExtended() && !qualifiedPinky.isExtended())
		    					return true;
		    			}
		    			else if(list5.getSelectedValue().toString().equals("2 - Index Middle"))
		    			{
		    				if(qualifiedIndex.isExtended() && qualifiedMiddle.isExtended() && !qualifiedRing.isExtended() && !qualifiedPinky.isExtended() && !qualifiedThumb.isExtended())
		    					return true;
		    			}
		    			else if(list5.getSelectedValue().toString().equals("2 - Thumb Pinky"))
		    			{
		    				if(qualifiedThumb.isExtended() && qualifiedPinky.isExtended() && !qualifiedMiddle.isExtended() && !qualifiedRing.isExtended() && !qualifiedIndex.isExtended())
		    					return true;
		    			}
		    			else if(list5.getSelectedValue().toString().equals("2 - Ring Pinky"))
		    			{
		    				if(qualifiedRing.isExtended() && qualifiedPinky.isExtended() && !qualifiedMiddle.isExtended() && !qualifiedThumb.isExtended() && !qualifiedIndex.isExtended())
		    					return true;
		    			}
		    			else if(list5.getSelectedValue().toString().equals("3 - Thumb Index Middle"))
		    			{
		    				if(qualifiedThumb.isExtended() && qualifiedIndex.isExtended() && qualifiedMiddle.isExtended() && !qualifiedRing.isExtended() && !qualifiedPinky.isExtended())
		    					return true;
		    			}
		    			else if(list5.getSelectedValue().toString().equals("3 - Middle Ring Pinky"))
		    			{
		    				if(qualifiedMiddle.isExtended() && qualifiedRing.isExtended() && qualifiedPinky.isExtended() && !qualifiedThumb.isExtended() && !qualifiedIndex.isExtended())
		    					return true;
		    			}
		    			else if(list5.getSelectedValue().toString().equals("3 - Thumb Ring Pinky"))
		    			{
		    				if(qualifiedThumb.isExtended() && qualifiedRing.isExtended() && qualifiedPinky.isExtended() && !qualifiedMiddle.isExtended() && !qualifiedIndex.isExtended())
		    					return true;
		    			}
		    			else if(list5.getSelectedValue().toString().equals("4 - Thumb Middle Ring Pinky"))
		    			{
		    				if(qualifiedThumb.isExtended() && qualifiedMiddle.isExtended() && qualifiedRing.isExtended() && qualifiedPinky.isExtended() && !qualifiedIndex.isExtended())
		    					return true;
		    			}
		    			else if(list5.getSelectedValue().toString().equals("4 - Index Middle Ring Pinky"))
		    			{
		    				if(qualifiedIndex.isExtended() && qualifiedMiddle.isExtended() && qualifiedRing.isExtended() && qualifiedPinky.isExtended() && !qualifiedThumb.isExtended())
		    					return true;
		    			}
	    			}
	    			
	    		}
	    		else if(conditionFacedown)
	    		{
	    			if(qualifiedHand.palmNormal().getY() < 0)
	    			{
	    				FingerList fl = qualifiedHand.fingers().extended();
		    			if(list5.getSelectedValue().toString().contains("Finger"))
			    		{
				    		if(fl.count()==1)	
		    				{
		    					switch(list5.getSelectedValue().toString())
				    			{
				    			case "Index Finger":														//rest fingers closed, only this open
				    				if(fl.get(0).equals(qualifiedIndex))
				    					return true;
				    				break;
				    			case "Thumb Finger":
				    				if(fl.get(0).equals(qualifiedThumb))
				    					return true;
				    				break;
				    			case "Middle Finger":														//rest fingers closed, only this open
				    				if(fl.get(0).equals(qualifiedMiddle))
				    					return true;
				    				break;
				    			case "Ring Finger":
				    				if(fl.get(0).equals(qualifiedRing))
				    					return true;
				    				break;
				    			case "Pinky Finger":														//rest fingers closed, only this open
				    				if(fl.get(0).equals(qualifiedPinky))
				    					return true;
				    				break;
				    			default:
				    				break;
				    			}
		    				}
			    		}
		    			else if(list5.getSelectedValue().toString().equals("Spiderman") && fl.count()==3)
		    			{
		    				if(qualifiedThumb.isExtended() && qualifiedIndex.isExtended() && qualifiedPinky.isExtended())
		    					return true;
		    			}
		    			else if(list5.getSelectedValue().toString().equals("Rock Sign") && fl.count()==2)
		    			{
		    				if(qualifiedIndex.isExtended() && qualifiedPinky.isExtended())
		    					return true;
		    			}
		    			else if(list5.getSelectedValue().toString().equals("Pinch"))
		    			{
		    				if(qualifiedHand.pinchDistance() < 30)
		    				{
//		    					System.out.println("pinched");
		    					return true;
		    				}
		    			}
		    			else if(list5.getSelectedValue().toString().equals("Fist"))
		    			{
		    				if(qualifiedHand.grabAngle() > 3)
		    				{
//		    					System.out.println("grabbed");
		    					return true;
		    				}
		    			}
		    			else if(list5.getSelectedValue().toString().equals("Open"))
		    			{
		    				if(qualifiedHand.grabAngle() < 2)
		    				{
//		    					System.out.println("ungrabbed");
		    					return true;
		    				}
		    			}
		    			else if(list5.getSelectedValue().toString().equals("2 - Thumb Index"))
		    			{
		    				if(qualifiedThumb.isExtended() && qualifiedIndex.isExtended() && !qualifiedMiddle.isExtended() && !qualifiedRing.isExtended() && !qualifiedPinky.isExtended())
		    					return true;
		    			}
		    			else if(list5.getSelectedValue().toString().equals("2 - Index Middle"))
		    			{
		    				if(qualifiedIndex.isExtended() && qualifiedMiddle.isExtended() && !qualifiedRing.isExtended() && !qualifiedPinky.isExtended() && !qualifiedThumb.isExtended())
		    					return true;
		    			}
		    			else if(list5.getSelectedValue().toString().equals("2 - Thumb Pinky"))
		    			{
		    				if(qualifiedThumb.isExtended() && qualifiedPinky.isExtended() && !qualifiedMiddle.isExtended() && !qualifiedRing.isExtended() && !qualifiedIndex.isExtended())
		    					return true;
		    			}
		    			else if(list5.getSelectedValue().toString().equals("2 - Ring Pinky"))
		    			{
		    				if(qualifiedRing.isExtended() && qualifiedPinky.isExtended() && !qualifiedMiddle.isExtended() && !qualifiedThumb.isExtended() && !qualifiedIndex.isExtended())
		    					return true;
		    			}
		    			else if(list5.getSelectedValue().toString().equals("3 - Thumb Index Middle"))
		    			{
		    				if(qualifiedThumb.isExtended() && qualifiedIndex.isExtended() && qualifiedMiddle.isExtended() && !qualifiedRing.isExtended() && !qualifiedPinky.isExtended())
		    					return true;
		    			}
		    			else if(list5.getSelectedValue().toString().equals("3 - Middle Ring Pinky"))
		    			{
		    				if(qualifiedMiddle.isExtended() && qualifiedRing.isExtended() && qualifiedPinky.isExtended() && !qualifiedThumb.isExtended() && !qualifiedIndex.isExtended())
		    					return true;
		    			}
		    			else if(list5.getSelectedValue().toString().equals("3 - Thumb Ring Pinky"))
		    			{
		    				if(qualifiedThumb.isExtended() && qualifiedRing.isExtended() && qualifiedPinky.isExtended() && !qualifiedMiddle.isExtended() && !qualifiedIndex.isExtended())
		    					return true;
		    			}
		    			else if(list5.getSelectedValue().toString().equals("4 - Thumb Middle Ring Pinky"))
		    			{
		    				if(qualifiedThumb.isExtended() && qualifiedMiddle.isExtended() && qualifiedRing.isExtended() && qualifiedPinky.isExtended() && !qualifiedIndex.isExtended())
		    					return true;
		    			}
		    			else if(list5.getSelectedValue().toString().equals("4 - Index Middle Ring Pinky"))
		    			{
		    				if(qualifiedIndex.isExtended() && qualifiedMiddle.isExtended() && qualifiedRing.isExtended() && qualifiedPinky.isExtended() && !qualifiedThumb.isExtended())
		    					return true;
		    			}
	    			}
	    		}
	    	}
    	} 
    	else if(conditionInvisible && qualifiedHand == null)
    	{
    		return true;
    	}
//    	System.out.println(qualifiedHand == null); 			////////////////////////////////////////////////////////////////////
    	conditionVisible = false;
		conditionInvisible = false;
		conditionFaceup = false;
		conditionFacedown = false;
    	return false;
    }
    
    public void refreshLists()
    {
    	list1 = parent.list1;
    	list2 = parent.list2;
    	list3 = parent.list3;
    	list4 = parent.list4;
    	list5 = parent.list5;
    	list6 = parent.list6;
    	listQualifier = parent.listQualifier;
//    	getOrgans();
    }
    
    
	
	Thread pipelineLeap = new Thread() {
		public void run()
		{
//			int i = 0;
			System.out.println("starting pipeline");
			while(pipeline)
			{
				
				if(!monitor)
				{
					refreshLists();
//					getOrgans();
					pipeline = parent.pipeline;
					monitor = parent.monitor;
					if(!SampleListener.currentFrame.hands().isEmpty())
    				{
						getOrgans();
    					if(mainHand != null && mainFinger != null)
    					{
    						if(list2.getSelectedValue().toString().equals("Palm"))
    						{
    							int tempX = (int)mainHand.palmPosition().getX();
    							if(tempX != previousX)
    							{
//    								lblO.setLocation(((int)(tempX*0.2)+95), 50);
        							currentPositionXlabel.setText(Integer.toString(((int)tempX)));
        							previousX = tempX;
    							}    							
    						}
    						else
    						{
    							int tempX = (int)mainFinger.tipPosition().getX();
    							if(tempX != previousX)
    							{
//    								lblO.setLocation(((int)(tempX*0.2)+95), 50);
        							currentPositionXlabel.setText(Integer.toString(((int)tempX)));
        							previousX = tempX;
    							}
    						}

    					}
    				}
				}
				if(parent.btnTestOutput.isEnabled())
				{
					
					if(isQualified())
					{
						parent.qualifiedOrNot.setText("qualified");
						if(!parent.comboBoxCommand.getSelectedItem().toString().equals("Note ON"))
						{
							if(previousX > Integer.parseInt(rangeSliderValue1.getText().toString()) 
									&& previousX < Integer.parseInt(rangeSliderValue2.getText().toString())
									&& preprevious != previousX)
							{
								int mapped = map(previousX, Integer.parseInt(rangeSliderValue1.getText().toString()),
										Integer.parseInt(rangeSliderValue2.getText().toString()),
										Integer.parseInt(parent.textFieldRangeStart.getText().toString()),
										Integer.parseInt(parent.textFieldRangeEnd.getText().toString()));
								parent.mappedMidiData.setText(mapped +"");
								if(parent.comboBoxCommand.getSelectedItem().toString().equals("CC"))
								{
									try {
										rcvr.send(new ShortMessage(ShortMessage.CONTROL_CHANGE ,(int)parent.comboBoxChannel.getSelectedItem(),parent.decimalOfNote, mapped), -1);
									} catch (InvalidMidiDataException e) {
										e.printStackTrace();
									}	
								}
								else if(parent.comboBoxCommand.getSelectedItem().toString().equals("Pitch Bend"))
								{
									try {
										rcvr.send(new ShortMessage(ShortMessage.PITCH_BEND ,(int)parent.comboBoxChannel.getSelectedItem(),parent.decimalOfNote, mapped), -1);
									} catch (InvalidMidiDataException e) {
										e.printStackTrace();
									}	
								}
								preprevious = previousX;
							}
						}
						else if(parent.comboBoxCommand.getSelectedItem().toString().equals("Note ON"))
						{
							/////////////event is on
//							System.out.println("note onnnn");
							if((previousX < Integer.parseInt(rangeSliderValue1.getText().toString()) 
									|| previousX > Integer.parseInt(rangeSliderValue2.getText().toString()))
									&& noteOff)
							{
								try {
									rcvr.send(new ShortMessage(ShortMessage.NOTE_ON ,(int)parent.comboBoxChannel.getSelectedItem(),parent.decimalOfNote, 64), -1);
								} catch (InvalidMidiDataException e) {
									e.printStackTrace();
								}
								System.out.println("NOTE ON");
								noteOff = false;
							}
							else if((previousX > Integer.parseInt(rangeSliderValue1.getText().toString()) 
									&& previousX < Integer.parseInt(rangeSliderValue2.getText().toString()))
									&& !noteOff)
							{
								try {
									rcvr.send(new ShortMessage(ShortMessage.NOTE_OFF ,(int)parent.comboBoxChannel.getSelectedItem(),parent.decimalOfNote, 64), -1);
								} catch (InvalidMidiDataException e) {
									e.printStackTrace();
								}
								System.out.println("NOTE OFF");
								noteOff = true;
							}
						}
					}
					else
					{
						parent.qualifiedOrNot.setText("unqualified");
					}
							
				}
				
//				System.out.println(i++);
				try {
					Thread.sleep(pipelineSleepTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
    
	private void getOrgans()
	{
		if(!list3.isSelectionEmpty())
		{
			if(list1.getSelectedValue().toString().equals("Right Hand"))
			{
				mainHand = SampleListener.rightHand;
				switch(list2.getSelectedValue().toString())
				{
				case "Index Finger":
					mainFinger = SampleListener.indexR;
					break;
				case "Thumb Finger":
					mainFinger = SampleListener.thumbR;
					break;
				case "Middle Finger":
					mainFinger = SampleListener.middleR;
					break;
				case "Ring Finger":
					mainFinger = SampleListener.ringR;
					break;
				case "Pinky Finger":
					mainFinger = SampleListener.pinkyR;
					break;
				case "Palm":
					mainHand = SampleListener.rightHand;
					mainFinger = SampleListener.indexR;
					break;
				default:
					break;
				}
			}
			else
			{
				mainHand = SampleListener.leftHand;
				switch(list2.getSelectedValue().toString())
				{
				case "Index Finger":
					mainFinger = SampleListener.indexL;
					break;
				case "Thumb Finger":
					mainFinger = SampleListener.thumbL;
					break;
				case "Middle Finger":
					mainFinger = SampleListener.middleL;
					break;
				case "Ring Finger":
					mainFinger = SampleListener.ringL;
					break;
				case "Pinky Finger":
					mainFinger = SampleListener.pinkyL;
					break;
				case "Palm":
					mainHand = SampleListener.leftHand;
					mainFinger = SampleListener.indexL;
					break;
				default:
					break;
				}
			}
			//System.out.println("got main hands and finger");
		}
		if(listQualifier.getModel().getSize()==1)
		{
//			System.out.println("jsdljdsfds");
			if(list4.getSelectedValue().toString().equals("Right Hand"))
			{
//				System.out.println("Qualified right hand");
				qualifiedHand = SampleListener.rightHand;
				qualifiedThumb = SampleListener.thumbR;
				qualifiedIndex = SampleListener.indexR;
				qualifiedMiddle = SampleListener.middleR;
				qualifiedRing = SampleListener.ringR;
				qualifiedPinky = SampleListener.pinkyR;
			}
			else if(list4.getSelectedValue().toString().equals("Left Hand"))
			{
//				System.out.println("Qualified left hand");
				qualifiedHand = SampleListener.leftHand;
				qualifiedThumb = SampleListener.thumbL;
				qualifiedIndex = SampleListener.indexL;
				qualifiedMiddle = SampleListener.middleL;
				qualifiedRing = SampleListener.ringL;
				qualifiedPinky = SampleListener.pinkyL;
			}
			if(!list6.isSelectionEmpty())
			{
//				System.out.println("scanning list 6");
				switch(list6.getSelectedValue().toString())
				{
				case "Visible":
					conditionVisible = true;
					conditionInvisible = false;
					conditionFaceup = false;
					conditionFacedown = false;
					break;
				case "Invisible":
					conditionVisible = false;
					conditionInvisible = true;
					conditionFaceup = false;
					conditionFacedown = false;
					break;
				case "Face Up":
					conditionVisible = false;
					conditionInvisible = false;
					conditionFaceup = true;
					conditionFacedown = false;
					break;
				case "Face Down":
					conditionVisible = false;
					conditionInvisible = false;
					conditionFaceup = false;
					conditionFacedown = true;
					break;
				default:
					break;
						
				}
			}
		}
//		refreshLists();
	}
	
	public RotationXPanel(MappingProperties mp)
	{
		setBounds(0, 0, 235, 140);
		setLayout(null);
		
		lblO1.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblO1.setHorizontalAlignment(SwingConstants.CENTER);
        lblO1.setBounds(75, 50, 30, 27);
        add(lblO1);
        
        lblO2.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblO2.setHorizontalAlignment(SwingConstants.CENTER);
        lblO2.setBounds(115, 50, 30, 27);
        add(lblO2);
        
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
        parent = mp;
        setVisible(false);
        
	}
	
	int map(int x, int in_min, int in_max, int out_min, int out_max) {
		  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
	
	Point getCircleXY(int centerX, int centerY, int radius, double theta)
	{
		theta = (theta * Math.PI / 180);
//		int x = (centerX - (int) (Math.sin(theta) * radius));
//		int y = (centerY - (int) (Math.cos(theta) * radius));
		return new Point((centerX - (int) (Math.sin(theta) * radius)), (centerY - (int) (Math.cos(theta) * radius)));
//		int xx = (int) (radius * Math.cos(theta));
//		int yy = (int) (radius * Math.sin(theta));
//		return new Point(xx,yy);
//		return new Point((radius * Math.cos(theta)), (radius * Math.sin(theta)));
//		return new Point((centerX - (int) (Math.sin(theta) * radius)), (centerY - (int) (Math.cos(theta) * radius)));
	}
}
