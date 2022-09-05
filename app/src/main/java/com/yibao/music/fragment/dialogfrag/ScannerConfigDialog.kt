package com.yibao.music.fragment.dialogfrag

import com.yibao.music.base.bindings.BaseBindingDialog
import android.view.Gravity
import com.yibao.music.R
import com.yibao.music.util.SharedPreferencesUtil.ContentValue
import android.content.Intent
import com.yibao.music.activity.SplashActivity
import com.yibao.music.fragment.dialogfrag.ScannerConfigDialog
import android.os.Bundle
import android.view.View
import com.yibao.music.databinding.ScannerConfigDialogBinding
import com.yibao.music.util.Constant

/**
 * Author：Sid
 * Des：${删除列表}
 * Time:2017/5/31 18:37
 *
 * @author Stran
 */
class ScannerConfigDialog : BaseBindingDialog<ScannerConfigDialogBinding>(), View.OnClickListener {
    override fun initData() {
        initView()
    }

    override fun onResume() {
        super.onResume()
        val window = dialog!!.window
        isCancelable = false
        dialog!!.setCanceledOnTouchOutside(false)
        if (window != null) {
            window.setGravity(Gravity.CENTER)
            window.setWindowAnimations(R.style.Theme_AppCompat_Dialog_Alert)
        }
    }

    override fun initListener() {
        mBinding.tvScannerCancel.setOnClickListener(this)
        mBinding.tvScannerContinue.setOnClickListener(this)
        mBinding.cbSize.setOnClickListener(this)
        mBinding.cbDuration.setOnClickListener(this)
    }

    private fun initView() {

        val aBooleanFileSize = mSp.getBoolean(Constant.MUSIC_FILE_SIZE_FLAG, false)
        val aBooleanDuration = mSp.getBoolean(Constant.MUSIC_DURATION_FLAG, false)
        mBinding.cbSize.isChecked = aBooleanFileSize
        mBinding.cbDuration.isChecked = aBooleanDuration
    }

    override fun onClick(v: View) {
        val auto = requireArguments().getBoolean(Constant.AUTO_LOAD)
        val id = v.id
        if (id == R.id.tv_scanner_cancel) {
            if (auto) {
                //  SplashActivity 接收 实时显示歌曲数量
                mBus.post(Constant.AUTO_LOAD)
            }
            dismiss()
        } else if (id == R.id.tv_scanner_continue) {
            mSp.putValues(
                ContentValue(Constant.MUSIC_FILE_SIZE_FLAG, mBinding.cbSize.isChecked),
                ContentValue(Constant.MUSIC_DURATION_FLAG, mBinding.cbDuration.isChecked)
            )
            if (auto) {
                mBus.post(Constant.AUTO_LOAD)
            } else {
                val intent = Intent(activity, SplashActivity::class.java)
                intent.putExtra(Constant.SCANNER_MEDIA, Constant.SCANNER_MEDIA)
                startActivity(intent)
            }
            dismiss()
        }
    }

    companion object {
        private val TAG = " ==== " + ScannerConfigDialog::class.java.simpleName + "  "

        /**
         * @param loadFlag true 自动扫描 、 false 手动扫描
         * @return ScannerConfigDialog
         */
        @JvmStatic
        fun newInstance(loadFlag: Boolean): ScannerConfigDialog {
            val dialog = ScannerConfigDialog()
            val bundle = Bundle()
            bundle.putBoolean(Constant.LOAD_FLAG, loadFlag)
            dialog.arguments = bundle
            return dialog
        }
    }
}