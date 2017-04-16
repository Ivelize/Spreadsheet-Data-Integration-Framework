package uk.ac.manchester.dstoolkit.service.util.training;

import java.util.Map;
import java.util.Set;

import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixCellIndex;
import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.TDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.ErrorMeasures;
import uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation.PerformanceErrorTypes;
import uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation.PerformanceMeasures;

public interface EvaluatorService {	
	
	public Map<PerformanceErrorTypes, Float> calculatePerformance(float[][] predictedMatrix,  float[][] observedMatrix);
	public Map<PerformanceErrorTypes, Float> calculatePerformance(float[][] predictedMatrix,
															float[][] observedMatrix, Set<SemanticMatrixCellIndex> iSet);
	
	public void attactAggrErrMeasure(PerformanceMeasures pm);
	
	public void attactSingErrMeasure(ErrorMeasures em);	
	
	public void runNumericExpSyntacticOnly(String filePath, Schema testSchema1, Schema testSchema2, 
												Map<ControlParameterType, ControlParameter> controlParameters);
	
	public SemanticMatrix runBayesFromConfigFilesTopK(String filePath, Schema testSchema1, Schema testSchema2);

	public void runBayesFromConfigFilesTopKrecursive(String filePath, Schema testSchema1, Schema testSchema2,
			 String alignmentPropLoc,
			 SDBStoreServiceImpl metaDataSDBStore, TDBStoreServiceImpl tdbStore,
			 String DATA_ANALYSIS, String EVID_CLASSES, String EVID_PROPS, String ENDPOINT_DATA,
			 Map<ControlParameterType, ControlParameter> controlParameters);
	
	public void runBayesFromConfigFiles(String filePath, Schema testSchema1, Schema testSchema2,
			SDBStoreServiceImpl metaDataSDBStore, TDBStoreServiceImpl graphsTDBStore,
			String DATA_ANALYSIS, String EVID_CLASSES, String EVID_PROPS, String ENDPOINT_DATA);

}
