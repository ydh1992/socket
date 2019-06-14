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
    return JSON.stringify({
        cmd: cmd.MSG,
        data: {sId: sId, rId: rId, msg: msg, msgType: msgTypes},
        type: type
    })
}
//登录发送消息
var onopen = function (uuid,groupId,ip) {
    return JSON.stringify({cmd: cmd.ONLINE,uuid: uuid,groupId:groupId,ip:ip})
}
