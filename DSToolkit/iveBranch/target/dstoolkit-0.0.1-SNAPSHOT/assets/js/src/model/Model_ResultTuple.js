/**
 * 
 */

function Model_ResultTuple(queryResultId){
	console.log("in Model_ResultTuple constructor");
	this.id = Controller_DataspaceManager.getUniqueID();
	this.queryResultId = queryResultId;
	this.resultValues = {};
	console.log("id: " + this.id);
};

Model_ResultTuple.prototype.getID = function(){
	console.log("in Model_ResultTuple.getID");
	return this.id;
};

Model_ResultTuple.prototype.setID = function(id){
	console.log("in Model_ResultTuple.setID");
	this.id = id;
};

// Create new association
Model_ResultTuple.prototype.addResultValue = function(resultValue){
	console.log("in Model_ResultTuple.addResultValue");
	console.log("resultValue.id: " + resultValue.id);
	console.log("resultValue.getID: " + resultValue.getID());
	this.resultValues[resultValue.id] = resultValue;
	//this.resultValues.push(resultValue);
};