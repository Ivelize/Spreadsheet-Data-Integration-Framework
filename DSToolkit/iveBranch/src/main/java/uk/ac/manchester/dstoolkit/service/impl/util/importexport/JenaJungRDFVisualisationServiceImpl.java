package uk.ac.manchester.dstoolkit.service.impl.util.importexport;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.imageio.ImageIO;

import net.rootdev.jenajung.JenaJungGraph;
import net.rootdev.jenajung.Transformers;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.exceptions.DSToolkitConfigException;
import uk.ac.manchester.dstoolkit.service.util.importexport.JenaJungRDFVisualisationService;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.FileManager;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationImageServer;

/**
 * This class allows RDF graphs to be visualised and saved into a PNG image using JenaJung which is a
 * toolkit used to visualising RDF graphs in java.
 * 
 * 
 * @author klitos
 *
 */
@Service(value = "jenaJungRDFVisualisationService")
public class JenaJungRDFVisualisationServiceImpl implements JenaJungRDFVisualisationService {

	private static Logger logger = Logger.getLogger(JenaJungRDFVisualisationServiceImpl.class);
	private static String jenajungProperties = "./src/main/resources/jenajung.properties";
	
	/*Load properties*/
	private static String OUTPUT_DIR;
	private static int IMAGE_WIDTH;
	private static int IMAGE_HEIGHT;
	
	/*Constructor*/
	public JenaJungRDFVisualisationServiceImpl() {
		/*Load jenajung.properties configuration*/
		loadConfiguration(jenajungProperties);		
	}
	
	/**
	 * Construct a graph visualisation of the triples specified by the URI.
	 * 
	 * Code inspired from: https://github.com/shellac/JenaJung
	 * 
	 * @param uri - URI that contains the RDF-triples
	 */
	public void visualiseRDFGraph(String uri) {
		try {
			Model model = FileManager.get().loadModel(uri);
			
			if (model == null) throw new Exception("Jena model in NULL.");
			
	    	/*Check whether output directory is configured correctly*/ 
	        File output_dir = new File(this.OUTPUT_DIR);
	        if (!output_dir.isDirectory()) throw new DSToolkitConfigException("Error - OUTPUT_DIR is not properly configured."); 			
			
	        Graph<RDFNode, Statement> g = new JenaJungGraph(model);
	        Layout<RDFNode, Statement> layout = new FRLayout<RDFNode, Statement>(g);
	        
	        layout.setSize(new Dimension(this.IMAGE_WIDTH, this.IMAGE_HEIGHT));
	        VisualizationImageServer<RDFNode, Statement> viz = new VisualizationImageServer<RDFNode, Statement>(layout, new Dimension(this.IMAGE_WIDTH, this.IMAGE_HEIGHT));
	        RenderContext<RDFNode, Statement> context = viz.getRenderContext();
	        context.setEdgeLabelTransformer(Transformers.EDGE);
	        context.setVertexLabelTransformer(Transformers.NODE);

	        viz.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
	        Image img = viz.getImage(new Point(IMAGE_WIDTH/2, IMAGE_HEIGHT/2), new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));

	        BufferedImage bi = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
	        Graphics2D graphics2d = bi.createGraphics();
	        /*Set background colour*/
	        graphics2d.setColor(Color.white);
	        graphics2d.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

	        /*Draw image to buffer and then output as a PNG*/
	        graphics2d.setColor(Color.white);
	        graphics2d.drawImage(img, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, Color.blue, null);
	        
	        File temp = File.createTempFile("graph_", ".png", new File(OUTPUT_DIR));
	        ImageIO.write(bi, "PNG", temp);
	        logger.debug("RDF Graph PNG image saved.");
			 
		 } catch (Exception exe) {
		  	 logger.error("Error - I/O error while writing the PNG.");
		 }
	}//end visualiseRDFGraph()		
	
	/**
	 * Construct a graph visualisation of the triples specified contained by a Jena model.
	 * 
	 * @param model - Jena model
	 */
	public void visualiseRDFGraph(Model model, boolean propertyLabel, boolean nodeLabel) {
		try {
			if (model == null) throw new Exception("Jena model in NULL.");
			
	    	/*Check whether output directory is configured correctly*/ 
	        File output_dir = new File(this.OUTPUT_DIR);
	        if (!output_dir.isDirectory()) throw new DSToolkitConfigException("Error - OUTPUT_DIR is not properly configured."); 			
			
	        Graph<RDFNode, Statement> g = new JenaJungGraph(model);
	        Layout<RDFNode, Statement> layout = new FRLayout<RDFNode, Statement>(g);
	        
	        layout.setSize(new Dimension(this.IMAGE_WIDTH, this.IMAGE_HEIGHT));
	        VisualizationImageServer<RDFNode, Statement> viz = new VisualizationImageServer<RDFNode, Statement>(layout, new Dimension(this.IMAGE_WIDTH, this.IMAGE_HEIGHT));
	        RenderContext<RDFNode, Statement> context = viz.getRenderContext();
	        if (propertyLabel) { 
	        	context.setEdgeLabelTransformer(Transformers.EDGE);
	        }
	        
	        if (propertyLabel) {
	        	context.setVertexLabelTransformer(Transformers.NODE);
			}
    
	        viz.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
	        Image img = viz.getImage(new Point(IMAGE_WIDTH/4, IMAGE_HEIGHT/4), new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));

	        BufferedImage bi = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
	        Graphics2D graphics2d = bi.createGraphics();
	        /*Set background colour*/
	        graphics2d.setColor(Color.white);
	        graphics2d.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

	        /*Draw image to buffer and then output as a PNG*/
	        graphics2d.setColor(Color.white);
	        graphics2d.drawImage(img, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, Color.blue, null);
	        
	        File temp = File.createTempFile("graph_", ".png", new File(OUTPUT_DIR));
	        ImageIO.write(bi, "PNG", temp);
	        logger.debug("RDF Graph PNG image saved.");
			 
		 } catch (Exception exe) {
		  	 logger.error("Error - I/O error while writing the PNG.");
		 }
	}//end visualiseRDFGraph()		
	
	/**
	 * Load JenaJung .property file from "./src/main/resources/jenajung.properties"
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
		 OUTPUT_DIR = connectionProperties.getProperty("OUTPUT_DIR");
		 IMAGE_WIDTH = Integer.parseInt(connectionProperties.getProperty("IMAGE_WIDTH"));
		 IMAGE_HEIGHT = Integer.parseInt(connectionProperties.getProperty("IMAGE_HEIGHT"));
    	 //print
		 logger.debug("OUTPUT_DIR: " + OUTPUT_DIR);
		 logger.debug("IMAGE_WIDTH: " + IMAGE_WIDTH);
		 logger.debug("IMAGE_HEIGHT: " + IMAGE_HEIGHT);
		} catch (FileNotFoundException exc) {
			logger.error("Exception - While loading JenaJung property file" + exc);
			exc.printStackTrace();
		} catch (IOException ioexc) {
			logger.error("JenaJung property file can not found", ioexc);
			ioexc.printStackTrace();
		}//end catch
	 }//end loadConfiguration()
}//end class
