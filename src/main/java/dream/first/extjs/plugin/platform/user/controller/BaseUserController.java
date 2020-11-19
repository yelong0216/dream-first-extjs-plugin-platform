/**
 * 
 */
package dream.first.extjs.plugin.platform.user.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yelong.commons.lang.Strings;
import org.yelong.core.model.collector.ModelCollectors;
import org.yelong.core.model.sql.SqlModel;
import org.yelong.support.servlet.resource.response.ResourceResponseException;
import org.yelong.support.spring.mvc.HandlerResponseWay;
import org.yelong.support.spring.mvc.ResponseWay;

import com.github.pagehelper.PageInfo;
import com.labbol.cocoon.core.utils.security.coder.JSDesCoder;

import dream.first.core.platform.user.model.User;
import dream.first.core.platform.user.service.UserCommonService;
import dream.first.extjs.base.controller.DFBaseExtJSCrudModelController;
import dream.first.extjs.base.msg.DFEJsonMsg;
import dream.first.extjs.plugin.platform.ExtJSPluginPlatform;
import dream.first.extjs.plugin.platform.user.handle.UserQueryHandler;
import dream.first.extjs.plugin.platform.user.service.UserService;
import dream.first.extjs.support.msg.JsonMsg;

/**
 * 用户控制器
 * 
 * @since 2.0
 */
@RequestMapping({ "user", "extjs/plugin/platform/user" })
public abstract class BaseUserController<M extends User> extends DFBaseExtJSCrudModelController<M> {

	@Resource
	protected UserCommonService userCommonService;

	@Resource
	protected UserService userService;

	@Resource
	protected UserQueryHandler userQueryHandler;

	@ResponseBody
	@RequestMapping("index")
	@ResponseWay(HandlerResponseWay.MODEL_AND_VIEW)
	public void index() throws ResourceResponseException, IOException {
		responseHtml(ExtJSPluginPlatform.RESOURCE_PRIVATES_PACKAGE,
				ExtJSPluginPlatform.RESOURCE_PREFIX + "/html/user/userManage.html");
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
			// User user = modelService.findById(User.class, userId);
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
			User user = this.modelService.collect(ModelCollectors.getModelByOnlyPrimaryKeyEQ(User.class, userIds));
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
		pageInfo = new PageInfo(userQueryHandler.queryUser(getRequest(), model));
		return pageInfoToJson(pageInfo);
	}

	@Override
	public boolean validateModel(M model, DFEJsonMsg msg) throws Exception {
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
	public void beforeQuery(M model) throws Exception {

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
	public void saveModel(M model) throws Exception {
		String usrRoles = getParameter("usrRoles");
		List<String> userRoles = null;
		if (StringUtils.isNotBlank(usrRoles)) {
			userRoles = Arrays.asList(usrRoles.split(","));
		}
		userService.save(model, userRoles);
	}

	@Override
	public void modifyModel(M model) throws Exception {
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
	public M retrieveModel(M model) throws Exception {
		return (M) userQueryHandler.getUser(getRequest(), model);
	}

	@Override
	public PageInfo<?> queryModel(SqlModel<M> sqlModel, Integer pageNum, Integer pageSize) throws Exception {
		return userQueryHandler.queryUser(getRequest(), sqlModel.getModel(), pageNum, pageSize);
	}

	public void checkPwdDIV(List<Map<String, Object>> modelList) {
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
