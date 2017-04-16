/**
 * 
 */

function Model_Schema(name, datasourceName, size, entities){
	console.log("in Model_Schema constructor");
	this.id = Controller_DataspaceManager.getUniqueID();
	this.name = name || null;
	this.datasourceName = datasourceName || null;
	this.size = size || null;
	this.entities = entities || {};
};

Model_Schema.prototype.getID = function(){
	console.log("in Model_Schema.getID");
	return this.id;
};

/*
 * @param: Javascript object
 * @desc: Takes a standard javascript object, parses its attributes and extracts 
 * 		  any values that can be represented by a Model_Schema Object
 */
Model_Schema.prototype.parseSchema = function(schemaObject){
	console.log("in Model_Schema.parseSchema");
	this.name = schemaObject.name;
	this.datasourceName = schemaObject.datasourceName;
	this.size = schemaObject.size;
	this.entities = schemaObject.entities;
	
	//finally override the ID if the passed object contains it
	if(typeof schemaObject.id != 'undefined')
			this.id = schemaObject.id;
};