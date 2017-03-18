package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml")); //??????
        primaryStage.setTitle("Tomography Simulation");

        FXMLLoader loader = new FXMLLoader();  // ???????
        loader.setLocation(Main.class.getResource("view/sample.fxml")); // ???????

        Controller controller = loader.getController();
        Image imageIn = new Image("file:/sample/asdf.jpg");
        controller.imageView.setImage(imageIn);

        //ImageView imageView = new ImageView(imageIn);
        //root.getChildren().addAll(imageView);


        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
