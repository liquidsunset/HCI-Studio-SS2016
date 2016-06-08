import com.leapmotion.leap.*;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

import static java.util.Objects.requireNonNull;

/**
 * Created by liquidsunset on 18.05.16.
 */

class LeapListener extends Listener {

    private ObjectProperty<Integer> indexFingerElement = new SimpleObjectProperty<>();
    private ObjectProperty<Boolean> editMode = new SimpleObjectProperty<>();
    private ObjectProperty<ScreenTapGesture> screenTapGestureProperty = new SimpleObjectProperty<>();
    private ObjectProperty<Boolean> resetAllProperty = new SimpleObjectProperty<>();
    private ObjectProperty<Integer> elementIteratorProperty = new SimpleObjectProperty<>();

    ObservableValue<Integer> indexFingerElementProperty() {
        return indexFingerElement;
    }

    ObservableValue<Boolean> isInEditMode() {
        return editMode;
    }

    ObservableValue<ScreenTapGesture> screenTapGestureValue() {
        return screenTapGestureProperty;
    }

    ObservableValue<Boolean> resetAllValue() {
        return resetAllProperty;
    }

    ObservableValue<Integer> elementIteratorValue() {
        return elementIteratorProperty;
    }


    private long startTime;
    private int sequenceCount;

    private static final Integer[] elementsTouched = new Integer[LeapFXConstant.SEQUENCE_LENGTH];
    private static final double[] angelsTouched = new double[LeapFXConstant.SEQUENCE_LENGTH];

    @Override
    public void onConnect(Controller controller) {
        super.onConnect(controller);
        editMode.setValue(false);
        resetAllProperty.setValue(false);
        sequenceCount = 0;
        elementIteratorProperty.setValue(requireNonNull(LeapFXConstant.SEQUENCE)[sequenceCount]);
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
                        if (Gesture.Type.TYPE_SCREEN_TAP.equals(gesture.type())) {
                            toggleEditMode();
                            screenTapGestureProperty.setValue(new ScreenTapGesture(gesture));
                            elementsTouched[sequenceCount] = indexFingerElement.getValue();
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

    private void saveData(){
        if(FileHandlingFunctions.saveUserLog(LeapCalcFunctions.getDefinedAngels(),
                LeapFXConstant.SEQUENCE, elementsTouched, angelsTouched, 0)){
            System.out.println("Data saved");
        } else {
            System.out.println("Error at saving the data");
        }
    }
}
