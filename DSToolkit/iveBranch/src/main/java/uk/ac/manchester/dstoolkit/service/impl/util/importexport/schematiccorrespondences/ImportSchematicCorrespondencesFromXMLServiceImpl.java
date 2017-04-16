package uk.ac.manchester.dstoolkit.service.impl.util.importexport.schematiccorrespondences;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.ConstructType;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.DirectionalityType;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.Parameter;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.SchematicCorrespondence;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.SchematicCorrespondenceType;
import uk.ac.manchester.dstoolkit.repository.canonical.SuperAbstractRepository;
import uk.ac.manchester.dstoolkit.repository.canonical.SuperLexicalRepository;
import uk.ac.manchester.dstoolkit.repository.canonical.SuperRelationshipRepository;
import uk.ac.manchester.dstoolkit.repository.morphisms.schematiccorrespondence.SchematicCorrespondenceRepository;
import uk.ac.manchester.dstoolkit.service.util.importexport.schematiccorrespondences.ImportSchematicCorrespondencesFromXMLService;

//TODO write proper tests

@Transactional(readOnly = true)
@Service(value = "importSchematicCorrespondencesFromXMLServiceImpl")
public class ImportSchematicCorrespondencesFromXMLServiceImpl implements ImportSchematicCorrespondencesFromXMLService {

	private static Logger logger = Logger.getLogger(ImportSchematicCorrespondencesFromXMLServiceImpl.class);

	@Autowired
	@Qualifier("schematicCorrespondenceRepository")
	private SchematicCorrespondenceRepository schematicCorrespondenceRepository;

	@Autowired
	@Qualifier("superAbstractRepository")
	private SuperAbstractRepository superAbstractRepository;

	@Autowired
	@Qualifier("superLexicalRepository")
	private SuperLexicalRepository superLexicalRepository;

	@Autowired
	@Qualifier("superRelationshipRepository")
	private SuperRelationshipRepository superRelationshipRepository;

