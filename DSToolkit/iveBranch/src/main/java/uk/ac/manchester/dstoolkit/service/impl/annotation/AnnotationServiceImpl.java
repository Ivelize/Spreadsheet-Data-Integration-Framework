/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.annotation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.annotation.Annotation;
import uk.ac.manchester.dstoolkit.domain.annotation.OntologyTerm;
import uk.ac.manchester.dstoolkit.domain.models.ModelManagementConstruct;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.QueryResult;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.domain.user.User;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;
import uk.ac.manchester.dstoolkit.repository.ModelManagementConstructRepository;
import uk.ac.manchester.dstoolkit.repository.annotation.AnnotationRepository;
import uk.ac.manchester.dstoolkit.repository.annotation.OntologyTermRepository;
import uk.ac.manchester.dstoolkit.repository.morphisms.mapping.MappingRepository;
import uk.ac.manchester.dstoolkit.repository.query.QueryRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.QueryResultRepository;
import uk.ac.manchester.dstoolkit.service.annotation.AnnotationService;
import uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl;

/**
 * @author chedeler
 *
 */
//@Transactional(readOnly = true)
@Service(value = "annotationService")
public class AnnotationServiceImpl extends GenericEntityServiceImpl<Annotation, Long> implements AnnotationService {

	private static Logger logger = Logger.getLogger(AnnotationServiceImpl.class);

	@Autowired
	@Qualifier("annotationRepository")
	private AnnotationRepository annotationRepository;

	//@Autowired
	//@Qualifier("mappingOperatorRepository")
	//private MappingOperatorRepository mappingOperatorRepository;

	@Autowired
	@Qualifier("modelManagementConstructRepository")
	private ModelManagementConstructRepository modelManagementConstructRepository;

	//@Autowired
	//@Qualifier("resultInstanceRepository")
	//private ResultInstanceRepository resultInstanceRepository;

	@Autowired
	@Qualifier("queryResultRepository")
	private QueryResultRepository queryResultRepository;

	@Autowired
	@Qualifier("ontologyTermRepository")
	private OntologyTermRepository ontologyTermRepository;

	@Autowired
	@Qualifier("mappingRepository")
	private MappingRepository mappingRepository;

	@Autowired
	@Qualifier("queryRepository")
	private QueryRepository queryRepository;

	//@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Transactional
	public void annotate(String ontologyTermName, String annotationValue, Long modelManagementConstructId, Long parentConstructId,
			Set<ModelManagementConstruct> constrainingModelManagementConstructs, boolean duplicateAnnotationAllowed, User user) {
		logger.debug("in annotate");
		logger.debug("ontologyTermName: " + ontologyTermName);
		logger.debug("annotationValue: " + annotationValue);
		logger.debug("modelManagementConstructId: " + modelManagementConstructId);
		logger.debug("parentConstructId: " + parentConstructId);
		logger.debug("constrainingModelManagementConstructs: " + constrainingModelManagementConstructs);
		if (constrainingModelManagementConstructs != null)
			logger.debug("constrainingModelManagementConstructs.size(): " + constrainingModelManagementConstructs.size());
		logger.debug("duplicateAnnotationAllowed: " + duplicateAnnotationAllowed);
		logger.debug("user: " + user);

		ModelManagementConstruct modelManagementConstruct = modelManagementConstructRepository.find(modelManagementConstructId);
		logger.debug("modelManagementConstruct: " + modelManagementConstruct);

		ModelManagementConstruct parentConstruct = modelManagementConstructRepository.find(parentConstructId);
		logger.debug("parentConstruct: " + parentConstruct);

		OntologyTerm ontologyTerm = ontologyTermRepository.getOntologyTermWithName(ontologyTermName);
		logger.debug("ontologyTerm: " + ontologyTerm);
		Annotation annotation = new Annotation(annotationValue, ontologyTerm);
		annotation.setUser(user);

		annotate(annotation, modelManagementConstruct, parentConstruct, constrainingModelManagementConstructs, duplicateAnnotationAllowed, user);
	}

	//@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Transactional
	public void annotate(String ontologyTermName, String annotationValue, ModelManagementConstruct modelManagementConstruct,
			ModelManagementConstruct parentConstruct, Set<ModelManagementConstruct> constrainingModelManagementConstructs,
			boolean duplicateAnnotationAllowed, User user) {
		logger.debug("in annotate");
		logger.debug("ontologyTermName: " + ontologyTermName);
		logger.debug("annotationValue: " + annotationValue);
		logger.debug("modelManagementConstruct: " + modelManagementConstruct);
		logger.debug("parentConstruct: " + parentConstruct);
		logger.debug("constrainingModelManagementConstructs: " + constrainingModelManagementConstructs);
		if (constrainingModelManagementConstructs != null)
			logger.debug("constrainingModelManagementConstructs.size(): " + constrainingModelManagementConstructs.size());
		logger.debug("duplicateAnnotationAllowed: " + duplicateAnnotationAllowed);
		logger.debug("user: " + user);

		OntologyTerm ontologyTerm = ontologyTermRepository.getOntologyTermWithName(ontologyTermName);
		logger.debug("ontologyTerm: " + ontologyTerm);
		Annotation annotation = new Annotation(annotationValue, ontologyTerm);
		annotation.setUser(user);

		annotate(annotation, modelManagementConstruct, parentConstruct, constrainingModelManagementConstructs, duplicateAnnotationAllowed, user);
	}

	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	public void annotate(Annotation annotation, ModelManagementConstruct modelManagementConstruct, ModelManagementConstruct parentConstruct,
			Set<ModelManagementConstruct> constrainingModelManagementConstructs, boolean duplicateAnnotationAllowed, User user) {
		logger.debug("in annotate");
		logger.debug("annotation: " + annotation);
		logger.debug("modelManagementConstruct: " + modelManagementConstruct);
		logger.debug("parentConstruct: " + parentConstruct);
		logger.debug("constrainingModelManagementConstructs: " + constrainingModelManagementConstructs);
		if (constrainingModelManagementConstructs != null)
			logger.debug("constrainingModelManagementConstructs.size(): " + constrainingModelManagementConstructs.size());
		logger.debug("duplicateAnnotationAllowed: " + duplicateAnnotationAllowed);
		logger.debug("user: " + user);

		addAnnotationToModelManagementConstruct(annotation, modelManagementConstruct, constrainingModelManagementConstructs,
				duplicateAnnotationAllowed, user);

		if (annotation.getOntologyTerm().getName().equals("expectancy")) {
			logger.debug("annotation is expectancy annotation - infer statisticalError annotation");
			inferAndAnnotateModelManagementConstructWithStatisticalErrorAnnotation(annotation, modelManagementConstruct, parentConstruct,
					constrainingModelManagementConstructs, duplicateAnnotationAllowed, user);
		}
	}

