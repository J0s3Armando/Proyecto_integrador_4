
package Clases;

import java.util.Date;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;

public class Conexion_db 
{
  //declaracion de variables para la conexion
  Connection conexion;
  Statement sen;
  ResultSet res;
  
  public Conexion_db()
  {
      conexion=null;
  }
  
  public void conectar() throws ClassNotFoundException, SQLException {
    Class.forName("oracle.jdbc.driver.OracleDriver");
    conexion = DriverManager.getConnection("jdbc:oracle:thin:@localhost:127.0.0.1:XE", "system", "GB3102JS9801");      
  }
  
  public void Consultar(String fecha1, String fecha2, int id)
{
   
    
}
        
}
