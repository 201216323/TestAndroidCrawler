package bruce.chang.testandroidcrawler;

import java.io.Serializable;

/**
 * Created by: BruceChang
 * Date on : 2016/12/11.
 * Time on: 15:30
 * Progect_Name:TestAndroidCrawler
 * Source Github：
 * Description:
 */

public class RadiumBean implements Serializable {
    private String targetUrl;
    private String img;
    private String name;
    private String address;

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
