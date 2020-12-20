package com.example.notificationtest.homemulity;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.notificationtest.R;
import com.example.notificationtest.cardstack.CardStackView;
import com.example.notificationtest.cardstack.StackAdapter;

public class TestStackAdapter extends StackAdapter<Integer> {

    public TestStackAdapter(Context context) {
        super(context);
    }

    @Override
    public void bindView(Integer data, int position, CardStackView.ViewHolder holder) {
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
        }

        public void onBind(Integer data, int position) {
            mLayout.getBackground().setColorFilter(ContextCompat.getColor(getContext(), data), PorterDuff.Mode.SRC_IN);
            mTextTitle.setText(String.valueOf(position));
        }
    }
}
