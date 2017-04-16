package uk.ac.manchester.dstoolkit.service.impl.query.querytranslator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.runtime.tree.CommonTree;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.ParticipationOfCMCInSuperRelationship;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationship;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationshipRoleType;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.JoinOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.MappingOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.Predicate;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.ReduceOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.ScanOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.SetOperationType;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.SetOperator;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultField;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.exceptions.TranslationException;
import uk.ac.manchester.dstoolkit.repository.canonical.SuperAbstractRepository;
import uk.ac.manchester.dstoolkit.repository.canonical.SuperLexicalRepository;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.service.impl.query.queryparser.ASTConstants;
import uk.ac.manchester.dstoolkit.service.impl.query.queryparser.ASTUtil;
import uk.ac.manchester.dstoolkit.service.query.querytranslator.GlobalQueryTranslatorService;

@Transactional(readOnly = true)
@Service(value = "globalQueryTranslatorService")
public class GlobalQueryTranslatorServiceImpl implements GlobalQueryTranslatorService {

	static Logger logger = Logger.getLogger(GlobalQueryTranslatorServiceImpl.class);

	//TODO add alias (as ...) to grammar and add handling of them here - rename operator

	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;

	@Autowired
	@Qualifier("superAbstractRepository")
	private SuperAbstractRepository superAbstractRepository;

	@Autowired
	@Qualifier("superLexicalRepository")
	private SuperLexicalRepository superLexicalRepository;

	//private CommonTree ast;
	//private Query query;
	//private MappingOperator currentRootOperator;
	private SetOperationType setOperationType;
	private String subQueryPrefix;

	//this keeps track of which mapping operator is associated with a super abstract, gets adjusted after join
	private Map<SuperAbstract, MappingOperator> saMappingOperatorsMap = new LinkedHashMap<SuperAbstract, MappingOperator>();
	private Set<Schema> schemas = new LinkedHashSet<Schema>();
	private Set<SuperLexical> superLexicals = new LinkedHashSet<SuperLexical>();
	private Set<String> schemaNames = new LinkedHashSet<String>();

	//TODO check union compatibility in resultType
	//TODO setOps is subquery though
	private List<JoinOperator> joinOperators = new ArrayList<JoinOperator>();
	private Map<String, MappingOperator> variableNameSubQueryRootOperator = new LinkedHashMap<String, MappingOperator>();
	private Map<String, SuperAbstract> variableNameSuperAbstractMap = new LinkedHashMap<String, SuperAbstract>();
	private Map<SuperAbstract, ScanOperator> superAbstractScanOperatorsMap = new LinkedHashMap<SuperAbstract, ScanOperator>();

	/*
	 * The aggregate function applied by the query, this value will be
	 * null for non-aggregate queries.
	 */
	//TODO private Aggregate aggregate;
	/*
	 * Contains OrderByItem objects. 
	 */
	//TODO private List<OrderByItem> orderByList;
	/*
	 * Function invoked by the query, indexed by variable name.
	 */
	//TODO private final Map<String, Function> functions;

	//TODO union? subqueries? orderBy, aggregates

	private Map<MappingOperator, String> subQueries = new HashMap<MappingOperator, String>();

	@SuppressWarnings("unused")
	private boolean unionQuery;

	//TODO refactor code into smaller sub-methods that can be tested bette
	//TODO I could have multipe constructs with the same name in a single schema now that I've got XML
	//TODO try to work out which element it is - global, local?

	/**
	 * 
	 */
	public GlobalQueryTranslatorServiceImpl() {
		super();
		subQueryPrefix = "";
		logger.debug("initialised translator");
		logger.debug("subQueryPrefix: " + subQueryPrefix);
	}

	/**
	 * @param subQueryPrefix
	 */
	public GlobalQueryTranslatorServiceImpl(String subQueryPrefix) {
		this();
		this.subQueryPrefix = subQueryPrefix;
		logger.debug("initialised translator object with sub-query prefix string: " + subQueryPrefix);
		logger.debug("subQueryPrefix: " + subQueryPrefix);
		//TODO what if we got nested subQueries? shoudn't the prefixes be concatenated? - check this
	}

