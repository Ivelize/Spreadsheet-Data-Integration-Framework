package uk.ac.manchester.dstoolkit.service.util.spreadsheet;

import java.util.ArrayList;

public class SpreadsheetProcessing extends AbstractBuilder{
	
	ArrayList<CellPOJO> spreadLst = new ArrayList<CellPOJO>();
	
	public void foundCellContentAsString(int page, int row, int cell, String content) {
		//System.out.println("celula: " + celula + ", linha: " + linha + ", conteudo: " + conteudo);	
		
		CellPOJO spreadContent = new CellPOJO();
		spreadContent.setCell(cell);
		spreadContent.setRow(row);
		spreadContent.setPage(page);
		spreadContent.setContent(content.toLowerCase().trim());
		
		spreadLst.add(spreadContent);

	}
	
	public ArrayList<CellPOJO> getResult()
	{
		return spreadLst;
	}

}
