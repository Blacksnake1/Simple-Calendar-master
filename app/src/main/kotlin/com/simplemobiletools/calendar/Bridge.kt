package com.simplemobiletools.calendar

import android.util.Log
import android.webkit.JavascriptInterface
import com.bumptech.glide.util.Util

class Bridge {

    @JavascriptInterface
    fun onEventReceived(type: String, data: String) {
        // 处理从 JavaScript 代码发送的事件数据
        Log.d("TAG", "onEventReceived: type = $type data = $data")
//        Util.reportData3(type, mutableMapOf("data" to data))
//        if (Weathering.isDebugMode) {
//            try {
//                Toast.makeText(Weathering.context, "接收到数据$type $data", Toast.LENGTH_SHORT).show()
//            } catch (t:Throwable) {
//                t.printStackTrace()
//            }
//        }

    }

/*    @JavascriptInterface
    fun getSdkData(): String? {
        val AppsFlyer_ID =
            App.context?.let { AppsFlyerLib.getInstance().getAppsFlyerUID(it) }
        val jsonData: JSONObject = JSON.parseObject("{}")
        jsonData.put("af_id", AppsFlyer_ID)
        jsonData.put("af_dev_key", App.token)
        jsonData.put("af_bundleIdentifier", App.packegae)
        val result: String = JSON.toJSONString(jsonData)
        Log.e(TAG, "getSdkData: $result")
        if (App.isDebugMode) {
            Toast.makeText(App.context, "getSdkData result = $result", Toast.LENGTH_SHORT).show()
        }
        return result
    }*/


}
