package com.example.notificationtest.gameSdk.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.Serializable;

/**
 * 浏览器Fragment基类
 */
public abstract class LeBaseFragment extends Fragment {

    private static final String KEY_LAYOUT_ID = "key_layout_id";
    private static final String KEY_TAG = "key_tag";
    public View contentView;
    public int layoutID;
    public Bundle bundle;
    public FragmentManager fragmentManager;
    public String currTag;

    public LeBaseFragment contentViewTag(int layoutId, String tag) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putInt(KEY_LAYOUT_ID, layoutId);
        bundle.putString(KEY_TAG, tag);
        currTag = tag;
        return this;
    }

    public LeBaseFragment params(String key, String value) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString(key, value);
        return this;
    }

    public LeBaseFragment paramsSerializable(String key, Serializable value) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putSerializable(key, value);
        return this;
    }

    public LeBaseFragment params(String key, int value) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putInt(key, value);
        return this;
    }

    public void addArguments() {
        if (bundle == null) {
            bundle = new Bundle();
        }
        setArguments(bundle);
    }
    /**
     * 添加Fragement到容器
     *
     * @param container 父容器
     */
    public LeBaseFragment commit(Context context, int container) {
        if (context instanceof FragmentActivity) {
            fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (bundle != null) {
                setArguments(bundle);
            }
            //replace:如果有就先remove它，然后再add它
            transaction.replace(container, this, currTag);
            transaction.commitNowAllowingStateLoss();
        }
        return this;
    }

    /**
     * 删除
     */
    public void remove(Context context) {
        if (context instanceof FragmentActivity) {
            fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Fragment fragment = fragmentManager.findFragmentByTag(currTag);
            transaction.remove(fragment);
            transaction.commit();
            onRemove();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        layoutID = bundle.getInt(KEY_LAYOUT_ID);
        currTag = bundle.getString(KEY_TAG);
        contentView = inflater.inflate(layoutID, container, false);
        contentView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });
        return contentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    /**
     * 查找View
     */
    public <T extends View> T findView(int id) {
        return contentView.findViewById(id);
    }

    /**
     * 初始化View
     */
    public abstract void initView();
    /**
     * 适配主题
     */
    public abstract void applyTheme();

    /**
     * 当frgment被异常调用
     */
    public abstract void onRemove();

    /**
     * 当frgment被异常调用
     */
    public abstract boolean onBackPressed();

}
