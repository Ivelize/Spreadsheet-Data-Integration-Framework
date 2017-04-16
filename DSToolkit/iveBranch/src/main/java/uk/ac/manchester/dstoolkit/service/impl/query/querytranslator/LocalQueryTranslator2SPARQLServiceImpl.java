package uk.ac.manchester.dstoolkit.service.impl.query.querytranslator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelProperty;
import uk.ac.manchester.dstoolkit.domain.models.canonical.ParticipationOfCMCInSuperRelationship;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationship;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationshipRoleType;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.Predicate;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.EvaluateExternallyOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.JoinOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.ReduceOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.ScanOperatorImpl;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators.EvaluatorOperator;
import uk.ac.manchester.dstoolkit.service.query.querytranslator.LocalQueryTranslator2SPARQLService;

/**
 * 
 * @author Klitos Christodoulou 
 */ 

//@Transactional(readOnly = true)
@Service(value = "localQueryTranslator2SPARQLService")
public class LocalQueryTranslator2SPARQLServiceImpl implements LocalQueryTranslator2SPARQLService {

	private static Logger logger = Logger.getLogger(LocalQueryTranslator2SPARQLService.class);
	
	/*Declare Data Structure for SPARQL Query <PREFIX, SELECT, WHERE>*/
	private Map<String, String> prefixMap = new HashMap<String, String>(); // PREFIX Clause 

	private Map<String, SuperLexical> selectMap = new LinkedHashMap<String, SuperLexical>(); // SELECT Clause- generate list of variables
	
	// varName, SuperAbstract, rdfTypeURI
	private Map<String, WhereInformation> rdfTypeMap = new HashMap<String, WhereInformation>(); // WHERE Clause - generate set of triple patterns	
	//TODO: see what is reconciling expression first
	//Add boolean expressions to filterList
	private List<FilterInformation> filterList = new ArrayList<FilterInformation>();

	//Join expressions
	private List<FilterInformation> joinList = new ArrayList<FilterInformation>();
		
	private int count; //Assign a unique number to each SuperAbstract's variable
	
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
	
	public String translate2SPARQL(EvaluateExternallyOperatorImpl evaluateExternallyOperator, Map<String, ControlParameter> controlParameters) {
		prefixMap  = new HashMap<String, String>(); // PREFIX - <URI,prefix>
		selectMap = new LinkedHashMap<String, SuperLexical>(); // Select
		rdfTypeMap = new HashMap<String, WhereInformation>(); // WHERE - rdf:type triple patterns
		filterList = new ArrayList<FilterInformation>(); // FILTER - list of boolean expressions
		
		count = 0; // Associate each SuperAbstract with a number n
		
		/*Add some popular namespaces*/
		prefixMap.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf");
		prefixMap.put("http://www.w3.org/2000/01/rdf-schema#", "rdfs");
		prefixMap.put("http://www.w3.org/2002/07/owl#", "owl");		
		prefixMap.put("http://www.w3.org/2001/XMLSchema#", "xsd");	
		
		logger.debug("in RDF_SPARQL:");
		logger.debug("in LocalTranslator2SPARQLImpl, translate");
		logger.debug("evaluateExternallyOperator: " + evaluateExternallyOperator);
		logger.debug("controlParameters: " + controlParameters);
		
		/*Locate the source*/
	    //TODO: Use this information to get the model to query
		String sourceName = evaluateExternallyOperator.getDataSource().getSchema().getName();
		String sourceUrl = evaluateExternallyOperator.getDataSource().getConnectionURL();

		logger.debug("sourceName: " + sourceName);
		logger.debug("sourceUrl: " + sourceUrl);
		
		
		/*Get query execution plan and call recursive method to gather information needed for the
		translation*/
		translateOperator(evaluateExternallyOperator.getPlanRootEvaluatorOperator());
		
		/*Call toString() to do the translation based on the information gathered*/
		//TODO: edit the runQuery in EvaluateExternallyOperatorImpl
		String queryString = toString();
		//After translating set the query string using the following function call
		evaluateExternallyOperator.setQueryString(queryString);
		return queryString;
	}//end translate2SPARQL()


