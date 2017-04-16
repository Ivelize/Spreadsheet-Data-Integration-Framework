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
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.BooleanVariables;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.RDFSchema.DereferenceURIAgentServiceImpl;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.resultset.ResultSetMem;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Range only applied on predicates not classes.
 * 
 * @author klitos
 *
 */
@Scope("prototype")
@Service
@Configurable(autowire = Autowire.BY_NAME)
public class RangeSemanticMatrix extends SemanticMetadataServiceImpl {
	
	private static Logger logger = Logger.getLogger(RangeSemanticMatrix.class);
	
	/*Control parameter, on whether to use a reasoner or not*/
	private boolean usingInferencing;
	/*Hold a reference to the SDBStore that holds the RDFS vocabularies*/
	private SDBStoreServiceImpl sdbStore;
	

	/*Constructors*/
	public RangeSemanticMatrix() {	
		logger.debug("in RangeSemanticMatrix");
		this.setSemMatrixType(SemanticMatrixType.RANGE);
		this.setPrecedenceLevel(4);
	}
	
	public RangeSemanticMatrix(SDBStoreServiceImpl sdbStore) {
		this.setSDBStoreService(sdbStore);
		dereferenceURIAgentService = new DereferenceURIAgentServiceImpl(sdbStore);
		this.setSemMatrixName("RangeSemanticMatrix");
		this.setSemMatrixType(SemanticMatrixType.RANGE);
		this.setPrecedenceLevel(4);
	}	
	
	public SemanticMatrix generateSemanticMatrix(List<CanonicalModelConstruct> sourceConstructs, List<CanonicalModelConstruct> targetConstructs) {
		logger.debug("in generateSemanticMatrix()");
		logger.info("SDBStoreServiceImpl: " + sdbStore);
		int rows = sourceConstructs.size();
		int columns = targetConstructs.size();
		logger.info("sourceConstructs [rows]: " + rows);
		logger.info("targetConstructs [columns]: " + columns);
		SemanticMatrix matrix = new SemanticMatrix(rows, columns);
		/*To be used for precedence level*/
		matrix.setType(SemanticMatrixType.RANGE);
		/*Check if it has control parameters*/
		Map<ControlParameterType, ControlParameter> controlParameters = this.getControlParameters();
		if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.USE_REASONER))) {
			usingInferencing = controlParameters.get(ControlParameterType.USE_REASONER).isBool();
		}
		
		for (CanonicalModelConstruct construct1 : sourceConstructs) {
			for (CanonicalModelConstruct construct2 : targetConstructs) {
				/*Get the [row][column] position to add the cell entry to*/
				int rowIndex = sourceConstructs.indexOf(construct1);
				int colIndex = targetConstructs.indexOf(construct2);
				logger.info("position [row][column]: [" + rowIndex + ", " + colIndex + "]");
				SemanticMatrixEntry entry = this.findRange(construct1, construct2);
				logger.debug("Entry is: " + entry);
				ArrayList<SemanticMatrixEntry> columnList = matrix.getRow(rowIndex);
				columnList.add(colIndex, entry);				
			}//end inner for
		}//end for		
		
		return matrix;		
	}//end generateSemanticMatrix()
	
	
	public SemanticMatrixEntry findRange(CanonicalModelConstruct construct1,
			  							  CanonicalModelConstruct construct2) {

		logger.debug("in findRange()");
		SemanticMatrixEntry entry = null;
		Model baseModel_1 = null;
		Model baseModel_2 = null;
		String construct1URI = null;
		String construct2URI = null;
		
		//Cases to SKIP
		if (((construct1 instanceof SuperAbstract) && (construct2 instanceof SuperLexical)) ||
			 ((construct1 instanceof SuperLexical) && (construct2 instanceof SuperAbstract)) )
			return null;
		
		construct1URI = this.getConstructPropURI(construct1).getValue();
		construct2URI = this.getConstructPropURI(construct2).getValue();
		logger.debug("construct1URI: " + construct1URI);
		logger.debug("construct2URI: " + construct2URI);
		
		/*Method applies only on predicates therefore SuperLexicals*/
		if ((construct1 instanceof SuperLexical) && (construct2 instanceof SuperLexical)) {
			
			baseModel_1 = this.subjectURIexistsDatasetSELECT(construct1URI);	
			
			if (baseModel_1 != null) {
				baseModel_2 = this.subjectURIexistsDatasetSELECT(construct2URI);
				if (baseModel_2 != null) {
					if (usingInferencing) {
						logger.debug("Using inferencing mode: " + usingInferencing);
						//NOTE: This functionality is not complete						
					} else {
						logger.debug("Using inferencing mode: " + usingInferencing);
									
						if (!construct1URI.equals(construct2URI)) {								
							ResultSet set_1 = this.getResultSetForSubjectURIandPredicate(baseModel_1, construct1URI, RDFS.range);
							ResultSet set_2 = this.getResultSetForSubjectURIandPredicate(baseModel_2, construct2URI, RDFS.range);	
							
							boolean shareSameRange = this.shareSameRange(set_1, set_2);
							
							if (shareSameRange) {
								entry = new SemanticMatrixEntry(construct1, construct2);
								entry.setTypeOfEntry(SemanticMatrixType.RANGE);
								entry.addCellValueToList(BooleanVariables.PSRA);	
							}							
						}//end if						
					}//end else					
				}//end if				
			}//end inner if			
		}//end if
		
		return entry;	
	}//end findRange()	
	
	public boolean shareSameRange(ResultSet set_1, ResultSet set_2) {
		ResultSetMem rs = (ResultSetMem) ResultSetFactory.makeRewindable(set_2);		
	    for ( ; set_1.hasNext() ; ) {
	        QuerySolution soln = set_1.next() ;
		    RDFNode res1 = soln.get("o");
		    for ( ; rs.hasNext() ; ) {
		        QuerySolution soln2 = rs.next() ;
			    RDFNode res2 = soln2.get("o");
				if (res1.equals(res2)) {
					logger.debug("Res 1: " + res1 + " share same range with, Res 2: " + res2);
					return true;
				}//end if				
		    }//end for
		    /*reset iterator*/
		    rs.reset();		    
	    }//end for		
	  return false;
	}//end shareSuperClass()	
}//end class