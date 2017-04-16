/*
 * @test Dataspace test
 */

DataspaceTest = TestCase("DataspaceTest");

DataspaceTest.prototype.testCreateDataspace = function() {
	var dataspace = new Model_Dataspace();
	assertObject("Dataspace object expected", dataspace);
};

DataspaceTest.prototype.testMorphism = function(){
	Controller_DataspaceManager.resetUniqueID();
	var morphism = new Morphism();
	morphism = new Morphism();
	morphism = new Morphism();
	assertEquals(3, morphism.id); 
};

DataspaceTest.prototype.testCorrespondenceInheritence = function(){
	Controller_DataspaceManager.resetUniqueID();
	var corr = new Model_SchematicCorrepondence();
	assertEquals(1, corr.id);
};

DataspaceTest.prototype.testResetUniqueID = function(){
	var test = Controller_DataspaceManager.resetUniqueID();
	assertTrue(test); 
};

DataspaceTest.prototype.testMorphismContainer = function(){
	var morphismContainer = new Model_MorphismSet();
	var morphism = new Morphism();
	assertTrue(morphismContainer.addMorphism(morphism));
};

DataspaceTest.prototype.testGetMorphismFromContainer = function(){
	var morphismContainer = new Model_MorphismSet();
	var morphism = new Morphism();
	var morphismID = morphism.id;
	morphismContainer.addMorphism(morphism);
	
	var returnedMorphism = morphismContainer.getMorphism(morphismID);

	assertEquals(morphismID, returnedMorphism.id);
};

DataspaceTest.prototype.testGetMorphismTestReturnValue = function(){
	var morphismContainer = new Model_MorphismSet();
	var morphism = new Morphism();
	var morphismID = morphism.id;
	morphismContainer.addMorphism(morphism);
	
	var returnedMorphism = morphismContainer.getMorphism(morphismID);

	assertTypeOf('object', returnedMorphism);
};


DataspaceTest.prototype.testGetMorphismTestReturnValueUndefined = function(){
	var morphismContainer = new Model_MorphismSet();
	var morphism = new Morphism();
	var morphismID = morphism.id;
	morphismContainer.addMorphism(morphism);
	
	var returnedMorphism = morphismContainer.getMorphism(3);

	assertUndefined(returnedMorphism);
};

DataspaceTest.prototype.testMorphismCheckConststructSet = function(){
	var morphism = new Morphism();
	morphism.addElementToConstructSet(1, 10);
	morphism.checkIsConstructInSet(1, 10);

	assertTrue(morphism.checkIsConstructInSet(1, 10));
};

DataspaceTest.prototype.testMorphismCheckConststructSetNotExists = function(){
	var morphism = new Morphism();
	morphism.addElementToConstructSet(1, 10);
	morphism.checkIsConstructInSet(1, 10);

	assertFalse(morphism.checkIsConstructInSet(2, 10));
}; 



//DataspaceTest.prototype.test = function() {
//	var dataspace = new Model_Dataspace();
//	assertTypeOf("Dataspace object expected", 'object', dataspace);
//};

//DataspaceTest.prototype.testGreet2 = function() {
//	var dataspace = new Model_Dataspace();
//	assertEquals("Hello World!", greeter.greet("World"));
//	jstestdriver.console.log("JsTestDriver", greeter.greet("World"));
//	console.log(greeter.greet("Browser", "World"));
//};