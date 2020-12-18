package com.example.notificationtest.cardstack.ui;

import android.content.Context;
import android.view.ViewGroup;

import com.example.notificationtest.cardstack.CardStackView;
import com.example.notificationtest.cardstack.StackAdapter;
import com.example.notificationtest.homemulity.LeHomeView;
import com.example.notificationtest.homemulity.LeWindowInfo;

import java.util.List;

public class CardStackAdapter extends StackAdapter<LeWindowInfo> {
    private Context mContext;
    List<LeWindowInfo> mWindowList;

    public CardStackAdapter(Context context, List<LeWindowInfo> mWindowList) {
        super(context);
        mContext = context;
        this.mWindowList = mWindowList;
    }

    @Override
    public void bindView(LeWindowInfo data, int position, CardStackView.ViewHolder holder) {
        if (holder instanceof LeHomeViewHolder) {
            LeHomeViewHolder h = (LeHomeViewHolder) holder;
            h.onBind(data, position);
        }
    }

    @Override
    protected CardStackView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        LeHomeView itemView = new LeHomeView(mContext, parent);
        return new LeHomeViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    static class LeHomeViewHolder extends CardStackView.ViewHolder {
        LeHomeView homeView;

        public LeHomeViewHolder(LeHomeView homeView) {
            super(homeView.contentView);
            this.homeView = homeView;
        }

        @Override
        public void onItemExpand(boolean b) {
        }

        public void onBind(LeWindowInfo data, int position) {
            homeView.onBind(data, position);

        }
    }

}
