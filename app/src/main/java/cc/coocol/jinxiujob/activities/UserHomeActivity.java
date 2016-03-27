package cc.coocol.jinxiujob.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.configs.MyConfig;
import cc.coocol.jinxiujob.gsons.ResponseStatus;
import cc.coocol.jinxiujob.models.BaseUserModel;
import cc.coocol.jinxiujob.networks.HttpClient;
import cc.coocol.jinxiujob.networks.URL;

public class UserHomeActivity extends BaseActivity {


    @Bind(R.id.nick)
    TextView nickVIew;
    @Bind(R.id.says)
    TextView saysView;
    @Bind(R.id.nick_photo)
    SimpleDraweeView nickPhotoView;

    public static final String TMP_PATH = "clip_temp.jpg";

    boolean isEditted = false;

    private final int START_ALBUM_REQUESTCODE = 1;
    private final int CAMERA_WITH_DATA = 2;
    private final int CROP_RESULT_CODE = 3;

    private BaseUserModel userModel;

    private Uri newHeadUri;

    private String nick;
    private String signature;

    public static boolean isPhotoChanged = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dismissProgressDialog();
            super.handleMessage(msg);
            if (msg.what == 44) {
                showSimpleSnack("未能获取用户信息", UserHomeActivity.this);
            } else if (msg.what == 88) {
                nickVIew.setText(userModel.getNick());
                saysView.setText(userModel.getSignature());
                nick = userModel.getNick();
                signature = userModel.getSignature();
            } else if (msg.what == 66) {
                showSimpleSnack("修改成功", UserHomeActivity.this);
                nickVIew.setText(nick);
                saysView.setText(signature);
            } else if (msg.what == 63) {
                showSimpleSnack("修改失败", UserHomeActivity.this);
            } else if (msg.what == 27) {
                showSimpleSnack("上传成功", UserHomeActivity.this);
                Uri uri = Uri.parse("http://115.28.22.98:7652/api/v1.0/static/head/" + uid + ".jpg");
                Fresco.getImagePipeline().evictFromCache(uri);
                nickPhotoView.setImageURI(uri);
                isPhotoChanged = true;
            } else if (msg.what == 28) {
                showSimpleSnack("上传失败", UserHomeActivity.this);
            }
        }
    };

    @OnClick(R.id.nick_layout)
    void editNick() {
        new MaterialDialog.Builder(this)
                .title("输入用户名")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        isEditted = true;
                        if (input.toString() != null) {
                            nick = input.toString();
                            modify(nick, signature);
                        }

                    }
                }).show();
    }

    @OnClick(R.id.sign_layout)
    void editSays() {
        new MaterialDialog.Builder(this)
                .title("输入签名")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        isEditted = true;
                        if (input.toString() != null) {
                            signature = input.toString();
                            modify(nick, signature);
                        }
                    }
                }).show();
    }

    @OnClick(R.id.head_image_layout)
    void editPhoto() {
        new MaterialDialog.Builder(this)
                .items(R.array.cameraoralnum)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which == 0) {
                            startCapture();
                        } else if (which == 1) {
                            startAlbum();
                        }
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // String result = null;
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case CROP_RESULT_CODE:
                String path = data.getStringExtra(ClipImageActivity.RESULT_PATH);
                upload(new File(path));
                break;
            case START_ALBUM_REQUESTCODE:
                startCropImageActivity(getFilePath(data.getData()));
                break;
            case CAMERA_WITH_DATA:
                // 照相机程序返回的,再次调用图片剪辑程序去修剪图片
                startCropImageActivity(Environment.getExternalStorageDirectory()
                        + "/" + TMP_PATH);
                break;
        }
        super.onActivityResult(requestCode, resultCode,data);
    }

    // 裁剪图片的Activity
    private void startCropImageActivity(String path) {
        ClipImageActivity.startActivity(this, path, CROP_RESULT_CODE);
    }

    private void startAlbum() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
            intent.setType("image/*");
            startActivityForResult(intent, START_ALBUM_REQUESTCODE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            try {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, START_ALBUM_REQUESTCODE);
            } catch (Exception e2) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }

    private void startCapture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
                Environment.getExternalStorageDirectory(), TMP_PATH)));
        startActivityForResult(intent, CAMERA_WITH_DATA);
    }

    /**
     * 通过uri获取文件路径
     *
     * @param mUri
     * @return
     */
    public String getFilePath(Uri mUri) {
        try {
            if (mUri.getScheme().equals("file")) {
                return mUri.getPath();
            } else {
                return getFilePathByUri(mUri);
            }
        } catch (FileNotFoundException ex) {
            return null;
        }
    }

    // 获取文件路径通过url
    private String getFilePathByUri(Uri mUri) throws FileNotFoundException {
        Cursor cursor = getContentResolver()
                .query(mUri, null, null, null, null);
        cursor.moveToFirst();
        return cursor.getString(1);
    }

    private void upload(final File image) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ResponseStatus responseStatus = new HttpClient().postUserHead(image, MyConfig.uid, MyConfig.token);
                    if (responseStatus != null && responseStatus.getStatus() != null &&
                            responseStatus.getStatus().equals("success")) {
                        handler.sendEmptyMessage(27);
                    } else {
                        handler.sendEmptyMessage(28);
                    }
                } catch (Exception w) {
                    w.toString();
                }

            }
        }).start();
    }


    @OnClick(R.id.goto_resume)
    void gotoResume() {
        Intent intent = new Intent(this, ResumeActivity.class);
        intent.putExtra("user_id", uid);
        startActivity(intent);
    }

    private int uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        ButterKnife.bind(this);

        if (getIntent() != null) {
            uid = getIntent().getIntExtra("user_id", -1);
            showProgressDialog("正在获取");
            requestUserHeader(uid);
            Uri uri = Uri.parse("http://115.28.22.98:7652/api/v1.0/static/head/" + uid + ".jpg");
            nickPhotoView.setImageURI(uri);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("我的资料");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
    }

    private void requestUserHeader(final int userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> m = new HashMap<>(2);
                m.put("token", MyConfig.token);
                ResponseStatus responseStatus = new HttpClient().get(URL.USER_HEADER + userId + "/header", m, false);
                if (responseStatus != null && responseStatus.getStatus() != null &&
                        responseStatus.getStatus().equals("success")) {
                    userModel = HttpClient.getGson().fromJson(responseStatus.getData(),
                            BaseUserModel.class);
                    if (userModel != null) {
                        handler.sendEmptyMessage(88);
                    } else {
                        handler.sendEmptyMessage(44);
                    }
                } else {
                    handler.sendEmptyMessage(44);
                }
            }
        }).start();
    }

    public void modify(final String nick, final String sig) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> map = new HashMap<>(4);
                map.put("token", MyConfig.token);
                map.put("nick", nick);
                map.put("signature", sig);
                ResponseStatus responseStatus = new HttpClient().post(URL.USER_HEADER + MyConfig.uid + "/profile", map, false);
                if (responseStatus != null && responseStatus.getStatus() != null) {
                    if (responseStatus.getStatus().equals("success")) {
                        handler.sendEmptyMessage(66);
                    } else {
                        handler.sendEmptyMessage(63);
                    }
                } else {
                    handler.sendEmptyMessage(63);
                }
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
