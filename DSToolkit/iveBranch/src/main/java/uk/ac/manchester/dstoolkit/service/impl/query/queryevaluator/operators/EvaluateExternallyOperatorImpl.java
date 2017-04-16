package uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.xmldb.api.base.CompiledExpression;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XQueryService;

import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.ModelType;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.Predicate;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.exceptions.OperatorException;
import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.RDFModel.SPARQLDataTranslatorServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.relational.RelationalDataTranslatorServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.xml.XMLDataTranslatorServiceImpl;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators.EvaluatorOperator;
import uk.ac.manchester.dstoolkit.service.util.ExternalDataSourcePoolUtilService;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryFactory;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * @author chedeler
 * @author ruhaila
 *
 * Revision (klitos):
 *  1. Add RDF in runQuery()
 */

//@Transactional(readOnly = true)
@Scope("prototype")
@Service
public class EvaluateExternallyOperatorImpl extends EvaluatorOperatorImpl {

	private static Logger logger = Logger.getLogger(EvaluateExternallyOperatorImpl.class);

	//TODO add means for retrieving only subset of results at a time ... check for relational and XML

	private ExternalDataSourcePoolUtilService externalDataSourcePoolUtilService;

	private EvaluatorOperator planRootEvaluatorOperator;

	private String queryString;
	//private DataSource dataSourceId; //TODO change this after I've changed the externaldatasourcepoolutil
	private String dataSourceName;
    private String schemaName;
	private ModelType modelType;

	/** counter on the number of blocks returned */
	private int numberOfBlocksReturned;
	/** counter on the number of resultInstances returned */
	private long numberOfTuplesReturned;
	/** ResultInstance list from which the next() method gets the next resultInstance**/
	private List<ResultInstance> resultInstanceList = new LinkedList<ResultInstance>();
	/** ResultInstance list from which the next() method gets the next tuple**/
	private LinkedList<ResultInstance> resultInstanceSwapQueue;
	private LinkedList<ResultInstance> resultInstanceQueue;
	private boolean ateof;
	private int blocksPerCall;
	private boolean EOFReceived = false;
	/** denotes if the block from GDS is part of the header */
	private boolean isHeader;

	/**
	 *
	 */
	public EvaluateExternallyOperatorImpl() {
		super();
	}

	public EvaluateExternallyOperatorImpl(EvaluatorOperator planRootEvaluatorOperator, ResultType resultType, long cardinality,
			Set<Predicate> joinPredicatesCarried, DataSource dataSource) { //int blocksPerCall, boolean isRoot
		super(resultType, cardinality, joinPredicatesCarried, dataSource);
		logger.debug("evaluateExternallyOperatorImpl.resultType: " + this.getResultType());
		this.setPlanRootEvaluatorOperator(planRootEvaluatorOperator);
        this.dataSourceName = dataSource.getName();
		this.schemaName = dataSource.getSchema().getName();
        this.modelType = dataSource.getSchema().getModelType();
		//TODO these two aren't used
		this.numberOfBlocksReturned = 0;
		this.numberOfTuplesReturned = 0;
		this.setHeader(true);
	}

    /*Changed*/
	public EvaluateExternallyOperatorImpl(String dataSourceName, String schemaName, String queryString, ModelType modelType) {
		this.dataSourceName = dataSourceName;
		this.schemaName = schemaName;
		this.queryString = queryString;
		this.modelType = modelType;
	}

