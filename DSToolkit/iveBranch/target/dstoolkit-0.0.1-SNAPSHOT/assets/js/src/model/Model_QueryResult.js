/**
 * 
 */

function Model_QueryResult(name, queryId, queryName, queryString, schemaName, schemaId, selectedPrecisionOrRecall, selectedPrecisionOrRecallValue, dataSources){
	console.log("in Model_QueryResult constructor");
		
	this.id = Controller_DataspaceManager.getUniqueID();
	this.name = name || null;
	this.queryId = queryId || null;
	this.queryName = queryName || null;
	this.queryString = queryString || null;
	this.schemaName = schemaName || null;
	this.schemaId = schemaId || null;
	this.selectedPrecisionOrRecall = selectedPrecisionOrRecall | null;
	this.selectedPrecisionOrRecallValue = selectedPrecisionOrRecallValue || null;
	this.dataSources = dataSources || null;
	
	//this.resultSetColumnNames = {};
	//this.resultSet = [][];
	//this.morphismSet = null;
	//this.precision = null;
	//this.recall = null;
};

Model_QueryResult.prototype.getID = function(){
	console.log("in Model_QueryResult.getID");
	return this.id;
};

Model_QueryResult.prototype.setID = function(id){
	console.log("in Model_QueryResult.setID");
	this.id = id;
};

Model_QueryResult.prototype.setResultColumnNames = function(resultColumnNames){
	console.log("in Model_QueryResult.setResultColumnNames");
	this.resultSetColumnNames = resultSetColumnNames;
};

Model_QueryResult.prototype.setResultSet = function(resultSet){
	console.log("in Model_QueryResult.setResultSet");
	this.resultSet = resultSet;
};

Model_QueryResult.prototype.setMappings = function(mappings){
	console.log("in Model_QueryResult.setMappings");
	this.mappings = mappings;
};

Model_QueryResult.prototype.setPrecision = function(precision){
	console.log("in Model_QueryResult.setPrecision");
	this.precision = precision;
};

Model_QueryResult.prototype.setRecall = function(recall){
	console.log("in Model_QueryResult.setRecall");
	this.recall = recall;
};

/*
 * @param: Javascript object
 * @desc: Takes a standard javascript object, parses its attributes and extracts 
 * 		  any values that can be represented by a Model_QueryResult Object
 */
Model_QueryResult.prototype.parseQueryResult = function(queryResultObject){
	console.log("in Model_QueryResult.parseQueryResult");
	
	this.name = queryResultObject.name;
	this.queryId = queryResultObject.queryId;
	this.queryName = queryResultObject.queryName;
	this.queryString = queryResultObject.queryString;
	this.schemaName = queryResultObject.schemaName;
	this.schemaId = queryResultObject.schemaId;
	this.selectedPrecisionOrRecall = queryResultObject.selectedPrecisionOrRecall;
	this.selectedPrecisionOrRecallValue = queryResultObject.selectedPrecisionOrRecallValue;
	this.dataSources = queryResultObject.dataSources;
	
	this.resultSetColumnNames = queryResultObject.resultSetColumnNames;
	this.resultSet = queryResultObject.resultSet;
	this.morphismSet = queryResultObject.morphismSet;
	this.precision = queryResultObject.precision;
	this.recall = queryResultObject.recall;
	
	//finally override the ID if the passed object contains it
	if(typeof queryResultObject.id != 'undefined')
			this.id = queryResultObject.id;
};