/**
 * 
 */
package dream.first.extjs.plugin.platform.user.dto;

import org.yelong.core.model.annotation.Select;
import org.yelong.core.model.annotation.Table;

import dream.first.core.platform.user.model.User;

@Table(value = "", alias = "usr")
/*
 * select usr.*, org.orgName usrOrgName from CO_USER usr inner join CO_ORG org
 * on usr.orgId = org.id inner join CO_USER_ROLE userrole on userrole.userId =
 * usr.id
 */
@Select("select usr.*, org.orgName usrOrgName " + "from CO_USER usr inner join CO_ORG org on usr.orgId = org.id "
		+ "inner join CO_USER_ROLE userrole on userrole.userId = usr.id")
public class UserByRoleDTO extends User {

	private static final long serialVersionUID = 1L;

	private String usrOrgName;

	public String getUsrOrgName() {
		return usrOrgName;
	}

	public void setUsrOrgName(String usrOrgName) {
		this.usrOrgName = usrOrgName;
	}

}
