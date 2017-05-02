package com.shuaijie.codenote.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shuaijie.codenote.R;


/**
 * 作者:姜帅杰
 * 版本:1.0
 * 创建日期:2016/9/3:23:19.
 */
public class MenuAdapter extends BaseAdapter {
    private String[] menu = {"上传到云端", "下载到本地", "意见反馈", "关于"};
    private int[] resId = {R.mipmap.upload_clouds, R.mipmap.download_clouds, R.mipmap.ic_feedback, R.mipmap.ic_about};
    private Context context;

    public MenuAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return menu.length;
    }

    @Override
    public Object getItem(int i) {
        return menu[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View menuItem = View.inflate(context, R.layout.menu_item, null);
        TextView tv_menu = (TextView) menuItem.findViewById(R.id.tv_menu);
        ImageView iv_menu = (ImageView) menuItem.findViewById(R.id.iv_menu);
        iv_menu.setImageResource(resId[i]);
        tv_menu.setText(menu[i]);
        return menuItem;
    }
}