	/** Builds the script used for the GDS and calls the GDS to retrieve the data from
	 * the DB. Parses the rowset and stores the result in vectors in a linked list.
	 * @throws OperatorException thrown in case of any error
	 * @return true/false
	 */
	public boolean open() { //throws OperatorException {
		logger.debug("in EvaluateExternallyOperatorImpl.open");
		logger.debug("Entering EvaluateExternallyOperatorImpl: open: " + this.toString());
		logger.debug("EvaluateExternallyOperatorImpl: Creating RowHandler..." + this.toString());
		resultInstanceQueue = new LinkedList<ResultInstance>();
		resultInstanceSwapQueue = new LinkedList<ResultInstance>();
		ateof = false;
		logger.debug("EvaluateExternallyOperatorImpl: Query string: " + this.getQueryString() + " - this.toString(): " + this.toString());
		logger.debug("EvaluateExternallyOperatorImpl: this.getMappingsUsedForExpansion(): " + this.getMappingsUsedForExpansion());
		/*
		try {
			GDSAccessor = new AsynchGDSAccessor("AsynchGDSBlockScan", this,
					blocksPerCall);
			GDSAccessor.start();
		} catch (Exception ex) {
			mLog
					.error(this.operatorId
							+ ": Exception running GDS in Scan::Open: "
							+ ex.toString());
			throw new OperatorException(TypeDefs.OPERATOR_EXCEPTION, mLog,
					this.operatorId);
		}
		mLog.debug("Exiting TableScanOp:" + this.operatorId + ":Open");
		return true;
		*/
        //List<ResultInstance> resultInstances = runQuery(dataSourceName, queryString);
 	    List<ResultInstance> resultInstances = runQuery(dataSourceName, schemaName, queryString);
		addToResultInstanceQueue(resultInstances);
		//resultInstanceList.addAll(resultInstances);
		//ruhai
		if (resultInstances != null)
			logger.debug("Processed " + resultInstances.size() + " resultInstances");
		setEOFReceived(true);
		logger.debug("leaving EvaluateExternallyOperatorImpl.open");
		return true;
	}

	/** iterates over the result and returns each row in form of
	 * a result instance.
	 * @return output result instance
	 * @throws OperatorException 
	 * @throws OperatorException in case of any error
	 */
	public ResultInstance next() { //throws OperatorException {
		logger.debug("in EvaluateExternallyOperatorImpl.next");
		logger.debug(toString());
		logger.debug("this.getMappingsUsedForExpansion(): " + this.getMappingsUsedForExpansion());
		//try {
		/*
		ResultInstance nextInstance = resultInstanceList.get(0);
		resultInstanceList.remove(nextInstance);
		logger.debug("leaving EvaluateExternallyOperatorImpl.next");
		return nextInstance;
		*/
		ResultInstance nextInstance = removeFromResultInstanceQueue();
		nextInstance.addAllMappings(this.getMappingsUsedForExpansion());
		//TODO think about whether there could be multiple mappings here
		logger.debug("this.getMappingsUsedForExpansion(): " + this.getMappingsUsedForExpansion());
		logger.debug("nextInstance: " + nextInstance);
		logger.debug("nextInstance.getMappings(): " + nextInstance.getMappings());
		logger.debug("Exiting  EvaluateExternallyOperatorImpl: next: " + this.toString());
		return nextInstance;
		//} catch (Exception ex) {
		//logger.error("EvaluateExternallyOperatorImpl: Error in getting blocks from the Tuple Queue" + this.toString());
		//throw new OperatorException(0, logger, this.toString());
		//}
	}

	/** closes everything - doesn't kill the GDS.
	 * @return true/false
	 */
	public boolean close() {
		logger.debug("in EvaluateExternallyOperatorImpl.close");
		logger.debug(toString());
		queryString = null;
		resultInstanceList = null;
		resultInstanceSwapQueue = null;
		resultInstanceQueue = null;
		//queryString = null;
		//GDSAccessor = null;
		cleanup();
		logger.debug("leaving EvaluateExternallyOperatorImpl.close");
		logger.debug(toString());
		return true;
	}

	/*
	public List<ResultInstance> getResultInstances() {
		return resultInstanceList;
	}
	*/

	public synchronized void setEOFReceived(boolean flag) {
		logger.debug("in setEOFReceived");
		logger.debug("flag: " + flag);
		EOFReceived = flag;
		//this.notifyAll();
	}

	public synchronized boolean getEOFReceived() {
		logger.debug("in getEOFReceived");
		return EOFReceived;
	}

