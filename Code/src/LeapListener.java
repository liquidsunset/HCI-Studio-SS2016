import com.leapmotion.leap.*;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

/**
 * Created by liquidsunset on 18.05.16.
 */

class LeapListener extends Listener {

    private ObjectProperty<Integer> indexFingerElement = new SimpleObjectProperty<>();
    private ObjectProperty<Vector> zoomVector = new SimpleObjectProperty<>();
    private ObjectProperty<Boolean> editMode = new SimpleObjectProperty<>();
    private ObjectProperty<KeyTapGesture> keyTapGestureProperty = new SimpleObjectProperty<>();
    private ObjectProperty<ScreenTapGesture> screenTapGestureProperty = new SimpleObjectProperty<>();
    private ObjectProperty<CircleGesture> circleGestureProperty = new SimpleObjectProperty<>();
    private ObjectProperty<SwipeGesture> swipeGestureProperty = new SimpleObjectProperty<>();
    private ObjectProperty<Boolean> resetAllProperty = new SimpleObjectProperty<>();

    ObservableValue<Integer> indexFingerElementProperty() {
        return indexFingerElement;
    }

    ObservableValue<Vector> zoomProperty() {
        return zoomVector;
    }

    ObservableValue<Boolean> isInEditmode() {
        return editMode;
    }

    ObservableValue<KeyTapGesture> keyTapGestureValue() {
        return keyTapGestureProperty;
    }

    ObservableValue<ScreenTapGesture> screenTapGestureValue() {
        return screenTapGestureProperty;
    }

    ObservableValue<CircleGesture> circleGestureValue() {
        return circleGestureProperty;
    }

    ObservableValue<SwipeGesture> swipeGestureValue() {
        return swipeGestureProperty;
    }

    ObservableValue<Boolean> resetAllValue() {
        return resetAllProperty;
    }

    private long startTime;

    @Override
    public void onConnect(Controller controller) {
        super.onConnect(controller);
        editMode.setValue(false);
        resetAllProperty.setValue(false);
    }

    @Override
    public void onFrame(Controller controller) {
        super.onFrame(controller);
        Frame frame = controller.frame();

        Screen screen = controller.locatedScreens().get(0);
        if (screen != null && screen.isValid()) {
            if (!frame.hands().isEmpty()) {
                Hand hand = frame.hands().rightmost();
                resetAllProperty.setValue(false);
                startTime = System.currentTimeMillis();
                if (hand.isValid()) {

                    Finger indexFinger = hand.fingers().fingerType(Finger.Type.TYPE_INDEX).rightmost();

                    indexFingerElement.setValue(getElementFromIndexFingerAngel(indexFinger.direction()));

                    for (Gesture gesture : frame.gestures()) {

                        switch (gesture.type()) {
                            case TYPE_KEY_TAP:
                                System.out.println("key tap");
                                keyTapGestureProperty.setValue(new KeyTapGesture(gesture));
                                break;
                            case TYPE_SCREEN_TAP:
                                System.out.println("screen tap");
                                toggleEditMode();
                                screenTapGestureProperty.setValue(new ScreenTapGesture(gesture));
                                break;
                            case TYPE_CIRCLE:
                                System.out.println("circle");
                                circleGestureProperty.setValue(new CircleGesture(gesture));
                                break;
                            case TYPE_SWIPE:
                                SwipeGesture swipe = new SwipeGesture(gesture);
                                Vector swipeDirection = swipe.direction();
                                System.out.println(swipeDirection.getX());
                                if (swipeDirection.getX() < 0) {
                                    System.out.println("left");
                                } else if (swipeDirection.getX() >= 0) {
                                    System.out.println("right");
                                }
                                swipeGestureProperty.setValue(new SwipeGesture(gesture));
                                break;
                        }
                    }
                }
            } else {
                if (!editMode.getValue()) {
                    for (int i = 0; i < LeapFXConstant.COUNT_ELEMENTS; i++) {
                        HUDJavaFX.resetElement(i);
                    }
                } else if (editMode.getValue()) {
                    long handsFreeTime = System.currentTimeMillis() - startTime;
                    if (handsFreeTime > LeapFXConstant.TIME_OUT_IN_MS) {
                        editMode.setValue(false);
                        resetAllProperty.setValue(true);
                    }
                }
            }
        }
    }

    private void toggleEditMode() {
        if (editMode.getValue())
            editMode.setValue(false);
        else
            editMode.setValue(true);
    }

    private int getElementFromIndexFingerAngel(Vector indexFingerVector) {
        double angel = LeapCalcFunctions.calcAngelBetweenVectorsInDegrees(indexFingerVector, Vector.xAxis());
        return LeapCalcFunctions.getRectangleFromAngel(angel);
    }
}
