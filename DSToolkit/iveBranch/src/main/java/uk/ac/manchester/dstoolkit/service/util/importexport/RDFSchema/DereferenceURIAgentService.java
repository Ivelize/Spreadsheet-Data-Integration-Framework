package uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema;

import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.ActionStatus;
import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public interface DereferenceURIAgentService {
	
	public void initiliaseURIagent(SDBStoreServiceImpl obj);
	
	public ActionStatus dereferenceURI(String modelName, String sourceURL);
	public ActionStatus dereferenceURIHttpClient(String modelName, String uri, String header);
	
	public boolean isGraphExists(String graphName);
	public boolean isGraphExistsASK(String graphName);
	
	public SDBStoreServiceImpl getSDBStoreService();	
	
	public boolean subjectURIexistsASK(Model model, String constructURI);

	public Model subjectURIexistsDatasetSELECT(String constructURI); 
	public ResultSet getResultSetForSubjectURIandPredicate(Model model, String constructURI, com.hp.hpl.jena.rdf.model.Property pred);
	
}
