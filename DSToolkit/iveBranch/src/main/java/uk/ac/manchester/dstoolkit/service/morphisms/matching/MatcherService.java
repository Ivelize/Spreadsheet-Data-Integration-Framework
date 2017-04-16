package uk.ac.manchester.dstoolkit.service.morphisms.matching;

import java.util.List;
import java.util.Map;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherType;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.distributions.ProbabilityDensityFunction;

public interface MatcherService {

	public abstract String getName();

	public abstract void setName(String name);

	public abstract MatcherType getMatcherType();

	public abstract void setMatcherType(MatcherType matcherType);

	/**
	 * @return the index
	 */
	public abstract int getIndex();

	/**
	 * @param index the index to set
	 */
	public abstract void setIndex(int index);

	public abstract Map<ControlParameterType, ControlParameter> getControlParameters();

	public abstract void setControlParameters(Map<ControlParameterType, ControlParameter> controlParameters);

	public abstract void addControlParameter(ControlParameter controlParameter);

	//public abstract void internalAddControlParameter(ControlParameter controlParameter);

	public abstract void removeControlParameter(ControlParameter controlParameter);

	//public abstract void internalRemoveControlParameter(ControlParameter controlParameter);

	/**
	 * @return the parentMatcher
	 */
	public abstract MatcherService getParentMatcher();

	/**
	 * @param parentMatcher the parentMatcher to set
	 */
	public abstract void setParentMatcher(MatcherService parentMatcher);

	public abstract void internalSetParentMatcher(MatcherService parentMatcher);

	/**
	 * @return the childMatchers
	 */
	public abstract List<MatcherService> getChildMatchers();

	/**
	 * @param childMatchers the childMatchers to set
	 */
	public abstract void setChildMatchers(List<MatcherService> childMatchers);

	public abstract void addChildMatcher(MatcherService childMatcher);

	public abstract void internalAddChildMatcher(MatcherService childMatcher);

	public abstract void removeChildMatcher(MatcherService childMatcher);

	public abstract void internalRemoveChildMatcher(MatcherService childMatcher);

	/**
	 * @return the matchings
	 */
	public abstract List<Matching> getMatchings();

	public boolean addMatchings(List<Matching> matchings);
	

	/**
	 * @param matchings the matchings to set
	 */
	public abstract void setMatchings(List<Matching> matchings);
	
	/***
	 * Adding PDF for Bayes
	 */
	public ProbabilityDensityFunction getPdfTP();

	public void attachPdfTP(ProbabilityDensityFunction pdfTP);

	public ProbabilityDensityFunction getPdfFP();

	public void attachPdfFP(ProbabilityDensityFunction pdfFP);

}