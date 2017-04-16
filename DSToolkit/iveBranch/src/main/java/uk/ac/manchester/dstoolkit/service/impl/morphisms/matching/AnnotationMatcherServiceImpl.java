package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

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
import uk.ac.manchester.dstoolkit.exceptions.PrimitiveMatcherException;
import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.AnnotationMatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;

/***
 * 
 * The creation of an new AnnotationMatcherServiceImpl means that the user will create a new object of AnnotationMatcherServiceImpl
 * and then add to it a set of child Matchers. Therefore in the match() this class needs to be able to create the sim matrix for 
 * the matchers. Probably it will need an aggregation strategy and a selection strategy.
 * 
 * @author klitos
 *
 */

//This class has all methods from MatcehrServiceImpl and implements methods from AnnotationMatcherService as well	
//It will override some implementation from the MatcherServiceImpl


@Scope("prototype")
@Service
@Configurable(autowire = Autowire.BY_NAME)
public class AnnotationMatcherServiceImpl extends MatcherServiceImpl implements AnnotationMatcherService {
	
	private static Logger logger = Logger.getLogger(AnnotationMatcherServiceImpl.class);
	
	private Map<ControlParameterType, ControlParameter> controlParameters = null;
	
	/*Hold a reference to the SDBStore that holds metadata*/
	private SDBStoreServiceImpl metaDataSDBStore = null;
		
	/***
	 * Constructor1:
	 *  - Client can choose to construct a new AnnotationMatcher supplying no arguments.
	 *  - Because the AnnotationMatcherService does not know which matchers to run, the user needs to specify
	 * which matchers to use, from the AnnotationMatcherService, and add them to this matcher as childMatchers using
	 * the method from MatecherServiceImpl addChildMatcher()
	 */
	
	public AnnotationMatcherServiceImpl() {
		logger.debug("in AnnotationMatcherServiceImpl");
	}
	
	/***
	 * This method will check if the user supplied the correct:
	 *   - type of child Matchers
	 *   - a set of controlled parameters
	 *   - TODO: I may need to change this method
	 */
	private boolean doChecks() {
		try {
			if (this.getChildMatchers().size() == 0 || this.getChildMatchers() == null) {
				throw new Exception("Need to supply a set of child matchers.");
			} else {
				for (MatcherService childMatcher : this.getChildMatchers()) {	
					if (!(childMatcher instanceof AnnotationMatcherServiceImpl)) {
						throw new Exception("Child matchers need to be from AnnotationMatcherService");
					}//end if
				}//end for
			}//end else
			
			/*Check ControlParameters*/
			controlParameters = this.getControlParameters();
			if (this.getControlParameters() == null || this.getControlParameters().size() == 0) {
				if (!controlParameters.containsKey(ControlParameterType.MATCH_SELECT_SELECTION_TYPE)) {
					throw new Exception("Specify match SELECTION type.");
				} 
				
				if (!controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE)) {
					throw new Exception("Specify match AGGREGATE type.");
				} 				
			}//end if			
		} catch (Exception exe) {
			logger.error("Exception: " + exe);
			return false;
		}
		
		return true;
	}//end init()
	
	
	/***
     * When the match() of an AnnotationMatcher is called from the SchemaServiceImpl, then this class needs to search for run the childMatchers
	 * attached to it.
	 */	
	public float[][] match(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2) {
		float[][] simMatrix = null;		
		try {
			/*Check if there are any child matchers - if not exception*/
			if (this.getChildMatchers().size() == 0 || this.getChildMatchers() == null) {
				throw new Exception("Need to supply a set of child matchers.");
			}
		
			if (this.getChildMatchers().size() == 1) {
				simMatrix = this.runChildMatcher(constructs1, constructs2); //[constructs1][constructs2] 				
			} else {
				//float[][][] simCube = this.runChildMatchers(constructs1, constructs2); // [childMatchers][constructs1][constructs2]
			}
		} catch (Exception exe) {
			logger.error("Exception: " + exe);
			return null;
		}		
		return simMatrix;
	}//end match
	
	
	
	/***
	 * If AnnotationMatcher has only one child matcher, find its type and then execute its match().
	 * 
	 * @param constructs1
	 * @param constructs2
	 * @return float[][] - a similarity matrix by running the match() of that matcher
	 */
	public float[][] runChildMatcher(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2) {
		logger.debug("in runChildMatcher [Single matcher detected]");
		try {
			if (this.getChildMatchers() == null || this.getChildMatchers().size() == 0) {
			    logger.error("This matcher should have child matchers");
		   	  	throw new PrimitiveMatcherException("Matcher should have child matchers");
			} else {
				float[][] simMatrix = new float[constructs1.size()][constructs2.size()];
				List<MatcherService> childMatchers = this.getChildMatchers();
				for (MatcherService matcher : childMatchers) {
					logger.debug("matcher: " + matcher);
					if (matcher instanceof RDFSLabelMatcherServiceImpl) {
						simMatrix = ((RDFSLabelMatcherServiceImpl) matcher).match(constructs1, constructs2);
					}//end if					
					//TODO: Add more Annotation Primitive Matchers here					
				}//end for				
				return simMatrix;
			}//end else
		} catch (PrimitiveMatcherException exe) {
			logger.debug("Exception: " + exe);
			return null;
		}		
	}//end runChildMatcher()	
	
	//---- Supportive Methods ----/
	public void attachMetaDataSDBStore(SDBStoreServiceImpl store) {
		logger.debug("in attachMetaDataSDBStore()");
		metaDataSDBStore = store;
	}
	
	public SDBStoreServiceImpl getMetaDataSDBStore() {
		return this.metaDataSDBStore;
	}	
}//end AnnotationMatcherServiceImpl