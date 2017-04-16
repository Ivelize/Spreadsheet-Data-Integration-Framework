package uk.ac.manchester.dstoolkit.service.impl.query.querytranslator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.Predicate;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.EvaluateExternallyOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.JoinOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.ReduceOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.ScanOperatorImpl;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators.EvaluatorOperator;
import uk.ac.manchester.dstoolkit.service.query.querytranslator.LocalQueryTranslator2SQLService;

//TODO change as much as possible to interfaces instead of classes, in all classes

//@Transactional(readOnly = true)
@Service(value = "localQueryTranslator2SQLService")
public class LocalQueryTranslator2SQLServiceImpl implements LocalQueryTranslator2SQLService {

	private static Logger logger = Logger.getLogger(LocalQueryTranslator2SQLServiceImpl.class);

	private List<String> selectList = new ArrayList<String>();
	private Map<String, String> fromMap = new HashMap<String, String>();
	private List<String> whereList = new ArrayList<String>();

	protected String removeUnionFromString(String stringWithUnion) {
		logger.debug("in removeUnionFromString");
		logger.debug("stringWithUnion: " + stringWithUnion);
		StringBuffer stringWithoutUnion = new StringBuffer();
		Pattern p = Pattern.compile("union\\d+\\.");
		Matcher m = p.matcher(stringWithUnion);
		boolean result = m.find();
		while (result) {
			m.appendReplacement(stringWithoutUnion, "");
			result = m.find();
		}
		// Add the last segment of input to 
		// the new String
		m.appendTail(stringWithoutUnion);
		logger.debug("stringWithoutUnion: " + stringWithoutUnion);
		return stringWithoutUnion.toString();
	}

	public String translate2SQL(EvaluateExternallyOperatorImpl evaluateExternallyOperator, Map<String, ControlParameter> controlParameters) {
		//TODO differentiate between EvaluateExternally and parameterisedEvaluateExternally

		selectList = new ArrayList<String>();
		fromMap = new HashMap<String, String>();
		whereList = new ArrayList<String>();

		logger.debug("in LocalTranslator2SQLImpl, translate");
		logger.debug("evaluateExternallyOperator: " + evaluateExternallyOperator);
		logger.debug("controlParameters: " + controlParameters);
		//controlParameters ignored here, are used in LocalTranslator2XQuery ... see there for more information
		translateOperator(evaluateExternallyOperator.getPlanRootEvaluatorOperator());
		String queryString = toString();
		evaluateExternallyOperator.setQueryString(queryString);
		return queryString;
	}

	/*
	public String translateOperator(MappingOperator plan, List<Predicate> predicates, String predicateValue) {
		logger.debug("in LocalTranslator2SQLImpl, translate");
		this.plan = plan;
		logger.debug("plan: " + this.plan);
		translateOperator(plan);
		addToWhereList(predicates);
		return toString();
	}
	*/

