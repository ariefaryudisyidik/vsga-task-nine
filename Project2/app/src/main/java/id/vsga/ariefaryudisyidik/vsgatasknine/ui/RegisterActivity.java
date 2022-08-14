package id.vsga.ariefaryudisyidik.vsgatasknine.ui;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;

import id.vsga.ariefaryudisyidik.vsgatasknine.R;
import id.vsga.ariefaryudisyidik.vsgatasknine.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        register();
    }

    private void register() {
        binding.btnSave.setOnClickListener(view -> {
            String username = binding.edtUsername.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();
            String email = binding.edtEmail.getText().toString().trim();
            String fullName = binding.edtFullName.getText().toString().trim();
            String schoolOrigin = binding.edtSchoolOrigin.getText().toString().trim();
            String residence = binding.edtResidence.getText().toString().trim();

            if (username.isEmpty()) {
                binding.edtUsername.setError(getString(R.string.empty));
            }
            if (password.isEmpty()) {
                binding.edtPassword.setError(getString(R.string.empty));
            }
            if (email.isEmpty()) {
                binding.edtEmail.setError(getString(R.string.empty));
            }
            if (fullName.isEmpty()) {
                binding.edtFullName.setError(getString(R.string.empty));
            }
            if (schoolOrigin.isEmpty()) {
                binding.edtSchoolOrigin.setError(getString(R.string.empty));
            }
            if (residence.isEmpty()) {
                binding.edtResidence.setError(getString(R.string.empty));
            }
            if (!username.isEmpty() && !password.isEmpty() && !email.isEmpty() &&
                    !fullName.isEmpty() && !schoolOrigin.isEmpty() && !residence.isEmpty()) {
                String fileContents = username + ";" +
                        password + ";" +
                        email + ";" +
                        fullName + ";" +
                        schoolOrigin + ";" +
                        residence;
                File file = new File(getFilesDir(), username);
                FileOutputStream outputStream;
                try {
                    file.createNewFile();
                    outputStream = new FileOutputStream(file, false);
                    outputStream.write(fileContents.getBytes());
                    outputStream.flush();
                    outputStream.close();
                    Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(this::navigateToLogin, 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void navigateToLogin() {
        onBackPressed();
    }
}