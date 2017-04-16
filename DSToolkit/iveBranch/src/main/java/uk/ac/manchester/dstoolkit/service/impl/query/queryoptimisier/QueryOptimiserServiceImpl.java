package uk.ac.manchester.dstoolkit.service.impl.query.queryoptimisier;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.exceptions.KeyNotPresentException;
import uk.ac.manchester.dstoolkit.exceptions.LookupException;
import uk.ac.manchester.dstoolkit.service.query.queryoptimisier.QueryOptimiserService;

//@Transactional(readOnly = true)
@Service(value = "queryOptimiserService")
public class QueryOptimiserServiceImpl implements QueryOptimiserService {

	private static Logger logger = Logger.getLogger(QueryOptimiserServiceImpl.class);

	public QueryOptimiserServiceImpl() {
	}

	//TODO this doesn't take into account whether the key is a composite key
	protected boolean isPrimaryKey(SuperLexical superLexical) throws LookupException {
		logger.debug("in isPrimaryKey");
		logger.debug("superLexical: " + superLexical);
		try {
			logger.debug("isPrimaryKey: " + superLexical.getIsIdentifier());
			if (superLexical.getIsIdentifier() != null && superLexical.getIsIdentifier())
				return true;
		} catch (KeyNotPresentException e) {
		}
		return false;
	}

	protected static boolean isSuperLexicalOfSuperAbstract(SuperLexical superLexical, SuperAbstract superAbstract) {
		logger.debug("in isSuperLexicalOfSuperAbstract");
		logger.debug("superLexical: " + superLexical);
		logger.debug("superAbstract: " + superAbstract);
		//TODO getting parentSuperAbstract might not work in all cases, e.g., XML - sort this
		return (superAbstract.equals(superLexical.getParentSuperAbstract()));
	}

