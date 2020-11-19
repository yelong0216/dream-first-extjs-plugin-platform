package dream.first.extjs.plugin.platform.scip.controller;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yelong.commons.lang.Strings;
import org.yelong.support.servlet.resource.response.ResourceResponseException;
import org.yelong.support.spring.mvc.HandlerResponseWay;
import org.yelong.support.spring.mvc.ResponseWay;
import org.yelong.tools.net.ipv4.Ipv4;
import org.yelong.tools.net.ipv4.Ipv4Utils;

import dream.first.core.platform.scip.model.SCIP;
import dream.first.extjs.base.controller.DFBaseExtJSCrudModelController;
import dream.first.extjs.base.msg.DFEJsonMsg;
import dream.first.extjs.plugin.platform.ExtJSPluginPlatform;

@RequestMapping({ "scip", "extjs/plugin/platform/scip" })
public abstract class BaseSCIPController<M extends SCIP> extends DFBaseExtJSCrudModelController<M> {

	@ResponseBody
	@RequestMapping("index")
	@ResponseWay(HandlerResponseWay.MODEL_AND_VIEW)
	public void index() throws ResourceResponseException, IOException {
		responseHtml(ExtJSPluginPlatform.RESOURCE_PRIVATES_PACKAGE,
				ExtJSPluginPlatform.RESOURCE_PREFIX + "/html/scip/scipManage.html");
	}

	@Override
	public void beforeQuery(SCIP model) {
		model.addConditionOperator("startIp", "LIKE");
	}

	@Override
	public boolean validateModel(SCIP model, DFEJsonMsg msg) {
		String startIp = model.getStartIp();
		Strings.requireNonBlank(startIp, "起始IP不能为空！");
		boolean lastStar = startIp.endsWith("*");
		if (lastStar ? !Ipv4Utils.isValidIpv4AddrLastStar(startIp) : !Ipv4Utils.isValidIpv4Addr(startIp)) {
			msg.setMsg(startIp + "不是标准的IPV4格式！");
			return false;
		}
		String endIp = model.getEndIp();
		if (StringUtils.isNotBlank(endIp)) {
			if (!Ipv4Utils.isValidIpv4Addr(endIp)) {
				msg.setMsg(endIp + "不是标准的IPV4格式！");
				return false;
			}
			if (!Ipv4Utils.validateFirstThreeSection(startIp, endIp)) {
				msg.setMsg("起始IP与结束IP的前三个段必须相同！");
				return false;
			}
			Ipv4 startIpv4 = new Ipv4(startIp);
			Ipv4 endIpv4 = new Ipv4(endIp);
			if (endIpv4.getFourSection() < startIpv4.getFourSection()) {
				msg.setMsg("结束IP必须大于起始IP！");
				return false;
			}
		}
		return true;
	}

}
