/**
 * 
 */
package dream.first.extjs.plugin.platform.user.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.yelong.core.jdbc.sql.condition.combination.CombinationConditionSqlFragment;
import org.yelong.core.jdbc.sql.condition.support.Condition;
import org.yelong.ssm.service.BaseSsmModelService;

import dream.first.core.model.service.DreamFirstModelService;
import dream.first.core.platform.user.model.User;
import dream.first.core.platform.user.model.UserExtraOrg;
import dream.first.core.platform.user.model.UserExtraOrgRole;
import dream.first.core.platform.user.model.UserRole;
import dream.first.core.platform.user.service.UserCommonService;
import dream.first.extjs.plugin.platform.user.service.UserService;

/**
 * @since 2.0
 */
public class UserServiceImpl extends BaseSsmModelService implements UserService {

	@Resource
	protected final DreamFirstModelService modelService;

	@Resource
	protected final UserCommonService userCommonService;

	public UserServiceImpl(UserCommonService userCommonService) {
		this.userCommonService = Objects.requireNonNull(userCommonService);
		this.modelService = Objects.requireNonNull(userCommonService.getDreamFirstModelService());
	}

	private static final String DEFAULT_PASSWORD = "95D98DDFECDB70676F59C71FEB1555A6";// q1w2e3r$

	@Override
	public void save(User user, List<String> userRoles) {
		if (StringUtils.isEmpty(user.getRealName())) {
			// user.setNameCode(PinYinUtils.getPinYinHeadChar(user.getRealName()));
		}
		user.setInitState("");
		user.setPassword(DEFAULT_PASSWORD);
		user.setPwdUpdateTime(new Date());
		user.setPwdSign(userCommonService.getPwdSign(user.getPassword()));
		modelService.saveSelective(user);
		// 创建用户角色关系
		if (CollectionUtils.isNotEmpty(userRoles)) {
			for (String roleId : userRoles) {
				UserRole userRole = new UserRole();
				userRole.setRoleId(roleId);
				userRole.setUserId(user.getId());
				modelService.saveSelective(userRole);
			}
		}
	}

	@Override
	public void modify(User user, List<String> userRoles) {
		modifyById(user, userRoles, true);
	}

	@Override
	public void modifyById(User user, List<String> userRoles, boolean overrideSourceRole) {
		modelService.modifySelectiveById(user);
		// 删除之前的关系
		if (overrideSourceRole) {
			CombinationConditionSqlFragment combinationCondition = createCombinationSqlCondition(modelService);
			combinationCondition.and("userId", "=", user.getId());
			modelService.removeByCondition(UserRole.class, combinationCondition);
			modelService.removeByCondition(UserExtraOrg.class, combinationCondition);
			modelService.removeByCondition(UserExtraOrgRole.class, combinationCondition);
		}
		// 创建用户角色关系
		if (CollectionUtils.isNotEmpty(userRoles)) {
			for (String roleId : userRoles) {
				UserRole userRole = new UserRole();
				userRole.setRoleId(roleId);
				userRole.setUserId(user.getId());
				modelService.saveSelective(userRole);
			}
		}
	}

	@Override
	public boolean modifyPassword(String userId, String oldPassword, String newPassword) {
		User user = new User();
		user.setPassword(newPassword);
		user.setPwdSign(userCommonService.getPasswordSign(newPassword));
		user.setInitState("02");
		user.setPwdUpdateTime(new Date());
		User sqlModel = new User();
		sqlModel.setId(userId);
		sqlModel.setPassword(oldPassword);
		return modelService.modifySelectiveBySqlModel(user, sqlModel) > 0;
	}

	@Override
	public Integer resetPassword(String[] userIds, String newPassword) {
		User sqlModel = new User();
		sqlModel.addCondition(new Condition("id", "in", userIds));
		User user = new User();
		user.setPassword(newPassword);
		user.setInitState("01");
		user.setPwdSign(userCommonService.getPasswordSign(newPassword));
		return modelService.modifySelectiveBySqlModel(user, sqlModel);
	}

}
