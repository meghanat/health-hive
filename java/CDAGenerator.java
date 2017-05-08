import org.json.*;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.io.*;
// import java.io.FileReader;
// import java.io.IOException;
// import java.io.InputStream;
// import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.*;
import java.time.LocalDate;
import java.time.ZoneId;

import javax.xml.bind.JAXBException;

import cdaTemplate.*;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;

import com.mongodb.ServerAddress;
import java.util.Arrays;




public class CDAGenerator {



 	private static String driverName = "org.apache.hive.jdbc.HiveDriver";


	//delete temp directory
	private static void deleteDir(File file) {

    	File[] contents = file.listFiles();
    	if (contents != null) {
        	for (File f : contents) {
            	deleteDir(f);
        	}
    	}
    	file.delete();
    	System.out.println("directory deleted");
	}

	private static String getEffectiveTime(){

		    Date date = new Date();
			LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			int year  = localDate.getYear();
			int month = localDate.getMonthValue();
			int day   = localDate.getDayOfMonth();
			String effectiveTime=Integer.toString(year);
			if(month<10){
				effectiveTime+="0";
			}
			effectiveTime+=Integer.toString(month);
			if(day<10){
				effectiveTime+="0";
			}
			effectiveTime+=Integer.toString(day);
			return effectiveTime;
	}


	private static void getRows(String tableName, String patientID, Connection con)throws SQLException{
		Statement stmt=con.createStatement();
		String selectQuery="select * from "+tableName+" where patient_id="+patientID;
		System.out.println(selectQuery);
		ResultSet res =stmt.executeQuery(selectQuery);
    	if (res.next()) {

        	System.out.println(res.getString(1));
      	}

	}
  
