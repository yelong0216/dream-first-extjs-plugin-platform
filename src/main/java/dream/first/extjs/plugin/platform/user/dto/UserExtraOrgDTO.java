/**
 * 
 */
package dream.first.extjs.plugin.platform.user.dto;

import org.yelong.core.model.annotation.Select;
import org.yelong.core.model.annotation.Table;

import dream.first.core.platform.user.model.UserExtraOrg;

/*
select usrextraorg.*, org.orgName usrextraorgOrgName, 
(select to_char(wm_concat(role.id)) from co_role role inner join co_user_extraorg_role er on role.id = er.roleId where er.orgId = usrextraorg.orgid) usrextraorgRoleIds, 
(select wm_concat(role.roleName) from co_role role inner join co_user_extraorg_role er on role.id = er.roleId where er.orgId = usrextraorg.orgid) usrextraorgRoleNames 
from co_user_extraorg usrextraorg 
inner join co_org org on org.id = usrextraorg.orgid 
 */
@Table(value = "CO_USER_EXTRAORG", alias = "usrextraorg")
@Select("select usrextraorg.*, org.orgName usrextraorgOrgName,  "
		+ "(select to_char(wm_concat(role.id)) from co_role role inner join co_user_extraorg_role er on role.id = er.roleId where er.orgId = usrextraorg.orgid) usrextraorgRoleIds,  "
		+ "(select wm_concat(role.roleName) from co_role role inner join co_user_extraorg_role er on role.id = er.roleId where er.orgId = usrextraorg.orgid) usrextraorgRoleNames  "
		+ "from co_user_extraorg usrextraorg  " + "inner join co_org org on org.id = usrextraorg.orgid ")
public class UserExtraOrgDTO extends UserExtraOrg {

	private static final long serialVersionUID = 1L;

	private String usrextraorgOrgName;

	private String usrextraorgRoleIds;

	private String usrextraorgRoleNames;

	public String getUsrextraorgOrgName() {
		return usrextraorgOrgName;
	}

	public void setUsrextraorgOrgName(String usrextraorgOrgName) {
		this.usrextraorgOrgName = usrextraorgOrgName;
	}

	public String getUsrextraorgRoleIds() {
		return usrextraorgRoleIds;
	}

	public void setUsrextraorgRoleIds(String usrextraorgRoleIds) {
		this.usrextraorgRoleIds = usrextraorgRoleIds;
	}

	public String getUsrextraorgRoleNames() {
		return usrextraorgRoleNames;
	}

	public void setUsrextraorgRoleNames(String usrextraorgRoleNames) {
		this.usrextraorgRoleNames = usrextraorgRoleNames;
	}

}
