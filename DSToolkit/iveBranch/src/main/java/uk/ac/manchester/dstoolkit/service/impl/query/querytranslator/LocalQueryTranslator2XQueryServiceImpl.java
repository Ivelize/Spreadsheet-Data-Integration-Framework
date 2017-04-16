package uk.ac.manchester.dstoolkit.service.impl.query.querytranslator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.ParticipationOfCMCInSuperRelationship;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexicalModelSpecificType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationship;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationshipRoleType;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.Predicate;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.EvaluateExternallyOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.JoinOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.ReduceOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.ScanOperatorImpl;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators.EvaluatorOperator;
import uk.ac.manchester.dstoolkit.service.query.querytranslator.LocalQueryTranslator2XQueryService;

/**
 * @author chedeler
 *
 *   
 * Revision (klitos):
 *  1. Rewrote splitUpReconcilingExpression()
 */


//@Transactional(readOnly = true)
@Service(value = "localQueryTranslator2XQueryService")
public class LocalQueryTranslator2XQueryServiceImpl implements LocalQueryTranslator2XQueryService {

	private static Logger logger = Logger.getLogger(LocalQueryTranslator2XQueryServiceImpl.class);

	private Map<String, ControlParameter> controlParameters = null;

	private Map<String, String> letMap;
	private Map<String, SuperAbstract> forMap;
	private List<WhereInformation> whereList;
	private Map<String, SuperLexical> returnMap;

	//same code as in 2SQLServiceImpl
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

