package cc.coocol.jinxiujob.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.configs.MyConfig;
import cc.coocol.jinxiujob.gsons.ResponseStatus;
import cc.coocol.jinxiujob.models.BaseUserModel;
import cc.coocol.jinxiujob.models.DetailEnterModel;
import cc.coocol.jinxiujob.networks.HttpClient;
import cc.coocol.jinxiujob.networks.URL;

public class UserHomeActivity extends BaseActivity {


    @Bind(R.id.nick)
    TextView nickVIew;
    @Bind(R.id.says)
    TextView saysView;
    @Bind(R.id.nick_photo)
    SimpleDraweeView nickPhotoView;

    boolean isEditted = false;


    private BaseUserModel userModel;

    private String nick;
    private String signature;

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
                nickPhotoView.setImageURI(uri);
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
                        Intent in;
                        if (which == 1) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");//相片类型
                            startActivityForResult(intent, 1);
                        } else if (which == 0) {
                            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                            startActivityForResult(intent, 0);
                        }
                    }
                })
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 0) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            try {
                BufferedOutputStream bos = new BufferedOutputStream(
                        new FileOutputStream("/sdcard/" + MyConfig.uid + ".jpg", false));
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
                showProgressDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            File file = new File("/sdcard/" + MyConfig.uid + ".jpg");
                            ResponseStatus responseStatus = new HttpClient().postUserHead(file, MyConfig.uid, MyConfig.token);
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
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && requestCode == 1) {
            Uri uri = data.getData();
            final File file = new File(uri.getPath());
            showProgressDialog();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ResponseStatus responseStatus = new HttpClient().postUserHead(file, MyConfig.uid, MyConfig.token);
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
    }

    public static void saveImage(Bitmap photo, String spath) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(spath, false));
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
