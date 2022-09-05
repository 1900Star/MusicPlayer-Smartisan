package com.yibao.music.base.bindings

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.yibao.music.model.MusicBean
import com.yibao.music.model.PlayListBean
import com.yibao.music.util.Constant
import java.util.*

/**
 * @author  luoshipeng
 * createDate：2021/6/22 0022 10:14
 * className   FixRecordAdapter
 * Des：TODO
 */
abstract class BaseBindingAdapter<T>(private var mList: MutableList<T>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    protected  val mTAG = " ==== " + this::class.java.simpleName + "  "
    private lateinit var mListener: OnItemListener<T>
    private lateinit var mLongClickListener: ItemLongClickListener
    private lateinit var mEditClickListener: ItemEditClickListener
    private lateinit var mMenuListener: OnOpenItemMoreMenuListener
    protected val TYPE_ITEM = 0
    protected val TYPE_FOOTER = 1
    protected var isSelectStatus = false
    protected var dataList: List<T> = mList

    override fun getItemCount() =
        if (mList.isNotEmpty()) mList.size else 0


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        bindView(holder, mList[position])
    }

    abstract fun bindView(holder: RecyclerView.ViewHolder, bean: T)


    /**
     * 获取列表的类型，根据类型设置最后一个item的文字内容。
     *
     * @return r
     */
    protected abstract fun getLastItemDes(): String?

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
        if (mList.size > Constant.NUMBER_ZERO) {
            mList.clear()
        }
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


    internal class LoadMoreHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {

    }

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
            val gridManager = manager
            gridManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (getItemViewType(position) == TYPE_FOOTER) gridManager.spanCount else 1
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
    interface ItemLongClickListener {
        /**
         * 删除item
         *
         * @param musicInfo       info
         * @param currentPosition 位置
         */
        fun deleteItemList(musicInfo: PlayListBean, currentPosition: Int)
    }


    open fun setItemLongClickListener(longClickListener: ItemLongClickListener) {
        this.mLongClickListener = longClickListener
    }

    protected open fun deletePlaylist(musicInfo: PlayListBean, itemPosition: Int) {
        mLongClickListener.deleteItemList(musicInfo, itemPosition)
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