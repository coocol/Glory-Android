package cc.coocol.jinxiujob.fragments;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.folderselector.FileChooserDialog;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.activities.ClipImageActivity;
import cc.coocol.jinxiujob.activities.ResumeActivity;
import cc.coocol.jinxiujob.configs.MyConfig;
import cc.coocol.jinxiujob.gsons.ResponseStatus;
import cc.coocol.jinxiujob.models.BaseUserModel;
import cc.coocol.jinxiujob.models.ResumeModel;
import cc.coocol.jinxiujob.networks.HttpClient;
import cc.coocol.jinxiujob.networks.URL;

public class ResumeFragment extends BaseFragment implements FileChooserDialog.FileCallback {

    @Bind(R.id.name)
    TextView nameView;
    @Bind(R.id.phone)
    TextView phoneView;
    @Bind(R.id.email)
    TextView emailView;
    @Bind(R.id.birthday)
    TextView birthdayView;
    @Bind(R.id.man_photo)
    SimpleDraweeView photoView;
    @Bind(R.id.college)
    TextView collegeView;
    @Bind(R.id.profess)
    TextView professView;
    @Bind(R.id.enter_time)
    TextView enterTimeView;
    @Bind(R.id.award)
    TextView awardView;
    @Bind(R.id.mysay)
    TextView describeView;
    @Bind(R.id.experience)
    TextView experienceView;
    @Bind(R.id.english)
    TextView englishView;
    @Bind(R.id.gender)
    TextView genderView;
    @Bind(R.id.place)
    TextView placeView;
    @Bind(R.id.hometown)
    TextView homeTownView;
    @Bind(R.id.file_click)
    TextView fileView;

    private final int START_ALBUM_REQUESTCODE = 1;
    private final int CAMERA_WITH_DATA = 2;
    private final int CROP_RESULT_CODE = 3;

    private BaseUserModel userModel;

