 
package Controllers;

import Clases.Conexion_db;
import Clases.MensajeAlerta;
import Clases.Tabla;
import com.panamahitek.ArduinoException;
import com.panamahitek.PanamaHitek_Arduino;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
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
    private Label lblEstatusError;
    @FXML
    private Button btnDesconectar;
    @FXML
    private Label lblEstatus;
    @FXML
    private DatePicker dtFinal;
    @FXML
    private TextArea txtVista;
    @FXML
    private Button btnGraficar;
    @FXML
    private ComboBox<String> cbxHumedad;
     @FXML
    private LineChart<String,Integer> lcGrafica;
    
    //para el llenado de las tablas en la base de datos
    @FXML
    private TableView tvTabla;
    @FXML
    private TableColumn<Tabla, Integer> col_id;
    @FXML
    private TableColumn<Tabla, Integer> col_planta;
    @FXML
    private TableColumn<Tabla, String> col_fecha;
    @FXML
    private TableColumn<Tabla, String> col_hora;
    @FXML
    private TableColumn<Tabla, Integer> col_humedad;
    @FXML
    private TableColumn<Tabla, Float> col_caudal;
    @FXML
    private TableColumn<Tabla, Float> col_consumoAgua;
    @FXML
    private ComboBox<String> cbxSimbolo;
    @FXML
    private ComboBox<String> cbxSensor;
    MensajeAlerta al = new MensajeAlerta("Error de conexión","Ha ocurrido un error con la conexión del servidor...Intente otra vez");
    //declaración de variables a utlilizar
    PanamaHitek_Arduino ino = new PanamaHitek_Arduino();
    
    ObservableList<String> ls =null;
    ObservableList<String> time= FXCollections.observableArrayList("1 hora","2 horas", "5 horas","1 día");
    ObservableList<String> simbolo = FXCollections.observableArrayList("=",">","<",">=","<=");
    ObservableList<String> sensores = FXCollections.observableArrayList("Humedad","Consumo de agua");
    ObservableList<String> cantidad_humedad = FXCollections.observableArrayList("0","10","20","30","40","50","60","70","80","90","100");
    ObservableList<Tabla> data = FXCollections.observableArrayList();
    //CONEXION DE LA BASE DE DATOS
    Connection conn =null;
    
    //escucha las acciones de arduuino
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
    
    private void MostrarMensaje(String msg) //muestra los datos recibidos en un panel
    {
        //divido el mesajejen en dos parte para poder hacer el insert en la tabla
        String[] cadena = msg.split(",");
        String planta =cadena[0];
        String humedad = cadena[1];
        String caudal = cadena[2];
        String consumo = cadena[3];
        try {
            String query ="INSERT INTO sensores VALUES (id_sensores.nextval,"+planta
            +",TO_DATE('"+mostrarFechaHora()+"','dd/mm/yyyy hh24:mi:ss'),'"+mostrarHora()+"',"
            +humedad+","+caudal+","+consumo+")";
            PreparedStatement s = conn.prepareStatement(query);
            s.execute();//se hace un commit automaticamente al ejecutar la sentencia
            
        } catch (SQLException ex) {
            MensajeAlerta alert = new MensajeAlerta("Error","Ha ocurrido una falla al insertar el dato...Verifique la conexión.");
            alert.MostrarMensaje();
        }
        txtVista.appendText("planta : "+planta+" ,humedad(%) : "+humedad+" ,caudal de agua(ml) : "+caudal+" ,sonsumo de agua(ml) : "+consumo+"\n");
    }
    
    private void MostrarPuertos() //muestra los puertos conectados
    {
        ls=FXCollections.observableArrayList();
        String[] portName = SerialPortList.getPortNames();
        for(String name:portName)
        {
            ls.add(name);
        }
        cbCaja.itemsProperty().setValue(ls);
    }
    
    @FXML //efectua la conexion con arduino
    private void ConectarArduino(ActionEvent event) {
        try 
        {
            ino.arduinoRX(cbCaja.getValue(), 9600, escucha);
            btnConectar.setDisable(true);
            btnDesconectar.setDisable(false);
            lblEstatus.setText("Conectado");
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

    @FXML //desabilita la conexion con arduino
    private void DesconectarArduino(ActionEvent event) {
        try 
        {
            ino.killArduinoConnection();
            btnConectar.setDisable(false);
            btnDesconectar.setDisable(true);
            lblEstatus.setText("Desconectado");
        } catch (ArduinoException ex) 
        {
            btnConectar.setDisable(true);
            btnDesconectar.setDisable(false);
            lblEstatus.setText("Conectado");
            lblEstatusError.setText("Fallo al desconectar la conecxión");
        }
    }
    
    private String mostrarFechaHora()
    {
        Date fecha = new Date();
        DateFormat fechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String date =fechaHora.format(fecha);
       return(date);
    }
    
    private String mostrarHora()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        Date now = new Date();
        String hora=sdf.format(now);
        return(hora);
    }
    
    private void solicitudQuery()
    {
       try {
                //fechaInicio es menor a la fecha2     
                String simbo =cbxSimbolo.getValue();
                String valor =cbxHumedad.getValue();
                DateTimeFormatter dt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                 Statement sentencia = conn.createStatement();
                 String query="SELECT*FROM sensores WHERE humedad "+simbo+valor+" AND fecha BETWEEN "
                 + "TO_DATE('"+dtInicial.getValue().format(dt)+" 00:00:00','dd/mm/yyyy hh24:mi:ss')"
                 +" AND TO_DATE('"+dtFinal.getValue().format(dt)+" 23:00:00','dd/mm/yyyy hh24:mi:ss')";
                 ResultSet resultado = sentencia.executeQuery(query);
                 tvTabla.getItems().clear();
                while(resultado.next())
                {
                   Tabla tbl = new Tabla();
                    tbl.setId(resultado.getInt("id_sensores"));
                    tbl.setId_plantas(resultado.getInt("id_plantas"));
                    tbl.setFecha(resultado.getString("fecha"));
                    tbl.setHora(resultado.getString("hora"));
                    tbl.setHumedad(resultado.getInt("humedad"));
                    tbl.setCaudal(resultado.getFloat("caudal_agua"));
                    tbl.setConsumoAgua(resultado.getFloat("consumo_agua"));
                    data.add(tbl);                                   
                }
                tvTabla.setItems(data);
               //se hace referencia en que parte se obtiene la informacion para mostrarla en el tableview
                col_id.setCellValueFactory(cellData -> cellData.getValue().getTablaid().asObject());
                col_planta.setCellValueFactory(cellData -> cellData.getValue().getTablaid_plantas().asObject());
                col_fecha.setCellValueFactory(cellData -> cellData.getValue().getTablaFecha());
                col_humedad.setCellValueFactory(cellData -> cellData.getValue().getTablaHumedad().asObject());
                col_hora.setCellValueFactory(cellData -> cellData.getValue().getTablaHora());
                col_caudal.setCellValueFactory(cellData -> cellData.getValue().getTablaCaudal().asObject());
                col_consumoAgua.setCellValueFactory(cellData -> cellData.getValue().getTablaConsumoAgua().asObject());               
            } catch (SQLException ex) 
            {
               al.MostrarMensaje(); //muestra un mensaje de error
            }  
    }
    
    @FXML
    private void Resultados()
    {
        LocalDate fechaInicio= dtInicial.getValue();
        LocalDate fechaFinal=dtFinal.getValue();
        if((fechaInicio==null || fechaFinal==null) || (cbxSimbolo.getValue()==null ||cbxHumedad.getValue()==null))
        {
            MensajeAlerta ms= new MensajeAlerta("Error!!","No has has seleccionado todas las opciones...");
            ms.MostrarMensaje();
        }
        else if( fechaInicio.isBefore(fechaFinal))
        {
            solicitudQuery();
        }
        else if(fechaInicio.isAfter(fechaFinal))
        {
            //fechaInicio es > a la primera fecha
            MensajeAlerta mg = new MensajeAlerta("Error de fecha","La combinación de fechas debe ser cronológica o iguales...");
            mg.MostrarMensaje();
        }
        else
        {
            //este else sirve como filtro si las dos fechas selecionadas son las mismas
            solicitudQuery();
        }      
    }

    @Override 
    public void initialize(URL location, ResourceBundle resources) {
        MostrarPuertos();
        btnDesconectar.setDisable(true);
        try {
            conn=Conexion_db.getConnection();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ControllerPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ControllerPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
        cbxTiempo.itemsProperty().setValue(time);
        cbxHumedad.itemsProperty().setValue(cantidad_humedad);
        cbxSensor.itemsProperty().setValue(sensores);
        cbxSimbolo.itemsProperty().setValue(simbolo);
    }
}
