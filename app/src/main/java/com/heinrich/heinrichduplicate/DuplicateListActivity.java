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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DuplicateListActivity extends Activity {

    private Activity _act = this;

    //private ListView mLvDuplicateFiles;
    //private ArrayAdapter<String> _adapter;
    private DuplicateAdapter _adapter;
    //private List<String> _duplicateFiles = new ArrayList<String>();
    // LinkedList add method gives O(1) performance while ArrayList gives O(n) in the worst case. LinkedList is faster. It will just reference the nodes so the first one disappears
    LinkedList<DuplGroup> _groups = new LinkedList<DuplGroup>();
    //private Map<String, ArrayList<String>> _allFiles = new HashMap<String, ArrayList<String>>();

    String[] _paths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duplicate_list);

        ListView mLvDuplicateFiles = (ListView) findViewById(R.id.duplicate_files);
//        _adapter = new ArrayAdapter<String>(_act,
//                android.R.layout.simple_list_item_1,
//                android.R.id.text1,
//                _duplicateFiles);
        _adapter = new DuplicateAdapter(this);
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
        //_allFiles.clear();
        _adapter.Clear();
        //_duplicateFiles.clear();
        _groups.clear();
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
                Map<String, ArrayList<String>> allFiles = new HashMap<String, ArrayList<String>>();
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
                            if (!allFiles.containsKey(md5)) {
                                fs = new ArrayList<String>();
                                allFiles.put(md5, fs);
                            }
                            fs = allFiles.get(md5);
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

                    for (String k : allFiles.keySet()) {
                        ArrayList<String> fs = allFiles.get(k);
                        if (fs.size() > 1) {
                            DuplGroup group = new DuplGroup(k);
                            List<FileInfo> files2 = new ArrayList<>(fs.size());
                            for (String p : fs.subList(1, fs.size())) {
                                files2.add(new FileInfo(p, false, group));
                            }
                            group.Files = files2;
                            _groups.add(group);
                        }
                    }

                    Log.d("doInBackground", "mAllFiles", (Throwable) allFiles);
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
            _adapter.addGroups(_groups);
            if (_groups.isEmpty()) {
                ToastUtil.showShortToast(_act, "No Duplicate Files.");
            }
        }
    }

}
