import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Gesture;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by liquidsunset on 18.05.16.
 */
public class HUDJavaFX extends Application {

    private LeapListener listener = new LeapListener();
    private Controller leapController = new Controller();

    private boolean editMode;
    private Integer highlightedElement;
    private Integer actualElement;
    private static final Rectangle[] rectangles = new Rectangle[LeapFXConstant.COUNT_ELEMENTS];
    private static final Text[] rectangleText = new Text[LeapFXConstant.COUNT_ELEMENTS];
    private static final Text sequenceText = new Text("");
    private static final StackPane[] elements = new StackPane[LeapFXConstant.COUNT_ELEMENTS];
    private Scene scene;
    private Group elementGroup;
    private static Integer actualSequenceElement = null;

    private static Integer[] sequence;

    public static void main(String[] args) {

        boolean createSequence;
        if (!LeapFXConstant.USE_DEFINED_SEQUENCE && args.length > 0) {
            try {
                createSequence = Boolean.parseBoolean(args[0]);
            } catch (Exception e) {
                createSequence = false;
            }
        } else {
            createSequence = false;
        }

        if (createSequence) {
            FileHandlingFunctions.createSequence(LeapFXConstant.SEQUENCE_LENGTH,
                    LeapFXConstant.OVERWRITE_SEQUENCE);
        }

        sequence = FileHandlingFunctions.getSequence();

        launch();
    }

    //Initialization of the elements and layout
    @Override
    public void init() throws Exception {
        super.init();

        //screenTap config
        leapController.config().setFloat("Gesture.ScreenTap.MinForwardVelocity", 35.0f);
        leapController.config().setFloat("Gesture.ScreenTap.HistorySeconds", 0.20f);
        leapController.config().setFloat("Gesture.ScreenTap.MinDistance", 4.0f);
        leapController.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);

        //save leap config
        leapController.config().save();

        //other things to init
        editMode = false;

        //initScene
        elementGroup = new Group();
        scene = new Scene(elementGroup, LeapFXConstant.HUD_WIDTH, LeapFXConstant.HUD_HEIGHT);
        initPrimaryElements();

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        leapController.addListener(listener);
        primaryStage.setScene(scene);

        final Stage secondaryStage = new Stage(StageStyle.UTILITY);
        initSecondaryStage(secondaryStage);

        listener.indexFingerElementProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (newValue == null) {
                    return;
                }

                if (editMode) {
                    return;
                } else {
                    highlightedElement = null;
                }

                actualElement = newValue;
                resetElement(oldValue);
                highlightElement(newValue);

            });
        });

        listener.isInEditMode().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> this.editMode = newValue);
        });

        listener.screenTapGestureValue().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (editMode) {
                    highlightedElement = actualElement;
                    selectElement(actualElement);
                }
            });
        });

        listener.elementIteratorValue().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (newValue != null) {
                    setSequenceText(newValue);
                    actualSequenceElement = newValue;
                    resetAllElements();
                }
            });
        });

        primaryStage.show();

        if (LeapFXConstant.SHOW_SUBVIEW) {
            secondaryStage.show();
        }
    }

    private void initPrimaryElements() {
        double elementWidth = Math.floor(LeapFXConstant.HUD_WIDTH / LeapFXConstant.COUNT_ELEMENTS);
        for (int i = 0; i < LeapFXConstant.COUNT_ELEMENTS; i++) {
            Rectangle rectangle = new Rectangle(elementWidth, LeapFXConstant.HUD_HEIGHT);
            Text text = new Text(String.valueOf(i + 1));

            rectangle.setFill(Color.WHITE);
            rectangle.setStroke(Color.BLACK);

            text.setFont(Font.font(LeapFXConstant.TEXT_SIZE));
            text.setBoundsType(TextBoundsType.VISUAL);

            if (LeapFXConstant.MIRROR_MAIN_VIEW) {
                text.setRotationAxis(Rotate.X_AXIS);
                text.setRotate(180.0);
            }

            StackPane stackPane = new StackPane();
            stackPane.setTranslateX(elementWidth * i);
            stackPane.setTranslateY(0.0);
            stackPane.getChildren().addAll(rectangle, text);

            rectangles[i] = rectangle;
            rectangleText[i] = text;
            elements[i] = stackPane;

        }
        elementGroup.getChildren().addAll(elements);
    }

    private void initSecondaryStage(Stage secondaryStage) {
        Rectangle rectangle = new Rectangle(200, 200);
        StackPane stackPane = new StackPane();

        rectangle.setFill(Color.WHITE);
        rectangle.setStroke(Color.BLACK);
        sequenceText.setFont(Font.font(LeapFXConstant.TEXT_SIZE));
        sequenceText.setBoundsType(TextBoundsType.VISUAL);

        if (LeapFXConstant.MIRROR_SUB_VIEW) {
            sequenceText.setRotationAxis(Rotate.X_AXIS);
            sequenceText.setRotate(180.0);
        }

        stackPane.getChildren().addAll(rectangle, sequenceText);
        secondaryStage.setScene(new Scene(stackPane, 200, 200));
    }

    private void setSequenceText(Integer sequenceNumber) {
        if (LeapFXConstant.SHOW_SUBVIEW) {
            sequenceText.setText(sequenceNumber.toString());
        } else {
            Text text = rectangleText[sequenceNumber - 1];
            text.setText(sequenceNumber.toString());
            text.setFill(Color.ORANGERED);
        }
    }

    private void highlightElement(Integer elem) {
        if (elem != null && elem < LeapFXConstant.COUNT_ELEMENTS &&
                !elem.equals(highlightedElement)) {
            Rectangle rec = rectangles[elem];
            rec.setFill(Color.CORNFLOWERBLUE);
        }
    }

    private static void resetElement(Integer elem) {
        if (elem != null && elem < LeapFXConstant.COUNT_ELEMENTS) {
            Rectangle rec = rectangles[elem];
            rec.setFill(Color.WHITE);
            Text text = rectangleText[elem];
            if (!LeapFXConstant.SHOW_SUBVIEW && actualSequenceElement != null
                    && elem.equals(actualSequenceElement - 1)) {
                text.setFill(Color.ORANGERED);
            } else {
                text.setFill(Color.BLACK);
            }
        }
    }

    static void resetAllElements() {
        for (int i = 0; i < LeapFXConstant.COUNT_ELEMENTS; i++) {
            resetElement(i);
        }
    }

    private void selectElement(Integer elem) {
        if (editMode) {
            Rectangle rec = rectangles[elem];
            rec.setFill(Color.LIGHTGREEN);
        }
    }

    static Integer[] getSequence() {
        return sequence;
    }

    @Override
    public void stop() {
        leapController.removeListener(listener);
    }
}
