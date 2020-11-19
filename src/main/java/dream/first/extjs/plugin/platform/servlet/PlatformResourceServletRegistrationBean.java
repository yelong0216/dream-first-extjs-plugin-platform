package dream.first.extjs.plugin.platform.servlet;

import org.yelong.support.servlet.resource.ResourceServlet;
import org.yelong.support.servlet.resource.response.ResourceResponseHandler;
import org.yelong.support.spring.boot.servlet.resource.ResourceServletRegistrationBean;

import dream.first.extjs.plugin.platform.servlet.PlatformResourceServletRegistrationBean.PlatformResourceServlet;

public class PlatformResourceServletRegistrationBean extends ResourceServletRegistrationBean<PlatformResourceServlet> {

	public static final String urlPrefix = "/resources/extjs/plugin/platform";

	public static final String resourceRootPath = "/dream/first/extjs/plugin/resources/platform/publics/extjs/plugin/platform";

	public PlatformResourceServletRegistrationBean() {
		this(urlPrefix);
	}

	public PlatformResourceServletRegistrationBean(String urlPrefix) {
		this(urlPrefix, resourceRootPath);
	}

	public PlatformResourceServletRegistrationBean(String urlPrefix, String resourceRootPath) {
		super(urlPrefix, resourceRootPath, new PlatformResourceServlet());
	}

	public static final class PlatformResourceServlet extends ResourceServlet {

		private static final long serialVersionUID = -454745587938652439L;

		public PlatformResourceServlet() {
		}

		public PlatformResourceServlet(ResourceResponseHandler resourceResponseHandler) {
			super(resourceResponseHandler);
		}

	}

}
