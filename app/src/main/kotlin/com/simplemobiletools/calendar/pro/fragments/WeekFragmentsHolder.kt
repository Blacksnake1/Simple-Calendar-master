package com.simplemobiletools.calendar.pro.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.simplemobiletools.calendar.pro.R
import com.simplemobiletools.calendar.pro.activities.MainActivity
import com.simplemobiletools.calendar.pro.adapters.MyWeekPagerAdapter
import com.simplemobiletools.calendar.pro.extensions.*
import com.simplemobiletools.calendar.pro.helpers.Formatter
import com.simplemobiletools.calendar.pro.helpers.WEEKLY_VIEW
import com.simplemobiletools.calendar.pro.helpers.WEEK_START_DATE_TIME
import com.simplemobiletools.calendar.pro.interfaces.WeekFragmentListener
import com.simplemobiletools.calendar.pro.views.MyScrollView
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.WEEK_SECONDS
import com.simplemobiletools.commons.views.MyViewPager
import kotlinx.android.synthetic.main.fragment_week_holder.view.*
import org.joda.time.DateTime

class WeekFragmentsHolder : MyFragmentHolder(), WeekFragmentListener {
    private val PREFILLED_WEEKS = 151
    private val MAX_SEEKBAR_VALUE = 14

    private var viewPager: MyViewPager? = null
    private var weekHolder: ViewGroup? = null
    private var defaultWeeklyPage = 0
    private var thisWeekTS = 0L
    private var currentWeekTS = 0L
    private var isGoToTodayVisible = false
    private var weekScrollY = 0

    override val viewType = WEEKLY_VIEW

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dateTimeString = arguments?.getString(WEEK_START_DATE_TIME) ?: return
        currentWeekTS = (DateTime.parse(dateTimeString) ?: DateTime()).seconds()
        thisWeekTS = DateTime.parse(requireContext().getFirstDayOfWeek(DateTime())).seconds()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val textColor = requireContext().getProperTextColor()
        weekHolder = inflater.inflate(R.layout.fragment_week_holder, container, false) as ViewGroup
        weekHolder!!.background = ColorDrawable(requireContext().getProperBackgroundColor())
        weekHolder!!.week_view_month_label.setTextColor(textColor)
        weekHolder!!.week_view_week_number.setTextColor(textColor)

        val itemHeight = requireContext().getWeeklyViewItemHeight().toInt()
        weekHolder!!.week_view_hours_holder.setPadding(0, 0, 0, itemHeight)

