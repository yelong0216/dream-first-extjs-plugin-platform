/**
 * 
 */
package dream.first.extjs.plugin.platform.user.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yelong.commons.lang.Strings;
import org.yelong.core.jdbc.dialect.Dialect;
import org.yelong.core.jdbc.dialect.DialectType;
import org.yelong.core.model.sql.SqlModel;

import com.github.pagehelper.PageInfo;
import com.labbol.cocoon.core.utils.security.coder.JSDesCoder;

import dream.first.core.platform.user.model.User;
import dream.first.core.platform.user.service.UserCommonService;
import dream.first.extjs.controller.BaseExtJSCrudModelController;
import dream.first.extjs.plugin.platform.user.dto.UserByRoleDTO;
import dream.first.extjs.plugin.platform.user.dto.UserDTO;
import dream.first.extjs.plugin.platform.user.service.UserService;
import dream.first.extjs.support.msg.JsonMsg;

/**
 * 用户控制器
 * 
 * @since 2.0
 */
@RequestMapping(value = "user")
public abstract class BaseUserController<M extends User> extends BaseExtJSCrudModelController<M> {

	@Resource
	protected UserCommonService userCommonService;

	@Resource
	protected UserService userService;

	@RequestMapping("index")
	public String index() {
		return "platform/user/userManage.jsp";
	}

	/**
	 * 修改用户密码
	 */
	@ResponseBody
	@RequestMapping(value = "modifyPassword")
	public String modifyPassword() {
		JsonMsg msg = new JsonMsg(false, "");
		String userId = getCurrentLoginUser().getId();
		String oldPassword = getRequest().getParameter("oldPassword");
		String newPassword = getRequest().getParameter("newPassword");
		msg.setSuccess(userService.modifyPassword(userId, oldPassword, newPassword));
		return toJson(msg);
	}

	/**
	 * ...
	 */
	@ResponseBody
	@RequestMapping(value = "passwordDIVCheck")
	public String passwordDIVCheck() {
		JsonMsg msg = new JsonMsg(true);
		String userId = getRequest().getParameter("model.id");
		if (StringUtils.isBlank(userId)) {
//			User user = modelService.findById(User.class, userId);
			// if ((user != null) &&
			// (StringUtils.isNotBlank(user.getPwdSign())) &&
			// (!this.userService.getPasswordSign(user.getPassword()).equals(user.getPwdSign())))
			// {
			// msg.setSuccess(false);
			// msg.setMsg("你的登录密码被篡改！为保障安全，请及时与客服联系重置并修改密码!");
			// }
		}
		return toJson(msg);
	}

	/**
	 * 查询
	 */
	@ResponseBody
	@RequestMapping(value = "queryUserWithCheck")
	public Object queryUserWithCheck(@ModelAttribute M model) throws Exception {
		return query(model);
	}

	/**
	 * 回显密码
	 */
	@ResponseBody
	@RequestMapping(value = "echoPassword")
	public String echoPassword() {
		JsonMsg msg = new JsonMsg(false);
		String userIds = getRequest().getParameter("userIds");
		if (StringUtils.isNotBlank(userIds)) {
			User user = this.modelService.findById(User.class, userIds);
			if (user != null) {
				msg.setMsg(JSDesCoder.strDec(user.getPassword(), "1", "2", "3"));
				msg.setSuccess(true);
			} else {
				msg.setMsg("用户信息不存在！");
			}
		} else {
			msg.setMsg("请选择一条记录！");
		}
		return toJson(msg);
	}

	/**
	 * 用户解除锁定
	 */
	@ResponseBody
	@RequestMapping(value = "unLockUser")
	public String unLockUser() throws Exception {
		String userIds = getRequest().getParameter("userIds");
		JsonMsg msg = new JsonMsg(true, "用户解锁成功");
		this.userCommonService.unLockUser(userIds);
		return toJson(msg);
	}

	/**
	 * 用户锁定
	 */
	@ResponseBody
	@RequestMapping(value = "lockUser")
	public String lockUser() throws Exception {
		String userIds = getRequest().getParameter("userIds");
		JsonMsg msg = new JsonMsg(true, "用户解锁成功");
		this.userCommonService.lockUser(userIds);
		return toJson(msg);
	}

	/**
	 * 重置密码
	 */
	@ResponseBody
	@RequestMapping(value = "resetPassword")
	public String resetPassword() throws Exception {
		String userIds = getRequest().getParameter("userIds");
		JsonMsg msg = new JsonMsg(true);
		this.userCommonService.resetPassword(userIds);
		return toJson(msg);
	}

	/**
	 * 重置密码第二版本
	 */
	@ResponseBody
	@RequestMapping(value = "resetPasswordV2")
	public String resetPasswordV2() throws Exception {
		String userIds = getRequest().getParameter("userIds");
		Strings.requireNonBlank(userIds, "必填参数缺失：userIds");
		String newPassword = getRequest().getParameter("newPassword");
		Strings.requireNonBlank(newPassword, "必填参数缺失：newPassword");
		JsonMsg msg = new JsonMsg(true);
		this.userService.resetPassword(userIds.split(","), newPassword);
		return toJson(msg);
	}

