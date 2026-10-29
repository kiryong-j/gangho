package com.gangho.yurang;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/** 강호유랑: 게임(assets/index.html)을 전체 화면 가로 WebView로 띄우는 화면 */
public class MainActivity extends Activity {
    private WebView web;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);

        web = new WebView(this);
        web.setBackgroundColor(0xFF0B0F18);
        setContentView(web);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);          // 저장 데이터(localStorage)
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setSupportZoom(false);
        s.setBuiltInZoomControls(false);
        s.setTextZoom(100);                    // 휴대폰 글꼴 크기 설정에 UI가 깨지지 않게

        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new WebChromeClient());

        hideSystemUI();
        if (state != null) web.restoreState(state);
        else web.loadUrl("file:///android_asset/index.html");
    }

    /** 상태 표시줄과 내비게이션 바를 숨긴다 (화면 가장자리를 쓸어 올리면 잠시 나타남) */
    @SuppressWarnings("deprecation")
    private void hideSystemUI() {
        View d = getWindow().getDecorView();
        d.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemUI();
    }

    /** 뒤로 가기: 열린 창이 있으면 닫고, 없으면 종료할지 묻는다 */
    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        String js = "(function(){var m=document.querySelectorAll('.modal');for(var i=0;i<m.length;i++){if(!m[i].hidden){"
                + "window.dispatchEvent(new KeyboardEvent('keydown',{key:'Escape'}));return 1;}}return 0;})()";
        web.evaluateJavascript(js, v -> {
            if ("1".equals(v)) return;
            new AlertDialog.Builder(this)
                    .setMessage("게임을 종료할까요? (진행 상황은 자동 저장됩니다)")
                    .setPositiveButton("종료", (dlg, w) -> finish())
                    .setNegativeButton("계속하기", null)
                    .show();
        });
    }

    @Override
    protected void onPause() {
        // 앱을 내릴 때 게임에 '화면 숨김'을 알려 자동 저장이 돌게 한다
        web.evaluateJavascript("try{window.__gySave&&window.__gySave()}catch(e){}", null);
        web.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        web.onResume();
        hideSystemUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        web.saveState(out);
    }
}
