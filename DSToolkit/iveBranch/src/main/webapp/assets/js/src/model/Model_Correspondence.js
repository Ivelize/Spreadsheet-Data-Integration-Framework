/**
 * 
 */

function Model_Correspondence(type){
	console.log("in Model_Correspondence constructor");
	this.id = Controller_DataspaceManager.getUniqueID();
	this.type = type;
	this.associationSet = {};
};

Model_Correspondence.prototype.getID = function(){
	console.log("in Model_Correspondence.getID");
	return this.id;
}

// Create new association
Model_Correspondence.prototype.addAssociation = function(association){
	console.log("in Model_Correspondence.addAssociation");
	this.associationSet[association.id] = association;
}