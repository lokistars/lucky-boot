requirejs.config({
    /*baseUrl: (function () {
        var result = document.location.pathname;
        return result;
    })()+'js/',*/
    baseUrl: '../', // (../上一级目录 ./当前目录 /根目录) 默认是main.js所在的文件夹为基准。
    paths: {
        "jquery": 'libs/jquery-3.5.1',  //起个别名
        "axios": 'libs/axios.min',
        "css": 'libs/css.min',
        "tql": 'libs/html.min',
        "tool": 'js/tool',
        "util": 'js/util',
        "layer": 'libs/layer',
        "bootstrap": 'libs/dist/js/bootstrap',
        "bootstrap-table": 'libs/dist/js/bootstrap-table',
        "table": 'libs/dist/js/bootstrap-table-zh-CN.min',
    },
    //如果没用define(...) 定义模块，比如jquery 就需要shim来定义
    shim: {
        "util": {  //Uttoolil 模块名称一致
            deps: ['tool'], //表示当前模块的依赖模块
            //如果Util.js暴露了多个全局变量，那么exports可以指定其中任何的一个，作为模块的返回结果。不过一般的框架，都只会使用1个全局变量，这样冲突的可能性会减少
            exports: 'DateUtils' //main.js中exports的值，一定要与Util.js中暴露出的全局变量名称一致或者函数名一致
            //如果要同时暴露多个变量，要用init函数。exports和init如果同时存在，会忽略exports。
            //init: function() {return {DateUtils:DateUtils,fn:fn}}
        },
        "bootstrap": {
            deps: ['css!libs/dist/css/bootstrap']
        },
        "table": {
            deps: [
                'bootstrap-table',
                'css!libs/dist/css/bootstrap-table'
            ]
        },
        "layer": {deps: ['css!libs/dist/css/layer.css']},

    }
});
require(['jquery','axios'], function ($,axios) {
    require(["tool", "bootstrap", 'layer'], function (tool) {
        $("#abc").click(function () {
            tool.str();
        })
        //console.dir(document.location.pathname);
    });
})

