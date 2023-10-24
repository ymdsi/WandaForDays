package com.example.sinupsample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CustomListAdapter extends ArrayAdapter<MemoData> {
    private final int Resource;
    private final List<MemoData> _MemoData;
    public CustomListAdapter(@NonNull Context context, int resource, List<MemoData> memoData) {
        super(context, resource, memoData);
        Resource = resource;
        _MemoData = memoData;
    }
    @Override
    public int getCount() {
        return super.getCount();
    }
    @Nullable
    @Override
    public MemoData getItem(int position) {
        return super.getItem(position);
    }
    //リストカードに日付、メモタイトル、メモ内容を表示させる
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder = new ViewHolder();
        View v;
        if (convertView != null){
            v = convertView;
        } else{
            v = LayoutInflater.from(getContext()).inflate(Resource, null);
            holder.memoDate = v.findViewById(R.id.memo_date);
            holder.memoTitle = v.findViewById(R.id.memo_title);
            holder.memoContents = v.findViewById(R.id.memo_contents);
        }
        MemoData data = _MemoData.get(position);
        holder.memoDate = v.findViewById(R.id.memo_date);
        holder.memoDate.setText(data.get_date());
        holder.memoTitle = v.findViewById(R.id.memo_title);
        holder.memoTitle.setText(data.get_memoTitle());
        holder.memoContents = v.findViewById(R.id.memo_contents);
        holder.memoContents.setText(data.get_memoContents());
        return v;
    }
    static class ViewHolder{
        TextView memoDate,memoTitle,memoContents;
    }
}