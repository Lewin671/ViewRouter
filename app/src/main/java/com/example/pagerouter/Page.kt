package com.example.pagerouter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient


open class Page(
    private val mContext: Activity,
    private val mUrl: String = "https://www.baidu.com/"
) {
    val view by lazy {
        createView().apply {
            this.id = viewId
        }
    }

    val viewId: Int by lazy {
        View.generateViewId()
    }

    private var mRouter: Router? = null

    fun setRouter(router: Router?) {
        mRouter = router
    }

    @SuppressLint("SetJavaScriptEnabled")
    open fun createView(): View {
        return WebView(mContext).apply {
            this.settings.javaScriptEnabled = true
            mUrl.let { this.loadUrl(it) }
            this.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    if (request?.hasGesture() == true) {
                        mRouter?.let { router ->
                            router.push(
                                Page(
                                    mContext,
                                    request.url.toString()
                                )
                            )
                            return true
                        }
                    }

                    return false
                }
            }
        }
    }


    fun onResume() {
        (view as? WebView)?.onResume()
    }

    fun onPause() {
        (view as? WebView)?.onPause()
    }

    fun destroy() {
        (view as? WebView)?.destroy()
    }

}