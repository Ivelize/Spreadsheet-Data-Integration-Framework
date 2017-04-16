package uk.ac.manchester.dstoolkit.service.impl.meta;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.SchematicCorrespondence;
import uk.ac.manchester.dstoolkit.service.meta.SchemaService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.SchematicCorrespondenceService;

public class SpreadsheetCorrespondencesGraphResult {
	
	private static Logger logger = Logger.getLogger(SpreadsheetCorrespondencesGraphResult.class);
	
	@Autowired
	@Qualifier("schemaService")
	private SchemaService schemaService;
	
	@Autowired
	@Qualifier("schematicCorrespondenceService")
	private SchematicCorrespondenceService schematicCorrespondenceService;
	
	
	@Test
	public void generateFileCorrespondencesResult(){
		
		/*try {
			List<SchematicCorrespondence> lstSC = schematicCorrespondenceService.findAll();
			schemaService.generateResultsToGraph(lstSC);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		*/
		
	}

}
