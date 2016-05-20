import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.SwipeGesture;
import com.leapmotion.leap.Vector;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * Created by liquidsunset on 18.05.16.
 */
public class HUDJavaFX extends Application {

    private LeapListener listener = new LeapListener();
    private Controller leapController = new Controller();

    private AnchorPane root = new AnchorPane();
    private Circle circle=new Circle(50, Color.DEEPSKYBLUE);


    //Initialization of the elements and layout
    @Override
    public void init() throws Exception {
        super.init();
        /*
        leapController.enableGesture(Gesture.Type.TYPE_SWIPE);
        leapController.config().setFloat("Gesture.Swipe.MinLength", 60f);
        leapController.config().setFloat("Gesture.Swipe.MinVelocity", 400f);
        leapController.config().save();

        leapController.enableGesture(Gesture.Type.TYPE_KEY_TAP);
        leapController.enableGesture(Gesture.Type.TYPE_CIRCLE);
          */
        leapController.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        leapController.addListener(listener);

        circle.setLayoutX(circle.getRadius());
        circle.setLayoutY(circle.getRadius());
        root.getChildren().add(circle);
        final Scene scene = new Scene(root, 800, 600);

        listener.pointProperty().addListener((ov, t, t1) -> {
            Platform.runLater(() -> {
                Point2D d=root.sceneToLocal(t1.getX()-scene.getX()-scene.getWindow().getX(),
                        t1.getY()-scene.getY()-scene.getWindow().getY());
                double dx=d.getX(), dy=d.getY();
                if(dx>=0d && dx<=root.getWidth()-2d*circle.getRadius() &&
                        dy>=0d && dy<=root.getHeight()-2d*circle.getRadius()){
                    circle.setTranslateX(dx);
                    circle.setTranslateY(dy);
                }
            });
        });

        listener.indexFingerAngleProperty().addListener((ov, t, t1) -> {
            Platform.runLater(() -> {

            });
        });

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    @Override
    public void stop(){
        leapController.removeListener(listener);
    }

    public static void main(String[] args) {
        launch();
    }
}