	/**
	 * This method assigns tuple types to all operators
	 * in a query plan (i.e. invokes setTupleType on the operator
	 * and all operators positioned below the operator in 
	 * a query plan). Can be used to update tuple types if
	 * the query plan has changed.
	 */
	/*
	public void assignResultTypes(Operator operator) throws LookupException {
		logger.debug("in assignResultTypes");
		if (operator instanceof JoinOperator) {
			logger.debug("operator is JoinOperator");
			
			// The tuple type is made up of the left input
			// fields + the right input fields
			 
			JoinOperator join = (JoinOperator) operator;
			assignResultTypes(join.getLeftInput());
			assignResultTypes(join.getRightInput());
			ResultType t = new ResultType(join.getLeftInput().getResultType());
			//TODO not sure this will work properly, check this
			t.merge(join.getRightInput().getResultType());
			logger.debug("Set join tuple result: " + t.toString());
			operator.setResultType(t);
		} else if (operator instanceof HashLoopsJoinOperator) {
			logger.debug("operator is HashLoopsJoinOperator");
			
			// The tuple type is made up of the input fields
			// + the attributes of the scanned relation
			
			HashLoopsJoinOperator join = (HashLoopsJoinOperator) operator;
			assignResultTypes(join.getInputOperator());
			ResultType t = new ResultType(join.getInputOperator().getResultType());
			SuperAbstractReference r = join.getSuperAbstractReference();
		
			// Add all fields of the scanned relation to the
			// tuple type
			
			//String superAbstractName = r.getSuperAbstractName();
			//String var = r.getVariableName();
			//TODO get all superLexicals of superAbstract
			Set<SuperLexical> superLexicals = r.getSuperAbstract().getSuperLexicals();
			
			//String[] attribs = data.getAttributes(table);
			//for(int i=0;i<attribs.length;i++) {
			//	Class type = data.lookupAttributeType(table,attribs[i]);
			//	t.add( new SuperLexicalReference(r,attribs[i],new Class[] {type} ) );
			//}
			
			for (SuperLexical superLexical : superLexicals) {
				//TODO sort out type of superlexicals
				//Class[] type = {Types.mapDataTypeToClassOfType(superLexical.getDataType())};
				DataType dataType = superLexical.getDataType();
				//t.add(new SuperLexicalReference(r, superLexical.getName(), superLexical, type));
				ResultField resultField = new ResultField(superLexical.getName(), dataType);
				t.addResultField(resultField);
			}
			logger.debug("Set hash loops join result type: " + t.toString());
			operator.setResultType(t);
		} else if (operator instanceof ReduceOperator) {
			logger.debug("operator is ReduceOperator");
			ReduceOperator reduce = (ReduceOperator) operator;
			if (reduce.getProjectList() != null) {
				
				// Its a project reduce
				
				Iterator<QueryElement> it = reduce.getProjectList().iterator();
				
				// Call assignTupleTypes on the child operator,
				// although the tuple type is not added to the projected
				// fields
				
				Operator input = operator.getInput(0);
				assignResultTypes(input);
				ResultType t = new ResultType();
			
				// Add all projected fields
				
				while (it.hasNext()) {
					QueryElement queryElement = it.next();
					if (queryElement instanceof Variable) {
						Variable variable = (Variable) queryElement;
						ResultField resultField = new ResultField(variable.getVariableName(), variable.getDataTypes().get(0)); //TODO check that this works
						//t.add( (QueryElement) it.next(). );
						t.addResultField(resultField);
					} else
						logger.error("queryElement isn't a variable --- check what to do with resultType"); //TODO check this here, doesn't look right
				}
				logger.debug("Set reduce(project) result type: " + t.toString());
				operator.setResultType(t);
			} else {
				logger.debug("else: operator is assumed to be aggregate - TODO check");
				//TODO check operator can't be anything else
				
				// Its an aggregate reduce
				
				Aggregate agg = reduce.getAggregate();
				
				// Call assignTupleTypes on the child operator
				// and inherit the input fields
				
				Operator input = operator.getInput(0);
				assignResultTypes(input);
				ResultType t = new ResultType();
				ResultField resultField = new ResultField(agg.getVariableName(), agg.getDataTypes().get(0));
				t.addResultField(resultField);
				//t.add( agg );
				logger.debug("Set reduce(aggregate) result type: " + t.toString());
				operator.setResultType(t);
			}
		} else if (operator instanceof OperationCallOperator) {
			logger.debug("operator is OperationCallOperator");
			OperationCallOperator opCall = (OperationCallOperator) operator;
		
			// Get the input tuple type
			
			Operator input = operator.getInput(0);
			assignResultTypes(input);
			ResultType t = new ResultType(input.getResultType());
			
			// Add the result of the operation call
			
			Function function = opCall.getFunction();
			ResultField resultField = new ResultField(function.getVariableName(), function.getDataTypes().get(0)); //TODO check that this will work
			//t.add(opCall.getFunction());
			t.addResultField(resultField);
			operator.setResultType(t);
			logger.debug("Set operation call result type: " + t.toString());
		} else if (operator instanceof ScanOperator) {
			logger.debug("operator is ScanOperator");
			ResultType t = new ResultType();
			SuperAbstractReference r = ((ScanOperator) operator).getSuperAbstractReference();
			Set<SuperLexical> superLexicals = r.getSuperAbstract().getSuperLexicals();
			for (SuperLexical superLexical : superLexicals) {
				//TODO sort out type of superlexicals
				//Class[] type = {Types.mapDataTypeToClassOfType(superLexical.getDataType())};
				//t.add(new SuperLexicalReference(r, superLexical.getName(), superLexical, type));
				ResultField resultField = new ResultField(superLexical.getName(), superLexical.getDataType());
				t.addResultField(resultField);
			}
			//t.addAll(getSuperLexicalsOfSuperAbstract(r));
			operator.setResultType(t);
			logger.debug("Set scan operator result type: " + t.toString());
		} else if (operator instanceof UnionOperator) {
			logger.debug("operator is UnionOperator");
			for (int i = 0; i < operator.getNumberOfInputs(); i++) {
				assignResultTypes(operator.getInput(i));
			}
			 
			// Assign the fields resulting from the union.
			
			ResultType t = new ResultType();
			List<QueryElement> fields = ((UnionOperator) operator).getResultTuple();
			for (int i = 0; i < fields.size(); i++) {
				QueryElement queryElement = fields.get(i);
				if (queryElement instanceof Variable) {
					Variable variable = (Variable) queryElement;
					ResultField resultField = new ResultField(variable.getVariableName(), variable.getDataTypes().get(0));
					//t.add((QueryElement)fields.get(i));
					t.addResultField(resultField);
				} else
					logger.error("queryElement not variable ---- check what to do with resultType"); //TODO check this

			}
			operator.setResultType(t);
		} else {
			logger.debug("operator is assumed to be orderByOperator - TODO check");
			//TODO check it can't be anything else
			
			// The operator is a order by operator.
			 
			Operator input = operator.getInput(0);
			assignResultTypes(input);
			ResultType t = new ResultType(input.getResultType());
			operator.setResultType(t);
			logger.debug("Set " + input.getClass().getName() + " operator" + " result type: " + t.toString());
		}
	}

	/*
	 * Utility method used to create a list of super lexical objects to represent the
	 * fields of a scanned super abstract - i.e. the list of super lexicals added as tuple
	 * fields as the result of a super abstract scan.
	 */
	/*
	protected List<SuperLexicalReference> getSuperLexicalsOfSuperAbstract(SuperAbstractReference superAbstractReference) throws LookupException {
		logger.debug("in getSuperLexicalsOfSuperAbstract");
		ArrayList<SuperLexicalReference> superLexicalReferences = new ArrayList<SuperLexicalReference>();
		
		// Add all fields of the scanned relation to the
		// tuple type
		
		//String superAbstractName = superAbstractReference.getSuperAbstractName();
		//String varName = superAbstractReference.getVariableName();
	
		// NOTE need to preserve the order of the fields!
		
		
		//String[] attribs = data.getAttributes(table);
		//for(int i=0;i<attribs.length;i++) {
		//    Class type = data.lookupAttributeType(table,attribs[i]);
		//    superLexicalReferences.add( new Attribute(relation,attribs[i],new Class[] {type} ) );
		//}
		
		Set<SuperLexical> superLexicals = superAbstractReference.getSuperAbstract().getSuperLexicals();
		for (SuperLexical superLexical : superLexicals) {
			//TODO sort out type of super lexicals
			//Class[] type = {Types.mapDataTypeToClassOfType(superLexical.getDataType())};
			DataType dataType = superLexical.getDataType();
			superLexicalReferences.add(new SuperLexicalReference(superAbstractReference, superLexical.getName(), superLexical, dataType));
		}
		return superLexicalReferences;
	}
	*/

