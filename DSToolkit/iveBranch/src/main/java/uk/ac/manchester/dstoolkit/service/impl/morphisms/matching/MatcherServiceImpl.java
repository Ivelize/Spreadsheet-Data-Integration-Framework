package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.OneToOneMatching;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.distributions.ProbabilityDensityFunction;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatchingService;

/**
 * @author chedeler
 * @author klitos
 *
 *	//TODO think about storing matchers and matcherConfiguration as provenance
 *  //TODO work on generic provenance model that will fit everything
 *  //TODO test
 *
 * ControlParameters that are processed right now (see also ConstructBasedMatcherServiceImpl for how they're used)
 * The choice of parameters used is inspired by the parameters used by COMA++
 * Direction: currently only both is supported and set as default in ConstructBasedMatcherServiceImpl.select; 
 * see DirectionType for the other types currently not supported
 * SelectionType: MAXN, MAXDELTA, THRESHOLD, MULTIPLE;
 * depending on the selectionType, the following additional parameters need to be provided:
 * for MAXN: candidateNumber, i.e., n
 * for MAXDELTA: delta
 * for THRESHOLD: threshold
 * for MULTIPLE: candidateNumber, delta, threshold
 * AggregationType: SIMMAX, SIMMIN, SIMAVERAGE, SIMWEIGHTED (we need to specify the weights) 
 * for SIMWEIGHTED the weights need to be provided for each matcher in the same order as the matchers, 
 * the parameter name for each weight should start with weight and only the ordering is used, 
 * no information from the name is used to identify which weight belongs to which matcher
 * 
 *  //TODO: Klitos: for SIMWEIGHT implement harmony to approximate the weight of each matcher.
 */

public abstract class MatcherServiceImpl implements MatcherService {

	private static Logger logger = Logger.getLogger(MatcherServiceImpl.class);
	
	@Autowired
	private MatchingService matchingService;
	
	private ProbabilityDensityFunction pdfTP;
	
	private ProbabilityDensityFunction pdfFP;

	private MatcherType matcherType;

	private int index;

	private String name;

	private Map<ControlParameterType, ControlParameter> controlParameters = new HashMap<ControlParameterType, ControlParameter>();

	private MatcherService parentMatcher;

	private List<MatcherService> childMatchers = new ArrayList<MatcherService>();

	private List<Matching> matchings = new ArrayList<Matching>();

	public MatcherServiceImpl() {
		logger.debug("in MatcherServiceImpl");
	}

	public MatcherServiceImpl(Map<ControlParameterType, ControlParameter> controlParameters) {
		this.controlParameters = controlParameters;
	}

	public MatcherServiceImpl(String name) {
		logger.debug("in MatcherServiceImpl");
		logger.debug("name: " + name);
		this.name = name;
	}

	public MatcherServiceImpl(String name, Map<ControlParameterType, ControlParameter> controlParameters) {
		logger.debug("in MatcherServiceImpl");
		logger.debug("name: " + name);
		logger.debug("controlParameters: " + controlParameters);
		this.setName(name);
		this.controlParameters = controlParameters;
	}

	public MatcherServiceImpl(MatcherType matcherType) {
		logger.debug("in MatcherServiceImpl");
		logger.debug("matcherType: " + matcherType);
		this.matcherType = matcherType;
	}

	public MatcherServiceImpl(MatcherType matcherType, Map<ControlParameterType, ControlParameter> controlParameters) {
		logger.debug("in MatcherServiceImpl");
		logger.debug("matcherType: " + matcherType);
		logger.debug("controlParameters: " + controlParameters);
		this.matcherType = matcherType;
		this.controlParameters = controlParameters;
	}

	
	/****
	 * Probability Density Functions
	 */
	public ProbabilityDensityFunction getPdfTP() {
		return pdfTP;
	}

	public void attachPdfTP(ProbabilityDensityFunction pdfTP) {
		this.pdfTP = pdfTP;
	}

	public ProbabilityDensityFunction getPdfFP() {
		return pdfFP;
	}

	public void attachPdfFP(ProbabilityDensityFunction pdfFP) {
		this.pdfFP = pdfFP;
	}

	/**
	 * @param index
	 */
	//TODO get rid of this
	public MatcherServiceImpl(int index) {
		super();
		this.index = index;
		logger.debug("in MatcherServiceImpl");
		logger.debug("index: " + index);
	}

	//TODO select for single value

