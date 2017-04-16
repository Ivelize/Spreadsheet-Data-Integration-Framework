package uk.ac.manchester.dstoolkit.service.util.spreadsheet;

import java.sql.DriverManager;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.service.DataspaceService;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class SpreadsheetModel {
	
	@Autowired
	@Qualifier("spreadsheetService")
	private SpreadsheetService spreadsheetService;
	
	static Logger logger = Logger.getLogger(SpreadsheetModel.class);

	public Boolean createSpreadsheetSchema(String schemaName, String tableName, List<String> atributtes) throws Exception{
		
		Boolean result = false;
	
		Class.forName("com.mysql.jdbc.Driver"); 
    	Connection conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/"+schemaName, "root", "root");
       
    	Statement st = (Statement) conn.createStatement();
    	
    	StringBuffer createTable = new StringBuffer();
    	createTable.append("CREATE TABLE ");
    	createTable.append(tableName.replaceAll(" ", "").replaceAll("_", "").trim() + "(");
    	for (int i = 0; i < atributtes.size(); i++) {
    		
    		if(atributtes.get(i).equalsIgnoreCase("date")){
    			atributtes.set(i, "eventdate");	
    		}
    		
    		if(atributtes.get(i).equalsIgnoreCase("order")){
    			atributtes.set(i, "orderr");	
    		}
    		
    		if(atributtes.get(i).equalsIgnoreCase("group")){
    			atributtes.set(i, "groupp");	
    		}
    		
    		if(atributtes.get(i).equalsIgnoreCase("references")){
    			atributtes.set(i, "referencess");	
    		}
    		
    		if(atributtes.get(i).equalsIgnoreCase("range")){
    			atributtes.set(i, "rangee");	
    		}
    		
    		if((i+1) != atributtes.size()){
    			createTable.append(atributtes.get(i).replaceAll(" ", "").replaceAll("_", "").trim() + " VARCHAR(50), ");
    		}else{
    			createTable.append(atributtes.get(i).replaceAll(" ", "").replaceAll("_", "").trim()  + " VARCHAR(50));");
    		}
		} 
    	
    	result = st.execute(createTable.toString());
    	
    	st.close();
    	conn.close();

		return result;
	}
	

	    

	    



}