	public synchronized void addToResultInstanceQueue(Collection<ResultInstance> col) {
		logger.debug("in addToResultInstanceQueue");
		if (col != null) {
			logger.debug("col.size: " + col.size());
			resultInstanceQueue.addAll(col);
		}
		//this.notifyAll();
	}

	public synchronized boolean doRemoveFromResultInstanceQueue() { //throws InterruptedException {
		logger.debug("in doRemoveFromResultInstanceQueue");
		while (resultInstanceQueue.isEmpty() && !getEOFReceived()) {
			logger.debug("resultInstanceQueue.isEmpty() && !getEOFReceived()");
			logger.debug("EvaluateExternallyOperatorImpl: will wait for tuples ..." + toString());
			//wait();
		}
		if (resultInstanceQueue.isEmpty() && getEOFReceived()) {
			logger.debug("resultInstanceQueue.isEmpty() && getEOFReceived()");
			logger.debug("EvaluateExternallyOperatorImpl: Signalling EOF" + toString());
			logger.debug("leaving doRemoveFromResultInstanceQueue, returning false");
			return false;
		} else {
			logger.debug("resultInstanceQueue.isEmpty() && getEOFReceived() - else");
			LinkedList<ResultInstance> tmp = resultInstanceSwapQueue;
			resultInstanceSwapQueue = resultInstanceQueue;
			resultInstanceQueue = tmp;
			logger.debug("leaving doRemoveFromResultInstanceQueue, returning true");
			return true;
		}
	}

	public ResultInstance removeFromResultInstanceQueue() { //throws InterruptedException {
		logger.debug("in removeFromResultInstanceQueue");
		logger.debug("resultInstanceSwapQueue.size(): " + resultInstanceSwapQueue.size());
		if (ateof) {
			logger.debug("is ateof: " + ateof);
			ResultInstance eofResultInstance = new ResultInstance();
			eofResultInstance.setEof(true);
			logger.debug("leaving removeFromResultInstanceQueue, returning new eofResultInstance");
			return eofResultInstance;
		} else {
			logger.debug("is not ateof");
			if (resultInstanceSwapQueue.isEmpty()) {
				logger.debug("resultInstanceSwapQueue is empty");
				if (!doRemoveFromResultInstanceQueue()) {
					ateof = true;
					ResultInstance eofResultInstance = new ResultInstance();
					eofResultInstance.setEof(true);
					logger.debug("leaving removeFromResultInstanceQueue, returning new eofResultInstance");
					return eofResultInstance;
				}
			}
			ResultInstance resultInstanceToReturn = resultInstanceSwapQueue.removeFirst();
			logger.debug("resultInstanceToReturn: " + resultInstanceToReturn);
			return resultInstanceToReturn;
		}
	}

