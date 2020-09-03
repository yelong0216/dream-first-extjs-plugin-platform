/**
 * 
 */
package dream.first.extjs.plugin.platform.user.controller;

import java.util.List;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;

import dream.first.core.platform.user.model.UserExtraOrg;
import dream.first.extjs.controller.BaseExtJSCrudModelController;
import dream.first.extjs.plugin.platform.user.dto.UserExtraOrgDTO;

/**
 * 用户附属部门控制器
 * 
 * @param <M>UserExtraOrg type
 * @since 2.0
 */
@RequestMapping(value = "userExtraOrg")
public abstract class BaseUserExtraOrgController<M extends UserExtraOrg> extends BaseExtJSCrudModelController<M> {

	@ResponseBody
	@RequestMapping(value = "queryUserExtraOrg")
	public String queryUserExtraOrg(@ModelAttribute M model) {
		model.addSortFields(getSortFieldMap());
		Integer pageNum = getPageNum();
		Integer pageSize = getPageSize();
		List<UserExtraOrgDTO> list = modelService.findPageBySqlModel(UserExtraOrgDTO.class, model, pageNum, pageSize);
		return pageInfoToJson(new PageInfo<>(list));
	}

}
