package uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema;

import java.util.List;
import java.util.Map;

import uk.ac.manchester.dstoolkit.service.impl.util.importexport.RDFModel.RDFClass;

/**
 * Interface: method signatures and constants for the RDF importer
 * ImportRDFSchemaFromXMLServiceImpl Class. 
 * 
 * @author Klitos Christodoulou
 * 
 */

public interface ImportRDFSchemaFromXMLService {
	
 public abstract void readRDFSchemaElementsFromXml(String fileLocation);
 
 public Map<String, RDFClass> getNameRDFClassMapMap();
 
 public List<RDFClass> getRDFClassList();
}//end class