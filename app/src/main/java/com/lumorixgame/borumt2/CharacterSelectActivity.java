package com.lumorixgame.borumt2;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;

public class CharacterSelectActivity extends Activity {
    private String sessionToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionToken = getIntent().getStringExtra("session_token");
        loadCharacters();
    }

    private void loadCharacters() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);
        TextView title = new TextView(this);
        title.setText("BÖRÜMT2 - Karakter Seçimi");
        title.setTextSize(28);
        layout.addView(title);
        setContentView(layout);

        new Thread(() -> {
            ServerClient client = new ServerClient();
            if (!client.connect("10.0.2.2", 5000)) {
                runOnUiThread(() -> Toast.makeText(this, "Sunucuya bağlanılamadı", Toast.LENGTH_LONG).show());
                return;
            }
            try {
                JSONObject request = new JSONObject();
                request.put("command", "LIST_CHARACTERS");
                request.put("session_token", sessionToken);
                JSONObject response = client.request(request);
                client.close();
                JSONArray characters = response == null ? null : response.optJSONArray("characters");
                runOnUiThread(() -> {
                    if (characters == null || characters.length() == 0) {
                        TextView empty = new TextView(this);
                        empty.setText("Henüz karakter yok.");
                        empty.setTextSize(20);
                        layout.addView(empty);
                        return;
                    }
                    for (int i = 0; i < characters.length(); i++) {
                        JSONObject c = characters.optJSONObject(i);
                        if (c == null) continue;
                        TextView item = new TextView(this);
                        item.setText(c.optString("name") + "  |  " + c.optString("class") + "  |  Lv." + c.optInt("level") + "  |  " + c.optLong("yang") + " Yang");
                        item.setTextSize(20);
                        item.setPadding(10, 20, 10, 20);
                        final String characterId = c.optString("id"); item.setOnClickListener(v -> selectCharacter(characterId));
                        layout.addView(item);
                    }
                });
            } catch (Exception e) {
                client.close();
                runOnUiThread(() -> Toast.makeText(this, "Karakter listesi alınamadı", Toast.LENGTH_LONG).show());
            }
          }).start();
    }
    private void selectCharacter(String characterId) { new Thread(() -> { ServerClient client = new ServerClient(); if (!client.connect("10.0.2.2", 5000)) { runOnUiThread(() -> Toast.makeText(this, "Sunucuya bağlanılamadı", Toast.LENGTH_LONG).show()); return; } try { JSONObject request = new JSONObject(); request.put("command", "SELECT_CHARACTER"); request.put("session_token", sessionToken); request.put("character_id", characterId); JSONObject response = client.request(request); client.close(); if (response != null && response.optBoolean("ok")) { runOnUiThread(() -> { Toast.makeText(this, "Karakter seçildi", Toast.LENGTH_SHORT).show(); startActivity(new android.content.Intent(this, MainActivity.class)); finish(); }); } else { runOnUiThread(() -> Toast.makeText(this, "Karakter seçilemedi", Toast.LENGTH_LONG).show()); } } catch (Exception e) { client.close(); runOnUiThread(() -> Toast.makeText(this, "Karakter seçim hatası", Toast.LENGTH_LONG).show()); } }).start(); }
    }
