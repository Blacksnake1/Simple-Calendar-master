package com.simplemobiletools.calendar.pro.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.annotation.NonNull
import com.simplemobiletools.calendar.pro.R
import com.simplemobiletools.calendar.pro.databinding.DialogWarningRequestNotifyBinding

class DialogWarningRequestNotify(
    @NonNull context: Context,
    themeResId: Int,
    mBuilder: Builder
) : Dialog(context) {
    private var binding: DialogWarningRequestNotifyBinding? = null
    private var mBuilder: Builder? = null

    init {
        this.mBuilder = mBuilder
    }

    class Builder {
        var mOnClickDialog: OnClickDialog? = null
        var content: String? = null

        interface OnClickDialog {
            fun onClose()
            fun onSetting()
        }

        fun setContent(content: String?): Builder {
            this.content = content
            return this
        }

        fun setOnClickListener(mOnClickDialog: OnClickDialog?): Builder {
            this.mOnClickDialog = mOnClickDialog
            return this
        }

        fun build(mContext: Context?): DialogWarningRequestNotify {
            return DialogWarningRequestNotify(mContext!!, R.style.DialogPermissrion, this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val displayMetrics = DisplayMetrics()

        setCancelable(false)
        binding = DialogWarningRequestNotifyBinding.inflate(LayoutInflater.from(context))
        setContentView(binding?.root!!)
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val layoutParams: WindowManager.LayoutParams? = this.window?.attributes
        layoutParams?.x = 100; // left margin
//        layoutParams?.y = 170; // bottom margin
        this.window?.attributes = layoutParams
        initEvent()

    }


    private fun initEvent() {
        binding?.apply {
            btnClose.setOnClickListener {
                if (mBuilder != null && mBuilder?.mOnClickDialog != null) {
                    mBuilder!!.mOnClickDialog!!.onClose()
                    dismiss()
                }
            }
            btnWarningSetting.setOnClickListener {
                mBuilder!!.mOnClickDialog!!.onSetting()
                dismiss()
            }

        }
        if (mBuilder == null) return
    }

}
