package com.simplemobiletools.calendar.pro.activities

import android.annotation.SuppressLint
import android.content.Intent
import com.simplemobiletools.calendar.pro.helpers.*
import com.simplemobiletools.commons.activities.BaseSplashActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseSplashActivity() {

    override fun initActivity() {
        Intent(this, Splash1Activity::class.java).apply {
            putExtra(DAY_CODE, intent.getStringExtra(DAY_CODE))
            putExtra(VIEW_TO_OPEN, intent.getIntExtra(VIEW_TO_OPEN, LAST_VIEW))
            startActivity(this)
        }
        finish()
    }
}
