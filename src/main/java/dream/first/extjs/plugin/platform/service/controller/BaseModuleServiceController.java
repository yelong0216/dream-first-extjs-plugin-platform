package dream.first.extjs.plugin.platform.service.controller;

import java.io.IOException;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yelong.support.servlet.resource.response.ResourceResponseException;
import org.yelong.support.spring.mvc.HandlerResponseWay;
import org.yelong.support.spring.mvc.ResponseWay;

import dream.first.core.platform.service.manage.CacheableModuleServiceManager;
import dream.first.core.platform.service.manage.ModuleServiceManager;
import dream.first.core.platform.service.model.ModuleService;
import dream.first.extjs.base.controller.DFBaseExtJSCrudModelController;
import dream.first.extjs.plugin.platform.ExtJSPluginPlatform;

/**
 * 模块服务控制器
 * 
 * @param <M> ModuleService type
 * @since 2.0
 */
@RequestMapping({ "moduleService", "extjs/plugin/platform/moduleService" })
public abstract class BaseModuleServiceController<M extends ModuleService> extends DFBaseExtJSCrudModelController<M> {

	@Resource
	protected ModuleServiceManager moduleServiceManager;

	@ResponseBody
	@RequestMapping("index")
	@ResponseWay(HandlerResponseWay.MODEL_AND_VIEW)
	public void index() throws ResourceResponseException, IOException {
		responseHtml(ExtJSPluginPlatform.RESOURCE_PRIVATES_PACKAGE,
				ExtJSPluginPlatform.RESOURCE_PREFIX + "/html/service/moduleServiceManage.html");
	}

	@Override
	public void beforeQuery(M model) throws Exception {
		model.addConditionOperator("serviceName", "LIKE");
		model.addConditionOperator("serviceNameEn", "LIKE");
		model.addConditionOperator("serviceCharger", "LIKE");
	}

	@Override
	public void afterDelete(String deleteIds) throws Exception {
		clearModuleServiceCache();
	}

	@Override
	public void afterModify(M model) throws Exception {
		clearModuleServiceCache();
	}

	@Override
	public void afterSave(M model) throws Exception {
		clearModuleServiceCache();
	}

	public void clearModuleServiceCache() {
		if (moduleServiceManager instanceof CacheableModuleServiceManager) {
			((CacheableModuleServiceManager) moduleServiceManager).clearCache();
		}
	}

}
