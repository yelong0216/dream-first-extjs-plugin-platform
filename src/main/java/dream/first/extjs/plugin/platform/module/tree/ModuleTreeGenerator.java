package dream.first.extjs.plugin.platform.module.tree;

import java.util.List;

import dream.first.core.platform.module.model.Module;
import dream.first.extjs.support.store.TreeStoreData;

/**
 * 模块树生成器
 * 
 * @since 2.0
 */
public interface ModuleTreeGenerator {

	List<TreeStoreData<Module>> generateTree(ModuleTreeGenerateConfig moduleTreeGenerateConfig);

}