	@Transactional(readOnly = true)
	private void inferAndAnnotateModelManagementConstructWithStatisticalErrorAnnotation(Annotation annotation,
			ModelManagementConstruct modelManagementConstruct, ModelManagementConstruct parentConstruct,
			Set<ModelManagementConstruct> constrainingModelManagementConstructs, boolean duplicateAnnotationAllowed, User user) {
		logger.debug("inferAndAnnotateModelManagementConstructWithStatisticalErrorAnnotation");
		logger.debug("annotation: " + annotation);
		logger.debug("modelManagementConstruct: " + modelManagementConstruct);
		logger.debug("parentConstruct: " + parentConstruct);
		logger.debug("constrainingModelManagementConstructs: " + constrainingModelManagementConstructs);
		if (constrainingModelManagementConstructs != null)
			logger.debug("constrainingModelManagementConstructs.size(): " + constrainingModelManagementConstructs.size());
		logger.debug("duplicateAnnotationAllowed: " + duplicateAnnotationAllowed);
		logger.debug("user: " + user);
		if (annotation.getValue().equals("true")) {
			logger.debug("expectancy: true");
			if (modelManagementConstruct.isUserSpecified()) {
				logger.debug("modelManagementConstruct is userSpecified - add FN annotation");
				//TODO the constrainingModelManagementConstructs should be the mappings here - 
				//here all the mappings used for query result should be the constraining constructs including the queryId
				annotate("statisticalError", "fn", modelManagementConstruct, parentConstruct, constrainingModelManagementConstructs,
						duplicateAnnotationAllowed, annotation.getUser());
			} else {
				logger.debug("modelManagementConstruct isn't userSpecified - add TP annotation");
				//TODO as above, but add fn annotation for the tuples and the mappings that haven't returned these tuples
				annotate("statisticalError", "tp", modelManagementConstruct, parentConstruct, constrainingModelManagementConstructs,
						duplicateAnnotationAllowed, annotation.getUser());
				if (modelManagementConstruct instanceof ResultInstance) {
					logger.debug("annotated construct is resultInstance - get mappings that produced it, compare with mappings that were used and create user specified resultinstance and add annotation for those mappings that didn't produce it");
					ResultInstance resultInstance = (ResultInstance) modelManagementConstruct;
					Set<Mapping> mappings = resultInstance.getMappings();
					Set<Mapping> mappingsThatDidntReturnInstance = new LinkedHashSet<Mapping>();
					Query query = null;
					QueryResult queryResult = null;
					if (parentConstruct != null && parentConstruct instanceof QueryResult) {
						queryResult = (QueryResult) parentConstruct;
						for (ModelManagementConstruct constrainingConstruct : constrainingModelManagementConstructs) {
							logger.debug("constrainingConstruct: " + constrainingConstruct);
							if (constrainingConstruct instanceof Query) {
								logger.debug("constrainingConstruct is Query");
								query = (Query) constrainingConstruct;
							}
						}
						for (Mapping mapping : queryResult.getMappings()) {
							if (!mappings.contains(mapping)) {
								logger.debug("found mapping that was used to expand query but didn't produce resultinstance");
								mappingsThatDidntReturnInstance.add(mapping);
							}
						}

						if (query == null)
							logger.error("didn't find query in constrainingConstructs - TODO sort this");

						if (mappingsThatDidntReturnInstance.size() > 0) {
							logger.debug("found mappings that didn't return instance, create user specified instance, add mappings and annotate with fn");

							/*
							ResultInstance missingResultInstance = new ResultInstance();
							missingResultInstance.setQuery(query);
							missingResultInstance.setResultType(resultInstance.getResultType());

							for (String resultFieldName : resultInstance.getResultFieldNameResultValueMap().keySet()) {
								logger.debug("resultFieldName: " + resultFieldName);
								ResultValue resultValue = resultInstance.getResultValue(resultFieldName);
								logger.debug("resultValue: " + resultValue);

								ResultValue value = new ResultValue(resultValue.getResultFieldName(), resultValue.getValue());
								missingResultInstance.addResultValue(resultValue.getResultFieldName(), value);
							}

							missingResultInstance.setUserSpecified(true);
							missingResultInstance.addAllMappings(mappingsThatDidntReturnInstance);
							if (queryResult == null)
								logger.error("no queryResult provided as parentConstruct of resultInstance - TODO sort this");
							else
								queryResult.addResultInstance(missingResultInstance);
							//resultInstanceRepository.save(missingResultInstance);
							//resultInstanceRepository.flush();
							*/
							Set<ModelManagementConstruct> constraining = new LinkedHashSet<ModelManagementConstruct>();
							constraining.add(query);
							constraining.addAll(mappingsThatDidntReturnInstance);
							annotate("statisticalError", "fn", modelManagementConstruct, parentConstruct, constraining, true, user);
						}
					}
				}
			}
		} else if (annotation.getValue().equals("false")) {
			logger.debug("expectancy: false");
			if (modelManagementConstruct.isUserSpecified()) {
				logger.debug("modelManagementConstruct is userSpecified - add TN annotation");
				annotate("statisticalError", "tn", modelManagementConstruct, parentConstruct, constrainingModelManagementConstructs,
						duplicateAnnotationAllowed, annotation.getUser());
			} else {
				logger.debug("modelManagementConstruct isn't userSpecified - add FP annotation");
				annotate("statisticalError", "fp", modelManagementConstruct, parentConstruct, constrainingModelManagementConstructs,
						duplicateAnnotationAllowed, annotation.getUser());
			}
		} else
			logger.error("unexpected expectancy annotation value: " + annotation.getValue());
	}

