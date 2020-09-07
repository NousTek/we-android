package com.we.beyond.adapter

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.we.beyond.Interface.OnItemClickListener
import com.we.beyond.R
import com.we.beyond.model.UserList
import com.we.beyond.util.ConstantFonts
import kotlinx.android.synthetic.main.list_user_types.view.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

/** It binds resolved issue user data set to views */
class ResolvedIssueUserAdapter(

    val context: Context,
    userDetails: ArrayList<UserList>,
    val trim: String
) : RecyclerView.Adapter<ResolvedIssueUserAdapter.ViewHolder>() {


    /** init array list */
    var userDetails: ArrayList<UserList>? = null

    /** init listeners */
    var onClick: OnItemClickListener? = null

    init {
        this.userDetails = userDetails
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.list_user_types,
            parent,
            false
        )

        return ViewHolder(view)

    }


    override fun getItemCount(): Int {


        return userDetails!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        /** initialize listeners and set data to views */
        val activity = context as Activity
        try {
            onClick = activity as OnItemClickListener
        } catch (e: Exception) {
            e.printStackTrace()

        }

        if (userDetails!![position].profileUrl != null && userDetails!![position].profileUrl.isNotEmpty()) {

            Picasso.with(context)
                .load(userDetails!![position].profileUrl)
                .placeholder(R.drawable.profile)
                //.memoryPolicy(MemoryPolicy.NO_CACHE)
                //.networkPolicy(NetworkPolicy.NO_CACHE)
                //.resize(400, 400)  // optional
                .into(holder.profilePic)
        } else {
            holder.profilePic.setBackgroundResource(R.drawable.profile)
        }

        holder.userName.text = userDetails!![position].name
        holder.userName.typeface = ConstantFonts.raleway_regular

        /** It call OnClick function of OnClick listener */
        holder.itemView.setOnClickListener {
            onClick!!.OnClick(userDetails!![position].name, userDetails!![position]._id)
        }


        val startPos =
            userDetails!![position].name.toLowerCase(Locale.US).indexOf(trim.toLowerCase(Locale.US))
        val endPos = startPos + trim.length
        if (startPos != -1) {
            val spannable = SpannableString(userDetails!![position].name)
            val blueColor = ColorStateList(
                arrayOf<IntArray>(intArrayOf()), intArrayOf(
                    Color.YELLOW
                )
            )
            val highlightSpan =
                TextAppearanceSpan(
                    null,
                    Typeface.BOLD,
                    -1,
                    ContextCompat.getColorStateList(context, R.color.button_background),
                    null
                )
            spannable.setSpan(
                highlightSpan,
                startPos,
                endPos,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            holder.userName.setText(spannable)
        }

    }


    /** View holder to initialize ui */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var userName = itemView.txt_user_Name!!
        var profilePic = itemView.img_profile_pic!!

    }
}
