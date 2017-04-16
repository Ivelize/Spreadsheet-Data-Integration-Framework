/**
 * 
 */

function Model_EvaluateQueryParameters(name, queryId, queryName, queryString, schemaName, schemaId, selectedPrecisionOrRecall, selectedPrecisionOrRecallValue, dataSources){
	console.log("in Model_EvaluateQueryParameters constructor");
		
	this.id = Controller_DataspaceManager.getUniqueID();
	this.name = name || null;
	this.queryId = queryId || null;
	this.queryName = queryName || null;
	this.queryString = queryString || null;
	this.schemaName = schemaName || null;
	this.schemaId = schemaId || null;
	this.selectedPrecisionOrRecall = selectedPrecisionOrRecall || null;
	this.selectedPrecisionOrRecallValue = selectedPrecisionOrRecallValue || null;
	this.dataSources = dataSources || null;
	
	//console.log("id: " + id);
	console.log("name: " + name);
	console.log("queryId: " + queryId);
	console.log("queryName: " + queryName);
	console.log("queryString: " + queryString);
	console.log("schemaName: " + schemaName);
	console.log("schemaId: " + schemaId);
	console.log("selectedPrecisionOrRecall: " + selectedPrecisionOrRecall);
	console.log("selectedPrecisionOrRecallValue: " + selectedPrecisionOrRecallValue);
	console.log("dataSources: " + dataSources);
};

Model_EvaluateQueryParameters.prototype.getID = function(){
	console.log("in Model_EvaluateQueryParameters.getID");
	return this.id;
};

Model_EvaluateQueryParameters.prototype.setID = function(id){
	console.log("in Model_EvaluateQueryParameters.setID");
	this.id = id;
};

/*
 * @param: Javascript object
 * @desc: Takes a standard javascript object, parses its attributes and extracts 
 * 		  any values that can be represented by a Model_QueryResult Object
 */
Model_EvaluateQueryParameters.prototype.parseEvaluateQueryParameters = function(evaluateQueryParametersObject){
	console.log("in Model_EvaluateQueryParameters.parseQueryResult");
	
	this.name = evaluateQueryParametersObject.name;
	this.queryId = evaluateQueryParametersObject.queryId;
	this.queryName = evaluateQueryParametersObject.queryName;
	this.queryString = evaluateQueryParametersObject.queryString;
	this.schemaName = evaluateQueryParametersObject.schemaName;
	this.schemaId = evaluateQueryParametersObject.schemaId;
	this.selectedPrecisionOrRecall = evaluateQueryParametersObject.selectedPrecisionOrRecall;
	this.selectedPrecisionOrRecallValue = evaluateQueryParametersObject.selectedPrecisionOrRecallValue;
	this.dataSources = evaluateQueryParametersObject.dataSources;
	
	//finally override the ID if the passed object contains it
	if(typeof evaluateQueryParametersObject.id != 'undefined')
			this.id = evaluateQueryParametersObject.id;
};