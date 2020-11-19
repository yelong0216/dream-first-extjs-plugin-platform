/**
 * 
 */
package dream.first.extjs.plugin.platform.user.handle.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.yelong.core.jdbc.dialect.Dialect;
import org.yelong.core.jdbc.dialect.DialectType;
import org.yelong.core.jdbc.sql.condition.ConditionConnectWay;
import org.yelong.core.jdbc.sql.condition.support.Condition;
import org.yelong.core.model.service.SqlModelService;

import com.github.pagehelper.PageInfo;

import dream.first.core.platform.user.model.User;
import dream.first.extjs.plugin.platform.user.dto.UserDTO;
import dream.first.extjs.plugin.platform.user.handle.UserQueryHandler;

/**
 * @author PengFei
 *
 */
public class DefaultUserQueryHandler implements UserQueryHandler {

	@Resource
	private SqlModelService modelService;

	protected String getFindUserVOSql() {
		Dialect dialect = modelService.getModelConfiguration().getDialect();
		DialectType dialectType = dialect.getDialectType();
		if (dialectType == DialectType.MYSQL) {
			/*
			 * select DISTINCT usr.* , org.orgName usrOrgName, org.orgNo usrOrgNo, (select
			 * group_concat(userrole.roleId) from CO_USER_ROLE userrole where
			 * userrole.userId = usr.id) usrRoles from CO_USER usr join co_org org on org.id
			 * = usr.orgId left join co_user_role userRole on userRole.userId = usr.id left
			 * join co_role role on role.id = userRole.roleId
			 */
			return "select DISTINCT usr.* ,\r\n" + "org.orgName usrOrgName, org.orgNo usrOrgNo,\r\n"
					+ "(select group_concat(userrole.roleId) from CO_USER_ROLE userrole where userrole.userId = usr.id) usrRoles  \r\n"
					+ "from CO_USER usr\r\n" + "join co_org org on org.id = usr.orgId\r\n"
					+ "left join co_user_role userRole on userRole.userId = usr.id\r\n"
					+ "left join co_role role on role.id = userRole.roleId";
		} else if (dialectType == DialectType.ORACLE) {
			return "select usr.*, org.orgName usrOrgName, org.orgNo usrOrgNo, " + "(select "
			// + "to_char(wm_concat(userrole.roleId)) "
					+ "LISTAGG(userrole.roleId,',') WITHIN GROUP (ORDER BY userrole.roleId) "
					+ "from CO_USER_ROLE userrole where userrole.userId = usr.id) usrRoles  " + "from CO_USER usr  "
					+ "inner join CO_ORG org on usr.orgId = org.id and org.state = '0' ";
		} else {
			throw new UnsupportedOperationException("不支持的数据库方言：" + dialectType);
		}
	}

	@Override
	public List<? extends User> queryUser(HttpServletRequest request, User sqlModel) {
		return modelService.findBySqlModel(UserDTO.class, getFindUserVOSql(), null, sqlModel);
	}

	@Override
	public PageInfo<? extends User> queryUser(HttpServletRequest request, User sqlModel, Integer pageNum,
			Integer pageSize) {
		// 添加条件
		String roleNames = request.getParameter("roleNames");
		if (StringUtils.isNotBlank(roleNames)) {
			Condition condition = new Condition("role.roleName", "IN", roleNames.split(","))
					.setGroupName("group_condition");
			sqlModel.addCondition(condition);
		}
		String roleIds = request.getParameter("roleIds");
		if (StringUtils.isNotBlank(roleIds)) {
			Condition condition = new Condition("role.id", "IN", roleIds.split(",")).setGroupName("group_condition")
					.setConnectWay(ConditionConnectWay.OR);
			sqlModel.addCondition(condition);
		}
		String usernames = request.getParameter("usernames");
		if (StringUtils.isNotBlank(usernames)) {
			Condition condition = new Condition("usr.username", "IN", usernames.split(","))
					.setGroupName("group_condition").setConnectWay(ConditionConnectWay.OR);
			sqlModel.addCondition(condition);
		}
		String orgIds = request.getParameter("orgIds");
		if (StringUtils.isNotBlank(orgIds)) {
			Condition condition = new Condition("usr.orgId", "IN", orgIds.split(",")).setGroupName("group_condition")
					.setConnectWay(ConditionConnectWay.OR);
			sqlModel.addCondition(condition);
		}
		return new PageInfo<>(
				modelService.findPageBySqlModel(UserDTO.class, getFindUserVOSql(), null, sqlModel, pageNum, pageSize));
	}

	@Override
	public User getUser(HttpServletRequest request, User sqlModel) {
		return modelService.findFirstBySqlModel(UserDTO.class, getFindUserVOSql(), null, sqlModel);
	}

}
