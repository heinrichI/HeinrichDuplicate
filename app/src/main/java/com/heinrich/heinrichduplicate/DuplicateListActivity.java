package com.heinrich.heinrichduplicate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.heinrich.heinrichduplicate.config.API;
import com.heinrich.heinrichduplicate.util.MD5;
import com.heinrich.heinrichduplicate.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DuplicateListActivity extends Activity {

    private Activity _act = this;

    //private ListView mLvDuplicateFiles;
    private ArrayAdapter<String> _adapter;
    private List<String> _duplicateFiles = new ArrayList<String>();
    private Map<String, ArrayList<String>> _allFiles = new HashMap<String, ArrayList<String>>();

    String[] _paths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duplicate_list);

        ListView mLvDuplicateFiles = (ListView) findViewById(R.id.duplicate_files);
        _adapter = new ArrayAdapter<String>(_act,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                _duplicateFiles);
        mLvDuplicateFiles.setAdapter(_adapter);

        _paths = getIntent().getStringArrayExtra(API.DIR_NAME);
        //setTitle(mPath);
        Analize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dupl_list, menu);
        return true;
    }

    private void Analize() {
        _allFiles.clear();
        _duplicateFiles.clear();
        _adapter.notifyDataSetChanged();

        new AnalizeTask().execute();
    }


    private class AnalizeTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(_act, null, "Analizinnng....", true, false);

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                for (String path: _paths) {

                    File file = new File(path);
                    File[] files = file.listFiles();
                    if (files != null) {
                        for (File f : files) {
                            if (f.isDirectory()) {
                                continue;
                            }
                            String md5 = MD5.calculateMD5(f);
                            ArrayList<String> fs;
                            if (!_allFiles.containsKey(md5)) {
                                fs = new ArrayList<String>();
                                _allFiles.put(md5, fs);
                            }
                            fs = _allFiles.get(md5);
                            fs.add(f.getPath());
                            Collections.sort(fs, new Comparator<String>() {
                                @Override
                                public int compare(String lhs, String rhs) {
                                    return lhs.length() - rhs.length();
                                }
                            });
                        }
                    } else {
                        //ToastUtil.showShortToast(mAct, "Direction is Empty");
                        return null;
                    }


                    for (String k : _allFiles.keySet()) {
                        ArrayList<String> fs = _allFiles.get(k);
                        if (fs.size() > 1) {
                            _duplicateFiles.addAll(fs.subList(1, fs.size()));
                        }
                    }

                    Log.d("doInBackground", "mAllFiles", (Throwable) _allFiles);
                }
            } catch (Exception e) {
                Log.e("doInBackground", String.valueOf(e));
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressDialog.dismiss();
            _adapter.notifyDataSetChanged();
            if (_duplicateFiles.isEmpty()) {
                ToastUtil.showShortToast(_act, "No Duplicate Files.");
            }
        }
    }

}
