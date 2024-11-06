package com.teniqs.matchymatch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.view.DragEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.teniqs.matchymatch.Utils.Common;
import com.teniqs.matchymatch.Utils.Constants;
import com.teniqs.matchymatch.Utils.Shaker;
import com.teniqs.matchymatch.Utils.Utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;

import static com.teniqs.matchymatch.Utils.Utils.getUriToResource;

public class CustomMainPuzzlePlayActivity extends AppCompatActivity{

    //    ArrayList<Integer> puzzleAssets, puzzleAssetsChange, puzzleAssetsSave;
//    ArrayList<String> puzzleSounds;
    int score = 0;
    int changer = 0;
    private static final Integer GREY = Color.parseColor("#D2D2D2");
    ImageView imageOne, imageTwo, imageThree, imageFour, imageFive, imageSix, imageSeven, imageEight, imageNine, imageTen, DRAGOne, DRAGtwo,  invisible_imageview, invisible_imageview2, invisible_imageview3;
//    ImageView DRAGThree;
    TextToSpeech tts;
    ConstraintLayout container;
    MediaPlayer winlevelsound;
    SimpleDraweeView ll;
    Random random;
    private String puzzle_name = "";
    private static final String LEN_PREFIX = "Count_";
    private static final String VAL_PREFIX = "IntValue_";
    //    private int value1, value2;
    ImageView homebutton;
    ArrayList<CustomMainPuzzle> customMainPuzzles, customMainPuzzlesChange, customMainPuzzlesSave;
    private static final Type TYPE = new TypeToken<ArrayList<CustomMainPuzzle>>() {}.getType();
    SharedPreferences myPuzzle;
    ImageView gifImageView;
    Picasso picasso;
    ArrayList<ImageView> imageViewsList;
    DraweeController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.custom_puzzle);

        picasso = Picasso.get();

        puzzle_name = getIntent().getExtras().getString("puzzleName");
        myPuzzle = this.getSharedPreferences(puzzle_name+"_MyAwesomePuzzle", Context.MODE_PRIVATE);
        customMainPuzzles = loadData();
        ConstraintLayout parent = findViewById(R.id.parent);
        Drawable background = getResources().getDrawable(getIntent().getExtras().getInt("bgColor"));
        parent.setBackground(background);


        if(customMainPuzzles != null) {
//            for (int i = 0; i < customMainPuzzles.size(); i++) {
//                ImageView imageView = (ImageView) parent.getChildAt(i);
//                Bitmap bitmap = null;
//                if (customMainPuzzles != null && customMainPuzzles.get(i) != null && !customMainPuzzles.get(i).getImage().equalsIgnoreCase("")) {
////                    byte[] b = Base64.decode(customMainPuzzles.get(i).getImage(), Base64.DEFAULT);
//                    File mSaveBit = new File(customMainPuzzles.get(i).getImage()); // Your image file
//                    String filePath = mSaveBit.getPath();
//                    bitmap = BitmapFactory.decodeFile(filePath);
////                    bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//                }
//
//                if (bitmap != null) {
////                    Bitmap bitmap = Utils.getImage(data);
//                    imageView.setImageBitmap(bitmap);
//                } else {
//                    imageView.setImageDrawable(getDrawable(R.drawable.image_placeholder));
//                }
//            }
            imageViewsList = new ArrayList<>();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < customMainPuzzles.size(); i++) {
                        ImageView imageView = (ImageView) parent.getChildAt(i);
                        Bitmap bitmap = null;
                        if (customMainPuzzles != null && customMainPuzzles.get(i) != null && !customMainPuzzles.get(i).getImage().equalsIgnoreCase("")) {
//                    byte[] b = Base64.decode(customMainPuzzles.get(i).getImage(), Base64.DEFAULT);
//                    bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                            picasso.load(new File(customMainPuzzles.get(i).getImage()))
                                    .into(imageView);
                        } else {
                            imageView.setImageDrawable(getDrawable(R.drawable.image_placeholder));
                        }

                        imageViewsList.add(imageView);
//                if (bitmap != null) {
////                    Bitmap bitmap = Utils.getImage(data);
//                    imageView.setImageBitmap(bitmap);
//                } else {
//                    imageView.setImageDrawable(getDrawable(R.drawable.image_placeholder));
//                }
                    }
                }
            });

        }

        random = new Random();
        winlevelsound = MediaPlayer.create(CustomMainPuzzlePlayActivity.this,R.raw.kidscheering);
        tts = new TextToSpeech(getApplicationContext(), status -> {
            if(status!=TextToSpeech.ERROR){

                tts.setLanguage(Constants.speechLocale);
                tts.setPitch((float) 1.1);
                tts.setSpeechRate((float) 0.6);
            }
        });
        ll = findViewById(R.id.ll);

        controller = Fresco.newDraweeControllerBuilder()
