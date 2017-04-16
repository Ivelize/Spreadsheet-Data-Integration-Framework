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
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.distributions.ProbabilityDensityFunction;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.AbstractKernelFunction;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.GaussianKernel;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.KernelCaseType;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.KernelDenstityEstimator;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.KernelEstimatorType;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.LogTransformation;
import uk.ac.manchester.dstoolkit.service.meta.DataSourceService;
import uk.ac.manchester.dstoolkit.service.meta.SchemaService;
import uk.ac.manchester.dstoolkit.service.util.ExternalDataSourcePoolUtilService;
import uk.ac.manchester.dstoolkit.service.util.importexport.GraphvizDotGeneratorService;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.LoadRDFSourceUtilService;
import uk.ac.manchester.dstoolkit.service.util.training.EvaluatorService;

public class CalculatePosteriorsForMatchersTest extends RDFAbstractInitialisation {

	private static Logger logger = Logger.getLogger(CalculatePosteriorsForMatchersTest.class);
	
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
	
	
	@Test
	public void testGeneratePlotDataForEditDistance() throws ExecutionException, IOException {

		//Write results in a csv file 
		File loc = new File("./src/test/resources/other/edit_distance_posteriors.csv");	
		BufferedWriter out = new BufferedWriter(new FileWriter(loc, true));
		
		//Write header for file
		out.append("prior,sim_score,posterior").append("\n");;
		
		/**
		 * Setup the Probability Distribution that corresponds to the behaviour of the Edit-distance matcher.
		 * Then use the probability distribution to estimate the integrals needed to estimate the likelihood 
		 * and negative likelihood that are needed for the calculation of the Bayesian theorem.
		 */
		//Kernel Density Estimation can be configured whether the Kernel has support or not				
		KernelDenstityEstimator kde_edit_tp  = null;
		KernelDenstityEstimator kde_edit_fp  = null;
		//Transformation function for the kde with support
		LogTransformation logTransformation = null;
		//Choose a kernel for KDE
		AbstractKernelFunction kernel = new GaussianKernel();
		
		//KDE with support - TP Case
		double h_tp = 0.609385;	
		logTransformation = new LogTransformation(-0.1, 1.1); //Transformation function to be used for the support 
		kde_edit_tp = new KernelDenstityEstimator(KernelEstimatorType.LEVENSHTEIN_KDE, kernel, h_tp, logTransformation);
		kde_edit_tp.readDataPointsFromLocation("./src/test/resources/training/kernel/tp_Levenshtein.dat");
		ProbabilityDensityFunction pdfTP = new ProbabilityDensityFunction(0, 1, kde_edit_tp, KernelCaseType.TP_CASE);
		pdfTP.createIntegralVector();
						
		
		//KDE with support - FP Case
		double h_fp = 0.27;					
		kde_edit_fp = new KernelDenstityEstimator(KernelEstimatorType.LEVENSHTEIN_KDE, kernel, h_fp, logTransformation);
		kde_edit_fp.readDataPointsFromLocation("./src/test/resources/training/kernel/fp_Levenshtein.dat");
		ProbabilityDensityFunction pdfFP = new ProbabilityDensityFunction(0, 1, kde_edit_fp, KernelCaseType.FP_CASE);
		pdfFP.createIntegralVector();
	 	    
	    /**
	     * Generate data for the plot
	     */
		//for each prior
	     for (double i=0; i<1; i=i+0.1) {
	        //for each similarity score
	        for (double j=0; j<1; j=j+0.1) {
	        	//calculate posterior p(c1=c2)
	            double l = pdfTP.approximateIntegralWithWidthSmart(j, 0.1);
	            double nl = pdfFP.approximateIntegralWithWidthSmart(j, 0.1);
	            	            
	            BayesianTheorem bayesTheorem = new BayesianTheorem(l, i, nl);
	            out.append("" + i + "," + j + "," + bayesTheorem.calcPosterior()).append("\n");	            
	        }//end for
	     }//end for	
	     
	    //Force changes to the file 
		if (out != null) {
			out.close();
		}//end if  
	}//end testGeneratePlotDataForEditDistance()
	
	@Test
	public void testGeneratePlotDataForNGram() throws ExecutionException, IOException {

		//Write results in a csv file 
		File loc = new File("./src/test/resources/other/nGram_posteriors.csv");	
		BufferedWriter out = new BufferedWriter(new FileWriter(loc, true));
		
		//Write header for file
		out.append("prior,sim_score,posterior").append("\n");;
		
		/**
		 * Setup the Probability Distribution that corresponds to the behaviour of the Edit-distance matcher.
		 * Then use the probability distribution to estimate the integrals needed to estimate the likelihood 
		 * and negative likelihood that are needed for the calculation of the Bayesian theorem.
		 */
		//Kernel Density Estimation can be configured whether the Kernel has support or not				
		KernelDenstityEstimator kde_ngram_tp = null;
		KernelDenstityEstimator kde_ngram_fp = null;
		//Transformation function for the kde with support
		LogTransformation logTransformation = null;
		//Choose a kernel for KDE
		AbstractKernelFunction kernel = new GaussianKernel();
		
		//KDE - TP Case
		double h_tp = 0.588729;
		logTransformation = new LogTransformation(-0.1, 1.1);
		kde_ngram_tp = new KernelDenstityEstimator(KernelEstimatorType.NGRAM_KDE, kernel, h_tp, logTransformation);
		kde_ngram_tp.readDataPointsFromLocation("./src/test/resources/training/kernel/tp_NGram.dat");
		ProbabilityDensityFunction pdfTP = new ProbabilityDensityFunction(0, 1, kde_ngram_tp, KernelCaseType.TP_CASE);
		pdfTP.createIntegralVector();
		
		//KDE - FP Case
		double h_fp = 0.816656;
		kde_ngram_fp = new KernelDenstityEstimator(KernelEstimatorType.NGRAM_KDE, kernel, h_fp, logTransformation);
		kde_ngram_fp.readDataPointsFromLocation("./src/test/resources/training/kernel/fp_NGram.dat");
		ProbabilityDensityFunction pdfFP = new ProbabilityDensityFunction(0, 1, kde_ngram_fp, KernelCaseType.FP_CASE);
		pdfFP.createIntegralVector();
	 	    
	    /**
	     * Generate data for the plot
	     */
		//for each prior
	     for (double i=0; i<1; i=i+0.1) {
	        //for each similarity score
	        for (double j=0; j<1; j=j+0.1) {
	        	//calculate posterior p(c1=c2)
	            double l = pdfTP.approximateIntegralWithWidthSmart(j, 0.1);
	            double nl = pdfFP.approximateIntegralWithWidthSmart(j, 0.1);
	            	            
	            BayesianTheorem bayesTheorem = new BayesianTheorem(l, i, nl);
	            out.append("" + i + "," + j + "," + bayesTheorem.calcPosterior()).append("\n");	            
	        }//end for
	     }//end for	
	     
	    //Force changes to the file 
		if (out != null) {
			out.close();
		}//end if  
	}//end testGeneratePlotDataForEditDistance()	
}//CalculatePosteriorsForMatchersTest()