	@Transactional(readOnly = true)
	public void propagateAnnotation(Set<Annotation> annotationsToPropagate, Set<ModelManagementConstruct> constructsToPropagateAnnotationsTo,
			Set<ModelManagementConstruct> constrainingModelManagementConstructs, boolean duplicateAnnotationsAllowed, User user) {
		logger.debug("in propagateAnnotation");
		logger.debug("annotationsToPropagate: " + annotationsToPropagate);
		logger.debug("annotationsToPropagate.size(): " + annotationsToPropagate.size());
		logger.debug("constructsToPropagateAnnotationsTo: " + constructsToPropagateAnnotationsTo);
		if (constructsToPropagateAnnotationsTo != null)
			logger.debug("constructsToPropagateAnnotationsTo.size(): " + constructsToPropagateAnnotationsTo.size());
		logger.debug("constrainingModelManagementConstructs: " + constrainingModelManagementConstructs);
		if (constrainingModelManagementConstructs != null)
			logger.debug("constrainingModelManagementConstructs.size(): " + constrainingModelManagementConstructs.size());
		logger.debug("duplicateAnnotationsAllowed: " + duplicateAnnotationsAllowed);

		ModelManagementConstruct constructToAnnotate = null;
		ModelManagementConstruct constructAnnotated = null;
		if (constructsToPropagateAnnotationsTo != null && !constructsToPropagateAnnotationsTo.isEmpty())
			constructToAnnotate = constructsToPropagateAnnotationsTo.iterator().next();
		if (annotationsToPropagate != null && !annotationsToPropagate.isEmpty())
			constructAnnotated = annotationsToPropagate.iterator().next().getAnnotatedModelManagementConstructs().iterator().next();

		logger.debug("constructToAnnotate: " + constructToAnnotate);
		logger.debug("constructAnnotated: " + constructAnnotated);

		if (constructAnnotated instanceof Mapping && constructsToPropagateAnnotationsTo.size() == 1 && constructToAnnotate instanceof QueryResult) {
			propagateAnnotationsFromMappingsToQueryResult(annotationsToPropagate, (QueryResult) constructToAnnotate,
					constrainingModelManagementConstructs, duplicateAnnotationsAllowed, user);
		}

		if (constructAnnotated instanceof ResultInstance && constructToAnnotate instanceof Mapping) {

			Set<Mapping> mappings = null;
			if (constructToAnnotate != null && constructToAnnotate instanceof Mapping) {
				mappings = new HashSet<Mapping>();
				for (ModelManagementConstruct construct : constructsToPropagateAnnotationsTo) {
					if (construct instanceof Mapping) {
						mappings.add((Mapping) construct);
						logger.debug("mappings: " + mappings);
					} else
						logger.error("found something else but a mapping: " + construct);
				}
			}
			propagateAnnotationsFromResultInstancesToMappingsProducingThoseResultInstances(annotationsToPropagate, mappings,
					constrainingModelManagementConstructs, duplicateAnnotationsAllowed, user);
		}

		//TODO; this is wrong, the annotations need to be propagated to the corresponding constructs which were queried first
		//before they get propagated to the mappings that were used to expand the query - need to keep track of the queryResult, mappings used etc.

	}

