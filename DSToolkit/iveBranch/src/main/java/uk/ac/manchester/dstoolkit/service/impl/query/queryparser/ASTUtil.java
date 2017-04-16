package uk.ac.manchester.dstoolkit.service.impl.query.queryparser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.tree.CommonTree;

import uk.ac.manchester.dstoolkit.exceptions.TranslationException;
import uk.ac.manchester.dstoolkit.service.query.queryparser.SQLQueryParserService;

public class ASTUtil {

	//TODO check this class, write tests, check whether I need everything

	/** AND token. */
	private static CommonToken mAndToken = null;
	/** OR token. */
	private static CommonToken mOrToken = null;
	/** = token. */
	private static CommonToken mEqualsToken = null;
	/** >= token. */
	private static CommonToken mGreaterThanEqualsToken = null;
	/** > token. */
	private static CommonToken mGreaterThanToken = null;
	/** < token. */
	private static CommonToken mLessThanEqualsToken = null;
	/** <= token */
	private static CommonToken mLessThanToken = null;
	/** literal token */
	private static CommonToken mLiteralToken = null;

	/**
	 * Gets the superAbstract name from a SUPER_LEXICAL_OF_SUPER_ABSTRACT node.
	 * 
	 * @param Node TABLECOLUMN node
	 * 
	 * @return the table name with dots to separate the components of the
	 *         name if there are multiple components, for example 
	 *         <tt>"a.b"</tt>.
	 */
	public static String getTableColumnName(CommonTree tableColumnNode) {
		if (tableColumnNode.getText().equals("SUPER_LEXICAL_OF_SUPER_ABSTRACT")) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < tableColumnNode.getChildCount(); ++i) {
				if (i > 0) {
					sb.append(".");
				}
				sb.append(tableColumnNode.getChild(i).getText());
			}
			return sb.toString();
		}
		return null;
	}

	/**
	 * Creates a new literal node containing the given text.
	 * 
	 * @param text text the literal node should contain
	 * 
	 * @return new literal node
	 */
	public static synchronized CommonTree getLiteralNode(String text) {
		if (mLiteralToken == null) {
			String condition = "x=2";
			try {
				CommonTree literal = (CommonTree) SQLQueryParserServiceImpl.getInstance().parseSQLForCondition(condition).getChild(1);

				mLiteralToken = (CommonToken) literal.getToken();
			} catch (uk.ac.manchester.dstoolkit.exceptions.SQLParserException e) {
				// Should not occur
			}
		}
		return new CommonTree(new CommonToken(mLiteralToken.getType(), text));
	}

	/**
	 * Gets a new greater than or equals (>=) node.
	 * 
	 * @param lhsChild left hand side child
	 * @param rhsChild right hand side child
	 * 
	 * @return new greater than or equals node containing the given children.
	 */
	public static synchronized CommonTree getGreaterThanEqualsNode(CommonTree lhsChild, CommonTree rhsChild) {
		if (mGreaterThanEqualsToken == null) {
			String condition = "x>=y";
			try {
				mGreaterThanEqualsToken = (CommonToken) SQLQueryParserServiceImpl.getInstance().parseSQLForCondition(condition).getToken();
			} catch (uk.ac.manchester.dstoolkit.exceptions.SQLParserException e) {
				// Should not occur
				throw new RuntimeException("Failed to parse " + condition + " : should not happen", e);
			}
		}
		CommonTree result = new CommonTree(mGreaterThanEqualsToken);
		result.addChild(lhsChild);
		result.addChild(rhsChild);
		return result;
	}

	/**
	 * Gets a new greater than (>) node.
	 * 
	 * @param lhsChild left hand side child
	 * @param rhsChild right hand side child
	 * 
	 * @return new greater than node containing the given children.
	 */
	public static synchronized CommonTree getGreaterThanNode(CommonTree lhsChild, CommonTree rhsChild) {
		if (mGreaterThanToken == null) {
			String condition = "x>y";
			try {
				mGreaterThanToken = (CommonToken) SQLQueryParserServiceImpl.getInstance().parseSQLForCondition(condition).getToken();
			} catch (uk.ac.manchester.dstoolkit.exceptions.SQLParserException e) {
				// Should not occur
				throw new RuntimeException("Failed to parse " + condition + " : should not happen", e);
			}
		}
		CommonTree result = new CommonTree(mGreaterThanToken);
		result.addChild(lhsChild);
		result.addChild(rhsChild);
		return result;
	}

	/**
	 * Gets a new less than or equals (<=) node.
	 * 
	 * @param lhsChild left hand side child
	 * @param rhsChild right hand side child
	 * 
	 * @return new less than or equals node containing the given children.
	 */
	public static synchronized CommonTree getLessThanEqualsNode(CommonTree lhsChild, CommonTree rhsChild) {
		if (mLessThanEqualsToken == null) {
			String condition = "x<=y";
			try {
				mLessThanEqualsToken = (CommonToken) SQLQueryParserServiceImpl.getInstance().parseSQLForCondition(condition).getToken();
			} catch (uk.ac.manchester.dstoolkit.exceptions.SQLParserException e) {
				// Should not occur
				throw new RuntimeException("Failed to parse " + condition + " : should not happen", e);
			}
		}
		CommonTree result = new CommonTree(mLessThanEqualsToken);
		result.addChild(lhsChild);
		result.addChild(rhsChild);
		return result;
	}

	/**
	 * Gets a new less than (<) node.
	 * 
	 * @param lhsChild left hand side child
	 * @param rhsChild right hand side child
	 * 
	 * @return new less than node containing the given children.
	 */
	public static synchronized CommonTree getLessThanNode(CommonTree lhsChild, CommonTree rhsChild) {
		if (mLessThanToken == null) {
			String condition = "x<y";
			try {
				mLessThanToken = (CommonToken) SQLQueryParserServiceImpl.getInstance().parseSQLForCondition(condition).getToken();
			} catch (uk.ac.manchester.dstoolkit.exceptions.SQLParserException e) {
				// Should not occur
				throw new RuntimeException("Failed to parse " + condition + " : should not happen", e);
			}
		}
		CommonTree result = new CommonTree(mLessThanToken);
		result.addChild(lhsChild);
		result.addChild(rhsChild);
		return result;
	}

	/**
	 * Gets a new equals (=) node.
	 * 
	 * @param lhsChild left hand side child
	 * @param rhsChild right hand side child
	 * 
	 * @return new equals node containing the given children.
	 */
	public static synchronized CommonTree getEqualsNode(CommonTree lhsChild, CommonTree rhsChild) {
		if (mEqualsToken == null) {
			String condition = "x=y";
			try {
				mEqualsToken = (CommonToken) SQLQueryParserServiceImpl.getInstance().parseSQLForCondition(condition).getToken();
			} catch (uk.ac.manchester.dstoolkit.exceptions.SQLParserException e) {
				// Should not occur
				throw new RuntimeException("Failed to parse " + condition + " : should not happen", e);
			}
		}
		CommonTree result = new CommonTree(mEqualsToken);
		result.addChild(lhsChild);
		result.addChild(rhsChild);
		return result;
	}

	/**
	 * Gets a new AND node with no children.
	 * 
	 * @return a new AND node.  
	 */
	public static synchronized CommonTree getAndNode() {
		if (mAndToken == null) {
			String condition = "x=y AND a=b";
			try {
				mAndToken = (CommonToken) SQLQueryParserServiceImpl.getInstance().parseSQLForCondition(condition).getToken();
			} catch (uk.ac.manchester.dstoolkit.exceptions.SQLParserException e) {
				// Should not occur
				throw new RuntimeException("Failed to parse " + condition + " : should not happen", e);
			}
		}
		return new CommonTree(mAndToken);
	}

	/**
	 * Creates a new AND node.
	 * 
	 * @param lhsChild left hand size child
	 * @param rhsChild right hand size child
	 * 
	 * @return a new AND node with the specified children.
	 */
	public static CommonTree getAndNode(CommonTree lhsChild, CommonTree rhsChild) {
		CommonTree result = getAndNode();
		result.addChild(lhsChild);
		result.addChild(rhsChild);
		return result;
	}

	/**
	 * Gets a new OR node with no children.
	 * 
	 * @return a new OR node, caller should add two children.
	 */
	public static synchronized CommonTree getOrNode() {
		if (mOrToken == null) {
			String condition = "x=y OR a=b";
			try {
				mOrToken = (CommonToken) SQLQueryParserServiceImpl.getInstance().parseSQLForCondition(condition).getToken();
			} catch (uk.ac.manchester.dstoolkit.exceptions.SQLParserException e) {
				// Should not occur
				throw new RuntimeException("Failed to parse " + condition + " : should not happen", e);
			}
		}
		return new CommonTree(mOrToken);
	}

	/**
	 * Gets a new OR node.
	 * 
	 * @param lhsChild left hand size child
	 * @param rhsChild right hand size child
	 * 
	 * @return a new OR node with the specified children.
	 */
	public static CommonTree getOrNode(CommonTree lhsChild, CommonTree rhsChild) {
		CommonTree result = getOrNode();
		result.addChild(lhsChild);
		result.addChild(rhsChild);
		return result;
	}

	/**
	 * Gets a new where node with no children.
	 * 
	 * @return a new where node.
	 */
	public static CommonTree getWhereNode() {
		return new CommonTree(new CommonToken(DSToolkitSQLParser.WHERE, ASTConstants.WHERE_TOKEN));
	}

	/**
	 * Is the given node a where node.
	 * 
	 * @param node node to check
	 * 
	 * @return <tt>true</tt> if the node is a where node, <tt>false</tt> 
	 *         otherwise.
	 */
	public static boolean isWhereNode(CommonTree node) {
		return node.getText().equals(ASTConstants.WHERE_TOKEN);
	}

	/**
	 * Is the given node an AND node.
	 * 
	 * @param node node to check
	 * 
	 * @return <tt>true</tt> if the node is an AND node, <tt>false</tt> 
	 *         otherwise.
	 */
	public static boolean isAndNode(CommonTree node) {
		return node.getText().equals(ASTConstants.AND_TOKEN);
	}

	/**
	 * Is the given node an OR node.
	 * 
	 * @param node node to check
	 * 
	 * @return <tt>true</tt> if the node is an OR node, <tt>false</tt> 
	 *         otherwise.
	 */
	public static boolean isOrNode(CommonTree node) {
		return node.getText().equals(ASTConstants.OR_TOKEN);
	}

	/**
	 * Takes QUERY branch of the SQL AST and returns a map that maps string
	 * representations of QUERY node direct children to their ASTs.
	 * 
	 * @param queryAST
	 *            QUERY AST node
	 * @return map of child ASTs
	 */
	public static Map<String, CommonTree> getQueryStructure(CommonTree queryAST) {
		Map<String, CommonTree> queryStructure = new HashMap<String, CommonTree>();

		for (int i = 0; i < queryAST.getChildCount(); i++) {
			CommonTree child = (CommonTree) queryAST.getChild(i);
			queryStructure.put(child.getText(), child);
		}
		return queryStructure;
	}

	/**
	 * Checks if a given AST contains QUERY nodes.
	 * 
	 * @param ast
	 *            abstract syntax tree
	 * @return <code>true</code> if AST contains subqueries
	 */
	public static boolean containsSubqueries(CommonTree ast) {
		for (int i = 0; i < ast.getChildCount(); i++) {
			CommonTree child = (CommonTree) ast.getChild(i);

			// We are interested in the top level query only.
			if (child.getText().equals(ASTConstants.QUERY_TOKEN)) {
				return true;
			} else {
				containsSubqueries(child);
			}
		}
		return false;
	}

	/**
	 * Extracts aggregate functions. (FUNCTION) branches of the AST are replaced
	 * by (TABLECOLUMN bindingID) branches, where bindingID is automatically
	 * generated, unique ID.
	 * 
	 * User provided list is populated with Function objects representing
	 * extracted aggregates. Each function object stores associated bindingID.
	 * 
	 * @param ast
	 *            ast - usually WHERE or SELECT_LIST branches.
	 * @param aggregates
	 *            a List to be populated with Function objects.
	 * @param functionRepository
	 *            function repository
	 * @throws ExpressionException
	 *             when there were problems with parsing parameter list.
	 * @throws NoSuchFunctionException
	 *             when aggregate function name could not be found in the
	 *             repository
	 * @throws UnsupportedTokenException 
	 */
	//TODO still need to sort out aggregates, not supported right now
	/*
	public static void extractAggregates(CommonTree ast,
	    List<Function> aggregates, FunctionRepository functionRepository)
	    throws ExpressionException, NoSuchFunctionException
	{
	    for (int i = 0; i < ast.getChildCount(); i++)
	    {
	        CommonTree child = (CommonTree) ast.getChild(i);

	        // We are interested in the top level query only.
	        if (child.getText().equals(ASTConstants.FUNCTION_TOKEN))
	        {
	            // Get function name and retrieve function object.
	            Function function = functionRepository
	                .getFunctionInstanceByName(extractFunctionName(child));
	            function.initialise(
	                getParameters(child, 1, functionRepository));

	            if (function.getType() == FunctionType.SQL_AGGREGATE
	                || function.getType() == FunctionType.UDF_AGGREGATE)
	            {
	                // Check if function already exists
	                Function existingAggregate = null;
	                for (Function f : aggregates)
	                {
	                    if (function.equals(f))
	                    {
	                        existingAggregate = f;
	                    }
	                }
	                // If it does not then generate new bindingID and add new
	                // function
	                if (existingAggregate == null)
	                {
	                    Annotation.addResultNameAnnotation(function,
	                        LQPBuilder.getNextID());
	                    aggregates.add(function);
	                }
	                else
	                {
	                    function = existingAggregate;
	                }

	                TreeAdaptor adaptor = new CommonTreeAdaptor();
	                CommonTree tableColumn = (CommonTree) adaptor
	                    .create(
	                        SQL92QueryParser.TABLECOLUMN,
	                        SQL92QueryParser.tokenNames[SQL92QueryParser.TABLECOLUMN]);
	                tableColumn.addChild((CommonTree) adaptor.create(
	                    SQL92QueryParser.ID,
	                    Annotation.getResultNameAnnotation(function)));
	                ast.setChild(i, tableColumn);
	            }
	        }
	        // Skip sub queries
	        else if (!child.getText().equals(ASTConstants.QUERY_TOKEN))
	        {
	            extractAggregates(child, aggregates, functionRepository);
	        }
	    }
	}
	*/

	/**
	 * Extracts subqueries from the AST and replaces QUERY branches with
	 * TABLECOLUMN nodes having automatically generated names. A mapping from
	 * the generated table column name to the QUERY branch it replaced is added
	 * to a map.
	 * 
	 * @param astWithSubqueries
	 *            abstract syntax tree with subqueries
	 * @param subqueries
	 *            map of extracted subqueries
	 */
	/*
	public static void extractReplaceSubqueries(CommonTree astWithSubqueries, Map<String, CommonTree> subqueries) {
		for (int i = 0; i < astWithSubqueries.getChildCount(); i++) {
			CommonTree child = (CommonTree) astWithSubqueries.getChild(i);

			if (child.getText().equals(ASTConstants.QUERY_TOKEN)) {
				// Assign variable name and modify AST
				String bindingID = Translator.getNextID();

				TreeAdaptor adaptor = new CommonTreeAdaptor();
				CommonTree tableColumn = (CommonTree) adaptor.create(DataspacesSQLParser.SUPER_LEXICAL_OF_SUPER_ABSTRACT,
						DataspacesSQLParser.tokenNames[DataspacesSQLParser.SUPER_LEXICAL_OF_SUPER_ABSTRACT]);
				tableColumn.addChild((CommonTree) adaptor.create(DataspacesSQLParser.ID, bindingID));
				astWithSubqueries.setChild(i, tableColumn);

				subqueries.put(bindingID, child);
			} else {
				extractReplaceSubqueries(child, subqueries);
			}
		}
	}
	*/

	/**
	 * Extracts relations from SQL AST. Adds extracted RELATION nodes to a list.
	 * 
	 * @param astWithRelationChildren
	 *            abstract syntax tree with RELATION nodes
	 * @param relationASTs
	 *            container list for nodes
	 */
	public static void extractSuperAbstracts(CommonTree astWithSuperAbstractChildren, List<CommonTree> superAbstractASTs) {
		for (int i = 0; i < astWithSuperAbstractChildren.getChildCount(); i++) {
			CommonTree child = (CommonTree) astWithSuperAbstractChildren.getChild(i);
			if (child.getText().equals(ASTConstants.SUPER_ABSTRACT_TOKEN)) {
				superAbstractASTs.add(child);
			}
		}
	}

	/**
	 * Extracts existential predicates and replaces them with predicates that
	 * are always TRUE. Adds extracted EXISTS nodes to the list.
	 * 
	 * @param astWithExistentialPredicates
	 *            abstract syntax tree with EXISTS nodes
	 * @param existPredASTs
	 *            container list for extracted predicates
	 */
	public static void extractReplaceExistentialPredicates(CommonTree astWithExistentialPredicates, List<CommonTree> existPredASTs) {
		for (int i = 0; i < astWithExistentialPredicates.getChildCount(); i++) {
			CommonTree child = (CommonTree) astWithExistentialPredicates.getChild(i);

			if (child.getText().equals(ASTConstants.NOT_TOKEN)) {
				CommonTree notChild = (CommonTree) child.getChild(0);

				if (notChild.getText().equals(ASTConstants.NOT_TOKEN)) {
					// We have double negation NOT(NOT p) -> p
					astWithExistentialPredicates.setChild(i, notChild.getChild(0));
					// We still need to parse the collapsed branch
					i--;
				} else if (notChild.getText().equals(ASTConstants.EXISTS_TOKEN)) {
					// We have NOT EXISTS
					existPredASTs.add(child);
					astWithExistentialPredicates.setChild(i, getTrueAST());
				}
			} else if (child.getText().equals(ASTConstants.EXISTS_TOKEN)) {
				// We have EXISTS
				existPredASTs.add(child);
				astWithExistentialPredicates.setChild(i, getTrueAST());
			} else if (!child.getText().equals(ASTConstants.QUERY_TOKEN)) {
				extractReplaceExistentialPredicates(child, existPredASTs);
			}
		}
	}

	/**
	 * Gets a predicate that always returns TRUE.
	 * 
	 * @return
	 *      tautology AST
	 */
	public static CommonTree getTrueAST() {
		SQLQueryParserService parser = SQLQueryParserServiceImpl.getInstance();
		try {
			return parser.parseSQLForCondition("1=1");
		} catch (uk.ac.manchester.dstoolkit.exceptions.SQLParserException e) {
			// IMPOSSIBLE
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Walks AST and checks if unsupported tokens are present.
	 * 
	 * @param ast
	 *            SQL abstract syntax tree
	 * @throws LQPException
	 *             when unsupported token is found
	 */
	public static void checkForUnsupportedConditionTokens(CommonTree ast) throws TranslationException {
		String[] unsupportedComparisonTokens = new String[] { ASTConstants.ALL_TOKEN, ASTConstants.ANY_TOKEN, ASTConstants.IN_TOKEN,
				ASTConstants.OR_TOKEN, ASTConstants.BETWEEN_TOKEN };

		for (int i = 0; i < ast.getChildCount(); i++) {
			CommonTree child = (CommonTree) ast.getChild(i);
			for (String token : unsupportedComparisonTokens) {
				if (child.getText().toUpperCase().equals(token)) {
					throw new TranslationException(token);
				}
			}
			checkForUnsupportedConditionTokens(child);
		}
	}

	//TODO functions not supported right now
	/**
	 * Walks function AST and returns function name.
	 * 
	 * @param functionAST
	 *            function AST
	 * @return function name
	 */
	/*
	public static String extractFunctionName(CommonTree functionAST)
	{
	    return functionAST.getChild(0).getText();
	}
	*/

	//TODO functions not supported right now
	/**
	 * Gets parameters from FUNCTION ast.
	 * 
	 * @param functionAST
	 *            FUNCTION branch of SQL AST
	 * @param startIdx
	 *            node index of the first parameter
	 * @param functionRepository
	 *            function repository
	 * @return a list of parameters represented as arithmetic expressions
	 * @throws ExpressionException
	 *             when there is a problem building expression
	 * @throws UnsupportedTokenException 
	 */
	/*
	public static List<ArithmeticExpression> getParameters(
	        CommonTree functionAST, int startIdx,
	        FunctionRepository functionRepository) 
	    throws ExpressionException 
	{
	    List<ArithmeticExpression> parameters = 
	        new ArrayList<ArithmeticExpression>();

	    for (int i = startIdx; i < functionAST.getChildCount(); i++)
	    {
	        CommonTree child = (CommonTree) functionAST.getChild(i);
	        parameters.add(ArithmeticExpressionFactory
	            .buildArithmeticExpression(child, functionRepository));
	    }
	    return parameters;
	}
	*/

	//TODO hopefully won't need those
	/**
	 * Extracts predicated from the condition AST. If AST is a conjunction - it
	 * will be split to several predicates.
	 * 
	 * @param predicateList
	 *            list to which extracted predicates will be added
	 * @param conditionAST
	 *            condition AST
	 * @param functionRepository
	 * @throws ExpressionException
	 */
	/*
	public static void extractPredicates(List<uk.ac.manchester.dataspaces.queryevaluation.querycompiler.translator.globaltranslator.Predicate> predicateList,
	    CommonTree conditionAST)
	//, FunctionRepository functionRepository
	    throws TranslationException
	{
	    if (conditionAST.getText().toUpperCase().equals(ASTConstants.AND_TOKEN))
	    {
	        for (int i = 0; i < conditionAST.getChildCount(); i++)
	        {
	            extractPredicates(predicateList,
	                (CommonTree) conditionAST.getChild(i)); //, functionRepository);
	        }
	    }
	    else
	    {
	        List<SuperLexicalReference> boundSuperLexicalReference = new ArrayList<SuperLexicalReference>();
	        extractBoundAttributes(boundSuperLexicalReference, conditionAST);

	        Predicate p =
	            new Predicate(ExpressionFactory.buildExpression(conditionAST,
	                functionRepository));

	        if (boundAttr.size() > 0)
	        {
	            if (boundAttr.size() > 1)
	            {
	                throw new IllegalStateException(
	                    "Multiple attributes that need to be bound are not " +
	                    "allowed in a single predicate.");
	            }
	            predicateList.add(new BindingPredicate(p, boundAttr.get(0)));
	        }
	        else
	        {
	            predicateList.add(p);
	        }
	    }
	}
	*/

	//TODO hopefully won't need this
	/**
	 * Extracts attributes to be bound from AST and rewrites AST to a
	 * conditionAST with BOUND tokens removed.
	 * 
	 * @param boundAttributes
	 *            a list to which attributes to be bound will be added
	 * @param conditionAST
	 *            condition AST
	 */
	/*
	private static void extractBoundSuperLexicalReference(List<SuperLexicalReference> boundSuperLexicalReferences,
	    CommonTree conditionAST)
	{
	    for (int i = 0; i < conditionAST.getChildCount(); i++)
	    {
	        CommonTree child = (CommonTree) conditionAST.getChild(i);
	        if (child.getType() == DataspacesSQLParser.BOUND)
	        {
	            CommonTree superLexicalOfSuperAbstractAST = (CommonTree) child.getChild(0);
	            boundSuperLexicals.add(new AttributeImpl(
	                tableColumnAST.getChild(1).getText(),
	                tableColumnAST.getChild(0).getText()));
	            conditionAST.setChild(i, tableColumnAST);
	        }
	        else
	        {
	            extractBoundAttributes(boundAttributes, child);
	        }
	    }
	}
	*/
}
