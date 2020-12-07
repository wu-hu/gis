package com.jh.utils.ajax;

import com.alibaba.fastjson.JSONObject;
import com.sun.istack.internal.NotNull;

public class AjaxResult {
    @NotNull
    public static String success() {
        return "{retcode: 0}";
    }

    @NotNull
    public static String error(String msg) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("retcode", 1);
        jsonObject.put("msg", msg);
        return jsonObject.toJSONString();
    }

    @NotNull
    public static String error(String msg, int errorcode) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("retcode", errorcode);
        jsonObject.put("msg", msg);
        return jsonObject.toJSONString();
    }
}
