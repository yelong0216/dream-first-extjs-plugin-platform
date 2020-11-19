/**
 * 
 */
package dream.first.extjs.plugin.platform.log.controller;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yelong.support.servlet.resource.response.ResourceResponseException;
import org.yelong.support.spring.mvc.HandlerResponseWay;
import org.yelong.support.spring.mvc.ResponseWay;

import dream.first.core.platform.log.model.Log;
import dream.first.extjs.base.controller.DFBaseExtJSCrudModelController;
import dream.first.extjs.plugin.platform.ExtJSPluginPlatform;

/**
 * 日志管理器
 * 
 * @since 2.0
 */
@RequestMapping({"log","extjs/plugin/platform/log"})
public abstract class BaseLogController<M extends Log> extends DFBaseExtJSCrudModelController<M> {

	@ResponseBody
	@RequestMapping("index")
	@ResponseWay(HandlerResponseWay.MODEL_AND_VIEW)
	public void index() throws ResourceResponseException, IOException {
		responseHtml(ExtJSPluginPlatform.RESOURCE_PRIVATES_PACKAGE,
				ExtJSPluginPlatform.RESOURCE_PREFIX + "/html/log/logManage.html");
	}

	@Override
	public void beforeQuery(M model) throws Exception {
		model.addConditionOperator("userName", "LIKE");
		String eventType = model.getEventType();
		if (StringUtils.isNotBlank(eventType)) {
			model.addCondition("eventType", "IN", eventType.split(","));
		}
	}

}
