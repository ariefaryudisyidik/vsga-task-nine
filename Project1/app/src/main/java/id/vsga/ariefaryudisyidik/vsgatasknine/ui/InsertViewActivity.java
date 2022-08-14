package id.vsga.ariefaryudisyidik.vsgatasknine.ui;


import static id.vsga.ariefaryudisyidik.vsgatasknine.utils.Constant.EXTRA_FILENAME;
import static id.vsga.ariefaryudisyidik.vsgatasknine.utils.Constant.REQUEST_CODE_STORAGE;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Objects;

import id.vsga.ariefaryudisyidik.vsgatasknine.R;
import id.vsga.ariefaryudisyidik.vsgatasknine.databinding.ActivityInsertViewBinding;

public class InsertViewActivity extends AppCompatActivity {

    private ActivityInsertViewBinding binding;
    private int eventID = 0;
    String fileName = "";
    String noteTemp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInsertViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            fileName = extras.getString(EXTRA_FILENAME);
            binding.edtFileName.setText(fileName);
            Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.edit_note));
        } else {
            Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.add_note));
        }
        eventID = 1;
        if (Build.VERSION.SDK_INT >= 23) {
            readFile();
        }

        binding.btnSave.setOnClickListener(view -> {
            String note = binding.edtNote.getText().toString();
            eventID = 2;
            if (!noteTemp.equals(note)) {
                if (Build.VERSION.SDK_INT >= 23) {
                    showDialogStorage();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (eventID == 1) {
                    readFile();
                } else {
                    showDialogStorage();
                }
            }
        }
    }

    private void addEdit() {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return;
        }
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File parent = new File(String.valueOf(path));
        if (parent.exists()) {
            File file = new File(path, binding.edtFileName.getText().toString());
            FileOutputStream outputStream;
            try {
                file.createNewFile();
                outputStream = new FileOutputStream(file);
                OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
                streamWriter.append(binding.edtNote.getText());
                streamWriter.flush();
                streamWriter.close();
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            parent.mkdir();
            File file = new File(path, binding.edtFileName.getText().toString());
            FileOutputStream outputStream;
            try {
                file.createNewFile();
                outputStream = new FileOutputStream(file, false);
                outputStream.write(binding.edtNote.getText().toString().getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        onBackPressed();
    }

    private void readFile() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, binding.edtFileName.getText().toString());
        if (file.exists()) {
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = br.readLine();
                while (line != null) {
                    text.append(line);
                    line = br.readLine();
                }
                br.close();
            } catch (IOException e) {
                System.out.println("Error " + e.getMessage());
            }
            noteTemp = text.toString();
            binding.edtNote.setText(text.toString());
        }
    }

    private void showDialogStorage() {
        new AlertDialog.Builder(this)
                .setTitle("Simpan Catatan")
                .setMessage("Apakah Anda yakin ingin menyimpan Catatan ini?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> addEdit()).setNegativeButton(android.R.string.no, null).show();
    }
}