	/**
	 * 查询用户
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String queryUser(@ModelAttribute M model) throws Exception {
		beforeQuery(model);
		PageInfo pageInfo = null;
		// setSortMap(model);
		model.addSortFields(getSortFieldMap());
		String roleId = getRequest().getParameter("roleId");
		if (StringUtils.isEmpty(roleId)) {
			pageInfo = new PageInfo(modelService.findBySqlModel(UserDTO.class, getFindUserVOSql(), model));
		} else {
			pageInfo = new PageInfo(modelService.findBySqlModel(UserByRoleDTO.class, model));
		}
		return pageInfoToJson(pageInfo);
	}

	@Override
	protected boolean validateModel(M model, JsonMsg msg) throws Exception {
		if (StringUtils.isBlank(model.getId())) {
			model.setUsername(model.getUsername().trim());
			User u = userCommonService.findByUserName(model.getUsername());
			if (u != null) {
				msg.setSuccess(false);
				msg.setMsg("用户名【" + model.getUsername() + "】已存在，请选择其他用户名！");
				return false;
			}
		}
		String usrRolesProperty = getRequest().getParameter("usrRolesProperty");
		if (StringUtils.isNotBlank(usrRolesProperty)) {
			String[] roleProps = usrRolesProperty.split(",");
			String firstProp = StringUtils.isBlank(roleProps[0]) ? "01" : roleProps[0];
			boolean result = true;
			String[] arrayOfString1;
			int j = (arrayOfString1 = roleProps).length;
			for (int i = 0; i < j; i++) {
				String roleProp = arrayOfString1[i];
				roleProp = StringUtils.isBlank(roleProp) ? "01" : roleProp;
				if (!roleProp.equals(firstProp)) {
					result = false;
					break;
				}
			}
			if (!result) {
				msg.setSuccess(false);
				msg.setMsg("不能同时拥有“业务”、“审计”和“系统”角色，请重新设置！");
				return false;
			}
		}
		return true;
	}

	@Override
	protected void beforeQuery(M model) throws Exception {

		model.addConditionOperator("realName", "LIKE");
		model.addConditionOperator("username", "LIKE");

		// 组织
		String orgNo = getParameter("orgNo");
		if (StringUtils.isNotEmpty(orgNo)) {
			model.addExtendAttribute("org.orgNo", orgNo + "%");
			model.addConditionOperator("org.orgNo", "LIKE");
		}
		String roleId = getParameter("roleId");

		if (StringUtils.isNotBlank(roleId)) {
			model.addExtendAttribute("userrole.roleId", roleId);
		}
	}

	@Override
	protected void saveModel(M model) throws Exception {
		String usrRoles = getParameter("usrRoles");
		List<String> userRoles = null;
		if (StringUtils.isNotBlank(usrRoles)) {
			userRoles = Arrays.asList(usrRoles.split(","));
		}
		userService.save(model, userRoles);
	}

	@Override
	protected void modifyModel(M model) throws Exception {
		String usrRoles = getParameter("usrRoles");
		List<String> userRoles = null;
		if (StringUtils.isNotBlank(usrRoles)) {
			userRoles = Arrays.asList(usrRoles.split(","));
		}
		boolean overrideSourceRole = getParameterBoolean("overrideSourceRole", true);
		userService.modifyById(model, userRoles, overrideSourceRole);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected M retrieveModel(M model) throws Exception {
		return (M) modelService.findFirstBySqlModel(UserDTO.class, getFindUserVOSql(), model);
	}

	@Override
	protected PageInfo<?> queryModel(SqlModel<M> sqlModel, Integer pageNum, Integer pageSize) throws Exception {
		return new PageInfo<>(
				modelService.findPageBySqlModel(UserDTO.class, getFindUserVOSql(), sqlModel, pageNum, pageSize));
	}

	protected String getFindUserVOSql() {
		Dialect dialect = modelService.getModelConfiguration().getDialect();
		DialectType dialectType = dialect.getDialectType();
		if (dialectType == DialectType.MYSQL) {
			return "select usr.*, org.orgName usrOrgName, org.orgNo usrOrgNo, "
					+ "(select group_concat(userrole.roleId) from CO_USER_ROLE userrole where userrole.userId = usr.id) usrRoles  "
					+ "from CO_USER usr  " + "inner join CO_ORG org on usr.orgId = org.id and org.state = '0' ";
		} else if (dialectType == DialectType.ORACLE) {
			return "select usr.*, org.orgName usrOrgName, org.orgNo usrOrgNo , " + "(select "
			// + "to_char(wm_concat(userrole.roleId)) "
					+ "LISTAGG(userrole.roleId,',') WITHIN GROUP (ORDER BY userrole.roleId) "
					+ "from CO_USER_ROLE userrole where userrole.userId = usr.id) usrRoles  " + "from CO_USER usr  "
					+ "inner join CO_ORG org on usr.orgId = org.id and org.state = '0' ";
		} else {
			throw new UnsupportedOperationException("不支持的数据库方言：" + dialectType);
		}
	}

	protected void checkPwdDIV(List<Map<String, Object>> modelList) {
		if ((modelList != null) && (modelList.size() > 0)) {
			for (Map<String, Object> model : modelList) {
				String pwdSign = (String) model.get("pwdSign");
				if ((StringUtils.isBlank(pwdSign)) || ((StringUtils.isNotBlank(pwdSign))
						&& (pwdSign.equals(this.userCommonService.getPasswordSign((String) model.get("password")))))) {
					model.put("isUnIllegalModified", Boolean.valueOf(false));
				} else {
					model.put("isUnIllegalModified", Boolean.valueOf(true));
				}
			}
		}
	}

}