	protected float[][] select(float[][] simMatrix) {
		logger.debug("in select");
		//[constructs1][constructs2]
		//default
		DirectionType direction = DirectionType.BOTH;
		String selectionType = null;
		SelectionType selection = null;
		int candidateNumber = -1; //MAXN
		float delta = -1.0F; //delta
		float threshold = -1.0F; //threshold

		if (controlParameters.containsKey(ControlParameterType.MATCH_SELECT_SELECTION_TYPE)) {
			selectionType = controlParameters.get(ControlParameterType.MATCH_SELECT_SELECTION_TYPE).getValue();
		}
		
		/**
		 * Select all syntactic matrices, usually used when this matrices are used with semantic matrices.
		 */
		if (selectionType.equals(SelectionType.SELECT_ALL.toString())) {
			return simMatrix;
		}		
		
		if (selectionType == null)
			selectionType = SelectionType.THRESHOLD.toString(); //set maxn as default with threshold = 0.3

		if (selectionType.equals(SelectionType.MAXN.toString())) {
			selection = SelectionType.MAXN;
			if (controlParameters.containsKey(ControlParameterType.MATCH_SELECT_MAXN_CANDIDATE_NUMBER)) {
				candidateNumber = new Integer(controlParameters.get(ControlParameterType.MATCH_SELECT_MAXN_CANDIDATE_NUMBER).getValue()).intValue();
			}
			if (candidateNumber == -1)
				logger.error("didn't find candidateNumber for MAXN - TODO sort this");
		} else if (selectionType.equals(SelectionType.MAXDELTA.toString())) {
			selection = SelectionType.MAXDELTA;
			if (controlParameters.containsKey(ControlParameterType.MATCH_SELECT_MAXDELTA_DELTA)) {
				delta = new Float(controlParameters.get(ControlParameterType.MATCH_SELECT_MAXDELTA_DELTA).getValue()).floatValue();
			}
			if (delta == -1.0)
				logger.error("didn't find delta for MAXDELTA - TODO sort this");
		} else if (selectionType.equals(SelectionType.THRESHOLD.toString())) {
			selection = SelectionType.THRESHOLD;
			if (controlParameters.containsKey(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE)) {
				threshold = new Float(controlParameters.get(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE).getValue()).floatValue();
			}
			if (threshold == -1.0) {
				logger.error("didn't find threshold for THRESHOLD - set 0.3 as default");
				threshold = 0.3F;
			}
		} else if (selectionType.equals(SelectionType.MULTIPLE.toString())) {
			if (controlParameters.containsKey(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE)) {
				threshold = new Float(controlParameters.get(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE).getValue()).floatValue();
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_SELECT_MAXDELTA_DELTA)) {
				delta = new Float(controlParameters.get(ControlParameterType.MATCH_SELECT_MAXDELTA_DELTA).getValue()).floatValue();
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_SELECT_MAXN_CANDIDATE_NUMBER)) {
				candidateNumber = new Integer(controlParameters.get(ControlParameterType.MATCH_SELECT_MAXN_CANDIDATE_NUMBER).getValue()).intValue();
			}
			if (threshold == -1.0)
				logger.error("didn't find threshold for MULTIPLE - TODO sort this");
			if (delta == -1.0)
				logger.error("didn't find delta for MULTIPLE - TODO sort this");
			if (candidateNumber == -1)
				logger.error("didn't find candidateNumber for MULTIPLE - TODO sort this");
		}

		float[][] selMatrix = null;
		if (selection.equals(SelectionType.MULTIPLE)) {
			selMatrix = selectMultiple(simMatrix, direction, selection, candidateNumber, delta, threshold);
		} else {
			selMatrix = selectSingle(simMatrix, direction, selection, candidateNumber, delta, threshold);
		}
		return selMatrix;
	}

	protected float[][] selectMultiple(float[][] simMatrix, DirectionType direction, SelectionType selection, int candidateNumber, float delta,
			float threshold) {
		logger.debug("in selectMultiple");
		logger.debug("simMatrix: " + simMatrix);
		logger.debug("direction: " + direction);
		logger.debug("selection: " + selection);
		logger.debug("candidateNumber: " + candidateNumber);
		logger.debug("delta: " + delta);
		logger.debug("threshold: " + threshold);
		//[constructs1][constructs2]
		if ((simMatrix == null) || (simMatrix.length == 0))
			return null;
		if ((candidateNumber <= 0) && (delta <= 0.0F) && (threshold <= 0.0F)) {
			return simMatrix;
		}

		int m = simMatrix.length;
		int n = simMatrix[0].length;

		float[][] selMatrix = new float[m][n];
		for (int i = 0; i < m; ++i)
			System.arraycopy(simMatrix[i], 0, selMatrix[i], 0, n);

		float[] forwardSelSim = new float[m];
		float[] backwardSelSim = new float[n];
		for (int i = 0; i < m; ++i) {
			float[] sortedSims = new float[n];
			for (int j = 0; j < n; ++j)
				sortedSims[j] = selMatrix[i][j];
			Arrays.sort(sortedSims);
			if (candidateNumber > 0) {
				if (candidateNumber > n) {
					forwardSelSim[i] = sortedSims[0];
				} else
					forwardSelSim[i] = sortedSims[(n - candidateNumber)];
			}
			if (delta > 0.0F) {
				float sim = sortedSims[(n - 1)] - sortedSims[(n - 1)] * delta;
				if (forwardSelSim[i] < sim)
					forwardSelSim[i] = sim;
			}
			if (threshold > 0.0F && forwardSelSim[i] < threshold)
				forwardSelSim[i] = threshold;
		}

		for (int j = 0; j < n; ++j) {
			float[] sortedSims = new float[m];
			for (int i = 0; i < m; ++i)
				sortedSims[i] = selMatrix[i][j];
			Arrays.sort(sortedSims);
			float maxSim = sortedSims[(m - 1)];

			if (candidateNumber > 0) {
				if (candidateNumber > m)
					backwardSelSim[j] = sortedSims[0];
				else
					backwardSelSim[j] = sortedSims[(m - candidateNumber)];
			}
			if (delta > 0.0F) {
				float sim = maxSim - maxSim * delta;
				if (backwardSelSim[j] < sim)
					backwardSelSim[j] = sim;
			}
			if (threshold > 0.0F && backwardSelSim[j] < threshold)
				backwardSelSim[j] = threshold;
		}

		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				if (selMatrix[i][j] < forwardSelSim[i])
					selMatrix[i][j] = 0.0F;
			}
		}

		for (int j = 0; j < n; ++j) {
			for (int i = 0; i < m; ++i) {
				if (selMatrix[i][j] < backwardSelSim[j])
					selMatrix[i][j] = 0.0F;
			}
		}
		return selMatrix;
	}

	protected float[][] selectSingle(float[][] simMatrix, DirectionType direction, SelectionType selection, int candidateNumber, float delta,
			float threshold) {
		logger.debug("in selectSingle");
		logger.debug("simMatrix: " + simMatrix);
		logger.debug("direction: " + direction);
		logger.debug("selection: " + selection);
		logger.debug("candidateNumber: " + candidateNumber);
		logger.debug("delta: " + delta);
		logger.debug("threshold: " + threshold);
		//[constructs1][constructs2]
		if (simMatrix == null)
			return null;
		if (selection.equals(SelectionType.MAXN) && candidateNumber <= 0)
			return simMatrix;
		if (selection.equals(SelectionType.MAXDELTA) && delta <= 0.0F)
			return simMatrix;
		if (selection.equals(SelectionType.THRESHOLD) && threshold <= 0.0F)
			return simMatrix;

		int m = simMatrix.length;
		if ((m == 0) || (simMatrix[0] == null)) {
			return null;
		}
		int n = simMatrix[0].length;

		float[][] selMatrix = new float[m][n];
		for (int i = 0; i < m; ++i)
			System.arraycopy(simMatrix[i], 0, selMatrix[i], 0, n);

		float[] forwardSelSim = new float[m];
		float[] backwardSelSim = new float[n];

		if (selection.equals(SelectionType.THRESHOLD)) {
			for (int i = 0; i < m; ++i) {
				forwardSelSim[i] = threshold;
			}
			for (int j = 0; j < n; ++j)
				backwardSelSim[j] = threshold;
		} else {
			for (int i = 0; i < m; ++i) {
				float[] sortedSims = new float[n];
				for (int j = 0; j < n; ++j)
					sortedSims[j] = selMatrix[i][j];
				Arrays.sort(sortedSims);
				if (selection.equals(SelectionType.MAXN)) {
					if (candidateNumber > n)
						forwardSelSim[i] = sortedSims[0];
					else {
						forwardSelSim[i] = sortedSims[(n - candidateNumber)];
					}
				} else if (selection.equals(SelectionType.MAXDELTA)) {
					forwardSelSim[i] = (sortedSims[(n - 1)] - sortedSims[(n - 1)] * delta);
				}
			}
			for (int j = 0; j < n; ++j) {
				float[] sortedSims = new float[m];
				for (int i = 0; i < m; ++i)
					sortedSims[i] = selMatrix[i][j];
				Arrays.sort(sortedSims);

				if (selection.equals(SelectionType.MAXN)) {
					if (candidateNumber > m)
						backwardSelSim[j] = sortedSims[0];
					else {
						backwardSelSim[j] = sortedSims[(m - candidateNumber)];
					}
				} else if (selection.equals(SelectionType.MAXDELTA)) {
					backwardSelSim[j] = (sortedSims[(m - 1)] - sortedSims[(m - 1)] * delta);
				}
			}
		}

		if (selection.equals(SelectionType.THRESHOLD)) {
			for (int i = 0; i < m; ++i) {
				for (int j = 0; j < n; ++j) {
					if (selMatrix[i][j] < forwardSelSim[i])
						selMatrix[i][j] = 0.0F;
				}
			}

			for (int j = 0; j < n; ++j) {
				for (int i = 0; i < m; ++i) {
					if (selMatrix[i][j] < backwardSelSim[j])
						selMatrix[i][j] = 0.0F;
				}
			}
		}
		return selMatrix;
	}

	protected float[][] aggregate(float[][][] simCube) {
		logger.debug("in aggregate");
		//[constructs1][constructs2] ([childMatchers][constructs1][constructs2])
		String aggregationType = null;
		float[] weights = null;

		if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE)) {
			aggregationType = controlParameters.get(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE).getValue();
		}

		/**
		 * For combined matchers, if MATCH_AGGREGATE_AGGREGATION_TYPE is not set then use AVERAGE aggregation
		 * strategy as default.
		 */
		if (aggregationType == null) {
			aggregationType = AggregationType.SIMAVERAGE.toString();
		}
		
		if (aggregationType.equals(AggregationType.SIMWEIGHTED.toString())) {
			weights = new float[simCube.length];
			int noWeights = 0;

			//TODO not the best method, as if more weights required will have to add them to ControlParameterType and add more code here ... think about this
			//assume weights are in the correct order with respect to the childmatchers - issue if not
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT1)) {
				weights[0] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT1).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT2)) {
				weights[1] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT2).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT3)) {
				weights[2] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT3).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT4)) {
				weights[3] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT4).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT5)) {
				weights[4] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT5).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT6)) {
				weights[5] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT6).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT7)) {
				weights[6] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT7).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT8)) {
				weights[7] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT8).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT9)) {
				weights[8] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT9).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT10)) {
				weights[9] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT10).getValue()).floatValue();
				noWeights++;
			}

			if (noWeights < simCube.length)
				logger.error("some weights seem to be missing - TODO sort this");
		}

		if (simCube == null)
			return null;

		int m = 0;
		int n = 0;
		int k = simCube.length;
		if (k > 0) {
			for (int i = 0; i < k; ++i) {
				if (simCube[i] != null) {
					m = simCube[i].length;
					if (m == 0) {
						n = 0;
						break;
					}
					n = simCube[i][0].length;
					break;
				}
			}
		}

		float[][] simMatrix = new float[m][n];
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				float maxSim = 0.0F;
				float minSim = 100000.0F;
				float sumSim = 0.0F;
				float weightedSim = 0.0F;

				for (int l = 0; l < k; ++l) {
					float sim = simCube[l][i][j];
					if (aggregationType.equals(AggregationType.SIMMAX.toString())) {
						if (maxSim < sim)
							maxSim = sim;
					} else if (aggregationType.equals(AggregationType.SIMMIN.toString())) {
						if (minSim > sim)
							minSim = sim;
					} else if (aggregationType.equals(AggregationType.SIMAVERAGE.toString()))
						sumSim += sim;
					else if (aggregationType.equals(AggregationType.SIMWEIGHTED.toString()))
						weightedSim += sim * weights[l];
				}

				if (aggregationType.equals(AggregationType.SIMMAX.toString()))
					simMatrix[i][j] = maxSim;
				else if (aggregationType.equals(AggregationType.SIMMIN.toString()))
					simMatrix[i][j] = minSim;
				else if (aggregationType.equals(AggregationType.SIMAVERAGE.toString()))
					simMatrix[i][j] = (sumSim / k);
				else if (aggregationType.equals(AggregationType.SIMWEIGHTED.toString()))
					simMatrix[i][j] = weightedSim;
			}
		}
		return simMatrix;
	}

	/**
	 * This static method will be used when the method for aggregating a syntactic similarity score is needed without 
	 * having to create a new object.
	 * 
	 * @param List<MatcherInfo> simCube
	 * @return a 2D similarity value which holds the final aggregated similarities
	 */
	public static float[][] aggregate(List<MatcherInfo> simCube, Map<ControlParameterType, ControlParameter> controlParameters) {
		logger.debug("in aggregate - static method");
		//[constructs1][constructs2] ([childMatchers][constructs1][constructs2])
		String aggregationType = null;
		float[] weights = null;

		/* Get the aggregation type if exists as a control parameter*/
		if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE)) {
			aggregationType = controlParameters.get(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE).getValue();
		}
		
		/**
		 * For combined matchers, if MATCH_AGGREGATE_AGGREGATION_TYPE is not set then use AVERAGE aggregation
		 * strategy as default.
		 */
		if (aggregationType == null) {
			aggregationType = AggregationType.SIMAVERAGE.toString();
		}
		
		/* Check if simCube is null and if it is then return null */
		if (simCube == null)
			return null;	
		
		/** Aggregation Type: Weighted Average **/
		if (aggregationType.equals(AggregationType.SIMWEIGHTED.toString())) {
			weights = new float[simCube.size()];
			int noWeights = 0;

			//TODO not the best method, as if more weights required will have to add them to ControlParameterType and add more code here ... think about this
			//assume weights are in the correct order with respect to the childmatchers - issue if not
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT1)) {
				weights[0] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT1).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT2)) {
				weights[1] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT2).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT3)) {
				weights[2] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT3).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT4)) {
				weights[3] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT4).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT5)) {
				weights[4] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT5).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT6)) {
				weights[5] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT6).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT7)) {
				weights[6] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT7).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT8)) {
				weights[7] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT8).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT9)) {
				weights[8] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT9).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT10)) {
				weights[9] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT10).getValue()).floatValue();
				noWeights++;
			}

			if (noWeights < simCube.size())
				logger.error("some weights seem to be missing - TODO sort this");
		}//end if
		
		int m = 0;
		int n = 0;
		int k = simCube.size();
		if (k > 0) {
			for (int i = 0; i < k; ++i) {
				//Get the 2D array from the List
				float [][] array = simCube.get(i).getSimMatrix();
				if (array != null) {
					m = array.length; //number of rows
					if (m == 0) {
						n = 0;
						break;
					}
					n = array[0].length; //number of columns
					break;
				}//end inner if
			}//end for
		}//end if		

		/* Create a new 2D matrix to return */
		float[][] simMatrix = new float[m][n];
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				float maxSim = 0.0F;
				float minSim = 100000.0F;
				float sumSim = 0.0F;
				float weightedSim = 0.0F;

				for (int l = 0; l < k; ++l) {
					float [][] array = simCube.get(l).getSimMatrix();
					float sim = array[i][j];
					if (aggregationType.equals(AggregationType.SIMMAX.toString())) {
						if (maxSim < sim)
							maxSim = sim;
					} else if (aggregationType.equals(AggregationType.SIMMIN.toString())) {
						if (minSim > sim)
							minSim = sim;
					} else if (aggregationType.equals(AggregationType.SIMAVERAGE.toString()))
						sumSim += sim;
					else if (aggregationType.equals(AggregationType.SIMWEIGHTED.toString()))
						weightedSim += sim * weights[l];
				}

				if (aggregationType.equals(AggregationType.SIMMAX.toString()))
					simMatrix[i][j] = maxSim;
				else if (aggregationType.equals(AggregationType.SIMMIN.toString()))
					simMatrix[i][j] = minSim;
				else if (aggregationType.equals(AggregationType.SIMAVERAGE.toString()))
					simMatrix[i][j] = (sumSim / k);
				else if (aggregationType.equals(AggregationType.SIMWEIGHTED.toString()))
					simMatrix[i][j] = weightedSim;
			}//end for
		}//end for
		return simMatrix;
	}//end aggregate()	
	
	
	protected float aggregate(float[] sim) {
		logger.debug("in aggregate");
		//[childMatchers]

		//TODO: same code as in float[][] aggregate(float[][][])

		String aggregationType = null;
		float[] weights = null;

		if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE)) {
			aggregationType = controlParameters.get(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE).getValue();
		}

		if (aggregationType == null)
			aggregationType = AggregationType.SIMAVERAGE.toString(); //set avg as default

		if (aggregationType.equals(AggregationType.SIMWEIGHTED.toString())) {
			weights = new float[sim.length];
			int noWeights = 0;

			//TODO not the best method, as if more weights required will have to add them to ControlParameterType and add more code here ... think about this
			//assume weights are in the correct order with respect to the childmatchers - issue if not
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT1)) {
				weights[0] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT1).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT2)) {
				weights[1] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT2).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT3)) {
				weights[2] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT3).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT4)) {
				weights[3] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT4).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT5)) {
				weights[4] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT5).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT6)) {
				weights[5] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT6).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT7)) {
				weights[6] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT7).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT8)) {
				weights[7] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT8).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT9)) {
				weights[8] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT9).getValue()).floatValue();
				noWeights++;
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT10)) {
				weights[9] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT10).getValue()).floatValue();
				noWeights++;
			}

			if (noWeights < sim.length)
				logger.error("some weights seem to be missing - TODO sort this");
		}

		float finalSim = -1F;
		if (sim == null)
			return -1F;

		float maxSim = 0.0F;
		float minSim = 100000.0F;
		float sumSim = 0.0F;
		float weightedSim = 0.0F;

		for (int l = 0; l < sim.length; ++l) {
			float similarity = sim[l];
			if (aggregationType.equals(AggregationType.SIMMAX.toString())) {
				if (maxSim < similarity)
					maxSim = similarity;
			} else if (aggregationType.equals(AggregationType.SIMMIN.toString())) {
				if (minSim > similarity)
					minSim = similarity;
			} else if (aggregationType.equals(AggregationType.SIMAVERAGE.toString()))
				sumSim += similarity;
			else if (aggregationType.equals(AggregationType.SIMWEIGHTED.toString()))
				weightedSim += similarity * weights[l];
		}

		if (aggregationType.equals(AggregationType.SIMMAX.toString()))
			finalSim = maxSim;
		else if (aggregationType.equals(AggregationType.SIMMIN.toString()))
			finalSim = minSim;
		else if (aggregationType.equals(AggregationType.SIMAVERAGE.toString()))
			finalSim = (sumSim / sim.length);
		else if (aggregationType.equals(AggregationType.SIMWEIGHTED.toString()))
			finalSim = weightedSim;

		logger.debug("finalSim: " + finalSim);
		return finalSim;
	}

	/**
	 * This method is responsible to create a similarity cube [][][]array out of the ArrayList that keeps the [][] sim matrices.
	 * 
	 * [childMatchers][setConstructs1][setConstructs2]
	 * 
	 * @return float[][][] - similarity cube.
	 */
	public float[][][] createSimCube(ArrayList<MatcherInfo> simCubeAsList) {
		logger.debug("in createSimCube()");
		if (simCubeAsList == null || simCubeAsList.size() == 0) {
			logger.debug("simCubeAsList is empty, return");
			return null;
		}
		
		/*Create a 3d array to hold [childMatchers][setConstructs1][setConstructs2]*/
		float[][][] simCube = new float[simCubeAsList.size()][][];
		
		for (MatcherInfo matcherInfo : simCubeAsList) {
			simCube[childMatchers.indexOf(matcherInfo)] = matcherInfo.getSimMatrix();			
		}//end for
		
		logger.debug("simCube: " + simCube);
		return simCube;	
	}//end createSimCube()	
		
	/**
	 * Method responsible for making the matches persistent
	 * @param matches
	 */
	protected void saveMatches(List<Matching> matches) {
		logger.info("in saveMatches");
		logger.info("matches.size(): " + matches.size());
		for (Matching matching : matches) {
			if (matching instanceof OneToOneMatching) {
				OneToOneMatching oneToOne = (OneToOneMatching) matching;
				//logger.debug("oneToOne.getConstruct1(): " + oneToOne.getConstruct1());
				//logger.debug("oneToOne.getConstruct2(): " + oneToOne.getConstruct2());
				//logger.debug("oneToOne.getConstruct1().getName(): " + oneToOne.getConstruct1().getName());
				//logger.debug("oneToOne.getConstruct2().getName(): " + oneToOne.getConstruct2().getName());
				//logger.debug("oneToOne.getScore(): " + oneToOne.getScore());
				//logger.debug("oneToOne.getMatcherName(): " + oneToOne.getMatcherName());
			}
			matchingService.addMatching(matching);
		}//end for
	}//end saveMatches()	
	
	
	/******************
	 * STATIC METHODS *
	 ******************/
	public static float[][] selectMethod(float[][] simMatrix, Map<ControlParameterType, ControlParameter> controlParameters) {
		logger.debug("in select");
		//[constructs1][constructs2]
		//default
		DirectionType direction = DirectionType.BOTH;
		String selectionType = null;
		SelectionType selection = null;
		int candidateNumber = -1; //MAXN
		float delta = -1.0F; //delta
		float threshold = -1.0F; //threshold

		if (controlParameters.containsKey(ControlParameterType.MATCH_SELECT_SELECTION_TYPE)) {
			selectionType = controlParameters.get(ControlParameterType.MATCH_SELECT_SELECTION_TYPE).getValue();
		}
		
		/**
		 * Select all syntactic matrices, usually used when this matrices are used with semantic matrices.
		 */
		if (selectionType.equals(SelectionType.SELECT_ALL.toString())) {
			return simMatrix;
		}		
		
		if (selectionType == null)
			selectionType = SelectionType.THRESHOLD.toString(); //set maxn as default with threshold = 0.3

		if (selectionType.equals(SelectionType.MAXN.toString())) {
			selection = SelectionType.MAXN;
			if (controlParameters.containsKey(ControlParameterType.MATCH_SELECT_MAXN_CANDIDATE_NUMBER)) {
				candidateNumber = new Integer(controlParameters.get(ControlParameterType.MATCH_SELECT_MAXN_CANDIDATE_NUMBER).getValue()).intValue();
			}
			if (candidateNumber == -1)
				logger.error("didn't find candidateNumber for MAXN - TODO sort this");
		} else if (selectionType.equals(SelectionType.MAXDELTA.toString())) {
			selection = SelectionType.MAXDELTA;
			if (controlParameters.containsKey(ControlParameterType.MATCH_SELECT_MAXDELTA_DELTA)) {
				delta = new Float(controlParameters.get(ControlParameterType.MATCH_SELECT_MAXDELTA_DELTA).getValue()).floatValue();
			}
			if (delta == -1.0)
				logger.error("didn't find delta for MAXDELTA - TODO sort this");
		} else if (selectionType.equals(SelectionType.THRESHOLD.toString())) {
			selection = SelectionType.THRESHOLD;
			if (controlParameters.containsKey(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE)) {
				threshold = new Float(controlParameters.get(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE).getValue()).floatValue();
			}
			if (threshold == -1.0) {
				logger.error("didn't find threshold for THRESHOLD - set 0.3 as default");
				threshold = 0.3F;
			}
		} else if (selectionType.equals(SelectionType.MULTIPLE.toString())) {
			if (controlParameters.containsKey(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE)) {
				threshold = new Float(controlParameters.get(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE).getValue()).floatValue();
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_SELECT_MAXDELTA_DELTA)) {
				delta = new Float(controlParameters.get(ControlParameterType.MATCH_SELECT_MAXDELTA_DELTA).getValue()).floatValue();
			}
			if (controlParameters.containsKey(ControlParameterType.MATCH_SELECT_MAXN_CANDIDATE_NUMBER)) {
				candidateNumber = new Integer(controlParameters.get(ControlParameterType.MATCH_SELECT_MAXN_CANDIDATE_NUMBER).getValue()).intValue();
			}
			if (threshold == -1.0)
				logger.error("didn't find threshold for MULTIPLE - TODO sort this");
			if (delta == -1.0)
				logger.error("didn't find delta for MULTIPLE - TODO sort this");
			if (candidateNumber == -1)
				logger.error("didn't find candidateNumber for MULTIPLE - TODO sort this");
		}

		float[][] selMatrix = null;
		if (selection.equals(SelectionType.MULTIPLE)) {
			selMatrix = selectMultipleStatic(simMatrix, direction, selection, candidateNumber, delta, threshold);
		} else {
			selMatrix = selectSingleStatic(simMatrix, direction, selection, candidateNumber, delta, threshold);
		}
		return selMatrix;
	}		
	
	public static float[][] selectMultipleStatic(float[][] simMatrix, DirectionType direction, SelectionType selection, int candidateNumber, float delta,
			float threshold) {
		logger.debug("in selectMultiple");
		logger.debug("simMatrix: " + simMatrix);
		logger.debug("direction: " + direction);
		logger.debug("selection: " + selection);
		logger.debug("candidateNumber: " + candidateNumber);
		logger.debug("delta: " + delta);
		logger.debug("threshold: " + threshold);
		//[constructs1][constructs2]
		if ((simMatrix == null) || (simMatrix.length == 0))
			return null;
		if ((candidateNumber <= 0) && (delta <= 0.0F) && (threshold <= 0.0F)) {
			return simMatrix;
		}

		int m = simMatrix.length;
		int n = simMatrix[0].length;

		float[][] selMatrix = new float[m][n];
		for (int i = 0; i < m; ++i)
			System.arraycopy(simMatrix[i], 0, selMatrix[i], 0, n);

		float[] forwardSelSim = new float[m];
		float[] backwardSelSim = new float[n];
		for (int i = 0; i < m; ++i) {
			float[] sortedSims = new float[n];
			for (int j = 0; j < n; ++j)
				sortedSims[j] = selMatrix[i][j];
			Arrays.sort(sortedSims);
			if (candidateNumber > 0) {
				if (candidateNumber > n) {
					forwardSelSim[i] = sortedSims[0];
				} else
					forwardSelSim[i] = sortedSims[(n - candidateNumber)];
			}
			if (delta > 0.0F) {
				float sim = sortedSims[(n - 1)] - sortedSims[(n - 1)] * delta;
				if (forwardSelSim[i] < sim)
					forwardSelSim[i] = sim;
			}
			if (threshold > 0.0F && forwardSelSim[i] < threshold)
				forwardSelSim[i] = threshold;
		}

		for (int j = 0; j < n; ++j) {
			float[] sortedSims = new float[m];
			for (int i = 0; i < m; ++i)
				sortedSims[i] = selMatrix[i][j];
			Arrays.sort(sortedSims);
			float maxSim = sortedSims[(m - 1)];

			if (candidateNumber > 0) {
				if (candidateNumber > m)
					backwardSelSim[j] = sortedSims[0];
				else
					backwardSelSim[j] = sortedSims[(m - candidateNumber)];
			}
			if (delta > 0.0F) {
				float sim = maxSim - maxSim * delta;
				if (backwardSelSim[j] < sim)
					backwardSelSim[j] = sim;
			}
			if (threshold > 0.0F && backwardSelSim[j] < threshold)
				backwardSelSim[j] = threshold;
		}

		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				if (selMatrix[i][j] < forwardSelSim[i])
					selMatrix[i][j] = 0.0F;
			}
		}

		for (int j = 0; j < n; ++j) {
			for (int i = 0; i < m; ++i) {
				if (selMatrix[i][j] < backwardSelSim[j])
					selMatrix[i][j] = 0.0F;
			}
		}
		return selMatrix;
	}

	public static float[][] selectSingleStatic(float[][] simMatrix, DirectionType direction, SelectionType selection, int candidateNumber, float delta,
			float threshold) {
		logger.debug("in selectSingle");
		logger.debug("simMatrix: " + simMatrix);
		logger.debug("direction: " + direction);
		logger.debug("selection: " + selection);
		logger.debug("candidateNumber: " + candidateNumber);
		logger.debug("delta: " + delta);
		logger.debug("threshold: " + threshold);
		//[constructs1][constructs2]
		if (simMatrix == null)
			return null;
		if (selection.equals(SelectionType.MAXN) && candidateNumber <= 0)
			return simMatrix;
		if (selection.equals(SelectionType.MAXDELTA) && delta <= 0.0F)
			return simMatrix;
		if (selection.equals(SelectionType.THRESHOLD) && threshold <= 0.0F)
			return simMatrix;

		int m = simMatrix.length;
		if ((m == 0) || (simMatrix[0] == null)) {
			return null;
		}
		int n = simMatrix[0].length;

		float[][] selMatrix = new float[m][n];
		for (int i = 0; i < m; ++i)
			System.arraycopy(simMatrix[i], 0, selMatrix[i], 0, n);

		float[] forwardSelSim = new float[m];
		float[] backwardSelSim = new float[n];

		if (selection.equals(SelectionType.THRESHOLD)) {
			for (int i = 0; i < m; ++i) {
				forwardSelSim[i] = threshold;
			}
			for (int j = 0; j < n; ++j)
				backwardSelSim[j] = threshold;
		} else {
			for (int i = 0; i < m; ++i) {
				float[] sortedSims = new float[n];
				for (int j = 0; j < n; ++j)
					sortedSims[j] = selMatrix[i][j];
				Arrays.sort(sortedSims);
				if (selection.equals(SelectionType.MAXN)) {
					if (candidateNumber > n)
						forwardSelSim[i] = sortedSims[0];
					else {
						forwardSelSim[i] = sortedSims[(n - candidateNumber)];
					}
				} else if (selection.equals(SelectionType.MAXDELTA)) {
					forwardSelSim[i] = (sortedSims[(n - 1)] - sortedSims[(n - 1)] * delta);
				}
			}
			for (int j = 0; j < n; ++j) {
				float[] sortedSims = new float[m];
				for (int i = 0; i < m; ++i)
					sortedSims[i] = selMatrix[i][j];
				Arrays.sort(sortedSims);

				if (selection.equals(SelectionType.MAXN)) {
					if (candidateNumber > m)
						backwardSelSim[j] = sortedSims[0];
					else {
						backwardSelSim[j] = sortedSims[(m - candidateNumber)];
					}
				} else if (selection.equals(SelectionType.MAXDELTA)) {
					backwardSelSim[j] = (sortedSims[(m - 1)] - sortedSims[(m - 1)] * delta);
				}
			}
		}

		if (selection.equals(SelectionType.THRESHOLD)) {
			for (int i = 0; i < m; ++i) {
				for (int j = 0; j < n; ++j) {
					if (selMatrix[i][j] < forwardSelSim[i])
						selMatrix[i][j] = 0.0F;
				}
			}

			for (int j = 0; j < n; ++j) {
				for (int i = 0; i < m; ++i) {
					if (selMatrix[i][j] < backwardSelSim[j])
						selMatrix[i][j] = 0.0F;
				}
			}
		}
		return selMatrix;
	}	
	
	//-----------------------index-----------------

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherService#getIndex()
	 */
	public int getIndex() {
		return index;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherService#setIndex(int)
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	//-----------------------controlParameters-----------------

	public Map<ControlParameterType, ControlParameter> getControlParameters() {
		return Collections.unmodifiableMap(controlParameters);
	}

	public void setControlParameters(Map<ControlParameterType, ControlParameter> controlParameters) {
		this.controlParameters = controlParameters;
	}

	public void addControlParameter(ControlParameter controlParameter) {
		this.controlParameters.put(controlParameter.getName(), controlParameter);
		//controlParameter.internalSetMatcher(this);
	}

	/*
	public void internalAddControlParameter(ControlParameter controlParameter) {
		this.controlParameters.put(controlParameter.getName(), controlParameter);
	}
	*/

	public void removeControlParameter(ControlParameter controlParameter) {
		this.controlParameters.remove(controlParameter.getName());
		//controlParameter.internalSetMatcher(null);
	}

	/*
	public void internalRemoveControlParameter(ControlParameter controlParameter) {
		this.controlParameters.remove(controlParameter.getName());
	}
	*/

	//-----------------------parentMatcher-----------------

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherService#getParentMatcher()
	 */
	public MatcherService getParentMatcher() {
		return parentMatcher;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherService#setParentMatcher(uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherService)
	 */
	public void setParentMatcher(MatcherService parentMatcher) {
		this.parentMatcher = parentMatcher;
		parentMatcher.internalAddChildMatcher(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherService#internalSetParentMatcher(uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherService)
	 */
	public void internalSetParentMatcher(MatcherService parentMatcher) {
		this.parentMatcher = parentMatcher;
	}

	//-----------------------childMatchers-----------------

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherService#getChildMatchers()
	 */
	public List<MatcherService> getChildMatchers() {
		return Collections.unmodifiableList(childMatchers);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherService#setChildMatchers(java.util.List)
	 */
	public void setChildMatchers(List<MatcherService> childMatchers) {
		this.childMatchers = childMatchers;
		for (MatcherService childMatcher : childMatchers)
			childMatcher.internalSetParentMatcher(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherService#addChildMatcher(uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherServiceImpl)
	 */
	public void addChildMatcher(MatcherService childMatcher) {
		this.childMatchers.add(childMatcher);
		childMatcher.internalSetParentMatcher(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherService#internalAddChildMatcher(uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherServiceImpl)
	 */
	public void internalAddChildMatcher(MatcherService childMatcher) {
		this.childMatchers.add(childMatcher);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherService#removeChildMatcher(uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherService)
	 */
	public void removeChildMatcher(MatcherService childMatcher) {
		this.childMatchers.remove(childMatcher);
		childMatcher.internalSetParentMatcher(null);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherService#internalRemoveChildMatcher(uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherService)
	 */
	public void internalRemoveChildMatcher(MatcherService childMatcher) {
		this.childMatchers.remove(childMatcher);
	}

	//-----------------------matchings-----------------

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherService#getMatchings()
	 */
	public List<Matching> getMatchings() {
		return matchings;
	}

	public boolean addMatchings(List<Matching> matchings) {
		return this.matchings.addAll(matchings);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherService#setMatchings(java.util.Set)
	 */
	public void setMatchings(List<Matching> matchings) {
		this.matchings = matchings;
	}

	//-----------------------name-----------------

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param matcherType the matcherType to set
	 */
	public void setMatcherType(MatcherType matcherType) {
		this.matcherType = matcherType;
	}

	/**
	 * @return the matcherType
	 */
	public MatcherType getMatcherType() {
		return matcherType;
	}	
	

}