	private void translateOperator(EvaluatorOperator operator) {
		logger.debug("in translateOperator, operator: " + operator);
		//int numInputs = operator.getNumberOfInputs();
		//for(int i=0;i<numInputs;i++)
		//	translateOperator(operator.getInput(i)); 
		if (operator instanceof ScanOperatorImpl) {
			logger.debug("operator is ScanOperator");
			ScanOperatorImpl scanOperator = (ScanOperatorImpl) operator;
			SuperAbstract superAbstract = scanOperator.getSuperAbstract();
			logger.debug("superAbstract: " + superAbstract);
			addToFromMap(superAbstract, scanOperator.getVariableName());
			String reconcilingExpression = scanOperator.getReconcilingExpression();
			String andOr = scanOperator.getAndOr();
			logger.debug("reconcilingExpression: " + reconcilingExpression);
			Set<Predicate> predicates = scanOperator.getPredicates();
			logger.debug("predicates: " + predicates);
			if (predicates != null && !predicates.isEmpty()) {
				if (!reconcilingExpression.contains("union")) {
					logger.debug("no union mentioned in reconcilingExpression");
					addToWhereList(reconcilingExpression, andOr);
				} else {
					logger.debug("union found in reconcilingExpression, remove all mentions of union");
					logger.debug("reconcilingExpression: " + reconcilingExpression);
					String reconcilingExpressionWithoutUnion = removeUnionFromString(reconcilingExpression);
					logger.debug("reconcilingExpressionWithoutUnion: " + reconcilingExpressionWithoutUnion);
					addToWhereList(reconcilingExpressionWithoutUnion, andOr);
				}
			}
		} else if (operator instanceof ReduceOperatorImpl) {
			logger.debug("operator is ReduceOperator");
			ReduceOperatorImpl reduceOperator = (ReduceOperatorImpl) operator;
			String projectList = reduceOperator.getReconcilingExpression();
			logger.debug("projectList, reconcilingExpression of ReduceOperator: " + projectList);
			Map<String, SuperLexical> superLexicals = reduceOperator.getSuperLexicals();
			if (!projectList.contains("union")) {
				logger.debug("no union mentioned in projectList, add it to selectList");
				addToSelectList(projectList, superLexicals);
			} else {
				logger.debug("union found in projectList, remove all mentions of union");
				logger.debug("projectList: " + projectList);
				String projectListWithoutUnion = removeUnionFromString(projectList);
				logger.debug("projectListWithoutUnion: " + projectListWithoutUnion);
				addToSelectList(projectListWithoutUnion, superLexicals);
			}
			translateOperator(reduceOperator.getInput());
		} else if (operator instanceof JoinOperatorImpl) {
			logger.debug("operator is JoinOperator");
			JoinOperatorImpl joinOperator = (JoinOperatorImpl) operator;
			
			String reconcilingExpression = joinOperator.getReconcilingExpression();
			String andOr = joinOperator.getAndOr();
			logger.debug("reconcilingExpression: " + reconcilingExpression);
			Set<Predicate> joinPredicates = joinOperator.getPredicates();
			logger.debug("joinPredicates: " + joinPredicates);
			
			if (joinPredicates != null && !joinPredicates.isEmpty())
				if (!reconcilingExpression.contains("union")) {
					logger.debug("no union mentioned in reconcilingExpression");
					addToWhereList(reconcilingExpression, andOr);
				} else {
					logger.debug("union found in reconcilingExpression, remove all mentions of union");
					logger.debug("reconcilingExpression: " + reconcilingExpression);
					String reconcilingExpressionWithoutUnion = removeUnionFromString(reconcilingExpression);
					logger.debug("reconcilingExpressionWithoutUnion: " + reconcilingExpressionWithoutUnion);
					addToWhereList(reconcilingExpressionWithoutUnion, andOr);
				}
			/*
			t_left_collection = Translate2SQL(left_collection, q);
			t_right_collection = Translate2SQL(right_collection, t_left_collection);
			for each predicate in predicate_list do
				for each lexical in predicate
					column = map2modelConstruct(lexical);
					replace lexical with column;
				t_right_collection.addWhere(predicate);
				return t_right_collection;
			*/
			//TODO check this works properly
			translateOperator(joinOperator.getLhsInput());
			translateOperator(joinOperator.getRhsInput());
		}
		//TODO add setOperators
	}

	private void addToSelectList(String projectList, Map<String, SuperLexical> superLexicals) {
		logger.debug("in addToSelectList");
		logger.debug("projectList: " + projectList);
		logger.debug("selectList: " + selectList);
		if (projectList.contains(".")) {
			logger.debug("projectList contains .");
			//logger.debug("superLexicals: " + superLexicals);
			//logger.debug("check order - is different!");
			//TODO this is a hack as it had the wrong variablename in case of joins, removing variable names here
			StringBuilder newProjectList = new StringBuilder();
			String[] result = projectList.split(",");
			for (int x = 0; x < result.length; x++) {
				String nextElement = result[x];
				logger.debug("nextElement: " + nextElement);
				if (nextElement.contains(".")) {
					nextElement = nextElement.substring(nextElement.indexOf(".") + 1);
					logger.debug("nextElement");
				}
				if (x > 0)
					newProjectList.append(", ");
				newProjectList.append(nextElement);
			}

			/*
			for (String superLexical : superLexicals.keySet()) {
				logger.debug("superLexical: " + superLexical);
				if (index > 0)
					newProjectList.append(", ");
				if (superLexical.contains(".")) {
					superLexical = superLexical.substring(superLexical.indexOf(".") + 1);
					logger.debug("superLexical: " + superLexical);
				}
				newProjectList.append(superLexical);
				index++;
			}
			*/
			logger.debug("newProjectList: " + newProjectList.toString());
			projectList = newProjectList.toString();
		}
		logger.debug("projectList: " + projectList);
		selectList.add(projectList);
		logger.debug("selectList: " + selectList);
		logger.debug("superLexicals: " + superLexicals);
	}

