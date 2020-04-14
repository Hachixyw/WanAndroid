package com.example.wanandroid.adapter;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.wanandroid.R;
import com.example.wanandroid.bean.Article;
import com.example.wanandroid.listener.OnItemClickListener;

import java.util.List;

public class SharedAdapter extends BaseQuickAdapter<Article, SharedAdapter.ViewHolder> implements LoadMoreModule {

    private Context mContext;
    private List<Article> list;
    private Toast mToast;
    private OnItemClickListener onItemClickListener;

    static class ViewHolder extends BaseViewHolder{
        TextView tv_share_title;
        TextView tv_share_name;
        TextView tv_share_time;
        LinearLayout ll_share;

        public ViewHolder(@NonNull View view) {
            super(view);
            tv_share_name=view.findViewById(R.id.tv_shared_name);
            tv_share_time=view.findViewById(R.id.tv_shared_time);
            tv_share_title=view.findViewById(R.id.tv_shared_title);
            ll_share=view.findViewById(R.id.ll_share_share);
        }
    }



    public SharedAdapter(Context context, List<Article> articles){
        //item布局中最外层的layout_height不能为match_parent，否则会导致屏幕只显示一条数据，上拉显示下一条数据
        super(R.layout.item_share,articles);
        this.mContext=context;
        this.list=articles;
    }

    public Article getArticle(int position){
        return list.get(position);
    }


    @Override
    protected void convert(@NonNull final SharedAdapter.ViewHolder viewHolder, final Article article) {
        viewHolder.tv_share_title.setText(Html.fromHtml(article.getTitle(),Html.FROM_HTML_MODE_LEGACY));
        Log.i("Debug","Title:"+article.getTitle()+" Author:"+article.getAuthor()
                +" Collect:"+article.isCollect()+" id:"+article.getId());
        viewHolder.tv_share_time.setText(""+article.getNiceShareDate());
        viewHolder.tv_share_name.setText(article.getShareUser());
        viewHolder.ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(viewHolder.getAdapterPosition(),v,viewHolder);
            }
        });
    }




    private void showToast(String text){
        if (mToast==null){
            mToast=Toast.makeText(mContext,text,Toast.LENGTH_SHORT);
        }else {
            mToast.setText(text);
        }
        mToast.show();
    }

    public void refresh(List<Article> list){
        this.list.addAll(list);
        Log.d("TAG","SharedAdapter list:"+this.list.size());
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removePosition(int position){
        list.remove(position);
        notifyDataSetChanged();
    }
}
