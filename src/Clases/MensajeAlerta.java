/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.StageStyle;

/**
 *
 * @author J.Armando
 */
public class MensajeAlerta {
    
    Alert mensaje=null;
    public MensajeAlerta(String titulo,String contenido)
    {
        mensaje= new Alert(AlertType.ERROR);
        mensaje.setTitle(titulo);
        mensaje.setHeaderText(null);
        mensaje.setContentText(contenido);
        mensaje.initStyle(StageStyle.UTILITY);
    }
    
    public void MostrarMensaje()
    {
        mensaje.showAndWait();
    }
}
