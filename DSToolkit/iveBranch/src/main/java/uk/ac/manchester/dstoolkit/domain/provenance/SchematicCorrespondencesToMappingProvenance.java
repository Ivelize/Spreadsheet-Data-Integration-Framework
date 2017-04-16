package uk.ac.manchester.dstoolkit.domain.provenance;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.SchematicCorrespondence;

/**
 * @author chedeler
 *
 */

@Entity
@Table(name = "SCHEMATIC_CORRESPONDENCES_TO_MAPPING_PROVENANCE")
public class SchematicCorrespondencesToMappingProvenance extends Provenance {

	private static final long serialVersionUID = 1L;

	@OneToOne
	@JoinColumn(name = "MAPPING_ID")
	private Mapping mapping;

	@ManyToMany
	@JoinTable(name = "SCHEMATIC_CORRESPONDENCES_UTILISED_FOR_MAPPINGS_PROVENANCE", joinColumns = { @JoinColumn(name = "PROVENANCE_ID") }, inverseJoinColumns = { @JoinColumn(name = "SCHEMATIC_CORRESPONDENCE_ID") })
	private Set<SchematicCorrespondence> schematicCorrespondences = new HashSet<SchematicCorrespondence>();

	public SchematicCorrespondencesToMappingProvenance() {
	}

	public SchematicCorrespondencesToMappingProvenance(Mapping mapping, Set<SchematicCorrespondence> schematicCorrespondences) {
		this.mapping = mapping;
		this.schematicCorrespondences = schematicCorrespondences;
	}

	public Mapping getMapping() {
		return mapping;
	}

	public void setMapping(Mapping mapping) {
		this.mapping = mapping;
	}

	public Set<SchematicCorrespondence> getSchematicCorrespondences() {
		return schematicCorrespondences;
	}

	public void setSchematicCorrespondences(Set<SchematicCorrespondence> schematicCorrespondences) {
		this.schematicCorrespondences = schematicCorrespondences;
	}

}
