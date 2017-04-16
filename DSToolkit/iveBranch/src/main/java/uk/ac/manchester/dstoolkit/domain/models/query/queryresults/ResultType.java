package uk.ac.manchester.dstoolkit.domain.models.query.queryresults;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.domain.DomainEntity;
import uk.ac.manchester.dstoolkit.domain.models.canonical.DataType;

/**
 * @author chedeler
 *
 */

@Entity
@Table(name = "RESULT_TYPES")
public class ResultType extends DomainEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7592000539991834127L;

	private static Logger logger = Logger.getLogger(ResultType.class);

	//@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	//@JoinColumn(name = "RESULT_TYPE_ID")
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	//private List<ResultField> resultFields = new ArrayList<ResultField>();

	@MapKey(name = "fieldName")
	//@OneToMany(mappedBy = "resultType", fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "RESULT_TYPE_RESULT_FIELDS", joinColumns = @JoinColumn(name = "RESULT_TYPE_ID"), inverseJoinColumns = @JoinColumn(name = "RESULT_FIELD_ID"))
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	private final Map<String, ResultField> resultFields = new LinkedHashMap<String, ResultField>();

	//TODO not sure whether that is always going to return the same order, seeing that it's stored persistently - check this - doesn't look like it
	//TODO: remove one of the two methods: getResultFieldAtPosition or getResultFieldWithIndex

	/**
	 * 
	 */
	public ResultType() {
		super();
	}

	/**
	 * @param resultType
	 */
	public ResultType(ResultType resultType) {
		Map<String, ResultField> resultFields = resultType.getResultFields();
		Set<String> resultFieldNames = resultFields.keySet();
		for (String resultFieldName : resultFieldNames) {
			ResultField newResultField = new ResultField(resultFieldName, resultFields.get(resultFieldName).getFieldType());
			newResultField.setCanonicalModelConstruct(resultFields.get(resultFieldName).getCanonicalModelConstruct());
			int index = resultFields.size();
			if (resultFields.get(resultFieldName).getIndex() != index) {
				logger.error("index of resultField in new resultType different from old resultField - use new index for now");
				newResultField.setIndex(index);
			} else
				newResultField.setIndex(resultFields.get(resultFieldName).getIndex());
			this.resultFields.put(resultFieldName, newResultField);
		}
	}

	/**
	 * @param lhs resultType
	 * @param rhs resultType
	 */
	public ResultType(ResultType lhs, ResultType rhs) {
		//resultFields.putAll(lhs.getResultFields());
		//resultFields.putAll(rhs.getResultFields());
		Map<String, ResultField> lhsResultFields = lhs.getResultFields();
		Set<String> lhsResultFieldNames = lhsResultFields.keySet();
		for (String resultFieldName : lhsResultFieldNames) {
			ResultField newResultField = new ResultField(resultFieldName, lhsResultFields.get(resultFieldName).getFieldType());
			newResultField.setCanonicalModelConstruct(lhsResultFields.get(resultFieldName).getCanonicalModelConstruct());
			int index = this.resultFields.size();
			if (lhsResultFields.get(resultFieldName).getIndex() != index) {
				logger.error("index of resultField in new resultType different from old resultField - use new index for now");
				newResultField.setIndex(index);
			} else
				newResultField.setIndex(lhsResultFields.get(resultFieldName).getIndex());
			this.resultFields.put(resultFieldName, newResultField);
		}
		Map<String, ResultField> rhsResultFields = rhs.getResultFields();
		Set<String> rhsResultFieldNames = rhsResultFields.keySet();
		for (String resultFieldName : rhsResultFieldNames) {
			ResultField newResultField = new ResultField(resultFieldName, rhsResultFields.get(resultFieldName).getFieldType());
			newResultField.setCanonicalModelConstruct(rhsResultFields.get(resultFieldName).getCanonicalModelConstruct());
			int index = this.resultFields.size();
			newResultField.setIndex(index);
			this.resultFields.put(resultFieldName, newResultField);
		}
	}

	/**
	 * @param resultFields
	 */
	public ResultType(Map<String, ResultField> resultFields) {
		//this.setResultFields(resultFields);
		Set<String> resultFieldNames = resultFields.keySet();
		for (String resultFieldName : resultFieldNames) {
			ResultField newResultField = new ResultField(resultFieldName, resultFields.get(resultFieldName).getFieldType());
			newResultField.setCanonicalModelConstruct(resultFields.get(resultFieldName).getCanonicalModelConstruct());
			int index = resultFields.size();
			newResultField.setIndex(index);
			this.resultFields.put(resultFieldName, newResultField);
		}
	}

	public ResultField getResultFieldWithName(String name) {
		return resultFields.get(name);
	}

	//-----------------------returnPosition-----------------

	/**
	 * @param name
	 * @return position of resultField with name in list of resultFields
	 */
	public int getPosition(String name) {
		int i = 0;
		Set<String> fieldNames = resultFields.keySet();
		for (String resultFieldName : fieldNames) {
			if (resultFieldName.equals(name))
				return i;
			i++;
		}
		i = 0;
		for (String resultFieldName : fieldNames) {
			if (resultFieldName.endsWith(name))
				return i;
			i++;
		}
		i = 0;
		for (String resultFieldName : fieldNames) {
			if (resultFieldName.contains(name))
				return i;
			i++;
		}
		return -1;
		/*
		for (ResultField resultField : resultFields) {
			String fieldName = resultField.getFieldName();
			if (fieldName.equals(name))
				return (resultFields.indexOf(resultField));
		}
		return -1;
		*/
	}

	//-----------------------getResultFieldAtPosition-----------------

	public ResultField getResultFieldAtPosition(int i) {
		int j = 0;
		Set<String> fieldNames = resultFields.keySet();
		for (String fieldName : fieldNames) {
			if (i == j)
				return resultFields.get(fieldName);
			j++;
		}
		return null;
	}

	//-----------------------merge-----------------

	/**
	 * @param resultType
	 */
	public void merge(ResultType resultType) {
		//TODO not sure this is the right merge, it's used to get the resultType of joins, so won't really work I think, check this
		resultFields.putAll(resultType.getResultFields());
	}

	//-----------------------isSetOpCompatible-----------------

	public boolean isSetOpCompatible(ResultType otherResultType) {
		//TODO change this for a less strict version
		Map<String, ResultField> otherResultFields = otherResultType.getResultFields();
		Set<String> otherResultFieldsNames = otherResultFields.keySet();
		int i = 0;
		for (String otherResultFieldName : otherResultFieldsNames) {
			DataType otherDataType = otherResultFields.get(otherResultFieldName).getFieldType();
			if (!otherDataType.equals(this.getResultFieldAtPosition(i).getFieldType()))
				return false;
			i++;
		}
		/*
		for (int i = 0; i < otherResultFields.size(); i++) {
			DataType otherDataType = otherResultFields.get(i).getFieldType();
			if (!otherDataType.equals(resultFields.get(i).getFieldType())) {
				return false;
			}
		}
		*/
		return true;
	}

	//-----------------------resultFields-----------------

	/**
	 * @return the resultFields
	 */
	public Map<String, ResultField> getResultFields() {
		return Collections.unmodifiableMap(resultFields);
	}

	public ResultField getResultFieldWithIndex(int index) {
		//TODO could also be implemented as query in ResultTypeRepository, might be better there, but leave here for now
		Collection<ResultField> fields = resultFields.values();
		for (ResultField resultField : fields) {
			if (resultField.getIndex() == index)
				return resultField;
		}
		return null;
	}

	/**
	 * @param resultFields the resultFields to set
	 */
	public void setResultFields(Map<String, ResultField> resultFields) {
		for (String resultFieldName : resultFields.keySet()) {
			addResultField(resultFieldName, resultFields.get(resultFieldName));
		}
		//this.resultFields = resultFields;
	}

	public void addResultField(String resultFieldName, ResultField resultField) {
		int currentIndex = resultFields.size();
		if (resultField.getIndex() == -1)
			resultField.setIndex(currentIndex);
		this.resultFields.put(resultFieldName, resultField);
	}

	public void removeResultField(String resultFieldName) {
		//TODO need to adjust the index of the remaining resultFields here
		this.resultFields.remove(resultFieldName);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ResultType [");
		if (id != null)
			builder.append("id=").append(id).append(", ");
		if (resultFields != null)
			builder.append("resultFields=").append(resultFields).append(", ");
		builder.append("version=").append(version).append("]");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((resultFields == null) ? 0 : resultFields.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResultType other = (ResultType) obj;
		if (resultFields == null) {
			if (other.resultFields != null)
				return false;
		} else if (!resultFields.equals(other.resultFields))
			return false;
		return true;
	}

}
