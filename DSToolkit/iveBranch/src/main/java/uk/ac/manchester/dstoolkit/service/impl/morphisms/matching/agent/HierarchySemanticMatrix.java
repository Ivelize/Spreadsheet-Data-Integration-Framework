package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.meta.SemanticMetadataService;

/***
 * The HierarchySemanticMatrix will have its own hierarchy of Classes
 * 
 * 
 * - This class is at the same level as a ConstructBasedMatcher
 * 
 *
 * 
 * @author klitos
 *
 */
@Scope("prototype")
@Service
@Configurable(autowire = Autowire.BY_NAME)
public class HierarchySemanticMatrix extends SemanticMetadataServiceImpl {
	private static Logger logger = Logger.getLogger(HierarchySemanticMatrix.class);

	public HierarchySemanticMatrix() {
		logger.debug("in HierarchySemanticMatrix");
		this.setPrecedenceLevel(1);
	}
	
	public HierarchySemanticMatrix(SDBStoreServiceImpl sdbStore) {
		logger.debug("in HierarchySemanticMatrix");
		this.setSDBStoreService(sdbStore);
		this.setSemMatrixName("HierarchySemanticMatrix");
		this.setSemMatrixType(SemanticMatrixType.HIERARCHY);
		this.setPrecedenceLevel(1); //TODO: look again at the precedence levels
	}//end constructor	
	
	/***
	 * This method will organise and create semantic matrices by looking for patterns from meta-data
	 * vocabularies. 
	 *  1. It checks the Hierarchy matrix for which semantic matrices to create.
	 *  2. It creates a semantic matrix for each case (a) Equivalent (b) share super-class (c) is-a relationships
	 *  3. It then uses the precedence level to do conflict resolution.
	 * 
	 * @return - a cube of the semantic matrices specified
	 */
	public List<SemanticMatrix> generateSemanticMatrices(List<CanonicalModelConstruct> sourceConstructs, List<CanonicalModelConstruct> targetConstructs) {
		List<SemanticMatrix> matrixCube = new ArrayList<SemanticMatrix>();
		Map<ControlParameterType, ControlParameter> controlParameters = null;
		SemanticMatrix conflictResSemMatrix = null;		
		 /*Firstly look for the set of semantic matrices to create otherwise throw an exception*/
		 List<SemanticMetadataService> semMatricesListToCreate;
		try {
			semMatricesListToCreate = this.getAttachedSemanticMatrices();
			if (this.getAttachedSemanticMatrices() == null || this.getAttachedSemanticMatrices().size() == 0) {
				logger.error("HierarchySemanticMatrix should have child semantic matrices");		
			 } else {
				 for (SemanticMetadataService matrix : semMatricesListToCreate) {
					 //call the appropriate method from the actual semantic matrix appropriate to generate the semantic-matrix
					 if (matrix instanceof SubsumptionSemanticMatrix) {
						 //call the method from SubsumptionSemanticMatrix class to generate the matrix
						 SemanticMatrix matrix2D = ((SubsumptionSemanticMatrix) matrix).generateSemanticMatrix(sourceConstructs, targetConstructs,
								 													  semMatricesListToCreate.indexOf(matrix));
						 
						 //logger.debug("\n Matrix:" + matrix2D.toString());
						 matrixCube.add(matrix2D);						 
					 } else if (matrix instanceof ShareSuperClassSemMatrix) {
						 //call the method from ShareSuperClassSemMatrix class to generate the matrix
						 SemanticMatrix matrix2D = ((ShareSuperClassSemMatrix) matrix).generateSemanticMatrix(sourceConstructs, targetConstructs,
								 													  semMatricesListToCreate.indexOf(matrix));
						 //logger.debug("\n Matrix:" + matrix2D.toString());
						 matrixCube.add(matrix2D);	
					 } else if (matrix instanceof EquivalenceSemanticMatrix) {
						 SemanticMatrix matrix2D = ((EquivalenceSemanticMatrix) matrix).generateSemanticMatrix(sourceConstructs, targetConstructs,
								  semMatricesListToCreate.indexOf(matrix));

						 //logger.debug("\n Matrix:" + matrix2D.toString());
						 matrixCube.add(matrix2D);						 
					 }//end if				 
				 }//end for				
				
				 //Check control parameters to find whether to do conflict resolution or not
				 controlParameters = this.getControlParameters();
				 if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.DO_CONFLICT_RESOLUTION))) {
					 boolean doConfRes = controlParameters.get(ControlParameterType.DO_CONFLICT_RESOLUTION).isBool();
					 logger.debug("Do conflict resolution of HierarchySemanticMatrix: " + doConfRes);					 
					 if (doConfRes) {						
						 //Do conflict resolution to produce the final SemanticMatrix to return
						 conflictResSemMatrix = doConflictResolution(sourceConstructs, targetConstructs, matrixCube);
						 matrixCube.clear();
						 matrixCube.add(conflictResSemMatrix);
				 	 }//end if					 
				 }//end control parameters
			 }//end else
		} catch (Exception exe) {
			logger.debug("Exception: " + exe);
			return null;
		}	
		
		return matrixCube;		
	}//end generateSemanticMatrices()
		
	/***
	 * This method will do conflict resolution according to the levels of precedence:
	 * 	Level-1: equivalent classes (=)
	 *  Level-2: same superClass (C+)
	 *  Level-3: is-a relationships (with directionality)
	 *  @returns - The final 2D SemanticMatrix with all the conflicts resolved according to the 
	 *  		   above levels of precedence.
	 */
	public SemanticMatrix doConflictResolution(List<CanonicalModelConstruct> sourceConstructs,
												List<CanonicalModelConstruct> targetConstructs, List<SemanticMatrix> matrixCube) {
		logger.debug("in doConflictResolution()");
		SemanticMatrix resolutionMatrix = new SemanticMatrix(sourceConstructs.size(), targetConstructs.size());
		resolutionMatrix.setType(SemanticMatrixType.HIERARCHY);
		
		for (CanonicalModelConstruct construct1 : sourceConstructs) {
			for (CanonicalModelConstruct construct2 : targetConstructs) {
				int rowIndex = sourceConstructs.indexOf(construct1);
				int colIndex = targetConstructs.indexOf(construct2);
				/*Min priority should be first*/
				int minPriority = Integer.MAX_VALUE; 
				/*Store the cell with the lowest minPriority*/
				SemanticMatrixEntry cell = null;				
				
				/*For each semantic matrix in the matrixCube*/
				 for (SemanticMatrix matrix : matrixCube) {
					 if (matrix.getType().ordinal() < minPriority) {
						 SemanticMatrixEntry tempCell = matrix.getCellSemanticEntry(rowIndex, colIndex);
						 if (tempCell != null) {
						 	 minPriority = matrix.getType().ordinal();
							 cell = tempCell;
						 }//end if
					 }//end if			
				 }//end for
				 
				 /*Add cell to [row][column]*/
				 ArrayList<SemanticMatrixEntry> columnList = resolutionMatrix.getRow(rowIndex);
				 columnList.add(colIndex, cell);				 
			}//end for
		}//end for		
		
		return resolutionMatrix;		
	}//end for
}//end HierarchySemanticMatrix
