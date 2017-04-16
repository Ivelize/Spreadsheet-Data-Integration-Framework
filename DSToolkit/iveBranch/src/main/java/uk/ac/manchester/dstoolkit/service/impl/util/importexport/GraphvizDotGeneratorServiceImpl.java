package uk.ac.manchester.dstoolkit.service.impl.util.importexport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.annotation.Annotation;
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelProperty;
import uk.ac.manchester.dstoolkit.domain.models.canonical.ConstructType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.JoinOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.MappingOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.ReduceOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.RenameOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.ScanOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.SetOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.OneToOneMatching;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.domain.user.User;
import uk.ac.manchester.dstoolkit.exceptions.DSToolkitConfigException;
import uk.ac.manchester.dstoolkit.repository.annotation.AnnotationRepository;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherInfo;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.ExpectationMatrixEntry;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixCellIndex;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixEntry;
import uk.ac.manchester.dstoolkit.service.impl.util.benchmark.DampeningEffectPolicy;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.BooleanVariables;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.BayesEntry;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.ErrorMeasures;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.distributions.ProbabilityMassFunction;
import uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation.PerformanceErrorTypes;
import uk.ac.manchester.dstoolkit.service.util.importexport.GraphvizDotGeneratorService;

/**
 * @author chedeler
 * @author klitos
 * 
 * Revision (klitos):
 *  1. Add constructor that loads Graphviz property file.
 *  2. Additional generateDot() methods. 
 *  3. Read/Write a graph in DOT language.
 *  4. Export a graph in DOT language as a PNG. 
 */
@Service(value = "graphvizDotGeneratorService")
public class GraphvizDotGeneratorServiceImpl implements GraphvizDotGeneratorService {

	private static Logger logger = Logger.getLogger(GraphvizDotGeneratorServiceImpl.class);

	/*Configuration file*/
	private static String dotProperties = "./src/main/resources/graphviz.properties";
	
	/*The source of the graph written in dot language.*/
	private StringBuilder generalGraph = new StringBuilder();
	
	/*Graphviz dot binary location*/
	private static String GRAPHVIZ_OUTPUT;
	private static String GRAPHVIZ_DOT_WIN;
	private static String GRAPHVIZ_DOT_LINUX_MAC;
	
	private boolean SHOW_LABELS = true;
	
	@Autowired
	@Qualifier("annotationRepository")
	private AnnotationRepository annotationRepository;
	
	/*Constructor*/
	public GraphvizDotGeneratorServiceImpl() {
		/*Load graphviz.properties configuration*/
		loadConfiguration(dotProperties);
		
		/*Create a new directory with timestamp to hold the results for each run*/
		GRAPHVIZ_OUTPUT = this.createDirWithTimestamp(GRAPHVIZ_OUTPUT);
	}
	
	/***
	 * Create a dir with a timestamp to hold the results
	 * 
	 * @param dirPath
	 * @return
	 */
	private String createDirWithTimestamp(String dirPath) {
		
		String result = "";
		
		Date date = new Date() ;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss") ;
		File theDir = new File(dirPath + "/" + dateFormat.format(date)) ;
				
		//if the directory does not exist, create it
		if (!theDir.exists()) {
			if(theDir.mkdir()) {
				result = theDir.getAbsolutePath();
			}
		} else {
			if (theDir.exists()) {
				result = theDir.getAbsolutePath();
			}			
		}
		
		return result;	
		
	}//end method
	
	
	
	
	
	/**
	 * Print a Contingency table to calculate the Probability Mass Functions (PMFs)
	 * 
	 * @param pmfList
	 * @param fileName
	 */
	public void generateDOTContingencyTable(Map<BooleanVariables, ProbabilityMassFunction> pmfList, String fileName) {
		StringBuilder stringBuilder = new StringBuilder();	
		
		//TODO complete this method stub
		
		File target = exportAsDOTFile(stringBuilder.toString(), "SyntacticMatrices", fileName);	
		this.exportDOT2PNG(target, "png", fileName, null);	
	}//end generateDOTContingencyTable()	
	
	
	/***
	 * OUTPUT: Takes as input a Single syntactic matrix and then it outputs it as a matrix representation.
	 * Location of output: ../SyntacticMatrices
	 *  
	 * This is used to output a single syntactic matrix as a PNG image. 
	 * @param fileName - the name to be attached at the start of each graph name
	 */
	public void generateDOT(Schema sourceSchema, Schema targetSchema, float[][] syntacticSimMatrix, String fileName) {

			int countMatches = 0;
			StringBuilder stringBuilder = new StringBuilder();		
			
			//Get the contructs from the Schemata
			ArrayList<CanonicalModelConstruct> sourceConstructs = getConstructs(sourceSchema.getCanonicalModelConstructs());
			ArrayList<CanonicalModelConstruct> targetConstructs = getConstructs(targetSchema.getCanonicalModelConstructs());
						
			//Create HEADER
			stringBuilder.append("graph synt_matrix {").append("\n");		
			stringBuilder.append("rankdir=LR;").append("\n");			
			stringBuilder.append("node [shape=plaintext, fontsize=12, fontname=arial];").append("\n");
			stringBuilder.append("Table [label = <").append("\n");
			stringBuilder.append("<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"3\">").append("\n");
			stringBuilder.append("  <tr>").append("\n");
			stringBuilder.append("    <TD></TD>").append("\n");
			//Create the Columns first
			for (CanonicalModelConstruct construct2 : targetConstructs) {
				stringBuilder.append("    <td>").append(construct2.getName()).append("</td> ").append("\n");
			}//end for
			stringBuilder.append("  </tr>").append("\n");
			
			for (CanonicalModelConstruct construct1 : sourceConstructs) {
				stringBuilder.append("  <tr>").append("\n");
				stringBuilder.append("    <td ALIGN=\"left\">").append(construct1.getName()).append("</td>").append("\n");
				for (CanonicalModelConstruct construct2 : targetConstructs) {
					/*Get the [row][column] position to add the cell entry to*/
					int rowIndex = sourceConstructs.indexOf(construct1);
					int colIndex = targetConstructs.indexOf(construct2);
					float cellValue = syntacticSimMatrix[rowIndex][colIndex];	
					if (cellValue != 0.0F) {
						countMatches = countMatches + 1;
						stringBuilder.append("    <td BGCOLOR=\"yellow\">").append(cellValue).append("</td>").append("\n");
					} else
						stringBuilder.append("    <td>").append(cellValue).append("</td>").append("\n");			
				}//end inner for
				stringBuilder.append("  </tr>").append("\n");
			}//end for	
			
			stringBuilder.append("</TABLE>>];\n");
			stringBuilder.append("}");
			stringBuilder.append("//------------------------------//").append("\n");
			stringBuilder.append("//Count Matches Discovered: ").append(countMatches).append("\n");			
			stringBuilder.append("//------------------------------//").append("\n");
		

		File target = exportAsDOTFile(stringBuilder.toString(), "SyntacticMatrices", fileName);	
		this.exportDOT2PNG(target, "png", fileName, null);	
	}//end generateDOT()
	
	
	/***
	 * OUTPUT: Takes as input a Single syntactic matrix and then it outputs it as a matrix representation.
	 * Location of output: ../SyntacticMatrices
	 *  
	 * This is used to output a single syntactic matrix as a PNG image. 
	 * @param fileName - the name to be attached at the start of each graph name
	 */
	public void generateDOT(List<CanonicalModelConstruct> sourceConstructs,	List<CanonicalModelConstruct> targetConstructs,
			float[][] syntacticSimMatrix, String fileName) {

			int countMatches = 0;
			StringBuilder stringBuilder = new StringBuilder();		
			
			//Create HEADER
			stringBuilder.append("graph synt_matrix {").append("\n");		
			stringBuilder.append("rankdir=LR;").append("\n");			
			stringBuilder.append("node [shape=plaintext, fontsize=12, fontname=arial];").append("\n");
			stringBuilder.append("Table [label = <").append("\n");
			stringBuilder.append("<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"3\">").append("\n");
			stringBuilder.append("  <tr>").append("\n");
			stringBuilder.append("    <TD></TD>").append("\n");
			//Create the Columns first
			for (CanonicalModelConstruct construct2 : targetConstructs) {
				stringBuilder.append("    <td>").append(construct2.getName()).append("</td> ").append("\n");
			}//end for
			stringBuilder.append("  </tr>").append("\n");
			
			for (CanonicalModelConstruct construct1 : sourceConstructs) {
				stringBuilder.append("  <tr>").append("\n");
				stringBuilder.append("    <td ALIGN=\"left\">").append(construct1.getName()).append("</td>").append("\n");
				for (CanonicalModelConstruct construct2 : targetConstructs) {
					/*Get the [row][column] position to add the cell entry to*/
					int rowIndex = sourceConstructs.indexOf(construct1);
					int colIndex = targetConstructs.indexOf(construct2);
					float cellValue = syntacticSimMatrix[rowIndex][colIndex];	
					if (cellValue != 0.0F) {
						countMatches = countMatches + 1;
						stringBuilder.append("    <td BGCOLOR=\"yellow\">").append(cellValue).append("</td>").append("\n");
					} else
						stringBuilder.append("    <td>").append(cellValue).append("</td>").append("\n");			
				}//end inner for
				stringBuilder.append("  </tr>").append("\n");
			}//end for	
			
			stringBuilder.append("</TABLE>>];\n");
			stringBuilder.append("}");
			stringBuilder.append("//------------------------------//").append("\n");
			stringBuilder.append("//Count Matches Discovered: ").append(countMatches).append("\n");			
			stringBuilder.append("//------------------------------//").append("\n");
		

		File target = exportAsDOTFile(stringBuilder.toString(), "SyntacticMatrices", fileName);	
		this.exportDOT2PNG(target, "png", fileName, null);	
	}//end generateDOT()

	/***
	 * OUTPUT: Takes as input a collection of Syntactic Matrices and then it outputs each one of them in a matrix representation.
	 * Location of output: ../SyntacticMatrices
	 *  
	 * This is used to output a collection of syntactic matrices as PNG images. 
	 */
	public void generateDOTSyn(List<CanonicalModelConstruct> sourceConstructs,	List<CanonicalModelConstruct> targetConstructs,
			List<MatcherInfo> simCubeOfMatchers) {

		for (MatcherInfo matrix : simCubeOfMatchers) {
			int countMatches = 0;
			StringBuilder stringBuilder = new StringBuilder();		
					
			//Create HEADER
			stringBuilder.append("//------------------------------//").append("\n");
			stringBuilder.append("//Matcher Name: ").append(matrix.getMatcherName()).append("\n");			
			stringBuilder.append("//------------------------------//").append("\n");	
			stringBuilder.append("graph synt_matrix {").append("\n");		
			stringBuilder.append("rankdir=LR;").append("\n");			
			stringBuilder.append("node [shape=plaintext, fontsize=12, fontname=arial];").append("\n");
			stringBuilder.append("Table [label = <").append("\n");
			stringBuilder.append("<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"3\">").append("\n");
			stringBuilder.append("  <tr>").append("\n");
			stringBuilder.append("    <TD></TD>").append("\n");
			//Create the Columns first
			for (CanonicalModelConstruct construct2 : targetConstructs) {
				stringBuilder.append("    <td>").append(construct2.getName()).append("</td> ").append("\n");
			}//end for
			stringBuilder.append("  </tr>").append("\n");
			
			//Get the 2d similarity matrix for this matcher
			float[][] syntacticSimMatrix = matrix.getSimMatrix();
						
			for (CanonicalModelConstruct construct1 : sourceConstructs) {
				stringBuilder.append("  <tr>").append("\n");
				stringBuilder.append("    <td ALIGN=\"left\">").append(construct1.getName()).append("</td>").append("\n");
				for (CanonicalModelConstruct construct2 : targetConstructs) {
					/*Get the [row][column] position to add the cell entry to*/
					int rowIndex = sourceConstructs.indexOf(construct1);
					int colIndex = targetConstructs.indexOf(construct2);
					float cellValue = syntacticSimMatrix[rowIndex][colIndex];					
					if (cellValue != 0.0F ) {
						countMatches = countMatches + 1;
						stringBuilder.append("    <td BGCOLOR=\"yellow\">").append(cellValue).append("</td>").append("\n");
					} else
						stringBuilder.append("    <td>").append(cellValue).append("</td>").append("\n");		
				}//end inner for
				stringBuilder.append("  </tr>").append("\n");
			}//end for	
			
			stringBuilder.append("</TABLE>>];\n");
			stringBuilder.append("}");
			stringBuilder.append("//------------------------------//").append("\n");
			stringBuilder.append("//Count Matches Discovered: ").append(countMatches).append("\n");			
			stringBuilder.append("//------------------------------//").append("\n");
		
		 File target = exportAsDOTFile(stringBuilder.toString(), "SyntacticMatrices", null);	
		 this.exportDOT2PNG(target, "png", null, null);	
		}//end for
	}//end generateDOT()	
	
