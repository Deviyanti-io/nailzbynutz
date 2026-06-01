package com.example.nailzbynutz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomNailColorActivity extends AppCompatActivity {

    private String selectedShape, selectedLength;
    private String selectedColorHex = null;
    private String selectedColorName = null;
    private String selectedColorType = "Solid";
    private String selectedFinish = "Glossy";
    private Uri uploadedImageUri = null;

    private HashMap<String, Integer> addonCounts = new HashMap<>();

    private GridLayout colorGrid;
    private Button btnTypeSolid, btnTypeOmbre, btnTypeFrench, btnTypeCatEye;
    private LinearLayout btnFinishGlossy, btnFinishMatte, btnFinishChrome, btnFinishGlitter;
    private LinearLayout btnUploadPhoto;
    private TextView tvUploadStatus, tvSelectedColorInfo;
    private boolean fromGelPolish;

    private ActivityResultLauncher<String> imagePickerLauncher;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_nail_color);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Menyiapkan desain...");
        progressDialog.setCancelable(false);

        selectedShape = getIntent().getStringExtra("SHAPE_DATA");
        selectedLength = getIntent().getStringExtra("LENGTH_DATA");
        if (selectedShape == null) selectedShape = "Almond";
        if (selectedLength == null) selectedLength = "Medium";

        fromGelPolish = getIntent().getBooleanExtra("FROM_GEL_POLISH", false);
        if (fromGelPolish) {
            String polishType = getIntent().getStringExtra("COLOR_TYPE_DATA");
            String topCoat = getIntent().getStringExtra("FINISH_DATA");
            if (polishType != null) selectedColorType = polishType;
            if (topCoat != null) selectedFinish = topCoat;
        }

        findViewById(R.id.btn_back_color).setOnClickListener(v -> finish());

        tvSelectedColorInfo = findViewById(R.id.tv_selected_color_info);
        colorGrid = findViewById(R.id.color_grid);
        colorGrid.setColumnCount(7);

        btnTypeSolid = findViewById(R.id.btn_type_solid);
        btnTypeOmbre = findViewById(R.id.btn_type_gradient);
        btnTypeFrench = findViewById(R.id.btn_type_french);
        btnTypeCatEye = findViewById(R.id.btn_type_cateye);

        btnFinishGlossy = findViewById(R.id.btn_finish_glossy);
        btnFinishMatte = findViewById(R.id.btn_finish_matte);
        btnFinishChrome = findViewById(R.id.btn_finish_chrome);
        btnFinishGlitter = findViewById(R.id.btn_finish_glitter);

        btnUploadPhoto = findViewById(R.id.btn_upload_photo_container);
        tvUploadStatus = findViewById(R.id.tv_upload_status);
        Button btnNextColor = findViewById(R.id.btn_next_color);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        uploadedImageUri = uri;
                        tvUploadStatus.setText("Foto referensi berhasil dipilih ✅");
                        tvUploadStatus.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(this, "Batal memilih foto", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        String[][] colorData = {
                {"#FFFFFF", "Pure White"}, {"#000000", "Classic Black"}, {"#FF4500", "Orange Red"}, {"#D80000", "Ruby Red"},
                {"#C80020", "Crimson"}, {"#8B0020", "Dark Burgundy"}, {"#5A0015", "Deep Maroon"}, {"#1A000A", "Midnight Red"},
                {"#FF5E97", "Bright Pink"}, {"#E463B1", "Bubblegum"}, {"#FF3399", "Hot Pink"}, {"#D81B60", "Fuchsia"},
                {"#FF0055", "Neon Rose"}, {"#C2185B", "Deep Pink"}, {"#20B2AA", "Light Sea Green"}, {"#00A896", "Teal"},
                {"#3A7D8C", "Muted Cyan"}, {"#0288D1", "Ocean Blue"}, {"#004D40", "Dark Pine"}, {"#0A1931", "Navy Blue"},
                {"#5F9EA0", "Cadet Blue"}, {"#4682B4", "Steel Blue"}, {"#006666", "Deep Teal"}, {"#69829C", "Slate Blue"},
                {"#3F51B5", "Indigo"}, {"#1C2833", "Dark Slate"}, {"#C77398", "Dusty Rose"}, {"#A34875", "Mauve"},
                {"#B3549C", "Orchid"}, {"#882D61", "Plum"}, {"#5C0632", "Dark Violet"}, {"#3D001D", "Deep Purple"},
                {"#FFD1BA", "Peach"}, {"#E8D7E7", "Lavender"}, {"#F3B0B3", "Pastel Pink"}, {"#D98880", "Rose Gold"},
                {"#E05345", "Coral"}, {"#A95050", "Terracotta"}, {"#FFD700", "Gold"}, {"#F4A460", "Sandy Brown"},
                {"#D2691E", "Chocolate"}, {"#8B4513", "Saddle Brown"}, {"#A0522D", "Sienna"}, {"#CD853F", "Peru"},
                {"#DEB887", "Burlywood"}, {"#D3D3D3", "Light Grey"}, {"#A9A9A9", "Dark Grey"}, {"#808080", "Grey"}
        };

        for (String[] data : colorData) {
            String hex = data[0];
            String name = data[1];

            androidx.cardview.widget.CardView colorCard = new androidx.cardview.widget.CardView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = (int) (42 * getResources().getDisplayMetrics().density);
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            int margin = (int) (4 * getResources().getDisplayMetrics().density);
            params.setMargins(margin, margin, margin, margin);
            colorCard.setLayoutParams(params);
            colorCard.setRadius((int) (21 * getResources().getDisplayMetrics().density));
            colorCard.setCardBackgroundColor(Color.parseColor(hex));
            colorCard.setClickable(true);

            colorCard.setOnClickListener(v -> {
                selectedColorHex = hex;
                selectedColorName = name;
                tvSelectedColorInfo.setText(name + " (" + hex + ")");

                for (int i = 0; i < colorGrid.getChildCount(); i++) {
                    ((androidx.cardview.widget.CardView) colorGrid.getChildAt(i)).setCardElevation(2);
                    ((androidx.cardview.widget.CardView) colorGrid.getChildAt(i)).setAlpha(0.6f);
                }
                colorCard.setCardElevation(12);
                colorCard.setAlpha(1.0f);
            });
            colorGrid.addView(colorCard);
        }

        setColorTypeListener(btnTypeSolid, "Solid");
        setColorTypeListener(btnTypeOmbre, "Ombre");
        setColorTypeListener(btnTypeFrench, "French Tip");
        setColorTypeListener(btnTypeCatEye, "Cat Eye");
        updateColorTypeUI(selectedColorType);

        setFinishListener(btnFinishGlossy, "Glossy");
        setFinishListener(btnFinishMatte, "Matte");
        setFinishListener(btnFinishChrome, "Chrome");
        setFinishListener(btnFinishGlitter, "Glitter");
        updateFinishUI(selectedFinish);

        setupAddonItem("Charms", R.id.tv_qty_charms, R.id.btn_min_charms, R.id.btn_add_charms);
        setupAddonItem("Pearls", R.id.tv_qty_pearls, R.id.btn_min_pearls, R.id.btn_add_pearls);
        setupAddonItem("Rhinestone", R.id.tv_qty_rhinestone, R.id.btn_min_rhinestone, R.id.btn_add_rhinestone);
        setupAddonItem("3D Flower", R.id.tv_qty_flower, R.id.btn_min_flower, R.id.btn_add_flower);
        setupAddonItem("Stickers", R.id.tv_qty_stickers, R.id.btn_min_stickers, R.id.btn_add_stickers);
        setupAddonItem("Bow", R.id.tv_qty_bow, R.id.btn_min_bow, R.id.btn_add_bow);

        btnUploadPhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        btnNextColor.setOnClickListener(v -> {
            if (selectedColorHex == null) {
                Toast.makeText(this, "Silakan pilih warna kuku terlebih dahulu!", Toast.LENGTH_SHORT).show();
                return;
            }

            progressDialog.show();
            if (uploadedImageUri != null) {
                String base64Image = encodeImageToBase64(uploadedImageUri);
                proceedToNextActivity(base64Image);
            } else {
                proceedToNextActivity(null);
            }
        });
    }

    // FUNGSI BARU: Mengubah Gambar ke Base64 (Teks)
    private String encodeImageToBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // Kompres gambar 30% agar database tidak penuh
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void proceedToNextActivity(String base64Image) {
        progressDialog.dismiss();
        Intent intent;
        if (fromGelPolish) {
            intent = new Intent(CustomNailColorActivity.this, CustomNailReviewActivity.class);
            intent.putExtra("SERVICE_TYPE", "Gel Nails");
            intent.putExtra("SIZE_DATA", "Standar (Gel Polish)");
            intent.putExtra("NOTES_DATA", "Tidak ada catatan khusus");
        } else {
            intent = new Intent(CustomNailColorActivity.this, CustomNailDetailsActivity.class);
            intent.putExtra("SERVICE_TYPE", "Custom Nails");
        }

        intent.putExtra("SHAPE_DATA", selectedShape);
        intent.putExtra("LENGTH_DATA", selectedLength);
        intent.putExtra("COLOR_TYPE_DATA", selectedColorType);
        intent.putExtra("COLOR_HEX_DATA", selectedColorHex);
        intent.putExtra("COLOR_NAME_DATA", selectedColorName);
        intent.putExtra("FINISH_DATA", selectedFinish);

        if (base64Image != null) {
            intent.putExtra("UPLOADED_IMAGE_DATA", base64Image);
        }

        ArrayList<String> activeAddons = new ArrayList<>();
        for (String key : addonCounts.keySet()) {
            if (addonCounts.get(key) != null && addonCounts.get(key) > 0) activeAddons.add(key);
        }
        intent.putStringArrayListExtra("ADDONS_DATA", activeAddons);
        intent.putExtra("ADDON_COUNTS_DATA", addonCounts);

        startActivity(intent);
    }

    private void setupAddonItem(String name, int tvQtyId, int btnMinId, int btnAddId) {
        TextView tvQty = findViewById(tvQtyId);
        TextView btnMin = findViewById(btnMinId);
        TextView btnAdd = findViewById(btnAddId);
        addonCounts.put(name, 0);

        btnAdd.setOnClickListener(v -> {
            int count = addonCounts.get(name) + 1;
            addonCounts.put(name, count);
            tvQty.setText(String.valueOf(count));
        });

        btnMin.setOnClickListener(v -> {
            int count = addonCounts.get(name);
            if(count > 0) {
                count--;
                addonCounts.put(name, count);
                tvQty.setText(String.valueOf(count));
            }
        });
    }

    private void setColorTypeListener(Button btn, String type) {
        btn.setOnClickListener(v -> {
            selectedColorType = type;
            updateColorTypeUI(type);
        });
    }

    private void updateColorTypeUI(String activeType) {
        resetButtonStyle(btnTypeSolid);
        resetButtonStyle(btnTypeOmbre);
        resetButtonStyle(btnTypeFrench);
        resetButtonStyle(btnTypeCatEye);
        if (activeType.equals("Solid")) setActiveButtonStyle(btnTypeSolid);
        else if (activeType.equals("Ombre")) setActiveButtonStyle(btnTypeOmbre);
        else if (activeType.equals("French Tip")) setActiveButtonStyle(btnTypeFrench);
        else if (activeType.equals("Cat Eye")) setActiveButtonStyle(btnTypeCatEye);
    }

    private void resetButtonStyle(Button btn) {
        btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.background_card)));
        btn.setTextColor(getColor(R.color.text_primary));
        btn.setWidth(1);
        btn.setTextColor(getColor(R.color.divider));
    }

    private void setActiveButtonStyle(Button btn) {
        btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.lavender_dark)));
        btn.setTextColor(getColor(R.color.white));
        btn.setWidth(0);
    }

    private void setFinishListener(LinearLayout layout, String finish) {
        layout.setOnClickListener(v -> {
            selectedFinish = finish;
            updateFinishUI(finish);
        });
    }

    private void updateFinishUI(String activeFinish) {
        resetFinishStyle(btnFinishGlossy);
        resetFinishStyle(btnFinishMatte);
        resetFinishStyle(btnFinishChrome);
        resetFinishStyle(btnFinishGlitter);
        if (activeFinish.equals("Glossy")) setActiveFinishStyle(btnFinishGlossy);
        else if (activeFinish.equals("Matte")) setActiveFinishStyle(btnFinishMatte);
        else if (activeFinish.equals("Chrome")) setActiveFinishStyle(btnFinishChrome);
        else if (activeFinish.equals("Glitter")) setActiveFinishStyle(btnFinishGlitter);
    }

    private void resetFinishStyle(LinearLayout layout) {
        layout.setBackgroundResource(R.drawable.bg_card_rounded);
    }

    private void setActiveFinishStyle(LinearLayout layout) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setCornerRadius(20f);
        gd.setColor(getColor(R.color.lavender_dark));
        layout.setBackground(gd);
    }
}