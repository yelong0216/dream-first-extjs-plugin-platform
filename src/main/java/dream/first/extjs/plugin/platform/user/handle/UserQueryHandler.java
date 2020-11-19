package dream.first.extjs.plugin.platform.user.handle;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.github.pagehelper.PageInfo;

import dream.first.core.platform.user.model.User;

/**
 * @author PengFei
 *
 */
public interface UserQueryHandler {

	List<? extends User> queryUser(HttpServletRequest request , User sqlModel);
	
	PageInfo<? extends User> queryUser(HttpServletRequest request , User sqlModel , Integer pageNum, Integer pageSize);

	User getUser(HttpServletRequest request , User sqlModel);
	
}
