 
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
import javafx.geometry.Side;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
    private LineChart<String,Number> lcGrafica;
    @FXML
    private CategoryAxis X;
    @FXML
    private NumberAxis Y;
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
    @FXML
    private DatePicker dt_fechaGrafica2;
    @FXML
    private DatePicker dt_fechaGrafica1;
    @FXML
    private ComboBox<String> cbxSimboloGrafica;
    @FXML
    private ComboBox<String> cbxValoresGrafica;
    @FXML
    private Button btnEncenderSensores;
    @FXML
    private Button btnApagarSensores;
    MensajeAlerta al = new MensajeAlerta("Error de conexión","Ha ocurrido un error con la conexión del servidor...Intente otra vez");
    //declaración de variables a utlilizar
    PanamaHitek_Arduino ino = new PanamaHitek_Arduino();
    //listas observables para ponerlos en los combobox
    ObservableList<String> simboloGrafica= FXCollections.observableArrayList(">","<",">=","<=");    
    ObservableList<String> ls =null;
    ObservableList<String> simbolo = FXCollections.observableArrayList("=",">","<",">=","<=");
    ObservableList<String> sensores = FXCollections.observableArrayList("Humedad","Caudal de agua","Consumo de agua");
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
        String planta1 =cadena[0];
        String humedad1 = cadena[1];
        String planta2 = cadena[2];
        String humedad2 = cadena[3];
        String caudal = cadena[4];
        String consumo = cadena[5];
        try { //se ejecuta para almacenar el dato recivido en una base de datos
            String query ="INSERT INTO sensores VALUES (id_sensores.nextval,"+planta1
            +",TO_DATE('"+mostrarFechaHora()+"','dd/mm/yyyy hh24:mi:ss'),'"+mostrarHora()+"',"
            +humedad1+","+caudal+","+consumo+")";
            PreparedStatement s = conn.prepareStatement(query);
            s.execute();//se hace un commit automaticamente al ejecutar la sentencia
            
            query ="INSERT INTO sensores VALUES (id_sensores.nextval,"+planta2
            +",TO_DATE('"+mostrarFechaHora()+"','dd/mm/yyyy hh24:mi:ss'),'"+mostrarHora()+"',"
            +humedad2+","+caudal+","+consumo+")";
            s = conn.prepareStatement(query);
            s.execute();//se hace un commit automaticamente al ejecutar la sentencia
            
        } catch (SQLException ex) {
            MensajeAlerta alert = new MensajeAlerta("Error","Ha ocurrido una falla al insertar el dato...Verifique la conexión.");
            alert.MostrarMensaje();
        }
        txtVista.appendText("planta : "+planta1+" ,humedad(%) : "+humedad1+" ,caudal de agua(ml) : "+caudal+" ,consumo de agua(ml) : "+consumo+"\n");
        txtVista.appendText("planta : "+planta2+" ,humedad(%) : "+humedad2+" ,caudal de agua(ml) : "+caudal+" ,consumo de agua(ml) : "+consumo+"\n");
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
            ino.arduinoRXTX(cbCaja.getValue().toString(), 9600, escucha);
            //btnConectar.setDisable(true);
            //btnDesconectar.setDisable(false);
            //btnEncenderSensores.setDisable(false);
            //btnApagarSensores.setDisable(true);
            //cbCaja.setDisable(true);
            lblEstatus.setText("Conectado");
        } catch (ArduinoException ex) {            
            lblEstatus.setText("Desconectado");
            lblEstatusError.setText("Fallo al conectar con arduino");
        }
    }
    
    @FXML
    private void encenderSensor(ActionEvent e)
    {
        try {
            ino.sendData("a");
        } catch (ArduinoException ex) {
            System.out.println("error de arduino");
        } catch (SerialPortException ex) {
           System.out.println("error de puerto");
        }
    }
    
    @FXML
    private void apagarSensor(ActionEvent e)
    {
        try {
            ino.sendData("b");
            //btnConectar.setDisable(true);
            //btnDesconectar.setDisable(false);
            //btnEncenderSensores.setDisable(false);
            //btnApagarSensores.setDisable(true);
            //cbCaja.setDisable(true);      
        } catch (ArduinoException ex) {
            MensajeAlerta al = new MensajeAlerta("Error!!","Ha ocurrido un error al intentar apagar los sensores.");
            al.MostrarMensaje();
        } catch (SerialPortException ex) {
           MensajeAlerta al = new MensajeAlerta("Error!!","Ha ocurrido un error al intentar apagar los sensores..Revise el puerto serial.");
            al.MostrarMensaje();
        }
    }

    @FXML //desabilita la conexion con arduino
    private void DesconectarArduino(ActionEvent event) {
        try 
        {
            ino.killArduinoConnection();
            //btnConectar.setDisable(false);
            //btnDesconectar.setDisable(true);
            //btnEncenderSensores.setDisable(true);
            //btnApagarSensores.setDisable(true);
            //cbCaja.setDisable(false);
            lblEstatus.setText("Desconectado");
        } catch (ArduinoException ex) 
        {
            lblEstatus.setText("Conectado");
            lblEstatusError.setText("Fallo al desconectar la conecxión");
        }
    }
    @FXML
    private void selectPuertoCom()
    {
        //btnConectar.setDisable(false);
        //btnDesconectar.setDisable(true);
        //btnEncenderSensores.setDisable(true);
        //btnApagarSensores.setDisable(true);
        //cbCaja.setDisable(false);
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
    //un metodo para hacer la consulta en 
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
    
    @FXML // se validan todos los componentes del apartado de datos
    private void Resultados(ActionEvent e)
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
    //==================== Codigo para mostrar la grafica =====================
    @FXML //aqui es validan que todos los componentes del apartado de grafica
    private void mostrarGrafica(ActionEvent e)
    {      
        LocalDate fechaInicio= dt_fechaGrafica1.getValue();
        LocalDate fechaFinal=dt_fechaGrafica2.getValue();
        if((fechaInicio==null || fechaFinal==null) || (cbxSimboloGrafica.getValue()==null ||cbxSensor.getValue()==null)||(cbxValoresGrafica.getValue()==null))
        {
            MensajeAlerta ms= new MensajeAlerta("Error!!","No has has seleccionado todas las opciones...");
            ms.MostrarMensaje();
        }
        else if( fechaInicio.isBefore(fechaFinal))
        {
            String sensor= verSensor();
            hacerSerie(sensor);
            
        }
        else if(fechaInicio.isAfter(fechaFinal))
        {
            //fechaInicio es > a la primera fecha
            MensajeAlerta mg = new MensajeAlerta("Error de fecha","La combinación de fechas debe ser cronológica o iguales...");
            mg.MostrarMensaje();
        }
        else
        {
            String sensor= verSensor();
            hacerSerie(sensor);
        }   
        
    }
 
    @FXML
    private void CambiarValoresGrafica() //este metodo es para que los valores del combobox cbxValoresGrafica cambien en funcion de cbxSensor
    {      
        //aqui ponemos los valores que puede tomas el combobox de Valores en funcion del tipo de sensor que se elija
        switch (cbxSensor.getValue()) {
            case "Humedad":
               ObservableList<String> humedad= FXCollections.observableArrayList("0","10","20","30","40","50","60","70","80","90","100");
                cbxValoresGrafica.itemsProperty().setValue(humedad);
                break;
            case "Caudal de agua":
                ObservableList<String> caudal =FXCollections.observableArrayList("00.00","05.00","10.00","15.00");
                cbxValoresGrafica.itemsProperty().setValue(caudal);
                break;
            default: //Consumo de agua
               ObservableList<String> consumo =FXCollections.observableArrayList("00.00","10.00","20.00","30.00");
               cbxValoresGrafica.itemsProperty().setValue(consumo);
                break;
        }
    }
            
    private String verSensor() //este metodo es para enviar a la db la tabla que se va a mostrar en funcion de cbxSensor
    {   String sensor;
        switch (cbxSensor.getValue()) {
            case "Humedad":
               sensor="humedad";
                break;
            case "Caudal de agua":
                sensor="caudal_agua";
                break;
            default: //Consumo de agua
               sensor="consumo_agua";
                break;
        }
        return sensor;
    }
    private void hacerSerie(String sensor) //aqui es donde se solicita a la base de datos la info para graficarla
    {     
        try {
            String simbo =cbxSimboloGrafica.getValue();
            String valor =cbxValoresGrafica.getValue();
            DateTimeFormatter dt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            Statement sentencia = conn.createStatement();
            String query="SELECT fecha,"+sensor+" FROM sensores WHERE "+sensor+" "+simbo+valor+" AND fecha BETWEEN "
                    + "TO_DATE('"+dt_fechaGrafica1.getValue().format(dt)+" 00:00:00','dd/mm/yyyy hh24:mi:ss')"
                    +" AND TO_DATE('"+dt_fechaGrafica2.getValue().format(dt)+" 23:00:00','dd/mm/yyyy hh24:mi:ss')";
            ResultSet resultado = sentencia.executeQuery(query);
            
            lcGrafica.getData().clear();          
            X.setLabel("Tiempo transcurrido año-mes-día hora(24h):min:seg"); 
            XYChart.Series<String,Number> serie = new XYChart.Series<String,Number>();
            switch(sensor)
            {
                case "humedad":
                    while(resultado.next())
                        {
                            serie.getData().add(new XYChart.Data<String,Number>(resultado.getString("fecha"),resultado.getInt(sensor)));
                        }
                    Y.setLabel("Porcentaje de humedad");
                    break;
                case "caudal_agua":
                    while(resultado.next())
                        {
                          serie.getData().add(new XYChart.Data<String,Number>(resultado.getString("fecha"),resultado.getFloat(sensor)));
                        }
                   Y.setLabel("Caudal de agua en mililitros/seg");
                    break;
                default:
                    while(resultado.next())
                        {
                            serie.getData().add(new XYChart.Data<String,Number>(resultado.getString("fecha"),resultado.getFloat(sensor)));
                        }
                    Y.setLabel("Consumo de agua en mililitros/seg");
                    break;
            }
            lcGrafica.setTitle(cbxSensor.getValue());
            lcGrafica.getData().add(serie);
            serie.setName(cbxSensor.getValue());
            
        } catch (SQLException ex) {
           MensajeAlerta a = new MensajeAlerta("Error!!","Ha ocurrido un error al intentar graficar...Verifique la conexión");
           a.MostrarMensaje();
        }
    }
    //==================== los valores y propiedades que se inicializan al iniciar la app ==============
    @Override 
    public void initialize(URL location, ResourceBundle resources) {
        MostrarPuertos();
       // btnConectar.setDisable(true);
        //btnDesconectar.setDisable(true);
        //btnEncenderSensores.setDisable(true);
        //btnApagarSensores.setDisable(true);        
        try {
            conn=Conexion_db.getConnection();
        } catch (ClassNotFoundException ex) {
            System.out.println("error de la base de datos");
        } catch (SQLException ex) {
            System.out.println("error de la base de query");
        }
        cbxSimboloGrafica.itemsProperty().setValue(simboloGrafica);
        cbxHumedad.itemsProperty().setValue(cantidad_humedad);
        cbxSensor.itemsProperty().setValue(sensores);
        cbxSimbolo.itemsProperty().setValue(simbolo);
    }
}
