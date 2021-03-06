package mainPackage;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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


public class PositionXPanelOld extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JLabel currentPositionXlabel = new JLabel("");
	private final JLabel lblO = new JLabel("O");
	private JLabel rangeSliderLabel1 = new JLabel();
    private JLabel rangeSliderValue1 = new JLabel();
    private JLabel rangeSliderLabel2 = new JLabel();
    private JLabel rangeSliderValue2 = new JLabel();
    private RangeSlider rangeSlider = new RangeSlider();
    public Hand mainHand = null, qualifiedHand = null;
	public Finger mainFinger = null, qualifiedThumb= null, qualifiedIndex= null, qualifiedMiddle= null, qualifiedRing= null, qualifiedPinky= null;
	boolean monitor = false, pipeline = false, pipelineSwitch = true;
	boolean conditionVisible = false, conditionInvisible = false, conditionFaceup = false, conditionFacedown = false;
	boolean noteOff = true, breakLoop = false, breakPipeline = false, iExisted = false;
	int monitorSleepTime = 15, pipelineSleepTime = 15;
	MappingProperties parent = null;
	JList list1, list2, list3, list4, list5, list6, listQualifier;
	static Receiver rcvr = MainWindow.rcvr;
	Thread monitorLeap = null, pipelineLeap = null;
	int previousX = 0, preprevious = 0;
	int hmdMode = 0;
	
	
	public void displayStats()
    {
		
		if(MainWindow.hmdMode)
			hmdMode = -1;
		else hmdMode =1;
    	monitor = true;
    	refreshLists();
    	breakLoop = false;
    	breakPipeline = false;
    	System.out.println("display stats called");

    	startThread();
    	System.out.println("I existed? " +iExisted);
    	System.out.println("btn enabled? " +parent.btnTestOutput.isEnabled());
    	if(iExisted && parent.btnTestOutput.isEnabled())
    	{
    		System.out.println("starting pipeline again");
    		startPipelineThread();	
    	}
    	
    }
	
	public void startThread()
	{
		monitorLeap = new Thread() {
			public void run()
			{
//				System.out.println("starting monitor monitor status: "+ monitor);
				while(monitor)
				{
					System.out.println("xxxmonitorxxx");
					refreshLists();
					if(!SampleListener.currentFrame.hands().isEmpty())
					{
						getOrgans();
						if(mainHand != null && mainFinger != null)
						{
							if(list2.getSelectedValue().toString().equals("Palm"))
							{
//								System.out.println("monitor x still on");
								int tempX = ((int)mainHand.palmPosition().getX())*hmdMode;
								if(tempX != previousX)
								{
									lblO.setLocation(((int)(tempX*0.2)+95), 50);
	    							currentPositionXlabel.setText(Integer.toString(((int)tempX)));
	    							previousX = tempX;
								}    							
							}
							else
							{
								int tempX = ((int)mainFinger.tipPosition().getX())*hmdMode;
								if(tempX != previousX)
								{
									lblO.setLocation(((int)(tempX*0.2)+95), 50);
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
					
					if(pipeline && pipelineSwitch)
					{
						
//						pipelineLeap.start();
						pipelineSwitch = false;
						iExisted = true;
						startPipelineThread();
					}
					if(breakLoop)
					{
						
						break;
						
					}
				}
				System.out.println("leaving monitor leap thread position X");
			}

		};
		if(!breakLoop && !monitorLeap.isAlive())
		{
			
			monitorLeap.start();
		}
		breakLoop = false;
	}
	
	public void hideStats()
    {
//    	parent.monitor = false;
    	monitor = false;
    	breakLoop = true;
//    	monitorLeap.stop();
    	if(!parent.runInBackground)
    	{
    		breakPipeline = false;
    		pipeline = false;
//    		parent.pipeline = false;
    		pipelineSwitch = true;
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
	    				if(qualifiedHand.pinchDistance() < 20)
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
		    				if(qualifiedHand.pinchDistance() < 20)
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
		    				if(qualifiedHand.pinchDistance() < 20)
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
    	else if(conditionInvisible)
    	{
    		if(qualifiedHand != null)
    		{
//    			System.out.println("in the not null loop");
    			FingerList fl = qualifiedHand.fingers().extended();
    			if(list5.getSelectedValue().toString().contains("Finger"))
        		{
    	    		if(fl.count()==1)	
    				{
    					switch(list5.getSelectedValue().toString())
    	    			{
    	    			case "Index Finger":														//rest fingers closed, only this open
    	    				if(fl.get(0).equals(qualifiedIndex))
    	    					return false;
    	    				break;
    	    			case "Thumb Finger":
    	    				if(fl.get(0).equals(qualifiedThumb))
    	    					return false;
    	    				break;
    	    			case "Middle Finger":														//rest fingers closed, only this open
    	    				if(fl.get(0).equals(qualifiedMiddle))
    	    					return false;
    	    				break;
    	    			case "Ring Finger":
    	    				if(fl.get(0).equals(qualifiedRing))
    	    					return false;
    	    				break;
    	    			case "Pinky Finger":														//rest fingers closed, only this open
    	    				if(fl.get(0).equals(qualifiedPinky))
    	    					return false;
    	    				break;
    	    			default:
    	    				break;
    	    			}
    				}
        		}
    			if(qualifiedHand != null)
    			{
    				if(list5.getSelectedValue().toString().equals("Spiderman") && fl.count()==3)
        			{
        				if(qualifiedThumb.isExtended() && qualifiedIndex.isExtended() && qualifiedPinky.isExtended())
        					return false;
        			}
        			else if(list5.getSelectedValue().toString().equals("Rock Sign") && fl.count()==2)
        			{
        				if(qualifiedIndex.isExtended() && qualifiedPinky.isExtended())
        					return false;
        			}
        			else if(list5.getSelectedValue().toString().equals("Pinch"))
        			{
        				if(qualifiedHand.pinchDistance() < 20)
        				{
//        					System.out.println("pinched11");
        					return false;
        				}
        			}
        			else if(list5.getSelectedValue().toString().equals("Fist"))
        			{
//        				System.out.println(qualifiedHand.grabAngle());
        				if(qualifiedHand.grabAngle() > 3)
        				{
//        					System.out.println("grabbed");
        					return false;
        				}
        			}
        			else if(list5.getSelectedValue().toString().equals("Open"))
        			{
        				if(qualifiedHand.grabAngle() < 2)
        				{
//        					System.out.println("ungrabbed");
        					return false;
        				}
//        				System.out.println("here");
        			}
    			}
    			if(qualifiedHand != null)
    			{
    				if(list5.getSelectedValue().toString().equals("2 - Thumb Index"))
        			{
        				if(qualifiedThumb.isExtended() && qualifiedIndex.isExtended() && !qualifiedMiddle.isExtended() && !qualifiedRing.isExtended() && !qualifiedPinky.isExtended())
        					return false;
        			}
        			else if(list5.getSelectedValue().toString().equals("2 - Index Middle"))
        			{
        				if(qualifiedIndex.isExtended() && qualifiedMiddle.isExtended() && !qualifiedRing.isExtended() && !qualifiedPinky.isExtended() && !qualifiedThumb.isExtended())
        					return false;
        			}
        			else if(list5.getSelectedValue().toString().equals("2 - Thumb Pinky"))
        			{
        				if(qualifiedThumb.isExtended() && qualifiedPinky.isExtended() && !qualifiedMiddle.isExtended() && !qualifiedRing.isExtended() && !qualifiedIndex.isExtended())
        					return false;
        			}
        			else if(list5.getSelectedValue().toString().equals("2 - Ring Pinky"))
        			{
        				if(qualifiedRing.isExtended() && qualifiedPinky.isExtended() && !qualifiedMiddle.isExtended() && !qualifiedThumb.isExtended() && !qualifiedIndex.isExtended())
        					return false;
        			}
        			else if(list5.getSelectedValue().toString().equals("3 - Thumb Index Middle"))
        			{
        				if(qualifiedThumb.isExtended() && qualifiedIndex.isExtended() && qualifiedMiddle.isExtended() && !qualifiedRing.isExtended() && !qualifiedPinky.isExtended())
        					return false;
        			}
        			else if(list5.getSelectedValue().toString().equals("3 - Middle Ring Pinky"))
        			{
        				if(qualifiedMiddle.isExtended() && qualifiedRing.isExtended() && qualifiedPinky.isExtended() && !qualifiedThumb.isExtended() && !qualifiedIndex.isExtended())
        					return false;
        			}
        			else if(list5.getSelectedValue().toString().equals("3 - Thumb Ring Pinky"))
        			{
        				if(qualifiedThumb.isExtended() && qualifiedRing.isExtended() && qualifiedPinky.isExtended() && !qualifiedMiddle.isExtended() && !qualifiedIndex.isExtended())
        					return false;
        			}
        			else if(list5.getSelectedValue().toString().equals("4 - Thumb Middle Ring Pinky"))
        			{
        				if(qualifiedThumb.isExtended() && qualifiedMiddle.isExtended() && qualifiedRing.isExtended() && qualifiedPinky.isExtended() && !qualifiedIndex.isExtended())
        					return false;
        			}
        			else if(list5.getSelectedValue().toString().equals("4 - Index Middle Ring Pinky"))
        			{
        				if(qualifiedIndex.isExtended() && qualifiedMiddle.isExtended() && qualifiedRing.isExtended() && qualifiedPinky.isExtended() && !qualifiedThumb.isExtended())
        					return false;
        			}
    			}
    			return true;
    			
    		}
    		
    		if(qualifiedHand == null)
    		{
//    			System.out.println("qualified hand is null");
    			return true;
    			
    		}
    			
//    		if(SampleListener.currentFrame.hands().count()>0)
//    		{
//    			for(Hand hand : SampleListener.currentFrame.hands()) 
//    			{
//        			if(hand.equals(qualifiedHand))
//        			{
//        				System.out.println("returning from here where hand is qualified");
//        				return false;
//        			}
//        				
//        		}
//    		}
    			
//    		return false;
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
    
    public void startPipelineThread()
    {
    	pipelineLeap = new Thread() {
    		public void run()
    		{
//    			int i = 0;
    			System.out.println("starting pipeline position X");
    			while(pipeline)
    			{
    				System.out.println("xxxpipelinexxx");
    				if(!monitor)
    				{
    					refreshLists();
//    					getOrgans();
    					if(!breakPipeline)
    					{
    						pipeline = parent.pipeline;
        					monitor = parent.monitor;	
    					}
    					
    					if(!SampleListener.currentFrame.hands().isEmpty())
        				{
    						getOrgans();
        					if(mainHand != null && mainFinger != null)
        					{
        						if(list2.getSelectedValue().toString().equals("Palm"))
        						{
        							int tempX = ((int) mainHand.palmPosition().getX())*hmdMode;
        							if(tempX != previousX)
        							{
//        								lblO.setLocation(((int)(tempX*0.2)+95), 50);
            							currentPositionXlabel.setText(Integer.toString(((int)tempX)));
            							previousX = tempX;
        							}    							
        						}
        						else
        						{
        							int tempX = ((int)mainFinger.tipPosition().getX())*hmdMode;
        							if(tempX != previousX)
        							{
//        								lblO.setLocation(((int)(tempX*0.2)+95), 50);
            							currentPositionXlabel.setText(Integer.toString(((int)tempX)));
            							previousX = tempX;
        							}
        						}

        					}
        				}
    				}
    				if(parent.btnTestOutput.isEnabled())
    				{
//    					System.out.println("enable btn in qualification loop X");
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
//    							System.out.println("note onnnn");
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
    				if(breakPipeline)
    				{
    					break;
    				}
//    				System.out.println(i++);
    				try {
    					Thread.sleep(pipelineSleepTime);
    				} catch (InterruptedException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    			}
    			System.out.println("leaving pipeline X");
    		}
    	};
    	System.out.println(!breakPipeline);
    	System.out.println(!pipelineLeap.isAlive());
    	if(!breakPipeline && !pipelineLeap.isAlive())
		{
			
			pipelineLeap.start();
		}
    	breakPipeline = false;
    }
	
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
	
    public PositionXPanelOld(MappingProperties mp)
	{
		setBounds(0, 0, 235, 140);
		setLayout(null);
		
		lblO.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblO.setHorizontalAlignment(SwingConstants.CENTER);
        lblO.setBounds(95, 50, 30, 27);
        
        add(lblO);
        
        currentPositionXlabel.setFont(new Font("Arial", Font.PLAIN, 17));
        currentPositionXlabel.setHorizontalAlignment(SwingConstants.CENTER);
        currentPositionXlabel.setBounds(77, 11, 63, 27);
        
        add(currentPositionXlabel);
        
        rangeSlider.setBounds(0, 79, 235, 38);
        rangeSlider.setPreferredSize(new Dimension(235, 38));
        rangeSlider.setMinimum(-500);
        rangeSlider.setMaximum(500);
        
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
        

        rangeSlider.setValue(-500);
        rangeSlider.setUpperValue(500);
        
        parent = mp;
        
        parent.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub
				if(iExisted && !parent.runInBackground)
				{
					breakPipeline=false;
					pipeline = true;
					parent.pipeline = true;
					startPipelineThread();
				}
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
//				System.out.println("ajfslkasfjklasfjklasfjklasdfjkl HIDDEEEENNNNNNNN");
			}
		});
        
        setVisible(false);
	}
    
    int map(int x, int in_min, int in_max, int out_min, int out_max) {
		  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
}
