package uk.ac.manchester.dstoolkit.domain.models.canonical;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "TEMP_CONSTRUCT")
public class TempConstruct extends CanonicalModelConstruct {

	private static final long serialVersionUID = 8048685385689324997L;

	public TempConstruct() {
		super();
		this.setTypeOfConstruct(ConstructType.TEMP_CONSTRUCT);
	}
}
