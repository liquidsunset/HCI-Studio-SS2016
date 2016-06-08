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
    private static final Text sequenceText = new Text("0");
    private static final StackPane[] elements = new StackPane[LeapFXConstant.COUNT_ELEMENTS];
    private Scene scene;
    private Group elementGroup;

    public static void main(String[] args) {

        boolean createSequence;
        if (args.length > 0) {
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

        LeapFXConstant.SEQUENCE = FileHandlingFunctions.getSequence();
        launch();
    }

    //Initialization of the elements and layout
    @Override
    public void init() throws Exception {
        super.init();

        //screenTap config
        leapController.config().setFloat("Gesture.ScreenTap.MinForwardVelocity", 50.0f);
        leapController.config().setFloat("Gesture.ScreenTap.HistorySeconds", 0.15f);
        leapController.config().setFloat("Gesture.ScreenTap.MinDistance", 6.0f);
        leapController.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);

        //save leap config
        leapController.config().save();

        //other things to init
        editMode = false;

        //initScene
        elementGroup = new Group();
        scene = new Scene(elementGroup, LeapFXConstant.HUD_WIDTH, LeapFXConstant.HUD_HEIGHT);
        initPrimaryElements();

        FileHandlingFunctions.createSequence(10, true);
        FileHandlingFunctions.saveUserLog(null, null, null, null, 0);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        leapController.addListener(listener);
        primaryStage.setScene(scene);

        final Stage secondaryStage = new Stage(StageStyle.UTILITY);
        initSecondaryStage(secondaryStage);

        listener.indexFingerElementProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (editMode) {
                    System.out.println("in edit mode");
                    return;
                }

                if (!newValue.equals(oldValue)) {
                    actualElement = newValue;
                    highlightElement(newValue);
                    resetElement(oldValue);
                }
            });
        });

        listener.isInEditMode().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                this.editMode = newValue;
                System.out.println(newValue);
            });
        });

        listener.screenTapGestureValue().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (editMode) {
                    highlightedElement = actualElement;
                    selectElement(actualElement);
                } else {
                    resetElement(highlightedElement);
                    highlightedElement = null;
                }
            });
        });

        listener.resetAllValue().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                System.out.println("reset all");
                if (newValue) {
                    highlightedElement = null;
                    for (int i = 0; i < LeapFXConstant.COUNT_ELEMENTS; i++) {
                        resetElement(i);
                    }
                }
            });
        });

        listener.elementIteratorValue().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                setSequenceText(newValue);
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

        stackPane.getChildren().addAll(rectangle, sequenceText);
        secondaryStage.setScene(new Scene(stackPane, 200, 200));
    }

    private void setSequenceText(Integer sequenceNumber) {
        if (LeapFXConstant.SHOW_SUBVIEW) {
            sequenceText.setText(sequenceNumber.toString());
        } else {
            Text text = rectangleText[sequenceNumber - 1];
            text.setText(sequenceNumber.toString());
            text.setFill(Color.FUCHSIA);
        }
    }

    private void highlightElement(Integer elem) {
        if (elem != null && elem < LeapFXConstant.COUNT_ELEMENTS &&
                !elem.equals(highlightedElement)) {
            Rectangle rec = rectangles[elem];
            rec.setFill(Color.LIGHTYELLOW);
        }
    }

    static void resetElement(Integer elem) {
        if (elem != null && elem < LeapFXConstant.COUNT_ELEMENTS) {
            Rectangle rec = rectangles[elem];
            Text text = rectangleText[elem];
            rec.setFill(Color.WHITE);
            text.setFill(Color.BLACK);
        }
    }

    private void selectElement(Integer elem) {
        if (editMode) {
            Rectangle rec = rectangles[elem];
            rec.setFill(Color.LIGHTGREEN);
        }
    }

    @Override
    public void stop() {
        leapController.removeListener(listener);
    }
}
