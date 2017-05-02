package com.shuaijie.codenote.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.shuaijie.codenote.R;
import com.shuaijie.codenote.bean.User;
import com.shuaijie.codenote.utils.CommonUtils;
import com.shuaijie.codenote.views.CircleImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class PersonInfoActivity extends BaseActivity implements View.OnClickListener {
    private CircleImageView perAvatar;
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private static int output_X = 480;
    private static int output_Y = 480;
    private Button uploadAvatar, updatePass, logout;
    private AlertDialog.Builder builder;

    @Override
    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_person_info);
        actionBar.setTitle("个人信息");
        uploadAvatar = (Button) findViewById(R.id.uploadAvatar);
        updatePass = (Button) findViewById(R.id.updatePass);
        logout = (Button) findViewById(R.id.logout);
        perAvatar = (CircleImageView) findViewById(R.id.personAvatar);
        uploadAvatar.setOnClickListener(this);
        updatePass.setOnClickListener(this);
        logout.setOnClickListener(this);
        if (CommonUtils.getSpData(getApplicationContext(), "avatorPath") == null) {
            perAvatar.setImageResource(R.mipmap.app_logo);
        } else {
            Bitmap oldImage = BitmapFactory.decodeFile(CommonUtils.getSpData(getApplicationContext(), "avatorPath"));
            perAvatar.setImageBitmap(oldImage);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout:
                BmobUser.logOut();
                CommonUtils.delSpData(getApplicationContext(), "avatorPath");
                CommonUtils.delSpData(getApplicationContext(), "currentUserId");
                finish();
                break;
            case R.id.updatePass:
                Intent intent = new Intent(this, UpdatePassActivity.class);
                startActivity(intent);
                break;
            case R.id.uploadAvatar:
                if (builder == null) {
                    builder = new AlertDialog.Builder(this);
                }
                final AlertDialog dialog = builder.create();
                View dialogView = LayoutInflater.from(this).inflate(R.layout.select_takephoto, null);
                TextView take_picture = (TextView) dialogView.findViewById(R.id.take_picture);
                TextView show_picture = (TextView) dialogView.findViewById(R.id.show_picture);
                dialog.setTitle("请选择上传方式");
                dialog.setIcon(R.mipmap.app_logo);
                dialog.setView(dialogView);
                dialog.show();
                take_picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        choseHeadImageFromCameraCapture();
                    }
                });
                show_picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        choseHeadImageFromGallery();
                    }
                });
                dialogView = null;
                break;
        }
    }

    //从本地相册选取图片作为头像
    private void choseHeadImageFromGallery() {
        Intent intentFromGallery = new Intent();
        // 设置文件类型
        intentFromGallery.setType("image/*");
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
    }

    // 启动手机相机拍摄照片作为头像
    private void choseHeadImageFromCameraCapture() {
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 判断存储卡是否可用，存储照片文件
        if (hasSdcard()) {
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                    .fromFile(new File(CommonUtils.DIR_PIC, CommonUtils.avatorName(getApplicationContext()))));
        }

        startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        // 用户没有进行有效的设置操作，返回
        if (resultCode == RESULT_CANCELED) {
            CommonUtils.showTip(getApplicationContext(), "取消");
            return;
        }

        switch (requestCode) {
            case CODE_GALLERY_REQUEST:
                Uri uri = intent.getData();
                cropRawPhoto(uri);
                break;

            case CODE_CAMERA_REQUEST:
                if (hasSdcard()) {
                    File tempFile = new File(
                            CommonUtils.DIR_PIC,
                            CommonUtils.avatorName(getApplicationContext()));
                    cropRawPhoto(Uri.fromFile(tempFile));
                } else {
                    CommonUtils.showTip(getApplicationContext(), "没有sd卡");
                }

                break;

            case CODE_RESULT_REQUEST:
                if (intent != null) {
                    setImageToHeadView(intent);
                }

                break;
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    /**
     * 上传头像
     */
    private void uploadAvator() {
        User currentUser = BmobUser.getCurrentUser(User.class);
        BmobFile delFile = new BmobFile();
        //将之前上传的头像删除
        if (currentUser.getFileUrl() != null) {
            delFile.setUrl(currentUser.getFileUrl());
            delFile.delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {

                }
            });
        }
        String picPath = CommonUtils.DIR_PIC + "/" + CommonUtils.avatorName(getApplicationContext());
        final BmobFile addFile = new BmobFile(new File(picPath));
        addFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    User user = new User();
                    user.setFileName(addFile.getFilename());
                    user.setFileUrl(addFile.getFileUrl());
                    User currentUser = BmobUser.getCurrentUser(User.class);
                    user.update(currentUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                CommonUtils.showTip(getApplicationContext(), "头像上传成功");
                                myHandler.sendEmptyMessage(100);//通知更改头像
                            } else {
                                CommonUtils.showTip(getApplicationContext(), "头像上传失败" + e.getMessage());
                            }
                        }
                    });
                } else {
                    CommonUtils.showTip(getApplicationContext(), e.getMessage());
                }
            }

        });
    }

    private MyHandler myHandler = new MyHandler(this);

    private class MyHandler extends Handler {
        WeakReference<PersonInfoActivity> weakReference;

        public MyHandler(PersonInfoActivity personInfoActivity) {
            weakReference = new WeakReference<PersonInfoActivity>(personInfoActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final PersonInfoActivity personInfoActivity = weakReference.get();
            switch (msg.what) {
                case 100:
                    personInfoActivity.perAvatar.setImageBitmap(BitmapFactory.decodeFile(CommonUtils.DIR_PIC + "/" + CommonUtils.avatorName(getApplicationContext())));
                    break;
            }
        }
    }


    /**
     * 裁剪原始的图片
     */
    public void cropRawPhoto(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // 设置裁剪
        intent.putExtra("crop", true);

        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", output_X);
        intent.putExtra("outputY", output_Y);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }

    /**
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private void setImageToHeadView(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            saveBitmap(photo);
            CommonUtils.setSpData(getApplicationContext(), "avatorPath", CommonUtils.DIR_PIC + "/" + CommonUtils.avatorName(getApplicationContext()));
            uploadAvator();
            myHandler.sendEmptyMessage(100);
        }
    }

    //将裁剪的图片保存
    private Uri saveBitmap(Bitmap bitmap) {
        File tmpDir = new File(Environment.getExternalStorageDirectory() + "/codeNote");
        if (!tmpDir.exists()) {
            tmpDir.mkdir();
        }
        File img = new File(tmpDir.getAbsolutePath() + "/" + CommonUtils.avatorName(getApplicationContext()));
        try {
            FileOutputStream fos = new FileOutputStream(img);
            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fos);
            fos.flush();
            fos.close();
            return Uri.fromFile(img);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */

    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 有存储的SDCard
            return true;
        } else {
            return false;
        }
    }
}
