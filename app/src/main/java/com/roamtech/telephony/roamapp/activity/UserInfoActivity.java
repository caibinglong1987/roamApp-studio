package com.roamtech.telephony.roamapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.application.AppConfig;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.bean.LoginInfo;
import com.roamtech.telephony.roamapp.bean.UserProfile;
import com.roamtech.telephony.roamapp.dialog.HeadOptionDialog.OnQuickOptionformClick;
import com.roamtech.telephony.roamapp.util.PictureObtain;
import com.roamtech.telephony.roamapp.util.SPreferencesTool;
import com.roamtech.telephony.roamapp.util.UriHelper;
import com.roamtech.telephony.roamapp.view.ActionSheetDialog;

import java.io.File;

public class UserInfoActivity extends BaseActivity {
    private TextView tvHeadphoto;
    private ImageView userPhoto;

    /**
     * 用户名
     */
    private TextView tvUserName;
    private TextView tvUserNameShow;
    private TextView tvSex;
    private TextView tvSexShow;
    private TextView tvArea;
    private TextView tvAreaShow;
    private static final int REQUEST_CODE_NAME = 1;
    private static final int REQUEST_CODE_GENDER = 2;
    private static final int REQUEST_CODE_ADDRESS = 3;
    private LoginInfo loginInfo;
    private PictureObtain mObtain;
    private Uri distUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        tvHeadphoto = (TextView) findViewById(R.id.tv_headphoto);
        userPhoto = (ImageView) findViewById(R.id.id_circle_image);

        tvUserName = (TextView) findViewById(R.id.tv_username);
        tvUserNameShow = (TextView) findViewById(R.id.id_username_show);

        tvSex = (TextView) findViewById(R.id.tv_sex);
        tvSexShow = (TextView) findViewById(R.id.tv_sex_show);

        tvArea = (TextView) findViewById(R.id.tv_area);
        tvAreaShow = (TextView) findViewById(R.id.tv_area_show);
        loginInfo = getLoginInfo();
        if (loginInfo.getUsername() == null || "".equals(loginInfo.getUsername())) {
            loginInfo.setUsername(LinphoneActivity.instance().getUserProfile().getName());
            loginInfo.setUser_gender(LinphoneActivity.instance().getUserProfile().getGender());
            loginInfo.setUseraddress(LinphoneActivity.instance().getUserProfile().getAddress());
        }
        tvUserNameShow.setText(loginInfo.getUsername());
        tvSexShow.setText(loginInfo.getUser_gender());
        tvAreaShow.setText(loginInfo.getUserAddress());
        if (loginInfo.getUser_photo() == null || "".equals(loginInfo.getUser_photo())){
            userPhoto.setImageResource(R.drawable.logo_default_userphoto);
        }else{
            userPhoto.setImageURI(UriHelper.obtainUri(loginInfo.getUser_photo()));
        }
        mObtain = new PictureObtain();
    }

    @Override
    public void setListener() {
        // TODO Auto-generated method stub
        super.setListener();
        tvHeadphoto.setOnClickListener(this);
        tvUserName.setOnClickListener(this);
        tvSex.setOnClickListener(this);
        tvArea.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_headphoto:
                new ActionSheetDialog(this)
                        .builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem(getString(R.string.takephoto), ActionSheetDialog.SheetItemColor.COLOR_888888,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        mObtain.dispatchTakePictureIntent(UserInfoActivity.this, AppConfig.SET_ADD_PHOTO_CAMERA);
                                    }
                                })
                        .addSheetItem(getString(R.string.takepicturefromAlbum), ActionSheetDialog.SheetItemColor.COLOR_888888,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        mObtain.getLocalPicture(UserInfoActivity.this, AppConfig.SET_ADD_PHOTO_ALBUM);
                                    }
                                }).show();
                break;
            case R.id.tv_username:
                Bundle bundle_name = new Bundle();
                bundle_name.putString("name", tvUserNameShow.getText().toString());
                toActivityForResult(NameEditActivity.class, REQUEST_CODE_NAME, bundle_name);
                break;
            case R.id.tv_sex:
                Bundle bundle_sex = new Bundle();
                bundle_sex.putString("gender", tvSexShow.getText().toString());
                toActivityForResult(SexEditActivity.class, REQUEST_CODE_GENDER, bundle_sex);
                break;
            case R.id.tv_area:
                Bundle bundle_address = new Bundle();
                bundle_address.putString("address", tvAreaShow.getText().toString());
                toActivityForResult(AreaEditActivity.class, REQUEST_CODE_ADDRESS, bundle_address);
                break;
        }
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        // TODO Auto-generated method stub
        super.onActivityResult(arg0, arg1, arg2);
        if (arg1 != RESULT_CANCELED && arg1 == RESULT_OK) {
            switch (arg0) {
                case REQUEST_CODE_NAME:
                    loginInfo.setUsername(arg2.getStringExtra("name"));
                    tvUserNameShow.setText(loginInfo.getUsername());
                    SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userName, loginInfo.getUsername());
                    break;
                case REQUEST_CODE_GENDER:
                    loginInfo.setUser_gender(arg2.getStringExtra("gender"));
                    tvSexShow.setText(loginInfo.getUser_gender());
                    SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_gender, loginInfo.getUser_gender());
                    break;
                case REQUEST_CODE_ADDRESS:
                    loginInfo.setUseraddress(arg2.getStringExtra("address"));
                    tvAreaShow.setText(loginInfo.getUserAddress());
                    SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_address, loginInfo.getUserAddress());
                    break;
                case AppConfig.SET_ADD_PHOTO_CAMERA:
                    //拍照
                    distUri = mObtain.obtainUrl();
                    mObtain.notifyChange(this, mObtain.getUri(this));
                    mObtain.cropBig(this, mObtain.getUri(this), distUri, AppConfig.REQUEST_CROP_PICTURE, 400, 400);
                    break;
                case AppConfig.SET_ADD_PHOTO_ALBUM:
                    //从相册获取
                    if (arg2 != null && arg2.getData() != null) {
                        distUri = mObtain.obtainUrl();
                        mObtain.cropBig(this, arg2.getData(), distUri, AppConfig.REQUEST_CROP_PICTURE, 400, 400);
                    }
                    break;
                case AppConfig.REQUEST_CROP_PICTURE:
                    //裁剪后的图片
                    String path = mObtain.getRealPathFromURI(this, distUri);
                    String imgPath = null;
                    if (!new File(path).exists()) {
                        return;
                    }
                    try {
                        Bitmap bitmap = mObtain.getimage(path);
                        imgPath = mObtain.saveBitmapFile(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Uri hear_uri = Uri.fromFile(new File(imgPath));
                    userPhoto.setImageURI(hear_uri);
                    loginInfo.setUser_photo(imgPath);
                    SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_headUrl, imgPath);
                    //等待完成 图片上传到服务器
                    break;
            }
        }
    }
}
