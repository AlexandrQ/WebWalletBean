package DBConn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 
public class SingletonDBConnection
{
  private static SingletonDBConnection singleInstance;  
  private static Connection dbConnect;
   
  private SingletonDBConnection()
  {	  
    try
    {     
    	Class.forName("org.postgresql.Driver");       
      try
      {        
        dbConnect  = DriverManager.getConnection("jdbc:postgresql://localhost:5432/myTestPostgresDB","postgres", "admin");
      }
      catch (SQLException e)
      {
        e.printStackTrace();
      }  
    }    
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
   
  public static SingletonDBConnection getInstance()
  {		
    if(singleInstance == null)
    {    		
      synchronized (SingletonDBConnection.class)
      {
        if(singleInstance == null)
        {        	
          singleInstance = new SingletonDBConnection();
        }
      }
    }
 
    return singleInstance;
  }
   
  public static Connection getConnInst()
  {	  
    try
    {
      dbConnect = DriverManager.getConnection("jdbc:postgresql://localhost:5432/myTestPostgresDB","postgres", "admin");
    }
    catch (SQLException e1)
    {
      e1.printStackTrace();
    }
     
    if(dbConnect == null)
    {
      try
      {        
    	  Class.forName("org.postgresql.Driver");
         
        try
        {          
        	dbConnect  = DriverManager.getConnection("jdbc:postgresql://localhost:5432/myTestPostgresDB","postgres", "admin");
        }
        catch (SQLException e)
        {
          e.printStackTrace();
        }  
      }      
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
     
    return dbConnect;   
  }
}