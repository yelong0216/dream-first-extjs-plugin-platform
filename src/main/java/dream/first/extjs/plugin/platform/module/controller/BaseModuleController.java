/**
 * 
 */
package dream.first.extjs.plugin.platform.module.controller;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yelong.commons.beans.BeanUtilsE;
import org.yelong.commons.lang.Strings;
import org.yelong.core.model.collector.ModelCollectors;
import org.yelong.support.servlet.resource.response.ResourceResponseException;
import org.yelong.support.spring.mvc.HandlerResponseWay;
import org.yelong.support.spring.mvc.ResponseWay;

import dream.first.core.platform.module.Modules;
import dream.first.core.platform.module.manage.CacheableModuleManager;
import dream.first.core.platform.module.manage.ModuleManager;
import dream.first.core.platform.module.model.Module;
import dream.first.core.platform.module.service.ModuleCommonService;
import dream.first.extjs.base.controller.DFBaseExtJSCrudModelController;
import dream.first.extjs.plugin.platform.ExtJSPluginPlatform;
import dream.first.extjs.plugin.platform.module.dto.ModuleDTO;
import dream.first.extjs.plugin.platform.module.tree.ModuleTreeGenerateConfig;
import dream.first.extjs.plugin.platform.module.tree.ModuleTreeGenerator;
import dream.first.extjs.support.msg.JsonMsg;
import dream.first.extjs.support.store.TreeStoreData;

/**
 * 模块控制器
 * 
 * @since 2.0
 */
@RequestMapping({ "module", "extjs/plugin/platform/module" })
public abstract class BaseModuleController<M extends ModuleDTO> extends DFBaseExtJSCrudModelController<M> {

	@Resource
	protected ModuleTreeGenerator moduleTreeGenerator;

	@Resource
	protected ModuleManager moduleManager;

	@Resource
	protected ModuleCommonService moduleCommonService;

	@ResponseBody
	@RequestMapping("index")
	@ResponseWay(HandlerResponseWay.MODEL_AND_VIEW)
	public void index() throws ResourceResponseException, IOException {
		responseHtml(ExtJSPluginPlatform.RESOURCE_PRIVATES_PACKAGE,
				ExtJSPluginPlatform.RESOURCE_PREFIX + "/html/module/moduleManage.html");
	}

	/**
	 * 复制模块
	 */
	@ResponseBody
	@RequestMapping("copyModule")
	public String copyModule() {
		String modelId = getParameter("modelId");
		Strings.requireNonBlank("必填参数缺失：modelId");
		Module module = modelService.collect(ModelCollectors.getModelByOnlyPrimaryKeyEQ(Module.class, modelId));
		Objects.requireNonNull("不存在的模块！");
		Integer copyNum = getParameterInteger("copyNum", 1);
		if (copyNum < 1) {
			throw new IllegalArgumentException("复制数量不能小于1个");
		}
		if (copyNum == 1) {
			moduleCommonService.save(module);
		} else {
			modelService.doOperation(() -> {
				for (int i = 0; i < copyNum; i++) {
					moduleCommonService.save(module);
				}
			});
		}
		return toJson(new JsonMsg(true));
	}

	/**
	 * 字符替换
	 */
	@ResponseBody
	@RequestMapping(value = "replace")
	public String replace() throws Exception {
		JsonMsg msg = new JsonMsg(false);
		HttpServletRequest request = getRequest();
		String column = request.getParameter("column");
		String find = request.getParameter("find");
		String replaceWith = request.getParameter("replaceWith");
		Module sqlModel = new Module();
		sqlModel.addExtendAttribute(column, "%" + find + "%");
		sqlModel.addConditionOperator(column, "LIKE");
		List<Module> modelList = modelService.findBySqlModel(Module.class, sqlModel);
		int successCount = 0;
		for (Module module : modelList) {
			Object value = BeanUtilsE.getProperty(module, column);
			if (null == value) {
				continue;
			}
			if (!(value instanceof String)) {
				throw new UnsupportedOperationException("目前只允许替换字符类型的数据！");
			}
			value = ((String) value).replace(find, replaceWith);
			BeanUtilsE.setProperty(module, column, value);
			modelService.collect(ModelCollectors.modifyModelByOnlyPrimaryKeyEQ(module));
			++successCount;
		}
		if (modelList.isEmpty()) {
			msg.setMsg("没有找到符合的数据：" + find);
		} else {
			msg.setSuccess(true);
			msg.setMsg("已成功替换：" + successCount + "处！");
		}
		return toJson(msg);
	}

	/**
	 * 生成模块树
	 * 
	 * @return 模块树JSON
	 */
	@ResponseBody
	@RequestMapping(value = "getModuleTree")
	public String getModuleTree() throws Exception {
		HttpServletRequest request = getRequest();
		ModuleTreeGenerateConfig config = new ModuleTreeGenerateConfig();
		String parentModuleNo = request.getParameter("parentModuleNo");
		config.setAuth(getParameterBoolean("isAuth", true));
		config.setAutoExpand(getParameterBoolean("autoExpand", false));
		config.setRecursion(getParameterBoolean("recursion", false));
		config.setSelectedModuleNo(getParameter("selectedModuleNo"));
		config.setShowCheckbox(getParameterBoolean("showCheckbox", false));
		config.setShowHiddenModule(getParameterBoolean("showHiddenModule", false));
		config.setShowOpModule(getParameterBoolean("showOpModule", false));
		config.setShowRoot(("-1".equals(parentModuleNo)) && getParameterBoolean("showRoot", false));
		if (StringUtils.isBlank(parentModuleNo) || "-1".equals(parentModuleNo)) {
			parentModuleNo = Modules.ROOT_MODULE_NO;
		}
		config.setParentModuleNo(parentModuleNo);
		List<TreeStoreData<Module>> treeStoreData = moduleTreeGenerator.generateTree(config);
		return null == treeStoreData ? "[]" : toJson(treeStoreData);
	}

	@Override
	public void beforeQuery(M model) throws Exception {
		model.addConditionOperator("moduleName", "like");
	}

	@Override
	public void saveModel(M model) throws Exception {
		moduleCommonService.save(model);
	}

	@Override
	public void modifyModel(M model) throws Exception {
		moduleCommonService.modifyById(model);
	}

	@Override
	public boolean deleteModel(String deleteIds) throws Exception {
		return moduleCommonService.removerByModuleNoBatch(deleteIds.split(","));
	}

	@Override
	public M retrieveModel(M model) throws Exception {
		if (StringUtils.isBlank(model.getModuleNo())) {
			return null;
		}
		return modelService
				.collect(ModelCollectors.getModelBySingleColumnEQ(getModelClass(), "moduleNo", model.getModuleNo()));
	}

	@Override
	public void afterDelete(String deleteIds) throws Exception {
		clearModuleCache();
	}

	@Override
	public void afterModify(M model) throws Exception {
		clearModuleCache();
	}

	@Override
	public void afterSave(M model) throws Exception {
		clearModuleCache();
	}

	protected void clearModuleCache() {
		if (moduleManager instanceof CacheableModuleManager) {
			((CacheableModuleManager) moduleManager).clearCache();
		}
	}

}
