import org.json.*;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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



public class CDAGenerator {



 	private static String driverName = "org.apache.hive.jdbc.HiveDriver";

	//create hive query for create table
	private static String getCreateQuery(String tableName,String CSVHeader){

		String query="create table "+tableName +"(";
		String columns[]=CSVHeader.split(",");
		for(int i=0;i<columns.length-1;i++){
			query+=columns[i]+" string,";
		}
		query+=columns[columns.length-1]+" string)";
		query+=" row format delimited fields terminated by ',' stored as textfile";
		// System.out.println(query);
		return query;
	}

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

	//remove CSV header

  
  public static void main(String[] args) throws SQLException {

		
		
		String patientID=args[0];
		String databaseName=args[1];
		String fileName=args[2];
		System.out.println(args[0]+args[1]+args[2]);

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