	/*
	 * Once a plan has been generated, it is necessary to insert additional
	 * project operators to eliminate tuple fields not required further
	 * up the query plan tree. The following two methods implement this.  
	 * Should be invoked AFTER physical optimisation.
	 */
	//TODO insert additional reduceOperators
	/*
	protected void insertProjectOperators(EvaluatorOperator plan) throws OptimisationException {
		logger.debug("in insertProjectOperators");
		insertProjectOperators(null, plan, new HashSet<SuperLexical>());
	}
	*/

	/*
	 * Recursively insert project operators. Works as follows:
	 * 1. If the operator requires any specific tuple fields
	 *    to do its job, these are unioned with the
	 *    requiredFields collection.
	 * 2. Calls insertProjectOperator for all children,
	 *    (passing the list of tuple fields created in 1)
	 * 3. If the operator outputs any tuple fields that are not
	 *    required by ops further up the tree, a PROJECT operator is inserted
	 *    to remove them following the application of the operator.
	 * NOTE: returns the List of tuple fields that make up the output
	 * tuple (after the possible insertion of a PROJECT) so that parent
	 * operators know which fields to project. 
	 */
	/*
	private List<SuperLexical> insertProjectOperators(EvaluatorOperator parent, EvaluatorOperator operator, List<SuperLexical> requiredFields)
			throws OptimisationException {
		logger.debug("in private insertProjectOperators");
		
		 // Note: 'used' will contain all tuple fields used by this
		 // operator and operators further up the tree. 'requiredFields'
		 // contains tuple fields required by operators further up the tree
		 // only.
		 
		Collection<QueryElement> used = fieldsUsedByOperator(operator, requiredFields);
		used.addAll(requiredFields);
		// process child operators
		ArrayList<QueryElement> inputResultType = new ArrayList<QueryElement>();
		for (int i = 0; i < operator.getNumberOfInputs(); i++) {
			
			 // Note that although this method may replace the inputs
			 // of some operators with projections, that operator will
			 // have already been processed and there is no need to 
			 // process the inserted projection. The replaceInput
			 // method of the Operator class does not change the order
			 // of operators, so 
			 
			List<QueryElement> l = insertProjectOperators(operator, operator.getInput(i), used);
			Iterator<QueryElement> it = l.iterator();
			while (it.hasNext())
				inputResultType.add(it.next());
		}
		// if an op-call, scan, or join,
		// does the operator output any fields not in the 'used'
		// collection? 
		
		 // Projections can be placed after:
	
		 // Only need to consider these operations because:
		 // - only op calls and joins support predicates 
		 // - ORDER BY: there will always be a project operator after an
		 //   order by because the logical optimiser writes
		 //   one order by immediately before the final projection
		 // - AGGREGATES: discard everything but the aggregate result
		 // - UNION: discards everything but the result
		 // (if any of the above assumptions change, the following code
		 // may have to change as a result)
		 // Note: this method recursively goes down the branches of the
		 // tree (to scans) and then works its way up. Therefore, when it encounters
		 // each operator, the only existing projection parent operators were
		 // inserted by the first phase of logical optimisation as the root of
		 // a query (or sub-query). Therefore, if the parent operator is a 
		 // projection, there is no need to insert an additional projection
		 // because the existing projection will project the required tuple
		 // anyway. Similarly, if the parent operator applies a union or aggregate
		 // there is no need for a project as these operators discard surplus
		 // tuple fields anyway.
		 
		// stores the actual output, including the effects any projection 
		ArrayList<QueryElement> outputResultType = new ArrayList<QueryElement>();
		// return the tuple fields for those operators which will not be
		// considered for the insertion of a subsequent projection
		if (operator instanceof OrderByOperator) {
			outputResultType.addAll(inputResultType);
			return outputResultType;
		} else if (operator instanceof UnionOperator) {
			// The returned tuples will be the output tuple of the operator.
			outputResultType.addAll(((UnionOperator) operator).getResultTuple());
			return outputResultType;
		} else if (operator instanceof ReduceOperator) {
			ReduceOperator reduce = (ReduceOperator) operator;
			if (reduce.getAggregate() != null) {
				// Only one aggregate field 
				// is created as a result
				outputResultType.add(reduce.getAggregate());
			} else {
				outputResultType.addAll(reduce.getProjectList());
			}
			return outputResultType;
		}
		
		 // At this point, a candidate for the insertion of a projection is
		 // being dealt with - operation call, join operator, scan operator 
		 
		// op calls create a single result field 
		if (operator instanceof OperationCallOperator) {
			OperationCallOperator opCall = (OperationCallOperator) operator;
			// add the result field 
			inputResultType.add(opCall.getFunction());
		}
		// hash loops join scans the right input - adds all table attributes 
		if (operator instanceof HashLoopsJoinOperator) {
			try {
				SuperAbstractReference r = ((HashLoopsJoinOperator) operator).getSuperAbstractReference();
				inputResultType.addAll(getSuperLexicalsOfSuperAbstract(r));
			} catch (LookupException e) {
				throw new OptimisationException("Unable to find attributes " + " of a scanned relation ", e);
			}
		}
		// table scans add all scanned attributes 
		if (operator instanceof ScanOperator) {
			try {
				SuperAbstractReference r = ((ScanOperator) operator).getSuperAbstractReference();
				inputResultType.addAll(getSuperLexicalsOfSuperAbstract(r));
			} catch (LookupException e) {
				throw new OptimisationException("Unable to find attributes " + " of a scanned relation ", e);
			}
		}
		if ((!(parent instanceof ReduceOperator)) && (!(parent instanceof UnionOperator)) && (!requiredFields.containsAll(inputResultType))) {
			outputResultType.addAll(insertProject(operator, parent, requiredFields, inputResultType));
		} else {
			outputResultType.addAll(inputResultType);
		}
		if (outputResultType == null) {
			throw new OptimisationException("Unexpected operator found in logical" + " query plan " + operator.getClass().getName());
		}
		return outputResultType;
	}
	*/

