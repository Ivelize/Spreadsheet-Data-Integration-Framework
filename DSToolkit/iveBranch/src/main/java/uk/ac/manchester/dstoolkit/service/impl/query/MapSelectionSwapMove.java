package uk.ac.manchester.dstoolkit.service.impl.query;

import org.apache.log4j.Logger;
import org.coinor.opents.Move;
import org.coinor.opents.Solution;

//@Transactional(readOnly = true)
//@Scope("prototype")
//@Service
public class MapSelectionSwapMove implements Move {

	/**
	 * 
	 */
	private static final long serialVersionUID = 362326353122508336L;
	private int mapping_index;
	private boolean movement;

	private static Logger logger = Logger.getLogger(MapSelectionSwapMove.class);

	public MapSelectionSwapMove(int mapping_index, boolean movement) {
		logger.debug("in MapSelectionSwapMove");
		logger.debug("mapping_index: " + mapping_index);
		logger.debug("movement: " + movement);

		this.mapping_index = mapping_index;
		this.movement = movement;
	}

	public void operateOn(Solution solution) {
		logger.debug("in operateOn");
		logger.debug("mapping_index: " + mapping_index);
		logger.debug("movement: " + movement);
		logger.debug("solution: " + solution);
		boolean[] selectedMappings = ((MapSelectionSolution) solution).getSelectedMappings();
		logger.debug("selectedMappings: " + selectedMappings);
		logger.debug("selectedMappings[mapping_index]: " + selectedMappings[mapping_index]);
		//if (movement) {
		if (selectedMappings[mapping_index])
			selectedMappings[mapping_index] = false;
		else
			selectedMappings[mapping_index] = true;
		//}
		logger.debug("selectedMappings[mapping_index]: " + selectedMappings[mapping_index]);
		logger.debug("operateOn in swapMove invoked, the mapping with the index " + mapping_index + " was modified");
		logger.debug("solution: " + solution);
	}

	//same code as in operateOn
	public void undoOperation(Solution solution) {
		logger.debug("in undoOperation");
		logger.debug("mapping_index: " + mapping_index);
		logger.debug("movement: " + movement);
		logger.debug("solution: " + solution);
		boolean[] selectedMappings = ((MapSelectionSolution) solution).getSelectedMappings();
		logger.debug("selectedMappings: " + selectedMappings);
		logger.debug("selectedMappings[mapping_index]: " + selectedMappings[mapping_index]);
		//if (movement) {
		if (selectedMappings[mapping_index])
			selectedMappings[mapping_index] = false;
		else
			selectedMappings[mapping_index] = true;
		//}
		logger.debug("selectedMappings[mapping_index]: " + selectedMappings[mapping_index]);
		logger.debug("undoOperation in swapMove invoked, the mapping with the index " + mapping_index + " was modified, i.e., modification undone");
		logger.debug("solution: " + solution);
	}

	@Override
	public String toString() {
		return "Move - mapping_index: " + mapping_index + ", movement: " + movement;
	}

	/** Identify a move for SimpleTabuList */
	@Override
	public int hashCode() {
		return new Boolean(movement).hashCode() + new Integer(mapping_index).hashCode();
	}

	/**
	 * @return the mapping_index
	 */
	public int getMapping_index() {
		return mapping_index;
	}

	/**
	 * @param mappingIndex the mapping_index to set
	 */
	public void setMapping_index(int mappingIndex) {
		mapping_index = mappingIndex;
	}

	/**
	 * @return the movement
	 */
	public boolean isMovement() {
		return movement;
	}

	/**
	 * @param movement the movement to set
	 */
	public void setMovement(boolean movement) {
		this.movement = movement;
	}

}
