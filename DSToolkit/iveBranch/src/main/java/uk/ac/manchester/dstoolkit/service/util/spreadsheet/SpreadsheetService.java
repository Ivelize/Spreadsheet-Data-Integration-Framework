package uk.ac.manchester.dstoolkit.service.util.spreadsheet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.smartcardio.ATR;

import cerg.ddex.openspreadsheet.director.XLSReader;

import com.mysql.jdbc.Statement;

public class SpreadsheetService{
	
	public ArrayList<CellPOJO> loadSpreadsheets(String directoryPath, String spreadsheetName){
		
		SpreadsheetProcessing spreadsheetBuilder = new SpreadsheetProcessing();
		if (spreadsheetName.endsWith("xls")){
			try {
            	XLSReader reader = new XLSReader(directoryPath);
				reader.build(spreadsheetBuilder);
			} catch (Exception e) {
				System.out.println("erro: "+ e.toString());
			}
		}
		
		return spreadsheetBuilder.getResult();
	}
	
	
	public Boolean saveSpreadsheetSchemaDatabase(String schemaName, String tableName, List<String> atributtes) throws Exception{
		
		SpreadsheetModel spreadModel = new SpreadsheetModel();
		Boolean result = spreadModel.createSpreadsheetSchema(schemaName, tableName, atributtes);
		
		return result;
		
	}
	
	public void createSchemaToSpreadsheet(String nameDB){
		
		   // JDBC driver name and database URL  
		    String DB_URL = "jdbc:mysql://localhost/";

		   //  Database credentials
		   String USER = "root";
		   String PASS = "root";
		   
		   Connection conn = null;
		   Statement stmt = null;
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL, USER, PASS);

		      //STEP 4: Execute a query
		      System.out.println("Creating database...");
		      stmt = (Statement) conn.createStatement();
		      
