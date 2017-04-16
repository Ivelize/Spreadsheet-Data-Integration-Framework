/**
 * 
 */

function Model_Query(queryName, queryString, description, schemaId){
	console.log("in Model_Query constructor");
	this.id = Controller_DataspaceManager.getUniqueID();
	this.queryName = queryName || null;
	this.queryString = queryString || null;
	this.description = description || null;
	this.schemaId = schemaId || {};
};

Model_Query.prototype.getID = function(){
	console.log("in Model_Query.getID");
	return this.id;
};

Model_Query.prototype.setID = function(id){
	console.log("in Model_Query.setID");
	this.id = id;
};

/*
 * @param: Javascript object
 * @desc: Takes a standard javascript object, parses its attributes and extracts 
 * 		  any values that can be represented by a Model_Schema Object
 */
Model_Query.prototype.parseQuery = function(queryObject){
	console.log("in Model_Query.parseQuery");
	this.queryName = queryObject.queryName;
	this.queryString = queryObject.queryString;
	this.description = queryObject.description;
	this.schemaId = queryObject.schemaId;
	
	//finally override the ID if the passed object contains it
	if(typeof queryObject.id != 'undefined')
			this.id = queryObject.id;
};