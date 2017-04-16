package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.ConstructBasedMatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatchingProducerService;

@Scope("prototype")
@Service
@Configurable(autowire = Autowire.BY_NAME)
public class ConstructBasedMatcherServiceImpl extends MatcherServiceImpl implements ConstructBasedMatcherService {

	private static Logger logger = Logger.getLogger(ConstructBasedMatcherServiceImpl.class);

	//TODO change all Matchers into concurrentMatchers - schemaMatcher takes ages at GMP

	@Autowired
	private MatchingProducerService matchingProducerService;

	public ConstructBasedMatcherServiceImpl() {
		logger.debug("in ConstructBasedMatcherServiceImpl");
	}

	public ConstructBasedMatcherServiceImpl(MatcherType matcherType) {
		super(matcherType);
		logger.debug("in ConstructBasedMatcherServiceImpl");
		logger.debug("matcherType: " + matcherType);
	}


	public Matching match(CanonicalModelConstruct construct1, CanonicalModelConstruct construct2,
			Map<ControlParameterType, ControlParameter> controlParameters) {
		logger.debug("in match");
		logger.debug("construct1: " + construct1);
		logger.debug("construct2: " + construct2);
		float similarity = this.match(construct1, construct2);
		logger.debug("similarity: " + similarity);
		List<Matching> matchings = new ArrayList<Matching>();
		Matching match = matchingProducerService.produceSingleMatching(similarity, construct1, construct2, controlParameters, this);
		matchings.add(match);
		/*Use the constructed list of matchings and call saveMatchers() to save the Matchings*/
		this.saveMatches(matchings);
		return match;
	}


	/**
	 * Some matchers will override this method (e.g., NameMatcherService) 
	 * 
	 * 
	 */
	protected float match(CanonicalModelConstruct construct1, CanonicalModelConstruct construct2) {
		logger.debug("in protected match");
		logger.debug("construct1: " + construct1);
		logger.debug("construct2: " + construct2);
		float[] sim = this.runChildMatchers(construct1, construct2); // [childMatchers]
		float similarity = this.aggregate(sim);
		logger.debug("similarity: " + similarity);
		return similarity;
	}

	/**
	 * The following method is called when we would like to have a matching result as a List of Matching objects.
	 * Call this method instead the float[][] that returns a 2d array of matches
	 */
	
	public List<Matching> match(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2,
			Map<ControlParameterType, ControlParameter> controlParameters) {
		logger.debug("in match");
		logger.debug("constucts1: " + constructs1);
		logger.debug("constucts2: " + constructs2);
		float[][] simMatrix = this.match(constructs1, constructs2); // [constructs1][constructs2]
		logger.debug("simMatrix: " + simMatrix);
		List<Matching> matchings = matchingProducerService.produceMatches(simMatrix, constructs1, constructs2, controlParameters, this);
		this.saveMatches(matchings);
		return matchings;
	}

	/**
	 * Some matchers will override this method (e.g., NameMatcherService) 
	 * 
	 * This matcher starts from this method... <---- START FROM HERE
	 * 
	 */
	public float[][] match(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2) {
		logger.debug("in protected match");
		logger.debug("constucts1: " + constructs1);
		logger.debug("constucts2: " + constructs2);
		float[][][] simCube = this.runChildMatchers(constructs1, constructs2); // [childMatchers][constructs1][constructs2]
		logger.debug("simCube: " + simCube);
		float[][] simMatrix = this.aggregate(simCube); // [constructs1][constructs2] | calls aggregate from MatcherServiceImpl
		logger.debug("simMatrix: " + simMatrix);
		simMatrix = this.select(simMatrix);
		logger.debug("simMatrix: " + simMatrix);
		return simMatrix;
	}

	public float[] runChildMatchers(CanonicalModelConstruct construct1, CanonicalModelConstruct construct2) {
		logger.debug("in runChildMatchers");
		logger.debug("construct1: " + construct1);
		logger.debug("construct2: " + construct2);
		// [childMatchers]
		if (this.getChildMatchers() == null || this.getChildMatchers().size() == 0) {
			logger.error("matcher should have child matchers - sort this");		
		 }
		else {
			float[] simArray = new float[this.getChildMatchers().size()];
			List<MatcherService> childMatchers = this.getChildMatchers();
			for (MatcherService matcher : childMatchers) {
				logger.debug("matcher: " + matcher);
				float sim = 0;
				if (matcher instanceof ConstructBasedMatcherServiceImpl) {
					logger.debug("matcher is ConstructBasedMatcherServiceImpl");
					sim = ((ConstructBasedMatcherServiceImpl) matcher).match(construct1, construct2);
				} else if (matcher instanceof StringBasedMatcherServiceImpl) {
					logger.debug("matcher is StringBasedMatcherServiceImpl");
					sim = ((StringBasedMatcherServiceImpl) matcher).match(construct1.getName(), construct2.getName());
				} else
					logger.error("unexpected matcher: " + matcher);
				logger.debug("sim: " + sim);
				simArray[childMatchers.indexOf(matcher)] = sim;
			}
			logger.debug("simArray: " + simArray);
			return simArray;
		}
		logger.debug("returning null");
		return null;
	}//end runChildMatchers()

	/**
	 * klitos: It calls methods from the superClass MatcherServiceImpl
	 * 
	 * This method produces a three dimension matrix from running the child matchers within a combined matcher. There is no 
	 * method for creating the 3d matrix for individual matchers. I will create one in the MatcherServiceImpl
	 * 
	 * @return a similarity cube
	 */
	public float[][][] runChildMatchers(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2) {
		logger.debug("in runChildMatchers [return sim cube]");
		// [childMatchers][constructs1][constructs2]
		if (this.getChildMatchers() == null || this.getChildMatchers().size() == 0)
			logger.error("matcher should have child matchers - sort this");
		else {
			float[][][] simCube = new float[this.getChildMatchers().size()][][];
			List<MatcherService> childMatchers = this.getChildMatchers();
			for (MatcherService matcher : childMatchers) {
				logger.debug("matcher: " + matcher);
				if (matcher instanceof ConstructBasedMatcherServiceImpl) {
					logger.debug("matcher is ConstructBasedMatcherServiceImpl");
					simCube[childMatchers.indexOf(matcher)] = ((ConstructBasedMatcherServiceImpl) matcher).match(constructs1, constructs2);
					
					//TODO: the else below is not needed delete
				} else if (matcher instanceof StringBasedMatcherServiceImpl) {
					logger.debug("matcher is StringBasedMatcherServiceImpl");
					for (CanonicalModelConstruct construct1 : constructs1) {
						for (CanonicalModelConstruct construct2 : constructs2) {
							logger.debug("construct1.getName(): " + construct1.getName());
							logger.debug("construct2.getName(): " + construct2.getName());
							float sim = ((StringBasedMatcherServiceImpl) matcher).match(construct1.getName(), construct2.getName());
							logger.debug("sim: " + sim);
							simCube[childMatchers.indexOf(matcher)][constructs1.indexOf(construct1)][constructs2.indexOf(construct2)] = sim;
						}
					}
				}
			}
			logger.debug("simCube: " + simCube);
			return simCube;
		}
		logger.debug("returning null");
		return null;
	}


}//endClass
