
import cda.*;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.ServerAddress;
import org.json.*;

import java.io.File;
import java.util.*;

public class ImportCda {

  private static String getPatientIDColumnName(String tableName,String databaseName){
    String patientIDColumn=null;
    MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
    DB db = mongoClient.getDB( "test" );
    // System.out.println("Connect to database successfully");
    DBCollection coll = db.getCollection("metadata");
    // System.out.println("Collection metadata selected successfully");

    BasicDBObject query=new BasicDBObject("organisation",databaseName);
   
    DBCursor cursor = coll.find(query);

    
    try{
      if(cursor.hasNext()){
        String metadata=cursor.next().toString();
        JSONObject obj = new JSONObject(metadata);
          
        //get all tables in the database
        JSONArray tables=obj.getJSONArray("tables");

        for (int i = 0; i < tables.length(); i++)
          {
              
              JSONObject table = tables.getJSONObject(i);

              if(table.getString("name").equals(tableName)){
                
                JSONObject columns=table.getJSONObject("columns");

                Iterator<?> keys = columns.keys();

                while( keys.hasNext() ) {
                    String key = (String)keys.next();
                    JSONObject column=columns.getJSONObject(key);
                    if(column.getBoolean("is_patient_id")){
                      patientIDColumn=key;
                      // System.out.println("patientIDColumn:"+patientIDColumn);
                      return patientIDColumn;
                    }
                }
                
                break;
              }
          }
        

      }
    }
    catch(Exception e){
      e.printStackTrace();
    }
    return patientIDColumn;

  }

  // private static String getEffectiveTimeColumnName(String tableName,String databaseName){

  //   String effectiveTimeColumn=null;
  //   MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
  //   DB db = mongoClient.getDB( "test" );
  //   // System.out.println("Connect to database successfully");
  //   DBCollection coll = db.getCollection("metadata");
  //   // System.out.println("Collection metadata selected successfully");

  //   BasicDBObject query=new BasicDBObject("organisation",databaseName);
   
  //   DBCursor cursor = coll.find(query);

    
  //   try{
  //     if(cursor.hasNext()){
  //       String metadata=cursor.next().toString();
  //       JSONObject obj = new JSONObject(metadata);
          
  //       //get all tables in the database
  //       JSONArray tables=obj.getJSONArray("tables");

  //       for (int i = 0; i < tables.length(); i++)
  //         {
              
  //             JSONObject table = tables.getJSONObject(i);

  //             if(table.getString("name").equals(tableName)){
                
  //               JSONObject columns=table.getJSONObject("columns");

  //               Iterator<?> keys = columns.keys();

  //               while( keys.hasNext() ) {
  //                   String key = (String)keys.next();
  //                   JSONObject column=columns.getJSONObject(key);
  //                   if(column.getBoolean("is_effective_time")){
  //                     effectiveTimeColumn=key;
  //                     System.out.println("effectiveTimeColumn:"+effectiveTimeColumn);
  //                     return effectiveTimeColumn;
  //                   }
  //               }
                
  //               break;
  //             }
  //         }
        

  //     }
  //   }
  //   catch(Exception e){
  //     e.printStackTrace();
  //   }
  //   return effectiveTimeColumn;


  // }
  
  private static void insertIntoTable(ClinicalDocument.Component.Section section,String patientID,String databaseName){

    if(section!=null){

      String tableName=section.getTitle();
      String patientIDColumn=getPatientIDColumnName(tableName,databaseName);
      System.out.println(tableName);

      // String effectiveTimeColumn=getEffectiveTimeColumnName(tableName,databaseName);
      String columnNames="(";
      String columnValues="("; 
      List<ClinicalDocument.Component.Section.Entry> entryList=section.getEntry();
      if(entryList!=null){

        for(ClinicalDocument.Component.Section.Entry entry: entryList){

          if(entry==null){
            continue;
          }
          else{
             ClinicalDocument.Component.Section.Entry.Observation.Code code=entry.getObservation().getCode();

             ClinicalDocument.Component.Section.Entry.Observation.Value value=entry.getObservation().getValue();

            String columnName=code.getOriginalText();
            if(columnName==null){

              columnName=code.getDisplayName();

            }
            String columnValue=value.getOriginalText();
            if(columnValue==null){

              columnValue=value.getDisplayName();

            }
            if(columnValue.trim().length()==0||columnValue==null){
                columnValue="null";
              }
            else{
              columnValue="'"+columnValue+"'";
            }

            // System.out.println("Column:"+columnName+" Value: "+columnValue);
            columnNames+=columnName+",";
            columnValues+=columnValue+",";
          }

        }
        // columnNames = columnNames.substring(0, columnNames.length()-1);
        columnNames+=patientIDColumn+")";
        columnValues += patientID+")";
        
        String query="INSERT INTO "+tableName+" "+columnNames+" VALUES "+columnValues;
        try{

          String connectionString="jdbc:hive2://localhost:10000/test";
          Connection con = DriverManager.getConnection(connectionString, "root", "hadoop");
          Statement stmt = con.createStatement();
          stmt.execute("SET hive.support.sql11.reserved.keywords=false");
          stmt.execute(query);  
        }
        catch(Exception e){
          e.printStackTrace();
        }
        
      }
    }
  }


  /**
   * @param args
   * @throws JAXBException 
   */
  public static void main(String[] args) throws JAXBException {
    
    File file=new File("sampleCDA.xml");
    ClinicalDocument doc = JAXBXMLHandler.unmarshal(file);

    String databaseName="iad";

    String patientID=doc.getRecordTarget().getPatientRole().getId().getExtension();

    List <ClinicalDocument.Component> componentList = new ArrayList<ClinicalDocument.Component>();
    
    componentList=doc.getComponent();


    for(ClinicalDocument.Component component:componentList){

      if(component==null){
        // System.out.println("Is null");
        continue;
      }

      ClinicalDocument.Component.Section section=component.getSection();
      insertIntoTable(section,patientID,databaseName);

    }
  }

}