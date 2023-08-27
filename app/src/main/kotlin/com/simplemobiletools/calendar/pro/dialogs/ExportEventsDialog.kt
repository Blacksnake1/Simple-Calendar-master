package com.simplemobiletools.calendar.pro.dialogs

import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.simplemobiletools.calendar.pro.R
import com.simplemobiletools.calendar.pro.activities.SimpleActivity
import com.simplemobiletools.calendar.pro.adapters.FilterEventTypeAdapter
import com.simplemobiletools.calendar.pro.extensions.config
import com.simplemobiletools.calendar.pro.extensions.eventsHelper
import com.simplemobiletools.commons.dialogs.FilePickerDialog
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import kotlinx.android.synthetic.main.dialog_export_events.view.*
import java.io.File

class ExportEventsDialog(
    val activity: SimpleActivity, val path: String, val hidePath: Boolean,
    val callback: (file: File, eventTypes: ArrayList<Long>) -> Unit
) {
    private var realPath = path.ifEmpty { activity.internalStoragePath }
    private val config = activity.config

    init {
        val view = (activity.layoutInflater.inflate(R.layout.dialog_export_events, null) as ViewGroup).apply {
            export_events_folder.setText(activity.humanizePath(realPath))
            export_events_filename.setText("${activity.getString(R.string.events)}_${activity.getCurrentFormattedDateTime()}")

            export_events_checkbox.isChecked = config.exportEvents
            export_events_checkbox_holder.setOnClickListener {
                export_events_checkbox.toggle()
            }
            export_tasks_checkbox.isChecked = config.exportTasks
            export_tasks_checkbox_holder.setOnClickListener {
                export_tasks_checkbox.toggle()
            }
            export_past_events_checkbox.isChecked = config.exportPastEntries
            export_past_events_checkbox_holder.setOnClickListener {
                export_past_events_checkbox.toggle()
            }

            if (hidePath) {
                export_events_folder_hint.beGone()
                export_events_folder.beGone()
            } else {
                export_events_folder.setOnClickListener {
                    activity.hideKeyboard(export_events_filename)
                    FilePickerDialog(activity, realPath, false, showFAB = true) {
                        export_events_folder.setText(activity.humanizePath(it))
                        realPath = it
                    }
                }
            }

            activity.eventsHelper.getEventTypes(activity, false) {
                val eventTypes = HashSet<String>()
                it.mapTo(eventTypes) { it.id.toString() }

                export_events_types_list.adapter = FilterEventTypeAdapter(activity, it, eventTypes)
                if (it.size > 1) {
                    export_events_pick_types.beVisible()
                }
            }
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(view, this, R.string.export_events) { alertDialog ->
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val filename = view.export_events_filename.value
                        when {
                            filename.isEmpty() -> activity.toast(R.string.empty_name)
                            filename.isAValidFilename() -> {
                                val file = File(realPath, "$filename.ics")
                                if (!hidePath && file.exists()) {
                                    activity.toast(R.string.name_taken)
                                    return@setOnClickListener
                                }

                                val exportEventsChecked = view.export_events_checkbox.isChecked
                                val exportTasksChecked = view.export_tasks_checkbox.isChecked
                                if (!exportEventsChecked && !exportTasksChecked) {
                                    activity.toast(R.string.no_entries_for_exporting)
                                    return@setOnClickListener
                                }

                                ensureBackgroundThread {
                                    config.apply {
                                        lastExportPath = file.absolutePath.getParentPath()
                                        exportEvents = exportEventsChecked
                                        exportTasks = exportTasksChecked
                                        exportPastEntries = view.export_past_events_checkbox.isChecked
                                    }

                                    val eventTypes = (view.export_events_types_list.adapter as FilterEventTypeAdapter).getSelectedItemsList()
                                    callback(file, eventTypes)
                                    alertDialog.dismiss()
                                }
                            }
                            else -> activity.toast(R.string.invalid_name)
                        }
                    }
                }
            }
    }
}
