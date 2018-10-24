/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;



/**
 *
 * @author J.Armando
 */
public class Tabla {
    
    private IntegerProperty  id;
    private StringProperty fecha;
    private StringProperty hora;
    private IntegerProperty humedad;
    private FloatProperty consumoAgua;
    
    public Tabla()
    {
        this.id=new SimpleIntegerProperty();
        this.fecha=new SimpleStringProperty();
        this.hora=new SimpleStringProperty();
        this.humedad=new SimpleIntegerProperty();
        this.consumoAgua=new SimpleFloatProperty();
    }

    
    public int getId() {
        return id.get();
    }

    
    public void setId(int id) {
        this.id.set(id);
    }
    
    public IntegerProperty getTablaid()
    {
        return this.id;
    }

    
    public String getFecha() {
        return fecha.get();
    }

   
    public void setFecha(String fecha) {
        this.fecha.set(fecha);
    }

   public StringProperty getTablaFecha()
    {
        return this.fecha;
    }
    public String getHora() {
        return hora.get();
    }

    
    public void setHora(String hora) {
        this.hora.set(hora);
    }

    public StringProperty getTablaHora()
    {
        return hora;
    }
    public int getHumedad() {
        return humedad.get();
    }

    
    public void setHumedad(int humedad) {
        this.humedad.set(humedad);
    }

    public IntegerProperty getTablaHumedad()
    {
        return this.humedad;
    }
    public float getConsumoAgua() {
        return consumoAgua.get();
    }

    
    public void setConsumoAgua(float consumo) {
        this.consumoAgua.set(consumo);
    }

    public FloatProperty getTablaConsumoAgua()
    {
        return this.consumoAgua;
    }
    
}
