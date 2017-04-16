package uk.ac.manchester.dstoolkit.service.impl.query.queryparser;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.TreeAdaptor;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.exceptions.ParserRecognitionException;
import uk.ac.manchester.dstoolkit.exceptions.SQLParserException;
import uk.ac.manchester.dstoolkit.service.query.queryparser.SQLQueryParserService;

@Service(value = "sqlQueryParserService")
public class SQLQueryParserServiceImpl implements SQLQueryParserService {

	private static SQLQueryParserService parserInstance = new SQLQueryParserServiceImpl();
	private static CommonTree ast;

	private SQLQueryParserServiceImpl() {
	}

	public static SQLQueryParserService getInstance() {
		return parserInstance;
	}

	public static CommonTree getAst() {
		return ast;
	}

	//TODO check whether any of these methods could be useful

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.impl.operators.query.queryparser.SQLQueryParserService#parseSQL(java.lang.String)
	 */
	public CommonTree parseSQL(String expression) throws SQLParserException {
		DSToolkitSQLLexer lex = new DSToolkitSQLLexer(new ANTLRNoCaseStringStream(expression));
		CommonTokenStream tokens = new CommonTokenStream(lex);

		DSToolkitSQLParser g = new DSToolkitSQLParser(tokens);

		try {
			DSToolkitSQLParser.statement_return result = g.statement();
			return (CommonTree) result.getTree();
		} catch (RecognitionException e) {
			throw new ParserRecognitionException(e);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.impl.operators.query.queryparser.SQLQueryParserService#parseSQLForQuery(java.lang.String)
	 */
	public CommonTree parseSQLForQuery(String expression) throws SQLParserException {
		DSToolkitSQLLexer lex = new DSToolkitSQLLexer(new ANTLRNoCaseStringStream(expression));
		CommonTokenStream tokens = new CommonTokenStream(lex);

		DSToolkitSQLParser g = new DSToolkitSQLParser(tokens);

		try {
			DSToolkitSQLParser.query_return result = g.query();
			return (CommonTree) result.getTree();
		} catch (RecognitionException e) {
			throw new ParserRecognitionException(e);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.impl.operators.query.queryparser.SQLQueryParserService#parseSQLForSuperAbstractExpression(java.lang.String)
	 */
	public CommonTree parseSQLForSuperAbstractExpression(String expression) throws SQLParserException {
		DSToolkitSQLLexer lex = new DSToolkitSQLLexer(new ANTLRNoCaseStringStream(expression));
		CommonTokenStream tokens = new CommonTokenStream(lex);

		DSToolkitSQLParser g = new DSToolkitSQLParser(tokens);
		try {
			DSToolkitSQLParser.super_abstract_expression_return result = g.super_abstract_expression();
			return (CommonTree) result.getTree();
		} catch (RecognitionException e) {
			throw new ParserRecognitionException(e);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.impl.operators.query.queryparser.SQLQueryParserService#parseSQLForSuperLexicalList(java.lang.String)
	 */
	public CommonTree parseSQLForSuperLexicalList(String expression) throws SQLParserException {
		DSToolkitSQLLexer lex = new DSToolkitSQLLexer(new ANTLRNoCaseStringStream(expression));
		CommonTokenStream tokens = new CommonTokenStream(lex);

		DSToolkitSQLParser g = new DSToolkitSQLParser(tokens);
		try {
			DSToolkitSQLParser.super_lexical_list_return result = g.super_lexical_list();
			return (CommonTree) result.getTree();
		} catch (RecognitionException e) {
			throw new ParserRecognitionException(e);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.impl.operators.query.queryparser.SQLQueryParserService#parseSQLForDerivedSuperLexical(java.lang.String)
	 */
	public CommonTree parseSQLForDerivedSuperLexical(String expression) throws SQLParserException {
		DSToolkitSQLLexer lex = new DSToolkitSQLLexer(new ANTLRNoCaseStringStream(expression));
		CommonTokenStream tokens = new CommonTokenStream(lex);

		DSToolkitSQLParser g = new DSToolkitSQLParser(tokens);
		try {
			DSToolkitSQLParser.derived_super_lexical_return result = g.derived_super_lexical();
			return (CommonTree) result.getTree();
		} catch (RecognitionException e) {
			throw new ParserRecognitionException(e);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.impl.operators.query.queryparser.SQLQueryParserService#parseSQLForValueExpressionOrStar(java.lang.String)
	 */
	public CommonTree parseSQLForValueExpressionOrStar(String expression) throws SQLParserException {
		if (expression.equals(ASTConstants.STAR_TOKEN)) {
			TreeAdaptor adaptor = new CommonTreeAdaptor();
			CommonTree ct = (CommonTree) adaptor.create(DSToolkitSQLParser.ID, ASTConstants.STAR_TOKEN);

			return ct;
		}

		DSToolkitSQLLexer lex = new DSToolkitSQLLexer(new ANTLRNoCaseStringStream(expression));
		CommonTokenStream tokens = new CommonTokenStream(lex);

		DSToolkitSQLParser g = new DSToolkitSQLParser(tokens);
		try {
			DSToolkitSQLParser.value_expression_return result = g.value_expression();
			return (CommonTree) result.getTree();
		} catch (RecognitionException e) {
			throw new ParserRecognitionException(e);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.impl.operators.query.queryparser.SQLQueryParserService#parseSQLForLiteral(java.lang.String)
	 */
	public CommonTree parseSQLForLiteral(String expression) throws SQLParserException {
		DSToolkitSQLLexer lex = new DSToolkitSQLLexer(new ANTLRNoCaseStringStream(expression));
		CommonTokenStream tokens = new CommonTokenStream(lex);

		DSToolkitSQLParser g = new DSToolkitSQLParser(tokens);

		try {
			DSToolkitSQLParser.literal_return result = g.literal();
			return (CommonTree) result.getTree();
		} catch (RecognitionException e) {
			throw new ParserRecognitionException(e);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.impl.operators.query.queryparser.SQLQueryParserService#parseSQLForCondition(java.lang.String)
	 */
	public CommonTree parseSQLForCondition(String expression) throws SQLParserException {
		DSToolkitSQLLexer lex = new DSToolkitSQLLexer(new ANTLRNoCaseStringStream(expression));
		CommonTokenStream tokens = new CommonTokenStream(lex);

		DSToolkitSQLParser g = new DSToolkitSQLParser(tokens);

		try {
			DSToolkitSQLParser.search_condition_return result = g.search_condition();
			return (CommonTree) result.getTree();
		} catch (RecognitionException e) {
			throw new ParserRecognitionException(e);
		}
	}
}
