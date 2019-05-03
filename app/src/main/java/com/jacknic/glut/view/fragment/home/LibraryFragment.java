package com.jacknic.glut.view.fragment.home;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.jacknic.glut.page.BorrowPage;
import com.jacknic.glut.util.PageTool;
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
public class LibraryFragment extends Fragment {
    private ImageView iv_search_book_shadow;
    private EditText et_search_book;
    private ImageView iv_search_book;
    private View fragment;
    private TagGroup hot_tag;
    private FloatingActionButton fab_mine;
    List<String> tags = new ArrayList<String>();
    private TextView tv_change_tags;
    private int tag_part = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.frag_library, container, false);
        initView();
        setOnClick();
        return fragment;
    }

    /**
     * 初始化视图
     */
    private void initView() {
        et_search_book = (EditText) fragment.findViewById(R.id.et_search_book);
        iv_search_book_shadow = (ImageView) fragment.findViewById(R.id.iv_search_book_shadow);
        iv_search_book = (ImageView) fragment.findViewById(R.id.iv_search_book);
        hot_tag = (TagGroup) fragment.findViewById(R.id.hot_tag);
        fab_mine = (FloatingActionButton) fragment.findViewById(R.id.fab_mine);
        tv_change_tags = (TextView) fragment.findViewById(R.id.tv_change_tag_list);
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
                    iv_search_book_shadow.setVisibility(View.VISIBLE);
                } else {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        //强制隐藏键盘
                        imm.hideSoftInputFromWindow(et_search_book.getWindowToken(), 0);
                    }
                    iv_search_book_shadow.setVisibility(View.INVISIBLE);
                }
            }
        });
        fab_mine.setOnClickListener(listener);
        iv_search_book.setOnClickListener(listener);
        tv_change_tags.setOnClickListener(listener);
        hot_tag.setOnClickListener(listener);
        hot_tag.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                hot_tag.callOnClick();
                et_search_book.setText(tag.replaceFirst("\\(\\d+\\)", "").trim());
                et_search_book.setSelection(et_search_book.getText().length());
            }
        });

    }

    //点击事件监听器
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_search_book:
                    String url = "http://libopac.glut.edu.cn:8080/opac/search?q=" + et_search_book.getText().toString();
                    PageTool.openWebPage(getActivity(), url);
                    break;
                case R.id.tv_change_tag_list:
                    if (tags.size() == 0) {
                        getTags();
                    } else {
                        showTags();
                    }
                    break;
                case R.id.fab_mine:
                    PageTool.open(getContext(), new BorrowPage());
                    break;
            }
        }
    };

    /**
     * 获取标签
     */
    private void getTags() {
        OkGo.get("http://libopac.glut.edu.cn:8080/opac/hotsearch").execute(new StringCallback() {
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
}
