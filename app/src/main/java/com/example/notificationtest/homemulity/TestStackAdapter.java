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
import com.example.notificationtest.homemulity.LeCardView;

public class TestStackAdapter extends StackAdapter<Integer> {

    public TestStackAdapter(Context context) {
        super(context);
    }

    @Override
    public void bindView(Integer data, int position, CardStackView.ViewHolder holder) {
        ColorItemViewHolder h = (ColorItemViewHolder) holder;
        h.onBind(data, position);
    }

    @Override
    protected CardStackView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
//        View view = getLayoutInflater().inflate(R.layout.list_card_item, parent, false);
        View view = LeCardView.buildFragemnt(parent).contentView;
        return new ColorItemViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    static class ColorItemViewHolder extends CardStackView.ViewHolder {
        View mLayout;
        View mContainerContent;
        TextView mTextTitle;

        public ColorItemViewHolder(View view) {
            super(view);
            mLayout = view.findViewById(R.id.frame_list_card_item);
            mTextTitle = (TextView) view.findViewById(R.id.text_list_card_title);
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
