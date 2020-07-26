package mainPackage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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
import com.leapmotion.leap.Vector;



public class PositionXPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private final JLabel currentPositionXlabel = new JLabel("");
	private final JLabel lblO = new JLabel("O");
	private JLabel rangeSliderLabel1 = new JLabel();
    private JLabel rangeSliderValue1 = new JLabel();
    private JLabel rangeSliderLabel2 = new JLabel();
    private JLabel rangeSliderValue2 = new JLabel();
    private RangeSlider rangeSlider = new RangeSlider();
    MappingProperties parent = null;
    public Hand mainHand = null, qualifiedHand = null;
	public Finger mainFinger = null, qualifiedThumb= null, qualifiedIndex= null, qualifiedMiddle= null, qualifiedRing= null, qualifiedPinky= null;
	JList list1, list2, list3, list4, list5, list6, listQualifier;
	boolean conditionVisible = false, conditionInvisible = false, conditionFaceup = false, conditionFacedown = false;
	boolean breakMonitor = false, breakPipeline = false;
	int previousX = 0, preprevious = 0;
	int hmdMode = 0;
	int monitorSleepTime = 15, pipelineSleepTime = 15;
	boolean isMonitoring = false, isPipelining = false, noteOff = true, thisPanelShown = false, pipelineWhileCondition = false, monitorWhileCondition = false,
			qualificationMode = false;
	static Receiver rcvr = MainWindow.rcvr;
	Thread pipelineThread = null, monitorThread = null;
	PositionXPanel thisPanel;
	int relativeMapped = 0, relativeDifference = 0, mapped = 0, velocityX = 0;
	
	public void runMonitoringThread()
	{
		monitorThread = new Thread() {
			
			public void run()
	    	{
				breakMonitor = false;
	    		isMonitoring = true;
//	    		System.out.println("inside the run function break monitor status: " + breakMonitor);
	    		while(monitorWhileCondition)
	    		{
//	    			System.out.println("main monitoring");
					if(!SampleListener.currentFrame.hands().isEmpty())
					{
						refreshLists();
						getOrgans();
//						System.out.println("main hand null? " + mainHand == null);
						if(mainHand != null && mainFinger != null)
						{
							if(list2.getSelectedValue().toString().equals("Palm"))
							{
//								System.out.println("monitor x still on");
								int tempX = ((int)mainHand.palmPosition().getX())*hmdMode;
//								System.out.println(mainHand.palmPosition().getX());
//								System.out.println(mainHand.palmVelocity().getX());
								if(tempX != previousX)
								{
//									System.out.println("jsdlfkj");
									lblO.setLocation(((int)(tempX*0.2)+95), 50);
	    							currentPositionXlabel.setText(Integer.toString(((int)tempX)));
	    							previousX = tempX;
	    							
	    							velocityX = (int) mainHand.palmVelocity().getX();
//	    							System.out.println(velocityX);
								}    							
							}
							else
							{
								int tempX = ((int)mainFinger.tipPosition().getX())*hmdMode;
//								velocityX = (int) mainFinger.tipVelocity().getX();
								if(tempX != previousX)
								{
									lblO.setLocation(((int)(tempX*0.2)+95), 50);
	    							currentPositionXlabel.setText(Integer.toString(((int)tempX)));
	    							velocityX = (tempX - previousX)*35;
	    							previousX = tempX;
//	    							Vector velocity = mainFinger.tipVelocity();
//	    							System.out.println(velocity.magnitude()+" sdf ");
//	    							System.out.println(velocityX);
								}
							}
						}
					}
//					if(!isPipelining)
//						System.out.println("should start pipeline now");
					if(parent!=null)
					{
						if(parent.btnTestOutput.isEnabled() && !isPipelining)
		    			{
		    				startPipeline();
		    			}	
					}
					
	    			if(breakMonitor)
	    			{
	    				isMonitoring = false;
	    				monitorWhileCondition = false;
	    				break;
	    			}
					try {
						Thread.sleep(monitorSleepTime);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
	    		}
	    		breakMonitor = false;
	    		isMonitoring = false;
	    	}
	    };
	    if(!monitorThread.isAlive() && !isMonitoring && thisPanelShown)
	    {
	    	System.out.println("about to start monitoring thread");
	    	monitorThread.start();
	    }
	    	
	}
    
	public void runpipeliningThread()
	{
		pipelineThread = new Thread() {
	    	
	    	public void run()
	    	{
	    		isPipelining = true;
	    		while(pipelineWhileCondition)
	    		{
//	    			System.out.println("isMonitoring? : " + isMonitoring);
	    			if(!isMonitoring)
					{
//	    				System.out.println("2222222222222222222");
//	    				System.out.println("jsfldksdfjklsdfjkl" + list6.getSelectedValue().toString());
						if(!SampleListener.currentFrame.hands().isEmpty())
	    				{
							refreshLists();
							getOrgans();
	    					if(mainHand != null && mainFinger != null)
	    					{
//	    						System.out.println("here");
	    						if(list2.getSelectedValue().toString().equals("Palm"))
	    						{
	    							int tempX = ((int) mainHand.palmPosition().getX())*hmdMode;
	    							 
//	    							int tempVelocityX = ((int) mainHand.palmVelocity().getX())*hmdMode;
//	    							System.out.println(mainHand.palmVelocity().getX());
	    							if(tempX != previousX)
	    							{
//	    								lblO.setLocation(((int)(tempX*0.2)+95), 50);
	        							currentPositionXlabel.setText(Integer.toString(((int)tempX)));
	        							previousX = tempX;
	        							velocityX = (int) mainHand.palmVelocity().getX();
//	        							System.out.println(velocityX);
	    							}    							
	    						}
	    						else
	    						{
	    							int tempX = ((int)mainFinger.tipPosition().getX())*hmdMode;
	    							
	    							if(tempX != previousX)
	    							{
//	    								lblO.setLocation(((int)(tempX*0.2)+95), 50);
	        							currentPositionXlabel.setText(Integer.toString(((int)tempX)));
	        							velocityX = (tempX - previousX)*35;
	        							
	        							previousX = tempX;
//	        							velocityX = (int) mainFinger.tipVelocity().getX();
//	        							System.out.println(velocityX);
	    							}
	    						}

	    					}
	    				}
					}
					if(parent.btnTestOutput.isEnabled())
					{
//						System.out.println("sfjdlsdfjkl");
//						System.out.println("333333");
						if(isQualified())
						{
							parent.qualifiedOrNot.setText("Qualified");
							parent.qualifiedOrNotPanel.setBackground(new Color(102, 204, 51));
//							System.out.println("qualified");
							if(!parent.comboBoxCommand.getSelectedItem().toString().equals("Note ON"))
							{
								
								
								
								if(previousX > Integer.parseInt(rangeSliderValue1.getText().toString()) 
										&& previousX < Integer.parseInt(rangeSliderValue2.getText().toString())
										&& preprevious != previousX)
								{
									mapped = map(previousX, Integer.parseInt(rangeSliderValue1.getText().toString()),
											Integer.parseInt(rangeSliderValue2.getText().toString()),
											Integer.parseInt(parent.textFieldRangeStart.getText().toString()),
											Integer.parseInt(parent.textFieldRangeEnd.getText().toString()));
									
									
									if(parent.relativeMIDICheckbox.isSelected())
									{
										if(!qualificationMode)
										{
//											System.out.println(mapped);
											relativeMapped = mapped;
											qualificationMode = true;
										}
										
										mapped = Math.max(0, Math.min(mapped + relativeDifference, 127));
									}
									

									if(thisPanelShown)
									{
										parent.mappedMidiData.setText(mapped +"");
										parent.midiProgressBar.setValue(mapped);	
									}
									
									
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
								if((previousX < Integer.parseInt(rangeSliderValue1.getText().toString()) 
										|| previousX > Integer.parseInt(rangeSliderValue2.getText().toString()))
										&& noteOff)
								{
									try {
//										System.out.println(map(Math.abs(velocityX),0,2500,0,127));
										velocityX = Math.max(-2500, Math.min(velocityX, 2500));
										rcvr.send(new ShortMessage(ShortMessage.NOTE_ON ,(int)parent.comboBoxChannel.getSelectedItem(),parent.decimalOfNote, map(Math.abs(velocityX),-2500,2500,0,127)), -1);
//										System.out.println((int)parent.comboBoxChannel.getSelectedItem());
//										rcvr.send(new ShortMessage(ShortMessage.NOTE_ON ,40, 20), -1);
//										System.out.println(velocityX);
//										System.out.println(map(velocityX,0,2000,0,127));
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
//										System.out.println(velocityX);
										velocityX = Math.max(-2500, Math.min(velocityX, 2500));
										rcvr.send(new ShortMessage(ShortMessage.NOTE_OFF ,(int)parent.comboBoxChannel.getSelectedItem(),parent.decimalOfNote, map(Math.abs(velocityX),-2500,2500,0,127)), -1);
//										System.out.println(parent.decimalOfNote);
//										rcvr.send(new ShortMessage(ShortMessage.NOTE_OFF ,40, 20), -1);
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
							if(parent.relativeMIDICheckbox.isSelected())
							{
								if(qualificationMode)
								{
//									System.out.println(previousX);
									relativeDifference = mapped - relativeMapped;
//									System.out.println(relativeDifference);
									qualificationMode = false;
								}	
							}
							
							parent.qualifiedOrNot.setText("Unqualified");
							parent.qualifiedOrNotPanel.setBackground(new Color(204, 51, 0));
						}
								
					}
					if(breakPipeline)
					{
						isPipelining = false;
						break;
					}
					try {
						Thread.sleep(pipelineSleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    		}
	    		breakPipeline = false;
	    		isPipelining = false;
	    	}
	    };
	    System.out.println("pipelinethread alive? "+pipelineThread.isAlive());
	    if(!pipelineThread.isAlive() && !isPipelining)
	    	pipelineThread.start();
	}
    
	
    public void startMonitoring()
    {
    	if(MainWindow.hmdMode)
			hmdMode = -1;
    	else hmdMode = 1;
    	if(!isMonitoring)
    	{
    		monitorWhileCondition = true;
    		runMonitoringThread();
    	}
    		
    }
    
    public void stopMonitoring()
    {
    	
    	breakMonitor = true;
    	monitorWhileCondition = false;
    }
    
    public void startPipeline()
    {
    	
    	pipelineWhileCondition = true;
    	runpipeliningThread();
    }
    
    public void stopPipeline()
    {
    	breakPipeline = true;
    	pipelineWhileCondition = false;
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
    
	public PositionXPanel(MappingProperties mp, MainWindow mw)
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
        setVisible(false);
        thisPanel = this;
        
        addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {
				thisPanelShown = true;
				startMonitoring();
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
				
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
				System.out.println("Component hidden");
				thisPanelShown = false;
				breakMonitor = true;
				breakPipeline = true;
				stopMonitoring();
				stopPipeline();
			}
		});
        
        parent.addComponentListener(parentListener);
        parent.btnDelete.addMouseListener(deteleBtnListener);
        parent.btnClose.addMouseListener(saveBtnListener);
	}
	MouseListener saveBtnListener = new MouseListener() {
		
		@Override
		public void mouseReleased(MouseEvent e) {
			thisPanelShown = false;
			breakMonitor = true;
			monitorWhileCondition = false;
			stopMonitoring();
			if(!parent.runInBackground)
			{
				breakPipeline = true;
				pipelineWhileCondition = false;
				stopPipeline();	
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
	};
	
	MouseListener deteleBtnListener = new MouseListener() {
		
		@Override
		public void mouseReleased(MouseEvent e) {
			thisPanelShown = false;
			breakMonitor = true;
			breakPipeline = true;
			stopMonitoring();
			stopPipeline();
			parent.removeComponentListener(parentListener);
	        parent.btnDelete.removeMouseListener(deteleBtnListener);
	        parent = null;
	        thisPanel = null;
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
	};
	ComponentListener parentListener = new ComponentListener() {
		
		@Override
		public void componentShown(ComponentEvent e) {
			monitorWhileCondition = true;
			pipelineWhileCondition = true;
			thisPanelShown = true;
			System.out.println("breakpipeline : \t\t" + breakPipeline +
					"\npipelinewhile condition : \t"+ pipelineWhileCondition+
					"\nbreakmonitor : \t\t\t"+ breakMonitor +
					"\nmonitorwhile condition : \t"+monitorWhileCondition+
					"\nIs monitoring : \t\t"+isMonitoring);
			startMonitoring();
		}
		
		@Override
		public void componentResized(ComponentEvent e) {
			
		}
		
		@Override
		public void componentMoved(ComponentEvent e) {
			
		}
		
		@Override
		public void componentHidden(ComponentEvent e) {
			System.out.println("parent hidden");
			if(!parent.runInBackground)
			{
				breakPipeline = true;
				stopPipeline();
			}
			breakMonitor = true;
			stopMonitoring();
			System.out.println("breakpipeline : \t\t" + breakPipeline +
					"\npipelinewhile condition : \t"+ pipelineWhileCondition+
					"\nbreakmonitor : \t\t\t"+ breakMonitor +
					"\nmonitorwhile condition : \t"+monitorWhileCondition);
		}
	};
	
	int map(int x, int in_min, int in_max, int out_min, int out_max) {

		  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
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
    
}
