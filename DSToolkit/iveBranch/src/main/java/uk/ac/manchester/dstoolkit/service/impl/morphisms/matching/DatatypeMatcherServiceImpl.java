package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.ConstructType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.DataType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.DataTypeMapper;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;

@Scope("prototype")
@Service
public class DatatypeMatcherServiceImpl extends ConstructBasedMatcherServiceImpl {

	private static Logger logger = Logger.getLogger(DatatypeMatcherServiceImpl.class);

	public DatatypeMatcherServiceImpl() {
		logger.debug("in DatatypeMatcherServiceImpl");
		this.setName("datatypeMatcher");
		this.setMatcherType(MatcherType.DATATYPE);
	}

	@Override
	public float match(CanonicalModelConstruct construct1, CanonicalModelConstruct construct2) {
		logger.debug("in match");
		logger.debug("construct1: " + construct1);
		logger.debug("construct2: " + construct2);
		logger.debug("construct1.getName: " + construct1.getName());
		logger.debug("construct2.getName: " + construct2.getName());
		if (this.getChildMatchers() != null && this.getChildMatchers().size() > 0)
			logger.error("dataTypeMatcher has childMatchers - shouldn't be the case - TODO sort this");

		DataType dataType1 = null;
		DataType dataType2 = null;

		if (this.getChildMatchers() != null && this.getChildMatchers().size() > 0)
			logger.error("dataTypeMatcher has childMatchers ... shouldn't be the case");

		if (construct1.getTypeOfConstruct().equals(ConstructType.SUPER_LEXICAL)) {
			logger.debug("construct1 is superLexical");
			dataType1 = ((SuperLexical) construct1).getDataType();
			logger.debug("dataType1: " + dataType1);
		} else
			logger.debug("construct1 isn't superLexical");

		if (construct2.getTypeOfConstruct().equals(ConstructType.SUPER_LEXICAL)) {
			logger.debug("construct2 is superLexical");
			dataType2 = ((SuperLexical) construct2).getDataType();
			logger.debug("dataType2: " + dataType2);
		} else
			logger.debug("construct2 isn't superLexical");

		float sim = computeDataTypeSimilarity(dataType1, dataType2);
		logger.debug("sim: " + sim);
		return sim;
	}

	@Override
	public float[][] match(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2) {
		logger.debug("in match");
		float[][] simMatrix = new float[constructs1.size()][constructs2.size()];
		for (CanonicalModelConstruct construct1 : constructs1) {
			for (CanonicalModelConstruct construct2 : constructs2) {
				logger.debug("construct1: " + construct1);
				logger.debug("construct2: " + construct2);
				logger.debug("construct1.getName: " + construct1.getName());
				logger.debug("construct2.getName: " + construct2.getName());
				simMatrix[constructs1.indexOf(construct1)][constructs2.indexOf(construct2)] = this.match(construct1, construct2);
			}
		}
		return simMatrix;
	}

	/***
	 * Pre-defined tables used for doing datatype similarities
	 * 
	 * @param dataType1
	 * @param dataType2
	 * @return similarity between datatypes in Float
	 */
	public float computeDataTypeSimilarity(DataType dataType1, DataType dataType2) {
		logger.debug("computeDataTypeSimilarity");
		logger.debug("dataType1: " + dataType1);
		logger.debug("dataType2: " + dataType2);
		if ((dataType1 == null) || (dataType2 == null))
			return 0.0F;
		if ((dataType1.equals(dataType2)))
			return 1.0F;
		if (DataTypeMapper.isIntegerType(dataType1) && DataTypeMapper.isIntegerType(dataType2))
			return 0.9F;
		if (DataTypeMapper.isRealType(dataType1) && DataTypeMapper.isRealType(dataType2))
			return 0.9F;
		if (DataTypeMapper.isStringType(dataType1) && DataTypeMapper.isStringType(dataType2))
			return 0.9F;
		if (DataTypeMapper.isTimeDateType(dataType1) && DataTypeMapper.isTimeDateType(dataType2))
			return 0.9F;
		if ((DataTypeMapper.isIntegerType(dataType1) && DataTypeMapper.isRealType(dataType2))
				|| (DataTypeMapper.isRealType(dataType1) && DataTypeMapper.isIntegerType(dataType2)))
			return 0.6F;
		if ((DataTypeMapper.isStringType(dataType1) && dataType2.equals(DataType.ENUM))
				|| (dataType1.equals(DataType.ENUM) && DataTypeMapper.isStringType(dataType2)))
			return 0.6F;
		if ((DataTypeMapper.isIntegerType(dataType1) && dataType2.equals(DataType.BOOLEAN))
				|| (dataType1.equals(DataType.BOOLEAN) && DataTypeMapper.isIntegerType(dataType2)))
			return 0.5F;
		if ((DataTypeMapper.isStringType(dataType1) && dataType2.equals(DataType.BOOLEAN))
				|| (dataType1.equals(DataType.BOOLEAN) && DataTypeMapper.isStringType(dataType2)))
			return 0.4F;
		if ((DataTypeMapper.isStringType(dataType1) && DataTypeMapper.isNumberType(dataType2))
				|| (DataTypeMapper.isNumberType(dataType1) && DataTypeMapper.isStringType(dataType2)))
			return 0.4F;
		if ((DataTypeMapper.isStringType(dataType1) && DataTypeMapper.isTimeDateType(dataType2))
				|| (DataTypeMapper.isTimeDateType(dataType1) && DataTypeMapper.isStringType(dataType2)))
			return 0.4F;

		return 0.0F;
	}

}
