package com.yibao.music.base.bindings

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.yibao.music.MusicApplication
import com.yibao.music.model.greendao.AlbumInfoDao
import com.yibao.music.model.greendao.MusicBeanDao
import com.yibao.music.util.RxBus
import com.yibao.music.util.SharedPreferencesUtil
import io.reactivex.disposables.CompositeDisposable
import java.lang.reflect.ParameterizedType

/**
 * @author  luoshipeng
 * createDate：2021/6/29 0029 10:25
 * className   BaseBindingFragment
 * Des：TODO
 */
abstract class BaseBindingFragment<T : ViewBinding> : Fragment() {
    val mTag = " ==== " + this::class.java.simpleName + "  "
    protected lateinit var mSpHelper: SharedPreferencesUtil
    private var isShowToUser = false
    private var _binding: T? = null
    protected val mBinding get() = _binding!!
    protected var mBus = RxBus.getInstance()
    protected lateinit var mMusicBeanDao: MusicBeanDao
    protected lateinit var mCompositeDisposable: CompositeDisposable
    protected lateinit var mContext: Context
    protected lateinit var mActivity: Activity
    protected lateinit var mAlbumDao: AlbumInfoDao
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = requireActivity()
        mActivity = requireActivity()
        mCompositeDisposable = CompositeDisposable()
        mMusicBeanDao = MusicApplication.getInstance().musicDao
        mAlbumDao = MusicApplication.getInstance().albumDao

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
        initView()
        initData()
        return mBinding.root
    }

    fun <T : ViewModel?> gets(modelClass: Class<T>): T {

        return ViewModelProvider(this).get(modelClass)
    }


    abstract fun initData()


    abstract fun initView()


    override fun onDestroy() {
        super.onDestroy()
        if (_binding != null) {
            _binding = null

        }
    }

}