package com.example.pagerouter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    val router = Router(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        router.push(Page(this))

        val customPage = object : Page(this) {
            override fun createView(): View {
                return TextView(this@MainActivity).apply {
                    this.text = "hello world";
                }
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            router.push(Page(this@MainActivity, "https://mbd.baidu.com/newspage/data/landingsuper?context=%7B%22nid%22%3A%22news_11067772285636413101%22%7D&n_type=-1&p_from=-1"))
        }, 3000)

    }

}