const express = require('express')
var path = require('path');
const passport = require('passport')
const session = require('express-session')
const app = express()
fileUpload = require('express-fileupload');
const uuidV1 = require('uuid/v1');
var MongoClient = require('mongodb').MongoClient,
    format = require('util').format;
var exec = require('child_process').exec;
var fs = require('fs')




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

app.get("/generateCDA",function(req,res){
    res.sendfile(path.join(__dirname, 'public', 'cda_generation', 'cda_generation.html'));

})
app.post("/cda",function(req,res){
    req.body.patient_id;
    patient_id=req.body.patient_id;
    unique_file =uuidV1()+"_"+patient_id+".xml";
    command="java CDAGenerator "+patient_id+" iad "+unique_file;
    exec(command, {
                        cwd: "java"
                    }, function(err, stdout, stderror) {
                        if(err){
                            console.log(stderror)
                        }
                        console.log(stdout)
                        res.send("Ok")
                    })
})



app.post('/data', function(req, res) {
    if (!req.files)
        return res.status(400).send('No files were uploaded.');
    count = 0;
    filenames = [];
    unique_folder = "data/" + uuidV1()
    folder_path="java/"+unique_folder;
    no_files = req.files.sampleFile.length
    console.log("no files:", no_files)

    metadata_file=req.files.metadataFile

    if (!fs.existsSync(unique_folder)) {
        fs.mkdirSync(folder_path);
        metadata_file_path=folder_path+"/metadata.json"
        metadata_file.mv(metadata_file_path,function(err){

        	if(err){
        		console.log("could not move metadata file")
        	}
        })
        for (var file in req.files.sampleFile) {

            file = req.files.sampleFile[file]

            filename = file.name

            filename_path = folder_path +"/"+ filename;
            filenames.push(filename)

            file.mv(filename_path, function(err) {
                if (err)
                    return res.status(500).send(err);
                count += 1;
                if (count == no_files) {
                    console.log("Files moved")
                    command="java HiveClient "+ unique_folder;
                    exec(command, {
				        cwd: "java"
				    }, function(err, stdout, stderror) {
				    	if(err){
				    		console.log(stderror)
				    	}
				    	console.log(stdout)
				        res.send("Ok")
				    })

                }
            });

        }


    }
    else {

        console.log("Folder exists")
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