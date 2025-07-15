package com.yibao.music.base.bindings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.yibao.music.MusicApplication
import com.yibao.music.R
import com.yibao.music.base.listener.OnHandleBackListener
import com.yibao.music.model.greendao.MusicBeanDao
import com.yibao.music.util.Constant
import com.yibao.music.util.HandleBackUtil
import com.yibao.music.util.RxBus
import com.yibao.music.util.SpUtils
import com.yibao.music.util.ToastUtil
import io.reactivex.disposables.CompositeDisposable
import java.lang.reflect.ParameterizedType

/**
 * @author  luoshipeng
 * createDate：2021/6/29 0029 10:25
 * className   BaseBindingFragment
 * Des：TODO
 */
abstract class BaseBindingFragment<T : ViewBinding> : Fragment(), OnHandleBackListener {
    val mTag = " ==== " + this::class.java.simpleName + "  "
    protected lateinit var mSp: SpUtils
    private var isShowToUser = false
    private var _binding: T? = null
    protected val mBinding get() = _binding!!
    protected var mBus: RxBus = RxBus.getInstance()
    protected lateinit var mMusicBeanDao: MusicBeanDao
    protected lateinit var mCompositeDisposable: CompositeDisposable
    protected lateinit var mContext: Context
    protected lateinit var mActivity: AppCompatActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = requireActivity()
        mActivity = requireActivity() as AppCompatActivity
        mSp = SpUtils(MusicApplication.getInstance(),Constant.MUSIC_CONFIG)
        mCompositeDisposable = CompositeDisposable()
        mMusicBeanDao = MusicApplication.getInstance().musicDao


    }

    fun initRecyclerView(recyclerView: RecyclerView) {

        val manager = LinearLayoutManager(MusicApplication.getInstance())
        manager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.isVerticalScrollBarEnabled = true
        recyclerView.layoutManager = manager

        val divider =
            DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL)

        ContextCompat.getDrawable(requireActivity(), R.drawable.shape_item_decoration)
            ?.let { divider.setDrawable(it) }

        recyclerView.addItemDecoration(divider)
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
        mSp = SpUtils(MusicApplication.getInstance(),Constant.MUSIC_CONFIG)
        initView()
        initData()

        return mBinding.root
    }

    fun <T : ViewModel> gets(modelClass: Class<T>): T {

        return ViewModelProvider(this)[modelClass]
    }


    abstract fun initData()


    abstract fun initView()

    protected  fun showMsg(msg:String){
        ToastUtil.show(requireActivity(), msg)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (_binding != null) {
            _binding = null
            mCompositeDisposable.clear()
            mCompositeDisposable.dispose()
        }
    }

    override fun onBackPressed(): Boolean {
        return HandleBackUtil.handleBackPress(this)
    }
}