	private void initialise() {
		logger.debug("in initialise");
		saMappingOperatorsMap = new LinkedHashMap<SuperAbstract, MappingOperator>();
		schemas = new LinkedHashSet<Schema>();
		superLexicals = new LinkedHashSet<SuperLexical>();
		schemaNames = new LinkedHashSet<String>();
		joinOperators = new ArrayList<JoinOperator>();
		variableNameSubQueryRootOperator = new LinkedHashMap<String, MappingOperator>();
		variableNameSuperAbstractMap = new LinkedHashMap<String, SuperAbstract>();
		superAbstractScanOperatorsMap = new LinkedHashMap<SuperAbstract, ScanOperator>();
		subQueries = new HashMap<MappingOperator, String>();
		setOperationType = null;
		subQueryPrefix = "";
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.impl.operators.query.querytranslator.GlobalQueryTranslatorService#translateAstIntoQuery(org.antlr.runtime.tree.CommonTree)
	 */
	public Query translateAstIntoQuery(Query query, String queryString, CommonTree ast) {
		logger.debug("in translateAstIntoQuery, no schema provided");
		initialise();
		CommonTree childNode0 = (CommonTree) ast.getChild(0);
		logger.debug("childNode0: " + childNode0.toStringTree());
		query.setQueryString(queryString);
		logger.debug("query.getSchemas(): " + query.getSchemas());
		if (query.getSchemas() != null && !query.getSchemas().isEmpty()) {
			logger.debug("found schemas over which query is posed, get their names and add to schemaNames");
			for (Schema schema : query.getSchemas()) {
				logger.debug("schema: " + schema);
				logger.debug("add schemaName to schemaNames: " + schema.getName());
				schemaNames.add(schema.getName());
			}
		}
		MappingOperator rootOperator = translateQuery(childNode0);
		query.setRootOperator(rootOperator);
		if (query.getSchemas() == null || query.getSchemas().size() == 0) {
			logger.debug("no schemas provided with queries, set schemas to those found during translation");
			query.setSchemas(schemas);
		}
		return query;
	}

	public Query translateAstIntoQuery(Query query, String queryString, CommonTree ast, Schema schema) {
		logger.debug("in translateAstIntoQuery");
		initialise();
		logger.debug("got single schema: " + schema);
		schemaNames.add(schema.getName());
		logger.debug("added schemaName to schemaNames: " + schemaNames);
		CommonTree childNode0 = (CommonTree) ast.getChild(0);
		logger.debug("childNode0: " + childNode0.toStringTree());
		query.setQueryString(queryString);
		logger.debug("query.getSchemas(): " + query.getSchemas());
		if (query.getSchemas() != null && !query.getSchemas().isEmpty()) {
			logger.debug("found schemas over which query is posed, get their names and add to schemaNames");
			for (Schema schema1 : query.getSchemas()) {
				logger.debug("schema1: " + schema1);
				logger.debug("add schemaName to schemaNames: " + schema1.getName());
				schemaNames.add(schema1.getName());
			}
		}
		MappingOperator rootOperator = translateQuery(childNode0);
		query.setRootOperator(rootOperator);
		query.addSchema(schema);
		return query;
	}

	public Query translateAstIntoQuery(Query query, String queryString, CommonTree ast, Set<Schema> schemas) {
		logger.debug("in translateAstIntoQuery");
		initialise();
		logger.debug("got list of schemas: " + schemas);
		for (Schema schema : schemas) {
			schemaNames.add(schema.getName());
			logger.debug("added schemaName to schemaNames: " + schemaNames);
		}
		logger.debug("query.getSchemas(): " + query.getSchemas());
		if (query.getSchemas() != null && !query.getSchemas().isEmpty()) {
			logger.debug("found schemas over which query is posed, get their names and add to schemaNames");
			for (Schema schema : query.getSchemas()) {
				logger.debug("schema: " + schema);
				logger.debug("add schemaName to schemaNames: " + schema.getName());
				schemaNames.add(schema.getName());
			}
		}
		CommonTree childNode0 = (CommonTree) ast.getChild(0);
		logger.debug("childNode0: " + childNode0.toStringTree());
		query.setQueryString(queryString);
		MappingOperator rootOperator = translateQuery(childNode0);
		query.setRootOperator(rootOperator);
		query.setSchemas(schemas);
		return query;
	}

	public MappingOperator translateQuery(CommonTree tree) {
		//TODO write tests to check that I'm covering everthing
		MappingOperator currentRootOperator = null;
		logger.debug("in translateQuery");
		logger.debug("tree: " + tree.toStringTree());
		String currentNodeAsText = tree.getText();
		logger.debug("currentNodeAsText: " + currentNodeAsText);
		if (currentNodeAsText.equals(ASTConstants.UNION_TOKEN) || currentNodeAsText.equals(ASTConstants.UNION_ALL_TOKEN)
				|| currentNodeAsText.equals(ASTConstants.EXCEPT_TOKEN) || currentNodeAsText.equals(ASTConstants.EXCEPT_ALL_TOKEN)
				|| currentNodeAsText.equals(ASTConstants.INTERSECT_TOKEN) || currentNodeAsText.equals(ASTConstants.INTERSECT_ALL_TOKEN)) {
			logger.debug("currentNode is SETOP of some kind -> translate union query");
			//TODO
			currentRootOperator = translateUnionQuery(tree);
			//TODO typechecking, set the resultType for all the operators
		} else if (currentNodeAsText.equals(ASTConstants.QUERY_TOKEN)) {
			logger.debug("currentNode is QUERY -> translate select query");
			currentRootOperator = translateSelectQuery(tree);
		}
		return currentRootOperator;
	}

	private MappingOperator translateSelectQuery(CommonTree selectAST) {
		logger.debug("in translateSelectQuery");
		//TODO still need to do type checking
		Map<String, CommonTree> queryStructure;
		queryStructure = ASTUtil.getQueryStructure(selectAST);

		CommonTree selectListAST = queryStructure.get(ASTConstants.SELECT_LIST_TOKEN);
		if (selectListAST != null)
			logger.debug("selectListAST: " + selectListAST.toStringTree());
		else
			logger.debug("selectListAST is null");
		CommonTree fromListAST = queryStructure.get(ASTConstants.FROM_LIST_TOKEN);
		if (fromListAST != null)
			logger.debug("fromListAST: " + fromListAST.toStringTree());
		else
			logger.debug("fromListAST is null");

		CommonTree groupByAST = queryStructure.get(ASTConstants.GROUP_BY_TOKEN);
		if (groupByAST != null)
			logger.debug("groupByAST: " + groupByAST.toStringTree());
		else
			logger.debug("groupByAST is null");
		CommonTree havingAST = queryStructure.get(ASTConstants.HAVING_TOKEN);
		if (havingAST != null)
			logger.debug("havingAST: " + havingAST.toStringTree());
		else
			logger.debug("havingAST is null");
		CommonTree whereAST = queryStructure.get(ASTConstants.WHERE_TOKEN);
		if (whereAST != null)
			logger.debug("whereAST: " + whereAST.toStringTree());
		else
			logger.debug("whereAST is null");

		//there shouldn't be an orderBy here, as grammar shouldn't allow for it
		//OrderByAST orderByAST = (OrderByAST) TypeCheckerUtil.getNextASTNodeInQuery(selectAST,OrderByAST.class);

		logger.debug("before translating fromList");
		//TODO will need this to resolve variable names in select and where
		translateFrom(fromListAST);
		logger.debug("translated fromList");

		MappingOperator currentRootOperator = null;

		if (whereAST != null) {
			logger.debug("found whereAST -> before translating whereAST");
			currentRootOperator = translateWhere(whereAST);
			logger.debug("after translating whereAST");
			logger.debug("currentRootOperator: " + currentRootOperator);
		}

		if (currentRootOperator == null)
			currentRootOperator = checkOperatorTree(currentRootOperator);

		//the resulting reduce operator, if needed, should be the root operator of the query, so process this last
		logger.debug("before translating selectList");
		MappingOperator reduce = translateSelect(selectListAST, currentRootOperator);
		currentRootOperator = reduce;
		logger.debug("translated selectList");

		printOperatorTree(currentRootOperator);

		return currentRootOperator;

		//there shouldn't be an orderBy as grammar doesn't allow for it
		//TODO check this
		/*
		if ( orderByAST != null ) {
		    if ( ! subQueryPrefix.equals("") ) {
		    	logger.warn("An ORDER BY clause was found in a sub-query." + 
		                " This is ignored because OGSA-DQP makes no" +
		                " guarantees about the order of tuples unless" +
		                " an ORDER BY clause is provided in the outer" + 
		                " query. ");
		    } else {
		    	translateOrderBy(orderByAST);
		    }
		}
		*/
	}

	/*
	 * Translate a union query. Consists of 2 unioned
	 * sub queries. The queries themselves can be union
	 * queries.
	 */
	private MappingOperator translateUnionQuery(CommonTree unionAST) throws TranslationException {
		//TODO check whether I need to check andOrs
		//TODO sort out resultType
		logger.debug("in translateUnionQuery");
		CommonTree q1 = (CommonTree) unionAST.getChild(0);
		CommonTree q2 = (CommonTree) unionAST.getChild(1);
		logger.debug("q1: " + q1.toStringTree());
		logger.debug("q2: " + q2.toStringTree());
		logger.debug("two sub-queries, initialise translator with subqueries and translate each subquery separately");
		//TODO check that subqueries are union compatible

		String u1Var = "union1." + subQueryPrefix;
		GlobalQueryTranslatorService translator1 = new GlobalQueryTranslatorServiceImpl(u1Var);
		//need to add these because autowire doesn't work for self-invocation, not particularly nice, try to find a better way - TODO
		translator1.setSchemaRepository(this.schemaRepository);
		translator1.setSuperAbstractRepository(this.superAbstractRepository);
		translator1.setSuperLexicalRepository(this.superLexicalRepository);
		translator1.setSchemaNames(this.schemaNames);

		MappingOperator union1 = translator1.translateQuery(q1);

		String u2Var = "union2." + subQueryPrefix;
		GlobalQueryTranslatorService translator2 = new GlobalQueryTranslatorServiceImpl(u2Var);
		//need to add these because autowire doesn't work for self-invocation, not particularly nice, try to find a better way - TODO
		translator2.setSchemaRepository(this.schemaRepository);
		translator2.setSuperAbstractRepository(this.superAbstractRepository);
		translator2.setSuperLexicalRepository(this.superLexicalRepository);
		translator2.setSchemaNames(this.schemaNames);
		MappingOperator union2 = translator2.translateQuery(q2);

		logger.debug("after translation; union1: " + union1);
		logger.debug("after translation; union2: " + union2);

		unionQuery = true;
		String unionType = unionAST.getText();
		logger.debug("unionType: " + unionType);
		if (unionType.equals("UNION"))
			setOperationType = SetOperationType.UNION;
		else if (unionType.equals("UNION ALL"))
			setOperationType = SetOperationType.UNION_ALL;
		else if (unionType.equals("EXCEPT"))
			setOperationType = SetOperationType.EXCEPT;
		else if (unionType.equals("EXCEPT_ALL"))
			setOperationType = SetOperationType.EXCEPT_ALL;
		else if (unionType.equals("INTERSECT"))
			setOperationType = SetOperationType.INTERSECT;
		else if (unionType.equals("INTERSECT ALL"))
			setOperationType = SetOperationType.INTERSECT_ALL;

		SetOperator setOperator = new SetOperator();
		setOperator.setLhsInput(union1);
		setOperator.setRhsInput(union2);
		if (union1.getDataSource() != null && union2.getDataSource() != null && union1.getDataSource().equals(union2.getDataSource()))
			setOperator.setDataSource(union1.getDataSource());
		setOperator.setSetOpType(setOperationType);
		ResultType union1ResultType = union1.getResultType();
		ResultType union2ResultType = union2.getResultType();
		if (!union1ResultType.isSetOpCompatible(union2ResultType))
			logger.error("ResultType not unioncompatible"); //TODO proper error handling, work on less strict version
		//TODO check that they're union-compatible
		setOperator.setResultType(union1.getResultType());
		logger.debug("created SetOp: " + setOperator);

		Map<String, SuperLexical> varNameSuperLexicalsMap = new LinkedHashMap<String, SuperLexical>();
		StringBuilder reconcilingExpressionBuilder = new StringBuilder();
		ResultType resultType = new ResultType();

		ReduceOperator reduce = new ReduceOperator();
		reduce.setInput(setOperator);
		reduce.setDataSource(setOperator.getDataSource());

		ResultType resultTypeOfSetOperator = setOperator.getResultType();
		Map<String, ResultField> resultFields = resultTypeOfSetOperator.getResultFields();
		Set<String> resultFieldNames = resultFields.keySet();
		int j = 0;
		//for (ResultField field : resultFields) {
		for (String resultFieldName : resultFieldNames) {
			if (j > 0)
				reconcilingExpressionBuilder.append(", ");
			//reconcilingExpressionBuilder.append(field.getFieldName());
			reconcilingExpressionBuilder.append(resultFieldName);
			//ResultField resultField = new ResultField(field.getFieldName(), field.getFieldType());
			ResultField resultField = new ResultField(resultFieldName, resultFields.get(resultFieldName).getFieldType());
			//resultField.setCanonicalModelConstruct(field.getCanonicalModelConstruct());
			resultField.setCanonicalModelConstruct(resultFields.get(resultFieldName).getCanonicalModelConstruct());
			//resultType.getResultFields().add(resultField);
			resultType.addResultField(resultFieldName, resultField);
			//resultField.setResultType(resultType);

			CanonicalModelConstruct cmc = resultField.getCanonicalModelConstruct();
			SuperLexical sl = null;
			if (cmc instanceof SuperLexical)
				sl = (SuperLexical) cmc;

			varNameSuperLexicalsMap.put(resultField.getFieldName(), sl);
			j++;
		}

		reduce.setReconcilingExpression(reconcilingExpressionBuilder.toString());
		reduce.setSuperLexicals(varNameSuperLexicalsMap);
		reduce.setResultType(resultType);
		logger.debug("set resultType or reduceOperator, resultType: " + resultType);
		logger.debug("reduceOperator: " + reduce);
		return reduce;

		/*
		logger.debug("add superAbstractReferences from the two union queries");
		if (saMappingOperatorsMap == null) {
			saMappingOperatorsMap = translator1.getSaMappingOperatorsMap();
			superAbstractReferences.putAll(translator2.getSuperAbstractReferences());
			logger.debug("superAbstractReferences: " + superAbstractReferences);
		}
		*/

		/*
		 * Set the result tuple. The union operator produces columns named
		 * as unionType.field1, unionType.field2 etc..
		 */
		//Iterator<QueryElement> it = translator1.getResultTuple().iterator();
		//resultTuple = new ArrayList<QueryElement>();
		//if (translator1.getResultTuple().size() != translator2.getResultTuple().size()) {
		/*
		 * This error should have been caught by the type checker if
		 * it exists, but this extra check is provided just in case!
		 */
		//throw new TranslationException("Query contains an error - all union" + " operations must act upon arguments that have the same number " + " of fields ");
		//}
		//int count = 1;
		//for (int i = 0; i < translator1.getResultTuple().size(); i++) {
		//QueryElement e1 = translator1.getResultTuple().get(i);
		//QueryElement e2 = translator2.getResultTuple().get(i);
		/* 
		 * type consistency between the two unioned tuple types
		 * should have already been checked by the type checker
		 */
		//Class[] type = e1.getJavaType(); //TODO this may not work
		//List<DataType> dataTypes1 = e1.getDataTypes();
		//List<DataType> dataTypes2 = e2.getDataTypes();

		//TODO do typeCheck/union compatibility check here --- proper error handling !!!!

		//logger.debug("dataTypes1: " + dataTypes1.toString());
		//logger.debug("dataTypes2: " + dataTypes2.toString());
		//resultTuple.add(new Variable(subQueryPrefix + unionType + ".field" + (count++), dataTypes1));
		//}

		/*
		 * The UNION may optionally contain an ORDER_BY, which consists
		 * of columns identified by an integer (1 is the first column).
		 * The resultTuple list is used to translate each integer column
		 * into an Element object from the resultTuple 
		 */
		//this shouldn't happen here, as different grammar is used, that doesn't allow order_by in sub-queries
		/*
		CommonTree orderByNode = 
		    TypeCheckerUtil.getNextASTNodeInQuery(q2,IntegerLiteralAST.class);
		while (orderByNode != null) {
		    try {
		        int column = Integer.parseInt(orderByNode.getText()) - 1;
		        // order by list is only created if there are order by
		        // attribs.
		        if ( orderByList == null ) {
		            orderByList = new ArrayList();
		        }
		        if ( orderByNode.getFirstChild() != null ) {
		        	String direction = orderByNode.getFirstChild().getText(); 
		            orderByList.add(new OrderByItem((QueryElement)resultTuple.get(column),direction));
		        } else {
		        	orderByList.add(new OrderByItem((QueryElement)resultTuple.get(column)));
		        }
		        orderByNode = 
		            TypeCheckerUtil.getNextASTNodeInQuery(
		                    orderByNode,IntegerLiteralAST.class);
		    } catch ( Exception e ) {
		    	throw new TranslationException("Unable to process ORDER BY" +
		                " columns specified for UNION query. Internal " +
		                "translator error: " + e.toString());
		    }
		}
		*/
	}

	/*
	 * Add a Sub-query tree node to the query and
	 * return the created Element representing the sub-query.
	 */
	private MappingOperator addSubQuery(CommonTree selectAST) throws TranslationException {
		logger.debug("in addSubQuery");
		String varName = "subquery" + Integer.toString(subQueries.size() + 1);
		logger.debug("varName: " + varName);
		GlobalQueryTranslatorService translator = new GlobalQueryTranslatorServiceImpl(subQueryPrefix + varName + ".");
		//need to add these because autowire doesn't work for self-invocation, not particularly nice, try to find a better way - TODO
		translator.setSchemaRepository(this.schemaRepository);
		translator.setSuperAbstractRepository(this.superAbstractRepository);
		translator.setSuperLexicalRepository(this.superLexicalRepository);
		translator.setSchemaNames(this.schemaNames);
		MappingOperator mappingOperator = translator.translateQuery(selectAST);
		mappingOperator.setVariableName(varName);
		logger.debug("translated subQuery, varName: " + varName);
		logger.debug("mappingOperator of subQuery: " + mappingOperator);
		/*
		 * Currently assumes that subqueries either return a single-field
		 * tuple or that other tuples are ignored. This is because sub-queries
		 * are only allowed in the WHERE clause as attrib IN subquery, or
		 * attrib = aggregate_subquery. Union is handled in the overloaded
		 * addSubQuery(UnionAST method). Therefore
		 * we treate the subquery type under the assumption that it contains
		 * a single element here.
		 */
		//TODO sort out type here, that won't work I think
		//TODO sort out subqueries
		//TODO sort out resultType
		//SubQuery q = new SubQuery(translator, varName, null);
		subQueries.put(mappingOperator, varName);
		this.variableNameSubQueryRootOperator.put(varName, mappingOperator);
		logger.debug("added subQuery to subQueries, varName: " + varName + " mappingOperator: " + mappingOperator);
		logger.debug("returning from addSubQuery");
		return mappingOperator;
	}

	/*
	 * Add an Aggregate tree node to the query and
	 * return the created Element representing the aggregate.
	 */
	//TODO sort out aggregates
	/*
	private Aggregate addAggregate(AggregateAST ast) throws TranslationException {
		//children can be attributes or function calls
		QueryElement arg = addIdentifier((IdentifierAST) ast.getFirstChild());
		/*
		 * Variable name assigned as agg + counter
		 */
	/*
	String varName = subQueryPrefix + "aggregate";
	if (aggregate == null) {
		aggregate = new Aggregate(ast.getText(), arg, varName, ast.getJavaType());
	} else {
		/* query involves multiple aggregates */
	/*
	aggregate.addFunction(ast.getText(), arg);
	}
	return aggregate;
	}
	*/

	/*
	 * Add a Function tree node to the query and
	 * return the created Element representing the function.
	 */
	//TODO sort out functions, not supported right now
	/*
	private QueryElement addFunction(IdentifierAST ast) throws TranslationException {
		String fnName = ast.getText();
		ArrayList parameters = new ArrayList();
		//children can be identifiers, literals
		AST treeNode = ast.getFirstChild();
		while (treeNode != null) {
			if (treeNode instanceof IdentifierAST) {
				parameters.add(addIdentifier((IdentifierAST) treeNode));
			} else if (treeNode instanceof LiteralAST) {
				parameters.add(toLiteral((LiteralAST) treeNode));
			}
			treeNode = treeNode.getNextSibling();
		}
		//before adding the function, need to check that it isn't identical
		//to another function call with the same parameter values
		Function duplicate = getFunction(fnName, parameters);
		if (duplicate != null)
			return duplicate;
		String varName = subQueryPrefix + fnName;
		//separate invocation of the same function may exist
		int i = 1;
		while (functions.containsKey(varName)) {
			varName = varName + i;
		}
		/* functions can't currently return collections, therefore type contains 1 element */
	/*
	Function func = new Function(fnName, parameters, varName, ast.getJavaType(), ast.getFunctionType());
	functions.put(varName, func);
	return func;
	}
	*/

	/*
	 * Returns the function with the given name/parameters if it exists, 
	 * else returns null
	 */
	//TODO sort out functions, not supported right now
	/*
	private Function getFunction(String functionName, ArrayList parameters) {
		Enumeration enum1 = functions.elements();
		while (enum1.hasMoreElements()) {
			Function f = (Function) enum1.nextElement();
			if (!f.getFunctionName().equals(functionName))
				continue;
			boolean matchingParams = true;
			if (parameters.size() != f.getParameters().size())
				continue;
			for (int i = 0; i < f.getParameters().size(); i++) {
				QueryElement e = (QueryElement) f.getParameters().get(i);
				QueryElement e1 = (QueryElement) parameters.get(i);
				if (e.getJavaType() != e1.getJavaType())
					matchingParams = false;
				if (e instanceof Literal) {
					if (e1 instanceof Literal) {
						if (!((Literal) e).value.equals(((Literal) e1).value))
							matchingParams = false;
					} else
						matchingParams = false;
				} else if (e instanceof SuperLexicalReference) {
					if (e1 instanceof SuperLexicalReference) {
						if (!((SuperLexicalReference) e).getSuperLexicalName().equals(((SuperLexicalReference) e1).getSuperLexicalName()))
							matchingParams = false;
						if (!((SuperLexicalReference) e).getSuperAbstractReference().getVariableName().equals(
								((SuperLexicalReference) e1).getSuperAbstractReference().getVariableName()))
							matchingParams = false;
					} else
						matchingParams = false;
				}
			}
			if (matchingParams)
				return f;
		}
		return null;
	}
	*/

	private MappingOperator checkOperatorTree(MappingOperator currentRootOperator) {
		logger.debug("in checkOperatorTree");
		Set<SuperAbstract> saSet = saMappingOperatorsMap.keySet();
		Set<MappingOperator> ops = new HashSet<MappingOperator>();
		for (SuperAbstract sa : saSet) {
			logger.debug("sa: " + sa);
			MappingOperator op = saMappingOperatorsMap.get(sa);
			if (!ops.contains(op)) {
				logger.debug("add op to set of ops: " + op);
				ops.add(op);
			}
		}
		if (ops.size() > 1) {
			//TODO
			//can happen, as I'm not setting the second join operator for all the superabstracts of the first join operator
			logger.debug("more than one operator still in map - don't think this should happen - check this = TODO");
			return null;
		} else {
			/*			try{		
							logger.debug("ruhai ops size: " + ops.size());
							logger.debug("ruhai debug marker");
							currentRootOperator = ops.iterator().next();
						}catch(Exception e){
							logger.debug("ruhai currentRootOperator iterator: " + e.toString());
							logger.debug("ruhai debug marker");
						}*/
			logger.debug("currentRootOperator: " + currentRootOperator);
			if (ops.size() > 0)
				currentRootOperator = ops.iterator().next();

			return currentRootOperator;
		}
	}

	//TODO not?

	/*
	 * Translate the where clause, which consists
	 * of a list of predicates
	 */
	private MappingOperator translateWhere(CommonTree whereAST) throws TranslationException {
		logger.debug("in translateWhere");
		logger.debug("whereAST: " + whereAST.getText());
		String andOr = null;
		if (whereAST.getText().toUpperCase().equals(ASTConstants.AND_TOKEN) || whereAST.getText().toUpperCase().equals(ASTConstants.OR_TOKEN)) {
			logger.debug("AND/OR Token");
			andOr = whereAST.getText();
			logger.debug("AND/OR: " + andOr);
		}
		logger.debug("whereAST.getChildCount(): " + whereAST.getChildCount());
		MappingOperator mp = null;
		for (int i = 0; i < whereAST.getChildCount(); i++) {
			CommonTree treeNode = (CommonTree) whereAST.getChild(i);
			logger.debug("treeNode = whereAST.child: " + treeNode.toStringTree());
			String treeNodeText = treeNode.getText();
			logger.debug("treeNodeText: " + treeNodeText);
			if (treeNodeText.toUpperCase().equals(ASTConstants.AND_TOKEN) || treeNodeText.toUpperCase().equals(ASTConstants.OR_TOKEN)) {
				logger.debug("found AND or OR, before calling translateWhere again");
				logger.debug("treeNodeText: " + treeNodeText);
				mp = translateWhere(treeNode);
				logger.debug("back from translateWhere");
				logger.debug("mappingOperator: " + mp);

			} else if (treeNodeText.toUpperCase().equals(ASTConstants.IN_TOKEN)) {
				logger.debug("IN token");
				logger.debug("before translatePredicate");
				if (i == 1)
					mp = translatePredicate(treeNode, andOr);
				else
					mp = translatePredicate(treeNode, null);
				logger.debug("after translatePredicate");
				logger.debug("mappingOperator: " + mp);

			} else {
				logger.debug("should be 'normal' predicate, before translatePredicate");
				MappingOperator operator;
				if (i == 1)
					operator = translatePredicate(treeNode, andOr);
				else
					operator = translatePredicate(treeNode, null);
				logger.debug("after translatePredicate");
				logger.debug("mappingOperator, operator: " + operator);

				if (mp == null || operator instanceof JoinOperator) {
					logger.debug("mp: " + mp);
					mp = operator;
					logger.debug("after setting mp, mp: " + mp);
				} else {
					logger.debug("not setting mp, mp: " + mp);
					if (andOr != null && operator != null && operator.getAndOr() == null) {
						logger.debug("AND/OR: " + andOr);
						logger.debug("set this in operator: " + operator);
						operator.setAndOr(andOr);
					}
				}
			}
		}
		if (andOr != null && mp != null && mp.getAndOr() == null) {
			logger.debug("AND/OR: " + andOr);
			logger.debug("set this in mp: " + mp);
			mp.setAndOr(andOr);
		}
		logger.debug("leaving translateWhere");
		logger.debug("mp: " + mp);
		return mp;
	}

	private void adjustSuperAbstractMappingOperatorMap(MappingOperator operatorToSet, MappingOperator currentOperatorToCheck) {
		MappingOperator mp1 = currentOperatorToCheck.getLhsInput();
		MappingOperator mp2 = currentOperatorToCheck.getRhsInput();

		if (mp1 instanceof ScanOperator) {
			SuperAbstract sa = ((ScanOperator) mp1).getSuperAbstract();
			saMappingOperatorsMap.put(sa, operatorToSet);
		} else if (mp1 instanceof JoinOperator || mp1 instanceof SetOperator)
			adjustSuperAbstractMappingOperatorMap(operatorToSet, mp1);

		if (mp2 instanceof ScanOperator) {
			SuperAbstract sa = ((ScanOperator) mp2).getSuperAbstract();
			saMappingOperatorsMap.put(sa, operatorToSet);
		} else if (mp2 instanceof JoinOperator || mp2 instanceof SetOperator)
			adjustSuperAbstractMappingOperatorMap(operatorToSet, mp2);
	}

	/*
	 * Translate predicates of the form:
	 * arg1 operator arg2, including:
	 * attribute IN sub-query
	 * attribute|literal|function op sub-query
	 * attribute|literal|function op attribute|literal|function 
	 */
	private MappingOperator translatePredicate(CommonTree ast, String andOr) throws TranslationException {
		logger.debug("in translatePredicate");
		logger.debug("ast.Text: " + ast.getText());
		if (ast.getText().equals(ASTConstants.AND_TOKEN) || ast.getText().equals(ASTConstants.OR_TOKEN)) {
			logger.debug("found AND or OR");
		}
		logger.debug("andOr: " + andOr);
		CommonTree arg1 = (CommonTree) ast.getChild(0);
		CommonTree arg2 = (CommonTree) ast.getChild(1);
		logger.debug("arg1: " + arg1.toStringTree());
		logger.debug("arg2: " + arg2.toStringTree());
		String arg1Text = arg1.getText();
		String arg2Text = arg2.getText();
		logger.debug("arg1.text: " + arg1Text);
		logger.debug("arg2.text: " + arg2Text);
		String astText = ast.getText();
		logger.debug("astText: " + astText);

		Map<SuperLexical, String> elem1 = null;
		Map<SuperLexical, String> elem2 = null;

		MappingOperator subQuery = null;

		if (arg1Text.equals(ASTConstants.SUPER_LEXICAL_OF_SUPER_ABSTRACT_TOKEN)) {
			logger.debug("arg1 is SUPER_LEXICAL_OF_SUPER_ABSTRACT_TOKEN");
			logger.debug("before finding SuperLexical"); //assume that it's a superlexical
			Map<SuperLexical, String> map = findSuperLexical(arg1);
			logger.debug("after finding superLexical");
			if (map != null) {
				logger.debug("superLexical: " + map.keySet().iterator().next());
				superLexicals.add(map.keySet().iterator().next());
				elem1 = map;
			}
		} else {
			logger.debug("arg1 should be literal: TODO check can't be anything else");
			//TODO should be literal, check that it can't be anything else
		}

		if (arg2Text.equals(ASTConstants.SUPER_LEXICAL_OF_SUPER_ABSTRACT_TOKEN)) {
			logger.debug("arg2 is SUPER_LEXICAL_OF_SUPER_ABSTRACT_TOKEN");
			logger.debug("before finding SuperLexical"); //assume that it's a superlexical
			Map<SuperLexical, String> map = findSuperLexical(arg2);
			logger.debug("after finding superLexical");
			
			//map is null throwing an exception
			logger.debug("Possible error >>" + map);
			
			//logger.debug("superLexical: " + map.keySet().iterator().next());
			if (map != null)
				superLexicals.add(map.keySet().iterator().next());
			elem2 = map;
		} else if (arg2Text.equals(ASTConstants.SELECT_LIST_TOKEN)) {
			logger.debug("arg2 is SELECT_LIST_TOKEN; add SubQuery");
			subQuery = addSubQuery(arg2);
			logger.debug("subQuery: " + subQuery);
			//TODO add subquery to mapping model (make query a mapping operator), no need: it'll just be a join with a subquery
		} else if (arg2Text.equals(ASTConstants.UNION_TOKEN) || arg2Text.equals(ASTConstants.UNION_ALL_TOKEN)
				|| arg2Text.equals(ASTConstants.INTERSECT_TOKEN) || arg2Text.equals(ASTConstants.INTERSECT_ALL_TOKEN)
				|| arg2Text.equals(ASTConstants.EXCEPT_TOKEN) || arg2Text.equals(ASTConstants.EXCEPT_ALL_TOKEN)) {
			logger.debug("arg2 is SET_OP token: add SubQuery");
			//TODO set op;
			subQuery = addSubQuery(arg2);
			logger.debug("subQuery: " + subQuery);
		} else if (arg2Text.equals(ASTConstants.QUERY_TOKEN)) {
			logger.debug("arg2 is QUERY_TOKEN, add subQuery");
			//TODO add subquery to mapping model (make query a mapping operator), no need: it'll just be a join with a subquery
			subQuery = addSubQuery(arg2);
			logger.debug("subQuery: " + subQuery);
		} else {
			logger.debug("arg2 should be literal: TODO check can't be anything else");
		}

		if (astText.equals(ASTConstants.IN_TOKEN)) {
			logger.debug("predicateAST is in: add join with subquery");
			//TODO add subquery
			//TODO this might be adding the subquery a second time, as it's probably got a query token as arg2Text when it's got an in token, check this
			//TODO check "in" in predicate
			//predicate = new Predicate(elem1, elem2, "in");
			//predicate.setAndOr(andOr);
			logger.debug("andOr: " + andOr);
			logger.debug("about to add join if elem2 != null");
			//TODO getting parentSuperAbstract might not work in all cases, e.g., XML - sort this
			if (elem1 != null && elem1.keySet().iterator().next() instanceof SuperLexical && subQuery != null) {
				logger.debug("join elem1 with subquery");
				SuperLexical sl1 = elem1.keySet().iterator().next();
				logger.debug("sl1: " + sl1);
				SuperAbstract sa1 = sl1.getParentSuperAbstract();
				logger.debug("sa1: " + sa1);
				String varName1 = elem1.get(sl1);
				logger.debug("varName1: " + varName1);
				String subQueryVarName = subQueries.get(subQuery);
				logger.debug("subQuery: " + subQuery);
				logger.debug("subQueryVarName: " + subQueryVarName);
				logger.debug("root operator of subQuery should be reduce operator and there should only be one superlexical associated with the reduce operator");
				String subQueryReduceFullSlVarName = null;
				String subQueryReduceSlName = null;
				SuperLexical subqueryReduceSl = null;
				String subqueryVarNameWithoutSlName = null;
				if (subQuery instanceof ReduceOperator) {
					ReduceOperator reduce = (ReduceOperator) subQuery;
					Map<String, SuperLexical> superLexicals = reduce.getSuperLexicals();
					if (superLexicals.size() > 1)
						logger.error("more than one superLexical associated with reduce, shouldn't be the case - TODO");//TODO
					Set<String> keySet = superLexicals.keySet();
					for (String key : keySet) {
						logger.debug("key: " + key);
						subqueryReduceSl = superLexicals.get(key);
						logger.debug("subqueryReduceSl: " + subqueryReduceSl);
						subQueryReduceFullSlVarName = key;
						logger.debug("subQueryReduceFullSlVarName: " + subQueryReduceFullSlVarName);
						subQueryReduceSlName = subqueryReduceSl.getName();
						logger.debug("subQueryReduceSlName: " + subQueryReduceSlName);
						subqueryVarNameWithoutSlName = subQueryReduceFullSlVarName.substring(subQueryReduceFullSlVarName.lastIndexOf(".") + 1);
						logger.debug("subqueryVarNameWithoutSlName: " + subqueryVarNameWithoutSlName);
					}
				} else
					logger.error("subQuery doesn't have reduce - check this");

				MappingOperator mp1 = saMappingOperatorsMap.get(sa1); //there should only be one at any point in time, if multiple scans, could have just added all the predicates, check this anyday!!! - TODO
				logger.debug("mp1: " + mp1);
				MappingOperator mp2 = subQuery;
				logger.debug("mp2: " + mp2);

				ResultType resultType = new ResultType(mp1.getResultType(), mp2.getResultType());

				JoinOperator joinOperator = new JoinOperator();

				//TODO don't think I will need this here, check this though
				/*
				if (mp1 instanceof ScanOperator && mp2 instanceof JoinOperator) {
					joinOperator.setLhsInput(mp2);
					joinOperator.setRhsInput(mp1);
				} else {
				*/
				joinOperator.setLhsInput(mp1);
				joinOperator.setRhsInput(mp2);
				//}
				if (mp1.getDataSource() != null && mp2.getDataSource() != null && mp1.getDataSource().equals(mp2.getDataSource()))
					joinOperator.setDataSource(mp1.getDataSource());
				joinOperator.setResultType(resultType);

				logger.debug("set resultType of joinOperator, resultType: " + resultType);
				StringBuilder reconcilingExpressionString = new StringBuilder(); //add varName instead of sa name, not sure I would need it - check this TODO				
				reconcilingExpressionString.append(varName1 + ".");
				reconcilingExpressionString.append(sl1.getName() + " ");
				reconcilingExpressionString.append("=" + " ");
				reconcilingExpressionString.append(subQueryReduceFullSlVarName); //+ "." + subQueryReduceSlName);
				logger.debug("reconcilingExpression: " + reconcilingExpressionString);
				joinOperator.setReconcilingExpression(reconcilingExpressionString.toString());

				logger.debug("mp1.getVariableName(): " + mp1.getVariableName());
				logger.debug("mp2.getVariableName(): " + mp2.getVariableName());

				if (mp1.getVariableName() == null) {
					logger.debug("setting mp1.varName to varName1: " + varName1);
					mp1.setVariableName(varName1);
				} else if (!mp1.getVariableName().equals(varName1)) {
					logger.error("varName of mp1 different from varName1, TODO check this");
					logger.debug("mp1.getVariableName(): " + mp1.getVariableName());
					logger.debug("varName1: " + varName1);
				}

				if (mp2.getVariableName() == null) {
					logger.debug("setting mp2.varName to subqueryVarNameWithoutSlName: " + subqueryVarNameWithoutSlName);
					mp2.setVariableName(subqueryVarNameWithoutSlName);
				} else if (!mp2.getVariableName().equals(subqueryVarNameWithoutSlName)) {
					logger.error("varName of mp2 different from subqueryVarNameWithoutSlName, TODO check this");
					logger.debug("mp2.getVariableName(): " + mp2.getVariableName());
					logger.debug("subqueryVarNameWithoutSlName: " + subqueryVarNameWithoutSlName);
				}

				Predicate predicate = new Predicate(sl1, subqueryReduceSl, "=");
				predicate.setAndOr(andOr);
				joinOperator.addPredicate(predicate);
				//TODO andOr?

				this.joinOperators.add(joinOperator);

				adjustSuperAbstractMappingOperatorMap(joinOperator, joinOperator);

				return joinOperator;
			}

			//the following has already been checked above, comment it out
			/*
			if (arg2Text.equals(ASTConstants.SELECT_LIST_TOKEN)) {
				logger.debug("second arg of predicateAST is SELECT: TODO check");
				//TODO check subquery in predicate
				//SubQuery subQ = (SubQuery) elem2;
				//Iterator<QueryElement> it = subQ.getQuery().getResultTuple().iterator();
				/* this is a collection of size 1 - the sub-query result */
			//QueryElement field = it.next();
			/* the reference to the sub-query (argument2) is replaced
			 * by the result field */
			//predicate.setArgument2(field);
			/*
			if (subQuery == null)
				subQuery = addSubQuery(arg2);
			else
				logger.debug("already got a subquery");
			}
			*/
		} else {
			logger.debug("predicate should be 'normal' predicate - TODO check");
			//TODO check 'normal' predicate
			String operator = ast.getText();
			logger.debug("operator: " + operator);

			//TODO getting parentSuperAbstract might not work in all cases, e.g., XML - sort this
			if (elem1 != null && elem1.keySet().iterator().next() instanceof SuperLexical && elem2 != null
					&& elem2.keySet().iterator().next() instanceof SuperLexical) {
				logger.debug("join");
				SuperLexical sl1 = elem1.keySet().iterator().next();
				logger.debug("sl1: " + sl1);
				SuperAbstract sa1 = sl1.getParentSuperAbstract();
				logger.debug("sa1: " + sa1);
				String varName1 = elem1.get(sl1);
				logger.debug("varName1: " + varName1);
				SuperLexical sl2 = elem2.keySet().iterator().next();
				logger.debug("sl2: " + sl2);
				SuperAbstract sa2 = sl2.getParentSuperAbstract();
				logger.debug("sa2: " + sa2);
				String varName2 = elem2.get(sl2);
				logger.debug("varName2: " + varName2);

				MappingOperator mp1 = saMappingOperatorsMap.get(sa1); //there should only be one at any point in time, if multiple scans, could have just added all the predicates, check this anyday!!! - TODO
				logger.debug("mp1: " + mp1);
				MappingOperator mp2 = saMappingOperatorsMap.get(sa2);
				logger.debug("mp2: " + mp2);
				logger.debug("mp1.resultType: " + mp1.getResultType());
				logger.debug("mp2.resultType: " + mp2.getResultType());

				if (mp1 == mp2)
					logger.debug("same mp for the two sa's - could just add the reconciling expression to it with and/or - TODO");//TODO

				ResultType resultType = new ResultType(mp1.getResultType(), mp2.getResultType());
				JoinOperator joinOperator = new JoinOperator();
				joinOperator.setLhsInput(mp1);
				joinOperator.setRhsInput(mp2);
				if (mp1.getDataSource() != null && mp2.getDataSource() != null && mp1.getDataSource().equals(mp2.getDataSource()))
					joinOperator.setDataSource(mp1.getDataSource());
				joinOperator.setResultType(resultType);
				logger.debug("set resultType of joinOperator, resultType: " + resultType);
				StringBuilder reconcilingExpressionString = new StringBuilder(); //add varName instead of sa name, not sure I would need it - check this TODO				
				reconcilingExpressionString.append(varName1 + ".");
				reconcilingExpressionString.append(sl1.getName() + " ");
				reconcilingExpressionString.append(operator + " ");
				reconcilingExpressionString.append(varName2 + ".");
				reconcilingExpressionString.append(sl2.getName() + " ");
				logger.debug("reconcilingExpression: " + reconcilingExpressionString);
				joinOperator.setReconcilingExpression(reconcilingExpressionString.toString());

				logger.debug("mp1.getVariableName(): " + mp1.getVariableName());
				logger.debug("mp2.getVariableName(): " + mp2.getVariableName());

				if (mp1.getVariableName() == null) {
					logger.debug("setting mp1.varName to varName1: " + varName1);
					mp1.setVariableName(varName1);
				} else if (!mp1.getVariableName().equals(varName1)) {
					logger.error("varName of mp1 different from varName1, TODO check this");
					logger.debug("mp1.getVariableName(): " + mp1.getVariableName());
					logger.debug("varName1: " + varName1);
				}

				if (mp2.getVariableName() == null) {
					logger.debug("setting mp2.varName to varName2: " + varName2);
					mp2.setVariableName(varName2);
				} else if (!mp2.getVariableName().equals(varName2)) {
					logger.error("varName of mp2 different from varName2, TODO check this");
					logger.debug("mp2.getVariableName(): " + mp2.getVariableName());
					logger.debug("varName2: " + varName2);
				}

				Predicate predicate = new Predicate(sl1, sl2, operator);
				predicate.setAndOr(andOr);
				joinOperator.addPredicate(predicate);

				this.joinOperators.add(joinOperator);

				adjustSuperAbstractMappingOperatorMap(joinOperator, joinOperator);

				return joinOperator;

			} else if (elem1 != null && elem1.keySet().iterator().next() instanceof SuperLexical && elem2 == null) {
				//TODO getting parentSuperAbstract might not work in all cases, e.g., XML - sort this
				logger.debug("elem1 != null & sL - add predicate to scan op");
				SuperLexical sl = elem1.keySet().iterator().next();
				logger.debug("sl: " + sl);
				String variableName = elem1.get(sl);
				logger.debug("variableName: " + variableName);
				SuperAbstract sa = sl.getParentSuperAbstract();
				logger.debug("sa: " + sa);
				if (sa == null)
					sa = (SuperAbstract) getSurroundingParentConstructContainedInSuperAbstractScanOperatorsMap(sl);
				logger.debug("sa: " + sa);
				ScanOperator scanOp = superAbstractScanOperatorsMap.get(sa);
				logger.debug("scanOp: " + scanOp);
				StringBuilder reconcilingExpressionString = new StringBuilder();
				/*
				//probably won't need this, check this though - TODO
				if (variableName != null)
					reconcilingExpressionString.append(variableName + ".");
				*/

				if (scanOp.getReconcilingExpression() != null) {
					reconcilingExpressionString.append(scanOp.getReconcilingExpression().getExpression());
					logger.debug("reconcilingExpression of scanOp: " + reconcilingExpressionString);
					logger.debug("scanOp already has expression, add new expression to it --- check for and/or");
					//TODO
					reconcilingExpressionString.append(" ");
					logger.debug("andOr: " + andOr);
					if (andOr != null)
						reconcilingExpressionString.append(andOr + " ");
					else
						reconcilingExpressionString.append("and ");
				}

				reconcilingExpressionString.append(variableName + ".");
				reconcilingExpressionString.append(sl.getName() + " ");
				reconcilingExpressionString.append(operator + " ");
				reconcilingExpressionString.append(arg2Text);
				logger.debug("reconcilingExpression: " + reconcilingExpressionString);

				scanOp.setReconcilingExpression(reconcilingExpressionString.toString());

				Predicate predicate = new Predicate(sl, arg2Text, operator);
				predicate.setAndOr(andOr);
				scanOp.addPredicate(predicate);

				return scanOp;
			} else if (elem1 == null && elem2 != null && elem2.keySet().iterator().next() instanceof SuperLexical) {
				logger.debug("elem2 != null and sl - add predicate to scan op");
				SuperLexical sl = elem2.keySet().iterator().next();
				logger.debug("sl: " + sl);
				String variableName = elem2.get(sl);
				logger.debug("variableName: " + variableName);
				SuperAbstract sa = sl.getParentSuperAbstract();
				logger.debug("saName: " + sa);
				if (sa == null)
					sa = (SuperAbstract) getSurroundingParentConstructContainedInSuperAbstractScanOperatorsMap(sl);
				logger.debug("sa: " + sa);
				ScanOperator scanOp = superAbstractScanOperatorsMap.get(sa);
				logger.debug("scanOp: " + scanOp);
				StringBuilder reconcilingExpressionString = new StringBuilder();
				reconcilingExpressionString.append(arg1Text + " ");
				reconcilingExpressionString.append(operator + " ");
				/*
				//probably won't need this, check this though - TODO
				if (variableName != null)
					reconcilingExpressionString.append(variableName + ".");
				*/
				reconcilingExpressionString.append(variableName + ".");
				reconcilingExpressionString.append(sl.getName() + " ");
				logger.debug("reconcilingExpression: " + reconcilingExpressionString);
				if (scanOp.getReconcilingExpression() != null) {
					String reString = scanOp.getReconcilingExpression().getExpression();
					logger.debug("reconcilingExpression of scanOp: " + reString);
					logger.debug("scanOp already has expression, add new expression to it --- check for and/or");
					//TODO
					StringBuilder reBuilder = new StringBuilder();
					reBuilder.append(reString + " ");
					if (andOr != null)
						reBuilder.append(andOr + " ");
					else
						reBuilder.append("and ");
					reBuilder.append(reconcilingExpressionString);
				} else
					scanOp.setReconcilingExpression(reconcilingExpressionString.toString());

				Predicate predicate = new Predicate(arg1Text, sl, operator);
				predicate.setAndOr(andOr);
				scanOp.addPredicate(predicate);

				return scanOp;
			}
		}
		return null;

	}

	private CanonicalModelConstruct getSurroundingParentConstructContainedInSuperAbstractScanOperatorsMap(CanonicalModelConstruct childConstruct) {
		Set<ParticipationOfCMCInSuperRelationship> participations = childConstruct.getParticipationInSuperRelationships();
		for (ParticipationOfCMCInSuperRelationship participation : participations) {
			if (participation.getRole().equals(SuperRelationshipRoleType.CHILD)) {
				SuperRelationship sr = participation.getSuperRelationship();
				logger.debug("sr: " + sr);
				Set<ParticipationOfCMCInSuperRelationship> parts = sr.getParticipationsOfConstructs();
				for (ParticipationOfCMCInSuperRelationship part : parts) {
					if (part.getRole().equals(SuperRelationshipRoleType.PARENT)) {
						logger.debug("found parent");
						CanonicalModelConstruct parentConstruct = part.getCanonicalModelConstruct();
						if (superAbstractScanOperatorsMap.containsKey(parentConstruct)) {
							logger.debug("parent construct: " + parentConstruct);
							return parentConstruct;
						}
					}
				}
			}
		}
		return null;
	}

	protected SuperLexical findSuperLexicalForParentSuperLexicalInSchema(String superLexicalName, String parentSuperLexicalName, String schemaName) {
		logger.debug("in findSuperLexicalForSuperAbstractInSchema");
		logger.debug("slName: " + superLexicalName);
		logger.debug("pslName: " + parentSuperLexicalName);
		logger.debug("schemaName: " + schemaName);

		SuperLexical sl = superLexicalRepository.getSuperLexicalWithNameOfParentSuperLexicalWithNameInSchemaWithName(superLexicalName,
				parentSuperLexicalName, schemaName);
		logger.debug("sl: " + sl);

		return sl;
	}

	protected SuperLexical findSuperLexicalForSuperAbstractInSchema(String superLexicalName, String superAbstractName, String schemaName) {
		logger.debug("in findSuperLexicalForSuperAbstractInSchema");
		logger.debug("slName: " + superLexicalName);
		logger.debug("saName: " + superAbstractName);
		logger.debug("schemaName: " + schemaName);

		SuperLexical sl = superLexicalRepository.getSuperLexicalWithNameOfSuperAbstractWithNameInSchemaWithName(superLexicalName, superAbstractName,
				schemaName);
		logger.debug("sl: " + sl);

		return sl;
	}

	protected SuperLexical findSuperLexicalForSuperAbstract(String superLexicalName, String superAbstractName) {
		SuperLexical superLexical = null;
		logger.debug("in findSuperLexicalForSuperAbstract");
		logger.debug("slName: " + superLexicalName);
		logger.debug("saName: " + superAbstractName);
		if (!schemaNames.isEmpty()) {
			for (String schemaName : schemaNames) {
				SuperLexical sl = findSuperLexicalForSuperAbstractInSchema(superLexicalName, superAbstractName, schemaName);
				if (superLexical == null)
					superLexical = sl;
				else if (sl != null) {
					//TODO
					logger.error("found multiple superLexical with same name for the superAbstract in the schemas provided --- TODO");
				}
			}
		} else {
			//could be a sa not listed in from clause - but could be listed, so just go with that one
			Set<SuperAbstract> superAbstracts = this.superAbstractScanOperatorsMap.keySet();
			for (SuperAbstract sa : superAbstracts) {
				if (sa.getName().equals(superAbstractName)) {
					String schemaName = sa.getSchema().getName();
					SuperLexical sl = findSuperLexicalForSuperAbstractInSchema(superLexicalName, superAbstractName, schemaName);
					if (superLexical == null)
						superLexical = sl;
					else if (sl != null) {
						superLexical = sl; //it's listed in fromClause, so just go with that one -- assuming there is only one right one
					}
				} else {
					//sa not listed in from clause, but no schemas listed, try to find it in one of the schemas of the sas listed
					String schemaName = sa.getSchema().getName();
					SuperLexical sl = findSuperLexicalForSuperAbstractInSchema(superLexicalName, superAbstractName, schemaName);
					if (superLexical == null)
						superLexical = sl;
					else if (sl != null) {
						//TODO
						logger.error("found multiple superLexical with same name for the superAbstract in the schemas provided --- TODO check this");
					}
				}
			}
		}
		return superLexical;
	}

	private Map<SuperLexical, String> findSuperLexical(CommonTree superLexicalOfSuperAbstractAST) {
		logger.debug("in findSuperLexical");
		logger.debug("superLexicalOfSuperAbstractAST: " + superLexicalOfSuperAbstractAST.toStringTree());
		Map<SuperLexical, String> map = new HashMap<SuperLexical, String>();
		String superLexicalName = superLexicalOfSuperAbstractAST.getText();
		logger.debug("superLexicalName: " + superLexicalName);
		/*
		if (ast.getType() == Types.FUNCTION)
			return addFunction(ast);
		TODO sort out functions
		*/

		if (superLexicalOfSuperAbstractAST.getChildCount() == 1) {
			logger.debug("name of superAbstract not provided");
			if (superAbstractScanOperatorsMap.size() == 1) {
				logger.debug("only one superAbstract, chances are superlexical belongs to it, check this");
				SuperAbstract superAbstract = superAbstractScanOperatorsMap.keySet().iterator().next();
				logger.debug("superAbstract: " + superAbstract);
				String child1Text = superLexicalOfSuperAbstractAST.getChild(0).getText();
				logger.debug("child1Text: " + child1Text);
				superLexicalName = child1Text;
				logger.debug("superLexicalName: " + superLexicalName);
				//superLexicalName = superLexicalName.toLowerCase();
				//logger.debug("superLexicalName in lowercase: " + superLexicalName);
				SuperLexical superLexical = findSuperLexicalForSuperAbstract(superLexicalName, superAbstract.getName());
				logger.debug("superLexical: " + superLexical);
				if (superLexical == null) {
					logger.error("didn't find superLexical - TODO sort this");
				} else {
					superLexicals.add(superLexical);
					String variableName = this.subQueryPrefix + superAbstract.getName();
					logger.debug("variableName: " + variableName);
					map.put(superLexical, variableName);
				}
				return map;
			}
		} else if (superLexicalOfSuperAbstractAST.getChildCount() == 2) {
			logger.debug("find out what information is provided, most likely variable name or superAbstractname and superLexicalName");
			String child1Text = superLexicalOfSuperAbstractAST.getChild(0).getText();
			logger.debug("child1Text: " + child1Text);
			//child1Text = child1Text.toCase();
			//logger.debug("child1Text in lowercase: " + child1Text);
			String child2Text = superLexicalOfSuperAbstractAST.getChild(1).getText();
			logger.debug("child2Text: " + child2Text);
			//child2Text = child2Text.toLowerCase();
			//logger.debug("child2Text in lowercase: " + child2Text);

			String superAbstractName = null;
			String variableName = null;
			/*
			if (superAbstractScanOperatorsMap.containsKey(child1Text)) {
				logger.debug("first child seems to be superAbstract, found corresponding scan operator");
				superAbstractName = child1Text;
				logger.debug("superAbstractName: " + superAbstractName);
				//assume second one is superLexicalName
				superLexicalName = child2Text;
				variableName = this.subQueryPrefix + superAbstractName;
				logger.debug("variableName: " + variableName);
			} else */
			if (superAbstractScanOperatorsMap.containsKey(this.subQueryPrefix + child1Text)) {
				logger.debug("first child with subQueryPrefix seems to be superAbstract, found corresponding scan operator");
				superAbstractName = this.subQueryPrefix + child1Text;
				logger.debug("superAbstractName with subQueryPrefix: " + superAbstractName);
				//assume second one is superLexicalName
				superLexicalName = child2Text;
				logger.debug("superLexicalName: " + superLexicalName);
				//superLexicalName = superLexicalName.toLowerCase();
				//logger.debug("superLexicalName in lowercase: " + superLexicalName);
				variableName = this.subQueryPrefix + superAbstractName;
				logger.debug("variableName: " + variableName);
			} else if (variableNameSuperAbstractMap.containsKey(child1Text)) {
				//TODO missing check that it isn't both
				logger.debug("first child seems to be variable name, found it in map");
				superAbstractName = variableNameSuperAbstractMap.get(child1Text).getName();
				variableName = child1Text;
				logger.debug("variableName: " + variableName);
				//assume second one is superLexicalName
				superLexicalName = child2Text;
			} else if (variableNameSuperAbstractMap.containsKey(this.subQueryPrefix + child1Text)) {
				//TODO missing check that it isn't both
				logger.debug("first child with subQueryPrefix seems to be variable name, found it in map");
				superAbstractName = variableNameSuperAbstractMap.get(this.subQueryPrefix + child1Text).getName();
				variableName = this.subQueryPrefix + child1Text;
				logger.debug("variableName: " + variableName);
				//assume second one is superLexicalName
				superLexicalName = child2Text;
			} else {
				//TODO
				logger.error("not sure what the first child is - TODO");
			}
			logger.debug("superAbstractName: " + superAbstractName);
			logger.debug("superLexicalName: " + superLexicalName);
			logger.debug("variableName: " + variableName);
			if (superAbstractName != null) {
				SuperLexical superLexical = findSuperLexicalForSuperAbstract(superLexicalName, superAbstractName);
				logger.debug("superLexical: " + superLexical);
				superLexicals.add(superLexical);
				map.put(superLexical, variableName);
				return map;
			}

		} else if (superLexicalOfSuperAbstractAST.getChildCount() == 3) {
			logger.debug("schemaName seems to be provided");
			String schemaName = superLexicalOfSuperAbstractAST.getChild(0).getText();
			logger.debug("schemaName: " + schemaName);
			String superAbstractName = superLexicalOfSuperAbstractAST.getChild(1).getText();
			logger.debug("superAbstractName: " + superAbstractName);
			//superAbstractName = superAbstractName.toLowerCase();
			//logger.debug("superAbstractName in lowercase: " + superAbstractName);
			superLexicalName = superLexicalOfSuperAbstractAST.getChild(2).getText();
			logger.debug("superLexicalName: " + superLexicalName);
			//superLexicalName = superLexicalName.toLowerCase();
			//logger.debug("superLexicalName in lowercase: " + superLexicalName);
			String variableName = this.subQueryPrefix + superAbstractName;
			logger.debug("variableName: " + variableName);

			SuperLexical superLexical = findSuperLexicalForSuperAbstractInSchema(superLexicalName, superAbstractName, schemaName);
			superLexicals.add(superLexical);
			map.put(superLexical, variableName);
			return map;
			/* NOTE: attributes a singular, therefore type contains 1 element */
		}
		return null;
	}

	/*
	 * Adds each superAbstract to a hashtable in which
	 * the key is the variable name and the element
	 * is a SuperAbstractReference object. Note that the variable
	 * name equals the superAbstract name.
	 */
	//TODO pull out buildSuperAbstractReference and buildJoin

	//TODO need to carry varNames around with me all the time, might be a rename operator though, instead of adding them to scan operator, check this
	private void translateFrom(CommonTree fromListAST) {
		logger.debug("in translateFrom");
		String varName = null;

		for (int i = 0; i < fromListAST.getChildCount(); i++) {
			CommonTree child = (CommonTree) fromListAST.getChild(i);
			logger.debug("child: " + child.toStringTree());
			String childText = child.getText();
			logger.debug("childText: " + childText);

			//TODO mapping operators model doesn't do subqueries; it's just a join with another sub-query tree, should be ok, but check this

			SuperAbstract superAbstract = null;

			if (childText.equals(ASTConstants.SUPER_ABSTRACT_TOKEN)) {
				logger.debug("found reference to superAbstract");
				if (child.getChildCount() == 1) {
					logger.debug("only one child element");
					CommonTree childOfSuperAbstractToken = (CommonTree) child.getChild(0);
					logger.debug("childOfSuperAbstractToken - should be superAbstractName: " + childOfSuperAbstractToken.toStringTree());
					String textOfChildOfSuperAbstractToken = childOfSuperAbstractToken.getText();
					logger.debug("textOfChildOfSuperAbstractToken - should be superAbstractName: " + textOfChildOfSuperAbstractToken);
					logger.debug("find superAbstract to scan");

					superAbstract = findSuperAbstract(textOfChildOfSuperAbstractToken);

					logger.debug("superAbstract: " + superAbstract);
					varName = this.subQueryPrefix + textOfChildOfSuperAbstractToken;
					logger.debug("varName: " + varName);
					variableNameSuperAbstractMap.put(varName, superAbstract);
					//TODO check whether this superAbstract should be added somewhere, e.g., variableNamesSAMap?

				} else if (child.getChildCount() == 2) {
					logger.debug("two child elements");
					CommonTree firstChildOfSuperAbstractToken = (CommonTree) child.getChild(0);
					logger.debug("firstChildOfSuperAbstractToken: " + firstChildOfSuperAbstractToken.toStringTree());
					String textOfFirstChildOfSuperAbstractToken = firstChildOfSuperAbstractToken.getText();
					logger.debug("textOfFirstChildOfSuperAbstractToken: " + textOfFirstChildOfSuperAbstractToken);
					CommonTree secondChildOfSuperAbstractToken = (CommonTree) child.getChild(1);
					logger.debug("secondChildOfSuperAbstractToken: " + secondChildOfSuperAbstractToken.toStringTree());
					String textOfSecondChildOfSuperAbstractToken = secondChildOfSuperAbstractToken.getText();
					logger.debug("textOfSecondChildOfSuperAbstractToken: " + textOfSecondChildOfSuperAbstractToken);

					logger.debug("check whether the schema is provided or whether we've got a variable name, one of them should be a superabstract though");

					//assumption: first element could either be name of schema or name of superabstract
					Schema schema = findSchema(textOfFirstChildOfSuperAbstractToken);
					SuperAbstract sa1 = findSuperAbstract(textOfFirstChildOfSuperAbstractToken);

					logger.debug("textOfFirstChildOfSuperAbstractToken: " + textOfFirstChildOfSuperAbstractToken);
					logger.debug("schema: " + schema);
					logger.debug("sa1: " + sa1);

					//assumption: second element could either be name of superabstract or variable name

					SuperAbstract sa2 = findSuperAbstract(textOfSecondChildOfSuperAbstractToken);

					logger.debug("textOfSecondChildOfSuperAbstractToken: " + textOfSecondChildOfSuperAbstractToken);
					logger.debug("sa2: " + sa2);

					if (schema != null && sa2 != null) {
						//TODO
						logger.error("found schema, sa2, assume that's correct, no matter of sa1 - TODO check this though");
						if (schemas != null && schemas.size() > 0 && !schemaNames.contains(schema.getName())) {
							//TODO
							logger.error("found schema, but isn't in the list of schemas provided - for now: add schema to list, check this though - TODO");
							schemas.add(schema);
							schemaNames.add(schema.getName());
						}
						superAbstract = sa2;
						varName = this.subQueryPrefix + textOfSecondChildOfSuperAbstractToken;
						logger.debug("varName: " + varName);
						variableNameSuperAbstractMap.put(varName, superAbstract);

					} else if (schema == null && sa1 != null && sa2 == null) {
						logger.debug("found sa1, but not sa2, assume sa2 is variable name");
						superAbstract = sa1;
						varName = this.subQueryPrefix + textOfSecondChildOfSuperAbstractToken;
						logger.debug("varName: " + varName);
						variableNameSuperAbstractMap.put(varName, superAbstract);

					} else if (schema == null && sa1 == null && sa2 == null) {
						//TODO
						logger.error("didn't find anything - problem - TODO");
					}

				} else {
					//TODO
					logger.debug("more than two child elements - TODO");
				}

			} else if (childText.equals(ASTConstants.JOIN_TOKEN) || childText.equals(ASTConstants.LEFT_OUTER_JOIN_TOKEN)
					|| childText.equals(ASTConstants.RIGHT_OUTER_JOIN_TOKEN) || childText.equals(ASTConstants.FULL_OUTER_JOIN_TOKEN)) {
				logger.debug("found join: TODO");
				//TODO sort out join predicates
				//Predicate joinPredicate = buildJoinPredicate(child);
				//logger.debug("joinPredicate: " + joinPredicate);
				//if (joinPredicate != null)
				//	joinPredicates.add(joinPredicate);
			} else {
				logger.error("unexpected token in fromList, TODO proper error handling");
			}
			if (superAbstract != null) {
				logger.debug("found superAbstract: " + superAbstract);
				ScanOperator scanOperator = new ScanOperator(superAbstract);
				if (varName != null) {
					scanOperator.setVariableName(varName);
					superAbstract.setVariableName(varName);
				}
				logger.debug("created ScanOperator: " + scanOperator);
				superAbstractScanOperatorsMap.put(superAbstract, scanOperator);
				saMappingOperatorsMap.put(superAbstract, scanOperator);
				logger.debug("about to set resultType of scanOperator");
				String resultFieldVariableName = "";
				if (varName != null)
					resultFieldVariableName = varName + ".";
				else
					resultFieldVariableName = superAbstract.getName();
				ResultType resultType = new ResultType();
				List<SuperLexical> superLexicals = superLexicalRepository.getAllSuperLexicalsOfSuperAbstractOrderedById(superAbstract.getId());
				//ruhai
				if (superLexicals != null) {
					for (SuperLexical superLexical : superLexicals) {
						ResultField resultField = new ResultField(resultFieldVariableName + superLexical.getName(), superLexical.getDataType());
						resultField.setCanonicalModelConstruct(superLexical);
						//resultType.getResultFields().add(resultField);
						resultType.addResultField(resultField.getFieldName(), resultField);
						logger.debug("added resultField to resultType of scanOperator, resultField: " + resultField);
						//resultField.setResultType(resultType);
					}
				}
				DataSource dataSource = superAbstract.getSchema().getDataSource();
				scanOperator.setDataSource(dataSource);
				scanOperator.setResultType(resultType);
				logger.debug("set resultType of scanOperator, resultType: " + resultType);
			}
		}
	}

	protected SuperAbstract findSuperAbstract(String superAbstractName) {
		logger.debug("in findSuperAbstract");
		logger.debug("superAbstractName: " + superAbstractName);
		//superAbstractName = superAbstractName.toLowerCase();
		//logger.debug("superAbstractName in lowerCase: " + superAbstractName);
		SuperAbstract superAbstract = null;

		if (!schemaNames.isEmpty()) {
			logger.debug("!schemaNames.isEmpty()");
			for (String schemaName : schemaNames) {
				logger.debug("schemaName: " + schemaName);
				List<SuperAbstract> sas = findAllSuperAbstractsWithNameInSchema(superAbstractName, schemaName);
				if (sas != null && sas.size() > 0) {
					SuperAbstract sa = null;
					if (sas.size() == 1) {
						sa = sas.get(0);
						logger.debug("sa: " + sa);
					} else {
						logger.debug("found multiple superAbstracts with name: " + superAbstractName);
						logger.debug("check whether they're global or local superAbstracts and try to find global superAbstract");
						boolean foundGlobalSA = false;
						for (SuperAbstract superAbstr : sas) {
							if (superAbstr.isGlobal()) {
								logger.debug("found global superAbstract");
								logger.debug("superAbstr: " + superAbstr);
								if (!foundGlobalSA) {
									sa = superAbstr;
									foundGlobalSA = true;
								} else {
									//TODO
									logger.error("found multiple global superAbstracts - TODO sort this out");
								}
							}
						}
					}
					if (superAbstract == null)
						superAbstract = sa;
					else if (sa != null) {
						//TODO
						logger.error("found multiple superAbstracts with same name in the schemas provided --- TODO");
					}
				}
			}
		} else {
			logger.debug("no schema names found");
			List<SuperAbstract> sas = findAllSuperAbstractsWithName(superAbstractName);
			if (sas == null || sas.size() == 0) {
				//TODO
				//TODO check whether queries return null or empty sets
				logger.error("didn't find superAbstract with name: " + superAbstractName);
			} else if (sas.size() > 1) {
				//TODO
				logger.error("found multiple superAbstracts with same name in the schemas provided - try to find global superAbstract");
				boolean foundGlobalSA = false;
				for (SuperAbstract superAbstr : sas) {
					if (superAbstr.isGlobal()) {
						logger.debug("found global superAbstract");
						logger.debug("superAbstr: " + superAbstr);
						if (!foundGlobalSA) {
							superAbstract = superAbstr;
							foundGlobalSA = true;
						} else {
							//TODO
							logger.error("found multiple global superAbstracts - TODO sort this out");
						}
					}
				}
			} else
				superAbstract = sas.get(0);
		}
		return superAbstract;
	}

	protected Schema findSchema(String schemaName) {
		logger.debug("schemaRepository: " + schemaRepository);
		return schemaRepository.getSchemaByName(schemaName);
	}

	protected SuperAbstract findSuperAbstractWithNameInSchema(String superAbstractName, String schemaName) {
		return superAbstractRepository.getSuperAbstractByNameInSchemaWithName(superAbstractName, schemaName);
	}

	protected List<SuperAbstract> findAllSuperAbstractsWithNameInSchema(String superAbstractName, String schemaName) {
		return superAbstractRepository.getAllSuperAbstractsByNameInSchemaWithName(superAbstractName, schemaName);
	}

	protected List<SuperAbstract> findAllSuperAbstractsWithName(String superAbstractName) {
		return superAbstractRepository.getAllSuperAbstractsWithName(superAbstractName);
	}

	private void printOperatorTree(MappingOperator rootOperator) {
		logger.debug("operator: " + rootOperator);
		if (rootOperator.getLhsInput() != null)
			printOperatorTree(rootOperator.getLhsInput());
		if (rootOperator.getRhsInput() != null)
			printOperatorTree(rootOperator.getRhsInput());
	}

	/*
	 * Translate a SELECT tree node.
	 */
	private MappingOperator translateSelect(CommonTree selectListAST, MappingOperator currentRootOperator) {
		logger.debug("in translateSelect");
		StringBuilder reconcilingExpressionBuilder = new StringBuilder();
		Map<String, SuperLexical> varNameSuperLexicalsMap = new LinkedHashMap<String, SuperLexical>();
		ResultType resultType = new ResultType();

		ReduceOperator reduce = new ReduceOperator();
		reduce.setInput(currentRootOperator);
		logger.debug("currentRootOperator: " + currentRootOperator);
		if (currentRootOperator != null)
			reduce.setDataSource(currentRootOperator.getDataSource());

		//TODO will need to carry varNames around with me and add them to all operators, but might be rename operator - check this

		for (int i = 0; i < selectListAST.getChildCount(); i++) {
			CommonTree child = (CommonTree) selectListAST.getChild(i);
			logger.debug("child of selectListAST: " + child.toStringTree());
			String childText = child.getText();
			logger.debug("childText: " + childText);

			if (childText.equals(ASTConstants.SUPER_LEXICAL_TOKEN)) {
				logger.debug("found reference to superLexical");

				if (child.getChildCount() > 1) {
					logger.debug("alias exists; TODO");
					//TODO grammar can't handle it though, work on this; deal with alias
				}

				CommonTree childOfSuperLexicalToken = (CommonTree) child.getChild(0);
				logger.debug("childOfSuperLexicalToken: " + childOfSuperLexicalToken.toStringTree());
				String textOfChildOfSuperLexicalToken = childOfSuperLexicalToken.getText();
				logger.debug("textOfChildOfSuperLexicalToken: " + textOfChildOfSuperLexicalToken);

				if (textOfChildOfSuperLexicalToken.equals(ASTConstants.SUPER_LEXICAL_OF_SUPER_ABSTRACT_TOKEN)) {
					logger.debug("found SUPER_LEXICAL_OF_SUPER_ABSTRACT_TOKEN");

					logger.debug("most likely variable name and superabstract name");
					String child1Text = childOfSuperLexicalToken.getChild(0).getText();
					logger.debug("child1Text: " + child1Text);
					String child2Text = null;
					if (childOfSuperLexicalToken.getChild(1) != null)
						child2Text = childOfSuperLexicalToken.getChild(1).getText();
					logger.debug("child2Text: " + child2Text);

					String fullVarName = "";

					Map<SuperLexical, String> map = findSuperLexical(childOfSuperLexicalToken);
					logger.debug("after finding superLexical");
					if (map != null) {
						SuperLexical sl = map.keySet().iterator().next();
						logger.debug("superLexical: " + sl);
						String varName = map.get(sl);
						logger.debug("varName of superAbstract of SuperLexical: " + varName);
						String superLexicalName = sl.getName();
						fullVarName = varName + "." + superLexicalName;
						logger.debug("fullVarName: " + fullVarName);
						varNameSuperLexicalsMap.put(fullVarName, sl);
						logger.debug("add sl to varNameSuperLexicalsMap for reduceOperator");
						//TODO might need to add subQueryPrefix to varName, check this - TODO
						ResultField resultField = new ResultField(fullVarName, sl.getDataType());
						resultField.setCanonicalModelConstruct(sl);
						logger.debug("added resultField to reduceOperator, resultField: " + resultField);
						//resultType.getResultFields().add(resultField);
						resultType.addResultField(resultField.getFieldName(), resultField);
						//resultField.setResultType(resultType);
					}

					if (i > 0)
						reconcilingExpressionBuilder.append(", ");
					if (this.subQueryPrefix.length() > 0) {
						reconcilingExpressionBuilder.append(this.subQueryPrefix);
					}
					if (child2Text == null) {
						reconcilingExpressionBuilder.append(fullVarName);
					} else {
						reconcilingExpressionBuilder.append(child1Text);
						reconcilingExpressionBuilder.append(".");
						reconcilingExpressionBuilder.append(child2Text);
					}
				} else if (textOfChildOfSuperLexicalToken.equals("*")) {
					logger.debug("found *, shouldn't need a reduce, basically done with select clause then, but add reduce operator for ease of further propcessing");

					ResultType resultTypeOfCurrentRootOperator = currentRootOperator.getResultType();

					Map<String, ResultField> resultFields = resultTypeOfCurrentRootOperator.getResultFields();
					Set<String> resultFieldNames = resultFields.keySet();
					int j = 0;
					//for (ResultField field : resultFields) {
					for (String resultFieldName : resultFieldNames) {
						if (j > 0)
							reconcilingExpressionBuilder.append(", ");
						//reconcilingExpressionBuilder.append(field.getFieldName());
						reconcilingExpressionBuilder.append(resultFieldName);
						//ResultField resultField = new ResultField(field.getFieldName(), field.getFieldType());
						ResultField resultField = new ResultField(resultFieldName, resultFields.get(resultFieldName).getFieldType());
						//resultField.setCanonicalModelConstruct(field.getCanonicalModelConstruct());
						resultField.setCanonicalModelConstruct(resultFields.get(resultFieldName).getCanonicalModelConstruct());
						//resultType.getResultFields().add(resultField);
						resultType.addResultField(resultFieldName, resultField);
						//resultField.setResultType(resultType);

						//CanonicalModelConstruct cmc = field.getCanonicalModelConstruct();
						CanonicalModelConstruct cmc = resultFields.get(resultFieldName).getCanonicalModelConstruct();
						SuperLexical sl = null;
						if (cmc instanceof SuperLexical)
							sl = (SuperLexical) cmc;

						varNameSuperLexicalsMap.put(resultField.getFieldName(), sl);
						j++;
					}

					//return currentRootOperator;
					//TODO could also add all the lexicals that have to be returned instead of leaving the *
				} else {
					logger.debug("unexpected token: " + textOfChildOfSuperLexicalToken + " TODO proper error handling");
				}

			} else if (childText.equals(ASTConstants.DISTINCT_TOKEN) || childText.equals(ASTConstants.ALL_TOKEN)) {
				logger.debug("found distinct or all - still TODO");
				//TODO sort out distinct and all
			} else {
				//TODO aggregates still to sort out
				/*
				 * else if (treeNode instanceof AggregateAST) {
				*  	AggregateAST ast = (AggregateAST) treeNode;
				*   resultTuple.add(addAggregate(ast));
				*  }
				 */
				logger.error("unexpected token in fromList, TODO proper error handling");
			}
		}

		reduce.setReconcilingExpression(reconcilingExpressionBuilder.toString());
		reduce.setSuperLexicals(varNameSuperLexicalsMap);
		reduce.setResultType(resultType);
		logger.debug("set resultType or reduceOperator, resultType: " + resultType);
		logger.debug("reduceOperator: " + reduce);
		logger.debug("reduce.reconcilingExpression: " + reduce.getReconcilingExpression().getExpression());
		currentRootOperator = reduce;
		return reduce;
	}

	//TODO these would be better off in the optimiser instead of here, move them

	/**
	 * @param ast the ast to set
	 */
	/*
	public void setAst(CommonTree ast) {
		this.ast = ast;
	}
	*/

	/**
	 * @return the ast
	 */
	/*
	public CommonTree getAst() {
		return ast;
	}
	*/

	/**
	 * @param superLexicalRepository the superLexicalRepository to set
	 */
	public void setSuperLexicalRepository(SuperLexicalRepository superLexicalRepository) {
		this.superLexicalRepository = superLexicalRepository;
	}

	/**
	 * @return the superLexicalRepository
	 */
	public SuperLexicalRepository getSuperLexicalRepository() {
		return superLexicalRepository;
	}

	/**
	 * @param superAbstractRepository the superAbstractRepository to set
	 */
	public void setSuperAbstractRepository(SuperAbstractRepository superAbstractRepository) {
		this.superAbstractRepository = superAbstractRepository;
	}

	/**
	 * @return the superAbstractRepository
	 */
	public SuperAbstractRepository getSuperAbstractRepository() {
		return superAbstractRepository;
	}

	/**
	 * @return the saMappingOperatorsMap
	 */
	private Map<SuperAbstract, MappingOperator> getSaMappingOperatorsMap() {
		return saMappingOperatorsMap;
	}

	/**
	 * @param saMappingOperatorsMap the saMappingOperatorsMap to set
	 */
	private void setSaMappingOperatorsMap(Map<SuperAbstract, MappingOperator> saMappingOperatorsMap) {
		this.saMappingOperatorsMap = saMappingOperatorsMap;
	}

	/**
	 * @return the schemaRepository
	 */
	public SchemaRepository getSchemaRepository() {
		return schemaRepository;
	}

	/**
	 * @param schemaRepository the schemaRepository to set
	 */
	public void setSchemaRepository(SchemaRepository schemaRepository) {
		this.schemaRepository = schemaRepository;
	}

	/**
	 * @return the joinOperators
	 */
	private List<JoinOperator> getJoinOperators() {
		return joinOperators;
	}

	/**
	 * @param joinOperators the joinOperators to set
	 */
	private void setJoinOperators(List<JoinOperator> joinOperators) {
		this.joinOperators = joinOperators;
	}

	/**
	 * @return the variableNameSubQueryRootOperator
	 */
	private Map<String, MappingOperator> getVariableNameSubQueryRootOperator() {
		return variableNameSubQueryRootOperator;
	}

	/**
	 * @param variableNameSubQueryRootOperator the variableNameSubQueryRootOperator to set
	 */
	private void setVariableNameSubQueryRootOperator(Map<String, MappingOperator> variableNameSubQueryRootOperator) {
		this.variableNameSubQueryRootOperator = variableNameSubQueryRootOperator;
	}

	/**
	 * @return the variableNameSuperAbstractMap
	 */
	private Map<String, SuperAbstract> getVariableNameSuperAbstractMap() {
		return variableNameSuperAbstractMap;
	}

	/**
	 * @param variableNameSuperAbstractMap the variableNameSuperAbstractMap to set
	 */
	private void setVariableNameSuperAbstractMap(Map<String, SuperAbstract> variableNameSuperAbstractMap) {
		this.variableNameSuperAbstractMap = variableNameSuperAbstractMap;
	}

	/**
	 * @return the subQueries
	 */
	private Map<MappingOperator, String> getSubQueries() {
		return subQueries;
	}

	/**
	 * @param subQueries the subQueries to set
	 */
	private void setSubQueries(Map<MappingOperator, String> subQueries) {
		this.subQueries = subQueries;
	}

	/**
	 * @return the schemaNames
	 */
	public Set<String> getSchemaNames() {
		return schemaNames;
	}

	/**
	 * @param schemaNames the schemaNames to set
	 */
	public void setSchemaNames(Set<String> schemaNames) {
		this.schemaNames = schemaNames;
	}

}
