package uk.ac.manchester.dstoolkit.service.query.queryparser;

import org.antlr.runtime.tree.CommonTree;

import uk.ac.manchester.dstoolkit.exceptions.SQLParserException;

public interface SQLQueryParserService {

	public abstract CommonTree parseSQL(String expression) throws SQLParserException;

	public abstract CommonTree parseSQLForQuery(String expression) throws SQLParserException;

	public abstract CommonTree parseSQLForSuperAbstractExpression(String expression) throws SQLParserException;

	public abstract CommonTree parseSQLForSuperLexicalList(String expression) throws SQLParserException;

	public abstract CommonTree parseSQLForDerivedSuperLexical(String expression) throws SQLParserException;

	public abstract CommonTree parseSQLForValueExpressionOrStar(String expression) throws SQLParserException;

	public abstract CommonTree parseSQLForLiteral(String expression) throws SQLParserException;

	public abstract CommonTree parseSQLForCondition(String expression) throws SQLParserException;

}