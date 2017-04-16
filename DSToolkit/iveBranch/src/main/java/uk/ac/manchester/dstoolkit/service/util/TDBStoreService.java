package uk.ac.manchester.dstoolkit.service.util;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;

public interface TDBStoreService {

	public Dataset getDataset();
	public Model getModel(String modelName);
	
	//public ActionStatus loadDataToModelFromRDFDump(String modelName, String sourceURL);
	public void removeNamedModel(String uri);
}