    //Add parameter schemaName
	private List<ResultInstance> runQuery(String dataSourceName, String schemaName, String queryString) {
		logger.debug("in runQuery");
		logger.debug("dataSourceName: " + dataSourceName);
		logger.debug("schemaName: " + schemaName);
		logger.debug("maxNumberOfResults: " + this.getMaxNumberOfResults());
		logger.debug("fetchSize: " + this.getFetchSize());

		//TODO incremental result fetching still needs to be implemented properly to get the next lot of results, currently only using maxNumberOfResults

		if (modelType.equals(ModelType.RELATIONAL)) {
			logger.debug("modelType is RELATIONAL");
			logger.debug("externalDataSourcePoolUtilService: " + externalDataSourcePoolUtilService);
            //add parameter schemaName
			ComboPooledDataSource externalRelationalDataSource = externalDataSourcePoolUtilService.getExternalRelationalDataSource(dataSourceName,
					schemaName);
			if (externalRelationalDataSource == null)
				externalRelationalDataSource = externalDataSourcePoolUtilService.getExternalRelationalDataSource(dataSourceName, null);

			logger.debug("externalRelationalDataSource: " + externalRelationalDataSource);
			JdbcTemplate jdbcTemplate = new JdbcTemplate(externalRelationalDataSource);
			if (this.getMaxNumberOfResults() > 0) {
				logger.debug("this.getMaxNumberOfResults() specified: " + this.getMaxNumberOfResults());
				jdbcTemplate.setMaxRows(this.getMaxNumberOfResults());
			}
			if (this.getFetchSize() > 0) {
				logger.debug("this.getFetchSize() specified: " + this.getFetchSize());
				jdbcTemplate.setFetchSize(this.getFetchSize());
			}
			logger.debug("jdbcTemplate: " + jdbcTemplate);
			logger.debug("queryString: " + queryString);
			long startTime = System.currentTimeMillis();
			logger.debug("startTime: " + startTime);
			SqlRowSet rowSet = jdbcTemplate.queryForRowSet(queryString);
			long gotResultTime = System.currentTimeMillis();
			logger.debug("gotResultTime: " + gotResultTime);
			logger.debug("rowSet: " + rowSet);
			//TODO think about whether this should be autowired or not ... could multiple of the evaluateExternally be trying to access the same translator at the same time if autowired???
			RelationalDataTranslatorServiceImpl dataTranslator = new RelationalDataTranslatorServiceImpl();
			List<ResultInstance> resultInstances = dataTranslator.translateResultSetIntoListOfResultInstances(rowSet, this.getResultType());
			long endTime = System.currentTimeMillis();
			long elapsedTime = endTime - startTime;
			logger.debug("Total time for delivery " + "(including conversion to resultInstances) " + elapsedTime + " ms.");
			logger.debug("resultInstances: " + resultInstances);
			// externalRelationalDataSource.close(); //TODO think about closing the connection, this one here doesn't work
			return resultInstances;
		} else if (modelType.equals(ModelType.XSD)) {
			logger.debug("modelType is XSD");
			logger.debug("externalDataSourcePoolUtilService: " + externalDataSourcePoolUtilService);
			XMLResource externalXMLDataSource = externalDataSourcePoolUtilService.getExternalXmlDataSource(dataSourceName);
			logger.debug("externalXMLDataSource: " + externalXMLDataSource);
			/*
			try {
				logger.debug("xml document content: " + externalXMLDataSource.getContent().toString());
			} catch (XMLDBException e) {
				// TODO Auto-generated catch block
				logger.debug("externalXMLDataSource.getContent().toString():" + e.toString());
			}
			*/
			try {
				org.xmldb.api.base.Collection collection = externalXMLDataSource.getParentCollection();
				logger.debug("collection: " + collection);
				XQueryService xQueryService = (XQueryService) collection.getService("XQueryService", "1.0");
				logger.debug("xQueryService: " + xQueryService);
				CompiledExpression compiledQuery = xQueryService.compile(queryString);
				logger.debug("compiledQuery: " + compiledQuery);
				long startTime = System.currentTimeMillis();
				logger.debug("startTime: " + startTime);
				ResourceSet resourceSet = xQueryService.execute(compiledQuery);
				long gotResultTime = System.currentTimeMillis();
				logger.debug("gotResultTime: " + gotResultTime);
				logger.debug("resourceSet: " + resourceSet);
				//TODO think about whether this should be autowired or not ... could multiple of the evaluateExternally be trying to access the same translator at the same time if autowired???
				XMLDataTranslatorServiceImpl dataTranslator = new XMLDataTranslatorServiceImpl();
				List<ResultInstance> resultInstances = dataTranslator.translateResultSetIntoListOfResultInstances(resourceSet, this.getResultType());
				long endTime = System.currentTimeMillis();
				long elapsedTime = endTime - startTime;
				logger.debug("Total time for delivery " + "(including conversion to resultInstances) " + elapsedTime + " ms.");
				if (collection != null)
					collection.close();
				logger.debug("resultInstances: " + resultInstances);
				return resultInstances;
			} catch (XMLDBException e) {
				logger.error("XMLDBException, org.xmldb.api.base.Collection : " + e.toString());
			}
		} else if (modelType.equals(ModelType.RDF)) {
			// This is required to translating the resulted tuples of SPARQL to whatever the outcome of SMql is
			logger.debug("RDF_modelType is RDF");
			logger.debug("RDF_externalDataSourcePoolUtilService: " + externalDataSourcePoolUtilService);
			
			//TODO: Need to check whether the correct SDB store is retrieved and if I am able to query
			
			SDBStoreServiceImpl externalRDFDataSource = externalDataSourcePoolUtilService.getExternalJenaRDFDataSource(dataSourceName);
			//getExternalJenaRDFDataSource(databaseNameRDF,schemaNameRDF)
			logger.debug("externalRDFDataSource: " + externalRDFDataSource);	
			
		    com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString);
			logger.debug("SPARQL query: " + query);
			
			/*Get the dataset*/
			Dataset dataSet = externalRDFDataSource.getDataset();
			
			//TODO: I have a dataset and i can now quey using SPARQL

			/*Ask the SPARQL query over the RDF Source*/
			long startTime = System.currentTimeMillis();
			logger.debug("SPARQL_startTime: " + startTime);
			
			com.hp.hpl.jena.query.QueryExecution queryExecution = com.hp.hpl.jena.query.QueryExecutionFactory.create(query, dataSet);
			logger.debug("queryExecution: " + queryExecution);	

			com.hp.hpl.jena.query.ResultSet resultSet = queryExecution.execSelect();

			long gotResultTime = System.currentTimeMillis();
            logger.debug("gotResultTime: " + gotResultTime);
			logger.debug("resultSet: " + resultSet);
			
			//Call SPARQL Data Translator service
			SPARQLDataTranslatorServiceImpl dataTranslator = new SPARQLDataTranslatorServiceImpl();
			List<ResultInstance> resultInstances = dataTranslator.translateResultSetIntoListOfResultInstances(resultSet, this.getResultType());
			            
			long endTime = System.currentTimeMillis();
			long elapsedTime = endTime - startTime;
			logger.debug("Total time for delivery " + "(including conversion to resultInstances) " + elapsedTime + " ms.");
			
		    //Free up the resources used for running the query
			if (queryExecution != null)
				queryExecution.close();
			
			logger.debug("resultInstances: " + resultInstances);
			return resultInstances;			
		}
		return null;
	}

	/**
	 * @return the queryString
	 */
	public String getQueryString() {
		return queryString;
	}

	/**
	 * @param queryString the queryString to set
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	/**
	 * @return the dataSourceName
	 */
	public String getDataSourceName() {
		return dataSourceName;
	}

	/**
	 * @param dataSourceName the dataSourceName to set
	 */
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	/**
	 * @return the modelType
	 */
	public ModelType getModelType() {
		return modelType;
	}

	/**
	 * @param modelType the modelType to set
	 */
	public void setModelType(ModelType modelType) {
		this.modelType = modelType;
	}

	/**
	 * @return the resultInstanceList
	 */
	public List<ResultInstance> getResultInstanceList() {
		return resultInstanceList;
	}

	/**
	 * @param planRootEvaluatorOperator the planRootEvaluatorOperator to set
	 */
	public void setPlanRootEvaluatorOperator(EvaluatorOperator planRootEvaluatorOperator) {
		this.planRootEvaluatorOperator = planRootEvaluatorOperator;
	}

	/**
	 * @return the planRootEvaluatorOperator
	 */
	public EvaluatorOperator getPlanRootEvaluatorOperator() {
		return planRootEvaluatorOperator;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EvaluateExternallyOperatorImpl [");
		if (dataSourceName != null)
			builder.append("dataSourceName=").append(dataSourceName).append(", ");
		if (modelType != null)
			builder.append("modelType=").append(modelType).append(", ");
		if (queryString != null)
			builder.append("queryString=").append(queryString).append(", ");
		;
		if (super.toString() != null)
			builder.append("toString()=").append(super.toString());
		builder.append("]");
		return builder.toString();
	}

	/**
	 * @return the externalDataSourcePoolUtilService
	 */
	public ExternalDataSourcePoolUtilService getExternalDataSourcePoolUtilService() {
		return externalDataSourcePoolUtilService;
	}

	/**
	 * @param externalDataSourcePoolUtilService the externalDataSourcePoolUtilService to set
	 */
	public void setExternalDataSourcePoolUtilService(ExternalDataSourcePoolUtilService externalDataSourcePoolUtilService) {
		this.externalDataSourcePoolUtilService = externalDataSourcePoolUtilService;
	}

	/**
	 * @return the externalDataSource
	 */
	/*
	public ComboPooledDataSource getExternalDataSource() {
		return externalDataSource;
	}
	*/

	/**
	 * @param externalDataSource the externalDataSource to set
	 */
	/*
	public void setExternalDataSource(ComboPooledDataSource externalDataSource) {
		this.externalDataSource = externalDataSource;
	}
	*/

	/**
	 * @return the jdbcTemplate
	 */
	/*
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	*/

	/**
	 * @param jdbcTemplate the jdbcTemplate to set
	 */
	/*
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	*/

	/**
	 * @return the numberOfBlocksReturned
	 */
	public int getNumberOfBlocksReturned() {
		return numberOfBlocksReturned;
	}

	/**
	 * @param numberOfBlocksReturned the numberOfBlocksReturned to set
	 */
	public void setNumberOfBlocksReturned(int numberOfBlocksReturned) {
		this.numberOfBlocksReturned = numberOfBlocksReturned;
	}

	/**
	 * @return the numberOfTuplesReturned
	 */
	public long getNumberOfTuplesReturned() {
		return numberOfTuplesReturned;
	}

	/**
	 * @param numberOfTuplesReturned the numberOfTuplesReturned to set
	 */
	public void setNumberOfTuplesReturned(long numberOfTuplesReturned) {
		this.numberOfTuplesReturned = numberOfTuplesReturned;
	}

	/**
	 * @return the resultInstanceSwapQueue
	 */
	public LinkedList<ResultInstance> getResultInstanceSwapQueue() {
		return resultInstanceSwapQueue;
	}

	/**
	 * @param resultInstanceSwapQueue the resultInstanceSwapQueue to set
	 */
	public void setResultInstanceSwapQueue(LinkedList<ResultInstance> resultInstanceSwapQueue) {
		this.resultInstanceSwapQueue = resultInstanceSwapQueue;
	}

	/**
	 * @return the resultInstanceQueue
	 */
	public LinkedList<ResultInstance> getResultInstanceQueue() {
		return resultInstanceQueue;
	}

	/**
	 * @param resultInstanceQueue the resultInstanceQueue to set
	 */
	public void setResultInstanceQueue(LinkedList<ResultInstance> resultInstanceQueue) {
		this.resultInstanceQueue = resultInstanceQueue;
	}

	/**
	 * @return the ateof
	 */
	@Override
	public boolean isAteof() {
		return ateof;
	}

	/**
	 * @param ateof the ateof to set
	 */
	@Override
	public void setAteof(boolean ateof) {
		this.ateof = ateof;
	}

	/**
	 * @return the blocksPerCall
	 */
	public int getBlocksPerCall() {
		return blocksPerCall;
	}

	/**
	 * @param blocksPerCall the blocksPerCall to set
	 */
	public void setBlocksPerCall(int blocksPerCall) {
		this.blocksPerCall = blocksPerCall;
	}

	/**
	 * @return the eOFReceived
	 */
	public boolean isEOFReceived() {
		return EOFReceived;
	}

	/**
	 * @param isHeader the isHeader to set
	 */
	public void setHeader(boolean isHeader) {
		this.isHeader = isHeader;
	}

	/**
	 * @return the isHeader
	 */
	public boolean isHeader() {
		return isHeader;
	}

	/**
	 * @param resultInstanceList the resultInstanceList to set
	 */
	public void setResultInstanceList(List<ResultInstance> resultInstanceList) {
		this.resultInstanceList = resultInstanceList;
	}
	
	/**
	 * @return the schemaName
	 */
	public String getSchemaName() {
		return schemaName;
	}

	/**
	 * @param schemaName the schemaName to set
	 */
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

}//end class
