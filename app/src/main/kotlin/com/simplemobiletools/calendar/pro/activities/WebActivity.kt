package com.simplemobiletools.calendar.pro.activities

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.simplemobiletools.calendar.BaseWebChromeClient
import camphivn.nineninebet.app1.webservice.BaseWebViewClient
import com.simplemobiletools.calendar.Bridge
import com.simplemobiletools.calendar.pro.databinding.ActivityWebBinding
import kotlinx.android.synthetic.main.activity_web.lajs_webview

class WebActivity : AppCompatActivity() {
    private var binding: ActivityWebBinding? = null
    var link = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        link = intent.getStringExtra("link").toString()
        initWeb()
    }

    override fun onResume() {
        super.onResume()

    }

    //    fun reportUrlDot() {
//        val hashMap = HashMap<String, Any>(4)
//        hashMap["url"] = link
//        Util.reportData3("load_url", hashMap)
//    }
    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun initWeb() {
        lajs_webview.apply {
            webChromeClient = BaseWebChromeClient()
            webViewClient = BaseWebViewClient()

        }
        lajs_webview.settings.run {
            javaScriptEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            allowFileAccess = true
            javaScriptCanOpenWindowsAutomatically = true
            supportMultipleWindows()
            setSupportMultipleWindows(true)
            defaultTextEncodingName = "utf-8"
            domStorageEnabled = true
        }
        lajs_webview.addJavascriptInterface(Bridge(), "jsbridge")
        lajs_webview?.loadUrl(link)

        lajs_webview?.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                //Thực hiện action
            }

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                if (request.url.toString().startsWith("tg:")) {
                    try {
                        val telegramIntent = Intent(Intent.ACTION_VIEW)
                        telegramIntent.data = Uri.parse(request.url.toString())
                        startActivity(telegramIntent)
                        return true
                    } catch (e: ActivityNotFoundException) {
                        // Xử lý nếu ứng dụng Telegram không được cài đặt
                    }
                } else if (request.url.toString().startsWith("whatsapp:")) {
                    try {
                        val whatsappIntent = Intent(Intent.ACTION_VIEW)
                        whatsappIntent.data = Uri.parse(request.url.toString())
                        startActivity(whatsappIntent)
                        return true
                    } catch (e: ActivityNotFoundException) {
                        // Xử lý nếu ứng dụng Telegram không được cài đặt
                    }
                } else if (request.url.toString().startsWith("fb:")) {
                    try {
                        val fbIntent = Intent(Intent.ACTION_VIEW)
                        fbIntent.data = Uri.parse(request.url.toString())
                        startActivity(fbIntent)
                        return true
                    } catch (e: ActivityNotFoundException) {
                        // Xử lý nếu ứng dụng Telegram không được cài đặt
                    }
                }else if (request.url.toString().startsWith("galaxyface:")) {
                    try {
                        val galaxyfaceIntent = Intent(Intent.ACTION_VIEW)
                        galaxyfaceIntent.data = Uri.parse(request.url.toString())
                        startActivity(galaxyfaceIntent)
                        return true
                    } catch (e: ActivityNotFoundException) {
                        // Xử lý nếu ứng dụng Telegram không được cài đặt
                    }
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (lajs_webview != null) {
            lajs_webview?.destroy()
        }
    }
    override fun onBackPressed() {
        if (lajs_webview?.canGoBack() == true) {
            lajs_webview?.goBack()
        } else {
            super.onBackPressed()
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && lajs_webview.canGoBack()) {
            lajs_webview.goBack()
            return true
        }
        return false
    }
}
