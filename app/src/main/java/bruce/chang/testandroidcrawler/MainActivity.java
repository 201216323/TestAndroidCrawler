package bruce.chang.testandroidcrawler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.info_list_view)
    ListView info_list_view;
    private List<RadiumBean> list = new ArrayList<>();
    private ProgressDialog dialog;
    private String url = "http://www.dianping.com/search/category/2/45";
    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36";
    @BindView(R.id.tvLastPage)
    TextView tvLastPage;

    @BindView(R.id.tvCurrentPage)
    TextView tvCurrentPage;

    @BindView(R.id.tvNextPage)
    TextView tvNextPage;

    List<Map<String, Object>> mMapList = new ArrayList<>();
    private String curPage;
    boolean firstLoad = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        switchOver();
        info_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, RadiumDetailActivity.class);
                intent.putExtra("url", list.get(i).getTargetUrl());
                startActivity(intent);
            }
        });
        tvLastPage.setOnClickListener(this);
        tvCurrentPage.setOnClickListener(this);
        tvNextPage.setOnClickListener(this);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Connection conn = Jsoup.connect(url);
            // 修改http包中的header,伪装成浏览器进行抓取
            conn.header("User-Agent", userAgent);
            Document doc = null;
            try {
                doc = conn.get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 获取页数的链接
            if (firstLoad) {
                Elements elementsPages = doc.getElementsByClass("content-wrap");
                Elements elementsPageA = elementsPages.first().getElementsByClass("shop-wrap").first().child(1).getElementsByTag("a");
                for (int i = 0; i < elementsPageA.size() - 2; i++) {
                    Element element = elementsPageA.get(i);
                    Element element1 = element.getElementsByClass("cur").first();
                    Map<String, Object> map = new HashMap<>();
                    if (element1 != null) {
                        curPage = element1.text();
                        map.put("page", "" + (i + 1));
                        map.put("url", url);
                        mMapList.add(map);
                    } else {
                        map.put("page", "" + (i + 1));
                        map.put("url", element.attr("href"));
                        mMapList.add(map);
                    }

                }
            }
            firstLoad = false;
            //获取场馆的数据
            Element elementDiv = doc.getElementById("shop-all-list");
            Elements elementsUl = elementDiv.getElementsByTag("ul");
            Elements elements = elementsUl.first().getElementsByTag("li");
            for (Element element : elements) {
                Elements elements1 = element.children();
                String targetUrl = elements1.get(0).getElementsByTag("a").attr("href");

                String img = elements1.get(0).getElementsByTag("img").first().attr("data-src");
                if (img.contains(".jpg")) {
                    int a = img.indexOf(".jpg");
                    img = img.substring(0, a + 4);
                }

                String radiumName = elements1.get(1).child(0).getElementsByTag("h4").text();
                String address0 = elements1.get(1).child(2).getElementsByTag("a").get(1).text();

                String address1 = elements1.get(1).child(2).getElementsByClass("addr").text();
//                StringBuilder stringBuilder = new StringBuilder();
//
//                if (elements1.get(2).child(0).children().size()>0){
//                    String  youhui = "";
//                    if (!"".equals(elements1.get(2).child(0).child(0).getElementsByClass("more").text())){
//                        youhui = elements1.get(2).child(0).getElementsByTag("a").get(1).attr("title");
//                    }else {
//                        youhui = elements1.get(2).child(0).getElementsByTag("a").get(1).attr("title");
//
//                    }
//
//                    stringBuilder.append(youhui+"+++");
//                }
                RadiumBean radiumBean = new RadiumBean();
                radiumBean.setTargetUrl("http://www.dianping.com" + targetUrl);
                radiumBean.setImg(img);
                radiumBean.setName(radiumName);
                radiumBean.setAddress(address0 + " " + address1);
                list.add(radiumBean);
            }
            // 执行完毕后给handler发送一个空消息
            Message message = new Message();
            message.arg1 = Integer.parseInt(curPage);
            handler.sendMessage(message);
        }
    };


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 收到消息后执行handler
            show();
            tvCurrentPage.setText("" + msg.arg1);
        }
    };

    // 将数据填充到ListView中
    private void show() {
        if (!list.isEmpty()) {
            MyAdapter adapter = new MyAdapter(list, MainActivity.this);
            info_list_view.setAdapter(adapter);
        }
        dialog.dismiss();
    }

    // 重新抓取
    public void switchOver() {
        if (isNetworkAvailable(MainActivity.this)) {
            // 显示“正在加载”窗口
            dialog = new ProgressDialog(this);
            dialog.setMessage("正在抓取数据...");
            dialog.setCancelable(false);
            dialog.show();

            list.clear();
            new Thread(runnable).start();  // 子线程

        } else {
            // 弹出提示框
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("当前没有网络连接！")
                    .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switchOver();
                        }
                    }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);  // 退出程序
                }
            }).show();
        }
    }

    // 判断是否有可用的网络连接
    public boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;
        else {   // 获取所有NetworkInfo对象
            NetworkInfo[] networkInfo = cm.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++)
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;  // 存在可用的网络连接
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvLastPage:
                if (curPage.equals("1")) {
                    Toast.makeText(this, "首页", Toast.LENGTH_SHORT).show();
                } else {
                    curPage = "" + (Integer.parseInt(curPage) - 1);

                    if (curPage.equals(1)) {
                        url = "http://www.dianping.com/search/category/2/45";
                    } else {

                        url = "http://www.dianping.com" + mMapList.get(Integer.parseInt(curPage) - 1).get("url").toString();
                    }
                    switchOver();
                    tvCurrentPage.setText(curPage);
                }

                break;
            case R.id.tvCurrentPage:
                switchOver();
                break;
            case R.id.tvNextPage:

                if (curPage.equals("" + (mMapList.size()))) {
                    Toast.makeText(this, "末页", Toast.LENGTH_SHORT).show();
                } else {
                    curPage = "" + (Integer.parseInt(curPage) + 1);
                    url = "http://www.dianping.com" + mMapList.get(Integer.parseInt(curPage) - 1).get("url").toString();
                    switchOver();
                    tvCurrentPage.setText(curPage);
                }
                break;
        }
    }
}
