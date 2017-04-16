/**
 * 
 */

function Model_MergedSchema(name, schema1ID, schema2ID){
	console.log("in Model_MergedSchema constructor");
	this.id = Controller_DataspaceManager.getUniqueID();
	this.name = name;
	this.schema1ID = schema1ID;
	this.schema2ID = schema2ID;
	this.correspondenceSet = new Object;
};

Model_MergedSchema.prototype.getID = function(){
	console.log("in Model_MergedSchema.getID");
	return this.id;
}

// Create new correspondence
Model_MergedSchema.prototype.addCorrespondence = function(correspondence){
	console.log("in Model_MergedSchema.addCorrespondence");
	this.correspondenceSet[correspondence.id] = correspondence;
}

// Return correspondence object
Model_MergedSchema.prototype.getCorrespondence = function(correspondenceID){
	console.log("in Model_MergedSchema.getCorrespondence");
	if(this.correspondenceSet[correspondenceID] != undefined)
		return this.correspondenceSet[correspondenceID];
	else
		return false;
}

// Delete correspondence object from Set
Model_MergedSchema.prototype.removeCorrespondence = function(correspondenceID){
	console.log("in Model_MergedSchema.removeCorrespondence");
	delete this.correspondenceSet[correspondenceID];
}
