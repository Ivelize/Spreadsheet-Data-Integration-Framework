/**
 * 
 */

var View_DataspaceOperation = {
		// add a datasource to the view
		matchParameters: function(){
			console.log("in View_DataspaceOperation.matchParameters");
			var dropdown = View_DataspaceOperation.getMatchParameters();
			var html = '<tr>'
			+ '<td class="title">Matching level</td>'
			+ '<td>'
			+ '<select>'
			+ '<option>Schema</option>'
			+ '<option>Instance</option>'
			+ '<option>Both</option>'
			+ '</select>'
			+ '</td>'
			+ '</tr>'
			
			+ '<tr>'
			+ '<td class="title" style="valign:top;">Type of match</td>'
			+ '<td>'
			+ '<select size="5" multiple="true">'
			+ '<option>String based</option>'
			+ '<option>Language based</option>'
			+ '<option>Constraint based</option>'
			+ '</select>'
			+ '</td>'
			+ '</tr>'
				
			+ '<tr>'
			+ '<td class="title">Operation Parameter</td>'
			+ '<td>'
			+ '<select>'
			+ '<option>Parameter 1</option>'
			+ '<option>Parameter 2</option>'
			+ '<option>Parameter 3</option>'
			+ '</select>'
			+ '</td>'
			+ '<td>'+dropdown+'</td>'
			+ '<td>'
			+ '<input type="text" />'
			+ '</td>'
			+ '</tr>';
			return html;
		},
		inferCorrespondenceParameters: function(){
			console.log("in View_DataspaceOperation.inferCorrespondenceParameters");
			var dropdown = View_DataspaceOperation.getMatchParameters();
			var html = '<tr>'
			+ '<td class="title">Match Threshold</td>'
			+ '<td>'
			+ '<input type="text" value="0.0">'
			+ '</td>'
			+ '</tr>' 
			
			+ '<tr>'
			+ '<td class="title">Operation Parameter</td>'
			+ '<td>'
			+ '<select>'
			+ '<option>Parameter 1</option>'
			+ '<option>Parameter 2</option>'
			+ '<option>Parameter 3</option>'
			+ '</select>'
			+ '</td>'
			+ '<td>'+dropdown+'</td>'
			+ '<td>'
			+ '<input type="text" />'
			+ '</td>'
			+ '</tr>';
			return html;
		},
		mergeParameters: function(){
			console.log("in View_DataspaceOperation.mergeParameters");
			var dropdown = View_DataspaceOperation.getMatchParameters();
			var html = '<tr>'
			+ '<td class="title">Name</td>'
			+ '<td>'
			+ '<input type="text" id="newMergeName"</td>'
			+ '</tr>'
			
			+ '<tr>'
			+ '<td class="title">Kind</td>'
			+ '<td><select>'
			+ '<option>Union schema</option>'
			+ '<option>Merged schema</option>'
			+ '</select>'
			+ '</td>'
			+ '</tr>'
			
			+ '<tr>'
			+ '<td class="title">Operation Parameter</td>'
			+ '<td>'
			+ '<select>'
			+ '<option>Parameter 1</option>'
			+ '<option>Parameter 2</option>'
			+ '<option>Parameter 3</option>'
			+ '</select>'
			+ '</td>'
			+ '<td>'+dropdown+'</td>'
			+ '<td>'
			+ '<input type="text" />'
			+ '</td>'
			+ '</tr>';
			return html;
		},
		//TODO: add the datasource selector here for the direction
		viewGenParameters: function(){
			console.log("in View_DataspaceOperation.viewGenParameters");
			var dropdown = View_DataspaceOperation.getMatchParameters();
			var html = '<tr>'
			+ '<td class="title">Direction</td>'
			+ '<td>Mondial_1_ds</td>'
			+ '<td>'
			+ '<select>'
			+ '<option>></option>'
			+ '<option><</option>'
			+ '</select>'
			+ '</td>'
			+ '<td> Mondial_2_ds</td>'
			+ '</tr>'
			
			+ '<tr>'
			+ '<td class="title">Operation Parameter</td>'
			+ '<td>'
			+ '<select>'
			+ '<option>Parameter 1</option>'
			+ '<option>Parameter 2</option>'
			+ '<option>Parameter 3</option>'
			+ '</select>'
			+ '</td>'
			+ '<td>'+dropdown+'</td>'
			+ '<td>'
			+ '<input type="text" />'
			+ '</td>'
			+ '</tr>';
			return html;
		},
		getMatchParameters: function(){
			console.log("in View_DataspaceOperation.getMatchParameters");
			var html = '<select>'
			+ '<option><=</option>'
			+ '<option>==</option>'
			+ '<option>>=</option>'
			+ '</select>';
			return html;
		},
		viewStructure: function(schema){
			//TODO: deal with nested entities, e.g., in XML
			console.log("in View_DataspaceOperation.viewStructure");
			var html = '';
			$.each(schema.entities, function(index, entity){
				html += '<ul class="viewStructure">'
				+ '<li id="'+entity.name+'" class="entityName">'+entity.name+'</li>';
				$.each(entity.attributes, function(index, attribute){
					html += '<li id="'+attribute.name+'" class="">'+attribute.name+' - '+attribute.dataType+'</li>';
				});
				html +='</ul>';
			});
			html += '';
			
			return html;
		},
		addMissingResultTuple: function() {
			console.log("in View_DataspaceOperation.addMissingResultTuple");
			var html = '<table id="addMissingResultTuple">';
			var dataspace = Controller_DataspaceManager.getActiveDataspace();
			var queryResultId = $('#query-results li.active:first').attr('id');
			console.log("queryResultId: " + queryResultId);
			var newResultSet = dataspace.getQueryResult(queryResultId);
			$.each(newResultSet.resultSetColumnNames, function(index, value){
				if (value != 'id') {
				html += '<tr>';
				html += '<td>'+value+'</td>';
				html += '<td>';
				html += '<input type="text" name = "'+value+'" size="50" />';
				html += '</td>'
				}
			});
			html += '</tr>';
			
			html += '<tr>'
			+ '<td></td>'
			+ '<td>'
			+ '<div id="saveMissingResultTuple" class="next-step-button button" style="float:right;">Save</div>'
			+ '</td>'
			+ '</tr>'
			+ '</table>';
			
			console.log("html: " + html);
			
			return html;
		},
		addDatasource: function(){
			console.log("in View_DataspaceOperation.addDatasource");
			var html = '<table id="addDatasourceTable">'
			+ '<tr>'
			+ '<td>Name</td>'
			+ '<td>'
			+ '<input type="text" name = "datasourceName" size="50" />'
			+ '</td>'
			+ '</tr>'
			
			+ '<tr>'
			+ '<td>Description</td>'
			+ '<td>'
			+ '<input type="text" name = "description" size="50" />'
			+ '</td>'
			+ '</tr>'
			
			+ '<tr>'
			+ '<td>Driver Class</td>'
			+ '<td>'
			+ '<input type="text" name = "driverClass" size="50" />'
			+ '</td>'
			+ '</tr>'
			
			+ '<tr>'
			+ '<td>Connection URL</td>'
			+ '<td>'
			+ '<input type="text" name="connectionURL" size="50" />'
			+ '</td>'
			+ '</tr>'
			
			+ '<tr>'
			+ '<td>Schema URL</td>'
			+ '<td>'
			+ '<input type="text" name="schemaURL" size="50" />'
			+ '</td>'
			+ '</tr>'
			
			+ '<tr>'
			+ '<td>Username</td>'
			+ '<td>'
			+ '<input type="text" name = "username" size="50" />'
			+ '</td>'
			+ '</tr>'
			
			+ '<tr>'
			+ '<td>Password</td>'
			+ '<td>'
			+ '<input type="password" name = "password" size="50" />'
			+ '</td>'
			+ '</tr>'
			
			+ '<tr>'
			+ '<td></td>'
			+ '<td>'
			+ '<div id="addDatasource" class="next-step-button button" style="float:right;">Connect</div>'
			+ '</td>'
			+ '</tr>'
			
			+ '</table>';
			
			return html;
		},
		addQuery: function(){
			console.log("in View_DataspaceOperation.addQuery");
			var html = '<table id="addQueryTable">'
			+ '<tr>'
			+ '<td>Name</td>'
			+ '<td>'
			+ '<input type="text" name="queryName" size="50" />'
			+ '</td>'
			+ '</tr>'
			
			+ '<tr>'
			+ '<td>Description</td>'
			+ '<td>'
			+ '<input type="text" name="queryDescription" size="50" />'
			+ '</td>'
			+ '</tr>'
			
			+ '<tr>'
			+ '<td style="vertical-align: top;">Query</td>'
			+ '<td>'
			+ '<textarea rows="15" cols="85" id="addQueryTextarea">SELECT * FROM </textarea>'
			+ '</td>'
			+ '</tr>'
			
			+ '<tr>'
			+ '<td></td>'
			+ '<td>'
			+ '<div id="addQuery" operation="addQuery" class="next-step-button button">Create Query</div>'
			+ '</td>'
			+ '</tr>'
			
			+ '</table>';
			
			return html;
		},
		runQuery: function(dataspaceList){
			console.log("in View_DataspaceOperation.runQuery");
			var html = '<table id="runQueryTable">'
			+ '<tr>'
			+ '<td>Name</td>'
			+ '<td>'
			+ '<input type="text" name="newQueryResultName" size="50" />'
			+ '</td>'
			
			+ '<tr>'
			+ '<td>Accuracy</td>'
			+ '<td>'
			
			+ '<select id="precisionRecallSelector">'
			+ '<option value="notSelected">Not set</option>'
			+ '<option value="precision">Precision</option>'
			+ '<option value="recall">Recall</option>'
			+ '</select> &nbsp; '
			+ '<input type="text" name="accuracy-measure" value="0.0" />'
			
			+'</td>'
			+ '</tr>'
			
			+ '<tr>'
			+ '<td>Datasources</td>'
			+ '<td>'
			+ '<select multiple size=5 id="datasourceSelector">';
			
				$.each(dataspaceList, function(index, schema){
					html+= '<option value="'+schema.id+'">'+ schema.name +'</option>';
				});
				
			html += '</select>'
			+ '</td>'
			+ '</tr>'
			
			+ '<tr>'
			+ '<td>'
			+ '</td>'
			+ '<td>'
			+ '<div id="runQuery" operation="runQuery" class="next-step-button button">Run Query</div>'
			+ '</td>'
			+ '</tr>'
			+ '</table>';
			
			return html;
		}
}; 