	/*
	 * Inserts a projection (reduce operator) to select the desired list of attributes/vars
	 * The projections input is the operator. The parents input becomes the project.
	 * The passed parameter inputTupleType refers to the input to the project operator (i.e
	 * the output of the operator after which the project is being inserted).
	 * The returned list is the projected attribute (output tuple type).
	 */

	/*
	private static List<QueryElement> insertProject(Operator operator, Operator parent, Collection<QueryElement> requiredFields,
			List<QueryElement> inputResultType) {
		logger.debug("in insertProject");
		ArrayList<QueryElement> newResultType = new ArrayList<QueryElement>();
		newResultType.addAll(inputResultType);
		newResultType.retainAll(requiredFields);
		// mLog.debug("Inserting projection before " + operator.getClass().getName() + " operator");
		// mLog.debug("Projection input = " + inputTupleType);
		// mLog.debug("Fields required by remaining operators = " + requiredFields);
		// newTupleType now contains the elements that should
		// be projected

		//TODO check whether join predicates are carried here, currently null
		ReduceOperator project = new ReduceOperator(operator, newResultType, operator.getCardinality(), null, operator.getSourceId());
		parent.replaceInput(operator, project);
		ArrayList<QueryElement> outputResultType = new ArrayList<QueryElement>();
		outputResultType.addAll(project.getProjectList());
		return outputResultType;
	}
	*/

