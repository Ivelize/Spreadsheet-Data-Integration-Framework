/**
 * 
 */

var View_DataspaceManager = {
		
		// add a dataspace to the view
		addDataspace: function (dataspace){
			console.log("in View_DataspaceManager.addDataspace");
			var html = '<li id="'+dataspace.id+'">'
			+ '<div class="dataspace-item-info">'+dataspace.name+'</div>'
			+ '</li>';
			$('#dataspace-list').append(html);
		},
		
		// make a new dataspace active in view
		mainView: function (dataspace){
			console.log("in View_DataspaceManager.mainView");
			// '<h2>'+dataspace.name+'</h2>';
			var html = ''
			+ '<h2>Schemas</h2>'
			+ '<ul id="schemas" >'
			+ '<ul id="source-schemas" >';
			$.each(dataspace.schemaIndex, function(index, schema){
				html += View_Dataspace.addSchema(schema);
			});
			html += '</ul>'
			+ '<ul id="merged-schemas"></ul>'
			+ '</ul>'
			//+ '<h2>Morphisms</h2><ul id="morphisms"></ul>'
			+ '<h2>Queries</h2><ul id="queries"></ul>'
			+ '<h2>Query Results</h2><ul id="query-results"></ul>';
				
			return html;
		}
	};