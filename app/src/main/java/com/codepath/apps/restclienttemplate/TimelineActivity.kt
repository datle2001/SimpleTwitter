package com.codepath.apps.restclienttemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException


class TimelineActivity : AppCompatActivity() {

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    //Handles click on menu items
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.compose) {
            Toast.makeText(this, "compose icon clicked", Toast.LENGTH_SHORT).show()
            var intent = Intent(this, ComposeActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val tweet = data?.getParcelableExtra("tweet") as Tweet

            listTweet.add(0, tweet)
            adapter.notifyItemInserted(0)
            rvTweets.scrollToPosition(0)
        }
        super.onActivityResult(requestCode, resultCode, data)
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
    companion object {
        val TAG = "TimelineActivity"
        val REQUEST_CODE = 20
    }
}