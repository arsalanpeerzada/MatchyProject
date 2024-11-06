package com.teniqs.matchymatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.teniqs.matchymatch.Utils.AlarmSoundService;
import com.teniqs.matchymatch.Utils.Common;
import com.teniqs.matchymatch.Utils.Constants;
import com.teniqs.matchymatch.Utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.teniqs.matchymatch.Utils.Utils.getUriToResource;

public class CustomMainPuzzleActivity extends AppCompatActivity {

    private static final Integer GREY = Color.parseColor("#D2D2D2");

    ImageView DRAGOne, DRAGtwo;
//    ImageView DRAGthree;
    TextToSpeech tts;
    int puzzle = 0;
    int changer = 0;
    MediaPlayer winlevelsound;
    int selectedPhoto;
    String firstTextVoice, secondTextVoice, thirdTextVoice, fourtTextVoice, fiveTextVoice, sixTextVoice, sevenTextVoice, eightTextVoice, nineTextVoice, tenTextVoice;

    //home button
    ImageView homebtn;

    //DB work
    private static final int SELECT_PHOTO = 7777;
    private static final int CAM_REQUEST = 1313;
    ImageView invisible_imageView;

    //activity images work
    ImageView image_one, image_two, image_three, image_four, image_five, image_six, image_seven, image_eight, image_nine, image_ten, bgimage;

    // warning photo popup
    ImageView circle_cancel_warning_popup;
    Dialog warningDialog;

    // voiceover record popup
    ImageView circle_voice_record_popup;
    EditText voiceEDT;
    Button voicebtn;
    Dialog voiceDialog;

    // select photo popup
    ImageView circle_cancel_select_popup;
    Button gallery_btn, camera_btn;
    Dialog photoDialog;

    //edit,delete, and upload popup
    ImageView circle_cancel_ten_popup;
    Button upload_image_btn, voiceover_btn, image_delete_btn;
    Dialog tenDialog;

    // save photo popup
    ImageView circle_cancel_save_popup;
    Button cancel_btn, save_image_btn;
    Dialog saveDialog;
    String puzzleName = "";
    ArrayList<CustomMainPuzzle> customMainPuzzles;
    private static final Type TYPE = new TypeToken<ArrayList<CustomMainPuzzle>>() {
    }.getType();
    SharedPreferences myPuzzle;
    ConstraintLayout container;
    SimpleDraweeView ll;
    Uri imageUri;
    double IMAGE_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        setContentView(R.layout.activity_green);
        Fresco.initialize(this);
        setContentView(R.layout.custom_puzzle);

        container = findViewById(R.id.parent);
//        bgimage = findViewById(R.id.bgimage);
//        bgimage.setImageResource(getIntent().getIntExtra("bgColor",0));
        Drawable background = getResources().getDrawable(getIntent().getExtras().getInt("bgColor"));
        container.setBackground(background);
        puzzleName = getIntent().getStringExtra("puzzleName");

        SharedPreferences myMusic = this.getSharedPreferences("MyMusic", Context.MODE_PRIVATE);

        ll = findViewById(R.id.ll);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
//                .setUri("android.resource://com.matchymatchproject.mirassociationdanny.matchymatch/drawable/puzzle_end_gif.gif")
                .setUri(getUriToResource(CustomMainPuzzleActivity.this, R.drawable.puzzle_end_gif))
//                .setUri("https://media4.giphy.com/avatars/100soft/WahNEDdlGjRZ.gif")
                .setAutoPlayAnimations(true)
                .build();
        ll.setController(controller);

        myPuzzle = this.getSharedPreferences(puzzleName + "_MyAwesomePuzzle", Context.MODE_PRIVATE);
        puzzle = myPuzzle.getInt("puzzle", 0);
        //puzzle  = getIntent().getExtras().getInt("puzzle");

