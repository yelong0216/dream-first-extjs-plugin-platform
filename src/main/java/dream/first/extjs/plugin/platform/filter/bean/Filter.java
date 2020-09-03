/**
 * 
 */
package dream.first.extjs.plugin.platform.filter.bean;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * @since 2.0
 */
public class Filter {

	// 字段名称
	private String fieldName;

	// 字段显示的文本
	private String fieldText;

	// 支持的操作符
	private List<String> supportOperators;

	// 字段类型： Date,String,Number,Dict
	private String fieldType;

	// 查询的字段类型：时间选择但是是字符类型的字段
	private String selectFieldType;

	// 字典类型
	private String dictType;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public List<String> getSupportOperators() {
		return supportOperators;
	}

	public void setSupportOperators(List<String> supportOperators) {
		this.supportOperators = supportOperators;
	}

	public void setSupportOperators(String... supportOperators) {
		this.supportOperators = Arrays.asList(supportOperators);
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getFieldText() {
		return fieldText;
	}

	public void setFieldText(String fieldText) {
		this.fieldText = fieldText;
	}

	public String getDictType() {
		return dictType;
	}

	public void setDictType(String dictType) {
		this.dictType = dictType;
	}

	public String getSelectFieldType() {
		if (StringUtils.isEmpty(selectFieldType)) {
			return this.fieldType;
		}
		return selectFieldType;
	}

	public void setSelectFieldType(String selectFieldType) {
		this.selectFieldType = selectFieldType;
	}

}
