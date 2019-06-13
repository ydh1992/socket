var BrowserUtil = {
    info: function () {
        var Browser = {name: "", version: ""};
        var ua = navigator.userAgent.toLowerCase();
        var s;
        (s = ua.match(/msie ([\d.]+)/)) ? Browser.ie = s[1] :
            (s = ua.match(/firefox\/([\d.]+)/)) ? Browser.firefox = s[1] :
                (s = ua.match(/chrome\/([\d.]+)/)) ? Browser.chrome = s[1] :
                    (s = ua.match(/opera.([\d.]+)/)) ? Browser.opera = s[1] :
                        (s = ua.match(/version\/([\d.]+).*safari/)) ? Browser.safari = s[1] : 0;
        if (Browser.ie) {//Js判断为IE浏览器
            Browser.name = "IE";
            Browser.version = Browser.ie;
            return Browser;
        }
        if (Browser.firefox) {//Js判断为火狐(firefox)浏览器
            Browser.name = "Firefox";
            Browser.version = Browser.firefox;
            return Browser;
        }
        if (Browser.chrome) {//Js判断为谷歌chrome浏览器
            Browser.name = "Chrome";
            Browser.version = Browser.chrome;
            return Browser;
        }
        if (Browser.opera) {//Js判断为opera浏览器
            Browser.name = "Opera";
            Browser.version = Browser.opera;
            return Browser;
        }
        if (Browser.safari) {//Js判断为苹果safari浏览器
            Browser.name = "Safari";
            Browser.version = Browser.safari;
            return Browser;
        }
        return Browser;
    }
}
var websocketurl = "ws://" + window.location.hostname + ":80/ws";   //ws://{ip}:{端口}/{java后端websocket配置的上下文}
var reconnectflag = false;//避免重复连接
var socket;

function createWebSocket(url, callbak) {
    try {
        if (window.WebSocket) {
            socket = new WebSocket(url);
        }
    } catch (e) {
        reconnect(url, callbak);
    }
}


function reconnect(url, callbak) {
    if (reconnectflag) return;
    reconnectflag = true;
    //没连接上会一直重连，设置延迟避免请求过多
    setTimeout(function () {
        createWebSocket(url, callbak);
        reconnectflag = false;
    }, 2000);
}

function stringToByte(str) {
    var bytes = new Array();
    var len, c;
    len = str.length;
    for (var i = 0; i < len; i++) {
        c = str.charCodeAt(i);
        if (c >= 0x010000 && c <= 0x10FFFF) {
            bytes.push(((c >> 18) & 0x07) | 0xF0);
            bytes.push(((c >> 12) & 0x3F) | 0x80);
            bytes.push(((c >> 6) & 0x3F) | 0x80);
            bytes.push((c & 0x3F) | 0x80);
        } else if (c >= 0x000800 && c <= 0x00FFFF) {
            bytes.push(((c >> 12) & 0x0F) | 0xE0);
            bytes.push(((c >> 6) & 0x3F) | 0x80);
            bytes.push((c & 0x3F) | 0x80);
        } else if (c >= 0x000080 && c <= 0x0007FF) {
            bytes.push(((c >> 6) & 0x1F) | 0xC0);
            bytes.push((c & 0x3F) | 0x80);
        } else {
            bytes.push(c & 0xFF);
        }
    }
    return bytes;
}

function byteToString(arr) {
    if (typeof arr === 'string') {
        return arr;
    }
    var str = '',
        _arr = arr;
    for (var i = 0; i < _arr.length; i++) {
        var one = _arr[i].toString(2),
            v = one.match(/^1+?(?=0)/);
        if (v && one.length == 8) {
            var bytesLength = v[0].length;
            var store = _arr[i].toString(2).slice(7 - bytesLength);
            for (var st = 1; st < bytesLength; st++) {
                store += _arr[st + i].toString(2).slice(2);
            }
            str += String.fromCharCode(parseInt(store, 2));
            i += bytesLength - 1;
        } else {
            str += String.fromCharCode(_arr[i]);
        }
    }
    return str;
}

// 消息类型
var cmd = {
    HEARTBEAT: "0",//心跳
    ONLINE: "1",//上线
    OFFLINE: "2",//下线
    ERROR: "3",//异常消息
    MSG: "4"//发送消息
};
//消息内容类型
var msgType = {
    TEXT: "0",//文本
    IMG: "1",//图片
    FILE: "2",//文件
    SYSTEM: "3"//系统
};
//发送类型
var type = {
    FRIEND: "0",//朋友
    GROUP: "1"//群组
};
// 超链接格式：a(地址)[文本]       如：a(http://www.layui.com)[layui]
// 图片格式：img[地址]            如：img[http://cdn.layui.com/xxx/a.jpg]
// 文件格式：file(地址)[文本]      如：file(http://cdn.layui.com/download/layim.zip)[layim.zip]
// 音频格式：audio[地址]          如：audio[http://cdn.layui.com/xxx/a.mp3]
// 视频格式：video[地址]          如：video[http://cdn.layui.com/xxx/a.avi]
//发送消息格式化
var sedMessage = function (sId, rId, msg, type) {
    var msgTypes = msgType.TEXT;
    //判断是什么格式的消息
    if (msg.indexOf("img[") >= 0 && msg.indexOf("]")) {
        msgTypes = msgType.IMG;
    } else if ((msg.indexOf("file(") >= 0 && msg.indexOf(")"))
        || (msg.indexOf("audio[") >= 0 && msg.indexOf("]")) || (msg.indexOf("video[") >= 0 && msg.indexOf("]"))) {
        msgTypes = msgType.FILE;
    }
    return {
        "cmd": cmd.MSG,
        "data": {"sId": sId, "rId": rId, "msg": stringToByte(msg), "msgType": msgTypes},
        "type": type
    }
}
//登录发送消息
var onopen = function (uuid,groupId, platForm, platFormVersion,ip) {
    return {"cmd": cmd.ONLINE, "uuid": uuid,"groupId":groupId,"platForm": platForm, "platFormVersion": platFormVersion,"ip":ip}
}
