package com.example.pagerouter

import android.app.Activity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.core.view.size

class Router(private val mActivity: Activity) {
    private val mBackStack: ArrayDeque<Page> = ArrayDeque()
    private var mRootView: ViewGroup? = null
    private var lock = false

    init {
        val activity: Activity = mActivity
        if (activity is ComponentActivity) {
            activity.onBackPressedDispatcher.addCallback {
                pop()
            }
        }
    }

    fun push(page: Page) {
        if (lock) {
            return
        }
        lock = true
        // 创建布局参数并设置为MATCH_PARENT
        // 创建布局参数并设置为MATCH_PARENT
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        page.setRouter(this)

        val topPage = mBackStack.lastOrNull()
        if (topPage == null) {
            mRootView = FrameLayout(mActivity).apply {
                this.addView(page.view, params)
            }
            mActivity.setContentView(mRootView)
            lock = false
        } else {
            mRootView?.addView(page.view, params)
            ChangeHandler().performChange(
                mRootView!!,
                topPage.view,
                page.view,
                true,
                object : AnimationListener {
                    override fun onAnimationEnd() {
                        lock = false
                    }

                })
        }


        mBackStack.lastOrNull()?.onPause()
        page.onResume()

        mBackStack.addLast(page)
    }

    fun pop() {
        if (lock) {
            return
        }
        lock = true

        if (mBackStack.isEmpty()) {
            return
        }

        val topPage = mBackStack.removeLast()
        val targetPage = mBackStack.lastOrNull()

        ChangeHandler().performChange(
            mRootView!!,
            topPage.view,
            targetPage?.view,
            false,
            object : AnimationListener {
                override fun onAnimationEnd() {
                    mRootView?.removeView(topPage.view)

                    topPage.setRouter(null)
                    topPage.destroy()
                    lock = false
                }
            })

        topPage.onPause()
        targetPage?.onResume()

        if (mBackStack.size == 0) {
            mActivity.finish()
        }
    }
}