	public String translate2XQuery(EvaluateExternallyOperatorImpl evaluateExternallyOperator, Map<String, ControlParameter> controlParameters) {
		//TODO differentiate between EvaluateExternally and parameterisedEvaluateExternally

		letMap = new HashMap<String, String>();
		forMap = new HashMap<String, SuperAbstract>();
		whereList = new ArrayList<WhereInformation>();
		returnMap = new LinkedHashMap<String, SuperLexical>();
		this.controlParameters = controlParameters;

		logger.debug("in LocalTranslator2XQueryImpl, translate");
		logger.debug("evaluateExternallyOperator: " + evaluateExternallyOperator);
		logger.debug("controlParameters: " + controlParameters);
		//only MaxNumberOfResults used
		//TODO sort out fetchSize

		String sourceName = evaluateExternallyOperator.getDataSource().getSchema().getName();
		String sourceUrl = evaluateExternallyOperator.getDataSource().getConnectionURL();

		logger.debug("sourceName: " + sourceName);
		logger.debug("sourceUrl: " + sourceUrl);

		letMap.put(sourceName, sourceUrl);

		translateOperator(evaluateExternallyOperator.getPlanRootEvaluatorOperator());
		String queryString = toString();
		evaluateExternallyOperator.setQueryString(queryString);
		return queryString;
	}

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
			addToForMap(superAbstract, scanOperator.getVariableName());
			String reconcilingExpression = scanOperator.getReconcilingExpression();
			String andOr = scanOperator.getAndOr();
			logger.debug("reconcilingExpression: " + reconcilingExpression);
			Set<Predicate> predicates = scanOperator.getPredicates();
			logger.debug("predicates: " + predicates);
			if (predicates != null && !predicates.isEmpty()) {
				if (!reconcilingExpression.contains("union")) {
					logger.debug("no union mentioned in reconcilingExpression");
					addToWhereList(predicates, reconcilingExpression, andOr);
				} else {
					logger.debug("union found in reconcilingExpression, remove all mentions of union");
					logger.debug("reconcilingExpression: " + reconcilingExpression);
					String reconcilingExpressionWithoutUnion = removeUnionFromString(reconcilingExpression);
					logger.debug("reconcilingExpressionWithoutUnion: " + reconcilingExpressionWithoutUnion);
					addToWhereList(predicates, reconcilingExpressionWithoutUnion, andOr);
				}
			}
		} else if (operator instanceof ReduceOperatorImpl) {
			logger.debug("operator is ReduceOperator");
			ReduceOperatorImpl reduceOperator = (ReduceOperatorImpl) operator;
			String projectList = reduceOperator.getReconcilingExpression();
			Map<String, SuperLexical> superLexicals = reduceOperator.getSuperLexicals();
			if (!projectList.contains("union")) {
				logger.debug("no union mentioned in projectList, add it to selectList");
				addToReturnList(projectList, superLexicals);
			} else {
				logger.debug("union found in projectList, remove all mentions of union");
				logger.debug("projectList: " + projectList);
				String projectListWithoutUnion = removeUnionFromString(projectList);
				logger.debug("projectListWithoutUnion: " + projectListWithoutUnion);
				addToReturnList(projectListWithoutUnion, superLexicals);
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
					addToWhereList(joinPredicates, reconcilingExpression, andOr);
				} else {
					logger.debug("union found in reconcilingExpression, remove all mentions of union");
					logger.debug("reconcilingExpression: " + reconcilingExpression);
					String reconcilingExpressionWithoutUnion = removeUnionFromString(reconcilingExpression);
					logger.debug("reconcilingExpressionWithoutUnion: " + reconcilingExpressionWithoutUnion);
					addToWhereList(joinPredicates, reconcilingExpressionWithoutUnion, andOr);
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

	private void addToReturnList(String projectList, Map<String, SuperLexical> superLexicals) {
		logger.debug("in addToRetutnList");
		logger.debug("projectList: " + projectList);
		logger.debug("returnMap: " + returnMap);
		returnMap.putAll(superLexicals);
		logger.debug("returnMap: " + returnMap);
		logger.debug("superLexicals: " + superLexicals);
	}

	private void addToForMap(SuperAbstract superAbstract, String varName) {
		logger.debug("in addToForMap");
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
			forMap.put(varName, superAbstract);
			logger.debug("added to FromMap: variableName: " + varName + " superAbstractName: " + superAbstractName);
		} else {
			logger.debug("variableName contains union, remove all mentions of union");
			String varNameWithoutUnion = this.removeUnionFromString(varName);
			logger.debug("varNameWithoutUnion: " + varNameWithoutUnion);
			forMap.put(varNameWithoutUnion, superAbstract);
			logger.debug("added to FromMap: varNameWithoutUnion: " + varNameWithoutUnion + " superAbstractName: " + superAbstractName);
		}

	}

	//here1
	private void addToWhereList(Set<Predicate> predicates, String reconcilingExpression, String andOr) {
		logger.debug("in addToWhereList");
		logger.debug("reconcilingExpression: " + reconcilingExpression);
		logger.debug("andOr: " + andOr);
		for (Predicate predicate : predicates) {
			logger.debug("predicate: " + predicate);
		}
		//Map<List<Predicate>, Map<String, String>> whereMap

		whereList.add(new WhereInformation(reconcilingExpression, andOr, predicates));
		logger.debug("whereList: " + whereList);
	}

	@Override
	public String toString() {
		logger.debug("in toString");

		int maxNumberOfResults = -1;
		int fetchSize = -1;

		if (controlParameters != null) {
			if (controlParameters.containsKey("maxNumberOfResults")) {
				maxNumberOfResults = Integer.getInteger(controlParameters.get("maxNumberOfResults").getValue());
				logger.debug("maxNumberOfResults: " + maxNumberOfResults);
			}
			if (controlParameters.containsKey("fetchSize")) {
				fetchSize = Integer.getInteger(controlParameters.get("fetchSize").getValue());
				logger.debug("fetchSize: " + fetchSize);
			}
		}

		StringBuffer letString = new StringBuffer("let ");
		logger.debug("letString: " + letString);
		Set<String> letEntryVariableNames = letMap.keySet();
		logger.debug("letEntryVariableNames(): " + letEntryVariableNames.size());
		if (letEntryVariableNames.size() > 1)
			logger.error("more than one source - shouldn't actually happen - TODO check this");
		int entryNo = 0;
		for (String sourceName : letEntryVariableNames) {
			logger.debug("sourceName: " + sourceName);
			if (entryNo > 0) {
				logger.debug("not first entry to let list, add new let");
				letString.append("let ");
			}
			letString.append("$").append(sourceName);
			letString.append(" := ");
			letString.append("doc(\"");
			letString.append(letMap.get(sourceName));
			letString.append("\")");
			logger.debug("letString: " + letString);
			entryNo++;
		}

		//TODO sort out fetchSize
		//TODO the subsequence stuff isn't going to work for multiple for - sort this!!!

		if (maxNumberOfResults > 0) {
			logger.debug("maxNumberOfResults specified: " + maxNumberOfResults);
			letString.append("let $subseq0toMaxNumberOfResults := subsequence(").append("\n");
		}

		/* 
		query with subsequence:
		
		<result>{
			let $MondialEuropeXML := doc("xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml")
			let $result := subsequence(
				for $city in $MondialEuropeXML//city
				where $city/@country='GB' 
				return <tuple>
					<city.name> {fn:data($res/name)} </city.name>
					<city.longitude> {fn:data($res/longitude)} </city.longitude>
					<city.latitude> {fn:data($res/latitude)} </city.latitude>
					<city.population> {fn:data($res/population)} </city.population>
					<city.province> {fn:data($res/@province)} </city.province>
					<city.country> {fn:data($res/@country)} </city.country>
					<city.id> {fn:data($res/@id)} </city.id>
					<city.is_state_cap> {fn:data($res/@is_state_cap)} </city.is_state_cap>
					<city.is_country_cap> {fn:data($res/@is_country_cap)} </city.is_country_cap>
				</tuple>, 1, 10)
				for $res in $result
				return $res
			}
		</result>
		
		
		<result>{
			let $MondialEuropeXML := doc("xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml")
			let $result := subsequence(
				for $country in $MondialEuropeXML//country
				for $city in $MondialEuropeXML//city
				where $city/@country=$country/@car_code
				return <tuple>
					<city.name> {fn:data($city/name)} </city.name>
					<city.longitude> {fn:data($city/longitude)} </city.longitude>
					<city.latitude> {fn:data($city/latitude)} </city.latitude>
					<city.population> {fn:data($city/population)} </city.population>
					<city.province> {fn:data($city/@province)} </city.province>
					<city.country> {fn:data($city/@country)} </city.country>
					<city.id> {fn:data($city/@id)} </city.id>
					<city.is_state_cap> {fn:data($city/@is_state_cap)} </city.is_state_cap>
					<city.is_country_cap> {fn:data($city/@is_country_cap)} </city.is_country_cap>
					<country.name> {fn:data($country/name)} </country.name>
					<country.area> {fn:data($country/@area)} </country.area>
					<country.memberships> {fn:data($country/@memberships)} </country.memberships>
					<country.capital> {fn:data($country/@capital)} </country.capital>
					<country.car_code> {fn:data($country/@car_code)} </country.car_code>
					<country.population> {fn:data($country/population)} </country.population>
					<country.population_growth> {fn:data($country/population_growth)} </country.population_growth>
					<country.infant_mortality> {fn:data($country/infant_mortality)} </country.infant_mortality>
					<country.gdp_total> {fn:data($country/gdp_total)} </country.gdp_total>
					<country.gdp_agri> {fn:data($country/gdp_agri)} </country.gdp_agri>
					<country.gdp_ind> {fn:data($country/gdp_ind)} </country.gdp_ind>
					<country.gdp_serv> {fn:data($country/gdp_serv)} </country.gdp_serv>
					<country.inflation> {fn:data($country/inflation)} </country.inflation>
					<country.indep_date> {fn:data($country/indep_date)} </country.indep_date>
					<country.government> {fn:data($country/government)} </country.government>
					<country.ethnicgroups> {fn:data($country/ethnicgroups)} </country.ethnicgroups>
					<country.religions> {fn:data($country/religions)} </country.religions>
					<country.languages> {fn:data($country/languages)} </country.languages>
			</tuple>, 1, 10)
			for $res in $result
			return $res
		} 
		</result>
		*/

		StringBuilder forString = new StringBuilder("");
		logger.debug("forString: " + forString);

		Set<String> forVariableNames = forMap.keySet();
		entryNo = 0;
		for (String forEntryVariableName : forVariableNames) {
			entryNo++;
			SuperAbstract forEntrySuperAbstract = forMap.get(forEntryVariableName);
			logger.debug("forEntrySuperAbstract: " + forEntrySuperAbstract);
			logger.debug("forEntryVariableName: " + forEntryVariableName);
			//not sure whether it makes sense to check whether it's global or local ... if it's local and there are multiple occurrences, won't know which one and
			//would still need to use //
			/*
			if (fromEntrySuperAbstractName.contains(".")) {
				fromEntrySuperAbstractName = fromEntrySuperAbstractName.substring(fromEntrySuperAbstractName.indexOf(".") + 1);
				logger.debug("fromEntrySuperAbstractName: " + fromEntrySuperAbstractName);
			}
			*/
			if (forEntrySuperAbstract.isGlobal()) {
				logger.debug("global superAbstract");
				forString.append("for ");
				forString.append("$").append(forEntryVariableName).append(" in ");
				forString.append("$").append(forEntrySuperAbstract.getSchema().getName()).append("//").append(forEntrySuperAbstract.getName())
						.append("\n");
			} else {
				logger.debug("local superAbstract, get its parent");
				Set<ParticipationOfCMCInSuperRelationship> participations = forEntrySuperAbstract.getParticipationInSuperRelationships();
				for (ParticipationOfCMCInSuperRelationship participation : participations) {
					if (participation.getRole().equals(SuperRelationshipRoleType.CHILD)) {
						SuperRelationship sr = participation.getSuperRelationship();
						logger.debug("sr: " + sr);
						Set<ParticipationOfCMCInSuperRelationship> parts = sr.getParticipationsOfConstructs();
						for (ParticipationOfCMCInSuperRelationship part : parts) {
							if (part.getRole().equals(SuperRelationshipRoleType.PARENT)) {
								logger.debug("found parent");
								CanonicalModelConstruct parentConstruct = part.getCanonicalModelConstruct();
								logger.debug("parent construct: " + parentConstruct);
								forString.append("$").append(forEntryVariableName).append(" in ");
								forString.append("$").append(forEntrySuperAbstract.getSchema().getName());
								forString.append("//").append(parentConstruct.getName()).append("/").append(forEntrySuperAbstract.getName())
										.append("\n");
							}
						}
					}
				}
			}
			/*
			fromString.append(" ");
			fromString.append(fromEntryVariableName);
			*/
			logger.debug("forString: " + forString);
		}//end for
		
		logger.debug("forString: " + forString);

		StringBuilder whereString = new StringBuilder("");

		logger.debug("whereList.size(): " + whereList.size());
		if (!whereList.isEmpty()) {
			whereString.append("where");
			logger.debug("whereString: " + whereString);
			int noWhereEntries = 0;
			for (WhereInformation whereEntry : whereList) {
				logger.debug("whereEntry: " + whereEntry);
				logger.debug("whereEntry.getAndOr(): " + whereEntry.getAndOr());
				logger.debug("whereEntry.getReconcilingExpression(): " + whereEntry.getReconcilingExpression());
				logger.debug("noWhereEntries: " + noWhereEntries);
				whereString.append(" ");
				if (noWhereEntries > 0) {
					logger.debug("not first whereEntry");
					whereString.append(whereEntry.getAndOr()).append(" ");
				}
				logger.debug("whereString: " + whereString);
				Set<Predicate> predicates = whereEntry.getPredicates();
				logger.debug("predicates.size(): " + predicates.size());
				String reconcilingExpression = whereEntry.getReconcilingExpression();
				logger.debug("reconcilingExpression: " + reconcilingExpression);
				List<String> reconcilingExpressionList = new ArrayList<String>();
				if (predicates.size() == 1)
					reconcilingExpressionList.add(reconcilingExpression);
				else if (predicates.size() > 1) {
					logger.debug("more than one predicate, split up reconcilingExpression");
					reconcilingExpressionList.addAll(this.splitUpReconcilingExpression(reconcilingExpression));
				}
				//TODO might have to add transient variable name to superLexicals

				int noPredicates = 0;
				for (Predicate predicate : predicates) {
					logger.debug("predicate: " + predicate);
					logger.debug("predicate.getAndOr(): " + predicate.getAndOr());
					logger.debug("noPredicates: " + noPredicates);
					if (noPredicates > 0) {
						logger.debug("not first predicate");
						whereString.append(" ").append(predicate.getAndOr()).append(" ");
					}
					//TODO compare andOr of whereEntry and first predicate
					logger.debug("whereString: " + whereString);
					if (predicate.getLiteral1() != null) {
						logger.debug("got first literal");
						whereString.append(predicate.getLiteral1());
						logger.debug("whereString: " + whereString);
					} else if (predicate.getSuperLexical1() != null) {
						logger.debug("got first superLexical");
						SuperLexical sl1 = predicate.getSuperLexical1();
						logger.debug("sl1: " + sl1);
						String parentName = null;
						if (reconcilingExpression.contains(sl1.getName())) {
							logger.debug("reconcilingExpression: " + reconcilingExpression);
							logger.debug("sl1.getName(): " + sl1.getName());
							logger.debug("found name of sl1 in reconcilingExpression");
							//TODO this doesn't really work yet if there are multiple predicates
							//assume parentName is before sl1.name and sl1 is first one listed in reconcilingExpression, could be multiple predicates in expression though
							String subStringBeforeSl1Name = reconcilingExpression.substring(0, reconcilingExpression.indexOf(sl1.getName()));
							logger.debug("subStringBeforeSl1Name: " + subStringBeforeSl1Name);
							if (subStringBeforeSl1Name.length() > 1 && subStringBeforeSl1Name.contains(".")) {
								parentName = subStringBeforeSl1Name.substring(0, subStringBeforeSl1Name.indexOf("."));
								logger.debug("parentName: " + parentName);
							}
						}
						//TODO get parent or nested parent, this assumes that the parent is listed as part of the query
						SuperAbstract sa1 = null;
						if (sl1.getParentSuperAbstract() != null) {
							logger.debug("got parentSuperAbstract");
							sa1 = sl1.getParentSuperAbstract();
							logger.debug("sa1: " + sa1);
							if (parentName != null && !parentName.equals(sa1.getName()) && !parentName.equals(sa1.getVariableName())) {
								logger.error("wrong parent - check nesting");
								sa1 = null;
							}
						}
						if (sa1 == null) {
							logger.debug("no parentSuperAbstract, check nesting");
							if (parentName != null)
								sa1 = (SuperAbstract) this.getSurroundingParentConstructWithNameContainedInForMap(sl1, parentName);
							else
								sa1 = (SuperAbstract) this.getSurroundingParentConstructContainedInForMap(sl1);
							logger.debug("sa1: " + sa1);
						}
						if (sa1 != null) {
							logger.debug("got sa1: " + sa1);
							if (this.forMap.containsValue(sa1)) {
								logger.debug("found sa1 in forMap");
								String variableName = this.getVariableNameForSuperAbstractFromForMap(sa1);
								whereString.append("$").append(variableName).append("/");
								if (sl1.getModelSpecificType().equals(SuperLexicalModelSpecificType.XSD_ATTRIBUTE))
									whereString.append("@");
								whereString.append(sl1.getName());
								logger.debug("whereString: " + whereString);
							} else {
								//TODO didn't find sa1 in forMap - sort this
								logger.error("didn't find sa1 in forMap - TODO sort this");
							}
						} else {
							//TODO no parentSuperAbstract for superLexical1 in predicate - check this
							logger.error("didn't find parent for superLexical - TODO check this");
						}
					}
					
					//String representation here
					logger.debug("whereString: " + whereString);
					whereString.append(predicate.getOperator());
					logger.debug("whereString: " + whereString);
					if (predicate.getLiteral2() != null) {
						logger.debug("got second literal");
						whereString.append(predicate.getLiteral2());
						logger.debug("whereString: " + whereString);
					} else if (predicate.getSuperLexical2() != null) {
						logger.debug("got second superLexical");
						SuperLexical sl2 = predicate.getSuperLexical2();
						logger.debug("sl2: " + sl2);
						String parentName = null;
						if (reconcilingExpression.contains(sl2.getName())) {
							logger.debug("reconcilingExpression: " + reconcilingExpression);
							logger.debug("sl2.getName(): " + sl2.getName());
							logger.debug("found name of sl2 in reconcilingExpression");
							logger.debug("predicate.getOperator(): " + predicate.getOperator());
							//TODO this doesn't really work yet if there are multiple predicates
							//assume parentName is before sl2.name and sl2 is last one listed in reconcilingExpression
							String subStringBeforeSl2Name = reconcilingExpression.substring(
									reconcilingExpression.lastIndexOf(predicate.getOperator()) + 1, reconcilingExpression.lastIndexOf(sl2.getName()));
							logger.debug("subStringBeforeSl2Name: " + subStringBeforeSl2Name);
							if (subStringBeforeSl2Name.length() > 1 && subStringBeforeSl2Name.contains(".")) {
								parentName = subStringBeforeSl2Name.substring(0, subStringBeforeSl2Name.lastIndexOf(".")).trim();
								logger.debug("parentName: " + parentName);
							}
						}
						//TODO get parent or nested parent, this assumes that the parent is listed as part of the query
						SuperAbstract sa2 = null;

						if (sl2.getParentSuperAbstract() != null) {
							logger.debug("got parentSuperAbstract");
							sa2 = sl2.getParentSuperAbstract();
							logger.debug("sa2: " + sa2);
							logger.debug("parentName: " + parentName);
							logger.debug("sa2.getName: " + sa2.getName());
							if (parentName != null && !parentName.equals(sa2.getName()) && !parentName.equals(sa2.getVariableName())) {
								logger.error("wrong parent - check nesting");
								sa2 = null;
							}
						}
						if (sa2 == null) {
							logger.debug("no parentSuperAbstract, check nesting");
							if (parentName != null)
								sa2 = (SuperAbstract) this.getSurroundingParentConstructWithNameContainedInForMap(sl2, parentName);
							else
								sa2 = (SuperAbstract) this.getSurroundingParentConstructContainedInForMap(sl2);
							logger.debug("sa2: " + sa2);
						}
						if (sa2 != null) {
							logger.debug("got sa2: " + sa2);
							if (this.forMap.containsValue(sa2)) {
								logger.debug("found sa2 in forMap");
								String variableName = this.getVariableNameForSuperAbstractFromForMap(sa2);
								whereString.append("$").append(variableName).append("/");
								if (sl2.getModelSpecificType().equals(SuperLexicalModelSpecificType.XSD_ATTRIBUTE))
									whereString.append("@");
								whereString.append(sl2.getName());
								logger.debug("whereString: " + whereString);
							} else {
								//TODO didn't find sa1 in forMap - sort this
								logger.error("didn't find sa1 in forMap - TODO sort this");
							}
						} else {
							//TODO no parentSuperAbstract for superLexical1 in predicate - check this
							logger.error("didn't find parent for superLexical - TODO check this");
						}
					}
					logger.debug("whereString: " + whereString);

					noPredicates++;
				}
				logger.debug("whereString: " + whereString);
				noWhereEntries++;
			}
			logger.debug("whereString: " + whereString);
		}

		//TODO ignored parentSuperLexicals of superLexicals

		StringBuilder returnString = new StringBuilder("return ");
		logger.debug("returnString: " + returnString);
		if (returnMap.size() == 0)
			logger.error("missing superLexicals to return - TODO sort this");
		else {
			logger.debug("returnMap: " + returnMap);
			returnString.append("<tuple>").append("\n");
			logger.debug("returnString: " + returnString);
			Set<String> returnKeySet = returnMap.keySet();
			for (String returnKey : returnKeySet) {
				String parentName = null;
				if (returnKey.contains("."))
					parentName = returnKey.substring(0, returnKey.indexOf("."));
				logger.debug("returnKey: " + returnKey);
				SuperLexical sl = returnMap.get(returnKey);
				logger.debug("sl: " + sl);
				SuperAbstract sa = null;
				if (sl.getParentSuperAbstract() != null) {
					logger.debug("got parentSuperAbstract");
					sa = sl.getParentSuperAbstract();
					logger.debug("parentName: " + parentName);
					logger.debug("sa: " + sa);
					logger.debug("sa.getName(): " + sa.getName());
					logger.debug("sa.getVariableName: " + sa.getVariableName());
					if (parentName != null && !parentName.equals(sa.getName()) && !parentName.equals(sa.getVariableName())) {
						//TODO wrong parent
						logger.error("found unexpected parent - check nesting for correct one");
						sa = null;
					}
				}
				if (sa == null) {
					logger.debug("no parentSuperAbstract found yet, check nesting");
					if (parentName != null)
						sa = (SuperAbstract) this.getSurroundingParentConstructWithNameContainedInForMap(sl, parentName);
					else
						sa = (SuperAbstract) this.getSurroundingParentConstructContainedInForMap(sl);
					logger.debug("sa: " + sa);
				}
				if (sa != null) {
					logger.debug("got sa: " + sa);
					if (this.forMap.containsValue(sa)) {
						logger.debug("found sa in forMap");
						String variableName = this.getVariableNameForSuperAbstractFromForMap(sa);
						returnString.append("<").append(variableName).append(".").append(sl.getName()).append("> {");
						returnString.append("fn:data(");
						returnString.append("$").append(variableName).append("/");
						if (sl.getModelSpecificType().equals(SuperLexicalModelSpecificType.XSD_ATTRIBUTE))
							returnString.append("@");
						returnString.append(sl.getName());
						returnString.append(")} </").append(variableName).append(".").append(sl.getName()).append(">\n");
						logger.debug("returnString: " + returnString);
					} else {
						//TODO didn't find sa1 in forMap - sort this
						logger.error("didn't find sa1 in forMap - TODO sort this");
					}
				} else {
					//TODO no parentSuperAbstract for superLexical1 in predicate - check this
					logger.error("didn't find parent for superLexical - TODO check this");
				}
			}
			//for (String returnEntry : returnMap) {
			//	logger.debug("returnEntry: " + returnEntry);
			//	if (returnMap.indexOf(returnEntry) > 0)
			//		returnString.append(",");
			//	returnString.append(" ");
			/*
			if (selectEntry.contains(".")) {
				String temp = selectEntry.substring(selectEntry.indexOf(".") + 1);
				logger.debug(temp);
				if (temp.contains("."))
					selectEntry = temp;
			}
			*/
			//returnString.append(returnEntry);
			returnString.append("</tuple>");
			logger.debug("returnString: " + returnString);
		}//end else
		logger.debug("returnString: " + returnString);

		StringBuilder queryString = new StringBuilder();
		queryString.append("<result>{").append("\n");
		queryString.append(letString).append("\n");
		queryString.append(forString);
		if (whereString != null) {
			queryString.append(whereString).append("\n");
		}
		queryString.append(returnString);

		/*
		, 1, 10)
		for $res in $result
		return $res
		*/

		logger.debug("queryString: " + queryString);
		//TODO the subsequence stuff isn't going to work for multiple for - sort this!!!
		//return $city, 1, 10)
		if (maxNumberOfResults > 0) {
			logger.debug("maxNumberOfResults specified: " + maxNumberOfResults);
			queryString.append(", 1, ").append(maxNumberOfResults).append("\n");
			queryString.append("for $subseqResult in $subseq0toMaxNumberOfResults").append("\n");
			queryString.append("return $subseqResult");
		}

		queryString.append("\n");

		//returnString.toString() + " " + forString.toString();
		//queryString = queryString + ";";

		queryString.append("} </result>");
		logger.debug("queryString: " + queryString.toString());
		return queryString.toString();

	}

	//new version
	private List<String> splitUpReconcilingExpressionNew(String reconcilingExpression) {

		List<String> reconcilingExpressions = new ArrayList<String>();

        //Fist get the array of AND

        String [] andArray = reconcilingExpression.split(" and ");

        if (andArray.length == 0)
          andArray = reconcilingExpression.split(" AND ");

        String [] orArray = reconcilingExpression.split(" or ");

        if (orArray.length == 0)
        	orArray = reconcilingExpression.split(" OR ");

        if (andArray.length == 1) {
          //no AND exists
          reconcilingExpressions.addAll(Arrays.asList(orArray));
        } else if (orArray.length == 1) {
          //no OR exists
          reconcilingExpressions.addAll(Arrays.asList(andArray));
        } else if (andArray.length >= orArray.length) {
          for (int i=0; i<andArray.length; i++) {
            reconcilingExpressions.addAll(splitUpReconcilingExpression(andArray[i]));
          }
        } else {
          for (int j=0; j<orArray.length; j++) {
            reconcilingExpressions.addAll(splitUpReconcilingExpression(orArray[j]));
          }
        }
        
        return reconcilingExpressions;
	}//end splitUpReconcilingExpression()	
	
	//Old Version 
	private List<String> splitUpReconcilingExpression(String reconcilingExpression) {
		logger.debug("in splitUpReconcilingExpression");
		logger.debug("reconcilingExpression: " + reconcilingExpression);
		List<String> reconcilingExpressions = new ArrayList<String>();
		int indexOfAnd = reconcilingExpression.indexOf("and");
		int indexOfOr = reconcilingExpression.indexOf("or");
		if ((indexOfAnd > -1 && indexOfOr == -1) || (indexOfAnd > -1 && indexOfAnd < indexOfOr)) {
			String expression = reconcilingExpression.substring(0, indexOfAnd);
			logger.debug("expression: " + expression);
			reconcilingExpressions.add(expression);
			reconcilingExpressions.addAll(splitUpReconcilingExpression(reconcilingExpression.substring(indexOfAnd + 4)));
		} else if ((indexOfAnd == -1 && indexOfOr > -1) || (indexOfOr > -1 && indexOfOr < indexOfAnd)) {
			String expression = reconcilingExpression.substring(0, indexOfOr);
			logger.debug("expression: " + expression);
			reconcilingExpressions.add(expression);
			reconcilingExpressions.addAll(splitUpReconcilingExpression(reconcilingExpression.substring(indexOfAnd + 3)));
		}
		return reconcilingExpressions;
	}

	private String getVariableNameForSuperAbstractFromForMap(SuperAbstract superAbstract) {
		Set<String> keySet = forMap.keySet();
		for (String key : keySet) {
			if (forMap.get(key).equals(superAbstract))
				return key;
		}
		return null;
	}

	private CanonicalModelConstruct getSurroundingParentConstructWithNameContainedInForMap(CanonicalModelConstruct childConstruct, String parentName) {
		logger.debug("childConstruct: " + childConstruct);
		logger.debug("parentName: " + parentName);
		Set<ParticipationOfCMCInSuperRelationship> participations = childConstruct.getParticipationInSuperRelationships();
		for (ParticipationOfCMCInSuperRelationship participation : participations) {
			if (participation.getRole().equals(SuperRelationshipRoleType.CHILD)) {
				SuperRelationship sr = participation.getSuperRelationship();
				logger.debug("sr: " + sr);
				Set<ParticipationOfCMCInSuperRelationship> parts = sr.getParticipationsOfConstructs();
				for (ParticipationOfCMCInSuperRelationship part : parts) {
					logger.debug("part.getCanonicalModelConstruct(): " + part.getCanonicalModelConstruct());
					if (part.getRole().equals(SuperRelationshipRoleType.PARENT)) {
						logger.debug("found parent");
						CanonicalModelConstruct parentConstruct = part.getCanonicalModelConstruct();
						logger.debug("parentConstruct: " + parentConstruct);
						logger.debug("((SuperAbstract) parentConstruct).getVariableName(): " + ((SuperAbstract) parentConstruct).getVariableName());
						if (forMap.containsValue(parentConstruct)) {
							//if (parentConstruct.getName().equals(parentName)) {
							//logger.debug("parent construct: " + parentConstruct);
							CanonicalModelConstruct construct = forMap.get(parentName);
							logger.debug("construct: " + construct);
							if (construct.equals(parentConstruct))
								return parentConstruct;
							//}

							//if (parentConstruct instanceof SuperAbstract) {
							//	logger.debug("parent construct is SuperAbstract: " + parentConstruct);
							//	CanonicalModelConstruct construct = forMap.get(parentName);
							//	logger.debug("construct: " + construct);
							//	if (construct.equals(parentConstruct))
							//		return parentConstruct;
							//}
						}
					}
				}
			}
		}
		return null;
	}

	private CanonicalModelConstruct getSurroundingParentConstructContainedInForMap(CanonicalModelConstruct childConstruct) {
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
						if (forMap.containsValue(parentConstruct)) {
							logger.debug("parent construct: " + parentConstruct);
							return parentConstruct;
						}
					}
				}
			}
		}
		return null;
	}

	public class WhereInformation {

		String reconcilingExpression;
		String andOr;
		Set<Predicate> predicates;

		WhereInformation(String reconcilingExpression, String andOr, Set<Predicate> predicates) {
			this.reconcilingExpression = reconcilingExpression;
			this.andOr = andOr;
			this.predicates = predicates;
		}

		public String getReconcilingExpression() {
			return this.reconcilingExpression;
		}

		public String getAndOr() {
			return this.andOr;
		}

		public Set<Predicate> getPredicates() {
			return this.predicates;
		}
	}

}
