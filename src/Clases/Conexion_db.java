
package Clases;

import java.util.Date;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TableView;

public class Conexion_db 
{
  
  public Conexion_db()
  {
      
  }
  
  public static Connection getConnection() throws ClassNotFoundException, SQLException {
    //Class.forName("oracle.jdbc.driver.OracleDriver");
    DriverManager.registerDriver( new oracle.jdbc.driver.OracleDriver() );
    Connection conexion = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:XE","system","GB3102JS9801"); 
    return conexion;
  }
  
  
   
}
