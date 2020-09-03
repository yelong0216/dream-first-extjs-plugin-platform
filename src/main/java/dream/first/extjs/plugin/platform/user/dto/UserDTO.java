/**
 * 
 */
package dream.first.extjs.plugin.platform.user.dto;

import org.yelong.core.model.annotation.Column;
import org.yelong.core.model.annotation.ExtendColumn;
import org.yelong.core.model.annotation.Select;

import dream.first.core.platform.user.model.User;

/*
 * select usr.*, org.orgName usrOrgName, org.orgNo usrOrgNo, 
	(select to_char(wm_concat(userrole.roleId)) from CO_USER_ROLE userrole where userrole.userId = usr.id) usrRoles 
	from CO_USER usr 
	inner join CO_ORG org on usr.orgId = org.id and org.state = '0'
 */
/*
 * @Select("select usr.*, org.orgName usrOrgName, org.orgNo usrOrgNo,  " +
 * "(select to_char(wm_concat(userrole.roleId)) from CO_USER_ROLE userrole where userrole.userId = usr.id) usrRoles  "
 * + "from CO_USER usr  " +
 * "inner join CO_ORG org on usr.orgId = org.id and org.state = '0'")
 */
@Select("select usr.*, org.orgName usrOrgName, org.orgNo usrOrgNo, employee.fullName employeeFullName ,"
		+ "(select group_concat(userrole.roleId) from CO_USER_ROLE userrole where userrole.userId = usr.id) usrRoles  "
		+ "from CO_USER usr  " + "inner join CO_ORG org on usr.orgId = org.id and org.state = '0' "
		+ "left join tb_qm_employee_info employee on employee.id = usr.employeeId")
public class UserDTO extends User {

	private static final long serialVersionUID = -6289492337819988492L;

	private String usrOrgName;

	private String usrOrgNo;

	private String usrRoles;

	@Column(columnName = "人员全名")
	@ExtendColumn()
	private String employeeFullName;

	public String getUsrOrgName() {
		return usrOrgName;
	}

	public void setUsrOrgName(String usrOrgName) {
		this.usrOrgName = usrOrgName;
	}

	public String getUsrOrgNo() {
		return usrOrgNo;
	}

	public void setUsrOrgNo(String usrOrgNo) {
		this.usrOrgNo = usrOrgNo;
	}

	public String getUsrRoles() {
		return usrRoles;
	}

	public void setUsrRoles(String usrRoles) {
		this.usrRoles = usrRoles;
	}

	public String getEmployeeFullName() {
		return employeeFullName;
	}

	public void setEmployeeFullName(String employeeFullName) {
		this.employeeFullName = employeeFullName;
	}

}
