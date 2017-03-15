const express = require('express')
var path = require('path');
const passport = require('passport')  
const session = require('express-session') 
const app = express()
fileUpload = require('express-fileupload');
const uuidV1 = require('uuid/v1');

app.use(fileUpload());
app.use(express.static('public'))
// respond with "hello world" when a GET request is made to the homepage
app.get('/', function (req, res) {
  res.sendfile( path.join( __dirname, 'public','login', 'index.html' ));
})
app.get('/home', function (req, res) {
  res.sendfile( path.join( __dirname, 'public','login', 'index.html' ));
})
app.post('/upload', function(req, res) {
  if (!req.files)
    return res.status(400).send('No files were uploaded.');
 
  // The name of the input field (i.e. "sampleFile") is used to retrieve the uploaded file 
 sampleFile = req.files.sampleFile;
 console.log(req.files.sampleFile.name)
 
  // Use the mv() method to place the file somewhere on your server 
  sampleFile.mv('data/'+uuidV1()+'.txt', function(err) {
    if (err)
      return res.status(500).send(err);
 
    res.send('File uploaded!');
  });
});


app.listen(3000);