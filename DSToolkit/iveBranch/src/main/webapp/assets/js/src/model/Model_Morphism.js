/**
 * @author Ian Arundale
 * @class Model_Morphism
 * @classDescription Javascript port of the Morphism Java classes with
 * 					 implementations of MorphismSet & MorphismFactory
 */

var MorphismFactory = {
    morphismSetFromJSON: function(morphismSetJSON) {
    	console.log("in Model_Morphism.MorphismFactory.morphismSetFromJSON");
    	// Create a new morphismSet and override the ID if required
        var morphismSet = new Model_MorphismSet();

        if(typeof morphismSetJSON.id != 'undefined')
        	morphismSet.id = morphismSetJSON.id;
        
        morphismSet.name = morphismSetJSON.name;
        morphismSet.type = morphismSetJSON.type;
        morphismSet.constructSet1IDs = morphismSetJSON.constructSet1IDs;
        morphismSet.constructSet2IDs = morphismSetJSON.constructSet2IDs;
        
        	var morphism;
        // 	foreach morphism create appropriate object
        $.each(morphismSetJSON.morphisms, function(index, morphismJSON){
            
        	//------------- deal with correspondences ------------- 
        	if(morphismSetJSON.type == 'schematicCorrespondence'){
        		morphism = new Model_SchematicCorrespondence();
        		morphism.name = morphismJSON.name;
        		morphism.shortname = morphismJSON.shortname;
        		morphism.correspondenceType = morphismJSON.correspondenceType;
        		morphism.description = morphismJSON.description;
        		morphism.parameters = morphismJSON.parameters;
        		morphism.parentSchematicCorrespondences = morphismJSON.parentSchematicCorrespondences;
        		morphism.childSchematicCorrespondences = morphismJSON.childSchematicCorrespondences;
            }
        	//------------- deal with matches ------------- 
            else if(morphismSetJSON.type == 'match'){
            	morphism = new Model_Match();
        		morphism.name = morphismJSON.name;
        		morphism.score = morphismJSON.score;
        		morphism.parameters = morphismJSON.parameters;
        		morphism.parentMatches = morphismJSON.parentMatches;
        		morphism.childMatches = morphismJSON.childMatches;
            }
        	//------------- deal with mappings------------- 
            else if(morphismSetJSON.type == 'mapping'){
            	morphism = new Model_Match();
            	morphism.query1String = morphismJSON.query1String;
            	morphism.query2String= morphismJSON.query2String;
            }
        	
        	// generic morphism properties
        	if(typeof morphismJSON.id != 'undefined')
    			morphism.id = morphismJSON.id;
            morphism.name = morphismJSON.name;
            morphism.type = morphismJSON.type;
            morphism.constructSet1 = morphismJSON.constructSet1;
            morphism.constructSet2 = morphismJSON.constructSet2;

        	morphismSet.addMorphism(morphism);
		});
        
        return morphismSet;
    }
};

/* MorphismSet */
function Model_MorphismSet(constructSet1, constructSet2) {
	console.log("in Model_Morphism.Model_MorphismSet");
    this.id = "t" + Controller_DataspaceManager.getUniqueID();
    this.constructSet1ID = constructSet1 || null;
    this.constructSet2ID = constructSet2 || null;
    this.morphisms = {};
}

Model_MorphismSet.prototype.parseFromJSON = function(morphismObject){
	console.log("in Model_Morphism.Model_MorphismSet.prototype.parseFromJSON");
	this.name = morphismObject.name;
    this.shortname = morphismObject.shortName;
    this.correspondenceType = morphismObject.type;
    this.description = morphismObject.description;
    this.parameters = morphismObject.parameters;
    this.parentSchematicCorrespondences = morphismObject.parentSchematicCorrespondences;;
    this.childSchematicCorrespondences = morphismObject.childSchematicCorrespondences;
    
    if(typeof morphismObject.id != 'undefined')
    	this.id = morphismObject.id;
}; 

//Create new correspondence
Model_MorphismSet.prototype.addMorphism = function(morphism) {
	console.log("in Model_Morphism.Model_MorphismSet.prototype.addMorphism");
    this.morphisms[morphism.id] = morphism;
    return true;
};

