package com.yibao.music.base.bindings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.yibao.music.MusicApplication
import com.yibao.music.databinding.LoadMoreFootviewBinding
import com.yibao.music.model.MusicBean
import com.yibao.music.util.Constant
import com.yibao.music.util.SnakbarUtil
import com.yibao.music.util.SpUtils
import java.util.*

/**
 * @author  luoshipeng
 * createDate：2021/6/22 0022 10:14
 * className   FixRecordAdapter
 * Des：TODO
 */
abstract class BaseBindingAdapter<T>(var mList: MutableList<T>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    protected val mTAG = " ==== " + this::class.java.simpleName + "  "
    private lateinit var mListener: OnItemListener<T>
    private lateinit var mLongClickListener: ItemLongClickListener<T>
    private lateinit var mEditClickListener: ItemEditClickListener
    private lateinit var mMenuListener: OnOpenItemMoreMenuListener
    protected val typeItem = 0
    protected val typeFooter = 1
    protected var isSelectStatus = false
    protected val mSp = SpUtils(MusicApplication.getInstance(), Constant.MUSIC_CONFIG)

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) {
            typeFooter
        } else typeItem
    }

    override fun getItemCount() = if (mList.isNotEmpty()) mList.size + 1 else 0


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position < mList.size) {
            bindView(holder, mList[position])
        }
    }

    abstract fun bindView(holder: RecyclerView.ViewHolder, bean: T)


    /**
     * 获取列表的类型，根据类型设置最后一个item的文字内容。
     *
     * @return r
     */
    protected abstract fun getLastItemDes(): String?


    //记录当前选中的条目索引
    private var selectedIndex = -1

    fun setSelectedPosition(position: Int) {
        selectedIndex = position
        notifyDataSetChanged()
    }

    fun isItemSelected(position: Int): Boolean {
        return selectedIndex == position

    }


    open fun clear() {
        mList.clear()
        notifyDataSetChanged()
    }

    open fun setItemSelectStatus(selectStatus: Boolean) {
        isSelectStatus = selectStatus
        notifyDataSetChanged()
    }

    open fun setData(list: List<T>) {
        mList.addAll(list)
        notifyDataSetChanged()
    }

    open fun deleteSong(position: Int) {
        mList.removeAt(position)
        notifyDataSetChanged()
    }

    open fun addData(list: List<T>?) {
        if (list != null) {
            for (t in list) {
                if (!mList.contains(t)) {
                    mList.add(t)
                }
            }
            notifyDataSetChanged()
        }
    }

    open fun setNewData(data: List<T>) {
        mList.clear()
        mList.addAll(data)
        notifyDataSetChanged()
    }

    open fun addData(position: Int, data: List<T>) {
        this.mList.addAll(position, data)
        notifyItemRangeInserted(position, data.size)
    }


    open fun getSections(): Array<Any?>? {
        return arrayOfNulls(0)
    }

    open fun getPositionForSection(section: Int): Int {
        for (i in 0 until itemCount) {
            if (i < mList.size) {
                val firstChar = getFirstChar(i)!!.uppercase(Locale.getDefault())[0]
                if (firstChar.code == section) {
                    return i
                }
            }
        }
        return -1
    }

    protected open fun getFirstChar(i: Int): String? {
        return null
    }


    open fun getSectionForPosition(position: Int): Int {
        return getFirstChar(position)!!.uppercase(Locale.getDefault())[0].code
    }


    open fun getData(): List<T>? {
        return mList
    }

    protected fun moreHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding =
            LoadMoreFootviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val listCount = "${mList.size}${getLastItemDes()}"
        binding.tvSongCount.text = listCount
        binding.loadLayout.setOnClickListener {
            SnakbarUtil.lastItem(binding.loadLayout)
        }

        return MoreHolder(binding)

    }

    inner class MoreHolder(viewBinding: LoadMoreFootviewBinding) :
        RecyclerView.ViewHolder(viewBinding.root)


    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        val params = holder.itemView.layoutParams
        if (params is StaggeredGridLayoutManager.LayoutParams) {
            params.isFullSpan = holder.layoutPosition == itemCount - 1
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val manager = recyclerView.layoutManager
        if (manager is GridLayoutManager) {
            manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (getItemViewType(position) == typeFooter) manager.spanCount else 1
                }
            }
        }
    }

    protected open fun openDetails(t: T, adapterPosition: Int) {
        mListener.showDetailsView(t, adapterPosition)
    }


    open fun setItemListener(listener: OnItemListener<T>?) {
        mListener = listener!!
    }


    interface OnItemListener<T> {
        // T 子类的数据类型
        //        void showDetailsView(T bean);
        /**
         * 显示详情页面
         *
         * @param bean
         * @param position
         * @param isEditStatus status
         */
        fun showDetailsView(bean: T, position: Int)
    }


    /**
     * Item长按的点击事件
     */
    interface ItemLongClickListener<T> {
        /**
         *
         * @param musicInfo       info
         * @param currentPosition 位置
         */
        fun longClickItem(musicInfo: T, currentPosition: Int)


    }


    open fun setItemLongClickListener(longClickListener: ItemLongClickListener<T>) {
        this.mLongClickListener = longClickListener
    }

    protected open fun setLongClick(musicInfo: T, itemPosition: Int) {
        mLongClickListener.longClickItem(musicInfo, itemPosition)
    }

    /**
     * Item长按的点击事件
     */
    interface ItemEditClickListener {
        /**
         * 删除的位置
         *
         * @param currentPosition c
         */
        fun deleteItemList(currentPosition: Int)
    }


    open fun setItemEditClickListener(editClickListener: ItemEditClickListener) {
        this.mEditClickListener = editClickListener
    }

    protected open fun editItemTitle(itemPosition: Int) {
        mEditClickListener.deleteItemList(itemPosition)
    }

    /**
     * Item更多菜单
     */
    interface OnOpenItemMoreMenuListener {
        /**
         * 更多菜单
         *
         * @param position  p
         * @param musicBean m
         */
        fun openClickMoreMenu(position: Int, musicBean: MusicBean)
    }

    open fun setOnItemMenuListener(listener: OnOpenItemMoreMenuListener) {
        mMenuListener = listener
    }

    protected open fun openItemMenu(musicBean: MusicBean, position: Int) {
        mMenuListener.openClickMoreMenu(position, musicBean)
    }


    private var mCheckBoxClickListener: OnCheckBoxClickListener<T>? = null

    open fun setCheckBoxClickListener(checkBoxClickListener: OnCheckBoxClickListener<T>?) {
        mCheckBoxClickListener = checkBoxClickListener
    }

    interface OnCheckBoxClickListener<T> {
        fun checkboxChange(t: T, isChecked: Boolean, position: Int)
    }

    protected open fun checkBoxClick(t: T, position: Int, isChecked: Boolean) {
        if (mCheckBoxClickListener != null) {
            mCheckBoxClickListener!!.checkboxChange(t, isChecked, position)
        }
    }


}