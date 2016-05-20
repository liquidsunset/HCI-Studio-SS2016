import com.leapmotion.leap.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;

/**
 * Created by liquidsunset on 18.05.16.
 */

public class LeapListener extends Listener {

    private ObjectProperty<Point2D> point = new SimpleObjectProperty<>();
    private ObjectProperty<Double> indexFingerAngle = new SimpleObjectProperty<>();
    private ObjectProperty<Double> zoomAngle = new SimpleObjectProperty<>();


    public ObservableValue<Point2D> pointProperty(){ return point; }
    public ObservableValue<Double> indexFingerAngleProperty(){ return indexFingerAngle; }
    public ObservableValue<Double> zoomAnglePropertz(){ return zoomAngle; }

    @Override
    public void onFrame(Controller controller) {
        super.onFrame(controller);
        Frame frame = controller.frame();
        if (!frame.hands().isEmpty()) {
            Screen screen = controller.locatedScreens().get(0);
            if (screen != null && screen.isValid()){
                Hand hand = frame.hands().rightmost();

                for (Gesture gesture : frame.gestures()){


                    switch (gesture.type()) {
                        case TYPE_KEY_TAP:
                            System.out.println("key tap");
                            break;
                        case TYPE_SCREEN_TAP:
                            System.out.println("screen tap");
                            break;
                        case TYPE_CIRCLE:
                            System.out.println("circle");
                            break;
                        case TYPE_SWIPE:
                            SwipeGesture swipe = new SwipeGesture(gesture);
                            Vector swipeDirection = swipe.direction();
                            System.out.println(swipeDirection.getX());
                            if(swipeDirection.getX() < 0) {
                                System.out.println("left");
                            } else if (swipeDirection.getX() >= 0){
                                System.out.println("right");
                            }
                            break;
                    }

                }
                if(hand.isValid()){
                    Vector intersect = screen.intersect(hand.palmPosition(),hand.direction(), true);
                    point.setValue(new Point2D(screen.widthPixels()*Math.min(1d,Math.max(0d,intersect.getX())),
                            screen.heightPixels()*Math.min(1d,Math.max(0d,(1d-intersect.getY())))));
                    Finger indexFinger = hand.fingers().fingerType(Finger.Type.TYPE_INDEX).rightmost();

                    indexFingerAngle.setValue(LeapCalcFunctions.calcAngelBetweenVectorsInDegrees(
                            indexFinger.direction(), Vector.xAxis()));


                }
            }
        }
    }
}
