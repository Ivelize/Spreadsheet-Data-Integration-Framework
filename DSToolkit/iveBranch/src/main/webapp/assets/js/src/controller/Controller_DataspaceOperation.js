/**
 * @author Ian Arundale
 * @class Controller_Dataspace_Operation
 * @classDescription Model the controller dataspace object, this object is responsible 
 * 					 for keeping track of what datasources and morphisms are interactable
 * 					 within the dataspace workspace
 */

var Controller_DataspaceOperation = {	
	
	operationFactory: function(type){
		console.log("in Controller_DataspaceOperation.operationFactory");
		var instance = {};
		if(type == 'match')
			instance = new OperationMatch();
	}
};

/* 
 * Abstract Operation
 */
function Operation(){
	console.log("in Controller_DataspaceOperation.Operation");
	this.name = 'Generic Operation';
};

Operation.prototype.showLightBox = function(){
	console.log("in Controller_DataspaceOperation.showLightBox");
	Controller_UserInterface.LightBox.setParameters(this.parameters);
	Controller_UserInterface.LightBox.setTitle(this.name);
	Controller_UserInterface.LightBox.setInvokeOperation(this.operationName);
	
	$('#lightbox_bg').fadeIn(300);
	$('#lightbox').show();
};

Operation.prototype.validate = {
	checkTwoSchemas : function(){
		console.log("in Controller_DataspaceOperation.validate.checkTwoSchemas");
		if($('#schemas li.active').length != 2){
			alert('Two Schemas required');
			return false;
		}
		return true;
	}
};

/*
 * Match Operation
 */
Match.prototype = new Operation();
Match.prototype.constructor = Match;
function Match(){
	console.log("in Controller_DataspaceOperation.Match");
	this.name = 'Match';
	this.operationName = 'match';
	this.parameters = View_DataspaceOperation.matchParameters();
};

Match.prototype.validateArguments = function(){
	console.log("in Controller_DataspaceOperation.validateArguments");
	//check two sources selected
	if(this.validate.checkTwoSchemas())
		return true;
	else
		return false;
};

/*
 * Merge Operation
 */
Merge.prototype = new Operation();
Merge.prototype.constructor = Merge;
function Merge(){
	console.log("in Controller_DataspaceOperation.Merge");
	this.name = 'Merge';
	this.operationName = 'merge';
	this.parameters = View_DataspaceOperation.mergeParameters();
}

/*
 * Merge Operation
 */
Infercorrespondence.prototype = new Operation();
Infercorrespondence.prototype.constructor = Merge;
function Infercorrespondence(){
	console.log("in Controller_DataspaceOperation.Infercorrespondence");
	this.name = 'Infercorrespondence';
	this.operationName = 'infercorrespondence';
	this.parameters = View_DataspaceOperation.inferCorrespondenceParameters();
}

/*
 * ViewGen Operation
 */
ViewGen.prototype = new Operation();
ViewGen.prototype.constructor = ViewGen;
function ViewGen(){
	console.log("in Controller_DataspaceOperation.ViewGen");
	this.name = 'ViewGen';
	this.operationName = 'viewgen';
	this.parameters = View_DataspaceOperation.viewGenParameters();
}

/*
 * ViewStructure Operation
 */
ViewStructure.prototype = new Operation();
ViewStructure.prototype.constructor = ViewStructure;
function ViewStructure(schema){
	console.log("in Controller_DataspaceOperation.ViewStructure");
	this.name = 'View Schema Structure';
	this.operationName = 'viewstructure';
	this.schema = schema; 
	this.structure = View_DataspaceOperation.viewStructure(schema);
};

ViewStructure.prototype.displayStructure = function(){
	console.log("in Controller_DataspaceOperation.displayStructure");
	Controller_UserInterface.LightBox.setStructure(this.structure);
	Controller_UserInterface.LightBox.setTitle(this.name +' - '+this.schema.name);
	Controller_UserInterface.LightBox.setInvokeOperation(this.operationName);
	
	$('#lightbox_bg').fadeIn(300);
	$('#lightbox').show();
};

/*
 * Add datasource Operation
 */
AddDatasource.prototype = new Operation();
AddDatasource.prototype.constructor = AddDatasource;
function AddDatasource(AddDatasource){
	console.log("in Controller_DataspaceOperation.AddDatasource");
	this.name = 'Add Datasource';
	this.operationName = 'adddatasource';
};

AddDatasource.prototype.displayContent = function(){
	console.log("in Controller_DataspaceOperation.AddDatasource.displayContent");
	Controller_UserInterface.LightBox.setStructure(this.structure);
	Controller_UserInterface.LightBox.setTitle(this.name);
	Controller_UserInterface.LightBox.hideParameters();
	var html = View_DataspaceOperation.addDatasource();
	Controller_UserInterface.LightBox.setGeneralContent(html);
	
	$('#lightbox_bg').fadeIn(300);
	$('#lightbox').show();
};

/*
 * Add missing result tuple operation
 */

AddMissingResultTuple.prototype = new Operation();
AddMissingResultTuple.prototype.constructor = AddMissingResultTuple;
function AddMissingResultTuple(AddMissingResultTuple) {
	console.log("in Controller_DataspaceOperation.AddMissingResultTuple");
	this.name = 'Add missing result Tuple';
	this.operationName = 'addmissingresulttuple';
};

AddMissingResultTuple.prototype.displayContent = function() {
	console.log("in Controller_DataspaceOperation.AddMissingResultTuple");
	Controller_UserInterface.LightBox.setStructure(this.structure);
	Controller_UserInterface.LightBox.setTitle(this.name);
	Controller_UserInterface.LightBox.hideParameters();
	var html = View_DataspaceOperation.addMissingResultTuple();
	console.log("html: " + html);
	Controller_UserInterface.LightBox.setGeneralContent(html);
	
	$('#lightbox_bg').fadeIn(300);
	$('#lightbox').show();
};

/*
 * Add query Operation
 */
CreateQuery.prototype = new Operation();
CreateQuery.prototype.constructor = CreateQuery;
function CreateQuery(){
	console.log("in Controller_DataspaceOperation.CreateQuery");
	this.name = 'Add Query';
	this.operationName = 'addquery';
};

CreateQuery.prototype.displayContent = function(){
	console.log("in Controller_DataspaceOperation.CreateQuery.displayContent");
	Controller_UserInterface.LightBox.setStructure(this.structure);
	Controller_UserInterface.LightBox.setTitle(this.name);
	Controller_UserInterface.LightBox.hideParameters();
	
	var html = View_DataspaceOperation.addQuery();
	Controller_UserInterface.LightBox.setGeneralContent(html);
	
	$('#lightbox_bg').fadeIn(300);
	$('#lightbox').show();
};

/*
 * Run query Operation
 */
RunQuery.prototype = new Operation();
RunQuery.prototype.constructor = RunQuery;
function RunQuery(){
	console.log("in Controller_DataspaceOperation.RunQuery");
	this.name = 'Run Query';
	this.operationName = 'runquery';
};

RunQuery.prototype.displayContent = function(){
	console.log("in Controller_DataspaceOperation.RunQuery.displayContent");
	Controller_UserInterface.LightBox.setStructure(this.structure);
	Controller_UserInterface.LightBox.setTitle(this.name);
	Controller_UserInterface.LightBox.hideParameters();
	
	var schemas  = Controller_DataspaceManager.getActiveDataspace().getSchemaIndex();
	var html = View_DataspaceOperation.runQuery(schemas);
	Controller_UserInterface.LightBox.setGeneralContent(html);
	
	$('#lightbox_bg').fadeIn(300);
	$('#lightbox').show();
};
