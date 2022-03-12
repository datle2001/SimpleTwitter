package com.codepath.apps.restclienttemplate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codepath.apps.restclienttemplate.models.Tweet

class TimelineActivityAdapter(private val listTweet: ArrayList<Tweet>): RecyclerView.Adapter<TimelineActivityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_tweet, parent,  false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tweet = listTweet[position]
        holder.tvUsername.text = tweet.user?.name
        holder.tvTweetBody.text = tweet.body
        holder.tvTime.text = tweet.createdAt
        Glide.with(holder.ivProfileImage).load(tweet.user?.publicImageUrl).into(holder.ivProfileImage)
    }

    override fun getItemCount(): Int {
        return listTweet.size
    }
    fun clear() {
        listTweet.clear()
        notifyDataSetChanged()
    }

// Add a list of items -- change to type used

    fun addAll(tweetList: List<Tweet>) {
        listTweet.addAll(tweetList)
        notifyDataSetChanged()

    }
    inner class ViewHolder(view : View): RecyclerView.ViewHolder(view) {
        val ivProfileImage = view.findViewById<ImageView>(R.id.ivProfileImage)
        val tvUsername = view.findViewById<TextView>(R.id.tvUsername)
        val tvTweetBody = view.findViewById<TextView>(R.id.tvTweetBody)
        val tvTime = view.findViewById<TextView>(R.id.tvTime)
    }
}