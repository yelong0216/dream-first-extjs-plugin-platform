/**
 * 
 */
package dream.first.extjs.plugin.platform.role.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import dream.first.core.platform.role.model.RoleDataRight;
import dream.first.extjs.controller.BaseExtJSCrudModelController;

/**
 * 数据角色管理器
 * 
 * @since 2.0
 */
@RequestMapping(value = "roleDataRight")
public abstract class BaseRoleDataRightController<M extends RoleDataRight> extends BaseExtJSCrudModelController<M> {

}