    @OnClick(R.id.file_click)
    void uploadFileResume() {
        new MaterialDialog.Builder(getContext())
                .items(new String[]{"PDF文件", "Microsoft Word文件"})
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which == 0) {
//                            new FileChooserDialog.Builder(ResumeFragment.this)
//                                    .chooseButton("确定")  // changes label of the choose button
//                                    .initialPath("/")  // changes initial path, defaults to external storage directory
//                                    .mimeType("application/pdf") // Optional MIME type filter
//                                    .tag("optional-identifier")
//                                    .show();
                        } else if (which == 1) {
//                            new FileChooserDialog.Builder(getActivity())
//                                    .chooseButton("确定")  // changes label of the choose button
//                                    .initialPath("/")  // changes initial path, defaults to external storage directory
//                                    .mimeType("application/msword") // Optional MIME type filter
//                                    .tag("optional-identifier")
//                                    .show();
                        }
                    }
                })
                .show();
    }

    private ResumeModel resumeModel = new ResumeModel();

    private ResumeActivity activity;

    private final int GET_SUCCESS = 40;
    private final int GET_FAIL = 42;
    private final int MODIFY_SUCCESS = 99;
    private final  int M_FAIL = 87;

    boolean isEdit = false;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            activity.dismissProgressDialog();
            if (msg.what == GET_FAIL) {
                activity.showSimpleSnack("你尚未填写简历", activity);
            } else if (msg.what == GET_SUCCESS) {
                refreshData();
            } else if (msg.what == MODIFY_SUCCESS) {
                activity.showSimpleSnack("修改成功", activity);
            } else if (msg.what == M_FAIL) {
                activity.showSimpleSnack("修改失败", activity);
            }
        }
    };

    private void refreshData() {
        if (resumeModel != null) {
            nameView.setText(resumeModel.getName());
            genderView.setText(resumeModel.getGender());
            phoneView.setText(resumeModel.getPhone());
            emailView.setText(resumeModel.getEmail());
            placeView.setText(resumeModel.getPlace());
            homeTownView.setText(resumeModel.getHometown());
            collegeView.setText(resumeModel.getCollege());
            enterTimeView.setText(resumeModel.getCollegeTime());
            professView.setText(resumeModel.getProfess());
            describeView.setText(resumeModel.getDescription());
            awardView.setText(resumeModel.getAward());
            birthdayView.setText(resumeModel.getBirthday());
            experienceView.setText(resumeModel.getExperience());
            englishView.setText(resumeModel.getEnglish());
        }
    }

    void showSingleInput(String title, final TextView editText, int inputType, final String field) {
        new MaterialDialog.Builder(getActivity())
                .inputType(inputType)
                .input(title, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        editText.setText(input);
                        updateResume(MyConfig.uid, field, input.toString());
                    }
                }).show();
    }


    void showMultipleInput(final TextView editText, String title, final String field) {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).title(title)
                .customView(R.layout.multilines_edittext, true).positiveText("确定").build();
        final EditText editText1 = (EditText) dialog.getCustomView().findViewById(R.id.tptxt);
        dialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(MaterialDialog dialog, DialogAction which) {
                String s = editText1.getText().toString();
                editText.setText(s);
                updateResume(MyConfig.uid, field, s);
            }
        });
        dialog.show();
    }

    @OnClick(R.id.name_rl)
    void editName() {
        showSingleInput("真实姓名", nameView, InputType.TYPE_CLASS_TEXT, "name");
    }

    @OnClick(R.id.phone_ll)
    void editPhone() {
        showSingleInput("联系电话", phoneView, InputType.TYPE_CLASS_PHONE, "phone");
    }

    @OnClick(R.id.email_ll)
    void editMail() {
        showSingleInput("联系邮箱", emailView, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, "email");
    }

    @OnClick(R.id.birth_ll)
    void editBirth() {
        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String  s = year + "-" +  (monthOfYear + 1) + "-" + dayOfMonth;
                birthdayView.setText(s);
                updateResume(MyConfig.uid, "birthday", s);
            }
        }, 2000, 1, 1).show();
    }

    @OnClick(R.id.gender_ll)
    void editGender() {
        new MaterialDialog.Builder(getActivity())
                .items(new String[]{"男","女"}).autoDismiss(true)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which == 0) {
                            genderView.setText("男");
                        } else if (which == 1) {
                            genderView.setText("女");
                        }
                    }
                })
                .show();
    }

    @OnClick(R.id.place_ll)
    void editPlace() {
        showSingleInput("现居地", placeView, InputType.TYPE_CLASS_TEXT, "place");
    }

    @OnClick(R.id.hometown_ll)
    void editHOme() {
        showSingleInput("户籍地", homeTownView, InputType.TYPE_CLASS_TEXT, "hometown");
    }

    @OnClick(R.id.photo_ll)
    void editPhoto() {
        new MaterialDialog.Builder(getActivity())
                .items(new String[]{"拍照","相册"}).autoDismiss(true)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which == 0) {

                        } else if (which == 1) {

                        }
                    }
                })
                .show();
    }

    @OnClick(R.id.college_ll)
    void editCollege() {
        showSingleInput("就读学校", collegeView, InputType.TYPE_CLASS_TEXT, "college");
    }

    @OnClick(R.id.profess_ll)
    void editProfess() {
        showSingleInput("就读专业", professView, InputType.TYPE_CLASS_TEXT, "profess");
    }

    @OnClick(R.id.enter_ll)
    void editEnter() {
        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String  s = year + "-" +  (monthOfYear + 1) + "-" + dayOfMonth;
                enterTimeView.setText(s);
                updateResume(MyConfig.uid, "college_time", s);
            }
        }, 2015, 1, 1).show();
    }

    @OnClick(R.id.english_ll)
    void editEnglish() {
        showMultipleInput(englishView, "外语能力", "english");
    }

    @OnClick(R.id.award_ll)
    void editAward() {
        showMultipleInput(awardView, "获奖情况", "award");
    }

    @OnClick(R.id.experience_ll)
    void editExp() {
        showMultipleInput(experienceView, "专业经验", "experience");
    }

    @OnClick(R.id.mysay_ll)
    void editSay() {
        showMultipleInput(describeView, "自我评价", "description");
    }


    @Override
    public String getTile() {
        return null;
    }

    public ResumeFragment() {

    }

    public static ResumeFragment newInstance(String param1, String param2) {
        ResumeFragment fragment = new ResumeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (ResumeActivity) getActivity();
        requestResume(MyConfig.uid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_resume, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    private void requestResume(final int userId) {
        activity.showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> m = new HashMap<>(2);
                m.put("token", MyConfig.token);
                ResponseStatus responseStatus = new HttpClient().get(URL.RESUME + userId, m, false);
                if (responseStatus != null && responseStatus.getStatus() != null)  {
                    if (responseStatus.getStatus().equals("fail") && responseStatus.getMsg().equals("no resume")) {
                        handler.sendEmptyMessage(GET_FAIL);
                    } else if (responseStatus.getStatus().equals("success")){
                        try{
                            resumeModel = HttpClient.getGson().fromJson(responseStatus.getData() ,ResumeModel.class);
                            handler.sendEmptyMessage(GET_SUCCESS);
                        } catch (Exception e) {
                            e.toString();
                        }
                    }
                }
            }
        }).start();
    }

    private void updateResume(final int userId, final String filed, final String value) {
        activity.showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> m = new HashMap<>(4);
                m.put("field", filed);
                m.put("value", value);
                m.put("token", MyConfig.token);
                ResponseStatus responseStatus = new HttpClient().post(URL.RESUME + userId, m, false);
                if (responseStatus != null && responseStatus.getStatus() != null && responseStatus.getStatus().equals("success"))  {
                    handler.sendEmptyMessage(MODIFY_SUCCESS);
                } else {
                    handler.sendEmptyMessage(M_FAIL);
                }
            }
        }).start();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // String result = null;