	/*
	 * Returns the fields (attibutes/variables) used by an operator
	 * (or an empty collection if none are used (e.g. table scan) )
	 * NOTE : the 'requiredFields' collection is provided
	 * for the case in which the operator is a cartesian product. This
	 * operator requires the tupleType to not be empty, but it doesn't
	 * care which field is present. Therefore, when encountering the
	 * cartesian product, this method uses each child operator of
	 * the cartesian product and produces a collection of the attributes/
	 * variables created up to that point in the query plan. For each input
	 * (left and right), the method prefers to add a tuple field that already
	 * exists in the 'alreadyRequiredFields' collection, therefore not 
	 * needlessly projecting an additional attribute futher down each input
	 * tree. If no such field exists, it arbitrarly chooses one from the  
	 * fields created by each input. Nothing is known about the size of each
	 * field, so no optimisation is possible here.
	 */

	/*
	private Collection<QueryElement> fieldsUsedByOperator(Operator operator, Collection<QueryElement> requiredFields) throws OptimisationException {
		logger.debug("in fieldsUsedByOperator");
		HashSet<QueryElement> fields = new HashSet<QueryElement>();
		
		 // Stores the elements used by an operator. All elements
		 // that are Variables or Attributes are added to 'fields'
		 
		HashSet<QueryElement> elements = new HashSet<QueryElement>();
		 
		 // used to hold predicates for those operators that use them
		 // (operation call/joins) - the attributes/variables in the 
		 // predicates are subsequently added to the fields collection.
		 
		Collection<Predicate> predicates = null;
		if (operator instanceof CartesianProductOperator) {
			
			 // NOTE - another reason why the insertion of projections
			 // has to take place after physical optimisation. Cartesian
			 // product is an exception - it has no join attributes but
			 // it needs at least on field from each input (i.e no empty
			 // tuple).
			 
			Collection<QueryElement> leftInput = getResultFields(operator.getInput(0));
			Collection<QueryElement> rightInput = getResultFields(operator.getInput(1));
			
			 // at least one tuple fields is required for each input,
			 // preferably something already in 'requiredFields'. It
			 // is safe to assume that leftInput and rightInput are both
			 // non-empty collections
			
			Iterator<QueryElement> it1 = leftInput.iterator();
			boolean assigned = false;
			QueryElement last = null;
			while (it1.hasNext()) {
				last = it1.next();
				if (requiredFields.contains(last)) {
					elements.add(last);
					assigned = true;
					break;
				}
			}
			// add the last field if non were contained in requiredFields
			if (!assigned)
				elements.add(last);
			assigned = false;
			// repeast for the right input 
			Iterator<QueryElement> it2 = rightInput.iterator();
			while (it2.hasNext()) {
				last = it2.next();
				if (requiredFields.contains(last)) {
					elements.add(last);
					break;
				}
			}
			if (!assigned)
				elements.add(last);
		} else if (operator instanceof JoinOperator) {
			//uses attribs/vars in the join predicates
			predicates = ((JoinOperator) operator).getJoinPredicates();
		} else if (operator instanceof HashLoopsJoinOperator) {
			predicates = ((HashLoopsJoinOperator) operator).getJoinPredicates();
			// NOTE - don't have to worry about scan predicates - they obviously
			// cannot be provided by input operators!
		} else if (operator instanceof OperationCallOperator) {
			// uses var/attributes in function parameters / predicates 
			predicates = ((OperationCallOperator) operator).getPredicates();
			elements.addAll(((OperationCallOperator) operator).getFunction().getParameters());
		} else if (operator instanceof ReduceOperator) {
			ReduceOperator reduce = (ReduceOperator) operator;
			// if an aggregate uses the aggregate parameter
			if (reduce.getAggregate() != null) {
				
				 // Loops through the arguments, although at the time of
				 // writing aggregate operator have only one argument 
				 // because the evaluator's reduce operator does not 
				 // allow for multiple ones. This code loops through in 
				 // case the operator is extended.
				 
				int n = reduce.getAggregate().getNumberOfArguments();
				for (int i = 0; i < n; i++) {
					elements.add(reduce.getAggregate().getArgument(i));
				}
			} else {
				// if project uses the projection attributes
				elements.addAll(reduce.getProjectList());
			}
		} else if (operator instanceof UnionOperator) {
			// uses all the input fields of the left and right operators
			// (the children of a union operator are either project ops
			//  or union ops).
			for (int i = 0; i < operator.getNumberOfInputs(); i++) {
				elements.addAll(fieldsUsedByOperator(operator.getInput(i), requiredFields));
			}
		} else if (operator instanceof OrderByOperator) {
			// uses order by attributes/variables
			Iterator<OrderByItem> it = ((OrderByOperator) operator).getOrderByItems().iterator();
			while (it.hasNext()) {
				OrderByItem item = it.next();
				if ((item.getQueryElement() instanceof SuperLexicalReference) || (item.getQueryElement() instanceof Variable)) {
					elements.add(item.getQueryElement());
				}
			}
		}
		// add var/attribute elements 
		Iterator<QueryElement> it = elements.iterator();
		while (it.hasNext()) {
			QueryElement o = it.next();
			if ((o instanceof SuperLexicalReference) || (o instanceof Variable)) {
				fields.add(o);
			}
		}
		// add predicates 
		if (predicates != null) {
			Iterator<Predicate> pit = predicates.iterator();
			while (pit.hasNext()) {
				Predicate pred = pit.next();
				QueryElement o = pred.getArgument1();
				if ((o instanceof SuperLexicalReference) || (o instanceof Variable)) {
					fields.add(o);
				}
				o = pred.getArgument2();
				if ((o instanceof SuperLexicalReference) || (o instanceof Variable)) {
					fields.add(o);
				}
			}
		}
		return fields;
	}
	*/