	//TODO add method for adding FN

	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	private void addAnnotationToModelManagementConstruct(Annotation annotation, ModelManagementConstruct modelManagementConstruct,
			Set<ModelManagementConstruct> constrainingModelManagementConstructs, Boolean duplicateAnnotationAllowed, User user) {

		//Even if duplicateAnnotation for an ontologyTerm and user is allowed, I don't think it makes sense to have multiple annotations with the same value
		//hence, if an annotation with the same value is found, the timestamp is updated independent of whether duplicate annotation is allowed or not
		//duplicateAnnotationAllowed only plays a role when an annotation for the same ontologyTerm and user is found but it has a different value from the one
		//to be set, in that case and if duplicate annotation is not allowed, the old value is overwritten and the timestamp is updated,
		//if duplicate annotation is allowed, the new annotation with the different value is added to the modelManagementConstruct in addition to the already
		//existing annotation

		logger.debug("in addAnnotationToModelManagementConstruct");
		logger.debug("annotation: " + annotation);
		logger.debug("annotation.getValue(): " + annotation.getValue());
		logger.debug("user: " + user);
		logger.debug("modelManagementConstruct: " + modelManagementConstruct);
		logger.debug("constrainingModelManagementConstructs: " + constrainingModelManagementConstructs);
		if (constrainingModelManagementConstructs != null) {
			logger.debug("constrainingModelManagementConstructs.size(): " + constrainingModelManagementConstructs.size());
			if (!constrainingModelManagementConstructs.isEmpty())
				logger.debug("constrainingModelManagementConstructs.iterator().next().getId(): "
						+ constrainingModelManagementConstructs.iterator().next().getId());
		}
		logger.debug("duplicateAnnotationAllowed: " + duplicateAnnotationAllowed);

		/*
		modelManagementConstruct = modelManagementConstructRepository.merge(modelManagementConstruct);
		annotation = annotationRepository.merge(annotation);
		if (constrainingModelManagementConstructs != null && constrainingModelManagementConstructs.size() > 0)
			for (ModelManagementConstruct constrainingConstruct : constrainingModelManagementConstructs)
				constrainingModelManagementConstructs.add(modelManagementConstructRepository.merge(constrainingConstruct));
		logger.debug("constrainingModelManagementConstructs.size(): " + constrainingModelManagementConstructs.size());
		*/

		OntologyTerm ontologyTerm = annotation.getOntologyTerm();
		logger.debug("ontologyTerm: " + ontologyTerm);
		String ontologyTermName = ontologyTerm.getName();
		String value = annotation.getValue();
		User annotationUser = annotation.getUser();

		logger.debug("ontologyTermName: " + ontologyTermName);
		logger.debug("value: " + value);
		logger.debug("annotationUser: " + annotationUser);
		if (annotationUser != user)
			logger.error("annotationUser different from user - TODO sort this");

		if (modelManagementConstruct.getId() == null) {
			logger.debug("didn't find id for modelManagementConstruct, make it persistent");
			if (modelManagementConstruct.isUserSpecified())
				logger.debug("modelManagementConstruct is userSpecified");
			else
				logger.debug("modelManagementConstruct isn't userSpecified - should it be?");
			modelManagementConstructRepository.save(modelManagementConstruct);
			modelManagementConstructRepository.flush();
		}

		List<Annotation> annotationsForModelManagementConstructProvidedByUser = annotationRepository
				.getAnnotationsForModelManagementConstructAndOntologyTermNameProvidedByUser(modelManagementConstruct, ontologyTermName, user);
		logger.debug("annotationsForModelManagementConstructProvidedByUser: " + annotationsForModelManagementConstructProvidedByUser);
		if (annotationsForModelManagementConstructProvidedByUser != null)
			logger.debug("annotationsForModelManagementConstructProvidedByUser.size(): "
					+ annotationsForModelManagementConstructProvidedByUser.size());

		if (annotationsForModelManagementConstructProvidedByUser == null || annotationsForModelManagementConstructProvidedByUser.isEmpty()) {
			logger.debug("didn't find annotation for modelManagementConstruct provided by User");
			//annotationRepository.fetch(modelManagementConstruct.getAnnotations());
			//modelManagementConstruct = modelManagementConstructRepository.fetchAnnotations(modelManagementConstruct);
			modelManagementConstruct.addAnnotation(annotation);
			//annotation.addAnnotatedModelManagementConstruct(modelManagementConstruct);
			if (constrainingModelManagementConstructs != null)
				annotation.addAllConstrainingModelManagementConstructs(constrainingModelManagementConstructs);
			this.addAnnotation(annotation);
			modelManagementConstructRepository.update(modelManagementConstruct);
			ontologyTermRepository.update(ontologyTerm);
			annotationRepository.save(annotation);
			logger.debug("added annotation to modelManagementConstruct");
		} else {
			logger.debug("found annotation for modelManagementConstruct provided by user");
			logger.debug("annotationsForModelManagementConstructProvidedByUser.size(): "
					+ annotationsForModelManagementConstructProvidedByUser.size());
			//if (annotationsForModelManagementConstructProvidedByUser.size() > 1) {
			//	logger.error("found more than one annotation - check whether duplicates are allowed - should be");
			if (duplicateAnnotationAllowed) {
				logger.debug("duplicateAnnotationAllowed");
				logger.debug("check whether I can find annotation with the same value");
				boolean foundAnnotationWithSameValue = false;
				for (Annotation currentAnnotation : annotationsForModelManagementConstructProvidedByUser) {
					logger.debug("currentAnnotation: " + currentAnnotation);
					logger.debug("currentAnnotation.getValue(): " + currentAnnotation.getValue());
					logger.debug("currentAnnotation.getOntologyTerm().getName(): " + currentAnnotation.getOntologyTerm().getName());
					logger.debug("currentAnnotation.getConstrainingModelManagementConstructs(): "
							+ currentAnnotation.getConstrainingModelManagementConstructs());
					if (constrainingModelManagementConstructs != null) {
						logger.debug("currentAnnotation.getConstrainingModelManagementConstructs().size(): "
								+ currentAnnotation.getConstrainingModelManagementConstructs().size());
						if (!constrainingModelManagementConstructs.isEmpty())
							logger.debug("currentAnnotation.getConstrainingModelManagementConstructs().iterator().next().getId(): "
									+ currentAnnotation.getConstrainingModelManagementConstructs().iterator().next().getId());
					}
					if (currentAnnotation.getValue().equals(value)) {
						logger.debug("found annotation with same value");
						logger.debug("check whether it's got the same constraining modelManagementConstructs");
						foundAnnotationWithSameValue = true;
						if (currentAnnotation.getConstrainingModelManagementConstructs().containsAll(constrainingModelManagementConstructs)) {
							logger.debug("found same constraining modelManagementConstructs - update timestamp");
							currentAnnotation.updateTimestamp();
							annotationRepository.update(currentAnnotation);
						} else {
							logger.debug("didn't find same constraining modelManagementConstructs - add new Annotation");
							annotation.addAnnotatedModelManagementConstruct(modelManagementConstruct);
							//modelManagementConstruct.addAnnotation(annotation);
							if (constrainingModelManagementConstructs != null)
								annotation.addAllConstrainingModelManagementConstructs(constrainingModelManagementConstructs);
							this.addAnnotation(annotation);
							modelManagementConstructRepository.update(modelManagementConstruct);
							ontologyTermRepository.update(ontologyTerm);
							logger.debug("added annotation to modelManagementConstruct");
							annotationRepository.save(annotation);
						}

					}
				}
				if (!foundAnnotationWithSameValue) {
					logger.debug("didn't find annotation with same value, as duplicates are allowed - add new annotation to modelManagementConstruct and persist");
					//modelManagementConstruct.addAnnotation(annotation);
					annotation.addAnnotatedModelManagementConstruct(modelManagementConstruct);
					if (constrainingModelManagementConstructs != null)
						annotation.addAllConstrainingModelManagementConstructs(constrainingModelManagementConstructs);
					this.addAnnotation(annotation);
					modelManagementConstructRepository.update(modelManagementConstruct);
					ontologyTermRepository.update(ontologyTerm);
					logger.debug("added annotation to modelManagementConstruct");
					annotationRepository.save(annotation);
				}
			} else {
				if (annotationsForModelManagementConstructProvidedByUser.size() > 1) {
					logger.error("no duplicateAnnotationAllowed, but found multiple annotations for ontologyTerm - shouldn't happen: "
							+ ontologyTermName);
					for (Annotation annot : annotationsForModelManagementConstructProvidedByUser) {
						logger.debug("annot: " + annot);
						logger.debug("annot.getValue(): " + annot.getValue());
						logger.debug("annot.getOntologyTerm().getName()" + annot.getOntologyTerm().getName());
						logger.debug("annot.getConstrainingModelManagementConstructs(): " + annot.getConstrainingModelManagementConstructs());
						if (constrainingModelManagementConstructs != null)
							logger.debug("annot.getConstrainingModelManagementConstructs().size(): "
									+ annot.getConstrainingModelManagementConstructs().size());
					}
				} else {
					logger.debug("annotationsForModelManagementConstructProvidedByUser.size() should be 1: "
							+ annotationsForModelManagementConstructProvidedByUser.size());
					//TODO decide what to do with the constraining modelManagementConstructs, add them for now, but may not be best idea
					logger.debug("no duplicateAnnotationAllowed, found only one annotation for ontologyTerm - overwrite value, add constrainingModelManagementConstructs - might not be ideal though");
					Annotation currentAnnotation = annotationsForModelManagementConstructProvidedByUser.get(0);
					logger.debug("currentAnnotation.getValue(): " + currentAnnotation.getValue());
					logger.debug("value: " + value);
					logger.debug("currentAnnotation.getConstrainingModelManagementConstructs(): "
							+ currentAnnotation.getConstrainingModelManagementConstructs());
					if (constrainingModelManagementConstructs != null)
						logger.debug("currentAnnotation.getConstrainingModelManagementConstructs().size(): "
								+ currentAnnotation.getConstrainingModelManagementConstructs().size());
					currentAnnotation.setValue(annotation.getValue());
					if (constrainingModelManagementConstructs != null)
						currentAnnotation.addAllConstrainingModelManagementConstructs(annotation.getConstrainingModelManagementConstructs());
					annotationRepository.update(currentAnnotation);
				}
			}
			//} else {
			//	logger.debug("only found one annotation for modelManagementConstruct, ontologyTerm and user");
			/*
			Annotation currentAnnotation = annotationsForModelManagementConstructProvidedByUser.get(0);
			logger.debug("currentAnnotation.getValue(): " + currentAnnotation.getValue());
			logger.debug("value: " + value);
			if (currentAnnotation.getValue().equals(value)) {
				logger.debug("same annotation value; update timestamp, value: " + value);
				currentAnnotation.updateTimestamp();
				annotationRepository.update(currentAnnotation);
			} else {
				logger.debug("different annotation value; check whether duplicates are allowed");
				if (duplicateAnnotationAllowed) {
					logger.debug("duplicateAnnotationAllowed - add new annotation to construct");
					modelManagementConstruct.addAnnotation(annotation);
					this.addAnnotation(annotation);
					modelManagementConstructRepository.update(modelManagementConstruct);
					ontologyTermRepository.update(ontologyTerm);
					logger.debug("added annotation to modelManagementConstruct");
				} else {
					logger.debug("no duplicateAnnotationAllowed - overwrite value");
					currentAnnotation.setValue(annotation.getValue());
					annotationRepository.update(currentAnnotation);
				}
			}
			*/
			//}
		}
		annotationRepository.flush();
	}