//Return existing correspondence
Model_MorphismSet.prototype.getMorphism = function(morphismID) {
	console.log("in Model_Morphism.Model_MorphismSet.prototype.getMorphism");
    return this.morphisms[morphismID];
};

//Return existing correspondence
Model_MorphismSet.prototype.addToMorphismConstructSet = function(morphismID, constructSetID, attributeID) {
	console.log("in Model_Morphism.Model_MorphismSet.prototype.addToMorphismConstructSet");
    var morphism = this.morphisms[morphismID];
    morphisms = '';
};

Model_MorphismSet.prototype.getID = function(){
	console.log("in Model_Morphism.Model_MorphismSet.prototype.getID");
	return this.id;
}


/* 
 * Abstract Morphism Superclass
 */
function Morphism() {
	console.log("in Model_Morphism.Morphism");
    this.id = Controller_DataspaceManager.getUniqueID();
    this.name = '';
    this.type;
    this.constructSet1 = [];
    this.constructSet2 = [];
    this.topHeight = 140;
};

/*
 * @param constructSetNo: identifies whether to enter the construct into constructSet 1 or 2
 * @param construct: 
 */
Morphism.prototype.addElementToConstructSet = function(constructSetNo, constructID) {
	console.log("in Model_Morphism.Morphism.prototype.addElementToConstructSet");
    if (constructSetNo == 1) {
        this.constructSet1.push(constructID);
    }
    else if (constructSetNo == 2) {
        this.constructSet2.push(constructID);
    }
};

Morphism.prototype.checkIsConstructInSet = function(constructSetNo, constructID) {
	console.log("in Model_Morphism.Morphism.prototype.checkIsConstructInSet");
    var isFound = false;
    if (constructSetNo == 1) {
        $.each(this.constructSet1,
        function(index, construct) {
            if (construct == constructID)
            isFound = true;
        });
    }
    else {
        $.each(this.constructSet2,
        function(index, construct) {
            if (construct == constructID)
            isFound = true;
        });
    }
    return isFound;
};

Morphism.prototype.getID = function(){
	console.log("in Model_Morphism.Morphism.prototype.getID");
	return this.id;
};

Morphism.prototype.addElementToConstructSet = function(constructSetNo, construct) {
	console.log("in Model_Morphism.Morphism.prototype.addElementToConstructSet");
    if (constructSetNo == 1) {
        this.constructSet1.push(construct);
    }
    else if (constructSetNo == 2) {
        this.constructSet2.push(construct);
    }
};

Morphism.prototype.validate = {
    checkTwoDatasources: function() {
    	console.log("in Model_Morphism.Morphism.prototype.validate.checkTwoDatasources");
        if ($('#schemas li.active').length != 2) {
            alert('Two Schemas required');
            return false;
        }
        return true;
    }
};


/*
 * Match Morphism
 */
Model_Match.prototype = new Morphism();
Model_Match.prototype.constructor = Model_Match;
function Model_Match() {
	console.log("in Model_Morphism.Model_Match");
    this.name = '';
    this.score = '';
    this.parentMatches = {};
    this.childMatches = {};
};

/*
 * Model Schematic Correpondence Morphism
 */
Model_SchematicCorrespondence.prototype = new Morphism();
Model_SchematicCorrespondence.prototype.constructor = Model_SchematicCorrespondence;
function Model_SchematicCorrespondence(type) {
	console.log("in Model_Morphism.Model_SchematicCorrespondence");
    Morphism.call(this);
    // required to call the parent constructor (i.e. to assign a UID)
    this.name = '';
    this.shortname = '';
    this.correspondenceType = type;
    this.description = '';
    this.parameters = {};
    this.parentSchematicCorrespondences = {};
    this.childSchematicCorrespondences = {};
};



/*
 * Mapping Operation
 */
Model_Mapping.prototype = new Morphism();
Model_Mapping.prototype.constructor = Merge;
function Model_Mapping() {
	console.log("in Model_Morphism.Model_Mapping");
    this.name = '';
    this.query1String = '';
    this.query2String = '';
};