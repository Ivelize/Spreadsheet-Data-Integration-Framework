package uk.ac.manchester.dstoolkit.service.morphisms.matching.RDF;

/***
 * This is a test class used to generate the 3D plots for the matchers. The plots show the "Resulting posterior probability" vs
 * the "Prior probability". Such plots are shown in the ISWC paper and thesis.
 * 
 * 
 * @author klitos
 */


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.RDFAbstractInitialisation;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.BayesianTheorem;
import uk.ac.manchester.dstoolkit.service.meta.DataSourceService;
import uk.ac.manchester.dstoolkit.service.meta.SchemaService;
import uk.ac.manchester.dstoolkit.service.util.ExternalDataSourcePoolUtilService;
import uk.ac.manchester.dstoolkit.service.util.importexport.GraphvizDotGeneratorService;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.LoadRDFSourceUtilService;
import uk.ac.manchester.dstoolkit.service.util.training.EvaluatorService;

public class CalculatePosteriorsForPMFTest extends RDFAbstractInitialisation {

	private static Logger logger = Logger.getLogger(CalculatePosteriorsForPMFTest.class);
	
	@Autowired
	@Qualifier("loadRDFSourceUtilService")
	private LoadRDFSourceUtilService loadRDFSourceUtilService;
	
	@Autowired
	@Qualifier("externalDataSourcePoolUtilService")
	private ExternalDataSourcePoolUtilService externalDataSourcePoolUtilService;
		
	@Autowired
	@Qualifier("dataSourceService")
	private DataSourceService dataSourceService;
	
	@Autowired
	@Qualifier("schemaService")
	private SchemaService schemaService;
	
	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;

	@Autowired
	@Qualifier("evaluatorService")
	private EvaluatorService evaluatorService;	
	
	@Autowired
	@Qualifier("graphvizDotGeneratorService")
	private GraphvizDotGeneratorService graphvizDotGeneratorService;
	
	
	/**
	 * Use this test to constuct a plot that shows the resulting posterior probability 
	 * 
	 * @throws ExecutionException
	 * @throws IOException
	 */
	
	@Test
	public void testGeneratePlotDataForEditDistance() throws ExecutionException, IOException {
		
		//Build the filePath to store the posteriors for the PMFs
		StringBuilder filePath = new StringBuilder("./src/test/resources/other/");
		
		//In this example the method output the P(c1=c2 | SB)
		String fileName = "SB_class_prior";
		filePath.append(fileName).append(".csv");
			
		//Write results in a csv file				
		File loc = new File(filePath.toString());	
		BufferedWriter out = new BufferedWriter(new FileWriter(loc, true));
		
		//Write header for file
		out.append("prior,prosterior(no_laplace),prosterior(with_laplace)").append("\n");;
 	    
	    /**
	     * Generate data for the plot
	     */
		//for each prior
		double step = 0.01;
		
        for (double i=0; i<1; i=i+step) {
        	//calculate posterior p(c1=c2) without Laplace
        	
        	//The best combination so far is 50 / 396, 86 / 2552
        	
            double l = BayesianTheorem.likelihoodWithLaplace(20, 396, 0, 2);
            double nl = BayesianTheorem.likelihoodWithLaplace(6, 2552, 0, 2);
                     	            
            BayesianTheorem bayesNoLaplace = new BayesianTheorem(l, i, nl);
            
        	//calculate posterior p(c1=c2) with Laplace            
            double l_laplace = BayesianTheorem.likelihoodWithLaplace(20, 396, 1, 2);
            double nl_laplace = BayesianTheorem.likelihoodWithLaplace(8, 2552, 1, 2);
                        
            
            BayesianTheorem bayesWithLaplace = new BayesianTheorem(l_laplace, i, nl_laplace);           
            
            out.append("" + i + "," + bayesNoLaplace.calcPosterior() + "," + bayesWithLaplace.calcPosterior()).append("\n");	            
        }//end for

	     
	    //Force changes to the file 
		if (out != null) {
			out.close();
		}//end if  
				
		//Use Gnuplot to output the result in .eps
		this.graphvizDotGeneratorService.plotPosteriorsPMF(fileName, "eps");
	}//end testGeneratePlotDataForEditDistance()
	
	
	
	
	
	
	
	
	
	
	/***
	 * Here I need to have a method that passes from the list PMFs and reads the contingency tables.
	 * It will then output for each semantic evidence the data needed for constructing the resulting 
	 * posterior probability plots. Like the ones I have used for the ISWC paper.
	 */
	@Test
	public void testGeneratePlotDataForAllPMFfromTDBStore() throws ExecutionException, IOException {
		
		//Build the filePath to store the posteriors for the PMFs
		StringBuilder filePath = new StringBuilder("./src/test/resources/other/");
		
		//SemEvidenceTrainingUtilImpl trainService = new SemEvidenceTrainingUtilImpl(tdbStore, DATA_ANALYSIS, EVID_CLASSES,
			//																		EVID_PROPS, ENDPOINT_DATA);
				
		//Create the likelihoods for Classes
		//trainService.createTrainingSetClasses(false);	

		//Create the likelihoods for Props
	//	trainService.createTrainingSetProps(false);	
				
		
		
	}//end testGeneratePlotDataForAllPMFfromTDBStore()
	
}//CalculatePosteriorsForMatchersTest()