	//TODO finish off sorting out the constrainingModelManagementConstructs
	//the annotation of a mapping is constrained by the query

	@Transactional(readOnly = true)
	private void propagateAnnotationsFromMappingsToQueryResult(Set<Annotation> annotations, QueryResult queryResult,
			Set<ModelManagementConstruct> constrainingModelManagementConstructs, boolean duplicateAnnotationAllowed, User user) {
		logger.debug("in propagateAnnotationsToQueryResult");
		logger.debug("annotations: " + annotations);
		logger.debug("queryResult: " + queryResult);
		logger.debug("user: " + user);
		logger.debug("constrainingModelManagementConstructs: " + constrainingModelManagementConstructs);
		if (constrainingModelManagementConstructs != null)
			logger.debug("constrainingModelManagementConstructs.size(): " + constrainingModelManagementConstructs.size());
		logger.debug("duplicateAnnotationAllowed: " + duplicateAnnotationAllowed);

		double beta = 1;

		Query query = queryResult.getQuery();
		User queryUser = query.getUser();
		logger.debug("queryUser: " + queryUser);
		if (queryUser != user)
			logger.error("queryUser different from user - TODO sort this");

		//TODO I think this only works if the query is just a scan, extra propagation is needed when it's not a scan

		if (query.isJustScanQuery()) { //.getRootOperator().queryIsJustScan()) {
			logger.debug("query is just scan");

			//TODO I guess this is just to check whether it's worth doing all the calculation, but doesn't add anything to the actual calculation - remove if that's the case
			/*
			long numberOfKnownTpsForQuery = queryRepository.getNumberOfResultInstancesOfQueryWithGivenAnnotationForGivenOntologyTerm(query,
					"statisticalError", "tp");
			logger.debug("numberOfKnownTpsForQuery: " + numberOfKnownTpsForQuery);

			if (numberOfKnownTpsForQuery > 0) {
			*/

			long numberOfResultsReturnedByMappingsUsed = queryRepository.getNumberOfResultInstancesOfQueryRetrievedByGivenMappings(query,
					queryResult.getMappings());
			logger.debug("numberOfResultsReturnedByMappingsUsed: " + numberOfResultsReturnedByMappingsUsed);

			long numberOfTpsForQueryReturnedByMappingsUsed = queryRepository
					.getNumberOfResultInstancesOfQueryWithGivenAnnotationValueForGivenOntologyTermRetrievedByGivenMappings(query, "statisticalError",
							"tp", queryResult.getMappings());
			logger.debug("numberOfTpsForQueryReturnedByMappingsUsed: " + numberOfTpsForQueryReturnedByMappingsUsed);

			long numberOfFpsForQueryReturnedByMappingsUsed = queryRepository
					.getNumberOfResultInstancesOfQueryWithGivenAnnotationValueForGivenOntologyTermRetrievedByGivenMappings(query, "statisticalError",
							"fp", queryResult.getMappings());
			logger.debug("numberOfFpsForQueryReturnedByMappingsUsed: " + numberOfFpsForQueryReturnedByMappingsUsed);

			long numberOfFnsForQueryReturnedByMappingsUsed = queryRepository
					.getNumberOfResultInstancesOfQueryWithGivenAnnotationValueForGivenOntologyTermRetrievedByGivenMappings(query, "statisticalError",
							"fn", queryResult.getMappings());
			logger.debug("numberOfFnsForQueryReturnedByMappingsUsed: " + numberOfFnsForQueryReturnedByMappingsUsed);

			Map<String, Double> precisionRecallFMeasure = this.calculatePrecisionRecallAndFMeasure(numberOfTpsForQueryReturnedByMappingsUsed,
					numberOfFpsForQueryReturnedByMappingsUsed, numberOfFnsForQueryReturnedByMappingsUsed, beta);

			//when an FN is added, it's associated to all the mappings that were used to expand the query
			//TODO decide whether the FNs should be included in here - I think they should be included, as they will be included in the recall and the f-measure
			double fractionOfAnnotatedResults = (numberOfTpsForQueryReturnedByMappingsUsed + numberOfFpsForQueryReturnedByMappingsUsed + numberOfFnsForQueryReturnedByMappingsUsed)
					/ (double) numberOfResultsReturnedByMappingsUsed;
			logger.debug("fractionOfAnnotatedResults: " + fractionOfAnnotatedResults);

			Double estimatedPrecision = precisionRecallFMeasure.get("precision");
			Double estimatedRecall = precisionRecallFMeasure.get("recall");
			Double estimatedFMeasure = precisionRecallFMeasure.get("fmeasure");

			if (estimatedPrecision != null)
				this.annotate("precision", estimatedPrecision.toString(), queryResult, null, constrainingModelManagementConstructs, false, user);
			if (estimatedRecall != null)
				this.annotate("recall", estimatedRecall.toString(), queryResult, null, constrainingModelManagementConstructs, false, user);
			if (estimatedFMeasure != null)
				this.annotate("f-measure", estimatedFMeasure.toString(), queryResult, null, constrainingModelManagementConstructs, false, user);
			this.annotate("fractionOfAnnotatedResults", new Double(fractionOfAnnotatedResults).toString(), queryResult, null,
					constrainingModelManagementConstructs, false, user);
		} else {
			logger.error("query isn't just scan - TODO add propagation");
		}

		/*
		if ((numberOfTpsForQueryReturnedByMappingsUsed != 0) || (numberOfFpsForQueryReturnedByMappingsUsed != 0)) {

			double estimatedPrecision = (double) numberOfTpsForQueryReturnedByMappingsUsed
					/ (double) (numberOfTpsForQueryReturnedByMappingsUsed + numberOfFpsForQueryReturnedByMappingsUsed);
			logger.debug("estimatedPrecision: " + estimatedPrecision);

			double estimatedRecall = numberOfTpsForQueryReturnedByMappingsUsed / (double) numberOfKnownTpsForQuery;
			logger.debug("estimatedRecall: " + estimatedRecall);

			double estimatedFMeasure = ((1 + (beta * beta)) * estimatedPrecision * estimatedRecall)
					/ (((beta * beta) * estimatedPrecision) + estimatedRecall);
			if (((Object) estimatedFMeasure).toString().equals("NaN"))
				estimatedFMeasure = 0.0;
			logger.debug("estimatedFMeasure: " + estimatedFMeasure);

			double fractionOfAnnotatedResults = (double) (numberOfTpsForQueryReturnedByMappingsUsed + numberOfFpsForQueryReturnedByMappingsUsed)
					/ (double) numberOfResultsReturnedByMappingsUsed;
			logger.debug("fractionOfAnnotatedResults: " + fractionOfAnnotatedResults);

			OntologyTerm precisionOT = ontologyTermRepository.getOntologyTermWithName("precision");
			OntologyTerm recallOT = ontologyTermRepository.getOntologyTermWithName("recall");
			OntologyTerm fmeasureOT = ontologyTermRepository.getOntologyTermWithName("f-measure");
			OntologyTerm fractionOfAnnotatedResultsOT = ontologyTermRepository.getOntologyTermWithName("fraction of annotated results");

			Annotation precisionAnnotation = new Annotation(new Double(estimatedPrecision).toString(), precisionOT);
			precisionAnnotation.setUser(user);
			precisionAnnotation.addModelManagementConstruct(queryResult);
			queryResult.addAnnotation(precisionAnnotation);
			//annotationRepository.update(precisionAnnotation);
			//queryResultRepository.update(queryResult);
			//ontologyTermRepository.update(precisionOT);

			Annotation recallAnnotation = new Annotation(new Double(estimatedRecall).toString(), recallOT);
			recallAnnotation.setUser(user);
			recallAnnotation.addModelManagementConstruct(queryResult);
			queryResult.addAnnotation(recallAnnotation);
			//annotationRepository.update(recallAnnotation);
			//queryResultRepository.update(queryResult);
			//ontologyTermRepository.update(recallOT);

			Annotation fmeasureAnnotation = new Annotation(new Double(estimatedFMeasure).toString(), fmeasureOT);
			fmeasureAnnotation.setUser(user);
			fmeasureAnnotation.addModelManagementConstruct(queryResult);
			queryResult.addAnnotation(fmeasureAnnotation);
			//annotationRepository.update(fmeasureAnnotation);
			//queryResultRepository.update(queryResult);
			//ontologyTermRepository.update(fmeasureOT);

			Annotation fractionOfAnnotatedResultsAnnotation = new Annotation(new Double(fractionOfAnnotatedResults).toString(),
					fractionOfAnnotatedResultsOT);
			fractionOfAnnotatedResultsAnnotation.setUser(user);
			fractionOfAnnotatedResultsAnnotation.addModelManagementConstruct(queryResult);
			queryResult.addAnnotation(fractionOfAnnotatedResultsAnnotation);
			//annotationRepository.update(fractionOfAnnotatedResultsAnnotation);
			//queryResultRepository.update(queryResult);
			//ontologyTermRepository.update(fractionOfAnnotatedResultsOT);

			//TODO not made persistent yet - sort this
		//}
		}
		*/
	}

