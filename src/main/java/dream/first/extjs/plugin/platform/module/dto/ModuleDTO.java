/**
 * 
 */
package dream.first.extjs.plugin.platform.module.dto;

import org.yelong.core.model.annotation.ExtendColumn;
import org.yelong.core.model.annotation.Select;
import org.yelong.core.model.annotation.Table;

import dream.first.core.platform.module.model.Module;

/**
 * @since 2.0
 */
@Table(value = "CO_MODULE", alias = "module")
/*
 * select module.* , parentModule.moduleName from CO_MODULE module left join
 * CO_MODULE parentModule on parentModule.moduleNo = module.parentModuleNo
 */
@Select("select module.* , parentModule.moduleName moduleParentModuleName " + "from CO_MODULE module  "
		+ "left join CO_MODULE parentModule on parentModule.moduleNo = module.parentModuleNo")
public class ModuleDTO extends Module {

	private static final long serialVersionUID = -4850215271903936147L;

	@ExtendColumn
	private String moduleParentModuleName;

	public String getModuleParentModuleName() {
		return moduleParentModuleName;
	}

	public void setModuleParentModuleName(String moduleParentModuleName) {
		this.moduleParentModuleName = moduleParentModuleName;
	}

}
