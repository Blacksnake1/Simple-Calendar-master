package com.simplemobiletools.calendar.pro.activities

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.simplemobiletools.calendar.pro.R
import com.simplemobiletools.calendar.pro.extensions.addDayEvents
import com.simplemobiletools.calendar.pro.extensions.config
import com.simplemobiletools.calendar.pro.extensions.isWeekendIndex
import com.simplemobiletools.calendar.pro.helpers.MonthlyCalendarImpl
import com.simplemobiletools.calendar.pro.helpers.MyWidgetMonthlyProvider
import com.simplemobiletools.calendar.pro.interfaces.MonthlyCalendar
import com.simplemobiletools.calendar.pro.models.DayMonthly
import com.simplemobiletools.commons.dialogs.ColorPickerDialog
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.IS_CUSTOMIZING_COLORS
import com.simplemobiletools.commons.helpers.LOWER_ALPHA
import kotlinx.android.synthetic.main.day_monthly_number_view.view.day_monthly_number_background
import kotlinx.android.synthetic.main.day_monthly_number_view.view.day_monthly_number_id
import kotlinx.android.synthetic.main.first_row.week_num
import kotlinx.android.synthetic.main.top_navigation.top_left_arrow
import kotlinx.android.synthetic.main.top_navigation.top_right_arrow
import kotlinx.android.synthetic.main.top_navigation.top_value
import kotlinx.android.synthetic.main.widget_config_monthly.*
import org.joda.time.DateTime

class WidgetMonthlyConfigureActivity : SimpleActivity(), MonthlyCalendar {
    private var mDays: List<DayMonthly>? = null
    private var dayLabelHeight = 0

    private var mBgAlpha = 0f
    private var mWidgetId = 0
    private var mBgColorWithoutTransparency = 0
    private var mBgColor = 0
    private var mTextColor = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        useDynamicTheme = false
        super.onCreate(savedInstanceState)
        setResult(Activity.RESULT_CANCELED)
        setContentView(R.layout.widget_config_monthly)
        initVariables()

        val isCustomizingColors = intent.extras?.getBoolean(IS_CUSTOMIZING_COLORS) ?: false
        mWidgetId = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (mWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !isCustomizingColors) {
            finish()
        }

