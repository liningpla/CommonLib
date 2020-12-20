package com.example.notificationtest.oldmutil;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 支持头部和尾部添加View的RecyclerView.Adapter
 * Created by lining
 */

public abstract class LeBaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	public static final int TYPE_HEADER = 1000;
	public static final int TYPE_FOOTER = 1001;

	private List<T> mDatas = new ArrayList<>();

	public List<T> getmDatas() {
		return mDatas;
	}

	private View mHeaderView;
	private View mFooterView;

	public View getFooterView() {
		return mFooterView;
	}

	public void setFooterView(View mFooterView) {
		this.mFooterView = mFooterView;
		// 动态添加的核心
		notifyItemInserted(getItemCount() - 1);
	}

	private OnItemClickListener mListener;

	public void setOnItemClickListener(OnItemClickListener li) {
		mListener = li;
	}

	public void setHeaderView(View headerView) {
		mHeaderView = headerView;
		notifyItemInserted(0);
	}

	public View getHeaderView() {
		return mHeaderView;
	}

	public void addDatas(List<T> datas) {
		if(datas != null){
			mDatas = datas;
		}
	}
	public void notifyDatas(List<T> datas) {
		if(datas != null){
			mDatas = datas;
		}
		notifyDataSetChanged();
	}
	@Override
	public int getItemViewType(int position) {
		if (mHeaderView == null && mFooterView == null) {
			return getItemType(position);
		} else if (mHeaderView != null && position == 0) {
			return TYPE_HEADER;
		} else if (mFooterView != null && position == getItemCount() - 1) {
			return TYPE_FOOTER;
		} else {
			return getItemType(position);
		}
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
		if (mHeaderView != null && viewType == TYPE_HEADER)
			return new Holder(mHeaderView);
		if (mFooterView != null && viewType == TYPE_FOOTER)
			return new Holder(mFooterView);
		return onCreate(parent, viewType);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
		if (getItemViewType(position) == TYPE_HEADER)
			return;
		if (getItemViewType(position) == TYPE_FOOTER)
			return;

		final int pos = getRealPosition(viewHolder);
		final T data = mDatas.get(pos);
		onBind(viewHolder, pos, data);
		if (mListener != null) {
			viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mListener.onItemClick(pos, data);
				}
			});
		}
		return;
	}

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);

		RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
		if (manager instanceof GridLayoutManager) {
			final GridLayoutManager gridManager = ((GridLayoutManager) manager);
			gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
				@Override
				public int getSpanSize(int position) {
					if (getItemViewType(position) == TYPE_HEADER || getItemViewType(position) == TYPE_FOOTER) {
						return gridManager.getSpanCount();
					} else {
						return 1;
					}
				}
			});
		}
	}

	@Override
	public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
		super.onViewAttachedToWindow(holder);
		ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
		if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams && holder.getLayoutPosition() == 0) {
			StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
			p.setFullSpan(true);
		}
	}

	public int getRealPosition(RecyclerView.ViewHolder holder) {
		int position = holder.getLayoutPosition();
		return mHeaderView == null ? position : position - 1;
	}

	@Override
	public int getItemCount() {
		if (mDatas != null) {
			if (mHeaderView != null && mFooterView == null) {
				return mDatas.size() + 1;
			}
			if (mHeaderView == null && mFooterView != null) {
				return mDatas.size() + 1;
			}
			if (mHeaderView != null && mFooterView != null) {
				return mDatas.size() + 2;
			}
			return mDatas.size();
		} else {
			return 0;
		}
	}

	public abstract RecyclerView.ViewHolder onCreate(ViewGroup parent, final int viewType);

	public abstract void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, T data);

	public abstract int getItemType(int position);

	public class Holder extends RecyclerView.ViewHolder {
		public Holder(View itemView) {
			super(itemView);
		}
	}

	public interface OnItemClickListener<T> {
		void onItemClick(int position, T data);
	}


	/**是否滑动到底部了*/
	public boolean isSlideToBottom(RecyclerView recyclerView) {
		if (recyclerView == null) return false;
		if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange())
			return true;
		return false;
	}
}
