package yaoxin.example.com.popularmoves.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import yaoxin.example.com.popularmoves.MainActivity;

/**
 * Created by yaoxinxin on 2016/12/20.
 */

public class Utils {

    private static final String SHAREDNAME = "popularMovie";

    private static final String MovieCollected = "movieCollected";

    public static final String SORTWAYKEY = "sortway";
    public static final String POPULARWAY = "sortway_pop";
    public static final String VOTEAVERAGEWAY = "sortway_ro";

    public static final String POPULARCURRENTPAGE = "popularCurrentPage";
    public static final String VOTECURRENTPAGE = "voteCurrentPage";

    public static final int NOTIFICATIONID = 10;
    private static final int PENDINGINTENTREQUESTCODE = 0;

    private static Utils instance;

    private Toast toast;

    private Utils() {

    }

    public static Utils getInstance() {
        if (instance == null) {
            synchronized (Utils.class) {
                if (instance == null) {
                    instance = new Utils();
                }
            }
        }

        return instance;
    }

    public static void setMovieCollected(Context c, boolean collected) {
        SharedPreferences sp = c.getSharedPreferences(SHAREDNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(MovieCollected, collected);
        editor.apply();
    }

    public static Boolean IsMovieCollected(Context c) {
        SharedPreferences sp = c.getSharedPreferences(SHAREDNAME, Context.MODE_PRIVATE);
        return sp.getBoolean(MovieCollected, false);
    }

    public static void setSortway(Context c, String sort) {
        SharedPreferences sp = c.getSharedPreferences(SHAREDNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SORTWAYKEY, sort);
        editor.apply();
    }

    public static String getSortway(Context c) {
        SharedPreferences sp = c.getSharedPreferences(SHAREDNAME, Context.MODE_PRIVATE);
        return sp.getString(SORTWAYKEY, POPULARWAY);
    }

    public static void setString(Context c, String key, String value) {
        SharedPreferences sp = c.getSharedPreferences(SHAREDNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context c, String key, String defaultValue) {
        SharedPreferences sp = c.getSharedPreferences(SHAREDNAME, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    public static void setBoolean(Context c, String key, boolean value) {
        SharedPreferences sp = c.getSharedPreferences(SHAREDNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static Boolean getBoolean(Context c, String key, boolean defaultvalue) {
        SharedPreferences sp = c.getSharedPreferences(SHAREDNAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultvalue);
    }

    public void showToast(Context c, String text) {
        if (toast == null) {
            toast = Toast.makeText(c, text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public static void showMovieNotification(Context c, String title, String text, int iconRes, int largeIconRes, String ticker, int number) {
        Intent intent = new Intent(c, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(c, PENDINGINTENTREQUESTCODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager manager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(c)
                .setContentText(text).setContentTitle(title).setSmallIcon(iconRes)
                .setLargeIcon(BitmapFactory.decodeResource(c.getResources(), largeIconRes))
                .setWhen(System.currentTimeMillis())//设置发出通知的时间为发出通知时的系统时间
                .setTicker(ticker)
                .setOngoing(false)//设置为true则无法通过左右滑动清除，只能通过clear()清除
                .setAutoCancel(true)//点击通知后消失
                .setNumber(number)
                .setPriority(Notification.PRIORITY_DEFAULT)//默认方式
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_VIBRATE)//优先级
                .build();
//        RemoteViews views;

        manager.notify(NOTIFICATIONID, notification);


    }


    public static int[] getScreenWidthAndHeight(Context c) {
        int wh[] = new int[2];
        WindowManager windowManager = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        wh[0] = windowManager.getDefaultDisplay().getWidth();
        wh[1] = windowManager.getDefaultDisplay().getHeight();
//        DisplayMetrics metrics = new DisplayMetrics();
        return wh;
    }

    public static View inflateView(Context c, int ResId, ViewGroup parent) {
        return LayoutInflater.from(c).inflate(ResId, parent, false);
    }

    public static void hideNavigationBar(Activity context) {
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                // | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION; // hide nav bar
        //| View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar

        if (android.os.Build.VERSION.SDK_INT >= 19) {
            uiFlags |= 0x00001000;    //SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide navigation bars - compatibility: building API level is lower thatn 19, use magic number directly for higher API target level
        } else {
            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }

        context.getWindow().getDecorView().setSystemUiVisibility(uiFlags);
    }


    public static Bitmap createQRCode(String text, int version) {

//        if (version >= 40) {
//            version = 40;
//        }
        int size = (version - 1) * 4 + 21;
        Hashtable<EncodeHintType, String> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.QR_VERSION, version + "");
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size);
            int[] pixel = new int[size * size];
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (bitMatrix.get(i, j)) {
                        pixel[i * size + j] = 0xff000000;
                    } else {
                        pixel[i * size + j] = 0xffffffff;
                    }
                }
            }
            bitmap.setPixels(pixel, 0, size, 0, 0, size, size);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap createQRCodewithLogo(String text, int version, Bitmap logo) {
        if (version <= 0) {
            throw new RuntimeException("QRCode version need >= 1");
        }
        int size = (version - 1) * 4 + 21;
        int logoSize = size / 10;//??
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size, hints);
            int matrixWidth = bitMatrix.getWidth();
            int matrixHeight = bitMatrix.getHeight();
            int halfwidth = matrixWidth / 2;
            int halfheight = matrixHeight / 2;
            Matrix m = new Matrix();
            float sx = (float) 2 * logoSize / logo.getWidth();
            float sy = (float) 2 * logoSize / logo.getHeight();
            m.setScale(sx, sy);
            Bitmap logoBitmap = Bitmap.createBitmap(logo, 0, 0, logo.getWidth(), logo.getHeight(), m, false);
            int[] pixel = new int[size * size];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (i > halfwidth - logoSize && i < halfwidth + logoSize && j > halfheight - logoSize &&
                            j < halfheight + logoSize) {
                        pixel[i * size + j] = logoBitmap.getPixel(j - halfwidth + logoSize, i - halfheight + logoSize);
                    } else {
                        if (bitMatrix.get(i, j)) {
                            pixel[i * size + j] = 0xff000000;
                        } else {
                            pixel[i * size + j] = 0xffffffff;
                        }
                    }
                }
            }
            Bitmap returnBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            returnBitmap.setPixels(pixel, 0, size, 0, 0, size, size);
            return returnBitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }


        return null;
    }

    public static int dp2px(Context context, float dp) {
        final float density = context.getResources().getDisplayMetrics().density;
        return (int) ((dp * density + 0.5));


    }

    public static int px2dp(Context context, float px) {
        final float density = context.getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5);

    }

    /**
     * 图片存储到系统相册
     *
     * @param context
     * @param source
     * @param title
     * @param description
     * @return
     */
    public static String caputrePhotoUtils(Context context, Bitmap source, String title, String description) throws IOException {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = null;
        String uristr = null;
        OutputStream outputStream = null;
        if (source != null) {
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            try {
                outputStream = resolver.openOutputStream(uri);
                source.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                outputStream.close();
            }

            long id = ContentUris.parseId(uri);
            // Wait until MINI_KIND thumbnail is generated.
            Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(resolver, id,
                    MediaStore.Images.Thumbnails.MINI_KIND, null);
            // This is for backward compatibility.
            Bitmap microThumb = StoreThumbnail(resolver, miniThumb, id, 50F, 50F,
                    MediaStore.Images.Thumbnails.MICRO_KIND);
        } else {
            resolver.delete(uri, null, null);
            uri = null;
        }

        if (uri != null) {
            uristr = uri.toString();
        }
        return uristr;
    }

    private final static Bitmap StoreThumbnail(ContentResolver cr, Bitmap source, long id, float width, float height, int kind) {

        Matrix matrix = new Matrix();

        float scaleX = width / source.getWidth();
        float scaleY = height / source.getHeight();

        matrix.setScale(scaleX, scaleY);

        Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
                source.getWidth(),
                source.getHeight(), matrix,
                true);

        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Images.Thumbnails.KIND, kind);
        values.put(MediaStore.Images.Thumbnails.IMAGE_ID, (int) id);
        values.put(MediaStore.Images.Thumbnails.HEIGHT, thumb.getHeight());
        values.put(MediaStore.Images.Thumbnails.WIDTH, thumb.getWidth());

        Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream thumbOut = cr.openOutputStream(url);

            thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
            thumbOut.close();
            return thumb;
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }


    public static AlertDialog showWarnMsg(Activity activity, String warnMsg, String positiveButtonText, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(warnMsg);
        builder.setPositiveButton(positiveButtonText, listener);
        builder.setCancelable(true);
        return builder.create();
    }

    public boolean isNFCUseful(Context c) {


        return false;
    }

    public static String zipcompress(String source) {


        return null;
    }


}