	@Transactional(readOnly = true)
	private void propagateAnnotationsFromResultInstancesToMappingsProducingThoseResultInstances(Set<Annotation> annotationsToPropagate,
			Set<Mapping> mappingsToAnnotate, Set<ModelManagementConstruct> constrainingModelManagementConstructs,
			boolean duplicateAnnotationsAllowed, User user) {

		//TODO think about this: right now the mappings are annotated with precision and recall with respect to the query that produced the annotated resultInstances
		//which means a mapping will have different precision and recall annotations for different queries - I guess that makes sense
		//added constraining modelManagementConstructs to annotation for that reason, i.e., situations where an annotation is only valid under certain circumstances, e.g. for a particular query

		logger.debug("in propagateAnnotationsToMappingsUsedToProduceQueryResult");
		logger.debug("annotations: " + annotationsToPropagate);
		logger.debug("mappings: " + mappingsToAnnotate);
		logger.debug("user: " + user);
		logger.debug("constrainingModelManagementConstructs: " + constrainingModelManagementConstructs);
		if (constrainingModelManagementConstructs != null)
			logger.debug("constrainingModelManagementConstructs.size(): " + constrainingModelManagementConstructs.size());
		logger.debug("duplicateAnnotationsAllowed: " + duplicateAnnotationsAllowed);
		User annotationUser = annotationsToPropagate.iterator().next().getUser(); //this assumes all annotations provided as input have been provided by the same user
		logger.debug("annotationUser: " + annotationUser);
		if (annotationUser != user)
			logger.error("annotationUser != user - TODO sort this");

		Set<ResultInstance> resultInstances = getAnnotatedResultInstances(annotationsToPropagate);
		Set<Mapping> fetchedMappings = fetchMappings(mappingsToAnnotate);
		logger.debug("fetchedMappings: " + fetchedMappings);
		logger.debug("fetchedMappings.size(): " + fetchedMappings.size());

		//same user??? should be, but not sure
		Set<QueryResult> queryResultsProducingResultInstances = queryResultRepository.getQueryResultsThatResultInstancesBelongTo(resultInstances);
		logger.debug("queryResultsProducingResultInstances: " + queryResultsProducingResultInstances);
		logger.debug("queryResultsProducingResultInstances.size(): " + queryResultsProducingResultInstances.size());

		Set<Query> queries = getQueriesForQueryResultsAndAddCorrespondingMappingsToFetchedMappings(queryResultsProducingResultInstances,
				fetchedMappings);
		logger.debug("fetchedMappings: " + fetchedMappings);
		logger.debug("fetchedMappings.size(): " + fetchedMappings.size());
		if (queries.size() > 1)
			logger.error("more than one query - should all be the same query - I think - TODO check this");
		else {
			Query query = queries.iterator().next();

			if (!query.isJustScanQuery()) { //.getRootOperator().queryIsJustScan()) {
				logger.debug("TODO - not just scanQuery - propagate annotations from resultInstances of query to resultInstances of constructs queried");
				propagateAnnotationsFromResultInstancesOfQueryToResultInstancesOfCanonicalModelConstructsQueried(annotationsToPropagate, query,
						constrainingModelManagementConstructs, duplicateAnnotationsAllowed, user);
			}

			double beta = 1;

			//TODO think about this: I think this should be the TPs for a mapping not the whole query
			/*
			long numberOfKnownTpsForQuery = queryRepository.getNumberOfResultInstancesOfQueryWithGivenAnnotationForGivenOntologyTerm(query,
					"statisticalError", "tp");
			logger.debug("numberOfKnownTpsForQuery: " + numberOfKnownTpsForQuery);			

			if (numberOfKnownTpsForQuery > 0) {
			*/
			for (Mapping mapping : fetchedMappings) {
				logger.debug("mapping: " + mapping);

				Set<Mapping> mappingSet = new LinkedHashSet<Mapping>();
				mappingSet.add(mapping);
				logger.debug("mappingSet: " + mappingSet);

				long numberOfResultsForQueryReturnedByMappingUsed = queryRepository.getNumberOfResultInstancesOfQueryRetrievedByGivenMappings(query,
						mappingSet);
				logger.debug("numberOfResultsForQueryReturnedByMappingUsed: " + numberOfResultsForQueryReturnedByMappingUsed);

				long numberOfTpsForQueryReturnedByMappingUsed = queryRepository
						.getNumberOfResultInstancesOfQueryWithGivenAnnotationValueForGivenOntologyTermRetrievedByGivenMappings(query,
								"statisticalError", "tp", mappingSet);
				logger.debug("numberOfTpsForQueryReturnedByMappingUsed: " + numberOfTpsForQueryReturnedByMappingUsed);

				long numberOfFpsForQueryReturnedByMappingUsed = queryRepository
						.getNumberOfResultInstancesOfQueryWithGivenAnnotationValueForGivenOntologyTermRetrievedByGivenMappings(query,
								"statisticalError", "fp", mappingSet);
				logger.debug("numberOfFpsForQueryReturnedByMappingUsed: " + numberOfFpsForQueryReturnedByMappingUsed);

				long numberOfFnsForQueryReturnedByMappingUsed = queryRepository
						.getNumberOfResultInstancesOfQueryWithGivenAnnotationValueForGivenOntologyTermRetrievedByGivenMappings(query,
								"statisticalError", "fn", mappingSet);
				logger.debug("numberOfFnsForQueryReturnedByMappingUsed: " + numberOfFnsForQueryReturnedByMappingUsed);

				Map<String, Double> precisionRecallFMeasure = this.calculatePrecisionRecallAndFMeasure(numberOfTpsForQueryReturnedByMappingUsed,
						numberOfFpsForQueryReturnedByMappingUsed, numberOfFnsForQueryReturnedByMappingUsed, beta);

				//when an FN is added, it's associated to all the mappings that were used to expand the query
				//TODO decide whether the FNs should be included in here - I think they should be included, as they will be included in the recall and the f-measure
				double fractionOfAnnotatedResults = (numberOfTpsForQueryReturnedByMappingUsed + numberOfFpsForQueryReturnedByMappingUsed + numberOfFnsForQueryReturnedByMappingUsed)
						/ (double) numberOfResultsForQueryReturnedByMappingUsed;
				logger.debug("fractionOfAnnotatedResults: " + fractionOfAnnotatedResults);

				Double estimatedPrecision = precisionRecallFMeasure.get("precision");
				Double estimatedRecall = precisionRecallFMeasure.get("recall");
				Double estimatedFMeasure = precisionRecallFMeasure.get("fmeasure");

				logger.debug("estimatedPrecision: " + estimatedPrecision);
				logger.debug("estimatedRecall: " + estimatedRecall);
				logger.debug("estimatedFMeasure: " + estimatedFMeasure);

				if (estimatedPrecision != null)
					this.annotate("precision", estimatedPrecision.toString(), mapping, null, constrainingModelManagementConstructs, false, user);
				if (estimatedRecall != null)
					this.annotate("recall", estimatedRecall.toString(), mapping, null, constrainingModelManagementConstructs, false, user);
				if (estimatedFMeasure != null)
					this.annotate("f-measure", estimatedFMeasure.toString(), mapping, null, constrainingModelManagementConstructs, false, user);
				this.annotate("fractionOfAnnotatedResults", new Double(fractionOfAnnotatedResults).toString(), mapping, null,
						constrainingModelManagementConstructs, false, user);
			}
			//}
		}
	}

