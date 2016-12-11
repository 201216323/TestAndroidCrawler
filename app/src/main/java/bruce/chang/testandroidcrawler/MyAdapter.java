package bruce.chang.testandroidcrawler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by: BruceChang
 * Date on : 2016/12/11.
 * Time on: 15:29
 * Progect_Name:TestAndroidCrawler
 * Source Github：
 * Description:
 */

public class MyAdapter extends BaseAdapter {

    List<RadiumBean> mRadiumBeanList;
    Context mContext;

    public MyAdapter(List<RadiumBean> radiumBeanList, Context context) {
        mRadiumBeanList = radiumBeanList;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mRadiumBeanList.size();
    }

    @Override
    public Object getItem(int i) {
        return mRadiumBeanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view==null){
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_layout,viewGroup,false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        RadiumBean radiumBean = mRadiumBeanList.get(i);
        Picasso.with(mContext)
                .load(radiumBean.getImg())
                .into(viewHolder.ivRadiumLogo);
        viewHolder.tvRadiumName.setText(radiumBean.getName());
        viewHolder.tvRadiumAddress.setText(radiumBean.getAddress());
        return view;
    }

    class ViewHolder {

        @BindView(R.id.ivRadiumLogo)
        ImageView ivRadiumLogo;
        @BindView(R.id.tvRadiumName)
        TextView tvRadiumName;
        @BindView(R.id.tvRadiumAddress)
        TextView tvRadiumAddress;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
