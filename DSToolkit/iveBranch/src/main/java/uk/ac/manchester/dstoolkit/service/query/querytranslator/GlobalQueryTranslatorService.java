package uk.ac.manchester.dstoolkit.service.query.querytranslator;

import java.util.Set;

import org.antlr.runtime.tree.CommonTree;

import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.MappingOperator;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.repository.canonical.SuperAbstractRepository;
import uk.ac.manchester.dstoolkit.repository.canonical.SuperLexicalRepository;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;

public interface GlobalQueryTranslatorService {

	/**
	 * @param queryString
	 * @param ast
	 * @return query
	 */
	public abstract Query translateAstIntoQuery(Query query, String queryString, CommonTree ast);

	/**
	 * @param queryString
	 * @param ast
	 * @param schema
	 * @return query
	 */
	public abstract Query translateAstIntoQuery(Query query, String queryString, CommonTree ast, Schema schema);

	/**
	 * @param queryString
	 * @param ast
	 * @param schemas
	 * @return query
	 */
	public abstract Query translateAstIntoQuery(Query query, String queryString, CommonTree ast, Set<Schema> schemas);

	/**
	 * @param ast
	 * @return mappingOperator
	 */
	public abstract MappingOperator translateQuery(CommonTree ast);

	/**
	 * @param schemaRepository
	 */
	public abstract void setSchemaRepository(SchemaRepository schemaRepository);

	/**
	 * @param superAbstractRepository
	 */
	public abstract void setSuperAbstractRepository(SuperAbstractRepository superAbstractRepository);

	/**
	 * @param superLexicalRepository
	 */
	public abstract void setSuperLexicalRepository(SuperLexicalRepository superLexicalRepository);

	public abstract void setSchemaNames(Set<String> schemaNames);

	/**
	 * @param resultVarName
	 * @param boundVar
	 * @return joinOperators
	 */
	//public abstract Set<JoinOperator> getApplicableJoinOperators(String resultVarName, Set<String> boundVar);
}