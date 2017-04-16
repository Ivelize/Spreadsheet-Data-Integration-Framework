package uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators;

import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.Predicate;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators.EvaluatorOperator;

@Scope("prototype")
@Service
public class HashLoopsJoinOperatorImpl extends JoinOperatorImpl { //implements AttributeSensitiveOperator {

	private static final Logger logger = Logger.getLogger(HashLoopsJoinOperatorImpl.class);

	public HashLoopsJoinOperatorImpl(Set<Predicate> predicates, EvaluatorOperator lhsInput, EvaluatorOperator rhsInput, ResultType resultType,
			long cardinality, Set<Predicate> joinPredicatesCarried, DataSource dataSource) {
		super(predicates, lhsInput, rhsInput, resultType, cardinality, joinPredicatesCarried, dataSource);
	}

	public HashLoopsJoinOperatorImpl(Set<Predicate> predicates, String reconcilingExpression, EvaluatorOperator lhsInput, EvaluatorOperator rhsInput,
			ResultType resultType, long cardinality, Set<Predicate> joinPredicatesCarried, DataSource dataSource) {
		super(predicates, reconcilingExpression, lhsInput, rhsInput, resultType, cardinality, joinPredicatesCarried, dataSource);
	}

	/*
	HashLoopsJoinOperatorImpl hlj = new HashLoopsJoinOperatorImpl(scan.getSuperAbstractReference(), logicalJoin.getLeftInput(), predicates, scan
			.getPredicates(), logicalJoin.getCardinality(), scan.getCardinality(), logicalJoin.getJoinPredicatesCarried(), scan.getSourceId());
	
	HashLoopsJoinOperatorImpl hlj = new HashLoopsJoinOperatorImpl(predicates, join.getLhsInput(), join.getRhsInput(), join.getResultType(),
			long cardinality, List<Predicate> joinPredicatesCarried, DataSource dataSource)
	*/

	@Override
	public boolean close() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	@Override
	public List<ResultInstance> getResultInstances() {
		// TODO Auto-generated method stub
		return null;
	}
	*/

	@Override
	public ResultInstance next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean open() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HashLoopsJoinOperatorImpl [");
		if (super.toString() != null)
			builder.append("toString()=").append(super.toString());
		builder.append("]");
		return builder.toString();
	}

	/*
	private Collection<Predicate> joinPredicates;
	private Collection<Predicate> scanPredicates;
	 
	private SuperAbstractReference superAbstractReference;
	
	private long scanCardinality;
	
	public HashLoopsJoinOperator(SuperAbstractReference superAbstractReference, Operator inputOperator, Collection<Predicate> joinPredicates, long joinCardinality, long scanCardinality, Collection<Predicate> joinPredicatesCarried, Long sourceId) {
	    super(joinCardinality, joinPredicatesCarried, sourceId);
	    this.superAbstractReference = superAbstractReference;
	    this.inputs = new Operator[] {inputOperator};
	    this.joinPredicates = joinPredicates;
	    this.scanPredicates = new HashSet<Predicate>();
	    this.scanCardinality = scanCardinality;
	}
	
	public HashLoopsJoinOperator(SuperAbstractReference superAbstractReference, Operator inputOperator, Collection<Predicate> joinPredicates, Collection<Predicate> scanPredicates, long joinCardinality, long scanCardinality, Collection<Predicate> joinPredicatesCarried, Long sourceId) {
	    this(superAbstractReference,inputOperator,joinPredicates,joinCardinality,scanCardinality, joinPredicatesCarried, sourceId);
	    this.scanPredicates = scanPredicates;   
	}
	
	public long getScanCardinality() { return this.scanCardinality; }   
	public Collection<Predicate> getJoinPredicates() { return joinPredicates; }
	public Collection<Predicate> getScanPredicates() { return scanPredicates; }
	public SuperAbstractReference getSuperAbstractReference() { return superAbstractReference; }
	public Operator getInputOperator() { return inputs[0]; }
	*/

	/**
	 * When join predicates are read by the evaluator, there is a sensitivity
	 * to the order of the predicate operands. e.g. a.1 = b.2 will only work
	 * if a.1 is in the left input and b.2 is in the right input. This method
	 * ensures that predicate operands are correctly ordered in this way.
	 * This method will only work once that the tupleType field has been assigned.
	 */
	/*
	public void orderPredicateOperands() throws QueryCompilerException {
	    Iterator<Predicate> it = joinPredicates.iterator();
	    while(it.hasNext()) {
	        Predicate p = (Predicate) it.next();
	        if (! inputs[0].getResultType().getResultFields().contains(p.getArgument1()) ) {
	            //TODO this check here won't work --- sort this
	        	p.reverseOperands();                
	        }
	    }
	}
	 
	public String toString() {
	    String s = "HASH_LOOPS_JOIN " + superAbstractReference.toString() + " (";
	    Iterator<Predicate> jit = joinPredicates.iterator();
	    boolean first = true;
	    while( jit.hasNext() ) {
	        if ( !first ) s = s + ", ";
	        s = s + jit.next().toString();
	        first = false;
	    }
	    Iterator<Predicate> pit = scanPredicates.iterator();
	    while( pit.hasNext() ) {
	        if ( !first ) s = s + ", ";
	        s = s + pit.next().toString();
	        first = false;
	    }
	    return s + ") [ " + inputs[0].toString() + " ]";     
	}
	*/
}
