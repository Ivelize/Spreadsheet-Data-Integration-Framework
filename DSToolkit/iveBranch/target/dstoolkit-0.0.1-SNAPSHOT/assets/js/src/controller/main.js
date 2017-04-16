/**
 * @author Ian
 * @description	The script that starts the entire process
 */



$(document).ready(function(){	
	
	$('body').hide().fadeIn(1000);

	/*
	 *  Functions for correspondence drag and drop
	 */
	
	// chrome fix.
	document.onselectstart = function () { return false; };

	jsPlumb.Defaults.DragOptions = { cursor: 'pointer', zIndex:2000 };
				jsPlumb.Defaults.PaintStyle = { strokeStyle:'#666' };
				jsPlumb.Defaults.EndpointStyle = { width:20, height:16, strokeStyle:'#666' };
				jsPlumb.Defaults.Endpoint = new jsPlumb.Endpoints.Rectangle();
				jsPlumb.Defaults.Anchors = ["TopCenter", "TopCenter"];
				jsPlumb.setDraggableByDefault(true);
				jsPlumb.Defaults.DragOptions = { cursor: 'pointer', zIndex:2000 };
				jsPlumb.Defaults.connectorStyle = {
	                strokeStyle: 'black',
	                lineWidth: 4
	            };

				isDrawingExistingMorphism = false;
				
				 addConnection = function() {
										
					var connections = jsPlumb.getConnections();
					var foundSome = false;
					
					//get temp morphismContainer
					var tempMorphismSet = Controller_DataspaceManager.getActiveDataspace().tempMorphismSet;
					
					// Reconstruct the Morphisms
					try{
						for (var connection in connections) {
							var l = connections[connection];
							if (l && l.length > 0) {
								foundSome = true;
								for (var j = 0; j < l.length; j++) {
									
									//alert(l[j].toSource());
									
									// source element
									var $droppedAttribute = $('#'+l[j].sourceId);
									
									// target element
									var $droppedOnMorphismID = l[j].targetId;
									
									//get the constructSet the dropped attribute belongs to
									var droppedAttributeID = $droppedAttribute.attr('id');
									var droppedAttributeConstructSetNo = $droppedAttribute.attr('constructset');
									
									//get the tempMorphismSet
									var tempMorphismSet = Controller_DataspaceManager.getActiveDataspace().tempMorphismSet;
								
									// try and get existing morphism
									var morphism = tempMorphismSet.getMorphism($droppedOnMorphismID);
									
									// check if the element already exists in this morphisms constructSet
									var constructExistsInMorphism = morphism.checkIsConstructInSet(droppedAttributeConstructSetNo, droppedAttributeID);

									// enter newly created relations into the morphism
									if(!constructExistsInMorphism){
										//correspondence = new Model_SchematicCorrepondence('SNSC');
										morphism.addElementToConstructSet(droppedAttributeConstructSetNo, droppedAttributeID);
										//alert(morphism.toSource());
									}
								}
							}
						}
					}
					catch(err){ 
						alert('caught exception '+err); 
					}
					
					console.log(tempMorphismSet);
					return true;
				};

				// connection listener
				jsPlumb.addListener(["jsPlumbConnection","jsPlumbConnectionDetached"], {
					jsPlumbConnection : function(p) {
						if(!isDrawingExistingMorphism)
							addConnection(); 
					},
					jsPlumbConnectionDetached : function(p) { 
						//showConnections(); 
					}
				});
});