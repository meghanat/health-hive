const express = require('express')
var path = require('path');
const passport = require('passport')
const session = require('express-session')
const app = express()
fileUpload = require('express-fileupload');
const uuidV1 = require('uuid/v1');
var MongoClient = require('mongodb').MongoClient , format = require('util').format;

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

app.get("/data", function(req, res) {
		res.sendfile(path.join(__dirname, 'public', 'uploadCSV', 'csvfileupload.html'));

})
app.post('/data', function(req, res) {
	if (!req.files)
		return res.status(400).send('No files were uploaded.');
	console.log(req.files)
	
});




app.listen(3000);