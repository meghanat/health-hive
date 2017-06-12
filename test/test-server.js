var chai = require('chai');
var chaiHttp = require('chai-http');
var server = require('../index');
var should = chai.should();
var supertest = require('supertest');
var request =supertest("localhost:3000")
chai.use(chaiHttp);


describe('app', function() {
  it('should login',function(done) {
  chai.request("http://localhost:3000")
    .post('/login')
    .send({'user': 'testing', 'pass': 'test'})
    .end(function(err, res){
      res.should.have.status(200);
      done();
    	});
	});

  it('should serve homepage',function(done) {
  chai.request("http://localhost:3000")
    .get('/home')
    .end(function(err, res){
      res.should.have.status(200);
      done();
    	});
	});

  it('should serve data upload page',function(done) {
  chai.request("http://localhost:3000")
    .get('/data')
    .end(function(err, res){
      res.should.have.status(200);
      done();
    	});
	});

it('should serve codesystem upload page',function(done) {
  chai.request("http://localhost:3000")
    .get('/codesystem')
    .end(function(err, res){
      res.should.have.status(200);
      done();
    	});
	});

it('should serve CDA Export page',function(done) {
  chai.request("http://localhost:3000")
    .get('/generateCDA')
    .end(function(err, res){
      res.should.have.status(200);
      done();
    	});
	});

it('should serve CDA Import page',function(done) {
  chai.request("http://localhost:3000")
    .get('/importCDA')
    .end(function(err, res){
      res.should.have.status(200);
      done();
    	});
	});
it(' should upload CSV files', function(done) {
       request.post('/data')
              .attach('sampleFile', 'test/sampleA.csv')
              .attach('sampleFile', 'test/sampleB.csv')
              .attach('metadataFile', 'test/metadata.json')
              .end(function(err, res) {
                  res.should.have.status(302); // 'success' page
                  done();
              });
    });

});