        val primaryColor = getProperPrimaryColor()
        config_save.setOnClickListener { saveConfig() }
        config_bg_color.setOnClickListener { pickBackgroundColor() }
        config_text_color.setOnClickListener { pickTextColor() }
        config_bg_seekbar.setColors(mTextColor, primaryColor, primaryColor)
    }

    private fun initVariables() {
        mBgColor = config.widgetBgColor
        mBgAlpha = Color.alpha(mBgColor) / 255f

        mBgColorWithoutTransparency = Color.rgb(Color.red(mBgColor), Color.green(mBgColor), Color.blue(mBgColor))
        config_bg_seekbar.apply {
            progress = (mBgAlpha * 100).toInt()

            onSeekBarChangeListener { progress ->
                mBgAlpha = progress / 100f
                updateBackgroundColor()
            }
        }
        updateBackgroundColor()

        mTextColor = config.widgetTextColor
        if (mTextColor == resources.getColor(R.color.default_widget_text_color) && config.isUsingSystemTheme) {
            mTextColor = resources.getColor(R.color.you_primary_color, theme)
        }

        updateTextColor()

        MonthlyCalendarImpl(this, this).updateMonthlyCalendar(DateTime().withDayOfMonth(1))
    }

    private fun saveConfig() {
        storeWidgetColors()
        requestWidgetUpdate()

        Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId)
            setResult(Activity.RESULT_OK, this)
        }
        finish()
    }

    private fun storeWidgetColors() {
        config.apply {
            widgetBgColor = mBgColor
            widgetTextColor = mTextColor
        }
    }

    private fun pickBackgroundColor() {
        ColorPickerDialog(this, mBgColorWithoutTransparency) { wasPositivePressed, color ->
            if (wasPositivePressed) {
                mBgColorWithoutTransparency = color
                updateBackgroundColor()
            }
        }
    }

    private fun pickTextColor() {
        ColorPickerDialog(this, mTextColor) { wasPositivePressed, color ->
            if (wasPositivePressed) {
                mTextColor = color
                updateTextColor()
                updateDays()
            }
        }
    }

    private fun requestWidgetUpdate() {
        Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this, MyWidgetMonthlyProvider::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(mWidgetId))
            sendBroadcast(this)
        }
    }

    private fun updateTextColor() {
        top_left_arrow.applyColorFilter(mTextColor)
        top_right_arrow.applyColorFilter(mTextColor)
        top_value.setTextColor(mTextColor)
        config_text_color.setFillWithStroke(mTextColor, mTextColor)
        updateLabels()
        config_save.setTextColor(getProperPrimaryColor().getContrastColor())
    }

    private fun updateBackgroundColor() {
        mBgColor = mBgColorWithoutTransparency.adjustAlpha(mBgAlpha)
        config_calendar.background.applyColorFilter(mBgColor)
        config_bg_color.setFillWithStroke(mBgColor, mBgColor)
        config_save.backgroundTintList = ColorStateList.valueOf(getProperPrimaryColor())
    }

    private fun updateDays() {
        val len = mDays!!.size

        if (config.showWeekNumbers) {
            week_num.setTextColor(mTextColor)
            week_num.beVisible()

            for (i in 0..5) {
                findViewById<TextView>(resources.getIdentifier("week_num_$i", "id", packageName)).apply {
                    text = "${mDays!![i * 7 + 3].weekOfYear}:"
                    setTextColor(mTextColor)
                    beVisible()
                }
            }
        }

        val dividerMargin = resources.displayMetrics.density.toInt()
        for (i in 0 until len) {
            findViewById<LinearLayout>(resources.getIdentifier("day_$i", "id", packageName)).apply {
                val day = mDays!![i]
                removeAllViews()

                val dayTextColor = if (config.highlightWeekends && day.isWeekend) {
                    config.highlightWeekendsColor
                } else {
                    mTextColor
                }

                addDayNumber(dayTextColor, day, this, dayLabelHeight) { dayLabelHeight = it }
                context.addDayEvents(day, this, resources, dividerMargin)
            }
        }
    }

    private fun addDayNumber(rawTextColor: Int, day: DayMonthly, linearLayout: LinearLayout, dayLabelHeight: Int, callback: (Int) -> Unit) {
        var textColor = rawTextColor
        if (!day.isThisMonth) {
            textColor = textColor.adjustAlpha(LOWER_ALPHA)
        }

        (View.inflate(this, R.layout.day_monthly_number_view, null) as RelativeLayout).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            linearLayout.addView(this)

            day_monthly_number_background.beVisibleIf(day.isToday)
            day_monthly_number_id.apply {
                setTextColor(textColor)
                text = day.value.toString()
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            }

            if (day.isToday) {
                day_monthly_number_background.setColorFilter(getProperPrimaryColor())
                day_monthly_number_id.setTextColor(getProperPrimaryColor().getContrastColor())
            }
        }
    }

    override fun updateMonthlyCalendar(context: Context, month: String, days: ArrayList<DayMonthly>, checkedEvents: Boolean, currTargetDate: DateTime) {
        runOnUiThread {
            mDays = days
            top_value.text = month
            updateDays()
        }
    }

    private fun updateLabels() {
        val weekendsTextColor = config.highlightWeekendsColor
        for (i in 0..6) {
            findViewById<TextView>(resources.getIdentifier("label_$i", "id", packageName)).apply {
                val textColor = if (config.highlightWeekends && context.isWeekendIndex(i)) {
                    weekendsTextColor
                } else {
                    mTextColor
                }

                setTextColor(textColor)
            }
        }
    }
}
