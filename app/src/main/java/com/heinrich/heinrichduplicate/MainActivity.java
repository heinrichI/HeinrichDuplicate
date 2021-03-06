package com.heinrich.heinrichduplicate;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heinrich.heinrichduplicate.config.API;
import com.heinrich.heinrichduplicate.util.ObjectSerializer;
import com.heinrich.heinrichduplicate.util.PermissionsHelper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


import lib.folderpicker.FolderPicker;

public class MainActivity extends Activity {

    private static final int REQUEST_DIRECTORY = 2;
    private static final int FOLDERPICKER_CODE = 3;

    private List<DirInfo> _folders;
    private DirAdapter _dirAdapter;

    private Activity _act = this;

    private SharedPreferences _prefs;
    static final String FOLDERS = "FOLDERS";
    private static final String PREFS_TAG = "SharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionsHelper.getReadStoragePermissions(_act);

/*      _adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                _foldersPath
        );
        ListView lvFolders = (ListView) findViewById(R.id.folders);
        lvFolders.setAdapter(_adapter);

        lvFolders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = _foldersPath.get(position);
                Intent intent = new Intent(_act, DuplicateListActivity.class);
                intent.putExtra(API.DIR_NAME, path);
                startActivity(intent);
            }
        });*/

        SharedPreferences _prefs =  getSharedPreferences(PREFS_TAG, MODE_PRIVATE);
        String jsonPreferences = _prefs.getString(FOLDERS, "");

        //https://stackoverflow.com/questions/28107647/how-to-save-listobject-to-sharedpreferences
        Type type = new TypeToken<List<DirInfo>>() {}.getType();
        Gson gson = new Gson();
        _folders = gson.fromJson(jsonPreferences, type);
        if (_folders == null)
            _folders = new ArrayList<DirInfo>();

        _dirAdapter = new DirAdapter(this, R.layout.dir_item,
                android.R.id.text1, _folders);

        ListView lvFolders = (ListView) findViewById(R.id.folders);
        lvFolders.setAdapter(_dirAdapter);
        lvFolders.setOnItemClickListener(myOnItemClickListener);
        lvFolders.setOnItemLongClickListener(myOnItemLongClickListener);
    }

    AdapterView.OnItemClickListener myOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            _dirAdapter.toggleChecked(position);
        }
    };

    AdapterView.OnItemLongClickListener myOnItemLongClickListener= new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
            Log.v("long clicked","pos: " + position);
            _dirAdapter.Delete(position);
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
            case R.id.add_folder:
                SelectFolder();
                return true;
            case R.id.analize:
                Analize();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void Analize() {
        List<String> directories = new ArrayList<String>();
        for (DirInfo dirInfo : _dirAdapter.getCheckedItems()) {
            directories.add(dirInfo.Path);
        }
        Intent intent = new Intent(_act, DuplicateListActivity.class);
        intent.putExtra(API.DIR_NAME, directories.toArray(new String[directories.size()]));
        //intent.putStringArrayListExtra(API.DIR_NAME, (ArrayList<String>) directories);
        startActivity(intent);
    }

    private void SelectFolder() {
        /*final Intent chooserIntent = new Intent(
                this,
                DirectoryChooserActivity.class);

        final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .newDirectoryName("DirChooserSample")
                .allowReadOnlyDirectory(false)
                .allowNewDirectoryNameModification(true)
                .initialDirectory(android.os.Environment.getExternalStorageDirectory().getPath())
                .build();

        chooserIntent.putExtra(
                DirectoryChooserActivity.EXTRA_CONFIG,
                config);

        startActivityForResult(chooserIntent, REQUEST_DIRECTORY);*/

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            i.addCategory(Intent.CATEGORY_BROWSABLE);
            startActivityForResult(Intent.createChooser(i, "Choose directory"), 9999);
        }*/

        Intent intent = new Intent(this, FolderPicker.class);
        startActivityForResult(intent, FOLDERPICKER_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult:", "onActivityResult");
        switch(requestCode) {
//            case REQUEST_DIRECTORY:
//                if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
//                    String dir = data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR);
//                    Log.d("onActivityResult", dir);
//                    _foldersPath.add(dir);
//                    _adapter.notifyDataSetChanged();
//                }
//                break;
//            case 9999:
//                Log.i("Test", "Result URI " + data.getData());
//                break;
            case FOLDERPICKER_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    String folderLocation = data.getExtras().getString("data");
                    Log.i("folderLocation", folderLocation);
                    _folders.add(new DirInfo(folderLocation, true));
                    _dirAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    /*protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences mPrefs = getSharedPreferences();
        mCurViewMode = mPrefs.getInt("view_mode", DAY_VIEW_MODE);
    }*/

    protected void onPause() {
        super.onPause();

//        SharedPreferences.Editor ed = _prefs.edit();
//        ed.("view_mode", mCurViewMode);
//        ed.commit();

        // save the task list to preference
        /*SharedPreferences.Editor editor = _prefs.edit();
        try {
            editor.putString(FOLDERS, ObjectSerializer.serialize(_folders));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit()*/

        if (_prefs == null)
            _prefs =  getSharedPreferences(PREFS_TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = _prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(_folders);
        editor.putString(FOLDERS, json);
        editor.commit();
    }


}
