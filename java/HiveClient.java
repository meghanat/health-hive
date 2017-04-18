import org.json.*;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class HiveClient {
  private static String driverName = "org.apache.hive.jdbc.HiveDriver";

  //if database does not exist, create database
  private static void createDatabase(String databaseName){
    try{ 
        Connection con = DriverManager.getConnection("jdbc:hive2://localhost:10000/default", "", "");
        Statement stmt = con.createStatement();
    
            ResultSet res;
            String sql = "create database if not exists "+ databaseName;
            stmt.execute(sql);
            System.out.println("database "+databaseName+" created");

           
    }
    catch(Exception e){
            
            System.out.println(e.getMessage());            
    }

  }

  /**
   * @param args
   * @throws SQLException
   */
  public static void main(String[] args) throws SQLException {

    //get folder name
    String folderName=args[0];
    String metaDataFilePath=folderName+"/metadata.json";
    String databaseName="";//same as the name of the organisation
    System.out.println(metaDataFilePath);
    try{
        String metadata=new String(Files.readAllBytes(Paths.get(metaDataFilePath)));
        System.out.println(metadata);  
        JSONObject obj = new JSONObject(metadata);
        databaseName = obj.getString("organisation");
        System.out.println(databaseName);
        createDatabase(databaseName);
  
    }
    catch(IOException e){
        e.printStackTrace();
    }
    catch(JSONException e){
        e.printStackTrace();
    }






    // Connection con = DriverManager.getConnection("jdbc:hive2://localhost:10000/default", "", "");
    // 

    // try{
    //         System.out.println("get request received");
    //         String resString="";
    //         ResultSet res;
    //         // show tables
    //         String sql = "show tables";
    //         resString+="Running: " + sql;
    //         System.out.println(resString);
    //         res= stmt.executeQuery(sql);
    //         if (res.next()) {
    //           resString+=res.getString(1);
    //           System.out.println(resString);
    //         }
    //         System.out.println(resString);
           
    //     }
    //     catch(Exception e){
            
    //         System.out.println(e.getMessage());
            
            
    //     }
    // // String tableName = "testHiveDriverTable";
    // // stmt.execute("drop table if exists " + tableName);
    // // stmt.execute("create table " + tableName + " (key int, value string)");
    // // show tables
    // String sql = "show tables";

    // System.out.println("Running: " + sql);
    // ResultSet res = stmt.executeQuery(sql);
    // if (res.next()) {
    //   System.out.println(res.getString(1));
    // }
    //    // describe table
    // sql = "describe " + tableName;
    // System.out.println("Running: " + sql);
    // res = stmt.executeQuery(sql);
    // while (res.next()) {
    //   System.out.println(res.getString(1) + "\t" + res.getString(2));
    // }


    // // load data into table
    // // NOTE: filepath has to be local to the hive server
    // // NOTE: /tmp/a.txt is a ctrl-A separated file with two fields per line
    // String filepath = "/tmp/a.txt";
    // sql = "load data local inpath '" + filepath + "' into table " + tableName;
    // System.out.println("Running: " + sql);
    // stmt.execute(sql);

    // // select * query
    // sql = "select * from " + tableName;
    // System.out.println("Running: " + sql);
    // res = stmt.executeQuery(sql);
    // while (res.next()) {
    //   System.out.println(String.valueOf(res.getInt(1)) + "\t" + res.getString(2));
    // }

    // // regular hive query
    // sql = "select count(1) from " + tableName;
    // System.out.println("Running: " + sql);
    // res = stmt.executeQuery(sql);
    // while (res.next()) {
    //   System.out.println(res.getString(1));
    // }
  }
}