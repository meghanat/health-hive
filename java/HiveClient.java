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

public class HiveClient {


 	private static String driverName = "org.apache.hive.jdbc.HiveDriver";

	//create hive query for create table
	private static String getCreateQuery(String tableName,String csvHeader){

		String query="create table "+tableName +"(";
			csvHeader = csvHeader.replace("\"", "");
			csvHeader = csvHeader.replace("\'", "");
		String columns[]=csvHeader.split(",");
		for(int i=0;i<columns.length-1;i++){
			query+=columns[i]+" string,";
		}
		query+=columns[columns.length-1]+" string)";
		query+=" row FORMAT SERDE 'org.apache.hadoop.hive.serde2.OpenCSVSerde' WITH SERDEPROPERTIES ('separatorChar' = ',') stored as textfile";
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

	//remove CSV header
	public static void removeFirstLine(String fileName) {

		System.out.println("csv file:"+fileName);  
		try{
			RandomAccessFile raf = new RandomAccessFile(fileName, "rw");          
			 //Initial write position                                             
			long writePosition = raf.getFilePointer();                            
			raf.readLine();                                                       
			// Shift the next lines upwards.                                      
			long readPosition = raf.getFilePointer();                             

			byte[] buff = new byte[1024];                                         
			int n;                                                                
			while (-1 != (n = raf.read(buff))) {                                  
				raf.seek(writePosition);                                          
				raf.write(buff, 0, n);                                            
				readPosition += n;                                                
				writePosition += n;                                               
				raf.seek(readPosition);                                           
			}                                                                     
			raf.setLength(writePosition);                                         
			raf.close();  
		}
		catch(IOException e){
			e.printStackTrace();
		}                                                        
	} 

	//create table and load data into table
	private static void createTable(String query,String folderName,String filename,String tableName,String databaseName){

		try{
			

			Connection con = DriverManager.getConnection("jdbc:hive2://localhost:10000/default", "root", "hadoop");
			Statement stmt = con.createStatement();
			
			// String loadStatement="";
			String dropTable="drop table if exists "+tableName;

			System.out.println("\n\n\nusing database:"+databaseName);
			stmt.execute("use "+databaseName);

			
			System.out.println("\ntable drop: "+dropTable);
			stmt.execute(dropTable);
			System.out.println("Table dropped");

			
			
			System.out.println("\ntable creation: "+query);
			stmt.execute(query);
			System.out.println("table created");
			String filepath="/home/hduser/health-hive/java/"+folderName+"/"+filename;
			System.out.println(filepath);

			

			String sql = "load data local inpath '" + filepath + "' into table " + tableName;


			System.out.println("Running: " + sql);
			stmt.execute(sql);

			
			System.out.println("data loaded");
			stmt.close();
			con.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}



	//if database does not exist, create database
	private static void createDatabase(String databaseName){
		try{ 

			Connection con = DriverManager.getConnection("jdbc:hive2://localhost:10000/default", "", "");
			Statement stmt = con.createStatement();
			String sql = "create database if not exists "+ databaseName;
			stmt.execute(sql);
			// System.out.println("database "+databaseName+" created");
			stmt.close();
			con.close();

			   
		}
		catch(Exception e){
				
				System.out.println(e.getMessage());            
		}

	}

  
  public static void main(String[] args) throws SQLException {

	//get folder name
	String folderName=args[0];
	String metaDataFilePath=folderName+"/metadata.json";
	String databaseName="";//same as the name of the organisation
	System.out.println(metaDataFilePath);
	try{
		String metadata=new String(Files.readAllBytes(Paths.get(metaDataFilePath)));
		// System.out.println(metadata);  
		JSONObject obj = new JSONObject(metadata);
		databaseName = obj.getString("organisation");
		// System.out.println(databaseName);
		createDatabase(databaseName);

		//for each table(csv file) create table in hive and import data into hive
		JSONArray arr = obj.getJSONArray("tables");
		for (int i = 0; i < arr.length(); i++)
		{
			String tableName = arr.getJSONObject(i).getString("name");//table name
			String fileName= arr.getJSONObject(i).getString("filename");//name of the csv file
			File file = new File(folderName+"/"+fileName);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String header=bufferedReader.readLine();//get header - line 1
			String createQuery=getCreateQuery(tableName,header);
			String path=folderName+"/"+fileName;
			removeFirstLine(path);
			createTable(createQuery,folderName,fileName,tableName,databaseName);
			fileReader.close();	
		}
		//delete folder and files
		File directory = new File(folderName);
		deleteDir(directory);


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