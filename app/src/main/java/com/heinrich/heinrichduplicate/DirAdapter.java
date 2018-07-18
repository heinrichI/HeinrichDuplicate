package com.heinrich.heinrichduplicate;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import java.util.ArrayList;
import java.util.List;

public class DirAdapter extends ArrayAdapter<DirInfo> {
    private final Activity _context;
    List<DirInfo> _objects;
    private SparseBooleanArray _checkedMap = new SparseBooleanArray();
    // Не рекомендуется: private HashMap<Integer, Boolean> _checkedMap = new HashMap<>();

    DirAdapter(Activity context, int resource,
               int textViewResourceId, List<DirInfo> objects) {
        super(context, resource, textViewResourceId, objects);
        _context = context;
        _objects = objects;

        for (int i = 0; i < objects.size(); i++) {
            _checkedMap.put(i, _objects.get(i).Checked);
        }
    }

    void toggleChecked(int position) {
        if (_checkedMap.get(position)) {
            _checkedMap.put(position, false);
        } else {
            _checkedMap.put(position, true);
        }

        notifyDataSetChanged();
    }

    public List<Integer> getCheckedItemPositions() {
        List<Integer> checkedItemPositions = new ArrayList<>();

        for (int i = 0; i < _checkedMap.size(); i++) {
            if (_checkedMap.get(i)) {
                (checkedItemPositions).add(i);
            }
        }
        return checkedItemPositions;
    }

    List<DirInfo> getCheckedItems() {
        List<DirInfo> checkedItems = new ArrayList<>();

        for (int i = 0; i < _checkedMap.size(); i++) {
            if (_checkedMap.get(i)) {
                (checkedItems).add(_objects.get(i));
            }
        }
        return checkedItems;
    }

    public void Delete(int position) {
        _checkedMap.delete(position);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        public android.widget.CheckedTextView CheckedTextView;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = _context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.dir_item, parent, false);
            holder.CheckedTextView = (CheckedTextView)convertView.findViewById(R.id.checkedTextView);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.CheckedTextView.setText(_objects.get(position).Path);

        Boolean checked = _checkedMap.get(position);
        if (checked != null) {
            holder.CheckedTextView.setChecked(checked);
        }

        return convertView;
    }
}
