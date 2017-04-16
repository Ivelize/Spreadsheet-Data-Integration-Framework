/**
 * 
 */

function Model_ResultValue(name, value){
	console.log("in Model_ResultValue constructor");
	this.id = Controller_DataspaceManager.getUniqueID();
	this.name = name;
	this.value = value;
	console.log("id: " + this.id);
};

Model_ResultValue.prototype.getID = function(){
	console.log("in Model_ResultValue.getID");
	return this.id;
};

Model_ResultValue.prototype.setID = function(id){
	console.log("in Model_ResultValue.setID");
	this.id = id;
	console.log("id: " + this.id);
};