	/***
	 * OUTPUT: Takes as input a collection of Semantic Matrices and then it outputs each one of them as a matrix representation.
	 * Location of output: ../SemanticMatrices
	 *  
	 * This is used to output a collection of semantic matrices as PNG images. 
	 */
	public void generateDOTSem(List<CanonicalModelConstruct> sourceConstructs, List<CanonicalModelConstruct> targetConstructs,
							List<SemanticMatrix> semMatrixCube) {
		
		for (SemanticMatrix matrix : semMatrixCube) {
			StringBuilder stringBuilder = new StringBuilder();		
			int countAnnotationsFound = 0;		
			
			//Create HEADER
			stringBuilder.append("//------------------------------//").append("\n");
			stringBuilder.append("//Semantic Matrix Type: ").append(matrix.getType().toString()).append("\n");			
			stringBuilder.append("//Number of rows: ").append(matrix.getNoRows()).append("\n");	
			stringBuilder.append("//Number of columns: ").append(matrix.getNoColumns()).append("\n");		
			stringBuilder.append("//------------------------------//").append("\n");		
			stringBuilder.append("graph sem_matrix {").append("\n");		
			stringBuilder.append("rankdir=LR;").append("\n");			
			stringBuilder.append("node [shape=plaintext, fontsize=12, fontname=arial];").append("\n");
			stringBuilder.append("Table [label = <").append("\n");
			stringBuilder.append("<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"3\">").append("\n");
			stringBuilder.append("  <tr>").append("\n");
			stringBuilder.append("    <TD></TD>").append("\n");
			//Create the Columns first
			for (CanonicalModelConstruct construct2 : targetConstructs) {
				stringBuilder.append("    <td>").append(construct2.getName()).append("</td> ").append("\n");
			}//end for
			stringBuilder.append("  </tr>").append("\n");
			
			for (CanonicalModelConstruct construct1 : sourceConstructs) {
				stringBuilder.append("  <tr>").append("\n");
				stringBuilder.append("    <td ALIGN=\"left\">").append(construct1.getName()).append("</td>").append("\n");
				for (CanonicalModelConstruct construct2 : targetConstructs) {
					/*Get the [row][column] position to add the cell entry to*/
					int rowIndex = sourceConstructs.indexOf(construct1);
					int colIndex = targetConstructs.indexOf(construct2);
					SemanticMatrixEntry cell = matrix.getCellSemanticEntry(rowIndex, colIndex);
					String cellValue = "N";
					if (cell != null) {						
    						//if is null then search if it has a list of boolean variables
							if (!cell.isCellValueListEmpty()) {
								Set<BooleanVariables> listOfBoolVars = cell.getCellValue();
								countAnnotationsFound = countAnnotationsFound + 1;
								stringBuilder.append("    <td BGCOLOR=\"yellow\">");
									String dirSymbol = cell.getDirSymbol();
									if (dirSymbol != null) {									
										stringBuilder.append(listOfBoolVars.toString() + " " + dirSymbol);
									} else {
										stringBuilder.append(listOfBoolVars.toString());
									}
								stringBuilder.append("</td>").append("\n");								
							}//end if
	
					} else {
						stringBuilder.append("    <td>").append(cellValue).append("</td>").append("\n");
					}
				}//end inner for
				stringBuilder.append("  </tr>").append("\n");
			}//end for	
			
			stringBuilder.append("</TABLE>>];\n");
			stringBuilder.append("}");
			stringBuilder.append("//------------------------------//").append("\n");
			stringBuilder.append("//Count Semantic Annotations Discovered: ").append(countAnnotationsFound).append("\n");			
			stringBuilder.append("//------------------------------//").append("\n");
		
		 File target = exportAsDOTFile(stringBuilder.toString(), "SemanticMatrices", null);	
		 this.exportDOT2PNG(target, "png", null, null);
		}//end for 		 
	}//end generateDOT()	
	
	/***
	 * OUTPUT: Takes as input Semantic Matrix that holds info about the Expectation and then it outputs it in a matrix representation.
	 * Location of output: ../ExpectationMatrix
	 *  
	 * This is used to output an Expectation Matrix as a PNG image. 
	 */
	public void expectationMatrixDOTSem(List<CanonicalModelConstruct> sourceConstructs, List<CanonicalModelConstruct> targetConstructs,
										SemanticMatrix matrix, boolean perc) {
		StringBuilder stringBuilder = new StringBuilder();		
		int countAnnotationsFound = 0;	
		
		//Create HEADER
		stringBuilder.append("//------------------------------//").append("\n");
		stringBuilder.append("//Semantic Matrix Type: ").append(matrix.getType().toString()).append("\n");			
		stringBuilder.append("//Number of rows: ").append(matrix.getNoRows()).append("\n");	
		stringBuilder.append("//Number of columns: ").append(matrix.getNoColumns()).append("\n");		
		stringBuilder.append("//------------------------------//").append("\n");		
		stringBuilder.append("graph sem_matrix {").append("\n");		
		stringBuilder.append("rankdir=LR;").append("\n");			
		stringBuilder.append("node [shape=plaintext, fontsize=12, fontname=arial];").append("\n");
		stringBuilder.append("Table [label = <").append("\n");
		stringBuilder.append("<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"3\">").append("\n");
		stringBuilder.append("  <tr>").append("\n");
		stringBuilder.append("    <TD></TD>").append("\n");
		//Create the Columns first
		for (CanonicalModelConstruct construct2 : targetConstructs) {
			stringBuilder.append("    <td>").append(construct2.getName()).append("</td> ").append("\n");
		}//end for
		stringBuilder.append("  </tr>").append("\n");
		
		for (CanonicalModelConstruct construct1 : sourceConstructs) {
			stringBuilder.append("  <tr>").append("\n");
			stringBuilder.append("    <td ALIGN=\"left\">").append(construct1.getName()).append("</td>").append("\n");
			for (CanonicalModelConstruct construct2 : targetConstructs) {
				/*Get the [row][column] position to add the cell entry to*/
				int rowIndex = sourceConstructs.indexOf(construct1);
				int colIndex = targetConstructs.indexOf(construct2);
				ExpectationMatrixEntry cell = (ExpectationMatrixEntry) matrix.getCellSemanticEntry(rowIndex, colIndex);
				String cellValue = "0";
				if (cell != null) {

					countAnnotationsFound = countAnnotationsFound + 1;
					
					if (perc) {
						cellValue = "" + ((ExpectationMatrixEntry) cell).getCellScoreAsPerc();
						stringBuilder.append("    <td BGCOLOR=\"yellow\">").append(cellValue).append(" %");
						stringBuilder.append("</td>").append("\n");	
					} else {
						cellValue = "" + ((ExpectationMatrixEntry) cell).getCellScore();
						stringBuilder.append("    <td BGCOLOR=\"yellow\">").append(cellValue).append(" ");
						stringBuilder.append("</td>").append("\n");	
					}				
					
				} else {
					stringBuilder.append("    <td>").append(cellValue).append("</td>").append("\n");
				}
			}//end inner for
			stringBuilder.append("  </tr>").append("\n");
		}//end for	
		
		stringBuilder.append("</TABLE>>];\n");
		stringBuilder.append("}");
		stringBuilder.append("//------------------------------//").append("\n");
		stringBuilder.append("//Count Annotations Discovered: ").append(countAnnotationsFound).append("\n");			
		stringBuilder.append("//------------------------------//").append("\n");
	
	 File target = exportAsDOTFile(stringBuilder.toString(), "ExpectationMatrix", null);	
	 this.exportDOT2PNG(target, "png", null, null);
	}	

