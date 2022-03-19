package com.codepath.apps.restclienttemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {
    lateinit var etCompose: EditText
    lateinit var btnCompose: Button
    lateinit var tvCountLength: TextView
    lateinit var client: TwitterClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnCompose = findViewById(R.id.btnTweet)
        tvCountLength = findViewById(R.id.tvCountLength)

        client = TwitterApplication.getRestClient(this)

        etCompose.addTextChangedListener {
            val remainingCharacter = MAX_CHARACTER - etCompose.text.length
            tvCountLength.text = "$remainingCharacter/$MAX_CHARACTER"
        }

        btnCompose.setOnClickListener {

            //get the content of etCompose
            val tweetContent = etCompose.text.toString()

            //1.Make sure the tweet isn't empty
            if (tweetContent.isEmpty()) {
                Toast.makeText(this, "Empty tweet is not allowed", Toast.LENGTH_SHORT).show()
            }
            //2.Make sure the tweet is under character count
            else if (tweetContent.length > MAX_CHARACTER) {
                Toast.makeText(
                    this,
                    "Tweet is too long. Must be under $MAX_CHARACTER characters",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                //Make an api call to Twitter to publish tweet
                client.publishTweet(tweetContent, object : JsonHttpResponseHandler() {
                    override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                        Log.i(TAG, "Successfully publish tweet")

                        val tweet = Tweet.fromJson(json.jsonObject)
                        val intent = Intent()
                        intent.putExtra("tweet", tweet)

                        setResult(RESULT_OK, intent)
                        finish()
                    }
                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String?,
                        throwable: Throwable?
                    ) {
                        Log.e(TAG, "Fail to publish tweet $response", throwable)
                    }
                })
            }
        }
    }
    companion object {
        val TAG = "ComposeActivity"
        val MAX_CHARACTER = 280
    }
}