	/**
	 * Recursive algorithm to Parse the SMql operator tree.
	 * Purpose: Gathering information to be used later by the toString() to do the 
	 * translation to a SPARQL query.
	 * 
	 * @param operator
	 */
	private void translateOperator(EvaluatorOperator operator) {
		//SuperAbstract superAbstract = scanOperator.getSuperAbstract();
		/*Get list of properties then check if properties are "namespace"*/

		logger.debug("in SPARQL translateOperator, operator: " + operator);
		if (operator instanceof ScanOperatorImpl) {
			logger.debug("SPARQL_operator is ScanOperator");
			ScanOperatorImpl scanOperator = (ScanOperatorImpl) operator;
			SuperAbstract superAbstract = scanOperator.getSuperAbstract();
			logger.debug("superAbstract: " + superAbstract);
			
			/*To be added later to the WHERE clause of the SPARQL query*/
			addToRDFTypeMap(superAbstract, scanOperator.getVariableName());
			
			String reconcilingExpression = scanOperator.getReconcilingExpression();
			String andOr = scanOperator.getAndOr();
			logger.debug("andOR: " + andOr);
			logger.debug("reconcilingExpression: " + reconcilingExpression);
			Set<Predicate> predicates = scanOperator.getPredicates();
			logger.debug("predicates: " + predicates);
			
			//Check whether the query has a WHERE clause, translated into a FILTER clause in a SPARQL query
			if (predicates != null && !predicates.isEmpty()) {
				//this is to take care of the WHERE clause of the SMql query and convert them into FILTER expressions for SPARQL
				if (!reconcilingExpression.contains("union")) {
					logger.debug("no union mentioned in reconcilingExpression");
					addToFilterList(predicates, reconcilingExpression, andOr);
				} else {
					logger.debug("union found in reconcilingExpression, remove all mentions of union");
					logger.debug("reconcilingExpression: " + reconcilingExpression);
					String reconcilingExpressionWithoutUnion = removeUnionFromString(reconcilingExpression);
					logger.debug("reconcilingExpressionWithoutUnion: " + reconcilingExpressionWithoutUnion);
					addToFilterList(predicates, reconcilingExpressionWithoutUnion, andOr);
				}
			}
		} else if (operator instanceof ReduceOperatorImpl) {
			logger.debug("SPARQL_operator is ReduceOperator");
			//Get the reduceOp from the algebraic expression
			ReduceOperatorImpl reduceOperator = (ReduceOperatorImpl) operator;
			String projectList = reduceOperator.getReconcilingExpression();
			logger.debug("projectList, reconcilingExpression of ReduceOperator: " + projectList);
			
			//Get the Map of superLexicals
			Map<String, SuperLexical> superLexicals = reduceOperator.getSuperLexicals();
			logger.debug("superLexicals>> : " + superLexicals);
			logger.debug("superLexicals size>> : " + superLexicals.size());
			
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
			
			//Recursive call
			translateOperator(reduceOperator.getInput());
	    } else if (operator instanceof JoinOperatorImpl) {
			logger.debug("SPARQL_operator is JoinOperator");	
			//Get the joinOp from the algebraic expression
			JoinOperatorImpl joinOperator = (JoinOperatorImpl) operator;

			String reconcilingExpression = joinOperator.getReconcilingExpression();
			String andOr = joinOperator.getAndOr();
			
			logger.debug("join reconcilingExpression: " + reconcilingExpression);
			Set<Predicate> joinPredicates = joinOperator.getPredicates();
			logger.debug("join Predicates: " + joinPredicates);
			
			if (joinPredicates != null && !joinPredicates.isEmpty())
				if (!reconcilingExpression.contains("union")) {
					logger.debug("no union mentioned in reconcilingExpression");
					addToJoinList(joinPredicates, reconcilingExpression, andOr);
				} else {
					logger.debug("union found in reconcilingExpression, remove all mentions of union");
					logger.debug("reconcilingExpression: " + reconcilingExpression);
					String reconcilingExpressionWithoutUnion = removeUnionFromString(reconcilingExpression);
					logger.debug("reconcilingExpressionWithoutUnion: " + reconcilingExpressionWithoutUnion);
					addToJoinList(joinPredicates, reconcilingExpressionWithoutUnion, andOr);
				}
			
			//TODO check this works properly
			//Recursive call
			translateOperator(joinOperator.getLhsInput());
			translateOperator(joinOperator.getRhsInput());
	    }		

	}//end translateOperator()
	
	
	/***
	 * SPARQL - SELECT CLAUSE
	 */
	private void addToSelectList(String projectList, Map<String, SuperLexical> superLexicals) {
		logger.debug("in addToSelectList");
		logger.debug("projectList: " + projectList);
		logger.debug("selectMap: " + selectMap);
		selectMap.putAll(superLexicals);
		logger.debug("selectMap: " + selectMap);
		logger.debug("superLexicals: " + superLexicals);
	}//end addToSelectList()
	
	
	/***
	 * SPARQL - FROM CLAUSE to WHERE CLAUSE
	 */
	private void addToRDFTypeMap(SuperAbstract superAbstract, String varName) {
		logger.debug("in addToWhereMap");
		logger.debug("superAbstract: " + superAbstract);
		logger.debug("variableName: " + varName);

		if (varName == null)
			varName = superAbstract.getName();
	
		String superAbstractName = superAbstract.getName();
		logger.debug("superAbstractName: " + superAbstractName);
		String schemaName = superAbstract.getSchema().getName();
		logger.debug("schemaName: " + schemaName);
		
		String rdfTypeURI = null;
		/*Check if superAbstract has an rdf_type property*/
		Set<CanonicalModelProperty> propertySet = superAbstract.getProperties();
		
		//TODO: what if it has more than one rdfType properties
		if (propertySet != null) {
			for (CanonicalModelProperty property : propertySet) {
				if (property.getName().equals("rdfTypeValue")){
					rdfTypeURI = property.getValue();	
				}//end inner if
			}//end for
		}//end if
		
		if (!varName.contains("union")) {
			logger.debug("variableName doesn't contain union");
			logger.debug("count previous: " + count);
			count = count + 1;
			rdfTypeMap.put(varName, new WhereInformation(superAbstract,superAbstractName,varName,rdfTypeURI,count));
			logger.debug("added to rdfTypeMap: variableName: " + varName + " superAbstractName: " + superAbstractName);
			logger.debug("count now: " + count);
		} else {
			logger.debug("variableName contains union, remove all mentions of union");
			String varNameWithoutUnion = this.removeUnionFromString(varName);
			logger.debug("varNameWithoutUnion: " + varNameWithoutUnion);
			logger.debug("count previous: " + count);
			count = count + 1;
			rdfTypeMap.put(varNameWithoutUnion, new WhereInformation(superAbstract,superAbstractName,varName,rdfTypeURI,count));
			logger.debug("added to FromMap: varNameWithoutUnion: " + varNameWithoutUnion + " superAbstractName: " + superAbstractName);
			logger.debug("count now: " + count);
		}
	
	}//end addToRDFTypeMap()
	
	
	/***
	 * SPARQL - to FILTER CLAUSE 
	 */	
	private void addToFilterList(Set<Predicate> predicates, String reconcilingExpression, String andOr) {
		logger.debug("in addToFilterList");
		logger.debug("reconcilingExpression: " + reconcilingExpression);
		logger.debug("andOr: " + andOr);
		for (Predicate predicate : predicates) {
			logger.debug("predicate: " + predicate);
		}

		filterList.add(new FilterInformation(reconcilingExpression, andOr, predicates));
		logger.debug("filterList: " + filterList);
	}		
	
