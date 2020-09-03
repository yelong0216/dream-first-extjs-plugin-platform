package dream.first.extjs.plugin.platform.module.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.yelong.core.annotation.Nullable;
import org.yelong.core.order.OrderComparator;

import dream.first.core.platform.module.Modules;
import dream.first.core.platform.module.constants.ModuleShow;
import dream.first.core.platform.module.constants.ModuleType;
import dream.first.core.platform.module.manage.ModuleManager;
import dream.first.core.platform.module.model.Module;
import dream.first.core.utils.AuthUtils;
import dream.first.extjs.support.store.TreeStoreData;

/**
 * 默认的模块树生成器
 * 
 * @since 2.0
 */
public class DefaultModuleTreeGenerator implements ModuleTreeGenerator {

	protected final ModuleManager moduleManager;

	public DefaultModuleTreeGenerator(ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}

	@Override
	public List<TreeStoreData<Module>> generateTree(ModuleTreeGenerateConfig c) {
		Module module = moduleManager.getByModuleNo(c.parentModuleNo);
		Objects.requireNonNull(module, "不存在的模块：" + c.parentModuleNo);
		if (!hasGranted(c.isAuth, module.getId(), module.getModuleNo())) {
			return null;
		}
		List<TreeStoreData<Module>> childrenTree = getChildrenTree(module, c);
		if (!c.showRoot) {
			return childrenTree;
		}
		TreeStoreData<Module> treeStoreData = toTreeStoreData(module, c.showCheckbox);
		if (CollectionUtils.isNotEmpty(childrenTree)) {
			treeStoreData.setChildren(childrenTree);
			if (c.autoExpand) {
				treeStoreData.setExpanded(c.autoExpand);
			}
		}
		return Arrays.asList(treeStoreData);
	}

	@Nullable
	protected List<TreeStoreData<Module>> getChildrenTree(Module module, ModuleTreeGenerateConfig c) {
		List<Module> modules = moduleManager.getByParentModuleNo(module.getModuleNo());
		modules = modules.stream().filter(x -> {
			if (!c.showHiddenModule) {
				return ModuleShow.SHOW.CODE.equals(x.getModuleShow());
			}
			return true;
		}).filter(x -> {
			if (!c.showOpModule) {
				return ModuleType.MODULE.CODE.equals(x.getModuleType());
			}
			return true;
		}).sorted(OrderComparator.ASC_INSTANCE).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(modules)) {
			return null;
		}
		List<TreeStoreData<Module>> treeStoreDatas = new ArrayList<TreeStoreData<Module>>(modules.size());
		for (Module childrenModule : modules) {
			// 没验证通过直接返回
			if (!hasGranted(c.isAuth, childrenModule.getId(), childrenModule.getModuleNo())) {
				continue;
			}
			TreeStoreData<Module> treeStoreData = toTreeStoreData(childrenModule, c.showCheckbox);
			treeStoreDatas.add(treeStoreData);
			if (!isRecursion(childrenModule.getModuleNo(), c)) {
				continue;
			}
			List<TreeStoreData<Module>> childChildrenTree = getChildrenTree(childrenModule, c);
			if (CollectionUtils.isNotEmpty(childChildrenTree)) {
				if (c.autoExpand) {
					treeStoreData.setExpanded(c.autoExpand);
				}
				treeStoreData.setChildren(childChildrenTree);
			} else {
				treeStoreData.setLeaf(true);
			}
		}
		return treeStoreDatas;
	}

	/**
	 * 是否需要递归
	 * 
	 * @param currentModuleNo 当前正在生成的模块编号
	 * @param config          生成配置
	 * @return <code>true</code> 需要递归
	 */
	protected boolean isRecursion(String currentModuleNo, ModuleTreeGenerateConfig config) {
		if (config.recursion) {
			return true;
		}
		String selectedModuleNo = config.getSelectedModuleNo();
		if (StringUtils.isBlank(selectedModuleNo)) {
			return false;
		}
		return selectedModuleNo.startsWith(currentModuleNo);
	}

	protected boolean hasGranted(boolean isAuth, String moduleId, String moduleNo) {
		return (!isAuth) || Modules.isRootModuleNo(moduleNo) || ((isAuth) && (AuthUtils.hasOpRight(moduleId)));
	}

	protected TreeStoreData<Module> toTreeStoreData(Module module, boolean showCheckbox) {
		TreeStoreData<Module> treeStoreData = new TreeStoreData<Module>();
		treeStoreData.setId(module.getId());
		treeStoreData.setText(module.getModuleName());
		treeStoreData.setIconCls((StringUtils.isNotBlank(module.getModuleIcon()) ? module.getModuleIcon()
				: ModuleType.MODULE.code().equals(module.getModuleType()) ? "app_boxesIcon" : "cmpIcon"));
		treeStoreData.setExtraParam1(module.getModuleType());
		treeStoreData.setExtraParam2(module.getModuleUrl());
		treeStoreData.setExtraParam3(module.getModuleNo());
		treeStoreData.setExtraParam4(module.getModuleProperty());
		treeStoreData.setChecked(showCheckbox ? false : null);
		return treeStoreData;

	}

}
