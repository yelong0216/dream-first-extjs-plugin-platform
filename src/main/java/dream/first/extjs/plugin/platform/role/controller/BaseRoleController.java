package dream.first.extjs.plugin.platform.role.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yelong.support.servlet.resource.response.ResourceResponseException;
import org.yelong.support.spring.mvc.HandlerResponseWay;
import org.yelong.support.spring.mvc.ResponseWay;

import dream.first.core.platform.role.constants.RoleType;
import dream.first.core.platform.role.model.Role;
import dream.first.core.platform.role.service.RoleCommonService;
import dream.first.core.platform.role.service.RoleRightCommonService;
import dream.first.extjs.base.controller.DFBaseExtJSCrudModelController;
import dream.first.extjs.base.msg.DFEJsonMsg;
import dream.first.extjs.plugin.platform.ExtJSPluginPlatform;
import dream.first.extjs.support.msg.JsonMsg;

/**
 * 基础的角色控制器
 * 
 * @param <M> role model type
 * @since 2.0
 */
@RequestMapping({ "role", "extjs/plugin/platform/role" })
public abstract class BaseRoleController<M extends Role> extends DFBaseExtJSCrudModelController<M> {

	@Resource
	protected RoleCommonService roleCommonService;

	@Resource
	protected RoleRightCommonService roleRightService;

	@ResponseBody
	@RequestMapping("index")
	@ResponseWay(HandlerResponseWay.MODEL_AND_VIEW)
	public void index() throws ResourceResponseException, IOException {
		responseHtml(ExtJSPluginPlatform.RESOURCE_PRIVATES_PACKAGE,
				ExtJSPluginPlatform.RESOURCE_PREFIX + "/html/role/roleManage.html");
	}

	/**
	 * 查询角色
	 */
	@ResponseBody
	@RequestMapping(value = "queryRole")
	public String queryRole(@ModelAttribute M model) throws Exception {
		return query(model).toString();
	}

	/**
	 * 获取角色拥有的模块权限
	 */
	@ResponseBody
	@RequestMapping(value = "getRoleRightModuleIDs")
	public String getRoleRightModuleIDs() {
		JsonMsg msg = new JsonMsg(true, "");
		String roleId = getRequest().getParameter("model.id");
		List<String> moduleIDList = roleRightService.findModuleIdByRoleId(roleId);
		if ((moduleIDList != null) && (moduleIDList.size() > 0)) {
			String ids = "";
			for (String id : moduleIDList) {
				ids = ids + id + ",";
			}
			ids = ids.substring(0, ids.length() - 1);
			msg.setMsg(ids);
		}
		return toJson(msg);
	}

	/**
	 * 复制角色
	 */
	@ResponseBody
	@RequestMapping(value = "copyRole")
	public String copyRole() {
		JsonMsg msg = new JsonMsg(true, "角色复制失败！");
		String roleIds = getRequest().getParameter("roleIds");
		this.roleCommonService.copyBatch(roleIds.split(","));
		msg.setMsg("角色复制成功！");
		return toJson(msg);
	}

	/**
	 * 获取角色树
	 */
	@ResponseBody
	@RequestMapping(value = "getRoleTree")
	public String getRoleTree() {
		List<Role> roleTree = roleCommonService.findRoleTree();
		return toJson(roleTree(roleTree));
	}

	/**
	 * 转换为角色树格式
	 */
	protected List<Map<String, Object>> roleTree(List<Role> roleList) {
		return roleList.stream().map(role -> {
			Map<String, Object> roleMap = new HashMap<>(4);
			roleMap.put("id", role.getId());
			roleMap.put("text", role.getRoleName());
			roleMap.put("extraParam1", role.getRoleType());
			roleMap.put("leaf", true);
			return roleMap;
		}).collect(Collectors.toList());
	}

	@Override
	public void beforeQuery(M model) throws Exception {
		model.addConditionOperator("roleName", "LIKE");
	}

	@Override
	public boolean validateModel(M model, DFEJsonMsg msg) throws Exception {
		if (StringUtils.isBlank(model.getId())) {
			if (roleCommonService.existByRoleName(model.getRoleName())) {
				msg.setSuccess(false);
				msg.setMsg("角色名称【" + model.getRoleName() + "】已存在，请选择其他角色名称！");
				return false;
			}
		}
		return true;
	}

	@Override
	public void saveModel(M model) throws Exception {
		if (model.getRoleType().equals(RoleType.OPERATE.code())) {
			String roleRights = getRequest().getParameter("roleRights");
			List<String> moduleIds = null;
			if (StringUtils.isNotEmpty(roleRights)) {
				moduleIds = Arrays.asList(roleRights.split(","));
			}
			roleCommonService.saveByOperator(model, moduleIds);
		} else {
			roleCommonService.saveByData(model, null);
		}
	}

	@Override
	public void modifyModel(M model) throws Exception {
		String roleRights = getRequest().getParameter("roleRights");
		List<String> moduleIds = null;
		if (StringUtils.isNotEmpty(roleRights)) {
			moduleIds = Arrays.asList(roleRights.split(","));
		}
		roleCommonService.modifyById(model, moduleIds);
	}

}
