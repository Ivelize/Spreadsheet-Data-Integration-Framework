package uk.ac.manchester.dstoolkit.service.impl.util;

/**
 * This is a library of SPARQL queries.
 * 
 * 
 * @author Klitos Christodoulou
 *
 */
public class SPARQLQueriesLibrary {

	/**
	 * List all Named Graphs in the Store
	 */
	public static String LIST_ALL_NAMED_GRAPHS = "SELECT DISTINCT ?g WHERE {GRAPH ?g {?s ?p ?o}}";

		
	/**
	 * Find whether a subject URI exists within the set of Named Graphs
	 */
	public static String askSubjectURIallNamedGraphs(String constructURI) {
		String queryStringAllGraphs =
			"ASK" +
			"WHERE {" +
			" GRAPH ?g { <" + constructURI + "> " + "?p ?o .}" +
			" }";
		return queryStringAllGraphs;
	}
	

	/**
	 * @param no_triples
	 * @return
	 */
	public static String listNoTriplesFromDefaultGraphOrNamedGraphs(int no_triples) {
		return "SELECT ?s ?p ?o ?g WHERE {{?s ?p ?o} UNION {GRAPH ?g {?s ?p ?o}}} LIMIT" + no_triples;
	}
	
	/**
	 * 
	 * @param graphURI - name of graph to search for
	 * @param entity1 - uri of entity1
	 * @param entity2 - uri of entity2
	 * @return
	 */
	public static String askForMappingWithFROM(String graphURI, String entity1, String entity2) {
		String queryAlignment = 
				"ASK" +
				" FROM <" + graphURI + ">" +
				" WHERE {" +
				" ?cell ?p1 <" + entity1 + "> ." +
				" ?cell ?p2 <" + entity2 + "> ." +
				" }";
	
		return queryAlignment;
	}	

	public static String askForMappingWithOutFROM(String entity1, String entity2) {
		String queryAlignment = 
				"ASK" +
				" WHERE {" +
				" ?cell ?p1 <" + entity1 + "> ." +
				" ?cell ?p2 <" + entity2 + "> ." +
				" }";
	
		return queryAlignment;
	}	
	
}//end class