	/***
	 * SPARQL - Sort out Joins
	 */	
	private void addToJoinList(Set<Predicate> predicates, String reconcilingExpression, String andOr) {
		logger.debug("in addToJoinList");
		logger.debug("reconcilingExpression: " + reconcilingExpression);
		logger.debug("andOr: " + andOr);
		for (Predicate predicate : predicates) {
			logger.debug("predicate: " + predicate);
		}

		joinList.add(new FilterInformation(reconcilingExpression, andOr, predicates));
		logger.debug("filterList: " + joinList);
	}
	
	/**
	 * Given a subject, predicate and an object, generate a string representation of a triple pattern.
	 * 
	 * @return a triple pattern.
	 */
	public String generateTriplePattern(String subject, String predicate, String object) {
		StringBuffer triplePattern = new StringBuffer(subject + " " + predicate + " " + object + "  .");		
		return triplePattern.toString();
	}
	
	/**
	 * 
	 * @return a randomly generated prefix string.
	 */
	public String generatePrefix() {
		String base   = "abcdefghijklmnopqrstuvwxyz";
		Random random = new Random();
		int stringLength = 3; 
		
		StringBuilder prefix = new StringBuilder();
	    
		for(int i = 0; i < stringLength; i++) {
	    	prefix.append(base.charAt(random.nextInt(base.length())));
	    }
		
		//TODO: sort this out later
		if (prefix.toString().equals("rdf") || prefix.toString().equals("rdfs") ||   prefix.toString().equals("owl"))
			return "abc";
		else
	        return prefix.toString();
	}//end generatePrefix()	
	
   /**
    * 	
    * @return prefix:predicate
    */
	public String getPrefixForpredicate(String uri) {
		logger.debug("in getPrefixForpredicate()");
		for (String key : prefixMap.keySet()) {
			if ((uri != null) && uri.contains(key)) {
				String predicate = uri.substring(key.length(),uri.length());
				logger.debug("predicate is: " + predicate);
		    	uri = ""+prefixMap.get(key)+":"+predicate;
				break;
			}//end if    
		}//end for				
		
		logger.debug("result " + uri);
		
		return uri;
	}//getPrefixForpredicate()
	
