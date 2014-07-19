package de.fu.mi.scuttle.lib;

import java.util.Set;

public interface ScuttleUser {

	ScuttleUser addPrivilege(String privilege);

	ScuttleUser setPrivileges(Set<String> privileges);

	Set<String> getPrivileges();

	ScuttleUser setRole(String role);

	String getRole();

	void setPassword(ScuttlePassword password);

	ScuttlePassword getPassword();

	ScuttleUser setUserLoginId(String userLoginId);

	String getUserLoginId();

}
