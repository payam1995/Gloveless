
package mainPackage;

import java.io.IOException;
import java.lang.Math;
import com.leapmotion.leap.*;

class SampleListener extends Listener {
    public void onInit(Controller controller) {
        System.out.println("Initialized");
    }

    public void onConnect(Controller controller) {
        System.out.println("Connected");
    }

    public void onDisconnect(Controller controller) {
        //Note: not dispatched when running in a debugger.
        System.out.println("Disconnected");
    }

    public void onExit(Controller controller) {
        System.out.println("Exited");
    }

    ///public declarationssss
    
    public static Frame currentFrame;
    public static Hand leftHand, rightHand;
    public static Finger indexL, indexR, thumbL, thumbR, middleL, middleR, ringL, ringR, pinkyL, pinkyR;
    
    public void onFrame(Controller controller) {
        // Get the most recent frame and report some basic information
        currentFrame = controller.frame();
//        System.out.println("inside onframe function");
//        System.out.println("Frame id: " + frame.id()
//                         + ", timestamp: " + frame.timestamp()
//                         + ", hands: " + frame.hands().count()
//                         + ", fingers: " + frame.fingers().count());

        //Get hands
        if(currentFrame.hands().count()==1)
        {
        	if(currentFrame.hand(0).isLeft())
        		rightHand = null;
        	else if(!currentFrame.hand(0).isLeft())
        		leftHand = null;
        }
        if(currentFrame.hands().count()==0)
        {
        	leftHand = null;
        	rightHand = null;
        }
        for(Hand hand : currentFrame.hands()) {
//            String handType = hand.isLeft() ? "Left hand" : "Right hand";
//            System.out.println("  " + handType + ", id: " + hand.id()
//                             + ", palm position: " + hand.palmPosition());

            
            // Get the hand's normal vector and direction
//            Vector normal = hand.palmNormal();
//            Vector direction = hand.direction();

            // Calculate the hand's pitch, roll, and yaw angles
//            System.out.println("  pitch: " + Math.toDegrees(direction.pitch()) + " degrees, "
//                             + "roll: " + Math.toDegrees(normal.roll()) + " degrees, "
//                             + "yaw: " + Math.toDegrees(direction.yaw()) + " degrees");

            // Get arm bone
//            Arm arm = hand.arm();
//            System.out.println("  Arm direction: " + arm.direction()
//                             + ", wrist position: " + arm.wristPosition()
//                             + ", elbow position: " + arm.elbowPosition());

            // Get fingers
            if(hand.isLeft())
            {
            	leftHand = hand;
            	for (Finger finger : hand.fingers()) {
            		switch(finger.type())
            		{
            		case TYPE_INDEX:
            			indexL = finger;
            			break;
            		case TYPE_THUMB:
            			thumbL = finger;
            			break;
            		case TYPE_MIDDLE:
            			middleL = finger;
            			break;
            		case TYPE_RING:
            			ringL = finger;
            			break;
            		case TYPE_PINKY:
            			pinkyL = finger;
            			break;
            		default:
            			break;
            		}
            	}
            }
            else if(hand.isRight())
            {
            	rightHand = hand;
            	for (Finger finger : hand.fingers()) {
            		switch(finger.type())
            		{
            		case TYPE_INDEX:
            			indexR = finger;
//            			System.out.println(finger.tipVelocity().);
            			break;
            		case TYPE_THUMB:
            			thumbR = finger;
            			break;
            		case TYPE_MIDDLE:
            			middleR = finger;
            			break;
            		case TYPE_RING:
            			ringR = finger;
            			break;
            		case TYPE_PINKY:
            			pinkyR = finger;
            			break;
            		default:
            			break;
            		}
            	}
            }
////	            for (Finger finger : hand.fingers()) {
////	                System.out.println("    " + finger.type() + ", id: " + finger.id()
////	                                 + ", length: " + finger.length()
////	                                 + "mm, width: " + finger.width() + "mm");
//
//                //Get Bones
//                for(Bone.Type boneType : Bone.Type.values()) {
//                    Bone bone = finger.bone(boneType);
//                    System.out.println("      " + bone.type()
//                                     + " bone, start: " + bone.prevJoint()
//                                     + ", end: " + bone.nextJoint()
//                                     + ", direction: " + bone.direction());
//                }
//            }
        }

        if (!currentFrame.hands().isEmpty()) {
//            System.out.println();
        }
    }
}

class Sample {
    public static void main(String[] args) {
        // Create a sample listener and controller
        SampleListener listener = new SampleListener();
        Controller controller = new Controller();

        // Have the sample listener receive events from the controller
        controller.addListener(listener);
        controller.setPolicy(Controller.PolicyFlag.POLICY_OPTIMIZE_HMD);
        // Keep this process running until Enter is pressed
        System.out.println("Press Enter to quit...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Remove the sample listener when done
        controller.removeListener(listener);
    }
}