  // public static ClinicalDocument.Component createComponent(JSONObject table,String patientID,Connection con){
  public static void createComponent(JSONObject table,String patientID,Connection con){

  	try
  	{
	  	String tableName=table.getString("name");
	  	ClinicalDocument.Component component= new ClinicalDocument.Component();
	  	ClinicalDocument.Component.Section section=new ClinicalDocument.Component.Section();
	  	section.setTitle(tableName);
		  	String patientIDColumn=null;
	  	//get patient id column
	  	JSONObject columns=table.getJSONObject("columns");

		Iterator<?> keys = columns.keys();

		while( keys.hasNext() ) {
	    	String key = (String)keys.next();
	    	JSONObject column=columns.getJSONObject(key);
	    	if(column.getBoolean("is_patient_id")){
	    		patientIDColumn=key;
	    		break;
	    	}
		}
		String query="select * from "+tableName+" where "+patientIDColumn+"="+patientID;
		System.out.println(query);
		try{
			try (PreparedStatement selectStmt = con.prepareStatement(
             query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
           ResultSet rs = selectStmt.executeQuery()) {
        if (!rs.isBeforeFirst()) {
          System.out.println("no rows found");
        }
        else {
          while (rs.next()) {
            for (int i = 1; i < rs.getMetaData().getColumnCount() + 1; i++) {
              System.out.print(" " + rs.getMetaData().getColumnName(i) + "=" + rs.getObject(i));
            }
            System.out.println("");
          }
        }
      }
    }
    catch (SQLException e) {
      throw new RuntimeException(e);
    }
		
	}
	catch(Exception e){
		e.printStackTrace();
	}			

 }
  
  public static void main(String[] args) throws SQLException {

		
		
		String patientID=args[0];
		String databaseName=args[1];
		String fileName=args[2];
		System.out.println(args[0]+args[1]+args[2]);
		String driverName = "org.apache.hive.jdbc.HiveDriver";

		//create object of object Factory
		ObjectFactory fact=new ObjectFactory();

		//create new document
		ClinicalDocument doc=fact.createClinicalDocument();

		//document id
		ClinicalDocument.Id id=fact.createClinicalDocumentId();
		id.setRoot("0");
		id.setExtension("0");
		doc.setId(id);

		//set title
		doc.setTitle("Patient Health Record");

		//set effective time of CDA generation
		ClinicalDocument.EffectiveTime et=fact.createClinicalDocumentEffectiveTime();
		et.setValue(getEffectiveTime());

		//set assignedcustodian
		ClinicalDocument.Custodian c=fact.createClinicalDocumentCustodian();
		ClinicalDocument.Custodian.AssignedCustodian.RepresentedCustodianOrganization rco=fact.createClinicalDocumentCustodianAssignedCustodianRepresentedCustodianOrganization();
		ClinicalDocument.Custodian.AssignedCustodian.RepresentedCustodianOrganization.Id rco_id=fact.createClinicalDocumentCustodianAssignedCustodianRepresentedCustodianOrganizationId();
		rco_id.setExtension(databaseName);
		rco_id.setRoot("0");
		rco.setId(rco_id);
		rco.setName(databaseName);
		ClinicalDocument.Custodian.AssignedCustodian ac=fact.createClinicalDocumentCustodianAssignedCustodian();
		ac.setRepresentedCustodianOrganization(rco);
		c.setAssignedCustodian(ac);

		//record Target
		ClinicalDocument.RecordTarget rt=fact.createClinicalDocumentRecordTarget();
		ClinicalDocument.RecordTarget.PatientRole pr=fact.createClinicalDocumentRecordTargetPatientRole();
		ClinicalDocument.RecordTarget.PatientRole.Id pr_id=fact.createClinicalDocumentRecordTargetPatientRoleId();
		pr_id.setRoot(databaseName);
		pr_id.setExtension(patientID);


		String connectionString="jdbc:hive2://localhost:10000/"+databaseName;
		Connection con = DriverManager.getConnection(connectionString, "root", "hadoop");


	
		//get metadata for user
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			
         // Now connect to your databases
         DB db = mongoClient.getDB( "test" );
         System.out.println("Connect to database successfully");
         DBCollection coll = db.getCollection("metadata");
         System.out.println("Collection metadata selected successfully");

         BasicDBObject query=new BasicDBObject("organisation",databaseName);
         
         DBCursor cursor = coll.find(query);
         List<ClinicalDocument.Component> componentList=new ArrayList<ClinicalDocument.Component>();	
         
		 try {
		   if(cursor.hasNext()) {
		   		String metadata=cursor.next().toString();
		   		JSONObject obj = new JSONObject(metadata);
				
				//get all tables in the database
				JSONArray tables=obj.getJSONArray("tables");
				for (int i = 0; i < tables.length(); i++)
				{
				    
				    JSONObject table = tables.getJSONObject(i);
				    // componentList.add(createComponent(table,patientID));
				    createComponent(table,patientID,con);
				    
				 	   
				}

		   }
		}catch(Exception e){
			e.printStackTrace();
		}
		 finally {
		   cursor.close();
		}

		
		// String connectionString="jdbc:hive2://localhost:10000/"+databaseName;
		// Connection con = DriverManager.getConnection(connectionString, "root", "hadoop");
		// Statement stmt = con.createStatement();
		// String showTables="show tables";




		
		// ResultSet res =stmt.executeQuery(showTables);
  //   	if (res.next()) {
  //   		String tableName=res.getString(1);
  //   		System.out.println(tableName);
  //       	spitOutAllTableRows(tableName,patientID,con);
  //     	}
			
		// ClinicalDocument doc=new ClinicalDocument();
		// doc.setId(patientID,"1.1.1.1.1");
		// doc.setTitle("Patient Health Record");
		// doc.setEffectiveTime(getEffectiveTime());
		// System.out.println(doc);

		// //Marshalling: Writing Java objects to XMl file
  //       try {
  //           JAXBXMLHandler.marshal(doc, new File(fileName));
  //       } catch (IOException e) {
  //           e.printStackTrace();
  //       } catch (JAXBException e) {
  //           e.printStackTrace();
  //       }

		// try{
		 //    PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		 //    writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<ClinicalDocument xmlns=\"urn:hl7-org:v3\" xmlns:voc=\"urn:hl7-org:v3/voc\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" templateId=\"1.1.1.1.1.1\">\n");
		 //    writer.println("<id extension=\"1\" root=\"1.1.1.1.1.1.1\" />\n<title>Patient Health Record</title>");
		
			// System.out.println(effectiveTime);
			// writer.println("<effectiveTime value=\""+effectiveTime+"\"/>");
			// writer.println("<custodian>\n<assignedCustodian>\n<representedCustodianOrganization>\n<id extension=\"1\" root=\"1.1.1.1.1.1.2\" />\n<name>Institute of Applied Dermatology</name>\n</representedCustodianOrganization>\n</assignedCustodian>\n</custodian>\n<recordTarget>\n<patientRole>\n<id extension=\""+patientID+"\" root=\"2.16.840.1.113883.3.933\" />\n</patientRole>\n</recordTarget>");
		 //    writer.close();
		// } 
		// catch (IOException e) {
		// 	e.printStackTrace();
		// }
	   
	}

}
