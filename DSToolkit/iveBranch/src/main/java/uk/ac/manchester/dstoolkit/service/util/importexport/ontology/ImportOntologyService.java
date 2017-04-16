package uk.ac.manchester.dstoolkit.service.util.importexport.ontology;

import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.repository.canonical.ParticipationOfCMCInSuperRelationshipRepository;
import uk.ac.manchester.dstoolkit.repository.canonical.SuperRelationshipRepository;

import com.hp.hpl.jena.ontology.OntModel;

public interface ImportOntologyService {
	
	public void init(OntModel ontoModel, Schema schema,
			SuperRelationshipRepository superRel, ParticipationOfCMCInSuperRelationshipRepository partRepo);
	
	public void mapOntologyToSuperModel();
	
	public void setOntoModel(OntModel ontoModel);
	public OntModel getOntoModel();
	
	public void setSchema(Schema schema);
	public Schema getSchema();
}
