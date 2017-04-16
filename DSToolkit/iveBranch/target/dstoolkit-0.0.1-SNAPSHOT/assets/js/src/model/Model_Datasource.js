/**
 * 
 */

function Model_Datasource(name, connectionURL, schemaURL, driverClass, userName, password, description){
	console.log("in Model_Datasource constructor");
	//this.id = Controller_DataspaceManager.getUniqueID();
	this.name = name || null;
	this.connectionURL = connectionURL || null;
	this.schemaURL = schemaURL || null;
	this.driverClass = driverClass || null;
	this.userName = userName || null;
	this.password = password || null;
	this.description = description || null;
};

Model_Datasource.prototype.getID = function(){
	console.log("in Model_Datasource.getID");
	return this.id;
};

Model_Datasource.prototype.setID = function(id) {
	console.log("in Model_Datasource.setID");
	this.id = id;
}

/*
 * @param: Javascript object
 * @desc: Takes a standard javascript object, parses its attributes and extracts 
 * 		  any values that can be represented by a Model_Schema Object
 */
Model_Datasource.prototype.parseDatasource = function(datasourceObject){
	console.log("in Model_Datasource.parseDatasource");
	this.name = datasourceObject.name;
	this.connectionURL = datasourceObject.connectionURL;
	this.schemaURL = datasourceObject.schemaURL;
	this.driverClass = datasourceObject.driverClass;
	this.userName = datasourceObject.userName;
	this.password = datasourceObject.password;
	this.description = datasourceObject.description;
	
	//finally override the ID if the passed object contains it
	if(typeof datasourceObject.id != 'undefined')
			this.id = datasourceObject.id;
};