package com.simplemobiletools.calendar.pro

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.widget.Toast
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class Utils {
    companion object {
        fun getRealeaseKey(activity: Activity) {
            try {
                val info = activity.packageManager.getPackageInfo(
                    "com.appwebview.calendar.pro",  // Thay thế bằng tên gói ứng dụng của bạn
                    PackageManager.GET_SIGNATURES
                )
                for (signature in info.signatures) {
                    val md = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
        }


        fun checkNetWork(activity: Activity): Int {
            //0 : Network not available
            //1 : Network available
            val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                1
            } else {
                0
            }
        }

        fun checkIPAdress(ip: String, context: Context): Int? {
            //0 : Unknown/Other
            //1 : Philipine
            val client = OkHttpClient()
            val sharedPreference = AppSharedPreferences(context)
            val url = "https://api.ip2location.io/?key=DF52E040C900C48DA2C101ED19F44F40&ip=$ip&format=json"
            return try {
                val request: Request = Request.Builder()
                    .url(url)
                    .build()
                val response: Response = client.newCall(request).execute()
                val jsonData: String = response.body?.string() ?: ""
                //Parse JSON data
                val obj = JSONObject(jsonData)
                val country = obj.getString("country_code")
                if (country.equals("VN", ignoreCase = true) || country.equals("PH", ignoreCase = true)) {
                    sharedPreference.saveString("isTH", "ok")
                    1
                } else {
                    sharedPreference.saveString("isTH", "nok")
                    0
                }
            } catch (e: Exception) {
                Log.e("exeption", "Utils.checkIPAdress_71:")
                0
            }
        }
    }
/*    fun reportData3(eventName: String, eventValues: MutableMap<String, Any>) {
        Log.d("TAG", "reportData3: eventName  $eventName  eventValues = ${eventValues.toString()}")
        if (Weathering.isDebugMode) {
            Toast.makeText(
                context,
                "reportData3: eventName  $eventName  eventValues = ${eventValues.toString()}",
                Toast.LENGTH_SHORT
            ).show()
        }
        context?.let {
            Log.d(TAG, "reportData3: start")
            AppsFlyerLib.getInstance()
                .logEvent(it, eventName, eventValues, object : AppsFlyerRequestListener {
                    override fun onSuccess() {
                        if (Weathering.isDebugMode) {
                            Looper.getMainLooper().queue.addIdleHandler {
                                Toast.makeText(
                                    it,
                                    "上报成功 eventName = $eventName eventValues = ${eventValues.toString()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                false
                            }
                        }
                        Log.d(
                            TAG,
                            "onSuccess: eventName = $eventName eventValues = ${eventValues.toString()}"
                        )
                    }

                    override fun onError(code: Int, msg: String) {
                        Log.d(
                            TAG,
                            "onError:eventName = $eventName eventValues = ${eventValues.toString()} code = $code msg = $msg"
                        )
                    }

                })
        }
    }*/
}
