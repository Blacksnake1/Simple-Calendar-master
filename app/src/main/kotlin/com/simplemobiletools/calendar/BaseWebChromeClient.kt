package com.simplemobiletools.calendar

import android.app.AlertDialog
import android.os.Message
import android.util.Log
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.simplemobiletools.listenerCodeDemo

class BaseWebChromeClient : WebChromeClient() {
    override fun onCreateWindow(
        view: WebView?,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message?
    ): Boolean {

        try {
            view?.let {
                val webview = WebView(it.context)
                val client = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view1: WebView?, url: String?): Boolean {
                        url?.let { it1 -> view.loadUrl(it1) }
                        return true
                    }
                }
                webview.webViewClient = client
                val transport = resultMsg!!.obj as WebView.WebViewTransport
                transport.webView = webview
                resultMsg.sendToTarget()
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }


        return true
    }

    override fun onJsAlert(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        AlertDialog.Builder(view?.context)
            .setTitle("JsAlert")
            .setMessage(message)
            .setPositiveButton(
                "OK"
            ) { dialog, which -> result!!.confirm() }
            .setCancelable(false)
            .show()
        return true
    }
    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        Log.d("TAG", "#onProgressChanged: newProgress = [${newProgress}]")
        if (newProgress == 100) {
            view?.loadUrl(listenerCodeDemo)
        }
        super.onProgressChanged(view, newProgress);
    }
}
