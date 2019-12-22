package nl.toefel.notekeeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView

class WebActivity : AppCompatActivity() {

    lateinit var webView : WebView
    lateinit var searchTerm: TextView
    lateinit var searchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        webView = findViewById(R.id.web_view)
        searchTerm = findViewById(R.id.search_term)
        searchButton = findViewById(R.id.button_search)
        searchButton.setOnClickListener { view ->
            webView.loadUrl("https://www.google.com/?q=${searchTerm.text}")
        }
        webView.webViewClient = WebViewClient()
    }
}
