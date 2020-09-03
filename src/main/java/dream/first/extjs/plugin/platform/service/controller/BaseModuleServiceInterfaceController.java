package dream.first.extjs.plugin.platform.service.controller;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yelong.commons.lang.Strings;
import org.yelong.core.jdbc.sql.condition.support.Condition;
import org.yelong.core.model.collector.ModelCollectors;
import org.yelong.core.model.sql.SqlModel;

import dream.first.core.platform.service.manage.CacheableModuleServiceManager;
import dream.first.core.platform.service.manage.ModuleServiceManager;
import dream.first.core.platform.service.model.ModuleServiceInterface;
import dream.first.extjs.controller.BaseExtJSCrudModelController;
import dream.first.extjs.support.msg.JsonMsg;

/**
 * 模块服务接口控制器
 * 
 * @param <M> ModuleServiceInterface type
 * @since 2.0
 */
@RequestMapping(value = "moduleServiceInterface")
public abstract class BaseModuleServiceInterfaceController<M extends ModuleServiceInterface>
		extends BaseExtJSCrudModelController<M> {

	@Resource
	protected ModuleServiceManager moduleServiceManager;

	/**
	 * 接口转移 将接口转移到另一个服务下面
	 */
	@ResponseBody
	@RequestMapping(value = "interfaceTransfer")
	public String interfaceTransfer() {
		JsonMsg msg = new JsonMsg(true);
		String interfaceIds = getRequest().getParameter("interfaceIds");
		String serviceId = getRequest().getParameter("serviceId");
		if (StringUtils.isBlank(interfaceIds) || StringUtils.isBlank(serviceId)) {
			return toJson(msg);
		}
		SqlModel<ModuleServiceInterface> sqlModel = new SqlModel<>(ModuleServiceInterface.class);
		sqlModel.addCondition(new Condition("id", "in", interfaceIds.split(",")));
		ModuleServiceInterface moduleServiceInterface = new ModuleServiceInterface();
		moduleServiceInterface.setServiceId(serviceId);
		modelService.modifySelectiveBySqlModel(moduleServiceInterface, sqlModel);
		return toJson(msg);
	}

	@Override
	protected void beforeQuery(M model) throws Exception {
		model.addConditionOperator("interfaceName", "LIKE");
		model.addConditionOperator("interfaceDesc", "LIKE");
	}

	@Override
	protected boolean validateModel(M model, JsonMsg msg) throws Exception {
		String interfaceName = model.getInterfaceName();
		Strings.requireNonBlank(interfaceName, "请录入接口名称！");
		boolean validateResult;
		ModuleServiceInterface sqlModel = new ModuleServiceInterface();
		sqlModel.setInterfaceName(interfaceName);
		if (isNew(model)) {
			validateResult = !modelService.existBySqlModel(ModuleServiceInterface.class, sqlModel);
		} else {
			String sourceInterfaceName = modelService.collect(ModelCollectors
					.getSingleValueByOnlyPrimaryKeyEQ(ModuleServiceInterface.class, "interfaceName", model.getId()));
			if (sourceInterfaceName.equals(interfaceName)) {
				validateResult = true;
			} else {
				validateResult = !modelService.existBySqlModel(ModuleServiceInterface.class, sqlModel);
			}
		}
		if (!validateResult) {
			msg.setMsg("接口名称【" + interfaceName + "】已存在，请选择其他接口名称！");
		}
		return validateResult;
	}

	@Override
	protected void afterDelete(String deleteIds) throws Exception {
		clearModuleServiceCache();
	}

	@Override
	protected void afterModify(M model) throws Exception {
		clearModuleServiceCache();
	}

	@Override
	protected void afterSave(M model) throws Exception {
		clearModuleServiceCache();
	}

	protected void clearModuleServiceCache() {
		if (moduleServiceManager instanceof CacheableModuleServiceManager) {
			((CacheableModuleServiceManager) moduleServiceManager).clearCache();
		}
	}

}
