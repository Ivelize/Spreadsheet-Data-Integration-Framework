/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.meta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.SchematicCorrespondence;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.GenericEntityService;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherInfo;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixCellIndex;
import uk.ac.manchester.dstoolkit.service.impl.util.TDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.BooleanVariables;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.distributions.ProbabilityMassFunction;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;
import uk.ac.manchester.dstoolkit.service.util.importexport.GraphvizDotGeneratorService;

/**
 * @author chedeler
 *
 */
public interface SchemaService extends GenericEntityService<Schema, Long> {

	//TODO add match 
	//TODO add infer correspondences
	//TODO add parameters to operators, parameterlist could be empty, in which case sensible defaults need to be chosen
	//TODO need to be able to get correspondences generated by merge
	//TODO check what happens in merge and compose when we've got correspondences between more than two schemas (e.g. partitioning)

	public List<Matching> match(Schema schema1, Schema schema2, Map<ControlParameterType, ControlParameter> controlParameters);

	public List<Matching> match(Schema schema1, Schema schema2, List<MatcherService> matchers,
			Map<ControlParameterType, ControlParameter> controlParameters);
	
	public List<MatcherInfo> runMatch(Schema sourceSchema, Schema targetSchema, List<MatcherService> matchers,
			Map<ControlParameterType, ControlParameter> controlParameters);

	public List<Matching> match(Schema schema1, Schema schema2, List<MatcherService> matchers, DataSource ds1, DataSource ds2, Map<ControlParameterType, ControlParameter> controlParameters)
			throws ExecutionException;
	
	public void schemaEnrichment(Schema schema1, Schema schema2);

	public Schema merge(Schema schema1, Schema schema2, Set<SchematicCorrespondence> schematicCorrespondencesBetweenSchema1AndSchema2);
	
	public Schema merge(Set<SchematicCorrespondence> schematicCorrespondenceBetweenSchema1AndSchema2);
	
	public Set<Schema> extract(List<SchematicCorrespondence> sc);
	
	public void generateResultsToGraph(List<SchematicCorrespondence> sc) throws IOException;
	
	public Set<SchematicCorrespondence> diff(Schema schema1, Schema schema2, Set<SchematicCorrespondence> schematicCorrespondencesBetweenSchema1AndSchema2);
	
	public Set<Mapping> viewGen(Set<SchematicCorrespondence> schematicCorrespondencesBetweenSchema1AndSchema2);
	
	public Mapping viewGen(Map<Integer, SchematicCorrespondence> sc);

	public void inferCorrespondence(Set<Schema> sourceSchemas, Set<Schema> targetSchemas, List<Matching> matchings,
			Map<ControlParameterType, ControlParameter> controlParameters);
	
	public Set<SchematicCorrespondence> inferCorrespondences(Set<Schema> sourceSchemas, Set<Schema> targetSchemas, List<Matching> matchings,
			Map<ControlParameterType, ControlParameter> controlParameters);
	
	public void createConditionHP(HashMap<SuperLexical, ArrayList<String>> source);

	public Schema getSchemaByName(String schemaName);
	
	public Schema getMinimumModel();
	
	public Integer getModelRepresentativity(SchematicCorrespondence sc);
	
	public void createAttributesCorrelation(List<SchematicCorrespondence> sc) throws IOException;

	//TODO add method for adding schemas (manually specified integration schemas) for XSD and SQL DDL

	/**
	 * @param schemaId
	 * @return
	 */
	public Schema findSchema(Long schemaId);

	/**
	 * @param schema
	 */
	public void addSchema(Schema schema);

	/**
	 * @param schemaId
	 */
	public void deleteSchema(Long schemaId);
	
	public List<SemanticMatrix> organiseMetadata(Schema sourceSchema, Schema targetSchema, List<SemanticMetadataService> semMatricesToCreate);

	public SemanticMatrix generateExpectationMatrix(Schema schema1, Schema schema2, String alignUrl);
	public SemanticMatrix generateExpectationMatrix(Schema schema1, Schema schema2, TDBStoreServiceImpl tdbStore, String expModelURI);
	
	public GraphvizDotGeneratorService getGraphvizDotGeneratorService();
	
	/***
	 * Accumulate evidences using Baye's
	 */

	public SemanticMatrix accumulateSyntacticEvidenceBayes(Schema sourceSchema, Schema targetSchema, List<MatcherInfo> syntacticCube,
			Map<ControlParameterType, ControlParameter> controlParameters) throws IOException;
	

	public SemanticMatrix accumulateSemEvidenceBayes(SemanticMatrix synBayesMatrix, Schema sourceSchema, Schema targetSchema, 
			List<SemanticMatrix> semanticCube, Map<BooleanVariables, ProbabilityMassFunction> pmfList,
			Set<BooleanVariables> evidencesToInclude, Map<ControlParameterType,
			ControlParameter> controlParameters);

	public List<Matching> produceAndSaveMatchings(Schema sourceSchema, Schema targetSchema, List<MatcherInfo> simCubeOfMatchers,
			Map<ControlParameterType, ControlParameter> controlParameters);
	
	public List<Matching> produceMatchesForSpecificCells(Schema sourceSchema, Schema targetSchema,
				final float[][] simMatrix, final Map<ControlParameterType, ControlParameter> controlParameters,
				Set<SemanticMatrixCellIndex> cellsSet, final MatcherService matcherService);
}