/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.CardinalityType;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.ConstructRelatedSchematicCorrespondenceType;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.DirectionalityType;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.ReconcilingExpression;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.ReconcilingExpressionType;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.SchematicCorrespondence;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.SchematicCorrespondenceType;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;
import uk.ac.manchester.dstoolkit.repository.morphisms.schematiccorrespondence.SchematicCorrespondenceRepository;
import uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.SchematicCorrespondenceService;

/**
 * @author chedeler
 *
 */
//@Transactional(readOnly = true)
@Service(value = "schematicCorrespondenceService")
public class SchematicCorrespondenceServiceImpl extends GenericEntityServiceImpl<SchematicCorrespondence, Long> implements
		SchematicCorrespondenceService {

	@Autowired
	@Qualifier("schematicCorrespondenceRepository")
	private SchematicCorrespondenceRepository schematicCorrespondenceRepository;

	//Input
	private List<SchematicCorrespondence> schematicCorrespondences1;
	private List<SchematicCorrespondence> schematicCorrespondences2;

	//Output
	private List<SchematicCorrespondence> composedSchematicCorrespondences;

	//sort out reconcilingExpression, move into parameter

	public List<SchematicCorrespondence> compose(List<SchematicCorrespondence> schematicCorrespondences1,
			List<SchematicCorrespondence> schematicCorrespondences2) {
		this.schematicCorrespondences1 = schematicCorrespondences1;
		this.schematicCorrespondences2 = schematicCorrespondences2;
		composedSchematicCorrespondences = new ArrayList<SchematicCorrespondence>();

		List<SchematicCorrespondence> scSet2 = new ArrayList<SchematicCorrespondence>(schematicCorrespondences2);
		List<SchematicCorrespondence> scSet1 = new ArrayList<SchematicCorrespondence>(schematicCorrespondences1);
		// nested for-loop to search for composable correspondences 
		for (int i = 0; i < scSet2.size(); i++) {
			SchematicCorrespondence sc2 = scSet2.get(i);
			for (int j = 0; j < scSet1.size(); j++) {
				SchematicCorrespondence sc1 = scSet1.get(j);
				//Check whether they are sa-sa corr or lex-lex corr
				if (sc1.getConstructRelatedSchematicCorrespondenceType() == ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT
						&& sc2.getConstructRelatedSchematicCorrespondenceType() == ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT) {
					// if sc1 and sc2 are sa-sa corr
					// check if they are 1-1 or m-n
					if (sc1.getCardinalityType() == CardinalityType.ONE_TO_ONE && sc2.getCardinalityType() == CardinalityType.ONE_TO_ONE) {
						// if they are 1-1
						SchematicCorrespondence scOut = composeO2OSaCorrespondences(sc1, sc2);
						if (scOut != null)
							composedSchematicCorrespondences.add(scOut);
					} else {
						// if they are m-n
						SchematicCorrespondence scOut = composeM2MSaCorrespondences(sc1, sc2);
						if (scOut != null)
							composedSchematicCorrespondences.add(scOut);
					}

				} else if (sc1.getConstructRelatedSchematicCorrespondenceType() == ConstructRelatedSchematicCorrespondenceType.SUPER_LEXICAL_TO_SUPER_LEXICAL
						&& sc2.getConstructRelatedSchematicCorrespondenceType() == ConstructRelatedSchematicCorrespondenceType.SUPER_LEXICAL_TO_SUPER_LEXICAL) {
					// if sc1 and sc2 are lex-lex corr
					// check if they are 1-1 or m-n
					if (sc1.getCardinalityType() == CardinalityType.ONE_TO_ONE && sc2.getCardinalityType() == CardinalityType.ONE_TO_ONE) {
						// if they are 1-1
						SchematicCorrespondence scOut = composeO2OLexCorrespondences(sc1, sc2);
						if (scOut != null)
							composedSchematicCorrespondences.add(scOut);
					} else {
						// if they are m-n
						SchematicCorrespondence scOut = composeM2MLexCorrespondences(sc1, sc2);
						if (scOut != null)
							composedSchematicCorrespondences.add(scOut);
					}
				}
			}

		}

		return composedSchematicCorrespondences;
	}

	private SchematicCorrespondence composeO2OSaCorrespondences(SchematicCorrespondence sc1, SchematicCorrespondence sc2) {

		SchematicCorrespondence sc3 = new SchematicCorrespondence("1:1SA2SA", "SA2SA", null);
		//sc3.setId(15);
		sc3.setCardinalityType(CardinalityType.ONE_TO_ONE);
		sc3.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT);
		SuperAbstract s2_sa1_1 = null;
		SuperAbstract s2_sa1_2 = null;
		SuperAbstract s1_sa1_1 = null;
		SuperAbstract s3_sa1_1 = null;
		/*
		List<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct> app_set1 = new ArrayList<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct>(
				sc1.getApplicationOfSchematicCorrespondenceToConstructs());
		List<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct> app_set2 = new ArrayList<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct>(
				sc2.getApplicationOfSchematicCorrespondenceToConstructs());
		*/
		// check whether the input correspondences all relate the same superabstracts of the MIDDLE schema.
		// Middle schema superabstract: group two of sc1 and group one of sc2

		s1_sa1_1 = (SuperAbstract) sc1.getConstructs2().iterator().next();
		s2_sa1_1 = (SuperAbstract) sc1.getConstructs2().iterator().next();
		/*
		for (int i = 0; i < app_set1.size(); i++) {
			ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct app = app_set1.get(i);
			if (app.getGroupLabel() == GroupLabel.TWO)
				s2_sa1_1 = (SuperAbstract) app.getCanonicalModelConstruct();
			if (app.getGroupLabel() == GroupLabel.ONE)
				s1_sa1_1 = (SuperAbstract) app.getCanonicalModelConstruct();
		}
		*/
		s2_sa1_2 = (SuperAbstract) sc2.getConstructs1().iterator().next();
		s3_sa1_1 = (SuperAbstract) sc2.getConstructs2().iterator().next();
		/*
		for (int i = 0; i < app_set2.size(); i++) {
			ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct app = app_set2.get(i);
			if (app.getGroupLabel() == GroupLabel.ONE)
				s2_sa1_2 = (SuperAbstract) app.getCanonicalModelConstruct();
			if (app.getGroupLabel() == GroupLabel.TWO)
				s3_sa1_1 = (SuperAbstract) app.getCanonicalModelConstruct();
		}
		*/
		if (s1_sa1_1 == null || s3_sa1_1 == null || s2_sa1_1 == null || s2_sa1_2 == null) {
			return null;
		}
		if (s2_sa1_1 != s2_sa1_2) {
			System.out.println("Not composable correspondences: difference middle schema superabstracts");
			return null;
		}
		// Update schematic correspondence type
		if (s3_sa1_1.getName().equals(s1_sa1_1.getName()))
			sc3.setSchematicCorrespondenceType(SchematicCorrespondenceType.SAME_NAME_SAME_CONSTRUCT);
		else
			sc3.setSchematicCorrespondenceType(SchematicCorrespondenceType.DIFFERENT_NAME_SAME_CONSTRUCT);
		// Relate the source and target of sc3
		sc3.addConstruct1(s1_sa1_1);
		sc3.addConstruct2(s3_sa1_1);
		/*
		ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct app1_sa1_sc = new ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct(
				GroupLabel.ONE, sc3, s1_sa1_1); // 1);
		ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct app2_sa1_sc = new ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct(
				GroupLabel.TWO, sc3, s3_sa1_1); // 1);
		*/
		sc3.setDirection(DirectionalityType.BIDIRECTIONAL);

		ReconcilingExpression re_source = new ReconcilingExpression();
		re_source.setTypeOfReconcilingExpression(ReconcilingExpressionType.SELECTION_PREDICATE);
		re_source.setExpression("*");
		//re_source.setSchematicCorrespondence(sc3);
		re_source.setSelectionTargetSuperAbstract(s3_sa1_1);
		sc3.addReconcilingExpression1(re_source);

		ReconcilingExpression re_target = new ReconcilingExpression();
		re_target.setTypeOfReconcilingExpression(ReconcilingExpressionType.SELECTION_PREDICATE);
		re_target.setExpression("*");
		//re_target.setSchematicCorrespondence(sc3);
		re_target.setSelectionTargetSuperAbstract(s1_sa1_1);
		sc3.addReconcilingExpression2(re_target);

		return sc3;
	}

	private SchematicCorrespondence composeM2MSaCorrespondences(SchematicCorrespondence sc1, SchematicCorrespondence sc2) {
		SchematicCorrespondence sc3 = new SchematicCorrespondence("m:nSA2SA", "SA2SA", null);
		//sc3.setId(15);
		Set<CanonicalModelConstruct> constr_s2_1 = sc1.getConstructs2();
		/*
		List<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct> app_set1 = new ArrayList<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct>(
				sc1.getApplicationOfSchematicCorrespondenceToConstructs());
		List<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct> app_s2_1 = new ArrayList<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct>();
		for (int i = 0; i < app_set1.size(); i++) {
			ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct app = app_set1.get(i);
			if (app.getGroupLabel() == GroupLabel.TWO) {
				app_s2_1.add(app);
			}
		}
		*/
		Set<CanonicalModelConstruct> constr_s2_2 = sc2.getConstructs1();
		/*
		List<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct> app_set2 = new ArrayList<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct>(
				sc2.getApplicationOfSchematicCorrespondenceToConstructs());
		List<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct> app_s2_2 = new ArrayList<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct>();
		for (int i = 0; i < app_set2.size(); i++) {
			ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct app = app_set2.get(i);
			if (app.getGroupLabel() == GroupLabel.ONE) {
				app_s2_2.add(app);
			}
		}
		*/
		if (!isComposableCorr(constr_s2_1, constr_s2_2))
			return null; // middle schema superabstracts do not match
		// Deciding cardinality and construct related type
		if (sc1.getCardinalityType() == CardinalityType.ONE_TO_ONE)
			sc3.setCardinalityType(CardinalityType.ONE_TO_MANY);
		else if (sc2.getCardinalityType() == CardinalityType.ONE_TO_ONE)
			sc3.setCardinalityType(CardinalityType.MANY_TO_ONE);
		else
			sc3.setCardinalityType(CardinalityType.MANY_TO_MANY);
		sc3.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT);
		// Deciding schematic correspondence type
		if (sc1.getSchematicCorrespondenceType() == SchematicCorrespondenceType.VERTICAL_PARTITIONING
				&& sc2.getSchematicCorrespondenceType() == SchematicCorrespondenceType.VERTICAL_PARTITIONING) {
			sc3.setSchematicCorrespondenceType(SchematicCorrespondenceType.VERTICAL_PARTITIONING);
			ReconcilingExpression re_selPred_source = new ReconcilingExpression();
			ReconcilingExpression re_selPred_target = new ReconcilingExpression();
			List<ReconcilingExpression> re_set11 = new ArrayList<ReconcilingExpression>(sc1.getReconcilingExpressions1());
			List<ReconcilingExpression> re_set12 = new ArrayList<ReconcilingExpression>(sc1.getReconcilingExpressions2());
			for (int i = 0; i < re_set11.size(); i++) {
				ReconcilingExpression re = re_set11.get(i);
				if (re.getTypeOfReconcilingExpression() == ReconcilingExpressionType.JOIN_PREDICATE) {
					ReconcilingExpression jp_out_1 = new ReconcilingExpression();
					jp_out_1.setTypeOfReconcilingExpression(ReconcilingExpressionType.JOIN_PREDICATE);
					jp_out_1.setJoinPred1(re.getJoinPred1());
					jp_out_1.setJoinPred2(re.getJoinPred2());
					jp_out_1.setExpression(re.getExpression());
					//jp_out_1.setSchematicCorrespondence(sc3);
					sc3.addReconcilingExpression1(jp_out_1);
				}
			}
			for (int i = 0; i < re_set12.size(); i++) {
				ReconcilingExpression re = re_set12.get(i);
				if (re.getTypeOfReconcilingExpression() == ReconcilingExpressionType.SELECTION_PREDICATE) {
					re_selPred_target.setSelectionTargetSuperAbstract(re.getSelectionTargetSuperAbstract());
				}
			}
			re_selPred_source.setTypeOfReconcilingExpression(ReconcilingExpressionType.SELECTION_PREDICATE);
			re_selPred_source.setExpression("*");
			//re_selPred_source.setSchematicCorrespondence(sc3);
			sc3.addReconcilingExpression1(re_selPred_source);

			List<ReconcilingExpression> re_set22 = new ArrayList<ReconcilingExpression>(sc2.getReconcilingExpressions2());
			List<ReconcilingExpression> re_set21 = new ArrayList<ReconcilingExpression>(sc2.getReconcilingExpressions1());
			for (int i = 0; i < re_set22.size(); i++) {
				ReconcilingExpression re = re_set22.get(i);
				if (re.getTypeOfReconcilingExpression() == ReconcilingExpressionType.JOIN_PREDICATE) {
					ReconcilingExpression jp_out_2 = new ReconcilingExpression();
					jp_out_2.setTypeOfReconcilingExpression(ReconcilingExpressionType.JOIN_PREDICATE);
					jp_out_2.setJoinPred1(re.getJoinPred1());
					jp_out_2.setJoinPred2(re.getJoinPred2());
					jp_out_2.setExpression(re.getExpression());
					//jp_out_2.setSchematicCorrespondence(sc3);
					sc3.addReconcilingExpression2(jp_out_2);
				}
			}
			for (int i = 0; i < re_set21.size(); i++) {
				ReconcilingExpression re = re_set21.get(i);
				if (re.getTypeOfReconcilingExpression() == ReconcilingExpressionType.SELECTION_PREDICATE) {
					re_selPred_source.setSelectionTargetSuperAbstract(re.getSelectionTargetSuperAbstract());
				}
			}
			re_selPred_target.setTypeOfReconcilingExpression(ReconcilingExpressionType.SELECTION_PREDICATE);
			re_selPred_target.setExpression("*");
			//re_selPred_target.setSchematicCorrespondence(sc3);
			sc3.addReconcilingExpression2(re_selPred_target);
		} else if (sc1.getSchematicCorrespondenceType() == SchematicCorrespondenceType.HORIZONTAL_PARTITIONING
				&& sc2.getSchematicCorrespondenceType() == SchematicCorrespondenceType.HORIZONTAL_PARTITIONING) {
			sc3.setSchematicCorrespondenceType(SchematicCorrespondenceType.HORIZONTAL_PARTITIONING);
			List<ReconcilingExpression> re_set1 = new ArrayList<ReconcilingExpression>(sc1.getReconcilingExpressions2());
			for (int i = 0; i < re_set1.size(); i++) {
				ReconcilingExpression re = re_set1.get(i);
				if (re.getTypeOfReconcilingExpression() == ReconcilingExpressionType.HOPA_PREDICATE) {
					ReconcilingExpression hpp_target = new ReconcilingExpression();
					hpp_target.setTypeOfReconcilingExpression(ReconcilingExpressionType.HOPA_PREDICATE);
					hpp_target.setSelectionTargetSuperAbstract(re.getSelectionTargetSuperAbstract());
					hpp_target.setExpression("*");
					//hpp_target.setSchematicCorrespondence(sc3);
					sc3.addReconcilingExpression2(hpp_target);

				}
			}
			List<ReconcilingExpression> re_set2 = new ArrayList<ReconcilingExpression>(sc2.getReconcilingExpressions1());
			for (int i = 0; i < re_set2.size(); i++) {
				ReconcilingExpression re = re_set2.get(i);
				if (re.getTypeOfReconcilingExpression() == ReconcilingExpressionType.HOPA_PREDICATE) {
					ReconcilingExpression hpp_source = new ReconcilingExpression();
					hpp_source.setTypeOfReconcilingExpression(ReconcilingExpressionType.HOPA_PREDICATE);
					hpp_source.setSelectionTargetSuperAbstract(re.getSelectionTargetSuperAbstract());
					hpp_source.setExpression("*");
					//hpp_source.setSchematicCorrespondence(sc3);
					sc3.addReconcilingExpression1(hpp_source);

				}
			}
		} else if (sc1.getSchematicCorrespondenceType() == SchematicCorrespondenceType.HORIZONTAL_VS_VERTICAL_PARTITIONING
				&& sc2.getSchematicCorrespondenceType() == SchematicCorrespondenceType.VERTICAL_PARTITIONING) {
			sc3.setSchematicCorrespondenceType(SchematicCorrespondenceType.HORIZONTAL_VS_VERTICAL_PARTITIONING);
			ReconcilingExpression re_selPred_source = new ReconcilingExpression();
			re_selPred_source.setTypeOfReconcilingExpression(ReconcilingExpressionType.SELECTION_PREDICATE);
			re_selPred_source.setExpression("*");
			//re_selPred_source.setSchematicCorrespondence(sc3);
			sc3.addReconcilingExpression1(re_selPred_source);
			List<ReconcilingExpression> re_set2 = new ArrayList<ReconcilingExpression>(sc2.getReconcilingExpressions2());
			List<ReconcilingExpression> re_set21 = new ArrayList<ReconcilingExpression>(sc2.getReconcilingExpressions1());
			for (int i = 0; i < re_set21.size(); i++) {
				ReconcilingExpression re = re_set21.get(i);
				if (re.getTypeOfReconcilingExpression() == ReconcilingExpressionType.SELECTION_PREDICATE) {
					re_selPred_source.setSelectionTargetSuperAbstract(re.getSelectionTargetSuperAbstract());
				}
			}
			for (int i = 0; i < re_set2.size(); i++) {
				ReconcilingExpression re = re_set2.get(i);
				if (re.getTypeOfReconcilingExpression() == ReconcilingExpressionType.JOIN_PREDICATE) {
					ReconcilingExpression jp_out_2 = new ReconcilingExpression();
					jp_out_2.setTypeOfReconcilingExpression(ReconcilingExpressionType.JOIN_PREDICATE);
					jp_out_2.setJoinPred1(re.getJoinPred1());
					jp_out_2.setJoinPred2(re.getJoinPred2());
					jp_out_2.setExpression(re.getExpression());
					//jp_out_2.setSchematicCorrespondence(sc3);
					sc3.addReconcilingExpression2(jp_out_2);
				}

			}
			List<ReconcilingExpression> re_set1 = new ArrayList<ReconcilingExpression>(sc1.getReconcilingExpressions2());
			for (int i = 0; i < re_set1.size(); i++) {
				ReconcilingExpression re = re_set1.get(i);
				if (re.getTypeOfReconcilingExpression() == ReconcilingExpressionType.HOPA_PREDICATE) {
					ReconcilingExpression hpp_target = new ReconcilingExpression();
					hpp_target.setTypeOfReconcilingExpression(ReconcilingExpressionType.HOPA_PREDICATE);
					hpp_target.setSelectionTargetSuperAbstract(re.getSelectionTargetSuperAbstract());
					hpp_target.setExpression("*");
					//hpp_target.setSchematicCorrespondence(sc3);
					sc3.addReconcilingExpression2(hpp_target);

				}
			}
		} else if (sc1.getSchematicCorrespondenceType() == SchematicCorrespondenceType.HORIZONTAL_PARTITIONING
				&& sc2.getSchematicCorrespondenceType() == SchematicCorrespondenceType.HORIZONTAL_VS_VERTICAL_PARTITIONING) {
			sc3.setSchematicCorrespondenceType(SchematicCorrespondenceType.HORIZONTAL_VS_VERTICAL_PARTITIONING);
			ReconcilingExpression re_selPred_source = new ReconcilingExpression();
			re_selPred_source.setTypeOfReconcilingExpression(ReconcilingExpressionType.SELECTION_PREDICATE);
			re_selPred_source.setExpression("*");
			//re_selPred_source.setSchematicCorrespondence(sc3);
			sc3.addReconcilingExpression1(re_selPred_source);
			List<ReconcilingExpression> re_set2 = new ArrayList<ReconcilingExpression>(sc2.getReconcilingExpressions2());
			List<ReconcilingExpression> re_set21 = new ArrayList<ReconcilingExpression>(sc2.getReconcilingExpressions1());
			for (int i = 0; i < re_set21.size(); i++) {
				ReconcilingExpression re = re_set21.get(i);
				if (re.getTypeOfReconcilingExpression() == ReconcilingExpressionType.SELECTION_PREDICATE) {
					re_selPred_source.setSelectionTargetSuperAbstract(re.getSelectionTargetSuperAbstract());
				}
			}
			for (int i = 0; i < re_set2.size(); i++) {
				ReconcilingExpression re = re_set2.get(i);
				if (re.getTypeOfReconcilingExpression() == ReconcilingExpressionType.JOIN_PREDICATE) {
					ReconcilingExpression jp_out_2 = new ReconcilingExpression();
					jp_out_2.setTypeOfReconcilingExpression(ReconcilingExpressionType.JOIN_PREDICATE);
					jp_out_2.setJoinPred1(re.getJoinPred1());
					jp_out_2.setJoinPred2(re.getJoinPred2());
					jp_out_2.setExpression(re.getExpression());
					//jp_out_2.setSchematicCorrespondence(sc3);
					sc3.addReconcilingExpression2(jp_out_2);
				}

			}
			List<ReconcilingExpression> re_set1 = new ArrayList<ReconcilingExpression>(sc1.getReconcilingExpressions2());
			for (int i = 0; i < re_set1.size(); i++) {
				ReconcilingExpression re = re_set1.get(i);
				if (re.getTypeOfReconcilingExpression() == ReconcilingExpressionType.HOPA_PREDICATE) {
					ReconcilingExpression hpp_target = new ReconcilingExpression();
					hpp_target.setTypeOfReconcilingExpression(ReconcilingExpressionType.HOPA_PREDICATE);
					hpp_target.setSelectionTargetSuperAbstract(re.getSelectionTargetSuperAbstract());
					hpp_target.setExpression("*");
					//hpp_target.setSchematicCorrespondence(sc3);
					sc3.addReconcilingExpression2(hpp_target);

				}
			}
		} else if (sc1.getCardinalityType() == CardinalityType.ONE_TO_ONE) {
			if (sc2.getSchematicCorrespondenceType() == SchematicCorrespondenceType.HORIZONTAL_PARTITIONING) {
				sc3.setSchematicCorrespondenceType(SchematicCorrespondenceType.HORIZONTAL_PARTITIONING);
				ReconcilingExpression re_selPred_target = new ReconcilingExpression();
				List<ReconcilingExpression> re_set2 = new ArrayList<ReconcilingExpression>(sc2.getReconcilingExpressions1());
				for (int i = 0; i < re_set2.size(); i++) {
					ReconcilingExpression re = re_set2.get(i);
					if (re.getTypeOfReconcilingExpression() == ReconcilingExpressionType.HOPA_PREDICATE) {
						ReconcilingExpression hpp_source = new ReconcilingExpression();
						hpp_source.setTypeOfReconcilingExpression(ReconcilingExpressionType.HOPA_PREDICATE);
						hpp_source.setSelectionTargetSuperAbstract(re.getSelectionTargetSuperAbstract());
						hpp_source.setExpression("*");
						//hpp_source.setSchematicCorrespondence(sc3);
						sc3.addReconcilingExpression1(hpp_source);

					}
				}
				List<ReconcilingExpression> re_set12 = new ArrayList<ReconcilingExpression>(sc1.getReconcilingExpressions2());
				for (int i = 0; i < re_set12.size(); i++) {
					ReconcilingExpression re = re_set12.get(i);
					if (re.getTypeOfReconcilingExpression() == ReconcilingExpressionType.SELECTION_PREDICATE) {
						re_selPred_target.setSelectionTargetSuperAbstract(re.getSelectionTargetSuperAbstract());
					}
				}
				re_selPred_target.setTypeOfReconcilingExpression(ReconcilingExpressionType.SELECTION_PREDICATE);
				re_selPred_target.setExpression("*");
				//re_selPred_target.setSchematicCorrespondence(sc3);
				sc3.addReconcilingExpression2(re_selPred_target);

			} else if (sc2.getSchematicCorrespondenceType() == SchematicCorrespondenceType.VERTICAL_PARTITIONING) {
				sc3.setSchematicCorrespondenceType(SchematicCorrespondenceType.VERTICAL_PARTITIONING);
				ReconcilingExpression re_selPred_source = new ReconcilingExpression();
				ReconcilingExpression re_selPred_target = new ReconcilingExpression();
				List<ReconcilingExpression> re_set12 = new ArrayList<ReconcilingExpression>(sc1.getReconcilingExpressions2());
				for (int i = 0; i < re_set12.size(); i++) {
					ReconcilingExpression re = re_set12.get(i);
					if (re.getTypeOfReconcilingExpression() == ReconcilingExpressionType.SELECTION_PREDICATE) {
						re_selPred_target.setSelectionTargetSuperAbstract(re.getSelectionTargetSuperAbstract());
					}
				}
				re_selPred_source.setTypeOfReconcilingExpression(ReconcilingExpressionType.SELECTION_PREDICATE);
				re_selPred_source.setExpression("*");
				//re_selPred_source.setSchematicCorrespondence(sc3);
				sc3.addReconcilingExpression1(re_selPred_source);

				List<ReconcilingExpression> re_set22 = new ArrayList<ReconcilingExpression>(sc2.getReconcilingExpressions2());
				List<ReconcilingExpression> re_set21 = new ArrayList<ReconcilingExpression>(sc2.getReconcilingExpressions1());
				for (int i = 0; i < re_set22.size(); i++) {
					ReconcilingExpression re = re_set22.get(i);
					if (re.getTypeOfReconcilingExpression() == ReconcilingExpressionType.JOIN_PREDICATE) {
						ReconcilingExpression jp_out_2 = new ReconcilingExpression();
						jp_out_2.setTypeOfReconcilingExpression(ReconcilingExpressionType.JOIN_PREDICATE);
						jp_out_2.setJoinPred1(re.getJoinPred1());
						jp_out_2.setJoinPred2(re.getJoinPred2());
						jp_out_2.setExpression(re.getExpression());
						//jp_out_2.setSchematicCorrespondence(sc3);
						sc3.addReconcilingExpression2(jp_out_2);
					}

				}
				for (int i = 0; i < re_set21.size(); i++) {
					ReconcilingExpression re = re_set21.get(i);
					if (re.getTypeOfReconcilingExpression() == ReconcilingExpressionType.SELECTION_PREDICATE) {
						re_selPred_source.setSelectionTargetSuperAbstract(re.getSelectionTargetSuperAbstract());
					}
				}
				re_selPred_target.setTypeOfReconcilingExpression(ReconcilingExpressionType.SELECTION_PREDICATE);
				re_selPred_target.setExpression("*");
				//re_selPred_target.setSchematicCorrespondence(sc3);
				sc3.addReconcilingExpression2(re_selPred_target);
			} else
				return null;
		} else if (sc2.getCardinalityType() == CardinalityType.ONE_TO_ONE) {
			if (sc1.getSchematicCorrespondenceType() == SchematicCorrespondenceType.HORIZONTAL_PARTITIONING) {
				sc3.setSchematicCorrespondenceType(SchematicCorrespondenceType.HORIZONTAL_PARTITIONING);
				List<ReconcilingExpression> re_set1 = new ArrayList<ReconcilingExpression>(sc1.getReconcilingExpressions2());
				for (int i = 0; i < re_set1.size(); i++) {
					ReconcilingExpression re = re_set1.get(i);
					if (re.getTypeOfReconcilingExpression() == ReconcilingExpressionType.HOPA_PREDICATE) {
						ReconcilingExpression hpp_target = new ReconcilingExpression();
						hpp_target.setTypeOfReconcilingExpression(ReconcilingExpressionType.HOPA_PREDICATE);
						hpp_target.setSelectionTargetSuperAbstract(re.getSelectionTargetSuperAbstract());
						hpp_target.setExpression("*");
						//hpp_target.setSchematicCorrespondence(sc3);
						sc3.addReconcilingExpression2(hpp_target);

					}
				}
				ReconcilingExpression re_selPred_source = new ReconcilingExpression();
				List<ReconcilingExpression> re_set21 = new ArrayList<ReconcilingExpression>(sc2.getReconcilingExpressions1());
				for (int i = 0; i < re_set21.size(); i++) {
					ReconcilingExpression re = re_set21.get(i);
					if (re.getTypeOfReconcilingExpression() == ReconcilingExpressionType.SELECTION_PREDICATE) {
						re_selPred_source.setSelectionTargetSuperAbstract(re.getSelectionTargetSuperAbstract());
					}
				}
				re_selPred_source.setTypeOfReconcilingExpression(ReconcilingExpressionType.SELECTION_PREDICATE);
				re_selPred_source.setExpression("*");
				//re_selPred_source.setSchematicCorrespondence(sc3);
				sc3.addReconcilingExpression1(re_selPred_source);
			} else if (sc1.getSchematicCorrespondenceType() == SchematicCorrespondenceType.VERTICAL_PARTITIONING) {
				sc3.setSchematicCorrespondenceType(SchematicCorrespondenceType.VERTICAL_PARTITIONING);
				ReconcilingExpression re_selPred_source = new ReconcilingExpression();
				ReconcilingExpression re_selPred_target = new ReconcilingExpression();
				List<ReconcilingExpression> re_set11 = new ArrayList<ReconcilingExpression>(sc1.getReconcilingExpressions1());
				List<ReconcilingExpression> re_set12 = new ArrayList<ReconcilingExpression>(sc1.getReconcilingExpressions2());
				for (int i = 0; i < re_set11.size(); i++) {
					ReconcilingExpression re = re_set11.get(i);
					if (re.getTypeOfReconcilingExpression() == ReconcilingExpressionType.JOIN_PREDICATE) {
						ReconcilingExpression jp_out_1 = new ReconcilingExpression();
						jp_out_1.setTypeOfReconcilingExpression(ReconcilingExpressionType.JOIN_PREDICATE);
						jp_out_1.setJoinPred1(re.getJoinPred1());
						jp_out_1.setJoinPred2(re.getJoinPred2());
						jp_out_1.setExpression(re.getExpression());
						//jp_out_1.setSchematicCorrespondence(sc3);
						sc3.addReconcilingExpression1(jp_out_1);
					}
				}
				for (int i = 0; i < re_set12.size(); i++) {
					ReconcilingExpression re = re_set12.get(i);
					if (re.getTypeOfReconcilingExpression() == ReconcilingExpressionType.SELECTION_PREDICATE) {
						re_selPred_target.setSelectionTargetSuperAbstract(re.getSelectionTargetSuperAbstract());
					}
				}
				re_selPred_source.setTypeOfReconcilingExpression(ReconcilingExpressionType.SELECTION_PREDICATE);
				re_selPred_source.setExpression("*");
				//re_selPred_source.setSchematicCorrespondence(sc3);
				sc3.addReconcilingExpression1(re_selPred_source);

				List<ReconcilingExpression> re_set21 = new ArrayList<ReconcilingExpression>(sc2.getReconcilingExpressions1());
				for (int i = 0; i < re_set21.size(); i++) {
					ReconcilingExpression re = re_set21.get(i);
					if (re.getTypeOfReconcilingExpression() == ReconcilingExpressionType.SELECTION_PREDICATE) {
						re_selPred_source.setSelectionTargetSuperAbstract(re.getSelectionTargetSuperAbstract());
					}
				}
				re_selPred_target.setTypeOfReconcilingExpression(ReconcilingExpressionType.SELECTION_PREDICATE);
				re_selPred_target.setExpression("*");
				//re_selPred_target.setSchematicCorrespondence(sc3);
				sc3.addReconcilingExpression2(re_selPred_target);

			} else
				return null;
		} else
			return null;
		sc3.addAllConstructs1(sc1.getConstructs1());
		/*
		for (int i = 0; i < app_set1.size(); i++) {
			ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct app = app_set1.get(i);
			CanonicalModelConstruct c = app.getCanonicalModelConstruct();
			if (app.getGroupLabel() == GroupLabel.ONE) {

				ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct app_out = new ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct(
						GroupLabel.ONE, sc3, c); //1);
			}
		}
		*/
		sc3.addAllConstructs2(sc2.getConstructs2());
		/*
		for (int i = 0; i < app_set2.size(); i++) {
			ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct app = app_set2.get(i);
			CanonicalModelConstruct c = app.getCanonicalModelConstruct();
			if (app.getGroupLabel() == GroupLabel.TWO) {

				ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct app_out = new ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct(
						GroupLabel.TWO, sc3, c); //1);
			}
		}
		System.out.println(sc3.getReconcilingExpressions1());
		System.out.println(sc3.getReconcilingExpressions2());
		*/
		return sc3;
	}

	private boolean isComposableCorr(Set<CanonicalModelConstruct> constr_1, Set<CanonicalModelConstruct> constr_2) {
		if (constr_1.size() != constr_2.size())
			return false;
		for (CanonicalModelConstruct construct1 : constr_1) {
			boolean found = false;
			for (CanonicalModelConstruct construct2 : constr_2) {
				if (construct1.equals(construct2)) {
					found = true;
					break;
				}
			}
			if (!found)
				return false;
		}
		return true;
	}

	/*
	private boolean isComposableCorr(List<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct> app_1,
			List<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct> app_2) {
		if (app_1.size() != app_2.size())
			return false;
		for (int i = 0; i < app_1.size(); i++) {
			ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct app1 = app_1.get(i);
			boolean found = false;
			for (int j = 0; j < app_2.size(); j++) {
				ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct app2 = app_2.get(j);
				if (app1.getCanonicalModelConstruct() == app2.getCanonicalModelConstruct()) {
					found = true;
					break;
				}
			}
			if (!found)
				return false;
		}
		return true;
	}
	*/

	private SchematicCorrespondence composeO2OLexCorrespondences(SchematicCorrespondence sc1, SchematicCorrespondence sc2) {

		SchematicCorrespondence sc3 = new SchematicCorrespondence("1:1Lex2Lex", "Lex2Lex", null);
		//sc3.setId(15);
		sc3.setCardinalityType(CardinalityType.ONE_TO_ONE);
		sc3.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_LEXICAL_TO_SUPER_LEXICAL);
		SuperLexical s2_lex1_1 = null;
		SuperLexical s2_lex1_2 = null;
		SuperLexical s1_lex1_1 = null;
		SuperLexical s3_lex1_1 = null;
		/*
		List<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct> app_set1 = new ArrayList<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct>(
				sc1.getApplicationOfSchematicCorrespondenceToConstructs());
		List<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct> app_set2 = new ArrayList<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct>(
				sc2.getApplicationOfSchematicCorrespondenceToConstructs());
		*/
		// check whether the input correspondences all relate the same superlexicals of the MIDDLE schema.
		// Middle schema superlexicals: group two of sc1 and group one of sc2

		//TODO might require an invert if not in right order; this currently assumes they're in the right order

		s1_lex1_1 = (SuperLexical) sc1.getConstructs1().iterator().next();
		s2_lex1_1 = (SuperLexical) sc1.getConstructs2().iterator().next();

		/*
		for (int i = 0; i < app_set1.size(); i++) {
			ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct app = app_set1.get(i);
			if (app.getGroupLabel() == GroupLabel.TWO)
				s2_lex1_1 = (SuperLexical) app.getCanonicalModelConstruct();
			if (app.getGroupLabel() == GroupLabel.ONE)
				s1_lex1_1 = (SuperLexical) app.getCanonicalModelConstruct();
		}
		*/

		s2_lex1_2 = (SuperLexical) sc2.getConstructs1().iterator().next();
		s3_lex1_1 = (SuperLexical) sc2.getConstructs2().iterator().next();

		/*
		for (int i = 0; i < app_set2.size(); i++) {
			ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct app = app_set2.get(i);
			if (app.getGroupLabel() == GroupLabel.ONE)
				s2_lex1_2 = (SuperLexical) app.getCanonicalModelConstruct();
			if (app.getGroupLabel() == GroupLabel.TWO)
				s3_lex1_1 = (SuperLexical) app.getCanonicalModelConstruct();
		}
		*/
		if (s1_lex1_1 == null || s3_lex1_1 == null || s2_lex1_1 == null || s2_lex1_2 == null) {
			return null;
		}
		if (s2_lex1_1 != s2_lex1_2) {
			System.out.println("Not composable correspondences: difference middle schema superabstracts");
			return null;
		}
		// Update schematic correspondence type
		if (s3_lex1_1.getName().equals(s1_lex1_1.getName()))
			sc3.setSchematicCorrespondenceType(SchematicCorrespondenceType.SAME_NAME_SAME_CONSTRUCT);
		else
			sc3.setSchematicCorrespondenceType(SchematicCorrespondenceType.DIFFERENT_NAME_SAME_CONSTRUCT);
		// Relate the source and target of sc3
		sc3.addConstruct1(s1_lex1_1);
		sc3.addConstruct2(s3_lex1_1);
		/*
		ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct app1_lex1_sc = new ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct(
				GroupLabel.ONE, sc3, s1_lex1_1); //1);
		ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct app2_lex1_sc = new ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct(
				GroupLabel.TWO, sc3, s3_lex1_1); //1);
		*/
		sc3.setDirection(DirectionalityType.BIDIRECTIONAL);

		return sc3;
	}

	private SchematicCorrespondence composeM2MLexCorrespondences(SchematicCorrespondence sc1, SchematicCorrespondence sc2) {
		SchematicCorrespondence sc3 = new SchematicCorrespondence("m:nLex2Lex", "Lex2Lex", null);
		//sc3.setId(15);
		Set<CanonicalModelConstruct> constr_s2_1 = sc1.getConstructs2();
		/*
		List<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct> app_set1 = new ArrayList<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct>(
				sc1.getApplicationOfSchematicCorrespondenceToConstructs());
		List<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct> app_s2_1 = new ArrayList<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct>();
		for (int i = 0; i < app_set1.size(); i++) {
			ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct app = app_set1.get(i);
			if (app.getGroupLabel() == GroupLabel.TWO) {
				app_s2_1.add(app);
			}
		}
		*/
		Set<CanonicalModelConstruct> constr_s2_2 = sc2.getConstructs1();
		/*
		List<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct> app_set2 = new ArrayList<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct>(
				sc2.getApplicationOfSchematicCorrespondenceToConstructs());
		List<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct> app_s2_2 = new ArrayList<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct>();
		for (int i = 0; i < app_set2.size(); i++) {
			ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct app = app_set2.get(i);
			if (app.getGroupLabel() == GroupLabel.ONE) {
				app_s2_2.add(app);
			}
		}
		*/
		if (!isComposableCorr(constr_s2_1, constr_s2_2))
			return null; // middle schema superabstracts do not match
		// Deciding cardinality and construct related type
		if (sc1.getCardinalityType() == CardinalityType.ONE_TO_ONE)
			sc3.setCardinalityType(CardinalityType.ONE_TO_MANY);
		else if (sc2.getCardinalityType() == CardinalityType.ONE_TO_ONE)
			sc3.setCardinalityType(CardinalityType.MANY_TO_ONE);
		else
			sc3.setCardinalityType(CardinalityType.MANY_TO_MANY);
		sc3.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_LEXICAL_TO_SUPER_LEXICAL);
		sc3.setSchematicCorrespondenceType(SchematicCorrespondenceType.LEXICAL_PARTITIONING);
		sc3.addAllConstructs1(sc1.getConstructs1());
		/*
		for (int i = 0; i < app_set1.size(); i++) {
			ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct app = app_set1.get(i);
			CanonicalModelConstruct c = app.getCanonicalModelConstruct();
			if (app.getGroupLabel() == GroupLabel.ONE) {
				@SuppressWarnings("unused")
				ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct app_out = new ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct(
						GroupLabel.ONE, sc3, c); //1);
			}
		}
		*/
		sc3.addAllConstructs2(sc2.getConstructs2());
		/*
		for (int i = 0; i < app_set2.size(); i++) {
			ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct app = app_set2.get(i);
			CanonicalModelConstruct c = app.getCanonicalModelConstruct();
			if (app.getGroupLabel() == GroupLabel.TWO) {
				@SuppressWarnings("unused")
				ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct app_out = new ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct(
						GroupLabel.TWO, sc3, c); //1);
			}
		}
		*/
		return sc3;
	}
	
	
	/**
	 * Invert the directions of schematic correspondence to cardinality one_to_one
	 */
	public SchematicCorrespondence invert(SchematicCorrespondence sc){
		
		SchematicCorrespondence newSchematicCorrespondence = new SchematicCorrespondence();	
				
		if (sc.getCardinalityType().equals(CardinalityType.ONE_TO_ONE)){
					
			if (sc.getConstructRelatedSchematicCorrespondenceType().equals(ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT)){
				
				newSchematicCorrespondence = getInverseSchematicCorrepondence(sc);
				
				for (SchematicCorrespondence childsc:sc.getChildSchematicCorrespondences()){
					
					newSchematicCorrespondence.addChildSchematicCorrespondence(getInverseSchematicCorrepondence(childsc));
	    		
				}	
			
			}
		}

		return newSchematicCorrespondence;
	}

	private SchematicCorrespondence getInverseSchematicCorrepondence(SchematicCorrespondence sc) {
		
		SchematicCorrespondence schematicCorrespondence = null;
		
		String shortName=sc.getShortName();
		Scanner s = new Scanner(shortName);
		s.useDelimiter(Pattern.compile("_"));	
		  
		String type = "";
		String asName = "";
		String fieldName = "";
		
		if (s.hasNext())
			type = s.next();
		if (s.hasNext())
			asName = s.next();
		if (s.hasNext())
			fieldName = s.next();
		
		try{
			
		    SchematicCorrespondenceType schematicCorrespondenceType = null;	
		    
		    CanonicalModelConstruct sourceConstruct = sc.getConstructs2().iterator().next();
			CanonicalModelConstruct targetConstruct = sc.getConstructs1().iterator().next();
			
		    String shortNameSC = null;
			String name = null;
			
			if (!type.equalsIgnoreCase("MSL")){
				
				schematicCorrespondence = new SchematicCorrespondence();
				
				if (asName.equals(fieldName)) {
					name = "SameNameSameConstruct_" + sourceConstruct.getSchema().getName() + "." + sourceConstruct.getName() + "_"
							+ targetConstruct.getSchema().getName() + "." + targetConstruct.getName();
					shortNameSC = "SNSC" + "_" + sourceConstruct.getName() + "_" + targetConstruct.getName();
					schematicCorrespondenceType = SchematicCorrespondenceType.SAME_NAME_SAME_CONSTRUCT;
				} else {
					name = "DifferentNameSameConstruct_" + sourceConstruct.getSchema().getName() + "." + sourceConstruct.getName() + "_"
							+ targetConstruct.getSchema().getName() + "." + targetConstruct.getName();
					shortNameSC = "DNSC_" + sourceConstruct.getName() + "_" + targetConstruct.getName();
					schematicCorrespondenceType = SchematicCorrespondenceType.DIFFERENT_NAME_SAME_CONSTRUCT;
				}

				schematicCorrespondence.setName(name);
				schematicCorrespondence.setShortName(shortNameSC);
				schematicCorrespondence.setSchematicCorrespondenceType(schematicCorrespondenceType);
				schematicCorrespondence.addConstruct1(sourceConstruct);
				schematicCorrespondence.addConstruct2(targetConstruct);
				schematicCorrespondence.setDirection(DirectionalityType.BIDIRECTIONAL);
				schematicCorrespondence
						.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT);
				schematicCorrespondence.setCardinalityType(CardinalityType.ONE_TO_ONE);
				
			} else if (type.equalsIgnoreCase("MSL")){
			
				schematicCorrespondence = new SchematicCorrespondence();
											
				if (sc.getDirection().equals(DirectionalityType.SECOND_TO_FIRST)){
					schematicCorrespondence.setDirection(DirectionalityType.FIRST_TO_SECOND);
					shortNameSC = "MSL_" + asName + "_NULL";
					name ="MissingSuperLexical_in_source_"+ sourceConstruct.getSchema().getName() + "." + sourceConstruct.getName();
					schematicCorrespondenceType = SchematicCorrespondenceType.MISSING_SUPER_LEXICAL;
					
				} else if (sc.getDirection().equals(DirectionalityType.FIRST_TO_SECOND)){
					schematicCorrespondence.setDirection(DirectionalityType.SECOND_TO_FIRST);
					shortNameSC = "MSL_" + asName + "_NULL";
					name ="MissingSuperLexical_in_target_"+ targetConstruct.getSchema().getName() + "." + targetConstruct.getName();
					schematicCorrespondenceType = SchematicCorrespondenceType.MISSING_SUPER_LEXICAL;
					
				}
				
				schematicCorrespondence.setName(name);
				schematicCorrespondence.setShortName(shortNameSC);
				schematicCorrespondence.setSchematicCorrespondenceType(schematicCorrespondenceType);
				schematicCorrespondence.addConstruct1(sourceConstruct);
				schematicCorrespondence.addConstruct2(targetConstruct);
				schematicCorrespondence.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT);
				schematicCorrespondence.setCardinalityType(CardinalityType.ONE_TO_ONE);

			}
		
						
		}catch(Exception e){
			System.out.println("ERROR: "+e);
		}
		
		return schematicCorrespondence;
	}
	

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.schematiccorrespondence.SchematicCorrespondenceService#addSchemticCorrespondence(uk.ac.manchester.dataspaces.domain.models.schematiccorrespondence.SchematicCorrespondence)
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addSchematicCorrespondence(SchematicCorrespondence schematicCorrespondence) {
		schematicCorrespondenceRepository.addSchematicCorrespondence(schematicCorrespondence);
	}
	
	public SchematicCorrespondence findSchematicCorrespondenceByName(String schematicCorrespondenceName){
		return schematicCorrespondenceRepository.findSchematicCorrespondenceByName(schematicCorrespondenceName);
	}
	
	public List<Long> findAllSchematicCorrespondences(){
		return schematicCorrespondenceRepository.findAllSchematicCorrespondences();
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.schematiccorrespondence.SchematicCorrespondenceService#deleteSchematicCorrespondence(java.lang.Long)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Transactional
	public void deleteSchematicCorrespondence(Long schematicCorrespondenceId) {
		// TODO
		schematicCorrespondenceRepository.delete(schematicCorrespondenceRepository.find(schematicCorrespondenceId));
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.schematiccorrespondence.SchematicCorrespondenceService#findSchematicCorrespondence(java.lang.Long)
	 */
	@Transactional(readOnly = true)
	public SchematicCorrespondence findSchematicCorrespondence(Long schematicCorrespondenceId) {
		return schematicCorrespondenceRepository.find(schematicCorrespondenceId);
	}

	/**
	 * @param schematicCorrespondenceRepository the schematicCorrespondenceRepository to set
	 */
	public void setSchematicCorrespondenceRepository(SchematicCorrespondenceRepository schematicCorrespondenceRepository) {
		this.schematicCorrespondenceRepository = schematicCorrespondenceRepository;
	}

	/**
	 * @return the schematicCorrespondenceRepository
	 */
	public SchematicCorrespondenceRepository getSchematicCorrespondenceRepository() {
		return schematicCorrespondenceRepository;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl#getRepository()
	 */
	@Override
	public GenericRepository<SchematicCorrespondence, Long> getRepository() {
		return schematicCorrespondenceRepository;
	}

}
