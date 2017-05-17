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
  
  public static ClinicalDocument.Component createComponent(JSONObject table,String patientID,Connection con){
  // public static void createComponent(JSONObject table,String patientID,Connection con){

  	try
  	{
	  	String tableName=table.getString("name");
	  	ClinicalDocument.Component component= new ClinicalDocument.Component();
	  	ClinicalDocument.Component.Section section=new ClinicalDocument.Component.Section();

	  	component.setSection(section);
	  	section.setTitle(tableName);
		String patientIDColumn=null;
		// String effectiveTimeColumn=null;
		// String effectiveTimeValue=null;
	  	//get patient id column
	  	JSONObject columns=table.getJSONObject("columns");

		Iterator<?> keys = columns.keys();

		while( keys.hasNext() ) {
	    	String key = (String)keys.next();
	    	JSONObject column=columns.getJSONObject(key);
	    	if(column.getBoolean("is_patient_id")){
	    		patientIDColumn=key;
	    	}
	    	// if(column.getBoolean("is_effective_time")){
	    	// 	effectiveTimeColumn=key;
	    	// }
		}
		String query="select * from "+tableName+" where "+patientIDColumn+"="+patientID;
		// ClinicalDocument.Component.Section.Entry.Observation.EffectiveTime et=new ClinicalDocument.Component.Section.Entry.Observation.EffectiveTime();
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
            	String columnName=rs.getMetaData().getColumnName(i).split("\\.")[1];
            	String value=rs.getObject(i).toString();
            	JSONObject column=columns.getJSONObject(columnName);
            	ClinicalDocument.Component.Section.Entry entry=new ClinicalDocument.Component.Section.Entry();
            	ClinicalDocument.Component.Section.Entry.Observation obs= new ClinicalDocument.Component.Section.Entry.Observation();
            	
            	if(column.getBoolean("is_patient_id")){
            		continue;
            	}
            	if(column.getBoolean("is_encoded")){
            		ClinicalDocument.Component.Section.Entry.Observation.Code code=new ClinicalDocument.Component.Section.Entry.Observation.Code();
            		code.setCode(column.getString("columnNameCode"));
            		code.setDisplayName(columnName);
            		code.setCodeSystem(column.getString("columnNameCodeSystem"));
            		code.setCodeSystemName("");
            		obs.setCode(code);

            		ClinicalDocument.Component.Section.Entry.Observation.Value val=new ClinicalDocument.Component.Section.Entry.Observation.Value();
            		val.setCode(value);
            		val.setCodeSystem(column.getString("columnValuesCodeSystem"));
            		val.setDisplayName("");
            		val.setType("CD");
            		obs.setValue(val);

            	}
            	else{

            		ClinicalDocument.Component.Section.Entry.Observation.Code code=new ClinicalDocument.Component.Section.Entry.Observation.Code();

            		code.setOriginalText(columnName);
            		obs.setCode(code);

            		ClinicalDocument.Component.Section.Entry.Observation.Value val=new ClinicalDocument.Component.Section.Entry.Observation.Value();
            		val.setOriginalText(value);
					obs.setValue(val);

            	}
            	entry.setObservation(obs);
            	section.getEntry().add(entry);
            }
          }
        }
      }
      return component;
    }
    catch (SQLException e) {
      throw new RuntimeException(e);
    }
		
	}
	catch(Exception e){
		e.printStackTrace();
	}	
	return null;		

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
		doc.setCustodian(c);
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
		pr.setId(pr_id);
		rt.setPatientRole(pr);
		doc.setRecordTarget(rt);


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
				    doc.getComponent().add(createComponent(table,patientID,con));
				}
				

		   }


			//Marshalling: Writing Java objects to XMl file
	        
	        JAXBXMLHandler.marshal(doc, new File(fileName));


		}catch(Exception e){
			e.printStackTrace();
		}
		 finally {
		   cursor.close();
		}	   
	}

}
