/**
 * @author Ian
 */

function Model_Entity(name){
	console.log("in Model_Entity constructor");
	this.id = Controller_DataspaceManager.getUniqueID();
	this.name = name;
	this.attributes = {};
	this.entities = {};
};

Model_Entity.prototype.getID = function(){
	console.log("in Model_Entity.getID");
	return this.id;
};