        homebtn = findViewById(R.id.homebtn);
        homebtn.setOnClickListener(v -> startActivity(new Intent(CustomMainPuzzleActivity.this, CustomMainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

        winlevelsound = MediaPlayer.create(CustomMainPuzzleActivity.this, R.raw.kidscheering);

        if (puzzle == 0) {

            puzzle += 1;

            myPuzzle = getSharedPreferences(puzzleName + "_MyAwesomePuzzle", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = myPuzzle.edit();
            editor.putInt("puzzle", puzzle);
            editor.apply();

        }



        tts = new TextToSpeech(getApplicationContext(), status -> {
            if (status != TextToSpeech.ERROR) {

                tts.setLanguage(Constants.speechLocale);
                tts.setPitch((float) 1.1);
                tts.setSpeechRate((float) 0.6);
            }
        });

        warningDialog = new Dialog(this);
        voiceDialog = new Dialog(this);
        photoDialog = new Dialog(this);
        tenDialog = new Dialog(this);
        saveDialog = new Dialog(this);
//        invisible_imageView  = (ImageView) findViewById(R.id.save_green_image_uri);
        invisible_imageView = findViewById(R.id.save_image_uri);
        image_one = findViewById(R.id.image_one);
        image_two = findViewById(R.id.image_two);
        image_three = findViewById(R.id.image_three);
        image_four = findViewById(R.id.image_four);
        image_five = findViewById(R.id.image_five);
        image_six = findViewById(R.id.image_six);
        image_seven = findViewById(R.id.image_seven);
        image_eight = findViewById(R.id.image_eight);
        image_nine = findViewById(R.id.image_nine);
        image_ten = findViewById(R.id.image_ten);
        new LoadDataAsyncTask().execute();

       //  target image linear layout
//        image_one  = (ConstraintLayout) findViewById(R.id.image_one);
//        image_two  = (ConstraintLayout) findViewById(R.id.image_two);
//        image_three  = (ConstraintLayout) findViewById(R.id.image_three);
//        image_four  = (ConstraintLayout) findViewById(R.id.image_four);
//        image_five  = (ConstraintLayout) findViewById(R.id.image_five);
//        image_six  = (ConstraintLayout) findViewById(R.id.image_six);
//        image_seven  = (ConstraintLayout) findViewById(R.id.image_seven);
//        image_eight  = (ConstraintLayout) findViewById(R.id.image_eight);
//        image_nine  = (ConstraintLayout) findViewById(R.id.image_nine);
//        image_ten = (ConstraintLayout) findViewById(R.id.image_ten);

        // drag image boxes
        DRAGOne = findViewById(R.id.image_puzzle_one);
        DRAGtwo = findViewById(R.id.image_puzzle_two);
       // DRAGthree = findViewById(R.id.image_puzzle_three);

        // declear images


        if(getIntent().getBooleanExtra("isCreating", false)) {

//            image_one.setOnDragListener(dragListener);
//            image_two.setOnDragListener(dragListener);
//            image_three.setOnDragListener(dragListener);
//            image_four.setOnDragListener(dragListener);
//            image_five.setOnDragListener(dragListener);
//            image_six.setOnDragListener(dragListener);
//            image_seven.setOnDragListener(dragListener);
//            image_eight.setOnDragListener(dragListener);
//            image_nine.setOnDragListener(dragListener);
//            image_ten.setOnDragListener(dragListener);

            DRAGOne.setOnTouchListener(Common.touchListener);
            DRAGtwo.setOnTouchListener(Common.touchListener);
           // DRAGthree.setOnTouchListener(Common.touchListener);

        }
        //DRAGthree.setOnLongClickListener(longClickListener);


        //-------------------------------------this code for record voice name start here....................

        //change first Voice



        //----------------------------------------end for fetching------------------------------------------


        //Drag and Drop work in OnCreate Method ................End Here........................

        //on Click get ImageView Id.................................

//        if(getIntent().getBooleanExtra("isCreating", false)) {

        image_one.setOnClickListener(v -> {

            Constants.selectedPhoto = v.getId();
            tenPopup(0, Constants.selectedPhoto);
        });

        image_two.setOnClickListener(v -> {

            Constants.selectedPhoto = v.getId();
            tenPopup(1, Constants.selectedPhoto);
        });

        image_three.setOnClickListener(v -> {

            Constants.selectedPhoto = v.getId();
            tenPopup(2, Constants.selectedPhoto);
        });

        image_four.setOnClickListener(v -> {

            Constants.selectedPhoto = v.getId();
            tenPopup(3, Constants.selectedPhoto);
        });

        image_five.setOnClickListener(v -> {

            Constants.selectedPhoto = v.getId();
            tenPopup(4, Constants.selectedPhoto);
        });

        image_six.setOnClickListener(v -> {

            Constants.selectedPhoto = v.getId();
            tenPopup(5, Constants.selectedPhoto);
        });

        image_seven.setOnClickListener(v -> {

            Constants.selectedPhoto = v.getId();
            tenPopup(6, Constants.selectedPhoto);
        });

        image_eight.setOnClickListener(v -> {

            Constants.selectedPhoto = v.getId();
            tenPopup(7, Constants.selectedPhoto);
        });

        image_nine.setOnClickListener(v -> {

            Constants.selectedPhoto = v.getId();
            tenPopup(8, Constants.selectedPhoto);
        });

        image_ten.setOnClickListener(v -> {

            Constants.selectedPhoto = v.getId();
            tenPopup(9, Constants.selectedPhoto);
        });
    }
    //------------------------------start here warningPopup-------------------------------
    public void warningPopup() {

        warningDialog.setContentView(R.layout.image_warning);
        circle_cancel_warning_popup = warningDialog.findViewById(R.id.circle_cancel_warning_popup);
        circle_cancel_warning_popup.setOnClickListener(v -> warningDialog.dismiss());


        warningDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        warningDialog.show();

    }


    //---------------------------warning popup end here....................................

    //------------------------------start here voice Popup-------------------------------
    public void voicePopup(CustomMainPuzzle customMainPuzzle) {

        voiceDialog.setContentView(R.layout.voiceover_popup);
        circle_voice_record_popup = voiceDialog.findViewById(R.id.circle_voice_cancelbtn);
        circle_voice_record_popup.setOnClickListener(v -> {
            voiceDialog.dismiss();
            //restart activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
        });
        voiceEDT = voiceDialog.findViewById(R.id.voiceEDT);
        voicebtn = voiceDialog.findViewById(R.id.voicebtn);
        voicebtn.setOnClickListener(v -> {

            if (customMainPuzzles == null) {
                customMainPuzzles = new ArrayList<>();

                customMainPuzzles.add(null);
                customMainPuzzles.add(null);
                customMainPuzzles.add(null);
                customMainPuzzles.add(null);
                customMainPuzzles.add(null);
                customMainPuzzles.add(null);
                customMainPuzzles.add(null);
                customMainPuzzles.add(null);
                customMainPuzzles.add(null);
                customMainPuzzles.add(null);
            }

            if (image_one.getId() == Constants.selectedPhoto) {

                String firstVoice = voiceEDT.getText().toString();

                customMainPuzzle.setVoice(firstVoice);
                customMainPuzzles.set(0, customMainPuzzle);

            } else if (image_two.getId() == Constants.selectedPhoto) {

                String secondVoice = voiceEDT.getText().toString();

                customMainPuzzle.setVoice(secondVoice);
                customMainPuzzles.set(1, customMainPuzzle);

            } else if (image_three.getId() == Constants.selectedPhoto) {

                String thirdVoice = voiceEDT.getText().toString();

                customMainPuzzle.setVoice(thirdVoice);
                customMainPuzzles.set(2, customMainPuzzle);

            } else if (image_four.getId() == Constants.selectedPhoto) {

                String fourthVoice = voiceEDT.getText().toString();

                customMainPuzzle.setVoice(fourthVoice);
                customMainPuzzles.set(3, customMainPuzzle);

            } else if (image_five.getId() == Constants.selectedPhoto) {

                String fiveVoice = voiceEDT.getText().toString();

                customMainPuzzle.setVoice(fiveVoice);
                customMainPuzzles.set(4, customMainPuzzle);

            } else if (image_six.getId() == Constants.selectedPhoto) {

                String sixVoice = voiceEDT.getText().toString();

                customMainPuzzle.setVoice(sixVoice);
                customMainPuzzles.set(5, customMainPuzzle);

            } else if (image_seven.getId() == Constants.selectedPhoto) {

                String sevenVoice = voiceEDT.getText().toString();

                customMainPuzzle.setVoice(sevenVoice);
                customMainPuzzles.set(6, customMainPuzzle);

            } else if (image_eight.getId() == Constants.selectedPhoto) {

                String eightVoice = voiceEDT.getText().toString();

                customMainPuzzle.setVoice(eightVoice);
                customMainPuzzles.set(7, customMainPuzzle);

            } else if (image_nine.getId() == Constants.selectedPhoto) {

                String nineVoice = voiceEDT.getText().toString();

                customMainPuzzle.setVoice(nineVoice);
                customMainPuzzles.set(8, customMainPuzzle);

            } else if (image_ten.getId() == Constants.selectedPhoto) {

                String tenVoice = voiceEDT.getText().toString();

                customMainPuzzle.setVoice(tenVoice);
                customMainPuzzles.set(9, customMainPuzzle);

            }
            voiceDialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadDataToView();
                }
            });

            saveData(customMainPuzzles);
//            new SaveDataAsyncTask().execute();
//            saveData(customMainPuzzles);


        });


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                voiceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                voiceDialog.show();
            }
        });


    }

    private void loadDataToView() {
        try {


//            SharedPreferences myFirstVoice = this.getSharedPreferences("MyFirstVoice", Context.MODE_PRIVATE);
//            firstTextVoice = myFirstVoice.getString("keyFirstVoice",null);
            if (customMainPuzzles.get(0) != null) {
                firstTextVoice = customMainPuzzles.get(0).getVoice();
            }

            if (customMainPuzzles.get(1) != null) {
                secondTextVoice = customMainPuzzles.get(1).getVoice();
            }

            if (customMainPuzzles.get(2) != null) {
                thirdTextVoice = customMainPuzzles.get(2).getVoice();
            }

            if (customMainPuzzles.get(3) != null) {
                fourtTextVoice = customMainPuzzles.get(3).getVoice();
            }

            if (customMainPuzzles.get(4) != null) {
                fiveTextVoice = customMainPuzzles.get(4).getVoice();
            }

            if (customMainPuzzles.get(5) != null) {
                sixTextVoice = customMainPuzzles.get(5).getVoice();
            }

            if (customMainPuzzles.get(6) != null) {
                sevenTextVoice = customMainPuzzles.get(6).getVoice();
            }

            if (customMainPuzzles.get(7) != null) {
                eightTextVoice = customMainPuzzles.get(7).getVoice();
            }

            if (customMainPuzzles.get(8) != null) {
                nineTextVoice = customMainPuzzles.get(8).getVoice();
            }

            if (customMainPuzzles.get(9) != null) {
                tenTextVoice = customMainPuzzles.get(9).getVoice();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //-------------------------------------this code for changing record voice end here....................


        //-------------------------------------this code for fetching puzzle images start here....................


        ArrayList<ImageView> imageViews = new ArrayList<>();
        imageViews.add(image_one);
        imageViews.add(image_two);
        imageViews.add(image_three);
        imageViews.add(image_four);
        imageViews.add(image_five);
        imageViews.add(image_six);
        imageViews.add(image_seven);
        imageViews.add(image_eight);
        imageViews.add(image_nine);
        imageViews.add(image_ten);


        //image fetching for imageView one
        for(int i = 0; i < imageViews.size(); i++) {
            try {
//                byte[] data = dbHelper.GetBitmapByName("firstgreen");
                Bitmap bitmap = null;
                if (customMainPuzzles != null && customMainPuzzles.get(i) != null && !customMainPuzzles.get(i).getImage().equalsIgnoreCase("")) {
                    byte[] b = Base64.decode(customMainPuzzles.get(i).getImage(), Base64.DEFAULT);
                    bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                    imageViews.get(i).setImageBitmap(bitmap);

                    Picasso.get()
                            .load(new File(customMainPuzzles.get(i).getImage()))
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .into(imageViews.get(i));

                } else {
                    imageViews.get(i).setImageDrawable(getDrawable(R.drawable.image_placeholder));
                }


                if (bitmap != null) {
//                    Bitmap bitmap = Utils.getImage(data);
                    image_one.setImageBitmap(bitmap);
                } else {
                    image_one.setImageDrawable(getDrawable(R.drawable.image_placeholder));
                }
            } catch (Exception e) {
                e.printStackTrace();
                imageViews.get(i).setImageDrawable(getDrawable(R.drawable.image_placeholder));
            }
        }


        //image fetching for imageView two
//        try {
//            Bitmap bitmap = null;
//            if (customMainPuzzles != null && customMainPuzzles.get(1) != null && !customMainPuzzles.get(1).getImage().equalsIgnoreCase("")) {
//                byte[] b = Base64.decode(customMainPuzzles.get(1).getImage(), Base64.DEFAULT);
//                bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//            }
//
//            if (bitmap != null) {
////                    Bitmap bitmap = Utils.getImage(data);
//                image_two.setImageBitmap(bitmap);
//            } else {
//                image_two.setImageDrawable(getDrawable(R.drawable.image_placeholder));
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        //image fetching for imageView three
//        try {
//            Bitmap bitmap = null;
//            if (customMainPuzzles != null && customMainPuzzles.get(2) != null && !customMainPuzzles.get(2).getImage().equalsIgnoreCase("")) {
//                byte[] b = Base64.decode(customMainPuzzles.get(2).getImage(), Base64.DEFAULT);
//                bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//            }
//
//            if (bitmap != null) {
////                    Bitmap bitmap = Utils.getImage(data);
//                image_three.setImageBitmap(bitmap);
//            } else {
//                image_three.setImageDrawable(getDrawable(R.drawable.image_placeholder));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        //image fetching for imageView four
//        try {
//            Bitmap bitmap = null;
//            if (customMainPuzzles != null && customMainPuzzles.get(3) != null && !customMainPuzzles.get(3).getImage().equalsIgnoreCase("")) {
//                byte[] b = Base64.decode(customMainPuzzles.get(3).getImage(), Base64.DEFAULT);
//                bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//            }
//
//            if (bitmap != null) {
////                    Bitmap bitmap = Utils.getImage(data);
//                image_four.setImageBitmap(bitmap);
//            } else {
//                image_four.setImageDrawable(getDrawable(R.drawable.image_placeholder));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        //image fetching for imageView five
//        try {
//            Bitmap bitmap = null;
//            if (customMainPuzzles != null && customMainPuzzles.get(4) != null && !customMainPuzzles.get(4).getImage().equalsIgnoreCase("")) {
//                byte[] b = Base64.decode(customMainPuzzles.get(4).getImage(), Base64.DEFAULT);
//                bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//            }
//
//            if (bitmap != null) {
////                    Bitmap bitmap = Utils.getImage(data);
//                image_five.setImageBitmap(bitmap);
//            } else {
//                image_five.setImageDrawable(getDrawable(R.drawable.image_placeholder));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        //image fetching for imageView six
//        try {
//            Bitmap bitmap = null;
//            if (customMainPuzzles != null && customMainPuzzles.get(5) != null && !customMainPuzzles.get(5).getImage().equalsIgnoreCase("")) {
//                byte[] b = Base64.decode(customMainPuzzles.get(5).getImage(), Base64.DEFAULT);
//                bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//            }
//
//            if (bitmap != null) {
////                    Bitmap bitmap = Utils.getImage(data);
//                image_six.setImageBitmap(bitmap);
//            } else {
//                image_six.setImageDrawable(getDrawable(R.drawable.image_placeholder));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        //image fetching for imageView seven
//        try {
//            Bitmap bitmap = null;
//            if (customMainPuzzles != null && customMainPuzzles.get(6) != null && !customMainPuzzles.get(6).getImage().equalsIgnoreCase("")) {
//                byte[] b = Base64.decode(customMainPuzzles.get(6).getImage(), Base64.DEFAULT);
//                bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//            }
//
//            if (bitmap != null) {
////                    Bitmap bitmap = Utils.getImage(data);
//                image_seven.setImageBitmap(bitmap);
//            } else {
//                image_seven.setImageDrawable(getDrawable(R.drawable.image_placeholder));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        //image fetching for imageView eight
//        try {
//            Bitmap bitmap = null;
//            if (customMainPuzzles != null && customMainPuzzles.get(7) != null && !customMainPuzzles.get(7).getImage().equalsIgnoreCase("")) {
//                byte[] b = Base64.decode(customMainPuzzles.get(7).getImage(), Base64.DEFAULT);
//                bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//            }
//
//            if (bitmap != null) {
////                    Bitmap bitmap = Utils.getImage(data);
//                image_eight.setImageBitmap(bitmap);
//            } else {
//                image_eight.setImageDrawable(getDrawable(R.drawable.image_placeholder));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        //image fetching for imageView nine
//        try {
//            Bitmap bitmap = null;
//            if (customMainPuzzles != null && customMainPuzzles.get(8) != null && !customMainPuzzles.get(8).getImage().equalsIgnoreCase("")) {
//                byte[] b = Base64.decode(customMainPuzzles.get(8).getImage(), Base64.DEFAULT);
//                bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//            }
//
//            if (bitmap != null) {
////                    Bitmap bitmap = Utils.getImage(data);
//                image_nine.setImageBitmap(bitmap);
//            } else {
//                image_nine.setImageDrawable(getDrawable(R.drawable.image_placeholder));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        //image fetching for imageView ten
//        try {
//            Bitmap bitmap = null;
//            if (customMainPuzzles != null && customMainPuzzles.get(9) != null && !customMainPuzzles.get(9).getImage().equalsIgnoreCase("")) {
//                byte[] b = Base64.decode(customMainPuzzles.get(9).getImage(), Base64.DEFAULT);
//                bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//            }
//
//            if (bitmap != null) {
////                    Bitmap bitmap = Utils.getImage(data);
//                image_ten.setImageBitmap(bitmap);
//            } else {
//                image_ten.setImageDrawable(getDrawable(R.drawable.image_placeholder));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
    //---------------------------voice popup end here....................................


    //------------------------------start here savePopup-------------------------------
    /*public void savePopup() {

        saveDialog.setContentView(R.layout.save_btn_popup);
        circle_cancel_save_popup = saveDialog.findViewById(R.id.circle_cancel_save_popup);
        circle_cancel_save_popup.setOnClickListener(v -> saveDialog.dismiss());
        cancel_btn = saveDialog.findViewById(R.id.cancelbtn);
        cancel_btn.setOnClickListener(v -> saveDialog.dismiss());
        save_image_btn = saveDialog.findViewById(R.id.savebtn);
        save_image_btn.setOnClickListener(v -> {


            //saveImage();

            saveDialog.dismiss();

        });

        saveDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        saveDialog.show();

    }*/

    private void saveImage(Bitmap bitmap) {
        int max = 1300;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        if (max >= w || max >= h) {
//                            dbHelper.addBitmap(pick_green_first, Utils.getBytes(bitmap));
            CustomMainPuzzle customPuzzle = new CustomMainPuzzle();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
            customPuzzle.setImage(encodedImage);
            saveDialog.dismiss();
            voicePopup(customPuzzle);
//                    Toast.makeText(CustomMainPuzzleActivity.this, "Save Success", Toast.LENGTH_LONG).show();
        } else {

            warningPopup();
        }
        //bitmap.recycle();
    }

    private class SaveImageAsyncTask extends AsyncTask<Void, Void, Void> {

        private Bitmap bitmap;
        private CustomMainPuzzle customPuzzle;

        public SaveImageAsyncTask(Bitmap bitmap, CustomMainPuzzle customPuzzle) {

            this.bitmap = bitmap;
            this.customPuzzle = customPuzzle;
        }

        @Override
        protected void onPreExecute() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //                        dialog.setMessage("Saving image, please wait.");
                    //                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    //                        dialog.show();
//                    progressDialog.cancel();
//                    progressDialog = null;
//                    progressDialog = progressDialogBuilder.build();
//                    progressDialog.setMessage("Saving Image, Please wait!");
//                    progressDialog.show();
                }
            });

        }
        @Override
        protected Void doInBackground(Void... args) {
            // do background work here


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.WEBP, 50, stream);
            byte[] byteArray = stream.toByteArray();
            String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
            customPuzzle.setImage(encodedImage);


