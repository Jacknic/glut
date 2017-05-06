package com.jacknic.glut.fragments.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.activity.BrowserActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import me.gujun.android.taggroup.TagGroup;
import okhttp3.Call;
import okhttp3.Response;


/**
 * 图书
 */
public class Fragment_ts extends Fragment {
    private ImageView iv_search_book_shadow;
    private EditText et_search_book;
    private ImageView iv_search_book;
    private View fragment;
    private TagGroup hot_tag;
    List<String> tags = new ArrayList<String>();
    private TextView tv_change_tags;
    private int tag_part = 0;
    private FloatingActionButton ts_fab_mine;
    private CardView cv_buttons;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_ts, container, false);
        initVIew();
        setOnClick();
        return fragment;
    }

    /**
     * 初始化视图
     */
    private void initVIew() {
        et_search_book = (EditText) fragment.findViewById(R.id.et_search_book);
        iv_search_book_shadow = (ImageView) fragment.findViewById(R.id.iv_search_book_shadow);
        iv_search_book = (ImageView) fragment.findViewById(R.id.ts_iv_search_book);
        hot_tag = (TagGroup) fragment.findViewById(R.id.hot_tag);
        ts_fab_mine = (FloatingActionButton) fragment.findViewById(R.id.ts_fab_mine);
        cv_buttons = (CardView) fragment.findViewById(R.id.ts_cv_buttons);
        tv_change_tags = (TextView) fragment.findViewById(R.id.ts_tv_change_tag_list);
    }


    /**
     * 显示标签
     */
    private void showTags() {
        ArrayList<String> showTags = new ArrayList<String>();
        for (int i = tag_part; i < tags.size() && i < tag_part + 15; i++) {
            showTags.add(tags.get(i));
        }
        hot_tag.setTags(showTags);
        tag_part = (tag_part + 15) % tags.size();
    }

    /**
     * 设置点击事件
     */
    private void setOnClick() {
        et_search_book.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    tv_change_tags.callOnClick();
                    iv_search_book_shadow.setAlpha(1.0f);
                } else {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_search_book.getWindowToken(), 0); //强制隐藏键盘
                    iv_search_book_shadow.setAlpha(0.0f);
                }
            }
        });
        ts_fab_mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.scale_fade_in);
                if (cv_buttons.getVisibility() == View.VISIBLE) {
                    anim = AnimationUtils.loadAnimation(getContext(), R.anim.scale_fade_out);
                    cv_buttons.setVisibility(View.GONE);
                } else {
                    cv_buttons.setVisibility(View.VISIBLE);
                }
                anim.setDuration(300L);
                cv_buttons.startAnimation(anim);
            }
        });
        iv_search_book.setOnClickListener(listener);
        fragment.setOnClickListener(listener);
        tv_change_tags.setOnClickListener(listener);
        hot_tag.setOnClickListener(listener);
        hot_tag.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                hot_tag.callOnClick();
                et_search_book.setText(tag.replaceFirst("\\(\\d+\\)", "").trim());
            }
        });

    }

    //点击事件监听器
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ts_iv_search_book:
                    Intent intent = new Intent(getContext(), BrowserActivity.class);
                    intent.setAction("http://202.193.80.181:8081/search?xc=3&kw=" + et_search_book.getText().toString());
//                Snackbar.make(fragment, "搜索图书: " + et_search_book.getText(), Snackbar.LENGTH_SHORT).show();
                    getActivity().startActivity(intent);
                    break;
                case R.id.ts_tv_change_tag_list:
                    if (tags.size() == 0) {
                        getTags();
                    } else {
                        showTags();
                    }
                    break;
            }
            if (cv_buttons.getVisibility() == View.VISIBLE) {
                ts_fab_mine.callOnClick();
            }
        }
    };

    /**
     * 获取标签
     */
    void getTags() {
        OkGo.get("http://202.193.80.181:8080/opac/hotsearch").execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                Document document = Jsoup.parse(s);
                Elements elements = document.select(".orderTable tr td a");
                for (Element element : elements) {
                    tags.add(element.text());
                }
                showTags();
            }
        });
    }

    public static Fragment newInstance() {
        return new Fragment_ts();
    }
}
