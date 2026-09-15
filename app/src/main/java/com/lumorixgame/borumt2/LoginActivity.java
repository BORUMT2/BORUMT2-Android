package com.lumorixgame.borumt2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import org.json.JSONObject;

public class LoginActivity extends Activity {
    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);

        username = new EditText(this);
        username.setHint("Kullanıcı adı");
        layout.addView(username);

        password = new EditText(this);
        password.setHint("Şifre");
        password.setInputType(129);
        layout.addView(password);

        Button login = new Button(this);
        login.setText("Giriş Yap");
        layout.addView(login);

        login.setOnClickListener(v -> login());
        setContentView(layout);
    }

    private void login() {
        new Thread(() -> {
            ServerClient client = new ServerClient();
            boolean connected = client.connect("10.0.2.2", 5000);
            if (!connected) {
                runOnUiThread(() -> Toast.makeText(this, "Sunucuya bağlanılamadı", Toast.LENGTH_LONG).show());
                return;
            }

            try {
                JSONObject request = new JSONObject();
                request.put("command", "LOGIN");
                request.put("username", username.getText().toString().trim());
                request.put("password", password.getText().toString());

                JSONObject response = client.request(request);
                client.close();

                if (response != null && response.optBoolean("ok")) {
                    String sessionToken = response.optString("session_token", "");
                    Intent intent = new Intent(this, CharacterSelectActivity.class);
                    intent.putExtra("session_token", sessionToken);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Giriş başarılı", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Kullanıcı adı veya şifre hatalı", Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Giriş hatası", Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}
