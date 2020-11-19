/**
 * 
 */
package dream.first.extjs.plugin.platform.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import dream.first.core.platform.module.manage.ModuleManager;
import dream.first.core.platform.org.manage.OrgManager;
import dream.first.core.platform.user.service.UserCommonService;
import dream.first.extjs.plugin.platform.dict.controller.BaseDictController;
import dream.first.extjs.plugin.platform.dict.controller.DefaultDictController;
import dream.first.extjs.plugin.platform.filter.controller.BaseFilterController;
import dream.first.extjs.plugin.platform.filter.controller.DefaultFilterController;
import dream.first.extjs.plugin.platform.icon.controller.BaseIconController;
import dream.first.extjs.plugin.platform.icon.controller.DefaultIconController;
import dream.first.extjs.plugin.platform.log.controller.BaseLogController;
import dream.first.extjs.plugin.platform.log.controller.DefaultLogController;
import dream.first.extjs.plugin.platform.module.controller.BaseModuleController;
import dream.first.extjs.plugin.platform.module.controller.DefaultModuleController;
import dream.first.extjs.plugin.platform.module.tree.DefaultModuleTreeGenerator;
import dream.first.extjs.plugin.platform.module.tree.ModuleTreeGenerator;
import dream.first.extjs.plugin.platform.org.controller.BaseOrgController;
import dream.first.extjs.plugin.platform.org.controller.DefaultOrgController;
import dream.first.extjs.plugin.platform.org.tree.DefaultOrgTreeGenerator;
import dream.first.extjs.plugin.platform.org.tree.OrgTreeGenerator;
import dream.first.extjs.plugin.platform.role.controller.BaseRoleController;
import dream.first.extjs.plugin.platform.role.controller.BaseRoleDataRightController;
import dream.first.extjs.plugin.platform.role.controller.DefaultRoleController;
import dream.first.extjs.plugin.platform.role.controller.DefaultRoleDataRightController;
import dream.first.extjs.plugin.platform.scip.controller.BaseSCIPController;
import dream.first.extjs.plugin.platform.scip.controller.DefaultSCIPController;
import dream.first.extjs.plugin.platform.security.controller.SecurityServiceController;
import dream.first.extjs.plugin.platform.service.controller.BaseModuleServiceController;
import dream.first.extjs.plugin.platform.service.controller.BaseModuleServiceInterfaceController;
import dream.first.extjs.plugin.platform.service.controller.DefaultModuleServiceController;
import dream.first.extjs.plugin.platform.service.controller.DefaultModuleServiceInterfaceController;
import dream.first.extjs.plugin.platform.servlet.PlatformResourceServletRegistrationBean;
import dream.first.extjs.plugin.platform.user.controller.BaseUserController;
import dream.first.extjs.plugin.platform.user.controller.BaseUserExtraOrgController;
import dream.first.extjs.plugin.platform.user.controller.DefaultUserController;
import dream.first.extjs.plugin.platform.user.controller.DefaultUserExtraOrgController;
import dream.first.extjs.plugin.platform.user.handle.UserQueryHandler;
import dream.first.extjs.plugin.platform.user.handle.impl.DefaultUserQueryHandler;
import dream.first.extjs.plugin.platform.user.service.UserService;
import dream.first.extjs.plugin.platform.user.service.impl.UserServiceImpl;

public class ExtJSPluginPlatformConfiguration {

	// 资源
	@Bean
	public PlatformResourceServletRegistrationBean platformResourceServletRegistrationBean() {
		return new PlatformResourceServletRegistrationBean();
	}

	// controller

	@Bean
	@ConditionalOnMissingBean(OrgTreeGenerator.class)
	public OrgTreeGenerator orgTreeGenerator(OrgManager orgManager) {
		return new DefaultOrgTreeGenerator(orgManager);
	}

	@Bean
	@ConditionalOnMissingBean(ModuleTreeGenerator.class)
	public ModuleTreeGenerator moduleTreeGenerator(ModuleManager moduleManager) {
		return new DefaultModuleTreeGenerator(moduleManager);
	}

	@Bean
	@ConditionalOnMissingBean(UserService.class)
	public UserService userService(UserCommonService userCommonService) {
		return new UserServiceImpl(userCommonService);
	}

	@Bean
	@ConditionalOnMissingBean(UserQueryHandler.class)
	public UserQueryHandler userQueryHandler() {
		return new DefaultUserQueryHandler();
	}

	// ==================================================controller==================================================
	
	@Bean
	@ConditionalOnMissingBean(BaseSCIPController.class)
	public DefaultSCIPController defaultSCIPDictController() {
		return new DefaultSCIPController();
	}

	@Bean
	@ConditionalOnMissingBean(BaseDictController.class)
	public DefaultDictController defaultDictController() {
		return new DefaultDictController();
	}

	@Bean
	@ConditionalOnMissingBean(BaseFilterController.class)
	public DefaultFilterController defaultFilterController() {
		return new DefaultFilterController();
	}

	@Bean
	@ConditionalOnMissingBean(BaseIconController.class)
	public DefaultIconController defaultIconController() {
		return new DefaultIconController();
	}

	@Bean
	@ConditionalOnMissingBean(BaseLogController.class)
	public DefaultLogController defaultLogController() {
		return new DefaultLogController();
	}

	@Bean
	@ConditionalOnMissingBean(BaseModuleController.class)
	public DefaultModuleController defaultModuleController() {
		return new DefaultModuleController();
	}

	@Bean
	@ConditionalOnMissingBean(BaseOrgController.class)
	public DefaultOrgController defaultOrgController() {
		return new DefaultOrgController();
	}

	@Bean
	@ConditionalOnMissingBean(BaseRoleController.class)
	public DefaultRoleController defaultRoleController() {
		return new DefaultRoleController();
	}

	@Bean
	@ConditionalOnMissingBean(BaseRoleDataRightController.class)
	public DefaultRoleDataRightController defaultRoleDataRightController() {
		return new DefaultRoleDataRightController();
	}

	@Bean
	@ConditionalOnMissingBean(SecurityServiceController.class)
	public SecurityServiceController securityServiceController() {
		return new SecurityServiceController();
	}

	@Bean
	@ConditionalOnMissingBean(BaseModuleServiceController.class)
	public DefaultModuleServiceController defaultModuleServiceController() {
		return new DefaultModuleServiceController();
	}

	@Bean
	@ConditionalOnMissingBean(BaseModuleServiceInterfaceController.class)
	public DefaultModuleServiceInterfaceController defaultModuleServiceInterfaceController() {
		return new DefaultModuleServiceInterfaceController();
	}

	@Bean
	@ConditionalOnMissingBean(BaseUserController.class)
	public DefaultUserController defaultUserController() {
		return new DefaultUserController();
	}

	@Bean
	@ConditionalOnMissingBean(BaseUserExtraOrgController.class)
	public DefaultUserExtraOrgController defaultUserExtraOrgController() {
		return new DefaultUserExtraOrgController();
	}

}
