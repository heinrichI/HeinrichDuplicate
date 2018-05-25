package com.heinrich.heinrichduplicate;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.heinrich.heinrichduplicate.config.API;

import java.util.ArrayList;
import java.util.List;


import lib.folderpicker.FolderPicker;

public class MainActivity extends Activity {

    private static final int REQUEST_DIRECTORY = 2;
    private static final int FOLDERPICKER_CODE = 3;

    private List<String> _foldersPath = new ArrayList<String>();
    ArrayAdapter<String> _adapter;

    private Activity _act = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionsHelper.getReadStoragePermissions(_act);

        ListView lvFolders = (ListView) findViewById(R.id.folders);
        _adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                _foldersPath
        );
        lvFolders.setAdapter(_adapter);

        lvFolders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = _foldersPath.get(position);
                Intent intent = new Intent(_act, DuplicateListActivity.class);
                intent.putExtra(API.DIR_NAME, path);
                startActivity(intent);
            }
        });
    }

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_folder) {
            selectFolder();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void selectFolder() {
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
            case 9999:
                Log.i("Test", "Result URI " + data.getData());
                break;
            case FOLDERPICKER_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    String folderLocation = data.getExtras().getString("data");
                    Log.i("folderLocation", folderLocation);
                }

                break;
        }
    }
}
