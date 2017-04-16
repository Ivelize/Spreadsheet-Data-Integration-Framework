/**
 * @author Ian Arundale
 * @pattern Singleton pattern
 * @class Controller_Dataspace
 * @classDescription Model the controller dataspace object, this object is responsible 
 * 					 for keeping track of what datasources and morphisms are interactable
 * 					 within the dataspace workspace
 */

var Controller_Dataspace = {
	
    // add a new schema to the dataspace
    addNewSchema: function(schema) {
    	console.log("in Controller_Dataspace.addNewSchema");
    	console.log("schema: " + schema.getID());
        var dataspace = Controller_DataspaceManager.getActiveDataspace();

        // add the schema to the dataspace
        dataspace.addSchema(schema);
        //console.log('schema added -' + dataspace.toSource());
        console.log('schema added -' + schema.getID());
        var html = View_Dataspace.addSchema(schema);
        $('#source-schemas').append(html);
    },
    // add a new data source to the dataspace
    addNewMorphism: function(morphism) {
    	console.log("in Controller_Dataspace.addNewMorphism");
    	var dataspace = Controller_DataspaceManager.getActiveDataspace();
    	
    	// add the datasource to the dataspace
    	dataspace.addMorphism(morphism);
    	//console.log('morphism added -' + morphism.toSource());
    	console.log('morphism added -' + morphism.getID());
    	var html = View_Dataspace.addMorphismToWorkspace(morphism);
    	$('#morphisms').append(html);
    },
    
    addNewQuery: function(query){
    	console.log("in Controller_Dataspace.addNewQuery");
        var dataspace = Controller_DataspaceManager.getActiveDataspace();
        dataspace.addQuery(query);
        //console.log('query added -' + query.toSource());
        console.log('query added -' + query.getID());
        var html = View_Dataspace.addQuery(query);
        $('#queries').append(html);

    },
    
    addNewQueryResult: function(queryResult){
    	console.log("in Controller_Dataspace.addNewQueryResult");
    	//console.log.("queryResult: " + queryResult.getID());
        var dataspace = Controller_DataspaceManager.getActiveDataspace();
        dataspace.addQueryResult(queryResult);
        //console.log('query added -' + query.toSource());
        console.log('queryResult added -' + queryResult.getID());
        var html = View_Dataspace.addQueryResult(queryResult);
        console.log("html: " + html);
        $('#query-results').append(html);
    },

    // display correspondence editor
    displayManualCorrespondenceEditor: function() {
    	console.log("in Controller_Dataspace.displayManualCorrespondenceEditor");
	
        /* ask for the name of the new merged datasource first
		 * var newMergedDatasourceName = prompt('Merged datasoure name:');
		if(!newMergedDatasourceName)
			return false;
		*/
    	
        var tempMorphismSet = Controller_Dataspace.setUpTempMergedSchemas();

        var dataspace = Controller_DataspaceManager.getActiveDataspace();
        var constructSet1 = [dataspace.getSchema(tempMorphismSet.constructSet1ID)];
        var constructSet2 = [dataspace.getSchema(tempMorphismSet.constructSet2ID)];
        
        var constructSetHtml = View_Dataspace.displayConstructSets(constructSet1, constructSet2);
        $('#manually-define-correspondences').show();

        Controller_UserInterface.Editor.setTitle('Manually define correspondences');
        Controller_UserInterface.Editor.setConstructSets(constructSetHtml);
        Controller_UserInterface.Editor.displayEditorPane();
        Controller_UserInterface.Editor.showMorphismOptions();

        Controller_Dataspace.setupAttributeDragDrop();
    },
    
    displayQueryResults : function(){
    	console.log("in Controller_Dataspace.displayQueryResults");
    	Controller_UserInterface.Editor.setTitle('Annotate query results');
    	$('#annotate-query-results').show();
        Controller_UserInterface.Editor.displayEditorPane();

    },

	displayMorphismInEditor: function(){
		console.log("in Controller_Dataspace.displayMorphismInEditor");
		jsPlumb.removeEveryEndpoint();
		jsPlumb.repaintEverything();
		
		// retrieve morphism from dataspace
		//Controller_Dataspace.setupAttributeDragDrop();
		var morphismID = $('#morphisms li.active:first').attr('id');
        var dataspace = Controller_DataspaceManager.getActiveDataspace();
		var morphismSet = dataspace.getMorphismSet(morphismID);
		if (morphismSet == undefined) {
			var queryResultId = $('#query-results li.active:first').attr('id');
			console.log("queryResultId: " + queryResultId);
			var queryResult = dataspace.getQueryResult(queryResultId);
			morphismSet = queryResult.morphismSet;
		}
		console.log("morphismSet: " + morphismSet);
		dataspace.tempMorphismSet = morphismSet;
		
		
		//retrieve construct sets from morphism
        var constructSet1 = dataspace.tempMorphismSet.constructSet1IDs;//[dataspace.getSchema(dataspace.tempMorphismSet.constructSet1ID)];
        var constructSet2 = dataspace.tempMorphismSet.constructSet2IDs;//[dataspace.getSchema(dataspace.tempMorphismSet.constructSet2ID)];
        
        var constructSetHtml = View_Dataspace.displayConstructSets(constructSet1, constructSet2);

        Controller_UserInterface.Editor.setTitle('View Morphism Relations');
        Controller_UserInterface.Editor.setConstructSets(constructSetHtml);
        $('#manually-define-correspondences').show();
        Controller_UserInterface.Editor.displayEditorPane();
        Controller_UserInterface.Editor.showMorphismOptions();
        
		$.each(dataspace.tempMorphismSet.morphisms, function(index, morphism){
			Controller_Dataspace.drawMorphismToEditor(morphism);
		});
		
		Controller_Dataspace.setupAttributeDragDrop();
		//alert('now display the morphisms please');
		
		$.each($("#correspondences li"), function(){
			var current = $(this);
			var prev = $(this).prev();
			var ofsetAmount = 90;
			var newHeight;
			var index = (current.index()) -1;
			
			if(prev.length != 0){				
				if(parseInt(current.css("top")) + (index * ofsetAmount) == parseInt(prev.css("top")) ){
					newHeight = parseInt(prev.css("top")) + ofsetAmount;
					current.css("top", newHeight+'px' );
				}
				else if(current.css("top") == prev.css("top")){
					newHeight = parseInt(current.css("top")) + ofsetAmount;
					current.css("top", newHeight+'px' );
				}
			}
		});
		
		$("#correspondences li").hide().fadeIn(2000);
		//Controller_Dataspace.displayMorphismAttributesInEditor();
	},

    setUpTempMergedSchemas: function() {
    	console.log("in Controller_Dataspace.setUpTempMergedSchemas");
    	//TODO: this should also work for merged schemas
        var schema1ID = $('#schemas li.active:first').attr('id');
        var schema2ID = $('#schemas li.active:last').attr('id');

        var dataspace = Controller_DataspaceManager.getActiveDataspace();

        //Create the temp merged schema
        dataspace.tempMorphismSet = new Model_MorphismSet(schema1ID, schema2ID);
        dataspace.tempMorphismSet.type = 'schematicCorrespondence';
        return dataspace.tempMorphismSet;
    },

    getTheTwoSelectedSchemas: function() {
    	console.log("in Controller_Dataspace.getTheTwoSelectedSchemas");
    	//TODO: this should also work for merged schemas
        var schema1ID = $('#schemas li.active:first').attr('id');
        var schema2ID = $('#schemas li.active:last').attr('id');

        var dataspace = Controller_DataspaceManager.getActiveDataspace();

        var schema1 = dataspace.getSchema(schema1ID);
        var schema2 = dataspace.getSchema(schema2ID);

        var schemas = {
            'schema1': schema1,
            'schema2': schema2
        };

        return schemas;
    },

    setupAttributeDragDrop: function() {
    	console.log("in Controller_Dataspace.setupAttributeDragDrop");
        // attribute endpoint
        schema1EndPoint = {
            endpoint: new jsPlumb.Endpoints.Rectangle(),
            style: {
                width: 25,
                height: 21,
                fillStyle: 'blue'
            },
            isSource: true,
            scope: 'attribute',
            maxConnections: 3,
            anchor: "AutoDefault",
            connectorStyle: {
                lineWidth: 2,
                strokeStyle: 'blue'
            },
            isTarget: false
        };

        var schema1List = $('#manual-correspondence-schema1 li');
        console.log("schema1List: " + schema1List.toSource());
        var counter = -40;
        $.each(schema1List,
        function(index, listItem) {
            console.log("listItem.id:" + listItem.id);
            //$('#manual-correspondence-datasource1 li#' + listItem.id).addEndpoint(datasource1EndPoint, {uuid: listItem.id});
            
            schema1EndPoint.uuid = listItem.id;
            
            jsPlumb.addEndpoint('#manual-correspondence-schema1 li#' + listItem.id, schema1EndPoint);
            counter = counter + 80;
            jsPlumb.animate(listItem.id, {
                left: '+=5',
                top: counter
            },
            {
                duration: 900
            });
        });

        schema2EndPoint = {
            endpoint: new jsPlumb.Endpoints.Rectangle(),
            style: {
                width: 25,
                height: 21,
                fillStyle: 'red'
            },
            isSource: true,
            anchor: "AutoDefault",
            scope: 'attribute',
            maxConnections: 3,
            connectorStyle: {
                lineWidth: 2,
                strokeStyle: 'red'
            },
            isTarget: false
        };

        var schema2List = $('#manual-correspondence-schema2 li');
        var counter = -40;
        $.each(schema2List,
        function(index, listItem) {
            console.log(listItem.id);
            //$('#manual-correspondence-datasource2 li#' + listItem.id).addEndpoint(datasource2EndPoint);
            schema2EndPoint.uuid = listItem.id;
            jsPlumb.addEndpoint('#manual-correspondence-schema2 li#' + listItem.id, schema2EndPoint);

            counter = counter + 80;
            jsPlumb.animate(listItem.id, {
                left: '-=15',
                top: counter
            },
            {
                duration: 900
            });
        });

        $('#manual-correspondence-schema1 li').draggable();
        $('#manual-correspondence-schema2 li').draggable();
    },

    addCorrespondence: function(clickHeight) {
    	console.log("in Controller_Dataspace.addCorrespondence");
        var tempMorphismSet = Controller_DataspaceManager.getActiveDataspace().tempMorphismSet;
        //TODO:sort this one, could be of different type and have additional parameters
        var correspondence = new Model_SchematicCorrespondence('SNSC');
        correspondence.topHeight = clickHeight;
        tempMorphismSet.addMorphism(correspondence);

        //create HTML representation of correspondence
		Controller_Dataspace.drawMorphismToEditor(correspondence);
    },

    drawMorphismToEditor: function(morphism) {
    	console.log("in Controller_Dataspace.drawMorphismToEditor");
        //create HTML representation of correspondence
    	
        var tempMorphismSet = Controller_DataspaceManager.getActiveDataspace().tempMorphismSet;

    	
        var html = View_Dataspace.addMorphism(morphism, tempMorphismSet.type);
        $html = $(html);
        $('#correspondences').append(html);

        var exampleDropOptions = {
            tolerance: 'touch',
            hoverClass: 'dropHover',
            activeClass: 'dragActive'
        };
        
        var tempMorphismSet = Controller_DataspaceManager.getActiveDataspace().tempMorphismSet;


        var morphismColor = "green";
        if (tempMorphismSet.type == 'match')
        	morphismColor  = "gray";
        else if (tempMorphismSet.type == 'schematicCorrespondence')
        	morphismColor  = "green";
        else if (tempMorphismSet.type == 'mapping')
        	morphismColor  = "purple";
        
        var morphismEndpoint = {
            endpoint: new jsPlumb.Endpoints.Dot({
                radius: 13 
            }),
            reattach: true,
            anchor: "TopCenter",
            style: {
                strokeStyle: morphismColor,
                opacity: 0.8
            },
            isSource: false,
            scope: 'attribute',
            maxConnections: 10,
            connectorStyle: {
                strokeStyle: morphismColor,
                lineWidth: 4
            },
            connector: new jsPlumb.Connectors.Straight(),
            isTarget: true,
            dropOptions: exampleDropOptions,
            uuid: morphism.id
        };

        //$("#correspondences li#"+morphism.id).addEndpoint(exampleEndpoint3);
        jsPlumb.addEndpoint('#correspondences li#'+morphism.id, morphismEndpoint);
        jsPlumb.draggable($("#correspondences li"));
        jsPlumb.draggable($(".window"));
    },

    saveNewMorphism: function() {
    	console.log("in Controller_Dataspace.saveNewMorphism");
    	var newName = prompt('Please enter a name for this MorphismSet');
    	if(!newName){
    		return false;
    	}
    	
		var dataspace = Controller_DataspaceManager.getActiveDataspace();
		var tempMorphismSet = dataspace.tempMorphismSet;
		
		tempMorphismSet.name = newName;
		
		var newID = dataspace.addMorphismSet(tempMorphismSet);
		
		dataspace.resetTempMorphism();
		var morphism = dataspace.getMorphismSet(newID);
		var morphismHtml = View_Dataspace.addMorphismToWorkspace(morphism);
		$('#morphisms').append(morphismHtml);
		Controller_UserInterface.Editor.hideEditorPane();
    },

	displayMorphismAttributesInEditor: function(){
		console.log("in Controller_Dataspace.displayMorphismAttributesInEditor");
		//reset jsPlumb
		jsPlumb.repaintEverything();

		var morphismID = $('#morphisms li.active:first').attr('id');
        var dataspace = Controller_DataspaceManager.getActiveDataspace();  
      	var morphismSet = dataspace.getMorphismSet(morphismID);
		if (morphismSet == undefined) {
			var queryResultId = $('#query-results li.active:first').attr('id');
			console.log("queryResultId: " + queryResultId);
			var queryResult = dataspace.getQueryResult(queryResultId);
			morphismSet = queryResult.morphismSet;
		}
		console.log("morphismSet: " + morphismSet);
		dataspace.tempMorphismSet = morphismSet;

        var constructSet1 = dataspace.tempMorphismSet.constructSet1IDs;//[dataspace.getSchema(dataspace.tempMorphismSet.constructSet1ID)];
        var constructSet2 = dataspace.tempMorphismSet.constructSet2IDs;//[dataspace.getSchema(dataspace.tempMorphismSet.constructSet2ID)];
        
        //Controller_UserInterface.Editor.setTitle('View Morphism Relations');
 
		isDrawingExistingMorphism = true;
		$.each(dataspace.tempMorphismSet.morphisms, function(index, morphism){
			//	alert(morphism.toSource());
			//	Controller_Dataspace.drawMorphismToEditor(morphism);
			try{
				$.each(morphism.constructSet1, function(index, attributeID){
					//alert(morphism.constructSet1.toSource());
				//	alert(attributeID+' - ' + morphism.id);
					try{
						var attributeElement = $('#'+attributeID);
						var morphismElement = $('#'+morphism.id);
						
						//var attributeConnection = jsPlumb.getConnections({source:attributeElement});
						//alert(attributeConnection.toSource());
					
						//jsPlumb.connect({source:attributeElement, target:morphismElement});
						
						jsPlumb.connect({uuids:[attributeID, morphism.id]});
					} catch(e){
						alert('Controller_Dataspace: Connection Error: '+e);
					}
				});
			} catch(e){
				alert('Controller_Dataspace Error Caught:'+e);
			}
			
			try{
				$.each(morphism.constructSet2, function(index, attributeID){
					//alert(morphism.constructSet1.toSource());
				//	alert(attributeID+' - ' + morphism.id);
					try{
						var attributeElement = $('#'+attributeID);
						var morphismElement = $('#'+morphism.id);
					
						//jsPlumb.connect({source:attributeElement, target:morphismElement});
						jsPlumb.connect({uuids:[attributeID, morphism.id]});
					} catch(e){
						alert('Error: '+e);
					}
				});
			} catch(e){
				alert(e);
			}
		});
		
		isDrawingExistingMorphism = false;
	}

};