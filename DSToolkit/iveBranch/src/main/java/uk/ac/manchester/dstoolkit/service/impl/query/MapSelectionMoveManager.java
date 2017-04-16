package uk.ac.manchester.dstoolkit.service.impl.query;

import java.util.Random;

import org.apache.log4j.Logger;
import org.coinor.opents.Move;
import org.coinor.opents.MoveManager;
import org.coinor.opents.Solution;

//@Transactional(readOnly = true)
//@Scope("prototype")
//@Service
public class MapSelectionMoveManager implements MoveManager {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3634578686563215485L;

	private static Logger logger = Logger.getLogger(MapSelectionMoveManager.class);

	Random r = new Random();
	int k;

	public Move[] getAllMoves(Solution solution) {
		logger.debug("in getAllMoves");
		boolean[] selectedMappings = ((MapSelectionSolution) solution).getSelectedMappings();
		logger.debug("selectedMappings: " + selectedMappings);
		Move[] moves = new Move[selectedMappings.length];
		logger.debug("moves: " + moves);

		for (int i = 0; i < selectedMappings.length; i++) {
			logger.debug("new move");
			logger.debug("i: " + i);
			if (selectedMappings[i])
				moves[i] = new MapSelectionSwapMove(i, false);
			else
				moves[i] = new MapSelectionSwapMove(i, true);
			logger.debug("moves[i]: " + moves[i]);
		}
		logger.debug("moves: " + moves);

		/*
		Move[] moves = new Move[1];
		k = r.nextInt(selectedMappings.length - 1);
		
		if (selectedMappings[k])
			moves[0] = new MapSelectionSwapMove( k, false );
		else
			moves[0] = new MapSelectionSwapMove( k, true );
		*/

		/*
		for( int i = 0; i < selectedMappings.length; i++ ) {

				moves[i] = new MapSelectionSwapMove(i, false );
			
				moves[selectedMappings.length * 2 -1 - i] = new MapSelectionSwapMove( i, true );
		
		}
		*/

		/*
		Move[] buffer = new Move[ selectedMappings.length*selectedMappings.length ];
		int nextBufferPos = 0;
		
		// Generate moves that move each customer
		// forward and back up to five spaces.
		for( int i = 0; i < selectedMappings.length; i++ ) 
			if (selectedMappings[i] = true)
				buffer[nextBufferPos++] = new MapSelectionSwapMove(i, false );
			else
				buffer[nextBufferPos++] = new MapSelectionSwapMove( i, true );
		 
		// Trim buffer
		Move[] moves = new Move[ nextBufferPos];
		System.arraycopy( buffer, 0, moves, 0, nextBufferPos );
		*/

		logger.debug("All moves are generated");

		return moves;
	} // end getAllMoves

} // end class MyMoveManager
