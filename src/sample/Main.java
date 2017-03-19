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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Tomography Simulation");

        Controller controller = fxmlLoader.getController();

        Image imageIn = new Image("https://static1.squarespace.com/static/55ccf522e4b0fc9c2b651a5d/55ce42e0e4b065516c646a6d/55ce42fbe4b0ef8aac6a0749/1439580951781/Slayer_Repentless_900.jpg");
        ImageView imageView = controller.getImageView();
        imageView.setImage(imageIn);

        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
    }

    public void createSinogram()
    {

    }


    public static void main(String[] args) {
        launch(args);
    }
}
