import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Gesture;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * Created by liquidsunset on 18.05.16.
 */
public class HUDJavaFX extends Application {

    private LeapListener listener = new LeapListener();
    private Controller leapController = new Controller();

    private boolean editMode;
    private Integer selectedElement;
    private Integer actualElement;
    private static final Rectangle[] elements = new Rectangle[LeapFXConstant.COUNT_ELEMENTS];
    private Scene scene;
    private Group rectangles;

    public static void main(String[] args) {
        launch();
    }

    //Initialization of the elements and layout
    @Override
    public void init() throws Exception {
        super.init();
        /*
        //swipe config
        leapController.enableGesture(Gesture.Type.TYPE_SWIPE);
        leapController.config().setFloat("Gesture.Swipe.MinLength", 60f);
        leapController.config().setFloat("Gesture.Swipe.MinVelocity", 400f);
        */

        /*
        // circle config
        leapController.config().setFloat("Gesture.Circle.MinRadius", 9.0f);
        leapController.config().setFloat("Gesture.Circle.MinArc", 5.0f);
        leapController.enableGesture(Gesture.Type.TYPE_CIRCLE);
        */

        //keyTap config
        leapController.config().setFloat("Gesture.KeyTap.MinDownVelocity", 60.0f);
        leapController.config().setFloat("Gesture.KeyTap.HistorySeconds", 0.15f);
        leapController.config().setFloat("Gesture.KeyTap.MinDistance", 10.0f);
        leapController.enableGesture(Gesture.Type.TYPE_KEY_TAP);

        //screenTap config
        leapController.config().setFloat("Gesture.ScreenTap.MinForwardVelocity", 40.0f);
        leapController.config().setFloat("Gesture.ScreenTap.HistorySeconds", 0.15f);
        leapController.config().setFloat("Gesture.ScreenTap.MinDistance", 5.0f);
        leapController.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);

        //save leap config
        leapController.config().save();

        //other things to init
        editMode = false;

        //initScene
        rectangles = new Group();
        scene = new Scene(rectangles, LeapFXConstant.ELEMENT_WIDTH * LeapFXConstant.COUNT_ELEMENTS,
                LeapFXConstant.ELEMENT_HEIGHT);
        initElements();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        leapController.addListener(listener);

        primaryStage.setScene(scene);

        listener.indexFingerElementProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (editMode)
                    return;
                if (!newValue.equals(oldValue)) {
                    actualElement = newValue;
                    highlightElement(newValue);
                    resetElement(oldValue);
                }
            });
        });

        listener.isInEditmode().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                this.editMode = newValue;
                System.out.println(newValue);
            });
        });

        listener.zoomProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {

            });
        });

        listener.circleGestureValue().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {

            });
        });

        listener.keyTapGestureValue().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                /*if (editMode) {
                    if (selectedElement != null && selectedElement == -1)
                        listener.toggleEditMode();
                }*/
            });
        });

        listener.screenTapGestureValue().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (editMode) {
                    selectedElement = actualElement;
                    selectElement(actualElement);
                } else {
                    resetElement(selectedElement);
                }

            });
        });

        listener.swipeGestureValue().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {

            });
        });

        primaryStage.show();

    }

    private void switchElements(Integer posOld, Integer posNew) {
        if (posOld < LeapFXConstant.COUNT_ELEMENTS && posNew < LeapFXConstant.COUNT_ELEMENTS) {
            Rectangle oldRec = elements[posOld];
            double marginXOld = oldRec.getX();

            Rectangle newRec = elements[posNew];
            double marginXNew = newRec.getX();

            oldRec.setX(marginXNew);
            newRec.setX(marginXOld);


            resetElement(posOld);
            selectElement(posNew);

            elements[posOld] = elements[posNew];
            elements[posNew] = oldRec;
        }
    }

    private void initElements() {
        for (int i = 0; i < LeapFXConstant.COUNT_ELEMENTS; i++) {
            Rectangle element = new Rectangle(LeapFXConstant.ELEMENT_WIDTH * i, 0.0,
                    LeapFXConstant.ELEMENT_WIDTH, LeapFXConstant.ELEMENT_HEIGHT);
            element.setFill(Color.WHITE);
            element.setStroke(Color.BLACK);
            elements[i] = element;

        }
        rectangles.getChildren().addAll(elements);
    }

    private void highlightElement(Integer elem) {
        if (elem != null && elem < LeapFXConstant.COUNT_ELEMENTS && !elem.equals(selectedElement)) {
            Rectangle rec = elements[elem];
            rec.setFill(Color.LIGHTYELLOW);
        }
    }

    static void resetElement(Integer elem) {
        if (elem != null && elem < LeapFXConstant.COUNT_ELEMENTS) {
            Rectangle rec = elements[elem];
            rec.setFill(Color.WHITE);
        }
    }

    private void selectElement(Integer elem) {
        if (editMode) {
            Rectangle rec = elements[elem];
            rec.setFill(Color.LIGHTGREEN);
        }
    }

    @Override
    public void stop() {
        leapController.removeListener(listener);
    }
}