	/***
	 * Bayes Inference: Generate Dot to view the Posteriors
	 *	  
	 * 
	 * @param perc - output results as percentage
	 */
	public void generateDOTBayes(List<CanonicalModelConstruct> sourceConstructs, List<CanonicalModelConstruct> targetConstructs,
								 SemanticMatrix matrix, List<MatcherInfo> syntacticCube, Set<BooleanVariables> evidencesToAccumulate,  boolean perc) {
		
		//Highlight cells that have accumulated both syntactic and semantic evidences
		Set<SemanticMatrixCellIndex> iSet = matrix.getIndexesSet();
		
		StringBuilder stringBuilder = new StringBuilder();		
		int countPosteriors = 0;		

		//Create HEADER
		stringBuilder.append("//------------------------------//").append("\n");
		stringBuilder.append("//Bayes Matrix Type: ").append(matrix.getType().toString()).append("\n");			
		stringBuilder.append("//Number of rows: ").append(matrix.getNoRows()).append("\n");	
		stringBuilder.append("//Number of columns: ").append(matrix.getNoColumns()).append("\n");
		
		if (evidencesToAccumulate != null) {
			stringBuilder.append("//Evidences: ").append(evidencesToAccumulate.toString()).append("\n");	
		}
		
		stringBuilder.append("//------------------------------//").append("\n");		
		stringBuilder.append("graph sem_matrix {").append("\n");
		
		if (evidencesToAccumulate != null) {
			stringBuilder.append("label=\"").append(evidencesToAccumulate.toString()).append("\";").append("\n");	
		} else if (syntacticCube != null) {			
			//Create the name of the matchers or matcher
			String matchersString = "";
			for (MatcherInfo info : syntacticCube) {
				
				if (syntacticCube.indexOf(info) == (syntacticCube.size() - 1) ) {
					matchersString = matchersString + info.getMatcherType().toString();
				} else {				
					matchersString = matchersString + info.getMatcherType().toString() + " & ";
				}
			}//end for
			
			stringBuilder.append("label=\"").append(matchersString).append("\";").append("\n");		
		}//end else		
		
		stringBuilder.append("rankdir=LR;").append("\n");			
		stringBuilder.append("node [shape=plaintext, fontsize=12, fontname=arial];").append("\n");
		stringBuilder.append("Table [label = <").append("\n");
		stringBuilder.append("<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"3\">").append("\n");
		stringBuilder.append("  <tr>").append("\n");
		stringBuilder.append("    <TD></TD>").append("\n");
		//Create the Columns first
		for (CanonicalModelConstruct construct2 : targetConstructs) {
			stringBuilder.append("    <td>").append(construct2.getName()).append("</td> ").append("\n");
		}//end for
		stringBuilder.append("  </tr>").append("\n");

		for (CanonicalModelConstruct construct1 : sourceConstructs) {
			stringBuilder.append("  <tr>").append("\n");
			stringBuilder.append("    <td ALIGN=\"left\">").append(construct1.getName()).append("</td>").append("\n");
			for (CanonicalModelConstruct construct2 : targetConstructs) {
				/*Get the [row][column] position to add the cell entry to*/
				int rowIndex = sourceConstructs.indexOf(construct1);
				int colIndex = targetConstructs.indexOf(construct2);
				BayesEntry cell = (BayesEntry) matrix.getCellSemanticEntry(rowIndex, colIndex);
			
				String cellValue = "N";
				if (cell != null) {						
					countPosteriors = countPosteriors + 1; 					

					//Highlight the cells that have both syn and sem evidences applied on them
					if (iSet != null) {
						
						if (matrix.setContainsIndex(rowIndex, colIndex)) {						
							if (perc) {
								stringBuilder.append("    <td BGCOLOR=\"yellow\">").append(cell.getLastPosteriorAsPerc()).append(" %").append("</td>").append("\n");
							} else {
								stringBuilder.append("    <td BGCOLOR=\"yellow\">").append(cell.getLastPosterior()).append("</td>").append("\n");
							}
						} else {
							if (perc) {
								stringBuilder.append("    <td>").append(cell.getLastPosteriorAsPerc()).append(" %").append("</td>").append("\n");
							} else {
								stringBuilder.append("    <td>").append(cell.getLastPosterior()).append("</td>").append("\n");
							}
						}
					} else {
						if (perc) {
							stringBuilder.append("    <td>").append(cell.getLastPosteriorAsPerc()).append(" %").append("</td>").append("\n");
						} else {
							stringBuilder.append("    <td>").append(cell.getLastPosterior()).append("</td>").append("\n");
						}						
					}//end else
				} else {
					stringBuilder.append("    <td>").append(cellValue).append("</td>").append("\n");
				}
			}//end inner for
			stringBuilder.append("  </tr>").append("\n");
		}//end for	

		stringBuilder.append("</TABLE>>];\n");
		stringBuilder.append("}");
		stringBuilder.append("//------------------------------//").append("\n");
		stringBuilder.append("//Bayes Posteriors Found: ").append(countPosteriors).append("\n");			
		stringBuilder.append("//------------------------------//").append("\n");

		File target = exportAsDOTFile(stringBuilder.toString(), "BayesMatrices", null);	
		this.exportDOT2PNG(target, "png", null, null); 
	}//end generateDOTBayes()	
	
	
	/**
	 * This method outputs schema as a list using Graphviz dot language.
	 * 
	 * @param schema  - Supermodel schema.
	 * @param align   - Specify alignment of constructs values ("horizontal, vertical").
	 * @param showURI - Show UIRs if exist (true/false).
 	 * @return String representation in the DOT specification language that shows schema constructs as a list
 	 * with their URIs if option is selected.
 	 * 
 	 * TODO: check if URIs exists or not before printing.
	 */
	public String generateDot(Schema schema, String align, boolean showURI, boolean showStatus) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("graph schema {").append("\n");		
		if (align.toLowerCase().equals("vertical")){
			stringBuilder.append("rankdir=LR;").append("\n");			
		}		
		//stringBuilder.append("ranksep=0.5;").append("\n");
		stringBuilder.append("node [shape=plaintext, fontsize=12, fontname=arial];").append("\n");
		//Get the name of the data source
		String dsName = schema.getDataSource().getName();
		
		Set<SuperAbstract> superAbstracts = schema.getSuperAbstracts();
		List<SuperAbstract> orderedSuperAbstracts = this.quickSortSuperAbstractsByNumberOfSuperLexicals(new ArrayList<SuperAbstract>(superAbstracts),
				false);
		
