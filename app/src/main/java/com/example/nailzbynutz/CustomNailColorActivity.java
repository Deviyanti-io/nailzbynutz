package com.example.nailzbynutz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class CustomNailColorActivity extends AppCompatActivity {

    // View components
    private ImageView btnBack;
    private Button btnNext;
    private Button btnTypeSolid, btnTypeGradient, btnTypeFrench, btnTypeCatEye;
    private LinearLayout btnGlossy, btnMatte, btnChrome, btnGlitter;
    private LinearLayout btnUploadPhoto;
    private GridLayout colorGrid;

    // Data selections
    private String selectedColorType = "Solid";
    private String selectedColorHex = "#D6001C";
    private String selectedFinish = "Glossy";
    private ArrayList<String> selectedAddons = new ArrayList<>();
    private String dataShape = "Almond", dataLength = "Medium";
    private String uploadedImageUri = "";

    private ActivityResultLauncher<Intent> galleryLauncher;

    // Color lists
    private List<ColorItem> solidColors = new ArrayList<>();
    private List<ColorItem> ombreColors = new ArrayList<>();
    private List<ColorItem> frenchColors = new ArrayList<>();
    private List<ColorItem> catEyeColors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_nail_color);

        if (getIntent().hasExtra("SHAPE_DATA")) dataShape = getIntent().getStringExtra("SHAPE_DATA");
        if (getIntent().hasExtra("LENGTH_DATA")) dataLength = getIntent().getStringExtra("LENGTH_DATA");

        // Initialize views
        btnBack = findViewById(R.id.btn_back_color);
        btnNext = findViewById(R.id.btn_next_color);
        btnTypeSolid = findViewById(R.id.btn_type_solid);
        btnTypeGradient = findViewById(R.id.btn_type_gradient);
        btnTypeFrench = findViewById(R.id.btn_type_french);
        btnTypeCatEye = findViewById(R.id.btn_type_cateye);
        btnGlossy = findViewById(R.id.btn_finish_glossy);
        btnMatte = findViewById(R.id.btn_finish_matte);
        btnChrome = findViewById(R.id.btn_finish_chrome);
        btnGlitter = findViewById(R.id.btn_finish_glitter);
        btnUploadPhoto = findViewById(R.id.btn_upload_photo_container);
        colorGrid = findViewById(R.id.color_grid);

        // Gallery launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            uploadedImageUri = uri.toString();
                            Toast.makeText(this, "Foto referensi berhasil dimuat!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Populate color data (full)
        populateColors();

        // Set default states
        updateColorTypeState("Solid");
        updateFinishState("Glossy");

        // Tab listeners
        btnTypeSolid.setOnClickListener(v -> updateColorTypeState("Solid"));
        btnTypeGradient.setOnClickListener(v -> updateColorTypeState("Ombre"));
        btnTypeFrench.setOnClickListener(v -> updateColorTypeState("French Tip"));
        btnTypeCatEye.setOnClickListener(v -> updateColorTypeState("Cat Eye"));

        // Finish listeners (without messing with image color filter)
        btnGlossy.setOnClickListener(v -> updateFinishState("Glossy"));
        btnMatte.setOnClickListener(v -> updateFinishState("Matte"));
        btnChrome.setOnClickListener(v -> updateFinishState("Chrome"));
        btnGlitter.setOnClickListener(v -> updateFinishState("Glitter"));

        // Add-ons
        setupAddonToggle(R.id.addon_charms, "Charms");
        setupAddonToggle(R.id.addon_pearls, "Pearls");
        setupAddonToggle(R.id.addon_rhinestone, "Rhinestone");
        setupAddonToggle(R.id.addon_flower, "3D Flower");
        setupAddonToggle(R.id.addon_stickers, "Stickers");
        setupAddonToggle(R.id.addon_bow, "Bow");

        btnUploadPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        btnBack.setOnClickListener(v -> finish());

        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(CustomNailColorActivity.this, CustomNailDetailsActivity.class);
            intent.putExtra("SHAPE_DATA", dataShape);
            intent.putExtra("LENGTH_DATA", dataLength);
            intent.putExtra("COLOR_TYPE_DATA", selectedColorType);
            intent.putExtra("COLOR_HEX_DATA", selectedColorHex);
            intent.putExtra("FINISH_DATA", selectedFinish);
            intent.putStringArrayListExtra("ADDONS_DATA", selectedAddons);
            intent.putExtra("UPLOADED_IMAGE_DATA", uploadedImageUri);
            startActivity(intent);
        });
    }

    private void populateColors() {
        // Solid colors (29 colors, full)
        solidColors.add(new ColorItem("Black", "#000000"));
        solidColors.add(new ColorItem("Milk White", "#F2F0EB"));
        solidColors.add(new ColorItem("Cherry Red", "#D6001C"));
        solidColors.add(new ColorItem("Silver", "#C4C2C0"));
        solidColors.add(new ColorItem("Caramel", "#D9A05B"));
        solidColors.add(new ColorItem("Peach Nude", "#F5D6C4"));
        solidColors.add(new ColorItem("Soft Peach", "#F5D1BC"));
        solidColors.add(new ColorItem("Rose Pink", "#E8A7B5"));
        solidColors.add(new ColorItem("Coral", "#F7A399"));
        solidColors.add(new ColorItem("Dusty Pink", "#E394A4"));
        solidColors.add(new ColorItem("Mauve", "#BA7EA9"));
        solidColors.add(new ColorItem("Dark Purple", "#4E2B6B"));
        solidColors.add(new ColorItem("Wine Red", "#731822"));
        solidColors.add(new ColorItem("Hot Pink", "#E04D79"));
        solidColors.add(new ColorItem("Lavender", "#8F63A8"));
        solidColors.add(new ColorItem("Royal Purple", "#78388C"));
        solidColors.add(new ColorItem("Pastel Lavender", "#DED2F9"));
        solidColors.add(new ColorItem("Soft Lilac", "#E8C5E5"));
        solidColors.add(new ColorItem("Taupe", "#8F8073"));
        solidColors.add(new ColorItem("Nude Beige", "#D1BCB2"));
        solidColors.add(new ColorItem("Ocean Blue", "#008EA6"));
        solidColors.add(new ColorItem("Navy Blue", "#0B4273"));
        solidColors.add(new ColorItem("Lime Green", "#9ED463"));
        solidColors.add(new ColorItem("Brown", "#8B5A2B"));
        solidColors.add(new ColorItem("Teal", "#118199"));
        solidColors.add(new ColorItem("Deep Teal", "#0F6E85"));
        solidColors.add(new ColorItem("Dark Cyan", "#08576B"));
        solidColors.add(new ColorItem("Denim Blue", "#5785A6"));
        solidColors.add(new ColorItem("Midnight Blue", "#1C364A"));

        // Ombre colors (10 colors)
        ombreColors.add(new ColorItem("Blush Pink", "#FF9A9E"));
        ombreColors.add(new ColorItem("Orchid", "#FECFEF"));
        ombreColors.add(new ColorItem("Sky Blue", "#A1C4FD"));
        ombreColors.add(new ColorItem("Ice Gradient", "#C2E9FB"));
        ombreColors.add(new ColorItem("Sunset Gel", "#F6D365"));
        ombreColors.add(new ColorItem("Melon Candy", "#FDA085"));
        ombreColors.add(new ColorItem("Lilac Dream", "#E0C3FC"));
        ombreColors.add(new ColorItem("Soft Ocean", "#8EC5FC"));
        ombreColors.add(new ColorItem("Mint Fresh", "#84FAB0"));
        ombreColors.add(new ColorItem("Aqua Bloom", "#8FD3F4"));

        // French Tip colors (8 colors)
        frenchColors.add(new ColorItem("Classic White", "#FFFFFF"));
        frenchColors.add(new ColorItem("Rose Petal", "#FFE4E1"));
        frenchColors.add(new ColorItem("Beige Silk", "#F5F5DC"));
        frenchColors.add(new ColorItem("Gold Line", "#FFD700"));
        frenchColors.add(new ColorItem("Deep Onyx", "#000000"));
        frenchColors.add(new ColorItem("Soft Violet", "#E6E6FA"));
        frenchColors.add(new ColorItem("Powder Blue", "#B0E0E6"));
        frenchColors.add(new ColorItem("Blossom", "#FFB6C1"));

        // Cat Eye colors (6 colors)
        catEyeColors.add(new ColorItem("Galaxy Velvet", "#2E1A47"));
        catEyeColors.add(new ColorItem("Deep Cosmic", "#1A2E40"));
        catEyeColors.add(new ColorItem("Jade Magnetic", "#1A402E"));
        catEyeColors.add(new ColorItem("Amber Flare", "#403A1A"));
        catEyeColors.add(new ColorItem("Ruby Laser", "#401A1A"));
        catEyeColors.add(new ColorItem("Nebula Purple", "#331A47"));
    }

    private void updateColorTypeState(String type) {
        selectedColorType = type;
        resetTabButton(btnTypeSolid);
        resetTabButton(btnTypeGradient);
        resetTabButton(btnTypeFrench);
        resetTabButton(btnTypeCatEye);

        Button activeTab = btnTypeSolid;
        if (type.equals("Ombre")) activeTab = btnTypeGradient;
        else if (type.equals("French Tip")) activeTab = btnTypeFrench;
        else if (type.equals("Cat Eye")) activeTab = btnTypeCatEye;
        setActiveTabButton(activeTab);

        List<ColorItem> targetList = solidColors;
        if (type.equals("Ombre")) targetList = ombreColors;
        else if (type.equals("French Tip")) targetList = frenchColors;
        else if (type.equals("Cat Eye")) targetList = catEyeColors;
        generateColorGrid(targetList);
    }

    private void resetTabButton(Button btn) {
        if (btn == null) return;
        btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.background_card)));
        btn.setTextColor(getColor(R.color.text_primary));
    }

    private void setActiveTabButton(Button btn) {
        if (btn == null) return;
        btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.lavender_dark)));
        btn.setTextColor(getColor(R.color.white));
    }

    private void generateColorGrid(List<ColorItem> colors) {
        colorGrid.removeAllViews();
        for (ColorItem color : colors) {
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(Gravity.CENTER);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setMargins(12, 12, 12, 12);
            item.setLayoutParams(params);

            View circle = new View(this);
            LinearLayout.LayoutParams circleParams = new LinearLayout.LayoutParams(80, 80);
            circle.setLayoutParams(circleParams);
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.OVAL);
            shape.setColor(Color.parseColor(color.hex));
            if (color.hex.equalsIgnoreCase("#FFFFFF") || color.hex.equalsIgnoreCase("#F2F0EB")) {
                shape.setStroke(2, Color.parseColor("#DDDDDD"));
            }
            circle.setBackground(shape);

            TextView name = new TextView(this);
            name.setText(color.name);
            name.setTextSize(10);
            name.setTextColor(getColor(R.color.text_primary));
            name.setGravity(Gravity.CENTER);
            name.setPadding(0, 8, 0, 0);
            name.setTypeface(getResources().getFont(R.font.poppins_medium));

            item.addView(circle);
            item.addView(name);
            item.setOnClickListener(v -> {
                selectedColorHex = color.hex;
                Toast.makeText(this, "Selected: " + color.name, Toast.LENGTH_SHORT).show();
            });
            colorGrid.addView(item);
        }
    }

    private void updateFinishState(String type) {
        selectedFinish = type;
        resetFinishCard(btnGlossy);
        resetFinishCard(btnMatte);
        resetFinishCard(btnChrome);
        resetFinishCard(btnGlitter);

        LinearLayout active = btnGlossy;
        if (type.equals("Matte")) active = btnMatte;
        else if (type.equals("Chrome")) active = btnChrome;
        else if (type.equals("Glitter")) active = btnGlitter;
        setActiveFinishCard(active);
    }

    private void resetFinishCard(LinearLayout layout) {
        if (layout == null) return;
        layout.setBackgroundResource(R.drawable.bg_card_rounded);
        TextView text = (TextView) layout.getChildAt(1);
        if (text != null) text.setTextColor(getColor(R.color.text_primary));
        // DO NOT change image color filter – keep original icon color
    }

    private void setActiveFinishCard(LinearLayout layout) {
        if (layout == null) return;
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setCornerRadius(32f);
        gd.setColor(getColor(R.color.lavender_dark));
        layout.setBackground(gd);
        TextView text = (TextView) layout.getChildAt(1);
        if (text != null) text.setTextColor(getColor(R.color.white));
        // DO NOT change image color filter – keep original icon color
    }

    private void setupAddonToggle(int layoutId, String name) {
        LinearLayout layout = findViewById(layoutId);
        if (layout == null) return;
        layout.setOnClickListener(v -> {
            if (selectedAddons.contains(name)) {
                selectedAddons.remove(name);
                resetAddonCard(layout);
            } else {
                selectedAddons.add(name);
                setActiveAddonCard(layout);
            }
        });
    }

    private void resetAddonCard(LinearLayout layout) {
        layout.setBackgroundResource(R.drawable.bg_card_rounded);
        TextView text = (TextView) layout.getChildAt(1);
        if (text != null) text.setTextColor(getColor(R.color.text_primary));
        // DO NOT change image color filter
    }

    private void setActiveAddonCard(LinearLayout layout) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setCornerRadius(32f);
        gd.setColor(getColor(R.color.lavender_dark));
        layout.setBackground(gd);
        TextView text = (TextView) layout.getChildAt(1);
        if (text != null) text.setTextColor(getColor(R.color.white));
        // DO NOT change image color filter
    }

    private static class ColorItem {
        String name;
        String hex;
        ColorItem(String name, String hex) {
            this.name = name;
            this.hex = hex;
        }
    }
}