/**
 * @author Ian Arundale
 * @pattern Singleton pattern
 * @class Controller_DataspaceManager
 * @classDescription Model the dataspace singleton object, this object is responsible 
 * 					 for keeping track of what datasources and morphisms are interactable
 * 					 within the workspace
 */

var Controller_DataspaceManager = {
	// add a new dataspace
	addNewDataspace: function(newName){
		console.log("in Controller_DataspaceManager.addNewDataspace");
		var newName = prompt('Enter name of dataspace');
			if(!newName)
				return false;

		var dataspace = new Model_Dataspace(newName);
		var dataspaceManager = new Model_DataspaceManager();
		dataspaceManager.addDataspace(dataspace);
		
		// create the new dataspace in the view
		View_DataspaceManager.addDataspace(dataspace);
		Controller_DataspaceManager.displayDataspace(dataspace.id);
		
		Controller_DataspaceManager.addDefaultSchemas();
		//Controller_DataspaceManager.addDefaultMorphisms();
	},
	
	addDefaultSchemas: function(){
		console.log("in Controller_DataspaceManager.addDefaultSchemas");
		// getJSON handler
		
		
		$.getJSON('http://localhost:8080/dstoolkit/app/dataspace/1/schemas', {},
			function(data, status){
				// append the schema to the Dataspace object and display in the GUI 
				$.each(data, function(index, schema) {
					var schemaModel = new Model_Schema();
					schemaModel.parseSchema(schema);
					Controller_Dataspace.addNewSchema(schemaModel);
				});
				Controller_DataspaceManager.addDefaultQueries();
			});

		/*
		var schema = new Model_Schema();
		schema.parseSchema(travel_datasource1);
		Controller_Dataspace.addNewSchema(schema);
		
		var schema2 = new Model_Schema();
		schema2.parseSchema(travel_datasource2);
		Controller_Dataspace.addNewSchema(schema2);
		
		var schema3 = new Model_Schema();
		schema3.parseSchema(travel_datasource3);
		Controller_Dataspace.addNewSchema(schema3);
		*/
	},
	
	addDefaultQueries: function() {
		console.log("in Controller_DataspaceManager.addDefaultQueries");
		
		$.getJSON('http://localhost:8080/dstoolkit/app/dataspace/1/queries', {},
				function(data, status){
					// append the query to the Dataspace object and display in the GUI 
					$.each(data, function(index, query) {
						var queryModel = new Model_Query();
						queryModel.parseQuery(query);
						Controller_Dataspace.addNewQuery(queryModel);
					});
				});
	},
	
	addDefaultMorphisms: function(){
		console.log("in Controller_DataspaceManager.addDefaultMorphisms");
		
		var tempMorphism = MorphismFactory.morphismSetFromJSON(morphism2);
		Controller_Dataspace.addNewMorphism(tempMorphism);

		var tempMorphism = MorphismFactory.morphismSetFromJSON(morphism3);
		Controller_Dataspace.addNewMorphism(tempMorphism);

		var tempMorphism = MorphismFactory.morphismSetFromJSON(morphism4);
		Controller_Dataspace.addNewMorphism(tempMorphism);
		
		var tempMorphism = MorphismFactory.morphismSetFromJSON(morphism1);
		Controller_Dataspace.addNewMorphism(tempMorphism);
		
		var tempMorphism = MorphismFactory.morphismSetFromJSON(morphism5);
		Controller_Dataspace.addNewMorphism(tempMorphism);
		
		var tempMorphism = MorphismFactory.morphismSetFromJSON(morphism6);
		Controller_Dataspace.addNewMorphism(tempMorphism);
		
	},
	
	//remove dataspace
	removeDataspace: function(){
		console.log("in Controller_DataspaceManager.removeDataspace");
		if (confirm('Are you sure you want to delete this Dataspace?')) {
			var dataspace = Controller_DataspaceManager.getActiveDataspace();
			
			// modify the sidebar
			$('#dataspace-list li[id='+dataspace.id+']').remove();;
			var newDataspaceID = $('#dataspace-list li:first').attr('id');
			
			if(newDataspaceID)
				Controller_DataspaceManager.displayDataspace(newDataspaceID);
			else
				$('#main').html('');
			
			//remove from DS manager
			var dataspaceManager = new Model_DataspaceManager();
			dataspaceManager.removeDataspace(dataspace);
		}
		else
			return false;
	},
	
	// display a dataspace
	displayDataspace: function displayDataspace (dataspace_id){
		console.log("in Controller_DataspaceManager.displayDataspace");
		// check if the dataspace is already active
		var $dataspaceListElement = $('#dataspace-list li#'+dataspace_id);
		if($dataspaceListElement.hasClass('active'))
			return false;
		
		// construct the dataspace object
		var dataspaceManager = new Model_DataspaceManager();
		var dataspaceObject = dataspaceManager.getDataspace(dataspace_id);
		
		// create the new dataspace in the view
		var mainView = View_DataspaceManager.mainView(dataspaceObject);
		
		//update the list items
		$('ul#dataspace-list li').removeClass('active');
		$dataspaceListElement.addClass('active');
		$('#main').hide();
		$('#main').html(mainView).fadeIn(200);	
	},
	
	// get a dataspace from the dataspace manager
	getDataspace: function(dataspaceID){
		console.log("in Controller_DataspaceManager.getDataspace");
		var dataspaceManager = new Model_DataspaceManager();
		return dataspaceManager.getDataspace(dataspaceID);
		
	},
	
	getActiveDataspace: function(){
		console.log("in Controller_DataspaceManager.getActiveDataspace");
		var activeDataspaceID = $('#dataspace-list li.active').attr('id');
		return Controller_DataspaceManager.getDataspace(activeDataspaceID);
	},
	
	getUniqueID: function(){
		console.log("in Controller_DataspaceManager.getUniqueID");
		/*
		var dsManager = new Model_DataspaceManager();
		var newID = dsManager.getUniqueID();
		return newID;
		*/
		
		$.getJSON('http://localhost:8080/dstoolkit/app/dataspace/1/nextUniqueId', {},
				function(id, status){
					console.log("id: " + id);
					console.log("status: " + status);
					return id;
				});
		
		/*
		$.ajax({
			'type' : 'GET',
			'url' : 'http://localhost:8080/dstoolkit/app/dataspace/1/nextUniqueId', 
			'contentType': 'application/json',
			'data': {}, 
			'dataType': 'json',
			'async': 'false',
			'success': function(id, status){
				console.log("id: " + id);
				console.log("status: " + status);
				return id;
		}});
		*/
	},
	
	resetUniqueID: function(){
		console.log("in Controller_DataspaceManager.resetUniqueID");
		var dsManager = new Model_DataspaceManager();
		var newID = dsManager.resetUniqueID();
		return true;
	}
};