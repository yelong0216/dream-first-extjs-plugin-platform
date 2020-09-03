/**
 * 
 */
package dream.first.extjs.plugin.platform.filter.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;

import dream.first.core.controller.BaseCoreController;
import dream.first.extjs.plugin.platform.filter.bean.Filter;

/**
 * 
 * @since 2.0
 */
@RequestMapping("filter")
public abstract class BaseFilterController extends BaseCoreController {

	@RequestMapping("index")
	public String index() {
		return "filter/filter.jsp";
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
