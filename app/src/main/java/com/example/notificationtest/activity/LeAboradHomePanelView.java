package com.example.notificationtest.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.notificationtest.R;
import com.example.notificationtest.httplib.HiHttp;
import com.example.notificationtest.httplib.HiLog;

import java.util.ArrayList;
import java.util.List;

/**首页推荐门户板块内容构造类*/
public class LeAboradHomePanelView extends LeAboradBaseView{

    private List<AbroadPanelItem> panelItems;
    private FrameLayout mRootView;
    /**一行最大显示数*/
    private final int LINE_MAX = 5;
    /**每行对屏幕的顶部边距*/
    private final int LINE_MARGN_TOP = 10;
    /**item视图的高度*/
    private int ITEM_VIEW_HEIGHT = 60;

    /**item的宽度*/
    private int itemWidth;
    /**item视图的高度*/
    private int itemViewHeight;
    /**每行对屏幕的顶部边距*/
    private int margnTop;

    private int spaceWidth;

    public LeAboradHomePanelView(Context context, ViewGroup layout){
        super(context, layout);
        mRootView = (FrameLayout) rootView;
        itemWidth = LeUI.getScreenWidth(mContext) / LINE_MAX;
        itemViewHeight = LeUI.getDensityDimen(mContext, ITEM_VIEW_HEIGHT);
        margnTop = LeUI.getDensityDimen(mContext, LINE_MARGN_TOP);
        /*每个item间的横向距离：屏幕宽度 - 每行最大数的item宽度和 - 左右屏幕边距， 在除以间距的个数*/
        spaceWidth = (LeUI.getScreenWidth(context) - itemWidth * LINE_MAX) / (LINE_MAX - 1);
        initData();
    }


    public FrameLayout.LayoutParams initLayoutPamras(int index){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(itemWidth, itemViewHeight);
        int line = index / LINE_MAX;
        int column = index % LINE_MAX;
        HiLog.i(HiHttp.TAG, "column:"+column+"  index:"+index+"  line:"+line);
        if(index % LINE_MAX == 0){
            layoutParams.leftMargin = 0;
        }else{
            layoutParams.leftMargin = spaceWidth + column * itemWidth;
        }
        layoutParams.topMargin = line * itemViewHeight + margnTop * line;
        return layoutParams;
    }

    /**初始化数据*/
    private void initData(){
        panelItems = new ArrayList<>();
        for(int i = 0; i < 17; i ++){
            AbroadPanelItem panelItem = new AbroadPanelItem();
            panelItem.setLayoutParams(initLayoutPamras(i));
            panelItem.setImage("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1566886533672&di=2daddd70fed2c44b68fd8b8ac17e4529&imgtype=0&src=http%3A%2F%2Fphoto.16pic.com%2F00%2F51%2F76%2F16pic_5176356_b.jpg");
            panelItem.setName("百度"+i);
            panelItem.setUrl("https://www.baidu.com");
            panelItems.add(panelItem);
        }
    }

    private ViewGroup getItemView(AbroadPanelItem panelItem){
        ViewGroup itemView = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.item_abroad_home_panel, null);
        ImageView itemImage = itemView.findViewById(R.id.iv_panel_item);
//        Picasso.with(mContext).load(panelItem.getUrl()).into(itemImage);
        TextView itemText = itemView.findViewById(R.id.tv_panel_item);
        itemText.setText(panelItem.getName());
        itemView.setOnClickListener(new ClickListener(panelItem));
        itemView.getHeight();
        return itemView;
    }

    private class ClickListener implements View.OnClickListener{
        AbroadPanelItem panelItem;
        public ClickListener(AbroadPanelItem panelItem) {
            this.panelItem = panelItem;
        }
        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public void laodView() {
        for (AbroadPanelItem panelItem: panelItems){
            mRootView.addView(getItemView(panelItem), panelItem.getLayoutParams());
        }
    }
}
