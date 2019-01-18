/**
 * 数组合并转换为map
 * @param form
 * @param form2
 */
function getFormData(form, form2) {
    // var unindexed_array = form;
    var indexed_array = {};

    $.map(form, function (n, i) {
        indexed_array[n['name']] = n['value'];
    });
    $.map(form2, function (n, i) {
        indexed_array[n['name']] = n['value'];
    });

    return indexed_array;
}

/**
 * 字符串转数组
 * @param s
 * @returns {any[]}
 */
function getArray(s) {
    var split = s.split(",");
    var arr = new Array();
    for (var i = 0; i < split.length; i++) {
        var sp = jQuery.parseJSON("{" + split[i] + "}");
        // arr.push(sp);
        var s2 = split[i].split(":");
        var s3 = '{"name":' + s2[0] + ',"value":' + s2[1] + '}';
        var s4 = jQuery.parseJSON(s3);
        arr.push(s4);

    }
    return arr;

}

function getFormJsonData(form) {
    var unindexed_array = form;
    var indexed_array = {};
    $.map(unindexed_array, function (n, i) {
        indexed_array[n['name']] = n['value'];
    });
    var s = JSON.stringify(indexed_array);
    var param = '{"paramMap": ';
    param += s;
    param += '}';
    return param;
}


function autofill1(data) {
    console.log(data)
    $.each(data, function (k, v) {
        var selector = '[name="' + k + '"]';
        elt = $('body').find(selector);
        if (elt.length == 1) {
            $(elt).val(v)
        }
        if (elt.attr("type") == "radio") {
            $(selector + '[value="' + v + '"]').attr("checked", true)
        }

        if (elt.attr("type") == "checkbox") {

        }
        if (elt.attr("type") == "select") {

            $(elt).val(v)
        }
        form.render();


    })
}

function autofill(data) {
    $.each(data, function (k, v) {
        var selector = '[name="' + k + '"]';
        elt = $('body').find(selector);
        if (elt.length == 1) {
            if (elt.attr("type") == "select") {
                $(elt).val(v)
            } else {
                $(elt).html(v)
            }
        }
        if (elt.attr("type") == "radio") {
            $(selector + '[value="' + v + '"]').attr("checked", true)
        }

        if (elt.attr("type") == "checkbox") {

        }
        if (elt.attr("type") == "text") {

        }
    })
}


/*
 * jquery 初始化form插件，传入一个json对象，为form赋值
 * version: 1.0.0-2013.06.24
 * @requires jQuery v1.5 or later
 * Copyright (c) 2013
 * note:  1、此方法能赋值一般所有表单，但考虑到checkbox的赋值难度，以及表单中很少用checkbox，这里不对checkbox赋值
 *		  2、此插件现在只接收json赋值，不考虑到其他的来源数据
 *		  3、对于特殊的textarea，比如CKEditor,kindeditor...，他们的赋值有提供不同的自带方法，这里不做统一，如果项目中有用到，不能正确赋值，请单独赋值
 */
(function ($) {
    $.fn.extend({
        initForm: function (options) {
            //默认参数
            var defaults = {
                jsonValue: "",
                isDebug: false	//是否需要调试，这个用于开发阶段，发布阶段请将设置为false，默认为false,true将会把name value打印出来
            }
            //设置参数
            var setting = $.extend({}, defaults, options);
            var form = this;
            // console.log(form.html());
            // console.log("--------------------------------------------------------");
            jsonValue = setting.jsonValue;
            //如果传入的json字符串，将转为json对象
            if ($.type(setting.jsonValue) === "string") {
                jsonValue = $.parseJSON(jsonValue);
            }
            //如果传入的json对象为空，则不做任何操作
            if (!$.isEmptyObject(jsonValue)) {
                var debugInfo = "";
                $.each(jsonValue, function (key, value) {
                    //是否开启调试，开启将会把name value打印出来
                    if (setting.isDebug) {
                        // alert("name:"+key+"; value:"+value);
                        debugInfo += "name:" + key + "; value:" + value + " || ";
                    }
                    var formField = form.find("[name='" + key + "']");
                    if ($.type(formField[0]) === "undefined") {
                        if (setting.isDebug) {
                            alert("can not find name:[" + key + "] in form!!!");	//没找到指定name的表单
                        }
                    } else {
                        var fieldTagName = formField[0].tagName.toLowerCase();
                        if (fieldTagName == "input") {
                            if (formField.attr("type") == "radio") {
                                $("input:radio[name='" + key + "'][value='" + value + "']").attr("checked", "checked");
                            } else {
                                formField.val(value);
                            }
                        } else if (fieldTagName == "select") {
                            //do something special
                            formField.val(value);
                        } else if (fieldTagName == "textarea") {
                            //do something special
                            formField.val(value);
                        } else {
                            formField.val(value);
                        }
                    }
                })
                if (setting.isDebug) {
                    alert(debugInfo);
                }
            }
            return form;	//返回对象，提供链式操作
        }
    });
})(jQuery);


(function ($) {
    $.fn.extend({
        setUnEditable: function () {
            var form = this;
            // console.log(form.html());
            // console.log("--------------------------------------------------------");
            var inputField = form.find("input").attr("readOnly", "true");
            form.find("textarea").attr('readonly', true);
            form.find(':radio').attr('disabled', true);
            form.find(':checkbox').attr('disabled', true);
            form.find(':button').attr('disabled', true);
            form.find('a').removeAttr('onclick');
            form.find('select').attr('disabled', true);
            // inputField.attr("readOnly","true");
            return form;	//返回对象，提供链式操作
        }
    });
})(jQuery);

