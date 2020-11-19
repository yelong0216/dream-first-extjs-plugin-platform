/**
 * 
 */
package dream.first.extjs.plugin.platform.org.controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yelong.support.servlet.resource.response.ResourceResponseException;
import org.yelong.support.spring.mvc.HandlerResponseWay;
import org.yelong.support.spring.mvc.ResponseWay;

import dream.first.core.platform.org.Orgs;
import dream.first.core.platform.org.manage.CacheableOrgManager;
import dream.first.core.platform.org.manage.OrgManager;
import dream.first.core.platform.org.model.Org;
import dream.first.core.platform.org.service.OrgCommonService;
import dream.first.extjs.base.controller.DFBaseExtJSCrudModelController;
import dream.first.extjs.base.msg.DFEJsonMsg;
import dream.first.extjs.plugin.platform.ExtJSPluginPlatform;
import dream.first.extjs.plugin.platform.org.tree.OrgTreeGenerator;
import dream.first.extjs.support.msg.JsonMsg;
import dream.first.extjs.support.store.TreeStoreData;

/**
 * 基础的组织控制器
 * 
 * @since 2.0
 */
@RequestMapping({ "org", "extjs/plugin/platform/org" })
public abstract class BaseOrgController<M extends Org> extends DFBaseExtJSCrudModelController<M> {

	@Resource
	protected OrgCommonService orgCommonService;

	@Resource
	protected OrgManager orgManager;

	@Resource
	protected OrgTreeGenerator orgTreeGenerator;

	@ResponseBody
	@RequestMapping("index")
	@ResponseWay(HandlerResponseWay.MODEL_AND_VIEW)
	public void index() throws ResourceResponseException, IOException {
		responseHtml(ExtJSPluginPlatform.RESOURCE_PRIVATES_PACKAGE,
				ExtJSPluginPlatform.RESOURCE_PREFIX + "/html/org/orgManage.html");
	}

	/**
	 * 获取组织树
	 */
	@ResponseBody
	@RequestMapping("getOrgTree")
	public String getOrgTree() {
		String parentOrgNo = getParameter("parentOrgNo", "-1");
		boolean single = getParameterBoolean("single", false);
		boolean showRoot = ((single) || ("-1".equals(parentOrgNo))) && (getParameterBoolean("showRoot", false));
		boolean recursion = getParameterBoolean("recursion", false);
		boolean recursionParent = getParameterBoolean("recursionParent", true);// 递归显示父
		boolean checkbox = getParameterBoolean("checkbox", false);
		parentOrgNo = (StringUtils.isBlank(parentOrgNo)) || ("-1".equals(parentOrgNo)) ? Orgs.ROOT_ORG_NO : parentOrgNo;
		List<TreeStoreData<Org>> treeStoreDatas = orgTreeGenerator.generateTree(parentOrgNo, single, showRoot,
				recursion, recursionParent, checkbox);
		return null == treeStoreDatas ? "[]" : toJson(treeStoreDatas);
	}

	/**
	 * 查询组织的子组织数量
	 */
	@ResponseBody
	@RequestMapping(value = "queryOrgChildOrgCount")
	public String queryOrgChildOrgCount() {
		JsonMsg msg = new JsonMsg(true, "0");
		String orgNo = getRequest().getParameter("orgNo");
		if (StringUtils.isBlank(orgNo)) {

		} else {
			Org sqlModel = new Org();
			sqlModel.setOrgNo(orgNo + "%");
			sqlModel.addConditionOperator("orgNo", "LIKE");
			Long count = modelService.countBySqlModel(Org.class, sqlModel);
			msg.setMsg("" + count + "");
		}
		return toJson(msg);
	}

	@Override
	public void beforeQuery(M model) throws Exception {
		model.addConditionOperator("orgName", "like");
	}

	@Override
	public boolean validateModel(M model, DFEJsonMsg msg) throws Exception {
		if (model.getOrgNo().equals(model.getParentOrgNo())) {
			msg.setMsg("上级机构不能为自己！");
			return false;
		}
		return true;
	}

	@Override
	public void saveModel(M model) throws Exception {
		String orgNo = orgCommonService.generateOrgNo(model.getParentOrgNo());
		model.setOrgNo(orgNo);
		orgCommonService.saveOrg(model);
	}

	@Override
	public void modifyModel(M model) throws Exception {
		if (!model.getParentOrgNo().equals(model.getOldParentOrgNo())) {
			String orgNo = orgCommonService.generateOrgNo(model.getParentOrgNo());
			model.setOldOrgNo(model.getOrgNo());
			model.setOrgNo(orgNo);
		}
		orgCommonService.modifyOrg(model);
	}

	@Override
	public boolean deleteModel(String deleteIds) throws Exception {
		if (StringUtils.isNotBlank(deleteIds)) {
			orgCommonService.removeOrgByOrgNos(deleteIds.split(","));
		}
		return true;
	}

	@Override
	public M retrieveModel(M model) throws Exception {
		String sql = "select org.*, org2.orgName orgParentOrgName from CO_ORG org left join CO_ORG org2 on org.parentorgno = org2.orgno and org2.state = '0'";
		return modelService.findFirstBySqlModel(getModelClass(), sql, null, model);
	}

	@Override
	public void afterDelete(String deleteIds) throws Exception {
		clearOrgCache();
	}

	@Override
	public void afterModify(M model) throws Exception {
		clearOrgCache();
	}

	@Override
	public void afterSave(M model) throws Exception {
		clearOrgCache();
	}

	public void clearOrgCache() {
		if (orgManager instanceof CacheableOrgManager) {
			((CacheableOrgManager) orgManager).clearCache();
		}
	}

}
