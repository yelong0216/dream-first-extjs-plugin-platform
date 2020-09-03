package dream.first.extjs.plugin.platform.org.tree;

import java.util.List;

import dream.first.core.platform.org.model.Org;
import dream.first.extjs.support.store.TreeStoreData;

/**
 * 组织树生成器
 * 
 * @since 2.0
 */
public interface OrgTreeGenerator {

	List<TreeStoreData<Org>> generateTree(String parentOrgNo, boolean single, boolean showRoot, boolean recursion,
			boolean recursionParent, boolean checkbox);

}