		      String sql = "CREATE DATABASE " + nameDB;
		      stmt.executeUpdate(sql);
		      System.out.println("Database created successfully...");

		   }catch(Exception e){

		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		   System.out.println("Goodbye!");
	}//end createSchemaToSpreadsheet
	
	public void dropSpreadsheetSchema(String nameDB){
	
	 // JDBC driver name and database URL
	   String DB_URL = "jdbc:mysql://localhost/";

	   //  Database credentials
	   String USER = "root";
	   String PASS = "root";
	   
	   Connection conn = null;
	   Statement stmt = null;
	   try{
	      //STEP 2: Register JDBC driver
	      Class.forName("com.mysql.jdbc.Driver");

	      //STEP 3: Open a connection
	      System.out.println("Connecting to a selected database...");
	      conn = DriverManager.getConnection(DB_URL, USER, PASS);
	      System.out.println("Connected database successfully...");
	      
	      //STEP 4: Execute a query
	      System.out.println("Deleting database...");
	      stmt = (Statement) conn.createStatement();
	      
	      String sql = "DROP DATABASE " + nameDB;
	      stmt.executeUpdate(sql);
	      System.out.println("Database deleted successfully...");
	   }catch(SQLException se){
	      //Handle errors for JDBC
	      se.printStackTrace();
	   }catch(Exception e){
	      //Handle errors for Class.forName
	      e.printStackTrace();
	   }finally{
	      //finally block used to close resources
	      try{
	         if(stmt!=null)
	            conn.close();
	      }catch(SQLException se){
	      }// do nothing
	      try{
	         if(conn!=null)
	            conn.close();
	      }catch(SQLException se){
	         se.printStackTrace();
	      }//end finally try
	   }//end try
	   System.out.println("Goodbye!");
	}//end dropSpreadsheetSchema()
	
	public void uploadSpreadsheet(File[] spreadsheetFiles, String source_target){
		
		for (int k = 0; k < spreadsheetFiles.length; k++) {
			
			List<String> lstAtributtes = new ArrayList<String>();
			
			if (spreadsheetFiles[k].getName().endsWith("xls") || spreadsheetFiles[k].getName().endsWith("csv")){
			
				String[] name = spreadsheetFiles[k].getName().split("[.]");
				String nameDB = "";
				
				if(source_target == "source"){
					nameDB = /*"dryad_" + source_target + "_" + */name[0].replaceAll("[^a-zA-Z0-9]", "");
				}else{
					nameDB = "schema" + source_target + "_" + name[0].replaceAll("[^a-zA-Z0-9]", "");
				}
				
				this.createSchemaToSpreadsheet(nameDB);
				
				System.out.println("Processando: "+ nameDB);
				
				try{
					if (spreadsheetFiles[k].getName().endsWith("xls")){
						// contem tudo o que o DDex conseguiu ler
						List<CellPOJO> spread = this.loadSpreadsheets(spreadsheetFiles[k].getPath(), spreadsheetFiles[k].getName());
						
						for (CellPOJO cellPOJO : spread) {
							if(cellPOJO.getRow() == 0){
								lstAtributtes.add(cellPOJO.getContent().replaceAll("[^a-zA-Z]", ""));
							}
						}
						
					}else if (spreadsheetFiles[k].getName().endsWith("csv")){
							
						BufferedReader reader = new BufferedReader(new FileReader(spreadsheetFiles[k]));
								
						//String content = "";
								
						//while((content = reader.readLine()) != null){	
								
							String[] listCSVSchema = reader.readLine().trim().split(",");
								
							for (String atributte : listCSVSchema) {
								lstAtributtes.add(atributte.replaceAll("[^a-zA-Z]", ""));
							}
						//}
					}
						
					
					if (this.saveSpreadsheetSchemaDatabase(nameDB, name[0].replaceAll("[^a-zA-Z]", ""), lstAtributtes)){
						
					}else{
						System.out.println("DEU CERTO!!!");
					}
					
				} catch (Exception e) {
					if(source_target == "source"){
						File file = new File(Constant.DIRECTORY_PATH_SPREADSHEETS_SOURCE + spreadsheetFiles[k].getName());
						//file.delete();
					}else{
						File file = new File(Constant.DIRECTORY_PATH_SPREADSHEETS_TARGET + spreadsheetFiles[k].getName());
						//file.delete();
					}
					this.dropSpreadsheetSchema(nameDB);
					e.printStackTrace();
				}

			}
		}//end for*/
	}
	
	public void selectFiles() throws Exception{
		
		BufferedReader reader = new BufferedReader(new FileReader(Constant.DIRECTORY_PATH_SPREADSHEETS_TESTE));
		
		File spreadsheetSourceSpreadsheets[];
		File spreadsheetsDirectory = new File(Constant.DIRECTORY_PATH_SPREADSHEETS_SOURCE);
		spreadsheetSourceSpreadsheets = spreadsheetsDirectory.listFiles();
		
		String line = new String();
		Set<String> aux = new HashSet<String>();
		
		while((line = reader.readLine()) != null) {
			aux.add(line);
        } 
		
		for (int i = 0; i < spreadsheetSourceSpreadsheets.length; i++) {
			
			String[] spreadsheetName = spreadsheetSourceSpreadsheets[i].getName().split("[.]");
			
			if(!aux.contains(spreadsheetName[0].replaceAll("[^a-zA-Z0-9]", ""))){
				File filed = new File(Constant.DIRECTORY_PATH_SPREADSHEETS_SOURCE + spreadsheetSourceSpreadsheets[i].getName());
				filed.delete();
			}
			
		}
	
	}
	
	public void countPairAttributesInSpreadsheet(File[] spreadsheetFiles) throws Exception {
		
		Map<String, Integer> attributesCount = new HashMap<String, Integer>();
		
		for (int k = 0; k < spreadsheetFiles.length; k++) {
			
			if (spreadsheetFiles[k].getName().endsWith("xls") || spreadsheetFiles[k].getName().endsWith("csv")){

				Set<String> lstAttributes = new TreeSet<String>();
				Set<String> lstAttributes2 = new TreeSet<String>();
				
				if (spreadsheetFiles[k].getName().endsWith("xls")){
					// contem tudo o que o DDex conseguiu ler
					List<CellPOJO> spread = this.loadSpreadsheets(spreadsheetFiles[k].getPath(), spreadsheetFiles[k].getName());
					
					System.out.println("Processando: "+ spreadsheetFiles[k].getName());
			
					if (spread.size() != 0){
						for (CellPOJO cellPOJO : spread) {
							if(cellPOJO.getRow() == 0){
								lstAttributes.add(cellPOJO.getContent().replaceAll("[^a-zA-Z0-9]", ""));
							}else{
								break;
							}
						}
					}
				}else if (spreadsheetFiles[k].getName().endsWith("csv")){
				
					BufferedReader reader = new BufferedReader(new FileReader(spreadsheetFiles[k]));
							
					String [] listCSVSchema =reader.readLine().trim().split(",");
							
					for (String atributte : listCSVSchema) {
						lstAttributes.add(atributte.replaceAll("[^a-zA-Z0-9]", ""));
					}
				}
					
				lstAttributes2.addAll(lstAttributes);
				
				for (String attribute1 : lstAttributes) {
					
					lstAttributes2.remove(attribute1);
					
					for (String attribute2 : lstAttributes2){
						
						if(!attribute1.equals(attribute2)){
							
							String pairAttributes = attribute1 + " " + attribute2;
							
							if (attributesCount.containsKey(pairAttributes)){
								attributesCount.put(pairAttributes, attributesCount.get(pairAttributes)+1);
							}else{
								attributesCount.put(pairAttributes, 1);
							}
						}
						
					}
				}

			}//end if xls
		
		}//end for*/
		
		File file = new File(Constant.DIRECTORY_FILE_SPREADSHEETS_ATTRIBUTES_CORRESPONDENCE_RESULT_GRAPH);
		PrintWriter pw;
		
	    // if file doesnt exists, then create it
	    if (!file.exists()) {
	        file.createNewFile();
	        pw = new PrintWriter(file.getAbsoluteFile());
	    }else{
	    	pw = new PrintWriter(new FileOutputStream(file.getAbsoluteFile(), true));
	    }
	    
	    for (Entry<String, Integer> pairAtributtes : attributesCount.entrySet()) {
	    	 
	    	pw.println(pairAtributtes.getKey() + ":" + pairAtributtes.getValue());
		}

	    pw.close();	
	}

	public void countAttributesInSpreadsheet(File[] spreadsheetFiles) throws Exception {
		
		Map<String, Integer> attributesCount = new HashMap<String, Integer>();
		
		for (int k = 0; k < spreadsheetFiles.length; k++) {
			
			if (spreadsheetFiles[k].getName().endsWith("xls") || spreadsheetFiles[k].getName().endsWith("csv")){

				Set<String> lstAttributes = new TreeSet<String>();
				Set<String> lstAttributes2 = new TreeSet<String>();
				
				if (spreadsheetFiles[k].getName().endsWith("xls")){
					// contem tudo o que o DDex conseguiu ler
					List<CellPOJO> spread = this.loadSpreadsheets(spreadsheetFiles[k].getPath(), spreadsheetFiles[k].getName());
					
					System.out.println("Processando: "+ spreadsheetFiles[k].getName());
			
					if (spread.size() != 0){
						for (CellPOJO cellPOJO : spread) {
							if(cellPOJO.getRow() == 0){
								lstAttributes.add(cellPOJO.getContent().replaceAll("[^a-zA-Z0-9]", ""));
							}else{
								break;
							}
						}
					}
				}else if (spreadsheetFiles[k].getName().endsWith("csv")){
				
					BufferedReader reader = new BufferedReader(new FileReader(spreadsheetFiles[k]));
							
					String [] listCSVSchema =reader.readLine().trim().split(",");
							
					for (String atributte : listCSVSchema) {
						lstAttributes.add(atributte.replaceAll("[^a-zA-Z0-9]", ""));
					}
				}
					
				lstAttributes2.addAll(lstAttributes);
				
				for (String attribute1 : lstAttributes) {
							
					if (attributesCount.containsKey(attribute1)){
						attributesCount.put(attribute1, attributesCount.get(attribute1)+1);
					}else{
						attributesCount.put(attribute1, 1);
					}

				}

			}//end if xls
		
		}//end for*/
		
		File file = new File(Constant.DIRECTORY_FILE_SPREADSHEETS_ATTRIBUTES_CORRESPONDENCE_RESULT_GRAPH);
		PrintWriter pw;
		
	    // if file doesnt exists, then create it
	    if (!file.exists()) {
	        file.createNewFile();
	        pw = new PrintWriter(file.getAbsoluteFile());
	    }else{
	    	pw = new PrintWriter(new FileOutputStream(file.getAbsoluteFile(), true));
	    }
	    
	    for (Entry<String, Integer> pairAtributtes : attributesCount.entrySet()) {
	    	 
	    	pw.println(pairAtributtes.getKey() + ":" + pairAtributtes.getValue());
		}

	    pw.close();	
	}
	
	public void getAttributesInSpreadsheet(File[] spreadsheetFiles) throws Exception {
		
		List<String> attributesFormatted = new ArrayList<String>();
		
		for (int k = 0; k < spreadsheetFiles.length; k++) {
			
			if (spreadsheetFiles[k].getName().endsWith("xls") || spreadsheetFiles[k].getName().endsWith("csv")){

				List<String> lstAttributes = new ArrayList<String>();
				
				if (spreadsheetFiles[k].getName().endsWith("xls")){
					// contem tudo o que o DDex conseguiu ler
					List<CellPOJO> spread = this.loadSpreadsheets(spreadsheetFiles[k].getPath(), spreadsheetFiles[k].getName());
					
					System.out.println("Processando: "+ spreadsheetFiles[k].getName());
			
					if (spread.size() != 0){
						for (CellPOJO cellPOJO : spread) {
							if(cellPOJO.getRow() == 0){
								lstAttributes.add(cellPOJO.getContent().replaceAll("[^a-zA-Z0-9]", ""));
							}else{
								break;
							}
						}
					}
				}else if (spreadsheetFiles[k].getName().endsWith("csv")){
				
					BufferedReader reader = new BufferedReader(new FileReader(spreadsheetFiles[k]));
							
					String [] listCSVSchema =reader.readLine().trim().split(",");
							
					for (String atributte : listCSVSchema) {
						lstAttributes.add(atributte.replaceAll("[^a-zA-Z0-9]", ""));
					}
				}
				
				int max = 10;
				if(lstAttributes.size() > 10){
					StringBuffer str = new StringBuffer();
					for (int i = 0; i < max; i++) {
						if(i == max-1){
							str.append(lstAttributes.get(i) + "," + spreadsheetFiles[k].getName().replaceAll("[^a-zA-Z0-9]", ""));
						}else{
							str.append(lstAttributes.get(i) + ",");
						}
					}
			
					attributesFormatted.add(str.toString());
				}
				
				
					

			}//end if xls
		
		}//end for*/
		
		File file = new File(Constant.DIRECTORY_PATH_SPREADSHEETS_TESTE);
		PrintWriter pw;
		
	    // if file doesnt exists, then create it
	    if (!file.exists()) {
	        file.createNewFile();
	        pw = new PrintWriter(file.getAbsoluteFile());
	    }else{
	    	pw = new PrintWriter(new FileOutputStream(file.getAbsoluteFile(), true));
	    }
	    
	    for (String string : attributesFormatted) {
			
	    	pw.println(string);
	    	
		}

	    pw.close();	

	  
	}


}
