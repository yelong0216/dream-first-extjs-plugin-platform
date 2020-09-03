/**
 * 
 */
package dream.first.extjs.plugin.platform.icon.controller;

import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yelong.commons.lang.Strings;

import dream.first.core.platform.icon.manage.CacheableIconManager;
import dream.first.core.platform.icon.manage.IconManager;
import dream.first.core.platform.icon.model.Icon;
import dream.first.extjs.controller.BaseExtJSCrudModelController;
import dream.first.extjs.support.msg.JsonMsg;

/**
 * 图标控制器
 * 
 * @since 2.0
 */
@RequestMapping("icon")
public abstract class BaseIconController<M extends Icon> extends BaseExtJSCrudModelController<M> {

	@Resource
	protected IconManager iconManager;

	@RequestMapping("index")
	public String index() {
		return "platform/icon/iconManage.jsp";
	}

	/**
	 * 图标复制
	 */
	@ResponseBody
	@RequestMapping("copyIcon")
	public String copyIcon() {
		String modelId = getParameter("modelId");
		Strings.requireNonBlank("必填参数缺失：modelId");
		Icon dict = modelService.findById(Icon.class, modelId);
		Objects.requireNonNull("不存在的图标！");
		Integer copyNum = getParameterInteger("copyNum", 1);
		if (copyNum < 1) {
			throw new IllegalArgumentException("复制数量不能小于1个");
		}
		if (copyNum == 1) {
			modelService.saveSelective(dict);
		} else {
			modelService.doOperation(() -> {
				for (int i = 0; i < copyNum; i++) {
					modelService.saveSelective(dict);
				}
			});
		}
		return toJson(new JsonMsg(true));
	}

	@Override
	protected void beforeQuery(M model) throws Exception {
		model.addConditionOperator("iconClass", "LIKE");
	}

	@Override
	protected void afterDelete(String deleteIds) throws Exception {
		clearIconCache();
	}

	@Override
	protected void afterModify(M model) throws Exception {
		clearIconCache();
	}

	@Override
	protected void afterSave(M model) throws Exception {
		clearIconCache();
	}

	protected void clearIconCache() {
		if (iconManager instanceof CacheableIconManager) {
			((CacheableIconManager) iconManager).clearCache();
		}
	}

}
