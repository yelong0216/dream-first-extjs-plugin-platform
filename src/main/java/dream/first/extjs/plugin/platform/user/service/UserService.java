/**
 * 
 */
package dream.first.extjs.plugin.platform.user.service;

import java.util.List;

import dream.first.core.platform.user.model.User;

/**
 * @since 2.0
 */
public interface UserService {

	/**
	 * 保存用户。并保存用户角色、用户附属部门信息
	 * 
	 * @param user      用户
	 * @param userRoles 用户角色id 可空
	 */
	void save(User user, List<String> userRoles);

	/**
	 * 保存用户。并保存用户角色、用户附属部门信息。这会删除原有的角色配置
	 * 
	 * @param user      用户
	 * @param userRoles 用户角色id 可空
	 */
	void modify(User user, List<String> userRoles);

	/**
	 * 保存用户。并保存用户角色、用户附属部门信息
	 * 
	 * @param user               用户
	 * @param userRoles          用户角色id 可空
	 * @param overrideSourceRole 是否覆盖原有的角色配置
	 */
	void modifyById(User user, List<String> userRoles, boolean overrideSourceRole);

	/**
	 * 修改用户密码
	 * 
	 * @param userId      用户id
	 * @param oldPassword 旧密码
	 * @param newPassword 新密码
	 * @return <tt>true</tt> 修改成功
	 * @since 1.0.5
	 */
	boolean modifyPassword(String userId, String oldPassword, String newPassword);

	/**
	 * 重置密码 重置密码后用户再次登录时需要修改密码
	 * 
	 * @param userIds     用户ids
	 * @param newPassword 新密码
	 * @return 重置的用户数量
	 */
	Integer resetPassword(String[] userIds, String newPassword);

}