	@Transactional(readOnly = true)
	private void propagateAnnotationsFromResultInstancesOfQueryToResultInstancesOfCanonicalModelConstructsQueried(
			Set<Annotation> annotationsToPropagate, Query query, Set<ModelManagementConstruct> constrainingModelManagementConstructs,
			boolean duplicateAnnotationsAllowed, User user) {
		logger.debug("in propagateAnnotationsFromResultInstancesToCanonicalModelConstructsQueried");
		logger.debug("annotationsToPropagate: " + annotationsToPropagate);
		logger.debug("query: " + query);
		logger.debug("user: " + user);
		logger.debug("constrainingModelManagementConstructs: " + constrainingModelManagementConstructs);
		if (constrainingModelManagementConstructs != null)
			logger.debug("constrainingModelManagementConstructs.size(): " + constrainingModelManagementConstructs.size());
		logger.debug("duplicateAnnotationsAllowed: " + duplicateAnnotationsAllowed);

		User annotationUser = annotationsToPropagate.iterator().next().getUser(); //this assumes all annotations provided as input have been provided by the same user
		logger.debug("annotationUser: " + annotationUser);
		if (annotationUser != user)
			logger.error("annotationUser != user - TODO sort this");

		Set<ResultInstance> resultInstances = new HashSet<ResultInstance>();
		for (Annotation annotation : annotationsToPropagate) {
			logger.debug("annotation: " + annotation);
			Set<ModelManagementConstruct> annotatedConstructs = annotation.getAnnotatedModelManagementConstructs();
			for (ModelManagementConstruct annotatedConstruct : annotatedConstructs) {
				logger.debug("annotatedConstruct: " + annotatedConstruct);
				if (annotatedConstruct instanceof ResultInstance) {
					resultInstances.add((ResultInstance) annotatedConstruct);
				}
			}
		}

		//TODO still need to finish off implementation of propagation

	}

