package com.yibao.music.fragment.dialogfrag

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.yibao.music.R
import com.yibao.music.base.bindings.BaseBindingDialog
import com.yibao.music.databinding.AddListDialogBinding
import com.yibao.music.databinding.PermissionHintDialogBinding
import com.yibao.music.util.Constant

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/5/31 18:37
 *
 * @author Stran
 */
class PermissionsDialog : BaseBindingDialog<PermissionHintDialogBinding>(), View.OnClickListener {


    override fun initData() {
        val title = requireArguments().getString(Constant.PERMISSION_HINT)
        mBinding.tvTitle.text = title

    }

    override fun initListener() {
        mBinding.tvAddListCancle.setOnClickListener(this)
        mBinding.tvAddListContinue.setOnClickListener(this)
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_add_list_cancle -> dismiss()
            R.id.tv_add_list_continue -> {
                toSelfSetting(requireActivity())
                dismiss()
            }
            else -> {}
        }
    }

    override fun onPause() {
        super.onPause()
        dismiss()
    }

    companion object {
        @JvmStatic
        fun newInstance(title: String?): PermissionsDialog {
            val bundle = Bundle()
            bundle.putString(Constant.PERMISSION_HINT, title)
            val dialog = PermissionsDialog()
            dialog.arguments = bundle
            return dialog
        }

    }

    private fun toSelfSetting(context: Context) {
        val mIntent = Intent()
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        mIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
        mIntent.data = Uri.fromParts("package", context.packageName, null)
        context.startActivity(mIntent)
    }
}
