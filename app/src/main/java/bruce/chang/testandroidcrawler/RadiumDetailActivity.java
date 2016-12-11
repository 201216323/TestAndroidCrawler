package bruce.chang.testandroidcrawler;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by: BruceChang
 * Date on : 2016/12/11.
 * Time on: 16:07
 * Progect_Name:TestAndroidCrawler
 * Source Github：
 * Description:
 */

public class RadiumDetailActivity extends AppCompatActivity {
    @BindView(R.id.wvRadiumDetail)
    WebView wvRadiumDetail;
    Intent mIntent;
    String url;
    @BindView(R.id.tvTargetUrl)
    TextView tvTargetUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radiumdetail);
        ButterKnife.bind(this);
        mIntent = getIntent();
        url = mIntent.getStringExtra("url");
        tvTargetUrl.setText("页面跳转地址是："+url);
        WebSettings settings = wvRadiumDetail.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        wvRadiumDetail.setHorizontalScrollBarEnabled(false);//水平不显示
        wvRadiumDetail.setVerticalScrollBarEnabled(false); //垂直不显示
        wvRadiumDetail.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        wvRadiumDetail.loadUrl(url);

    }


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_BACK) && wvRadiumDetail.canGoBack()) {
//            wvRadiumDetail.goBack(); //goBack()表示返回WebView的上一页面
//            return true;
//        }
//        return false;
//    }
}
