package com.example.patrycja.btappvol2;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private BluetoothChatService mBluetoothConnection;
    private Dialog dialog;
    DrawingView dv;
    private Paint mPaint;
    private Paint mSecondPaint;


    private ActivityCallback mActivityCallback = new ActivityCallback() {
        @Override
        public void setReceivedBytes(String incomingMessage) {
            readReceivedData(incomingMessage);
        }

        private void readReceivedData(final String data){
            String dataSplit[] = data.split(",");

            /*if(dataSplit.length == 1){
                byte [] bitmapdata = dataSplit[0].getBytes();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                final Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dv.setBackground(drawable);
                    }
                });
            }else */{

                final float x = Float.parseFloat(dataSplit[1]);
                final float y = Float.parseFloat(dataSplit[2]);
                String event = dataSplit[0];
                String receivedMode = dataSplit[3];

                switch (receivedMode) {
                    case "c":
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dv.clearDrawing();
                            }
                        });
                        break;
                    case "d":
                        mSecondPaint.reset();
                        mSecondPaint.setAntiAlias(true);
                        mSecondPaint.setDither(true);
                        mSecondPaint.setColor(Color.RED);
                        mSecondPaint.setStyle(Paint.Style.STROKE);
                        mSecondPaint.setStrokeJoin(Paint.Join.ROUND);
                        mSecondPaint.setStrokeCap(Paint.Cap.ROUND);
                        mSecondPaint.setStrokeWidth(12);
                        break;
                    case "e":
                        mSecondPaint.setStrokeWidth(36);
                        mSecondPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                        break;
                }

                switch (event) {
                    case "start":
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dv.touch_start_second(x, y);
                                dv.invalidate();
                            }
                        });
                        break;
                    case "move":
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dv.touch_move_second(x, y);
                                dv.invalidate();
                            }
                        });
                        break;
                    case "up":
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dv.touch_up_second();
                                dv.invalidate();
                            }
                        });
                        break;
                }
            }
        }

        @Override
        public void setBluetoothConnectionInstance(BluetoothChatService instance) {
            mBluetoothConnection = instance;
        }

        @Override
        public void dismissConnectionDialog() {
            dialog.dismiss();
        }

        @Override
        public void setConnectionStatus(final BluetoothChatService.ConnectionStatus status) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    switch (status) {
                        case CONNECTED:
                            Toast.makeText(MainActivity.this, "Connected",Toast.LENGTH_LONG).show();
                            break;
                        case DISCONNECTED:
                            Toast.makeText(MainActivity.this, "Disconnected",Toast.LENGTH_LONG).show();
                            break;
                    }

                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        dv = new DrawingView(this);
        setContentView(dv);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

        mSecondPaint = new Paint();
        mSecondPaint.setAntiAlias(true);
        mSecondPaint.setDither(true);
        mSecondPaint.setColor(Color.RED);
        mSecondPaint.setStyle(Paint.Style.STROKE);
        mSecondPaint.setStrokeJoin(Paint.Join.ROUND);
        mSecondPaint.setStrokeCap(Paint.Cap.ROUND);
        mSecondPaint.setStrokeWidth(12);
    }


    public class DrawingView extends View {

        private String action;
        private String mode = "d";

        public int width;
        public int height;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Path mSecondPath;
        private Paint mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;
        private Path circleSecondPath;

        public DrawingView(Context c) {
            super(c);
            context = c;
            mPath = new Path();
            mSecondPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circleSecondPath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            width = w;
            height = h;

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
            canvas.drawPath(mSecondPath, mSecondPaint);
            canvas.drawPath(circlePath, circlePaint);
            canvas.drawPath(circleSecondPath, circlePaint);
        }

        public void clearDrawing() {
            setDrawingCacheEnabled(false);
            // don't forget that one and the match below,
            // or you just keep getting a duplicate when you save.

            onSizeChanged(width, height, width, height);
            invalidate();

            setDrawingCacheEnabled(true);
        }

        public void saveDrawing(DrawingView drawView){
            try {
                drawView.setDrawingCacheEnabled(true);
                Bitmap bitmap = drawView.getDrawingCache();
                File f = null;
                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                    File file = new File(Environment.getExternalStorageDirectory(),"TTImages_cache");
                    if(!file.exists()){
                        file.mkdirs();
                    }
                    f = new File(file.getAbsolutePath()+file.separator+ "filename"+".png");
                }
                FileOutputStream ostream = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.PNG, 10, ostream);
                ostream.close();
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        private float mX, mY;
        private float mX2, mY2;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_start_second(float x, float y) {
            mSecondPath.reset();
            mSecondPath.moveTo(x, y);
            mX2 = x;
            mY2 = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }

        private void touch_move_second(float x, float y) {
            float dx = Math.abs(x - mX2);
            float dy = Math.abs(y - mY2);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mSecondPath.quadTo(mX2, mY2, (x + mX2) / 2, (y + mY2) / 2);
                mX2 = x;
                mY2 = y;

                circleSecondPath.reset();
                circleSecondPath.addCircle(mX2, mY2, 30, Path.Direction.CW);
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            circlePath.reset();
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }

        private void touch_up_second() {
            mSecondPath.lineTo(mX2, mY2);
            circleSecondPath.reset();
            // commit the path to our offscreen
            mCanvas.drawPath(mSecondPath, mSecondPaint);
            // kill this so we don't double draw
            mSecondPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    action = "start," + x + "," + y + "," + mode;
                    byte[] bytesS = action.getBytes();
                    if(mBluetoothConnection != null) {
                        mBluetoothConnection.write(bytesS);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    action = "move," + x + "," + y + "," + mode;
                    byte[] bytesM = action.getBytes();
                    if(mBluetoothConnection != null){
                        mBluetoothConnection.write(bytesM);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    action = "up," + x + "," + y + "," + mode;
                    byte[] bytesU = action.getBytes();
                    if(mBluetoothConnection != null) {
                        mBluetoothConnection.write(bytesU);
                    }
                    break;
            }
            return true;
        }
    }

    private static final int CLEAR_MENU_ID = Menu.FIRST;
    private static final int SAVE_MENU_ID = Menu.FIRST + 1;
    private static final int ERASE_MENU_ID = Menu.FIRST + 2;
    private static final int DRAW_MENU_ID = Menu.FIRST + 3;
    private static final int CHOOSE_FROM_GALLERY = Menu.FIRST + 4;
    private static final int TAKE_PICTURE = Menu.FIRST + 5;
    private static final int BLUETOOTH_CONNECTION_ID = Menu.FIRST + 6;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, CLEAR_MENU_ID, 0, "Clear").setShortcut('3', 'c');
        menu.add(0, SAVE_MENU_ID, 0, "Save").setShortcut('4', 's');
        menu.add(0, ERASE_MENU_ID, 0, "Erase").setShortcut('5', 'e');
        menu.add(0, DRAW_MENU_ID, 0, "Draw").setShortcut('6', 'd');
        menu.add(0, CHOOSE_FROM_GALLERY, 0, "Choose picture from gallery").setShortcut('7', 'b');
        //menu.add(0, TAKE_PICTURE, 0, "Take a picture").setShortcut('9', 'p');
        menu.add(0, BLUETOOTH_CONNECTION_ID, 0, "Bluetooth connection").setShortcut('8', 'a');

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xFF);

        switch (item.getItemId()) {
            case CLEAR_MENU_ID:
                dv.mode = "c";
                dv.clearDrawing();
                return true;
            case SAVE_MENU_ID:
                dv.saveDrawing(dv);
                return true;
            case ERASE_MENU_ID:
                dv.mode = "e";
                mPaint.setStrokeWidth(36);
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                return true;
            case DRAW_MENU_ID:
                dv.mode = "d";
                mPaint.reset();
                mPaint.setAntiAlias(true);
                mPaint.setDither(true);
                mPaint.setColor(Color.GREEN);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeJoin(Paint.Join.ROUND);
                mPaint.setStrokeCap(Paint.Cap.ROUND);
                mPaint.setStrokeWidth(12);
                return true;
            case CHOOSE_FROM_GALLERY:
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
                return true;
            /*case TAKE_PICTURE:
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);
                return true;*/
            case BLUETOOTH_CONNECTION_ID:
                dialog = new ConnectionDialog(MainActivity.this, mActivityCallback);
                dialog.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            /*case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        //Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        //dv.setBackground(drawable);
                    } catch (IOException e) { z
                        Toast.makeText(MainActivity.this, "You can't set this image as background.",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                break;*/
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        dv.setBackground(drawable);

                        /*if(mBluetoothConnection != null) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] byteArray = stream.toByteArray();

                            mBluetoothConnection.write(byteArray);
                        }*/

                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "You can't set this image as background.",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                }
                break;
        }
    }

    /*BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String test = intent.getStringExtra("theMessage");
            String data[] = test.split(",");
            float x = Float.parseFloat(data[1]);
            float y = Float.parseFloat(data[2]);
            String event = data[0];

            dv.setData(event, x, y);

        }
    };*/


}
