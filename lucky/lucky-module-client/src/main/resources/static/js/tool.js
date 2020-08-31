define(['tql!thymeleaf/main.html', 'table'], function (main) {  /*define 定义一个模块 可以 return 一个函数或者数组 对象*/
    //$(table).bootstrapTable('refresh');  刷新表格
    //$(table).bootstrapTable('getSelections'); 获取选中的行
    function f() {
        var hash = [];
        var columns = [
            {
                checkbox: true,
                visible: true                  //是否显示复选框
            }, {
                field: 'id',
                title: 'Item ID',
                valign: 'middle',
                align: 'center',
                width: '10%',
            }, {
                field: 'name',
                title: 'Item Name',
                align: 'center',
                valign: 'middle',
                width: '10%',
            }, {
                field: 'price',
                title: 'Item Price',
                align: 'center',
                valign: 'middle',
                width: '10%',
            }, {
                field: 'weather',
                title: '列1',
                align: 'center',
                valign: 'middle',
                width: '10%',
            }, {
                field: 'wind',
                title: '列2',
                align: 'center',
                valign: 'middle',
                width: '10%',
            }, {
                field: 'temp',
                title: '列3',
                align: 'center',
                valign: 'middle',
                width: '10%',
            }, {
                field: 'status',
                title: '状态',
                align: 'center',
                valign: 'middle',
                width: '10%',
                formatter: function (value, row, index) {
                    if (value != null && value != "") {
                        var val = value.split(",");
                        var map = new Map();
                        map.set("2", "运营");
                        map.set("4", "Ro");
                        map.set("6", "合规");
                        map.set("8", "运营驳回");
                        map.set("10", "合规驳回");
                        var i = 0;
                        val.forEach(str => {
                            hash[i] = map.get(str);
                            i++;
                        })
                        return hash;
                    }
                    return '';
                },
                cellStyle: function (value, row, index) {   //单元格样式
                    return {
                        css: {
                            "white-space": 'nowrap',  //文本不进行换行
                            "text-overflow": 'ellipsis',
                            "overflow": 'hidden',
                            "max-width":"50px"
                        }
                    }
                }
            }];
        $("#table").bootstrapTable({
            url: "../json/data.json",
            method: 'GET',
            dataType: "json",
            toolbar: '#toolbar',              //工具按钮用哪个容器
            cache: false,                       //是否使用缓存，默认为true，false 不缓存
            striped: true,                      //是否显示行间隔色
            pagination: true,                   //是否显示分页（*）
            sortable: true,                     //是否启用排序
            sortOrder: "asc",                   //排序方式
            sidePagination: "client",           //分页方式：client客户端分页，server服务端分页（*）
            pageNumber: 1,                      //初始化加载第一页，默认第一页,并记录
            pageSize: 5,                        //每页的记录行数（*）
            pageList: [5, 10, 15],              //可供选择的每页的行数（*）
            strictSearch: false,
            search: false,                      //是否显示表格搜索
            showColumns: false,                  //是否显示所有的列（选择显示的列）
            showRefresh: false,                  //是否显示刷新按钮
            minimumCountColumns: 2,             //最少允许的列数
            clickToSelect: true,                //是否启用点击选中行
            //height: 500,                      //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
            uniqueId: "id",                     //每一行的唯一标识，一般为主键列
            showToggle: false,                   //是否显示详细视图和列表视图的切换按钮
            cardView: false,                    //是否显示详细视图
            detailView: false,                  //是否显示父子表
            queryParamsType: "limit",
            //得到查询的参数
            queryParams: function (params) {
                //这里的键的名字和控制器的变量名必须一致，这边改动，控制器也需要改成一样的
                var temp = {
                    rows: params.limit,                         //页面大小
                    page: (params.offset / params.limit) + 1,   //页码 服务端分页才会显示
                    sort: params.sort,      //排序列名
                    sortOrder: params.order //排位命令（desc，asc）
                };
                return $.extend(temp, $("#search-form").serialize());
            },
            columns: columns,
            onLoadSuccess: function () {
                $('.bootstrap-table tr td').each(function () {
                    $(this).attr("title", $(this).text());
                    $(this).css("cursor", 'pointer');
                });
            },
            onLoadError: function () {
                layer.msg("数据加载失败！");
            },
            onDblClickRow: function (row, $element) {
                var id = row.id;
                layer.msg(id);
            },
        });
        //按钮
        initEvents();
    }
    function initEvents() {
        //查询
        $("#btn-search").click(function () {
            $("table.table").bootstrapTable('refreshOptions', {pageNumber: 1});
        });
    }
    var str = function () {
        var context = {};
        var html = '';
        html += '<div class="form-inline">';
        html += '<form id="search-form" onsubmit="return false;">';
        html += '<div class="form-group">';
        html +='<button type="button" class="my-btn" id="btn-search">';
        html +='<span class="glyphicon glyphicon-search position-left" aria-hidden="true"></span>查询';
        html +='</button>';
        html += '</div></form></div>';
        html += '<table id="table"></table>';
        context.html = html;
        layer.open({
            type: 1,
            title: '很多时候，我们想最大化看，比如像这个页面。',
            shadeClose: true,
            shade: false,
            layerMore: true,
            maxmin: true, //开启最大化最小化按钮
            area: ['893px', '600px'],
            content: main(context),
            id: 'idlayer'
        });
        f();
    }
    return {"str": str};
});