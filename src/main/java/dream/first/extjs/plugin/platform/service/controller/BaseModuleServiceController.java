package dream.first.extjs.plugin.platform.service.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;

import dream.first.core.platform.service.manage.CacheableModuleServiceManager;
import dream.first.core.platform.service.manage.ModuleServiceManager;
import dream.first.core.platform.service.model.ModuleService;
import dream.first.extjs.controller.BaseExtJSCrudModelController;

/**
 * 模块服务控制器
 * 
 * @param <M> ModuleService type
 * @since 2.0
 */
@RequestMapping(value = "moduleService")
public abstract class BaseModuleServiceController<M extends ModuleService> extends BaseExtJSCrudModelController<M> {

	@Resource
	protected ModuleServiceManager moduleServiceManager;

	@RequestMapping("index")
	public String index() {
		return "platform/service/moduleServiceManage.jsp";
	}

	@Override
	protected void beforeQuery(M model) throws Exception {
		model.addConditionOperator("serviceName", "LIKE");
		model.addConditionOperator("serviceNameEn", "LIKE");
		model.addConditionOperator("serviceCharger", "LIKE");
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
