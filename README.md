# TestAndroidCrawler
网络爬虫技术Jsoup的使用，来获取大众点评 健身场馆的有关数据

> 本文由我的微信公众号（bruce常）原创首发，
并同步发表到csdn博客，欢迎转载，2016年12月11日。

### 概述：

本周五，接到一个任务，要使用爬虫技术来获取某点评网站里面关于健身场馆的数据，之前从未接触过爬虫技术，于是就从网上搜了一点学习资料，本篇文章就记录爬虫技术Jsoup技术，爬虫技术听名称很牛叉，其实没什么难点，慢慢的用心学习就会了。
### Jsoup介绍：
Jsoup 是一个 Java 的开源HTML解析器，可直接解析某个URL地址、HTML文本内容，Jsoup官网jar包[下载地址](https://jsoup.org/download)。

Jsoup主要有以下功能：
1. 从一个URL，文件或字符串中解析HTML
2. 使用DOM或CSS选择器来查找、取出数据
3. 对HTML元素、属性、文本进行操作
4. 清除不受信任的HTML (来防止XSS攻击)


### 使用Jsoup爬虫技术你需要的能力有：
1. 我们是用安卓开发的，首先肯定要有一定的安卓开发能力，会写简单的页面。
2. Jsoup中用到了Javascript语言，没有此语言能力在获取数据的时候就比较吃力，这是此爬虫技术的重中之重。
3. 查阅文档与解决问题的能力和技巧（有点废话）

上面三条中对于一个安卓开发者来说，最难的就是熟练使用Javascript语言，小编就遇到了这个问题，小编还有一定的javascript基础，系统的学习过此语言，但是在使用中还是很吃力的，问同学、问朋友、问同事，最后还是靠自己来获取自己想要的数据。

### 爬虫技术没那么难，思路就是这么的简单

1. 得到自己想要爬取数据的url.
2. 通过Jsoup的jar包中的方法将Html解析成Document，
3. 使用Document中的一些列get、first、children等方法获取自己想要的数据，如图片地址、名称、时间。
4. 将得到的数据封装成自己的实体类。
5. 将实体中的数据在页面加载出来。

### 实战，获取**点评网站中的场馆数据：

###### 先奉上效果图，没有图不说话：
![image](http://a4.qpic.cn/psb?/V10Llwbb1wSOar/COp7gE.LCkWeebjFo0FHzIvXONixFaqsOQsBICE2WVE!/b/dGcBAAAAAAAA&ek=1&kp=1&pt=0&bo=TgFeAk4BXgICCCw!&tm=1481464800&sce=0-12-12&rf=0-18)

这就是今天要实现的效果，左边图片是场馆的logo，右边上方是场馆的名称，下边是场馆的地址信息，点击进去可以根据超链接地址跳转新的页面，页面的Url地址小编已经拿到，但可能是因为重定向的问题，webview没有加载出来，有兴趣的可以输入链接地址来验证。

#### 首先：新建一个空的项目.

上面的效果，只要接触过安卓开发的都能写出来，所以不是本篇文章的重点，这里就不过多说明，大家可以使用ListView或者RecyclerView来实现，我这里用ListView。

小编这里是为了加入侧边栏所以使用的是DrawerLayout，但后来没有用到，所以也就没有侧边栏的效果，不过后期如有时间会加上去的，上一页下一页是为了简单的模仿浏览器中的操作，此效果只能显示前9页数据，网页链接中有50页的数据，为什么没有实现呢？

很简单，因为50页的链接地址不是一次性返回的，小编为了方便，只获取了前9页数据的url，毕竟是为了抓取数据显示而已。

#### 其次：主程序设计

1. 通过网页得到**点评健身场馆的url地址是：http://www.dianping.com/search/category/2/45
2. 抓取数据是一个耗时的操作，需要在一个线程中完成，这里使用 new Thread(runnable).start()方式，在runnable代码中获取场馆的logo、名称、地址如下：
```
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

                RadiumBean radiumBean = new RadiumBean();
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
```

>1. 通过Jsoup.connect()方法，根据目标地址url来得到Connection对象，
>2. 将我们的app伪装成浏览器，防止人家后台发现我们在爬取人家的数据，这需要修改修改http包中的header，来设置User-Agent，此值可以在谷歌浏览器中输入“about:version”来查看，也可以访问此[地址查看](http://www.966266.com/jishu/user-agent-chaxun.html)。
>3.  通过Connection对象的get()方法来获得整个页面源代码所在的Document
>4.  通过分析源代码，使用Document的对象来得到我们想要的数据，上面程序中img待变场馆logo的url，radiumName是小编得到的场馆的名称，address0和address1是小编得到的场馆地址的信息，这里通过组合来使用。
>5.  构造我们ListView所用到的数据
>6.  通过Handle来更新页面信息，curPage（当前页）稍后说明。

3. 在得到数据后页面加载显示
```
if (!list.isEmpty()) {
            MyAdapter adapter = new MyAdapter(list, MainActivity.this);
            info_list_view.setAdapter(adapter);
        }
```

4.点击跳转到场馆的详情页，这里本想用Webview加载的，但是可能是网页重定向的问题，webview也能加载出来，但一会就显示无法连接网络，所以场馆详情页就显示出了我们得到的场馆详情页的url。

基本的抓取数据、加载数据流程就是这样的，但是仅仅靠上面的数据还是不能完全实现我们的效果的。

### 完善页面，实现上下页翻页功能。

1. 页面在爬取数据的时候显示一个ProgressDialog来提示用户。

```
ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("正在抓取数据...");
            dialog.setCancelable(false);
            dialog.show();
```
数据加载完毕，关闭此dialog。

```
 dialog.dismiss();
```
2.ProgresDialog加载前做是否有网络的判断，有网的时候才显示ProgressDialog，无网络的时候给出提示。

```
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
```

3.完善runnable，抓取当前页码、上一页、下一页的链接地址。

```
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
```

因为在网页中，第一次进入返回了前9页和第50页的数据，这里只取前9页的数据，firstLoad代表第一次加载，mMapList用来存放页码和页面跳转时候的url，对js中的代码不明白的朋友们，要好好学学js，这里小编就不介绍js了，至于我为什么知道取这些字段，那是小编盯着网页源程序代码看了半天看出来的。

4. 这个时候就用到了之前runnable中的Message对象中的curPage

curPage代表当前页码，从1开始………………在handle接收到消息后显示此页码信息。

```
tvCurrentPage.setText("" + msg.arg1);
```

5. 模仿网页的上一页下一页，我们需要处理TextView的点击事件。

下一页事件：
```
if (curPage.equals("" + (mMapList.size()))) {
                    Toast.makeText(this, "末页", Toast.LENGTH_SHORT).show();
                } else {
                    curPage = "" + (Integer.parseInt(curPage) + 1);
                    url = "http://www.dianping.com" + mMapList.get(Integer.parseInt(curPage) - 1).get("url").toString();
                    switchOver();
                    tvCurrentPage.setText(curPage);
                }
```

上一页事件：

```
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
```

经过小编测试，在点击下一页的时候没有bug，在点击上一页的时候，会出现doc为null，从而奔溃的bug，小编在努力解决中，但还没解决掉。

6. 附上完整的runnable代码，毕竟这是此程序的关键部分。

```
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
```
有不明白的可以对照完整的runnable代码来理解。

### 通过上面的步骤，我们已经完成了抓取、加载、上下页切换的效果。但但请看下面。
通过小编的切身体验，发现jsoup爬虫获取数据时候的几个需要注意的地方。
1. 个人要会js，再强调一遍，不会js，上面我写的js的程序应该会非常的迷糊，即便会的人，因为每个人写的也不一样，也是不好看懂的。
2. 我们在爬取数据的时候所用的class  id 等字段一旦发生变化，那就得不到相应的标签了，页面就会发生奔溃，这一点也是致命的一点把。
3. 要想非常逼真的实现网页中的效果，那你就要好好的看看网页的源代码了，网页代码有很大的灵活性，需要你仔细分析记录规律。


### 测试程序已经上传到了github，有需要的可以下载源程序。


下载地址：[点我点我点我](https://github.com/201216323/TestAndroidCrawler)

微信公众号,欢迎添加关注，不定时为大家分享个人开发中的技术文章。

QQ邮箱：1060140613@qq.com，如有疑问可以QQ或者邮件联系我。

![image](http://a3.qpic.cn/psb?/V10Llwbb1wSOar/f.mXHYCxXqBA9FHOkSaPIaaFK8bXsj11Qd190qFsdpw!/b/dGYBAAAAAAAA&ek=1&kp=1&pt=0&bo=AgECAQIBAgEFACM!&tm=1481468400&sce=0-12-12&rf=viewer_311)












 

