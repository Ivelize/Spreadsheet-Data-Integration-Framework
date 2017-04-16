/**
 * @author Ian Arundale
 * @pattern Singleton pattern
 * @class Controller_Dataspace
 * @classDescription Model the controller dataspace singleton object, this object is responsible 
 * 					 for keeping track of what datasources and morphisms are interactable
 * 					 within the workspace
 */

var Controller_UserInterface = {
	// show right click menu
	showSchemaMenu: function(e){
		console.log("in Controller_UserInterface.showSchemaMenu");
		var dataspaceEnvironmentStatus = {};

		var html = View_UserInterface.showSchemaMenu(dataspaceEnvironmentStatus);	
		Controller_UserInterface.displayRightClickMenu(html, e);
	},
	
	// show right click menu
	showMainContainerMenu: function(e){
		console.log("in Controller_UserInterface.showMainContainerMenu");
		var html = View_UserInterface.showMainContainerMenu();	
		Controller_UserInterface.displayRightClickMenu(html, e);
	},
	
	showManualDefineCorrespondenceMenu: function(e){
		console.log("in Controller_UserInterface.showManualDefineCorrespondenceMenu");
		var html = View_UserInterface.showManualDefineCorrespondenceMenu();	
		Controller_UserInterface.displayRightClickMenu(html, e);
	},
	
	showCorrespondenceMenu: function(e){
		console.log("in Controller_UserInterface.showCorrespondenceMenu");
		var html = View_UserInterface.showCorrespondenceMenu();
		Controller_UserInterface.displayRightClickMenu(html, e);
	},
	
	showQueryMenu: function(e){
		console.log("in Controller_UserInterface.showQueryMenu");
		var html = View_UserInterface.showQueryMenu();	
		Controller_UserInterface.displayRightClickMenu(html, e);
	},
	
	showQueryResultMenu: function(e){
		console.log("in Controller_UserInterface.showQueryResultMenu");
		var html = View_UserInterface.showQueryResultMenu();	
		Controller_UserInterface.displayRightClickMenu(html, e);
	},
	
	showAnnotateMenu: function(e){
		console.log("in Controller_UserInterface.showAnnotateMenu");
		var html = View_UserInterface.showAnnotateMenu();	
		Controller_UserInterface.displayRightClickMenu(html, e);
	},
	
	showQueryMenu: function(e){
		console.log("in Controller_UserInterface.showQueryMenu");
		var html = View_UserInterface.showQueryMenu();	
		Controller_UserInterface.displayRightClickMenu(html, e);
	},
	
	displayRightClickMenu: function(html, e){
		console.log("in Controller_UserInterface.displayRightClickMenu");
		$('#right-click-menu').html('');
		$('#overlay').show();
		$('#right-click-menu').append(html);
		$('#right-click-menu').css({
		    top: e.pageY+'px',
		    left: e.pageX+'px'
		}).show(100);
	},
	
	showResultSet : function(queryResult){
		console.log("in Controller_UserInterface.showResultSet");
		$('#annotate-query-results').show();
		Controller_UserInterface.Editor.displayEditorPane();
		Controller_UserInterface.Editor.showAnnotateQueryResultOptions(queryResult);
	},
	
	clearActiveClass : function(){
		console.log("in Controller_UserInterface.clearActiveClass");
		$('#source-schemas li').removeClass('active');
		$('#merged-schemas li').removeClass('active');
		$('#morphisms li').removeClass('active');
		$('#queries li').removeClass('active');
		$('#query-results li').removeClass('active');
	},
	
	createNewResultTuple : function(queryResultId) {
		var newResultTuple = new Model_ResultTuple(queryResultId);
		var dataspace = Controller_DataspaceManager.getActiveDataspace();
		var newResultSet = dataspace.getQueryResult(queryResultId);
		
		$.each(newResultSet.resultSetColumnNames, function(index, name){
			console.log("name: " + name);
			var value = $('table#addMissingResultTuple input[name="' + name + '"]').attr('value');
			console.log("value: " + value);
			var resultValue = new Model_ResultValue(name, value);
			console.log("resultValue: " + resultValue);
			
			$.ajax({ 
				'type' : 'GET',
				'url' : 'http://localhost:8080/dstoolkit/app/dataspace/1/nextUniqueId',
				'contentType': 'application/json',
				'dataType': 'json',
				'async' : false,
				'success': function(id, status){
						console.log("id: " + id);
						console.log("status: " + status);
						resultValue.setID(id);		
						newResultTuple.addResultValue(resultValue);	
			}});
		});
		return newResultTuple;
	},
	
	/*
	 * Lightbox UI Element
	 */
	LightBox : {
		setTitle: function(titleHtml){
			console.log("in Controller_UserInterface.LightBox.setTitle");
			$('#lightbox-title').html(titleHtml);
		},
		setParameters: function(parameterTableHtml){
			console.log("in Controller_UserInterface.LightBox.setParameters");
			$('table#operationParameters').append(parameterTableHtml);
			$('#operationParamLayer').show();
			$('#runOperationContainer').show();
			$('#run-operation').css('display', 'inline-block');
			$('#manageParameters ul').show();
		},
		setStructure: function(schemaStructureHtml){
			console.log("in Controller_UserInterface.LightBox.setStructure");
			$('#schemaDefinition').append(schemaStructureHtml);
			$('#schemaDefinition').show();
		},
		hideParameters: function(){
			console.log("in Controller_UserInterface.LightBox.hideParameters");
			$('#manageParameters ul').hide();
		},
		setGeneralContent : function(html){
			console.log("in Controller_UserInterface.LightBox.setGeneralContent");
			$('#generalContent').append(html);
			$('#generalContent').show();
		},
		closeLightbox: function(){
			console.log("in Controller_UserInterface.LightBox.closeLightbox");
			$('#lightbox').hide();
			$('#operationParamLayer').hide();
			$('#manageParameters ul').hide();
			$('#runOperationContainer').hide();
			$('#schemaDefinition').hide();
			$('#operationViewStructureLayer').hide();
			$('#lightbox_bg').fadeOut(300);
			$('#lightbox-title').html('');
			$('#operationParameters tbody').html('');
			$('#schemaDefinition').html('');
			$('#generalContent').html('');
			$('#run-operation').attr('operation', '');
		},
		setInvokeOperation: function(operation){
			console.log("in Controller_UserInterface.LightBox.setInvokeOperation");
			$('#run-operation').attr('operation', operation);
		}
	},
	
	/*
	 * Rename function
	 */
	renameElement: function(){
		console.log("in Controller_UserInterface.renameElement");
        var elementID = $('#main li.active:first').attr('id');
		var dataspace = Controller_DataspaceManager.getActiveDataspace();
		var element = dataspace.findElementInDataspace(elementID);
		var newName = prompt('Please enter new name');
		if(newName){
			element.name = newName;
			$('#'+elementID).html(newName);
		}
	},
	
	/*
	 * Editor UI element
	 */
	Editor : {
		setTitle : function(titleHtml){
			console.log("in Controller_UserInterface.Editor.setTitle");
			$('#editor-title').html(titleHtml);
		},
		setConstructSet1: function(constructSet1Html){
			console.log("in Controller_UserInterface.Editor.setConstructSet1");
			$('#manual-correspondence-schema1').html(constructSet1Html);
		},
		setConstructSet2: function(constructSet2Html){
			console.log("in Controller_UserInterface.Editor.setConstructSet2");
			$('#manual-correspondence-schema2').html(constructSet2Html);
		},
		setConstructSets: function(constructSetHtml){
			console.log("in Controller_UserInterface.Editor.setConstructSets");
			this.setConstructSet1(constructSetHtml.constructSet1Html);
			this.setConstructSet2(constructSetHtml.constructSet2Html);
		},
		displayEditorPane: function(){
			console.log("in Controller_UserInterface.Editor.displayEditorPane");
			$('#editor-container').show();
			$('#editor-main').fadeIn(1000);
			$('#editor-content').show("slide", { direction: "left" }, 300);
			$('#back-button').css({'border-color':'transparent #fff transparent transparent'});
			
			$('div.container').hide("slide", { direction: "right" }, 400);
			$('#main-options').hide();
		},
		showAnnotateQueryResultOptions : function(queryResult){
			console.log("in Controller_UserInterface.Editor.showAnnotateQueryResultOptions");
			console.log("queryResult: " + queryResult);
			$('table#query-results').html('');
			var newResultSet = queryResult;
				
			var html ='<tr>';
			$.each(newResultSet.resultSetColumnNames, function(index, value){
				if (value != 'id') {
					console.log("value isn't id: " + value);
					html += '<th>'+value+'</th>';
				}
			});
			html += '</tr>';
			
			$.each(newResultSet.resultSet, function(index, resultTuple){
				$.each(resultTuple, function(index, resultValue){
					console.log("index: " + index);
					if (index == 0) {
						//this assumes that id is the first one in the list - should be the case
						html += '<tr id='+resultValue+'>';
					} else {
						html += '<td>'+resultValue+'</td>';
					}
				});
				html += '</tr>';
			});
			html +='</tr>';
			
			$('table#query-results').append(html);
			$('#annotate-query-results-options').show("slide", { direction: "right" }, 400);
		},
		showMorphismOptions : function(){
			console.log("in Controller_UserInterface.Editor.showMorphismOptions");
			$('#correspondence-options').show("slide", { direction: "right" }, 400);
		},
		hideEditorPane: function(){
			console.log("in Controller_UserInterface.Editor.hideEditorPane");
			jsPlumb.removeEveryEndpoint();
			$('#back-button').css({'border-color':'transparent #4F4F4F transparent transparent'});
			$('#editor-main').fadeOut('300');
			$('#editor-content').hide("slide", { direction: "left" }, 400, function(){
				$('#editor-container').hide();	
				$('#manually-define-correspondences').hide();
				$('#annotate-query-results').hide();
			});

			//clear the datasources
			$('#correspondences').html('');
			
			$('#main-options, div.container`').show("slide", { direction: "right" }, 400);
			$('#main-options').show();
			$('#correspondence-options, #annotate-query-results-options').hide();
		},
		callAttributes: function(){
			console.log("in Controller_UserInterface.Editor.callAtributes");
			Controller_Dataspace.displayMorphismAttributesInEditor();
		},
		hideAllConnections: function(){
			console.log("in Controller_UserInterface.Editor.hideAllConnections");
			$.each($("#correspondences li"), function(index, morphism){
				//alert(morphism.id);
				jsPlumb.hide(morphism.id);
			});
		},
		showAllConnections: function(){
			console.log("in Controller_UserInterface.Editor.showAllConnections");
			$.each($("#correspondences li"), function(index, morphism){
				jsPlumb.show(morphism.id);
			});
		}
	}
	
};



