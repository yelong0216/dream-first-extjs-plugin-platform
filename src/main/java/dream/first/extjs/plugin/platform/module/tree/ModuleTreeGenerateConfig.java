package dream.first.extjs.plugin.platform.module.tree;

public class ModuleTreeGenerateConfig {

	protected String parentModuleNo;

	protected boolean showRoot;

	protected boolean showOpModule;

	protected boolean showCheckbox;

	protected boolean showHiddenModule;

	protected boolean autoExpand;

	protected boolean recursion;

	protected boolean isAuth;

	protected String selectedModuleNo;

	public String getParentModuleNo() {
		return parentModuleNo;
	}

	public void setParentModuleNo(String parentModuleNo) {
		this.parentModuleNo = parentModuleNo;
	}

	public boolean isShowRoot() {
		return showRoot;
	}

	public void setShowRoot(boolean showRoot) {
		this.showRoot = showRoot;
	}

	public boolean isShowOpModule() {
		return showOpModule;
	}

	public void setShowOpModule(boolean showOpModule) {
		this.showOpModule = showOpModule;
	}

	public boolean isShowCheckbox() {
		return showCheckbox;
	}

	public void setShowCheckbox(boolean showCheckbox) {
		this.showCheckbox = showCheckbox;
	}

	public boolean isShowHiddenModule() {
		return showHiddenModule;
	}

	public void setShowHiddenModule(boolean showHiddenModule) {
		this.showHiddenModule = showHiddenModule;
	}

	public boolean isAutoExpand() {
		return autoExpand;
	}

	public void setAutoExpand(boolean autoExpand) {
		this.autoExpand = autoExpand;
	}

	public boolean isRecursion() {
		return recursion;
	}

	public void setRecursion(boolean recursion) {
		this.recursion = recursion;
	}

	public boolean isAuth() {
		return isAuth;
	}

	public void setAuth(boolean isAuth) {
		this.isAuth = isAuth;
	}

	public String getSelectedModuleNo() {
		return selectedModuleNo;
	}

	public void setSelectedModuleNo(String selectedModuleNo) {
		this.selectedModuleNo = selectedModuleNo;
	}

}