//            progressBar.setVisibility(View.GONE);
//            tv_pBar.setVisibility(View.GONE);

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            // do UI work here

        }
    }





    private class LoadDataAsyncTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

//                        dialog.setMessage("Saving image, please wait.");
//                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                        dialog.show();
//                    progressDialog = progressDialogBuilder.build();
//                    progressDialog.setMessage("Loading Data, Please wait!");
//                    progressDialog.show();
                }
            });
        }
        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... args) {
            // do background work here

            customMainPuzzles = loadData();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadDataToView();
                }
            });


            //Drag and Drop work in OnCreate Method ................Start Here........................
            if (image_one.getDrawable() == null || image_two.getDrawable() == null || image_three.getDrawable() == null || image_four.getDrawable() == null
                    || image_five.getDrawable() == null || image_six.getDrawable() == null || image_seven.getDrawable() == null || image_eight.getDrawable() == null
                    || image_nine.getDrawable() == null || image_ten.getDrawable() == null) {


            } else {
                //..................................drag drop work for score one...............................

//                try {
//                    if (puzzle == 1) {
//                        // 5,7,1
//                        //set image five in DRAGOne
//                        try {
//                            if (DRAGOne.getDrawable() == null) {
//
//                                Bitmap bitmap = null;
//                                if (customMainPuzzles != null && customMainPuzzles.get(4) != null && !customMainPuzzles.get(4).getImage().equalsIgnoreCase("")) {
//                                    byte[] b = Base64.decode(customMainPuzzles.get(4).getImage(), Base64.DEFAULT);
//                                    bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//                                }
//
//                                if (bitmap != null) {
////                    Bitmap bitmap = Utils.getImage(data);
//                                    DRAGOne.setImageBitmap(bitmap);
//                                } else {
//                                    DRAGOne.setImageDrawable(getDrawable(R.drawable.image_placeholder));
//                                }
//                            } else {
//
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        //set image seven in DRAGtwo
//                        try {
//                            if (DRAGtwo.getDrawable() == null) {
//
//                                Bitmap bitmap = null;
//                                if (customMainPuzzles != null && customMainPuzzles.get(6) != null && !customMainPuzzles.get(6).getImage().equalsIgnoreCase("")) {
//                                    byte[] b = Base64.decode(customMainPuzzles.get(6).getImage(), Base64.DEFAULT);
//                                    bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//                                }
//
//                                if (bitmap != null) {
////                    Bitmap bitmap = Utils.getImage(data);
//                                    DRAGtwo.setImageBitmap(bitmap);
//                                } else {
//                                    DRAGtwo.setImageDrawable(getDrawable(R.drawable.image_placeholder));
//                                }
//                            } else {
//
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        //set image one in DRAGthree
//                        try {
//                            if (DRAGthree.getDrawable() == null) {
//
//                                Bitmap bitmap = null;
//                                if (customMainPuzzles != null && customMainPuzzles.get(0) != null && !customMainPuzzles.get(0).getImage().equalsIgnoreCase("")) {
//                                    byte[] b = Base64.decode(customMainPuzzles.get(0).getImage(), Base64.DEFAULT);
//                                    bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//                                }
//
//                                if (bitmap != null) {
////                    Bitmap bitmap = Utils.getImage(data);
//                                    DRAGthree.setImageBitmap(bitmap);
//                                } else {
//                                    DRAGthree.setImageDrawable(getDrawable(R.drawable.image_placeholder));
//                                }
//
//                            } else {
//
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    if (puzzle == 2) {
//                        // 3,4,10
//                        //set image three in DRAGOne
//                        try {
//                            if (DRAGOne.getDrawable() == null) {
//
//                                Bitmap bitmap = null;
//                                if (customMainPuzzles != null && customMainPuzzles.get(2) != null && !customMainPuzzles.get(2).getImage().equalsIgnoreCase("")) {
//                                    byte[] b = Base64.decode(customMainPuzzles.get(2).getImage(), Base64.DEFAULT);
//                                    bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//                                }
//
//                                if (bitmap != null) {
////                    Bitmap bitmap = Utils.getImage(data);
//                                    DRAGOne.setImageBitmap(bitmap);
//                                } else {
//                                    DRAGOne.setImageDrawable(getDrawable(R.drawable.image_placeholder));
//                                }
//
//                            } else {
//
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        //set image four in DRAGtwo
//                        try {
//                            if (DRAGtwo.getDrawable() == null) {
//
//                                Bitmap bitmap = null;
//                                if (customMainPuzzles != null && customMainPuzzles.get(3) != null && !customMainPuzzles.get(3).getImage().equalsIgnoreCase("")) {
//                                    byte[] b = Base64.decode(customMainPuzzles.get(3).getImage(), Base64.DEFAULT);
//                                    bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//                                }
//
//                                if (bitmap != null) {
////                    Bitmap bitmap = Utils.getImage(data);
//                                    DRAGtwo.setImageBitmap(bitmap);
//                                } else {
//                                    DRAGtwo.setImageDrawable(getDrawable(R.drawable.image_placeholder));
//                                }
//
//                            } else {
//
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        //set image ten in DRAGthree
//                        try {
//                            if (DRAGthree.getDrawable() == null) {
//
//                                Bitmap bitmap = null;
//                                if (customMainPuzzles != null && customMainPuzzles.get(9) != null && !customMainPuzzles.get(9).getImage().equalsIgnoreCase("")) {
//                                    byte[] b = Base64.decode(customMainPuzzles.get(9).getImage(), Base64.DEFAULT);
//                                    bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//                                }
//
//                                if (bitmap != null) {
////                    Bitmap bitmap = Utils.getImage(data);
//                                    DRAGthree.setImageBitmap(bitmap);
//                                } else {
//                                    DRAGthree.setImageDrawable(getDrawable(R.drawable.image_placeholder));
//                                }
//
//                            } else {
//
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                    if (puzzle == 3) {
//                        // 2,6,9
//                        //set image two in DRAGOne
//                        try {
//                            if (DRAGOne.getDrawable() == null) {
//
//                                Bitmap bitmap = null;
//                                if (customMainPuzzles != null && customMainPuzzles.get(1) != null && !customMainPuzzles.get(1).getImage().equalsIgnoreCase("")) {
//                                    byte[] b = Base64.decode(customMainPuzzles.get(1).getImage(), Base64.DEFAULT);
//                                    bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//                                }
//
//                                if (bitmap != null) {
////                    Bitmap bitmap = Utils.getImage(data);
//                                    DRAGOne.setImageBitmap(bitmap);
//                                } else {
//                                    DRAGOne.setImageDrawable(getDrawable(R.drawable.image_placeholder));
//                                }
//
//                            } else {
//
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        //set image six in DRAGtwo
//                        try {
//                            if (DRAGtwo.getDrawable() == null) {
//
//                                Bitmap bitmap = null;
//                                if (customMainPuzzles != null && customMainPuzzles.get(5) != null && !customMainPuzzles.get(5).getImage().equalsIgnoreCase("")) {
//                                    byte[] b = Base64.decode(customMainPuzzles.get(5).getImage(), Base64.DEFAULT);
//                                    bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//                                }
//
//                                if (bitmap != null) {
////                    Bitmap bitmap = Utils.getImage(data);
//                                    DRAGtwo.setImageBitmap(bitmap);
//                                } else {
//                                    DRAGtwo.setImageDrawable(getDrawable(R.drawable.image_placeholder));
//                                }
//
//                            } else {
//
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        //set image nine in DRAGthree
//                        try {
//                            if (DRAGthree.getDrawable() == null) {
//
//                                Bitmap bitmap = null;
//                                if (customMainPuzzles != null && customMainPuzzles.get(8) != null && !customMainPuzzles.get(8).getImage().equalsIgnoreCase("")) {
//                                    byte[] b = Base64.decode(customMainPuzzles.get(8).getImage(), Base64.DEFAULT);
//                                    bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//                                }
//
//                                if (bitmap != null) {
////                    Bitmap bitmap = Utils.getImage(data);
//                                    DRAGthree.setImageBitmap(bitmap);
//                                } else {
//                                    DRAGthree.setImageDrawable(getDrawable(R.drawable.image_placeholder));
//                                }
//
//                            } else {
//
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                    if (puzzle == 4) {
//                        // 8,9,3
//                        //set image eight in DRAGOne
//                        try {
//                            if (DRAGOne.getDrawable() == null) {
//
//                                Bitmap bitmap = null;
//                                if (customMainPuzzles != null && customMainPuzzles.get(7) != null && !customMainPuzzles.get(7).getImage().equalsIgnoreCase("")) {
//                                    byte[] b = Base64.decode(customMainPuzzles.get(7).getImage(), Base64.DEFAULT);
//                                    bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//                                }
//
//                                if (bitmap != null) {
////                    Bitmap bitmap = Utils.getImage(data);
//                                    DRAGOne.setImageBitmap(bitmap);
//                                } else {
//                                    DRAGOne.setImageDrawable(getDrawable(R.drawable.image_placeholder));
//                                }
//
//                            } else {
//
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        //set image nine in DRAGtwo
//                        try {
//                            if (DRAGtwo.getDrawable() == null) {
//
//                                Bitmap bitmap = null;
//                                if (customMainPuzzles != null && customMainPuzzles.get(8) != null && !customMainPuzzles.get(8).getImage().equalsIgnoreCase("")) {
//                                    byte[] b = Base64.decode(customMainPuzzles.get(8).getImage(), Base64.DEFAULT);
//                                    bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//                                }
//
//                                if (bitmap != null) {
////                    Bitmap bitmap = Utils.getImage(data);
//                                    DRAGtwo.setImageBitmap(bitmap);
//                                } else {
//                                    DRAGtwo.setImageDrawable(getDrawable(R.drawable.image_placeholder));
//                                }
//
//                            } else {
//
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        //set image three in DRAGthree
//                        try {
//                            if (DRAGthree.getDrawable() == null) {
//
//                                Bitmap bitmap = null;
//                                if (customMainPuzzles != null && customMainPuzzles.get(2) != null && !customMainPuzzles.get(2).getImage().equalsIgnoreCase("")) {
//                                    byte[] b = Base64.decode(customMainPuzzles.get(2).getImage(), Base64.DEFAULT);
//                                    bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//                                }
//
//                                if (bitmap != null) {
////                    Bitmap bitmap = Utils.getImage(data);
//                                    DRAGthree.setImageBitmap(bitmap);
//                                } else {
//                                    DRAGthree.setImageDrawable(getDrawable(R.drawable.image_placeholder));
//                                }
//                            } else {
//
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            }

//            progressDialog.dismiss();

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            // do UI work here

        }
    }


    //---------------------------save popup end here....................................

    //--------------------------------------start here ten images popup work-------------------------------------
    public void tenPopup(int index, double id){

        tenDialog.setContentView(R.layout.delete_edit_photo);
        circle_cancel_ten_popup = tenDialog.findViewById(R.id.circle_cancel_ten_popup);
        circle_cancel_ten_popup.setOnClickListener(v -> tenDialog.dismiss());
        upload_image_btn = tenDialog.findViewById(R.id.upload_image_btn);
        upload_image_btn.setOnClickListener(v -> {

            photoPopup(id);
            tenDialog.dismiss();
        });
        voiceover_btn = tenDialog.findViewById(R.id.voice_edit_btn);
        voiceover_btn.setOnClickListener(v -> {

            voicePopup(customMainPuzzles.get(index));
            tenDialog.dismiss();
        });

        tenDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tenDialog.show();

    }
    //---------------------------ten images popup end here....................................

    //---------------------------select photo popup start here....................................

    public void photoPopup(double id){

        IMAGE_ID = id;

        photoDialog.setContentView(R.layout.select_photo_popup);
        circle_cancel_select_popup = photoDialog.findViewById(R.id.circle_cancel_select_popup);
        circle_cancel_select_popup.setOnClickListener(v -> photoDialog.dismiss());
        gallery_btn = photoDialog.findViewById(R.id.gallerybtn);
        gallery_btn.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent,SELECT_PHOTO);
            photoDialog.dismiss();
            //savePopup();
        });
        camera_btn = photoDialog.findViewById(R.id.camerabtn);
        camera_btn.setOnClickListener(v -> {
            if (Common.checkPermission(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, CAM_REQUEST, true)) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    ContentValues values = new ContentValues();
//                    values.put(MediaStore.Images.Media.TITLE, "MyPicture");
//                    values.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken on " + System.currentTimeMillis());
//                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"Matchymatch");
                directory.mkdirs();
                File output = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Matchymatch/" + puzzleName +id+ ".jpeg");
//                output.getParentFile().mkdirs();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(CustomMainPuzzleActivity.this, "com.techwitz.matchymatch.provider",output));



                startActivityForResult(intent, CAM_REQUEST);
            }
            photoDialog.dismiss();
            //savePopup();
        });

        photoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        photoDialog.show();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == CAM_REQUEST) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                ContentValues values = new ContentValues();
