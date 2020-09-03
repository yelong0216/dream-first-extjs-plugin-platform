/**
 * 
 */
package dream.first.extjs.plugin.platform.log.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import dream.first.core.platform.log.model.Log;
import dream.first.extjs.controller.BaseExtJSCrudModelController;

/**
 * 日志管理器
 * 
 * @since 2.0
 */
@RequestMapping("log")
public abstract class BaseLogController<M extends Log> extends BaseExtJSCrudModelController<M> {

	@RequestMapping("index")
	public String index() {
		return "platform/log/logManage.jsp";
	}

	@Override
	protected void beforeQuery(M model) throws Exception {
		model.addConditionOperator("userName", "LIKE");
		String eventType = model.getEventType();
		if (StringUtils.isNotBlank(eventType)) {
			model.addCondition("eventType", "IN", eventType.split(","));
		}
	}

}