//        if (resultCode != RESULT_OK) {
//            return;
//        }
//
//        switch (requestCode) {
//            case CROP_RESULT_CODE:
//                String path = data.getStringExtra(ClipImageActivity.RESULT_PATH);
//                upload(new File(path));
//                break;
//            case START_ALBUM_REQUESTCODE:
//                startCropImageActivity(getFilePath(data.getData()));
//                break;
//            case CAMERA_WITH_DATA:
//                // 照相机程序返回的,再次调用图片剪辑程序去修剪图片
//                startCropImageActivity(Environment.getExternalStorageDirectory()
//                        + "/" + TMP_PATH);
//                break;
//        }
//        super.onActivityResult(requestCode, resultCode,data);
//    }

    // 裁剪图片的Activity
//    private void startCropImageActivity(String path) {
//        ClipImageActivity.startActivity(this, path, CROP_RESULT_CODE);
//    }

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

//    private void startCapture() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
//                Environment.getExternalStorageDirectory(), TMP_PATH)));
//        startActivityForResult(intent, CAMERA_WITH_DATA);
//    }

    /**
     * 通过uri获取文件路径
     *
     * @param mUri
     * @return
     */
//    public String getFilePath(Uri mUri) {
//        try {
//            if (mUri.getScheme().equals("file")) {
//                return mUri.getPath();
//            } else {
//                return getFilePathByUri(mUri);
//            }
//        } catch (FileNotFoundException ex) {
//            return null;
//        }
//    }

//    // 获取文件路径通过url
//    private String getFilePathByUri(Uri mUri) throws FileNotFoundException {
//        Cursor cursor = getContentResolver()
//                .query(mUri, null, null, null, null);
//        cursor.moveToFirst();
//        return cursor.getString(1);
//    }

    @Override
    public void onFileSelection(FileChooserDialog dialog, File file) {

    }
}
