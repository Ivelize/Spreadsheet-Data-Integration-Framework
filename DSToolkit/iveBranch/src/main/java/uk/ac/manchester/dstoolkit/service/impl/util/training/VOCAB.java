package uk.ac.manchester.dstoolkit.service.impl.util.training;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * Vocabulary Definitions
 * @author klitos
 */
public class VOCAB {
    /** Namespace of SKOS vocabulary as a String */
    public static final String SKOS_NS = "http://www.w3.org/2004/02/skos/core#";
    
    /** Namespace from my vocabulary used to make results persistent**/
    public static final String MY_NS = "x-ns://train.metadata/";
    
    //Classes
    public static final Property skosExactMatch = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#exactMatch");
    
    public static final Property voidDataset = ResourceFactory.createProperty("http://vocab.deri.ie/void#Dataset");
    
        
	/** Setup vocabulary to use for storing the results **/	
    
	/*on which predicate*/
    public static final Property onPredicate = ResourceFactory.createProperty( MY_NS, "#onPredicate" );
	/*total number of pairs that have the axiom*/
    public static final Property totalNoPairs = ResourceFactory.createProperty( MY_NS, "#totalNoOfPairs" );
	/*number of pairs that are Classes that have the axiom and rdf:type rdfs:Class or owl:Class*/
    public static final Property pairsNoClasses = ResourceFactory.createProperty( MY_NS, "#pairsNoClasses" );
	/*number of pairs that are Classes that have the axiom and rdf:type rdfs:Class or owl:Class*/		
    public static final Property pairsNoClassesExplicit = ResourceFactory.createProperty( MY_NS, "#pairsNoClassesExplicit" );
	/*number of pairs that are Classes that have the axiom but not rdf:type rdfs:Class or owl:Class*/		
    public static final Property pairsNoClassesImplicit = ResourceFactory.createProperty( MY_NS, "#pairsNoClassesImplicit" );
	/*number of pairs that are Classes but are used to state membership restrictions*/		
    public static final Property pairsNoMemberRestrict = ResourceFactory.createProperty( MY_NS, "#pairsNoMemberRestrict" );		
	/*number of pairs that are Properties that have the axiom*/
    public static final Property pairsNoProperties = ResourceFactory.createProperty( MY_NS, "#pairsNoProps" );
	/*number of pairs that are Properties that have the axiom and rdf:Property*/		
    public static final Property pairsNoPropsExplicit = ResourceFactory.createProperty( MY_NS, "#pairsNoPropsExplicit" );
	/*number of pairs that are Properties that have the axiom but not rdf:Property*/		
    public static final Property pairsNoPropsImplicit = ResourceFactory.createProperty( MY_NS, "#pairsNoPropsImplicit" );
	/*number of pairs that are Instances that have the axiom*/
    public static final Property pairsNoInstances = ResourceFactory.createProperty( MY_NS, "#pairsNoInstances" );
	/*number of pairs that are something else*/
    public static final Property pairsNoOther = ResourceFactory.createProperty( MY_NS, "#pairsNoOther" );
    
	/**Hold the totals**/
    public static final Property totalPairsEquiClasses = ResourceFactory.createProperty( MY_NS, "#totalPairsEquiClasses" );	
    public static final Property totalPairsNonEquiClasses = ResourceFactory.createProperty( MY_NS, "#totalPairsNonEquiClasses" );	
    public static final Property totalPairsEquiProps = ResourceFactory.createProperty( MY_NS, "#totalPairsEquiProps" );
    public static final Property totalPairsNonEquiProps = ResourceFactory.createProperty( MY_NS, "#totalPairsNonEquiProps" );
	
    /**Custom Rules for Non-equivalent Properties**/
    public static final Property onRule1 = ResourceFactory.createProperty( MY_NS, "#rule1" );
    public static final Property onRule2 = ResourceFactory.createProperty( MY_NS, "#rule2" );
    
    /**For evidence RDF-graph**/
    public static final Property onEvidence = ResourceFactory.createProperty( MY_NS, "#onEvidence" );
    public static final Property evidenceAndEqui = ResourceFactory.createProperty( MY_NS, "#evidenceAndEqui" );    
    public static final Property evidenceAndNonEqui = ResourceFactory.createProperty( MY_NS, "#evidenceAndNonEqui" );     
       
	/****/
    
	/** Vocabulary to hold the classification of constructs TP/FP **/    
    public static final Property sourceEntity = ResourceFactory.createProperty( MY_NS, "#sourceEntity" );
    public static final Property targetEntity = ResourceFactory.createProperty( MY_NS, "#targetEntity" );
    public static final Property classLabel = ResourceFactory.createProperty( MY_NS, "#classLabel" );
    public static final Property position = ResourceFactory.createProperty( MY_NS, "#position" );
    public static final Property matcherService = ResourceFactory.createProperty( MY_NS, "#matcherService" );
    public static final Property score = ResourceFactory.createProperty( MY_NS, "#score" );
    public static final Property squareError = ResourceFactory.createProperty( MY_NS, "#squareError" );
    public static final Property absoluteError = ResourceFactory.createProperty( MY_NS, "#absoluteError" );    
    public static final Property fileName = ResourceFactory.createProperty( MY_NS, "#fileName" );
    
}
