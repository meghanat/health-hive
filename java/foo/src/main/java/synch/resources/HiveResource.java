package synch.resources;

import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

@Path("/hive")
@Produces(MediaType.TEXT_PLAIN)
public class HiveResource {
    private static String connectionString = "jdbc:hive2://localhost:10000/default";
   private static String driverName = "org.apache.hive.jdbc.HiveDriver";
   private static String queryString = "show tables";
   private static Connection con;
   private static ResultSet resultSet;
   private static Statement sqlStatement;

    @GET
    public String showTables() {
        
    try{
            Class.forName(driverName);
            con = DriverManager.getConnection(connectionString);
            System.out.println("Created connection. Preparing statement");
            sqlStatement = con.createStatement();
                System.out.println("Executing "+queryString);
            System.out.println("get request received");
            resultSet = sqlStatement.executeQuery(queryString);
            // show tables
             while(resultSet.next())
                       {
                               System.out.println("Result set "+resultSet.getString(1));
                       }
            con.close();
           
        }
        catch(SQLException sqle){
            
            System.out.println("Got sql exception");
            sqle.printStackTrace();
            return sqle.getMessage();
            
            
        }
        catch(Exception e)
               {
                       System.out.println("Got exception");
                       e.printStackTrace();
               }
    }
}
