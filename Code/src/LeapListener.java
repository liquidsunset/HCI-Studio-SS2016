import com.leapmotion.leap.*;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

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

    ObservableValue<Boolean> resetAllValues() {
        return resetAllProperty;
    }

    ObservableValue<Integer> elementIteratorValue() {
        return elementIteratorProperty;
    }


    private long startTime;
    private long delayTime;
    private int sequenceCount;
    private boolean start = false;
    private boolean shouldReset = false;

    private static final Integer[] elementsTouched = new Integer[LeapFXConstant.SEQUENCE_LENGTH];
    private static final double[] angelsTouched = new double[LeapFXConstant.SEQUENCE_LENGTH];
    private static final Integer[] sequence = HUDJavaFX.getSequence();

    @Override
    public void onConnect(Controller controller) {
        super.onConnect(controller);
        editMode.setValue(false);
        resetAllProperty.setValue(false);
        sequenceCount = 0;
    }

    @Override
    public void onFrame(Controller controller) {

        super.onFrame(controller);
        Frame frame = controller.frame();


        if (editMode.getValue()) {
            long timeConsumed = System.currentTimeMillis() - delayTime;
            if (timeConsumed >= LeapFXConstant.TIME_OUT_IN_MS) {
                toggleEditMode();
                resetAllElements();
                int saveLastValue = indexFingerElement.getValue();
                indexFingerElement.setValue(null);
                indexFingerElement.setValue(saveLastValue);
                if (sequenceCount == LeapFXConstant.SEQUENCE_LENGTH - 1) {
                    saveData(System.currentTimeMillis() - startTime);
                    System.exit(0);
                } else {
                    sequenceCount++;
                    elementIteratorProperty.setValue(sequence[sequenceCount]);
                }
            }
        }

        Screen screen = controller.locatedScreens().get(0);
        if (screen != null && screen.isValid()) {
            if (!frame.hands().isEmpty()) {
                shouldReset = true;
                Hand hand = frame.hands().rightmost();

                resetAllProperty.setValue(false);

                if (hand.isValid()) {

                    if (!start) {
                        start = true;
                        startTime = System.currentTimeMillis();
                        elementIteratorProperty.setValue(sequence[sequenceCount]);
                    }

                    Finger indexFinger = hand.fingers().fingerType(Finger.Type.TYPE_INDEX).rightmost();
                    indexFingerElement.setValue(getElementFromIndexFingerAngel(indexFinger.direction()));

                    for (Gesture gesture : frame.gestures()) {
                        if (Gesture.Type.TYPE_SCREEN_TAP.equals(gesture.type())) {
                            toggleEditMode();
                            delayTime = System.currentTimeMillis();
                            screenTapGestureProperty.setValue(new ScreenTapGesture(gesture));
                            if (!LeapFXConstant.FREE_MODE) {
                                elementsTouched[sequenceCount] = indexFingerElement.getValue() + 1;
                                angelsTouched[sequenceCount] = LeapCalcFunctions
                                        .calcAngelBetweenVectorsInDegrees(indexFinger.direction(),
                                                Vector.xAxis());
                            }
                        }
                    }
                }
            } else {
                if (!editMode.getValue()) {
                    if (shouldReset) {
                        resetAllElements();
                        shouldReset = false;
                    }
                }
            }
        }
    }

    private void resetAllElements() {
        for (int i = 0; i < LeapFXConstant.COUNT_ELEMENTS; i++) {
            HUDJavaFX.resetElement(i);
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

    private void saveData(long time) {
        if (FileHandlingFunctions.saveUserLog(LeapCalcFunctions.getDefinedAngels(),
                sequence, elementsTouched, angelsTouched, time)) {
            System.out.println("Data saved");
        } else {
            System.out.println("Error at saving the data");
        }
    }
}
