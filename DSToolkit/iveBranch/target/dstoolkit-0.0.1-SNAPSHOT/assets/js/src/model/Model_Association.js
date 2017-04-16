/**
 * 
 */

function Model_Association(schemaID, attributeName){
	console.log("in Model_Association constructor");
	this.id = Controller_DataspaceManager.getUniqueID();
	this.schemaID = schemaID;
	this.attributeName = attributeName;
};

Model_Association.prototype.getID = function(){
	console.log("in Model_Association.getID");
	return this.id;
}