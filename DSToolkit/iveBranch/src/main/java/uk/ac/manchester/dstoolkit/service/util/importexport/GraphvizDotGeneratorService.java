/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.util.importexport;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.MappingOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.domain.user.User;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherInfo;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.BooleanVariables;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.ErrorMeasures;
import uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation.PerformanceErrorTypes;

/**
 * @author chedeler
 * @author klitos
 *
 */
public interface GraphvizDotGeneratorService {

	public String returnLocation();
	
	public abstract String generateDot(Set<Mapping> mappings, User currentUser);

	public abstract String generateDot(MappingOperator queryRootOperator, User currentUser);

	public abstract String generateDot(List<SuperAbstract> sourceSuperAbstracts, List<SuperAbstract> targetSuperAbstracts, List<Matching> matchings,
			boolean showMatchingScore, User currentUser);

	public abstract String generateDot(Schema schema);
	
	public String generateDot(Schema schema, String align, boolean showURI, boolean showStatus);
	
	public File exportAsDOTFile(String inputDotString, String dirName, String fileName);
	
	public void readExternalDOTFile(String inputDOTFile);
	
	public void exportDOT2PNG(File dot, String imageType, String fileName, String osType);
	
	public void generateDOTSyn(List<CanonicalModelConstruct> sourceConstructs,	List<CanonicalModelConstruct> targetConstructs,
			List<MatcherInfo> simCubeOfMatchers);
        
    public void generateDOTSem(List<CanonicalModelConstruct> sourceConstructs,	List<CanonicalModelConstruct> targetConstructs,
							List<SemanticMatrix> semMatrixCube); 
	
	public void generateDOT(List<CanonicalModelConstruct> sourceConstructs,	List<CanonicalModelConstruct> targetConstructs,
			float[][] syntacticSimMatrix, String fileName);
	
	public void expectationMatrixDOTSem(List<CanonicalModelConstruct> sourceConstructs, List<CanonicalModelConstruct> targetConstructs,
			SemanticMatrix matrix, boolean perc);

	public void generateDOTBayes(List<CanonicalModelConstruct> sourceConstructs, List<CanonicalModelConstruct> targetConstructs,
			 SemanticMatrix matrix, List<MatcherInfo> syntacticCube, Set<BooleanVariables> evidencesToAccumulate,  boolean perc);
	
	public void exportToCSVFile(Map<PerformanceErrorTypes, Float> error1, Map<PerformanceErrorTypes, Float> error2,
			Set<BooleanVariables> evidencesToAccumulate, MatcherType mt, Map<ControlParameterType, ControlParameter> controlParameters);
	
	public void exportToCSVFileOnlySyntacticEvidence(Map<PerformanceErrorTypes, Float> error1, Map<PerformanceErrorTypes, Float> error2,
			MatcherType mt,	Map<ControlParameterType, ControlParameter> controlParameters);
	
	public void exportIndividualPairErrorsSynOnly(float[][] error1, float[][] error2, MatcherType mt, ErrorMeasures measure,
			Map<ControlParameterType, ControlParameter> controlParameters);
	
	public void exportToCSVFileSynSemEvidence(Map<PerformanceErrorTypes, Float> error1, Map<PerformanceErrorTypes, Float> error2,
            Map<ControlParameterType, ControlParameter> controlParameters);
	
	public void exportIndividualPairErrorsSynSem(float[][] error1, float[][] error2, ErrorMeasures measure, MatcherType mt,
			Map<ControlParameterType, ControlParameter> controlParameters,
			Set<BooleanVariables> evidencesToAccumulate);
	
	public void generateDOT(Schema sourceSchema, Schema targetSchema, float[][] syntacticSimMatrix, String fileName);
	
	public void plotPosteriorsPMF(String fileName, String termType);

}