//right click
$(document).ready(function(){
	$('#overlay').click(function(){
		console.log("in Controller_UserInterface.#overlay");
		$('#right-click-menu').hide(100);
		$(this).hide();
	});
	
	$("#right-click-menu li").live('click', function(){
		console.log("in Controller_UserInterface.#right-click-menu li");
		$('#right-click-menu').hide(100);
		$('#overlay').hide();
	});
	
	/*$('#editor-main').live('dblclick', function(){
		alert('Refreshing attributes');
		Controller_UserInterface.Editor.callAtrubutes();
	});
	*/
	
});


/* action listners */
$(document).ready(function(){
		$('ul#dataspace-list li').live('click', function(){
			console.log("in Controller_UserInterface.ul#dataspace-list li");
			var id = $(this).attr('id');
			Controller_DataspaceManager.displayDataspace(id);
		});
		
		$('#add-dataspace').live('click', function(){
			console.log("in Controller_UserInterface.#add-dataspace");
			Controller_DataspaceManager.addNewDataspace();
		});

		$('#remove-dataspace').live('click', function(){
			console.log("in Controller_UserInterface.#remove-dataspace");
			Controller_DataspaceManager.removeDataspace();
		});	
		
		$('#exitLightbox').live('click', function(){
			console.log("in Controller_UserInterface.#exitLightbox");
			Controller_UserInterface.LightBox.closeLightbox();
		});
		
		$('#source-schemas li, #merged-schemas li').live('click', function(){
			console.log("in Controller_UserInterface.#source-schemas li, #merged-schemas li");
			$(this).toggleClass('active');	
		});
		
		$('#morphisms li').live('click', function(){
			console.log("in Controller_UserInterface.#morphisms li");
			$(this).toggleClass('active');
		});
		
		$('#queries li').live('click', function(){
			console.log("in Controller_UserInterface.#queries li");
			$(this).toggleClass('active');
		});
		
		$('#query-results li').live('click', function(){
			console.log("in Controller_UserInterface.#query-results li");
			$(this).toggleClass('active');
		});
		
		$('#addManualCorrespondence').live('click', function(e){
			console.log("in Controller_UserInterface.#addManualCorrespondence");
			 var x = e.pageX;
			 var y= e.pageY;
			 Controller_Dataspace.addCorrespondence(y);
		});
		
		/* 
		 * Operations 
		 */
		$('#dropDownRunMatch').live('click', function(){
			console.log("in Controller_UserInterface.#dropDownRunMatch");
			var operation = new Match();
			if(operation.validateArguments())
				operation.showLightBox();
		});
		
		$('#dropDownRunInferCorrespondence').live('click', function(){
			console.log("in Controller_UserInterface.#dropDownRunInferCorrespondence");
			//Controller_DataspaceOperation.runMatch();
			var operation = new Infercorrespondence();
			operation.showLightBox();
		});
		
		$('#dropDownViewGen').live('click', function(){
			console.log("in Controller_UserInterface.#dropDownViewGen");
			var operation = new ViewGen();
			operation.showLightBox();
		});	
		
		$('#dropDownRunMerge').live('click', function(){
			console.log("in Controller_UserInterface.#dropDownRunMerge");
			//Controller_DataspaceOperation.runMatch();
			var operation = new Merge();
			operation.showLightBox();
		});
		
		$('#dropDownAddDatasource, #headerBarAddDatasource').live('click', function(){
			console.log("in Controller_UserInterface.#dropDownAddDatasource, #headerBarAddDatasource");
			var operation = new AddDatasource();
			operation.displayContent();
		});
		
		$('#headerBarClearSelected').live('click', function(){
			console.log("in Controller_UserInterface.#headerBarClearSelected");
			Controller_UserInterface.clearActiveClass();

		});
		
		$('#dropDownRunQuery, #headerBarReRunQuery').live('click', function(){
			console.log("in Controller_UserInterface.#dropDownRunQuery, #headerBarReRunQuery");
			var operation = new RunQuery();
			operation.displayContent();
		});
		
		$('#saveMissingResultTuple').live('click', function(){
			console.log("in Controller_UserInterface.#addMissingResultTuple");
			var queryResultId = $('#query-results li.active:first').attr('id');
			console.log("queryResultId: " + queryResultId);
			
			var newResultTuple = Controller_UserInterface.createNewResultTuple(queryResultId);
			
			console.log("newResultTuple: " + newResultTuple);
			var newResultTupleDTO = JSON.stringify(newResultTuple);
			console.log("newResultTupleDTO: " + newResultTupleDTO);
	
			$.ajax({
				'type' : 'POST',
				'url' : 'http://localhost:8080/dstoolkit/app/dataspace/1/resultTuple', 
				'contentType': 'application/json',
				'data': newResultTupleDTO, 
				'dataType': 'json',
				'success': function(data, status){
					console.log("data, should be queryID: " + data);
					newResultTuple.setID(data);
					console.log("newResultTuple.getID(): " + newResultTuple.getID());
			}});
			
			Controller_UserInterface.LightBox.closeLightbox();
		});
		
		
		
		
		$('#addDatasource').live('click', function(){
			console.log("in Controller_UserInterface.#addDatasource");
			var name = $('table#addDatasourceTable input[name="datasourceName"]').attr('value');
			var description = $('table#addDatasourceTable input[name="description"]').attr('value');
			var driverClass = $('table#addDatasourceTable input[name="driverClass"]').attr('value');
			var connectionURL = $('table#addDatasourceTable input[name="connectionURL"]').attr('value');
			var schemaURL = $('table#addDatasourceTable input[name="schemaURL"]').attr('value');
			var username = $('table#addDatasourceTable input[name="username"]').attr('value');
			var password = $('table#addDatasourceTable input[name="password"]').attr('value');
			 
			var datasource = new Model_Datasource(name, connectionURL, schemaURL, driverClass, username, password, description);
			
			// ajax handler
			/*
			$.ajax({
				url: 'http://localhost:8080/dstoolkit/app/dataspace/1/datasource',
				statusCode: {
					404: function(){
						alert('Page not found');
					}
				},
				success: function(data, status){
					// append the datasource to the Dataspace object and display in the GUI 
				}
			});
			*/
			
			
			var datasourceDTO = JSON.stringify(datasource);
			$.ajax({
				'type' : 'POST',
				'url' : 'http://localhost:8080/dstoolkit/app/dataspace/1/datasource', 
				'contentType': 'application/json',
				'data': datasourceDTO, 
				'dataType': 'json',
				'success': function(data, status){
					//var data2 = data;
					var schema = new Model_Schema();
					schema.parseSchema(data);
					Controller_Dataspace.addNewSchema(schema);
			}});
			
			Controller_UserInterface.LightBox.closeLightbox();
		});
		
		$('#headerBarAddMissingResultTuple').live('click', function() {
			console.log("in Controller_UserInterface.#headerBarAddMissingResultTuple");
			var operation = new AddMissingResultTuple();
			operation.displayContent();
		});
		
		$('#dropDownAddQuery').live('click', function(){
			console.log("in Controller_UserInterface.#dropDownAddQuery");
			//Controller_DataspaceOperation.runMatch();
			var operation = new CreateQuery();
			operation.displayContent();
		});
		
		$('#dropDownSetAnnotatedAsExpected').live('click', function(){
			console.log("in Controller_UserInterface.#dropDownSetAnnotatedAsExpected");
			$.each($('#query-results tr.selected'), function(){
				$(this).removeClass().addClass('expected');
			});
		});
		
		$('#dropDownSetAnnotatedAsNotExpected').live('click', function(){
			console.log("in Controller_UserInterface.#dropDownSetAnnotatedAsNotExpected");
			$.each($('#query-results tr.selected'), function(){
				$(this).removeClass().addClass('not-expected');
			});
		});
		
		$('#dropDownSetAnnotatedAsStandard').live('click', function(){
			console.log("in Controller_UserInterface.#dropDownSetAnnotatedAsStandard");
			$.each($('#query-results tr.selected'), function(){
				$(this).removeClass();
			});
		});
		
		$('#dropDownRunViewStructure').live('click', function(){
			console.log("in Controller_UserInterface.#dropDownRunViewStructure");
			//Controller_DataspaceOperation.runMatch();
			//TODO: sort this for merged schemas too
			var $schemaID = $('#schemas li.active:first').attr('id');
			var dataspace = Controller_DataspaceManager.getActiveDataspace();
			var schema = dataspace.getSchema($schemaID);
			
			var operation = new ViewStructure(schema);
			operation.displayStructure();
		});
		
		$('#dropDownShowResultSet').live('click', function(){
			console.log("in Controller_UserInterface.#dropDownShowResultSet");
			// show result set
			var queryResultId = $('#query-results li.active:first').attr('id');
			console.log("queryResultId: " + queryResultId);
			var dataspace = Controller_DataspaceManager.getActiveDataspace();
			var queryResult = dataspace.getQueryResult(queryResultId);
			Controller_UserInterface.showResultSet(queryResult);
		});
		
		$('#dropDownShowMorphism, #dropDownShowMappingsForQueryResult, #headerBarShowMappings').live('click', function() {
			Controller_Dataspace.displayMorphismInEditor();
		});
		
		/*
		 * Invoke operations
		 */
		$('div[operation="match"]').live('click', function(){
			console.log("in Controller_UserInterface.div[operation=match]");
			
			var selectedSchemas = Controller_Dataspace.getTheTwoSelectedSchemas();
			var newName = 'Match '+selectedSchemas.schema1.name +' - '+selectedSchemas.schema2.name;
			
			// add correspondences returned from merge
			var tempMorphism = MorphismFactory.morphismSetFromJSON(morphism2);
			tempMorphism.name = newName;
			Controller_Dataspace.addNewMorphism(tempMorphism);
			
			Controller_UserInterface.LightBox.closeLightbox();
			
			//$('#morphisms').append('<li class="match">Match S1-S2</li>');
			Controller_UserInterface.LightBox.closeLightbox();
		});
		
		$('div[operation="infercorrespondence"]').live('click', function(){
			console.log("in Controller_UserInterface.div[operation=infercorrespondence]");

			var selectedSchemas = Controller_Dataspace.getTheTwoSelectedSchemas();
			var newName = 'Corresp '+selectedSchemas.schema1.name +' - '+selectedSchemas.schema2.name;
			
			// add correspondences returned from merge
			var tempMorphism = MorphismFactory.morphismSetFromJSON(morphism1);
			tempMorphism.name = newName;
			Controller_Dataspace.addNewMorphism(tempMorphism);
			
			//$('#morphisms').append('<li class="schematicCorrespondence">'+newName+'</li>');
			Controller_UserInterface.LightBox.closeLightbox();
		});
		
		$('div[operation="viewgen"]').live('click', function(){
			console.log("in Controller_UserInterface.div[operation=viewgen]");
			
			var selectedSchemas = Controller_Dataspace.getTheTwoSelectedSchemas();
			var newName = 'Mappings '+selectedSchemas.schema1.name +' - '+selectedSchemas.schema2.name;
			
			// add mapping returned from merge
			var tempMorphism = MorphismFactory.morphismSetFromJSON(morphism5);
			tempMorphism.name = newName;
			Controller_Dataspace.addNewMorphism(tempMorphism);
			
			//$('#morphisms').append('<li class="schematicCorrespondence">'+newName+'</li>');
			Controller_UserInterface.LightBox.closeLightbox();
		});

		$('div[operation="merge"]').live('click', function(){
			console.log("in Controller_UserInterface.div[operation=merge]");
			
			var newName = $('#newMergeName').val();
			if(newName == '')
				newName = 'New merged schema';

			var selectedSchemas = Controller_Dataspace.getTheTwoSelectedSchemas();
			
			// add correspondences returned from merge
			var tempMorphism = MorphismFactory.morphismSetFromJSON(morphism1);
			tempMorphism.name = newName+'-'+selectedSchemas.schema1.name;
			Controller_Dataspace.addNewMorphism(tempMorphism);

			var tempMorphism = MorphismFactory.morphismSetFromJSON(morphism1);
			tempMorphism.name = newName+'-'+selectedSchemas.schema2.name;
			Controller_Dataspace.addNewMorphism(tempMorphism);
			
			Controller_UserInterface.clearActiveClass();
			
			// add merged schema returned from merge
			$('#merged-schemas').append('<li class="schema active">'+newName+'</li>');
			
			
			Controller_UserInterface.LightBox.closeLightbox();
		});

		$('div[operation="addQuery"]').live('click', function(){
			console.log("in Controller_UserInterface.div[operation=addQuery]");
			var schemaID = $('#schemas li.active:first').attr('id');
			console.log("schemaID: " + schemaID);
			var queryName = $('table#addQueryTable input[name="queryName"]').attr('value');
			console.log("queryName: " + queryName);
			var queryDescription = $('table#addQueryTable input[name="queryDescription"]').attr('value');
			console.log("queryDescription: " + queryDescription);
			var queryString = $('table#addQueryTable textarea[id="addQueryTextarea"]').attr('value');
			console.log("queryString: " + queryString);
			
			var query = new Model_Query(queryName, queryString, queryDescription, schemaID);
			
			var queryDTO = JSON.stringify(query);
			$.ajax({
				'type' : 'POST',
				'url' : 'http://localhost:8080/dstoolkit/app/dataspace/1/query', 
				'contentType': 'application/json',
				'data': queryDTO, 
				'dataType': 'json',
				'success': function(data, status){
					console.log("data, should be queryID: " + data);
					query.setID(data);
					console.log("query.getID(): " + query.getID());
					Controller_Dataspace.addNewQuery(query);
			}});
			
			//$('#queries').append('<li class="query">'+queryName+'</li>');
			Controller_UserInterface.LightBox.closeLightbox();
		});
		
		$('div[operation="runQuery"]').live('click', function(){
			console.log("in Controller_UserInterface.div[operation=runQuery]");
			var queryID = $('#queries li.active:first').attr('id');
			console.log("queryID: " + queryID);
			var dataspace = Controller_DataspaceManager.getActiveDataspace();
			if (queryID == undefined){
				console.log("undefined queryID");
				var queryResultID = $('#query-results li.active:first').attr('id');
				console.log("queryResultID: " + queryResultID);
				queryResult = dataspace.getQueryResult(queryResultID);
				queryID = queryResult.queryId;
				console.log("queryID: " + queryID);
			}
			var	query = dataspace.getQuery(queryID);
			console.log("query: " + query);
			var queryName = query.queryName;
			console.log("queryName: " + queryName);
			var queryString = query.queryString;
			console.log("queryString: " + queryString);
			var schemaId = query.schemaId;
			console.log("schemaId: " + schemaId);
			var schemaName = dataspace.getSchema(schemaId).name;
			console.log("schemaName: " + schemaName);
			var name = $('table#runQueryTable input[name="newQueryResultName"]').attr('value');
			console.log("name: " + name);
			var precisionOrRecall = $('table#runQueryTable select#precisionRecallSelector').val();
			console.log("precisionOrRecall: " + precisionOrRecall);
			var precisionOrRecallValue = $('table#runQueryTable input[name="accuracy-measure"]').attr('value');
			console.log('precisionOrRecallValue: ' + precisionOrRecallValue);
			var schemaIds = [];
			var schemaNames = [];
			$('table#runQueryTable select#datasourceSelector :selected').each(function(i, selected) {
				schemaIds[i] = $(selected).val();
				console.log("schemaIds[i]: " + schemaIds[i]);
				schemaNames[i] = $(selected).text();
				console.log("schemaNames[i]: " + schemaNames[i]);
			});
			console.log("schemaIds: " + schemaIds);
			console.log("schemaNames: " + schemaNames);
			
			var evaluateQueryParameters = new Model_EvaluateQueryParameters(name, queryID, queryName, queryString, schemaName, schemaId, precisionOrRecall, precisionOrRecallValue, schemaNames)
			console.log("evaluateQueryParameters: " + evaluateQueryParameters);
			//var newName = $('#newQueryResultName').val();
			//if(newName == '')
			//	newName = 'New QueryResult';
			
			var evaluateQueryParametersDTO = JSON.stringify(evaluateQueryParameters);
			console.log("evaluateQueryParametersDTO: " + evaluateQueryParametersDTO);
			$.ajax({
				'type' : 'POST',
				'url' : 'http://localhost:8080/dstoolkit/app/dataspace/1/query/'+queryID, 
				'contentType': 'application/json',
				'data': evaluateQueryParametersDTO, 
				'dataType': 'json',
				'success': function(data, status){
				//var data2 = data;
				var completeQueryResult = new Model_QueryResult();
				completeQueryResult.parseQueryResult(data);
				Controller_Dataspace.addNewQueryResult(completeQueryResult);
			}});
			
			//$('#query-results').append('<li class="query-result">'+name+'</li>');

			Controller_UserInterface.LightBox.closeLightbox();
			//Controller_UserInterface.showResultSet();
		
		});
		
		
		

		
		// make datasources draggable
		$("#schemas li").live('mouseover', function(){
			console.log("in Controller_UserInterface.#schemas li");
			//$("#dataspace-single-datasources li").draggable();
		});
		
		// make .window draggable
		$("a.toggleHide").live('click', function(){
			console.log("in Controller_UserInterface.a.toggleHide");
			jsPlumb.toggle($(this).attr("rel"));
		});	
		
		$('#overlay').click(function(){
			console.log("in Controller_UserInterface.#overlay");
			$('#right-click-menu').hide(100);
			$(this).hide();
		});
		
		$("#right-click-menu li").live('click', function(){
			console.log("in Controller_UserInterface.#right-click-menu li");
			$('#right-click-menu').hide(100);
			$('#overlay').hide();
		});

		$('#manageParameters li#add-parameter').click(function(){
			console.log("in Controller_UserInterface.#manageParameters li#add-parameter");
			var $row = $('table#operationParameters tr:last').html();
			$row = '<tr>'+$row+'</tr>';
			$('table#operationParameters tbody').append($row);
		});
		
		$('#manageParameters li#remove-parameter').click(function(){
			console.log("in Controller_UserInterface.#manageParameters li#remove-parameter");
			$('#operationParameters tr.active').remove();
		});
		
		$('#operationParameters tr td.title').live('click', function(e){
			console.log("in Controller_UserInterface.#operationParameters tr td.title");
			$(this).parent().toggleClass('active');
		});
		
		
		//disable right click on the page
		$(document).bind("contextmenu", function(e) {
			console.log("in Controller_UserInterface.contextmenu");
			right_clicked_element = $(e.target);
	
			if( right_clicked_element.attr('id') == "workspace" ){
				console.log("workspace");
				//showMenu('#workspace-menu', e);
				Controller_UserInterface.showMainContainerMenu(e);
			}
			else if( right_clicked_element.hasClass('schema') && right_clicked_element.hasClass('active') ){
				/*
				if($('#schemas li.active').length > 1 && right_clicked_element.hasClass('active')){
					console.log("multipe schemas selected");
					showMenu('#multiple-source-menu', e);
				}
				else if( right_clicked_element.hasClass('combined') ) {// show single source menu 
					console.log("combined schemas selected");
					showMenu('#combined-source-menu', e);
				}
				else {
				*/
				//TODO: check how many and what elements are selected 
				console.log("schema menu");
				Controller_UserInterface.showSchemaMenu(e);  //showMenu('#data-source-menu', e); 
				//	}
			}
			else if( right_clicked_element.hasClass('morphismElement') && right_clicked_element.hasClass('active') ){
					Controller_UserInterface.showCorrespondenceMenu(e);
			}
			else if( right_clicked_element.hasClass('query') && right_clicked_element.hasClass('active') ){
				Controller_UserInterface.showQueryMenu(e);
			}
			else if( right_clicked_element.hasClass('query-result') && right_clicked_element.hasClass('active') ){
				Controller_UserInterface.showQueryResultMenu(e);
			}
			else if( right_clicked_element.parents('table').attr('id') == 'query-results' ) {
				Controller_UserInterface.showAnnotateMenu(e);
			}
			else if( right_clicked_element.attr('id') == "editor-main" ){
				Controller_UserInterface.showManualDefineCorrespondenceMenu(e);
			}
			else if( right_clicked_element.attr('id') == "main-container" || right_clicked_element.attr('id') == "schemas" ){
				Controller_UserInterface.showMainContainerMenu(e);
			}
	
			//$('#workspace').mousedown(function(e){
				//	hideMenu();
		//	});
			return false;
		});
		
		/*
		 * keydown listeners
		 */
		$(document).bind('keydown', 'ctrl+n', function(){
			console.log("in Controller_UserInterface.keydown ctrl+n");
			Controller_DataspaceManager.addNewDataspace();
			}			
		);
		
		$(document).bind('keydown', 'esc', function(){
			console.log("in Controller_UserInterface.keydown esc");
			Controller_UserInterface.LightBox.closeLightbox();
		}			
		);
		
		$('#query-results tr').live('click', function(){
			console.log("in Controller_UserInterface.#query-results tr");
			$(this).toggleClass('selected');
		});
		
}); // document.ready()
