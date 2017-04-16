/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.meta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.AbstractIntegrationTest;
import uk.ac.manchester.dstoolkit.domain.Dataspace;
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.OneToOneMatching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.SchematicCorrespondence;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.repository.morphisms.schematiccorrespondence.SchematicCorrespondenceRepository;
import uk.ac.manchester.dstoolkit.service.DataspaceService;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.NGramMatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.SelectionType;
import uk.ac.manchester.dstoolkit.service.meta.SchemaService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.InferCorrespondenceService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.SchematicCorrespondenceService;
import uk.ac.manchester.dstoolkit.service.util.spreadsheet.CellPOJO;
import uk.ac.manchester.dstoolkit.service.util.spreadsheet.Constant;
import uk.ac.manchester.dstoolkit.service.util.spreadsheet.SpreadsheetService;

/**
 * @author ive
 *
 */

public class UploadSpreadsheetSchemaTest {

private static Logger logger = Logger.getLogger(UploadSpreadsheetSchemaTest.class);
	
	@Autowired
	@Qualifier("schemaService")
	private SchemaService schemaService;
	
	@Autowired
	@Qualifier("schematicCorrespondenceService")
	private SchematicCorrespondenceService schematicCorrespondenceService;
	
	@Autowired
	@Qualifier("schematicCorrespondenceRepository")
	private SchematicCorrespondenceRepository schematicCorrespondenceRepository;
	
	
	@Test
	public void dataIntegration1() throws ExecutionException {
		
		logger.debug("---------------------------------------------------------------");
		logger.debug("-                                                             -");
		logger.debug("-------------------- LOAD SPREADSHEETS ------------------------");
		logger.debug("-                                                             -");
		logger.debug("---------------------------------------------------------------");
		
		SpreadsheetService spreadsheetService = new SpreadsheetService();
		
		
		//spreadsheetService.dropSpreadsheetSchema("test1");
		
		
		File spreadsheetSourceSpreadsheets[];
		File spreadsheetsDirectory = new File(Constant.DIRECTORY_PATH_SPREADSHEETS_SOURCE);
		spreadsheetSourceSpreadsheets = spreadsheetsDirectory.listFiles();
		spreadsheetService.uploadSpreadsheet (spreadsheetSourceSpreadsheets, "source");
	
		/*File spreadsheetTargetSpreadsheets[];
		File spreadsheetsDirectoryTarget = new File(Constant.DIRECTORY_PATH_SPREADSHEETS_TARGET);
		spreadsheetTargetSpreadsheets = spreadsheetsDirectoryTarget.listFiles();
		spreadsheetService.uploadSpreadsheet (spreadsheetTargetSpreadsheets, "target");*/
		
		
	}// end method
	
	
	/*@Test
	public void countAttributesSpreadsheets() throws ExecutionException {
		
		SpreadsheetService spreadsheetService = new SpreadsheetService();
		File spreadsheetSourceSpreadsheets[];
		File spreadsheetsDirectory = new File(Constant.DIRECTORY_PATH_SPREADSHEETS_SOURCE);
		spreadsheetSourceSpreadsheets = spreadsheetsDirectory.listFiles();
		try {
			spreadsheetService.countAttributesInSpreadsheet(spreadsheetSourceSpreadsheets);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}*/
	
	@Test
	public void getAttributesSpreadsheets() throws ExecutionException {
		
		SpreadsheetService spreadsheetService = new SpreadsheetService();
		File spreadsheetSourceSpreadsheets[];
		File spreadsheetsDirectory = new File(Constant.DIRECTORY_PATH_SPREADSHEETS_SOURCE);
		spreadsheetSourceSpreadsheets = spreadsheetsDirectory.listFiles();
		try {
			spreadsheetService.getAttributesInSpreadsheet(spreadsheetSourceSpreadsheets);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	@Test
	public void LoadSpreadsheetsConfiguration() throws ExecutionException {
		
		SpreadsheetService spreadsheetService = new SpreadsheetService();

		try {
			List<CellPOJO> spread = spreadsheetService.loadSpreadsheets(Constant.DIRECTORY_PATH_CONFIGURATION, "system_configuration.xls");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	

	/*@Test
	public void selectSpreadsheets() throws ExecutionException {
		
		SpreadsheetService spreadsheetService = new SpreadsheetService();

		try {
			spreadsheetService.selectFiles();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	/*@Test
	public void countAttributesCorrelationSpreadsheets() throws ExecutionException {
		
		List<SchematicCorrespondence> lstSC = schematicCorrespondenceRepository.findAll();
		try {
			schemaService.createAttributesCorrelation(lstSC);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}*/
	
	/*@Test
	public void getSpreadsheetsCorrelation() throws ExecutionException {
		
		try {
			System.out.println("iniciando");
			List<SchematicCorrespondence> lstSC =  schematicCorrespondenceService.findAllSchematicCorrespondences();
	        schemaService.generateResultsToGraph(lstSC);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
	}*/
	
	
	
}
