/**
 * 
 */
package test.dream.first.extjs.platform.plugin.module.tree;

import org.junit.jupiter.api.Test;

import dream.first.core.platform.module.manage.ModuleManager;
import dream.first.core.platform.module.manage.SimpleModuleManager;
import dream.first.core.platform.module.service.ModuleCommonService;
import dream.first.core.platform.module.service.impl.ModuleCommonServiceImpl;
import dream.first.extjs.plugin.platform.module.tree.DefaultModuleTreeGenerator;
import dream.first.extjs.plugin.platform.module.tree.ModuleTreeGenerator;
import test.dream.first.extjs.platform.plugin.ModelObjectSupplier;

/**
 * 
 * @since
 */
public class ModuleTreeGeneratorTest {

	ModuleCommonService moduleCommonService = new ModuleCommonServiceImpl(ModelObjectSupplier.modelService);

	ModuleManager moduleManager = new SimpleModuleManager(moduleCommonService);
	
	ModuleTreeGenerator moduleTreeGenerator = new DefaultModuleTreeGenerator(moduleManager);
	
	@Test
	public void generate() {
//		List<TreeStoreData<Module>> generateTree = moduleTreeGenerator.generateTree("0000", true, false, false, false, true, true, false);
//		System.out.println(generateTree);
	}
	
}
