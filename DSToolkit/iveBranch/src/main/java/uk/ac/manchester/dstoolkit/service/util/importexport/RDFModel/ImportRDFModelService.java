package uk.ac.manchester.dstoolkit.service.util.importexport.RDFModel;

import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.repository.canonical.ParticipationOfCMCInSuperRelationshipRepository;
import uk.ac.manchester.dstoolkit.repository.canonical.SuperRelationshipRepository;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.ImportRDFSchemaFromXMLService;

/**
 * Interface: From method signatures and constants for the RDF importer
 * ImportRDFModelServiceImpl Class. 
 * 
 * @author Klitos Christodoulou
 * 
 */

public interface ImportRDFModelService {

	 public abstract void populateRDFModelToCanonicalModel(ImportRDFSchemaFromXMLService importRDFSchemaFromXMLService, Schema schema);
	
	 public void init(SuperRelationshipRepository superRel, ParticipationOfCMCInSuperRelationshipRepository partRepo);
}
