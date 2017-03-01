var express = require('express')
var app = express()
fileUpload = require('express-fileupload');
app.use(fileUpload());

// respond with "hello world" when a GET request is made to the homepage
app.get('/', function (req, res) {
  res.sendfile('public/fileupload.html');
})

app.post('/upload', function(req, res) {
  if (!req.files)
    return res.status(400).send('No files were uploaded.');
 
  // The name of the input field (i.e. "sampleFile") is used to retrieve the uploaded file 
 sampleFile = req.files.sampleFile;
 
  // Use the mv() method to place the file somewhere on your server 
  sampleFile.mv('data/here.txt', function(err) {
    if (err)
      return res.status(500).send(err);
 
    res.send('File uploaded!');
  });
});


app.listen(3000);