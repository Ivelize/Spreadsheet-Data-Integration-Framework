package uk.ac.manchester.dstoolkit.service.util.importexport.schematiccorrespondences;

import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

public interface ImportSchematicCorrespondencesFromXMLService {

	public abstract void readSchematicCorrespondencesFromXml(String fileLocation) throws JAXBException, FileNotFoundException;

}