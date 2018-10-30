
package Clases;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


/**
 *
 * @author J.Armando
 */
public class Principal extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
       Parent ventana=FXMLLoader.load(getClass().getResource("/FXML/Principal1.fxml"));
       Scene window = new Scene(ventana);
       primaryStage.setTitle("Proyecto integrador 2.0");
       primaryStage.getIcons().add(new Image("imagenes/plant.png"));
       primaryStage.setScene(window);
       primaryStage.show();
    }
    
    public static void main(String[] args)
    {
        launch(args);
    }
    
}