        viewPager = weekHolder!!.week_view_view_pager
        viewPager!!.id = (System.currentTimeMillis() % 100000).toInt()
        setupFragment()
        return weekHolder
    }

    override fun onResume() {
        super.onResume()
        context?.config?.allowCustomizeDayCount?.let { allow ->
            weekHolder!!.week_view_days_count.beVisibleIf(allow)
            weekHolder!!.week_view_seekbar.beVisibleIf(allow)
        }
        setupSeekbar()
    }

    private fun setupFragment() {
        addHours()
        setupWeeklyViewPager()

        weekHolder!!.week_view_hours_scrollview.setOnTouchListener { view, motionEvent -> true }

        weekHolder!!.week_view_seekbar.apply {
            progress = context?.config?.weeklyViewDays ?: 7
            max = MAX_SEEKBAR_VALUE

            onSeekBarChangeListener {
                if (it == 0) {
                    progress = 1
                }

                updateWeeklyViewDays(progress)
            }
        }

        setupWeeklyActionbarTitle(currentWeekTS)
    }

    private fun setupWeeklyViewPager() {
        val weekTSs = getWeekTimestamps(currentWeekTS)
        val weeklyAdapter = MyWeekPagerAdapter(requireActivity().supportFragmentManager, weekTSs, this)

        defaultWeeklyPage = weekTSs.size / 2

        viewPager!!.apply {
            adapter = weeklyAdapter
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                override fun onPageSelected(position: Int) {
                    currentWeekTS = weekTSs[position]
                    val shouldGoToTodayBeVisible = shouldGoToTodayBeVisible()
                    if (isGoToTodayVisible != shouldGoToTodayBeVisible) {
                        (activity as? MainActivity)?.toggleGoToTodayVisibility(shouldGoToTodayBeVisible)
                        isGoToTodayVisible = shouldGoToTodayBeVisible
                    }

                    setupWeeklyActionbarTitle(weekTSs[position])
                }
            })
            currentItem = defaultWeeklyPage
        }

        weekHolder!!.week_view_hours_scrollview.setOnScrollviewListener(object : MyScrollView.ScrollViewListener {
            override fun onScrollChanged(scrollView: MyScrollView, x: Int, y: Int, oldx: Int, oldy: Int) {
                weekScrollY = y
                weeklyAdapter.updateScrollY(viewPager!!.currentItem, y)
            }
        })
    }

    private fun addHours(textColor: Int = requireContext().getProperTextColor()) {
        val itemHeight = requireContext().getWeeklyViewItemHeight().toInt()
        weekHolder!!.week_view_hours_holder.removeAllViews()
        val hourDateTime = DateTime().withDate(2000, 1, 1).withTime(0, 0, 0, 0)
        for (i in 1..23) {
            val formattedHours = Formatter.getTime(requireContext(), hourDateTime.withHourOfDay(i))
            (layoutInflater.inflate(R.layout.weekly_view_hour_textview, null, false) as TextView).apply {
                text = formattedHours
                setTextColor(textColor)
                height = itemHeight
                weekHolder!!.week_view_hours_holder.addView(this)
            }
        }
    }

    private fun getWeekTimestamps(targetSeconds: Long): List<Long> {
        val weekTSs = ArrayList<Long>(PREFILLED_WEEKS)
        val dateTime = Formatter.getDateTimeFromTS(targetSeconds)
        val shownWeekDays = requireContext().config.weeklyViewDays
        var currentWeek = dateTime.minusDays(PREFILLED_WEEKS / 2 * shownWeekDays)
        for (i in 0 until PREFILLED_WEEKS) {
            weekTSs.add(currentWeek.seconds())
            currentWeek = currentWeek.plusDays(shownWeekDays)
        }
        return weekTSs
    }

    private fun setupWeeklyActionbarTitle(timestamp: Long) {
        val startDateTime = Formatter.getDateTimeFromTS(timestamp)
        val month = Formatter.getShortMonthName(requireContext(), startDateTime.monthOfYear)
        weekHolder!!.week_view_month_label.text = month
        val weekNumber = startDateTime.plusDays(3).weekOfWeekyear
        weekHolder!!.week_view_week_number.text = "${getString(R.string.week_number_short)} $weekNumber"
    }

    override fun goToToday() {
        currentWeekTS = thisWeekTS
        setupFragment()
    }

    override fun showGoToDateDialog() {
        if (activity == null) {
            return
        }

        val view = layoutInflater.inflate(getDatePickerDialogStyle(), null)
        val datePicker = view.findViewById<DatePicker>(R.id.date_picker)

        val dateTime = getCurrentDate()!!
        datePicker.init(dateTime.year, dateTime.monthOfYear - 1, dateTime.dayOfMonth, null)

        activity?.getAlertDialogBuilder()!!
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.ok) { dialog, which -> dateSelected(dateTime, datePicker) }
            .apply {
                activity?.setupDialogStuff(view, this)
            }
    }

    private fun dateSelected(dateTime: DateTime, datePicker: DatePicker) {
        val month = datePicker.month + 1
        val year = datePicker.year
        val day = datePicker.dayOfMonth
        val newDateTime = dateTime.withDate(year, month, day)
        currentWeekTS = requireContext().getFirstDayOfWeekDt(newDateTime).seconds()
        setupFragment()
    }

    private fun setupSeekbar() {
        if (context?.config?.allowCustomizeDayCount != true) {
            return
        }

        // avoid seekbar width changing if the days count changes to 1, 10 etc
        weekHolder!!.week_view_days_count.onGlobalLayout {
            if (weekHolder!!.week_view_seekbar.width != 0) {
                weekHolder!!.week_view_seekbar.layoutParams.width = weekHolder!!.week_view_seekbar.width
            }
            (weekHolder!!.week_view_seekbar.layoutParams as RelativeLayout.LayoutParams).removeRule(RelativeLayout.START_OF)
        }

        updateDaysCount(context?.config?.weeklyViewDays ?: 7)
    }

    private fun updateWeeklyViewDays(days: Int) {
        requireContext().config.weeklyViewDays = days
        updateDaysCount(days)
        setupWeeklyViewPager()
    }

    private fun updateDaysCount(cnt: Int) {
        weekHolder!!.week_view_days_count.text = requireContext().resources.getQuantityString(R.plurals.days, cnt, cnt)
    }

    override fun refreshEvents() {
        (viewPager?.adapter as? MyWeekPagerAdapter)?.updateCalendars(viewPager!!.currentItem)
    }

    override fun shouldGoToTodayBeVisible() = currentWeekTS != thisWeekTS

    override fun getNewEventDayCode(): String {
        val currentTS = System.currentTimeMillis() / 1000
        return if (currentTS > currentWeekTS && currentTS < currentWeekTS + WEEK_SECONDS) {
            Formatter.getTodayCode()
        } else {
            Formatter.getDayCodeFromTS(currentWeekTS)
        }
    }

    override fun scrollTo(y: Int) {
        weekHolder!!.week_view_hours_scrollview.scrollY = y
        weekScrollY = y
    }

    override fun updateHoursTopMargin(margin: Int) {
        weekHolder?.apply {
            week_view_hours_divider?.layoutParams?.height = margin
            week_view_hours_scrollview?.requestLayout()
            week_view_hours_scrollview?.onGlobalLayout {
                week_view_hours_scrollview.scrollY = weekScrollY
            }
        }
    }

    override fun getCurrScrollY() = weekScrollY

    override fun updateRowHeight(rowHeight: Int) {
        val childCnt = weekHolder!!.week_view_hours_holder.childCount
        for (i in 0..childCnt) {
            val textView = weekHolder!!.week_view_hours_holder.getChildAt(i) as? TextView ?: continue
            textView.layoutParams.height = rowHeight
        }

        weekHolder!!.week_view_hours_holder.setPadding(0, 0, 0, rowHeight)
        (viewPager!!.adapter as? MyWeekPagerAdapter)?.updateNotVisibleScaleLevel(viewPager!!.currentItem)
    }

    override fun getFullFragmentHeight() =
        weekHolder!!.week_view_holder.height - weekHolder!!.week_view_seekbar.height - weekHolder!!.week_view_days_count_divider.height

    override fun printView() {
        val lightTextColor = resources.getColor(R.color.theme_light_text_color)
        weekHolder!!.apply {
            week_view_days_count_divider.beGone()
            week_view_seekbar.beGone()
            week_view_days_count.beGone()
            addHours(lightTextColor)
            week_view_week_number.setTextColor(lightTextColor)
            week_view_month_label.setTextColor(lightTextColor)
            background = ColorDrawable(Color.WHITE)
            (viewPager?.adapter as? MyWeekPagerAdapter)?.togglePrintMode(viewPager?.currentItem ?: 0)

            Handler().postDelayed({
                requireContext().printBitmap(weekHolder!!.week_view_holder.getViewBitmap())

                Handler().postDelayed({
                    week_view_days_count_divider.beVisible()
                    week_view_seekbar.beVisible()
                    week_view_days_count.beVisible()
                    week_view_week_number.setTextColor(requireContext().getProperTextColor())
                    week_view_month_label.setTextColor(requireContext().getProperTextColor())
                    addHours()
                    background = ColorDrawable(requireContext().getProperBackgroundColor())
                    (viewPager?.adapter as? MyWeekPagerAdapter)?.togglePrintMode(viewPager?.currentItem ?: 0)
                }, 1000)
            }, 1000)
        }
    }

    override fun getCurrentDate(): DateTime? {
        return if (currentWeekTS != 0L) {
            Formatter.getDateTimeFromTS(currentWeekTS)
        } else {
            null
        }
    }
}
