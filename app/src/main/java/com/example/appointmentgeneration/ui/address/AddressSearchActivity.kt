package com.example.appointmentgeneration.ui.address

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.appointmentgeneration.R

class AddressSearchActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_search)

        setupWebView()
    }

    private fun setupWebView() {
        webView = findViewById(R.id.webView)
        webView.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            domStorageEnabled = true
            setSupportMultipleWindows(true)
        }

        webView.addJavascriptInterface(AddressJavascriptInterface(), "Android")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                // 페이지 로딩 완료 시 주소검색 실행
                webView.loadUrl("javascript:execDaumPostcode();")
            }
        }

        // 다음(카카오) 우편번호 서비스 페이지 로드
        webView.loadUrl("file:///android_asset/daum_address.html")
    }

    inner class AddressJavascriptInterface {
        @JavascriptInterface
        fun processAddress(address: String) {
            val resultIntent = Intent().apply {
                putExtra("address", address)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}