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