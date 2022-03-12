package com.codepath.apps.restclienttemplate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException


class TimelineActivity : AppCompatActivity() {
    private val TAG = "TimelineActivity"
    lateinit var client: TwitterClient
    lateinit var rvTweets: RecyclerView
    lateinit var swipeContainer: SwipeRefreshLayout
    lateinit var adapter: TimelineActivityAdapter
    var listTweet = ArrayList<Tweet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)
        client = TwitterApplication.getRestClient(this)

        rvTweets = findViewById(R.id.rvTweets)
        adapter = TimelineActivityAdapter(listTweet)

        swipeContainer = findViewById(R.id.swipeContainer)
        swipeContainer.setOnRefreshListener {
            Log.i(TAG, "refreshing timeline")
            populateHomeTimeline()
        }
        rvTweets.layoutManager = LinearLayoutManager(this)
        rvTweets.adapter = adapter

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light)

        populateHomeTimeline()
    }
    fun populateHomeTimeline() {
        client.getHomeTimeline(object: JsonHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.i(TAG, "$response")
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                try {
                    //clear out current tweets
                    adapter.clear()
                    Log.i(TAG, "Successful!")
                    val jsonArray = json.jsonArray
                    val listOfNewTweetRetrieved = Tweet.fromJsonArray(jsonArray)
                    listTweet.addAll(listOfNewTweetRetrieved)
                    adapter.notifyDataSetChanged()
                    swipeContainer.isRefreshing = false
                } catch(e: JSONException) {
                    Log.e(TAG, "JSON exception: $e")
                }

            }

        })
    }
}