/**
 * 
 */
package dream.first.extjs.plugin.platform.dict.controller;

import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yelong.commons.lang.Integers;
import org.yelong.commons.lang.Strings;

import dream.first.core.platform.dict.manage.CacheableDictManager;
import dream.first.core.platform.dict.manage.DictManager;
import dream.first.core.platform.dict.model.Dict;
import dream.first.extjs.controller.BaseExtJSCrudModelController;
import dream.first.extjs.support.msg.JsonMsg;

/**
 * 基础的字典控制器
 * 
 * @since 2.0
 */
@RequestMapping("dict")
public abstract class BaseDictController<M extends Dict> extends BaseExtJSCrudModelController<M> {

	@Resource
	protected DictManager dictManager;

	@RequestMapping("index")
	public String index() {
		return "platform/dict/dictManage.jsp";
	}

	/**
	 * 复制字典
	 */
	@ResponseBody
	@RequestMapping("copyDict")
	public String copyDict() {
		String modelId = getParameter("modelId");
		Strings.requireNonBlank("必填参数缺失：modelId");
		Dict dict = modelService.findById(Dict.class, modelId);
		Objects.requireNonNull("不存在的字典！");
		Integer copyNum = getParameterInteger("copyNum", 1);
		if (copyNum < 1) {
			throw new IllegalArgumentException("复制数量不能小于1个");
		}
		if (copyNum == 1) {
			modelService.saveSelective(dict);
		} else {
			final Integer dictOrder = modelService.getBaseDataBaseOperation()
					.selectSingleObject("select max(dictOrder) from CO_DICT where dictType = ?", dict.getDictType());
			// 如果字典值为01,02依次递增，也会默认进行设置
			final String dictValue = modelService.getBaseDataBaseOperation()
					.selectSingleObject("select max(dictValue) from CO_DICT where dictType = ?", dict.getDictType());

			modelService.doOperation(() -> {
				int final_dictOrder = dictOrder == null ? 1 : dictOrder;
				Integer dictValueInt = Integers.valueOf(dictValue);
				for (int i = 0; i < copyNum; i++) {
					dict.setDictOrder(++final_dictOrder);
					if (null != dictValueInt) {
						dict.setDictValue(String.format("%02d", ++dictValueInt));
					}
					modelService.saveSelective(dict);
				}
			});
		}
		return toJson(new JsonMsg(true));
	}

	@Override
	protected void beforeQuery(M model) throws Exception {
		model.addConditionOperator("dictType", "like");
		model.addConditionOperator("dictText", "like");
	}

	@Override
	protected void afterDelete(String deleteIds) throws Exception {
		clearDictCache();
	}

	@Override
	protected void afterModify(M model) throws Exception {
		clearDictCache();
	}

	@Override
	protected void afterSave(M model) throws Exception {
		clearDictCache();
	}

	protected void clearDictCache() {
		if (dictManager instanceof CacheableDictManager) {
			((CacheableDictManager) dictManager).clearCache();
		}
	}

}