//                .setUri("android.resource://com.matchymatchproject.mirassociationdanny.matchymatch/drawable/puzzle_end_gif.gif")
                .setUri(getUriToResource(CustomMainPuzzlePlayActivity.this, R.drawable.puzzle_end_gif))
//                .setUri("https://media4.giphy.com/avatars/100soft/WahNEDdlGjRZ.gif")
                .setAutoPlayAnimations(true)
                .build();


        Handler forWait = new Handler();
        forWait.postDelayed(() -> getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE), 4000);


        imageOne = findViewById(R.id.image_one);
        imageTwo = findViewById(R.id.image_two);
        imageThree = findViewById(R.id.image_three);
        imageFour = findViewById(R.id.image_four);
        imageFive = findViewById(R.id.image_five);
        imageSix = findViewById(R.id.image_six);
        imageSeven = findViewById(R.id.image_seven);
        imageEight = findViewById(R.id.image_eight);
        imageNine = findViewById(R.id.image_nine);
        imageTen = findViewById(R.id.image_ten);
        DRAGOne = findViewById(R.id.image_puzzle_one);
        DRAGtwo = findViewById(R.id.image_puzzle_two);
//        DRAGThree = findViewById(R.id.image_puzzle_three);
        invisible_imageview = findViewById(R.id.invisible_imageview);
        invisible_imageview2 = findViewById(R.id.invisible_imageview2);
        invisible_imageview3 = findViewById(R.id.invisible_imageview3);


        //gifImageView = findViewById(R.id.gif);

        container = findViewById(R.id.parent);

        homebutton = findViewById(R.id.homebtn);

        homebutton.setOnClickListener(v -> {
            Intent LH = new Intent(CustomMainPuzzlePlayActivity.this , ParrotActivity.class);
            LH.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(LH);
        });

        score = myPuzzle.getInt("score", 0);

        if(score != 0 && score != 10)
        {
            customMainPuzzlesChange = loadSavedData();
            customMainPuzzlesSave = new ArrayList<>(customMainPuzzlesChange);


            for(int i = 0; i < customMainPuzzles.size(); i++){
                ImageView imageView = (ImageView)parent.getChildAt(i);
                boolean isMatched = false;

                for(int j = 0; j < customMainPuzzlesChange.size(); j++) {

                    if(customMainPuzzles.get(i).getImage().equalsIgnoreCase(customMainPuzzlesChange.get(j).getImage()))
                    {
                        isMatched = true;
                        break;
                    }
                    else {
                        isMatched = false;
                    }
                }


                if(!isMatched)
                {
                    imageView.setAlpha(0.5f);
                }
            }


        }
        else {
            customMainPuzzlesChange = loadData();
            customMainPuzzlesSave = loadData();

        }


        int num = random.nextInt(customMainPuzzlesChange.size());

//        byte[] b = Base64.decode(customMainPuzzlesChange.get(num).getImage(), Base64.DEFAULT);
//        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        File mSaveBit = new File(customMainPuzzlesChange.get(num).getImage()); // Your image file
        String filePath = mSaveBit.getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        bitmap = rotateImageIfRequired(filePath, bitmap);
        DRAGOne.setImageBitmap(bitmap);
