package com.yibao.music.base.bindings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.yibao.music.MusicApplication
import com.yibao.music.R
import com.yibao.music.util.Constant
import com.yibao.music.util.RxBus
import com.yibao.music.util.SharedPreferencesUtil
import java.lang.reflect.ParameterizedType

/**
 * @author luoshipeng
 * createDate：2019/8/9 0009 17:12
 * className   BaseDialog
 * Des：TODO
 */
abstract class BaseBindingDialog<T : ViewBinding> : DialogFragment() {
    protected var mTag = " ==== " + javaClass.simpleName + "  "
    private var _binding: T? = null
    protected val mBinding get() = _binding!!
    protected lateinit var mBus: RxBus
    protected lateinit var mSp: SharedPreferencesUtil

    fun <T : ViewModel?> gets(modelClass: Class<T>): T {

        return ViewModelProvider(this).get(modelClass)
    }

    protected open fun initRecyclerView(recyclerView: RecyclerView) {
        val manager = LinearLayoutManager(activity)
        manager.orientation = LinearLayoutManager.VERTICAL
        manager.isSmoothScrollbarEnabled = true
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.isVerticalScrollBarEnabled = true
        recyclerView.layoutManager = manager
//        val divider = GridItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL)
//        divider.setDrawable(
//            ContextCompat.getDrawable(
//                requireActivity(),
//                R.drawable.shape_item_decoration
//            )!!
//        )
//        recyclerView.addItemDecoration(divider)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window!!.setLayout(width, height)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setBackgroundDrawableResource(R.drawable.shape_dialog_bg);
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val type = javaClass.genericSuperclass
        val clazz = (type as ParameterizedType).actualTypeArguments[0] as Class<T>
        val method = clazz.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        _binding = method.invoke(null, layoutInflater, container, false) as T
        mBus = RxBus.getInstance()

        mSp = SharedPreferencesUtil(MusicApplication.getInstance(), Constant.MUSIC_CONFIG)



        initData()
        initListener()

        return mBinding.root
    }



    protected abstract fun initListener()
    protected abstract fun initData()
}