//                values.put(MediaStore.Images.Media.TITLE, "MyPicture");
//                values.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken on " + System.currentTimeMillis());
//                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"Matchymatch");
                directory.mkdirs();
                File output = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Matchymatch/" + puzzleName + Constants.selectedPhoto+ ".jpeg");
//                output.getParentFile().mkdirs();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(CustomMainPuzzleActivity.this, "com.techwitz.matchymatch.provider",output));

                startActivityForResult(intent, CAM_REQUEST);

            }
        }
        else{
            if(requestCode == CAM_REQUEST){
                Toast.makeText(CustomMainPuzzleActivity.this, getString(R.string.permission_camera), Toast.LENGTH_SHORT).show();
            }
        }
    }
    //---------------------------select popup end here....................................
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK ){
            File file = null;
            String fileName = puzzleName + Constants.selectedPhoto + ".jpeg";
            String imagePath = "";
            Bitmap bitmap = null;

            if(data != null)
            {
                if (requestCode == SELECT_PHOTO )
                {
//                    Uri pickedimage = data.getData();
//                    try {
//                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pickedimage);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    invisible_imageView.setImageURI(pickedimage);
                    Uri pickedimage = data.getData();
                    //                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pickedimage);
                    if (data.toString().contains("content:")) {
                        imagePath = Utils.getPath(CustomMainPuzzleActivity.this, pickedimage);
                    } else if (data.toString().contains("file:")) {
                        imagePath = pickedimage.getPath();
                    } else {
                        imagePath = null;
                    }
//                    imagePath = Utils.getPath( CustomMainPuzzleActivity.this.getApplicationContext( ), pickedimage);
                    file = new File(imagePath);
                    Picasso.get()
                            .load(file)
                            .into(invisible_imageView);
                }
                else if (requestCode == CAM_REQUEST)
                {
//                    if(data.getExtras() != null) {
//                        bitmap = (Bitmap) data.getExtras().get("data");
//                    }
//                    else {
//                        try {
//                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    invisible_imageView.setImageBitmap(bitmap);
                    imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Matchymatch/" + fileName;

                    file = new File(imagePath);
                    file.getParentFile().mkdirs();

                    Picasso.get()
                            .load(file)
                            .into(invisible_imageView);
                }
                if(file != null) {
                    //                saveImage(bitmap);
                    CustomMainPuzzle customPuzzle = new CustomMainPuzzle();
//                    new SaveImageAsyncTask(finalBitmap, customPuzzle).execute();

                    customPuzzle.setImage(imagePath);
                    voicePopup(customPuzzle);


                }
            }
            else {
//                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Matchymatch/" + fileName;

                file = new File(imagePath);
                file.getParentFile().mkdirs();
                Picasso.get()
                        .load(file)
                        .into(invisible_imageView);
            }
//            invisible_imageView.setImageBitmap(bitmap);
            if (file != null) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
////                        dialog.setMessage("Saving image, please wait.");
////                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
////                        dialog.show();
//                        progressDialog = progressDialogBuilder.build();
//                        progressDialog.setMessage("Saving Image, Please wait!");
//                        progressDialog.show();
//                    }
//                });
//                saveImage(Constants.selectedPhoto, bitmap);
//                CustomMainPuzzle customPuzzle = new CustomMainPuzzle();
//                new SaveImageAsyncTask(bitmap, customPuzzle).execute();
//                voicePopup(customPuzzle);
                CustomMainPuzzle customPuzzle = new CustomMainPuzzle();
//                    new SaveImageAsyncTask(finalBitmap, customPuzzle).execute();

                customPuzzle.setImage(imagePath);
                voicePopup(customPuzzle);

            }
        }

    }

    public ArrayList<CustomMainPuzzle> loadData() {
        customMainPuzzles = new Gson().fromJson(myPuzzle.getString("customPuzzles", null), TYPE);

        return customMainPuzzles;
    }

    public void saveData (ArrayList<CustomMainPuzzle> list) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences.Editor editor = myPuzzle.edit();

                boolean isComplete = false;

                for(int i = 0; i < list.size(); i++)
                {
                    if(list.get(i) != null)
                    {
                        isComplete = true;
                    }
                    else
                    {
                        isComplete = false;
                        break;
                    }
                }

                if(isComplete)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CustomMainPuzzleActivity.this, R.style.MyDialogTheme);
                    builder.setMessage("Puzzle created. Click on Parrot on homescreen to play.");
                    builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            onBackPressed();
                        }
                    });

                    builder.create().show();
                }

                editor.putString("customPuzzles", new Gson().toJson(list));
                editor.putBoolean("isComplete", isComplete);
                editor.putInt("score", 0);
                editor.apply();
            }
        });



//        customMainPuzzles = list;
    }
    @Override
    protected void onPause() {

        super.onPause();
        stopService(new Intent(CustomMainPuzzleActivity.this, AlarmSoundService.class));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
