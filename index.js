const express = require('express')
var path = require('path');
const passport = require('passport')
const session = require('express-session')
const app = express()
fileUpload = require('express-fileupload');
const uuidV1 = require('uuid/v1');
var MongoClient = require('mongodb').MongoClient , format = require('util').format;
var exec = require('child_process').exec;
var WebHDFS = require('webhdfs');
var fs=require('fs')

// Create a new
var hdfs = WebHDFS.createClient({
  user: 'hduser', // Hadoop user
  host: 'localhost', // Namenode host
  port: 50070 // Namenode port
});

module.exports = hdfs;



app.use(fileUpload());
app.use(express.static('public'))
// respond with "hello world" when a GET request is made to the homepage
app.get('/', function(req, res) {
		res.sendfile(path.join(__dirname, 'public', 'login', 'index.html'));
})
app.get('/home', function(req, res) {
		res.sendfile(path.join(__dirname, 'public', 'login', 'index.html'));
})
app.get("/codesystem", function(req, res) {
		res.sendfile(path.join(__dirname, 'public', 'codesystem', 'fileupload.html'));

})
app.post('/codesystem', function(req, res) {
	if (!req.files)
		return res.status(400).send('No files were uploaded.');
	sampleFile = req.files.sampleFile;
	codeSystem = JSON.parse(req.files.sampleFile.data)
	codeSystemOID = uuidV1()
	codeSystem["codeSystem"] = codeSystemOID

	MongoClient.connect('mongodb://127.0.0.1:27017/test', function(err, db) {
	  	if (err) throw err;
	  		console.log("Connected to Database");
	  
		//insert record
		db.collection('codesystem').insert(codeSystem, function(err, records) {
			if (err) throw err;
			console.log("Record added");
			res.send('File uploaded! <br/><br/>Code System Code : ' + codeSystemOID);
		});
	});

	
});

app.post('/metadata', function(req, res) {
	if (!req.files)
		return res.status(400).send('No files were uploaded.');
	sampleFile = req.files.sampleFile;
	codeSystem = JSON.parse(req.files.sampleFile.data)
	codeSystemOID = uuidV1()
	codeSystem["codeSystem"] = codeSystemOID

	MongoClient.connect('mongodb://127.0.0.1:27017/test', function(err, db) {
	  	if (err) throw err;
	  		console.log("Connected to Database");
	  
		//insert record
		db.collection('metadata').insert(codeSystem, function(err, records) {
			if (err) throw err;
			console.log("Record added");
			res.send('File uploaded! <br/><br/>Code System Code : ' + codeSystemOID);
		});
	});

	
});

app.get("/data", function(req, res) {
		res.sendfile(path.join(__dirname, 'public', 'uploadCSV', 'csvfileupload.html'));

})
app.post('/data', function(req, res) {
	if (!req.files)
		return res.status(400).send('No files were uploaded.');
	count =0;
	filenames=[];

	no_files=req.files.sampleFile.length
	for(var file in req.files.sampleFile){

		console.log("file:",req.files.sampleFile[file])
		filename=req.files.sampleFile[file].name
		filename_path="data/"+filename;
		filenames.push(filename);
		console.log("filename:",filename)
		req.files.sampleFile[file].mv(filename_path,function(err) {
    		if (err)
      			return res.status(500).send(err);
      		count+=1;
      		if(count==no_files){

      			for(i in filenames){

      				local_path="data/"+filenames[i]
      				remote_path="/user/hive/data/"+filenames[i]
      				var localFileStream = fs.createReadStream(local_path);
					var remoteFileStream = hdfs.createWriteStream(remote_path);
					 
					localFileStream.pipe(remoteFileStream);
					 
					remoteFileStream.on('error', function onError (err) {
					  // Do something with the error 
					  console.log("error:",err)
					});
					 
					remoteFileStream.on('finish', function onFinish () {
					  // Upload is done 
					  console.log("uploaded file");
					});
      			}

      		}
 
		    
		});

		
	}
});

	// var child;

	// child = exec("ls -la",
 //  	 function (error, stdout, stderr) {
 //      console.log('stdout: ' + stdout);
 //      console.log('stderr: ' + stderr);
 //      if (error !== null) {
 //          console.log('exec error: ' + error);
 //      }
 //   });






app.listen(3000);