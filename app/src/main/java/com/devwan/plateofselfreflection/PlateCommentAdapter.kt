package com.devwan.plateofselfreflection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class PlateCommentAdapter(
    private var commentList: ArrayList<String>,
    private val layoutInflater: LayoutInflater
) : BaseAdapter() {

    class ViewHolder {
        var comment: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.card_comment, null)
            holder = ViewHolder()
            holder.comment = view.findViewById(R.id.textView_commentText)
            view.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
            view = convertView
        }
        holder.comment?.text = commentList.get(position)

        return view
    }

    override fun getItem(position: Int): Any {
        return commentList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return commentList.size
    }

    fun setData(newData: ArrayList<String>) {
        commentList = newData
        notifyDataSetChanged()
    }
}