//        value1 = puzzleAssetsChange.get(num);
        customMainPuzzlesChange.remove(num);
        if(customMainPuzzlesChange.size() > 0) {
            num = random.nextInt(customMainPuzzlesChange.size());
//            b = Base64.decode(customMainPuzzlesChange.get(num).getImage(), Base64.DEFAULT);
            //            bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            mSaveBit = new File(customMainPuzzlesChange.get(num).getImage());
            filePath = mSaveBit.getPath();
            bitmap = BitmapFactory.decodeFile(filePath);
            bitmap = rotateImageIfRequired(filePath, bitmap);

            DRAGtwo.setImageBitmap(bitmap);
//            value2 = puzzleAssetsChange.get(num);
            customMainPuzzlesChange.remove(num);
        }
//        if(customMainPuzzlesChange.size() > 0) {
//            num = random.nextInt(customMainPuzzlesChange.size());
////            b = Base64.decode(customMainPuzzlesChange.get(num).getImage(), Base64.DEFAULT);
////            bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//            mSaveBit = new File(customMainPuzzlesChange.get(num).getImage());
//            filePath = mSaveBit.getPath();
//            bitmap = BitmapFactory.decodeFile(filePath);
//            bitmap = rotateImageIfRequired(filePath, bitmap);
//            DRAGThree.setImageBitmap(bitmap);
////            value2 = puzzleAssetsChange.get(num);
//            customMainPuzzlesChange.remove(num);
//        }


        imageOne.setOnDragListener(dragListener);
        imageTwo.setOnDragListener(dragListener);
        imageThree.setOnDragListener(dragListener);
        imageFour.setOnDragListener(dragListener);
        imageFive.setOnDragListener(dragListener);
        imageSix.setOnDragListener(dragListener);
        imageSeven.setOnDragListener(dragListener);
        imageEight.setOnDragListener(dragListener);
        imageNine.setOnDragListener(dragListener);
        imageTen.setOnDragListener(dragListener);

        //DRAGOne.setOnLongClickListener(longClickListener);
        //DRAGtwo.setOnLongClickListener(longClickListener);
        DRAGOne.setOnTouchListener(Common.touchListener);
        DRAGtwo.setOnTouchListener(Common.touchListener);
