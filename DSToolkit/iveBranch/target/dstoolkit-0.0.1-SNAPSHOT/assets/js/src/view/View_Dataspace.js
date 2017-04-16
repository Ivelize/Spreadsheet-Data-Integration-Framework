/**
 * @author Ian Arundale
 */

var View_Dataspace = {
    // add a schema to the view
    addSchema: function(schema) {
    	console.log("in View_Dataspace.addSchema");
        var html = '<li id="' + schema.id + '" class="schema">' + schema.name + '</li>';
        return html;
    },

    addMorphismToWorkspace: function(morphismSet) {
    	console.log("in View_Dataspace.addMorphismToWorkspace");
    	var html = '<li id="' + morphismSet.id + '" class="'+morphismSet.type+' morphismElement">' + morphismSet.name + '</li>';
    	return html;
    },

    displayConstructSets: function(constructSet1, constructSet2) {
    	console.log("in View_Dataspace.displayConstructSets");
    	var dataspace = Controller_DataspaceManager.getActiveDataspace();
        var html = '';
        $.each(constructSet1,
        function(index, schemaId) {
        	console.log("schemaId: " + schemaId);
        	var schema = dataspace.getSchema(schemaId);
        	console.log("schema: " + schema);
            $.each(schema.entities,
            function(index, entity) {
                html += '<ul class="">'
                + '<li id="'+entity.id+'" class="window entityName" constructset="1">'+schema.name +'<br /><b>'+entity.name+'</b></li>';
                $.each(entity.attributes,
                function(index, attribute) {
                    html += '<li id="' + attribute.id + '" class="window" datasource="' + schema.name + '" attributename="' + attribute.name + '" entityName=' + entity.name + ' constructset="1">'
                    + schema.name + '<br /><b>' + entity.name + '.' + attribute.name + '</b>'
                    + '</li>';
                });
                html += '</ul>';
            });
        });

        var contructSet1 = html;

        html = '';
        $.each(constructSet2,
        function(index, schemaId) {
        	console.log("schemaId: " + schemaId);
        	var schema = dataspace.getSchema(schemaId);
        	console.log("schema: " + schema);
            $.each(schema.entities,
            function(index, entity) {
                html += '<ul class="">'
                + '<li id="'+entity.id+'" class="window entityName" constructset="2">'+schema.name +'<br /><b>'+entity.name+'</b></li>';
                $.each(entity.attributes,
                function(index, attribute) {
                    html += '<li id="' + attribute.id + '" class="window" datasource="' + schema.name + '" attributename="' + attribute.name + '" entityName=' + entity.name + ' constructset="2">'
                    + schema.name + '<br /><b>' + entity.name + '.' + attribute.name + '</b>';
                    + '</li>';
                });
                html += '</ul>';
            });
        });

        var contructSet2 = html;
        var constructHtml = {
            constructSet1Html: contructSet1,
            constructSet2Html: contructSet2
        };

        return constructHtml;
    },

    addMorphism: function(morphism, type) {
    	console.log("in View_Dataspace.addMorphism");
        var html = '<li id="' + morphism.id + '" class="morphism window" type="'+type+'"';
        if(morphism.topHeight != "undefined"){
        	// make up the difference for the header
        	actualHeight = morphism.topHeight-86;
        	html+= 'style="top:'+actualHeight +'px;"';
        }
        html += '>';
        if(type == 'match')
        	html += morphism.score;
        else if(type == 'schematicCorrespondence')
        	html += morphism.correspondenceType + '<br />'+morphism.shortname;
        else if(type == 'mapping')
        	html += morphism.query1String + '<hr />'+morphism.query2String;
        	
        html += '<br />'
        + '<a rel="'+morphism.id+'" class="toggleHide">Toggle connections</a>'
        + '</li>';
        return html;
    },


    /*
		 * Plan to convert this function into a factory to handle the multiple variations
		 */
    runMatch: function() {
    	console.log("in View_Dataspace.runMatch");
        var html = '<div>';
    },
    
    addQuery: function(query){
    	console.log("in View_Dataspace.addQuery");
    	var html = '<li id="' + query.id + '" class="query">'+query.queryName+'</li>';
        return html;
    },
    
    addQueryResult: function(queryResult){
    	console.log("in View_Dataspace.addQueryResult");
    	var html = '<li id="' + queryResult.id + '"class="query-result">'+queryResult.name+'</li>';
    	console.log("html: " + html);
        return html;
    }
};