		for (SuperAbstract sa : orderedSuperAbstracts) {
			stringBuilder.append(dsName).append("_").append(sa.getName()).append(" ");
			stringBuilder.append("[label = <").append("\n");
			stringBuilder.append("<TABLE BORDER=\"0\" CELLBORDER=\"0\" CELLSPACING=\"0\" CELLPADDING=\"3\">").append("\n");
			stringBuilder.append("  <tr>").append("\n");
			stringBuilder.append("    <td ALIGN=\"left\" COLSPAN=\"3\"><B>").append(sa.getName()).append("</B></td>").append("\n");
			if (showURI) { 
			 stringBuilder.append("    <td ALIGN=\"left\">").append(getConstructURI_SA(sa)).append("</td>").append("\n");			
			} else {
			 stringBuilder.append("    <td>").append("").append("</td>").append("\n");		
			}
			if (showStatus) { 
				 stringBuilder.append("    <td ALIGN=\"left\">").append(" | " + getConstructDereferenceStatus(sa)).append("</td>").append("\n");			
			} else {
				 stringBuilder.append("    <td>").append("").append("</td>").append("\n");		
			}
			stringBuilder.append("  </tr>").append("\n");			
			//Add the SuperLexicals for this SA
			for (SuperLexical sl : sa.getSuperLexicals()) {
				stringBuilder.append("  <tr>").append("\n");
				stringBuilder.append("    <td></td>").append("\n");				
				stringBuilder.append("    <td ALIGN=\"left\">").append(sl.getName()).append("</td>").append("\n");
				stringBuilder.append("    <td></td>").append("\n");	
				if (showURI) { 
				 stringBuilder.append("    <td ALIGN=\"left\">").append(getConstructURI_SL(sl)).append("</td>").append("\n");			
				} else {
				 stringBuilder.append("    <td>").append("").append("</td>").append("\n");		
				}
				if (showStatus) { 
					 stringBuilder.append("    <td ALIGN=\"left\">").append(" | " + getConstructDereferenceStatus(sl)).append("</td>").append("\n");			
				} else {
					 stringBuilder.append("    <td>").append("").append("</td>").append("\n");		
				}
				stringBuilder.append("  </tr>").append("\n");				
			}//end for			
			stringBuilder.append("</TABLE>>];\n");			
		}//end for		
		stringBuilder.append("}");
		return stringBuilder.toString();
	}//end generateDot()	
	
	public String getConstructURI_SA(SuperAbstract sa) {	
		Set<CanonicalModelProperty> propertySet = sa.getProperties();
		String constructURI = null;
		for (CanonicalModelProperty property_element : propertySet) {
			if (property_element.getName().equals("rdfTypeValue")) {
				constructURI = property_element.getValue();
				break;
			}	
		}//end for	
		return constructURI;
	}//end getConstructURI_SA()
	
	public String getConstructURI_SL(SuperLexical sl) {	
		Set<CanonicalModelProperty> propertySet = sl.getProperties();
		String constructURI = null;
		for (CanonicalModelProperty property_element : propertySet) {
			if (property_element.getName().equals("constructURI")) {
				constructURI = property_element.getValue();
				break;
			}	
		}//end for	
		return constructURI;
	}//end getConstructURI_SL()	
	
	public String getConstructDereferenceStatus(CanonicalModelConstruct c) {	
		Set<CanonicalModelProperty> propertySet = c.getProperties();
		String constructURI = null;
		for (CanonicalModelProperty property_element : propertySet) {
			if (property_element.getName().equals("dereferenceStatus")) {
				constructURI = property_element.getValue();
				break;
			}	
		}//end for	
		return constructURI;
	}//end getConstructURI_SL()	
	
	public String generateDot(Schema schema) {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("graph schema {").append("\n");
		stringBuilder.append("rankdir=LR;").append("\n");
		stringBuilder.append("ranksep=0.5;").append("\n");
		stringBuilder.append("node [shape=record];").append("\n");

		String dsName = schema.getDataSource().getName();

		Set<SuperAbstract> superAbstracts = schema.getSuperAbstracts();
		List<SuperAbstract> orderedSuperAbstracts = this.quickSortSuperAbstractsByNumberOfSuperLexicals(new ArrayList<SuperAbstract>(superAbstracts),
				false);
		String previousSaName = null;
		int noOfSuperAbstractsProcessed = 0;
		for (SuperAbstract sa : orderedSuperAbstracts) {
			stringBuilder.append(dsName).append("_").append(sa.getName()).append(" ");
			stringBuilder.append("[label = \"<").append(sa.getName()).append("> ").append(sa.getName()).append(" (").append(sa.getCardinality())
					.append(") ");
			for (SuperLexical sl : sa.getSuperLexicals()) {
				stringBuilder.append(" | ").append("<").append(sl.getName()).append("> ").append(sl.getName()).append(" (")
						.append(sl.getNumberOfDistinctValues()).append(") ");
			}
			stringBuilder.append("\"];\n");
			if (noOfSuperAbstractsProcessed % 4 == 0) {
				previousSaName = null;
			}
			if (previousSaName != null) {
				stringBuilder.append(dsName).append("_").append(previousSaName).append("--").append(dsName).append("_").append(sa.getName())
						.append(" [style=\"invis\"];\n");
			}
			previousSaName = sa.getName();
			noOfSuperAbstractsProcessed++;
		}
		stringBuilder.append("}");
		return stringBuilder.toString();
	}

	protected List<SuperAbstract> quickSortSuperAbstractsByNumberOfSuperLexicals(List<SuperAbstract> unorderedSuperAbstracts, boolean orderAscending) {
		if (unorderedSuperAbstracts.size() <= 1)
			return unorderedSuperAbstracts;
		int listIndex = 0;
		SuperAbstract pivotSuperAbstract = unorderedSuperAbstracts.get(listIndex);
		int pivotNumberOfSuperLexicals = pivotSuperAbstract.getSuperLexicals().size();
		List<SuperAbstract> superAbstractsWithLessSuperLexicals = new ArrayList<SuperAbstract>();
		List<SuperAbstract> superAbstractsWithMoreSuperLexicals = new ArrayList<SuperAbstract>();
		unorderedSuperAbstracts.remove(pivotSuperAbstract);
		for (SuperAbstract currentSuperAbstract : unorderedSuperAbstracts)
			if (currentSuperAbstract.getSuperLexicals().size() <= pivotNumberOfSuperLexicals)
				superAbstractsWithLessSuperLexicals.add(currentSuperAbstract);
			else
				superAbstractsWithMoreSuperLexicals.add(currentSuperAbstract);
		return this.addToOrderedListOfSuperAbstracts(superAbstractsWithLessSuperLexicals, pivotSuperAbstract, superAbstractsWithMoreSuperLexicals,
				orderAscending);
	}

	private List<SuperAbstract> addToOrderedListOfSuperAbstracts(List<SuperAbstract> superAbstractsWithLessSuperLexicals,
			SuperAbstract pivotSuperAbstract, List<SuperAbstract> superAbstractsWithMoreSuperLexicals, boolean orderAscending) {
		List<SuperAbstract> orderedListOfSuperAbstract = new ArrayList<SuperAbstract>();
		if (orderAscending)
			orderedListOfSuperAbstract.addAll(quickSortSuperAbstractsByNumberOfSuperLexicals(superAbstractsWithLessSuperLexicals, orderAscending));
		else
			orderedListOfSuperAbstract.addAll(quickSortSuperAbstractsByNumberOfSuperLexicals(superAbstractsWithMoreSuperLexicals, orderAscending));
		orderedListOfSuperAbstract.add(pivotSuperAbstract);
		if (orderAscending)
			orderedListOfSuperAbstract.addAll(quickSortSuperAbstractsByNumberOfSuperLexicals(superAbstractsWithMoreSuperLexicals, orderAscending));
		else
			orderedListOfSuperAbstract.addAll(quickSortSuperAbstractsByNumberOfSuperLexicals(superAbstractsWithLessSuperLexicals, orderAscending));
		return orderedListOfSuperAbstract;
	}

	public String generateDot(List<SuperAbstract> sourceSuperAbstracts, List<SuperAbstract> targetSuperAbstracts, List<Matching> matchings,
			boolean showMatchingScore, User currentUser) {
		
		logger.debug("in generateDot()");
		
		//StringBuilder stringBuilder = new StringBuilder();
		StringBuilder stringBuilder = null;
		/*
		 * if (sourceSuperAbstracts.size() < targetSuperAbstracts.size()) { List<SuperAbstract> temp = sourceSuperAbstracts; sourceSuperAbstracts = targetSuperAbstracts; targetSuperAbstracts = temp; }
		 */

		for (SuperAbstract sa1 : sourceSuperAbstracts) {
			
			stringBuilder = new StringBuilder();

			stringBuilder.append("graph matchings {").append("\n");
			stringBuilder.append("rankdir=LR;").append("\n");
			stringBuilder.append("ranksep=8.0;").append("\n");
			stringBuilder.append("node [shape=record];").append("\n");

			String dsName1 = sa1.getSchema().getDataSource().getName();
			stringBuilder.append(dsName1).append("_").append(sa1.getName()).append(" ");
			stringBuilder.append("[label = \"<").append(sa1.getName()).append("> ").append(sa1.getName());
			for (SuperLexical sl1 : sa1.getSuperLexicals()) {
				stringBuilder.append(" | ").append("<").append(sl1.getName()).append("> ").append(sl1.getName());
			}
			stringBuilder.append("\"];\n");
			// TODO refactor
			for (SuperAbstract sa2 : targetSuperAbstracts) {
				String dsName2 = sa2.getSchema().getDataSource().getName();
				stringBuilder.append(dsName2).append("_").append(sa2.getName()).append(" ");
				stringBuilder.append("[style = filled, label = \"<").append(sa2.getName()).append("> ").append(sa2.getName());
				for (SuperLexical sl2 : sa2.getSuperLexicals()) {
					stringBuilder.append(" | ").append("<").append(sl2.getName()).append("> ").append(sl2.getName());
				}
				stringBuilder.append("\"];\n");
			}

			/*
			int maxNumberOfSuperAbstracts = 0;
			boolean sourceIsLarger = false;
			if (sourceSuperAbstracts.size() > targetSuperAbstracts.size()) {
				maxNumberOfSuperAbstracts = sourceSuperAbstracts.size();
			} else {
				maxNumberOfSuperAbstracts = targetSuperAbstracts.size();
				List<SuperAbstract> temp = sourceSuperAbstracts;
				sourceSuperAbstracts = targetSuperAbstracts;
				targetSuperAbstracts = temp;
			}
			for (int i = 0; i < maxNumberOfSuperAbstracts; i++) {
				SuperAbstract sa1 = sourceSuperAbstracts.get(i);
				String dsName1 = sa1.getSchema().getDataSource().getName();
				stringBuilder.append(dsName1).append("_").append(sa1.getName()).append(" ");
				stringBuilder.append("[label = \"<").append(sa1.getName()).append("> ").append(sa1.getName());
				for (SuperLexical sl1 : sa1.getSuperLexicals()) {
					stringBuilder.append(" | ").append("<").append(sl1.getName()).append("> ").append(sl1.getName());
				}
				stringBuilder.append("\"];\n");
				//TODO refactor
				if (targetSuperAbstracts.size() > i) {
					SuperAbstract sa2 = targetSuperAbstracts.get(i);
					String dsName2 = sa2.getSchema().getDataSource().getName();
					stringBuilder.append(dsName2).append("_").append(sa2.getName()).append(" ");
					stringBuilder.append("[style = filled, label = \"<").append(sa2.getName()).append("> ").append(sa2.getName());
					for (SuperLexical sl2 : sa2.getSuperLexicals()) {
						stringBuilder.append(" | ").append("<").append(sl2.getName()).append("> ").append(sl2.getName());
					}
					stringBuilder.append("\"];\n");
				}
			}
			*/

			for (Matching matching : matchings) {
				//One-to-one matching only
				if (matching instanceof OneToOneMatching) {
					OneToOneMatching oneToOne = (OneToOneMatching) matching;
					CanonicalModelConstruct construct1 = null, construct2 = null;
					SuperAbstract sa1OfMatching = null, sa2OfMatching = null;
					if (oneToOne.getConstruct1() instanceof SuperAbstract) {
						sa1OfMatching = (SuperAbstract) oneToOne.getConstruct1();
					} else if (oneToOne.getConstruct1() instanceof SuperLexical) {
						sa1OfMatching = ((SuperLexical) oneToOne.getConstruct1()).getFirstAncestorSuperAbstract();
					}
					if (oneToOne.getConstruct2() instanceof SuperAbstract) {
						sa2OfMatching = (SuperAbstract) oneToOne.getConstruct2();
					} else if (oneToOne.getConstruct2() instanceof SuperLexical) {
						sa2OfMatching = ((SuperLexical) oneToOne.getConstruct2()).getFirstAncestorSuperAbstract();
					}
					if ((sa1.equals(sa1OfMatching) && targetSuperAbstracts.contains(sa2OfMatching))
							|| (sa1.equals(sa2OfMatching) && targetSuperAbstracts.contains(sa1OfMatching))) {
						String dsName1OfMatching = null, dsName2OfMatching = null;
						if (sourceSuperAbstracts.contains(sa1OfMatching)) {
							construct1 = oneToOne.getConstruct1();
							construct2 = oneToOne.getConstruct2();
							dsName1OfMatching = sa1OfMatching.getSchema().getDataSource().getName();
							dsName2OfMatching = sa2OfMatching.getSchema().getDataSource().getName();
						} else if (targetSuperAbstracts.contains(sa1OfMatching)) {
							construct1 = oneToOne.getConstruct2();
							construct2 = oneToOne.getConstruct1();
							SuperAbstract temp = sa1OfMatching;
							sa1OfMatching = sa2OfMatching;
							sa2OfMatching = temp;
							dsName1OfMatching = construct1.getSchema().getDataSource().getName();
							dsName2OfMatching = construct2.getSchema().getDataSource().getName();
						}

						stringBuilder.append(dsName1OfMatching).append("_").append(sa1OfMatching.getName()).append(":").append(construct1.getName())
								.append("--");
						stringBuilder.append(dsName2OfMatching).append("_").append(sa2OfMatching.getName()).append(":").append(construct2.getName());
						if (showMatchingScore)
							stringBuilder.append(" [label = \"").append(matching.getScore()).append("\"");
						if (matching.getMatcherName().equals("instanceMatcher"))
							stringBuilder.append(", style = dotted");
						stringBuilder.append("];\n");
					}
				}
			}//end for
			
			stringBuilder.append("}\n\n");
			
			File target = exportAsDOTFile(stringBuilder.toString(), "Matching", "SA");	
			this.exportDOT2PNG(target, "png", "SA", null);
			
			
		}

		return stringBuilder.toString();
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.util.importexport.GraphvizDotGeneratorService#generateDot(java.util.List)
	 */
	public String generateDot(Set<Mapping> mappings, User currentUser) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("graph mappings {").append("\n");
		stringBuilder.append("rankdir=LR;").append("\n");
		stringBuilder.append("node [style=rounded, shape=box];").append("\n");
		int i = 1;
		for (Mapping mapping : mappings) {
			double prec = -1;
			double rec = -1;
			List<Annotation> precisionAnnotations = annotationRepository.getAnnotationsForModelManagementConstructAndOntologyTermNameProvidedByUser(
					mapping, "precision", currentUser);
			if (precisionAnnotations == null || precisionAnnotations.size() == 0)
				logger.debug("no precision annotation found for mapping");
			else if (precisionAnnotations.size() > 1)
				logger.error("more than one precision annotation for mapping - TODO sort this");
			else {
				Annotation precision = precisionAnnotations.get(0);
				logger.debug("found precision for mapping: " + precision.getValue());
				prec = Double.parseDouble(precision.getValue());
			}
			List<Annotation> recallAnnotations = annotationRepository.getAnnotationsForModelManagementConstructAndOntologyTermNameProvidedByUser(
					mapping, "recall", currentUser);
			if (recallAnnotations == null || recallAnnotations.size() == 0)
				logger.debug("no recall annotation found for mapping");
			else if (recallAnnotations.size() > 1)
				logger.error("more than one recall annotation for mapping - TODO sort this");
			else {
				Annotation recall = recallAnnotations.get(0);
				logger.debug("found recall for mapping: " + recall.getValue());
				rec = Double.parseDouble(recall.getValue());
			}
			Set<CanonicalModelConstruct> constructs1 = mapping.getConstructs1();
			Set<CanonicalModelConstruct> constructs2 = mapping.getConstructs2();
			String precString = "";
			String recString = "";
			if (prec < 0)
				precString = "unknown";
			else
				precString = String.valueOf(prec);
			if (rec < 0)
				recString = "unknown";
			else
				recString = String.valueOf(rec);
			for (CanonicalModelConstruct construct1 : constructs1) {
				stringBuilder.append(construct1.getSchema().getName()).append("_").append(construct1.getName()).append(" [label = \"")
						.append(construct1.getSchema().getName()).append(".").append(construct1.getName()).append("\"];").append("\n");
			}
			stringBuilder.append("Mapping").append(i).append(" [label = \"").append(mapping.getQuery2String()).append("\\n").append("precision: ")
					.append(precString).append("\\n").append("recall: ").append(recString).append("\"];").append("\n");
			for (CanonicalModelConstruct construct2 : constructs2) {
				stringBuilder.append(construct2.getSchema().getName()).append("_").append(construct2.getName()).append(" [label = \"")
						.append(construct2.getSchema().getName()).append(".").append(construct2.getName()).append("\"];").append("\n");
			}
			for (CanonicalModelConstruct construct1 : constructs1) {
				stringBuilder.append(construct1.getSchema().getName()).append("_").append(construct1.getName()).append("--").append("Mapping")
						.append(i).append(";").append("\n");
			}
			for (CanonicalModelConstruct construct2 : constructs2) {
				stringBuilder.append("Mapping").append(i).append("--").append(construct2.getSchema().getName()).append("_")
						.append(construct2.getName()).append(";").append("\n");
			}
			i++;
		}
		stringBuilder.append("}");
		return stringBuilder.toString();
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.util.importexport.GraphvizDotGeneratorService#generateDot(uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.MappingOperator)
	 */
	public String generateDot(MappingOperator queryRootOperator, User currentUser) {
		logger.debug("in generateDot, queryRootOperator: " + queryRootOperator);
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("graph query {").append("\n");
		stringBuilder.append("rankdir=TB;").append("\n");
		stringBuilder.append("node [style=rounded, shape=box];").append("\n");

		stringBuilder.append(generateDot(queryRootOperator, 1));

		stringBuilder.append("}");
		return stringBuilder.toString();
	}

	private String generateDot(MappingOperator queryOperator, int i) {
		logger.debug("in private generateDot");
		StringBuilder stringBuilder = new StringBuilder();
		logger.debug("queryOperator: " + queryOperator);
		logger.debug("i: " + i);
		String nameOfCurrentOperator = "";
		String nameOfLhsChild = null;
		String nameOfRhsChild = null;
		if (queryOperator instanceof ReduceOperator) {
			stringBuilder.append("Reduce").append(i).append(" [label = \"REDUCE\\n").append(queryOperator.getReconcilingExpression().getExpression())
					.append("\"];").append("\n");
			nameOfCurrentOperator = "Reduce" + i;
		} else if (queryOperator instanceof RenameOperator) {
			stringBuilder.append("Rename").append(i).append(" [label = \"RENAME\\n").append(((RenameOperator) queryOperator).getNewName())
					.append("\"];").append("\n");
			nameOfCurrentOperator = "Rename" + i;
		} else if (queryOperator instanceof JoinOperator) {
			stringBuilder.append("Join").append(i).append(" [label = \"JOIN\\n").append(queryOperator.getReconcilingExpression().getExpression())
					.append("\"];").append("\n");
			nameOfCurrentOperator = "Join" + i;
		} else if (queryOperator instanceof ScanOperator) {
			stringBuilder.append("Scan").append(i).append(" [label = \"SCAN\\n")
					.append(((ScanOperator) queryOperator).getSuperAbstract().getSchema().getName()).append(".")
					.append(((ScanOperator) queryOperator).getSuperAbstract().getName()).append(" ")
					.append(((ScanOperator) queryOperator).getVariableName()).append("\"];").append("\n");
			nameOfCurrentOperator = "Scan" + i;
		} else if (queryOperator instanceof SetOperator) {
			stringBuilder.append(((SetOperator) queryOperator).getSetOpType().toString()).append(i).append(" [label = \"")
					.append(((SetOperator) queryOperator).getSetOpType()).append("\"];").append("\n");
			nameOfCurrentOperator = ((SetOperator) queryOperator).getSetOpType().toString() + i;
		}
		logger.debug("nameOfCurrentOperator: " + nameOfCurrentOperator);
		i++;
		logger.debug("i: " + i);
		if (queryOperator.getLhsInput() != null) {
			MappingOperator lhsChild = queryOperator.getLhsInput();
			if (lhsChild instanceof ReduceOperator)
				nameOfLhsChild = "Reduce" + i;
			else if (lhsChild instanceof RenameOperator)
				nameOfLhsChild = "Rename" + i;
			else if (lhsChild instanceof JoinOperator)
				nameOfLhsChild = "Join" + i;
			else if (lhsChild instanceof ScanOperator)
				nameOfLhsChild = "Scan" + i;
			else if (lhsChild instanceof SetOperator)
				nameOfLhsChild = ((SetOperator) lhsChild).getSetOpType().toString() + i;
			logger.debug("nameOfLhsChild: " + nameOfLhsChild);
			stringBuilder.append(generateDot(lhsChild, i));

			i += countChildOperators(queryOperator.getLhsInput());
		}
		logger.debug("i: " + i);
		if (queryOperator.getRhsInput() != null) {
			MappingOperator rhsChild = queryOperator.getRhsInput();
			if (rhsChild instanceof ReduceOperator)
				nameOfRhsChild = "Reduce" + i;
			else if (rhsChild instanceof RenameOperator)
				nameOfRhsChild = "Rename" + i;
			else if (rhsChild instanceof JoinOperator)
				nameOfRhsChild = "Join" + i;
			else if (rhsChild instanceof ScanOperator)
				nameOfRhsChild = "Scan" + i;
			else if (rhsChild instanceof SetOperator)
				nameOfRhsChild = ((SetOperator) rhsChild).getSetOpType().toString() + i;
			logger.debug("nameOfRhsChild: " + nameOfRhsChild);
			stringBuilder.append(generateDot(rhsChild, i));
		}
		if (nameOfLhsChild != null)
			stringBuilder.append(nameOfCurrentOperator).append("--").append(nameOfLhsChild).append(";").append("\n");
		if (nameOfRhsChild != null)
			stringBuilder.append(nameOfCurrentOperator).append("--").append(nameOfRhsChild).append(";").append("\n");

		return stringBuilder.toString();
	}

	private int countChildOperators(MappingOperator queryOperator) {
		logger.debug("in countChildOperators");
		int numberOfChildren = 1;
		if (queryOperator.getLhsInput() != null)
			numberOfChildren += countChildOperators(queryOperator.getLhsInput());
		if (queryOperator.getRhsInput() != null)
			numberOfChildren += countChildOperators(queryOperator.getRhsInput());
		return numberOfChildren;
	}



	/**
	 * 
	 * @param dot - Graph source file in DOT language.
	 * @param imageType - Type of the image to be produces, supports: png, gif, dot, fig, pdf, ps, svg.
	 * @param fileName - The filename to give to the image file, it can also be null (default in "graph_")
	 * @param osType - Specify Operating System, helps to locate dot.exe in WIN or dot binary in Linux/Mac.
	 *               - default is WIN.
	 */
	public void exportDOT2PNG(File dot, String imageType, String fileName, String osType) {
	    File tempIMG;
	    String dotLocation;
		try {
			
	    	if ( (fileName == null) || fileName.equals("") ) {
	    		fileName = "graph_";
	    	} else {
	    		fileName = fileName.trim()+"_";
	    	}
			
			tempIMG = File.createTempFile(fileName, "."+imageType, new File(dot.getParent()));
	        Runtime rt = Runtime.getRuntime();
	        
	        dotLocation = this.GRAPHVIZ_DOT_WIN;

	        if ((osType != null) && (osType.toLowerCase().equals("linux") || osType.toLowerCase().equals("mac"))){
		        dotLocation = this.GRAPHVIZ_DOT_LINUX_MAC;	        	
	        }        
	        	        
	        String[] args = {dotLocation, "-T"+imageType, dot.getAbsolutePath(), "-o", tempIMG.getAbsolutePath()};
			/*Run external process*/
	        Process proc = rt.exec(args);
		    InputStream stderr = proc.getErrorStream();
		    InputStreamReader isr = new InputStreamReader(stderr);
		    BufferedReader br = new BufferedReader(isr);
		   
		    while ((br.readLine()) != null) { }
		   		    
		    int exitValue = proc.waitFor();
		    if (exitValue == 0) {
		    	logger.debug("Export DOT2PNG: normal termination");		    	
		    } else {
		    	logger.debug("Export DOT2PNG: FAILED, exit value is:" + exitValue);			    	
		    }//end else		    	
   
		} catch (IOException exe) {
			logger.error("Error - I/O processing of tempfile in dir :" + this.GRAPHVIZ_OUTPUT);
	    }
	    catch (InterruptedException exe) {
	       	logger.error("Error - execution of the external dot.exe or dot bin was interrupted");
	    }	
	}//end exportDOT2PNG()
	
	/**
	 * 
	 * @param inputDotString - A string representation of the DOT language for this graph. 
	 * @param dirName - The name of the output directory
	 * @param fileName - The name to be attached at the start of each file, default is "graph_"
	 * 
	 * @return - a File object that contains the source of the graph.
	 * @throws java.io.IOException
	 */
	public File exportAsDOTFile(String inputDotString, String dirName, String fileName) {
		logger.debug("in exportAsDOTFile");
	    File temp;
	    String location;
	    try {    	
	    	location = this.GRAPHVIZ_OUTPUT;
	    	
	    	//if (dirName != null){
    	    	//location = this.GRAPHVIZ_OUTPUT + dirName;
        		//logger.debug("path location is: " + location);  	
     	    //} 
	    	
	    	if ( (fileName == null) || fileName.equals("") ) {
	    		fileName = "graph_";
	    	} else {
	    		fileName = fileName.trim()+"_";
	    	}
	    	
	    	/*Check whether graphviz output directory is configured correctly*/ 
	        
	    	File graphiz_out_dir = new File(this.GRAPHVIZ_OUTPUT);	        
	        if (!graphiz_out_dir.isDirectory()) throw new DSToolkitConfigException("Error - GRAPHVIZ_OUTPUT is not properly configured.");  
	    	
	    	/*Check whether directory exists, if not create it*/
	        File dir = null;
	        if (dirName != null) {
	        	dir = new File(location + "/" + dirName);
	        	logger.debug("path location is: " + dir.getCanonicalPath());  
	        } else {
	        	dir = new File(location);
	        	logger.debug("path location is: " +  dir.getCanonicalPath());  
	        }	    	
	    	
	    	if (!dir.isDirectory()) {
	    		boolean created = dir.mkdir();
	    		if (!created) throw new SecurityException ("Error - while creating Graphviz directory."); 
	    	}
	    	
	    	/*Create the file in that location*/
	        temp = File.createTempFile(fileName, ".dot.tmp", dir);
	        FileWriter out = new FileWriter(temp);
	        out.write(inputDotString);
	        out.close();
	    } catch (Exception exe) {
	       	logger.error("Error - I/O error while writing the dot source to file.");
	        return null;
	    } 
	     return temp;
	}//end 
	
	
	/***
	 * Output the individual errors 
	 * @param error1 - holds individual errors between M{ng} - M{exp} or M{ed} - M{exp} or M{avg}
	 * @param error2 - holds individual errors between M{syn}, M{exp}
	 * @param mt - the name of the individual Matcher
	 * @param controlParameters
	 */
	public void exportIndividualPairErrorsSynOnly(float[][] error1, float[][] error2, MatcherType mt, ErrorMeasures measure,
																					Map<ControlParameterType, ControlParameter> controlParameters) {
		logger.debug("Only()");		
		//Write the results in a file.
		File loc = null;		
		BufferedWriter out_perform = null;
		
		try {
			
			//If matcher type is null introduce the name COMA
			String matcherName = "COMA";
			if (mt != null) {
				matcherName = mt.toString();
			}			
			
			//Create name for csv file
			String fileName = matcherName +"_"+ measure.getmType().toString();			
			
			//Create the perform_syn_only.csv file
			loc = new File(this.GRAPHVIZ_OUTPUT + "/" + fileName +".csv");
			out_perform = new BufferedWriter(new FileWriter(loc, true));
		
			//Prepare header
			out_perform.append("\nMatcher : ").append(mt.toString()).append("\n");
			out_perform.append("Error Measure : ").append(measure.getmType().toString()).append("\n");
			out_perform.append("pair").append("|").append("D_1").append("|").append("D_2").append("|").append("\n");
			
			//error 1 - individual errors btw matcher and Mexp
			//error 2 - individual errors btw M{syn} and M{exp}
			
			int rowsNo 	  = error1.length;
			int columnsNo = error1[0].length;	
			
			DecimalFormat df = new DecimalFormat("#.####");
						
			for (int i=0; i< rowsNo; i++) {
				for (int j=0; j< columnsNo; j++) {
					float d_1  =  error1[i][j];
					float d_2  =  error2[i][j];		
					out_perform.append("[" + i + "," + j + "]").append("|").append(df.format(d_1)).append("|").append(df.format(d_2)).append("|").append("\n");

				}//end inner for
			}//end for		
		
			//flush and close file
			out_perform.close();	
			
			//Call method to generate GNUPLOT code for the graph
			//this.plotIndividualErrors(fileName, mt, "eps", false);
			this.plotIndividualErrors(fileName, mt, "eps", false,true,true); //use this line to highlight regions from the plot
		
		} catch (IOException e) {
	       	logger.error("Error - I/O error while writing csv performance file for syntactic only.");
		} 	
		
	}//end exportIndividualPairErrorsSynOnly()
	
	
	/***
	 * Output the individual errors [Syn & Sem]
	 * @param error1 - holds individual errors between M{syn}, M{exp}
	 * @param error2 - holds individual errors between M{syn&sem}, M{exp}
	 * 
	 * @param controlParameters
	 */
	public void exportIndividualPairErrorsSynSem(float[][] error1, float[][] error2, ErrorMeasures measure, MatcherType mt,
															Map<ControlParameterType, ControlParameter> controlParameters,
															Set<BooleanVariables> evidencesToAccumulate) {
		logger.debug("exportIndividualPairErrorsSynSem()");		
		//Write the results in a file.
		File loc = null;		
		BufferedWriter out_perform = null;
		StringBuilder fileName = null;
		
		//Hold the dampening effect policy
		DampeningEffectPolicy dep = null;
		
		try {
			
			if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.DAMPENING_EFFECT_POLICY))) {
				ControlParameter controlParam = controlParameters.get(ControlParameterType.DAMPENING_EFFECT_POLICY);
				dep = controlParam.getDampeningEffectPolicy();
			}//end if			
						
			//Build the filename
			fileName = new StringBuilder();			
			
			//MatcherType
			if (mt != null) {
				fileName.append(mt.toString());
			}			
			
			//Create name for csv file		
			if (evidencesToAccumulate != null) {
				fileName.append("_" + measure.getmType().toString()).append("_syn_sem_");
				fileName.append(this.toStringSet(evidencesToAccumulate));			
			} 
			
			//Create the perform_syn_only.csv file
			loc = new File(this.GRAPHVIZ_OUTPUT + "/" + fileName + ".csv");
			out_perform = new BufferedWriter(new FileWriter(loc, true));
		
			/**
			 * Header for the performance.csv file
			 */
			String evidencesString = "";
			
			if ( (dep!=null) && dep.equals(DampeningEffectPolicy.SOME_EVIDENCE) ) {				
				evidencesString = evidencesToAccumulate.toString().replace("[", "{");
				evidencesString = evidencesString.replace("]", "}");			
			} else if ( (dep!=null) && dep.equals(DampeningEffectPolicy.COMBINATION_OF_EVIDENCE) ) {
				evidencesString = evidencesToAccumulate.toString().replace("[", "");
				evidencesString = evidencesString.replace("]", "");
				evidencesString = evidencesString.replace(",", " {/Symbol \331}");				
			} else {
				evidencesString = evidencesToAccumulate.toString().replace(",", ".");
			}			
			
			
			//Prepare header
			out_perform.append("\nEvidence: ").append(evidencesString).append("\n");
			out_perform.append("Error Measure : ").append(measure.getmType().toString()).append("\n");
			out_perform.append("pair").append("|").append("D_1").append("|").append("D_2").append("|").append("\n");
			
			//error 1 - individual errors btw matcher and Mexp
			//error 2 - individual errors btw M{syn} and M{exp}
			
			int rowsNo 	  = error1.length;
			int columnsNo = error1[0].length;	
			
			DecimalFormat df = new DecimalFormat("#.####");
						
			for (int i=0; i< rowsNo; i++) {
				for (int j=0; j< columnsNo; j++) {
					float d_1  =  error1[i][j];
					float d_2  =  error2[i][j];		
					out_perform.append("[" + i + "," + j + "]").append("|").append(df.format(d_1)).append("|").append(df.format(d_2)).append("|").append("\n");

				}//end inner for
			}//end for		
		
			//flush and close file
			out_perform.close();	
			
			//Call method to generate GNUPLOT code for the graph
			//this.plotIndividualErrorsSynSem(fileName.toString(), mt, "eps", false);
			this.plotIndividualErrorsSynSem(fileName.toString(), mt, "eps", false,true,true); //use this line to highlight regions from the plot
			
		} catch (IOException e) {
	       	logger.error("Error - I/O error while writing csv performance file for syntactic only.");
		} 	
		
	}//end exportIndividualPairErrorsSynOnly()
	
	
	
	/***
	 * GNUPLOT: Output GNUplot code to plot the individual difference in error with the expectation matrix
	 * 
	 * @param fileName - the same fileName as the .csv file used to generate the data for this plot
	 * @param evidencesToAccumulate - get a list of the evidences accumulated
	 */
	public void plotIndividualErrorsSynSem(String fileName, MatcherType mt, String termType, boolean semEvidenceMode,
										  boolean highlight, boolean color) {
		//Open up a file
		File loc = null;		
		BufferedWriter out = null;
		
		try {
			
			//Create the file name for the GNUplot code
			loc = new File(this.GRAPHVIZ_OUTPUT + "/" + fileName + ".plt");
			out = new BufferedWriter(new FileWriter(loc, false));
			
			out.append("clear").append("\n");
			out.append("reset").append("\n");
			
			//set the size so as to make plots visible when .eps or .pdf in latex
			out.append("set size 0.6, 0.6").append("\n");
			
			out.append("set datafile separator \"|\"").append("\n");
			
			out.append("set key off").append("\n");
			
			//Highlight the areas with the bounding boxes
			if (highlight) {		
				out.append("#set grid").append("\n\n");
				
				out.append("#Area 1").append("\n");
				out.append("set object 11 rect from 0.2,0 to 1,0.2 fc rgb \"yellow\" fs pattern 2 noborder").append("\n");
				out.append("set object 12 polygon from 0,0 to 0.2,0 to 0.2,0.2").append("\n");
				out.append("set object 12 fc rgb \"yellow\" fs pattern 2 noborder").append("\n");
				out.append("#Area 2").append("\n");
				out.append("set object 21 rect from 0,0.2 to 0.2,1 fc rgb \"#8FBC8F\" fs pattern 2 noborder").append("\n");
				out.append("set object 22 polygon from 0,0 to 0,0.2 to 0.2,0.2").append("\n");
				out.append("set object 22 fc rgb \"#8FBC8F\" fs pattern 2 noborder").append("\n");
				out.append("#Area 3").append("\n");
				out.append("set object 31 polygon from 0.2,0.2 to 1,0.2 to 1,1").append("\n");
				out.append("set object 31 fc rgb \"#ADD8E6\" fs pattern 2 noborder").append("\n");
				out.append("#Area 4").append("\n");
				out.append("set object 41 polygon from 0.2,0.2 to 0.2,1 to 1,1").append("\n");
				out.append("set object 42 fc rgb \"#FFEFD5\" fs pattern 2 noborder").append("\n\n");			
			} else {
				out.append("set grid").append("\n");								
			}//end highlight			
			
			out.append("set autoscale").append("\n").append("\n");
			
			out.append("set xrange [0:1]").append("\n");
			out.append("set yrange [0:1]").append("\n");		

			out.append("set xtics 0.1").append("\n");
			out.append("set ytics 0.1").append("\n").append("\n");
			
			/***
			 * Set axis-labels according to the mode
			 */
			String xAxisLabel = "error ";
			String yAxisLabel = "error ";			
			
			if (semEvidenceMode) {
				//TODO: this for the semantic evidence
			} else {
				
				//MODE: Syntactic evidence only 
				//TODO: Remember to add other matchers here
				if ((mt != null) && (mt.equals(mt.COMA_AVG))) {
					xAxisLabel = xAxisLabel + "M_{avg}";		
					out.append("set xlabel " + "\""+ xAxisLabel +"\"").append("\n");
					out.append("set ylabel " + "\""+ yAxisLabel + "M_{syn,sem}\"").append("\n").append("\n");
				} if ((mt != null) && (mt.equals(mt.BAYESIAN_APPROACH))) {
					xAxisLabel = xAxisLabel + "M_{syn}";	
					out.append("set xlabel " + "\""+ xAxisLabel +"\"").append("\n");
					out.append("set ylabel " + "\""+ yAxisLabel + "M_{syn,sem}\"").append("\n").append("\n");
				}
			}

			out.append("plot \"").append(fileName).append(".csv").append("\" using 2:3, x with lines lt 1 notitle").append("\n");
			
			if (termType.equals("png")) {
				out.append("\nset term png enhanced size 800,600").append("\n");
				out.append("set output \"").append(fileName).append(".png\"").append("\n");
			} else if (termType.equals("eps")) {
				if (color) {
					out.append("\nset terminal postscript eps enhanced dashed").append("\n");
					out.append("set output \"").append(fileName).append(".eps\"").append("\n");
				} else {
					out.append("\nset terminal postscript eps enhanced monochrome dashed").append("\n");
					out.append("set output \"").append(fileName).append(".eps\"").append("\n");
				}
			}
			
			out.append("replot");		
			
			//At the end close the file
			if (out != null) { out.close(); }			
		} catch (IOException e) {
			logger.error("Error - I/O error while plotting individual errors.");
		}//end catch		
	}//end generateGnuplotForIndividualErrors()	
	
	
	/***
	 * GNUPLOT: Output GNUplot code to plot the individual difference in error with the expectation matrix
	 * 
	 * @param fileName - the same fileName as the .csv file used to generate the data for this plot
	 * @param evidencesToAccumulate - get a list of the evidences accumulated
	 */
	public void plotIndividualErrors(String fileName, MatcherType mt, String termType, boolean semEvidenceMode,
																				boolean highlight, boolean color) {
		//Open up a file
		File loc = null;		
		BufferedWriter out = null;
		
		try {
			
			//Create the file name for the GNUplot code
			loc = new File(this.GRAPHVIZ_OUTPUT + "/" + fileName + ".plt");
			out = new BufferedWriter(new FileWriter(loc, false));
			
			out.append("clear").append("\n");
			out.append("reset").append("\n");
			
			//set the size so as to make plots visible when .eps or .pdf in latex
			out.append("set size 0.6, 0.6").append("\n");
			
			out.append("set datafile separator \"|\"").append("\n");
			
			out.append("set key off").append("\n");
			
			//Highlight the areas with the bounding boxes
			if (highlight) {		
				out.append("#set grid").append("\n\n");
				
				out.append("#Area 1").append("\n");
				out.append("set object 11 rect from 0.2,0 to 1,0.2 fc rgb \"yellow\" fs pattern 2 noborder").append("\n");
				out.append("set object 12 polygon from 0,0 to 0.2,0 to 0.2,0.2").append("\n");
				out.append("set object 12 fc rgb \"yellow\" fs pattern 2 noborder").append("\n");
				out.append("#Area 2").append("\n");
				out.append("set object 21 rect from 0,0.2 to 0.2,1 fc rgb \"darkseagreen\" fs pattern 2 noborder").append("\n");
				out.append("set object 22 polygon from 0,0 to 0,0.2 to 0.2,0.2").append("\n");
				out.append("set object 22 fc rgb \"darkseagreen\" fs pattern 2 noborder").append("\n");
				out.append("#Area 3").append("\n");
				out.append("set object 31 polygon from 0.2,0.2 to 1,0.2 to 1,1").append("\n");
				out.append("set object 31 fc rgb \"#ADD8E6\" fs pattern 2 noborder").append("\n");
				out.append("#Area 4").append("\n");
				out.append("set object 41 polygon from 0.2,0.2 to 0.2,1 to 1,1").append("\n");
				out.append("set object 42 fc rgb \"#FFEFD5\" fs pattern 2 noborder").append("\n\n");			
			} else {
				out.append("set grid").append("\n");								
			}//end highlight			
			
			out.append("set autoscale").append("\n").append("\n");
			
			out.append("set xrange [0:1]").append("\n");
			out.append("set yrange [0:1]").append("\n");		

			out.append("set xtics 0.1").append("\n");
			out.append("set ytics 0.1").append("\n").append("\n");
			
			/***
			 * Set axis-labels according to the mode
			 */
			String xAxisLabel = "error ";
			String yAxisLabel = "error ";			
			
			if (semEvidenceMode) {
				//TODO: this for the semantic evidence
			} else {
				
				//MODE: Syntactic evidence only 
				//TODO: Remember to add other matchers here
				if ((mt != null) && (mt.equals(mt.LEVENSHTEIN))) {
					xAxisLabel = xAxisLabel + "M_{ed}";
					out.append("set xlabel " + "\""+ xAxisLabel +"\"").append("\n");
					out.append("set ylabel " + "\""+ yAxisLabel + "M_{syn}\"").append("\n").append("\n");
				} else if ((mt != null) && (mt.equals(mt.NGRAM))) {
					xAxisLabel = xAxisLabel + "M_{ng}";		
					out.append("set xlabel " + "\""+ xAxisLabel +"\"").append("\n");
					out.append("set ylabel " + "\""+ yAxisLabel + "M_{syn}\"").append("\n").append("\n");
				} else if ((mt != null) && (mt.equals(mt.COMA_AVG))) {
					xAxisLabel = xAxisLabel + "M_{avg}";		
					out.append("set xlabel " + "\""+ xAxisLabel +"\"").append("\n");
					out.append("set ylabel " + "\""+ yAxisLabel + "M_{syn}\"").append("\n").append("\n");
				} else {
					xAxisLabel = xAxisLabel + "M_{syn}";	
					out.append("set xlabel " + "\""+ xAxisLabel +"\"").append("\n");
					out.append("set ylabel " + "\""+ yAxisLabel + "M_{syn,sem}\"").append("\n").append("\n");
				}
			}

			out.append("plot \"").append(fileName).append(".csv").append("\" using 2:3, x with lines lt 1 notitle").append("\n");
			
			if (termType.equals("png")) {
				out.append("\nset term png enhanced size 800,600").append("\n");
				out.append("set output \"").append(fileName).append(".png\"").append("\n");
			} else if (termType.equals("eps")) {
				if (color) {
					out.append("\nset terminal postscript eps enhanced dashed").append("\n");
					out.append("set output \"").append(fileName).append(".eps\"").append("\n");
				} else {
					out.append("\nset terminal postscript eps enhanced monochrome dashed").append("\n");
					out.append("set output \"").append(fileName).append(".eps\"").append("\n");
				}
			}
			
			out.append("replot");		
			
			//At the end close the file
			if (out != null) { out.close(); }			
		} catch (IOException e) {
			logger.error("Error - I/O error while plotting individual errors.");
		}//end catch		
	}//end generateGnuplotForIndividualErrors()	
	
	/***
	 * Write the results for measuring the error1, error2 only for the syntactic evidence.
	 * We would like to prove that our approach of aggregating syntactic evidence with Bayes works by measuring the 
	 * error with the Expectation Matrix. 
	 */
	public void exportToCSVFileOnlySyntacticEvidence(Map<PerformanceErrorTypes, Float> error1, Map<PerformanceErrorTypes, Float> error2,
													MatcherType mt,	Map<ControlParameterType, ControlParameter> controlParameters) {
		
		logger.debug("exportToCSVFileOnlySyntacticEvidence()");		
		boolean show_perc_change = false;
		boolean plot_perc_change = false;
				
		if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.SHOW_PERC_CHANGE))) {
			show_perc_change = controlParameters.get(ControlParameterType.SHOW_PERC_CHANGE).isBool();
		}
		
		if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.PLOT_PERC_CHANGE))) {
			plot_perc_change = controlParameters.get(ControlParameterType.PLOT_PERC_CHANGE).isBool();
		}	
		
		//Write the results in a file.
		File loc = null;		
		BufferedWriter out_perform = null;
		
		try {
			//Create the perform_syn_only.csv file
			loc = new File(this.GRAPHVIZ_OUTPUT+"/perform_aggr_measures_syn_only.csv");
			out_perform = new BufferedWriter(new FileWriter(loc, true));
			
			
			//If matcher type is null introduce the name COMA
			String matcherName = "COMA";
			if (mt != null) {
				matcherName = mt.toString();
			}			
			
			//Header - the name of the single matcher to compare 
			out_perform.append("\nMatcher : ").append(matcherName).append("\n");
			
			if (show_perc_change) {
				out_perform.append("\t").append(",").append("Error 1,").append("Error 2,").append("Difference,").append("% Change").append("\n");	
			} else {
				out_perform.append("\t").append(",").append("Error 1,").append("Error 2,").append("Difference").append("\n");
			}//end if	
			
			//Control the floating point
			DecimalFormat df = new DecimalFormat("#.####");
			
			//For all the chosen Performance measures 
			for (Map.Entry<PerformanceErrorTypes, Float> entry1 : error1.entrySet()) {
				PerformanceErrorTypes index = entry1.getKey();
				
				out_perform.append(index.toString()).append(","); //measure type
				Float err1 = entry1.getValue();
				out_perform.append(df.format(err1)).append(","); //error1
				Float err2 = error2.get(index);
				out_perform.append(df.format(err2)).append(","); //error2
				Float dif = err1 - err2;
				out_perform.append(df.format(dif)); //diff
				
				if (show_perc_change) {
					//CASE: Show percentage of change 
					Float percDecrease = ((err1 - err2) / Math.abs(err1)) * 100;
									
					out_perform.append(",").append(df.format(percDecrease)); 
				}//end if
				
				//change line
				out_perform.append("\n");
			}//end for			
			
			//flush and close file
			out_perform.close();			
			
		} catch (IOException e) {
	       	logger.error("Error - I/O error while writing csv performance file for syntactic only.");
		} 		
	}//end exportToCSVFileOnlySyntacticEvidence()	
	
	/***
	 * We would like to find the difference in error measuring the following
	 * 
	 * error 1 - Msyn with Mexp
	 * error 2 - Msyn&sem with Mexp
	 */
	public void exportToCSVFileSynSemEvidence(Map<PerformanceErrorTypes, Float> error1, Map<PerformanceErrorTypes, Float> error2,
											                        Map<ControlParameterType, ControlParameter> controlParameters) {
		
		logger.debug("exportToCSVFileOnlySyntacticEvidence()");		
		boolean show_perc_change = false;
		boolean plot_perc_change = false;
				
		if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.SHOW_PERC_CHANGE))) {
			show_perc_change = controlParameters.get(ControlParameterType.SHOW_PERC_CHANGE).isBool();
		}
		
		if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.PLOT_PERC_CHANGE))) {
			plot_perc_change = controlParameters.get(ControlParameterType.PLOT_PERC_CHANGE).isBool();
		}	
		
		//Write the results in a file.
		File loc = null;		
		BufferedWriter out_perform = null;
		
		try {
			//Create the perform_syn_only.csv file
			loc = new File(this.GRAPHVIZ_OUTPUT+"/perform_aggr_measures_syn_sem.csv");
			out_perform = new BufferedWriter(new FileWriter(loc, true));
			
			if (show_perc_change) {
				out_perform.append("\t").append(",").append("Error 1,").append("Error 2,").append("Difference,").append("% Change").append("\n");	
			} else {
				out_perform.append("\t").append(",").append("Error 1,").append("Error 2,").append("Difference").append("\n");
			}//end if	
			
			//Control the floating point
			DecimalFormat df = new DecimalFormat("#.####");
			
			//For all the chosen Performance measures 
			for (Map.Entry<PerformanceErrorTypes, Float> entry1 : error1.entrySet()) {
				PerformanceErrorTypes index = entry1.getKey();
				
				out_perform.append(index.toString()).append(","); //measure type
				Float err1 = entry1.getValue();
				out_perform.append(df.format(err1)).append(","); //error1
				Float err2 = error2.get(index);
				out_perform.append(df.format(err2)).append(","); //error2
				Float dif = err1 - err2;
				out_perform.append(df.format(dif)); //diff
				
				if (show_perc_change) {
					//CASE: Show percentage of change 
					Float percDecrease = ((err1 - err2) / Math.abs(err1)) * 100;
									
					out_perform.append(",").append(df.format(percDecrease)); 
				}//end if
				
				//change line
				out_perform.append("\n");
			}//end for			
			
			//flush and close file
			out_perform.close();			
			
		} catch (IOException e) {
	       	logger.error("Error - I/O error while writing csv performance file for syn & sem.");
		} 		
	}//end exportToCSVFileSynSemEvidence()
	
	
	/***
	 * Write results of measuring the error1, error2 for each combinations of evidences in a csv file
	 */
	public void exportToCSVFile(Map<PerformanceErrorTypes, Float> error1, Map<PerformanceErrorTypes, Float> error2,
																Set<BooleanVariables> evidencesToAccumulate,
																MatcherType mt,
																Map<ControlParameterType, ControlParameter> controlParameters) {
		logger.debug("exportToCSVFile()");
			
		//Hold the dampening effect policy
		DampeningEffectPolicy dep = null;
		
		boolean show_perc_incr_decr = false;
		boolean show_perc_change = false;
		
		boolean plot_perc_incr_decr = false;
		boolean plot_perc_change = false;
		
		if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.DAMPENING_EFFECT_POLICY))) {
			ControlParameter controlParam = controlParameters.get(ControlParameterType.DAMPENING_EFFECT_POLICY);
			dep = controlParam.getDampeningEffectPolicy();
		}//end if
		
		if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.SHOW_PERC_INCR_DECR))) {
			show_perc_incr_decr = controlParameters.get(ControlParameterType.SHOW_PERC_INCR_DECR).isBool();
		}	
		
		if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.SHOW_PERC_CHANGE))) {
			show_perc_change = controlParameters.get(ControlParameterType.SHOW_PERC_CHANGE).isBool();
		}
		
		if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.PLOT_PERC_INCR_DECR))) {
			plot_perc_incr_decr = controlParameters.get(ControlParameterType.PLOT_PERC_INCR_DECR).isBool();
		}
		
		if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.PLOT_PERC_CHANGE))) {
			plot_perc_change = controlParameters.get(ControlParameterType.PLOT_PERC_CHANGE).isBool();
		}				
		
				
		File loc = null;		
		BufferedWriter out_perform = null;
		
		//Buffers used for writing the data used for the plots
		BufferedWriter out_decrease = null;
		BufferedWriter out_increase = null;
		BufferedWriter out_change = null;
		File file_decrease = null;
		File file_increase = null;	
		File file_change = null;
				
		try {
				
			//Create the performance.csv file
			loc = new File(this.GRAPHVIZ_OUTPUT+"/performance.csv");
			out_perform = new BufferedWriter(new FileWriter(loc, true));
	
			/**
			 * Header for the performance.csv file
			 */
			String evidencesString = "";
			
			if ( (dep!=null) && dep.equals(DampeningEffectPolicy.SOME_EVIDENCE) ) {				
				evidencesString = evidencesToAccumulate.toString().replace("[", "{");
				evidencesString = evidencesString.replace("]", "}");			
			} else if ( (dep!=null) && dep.equals(DampeningEffectPolicy.COMBINATION_OF_EVIDENCE) ) {
				evidencesString = evidencesToAccumulate.toString().replace("[", "");
				evidencesString = evidencesString.replace("]", "");
				evidencesString = evidencesString.replace(",", " {/Symbol \331}");				
			} else {
				evidencesString = evidencesToAccumulate.toString().replace(",", ".");
			}		
			
			//Get the matcher name
			String matcherName = "";
			if (mt != null) {
				matcherName = mt.toString();
				out_perform.append("\nEvidence [").append(matcherName + "] :").append(evidencesString).append("\n");
			} else { 			
				out_perform.append("\nEvidence :").append(evidencesString).append("\n");
			}
			
			//Derive the data needed for the plots
			if (plot_perc_incr_decr && show_perc_incr_decr) {
				
				file_decrease =  new File(this.GRAPHVIZ_OUTPUT+"/decrease_graph_data.csv");
				out_decrease = new BufferedWriter(new FileWriter(file_decrease, true));
				file_increase =  new File(this.GRAPHVIZ_OUTPUT+"/increase_graph_data.csv");
				out_increase = new BufferedWriter(new FileWriter(file_increase, true));
				
				//Add the header to the new files
				if (this.SHOW_LABELS) {
					
					out_decrease.append("Combination,").append("Number,");
					out_increase.append("Combination,").append("Number,");
					for (Map.Entry<PerformanceErrorTypes, Float> entry1 : error1.entrySet()) {
						PerformanceErrorTypes index = entry1.getKey();			
						out_decrease.append(index.toString()).append(",");
						out_increase.append(index.toString()).append(",");
					}//end for
					
					out_decrease.append("\n").append(evidencesString).append(",").append(" ,");
					out_increase.append("\n").append(evidencesString).append(",").append(" ,");
					
					//Do not print label again
					this.SHOW_LABELS = false;
				} else {
					out_decrease.append("\n").append(evidencesString).append(",").append(" ,");
					out_increase.append("\n").append(evidencesString).append(",").append(" ,");					
				}
			} else if (plot_perc_change && show_perc_change) {
				file_change =  new File(this.GRAPHVIZ_OUTPUT+"/change_graph_data.csv");
				out_change = new BufferedWriter(new FileWriter(file_change, true));
				
				//Add the header to the perc of change file
				if (this.SHOW_LABELS) {
					
					out_change.append("Combination,").append("Number,");

					for (Map.Entry<PerformanceErrorTypes, Float> entry1 : error1.entrySet()) {
						PerformanceErrorTypes index = entry1.getKey();			
						out_change.append(index.toString()).append(",");	
					}//end for
					
					out_change.append("\n").append(evidencesString).append(",").append(" ,");
					
					//Do not print label again
					this.SHOW_LABELS = false;
				} else {
					out_change.append("\n").append(evidencesString).append(",").append(" ,");							
				}
				
			}//end if
			
			
			
			/***************
			 * Add the appropriate HEADER for the performance.csv file
			 */
			if (show_perc_incr_decr) {
				//Header for the percentage of increase decrease 
				out_perform.append("\t").append(",").append("Error 1,").append("Error 2,").append("Difference,").append("% Decrease,").append("% of Increase").append("\n");				
			} else if (show_perc_change) {
				out_perform.append("\t").append(",").append("Error 1,").append("Error 2,").append("Difference,").append("% Change").append("\n");	
			} else {
				out_perform.append("\t").append(",").append("Error 1,").append("Error 2,").append("Difference").append("\n");
			}			
			
			//Control the floating point
			DecimalFormat df = new DecimalFormat("#.####");
			
			for (Map.Entry<PerformanceErrorTypes, Float> entry1 : error1.entrySet()) {			
				PerformanceErrorTypes index = entry1.getKey();
				
				out_perform.append(index.toString()).append(","); //measure type
				Float err1 = entry1.getValue();
				out_perform.append(df.format(err1)).append(","); //error1
				Float err2 = error2.get(index);
				out_perform.append(df.format(err2)).append(","); //error2
				Float dif = err1 - err2;
				out_perform.append(df.format(dif)); //diff
								
				if (show_perc_change) {
					//CASE: Show percentage of change 
					Float percDecrease = ((err1 - err2) / Math.abs(err1)) * 100;
					
					//Inspired by: http://www.mathsisfun.com/numbers/percentage-change.html
					//We would like err2 to be as close to 0 as possible indicating that the error is small
										
					out_perform.append(",").append(df.format(percDecrease)).append("%"); 
					
					if (plot_perc_change) {
						out_change.append(df.format(percDecrease)).append(",");
					}					
				} else if (show_perc_incr_decr) {
					/***
					 * Here we are calculate the Percentage of Change
					 */					
					if (err1 > err2) {
						//Means Decrease
						Float percDecrease = ((err1 - err2) /  Math.abs(err1)) * 100;
						out_perform.append(",").append(df.format(percDecrease)).append("%").append(","); 
						out_perform.append(" ").append(",");
						
						if (plot_perc_incr_decr) {
							out_decrease.append(df.format(percDecrease)).append(",");
						}
						
					} else if (err2 > err1) {
						//Means Increase
						Float percIncrease = ((err2 - err1) /  Math.abs(err1)) * 100;
						out_perform.append(",").append(" ").append(",");
						out_perform.append(df.format(percIncrease)).append("%").append(",");
						
						if (plot_perc_incr_decr) {
							out_increase.append(df.format(percIncrease)).append(",");
						}
						
					} else {
						//Means no change
						out_perform.append(" ").append(",");
						out_perform.append(" ").append(",");						
					}					
				}//end if
				
				out_perform.append("\n");						 
			}//end for	
			
			//flush to file
			out_perform.close();
			
			if (out_decrease != null) {
				out_decrease.close();
			} 

			if (out_increase != null) {
				out_increase.close();
			}		
			
			if (out_change != null) {
				out_change.close();
			}	
			
		} catch (IOException e) {
	       	logger.error("Error - I/O error while writing csv performance file.");
		} 
	}//end exportToCSVFile()
		
	/**
	 * Use to plot posterior plots 
	 * 
	 * @param fileName - name of the file
	 * @param termType - .eps or .png
	 */
	public void plotPosteriorsPMF(String fileName, String termType) {
		//Open up a file
		File loc = null;		
		BufferedWriter out = null;
		
		try {
			
			//Create the file name for the GNUplot code
			StringBuilder filePath = new StringBuilder("./src/test/resources/other/");
			filePath.append(fileName).append(".plt");
			
			loc = new File(filePath.toString());
			out = new BufferedWriter(new FileWriter(loc, false));
			
			out.append("clear").append("\n");
			out.append("reset").append("\n");
			
			//set the size so as to make plots visible when .eps or .pdf in latex
			out.append("set size 0.6, 0.6").append("\n");
						
			out.append("set datafile separator \",\"").append("\n");
			
			out.append("set style fill pattern 0 border").append("\n");
			
			out.append("set key off").append("\n");
			out.append("set grid").append("\n");
			out.append("set autoscale").append("\n").append("\n");
			
			out.append("set xrange [0:1]").append("\n");
			out.append("set yrange [0:1]").append("\n");		

			out.append("set xtics 0.1").append("\n");
			out.append("set ytics 0.1").append("\n").append("\n");
			
			out.append("set xlabel \"Prior probability\"").append("offset 2").append("\n");
			out.append("set ylabel \"Resulting posterior probability\"").append("offset 2").append("\n");
						
			out.append("#use index to skip some points").append("\n");
			out.append("plot \"").append(fileName).append(".csv").append("\" using 1:3 every 3::1 w lp").append("\n");
			
			if (termType.equals("png")) {
				out.append("\nset term png enhanced size 800,600").append("\n");
				out.append("set output \"").append(fileName).append(".png\"").append("\n");
			} else if (termType.equals("eps")) {
				out.append("\nset terminal postscript eps enhanced monochrome dashed").append("\n");
				out.append("set output \"").append(fileName).append(".eps\"").append("\n");
			}
			
			out.append("replot");		
			
			//At the end close the file
			if (out != null) { out.close(); }			
		} catch (IOException e) {
			logger.error("Error - I/O error while plotting individual errors.");
		}//end catch		
	}//end plotPosteriorsPMF()
	
	
	/**
	 * Read external DOT graph from a file. 
	 * 
	 * @param inputDOTFile - Location of text file containing the Graphviz graph specification language. 
	 */
	public void readExternalDOTFile(String inputDOTFile) {
		StringBuilder tempGraph = new StringBuilder();		
		try {
			FileInputStream inputStream = new FileInputStream(inputDOTFile);
			DataInputStream dataInputStream = new DataInputStream(inputStream);
			BufferedReader buffer = new BufferedReader(new InputStreamReader(dataInputStream));
			String line;
			while ((line = buffer.readLine()) != null) {
				tempGraph.append(line);
			}//end while
			dataInputStream.close();
		} catch (Exception e) {
			logger.error("Exception - While reading DOT file: " + e.getMessage());
		}		   
		this.generalGraph = tempGraph;
	}//end readExternalDOTFile()	

	/**
	 * Returns a String representation of a Graph that was previously imported. 
	 * @return - String representation of an imported Graph in DOT language.
	 */
	public String returnGraphInDot() {
		return this.generalGraph.toString();
	}//end returnGraphInDot()
	
	/**
	 * Returns the location that this Graphviz object stores the files
	 */
	public String returnLocation() {
		return GRAPHVIZ_OUTPUT;
	}	
	
	/**
	 * From string [two, one] to two_one
	 */
	private String toStringSet(Set<BooleanVariables> iSet) {
		 String oldString = iSet.toString();
		 String newString = iSet.toString().substring(1, oldString.length()-1);
		 newString = newString.replace(", ", "_"); 	
		
		return newString;
	}
	
	private ArrayList<CanonicalModelConstruct> getConstructs(Set<CanonicalModelConstruct> inputConstructs) {
		logger.debug("in getConstructs");
		ArrayList<CanonicalModelConstruct> outputConstructs = new ArrayList<CanonicalModelConstruct>();

		/*Do not include SuperRelationships, why? - anw in RDF this is ignored*/
		for (CanonicalModelConstruct inputConstruct : inputConstructs) {
			//logger.debug("inputConstruct.getSchema.getName: " + inputConstruct.getSchema().getName());
			if (!inputConstruct.getTypeOfConstruct().equals(ConstructType.SUPER_RELATIONSHIP)) {
				//logger.debug("inputConstruct.type: " + inputConstruct.getTypeOfConstruct());				
				//logger.debug("outputConstructs.size: " + outputConstructs.size());
				outputConstructs.add(inputConstruct);
				//logger.debug("added inputConstruct.name: " + inputConstruct.getName());
				//logger.debug("outputConstructs.size: " + outputConstructs.size());
			} 
		}
		return outputConstructs;
	}
	
	/**
	 * Load Graphviz .property file from "./src/main/resources/graphviz.properties"
	 * 
	 * @param fileName
	 */
	protected void loadConfiguration(String filePath) {
	 try {
		 logger.debug("in loadConfiguration:" + filePath);
		 InputStream propertyStream = new FileInputStream(filePath);
		 Properties connectionProperties = new java.util.Properties();
		 connectionProperties.load(propertyStream);
		 //load
		 GRAPHVIZ_OUTPUT = connectionProperties.getProperty("GRAPHVIZ_OUTPUT_DIR");
		 GRAPHVIZ_DOT_WIN = connectionProperties.getProperty("GRAPHVIZ_DOT_WIN_EXE");
		 GRAPHVIZ_DOT_LINUX_MAC = connectionProperties.getProperty("GRAPHVIZ_DOT_LINUX_MAC");
    	 //print
		 logger.debug("GRAPHVIZ_OUTPUT: " + GRAPHVIZ_OUTPUT);
		 logger.debug("GRAPHVIZ_DOT_WIN: " + GRAPHVIZ_DOT_WIN);
		 logger.debug("GRAPHVIZ_DOT_LINUX_MAC: " + GRAPHVIZ_DOT_LINUX_MAC);
		} catch (FileNotFoundException exc) {
			logger.error("Exception - While loading Graphiz property file" + exc);
			exc.printStackTrace();
		} catch (IOException ioexc) {
			logger.error("Graphiz property file can not found", ioexc);
			ioexc.printStackTrace();
		}//end catch
	 }//end loadConfiguration()
}//end Class
