package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;

/****
 * 
 * 
 * 
 * Matcher Type: Combined Matcher
 * Working on: Construct level (element level)
 * 
 * @author chedeler
 * @author klitos
 *
 */

@Scope("prototype")
@Service
public class NameMatcherServiceImpl extends ConstructBasedMatcherServiceImpl {

	private static Logger logger = Logger.getLogger(NameMatcherServiceImpl.class);

	public NameMatcherServiceImpl() {
		logger.debug("in NameMatcherServiceImpl");
		this.setName("nameMatcher");
		this.setMatcherType(MatcherType.NAME);
	}
	
	/**
	 * This matcher extends the ConstructBasedMatcherServiceImpl which means that they are in a collection of
	 * matchers that are working over constructs thus the name ConstructBasedMatchers. This particular matcher
	 * works on the names of each construct. In other words for each construct it gets the name and then it
	 * compares its pairwise similarities.
	 * 
	 */
	
	@Override
	public float match(CanonicalModelConstruct construct1, CanonicalModelConstruct construct2) {
		logger.debug("in match");
		logger.debug("construct1: " + construct1);
		logger.debug("construct2: " + construct2);
		if (construct1.equals(construct2))
			return 1.0F;
		String name1 = construct1.getName();
		String name2 = construct2.getName();
		
		logger.debug("name1: " + name1);
		logger.debug("name2: " + name2);
		float similarity = 0.0F;
		for (MatcherService childMatcher : this.getChildMatchers()) {
			logger.debug("childMatcher: " + childMatcher);
			logger.debug("this.getChildMatchers().size(): " + this.getChildMatchers().size());
			if (childMatcher instanceof StringBasedMatcherServiceImpl) {
				logger.debug("childMatcher is StringBasedMatcher");
				StringBasedMatcherServiceImpl stringBasedMatcher = (StringBasedMatcherServiceImpl) childMatcher;
				
				/*
				 * Here it will call the child matchers of the StringBasedMatcher to do the match. For now this method will 
				 * call nGram to perform the match
				 */
				
				similarity = stringBasedMatcher.match(name1, name2);
				logger.debug("similarity: " + similarity);
				//TODO sort out aggregates, selection etc. if multiple childMatchers
				
				/* Klitos note: here the list may have several StringBasedMatchers, at the moment we have only 
				* nGram which can be used as a child matcher for performing stringBasedMatching. 
				*
				* 
				* IF there is a combination of StringBasedMatchers then this should be able to aggregate.
				*/
			} else
				logger.error("childMatcher isn't StringBasedMatcher, return 0.0F");
		}
		return similarity;
	}
	
	/***
	 * This method it will find the similarities between the names of constructs. In doing so it calls the match() method
	 * which then calls a collection of child matchers. The child matchers are of type StringBasedMatcherServiceImpl because
	 * they are working on string based similarity. 
	 * 
	 * This method is still not called because is not complete. The name matcher can have child matchers and in this case each
	 * individual matcher will produce a similarity matrix using the method below. The next step is to implement a method that will
	 * take this individual matrices that will be stored in a simCube and according to the control parameters provided 
	 * i.e., AggregationType and SelectionType it will call a new method that sorts out the aggregation. Maybe this 
	 * aggregate method is the one from the MatcherServiceImpl
	 */
	@Override
	public float[][] match(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2) {
		logger.debug("in match");
		/*Construct a similarity matrix that will hold the match similarity scores
		 *   - contructs1 (source constructs at the rows)
		 *   - contructs2 (target constructs at the columns)
		 */
		float[][] simMatrix = new float[constructs1.size()][constructs2.size()];
		logger.info("constructs1.size(): " + constructs1.size());
		logger.info("constructs2.size(): " + constructs2.size());
		/*Monitor the time to start the construction of the similarity matrix*/
		long startTime = System.nanoTime();
		logger.info("start matching all constructs with StringBasedMatcher: " + startTime);
		for (CanonicalModelConstruct construct1 : constructs1) {		
			for (CanonicalModelConstruct construct2 : constructs2) {				
				logger.debug("construct1: " + construct1);
				logger.debug("construct2: " + construct2);
				logger.debug("construct1.getName: " + construct1.getName());
				logger.debug("construct2.getName: " + construct2.getName());
				long startTimeSingleMatch = System.nanoTime();
				logger.info("start matching two constructs with StringBasedMatcher: " + startTimeSingleMatch);
				simMatrix[constructs1.indexOf(construct1)][constructs2.indexOf(construct2)] = this.match(construct1, construct2);
				long endTimeSingleMatch = System.nanoTime();
				logger.info("finished matching two constructs with StringBasedMatcher: " + endTimeSingleMatch);
				logger.info("duration for matching two constructs (in seconds): " + (endTimeSingleMatch - startTimeSingleMatch) / 1.0e9);
				logger.info("simMatrix[constructs1.indexOf(construct1)][constructs2.indexOf(construct2)]: "
						+ simMatrix[constructs1.indexOf(construct1)][constructs2.indexOf(construct2)]);
				logger.info("construct1.getName: " + construct1.getName());
				logger.info("construct2.getName: " + construct2.getName());
				logger.info("construct1.getName().length(): " + construct1.getName().length());
				logger.info("construct2.getName().length(): " + construct2.getName().length());
			}
		}
		long endTime = System.nanoTime();
		logger.info("constructs1.size(): " + constructs1.size());
		logger.info("constructs2.size(): " + constructs2.size());
		logger.info("finished matching all constructs with StringBasedMatcher: " + endTime);
		logger.info("duration for matching all constructs (in seconds): " + (endTime - startTime) / 1.0e9);
		return simMatrix;
	}
}//end class