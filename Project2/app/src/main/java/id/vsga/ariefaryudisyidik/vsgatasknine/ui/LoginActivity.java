package id.vsga.ariefaryudisyidik.vsgatasknine.ui;

import static id.vsga.ariefaryudisyidik.vsgatasknine.utils.Constant.FILENAME;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import id.vsga.ariefaryudisyidik.vsgatasknine.R;
import id.vsga.ariefaryudisyidik.vsgatasknine.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();
        login();
        register();
    }


    private void login() {
        binding.btnLogin.setOnClickListener(view -> {
            username = binding.edtUsername.getText().toString().trim();
            password = binding.edtPassword.getText().toString().trim();

            if (username.isEmpty()) {
                binding.edtUsername.setError(getString(R.string.empty));
            }
            if (password.isEmpty()) {
                binding.edtPassword.setError(getString(R.string.empty));
            }
            if (!username.isEmpty() && !password.isEmpty()) {
                File sdcard = getFilesDir();
                File file = new File(sdcard, username);

                if (file.exists()) {
                    StringBuilder text = new StringBuilder();
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String line = br.readLine();
                        while (line != null) {
                            text.append(line);
                            line = br.readLine();
                            br.close();
                        }
                    } catch (IOException e) {
                        System.out.println("Error " + e.getMessage());
                    }
                    String data = text.toString();
                    String[] userData = data.split(";");

                    if (userData[1].equals(password)) {
                        saveLoginFile();
                        navigateToHome();
                    } else {
                        Toast.makeText(this, getString(R.string.password_invalid), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.user_not_found), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveLoginFile() {
        String fileContents = username + ";" + password;
        File file = new File(getFilesDir(), FILENAME);
        FileOutputStream outputStream;

        try {
            file.createNewFile();
            outputStream = new FileOutputStream(file, false);
            outputStream.write(fileContents.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    private void register() {
        binding.btnRegister.setOnClickListener(view -> navigateToRegister());
    }

    private void navigateToHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void navigateToRegister() {
        startActivity(new Intent(this, RegisterActivity.class));
    }
}