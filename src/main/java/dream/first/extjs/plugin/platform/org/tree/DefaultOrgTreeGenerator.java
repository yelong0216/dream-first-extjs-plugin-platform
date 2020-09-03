/**
 * 
 */
package dream.first.extjs.plugin.platform.org.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.yelong.core.order.OrderComparator;

import dream.first.core.platform.org.manage.OrgManager;
import dream.first.core.platform.org.model.Org;
import dream.first.extjs.support.store.TreeStoreData;

/**
 * 默认的组织树生成器
 * 
 * @since 2.0
 */
public class DefaultOrgTreeGenerator implements OrgTreeGenerator {

	protected final OrgManager orgManager;

	public DefaultOrgTreeGenerator(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	@Override
	public List<TreeStoreData<Org>> generateTree(String parentOrgNo, boolean single, boolean showRoot,
			boolean recursion, boolean recursionParent, boolean checkbox) {
		Org org = orgManager.getByOrgNo(parentOrgNo);
		Objects.requireNonNull(org, "不存在的组织机构:" + parentOrgNo);
		List<TreeStoreData<Org>> storeTreeDatas = new ArrayList<TreeStoreData<Org>>();
		if (showRoot) {
			TreeStoreData<Org> treeStoreData = toTreeStoreData(org);
			treeStoreData.setExpanded("01".equals(org.getShowChild()));
			List<TreeStoreData<Org>> childrens = getChildrens(org, recursion, checkbox);
			if (CollectionUtils.isEmpty(childrens)) {
				treeStoreData.setLeaf(true);
			} else {
				treeStoreData.setChildren(childrens);
			}
			if (recursionParent) {
				boolean rootOrg = false;// 是否是最根的那个节点（最根有且仅有一个）
				String currentParentOrgNo = org.getParentOrgNo();
				while (!rootOrg) {// 递归查询父-直到最根的节点
					TreeStoreData<Org> parentTreeStoreData = getParent(currentParentOrgNo, false, checkbox);
					if (null != parentTreeStoreData) {
						parentTreeStoreData.setChildren(Arrays.asList(treeStoreData));
						treeStoreData = parentTreeStoreData;
						currentParentOrgNo = (String) parentTreeStoreData.getExtraParam2();// 父编号
						parentTreeStoreData.setExtraParam2(null);
					} else {
						rootOrg = true;
					}
				}
			}
			storeTreeDatas.add(treeStoreData);
		} else {
			storeTreeDatas = getChildrens(org, recursion, checkbox);
		}
		return storeTreeDatas;
	}

	/**
	 * 获取父
	 * 
	 * @param root
	 * @param recursion 是否递归
	 * @param checkbox
	 * @return
	 */
	private TreeStoreData<Org> getParent(String parentOrgNo, boolean recursion, boolean checkbox) {
		Org parentOrg = orgManager.getByOrgNo(parentOrgNo);
		if (null == parentOrg) {
			return null;
		}
//		TreeStoreData<Org> treeStoreData = toTreeStoreData<Org>(parentOrg);
		TreeStoreData<Org> treeStoreData = new TreeStoreData<Org>();
		treeStoreData.setId("" + System.currentTimeMillis());
		treeStoreData.setText(parentOrg.getOrgName());
		treeStoreData.setExtraParam1("" + System.currentTimeMillis());
		treeStoreData.setExtraParam2(parentOrg.getParentOrgNo());
		if (checkbox) {
			treeStoreData.setChecked(false);
		}
		if (recursion) {
			TreeStoreData<Org> parentTreeStoreData = getParent(parentOrg.getParentOrgNo(), recursion, checkbox);
			if (null != parentTreeStoreData) {
				parentTreeStoreData.setChildren(Arrays.asList(treeStoreData));
				treeStoreData = parentTreeStoreData;
			}
		}
		return treeStoreData;
	}

	/**
	 * 获取子
	 * 
	 * @param root
	 * @param recursion
	 * @param checkbox
	 * @return
	 */
	private List<TreeStoreData<Org>> getChildrens(Org root, boolean recursion, boolean checkbox) {
		Org sqlModel = new Org();
		sqlModel.setParentOrgNo(root.getOrgNo());
		sqlModel.addSortField("org.orgOrder", "asc");
		List<Org> childOrgs = orgManager.getByParentOrgNo(root.getOrgNo());
		if (CollectionUtils.isEmpty(childOrgs)) {
			return Collections.emptyList();
		}
		childOrgs.sort(OrderComparator.ASC_INSTANCE);
		List<TreeStoreData<Org>> storeTreeDatas = new ArrayList<TreeStoreData<Org>>(childOrgs.size());
		for (Org org : childOrgs) {
			TreeStoreData<Org> treeStoreData = toTreeStoreData(org);
			if (checkbox) {
				treeStoreData.setChecked(false);
			}
			if (!"01".equals(root.getShowChild())) {// 是否需要展示子
				continue;
			}
			List<TreeStoreData<Org>> childrens = getChildrens(org, recursion, checkbox);
			if (CollectionUtils.isEmpty(childrens)) {
				treeStoreData.setLeaf(true);
			} else {
				treeStoreData.setChildren(childrens);
			}
			storeTreeDatas.add(treeStoreData);
		}
		return storeTreeDatas;
	}

	/**
	 * 转换组织为树数据
	 * 
	 * @param org
	 * @return
	 */
	private TreeStoreData<Org> toTreeStoreData(Org org) {
		TreeStoreData<Org> treeStoreData = new TreeStoreData<Org>();
		treeStoreData.setId(org.getId());
		treeStoreData.setText(org.getOrgName());
		treeStoreData.setExtraParam1(org.getOrgNo());
		return treeStoreData;
	}

}
