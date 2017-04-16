package uk.ac.manchester.dstoolkit.domain.user;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import uk.ac.manchester.dstoolkit.domain.Dataspace;
import uk.ac.manchester.dstoolkit.domain.DomainEntity;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;

/**
 * @author chedeler
 *
 */

@Entity
@Table(name = "USERS")
public class User extends DomainEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5147517501439595626L;

	private static final RoleType DEFAULT_ROLE_TYPE = RoleType.USER;
	
	@Column(name = "USER_NAME", nullable = false, unique = true)
	private String userName;

	@Column(name = "PASSWORD", nullable = false)
	private String password;

	@Column(name = "EMAIL", nullable = false, unique = true)
	private String email;

	@Column(name = "FIRST_NAME")
	private String firstName;

	@Column(name = "LAST_NAME")
	private String lastName;

	@Column(name = "INSTITUTION_NAME")
	private String institutionName;

	@Column(name = "ACCEPT_TERMS")
	private boolean acceptTerms;

	@Column(name = "DATE_CREATED")
	private Date dateCreated;

	@ManyToMany(mappedBy = "users")
	private Set<Dataspace> dataspaces = new LinkedHashSet<Dataspace>();

	@OneToMany(mappedBy = "user")
	private Set<Query> queries = new LinkedHashSet<Query>();

	@ManyToMany(mappedBy = "users", fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	private Set<Role> roles = new LinkedHashSet<Role>();

	/**
	 * 
	 */
	public User() {
		super();
		Role role = new Role(DEFAULT_ROLE_TYPE);
		this.addRole(role);
	}

	/**
	 * @param roleType
	 */
	public User(RoleType roleType) {
		super();
		Role role = new Role(roleType);
		this.addRole(role);
	}

	//-----------------------userName-----------------

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	//-----------------------password-----------------

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	//-----------------------email-----------------

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	//-----------------------firstName-----------------

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	//-----------------------lastName-----------------

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	//-----------------------institutionName-----------------

	/**
	 * @return the institutionName
	 */
	public String getInstitutionName() {
		return institutionName;
	}

	/**
	 * @param institutionName the institutionName to set
	 */
	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

	//-----------------------isAcceptTerms-----------------

	/**
	 * @return the acceptTerms
	 */
	public boolean isAcceptTerms() {
		return acceptTerms;
	}

	/**
	 * @param acceptTerms the acceptTerms to set
	 */
	public void setAcceptTerms(boolean acceptTerms) {
		this.acceptTerms = acceptTerms;
	}

	//-----------------------dateCreated-----------------

	/**
	 * @return the dateCreated
	 */
	public Date getDateCreated() {
		return dateCreated;
	}

	/**
	 * @param dateCreated the dateCreated to set
	 */
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	//-----------------------dataspaces-----------------

	/**
	 * @return the dataspaces
	 */
	public Set<Dataspace> getDataspaces() {
		//return Collections.unmodifiableSet(dataspaces);
		return dataspaces;
	}

	/**
	 * @param dataspaces the dataspaces to set
	 */
	public void setDataspaces(Set<Dataspace> dataspaces) {
		this.dataspaces = dataspaces;
		for (Dataspace dataspace : dataspaces) {
			dataspace.internalAddUser(this);
		}
	}

	public void addDataspace(Dataspace dataspace) {
		this.dataspaces.add(dataspace);
		dataspace.internalAddUser(this);
	}

	public void internalAddDataspace(Dataspace dataspace) {
		this.dataspaces.add(dataspace);
	}

	public void removeDataspace(Dataspace dataspace) {
		this.dataspaces.remove(dataspace);
		dataspace.internalRemoveUser(this);
	}

	public void internalRemoveDataspace(Dataspace dataspace) {
		this.dataspaces.remove(dataspace);
	}

	//-----------------------queries-----------------

	/**
	 * @return the queries
	 */
	public Set<Query> getQueries() {
		//return Collections.unmodifiableSet(queries);
		return queries;
	}

	/**
	 * @param queries the queries to set
	 */
	public void setQueries(Set<Query> queries) {
		this.queries = queries;
		for (Query query : queries) {
			query.internalSetUser(this);
		}
	}

	public void addQuery(Query query) {
		queries.add(query);
		query.internalSetUser(this);
	}

	public void internalAddQuery(Query query) {
		queries.add(query);
	}

	public void removeQuery(Query query) {
		queries.remove(query);
		query.internalSetUser(null);
	}

	public void internalRemoveQuery(Query query) {
		queries.remove(query);
	}

	//-----------------------roles-----------------

	/**
	 * @return the roles
	 */
	public Set<Role> getRoles() {
		//return Collections.unmodifiableSet(roles);
		return roles;
	}

	/**
	 * @param roles the roles to set
	 */
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
		for (Role role : roles) {
			role.internalAddUser(this);
		}
	}

	/**
	 * @param role
	 */
	public void addRole(Role role) {
		roles.add(role);
		role.internalAddUser(this);
	}

	public void internalAddRole(Role role) {
		roles.add(role);
	}

	public void removeRole(Role role) {
		roles.remove(role);
		role.internalRemoveUser(this);
	}

	public void internalRemoveRole(Role role) {
		roles.remove(role);
	}

	/**
	 * @param roleType
	 */
	public void removeRole(RoleType roleType) {
		Role roleToFind = new Role(roleType);
		if (roles.size() > 1 && roles.contains(roleToFind))
			roles.remove(roleToFind);
		roleToFind.internalRemoveUser(this);
		//TODO add proper error handling here when trying to remove last role
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateCreated == null) ? 0 : dateCreated.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((institutionName == null) ? 0 : institutionName.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
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
		User other = (User) obj;
		if (dateCreated == null) {
			if (other.dateCreated != null)
				return false;
		} else if (!dateCreated.equals(other.dateCreated))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (institutionName == null) {
			if (other.institutionName != null)
				return false;
		} else if (!institutionName.equals(other.institutionName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (roles == null) {
			if (other.roles != null)
				return false;
		} else if (!roles.equals(other.roles))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [acceptTerms=").append(acceptTerms).append(", ");
		if (dateCreated != null)
			builder.append("dateCreated=").append(dateCreated).append(", ");
		if (email != null)
			builder.append("email=").append(email).append(", ");
		if (firstName != null)
			builder.append("firstName=").append(firstName).append(", ");
		if (institutionName != null)
			builder.append("institutionName=").append(institutionName).append(", ");
		if (lastName != null)
			builder.append("lastName=").append(lastName).append(", ");
		if (password != null)
			builder.append("password=").append(password).append(", ");
		if (userName != null)
			builder.append("userName=").append(userName);
		builder.append("]");
		return builder.toString();
	}

}
