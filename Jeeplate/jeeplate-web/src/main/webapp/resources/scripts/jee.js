/*
 *  e.g. form with id2 == "form:id2"
 * getWidgetVarById('form:dialogId');
 * equals to the expression   #{p:widgetVar('form:dialogId')} (Server-side eval)
 */
function getWidgetVarById(id) {
    for (var propertyName in PrimeFaces.widgets) {
        if (PrimeFaces.widgets[propertyName].id === id) {
            return PrimeFaces.widgets[propertyName];
        }
    }
};

String.prototype.format = function () {
    var str = this;
    for (var i = 0; i < arguments.length; i++) {
        var reg = new RegExp("\\{" + i + "\\}", "gm");
        str = str.replace(reg, arguments[i]);
    }
    return str;
};

var JS = {
    handleLoginRequest: function (wvid, xhr, status, args) {
        var pf = PF(wvid);
        if (args.invalid) {
            pf.jq.effect("shake", {times: 5}, 100);
            pf.show();
        } else {
            pf.show();
        }
    },
    handleRoleSwitch: function (wvid, input, role) {
        var elem = "#{0}".format(input).replace(/:/g, "\\:");
        console.log("--->"+input,role,elem);
        var pf = PF(wvid);
        var jq = $(elem);
        
        console.log(jq);
        jq.html(role);
        pf.show();
    }
};
