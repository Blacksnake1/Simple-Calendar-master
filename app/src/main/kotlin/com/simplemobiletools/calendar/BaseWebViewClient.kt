package camphivn.nineninebet.app1.webservice

import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import java.util.concurrent.atomic.AtomicBoolean

private const val TAG = "##BaseWebViewClient"

class BaseWebViewClient : WebViewClient() {
    var loaded: AtomicBoolean = AtomicBoolean(false)
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        return super.shouldOverrideUrlLoading(view, url)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        val result = loaded.compareAndSet(false, true)
        if (!result) {
            return
        }
        view?.let { webView ->
            Log.d(TAG, "onPageFinished: url = $url")
//            webView.loadUrl(listenerCodeDemo)
            setZone(webView)
        }

    }


    fun setZone(webView: WebView) {
        val philippinesTimeZone = "Asia/Manila"
        val javascriptCode = """
    Intl.DateTimeFormat().resolvedOptions().timeZone = '$philippinesTimeZone';
""".trimIndent()

        // 在 WebView 中执行 JavaScript 代码
        webView.evaluateJavascript(javascriptCode, null)
        Log.d(TAG, "setZone: javascriptCode = $javascriptCode")
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
    }

    override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
        return super.shouldInterceptRequest(view, url)
    }

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        return super.shouldInterceptRequest(view, request)
    }

    override fun onReceivedError(
        view: WebView?,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
        super.onReceivedError(view, errorCode, description, failingUrl)
    }

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
    }
}