package com.simplemobiletools.calendar.pro.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.simplemobiletools.calendar.pro.AppSharedPreferences
import com.simplemobiletools.calendar.pro.databinding.ActivitySplash1Binding
import com.simplemobiletools.calendar.pro.extensions.getNewEventTimestampFromCode
import com.simplemobiletools.calendar.pro.helpers.*
import org.joda.time.DateTime
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Splash1Activity : AppCompatActivity() {
    private var binding: ActivitySplash1Binding? = null
    lateinit var sharedPreference: AppSharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplash1Binding.inflate(layoutInflater)
        setContentView(binding?.root)
        initData()



    }

    private fun initData() {
        sharedPreference = AppSharedPreferences(this)
        if (!sharedPreference.getString("ip", "").isNullOrEmpty()) {
            startActivity(Intent(this, SplashSecondActivity::class.java))
            finish()
        } else {
            // Gọi GetPublicIPAddress()
            val getPublicIPAddress = GetPublicIPAddress(this)
            getPublicIPAddress.execute()
        }
    }





}
class GetPublicIPAddress(private var context: Activity) : AsyncTask<Void, Void, String>() {

    private val TAG = "GetPublicIPAddress"
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyApp", AppCompatActivity.MODE_PRIVATE)
    override fun doInBackground(vararg params: Void?): String? {
        var ipAddress: String? = null
        try {
            // Tạo kết nối tới trang web hỗ trợ lấy địa chỉ IP public
            val url = URL("https://checkip.amazonaws.com/")
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "GET"

            // Lấy dữ liệu trả về từ trang web
            val inStream = BufferedReader(InputStreamReader(con.inputStream))
            ipAddress = inStream.readLine()
            inStream.close()
        } catch (e: IOException) {
            Log.e(TAG, "errorip: " + e.message)
        }
        return ipAddress
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        // In ra địa chỉ IP public của thiết bị
        if (result != null) {
            // Lưu giá trị địa chỉ IP public vào SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putString("ip", result)
            editor.apply()
            // In ra địa chỉ IP public của thiết bị
            Log.i(TAG, "Thắng Địa chỉ IP public của thiết bị: $result")
            context.startActivity(Intent(context, SplashSecondActivity::class.java))
            context.finish()
        } else {
            Log.e(TAG, "không lấy được ip")
        }
    }
}
