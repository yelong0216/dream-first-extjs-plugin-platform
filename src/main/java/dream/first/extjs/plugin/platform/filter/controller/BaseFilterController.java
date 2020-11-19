/**
 * 
 */
package dream.first.extjs.plugin.platform.filter.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yelong.support.servlet.resource.response.ResourceResponseException;
import org.yelong.support.spring.mvc.HandlerResponseWay;
import org.yelong.support.spring.mvc.ResponseWay;

import com.google.gson.Gson;

import dream.first.extjs.base.controller.DFBaseExtJSController;
import dream.first.extjs.plugin.platform.ExtJSPluginPlatform;
import dream.first.extjs.plugin.platform.filter.bean.Filter;

/**
 * 
 * @since 2.0
 */
@RequestMapping({ "filter", "extjs/plugin/platform/filter" })
public abstract class BaseFilterController extends DFBaseExtJSController {

	@ResponseBody
	@RequestMapping("index")
	@ResponseWay(HandlerResponseWay.MODEL_AND_VIEW)
	public void index() throws ResourceResponseException, IOException {
		responseHtml(ExtJSPluginPlatform.RESOURCE_PRIVATES_PACKAGE,
				ExtJSPluginPlatform.RESOURCE_PREFIX + "/html/filter/filter.html");
	}

	protected void handFilterInfo() {
		String filters = getRequest().getParameter("filters");
		Gson gson = new Gson();
		FilterWrapper filterWrapper = gson.fromJson(filters, FilterWrapper.class);
		List<Filter> filterList = filterWrapper.getFilters();

		Map<String, Filter> filterMap = new LinkedHashMap<>();
		for (Filter filter : filterList) {
			filterMap.put(filter.getFieldName(), filter);
		}
		getRequest().setAttribute("filters", toJson(filterMap));
	}

	public static class FilterWrapper {

		private List<Filter> filters;

		public List<Filter> getFilters() {
			return filters;
		}

		public void setFilters(List<Filter> filters) {
			this.filters = filters;
		}

	}

}