//        DRAGThree.setOnTouchListener(Common.touchListener);
        winlevelsound = MediaPlayer.create(CustomMainPuzzlePlayActivity.this,R.raw.kidscheering);



    }
    public static Bitmap rotateImageIfRequired(String fileName, Bitmap bitmap){
        Bitmap bMap=bitmap;
        try {
            ExifInterface exif = new ExifInterface(fileName);
            if(exif!=null){
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        bMap = Utils.rotateBitmap(bitmap, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        bMap = Utils.rotateBitmap(bitmap, 180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        bMap = Utils.rotateBitmap(bitmap, 270);
                        break;
                    case ExifInterface.ORIENTATION_NORMAL:
                        bMap=bitmap;
                    default:
                        bMap=bitmap;
                        break;
                }
            }
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bMap;
    }


    public static boolean areDrawablesIdentical(Drawable drawableA, Drawable drawableB) {
        Drawable.ConstantState stateA = drawableA.getConstantState();
        Drawable.ConstantState stateB = drawableB.getConstantState();
        // If the constant state is identical, they are using the same drawable resource.
        // However, the opposite is not necessarily true.
        return (stateA != null && stateB != null && stateA.equals(stateB))
                || getBitmap(drawableA).sameAs(getBitmap(drawableB));
    }

    public static Bitmap getBitmap(Drawable drawable) {
        Bitmap result;
        if (drawable instanceof BitmapDrawable) {
            result = ((BitmapDrawable) drawable).getBitmap();
        } else {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            // Some drawables have no intrinsic width - e.g. solid colours.
            if (width <= 0) {
                width = 1;
            }
            if (height <= 0) {
                height = 1;
            }

            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return result;
    }

    private void onDragSuccess(){
        if(customMainPuzzlesChange.size() > 0) {
            int index = random.nextInt(customMainPuzzlesChange.size());
//            byte[] b = Base64.decode(customMainPuzzlesChange.get(index).getImage(), Base64.DEFAULT);
//            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            File mSaveBit = new File(customMainPuzzlesChange.get(index).getImage());
            String filePath = mSaveBit.getPath();
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            bitmap = rotateImageIfRequired(filePath, bitmap);
            invisible_imageview.setImageBitmap(bitmap);

            if (DRAGtwo.getDrawable() != null && !(areDrawablesIdentical(invisible_imageview.getDrawable(), DRAGtwo.getDrawable()))) {
                DRAGOne.setImageBitmap(bitmap);
                customMainPuzzlesChange.remove(index);

            } else {
                DRAGOne.setImageDrawable(null);
            }
        }else {
            DRAGOne.setImageDrawable(null);
        }
    }

    View.OnDragListener dragListener = new View.OnDragListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public boolean onDrag(View v, DragEvent event) {
            int dragEvent = event.getAction();
            final View view = (View) event.getLocalState();

            switch (dragEvent) {

                case DragEvent.ACTION_DRAG_ENTERED:

                    break;

                case DragEvent.ACTION_DRAG_EXITED:

                    break;

                case DragEvent.ACTION_DROP:

                    if (view != null)
//                    runOnUiThread(new Runnable() {
                        new Handler().post(() -> {


                            Drawable drawableView = ((ImageView) view).getDrawable();
                            Drawable drawableV = ((ImageView) v).getDrawable();
                            if (!areDrawablesIdentical(drawableView, drawableV)) {
//                            if (((BitmapDrawable)((ImageView) view).getDrawable()).getBitmap() != ((BitmapDrawable)((ImageView) v).getDrawable()).getBitmap()) {

                                Shaker shake = new Shaker(v, 0, 15, GREY, Color.RED);
                                shake.shake();
                                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                vibe.vibrate(100);
                                tts.speak(getString(R.string.try_again), TextToSpeech.QUEUE_FLUSH, null);

                            } else {

                                changer += 1;

                                score += 1;



//                                new Thread(){
//                                    public void run()
//                                    {


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//
//                                    }
//                                });
//                                new Thread(() -> {
                                        ArrayList<CustomMainPuzzle> tempList = new ArrayList<>();

                                        final boolean[] isContinue = {true};
                                        final File[] mSaveBit = {null};

                                        for(int i = 0; i < imageViewsList.size(); i++)
                                        {
                                            if(v.getId() == imageViewsList.get(i).getId())
                                            {
                                                tts.speak(customMainPuzzles.get(i).getVoice(), TextToSpeech.QUEUE_FLUSH, null);

                                                for(int j = 0; j < customMainPuzzlesSave.size(); j++)
                                                {
                                                    if(customMainPuzzles.get(i).getImage().equalsIgnoreCase(customMainPuzzlesSave.get(j).getImage())) {
                                                        customMainPuzzlesSave.remove(j);
//                                                                                    break;
                                                        v.setAlpha(0.5f);
                                                        break;
                                                    }
                                                }

                                                tempList.add(customMainPuzzles.get(i));

                                            }
                                        }

//                                        for (int i = 0; i < customMainPuzzles.size(); i++) {
////                                        byte[] b = Base64.decode(customMainPuzzles.get(i).getImage(), Base64.DEFAULT);
////                                        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//                                            if (isContinue[0]) {
//                                                mSaveBit[0] = new File(customMainPuzzles.get(i).getImage());
////                                        String filePath = mSaveBit.getPath();
////                                        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
////                                        bitmap = rotateImageIfRequired(filePath, bitmap);
////                                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
//                                                //invisible_imageview.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));
//                                                //Drawable drawable = invisible_imageview.getDrawable();
////                                            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
////                                            invisible_imageview.setImageBitmap(bitmap);
//
//                                                int finalI = i;
//
//
//                                                Picasso.get()
//                                                        .load(mSaveBit[0])
//                                                        .into(invisible_imageview3, new Callback() {
//                                                            @Override
//                                                            public void onSuccess() {
//
//                                                                Drawable drawable = invisible_imageview3.getDrawable();
//
//                                                                if (areDrawablesIdentical(drawable, ((ImageView) v).getDrawable()))
////                                            if(((BitmapDrawable)invisible_imageview.getDrawable()).getBitmap() == ((BitmapDrawable)((ImageView) v).getDrawable()).getBitmap())
////                                    if(encodedImage[0].equals(customMainPuzzles.get(i).getImage()))
//                                                                {
//                                                                    tts.speak(customMainPuzzles.get(finalI).getVoice(), TextToSpeech.QUEUE_FLUSH, null);
//                                                                    final boolean[] isContinueSave = {true};
//                                                                    for (int j = 0; j < customMainPuzzlesSave.size(); j++) {
////                                                byte[] bytes = Base64.decode(customMainPuzzlesSave.get(j).getImage(), Base64.DEFAULT);
//                                                                        if (isContinueSave[0]) {
//                                                                            mSaveBit[0] = new File(customMainPuzzlesSave.get(j).getImage());
////                                                                filePath = mSaveBit[0].getPath();
////                                                                bitmap = BitmapFactory.decodeFile(filePath);
////                                                                bitmap = rotateImageIfRequired(filePath, bitmap);
////                                                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
////                                                                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
////                                                                byte[] bytes = stream.toByteArray();
////                                                                Drawable drawableSave = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
//
//                                                                            //invisible_imageview.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
//                                                                            //Drawable drawableSave = invisible_imageview.getDrawable();
////                                                    Bitmap bitmapSave = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                                                                            int finalJ = j;
////                                                                            invisible_imageview2.setImageDrawable(null);
//                                                                            Picasso.get()
//                                                                                    .load(mSaveBit[0])
//                                                                                    .into(invisible_imageview2, new Callback() {
//                                                                                        @Override
//                                                                                        public void onSuccess() {
//
////                                                                                            try {
////                                                                                                Thread.sleep(50);
////                                                                                            } catch (InterruptedException e) {
////                                                                                                e.printStackTrace();
////                                                                                            }
//                                                                                            Drawable drawableSave = invisible_imageview2.getDrawable();
//                                                                                            if (areDrawablesIdentical(drawable, drawableSave))
////                                                    if(customMainPuzzles.get(i) == customMainPuzzlesSave.get(j))
//                                                                                            {
//                                                                                                customMainPuzzlesSave.remove(finalJ);
////                                                                                    break;
//                                                                                                v.setAlpha(0.5f);
//                                                                                                isContinueSave[0] = false;
//                                                                                            }
//                                                                                        }
//
//                                                                                        @Override
//                                                                                        public void onError(Exception e) {
//                                                                                            e.printStackTrace();
//                                                                                        }
//                                                                                    });
//                                                                        }
//
//
//                                                                    }
//                                                                    //                                customMainPuzzles.remove(i);
//                                                                    //                                puzzleSounds.remove(i);
//                                                                    tempList.add(customMainPuzzles.get(finalI));
////                                                            break;
//                                                                    isContinue[0] = false;
//                                                                }
//                                                            }
//
//                                                            @Override
//                                                            public void onError(Exception e) {
//                                                                e.printStackTrace();
//                                                            }
//                                                        });
//
//                                            }
//
//
//                                        }
//                                        customMainPuzzles.removeAll(tempList);
                                        tempList.clear();
                                        saveData(customMainPuzzlesSave, score);
                                    }
                                });//.start();


//                                    }
//                                }.start();



//                                new Handler().post(new Runnable() {
//                                    @Override
//                                    public void run() {
                                if (view.getId() == R.id.image_puzzle_one) {
                                    if (customMainPuzzlesChange.size() > 0) {
                                        int index = random.nextInt(customMainPuzzlesChange.size());
//                                        byte[] b = Base64.decode(customMainPuzzlesChange.get(index).getImage(), Base64.DEFAULT);
//                                        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                                        File mSaveBit = new File(customMainPuzzlesChange.get(index).getImage());
                                        String filePath = mSaveBit.getPath();

//                                        bitmap = rotateImageIfRequired(filePath, bitmap);
//                                        invisible_imageview.setImageBitmap(bitmap);

                                        picasso.load(mSaveBit)
                                                .into(invisible_imageview, new Callback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        if (DRAGtwo.getDrawable() != null && ((BitmapDrawable) invisible_imageview.getDrawable()).getBitmap() != ((BitmapDrawable) (DRAGtwo.getDrawable())).getBitmap()) {
                                                            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                                                            bitmap = rotateImageIfRequired(filePath, bitmap);
                                                            DRAGOne.setImageBitmap(bitmap);
                                                            customMainPuzzlesChange.remove(index);

                                                        } else {
                                                            DRAGOne.setImageDrawable(null);
                                                            DRAGOne.setOnTouchListener(null);
                                                        }
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                });

//                                                if (DRAGtwo.getDrawable() != null && !(areDrawablesIdentical(invisible_imageview.getDrawable(), DRAGtwo.getDrawable())) && DRAGThree.getDrawable() != null && !(areDrawablesIdentical(invisible_imageview.getDrawable(), DRAGThree.getDrawable()))) {

                                    } else {
                                        DRAGOne.setImageDrawable(null);
                                        DRAGOne.setOnTouchListener(null);
                                    }
                                } else if (view.getId() == R.id.image_puzzle_two) {

                                    if (customMainPuzzlesChange.size() > 0) {
                                        int index = random.nextInt(customMainPuzzlesChange.size());
//                                        byte[] b = Base64.decode(customMainPuzzlesChange.get(index).getImage(), Base64.DEFAULT);
//                                        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                                        File mSaveBit = new File(customMainPuzzlesChange.get(index).getImage());
                                        String filePath = mSaveBit.getPath();

//                                        bitmap = rotateImageIfRequired(filePath, bitmap);
//                                        invisible_imageview.setImageBitmap(bitmap);

                                        picasso.load(mSaveBit)
                                                .into(invisible_imageview, new Callback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        if (DRAGOne.getDrawable() != null && ((BitmapDrawable) invisible_imageview.getDrawable()).getBitmap() != ((BitmapDrawable) (DRAGOne.getDrawable())).getBitmap() ) {
                                                            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                                                            bitmap = rotateImageIfRequired(filePath, bitmap);
                                                            DRAGtwo.setImageBitmap(bitmap);
                                                            customMainPuzzlesChange.remove(index);
                                                        } else {
                                                            DRAGtwo.setImageDrawable(null);
                                                            DRAGtwo.setOnTouchListener(null);
                                                        }
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                });

//                                                if (DRAGOne.getDrawable() != null && !(areDrawablesIdentical(invisible_imageview.getDrawable(), DRAGOne.getDrawable())) && DRAGThree.getDrawable() != null && !(areDrawablesIdentical(invisible_imageview.getDrawable(), DRAGThree.getDrawable()))) {

                                    } else {
                                        DRAGtwo.setImageDrawable(null);
                                        DRAGtwo.setOnTouchListener(null);
                                    }
                                }
//                                else if (view.getId() == R.id.image_puzzle_three) {
//
//                                    if (customMainPuzzlesChange.size() > 0) {
//                                        int index = random.nextInt(customMainPuzzlesChange.size());
////                                        byte[] b = Base64.decode(customMainPuzzlesChange.get(index).getImage(), Base64.DEFAULT);
////                                        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//                                        File mSaveBit = new File(customMainPuzzlesChange.get(index).getImage());
//                                        String filePath = mSaveBit.getPath();
//
////                                        bitmap = rotateImageIfRequired(filePath, bitmap);
////                                        invisible_imageview.setImageBitmap(bitmap);
//
//                                        picasso.load(mSaveBit)
//                                                .into(invisible_imageview, new Callback() {
//                                                    @Override
//                                                    public void onSuccess() {
//
//                                                        if (DRAGtwo.getDrawable() != null && ((BitmapDrawable) invisible_imageview.getDrawable()).getBitmap() != ((BitmapDrawable) (DRAGtwo.getDrawable())).getBitmap() && DRAGOne.getDrawable() != null && ((BitmapDrawable) invisible_imageview.getDrawable()).getBitmap() != ((BitmapDrawable) (DRAGOne.getDrawable())).getBitmap()) {
//                                                            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
//                                                            bitmap = rotateImageIfRequired(filePath, bitmap);
//                                                            DRAGThree.setImageBitmap(bitmap);
//                                                            customMainPuzzlesChange.remove(index);
//                                                        } else {
//                                                            DRAGThree.setImageDrawable(null);
//                                                            DRAGThree.setOnTouchListener(null);
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void onError(Exception e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                });
//
////                                                if (DRAGOne.getDrawable() != null && !(areDrawablesIdentical(invisible_imageview.getDrawable(), DRAGOne.getDrawable())) && DRAGtwo.getDrawable() != null && !(areDrawablesIdentical(invisible_imageview.getDrawable(), DRAGtwo.getDrawable()))) {
//
//                                    } else {
//                                        DRAGThree.setImageDrawable(null);
//                                        DRAGThree.setOnTouchListener(null);
//                                    }
//                                }
//                                    }
//                                });


//                                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


//                                new Handler().post(new Runnable() {
//                                    @Override
//                                    public void run() {
                                if (score == 10) {
                                    container.setAlpha(0.5f);
                                    ll.setController(controller);
                                    ll.setVisibility(View.VISIBLE);
                                    //                                    bgimage.setAlpha(0.5f);
                                    winlevelsound.start();

                                    //                                    ImageView ll = findViewById(R.id.ll);
                                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    final Handler DuckBackNavigate = new Handler();
                                    DuckBackNavigate.postDelayed(() -> {

                                        Intent IDB = new Intent(CustomMainPuzzlePlayActivity.this, ParrotActivity.class);
                                        //                                          IDB.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        IDB.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(IDB);
                                    }, 3500);

                                    score = 0;
                                    SharedPreferences.Editor editorr = myPuzzle.edit();
                                    editorr.putInt("score", score);
                                    saveData(loadData(), score);
                                    editorr.commit();
                                    // textView.setText("Score : " + score);
                                }


//
//                                    }
//                                });

//                                Handler handler = new Handler();
//
//                                handler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//
//
////                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//
//                                    }
//                                });


                            }


                        });


                    break;
            }
            return true;
        }
    };

    public ArrayList<CustomMainPuzzle> loadData() {
        ArrayList<CustomMainPuzzle> customMainPuzzlesList= new Gson().fromJson(myPuzzle.getString("customPuzzles", null), TYPE);

        return customMainPuzzlesList;
    }
    public ArrayList<CustomMainPuzzle> loadSavedData() {
        ArrayList<CustomMainPuzzle> customMainPuzzlesList = new Gson().fromJson(myPuzzle.getString("customPuzzlesValues", null), TYPE);

        return customMainPuzzlesList;
    }

    public void saveData (ArrayList<CustomMainPuzzle> list, int scoree) {
        SharedPreferences.Editor editor = myPuzzle.edit();

        if(list!= null) {
            editor.putString("customPuzzlesValues", new Gson().toJson(list));
        }
        else {
            editor.putString("customPuzzlesValues", null);
        }
        editor.putBoolean("isComplete", true);
        editor.putInt("score", scoree);
        editor.apply();
//        customMainPuzzles = list;
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
