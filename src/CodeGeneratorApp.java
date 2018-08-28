import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import view.core.AppPane;

/**
 * @author boyce
 */
public class CodeGeneratorApp extends Application {
    private AppPane app;
    private static final String NAME = "CODE_GENERATOR";

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("window/app.fxml"));
        this.primaryStage = primaryStage;

        //Creating a Scene by passing the group object, height and width
        Scene scene = new Scene(root, 800, 600);

        //setting color to the scene
        scene.setFill(Color.BROWN);

        //Setting the title to Stage.
        primaryStage.setTitle(NAME);

        //Adding the scene to Stage
        primaryStage.setScene(scene);

        //Displaying the contents of the stage
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    @Override
    public void init() throws Exception {
        this.app = new AppPane();
        super.init();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void disable() {
        this.primaryStage.hide();
    }
}
