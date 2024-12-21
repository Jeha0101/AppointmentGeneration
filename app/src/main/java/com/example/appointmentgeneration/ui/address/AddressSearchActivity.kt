package com.example.appointmentgeneration.ui.address

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.appointmentgeneration.R
import android.webkit.WebChromeClient
import android.webkit.WebSettings


class AddressSearchActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_search)

        setupWebView()
    }

    private fun setupWebView() {
        webView = findViewById(R.id.webView)

        // WebView 설정: 자바스크립트 실행 허용, 팝업 허용 등
        webView.settings.apply {
            javaScriptEnabled = true  // 자바스크립트 실행 가능하도록
            javaScriptCanOpenWindowsAutomatically = true // 팝업 자동 실행
            domStorageEnabled = true // DOM Storage 사용하도록
            allowFileAccess = true
            allowContentAccess = true
            setSupportMultipleWindows(true) // 다중 윈도우 (팝업) 허용
        }

        // WebChromeClient 설정: 팝업 처리를 위해 필수!
        webView.webChromeClient = object : WebChromeClient() {
            // 새 창(팝업)이 열릴 때 호출되는 메서드
            override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
                // 새 WebView 생성 (팝업 내용을 표시할 녀석)
                val newWebView = WebView(this@AddressSearchActivity).apply {
                    settings.javaScriptEnabled = true // 여기서도 자바스크립트 켜주고
                    // ... 기타 필요한 설정 있으면 추가 ...
                }

                // 메시지 객체에서 WebViewTransport 가져와서 새 WebView 설정
                val transport = resultMsg?.obj as? WebView.WebViewTransport
                transport?.webView = newWebView
                resultMsg?.sendToTarget()

                // 새 창(팝업)을 어떻게 띄울지 여기서 결정 (예: Dialog 사용)
                // 여기서는 일단 간단하게 true만 반환 (실제로는 팝업 띄우는 로직 필요)
                return true
            }
        }

        // JavaScript와 통신하기 위한 인터페이스 연결
        webView.addJavascriptInterface(AddressJavascriptInterface(), "Android")


        // WebViewClient 설정: 페이지 로딩 완료 시 JavaScript 함수 호출
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                // 페이지 로딩 끝났으면 주소 검색 실행
                webView.loadUrl("javascript:execDaumPostcode();")
            }

        }


        // 카카오 우편번호 서비스 페이지 로드
        webView.loadUrl("file:///android_asset/daum_address.html")
    }

    // JavaScript에서 호출할 수 있는 함수들을 정의하는 클래스
    inner class AddressJavascriptInterface {
        // 주소 검색 결과 처리 함수
        @JavascriptInterface
        fun processAddress(address: String) {
            // 결과 데이터를 Intent에 담아서
            val resultIntent = Intent().apply {
                putExtra("address", address)
            }
            // 결과 설정하고 Activity 종료
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}