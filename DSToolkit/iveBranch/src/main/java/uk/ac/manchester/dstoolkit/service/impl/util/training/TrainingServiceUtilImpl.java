package uk.ac.manchester.dstoolkit.service.impl.util.training;

import uk.ac.manchester.dstoolkit.service.util.training.TrainingServiceUtil;

import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

/*********************************************************************************************************
 * 									TrainingServiceUtilImpl (Abstract)
 * 													|
 * 					________________________________|___________________________________
 * 					|								|									|
 * 					|								|									|
 * 		SynEvidenceTrainingUtilImpl		SemEvidenceDataAnalysisUtilImpl		SemEvidenceTrainingUtilImpl
 * 
 * @author klitos
 */
public abstract class TrainingServiceUtilImpl implements TrainingServiceUtil {
	
	/**
	 * @return - a PrefixMapping, holds prefix and namespace
	 */
	public PrefixMapping createPrefixMap() {
		PrefixMapping pmap = PrefixMapping.Factory.create();
		pmap.setNsPrefix("owl", OWL.getURI());
		pmap.setNsPrefix("rdf", RDF.getURI());
		pmap.setNsPrefix("rdfs", RDFS.getURI());
		pmap.setNsPrefix("xsd", XSD.getURI());
		pmap.setNsPrefix("skos", "http://www.w3.org/2004/02/skos/core#");
		pmap.setNsPrefix("void", "http://vocab.deri.ie/void#");
		pmap.setNsPrefix("dbpedia", "http://dbpedia.org/ontology/");
		pmap.setNsPrefix("fn", "http://www.w3.org/2005/xpath-functions#");		
		pmap.setNsPrefix("afn", "http://jena.hpl.hp.com/ARQ/function#");	
		pmap.setNsPrefix("align", "http://knowledgeweb.semanticweb.org/heterogeneity/alignment");
		return pmap;
	}//end createPrefixMap()
	
	/**
	 * @return String - List of common namespace prefixes to attach on queries
	 */
	public String getNSPrefixes() {		
		String prefixes = 
                "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
    	        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
    	        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
    	        "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> " +
    	        "PREFIX afn: <http://jena.hpl.hp.com/ARQ/function#> " +
    	        "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> " +
    	        "PREFIX void: <http://vocab.deri.ie/void#> " +
    	        "PREFIX j.0: <x-ns://train.metadata/#> " +
    	        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
    	        "PREFIX align: <http://knowledgeweb.semanticweb.org/heterogeneity/alignment> " +
    	        "PREFIX align2: <http://knowledgeweb.semanticweb.org/heterogeneity/alignment#> ";		
		return prefixes;
	}//end getNSPrefixes()	
}//end Class