	//private final String fileLocation = "./src/main/xml/schematicCorrespondences.xml";
	private final Set<SchematicCorrespondence> schematicCorrespondences = new HashSet<SchematicCorrespondence>();

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.util.importexport.schematiccorrespondences.ImportSchematicCorrespondencesFromXMLService#readSchematicCorrespondencesFromXml(java.lang.String)
	 */
	public void readSchematicCorrespondencesFromXml(String fileLocation) throws JAXBException, FileNotFoundException {
		JAXBContext context = JAXBContext.newInstance("uk.ac.manchester.dstoolkit.service.impl.util.importexport.schematiccorrespondences");
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Correspondences correspondences = (Correspondences) unmarshaller.unmarshal(new FileReader(fileLocation));

		List<Correspondence> listOfCorrespondences = correspondences.getCorrespondence();
		for (Correspondence correspondence : listOfCorrespondences) {
			logger.debug("Name = " + correspondence.getName() + ", ShortName = " + correspondence.getShortName());

			SchematicCorrespondence currentSchematicCorrespondence = buildSchematicCorrespondence(correspondence, null);
			schematicCorrespondences.add(currentSchematicCorrespondence);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	private SchematicCorrespondence buildSchematicCorrespondence(Correspondence correspondence, SchematicCorrespondence parentSchematicCorrespondence) {

		String type = correspondence.getType();
		SchematicCorrespondenceType schematicCorrespondenceType = getSchematicCorrespondenceType(type);

		SchematicCorrespondence schematicCorrespondence = new SchematicCorrespondence(correspondence.getName(), correspondence.getShortName(),
				schematicCorrespondenceType);
		schematicCorrespondence.setDescription(correspondence.getDescription());

		String direction = correspondence.getDirection();
		DirectionalityType schematicCorrespondenceDirection = getSchematicCorrespondenceDirection(direction);
		schematicCorrespondence.setDirection(schematicCorrespondenceDirection);

		List<Param> params = correspondence.getParam();
		if (params != null)
			processParameters(params, schematicCorrespondence);

		Constructs1 constructs1 = correspondence.getConstructs1();
		if (constructs1 != null) {
			List<Constr> constructs = constructs1.getConstr();
			processConstructs(constructs, schematicCorrespondence, true);
		} else
			logger.error("Constructs1 of constructs missing for schematic correspondence");

		Constructs1 constructs2 = correspondence.getConstructs1();
		if (constructs2 != null) {
			List<Constr> constructs = constructs2.getConstr();
			processConstructs(constructs, schematicCorrespondence, false);
		} else
			logger.error("Constructs2 of constructs missing for schematic correspondence");

		List<Correspondence> childCorrespondences = correspondence.getCorrespondence();
		if (childCorrespondences != null) {
			for (Correspondence childCorrespondence : childCorrespondences) {
				logger.debug("childCorrespondence: Name = " + childCorrespondence.getName() + ", ShortName = " + childCorrespondence.getShortName());
				SchematicCorrespondence childSchematicCorrespondence = buildSchematicCorrespondence(childCorrespondence, schematicCorrespondence);
				schematicCorrespondence.addChildSchematicCorrespondence(childSchematicCorrespondence);
			}
		}

		schematicCorrespondenceRepository.save(schematicCorrespondence);
		return schematicCorrespondence;
	}

	private void processConstructs(List<Constr> constructs, SchematicCorrespondence schematicCorrespondence, boolean isConstructs1) {
		logger.debug("in processConstructs");
		if (constructs != null) {
			for (Constr constr : constructs) {
				String constructName = constr.getName();
				String parentConstrName = constr.getParentConstrName();
				String schemaName = constr.getSchemaName();
				String kindOfConstruct = constr.getKind();
				logger.debug("schematic correspondence applied to constr, name: " + constructName + " schema: " + schemaName + " kind: "
						+ kindOfConstruct);

				CanonicalModelConstruct construct = this.getConstructOfKindByNameInSchemaWithName(constructName, kindOfConstruct, parentConstrName,
						schemaName);

				if (construct == null)
					logger.error("didn't find construct");
				else {
					if (isConstructs1) {
						logger.debug("is constructs1");
						schematicCorrespondence.addConstruct1(construct);
					} else {
						logger.debug("is constructs2");
						schematicCorrespondence.addConstruct2(construct);
					}
				}

				logger.debug("schematicCorrespondence: " + schematicCorrespondence);
			}
		} else
			logger.error("constucts missing for schematic correspondence");
	}

	private SchematicCorrespondenceType getSchematicCorrespondenceType(String type) {
		SchematicCorrespondenceType schematicCorrespondenceType = null;

		if (type.equalsIgnoreCase("SAME_NAME_SAME_CONSTRUCT"))
			schematicCorrespondenceType = SchematicCorrespondenceType.SAME_NAME_SAME_CONSTRUCT;
		if (type.equalsIgnoreCase("DIFFERENT_NAME_SAME_CONSTRUCT"))
			schematicCorrespondenceType = SchematicCorrespondenceType.DIFFERENT_NAME_SAME_CONSTRUCT;
		if (type.equalsIgnoreCase("DIFFERENT_NAME_DIFFERENT_CONSTRUCT"))
			schematicCorrespondenceType = SchematicCorrespondenceType.DIFFERENT_NAME_DIFFERENT_CONSTRUCT;
		if (type.equalsIgnoreCase("SAME_NAME_DIFFERENT_CONSTRUCT"))
			schematicCorrespondenceType = SchematicCorrespondenceType.SAME_NAME_DIFFERENT_CONSTRUCT;
		if (type.equalsIgnoreCase("LOGICALLY_INCLUDED_CONSTRUCT"))
			schematicCorrespondenceType = SchematicCorrespondenceType.LOGICALLY_INCLUDED_CONSTRUCT;
		if (type.equalsIgnoreCase("MISSING_SUPER_LEXICAL"))
			schematicCorrespondenceType = SchematicCorrespondenceType.MISSING_SUPER_LEXICAL;
		if (type.equalsIgnoreCase("DIFFERENT_TYPE"))
			schematicCorrespondenceType = SchematicCorrespondenceType.DIFFERENT_TYPE;
		if (type.equalsIgnoreCase("HORIZONTAL_PARTITIONING"))
			schematicCorrespondenceType = SchematicCorrespondenceType.HORIZONTAL_PARTITIONING;
		if (type.equalsIgnoreCase("VERTICAL_PARTITIONING"))
			schematicCorrespondenceType = SchematicCorrespondenceType.VERTICAL_PARTITIONING;
		if (type.equalsIgnoreCase("HORIZONTAL_VS_VERTICAL_PARTITIONING"))
			schematicCorrespondenceType = SchematicCorrespondenceType.HORIZONTAL_VS_VERTICAL_PARTITIONING;
		if (type.equalsIgnoreCase("COMPOSITE_PARTITIONING"))
			schematicCorrespondenceType = SchematicCorrespondenceType.COMPOSITE_PARTITIONING;
		if (type.equalsIgnoreCase("LEXICAL_PARTITIONING"))
			schematicCorrespondenceType = SchematicCorrespondenceType.LEXICAL_PARTITIONING;

		if (schematicCorrespondenceType == null)
			logger.error("unknown schematic correspondence type");

		return schematicCorrespondenceType;
	}

	private DirectionalityType getSchematicCorrespondenceDirection(String direction) {
		DirectionalityType schematicCorrespondenceDirection = null;

		if (direction.equalsIgnoreCase("BIDIRECTIONAL"))
			schematicCorrespondenceDirection = DirectionalityType.BIDIRECTIONAL;
		if (direction.equalsIgnoreCase("FIRST_TO_SECOND"))
			schematicCorrespondenceDirection = DirectionalityType.FIRST_TO_SECOND;
		if (direction.equalsIgnoreCase("SECOND_TO_FIRST"))
			schematicCorrespondenceDirection = DirectionalityType.SECOND_TO_FIRST;

		if (schematicCorrespondenceDirection == null)
			logger.error("unknown schematic correspondence direction");

		return schematicCorrespondenceDirection;
	}

	private void processParameters(List<Param> params, SchematicCorrespondence schematicCorrespondence) {
		for (Param param : params) {
			String direction = param.getDirection();
			DirectionalityType parameterDirection = getSchematicCorrespondenceDirection(direction);
			logger.debug("parameter: name: " + param.getName() + "value: " + param.getValue());
			Parameter parameter = new Parameter(param.getName(), param.getValue(), parameterDirection, schematicCorrespondence);

			List<Constr> constrs = param.getConstr();
			if (constrs != null) {
				for (Constr constr : constrs) {
					String constructName = constr.getName();
					String parentConstrName = constr.getParentConstrName();
					String schemaName = constr.getSchemaName();
					String kindOfConstruct = constr.getKind();
					logger.debug("parameter applied to constr, name: " + constructName + " schema: " + schemaName + " kind: " + kindOfConstruct);

					CanonicalModelConstruct construct = this.getConstructOfKindByNameInSchemaWithName(constructName, kindOfConstruct,
							parentConstrName, schemaName);
					logger.debug("construct: " + construct);
					parameter.getAppliedTo().add(construct);
					logger.debug("parameter: " + parameter);
					logger.debug("schematicCorrespondence: " + schematicCorrespondence);
				}
			}
		}
	}

	private CanonicalModelConstruct getConstructOfKindByNameInSchemaWithName(String constructName, String kindOfConstruct, String parentConstrName,
			String schemaName) {
		CanonicalModelConstruct construct = null;
		ConstructType typeOfConstruct = null;
		if (kindOfConstruct.equals("SuperAbstract")) {
			typeOfConstruct = ConstructType.SUPER_ABSTRACT;
			construct = superAbstractRepository.getSuperAbstractByNameInSchemaWithName(constructName, schemaName);
		} else if (kindOfConstruct.equals("SuperLexical")) {
			typeOfConstruct = ConstructType.SUPER_LEXICAL;
			construct = superLexicalRepository.getSuperLexicalWithNameOfSuperAbstractWithNameInSchemaWithName(constructName, parentConstrName,
					schemaName);
		} else if (kindOfConstruct.equals("SuperRelationship")) {
			typeOfConstruct = ConstructType.SUPER_RELATIONSHIP;
			construct = superRelationshipRepository.getSuperRelationshipByNameInSchemaWithName(constructName, schemaName);
		} else
			logger.error("unexpected kind of construct: " + kindOfConstruct);

		logger.debug("typeOfConstruct: " + typeOfConstruct);
		logger.debug("construct: " + construct);
		return construct;
	}

}