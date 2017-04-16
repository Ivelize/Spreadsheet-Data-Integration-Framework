package uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.service.util.importexport.ExpMatrix.ImportExpMatrixService;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * This class provides a service to the user that reads the actual matches from an 
 * xml file and then creates the Expectation Matrix that is wrapped as a SemanticMatrix object.
 * 
 * @author Klitos Christodoulou
 */

@Service(value = "importExpMatrixService")
public class ImportExpMatrixServiceImpl implements ImportExpMatrixService {

	static Logger logger = Logger.getLogger(ImportExpMatrixServiceImpl.class);
	private ExpMatrix expMatrix;
	
	/*Constructor*/
	public ImportExpMatrixServiceImpl() {
		expMatrix = null;
	}
	
	/***
	 * This method is responsible for reading the XML file into a data structure created by the pom.xml
	 * and ./src/main/xsd/ExpMatrix.xsd
	 * @param fileLocation
	 */
	public void readMatrixFromXML(String fileLocation) {
		logger.debug("in readMatrixFromXML");
		logger.debug("Alignment file path: " + fileLocation);
		/*Transform data between XML elements and Java objects*/		
		JAXBContext context;
		
		try {
			/*Invoke the JAXB object to read the XML file into a datastructure that is created
			 *automatically by the pom.xml and schema .xsd document*/
			context = JAXBContext.newInstance("uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix");
			
			/*convert XML data into a tree of Java content objects*/
			Unmarshaller unmarshaller = context.createUnmarshaller();
			expMatrix = (ExpMatrix) unmarshaller.unmarshal(new FileReader(fileLocation));
		} catch (JAXBException e) {
			logger.error("error with the Expectation Alignment file");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			logger.error("Expectation Alignment file is missing");
			e.printStackTrace();
		}		
	}//end readMatrixFromXML()
	
	/***
	 * Traverse ClassAlign elements from the XML document and searches for any alignment that exists in the 
	 * constructs. If an alignment is discovered it is then returned.
	 */
	public ClassesAlign findClassAlign(Resource res1, Resource res2) {
		//logger.debug("in findClassAlign");
		List<ClassesAlign> classesList = expMatrix.getClassesAlign();
		List<Resource> array1 = new ArrayList<Resource>();
		List<Resource> array2 = null;
		array1.add(res1);
		array1.add(res2);
		
		for (ClassesAlign entry : classesList) {
			array2 = new ArrayList<Resource>();
			Resource res1fromXML = this.fromStringToResource(entry.getClass1URI());
			Resource res2fromXML = this.fromStringToResource(entry.getClass2URI());
			array2.add(res1fromXML);
			array2.add(res2fromXML);			
			
    		//logger.debug("res1fromXML: " + res1fromXML.getURI());
			//logger.debug("res2fromXML: " + res2fromXML.getURI());

			if (this.isAlign(array1, array2)) {
				return entry;
			}
		}//end for		
		return null;
	}//findClassAlign 
	
	/***
	 * Traverse PredicatesAlign elements from the XML document and searches for any alignment that exists in the 
	 * constructs. If an alignment is discovered it is then returned.
	 */
	public PredicatesAlign findPredAlign(Resource res1, Resource res2, Resource res1SA, Resource res2SA) {
		List<PredicatesAlign> predList = expMatrix.getPredicatesAlign();
		List<Resource> array1 = new ArrayList<Resource>();
		List<Resource> array2 = null;
		array1.add(res1);
		array1.add(res2);
		array1.add(res1SA);
		array1.add(res2SA);
		
		for (PredicatesAlign entry : predList) {
			array2 = new ArrayList<Resource>();
			Resource res1SAfromXML = this.fromStringToResource(entry.getClass1URI());
			Resource res2SAfromXML = this.fromStringToResource(entry.getClass2URI());
			Resource res1fromXML = this.fromStringToResource(entry.getProp1URI());
			Resource res2fromXML = this.fromStringToResource(entry.getProp2URI());			
			array2.add(res1SAfromXML);
			array2.add(res2SAfromXML);			
			array2.add(res1fromXML);
			array2.add(res2fromXML);
			
			if (this.isAlign(array1, array2)) {
				return entry;
			}//end if
		}//end for
		
		return null;
	}//end findPredAlign()	
	
	/**
	 * This method takes a string in the form prefix:localName. It then finds which the namespace string
	 * that matches the 
	 * @param uri
	 * @return
	 */
	public Resource fromStringToResource(String s) {
		//logger.debug("in fromStringToResource()");
		if ((s == null) || s.equals("")) {
			return null;
		}
		/*Get list of namespaces*/
		List<String> nsList = expMatrix.getNamespacePrefixes().getNameSpace();
		String[] prefixArray = s.split(":");
		String prefix1 = prefixArray[0].trim();
		String localName1 = prefixArray[1].trim();
		
		for (String entry : nsList) {
			String[] splitArray = entry.split("=");
		
			if (prefix1.equals(splitArray[0].trim())) {
				String uri = new String(splitArray[1].trim()+localName1);
				//logger.debug("uri: " + uri);
				return ResourceFactory.createResource(uri);
			}//end if			
		}//end for
		
		//Resource res = ResourceFactory.createResource(uri);	
		return null;		
	}//end fromStringToResource()	
	
	/***
	 * Return an alignment if exists by comparing Resource elements that exist in a Set.
	 * @return true when an alignment exists, false otherwise
	 */
	public boolean isAlign(List<Resource> array1, List<Resource> array2) {
		 Set<Resource> set1 = new HashSet<Resource>(array1);
		 Set<Resource> set2 = new HashSet<Resource>(array2);
		 int set1s = set1.size();
		 int set2s = set2.size();
		 set1.removeAll(set2);
		 
		 if ((set1s == set2s) && set1.size() == 0) {
			 return true;
		 } else {
			 return false; 
		 }
	}//end isAlign()
	
	/***
	 * Method that returns a pointer to the Root XML element GTMatrix. 
	 */
	public ExpMatrix getRootElement() {
		return this.expMatrix;
	}//end getRootElement()
}//end Class