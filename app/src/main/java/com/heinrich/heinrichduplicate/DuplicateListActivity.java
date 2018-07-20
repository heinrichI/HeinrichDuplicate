package com.heinrich.heinrichduplicate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.heinrich.heinrichduplicate.config.API;
import com.heinrich.heinrichduplicate.util.FileUtil;
import com.heinrich.heinrichduplicate.util.MD5;
import com.heinrich.heinrichduplicate.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
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

        ListView lvDuplicateFiles = (ListView) findViewById(R.id.duplicate_files);
//        _adapter = new ArrayAdapter<String>(_act,
//                android.R.layout.simple_list_item_1,
//                android.R.id.text1,
//                _duplicateFiles);
        lvDuplicateFiles.setOnItemClickListener(myOnItemClickListener);

        _adapter = new DuplicateAdapter(this);
        lvDuplicateFiles.setAdapter(_adapter);

        _paths = getIntent().getStringArrayExtra(API.DIR_NAME);
        //setTitle(mPath);
        Analize();
    }

    AdapterView.OnItemClickListener myOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            _adapter.toggleChecked(position);
        }
    };

//    @Override
//    public void onItemCheckedStateChanged(android.view.ActionMode mode,
//                                          int position, long id, boolean checked)
//    {
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dupl_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.setBytext:
                SetByText();
                return true;
            case R.id.delete:
                DeleteFiles();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void DeleteFiles() {
        if (_groups.isEmpty()) {
            ToastUtil.showShortToast(_act, "Please Exe Analize Task before..");
            return;
        }
        new DeleteFileTask().execute();
    }

    private void SetByText()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Set by text");
        alert.setMessage("Enter text");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                _adapter.SetByText(value);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    private void Analize() {
        //_allFiles.clear();
        _adapter.Clear();
        //_duplicateFiles.clear();
        _groups.clear();
        _adapter.notifyDataSetChanged();

        new AnalizeTask().execute(_paths);
    }


    private class AnalizeTask extends AsyncTask<String, String, Void> {
        ProgressDialog _progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _progressDialog = ProgressDialog.show(_act, null, "Analizinnng....", true, false);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                publishProgress("Read files");
                List<File> files = FileUtil.GetFiles(params);

                publishProgress(String.format("Sort by file size %1$s files", files.size()));
                files = FileUtil.GetFilesWithSameSize(files);

                int current = 0;
                int total = files.size();

                Map<String, ArrayList<String>> allFiles = new HashMap<String, ArrayList<String>>();
                for (File f : files) {
                    Log.d("Calculate md5 for ",  f.getPath());
                    current++;
                    String progressMessage = String.format("%1$s of %2$s (%3$s)", current, total, f.getPath());
                    publishProgress(progressMessage);
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
                //Log.d("doInBackground", "mAllFiles", allFiles.toString());
                }//end for

                for (String k : allFiles.keySet()) {
                    ArrayList<String> fs = allFiles.get(k);
                    if (fs.size() > 1) {
                        DuplGroup group = new DuplGroup(k);
                        List<FileInfo> files2 = new ArrayList<>(fs.size());
                        for (String p : fs) {
                            files2.add(new FileInfo(p, false, group));
                        }
                        group.Files = files2;
                        _groups.add(group);
                    }
                }
            } catch (Exception e) {
                Log.e("doInBackground", String.valueOf(e));
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Log.d("onProgressUpdate ",  values[0]);
            super.onProgressUpdate(values);
            //_progressDialog.setMessage(String.format("Calculate %1$s", values[0]));
            _progressDialog.setMessage( values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            _progressDialog.dismiss();
            _adapter.addGroups(_groups);
            if (_groups.isEmpty()) {
                ToastUtil.showShortToast(_act, "No Duplicate Files.");
            }
        }
    }

    private class DeleteFileTask extends AsyncTask<Void, String, Void> {
        ProgressDialog _progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _progressDialog = ProgressDialog.show(_act, null, "Deleting....", true, false);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            //super.onProgressUpdate(values);
            //_adapter.notifyDataSetChanged();
            _progressDialog.setMessage(String.format("Delete %1$s", values[0]));
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
//                Iterator<String> iterator = _groups.iterator();
//                while (iterator.hasNext()) {
//                    String f = iterator.next();
//                    new File(f).delete();
//                    iterator.remove();
//                    publishProgress(0);
//                    //TimeUnit.SECONDS.sleep(3);
//                }
                List<FileInfo> forDelete = _adapter.GetChecked();
                for (FileInfo file : forDelete)
                {
                    new File(file.Path).delete();
                    //iterator.remove();
                    publishProgress(file.Path);
                }
            } catch (Exception ex) {
                Log.e("doInBackground", String.valueOf(ex));
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            _progressDialog.dismiss();
            _groups.clear();
            //_adapter.addGroups(_groups);
            _adapter.notifyDataSetChanged();
            //if (mDuplicateFiles.isEmpty()) {
            //    ToastUtil.showShortToast(mAct, "Success.");
            //}
        }
    }

}
