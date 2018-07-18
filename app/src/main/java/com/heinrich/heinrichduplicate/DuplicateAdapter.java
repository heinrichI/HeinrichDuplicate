package com.heinrich.heinrichduplicate;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

public class DuplicateAdapter extends BaseAdapter {
    private class ListInfo {
        public String Header;
        public int TypeSeparator;
        public ListInfo(String header, int typeSeparator) {
            Header = header;
            TypeSeparator = typeSeparator;
        }
    }

    class ViewHolder {
        public TextView textView;
        public CheckedTextView checkedTextView;
        public ImageView imageView;
    }

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private LinkedList<ListInfo> _data = new LinkedList<ListInfo>();

    private SparseArray<FileInfo> _mapPositionFileInfo = new SparseArray<FileInfo>();

    private LayoutInflater _inflater;

    Context _context;

    public DuplicateAdapter(Context context) {
        _context = context;
        _inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        return _data.get(position).TypeSeparator;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return _data.size();
    }

    @Override
    public String getItem(int position) {
        return _data.get(position).Header;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = _inflater.inflate(R.layout.dupl_item, null);
                    holder.checkedTextView = (CheckedTextView) convertView.findViewById(R.id.checkedTextView);
                    holder.imageView = (ImageView) convertView.findViewById(R.id.img);
                    break;
                case TYPE_SEPARATOR:
                    convertView = _inflater.inflate(R.layout.dupl_header_item, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.textSeparator);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        switch (rowType) {
            case TYPE_SEPARATOR:
                holder.textView.setText(_data.get(position).Header);
                break;
            case TYPE_ITEM:
                holder.checkedTextView.setText(_data.get(position).Header);
                FileInfo info = _mapPositionFileInfo.get(position);
                if (info != null) {
                    holder.checkedTextView.setChecked(info.Checked);
                    holder.imageView.setImageBitmap(BitmapFactory.decodeFile(info.Path));
                }

                break;
        }

        return convertView;
    }

    public void Clear()
    {
        _mapPositionFileInfo.clear();
        _data.clear();
    }

    public void addGroups(List<DuplGroup> groups) {
        Clear();
        for (DuplGroup g : groups) {
            //_mapPositionFileInfo.append(_data.size(), g);
            _data.add(new ListInfo(g.Header, TYPE_SEPARATOR));
            for (FileInfo info :g.Files) {
                _mapPositionFileInfo.append(_data.size(), info);
                _data.add(new ListInfo(info.Path, TYPE_ITEM));
            }
        }
        notifyDataSetChanged();
    }

    void toggleChecked(int position) {
        /*boolean allChecked = true;
        for (int i = 0; i < mCheckedMap.size(); i++)
        {
            if (!mCheckedMap.get(i)
                    && i != position)
            {
                allChecked = false;
                break;
            }
        }
        if (allChecked)
        {
            Toast.makeText(_context.getApplicationContext(), "Вы не можете выбрать все записи!", Toast.LENGTH_LONG)
                    .show();
            return;
        }*/

        FileInfo info = _mapPositionFileInfo.get(position);
        if (info != null)
        {
            if (info.Group.IsAllChecked(info))
            {
                Toast.makeText(_context.getApplicationContext(), "Вы не можете выбрать все записи!", Toast.LENGTH_LONG)
                        .show();
                return;
            }

            info.Checked = !info.Checked;
        }

        notifyDataSetChanged();
    }

    public void SetByText(String value) {
        for(int i = 0; i < _mapPositionFileInfo.size(); i++) {
            FileInfo info = _mapPositionFileInfo.valueAt(i);
            if (info.Path.contains(value)) {
                if (info.Group.IsAllChecked(info)) {
                    Toast.makeText(_context.getApplicationContext(), "Вы не можете выбрать все записи!", Toast.LENGTH_LONG)
                            .show();
                    continue;
                }
                info.Checked = true;
            }
        }
    }
}
