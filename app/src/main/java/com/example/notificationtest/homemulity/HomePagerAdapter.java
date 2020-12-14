package com.example.notificationtest.homemulity;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**首页多窗口适配器*/
public class HomePagerAdapter extends PagerAdapter {

    private Context context;
    private List<LeWindowInfo> windowInfos = new ArrayList<>();

    public HomePagerAdapter(Context context, List<LeWindowInfo> windowInfos){
        this.context = context;
        if(windowInfos != null){
            this.windowInfos = windowInfos;
        }
    }

    public void notifyChange(List<LeWindowInfo> windowInfos){
        if(windowInfos != null){
            this.windowInfos = windowInfos;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return windowInfos.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LeHomeView homeView = null;
        if(windowInfos != null){
            LeWindowInfo windowInfo = windowInfos.get(position);
            windowInfo.position = position;
            homeView = LeHomeView.buildFragemnt(container, windowInfo);
            homeView.addToParent();
            Log.i(MultiWindowActivity.TAG, "----instantiateIteme----position = "+position);
        }
        return homeView.contentView;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
