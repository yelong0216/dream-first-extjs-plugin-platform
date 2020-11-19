Coe.initialize();
Co.initialize();

var apiPrefix = "/extjs/plugin/platform";
rootPath = rootPath + apiPrefix;

Ext.onReady(function() {
	var API = {
			saveDict : rootPath + "/dict/save",
			queryDict : rootPath + "/dict/query",
			deleteDict : rootPath + "/dict/delete",
			retrieveDict : rootPath + "/dict/retrieve",
			modifyDict : rootPath + "/dict/modify",
			copyDict : rootPath + "/dict/copyDict"
	}; 
	var pointer = '<span style="color:red;font-weight:bold">*</span>';

	//============================ Model =========================
	Co.defineModel("Dict", ["id","dictType","dictValue","dictText","dictOrder"]);
	//============================ Store =========================
	var dictGridStore = Co.gridStore("dictGridStore", API.queryDict, "Dict", {
		autoLoad : false,
		output : "tbar",
		sorters : [{
			property : "createTime",
			direction : "desc"
		},{
			property : "dictOrder",
			direction : "desc"
		}]
	});

	//============================ View =========================
	var tbar = Co.toolbar("tbar", [{
		rid : "8a9b3e14437b187001437b29b9790013",
		type : "+", 
		handler : addDict,
		showAtContextMenu : false
	},{
		rid : "8a9b3e14437b187001437b29e4a70016",
		type : "*",
		handler : deleteDict,
		showAtContextMenu : true
	},{
		rid : "8a9b3e14437b187001437b2a0d5e0019",
		type : "-",
		handler : editDict,
		showAtContextMenu : true
	},{
		xtype : "splitbutton",
		text : "复制",
		iconCls : "copy",
		handler : copyDict,
		menu : Ext.create("Ext.menu.Menu", {
			items : {
				text : "复制(指定数量)",
				iconCls : "copy",
				handler : copyDictMulti
			}
		}),
		showAtContextMenu : true
	},"->",{
		rid : "8a9b3e14437b187001437b2a47d3001c",
		type : "@",
		handler : searchDict,
		searchField : ["dictText",{
			xtype : "textfield",
			id : "search_dictType",
			name : "dictType"
		}],
		searchEmptyText : ["请输入名称...","请输入类型..."]
	}
	]);

	var columns = [
		Co.gridRowNumberer(),
		{header : "id", dataIndex : "id", width : 250, hidden : true},
		{header : "类型", dataIndex : "dictType", width : 250, hidden : false},
		{header : "值", dataIndex : "dictValue", width : 250, hidden : false},
		{header : "显示名称", dataIndex : "dictText", width : 250, hidden : false},
		{header : "顺序", dataIndex : "dictOrder", width : 250, hidden : false}
		];

	var dictGrid = Co.grid("dictGrid", dictGridStore, columns, tbar, null, {
		listeners : {
			itemdblclick : function(view, record) {
				editDict();
			}
		}
	});

	Co.load(dictGridStore);

	var dictForm = Co.form(API.saveDict, [{
		xtype : "textfield",
		id : "dictType",
		name : "model.dictType",
		fieldLabel : "类型",
		allowBlank : false,
		blankText : "类型不能为空",
		beforeLabelTextTpl : pointer,
		editable : true,
		readOnly : false,
		maxLength: 64,
		maxLengthText: "类型最多不能超过64字符",
		enforceMaxLength: true
	},{
		xtype : "textfield",
		id : "dictValue",
		name : "model.dictValue",
		fieldLabel : "值",
		allowBlank : false,
		blankText : "值不能为空",
		beforeLabelTextTpl : pointer,
		editable : true,
		readOnly : false,
		maxLength: 64,
		maxLengthText: "值最多不能超过64字符",
		enforceMaxLength: true
	},{
		xtype : "textfield",
		id : "dictText",
		name : "model.dictText",
		fieldLabel : "显示名称",
		allowBlank : false,
		blankText : "显示名称不能为空",
		beforeLabelTextTpl : pointer,
		editable : true,
		readOnly : false,
		maxLength: 64,
		maxLengthText: "显示名称最多不能超过64字符",
		enforceMaxLength: true
	},{
		xtype : "numberfield",
		id : "dictOrder",
		name : "model.dictOrder",
		fieldLabel : "顺序",
		allowBlank : true,
		editable : true,
		readOnly : false,
		hideTrigger : true,
		minValue : 0,
		maxValue : Co.maxInt
	},{
		xtype : "hiddenfield",
		id : "id",
		name : "model.id"
	}]);

	var dictFormWindow = Co.formWindow("新增", dictForm, 450, 225	, "fit", {
		okHandler : saveDict
	});

	Ext.create("Ext.container.Viewport", {
		layout : "fit",
		items : dictGrid,
		border : false,
		frame : false
	});
	//============================ Function =========================
	function addDict() {
		Co.resetForm(dictForm, true);
		Ext.getCmp("dictType").setValue(Ext.getCmp("search_dictType").getValue());
		dictFormWindow.setTitle("新增");
		dictFormWindow.show();
	}

	function saveDict() {
		Co.formSave(dictForm, function(form, action){
			Co.alert("保存成功！", function(){
				dictFormWindow.hide();
				Co.reload(dictGridStore);
			});
		});
	}

	function editDict() {
		Co.formLoad(dictForm, dictGrid, API.retrieveDict, function(result, opts, selectedId){
			dictFormWindow.setTitle("修改");
			dictFormWindow.show();
		});
	}

	function deleteDict() {
		Co.gridDelete(dictGrid, API.deleteDict, function(result){
			if (result.success === true) {
				Co.alert("删除成功！", function(){
					Co.reload(dictGridStore);
				});
			} else {
				Co.alert(result.msg);
			}
		});	
	}

	function searchDict() {
		Co.load(dictGridStore);
	}

	//=========================复制=========================

	var dictCopyForm = Co.form(null, [{
		xtype : "displayfield",
		id : "copy_dictType",
		fieldLabel : "类型",
		allowBlank : false,
		blankText : "类型不能为空",
		editable : true,
		readOnly : false,
		maxLength: 64,
		maxLengthText: "类型最多不能超过64字符",
		enforceMaxLength: true
	},{
		xtype : "numberfield",
		id : "copyNum",
		name : "copyNum",
		fieldLabel : "复制数量",
		beforeLabelTextTpl : pointer,
		allowBlank : false,
		editable : true,
		readOnly : false,
		hideTrigger : true,
		minValue : 1,
		maxValue : 99
	},{
		xtype : "hiddenfield",
		id : "modelId",
		name : "modelId"
	}]);

	var dictCopyFormWindow = Co.formWindow("复制字典", dictCopyForm, 450, 180, "fit", {
		okHandler : function(){
			var copyNum = Ext.getCmp("copyNum").getValue();
			if(Co.isEmpty(copyNum)){
				Co.showError("请录入复制的数量！");
				return;
			}
			exec_copyDict(Ext.getCmp("modelId").getValue(),copyNum);
		}
	});

	/**
	 * 复制字典。默认复制1条
	 */
	function copyDictMulti(){
		var record = Co.getGridSelectSingleRecord(dictGrid);
		if(record){
			Co.resetForm(dictCopyForm, true);
			Ext.getCmp("modelId").setValue(record.data.id);
			Ext.getCmp("copy_dictType").setValue(record.data.dictType);
			dictCopyFormWindow.show();
		}
	}

	/**
	 * 复制字典。默认复制1条
	 */
	function copyDict(){
		var record = Co.getGridSelectSingleRecord(dictGrid);
		if(record){
			exec_copyDict(record.data.id,1);
		}
	}

	/**
	 * 执行复制
	 */
	function exec_copyDict(dictId,copyNum){
		Co.showConfirm("确定要复制吗？", function(btn){
			if (btn == "ok") {
				Co.request(API.copyDict,{"modelId":dictId,"copyNum":copyNum},function(result){
					if(result.success){
						Co.alert("复制成功！",function(){
							Co.reload(dictGridStore);
							dictCopyFormWindow.hide();
						})
					} else {
						Co.showError(result.msg);
					}
				})
			}
		});

	}

});