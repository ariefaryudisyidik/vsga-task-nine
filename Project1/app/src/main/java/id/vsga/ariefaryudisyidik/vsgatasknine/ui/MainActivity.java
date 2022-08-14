package id.vsga.ariefaryudisyidik.vsgatasknine.ui;

import static id.vsga.ariefaryudisyidik.vsgatasknine.utils.Constant.EXTRA_FILENAME;
import static id.vsga.ariefaryudisyidik.vsgatasknine.utils.Constant.REQUEST_CODE_STORAGE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.splashscreen.SplashScreen;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import id.vsga.ariefaryudisyidik.vsgatasknine.R;
import id.vsga.ariefaryudisyidik.vsgatasknine.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        showNoteList();
    }

    private void showNoteList() {
        binding.lvNote.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent toInsertView = new Intent(this, InsertViewActivity.class);
            //noinspection unchecked
            Map<String, Object> data = (Map<String, Object>) adapterView.getAdapter().getItem(i);
            item = Objects.requireNonNull(data.get("name")).toString();
            toInsertView.putExtra(EXTRA_FILENAME, item);
            Toast.makeText(this, "You clicked " + item, Toast.LENGTH_SHORT).show();
            startActivity(toInsertView);
        });

        binding.lvNote.setOnItemLongClickListener((adapterView, view, i, l) -> {
            //noinspection unchecked
            Map<String, Object> data = (Map<String, Object>) adapterView.getAdapter().getItem(i);
            item = Objects.requireNonNull(data.get("name")).toString();
            showDeleteDialog(item);
            return true;
        });
    }

    private void showDeleteDialog(String filename) {
        new AlertDialog.Builder(this).setTitle("Hapus catatan ini?")
                .setMessage("Apakah Anda yakin ingin menghapus Catatan " + filename + "?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> fileDelete(filename))
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void fileDelete(String filename) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, filename);
        if (file.exists()) {
            file.delete();
        }
        fetchFileList();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_menu) {
            startActivity(new Intent(this, InsertViewActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkStoragePermission()) {
                fetchFileList();
            }
        } else {
            fetchFileList();
        }
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_CODE_STORAGE);
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchFileList();
            }
        }
    }

    private void fetchFileList() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File directory = new File(String.valueOf(path));

        if (directory.exists()) {
            File[] files = directory.listFiles();
            assert files != null;
            String[] filenames = new String[files.length];
            String[] dateCreated = new String[files.length];

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
            ArrayList<Map<String, Object>> itemDataList = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                filenames[i] = files[i].getName();
                Date lastModDate = new Date(files[i].lastModified());
                dateCreated[i] = sdf.format(lastModDate);
                Map<String, Object> listItemMap = new HashMap<>();
                listItemMap.put("name", filenames[i]);
                listItemMap.put("date", dateCreated[i]);
                itemDataList.add(listItemMap);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(this, itemDataList, android.R.layout.simple_list_item_2,
                    new String[]{"name", "date"},
                    new int[]{android.R.id.text1, android.R.id.text2});
            binding.lvNote.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
        }
    }
}