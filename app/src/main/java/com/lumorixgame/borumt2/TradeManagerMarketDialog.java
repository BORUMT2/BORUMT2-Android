package com.lumorixgame.borumt2;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lumorixgame.borumt2.item.Item;
import com.lumorixgame.borumt2.item.TradeManagerMarket;

public class TradeManagerMarketDialog {

    public static void show(Context context, Player player) {
        Item[] materials = TradeManagerMarket.getUpgradeMaterials();

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 24, 32, 16);

        TextView title = new TextView(context);
        title.setText("Ticaret Yöneticisi");
        title.setTextSize(20);
        title.setTextColor(Color.rgb(90, 55, 25));
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.addView(title);

        TextView yang = new TextView(context);
        yang.setText("Yang: " + player.getYang());
        yang.setTextSize(16);
        layout.addView(yang);

        LinearLayout materialList = new LinearLayout(context);
        materialList.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < materials.length; i++) {
            final int index = i;
            Item item = materials[i];

            Button button = new Button(context);
            button.setText(
                    item.name + "\n" +
                    String.format("%,d Yang", item.yangValue)
            );

            button.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Satın Al")
                        .setMessage(
                                item.name + "\n\n" +
                                "Fiyat: " +
                                String.format("%,d Yang", item.yangValue) +
                                "\n\nSatın almak istiyor musun?"
                        )
                        .setNegativeButton("Vazgeç", null)
                        .setPositiveButton("Satın Al", (dialog, which) -> {
                            boolean success =
                                    com.lumorixgame.borumt2.item.TradeManagerShop
                                            .buyUpgradeMaterial(player, index);

                            if (success) {
                                yang.setText("Yang: " + player.getYang());
                            } else {
                                new AlertDialog.Builder(context)
                                        .setTitle("Satın Alınamadı")
                                        .setMessage(
                                                "Yeterli Yang veya envanter alanı yok."
                                        )
                                        .setPositiveButton("Tamam", null)
                                        .show();
                            }
                        })
                        .show();
            });

            materialList.addView(button);
        }

        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(materialList);
        layout.addView(
                scrollView,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1f
                )
        );

        Button close = new Button(context);
        close.setText("Kapat");
        layout.addView(close);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(layout)
                .create();

        close.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
