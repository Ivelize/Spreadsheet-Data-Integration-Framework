package uk.ac.manchester.dstoolkit.domain.user;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import uk.ac.manchester.dstoolkit.domain.DomainEntity;

/**
 * @author chedeler
 *
 */
@Entity
@Table(name = "ROLES")
public class Role extends DomainEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3724107083356843340L;

	@Enumerated(EnumType.STRING)
	@Column(name = "ROLE_ROLE_TYPE")
	private RoleType roleType;

	@ManyToMany
	@JoinTable(name = "ROLE_USERS", joinColumns = { @JoinColumn(name = "ROLE_ID") }, inverseJoinColumns = { @JoinColumn(name = "USER_ID") })
	private Set<User> users = new LinkedHashSet<User>();

	/**
	 * 
	 */
	public Role() {
	}

	/**
	 * @param roleType
	 */
	public Role(RoleType roleType) {
		this.setRoleType(roleType);
	}

	//-----------------------roleType-----------------

	/**
	 * @param roleType the roleType to set
	 */
	public void setRoleType(RoleType roleType) {
		this.roleType = roleType;
	}

	/**
	 * @return the roleType
	 */
	public RoleType getRoleType() {
		return roleType;
	}

	//-----------------------users-----------------

	/**
	 * @return the users
	 */
	public Set<User> getUsers() {
		//return Collections.unmodifiableSet(users);
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(Set<User> users) {
		this.users = users;
		for (User user : users) {
			user.internalAddRole(this);
		}
	}

	public void addUser(User user) {
		this.users.add(user);
		user.internalAddRole(this);
	}

	public void internalAddUser(User user) {
		this.users.add(user);
	}

	public void removeUser(User user) {
		this.users.remove(user);
		user.internalRemoveRole(this);
	}

	public void internalRemoveUser(User user) {
		this.users.remove(user);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Role [");
		if (roleType != null)
			builder.append("roleType=").append(roleType).append(", ");
		if (users != null)
			builder.append("users=").append(users);
		builder.append("]");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((roleType == null) ? 0 : roleType.hashCode());
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
		Role other = (Role) obj;
		if (roleType == null) {
			if (other.roleType != null)
				return false;
		} else if (!roleType.equals(other.roleType))
			return false;
		return true;
	}
}
