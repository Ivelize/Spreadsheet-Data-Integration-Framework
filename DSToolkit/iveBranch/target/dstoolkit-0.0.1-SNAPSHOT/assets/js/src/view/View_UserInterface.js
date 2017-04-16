/**
 * 
 */

var View_UserInterface = {
		// show schema right click menu
		showSchemaMenu: function (dsStatus){
			console.log("in View_UserInterface.schemaSchemaMenu");
			var html = '<li onclick="Controller_UserInterface.renameElement();">Rename</li><hr/>'
			+ '<li id="dropDownRunMatch">Match</li>'
			+ '<li id="dropDownRunInferCorrespondence">InferCorrespondence</li>'
			+ '<li id="dropDownRunMerge">Merge</li>'
			+ '<li id="dropDownViewGen">ViewGen</li>'
			//html += '<li onclick="Controller_Dataspace.runViewGen();">ViewGen</li>';
			//html += '<li onclick="Controller_Dataspace.runAnswerQuery();">AnswerQuery</li>';
			+ '<hr /><li onclick="Controller_Dataspace.displayManualCorrespondenceEditor();">Manually Define Correspondences</li>'
			+ '<li id="dropDownRunViewStructure">View Schema structure</li>'
			//html += '<li onclick="Controller_Dataspace.displayManualCorrespondenceEditor();">View populated schema</li>';
			+ '<hr /><li id="dropDownAddQuery">Add query</li>';
			return html;
		},
		//TODO: this doesn't look right - sort this
		/*
		showMergedSchemaMenu: function(){
			var html = '<li onclick="Controller_Dataspace.runMatch();">Match</li>'
			+'<li onclick="Controller_Dataspace.runInferCorrespondence();">InferCorrespondence</li>'
			+'<li onclick="Controller_Dataspace.runMerge();">Merge</li>'
			+'<li onclick="Controller_Dataspace.runViewGen();">ViewGen</li>'
			+'<li onclick="Controller_Dataspace.runAnswerQuery();">AnswerQuery</li>'
			+'<li onclick="Controller_Dataspace.runAnswerQuery();">Annotate</li>'
			+'<hr /><li onclick="Controller_Dataspace.displayManualCorrespondenceEditor();">Manually Define Correspondences</li>'
			+'<hr /><li onclick="Controller_Dataspace.displayManualCorrespondenceEditor();">Run Query</li>'
			return html;
		},
		*/
		showManualDefineCorrespondenceMenu: function(){
			console.log('in View_UserInterface.showManualDefineCorrespondenceMenu');
			var html = '<li class="schema" id="addManualCorrespondence">Same name, same concept</li>'
			+'<li class="schema">Different name, same concept</li>'
			+'<li class="schema">Missing construct</li>'
			+'<li class="shema">Horizonal partitioning</li>'
			+ '<li class="data-source">Vertical partitioning</li>';
			return html;
		},
		showMainContainerMenu: function(){
			console.log("in View_UserInterface.showMainContainerMenu");
			var html = '<li id="dropDownAddDatasource">Add Datasource</li>'
			+'<li>something</li><li class="data-source">something</li>';
			return html;
		},
		showCorrespondenceMenu: function(){
			console.log("in View_UserInterface.showCorrespondenceMenu");
			var html = '<li onclick="Controller_UserInterface.renameElement();">Rename</li><hr/>'
			+ '<li id="dropDownShowMorphism">Show</li>';
			//html += '<li id="dropDownRunInferCorrespondence">InferCorrespondence</li>';

			return html;
		},
		showQueryMenu: function(){
			console.log("in View_UserInterface.showQueryMenu");
			var html = '<li onclick="Controller_UserInterface.renameElement();">Rename</li><hr/>'
			+ '<li id="dropDownRunQuery">Run Query</li>';
			return html;
		},
		showQueryResultMenu: function(){
			console.log("in View_UserInterface.showQueryResultMenu");
			var html = '<li onclick="Controller_UserInterface.renameElement();">Rename</li><hr/>'
			+ '<li id="dropDownShowResultSet">Show ResultSet</li>'
			+ '<li id="dropDownShowMappingsForQueryResult">Show Mappings</li>';
			return html;
		},
		showAnnotateMenu: function(){
			console.log("in View_UserInterface.showAnnotateMenu");
			var html = '<li id="dropDownSetAnnotatedAsExpected">Mark As Expected</li>'
			+ '<li id="dropDownSetAnnotatedAsNotExpected">Mark As Unexpected</li><hr />'
			+ '<li id="dropDownSetAnnotatedAsStandard">Remove Annotation</li>';
			return html;
		}
	};