	/**
	 * Split a reconciling expression.
	 * @return a List of reconciling expressions
	 */
	//TODO: what if this is S.students_FirstName = 'YolANDa' AND S.students_LastName = 'Gil'
	//method outputs wrong results
	//TODO: change this to regular expresions.
	private List<String> splitUpReconcilingExpression(String reconcilingExpression) {

		List<String> reconcilingExpressions = new ArrayList<String>();

        //Fist get the array of AND

        String [] andArray = reconcilingExpression.split(" and ");

        if (andArray.length == 1)
        	andArray = reconcilingExpression.split(" AND ");

        String [] orArray = reconcilingExpression.split(" or ");

        if (orArray.length == 1)
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
	
	/**
	 * Of the form Var.superLexicaName = ""
	 * @return given a reconcilingExpression and superLexical Name return the parentName
	 */
	private String getParentName(String reconcilingExpression, String sl_name) {
		String parentName = "not_exist";		
		String subStringBeforeSlName = reconcilingExpression.substring(0, reconcilingExpression.indexOf(sl_name));
		logger.debug("subStringBeforeSlName: " + subStringBeforeSlName);
		if (subStringBeforeSlName.length() > 1 && subStringBeforeSlName.contains(".")) {
			parentName = subStringBeforeSlName.substring(0, subStringBeforeSlName.indexOf("."));
			logger.debug("parentName>>: " + parentName);
		}
	  return parentName;
	}//getParentName()

	
	//Return the WhereInformation for this SA if exists
	private WhereInformation getSAforSuperLexical(SuperAbstract sa) {
		logger.debug("in getSAforSuperLexical");

		String varName = sa.getVariableName();
		
		if (varName == null)
			varName = sa.getName();
					
		return rdfTypeMap.get(varName);
	}//end 

	
	/**
	 * Use the information gathered to SPARQL Data Structures to produce a String representation of 
	 * the SPARQL query. 
	 */
	@Override
	public String toString() {
		logger.debug("in toString SPARQL");
		
		//SPARQL - SELECT CLAUSE
		StringBuffer selectString = new StringBuffer("SELECT ");
		   		
		//SPARQL - WHERE CLAUSE 
		StringBuffer whereString = new StringBuffer("\nWHERE { \n");
		
		//SPARQL - SELECT CLAUSE
		if (selectMap.size() == 0) {
			selectString.append("*");
			logger.error("missing superLexicals to select use select *");
		}
		else {
			logger.debug("selectMap>>>: " + selectMap);
			
			for (String selectKey : selectMap.keySet()) {
							
				logger.debug("Edw>>>: " + selectKey);
				String superAbstractName = null;
				String superLexicalName = null;
				SelectInformation selectInfo = null;
				
				if (selectKey.contains(".")) {
					superAbstractName = selectKey.substring(0, selectKey.indexOf("."));
					superLexicalName = selectKey.substring(selectKey.indexOf(".")+1,selectKey.length());
				}
				
				logger.debug("superAbstractName : " + superAbstractName);
				logger.debug("superLexicalName : " + superLexicalName);	
	   			//Store some information
	   			selectInfo = new SelectInformation(superLexicalName, superAbstractName);
				
				if (rdfTypeMap.isEmpty()) {	
					//Generate and store Variable for this superLexical
					selectInfo.setVariable(superLexicalName);
					
					String variable = selectInfo.getVariable();
					logger.debug("Variable : " + variable);	
					
		   			//Add them to the SELECT clause of a SPARQL query
		   			selectString.append(variable+" ");
				}	   			
	   			
	   			//Generate appropriate prefix bindings
				SuperLexical sl = selectMap.get(selectKey);
				logger.debug("SuperLexical: " + sl);
				
				Set<CanonicalModelProperty> modelPropertySet = sl.getProperties();
				
				if (!modelPropertySet.isEmpty()){
					//Get the namespace of the property
					for (CanonicalModelProperty property : modelPropertySet) {
						if (property.getName().equals("namespaceURI")){							
							if (!prefixMap.containsKey(property.getValue())) {
								String prefixName = generatePrefix();
								logger.debug("random prefixName: " + prefixName);
								prefixMap.put(property.getValue(), prefixName);	
								selectInfo.setURI(property.getValue());
								selectInfo.setPrefix(prefixName);
							} else {
								selectInfo.setURI(property.getValue());
								selectInfo.setPrefix(prefixMap.get(property.getValue()));
							}
						 break;
						}//end inner if
					}//end for
					

				}//end if
				
				
	   			//Generate triple patterns of the form ?s_i prefix:superLexicalName ?superLexical
	   			//TODO: find the superAbstract's unique number
	   			for (String parentSA : rdfTypeMap.keySet()) {
	   				if (superAbstractName.equals(parentSA)) {
	   					WhereInformation meta = rdfTypeMap.get(parentSA);
	   					
	   					int uniqueNum = meta.getUniqueNum();
	   					selectInfo.setParentUniqueNum(uniqueNum);
	   					
						//Generate and store Variable for this superLexical
						selectInfo.setVariable(superLexicalName);
	   					
						String variable = selectInfo.getVariable();
						logger.debug("Variable>>: " + variable);	   					
	   					
			   			//Add them to the SELECT clause of a SPARQL query
			   			selectString.append(variable+" ");
	   		   			
	   					logger.debug("TEST >>>"+ meta.getSAName());
	   					logger.debug("TEST number>>>"+ uniqueNum);
	   						   		   			
	   					//add this to the WHERE Clause
	   					String triplePattern = generateTriplePattern("?s" + meta.getUniqueNum(), selectInfo.getNSPrefix(), variable);
     					
	   					//-->whereString.append(triplePattern+"\n");
	   					
	   					selectInfo.setTriplePattern(triplePattern);
	   					
	   					//attach this superLexical to its superAbstract - to be used later for reordering if needed
	   					meta.addToPropList(selectInfo);
	   				}	   				
	   			}//end inner for
			}//end for			
		}//end else
		

		//SPARQL - WHERE CLAUSE -> FILTER is within WHERE clause
		if (!rdfTypeMap.isEmpty()) {
			for (String key : rdfTypeMap.keySet()) {
				WhereInformation meta = rdfTypeMap.get(key);			
				String triplePattern = generateTriplePattern("?s" + meta.getUniqueNum(), "rdf:type", getPrefixForpredicate(meta.getRdfTypeURI()));
				meta.setTriplePattern(triplePattern);
				//-->whereString.append(triplePattern+"\n");
			}//end for
		}
		
   		
     	//SPARQL - FILTER within the WHERE clause    
   		if (!filterList.isEmpty()) {
   			//Case: gp FILTER expr
   			for (FilterInformation filterEntry : filterList) {
				logger.debug("filterEntry-->: " + filterEntry);
				logger.debug("filterEntry.getAndOr(): " + filterEntry.getAndOr());
				logger.debug("predicateSet: " + filterEntry.getPredicates());
   				
  				
   				Set<Predicate> predicates = filterEntry.getPredicates();
			    String reconcilingExpression = filterEntry.getReconcilingExpression(); // Get reconciling expression				
			    logger.debug("reconcilingExpression: " + reconcilingExpression);
			    
			    /*Split reconciling expression if AND/ OR exists*/
				List<String> reconcilingExpressionList = new ArrayList<String>();
				if (predicates.size() == 1)
					reconcilingExpressionList.add(reconcilingExpression);
				else if (predicates.size() > 1) {
					logger.debug("more than one predicate, split up reconcilingExpression");					
					reconcilingExpressionList.addAll(this.splitUpReconcilingExpression(reconcilingExpression));
				}
   				
			    logger.debug("reconcilingExpressionList>>: " + reconcilingExpressionList);
		
				int noPredicates = 0;
				/*Pass from predicates and check if the filter ?var exists if not create it*/
			    for (Predicate predicate : predicates) {
					//get the superlexical's SA and add it to the map of SuperLexicals.
			    	
			    	//SuperLexical 1
					SuperLexical sl1 = predicate.getSuperLexical1();					
				    logger.debug("SuperLexical_1 >>: " + sl1);
			    
				    SuperAbstract sa1 = null;
				    WhereInformation info = null;
				    boolean notFound = false;
				    
				    if (sl1 != null) {
				    	StringBuffer filterIntermediate = new StringBuffer("");				    	

						if (noPredicates > 0) {
							logger.debug("not first predicate");
							String booleanOperator = predicate.getAndOr(); 
							
							if (booleanOperator.equals("AND") || booleanOperator.equals("and"))
								booleanOperator = "&&";
						    
							if (booleanOperator.equals("OR") || booleanOperator.equals("or"))
								booleanOperator = "||";							
								
							
							filterIntermediate.append(" ").append(booleanOperator).append(" ");
						}
				    	
				    	sa1 = sl1.getParentSuperAbstract();	
			    		logger.debug("getParentSuperAbstract :: " + sa1);
				    	
				    	//Get the superAbstract of that superLexical
				    	if (sa1 != null) {				    			
				    		
				    		info = this.getSAforSuperLexical(sa1);
				    		
				    		if (info != null) {
				    			logger.debug("info :: " + info.getSAName());
				    		
				    			List<SelectInformation> selectL = info.getPropList();
		   					
				    			for (SelectInformation entry : selectL) {
				    				
				    				if (sl1.getName().equals(entry.getSuperLexicalName())) {
				    					/*Add the expression to the Filter Clause*/
				    					String literal1 = predicate.getLiteral1();
		   							
				    					if (literal1 == null) {
				    						literal1 = predicate.getLiteral2();
				    					}		   							
				    					//Output String
				    					filterIntermediate.append(" "+entry.getVariable() +" " + predicate.getOperator() + " " + literal1);
				    					notFound = true;
				    				}	   						
				    			}//end for
				    			
				    			if (!notFound) {
						    		logger.debug("?var does not exist - create");	
					    			logger.debug("superAbstract name :: " + info.getSAName());
						    		logger.debug("superLexical name :: " + sl1.getName());
						    		SelectInformation selectInfo = new SelectInformation(sl1.getName(), info.getSAName());
						    		
						    		//Get prefix information
									Set<CanonicalModelProperty> modelPropertySet = sl1.getProperties();
									
									if (!modelPropertySet.isEmpty()){
										//Get the namespace of the property
										for (CanonicalModelProperty property : modelPropertySet) {
											if (property.getName().equals("namespaceURI")){							
												if (!prefixMap.containsKey(property.getValue())) {
													String prefixName = generatePrefix();
													logger.debug("random prefixName: " + prefixName);
													prefixMap.put(property.getValue(), prefixName);	
													selectInfo.setURI(property.getValue());
													selectInfo.setPrefix(prefixName);
												} else {
													selectInfo.setURI(property.getValue());
													selectInfo.setPrefix(prefixMap.get(property.getValue()));
												}
											 break;
											}//end inner if
										}//end for
									}//end if
									
									//Set characteristics for this SelectInformation
				   					selectInfo.setParentUniqueNum(info.getUniqueNum());
				   					
									//Generate and store Variable for this superLexical
									selectInfo.setVariable(sl1.getName());
				   					
									String variable = selectInfo.getVariable();
									logger.debug("Variable>>: " + variable);	   					
				   						   		   			
				   					//add this to the WHERE Clause
				   					String triplePattern = generateTriplePattern("?s" + info.getUniqueNum(), selectInfo.getNSPrefix(), variable);
			     					//-->whereString.append(triplePattern+"\n");
				   					selectInfo.setTriplePattern(triplePattern);
				   					
				   					//attach this superLexical to its superAbstract - to be used later for reordering if needed
				   					info.addToPropList(selectInfo);
				   					
			    					//Add the expression to the Filter Clause
			    					String literal1 = predicate.getLiteral1();
			    					if (literal1 == null) {
			    						literal1 = predicate.getLiteral2();
			    					}		   							
			    					//Output String
			    					filterIntermediate.append(" "+variable +" " + predicate.getOperator() + " " + literal1);
													    		
				    			}//end notFound
				    		
				    		}//end if
				    		else {
					    		logger.debug("WhereInformation does not exist");
				    		}   		
				    		
				    		info.getFilterClause().append(filterIntermediate);				    		
				    	}				    	
			    	
				    	
				    }//end if sl1
	
					SuperLexical sl2 = predicate.getSuperLexical2();					
				    logger.debug("SuperLexical_2 >>: " + sl2);
			    
				    SuperAbstract sa2 = null;
				    WhereInformation info2 = null;
				    boolean notFound2 = false;
				    
				    if (sl2 != null) {
				    	logger.debug("got second superLexical - TODO: not used");
				    	//TODO: For now this is not used.				    
				    }//end if sl2  
				    
				  noPredicates++;
					
				}//end inner for  				
   			}//end for  			
   		}//end filterList
   		
   		
     	//SPARQL - Add JOIN condition
   		if (!joinList.isEmpty()) {
   		   for (FilterInformation joinEntry : joinList) {

   		    for (Predicate joinPredicate : joinEntry.getPredicates()) {
				
   		    	SuperLexical sl1 = joinPredicate.getSuperLexical1();	
			    SuperAbstract sa1 = sl1.getParentSuperAbstract();
			    
   		    	SuperLexical sl2 = joinPredicate.getSuperLexical2();	
			    SuperAbstract sa2 = sl2.getParentSuperAbstract();

			    
			    String reconcilingExpression = joinEntry.getReconcilingExpression();			
			    logger.debug("join reconcilingExpression: " + reconcilingExpression);
   		    	
   		    	//if superAbstracts are not null, check whether there is a superRelationship between them
			    //Assumes that super abstracts are part of the query
			    if ( (sa1 != null) && (sa2 != null) ) {
			      	
			    	//Collect information
					SuperRelationship superRel = null;	    	
			    	
			    	
					Set<ParticipationOfCMCInSuperRelationship> participations = sa1.getParticipationInSuperRelationships();
					
					//Not necessary to continue searching, just break
					search:
					for (ParticipationOfCMCInSuperRelationship participation : participations) {
						if (participation.getRole().equals(SuperRelationshipRoleType.REFERENCING)) {
							SuperRelationship sr = participation.getSuperRelationship();
							logger.debug("sr: " + sr.getName());
							
							Set<ParticipationOfCMCInSuperRelationship> parts = sr.getParticipationsOfConstructs();
							for (ParticipationOfCMCInSuperRelationship part : parts) {
								if (part.getRole().equals(SuperRelationshipRoleType.REFERENCED)) {
									logger.debug("found parent");
									CanonicalModelConstruct parentConstruct = part.getCanonicalModelConstruct();
									String parentConstName = parentConstruct.getName();
									logger.debug("parent construct: " + parentConstName );
									
									// if parentconstruct is equal with sa2 name then found relationship between them	
									if ((parentConstruct != null) && (sa2 == parentConstruct)){
										logger.debug("Found the same CanonicalModelConstruct");
										superRel = sr;
										break search;
										
									} else if ((parentConstruct != null) && (parentConstName.equals(sa2.getName()) )) {
										logger.debug("Found the same CanonicalModelConstruct, from names");
										superRel = sr;
										break search;
									}
									
								}//end inner if
							}//end inner for
							
						}//end if
					}//end for	
					
					logger.debug("Final relationship : " + superRel);	
					if (superRel != null) {
						logger.debug("Relationship found between SA1 and SA2");	
						
						WhereInformation sa1_info = this.getSAforSuperLexical(sa1);
						logger.debug("Join UniqueNum1 >>: " + sa1_info.getUniqueNum());
						WhereInformation sa2_info = this.getSAforSuperLexical(sa2);
						logger.debug("Join UniqueNum2 >>: " + sa2_info.getUniqueNum());
						
						JoinInformation joinObject = new JoinInformation (sa1, sa1_info.getUniqueNum(), sa1, sa2_info.getUniqueNum(), superRel);
	
						//Find prefix for SuperRelationship name
						Set<CanonicalModelProperty> modelPropertySet = superRel.getProperties();
						
						if (!modelPropertySet.isEmpty()){
							//Get the namespace of the property
							for (CanonicalModelProperty property : modelPropertySet) {
								if (property.getName().equals("namespaceURI")){							
									if (!prefixMap.containsKey(property.getValue())) {
										String prefixName = generatePrefix();
										logger.debug("random prefixName: " + prefixName);
										prefixMap.put(property.getValue(), prefixName);	
										joinObject.setURI(property.getValue());
										joinObject.setPrefix(prefixName);
									} else {
										joinObject.setURI(property.getValue());
										joinObject.setPrefix(prefixMap.get(property.getValue()));
									}
								 break;
								}//end inner if
							}//end for
						}//end if				
						
						//save it 
						sa1_info.addToJoinList(joinObject);				
						
					}//end if		    	
			   }   		    
   		    }//end inner for
   		   }//end for
   		}//end if  		  		
   		
   		
		//SPARQL - PREFIX CLAUSE
		StringBuffer prefixString = new StringBuffer();

		for (String key : prefixMap.keySet()) {
		    //logger.debug("URI as Key: " + key + ", Prefix as Value: " + prefixMap.get(key));
		    prefixString.append("PREFIX " + prefixMap.get(key) +": <" + key +">\n");		    
		}//end for
   		
  
   		//OUTPUT
		//SPARQL Query
   		StringBuilder queryString = new StringBuilder();
   		queryString.append(prefixString);
   		queryString.append(selectString);
	
   		for (String parentSA : rdfTypeMap.keySet()) {

   			WhereInformation meta = rdfTypeMap.get(parentSA);
   			
   			//print rdf:type
			whereString.append(meta.getTriplePattern()+"\n");
   			
   			for (SelectInformation selectInfo : meta.getPropList()) {
   				//print the variable bindings
   				whereString.append(selectInfo.getTriplePattern()+"\n");   				
   			}		
		
			//print filter if it has
			if (!meta.getFilterClause().toString().equals("")) {
				whereString.append("FILTER (");
				whereString.append(meta.getFilterClause());
				whereString.append(" )\n");			
			}
			
			//add Join condition if exists
			for (JoinInformation joinInfo : meta.getJoinList()) {
				whereString.append(joinInfo.toString());		
				whereString.append("\n");		
			}
			
		}//end for

   		queryString.append(whereString + "}");
		
		return queryString.toString();
	}//end toString()
	
	
	
	
	/********************************************
	 *                                          *
	 * Inner Classes to hold extra information  *
	 *                                          *
	 ********************************************/	
	public class WhereInformation {
		
		private SuperAbstract superAbstract;
		private String superAbstractName; 
		private String superAbstractVarName; 
		private String rdfTypeURI; 
		private int    uniqueNumber;
		private String prefix;
		private String triplePattern;
		private List<SelectInformation> propertiesList;
		private List<JoinInformation> joinList;
		StringBuffer filterClause;
		
		public WhereInformation(SuperAbstract superAbstractRef, String saName, String saVarName, String uri, int num) {
			superAbstract = superAbstractRef;
			superAbstractName = saName;
			superAbstractVarName = saVarName;
			rdfTypeURI = uri;
			uniqueNumber  = num;
			propertiesList = new ArrayList<SelectInformation>();
			joinList = new ArrayList<JoinInformation>();
			filterClause = new StringBuffer("");
		}
		
		public SuperAbstract getSuperAbstractRef() {
			return this.superAbstract;
		}
		
		public String getSAName() {
			return this.superAbstractName;
		}
		
		public String getSAVarName() {
			return this.superAbstractVarName;
		}
		
		public String getRdfTypeURI() {
			return this.rdfTypeURI;
		}
		
		public List<SelectInformation> getPropList() {
			//attach superLexicals to this superAbstract
			return propertiesList;
		}	
		
		public List<JoinInformation> getJoinList() {
			//attach superLexicals to this superAbstract
			return joinList;
		}
		
		public StringBuffer getFilterClause() {
			return this.filterClause;
		}
		
		public int getUniqueNum() {
			return this.uniqueNumber;
		}
		
		public String getTriplePattern() {
			return this.triplePattern;
		}
		
		public void setTriplePattern(String tp) {
			triplePattern = tp;
		}
		
		public void addToPropList(SelectInformation meta) {
			//attach superLexicals to this superAbstract
			propertiesList.add(meta);
		}	
		
		public void addToJoinList(JoinInformation join) {
			//attach superLexicals to this superAbstract
			joinList.add(join);
		}			
		
	}//end WhereInformation()
	
	public class SelectInformation {
		
		private String superLexicalName;
		private String superLexicalParentSAName;
		private String uri; 
		private String prefix;
		private String variable;
		private String triplePattern;
		private int parentUniqueNum;
		
		public SelectInformation(String lexicalName, String parentSAName) {
			superLexicalName = lexicalName;
			superLexicalParentSAName = parentSAName;
			parentUniqueNum = -1;
		}
		
		public String getSuperLexicalName() {
			return this.superLexicalName;
		}
		
		public String getParentSAName() {
			return this.superLexicalParentSAName;
		}
		
		public String getVariable() {
			return this.variable;
		}
		
		public int getParentUniqueNum() {
			return this.parentUniqueNum;
		}
		
		public String getTriplePattern() {
			return this.triplePattern;
		}
		
		public String getNSPrefix() {
			if (this.prefix != null)
				return prefix + ":" + superLexicalName;
			else 
				return uri + superLexicalName;		
		}
		
		public void setURI(String u) {
			this.uri = u;
		}
		
		public void setPrefix(String p) {
			this.prefix = p;
		}
		
		public void setVariable(String var) {
			if (parentUniqueNum != -1)			
				this.variable = new String("?"+ var + parentUniqueNum);
			else 
				this.variable = new String("?"+ var);
		}
		
		public void setTriplePattern(String tp) {
			triplePattern = tp;
		}
		
		public void setParentUniqueNum(int num) {
			parentUniqueNum = num;
		}
	
	}//end SelectInformation inner Class	
	
	public class FilterInformation {

		private String reconcilingExpression;
		private String andOr;
		private Set<Predicate> predicates;

		public FilterInformation(String reconcilingExpression, String andOr, Set<Predicate> predicates) {
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
	}//end FilterInformation inner Class	
	
	
	public class JoinInformation {
		private SuperAbstract  sa1;
		private int uniqueNum1;
		private SuperAbstract sa2;
		private int uniqueNum2; 
		private String uri; 
		private String prefix;
		SuperRelationship superRelationship;
		
		public JoinInformation(SuperAbstract super1, int num1, SuperAbstract super2, int num2, SuperRelationship rel) {
			sa1 = super1;
			uniqueNum1 = num1;
			sa2 = super2;
			uniqueNum2 = num2;
			superRelationship = rel;
		}
		
		public SuperAbstract getSuperAbstract1() {
			return this.sa1;
		}

		public SuperAbstract getSuperAbstract2() {
			return this.sa2;
		}
		
		public int getUniqueNum1() {
			return this.uniqueNum1;
		}

		public int getUniqueNum2() {
			return this.uniqueNum2;
		}
		
		public SuperRelationship getsuperRelationship() {
			return this.superRelationship;
		}
		
		public String getNSPrefix() {
			if (this.prefix != null)
				return prefix + ":" + superRelationship.getName();
			else 
				return uri + superRelationship.getName();		
		}
		
		public void setURI(String u) {
			this.uri = u;
		}
		
		public void setPrefix(String p) {
			this.prefix = p;
		}
		
		//TODO: sort out prefix
		public String toString() {
			return "?s" + uniqueNum1 + " " + getNSPrefix() + " " + "?s" + uniqueNum2 + " .";
		}
	}//end FilterInformation inner Class	
	
}//end class