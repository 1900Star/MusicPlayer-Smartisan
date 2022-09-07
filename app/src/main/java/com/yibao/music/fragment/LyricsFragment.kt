package com.yibao.music.fragment

import android.os.Bundle
import com.yibao.music.base.bindings.BaseMusicFragmentDev
import com.yibao.music.databinding.LyricsFragmentBinding
import com.yibao.music.util.LogUtil

/**
 * @author luoshipeng
 * createDate：2019/12/26 0026 17:17
 * className   LyricsFragment
 * Des：TODO
 */
class LyricsFragment : BaseMusicFragmentDev<LyricsFragmentBinding>() {
    private var mPosition = 0
    private lateinit var mLyrics: String

    override fun initView() {
        mLyrics = requireArguments().getString("lyrics").toString()
        mPosition = requireArguments().getInt("position")
        setLyrics()
    }


    private fun setLyrics() {
        LogUtil.d(mTag, "显示歌词 $mPosition")
        val replace = mLyrics.replace("\\n", "\n\n")
        mBinding.tvLyricsPage.text = replace
    }


    companion object {
        @JvmStatic
        fun newInstance(position: Int, lyrics: String?): LyricsFragment {
            val args = Bundle()
            args.putInt("position", position)
            args.putString("lyrics", lyrics)
            val fragment = LyricsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initData() {


    }
}