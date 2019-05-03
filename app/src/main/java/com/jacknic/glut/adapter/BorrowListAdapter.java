package com.jacknic.glut.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jacknic.glut.R;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 图书借阅适配器
 */
public class BorrowListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private Elements elements;

    public BorrowListAdapter(Context context, Elements elements) {
        inflater = LayoutInflater.from(context);
        this.elements = elements;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_book, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder itemHolder = (ViewHolder) holder;
        itemHolder.bindHolder(elements.get(position));
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }


    private class ViewHolder extends RecyclerView.ViewHolder {
        private View view;

        ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
        }

        /**
         * 设置文字
         *
         * @param id
         * @param text
         */
        private void setText(int id, String text) {
            TextView item_text_view = (TextView) view.findViewById(id);
            item_text_view.setText(text);
        }

        void bindHolder(Element element) {
            final String book_no = element.child(1).text();
            setText(R.id.item_book_no, "编号: " + book_no);
            String book_title = element.child(2).text();
            final AlertDialog alertDialog = new AlertDialog.Builder(inflater.getContext())
                    .setTitle("续借" + "《" + book_title + "》?")
                    .setNegativeButton(android.R.string.cancel, null)
                    .setMessage("  注意：借书（续借）达21天后方可续借，最大续借次数为1")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            renew(book_no);
                        }
                    }).create();
            view.findViewById(R.id.item_book_borrow).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.show();
                }
            });
            setText(R.id.item_book_title, book_title);
            String str_borrow_date = element.child(7).text();
            setText(R.id.item_book_borrow_date, "借出: " + str_borrow_date);
            setText(R.id.item_book_return_date, "应还: " + element.child(8).text());
        }

        /**
         * 续借
         */
        private void renew(String book_no) {

            OkGo.post("http://libopac.glut.edu.cn:8080/opac/loan/doRenew")
                    .params("barcodeList", book_no)
                    .params("furl", "/opac/loan/renewList")
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            Activity activity = (Activity) inflater.getContext();
                            Document document = Jsoup.parse(s);
                            Element content = document.getElementById("content");
                            AlertDialog alertDialog = new AlertDialog.Builder(activity)
                                    .setPositiveButton(android.R.string.ok, null)
                                    .setMessage(content.text())
                                    .create();
                            alertDialog.show();
                        }
                    });
        }
    }
}
