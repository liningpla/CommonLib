package com.example.notificationtest.homemulity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.common.utils.Utils;
import com.example.notificationtest.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MultiWindowActivity extends ComponentActivity {

    public static final String TAG = "MultiWindow";

    private Button btn_add, btn_show;
    private ClickListener clickListener;
    private RecyclerView rv_multi;
    private List<LeWindowInfo> windowList = new ArrayList<>();
    private MutliWinAdapter mutliWinAdapter;
    private GridLayoutManager layoutManager;
    private ItemTouchHelper mItemTouchHelper;
    private SmallVideoDecoration smallVideoDecoration;
    private int currentIndex;
    private BigDecimal sreenRatio;//当前手机屏幕的宽高比
    private int titleHeight;//条目顶部高度
    private int itemWidth;//条目的宽度
    private int itemHeight;//条目高度
    private int spaceWidth;//空白宽度
    private boolean isMultiType = false;//是否是多窗口模式

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_window);
        clickListener = new ClickListener();
        initData();
        initView();
    }

    private void initData() {
        windowList.add(new LeWindowInfo(0));
        titleHeight = Utils.dp2px(this, 2);
        sreenRatio = new BigDecimal((float) Utils.getScreenWidth(this) / Utils.getScreenHeight(this));
        spaceWidth = Utils.dp2px(this, 2);
        itemWidth = (Utils.getScreenWidth(this)) / 2 - spaceWidth;
        itemHeight = (int) (itemWidth / sreenRatio.doubleValue()) + titleHeight;
    }


    private void initView() {
        btn_add = findViewById(R.id.btn_add);
        btn_show = findViewById(R.id.btn_show);
        btn_add.setOnClickListener(clickListener);
        btn_show.setOnClickListener(clickListener);
        rv_multi = findViewById(R.id.rv_multi);
        mutliWinAdapter = new MutliWinAdapter(this, windowList);
        smallVideoDecoration = new SmallVideoDecoration(Utils.dp2px(this, 2));
        mItemTouchHelper = new ItemTouchHelper(new DragItemHelperCallBack());
        updateWindowState();
    }

    @SuppressLint("WrongConstant")
    private void updateWindowState() {
        if (!isMultiType) {
            Log.i(TAG, "----updateWindowState 1 = " + isMultiType);
            layoutManager = new GridLayoutManager(this, 1){
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            rv_multi.setLayoutManager(layoutManager);
            rv_multi.setAdapter(mutliWinAdapter);
            rv_multi.removeItemDecoration(smallVideoDecoration);
        } else {
            Log.i(TAG, "----updateWindowState 2 = " + isMultiType);
            layoutManager = new GridLayoutManager(this, 2){
                @Override
                public boolean canScrollVertically() {
                    return true;
                }
            };
            rv_multi.addItemDecoration(smallVideoDecoration);
            rv_multi.setLayoutManager(layoutManager);
            rv_multi.setAdapter(mutliWinAdapter);
            mItemTouchHelper.attachToRecyclerView(rv_multi);
        }
        layoutManager.setOrientation(GridLayout.VERTICAL);
        mutliWinAdapter.notifyDatas(windowList);
        rv_multi.scrollToPosition(currentIndex);
    }

    private class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_add:
                    isMultiType = false;
                    windowList.add(new LeWindowInfo(windowList.size() + 1));
                    currentIndex = windowList.size() - 1;
                    updateWindowState();
                    break;
                case R.id.btn_show:
                    isMultiType = true;
                    updateWindowState();
                    break;
            }
        }
    }

    /**
     * 多窗口数据适配器
     */
    public class MutliWinAdapter extends LeBaseRecyclerAdapter<LeWindowInfo> implements IOnItemMoveListener {
        private Context mContext;
        List<LeWindowInfo> mWindowList;

        public MutliWinAdapter(Context context, List<LeWindowInfo> windowList) {
            mContext = context;
            mWindowList = windowList;
            addDatas(mWindowList);
        }

        @Override
        public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
            View itemView = new LeHomeView(mContext, parent).contentView;
            RecyclerView.ViewHolder holder = new MutiliWinHolder(itemView);
            return holder;
        }

        @Override
        public void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, LeWindowInfo data) {
            viewHolder.setIsRecyclable(false);
            final MutiliWinHolder holder = (MutiliWinHolder) viewHolder;
            RecyclerView.LayoutParams layoutParams = new GridLayoutManager.LayoutParams(itemWidth, itemHeight);
            if (!isMultiType && RealPosition == currentIndex) {//当前显示的窗口
                layoutParams = new GridLayoutManager.LayoutParams(Utils.getScreenWidth(mContext), Utils.getScreenHeight(mContext));
            }
            holder.itemView.setLayoutParams(layoutParams);
            holder.tv_home_name.setText("第" + (RealPosition + 1) + "个窗口");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isMultiType = false;
                    currentIndex = RealPosition;
                    updateWindowState();
                }
            });
        }

        @Override
        public int getItemType(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            if (mWindowList != null) {
                return mWindowList.size();
            }
            return 0;
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {

        }

        @Override
        public void onItemDissmiss(int positon) {

        }

        //窗口组件
        class MutiliWinHolder extends RecyclerView.ViewHolder implements IOnDragVHListener {
            private View itemView;
            private TextView tv_home_name;

            public MutiliWinHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                this.tv_home_name = itemView.findViewById(R.id.tv_home_name);
            }

            @Override
            public void onItemSelected() {
            }

            @Override
            public void onItemFinish() {
            }
        }

    }

    public class DragItemHelperCallBack extends ItemTouchHelper.Callback {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            Log.i(TAG, "----getMovementFlags----isMultiType =" + isMultiType);
            if (!isMultiType) {
                return 0;
            }
            int dragFlags = 0;
            // 支持左右滑动(删除)操作, swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Log.i(TAG, "----onMove----isMultiType =" + isMultiType);
            if (!isMultiType) {
                return false;
            }
            //被按下拖拽时候的position
            int fromPosition = viewHolder.getAdapterPosition();
            //当前拖拽到的item的posiiton
            int toPosition = target.getAdapterPosition();

            //回调到adapter 当中处理移动过程中,数据变更的逻辑,以及更新UI
            if (recyclerView.getAdapter() instanceof IOnItemMoveListener) {
                IOnItemMoveListener listener = ((IOnItemMoveListener) recyclerView.getAdapter());
                listener.onItemMove(fromPosition, toPosition);
            }

            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            Log.i(TAG, "----onSwiped----isMultiType =" + isMultiType);
            if (!isMultiType) {
                return;
            }
            //回调到adapter 当中处理移动过程中,数据变更的逻辑,以及更新UI
            if (rv_multi.getAdapter() instanceof IOnItemMoveListener) {
                IOnItemMoveListener listener = ((IOnItemMoveListener) rv_multi.getAdapter());
                listener.onItemDissmiss(viewHolder.getAdapterPosition());
            }
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            Log.i(TAG, "----onSelectedChanged----isMultiType =" + isMultiType);
            if (!isMultiType) {
                return;
            }
            // 不在闲置状态
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                if (viewHolder instanceof IOnDragVHListener) {
                    IOnDragVHListener itemViewHolder = (IOnDragVHListener) viewHolder;
                    itemViewHolder.onItemSelected();
                }
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            Log.i(TAG, "----clearView----isMultiType =" + isMultiType);
            if (!isMultiType) {
                return;
            }
            if (viewHolder instanceof IOnDragVHListener) {
                IOnDragVHListener itemViewHolder = (IOnDragVHListener) viewHolder;
                itemViewHolder.onItemFinish();
            }
            super.clearView(recyclerView, viewHolder);
        }
    }

    /**
     * 拖动视图监听
     */
    private interface IOnItemMoveListener {
        void onItemMove(int fromPosition, int toPosition);

        void onItemDissmiss(int positon);
    }

    /**
     * 拖动操作回调
     */
    private interface IOnDragVHListener {
        void onItemSelected();

        void onItemFinish();
    }
}
