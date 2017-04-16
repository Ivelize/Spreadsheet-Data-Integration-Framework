/**
* @author Ian Arundale
* @pattern Singleton pattern
* @class Model_Dataspace
* @classDescription Model the dataspace singleton object, this object is responsible 
*						 for keeping track of what datasources and morphisms are interactable
*						 within the workspace
*/

function Model_Dataspace(dataspace_name) {
	console.log("in Model_Dataspace constructor");
	//assign the properties
	this.id;
	this.name = dataspace_name;
	this.schemaIndex = {};
	this.morphismIndex = {};
	this.tempMorphismSet = {};
	this.querySet = {};
	this.queryResultSet = {};
};

/*
* Data source functions
*/
// Add data source to the datasource array
Model_Dataspace.prototype.addSchema = function(schema) {
	console.log("in Model_Dataspace.addSchema");
	this.schemaIndex[schema.getID()] = schema;
};

// Add data source to the datasource array
Model_Dataspace.prototype.addMorphism = function(morphism) {
	console.log("in Model_Dataspace.addMorphism");
	this.morphismIndex[morphism.id] = morphism;
};

// Remove datasource form the datasource array
Model_Dataspace.prototype.removeSchema = function(schema) {
	console.log("in Model_Dataspace.removeSchema");
	this.schemaIndex[schema.getID()].pop();
};
/*
* @desc: Retrieve the datasource from the datasource array
* @return Datasource Object
*/
Model_Dataspace.prototype.getSchema = function(schema_id) {
	console.log("in Model_Dataspace.getSchema");
	return this.schemaIndex[schema_id];
};

// Return a new id number for datasource to be registered with
Model_Dataspace.prototype.getNewSchemaID = function() {
	console.log("in Model_Dataspace.getNewSchemaID");
	this.currentSchemaID++;
	return this.currentSchemaID;
};

//Return jQuery object of li elements that are currently selected
Model_Dataspace.prototype.get_selected_schemas = function() {
	console.log("in Model_Dataspace.get_selected_schemas");
	return $('div#schemas li.selected');
};

Model_Dataspace.prototype.getSchemaIndex = function() {
	console.log("in Model_Dataspace.getSchemaIndex");
	return this.schemaIndex;
};

/*
* Morphism functions
*/

// Add morphism to the datasource array
Model_Dataspace.prototype.addMorphismSet = function(morphism) {
	console.log("in Model_Dataspace.addMorphismSet");
	this.morphismIndex[morphism.getID()] = morphism;
	return morphism.getID();
};

// Remove morphism  form the morphism  array
Model_Dataspace.prototype.removeMorphismSet = function(morphism) {
	console.log("in Model_Dataspace.removeMorphismSet");
	this.morphismIndex[morphism.getID()].pop();
};
/*
* @desc: Retrieve the morphism  from the morphism array
* @return Datasource Object
*/
Model_Dataspace.prototype.getMorphismSet = function(morphism_id) {
	console.log("in Model_Dataspace.getMorphismSet");
	return this.morphismIndex[morphism_id];
};

Model_Dataspace.prototype.resetTempMorphism = function(){
	console.log("in Model_Dataspace.resetTempMorphism");
	this.tempMorphismSet = {};
};

/*
 * Query functions
 */

Model_Dataspace.prototype.addQuery = function(query){
	console.log("in Model_Dataspace.addQuery");
	this.querySet[query.id] = query;
};


Model_Dataspace.prototype.getQuery = function(query_id) {
	console.log("in Model_Dataspace.getQuery");
	return this.querySet[query_id];
};

Model_Dataspace.prototype.addQueryResult = function(queryResult){
	console.log("in Model_Dataspace.addQueryResult");
	this.queryResultSet[queryResult.id] = queryResult;
};


Model_Dataspace.prototype.getQueryResult = function(queryResult_id) {
	console.log("in Model_Dataspace.getQueryResult");
	return this.queryResultSet[queryResult_id];
};


/*
 * Search functions
 */
Model_Dataspace.prototype.findElementInDataspace = function(elementID) {
	console.log("in Model_Dataspace.findElementInDataspace");
	// search schema index
	if(this.schemaIndex[elementID] != undefined){
		return(this.schemaIndex[elementID]);
	}
	// search morphism index
	else if(this.morphismIndex[elementID] != undefined){
		//alert('this is a morphism');
		return(this.morphismIndex[elementID]);
	}	
	else if(this.querySet[elementID] != undefined){
		//alert('this is a query');
		return(this.querySet[elementID]);
	}
	else if(this.queryResultSet[elementID] != undefined){
		//alert('this is a queryResult');
		return(this.queryResultSet[elementID]);
	}
	else{
		alert('Model_Dataspace Error: can not find object in dataspace');
		return false;
	}
};


/*
* Depreciated - here for legacy reasons only
*/

// update id
Model_Dataspace.prototype.setID = function(id) {
	console.log("in Model_Dataspace.setID");
	this.id = id;
}

// get id
Model_Dataspace.prototype.getID = function() {
	console.log("in Model_Dataspace.getID");
	return this.id;
}

