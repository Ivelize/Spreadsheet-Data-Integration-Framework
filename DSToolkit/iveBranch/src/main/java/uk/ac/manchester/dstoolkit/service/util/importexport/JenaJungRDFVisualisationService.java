package uk.ac.manchester.dstoolkit.service.util.importexport;

import com.hp.hpl.jena.rdf.model.Model;

public interface JenaJungRDFVisualisationService {

	public void visualiseRDFGraph(String uri);
	
	public void visualiseRDFGraph(Model model, boolean propertyLabel, boolean nodeLabel);
	
}
