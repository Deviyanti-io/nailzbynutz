package com.example.nailzbynutz;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import java.util.ArrayList;

public class CustomNailColorActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 100;
    private static final int PERMISSION_REQUEST = 101;

    private String selectedShape, selectedLength;
    private String selectedColorHex = null;
    private String selectedColorType = "Solid";
    private String selectedFinish = "Glossy";
    private ArrayList<String> selectedAddons = new ArrayList<>();
    private String uploadedImagePath = null;

    private GridLayout colorGrid;
    private Button btnTypeSolid, btnTypeOmbre, btnTypeFrench, btnTypeCatEye;
    private LinearLayout btnFinishGlossy, btnFinishMatte, btnFinishChrome, btnFinishGlitter;
    private LinearLayout addonCharms, addonPearls, addonRhinestone, addonFlower, addonStickers, addonBow;
    private LinearLayout btnUploadPhoto;
    private TextView tvUploadStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_nail_color);

        // Ambil data dari intent (bisa dari CustomNailShapeActivity atau GelPolishActivity)
        selectedShape = getIntent().getStringExtra("SHAPE_DATA");
        selectedLength = getIntent().getStringExtra("LENGTH_DATA");
        if (selectedShape == null) selectedShape = "Almond";
        if (selectedLength == null) selectedLength = "Medium";

        // Jika datang dari GelPolishActivity, ambil color type dan finish
        boolean fromGelPolish = getIntent().getBooleanExtra("FROM_GEL_POLISH", false);
        if (fromGelPolish) {
            String polishType = getIntent().getStringExtra("COLOR_TYPE_DATA");
            String topCoat = getIntent().getStringExtra("FINISH_DATA");
            if (polishType != null) selectedColorType = polishType;
            if (topCoat != null) selectedFinish = topCoat;
        }

        // Inisialisasi view
        ImageView btnBackColor = findViewById(R.id.btn_back_color);
        btnBackColor.setOnClickListener(v -> finish());

        colorGrid = findViewById(R.id.color_grid);
        colorGrid.setColumnCount(7); // 7 kolom agar rapi

        btnTypeSolid = findViewById(R.id.btn_type_solid);
        btnTypeOmbre = findViewById(R.id.btn_type_gradient);
        btnTypeFrench = findViewById(R.id.btn_type_french);
        btnTypeCatEye = findViewById(R.id.btn_type_cateye);

        btnFinishGlossy = findViewById(R.id.btn_finish_glossy);
        btnFinishMatte = findViewById(R.id.btn_finish_matte);
        btnFinishChrome = findViewById(R.id.btn_finish_chrome);
        btnFinishGlitter = findViewById(R.id.btn_finish_glitter);

        addonCharms = findViewById(R.id.addon_charms);
        addonPearls = findViewById(R.id.addon_pearls);
        addonRhinestone = findViewById(R.id.addon_rhinestone);
        addonFlower = findViewById(R.id.addon_flower);
        addonStickers = findViewById(R.id.addon_stickers);
        addonBow = findViewById(R.id.addon_bow);

        btnUploadPhoto = findViewById(R.id.btn_upload_photo_container);
        tvUploadStatus = findViewById(R.id.tv_upload_status);
        Button btnNextColor = findViewById(R.id.btn_next_color);

        // ================= 1. DAFTAR WARNA LENGKAP (108 warna) =================
        String[] colorHexes = {
                // Baris 1 (001 - 012)
                "#FF4500", "#D80000", "#C80020", "#8B0020", "#5A0015", "#1A000A", "#FF5E97", "#E463B1", "#FF3399", "#D81B60", "#FF0055", "#C2185B",
                // Baris 2 (013 - 024)
                "#20B2AA", "#00A896", "#3A7D8C", "#0288D1", "#004D40", "#0A1931", "#5F9EA0", "#4682B4", "#006666", "#69829C", "#3F51B5", "#1C2833",
                // Baris 3 (025 - 036)
                "#C77398", "#A34875", "#B3549C", "#882D61", "#5C0632", "#3D001D", "#FFD1BA", "#E8D7E7", "#F3B0B3", "#D98880", "#E05345", "#A95050",
                // Baris 4 (037 - 048)
                "#F2EBE1", "#C5B4AC", "#9CA3A3", "#ABBAD1", "#B19CD9", "#5E4B5B", "#A3E4D7", "#85C1E9", "#5DADE2", "#1F1248", "#152B75", "#09091A",
                // Baris 5 (049 - 060)
                "#9EA685", "#A39752", "#6E7F47", "#4A5D23", "#274E37", "#0B291B", "#E6C587", "#7CB342", "#388E3C", "#1B5E20", "#4D6A66", "#14211D",
                // Baris 6 (061 - 072)
                "#FFD700", "#E69A28", "#EBB382", "#E59866", "#D4AC0D", "#E05C36", "#F58231", "#E7736F", "#E04836", "#900C3F", "#581845", "#2C0811",
                // Baris 7 (073 - 084)
                "#FFC0CB", "#FF8DA1", "#E1BEE7", "#CE93D8", "#BA68C8", "#8E44AD", "#875A4B", "#BA2F00", "#6E1A00", "#3E1919", "#58000C", "#2D0A11",
                // Baris 8 (085 - 096)
                "#E02401", "#B30006", "#A30000", "#3F000C", "#800000", "#690005", "#A97F79", "#E52B12", "#3D0C24", "#4A0011", "#7D7474", "#342C3A",
                // Baris 9 (097 - 108)
                "#EAECEE", "#D5C4B1", "#A699A6", "#A06A42", "#A03104", "#4E5124", "#050B14", "#F7F9FA", "#4F8684", "#DC5815", "#C00000", "#4A1505"
        };

        // Isi Grid dengan CardView warna
        for (String hex : colorHexes) {
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
                Toast.makeText(this, "Warna dipilih", Toast.LENGTH_SHORT).show();
                // Reset elevasi semua card
                for (int i = 0; i < colorGrid.getChildCount(); i++) {
                    ((androidx.cardview.widget.CardView) colorGrid.getChildAt(i)).setCardElevation(2);
                }
                colorCard.setCardElevation(12);
            });
            colorGrid.addView(colorCard);
        }

        // ================= 2. COLOR TYPE =================
        setColorTypeListener(btnTypeSolid, "Solid");
        setColorTypeListener(btnTypeOmbre, "Ombre");
        setColorTypeListener(btnTypeFrench, "French Tip");
        setColorTypeListener(btnTypeCatEye, "Cat Eye");
        updateColorTypeUI(selectedColorType); // update dari data intent jika ada

        // ================= 3. FINISH =================
        setFinishListener(btnFinishGlossy, "Glossy");
        setFinishListener(btnFinishMatte, "Matte");
        setFinishListener(btnFinishChrome, "Chrome");
        setFinishListener(btnFinishGlitter, "Glitter");
        updateFinishUI(selectedFinish);

        // ================= 4. ADD-ONS =================
        setAddonListener(addonCharms, "Charms");
        setAddonListener(addonPearls, "Pearls");
        setAddonListener(addonRhinestone, "Rhinestone");
        setAddonListener(addonFlower, "3D Flower");
        setAddonListener(addonStickers, "Stickers");
        setAddonListener(addonBow, "Bow");

        // ================= 5. UPLOAD FOTO =================
        btnUploadPhoto.setOnClickListener(v -> checkAndOpenGallery());

        // ================= 6. NEXT BUTTON =================
        btnNextColor.setOnClickListener(v -> {
            if (selectedColorHex == null) {
                Toast.makeText(this, "Silakan pilih warna kuku terlebih dahulu!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(CustomNailColorActivity.this, CustomNailDetailsActivity.class);
            intent.putExtra("SHAPE_DATA", selectedShape);
            intent.putExtra("LENGTH_DATA", selectedLength);
            intent.putExtra("COLOR_TYPE_DATA", selectedColorType);
            intent.putExtra("COLOR_HEX_DATA", selectedColorHex);
            intent.putExtra("FINISH_DATA", selectedFinish);
            intent.putExtra("UPLOADED_IMAGE_DATA", uploadedImagePath);
            intent.putStringArrayListExtra("ADDONS_DATA", selectedAddons);
            startActivity(intent);
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

    private void setAddonListener(LinearLayout layout, String addonName) {
        layout.setOnClickListener(v -> {
            if (selectedAddons.contains(addonName)) {
                selectedAddons.remove(addonName);
                layout.setBackgroundResource(R.drawable.bg_card_rounded);
            } else {
                selectedAddons.add(addonName);
                GradientDrawable gd = new GradientDrawable();
                gd.setShape(GradientDrawable.RECTANGLE);
                gd.setCornerRadius(20f);
                gd.setColor(getColor(R.color.lavender_light));
                gd.setStroke(2, getColor(R.color.lavender_dark));
                layout.setBackground(gd);
            }
        });
    }

    private void checkAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST);
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            uploadedImagePath = imageUri.toString();
            tvUploadStatus.setText("Foto terpilih ✅");
            tvUploadStatus.setVisibility(android.view.View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            Toast.makeText(this, "Izin penyimpanan diperlukan untuk upload foto", Toast.LENGTH_SHORT).show();
        }
    }
}