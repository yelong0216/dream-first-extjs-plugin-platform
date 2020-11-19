Coe.initialize();
Co.initialize();

var pointer = '<span style="color:red;font-weight:bold">*</span>';
//默认的内边距
var defaultPadding = "0 0 5 0";

var defaultRadioInterval = 15;

var apiPrefix = "/extjs/plugin/platform";
rootPath = rootPath + apiPrefix;

var API = {
		saveSCIP : rootPath + "/scip/save",
		querySCIP : rootPath + "/scip/query",
		deleteSCIP : rootPath + "/scip/delete",
		retrieveSCIP : rootPath + "/scip/retrieve"
	};

var sCIPGrid;
var sCIPGridStore;
var sCIPForm;
var sCIPFormWindow;

Ext.onReady(function() {
		
	//============================ Model =========================
	Co.defineModel("SCIP", ["id","ipType","startIp","endIp","remark"]);
	//============================ Store =========================
	sCIPGridStore = Co.gridStore("sCIPGridStore", API.querySCIP, "SCIP", {
		autoLoad : false,
		output : "sCIPTbar",
		sorters : [{
			property : "createTime",
			direction : "desc"
		}]
	});
		
	//============================ View =========================
	var sCIPTbar = Co.toolbar("sCIPTbar", [{
			type : "+", 
			handler : addSCIP,
			showAtContextMenu : true
		},"->",{
			type : "@",
			handler : searchSCIP,
			searchField : ["startIp"],
			searchEmptyText : ["请输入起始IP..."]
		}
	]);
	
	var sCIPColumns = [
		Co.gridRowNumberer(),
		{"header" : "主键" , "dataIndex" : "id" , "width" : 100 , "hidden" : true , "sortable" : false},
		{"header" : "地址类型" , "dataIndex" : "ipType" , "width" : 100 , "hidden" : false , "sortable" : false,renderer : function(v){
			return Co.dictText("ipType",v);
		}},
		{"header" : "起始地址" , "dataIndex" : "startIp" , "width" : 120 , "hidden" : false , "sortable" : false},
		{"header" : "结束地址" , "dataIndex" : "endIp" , "width" : 120 , "hidden" : false , "sortable" : false},
		{"header" : "说明" , "dataIndex" : "remark" , "width" : 200 , "hidden" : false , "sortable" : false},
		{header : "操作", dataIndex : "", width : 100,align : "center", hidden : false,renderer : function(v,obj){
			var record = obj.record;
			var id = record.data.id;
			var operator = "";
			operator += "<span style='cursor:pointer;color: blue;' onclick='editSCIP(\""+id+"\")'>修改</span>";
			operator += "&nbsp;&nbsp;&nbsp;";
			operator += "<span style='cursor:pointer;color: blue;' onclick='deleteSCIP(\""+id+"\")'>删除</span>";
			return operator;
		}},
	];
	
	sCIPGrid = Co.grid("sCIPGrid", sCIPGridStore, sCIPColumns, sCIPTbar, null, {
		listeners : {
			itemdblclick : function(view, record) {
				editSCIP(record.data.id);
			}
		}
	});
	
	Co.load(sCIPGridStore);
	
	sCIPForm = Co.form(API.saveSCIP, [{
		layout : "column",
		border : false,
		bodyCls : "panel-background-color",
		padding : defaultPadding,
		items : [{
			columnWidth : .5,
			border : false,
			bodyCls : "panel-background-color",
			layout : "form",
			items : [Co.radioGroupDict("ipType", "model.ipType", "地址类型", "ipType",{
				columns : [100+defaultRadioInterval,100+defaultRadioInterval],
				editable : false,
				labelWidth : 100,
				allowBlank : false,
				beforeLabelTextTpl : pointer,
				blankText : "请选择地址类型！"
			})]
		}]
	},{
		layout : "column",
		border : false,
		bodyCls : "panel-background-color",
		padding : defaultPadding,
		items : [{
			columnWidth : .5,
			border : false,
			bodyCls : "panel-background-color",
			layout : "form",
			items : [{
				xtype : "textfield",
				id : "startIp",
				name : "model.startIp",
				fieldLabel : "起始地址",
				beforeLabelTextTpl : pointer,
				labelWidth : 100,
				allowBlank : false,
				blankText : "起始地址为必填项！",
				editable : true,
				readOnly : false,
				maxLength : 32,
				maxLengthText : "起始地址最多32个字！",
				enforceMaxLength : false
			}]
		},{
			columnWidth : .5,
			border : false,
			bodyCls : "panel-background-color",
			layout : "form",
			items : [{
				xtype : "textfield",
				id : "endIp",
				name : "model.endIp",
				fieldLabel : "结束地址",
				labelWidth : 100,
				allowBlank : true,
				blankText : "结束地址为必填项！",
				editable : true,
				readOnly : false,
				maxLength : 32,
				maxLengthText : "结束地址最多32个字！",
				enforceMaxLength : false
			}]
		}]
	},{
		layout : "column",
		border : false,
		bodyCls : "panel-background-color",
		padding : defaultPadding,
		items : [{
			columnWidth : 1,
			border : false,
			bodyCls : "panel-background-color",
			layout : "form",
			items : [{
				xtype : "textfield",
				id : "remark",
				name : "model.remark",
				fieldLabel : "说明",
				labelWidth : 100,
				allowBlank : true,
				editable : true,
				readOnly : false,
				maxLength : 256,
				maxLengthText : "说明最多256个字！",
				enforceMaxLength : false
			}]
		}]
	},{
		xtype : "hiddenfield",
		id : "id",
		name : "model.id"
	}]);
	
	sCIPFormWindow = Co.formWindow("新增", sCIPForm, 650, 210, "fit", {
		okHandler : saveSCIP
	});
	
	Ext.create("Ext.container.Viewport", {
		layout : "fit",
		items : sCIPGrid
	});
	//============================ Function =========================
	function addSCIP() {
		Co.resetForm(sCIPForm, true);
		Co.setValue({"ipType":"01"});
		sCIPFormWindow.setTitle("新增");
		sCIPFormWindow.show();
	}
	
	function saveSCIP() {
		Co.formSave(sCIPForm, function(form, action){
			Co.alert("保存成功！", function(){
				sCIPFormWindow.hide();
				Co.reload(sCIPGridStore);
			});
		});
	}
	
	function searchSCIP() {
		Co.load(sCIPGridStore);
	}
});

function editSCIP(modelId) {
	if(modelId){
		Co.formLoadWithoutGrid(sCIPForm, API.retrieveSCIP,{"model.id":modelId}, function(result, opts, selectedId){
			if (true === result.success) {
				sCIPFormWindow.setTitle("修改");
				sCIPFormWindow.show();
			} else {
				Co.showError(result.msg || "数据加载失败！");
			}
		});
	}
}
	
function deleteSCIP(modelId) {
	if(modelId){
		Co.confirm("确定删除吗？", function(btn){
			if (btn == "yes") {
				Co.request(API.deleteSCIP,{"deleteIds":modelId}, function(result){
					if (result.success === true) {
						Co.alert("删除成功！", function(){
							Co.reload(sCIPGridStore);
						});
					} else {
						Co.alert(result.msg);
					}
				});
			}
		});
	}
}
