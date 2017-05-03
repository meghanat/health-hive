const express = require('express')
var path = require('path');
const passport = require('passport')
const session = require('express-session')
const app = express()
fileUpload = require('express-fileupload');
const uuidV1 = require('uuid/v1');
var MongoClient = require('mongodb').MongoClient,
    format = require('util').format;

var LocalStrategy = require('passport-local').Strategy;

var mongoose = require('mongoose');    
var exec = require('child_process').exec;
var fs = require('fs')
var bCrypt=require("bcrypt-nodejs")

var flash = require('connect-flash');




app.use(fileUpload());
app.use(express.static('public'))

var mongoose = require('mongoose');
 
User=mongoose.model('User',{
        username: String,
    password: String
    
});

mongoose.connect("mongodb://localhost/passport");

app.use(session({secret: 'mySecretKey'}));
app.use(passport.initialize());
app.use(passport.session());

app.use(flash());
passport.serializeUser(function(user, done) {
  done(null, user._id);
});
 
passport.deserializeUser(function(id, done) {
  User.findById(id, function(err, user) {
    done(err, user);
  });
});

var isValidPassword = function(user, password){
  return bCrypt.compareSync(password, user.password);
}

// Generates hash using bCrypt
var createHash = function(password){
 return bCrypt.hashSync(password, bCrypt.genSaltSync(10), null);
}


 
// As with any middleware it is quintessential to call next()
// if the user is authenticated
var isAuthenticated = function (req, res, next) {
  if (req.isAuthenticated())
    return next();
  res.redirect('/');
}

passport.use('signup', new LocalStrategy({
    passReqToCallback : true
  },
  function(req, username, password, done) {

    console.log("signing uploaded");
    findOrCreateUser = function(){
      // find a user in Mongo with provided username
      User.findOne({'username':username},function(err, user) {
        // In case of any error return
        if (err){
          console.log('Error in SignUp: '+err);
          return done(err);
        }
        // already exists
        if (user) {
          console.log('User already exists');
          return done(null, false, 
             req.flash('message','User Already Exists'));
             
        } else {
          // if there is no user with that email
          // create the user
          var newUser = new User();
          // set the user's local credentials
          newUser.username = username;
          newUser.password = createHash(password);
 
          // save the user
          newUser.save(function(err) {
            if (err){
              console.log('Error in Saving user: '+err);  
              throw err;  
            }
            console.log('User Registration succesful');    
            return done(null, newUser);
          });
        }
      });
    };
     
    // Delay the execution of findOrCreateUser and execute 
    // the method in the next tick of the event loop
    process.nextTick(findOrCreateUser);
  })
);



passport.use('login', new LocalStrategy({
    passReqToCallback : true
  },
  function(req, username, password, done) { 

    console.log("heeeer")
    // check in mongo if a user with username exists or not
    User.findOne({ 'username' :  username }, 
      function(err, user) {
        // In case of any error, return using the done method
        if (err)
          return done(err);
        // Username does not exist, log error & redirect back
        if (!user){
          console.log('User Not Found with username '+username);
          return done(null, false, 
             req.flash('message','User Not Found'));
                
        }
        // User exists but wrong password, log the error 
        if (!isValidPassword(user, password)){
          console.log('Invalid Password');
          return done(null, false, 
             req.flash('message','Password Incorrect'));
              
        }
        // User and password both match, return user from 
        // done method which will be treated like success
        return done(null, user);
      }
    );
}));




// respond with "hello world" when a GET request is made to the homepage
app.get('/', function(req, res) {
    res.sendfile(path.join(__dirname, 'public', 'login', 'index.html'));
})

app.post('/login', passport.authenticate('login', {
    successRedirect: '/home',
    failureRedirect: '/login-failure',
  }));

app.get("/login-failure",function(req,res){

    res.sendfile(path.join(__dirname, 'public', 'login', 'login-failure.html'));

})

app.get("/signup",function(req,res){

    res.sendfile(path.join(__dirname, 'public', 'login', 'signup.html'));

})

app.get("/signup-failure",function(req,res){

    res.sendfile(path.join(__dirname, 'public', 'login', 'signup-failure.html'));

})
app.post('/signup', passport.authenticate('signup', {
    successRedirect: '/home',
    failureRedirect: '/signup-failure',
  }));



app.get('/home',isAuthenticated, function(req, res) {
    res.sendfile(path.join(__dirname, 'public', 'home', 'index.html'));
})
app.get("/codesystem",isAuthenticated, function(req, res) {
    res.sendfile(path.join(__dirname, 'public', 'codesystem', 'fileupload.html'));

})
app.post('/codesystem',isAuthenticated, function(req, res) {
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

app.post('/metadata', isAuthenticated,function(req, res) {
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

app.get("/data",isAuthenticated, function(req, res) {
    res.sendfile(path.join(__dirname, 'public', 'uploadCSV', 'csvfileupload.html'));

})

app.get("/generateCDA", isAuthenticated ,function(req,res){
    res.sendfile(path.join(__dirname, 'public', 'cda_generation', 'cda_generation.html'));

})
app.post("/cda", isAuthenticated,function(req,res){
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



app.post('/data', isAuthenticated ,function(req, res) {
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