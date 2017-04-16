package uk.ac.manchester.dstoolkit.domain.user;

/**
 * @author chedeler
 *
 */
public enum RoleType {
	USER("USER"), ADMIN("ADMIN");

	private final String shortName;

	RoleType(String shortName) {
		this.shortName = shortName;
	}

	@SuppressWarnings("unused")
	private String getShortName() {
		return shortName;
	}
}
