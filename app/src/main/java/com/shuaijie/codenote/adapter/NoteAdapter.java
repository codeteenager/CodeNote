package com.shuaijie.codenote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.shuaijie.codenote.R;
import com.shuaijie.codenote.bean.Note;

/**
 * Created by 姜帅杰 on 2016/2/2.
 * 笔记适配器
 */
public class NoteAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Note> data;

    public NoteAdapter(Context context, ArrayList data) {
        this.context = context;
        this.data = data;
    }

    public void refreshData(ArrayList<Note> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Note note = data.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_note, null);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvNoteTitle);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvNoteTime);
            viewHolder.tvId = (TextView) convertView.findViewById(R.id.tvNoteId);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tvNoteContent);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.tvTitle.setText(note.getTitle());
        viewHolder.tvTime.setText(note.getTime());
        viewHolder.tvId.setText(note.getId() + "");
        viewHolder.tvContent.setText(note.getContent());
        return convertView;
    }

    //用于第一次查找的组件，避免下次重复查找。
    private static class ViewHolder {
        TextView tvTitle, tvTime, tvId, tvContent;
    }
}