	/*
	 * When inserting projections below a cartesian product, at least
	 * on tuple field needs to exist. Therefore, the tuple fields that
	 * have been created by its left and right inputs at that stage
	 * need to be deduced. This method returns a collection of the tuple
	 * fields created by an operator. Tuple fields can be created by:
	 * - operation calls
	 * - scan operators
	 * Don't need to consider join operator because they just combine
	 * fields together. The recusion stops at projects, aggregates and
	 * unions because they discard fields. 
	 */
	/*
	private Collection<QueryElement> getResultFields(Operator operator) throws OptimisationException {
		logger.debug("in getResultFields(operator)");
		return getResultFields(operator, new HashSet<QueryElement>());
	}
	*/

	/*
	private Collection<QueryElement> getResultFields(Operator operator, HashSet<QueryElement> fields) throws OptimisationException {
		logger.debug("in getResultFields(operator, fields)");
		if (operator instanceof OperationCallOperator) {
			// add the result field 
			fields.add(((OperationCallOperator) operator).getFunction());
		} else if (operator instanceof ScanOperator) {
			// add attributes
			try {
				SuperAbstractReference r = ((ScanOperator) operator).getSuperAbstractReference();
				fields.addAll(getSuperLexicalsOfSuperAbstract(r));
			} catch (LookupException e) {
				throw new OptimisationException("Unable to find attributes " + " of a scanned relation ", e);
			}
		} else if (operator instanceof ReduceOperator) {
			ReduceOperator reduce = (ReduceOperator) operator;
			if (reduce.getAggregate() != null) {
				fields.add(reduce.getAggregate());
			} else {
				fields.addAll(reduce.getProjectList());
			}
			return fields;
		} else if (operator instanceof UnionOperator) {
			fields.addAll(((UnionOperator) operator).getResultTuple());
			return fields;
		}
		for (int i = 0; i < operator.getNumberOfInputs(); i++) {
			getResultFields(operator.getInput(i), fields);
		}
		return fields;
	}
	*/

}
