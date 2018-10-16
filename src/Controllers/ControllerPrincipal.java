
package Controllers;

import com.panamahitek.ArduinoException;
import com.panamahitek.PanamaHitek_Arduino;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;


public class ControllerPrincipal implements Initializable{
    @FXML
    private ComboBox<String> cbCaja;

    @FXML
    private DatePicker dtInicial;

    @FXML
    private Button btnConectar;

    @FXML
    private Button btnMostrar;

    @FXML
    private ComboBox<String> cbxTiempo;

    @FXML
    private TableView<String> tvTabla;

    @FXML
    private Label lblEstatusError;

    @FXML
    private Button btnDesconectar;

    @FXML
    private Label lblEstatus;

    @FXML
    private DatePicker dtFinal;

    @FXML
    private TextArea txtVista;
    
    //declaración de variables a utlilizar
    PanamaHitek_Arduino ino = new PanamaHitek_Arduino();
    
    ObservableList<String> ls =null;
    ObservableList<String> time= FXCollections.observableArrayList("1 hora","2 horas", "5 horas","1 día");
    
    SerialPortEventListener escucha = new SerialPortEventListener() {

        @Override
        public void serialEvent(SerialPortEvent spe) {
            try {
                if(ino.isMessageAvailable())
                {
                    //lo que se hace cuando recibe un mensaje por puerto serial
                    MostrarMensaje(ino.printMessage()); //mostramos lo que envia arduino en un cuadro de texto
                }
                } catch (SerialPortException ex) {
                lblEstatus.setText("Desconectado");
                lblEstatusError.setText("Fallo al recibir los datos");                  
                } catch (ArduinoException ex) {
                lblEstatus.setText("Desconectado");
                lblEstatusError.setText("Fallo del la conexion con arduino");   
                }
        }
    };
    
    private void MostrarMensaje(String msg)
    {
        txtVista.appendText(msg+"\n");
    }
    
    private void MostrarPuertos()
    {
        ls=FXCollections.observableArrayList();
        String[] portName = SerialPortList.getPortNames();
        for(String name:portName)
        {
            ls.add(name);
        }
        cbCaja.itemsProperty().setValue(ls);
    }
    
    @FXML
    private void ConectarArduino(ActionEvent event) {
        try 
        {
            ino.arduinoRX(cbCaja.getValue(), 9600, escucha);
            btnConectar.setDisable(true);
            btnDesconectar.setDisable(false);
        } catch (ArduinoException ex) {
            btnConectar.setDisable(true);
            btnDesconectar.setDisable(false);
            lblEstatus.setText("Desconectado");
            lblEstatusError.setText("Fallo al conectar con arduino");
        } catch (SerialPortException ex) {
            
            btnConectar.setDisable(true);
            btnDesconectar.setDisable(false);
            lblEstatus.setText("Desconectado");
            lblEstatusError.setText("Fallo del puerto serial");
        }
    }

    @FXML
    private void DesconectarArduino(ActionEvent event) {
        try 
        {
            ino.killArduinoConnection();
            btnConectar.setDisable(false);
            btnDesconectar.setDisable(true);
        } catch (ArduinoException ex) 
        {
            btnConectar.setDisable(true);
            btnDesconectar.setDisable(false);
            lblEstatus.setText("Conectado");
            lblEstatusError.setText("Fallo al desconectar la conecxión");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MostrarPuertos();
        btnDesconectar.setDisable(true);
        cbxTiempo.itemsProperty().setValue(time);
    }
}