	private void addToFromMap(SuperAbstract superAbstract, String varName) {
		logger.debug("in addToFromMap");
		logger.debug("superAbstract: " + superAbstract);
		logger.debug("variableName: " + varName);
		if (varName == null)
			varName = superAbstract.getName();
		String superAbstractName = superAbstract.getName();
		logger.debug("superAbstractName: " + superAbstractName);
		String schemaName = superAbstract.getSchema().getName();
		logger.debug("schemaName: " + schemaName);
		if (!varName.contains("union")) {
			logger.debug("variableName doesn't contain union");
			fromMap.put(varName, superAbstractName);
			logger.debug("added to FromMap: variableName: " + varName + " superAbstractName: " + superAbstractName);
		} else {
			logger.debug("variableName contains union, remove all mentions of union");
			String varNameWithoutUnion = this.removeUnionFromString(varName);
			logger.debug("varNameWithoutUnion: " + varNameWithoutUnion);
			fromMap.put(varNameWithoutUnion, superAbstractName);
			logger.debug("added to FromMap: varNameWithoutUnion: " + varNameWithoutUnion + " superAbstractName: " + superAbstractName);
		}

	}

	private void addToWhereList(String reconcilingExpression, String andOr) {
		logger.debug("in addToWhereList");
		logger.debug("reconcilingExpression: " + reconcilingExpression);
		logger.debug("andOr: " + andOr);
		if (whereList.isEmpty())
			whereList.add(reconcilingExpression);
		else {
			if (andOr != null) {
				String expression = andOr + " " + reconcilingExpression;
				whereList.add(expression);
			} else {
				String expression = "and " + reconcilingExpression;
				whereList.add(expression);
			}
		}
		logger.debug("whereList: " + whereList);
	}

	@Override
	public String toString() {
		logger.debug("in toString");
		StringBuffer selectString = new StringBuffer("SELECT");
		logger.debug("selectString: " + selectString);
		if (selectList.size() == 0)
			selectString.append(" *");
		logger.debug("selectList: " + selectList);

		for (String selectEntry : selectList) {
			logger.debug("selectEntry: " + selectEntry);
			if (selectList.indexOf(selectEntry) > 0)
				selectString.append(",");
			selectString.append(" ");
			/*
			if (selectEntry.contains(".")) {
				String temp = selectEntry.substring(selectEntry.indexOf(".") + 1);
				logger.debug(temp);
				if (temp.contains("."))
					selectEntry = temp;
			}
			*/

			selectString.append(selectEntry);
			logger.debug("selectString: " + selectString);
		}

		logger.debug("selectString: " + selectString);
		StringBuffer fromString = new StringBuffer("FROM");
		logger.debug("fromString: " + fromString);
		Set<String> fromVariableNames = fromMap.keySet();
		int entryNo = 0;
		for (String fromEntryVariableName : fromVariableNames) {
			entryNo++;
			if (entryNo > 1)
				fromString.append(",");
			String fromEntrySuperAbstractName = fromMap.get(fromEntryVariableName);
			logger.debug("fromEntrySuperAbstractName: " + fromEntrySuperAbstractName);
			logger.debug("fromEntryVariableName: " + fromEntryVariableName);
			/*
			if (fromEntrySuperAbstractName.contains(".")) {
				fromEntrySuperAbstractName = fromEntrySuperAbstractName.substring(fromEntrySuperAbstractName.indexOf(".") + 1);
				logger.debug("fromEntrySuperAbstractName: " + fromEntrySuperAbstractName);
			}
			*/
			fromString.append(" ");
			fromString.append(fromEntrySuperAbstractName);
			if (!fromEntryVariableName.equals(fromEntrySuperAbstractName)) {
				fromString.append(" ");
				fromString.append(fromEntryVariableName);
			}
			/*
			fromString.append(" ");
			fromString.append(fromEntryVariableName);
			*/
			logger.debug("fromString: " + fromString);
		}
		logger.debug("fromString: " + fromString);

		String queryString = selectString.toString() + " " + fromString.toString();

		if (!whereList.isEmpty()) {
			StringBuffer whereString = new StringBuffer("WHERE");
			logger.debug("whereString: " + whereString);
			for (String whereEntry : whereList) {
				whereString.append(" ");
				whereString.append(whereEntry);
				logger.debug("whereString: " + whereString);
			}
			logger.debug("whereString: " + whereString);
			queryString = queryString + " " + whereString.toString();
		}
		//queryString = queryString + ";"; //Oracle doesn't seem to like it

		//TODO change queryString to StringBuilder
		logger.debug("queryString: " + queryString);
		return queryString;

	}
}
