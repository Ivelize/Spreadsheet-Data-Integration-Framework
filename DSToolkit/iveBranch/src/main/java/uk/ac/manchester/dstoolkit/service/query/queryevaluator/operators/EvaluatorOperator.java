package uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators;

import java.util.Set;

import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.JoinOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.Predicate;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.exceptions.OperatorException;

public interface EvaluatorOperator {

	/** initializes the operator and calls open on the input
	 * @throws OperatorException throws a general exception in case of any error
	 * @return boolean - true/false denoting success/failure
	 * @throws Exception 
	 */
	public boolean open(); //throws Exception;

	/** Fetches the next result by calling next on the input operator
	 * @throws OperatorException thrown if there is a problem of the next on input fails
	 * @return the next tuple after computation in the current operator
	 * @throws OperatorException 
	 * @throws InterruptedException 
	 */
	public ResultInstance next(); //throws InterruptedException, OperatorException;

	/** closes the open inputs, streams and the current operator
	 * @return true/false
	 */
	public boolean close();

	public ResultType getResultType();

	public long getCardinality();

	public DataSource getDataSource();

	public Set<Predicate> getJoinPredicatesCarried();

	public void setDataSource(DataSource dataSource);

	public void setJoinPredicatesCarried(Set<Predicate> joinPredicatesCarried);

	public void setResultType(ResultType resultType);

	public EvaluatorOperator getLhsInput();

	public EvaluatorOperator getRhsInput();

	public String getVariableName();

	public void setVariableName(String variableName);

	public EvaluatorOperator getInput();

	public Set<JoinOperator> getJoinOperatorsCarried();

	public void setJoinOperatorsCarried(Set<JoinOperator> joinOperatorsCarried);

	public void replaceInput(EvaluatorOperator oldOperator, EvaluatorOperator newOperator);

	public String getAndOr();

	public void setQueryString(String queryString);

	public void setMappingsUsedForExpansion(Set<Mapping> mappingsUsedForExpansion);

	public void addMappingUsedForExpansion(Mapping mappingUsedForExpansion);

	public void addAllMappingsUsedForExpansion(Set<Mapping> mappingsUsedForExpansion);

	public Set<Mapping> getMappingsUsedForExpansion();

	public void setMaxNumberOfResults(int maxNumberOfResults);

	public void setFetchSize(int fetchSize);
}
