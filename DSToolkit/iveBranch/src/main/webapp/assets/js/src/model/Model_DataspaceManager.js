/**
 * @author Ian Arundale
 * @pattern Singleton pattern
 * @class Model_DataspaceManager
 * @classDescription Model the dataspace singleton object, this object is responsible 
 * 					 for keeping track of what datasources and morphisms are interactable
 * 					 within the workspace
 */

function Model_DataspaceManager(){
	console.log("in Model_DataspaceManager constructor");
	
	// the cached instance
	var instance;
	
	//rewrite the constructor
	Model_DataspaceManager = function Model_DataspaceManager() {
		return instance;
	}
	
	// carry over the prototype properties
	Model_DataspaceManager.prototype = this;
	
	//the instance
	instance = new Model_DataspaceManager();
	
	//reset the constructor
	instance.constructor = Model_DataspaceManager;
	
	//the functionality
	instance.dataspaceIndex = {};
	instance.UID = 1000;
	
	return instance;
	
};

// add a new dataspace to the manager
Model_DataspaceManager.prototype.addDataspace = function(dataspace){
	console.log("in Model_DataspaceManager.addDataspace");
	dataspace.setID(this.getUniqueID());
	this.dataspaceIndex[dataspace.getID()] = dataspace;
};

// remove dataspace from DS manager
Model_DataspaceManager.prototype.removeDataspace = function(dataspace){
	console.log("in Model_DataspaceManager.removeDataspace");
	console.log(this.dataspaceIndex);
	delete this.dataspaceIndex[dataspace.getID()];
};

// get a dataspace from the dataspaceIndex
Model_DataspaceManager.prototype.getDataspace = function(dataspace_id){
	console.log("in Model_DataspaceManager.getDataspace");
	return this.dataspaceIndex[dataspace_id];
};

Model_DataspaceManager.prototype.getUniqueID = function(){
	console.log("in Model_DataspaceManager.getUniqueID");
	return this.UID++;
}

/* Primarily for test cases */
Model_DataspaceManager.prototype.resetUniqueID = function(){
	console.log("in Model_DataspaceManager.resetUniqueID");
	this.UID = 1;
}
