package uk.ac.manchester.dstoolkit.service.impl.query;

import java.util.List;

import org.apache.log4j.Logger;
import org.coinor.opents.SolutionAdapter;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;

//@Transactional(readOnly = true)
//@Scope("prototype")
//@Service
public class MapSelectionSolution extends SolutionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5179420884283818306L;

	private boolean[] selectedMappings;

	private static Logger logger = Logger.getLogger(MapSelectionSolution.class);

	public MapSelectionSolution() {
	} // Appease clone()

	public MapSelectionSolution(List<Mapping> candidateMappings) {
		logger.debug("in MapSelectionSolution");
		logger.debug("candidateMappings: " + candidateMappings);
		// Crudely initialize solution
		java.util.Random r = new java.util.Random();
		selectedMappings = new boolean[candidateMappings.size()];
		boolean mappingSelected = false;
		for (int i = 0; i < selectedMappings.length; i++) {
			logger.debug("i: " + i);
			selectedMappings[i] = r.nextBoolean();
			logger.debug("selectedMappings[i]: " + selectedMappings[i]);
			if (selectedMappings[i]) {
				mappingSelected = true;
			}
			logger.debug("mappingSelected: " + mappingSelected);
		}
		logger.debug("mappingSelected: " + mappingSelected);
		if (!mappingSelected) {
			logger.debug("no mappings selected, choose first one");
			selectedMappings[0] = true;
		}
	}

	@Override
	public Object clone() {
		logger.debug("in clone");
		for (int i = 0; i < this.selectedMappings.length; i++) {
			logger.debug("i: " + i);
			logger.debug("this.selectedMappings[i]: " + this.selectedMappings[i]);
		}
		MapSelectionSolution copy = (MapSelectionSolution) super.clone();
		copy.selectedMappings = this.selectedMappings.clone();
		logger.debug("copy.selectedMappings: " + copy.selectedMappings);
		for (int i = 0; i < copy.selectedMappings.length; i++) {
			logger.debug("i: " + i);
			logger.debug("copy.selectedMappings[i]: " + copy.selectedMappings[i]);
		}
		logger.debug("copy: " + copy);
		return copy;
	}

	@Override
	public String toString() {
		logger.debug("in toString");
		StringBuffer s = new StringBuffer();

		//s.append("Solution value: " + getObjectiveValue()[0]);
		s.append("Sequence: [ ");

		if (selectedMappings.length > 0) {
			for (int i = 0; i < selectedMappings.length - 1; i++)
				s.append(selectedMappings[i]).append(", ");

			s.append(selectedMappings[selectedMappings.length - 1]);

		}
		s.append(" ]");
		logger.debug("s.toString(): " + s.toString());

		return s.toString();
	}

	/**
	 * @return the selectedMappings
	 */
	public boolean[] getSelectedMappings() {
		return selectedMappings;
	}

	/**
	 * @param selectedMappings the selectedMappings to set
	 */
	public void setSelectedMappings(boolean[] selectedMappings) {
		this.selectedMappings = selectedMappings;
	}

}
