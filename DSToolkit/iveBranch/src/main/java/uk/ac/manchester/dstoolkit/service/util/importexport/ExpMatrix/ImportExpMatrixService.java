package uk.ac.manchester.dstoolkit.service.util.importexport.ExpMatrix;

import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.ClassesAlign;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.ExpMatrix;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.PredicatesAlign;

import com.hp.hpl.jena.rdf.model.Resource;

public interface ImportExpMatrixService {
	
	public void readMatrixFromXML(String fileLocation);
	public ClassesAlign findClassAlign(Resource res1, Resource res2);
	public PredicatesAlign findPredAlign(Resource res1, Resource res2, Resource res1SA, Resource res2SA);
	public ExpMatrix getRootElement();

}
