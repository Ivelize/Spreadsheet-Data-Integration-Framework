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
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelProperty;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.StringBasedMatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.BooleanVariables;

/**
 * This class organises information on: 
 *  - whether a pair of constructs belong to the same namespace URI.
 * 
 * @author klitos
 */ 


@Scope("prototype")
@Service
@Configurable(autowire = Autowire.BY_NAME)
public class NameSpaceSemMatrix extends SemanticMetadataServiceImpl {
	private static Logger logger = Logger.getLogger(NameSpaceSemMatrix.class);
	private StringBasedMatcherServiceImpl stringBasedMatcher;
	private float threshold = -1.0F; 
	
	public NameSpaceSemMatrix() {
		logger.debug("in NameSpaceSemMatrix");
		this.setSemMatrixName("NameSpaceSemMatrix");
		this.setSemMatrixType(SemanticMatrixType.NAMESPACE);
		this.setPrecedenceLevel(5);
	}

	public NameSpaceSemMatrix(StringBasedMatcherServiceImpl stringMatcher) {
		logger.debug("in NameSpaceSemMatrix");
		this.setSemMatrixName("NameSpaceSemMatrix");
		this.setSemMatrixType(SemanticMatrixType.NAMESPACE);
		this.setPrecedenceLevel(5);
		stringBasedMatcher = stringMatcher;
	}		

	/**
	 * This 
	 * 
	 */
	public SemanticMatrix generateSemanticMatrix(List<CanonicalModelConstruct> sourceConstructs, List<CanonicalModelConstruct> targetConstructs) {
		logger.debug("in generateSemanticMatrix()");
		int rows = sourceConstructs.size();
		int columns = targetConstructs.size();
		logger.info("sourceConstructs [rows]: " + rows);
		logger.info("targetConstructs [columns]: " + columns);		
		SemanticMatrix matrix = new SemanticMatrix(rows, columns);
		/*To be used for precedence level*/
		matrix.setType(SemanticMatrixType.NAMESPACE);
		/*Check if the semantic matrix has control parameters*/
		//NOTE: Do this method to use a String based mathcer to find whether namespace URIs are the same
		Map<ControlParameterType, ControlParameter> controlParameters = this.getControlParameters();		
		if (controlParameters.containsKey(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE)) {
			threshold = new Float(controlParameters.get(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE).getValue()).floatValue();
		}
		if (threshold == -1.0) {
			logger.error("didn't find threshold for THRESHOLD - set 0.98 as default");
			threshold = 0.98F;
		}		
		for (CanonicalModelConstruct construct1 : sourceConstructs) {
			for (CanonicalModelConstruct construct2 : targetConstructs) {
				/*Get the [row][column] position to add the cell entry to*/
				int rowIndex = sourceConstructs.indexOf(construct1);
				int colIndex = targetConstructs.indexOf(construct2);
				logger.info("position [row][column]: [" + rowIndex + ", " + colIndex + "]");
				SemanticMatrixEntry entry = this.checkNamespaceURI(construct1, construct2);
				logger.debug("Entry is: " + entry);
				ArrayList<SemanticMatrixEntry> columnList = matrix.getRow(rowIndex);
				columnList.add(colIndex, entry);				
			}//end inner for
		}//end for		
		
		return matrix;		
	}//end generateSemanticMatrix()
	
	
	/**
	 * A String based matcher will be used to perform a comparison between a pair of namespace URIs.
	 */	
	public SemanticMatrixEntry checkNamespaceURI(CanonicalModelConstruct construct1,
	 		  									 CanonicalModelConstruct construct2) {
		
		logger.debug("in checkNamespaceURI()");
		SemanticMatrixEntry entry = null;
		
		CanonicalModelProperty nsProp1 = this.getConstructPropNS(construct1);
		CanonicalModelProperty nsProp2 = this.getConstructPropNS(construct2);

		logger.debug("nsProp1: " + nsProp1);
		logger.debug("nsProp2: " + nsProp2);
		
		if ((nsProp1 == null) || (nsProp2 == null)) {
			return null;
		}		
		
		//Cases to SKIP
		if (((construct1 instanceof SuperAbstract) && (construct2 instanceof SuperLexical)) ||
			 ((construct1 instanceof SuperLexical) && (construct2 instanceof SuperAbstract)) )
			return null;
		
		String construct1NS = nsProp1.getValue();			
		String construct2NS = nsProp2.getValue();
						
		/*1. Check whether constructs are both SuperAbtsracts*/
		if ((construct1 instanceof SuperAbstract) && (construct2 instanceof SuperAbstract)) {
			if (construct1NS.equals(construct2NS)) {
				entry = new SemanticMatrixEntry();
				entry.setTypeOfEntry(SemanticMatrixType.NAMESPACE);
				entry.addCellValueToList(BooleanVariables.CSN);		
			}		
		}  else if ((construct1 instanceof SuperLexical) && (construct2 instanceof SuperLexical)) {
			if (construct1NS.equals(construct2NS)) {
				entry = new SemanticMatrixEntry();
				entry.setTypeOfEntry(SemanticMatrixType.NAMESPACE);
				entry.addCellValueToList(BooleanVariables.PSN);
			}			
		}//end if	
		
		return entry;
	}//end checkNamespaceURI()
}//end Class