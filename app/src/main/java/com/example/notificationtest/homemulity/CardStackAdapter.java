package com.example.notificationtest.homemulity;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.notificationtest.R;
import com.example.notificationtest.cardstack.CardStackView;
import com.example.notificationtest.cardstack.StackAdapter;
import com.example.notificationtest.oldmutil.LeWindowInfo;

public class CardStackAdapter extends StackAdapter<LeWindowInfo> {

    public CardStackAdapter(Context context) {
        super(context);
    }

    @Override
    public void bindView(LeWindowInfo data, int position, CardStackView.ViewHolder holder) {
        CardMainHolder h = (CardMainHolder) holder;
        h.onBind(data, position);
    }

    @Override
    protected CardStackView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        LeCardView leCardView = LeCardView.buildFragemnt(parent);
        return new CardMainHolder(leCardView);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    static class CardMainHolder extends CardStackView.ViewHolder {
        View contentView;
        View mLayout;
        TextView mTextTitle;

        public CardMainHolder(LeCardView leCardView) {
            super(leCardView.contentView);
            contentView = leCardView.contentView;
            mLayout = contentView.findViewById(R.id.frame_list_card_item);
            mTextTitle = (TextView) contentView.findViewById(R.id.text_list_card_title);
        }

        @Override
        public void onItemExpand(boolean b) {
            Log.i(LeCardView.TAG, "----onItemExpand = " + b);
            if(b){
                mLayout.setBackgroundResource(R.drawable.shape_rectangle_no_radius);
            }else{
                mLayout.setBackgroundResource(R.drawable.shape_rectangle_with_radius);
            }
        }

        public void onBind(LeWindowInfo data, int position) {
            mLayout.getBackground().setColorFilter(ContextCompat.getColor(getContext(), data.bgColor), PorterDuff.Mode.SRC_IN);
            mTextTitle.setText(String.valueOf(position));
        }
    }
}
