package uk.ac.manchester.dstoolkit.service.meta;

import java.util.List;
import java.util.Map;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelProperty;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.PrecedenceLevel;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixType;
import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public interface SemanticMetadataService {
	
	public PrecedenceLevel getPrecedenceLevel();
	
	public void setPrecedenceLevel(int l);
	
	public String getSemMatrixName();
	
	public void setSemMatrixName(String name);

	public void setSemMatrixType(SemanticMatrixType type);

	public SemanticMatrixType getSemMatrixType();
	
	public SDBStoreServiceImpl getSDBStoreService();
	
	public void setSDBStoreService(SDBStoreServiceImpl store);
	
	public List<SemanticMetadataService> getAttachedSemanticMatrices();

	public void attachSemMatrix(SemanticMetadataService m);	
	
	public CanonicalModelProperty getConstructPropURI(CanonicalModelConstruct construct);
	public CanonicalModelProperty getConstructPropNS(CanonicalModelConstruct construct);
	
	public Model subjectURIexistsDatasetSELECT(String constructURI);
	public boolean subjectURIexistsASK(Model model, String constructURI);
	public boolean isGraphExistsASK(String graphName);   
	public boolean isGraphExists(String graphName);
	
	public void addControlParameter(ControlParameter controlParameter);
	public Map<ControlParameterType, ControlParameter> getControlParameters();
	public ResultSet getResultSetForSubjectURIandPredicate(Model model, String constructURI, com.hp.hpl.jena.rdf.model.Property pred);
}//end SemanticMetadataService
