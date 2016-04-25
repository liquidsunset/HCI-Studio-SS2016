import java.io.IOException;

import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.State;

class LeapListener extends Listener {
	public void onInit(Controller controller) {
		System.out.println("Initialized");
	}
	
	public void onConnect(Controller controller) {
		System.out.println("Connected to Motion Sensor");
		
		//Set gestures that shall be tracked
		controller.enableGesture(Gesture.Type.TYPE_SWIPE);
		controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
		controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
		controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
	}
	
	public void onDisconnect(Controller controller) {
		System.out.print("Motion Sensor disconnected");
	}
	
	public void onExit(Controller controller) {
		System.out.print("Exited");
	}	
	
	public void onFrame(Controller controller) {
		Frame frame = controller.frame();
		
		//FrameData
		/*System.out.println("Frame id: " + frame.id()
							+ ", Timestamp: " + frame.timestamp()
							+ ", Hands: " + frame.hands().count()
							+ ", Fingers: " + frame.fingers().count()
							+ ", Tools: " + frame.tools().count()
							+ ", Gestures: " + frame.gestures().count());
		*/
		
		//HandData
		/*for (Hand hand : frame.hands()) {
			String handType = hand.isLeft() ? "Left Hand" : "Right Hand";
			System.out.println(handType + " " + ", id: " + hand.id()
								+ ", Palm Position: " + hand.palmPosition());
			
			Vector normal = hand.palmNormal();
			Vector direction = hand.direction();
			
			
			System.out.println("Pitch: " + Math.toDegrees(direction.pitch())
								+ " Roll: " + Math.toDegrees(normal.roll())
								+ " Yaw: " + Math.toDegrees(direction.yaw()));
		}*/
		
		//Finger- and BoneData
		/*for (Finger finger : frame.fingers()) {
			System.out.println("Finger Type: " + finger.type()
								+ " ID: " + finger.id()
								+ " finger length (mm): " + finger.length()
								+ " finger width (mm): " + finger.width());
			
			for (Bone.Type boneType : Bone.Type.values()) {
				Bone bone = finger.bone(boneType);
				System.out.println("Bone Type: " + bone.type()
									+ " Start: " + bone.prevJoint()
									+ " End: " + bone.nextJoint()
									+ " Direction: " + bone.direction());
			}
		}*/
		
		//ToolData
		/*for (Tool tool : frame.tools()) {
			System.out.println("Tool ID: " + tool.id()
								+ " Tip Position: " + tool.direction()
								+ " Direction: " + tool.direction()
								+ " Width: " + tool.width()
								+ " Touch Distance (mm): " + tool.touchDistance());
		}*/
		
		//Circle Gesture Data
		//Hand Swipe Gesture Data
		//Screen Tap Gesture Data
		//Key Tap Gesture Data
		GestureList gestures = frame.gestures();
		for (int i = 0; i < gestures.count(); i++) {
			Gesture gesture = gestures.get(i);
			
			switch (gesture.type()) {
				case TYPE_CIRCLE:
					CircleGesture circle = new CircleGesture(gesture);
					
					String clockwiseness;
					if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI/4) {
						clockwiseness = "clockwise";
					}
					else {
						clockwiseness = "counter clockwise";
					}
					
					double sweptAngle = 0;
					if (circle.state() != State.STATE_START) {
						CircleGesture previous = new CircleGesture(controller.frame(1).gesture(circle.id()));
						sweptAngle = (circle.progress() - previous.progress()) * 2 * Math.PI;
					}
					
					System.out.println("Circle ID: " + circle.id()
										+ " State: " + circle.state()
										+ " Progress: " + circle.progress()
										+ " Radius: " + circle.radius()
										+ " Angle: " + Math.toDegrees(sweptAngle)
										+ " " + clockwiseness);
				case TYPE_SWIPE:
					SwipeGesture swipe = new SwipeGesture(gesture);
					System.out.println("Swipe ID: " + swipe.id()
										+ " State: " + swipe.state()
										+ " Swipe Position: " + swipe.position()
										+ " Direction: " + swipe.direction()
										+ " Speed: " + swipe.speed());
					break;
					
				case TYPE_SCREEN_TAP:
					ScreenTapGesture screenTap = new ScreenTapGesture(gesture);
					System.out.println("Tap ID: " + screenTap.id()
										+ " State: " + screenTap.state()
										+ " Position: " + screenTap.position()
										+ " Direction: " + screenTap.direction());
					break;
					
				case TYPE_KEY_TAP:
					KeyTapGesture keyTap = new KeyTapGesture();
					System.out.println("ID: " + keyTap.id()
										+ " State: " + keyTap.state()
										+ " Position: " + keyTap.position()
										+ " Direction: " + keyTap.direction());
					
				default:
					System.out.println("Unknown gesture!");
					break;
			
			
			
			}
		}
	}
}

public class LeapController {

	public static void main(String[] args) {
		LeapListener listener = new LeapListener();
		Controller controller = new Controller();
			
		controller.addListener(listener);
		
		System.out.println("Press enter to quit");
		
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		controller.removeListener(listener);
		

		
	}

}