	public Map<String, Double> calculatePrecisionRecallAndFMeasure(long numberOfTPs, long numberOfFPs, long numberOfFNs, double beta) {
		Map<String, Double> precisionRecallFMeasure = new HashMap<String, Double>();
		logger.debug("in calculateAndReturnPrecisionRecallAndFMeasure");
		logger.debug("numberOfTPs: " + numberOfTPs);
		logger.debug("numberOfFPs: " + numberOfFPs);
		logger.debug("numberOfFNs: " + numberOfFNs);
		logger.debug("beta: " + beta);

		if ((numberOfTPs > 0) || (numberOfFPs > 0)) {

			double precision = (double) numberOfTPs / (double) (numberOfTPs + numberOfFPs);
			logger.debug("precision: " + precision);

			double recall = (double) numberOfTPs / (double) (numberOfTPs + numberOfFNs);
			logger.debug("recall: " + recall);

			double fmeasure = ((1 + (beta * beta)) * precision * recall) / (((beta * beta) * precision) + recall);
			if (((Object) fmeasure).toString().equals("NaN"))
				fmeasure = 0.0;
			logger.debug("fmeasure: " + fmeasure);

			precisionRecallFMeasure.put("precision", new Double(precision));
			precisionRecallFMeasure.put("recall", new Double(recall));
			precisionRecallFMeasure.put("fmeasure", new Double(fmeasure));
		}
		return precisionRecallFMeasure;
	}

	@Transactional(readOnly = true)
	private Set<Query> getQueriesForQueryResultsAndAddCorrespondingMappingsToFetchedMappings(Set<QueryResult> queryResultsProducingResultInstances,
			Set<Mapping> fetchedMappings) {
		Set<Query> queries = new HashSet<Query>();
		for (QueryResult queryResult : queryResultsProducingResultInstances) {
			logger.debug("queryResult: " + queryResult);
			logger.debug("queryResult.getMappings(): " + queryResult.getMappings());
			logger.debug("queryResult.getMappings().size(): " + queryResult.getMappings().size());
			queries.add(queryResult.getQuery());
			logger.debug("query: " + queryResult.getQuery());

			if (fetchedMappings == null || fetchedMappings.isEmpty()) {
				logger.debug("no mappings provided as input, get them from queryResults");
				if (fetchedMappings == null)
					fetchedMappings = queryResult.getMappings();
				else
					fetchedMappings.addAll(queryResult.getMappings());

				logger.debug("fetchedMappings: " + fetchedMappings);
				logger.debug("fetchedMappings.size(): " + fetchedMappings.size());
			}
		}
		return queries;
	}

	@Transactional(readOnly = true)
	private Set<ResultInstance> getAnnotatedResultInstances(Set<Annotation> annotations) {
		Set<ResultInstance> resultInstances = new HashSet<ResultInstance>();
		for (Annotation annotation : annotations) {
			logger.debug("annotation: " + annotation);
			Set<ModelManagementConstruct> annotatedConstructs = annotation.getAnnotatedModelManagementConstructs();
			for (ModelManagementConstruct annotatedConstruct : annotatedConstructs) {
				logger.debug("annotatedConstruct: " + annotatedConstruct);
				if (annotatedConstruct instanceof ResultInstance) {
					resultInstances.add((ResultInstance) annotatedConstruct);
				}
			}
		}
		return resultInstances;
	}

	@Transactional(readOnly = true)
	private Set<Mapping> fetchMappings(Set<Mapping> mappingsToAnnotate) {
		Set<Mapping> fetchedMappings = new LinkedHashSet<Mapping>();
		if (mappingsToAnnotate != null) {
			for (Mapping mapping : mappingsToAnnotate) {
				fetchedMappings.add(mappingRepository.find(mapping.getId()));
			}
		}
		return fetchedMappings;
	}

	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	public void addAnnotation(Annotation annotation) {
		logger.debug("in addAnnotation, annotation: " + annotation);
		annotationRepository.save(annotation);
		logger.debug("annotation saved: " + annotation);
	}

	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	public void deleteAnnotation(Long annotationId) {
		//TODO doesn't take into account any associations with anything
		Annotation annotation = annotationRepository.find(annotationId);
		OntologyTerm ontologyTerm = annotation.getOntologyTerm();
		//ontologyTerm.removeAnnotation(annotation);
		//TODO still need to get all the modelManagementConstructs with this annotation and remove the association to the annotation before deleting it
		annotationRepository.delete(annotation);
	}

	@Transactional(readOnly = true)
	public Annotation findAnnotation(Long annotationId) {
		return annotationRepository.find(annotationId);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl#getRepository()
	 */
	@Override
	public GenericRepository<Annotation, Long> getRepository() {
		return annotationRepository;
	}

}
