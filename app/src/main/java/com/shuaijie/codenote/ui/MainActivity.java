package com.shuaijie.codenote.ui;

import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shuaijie.codenote.adapter.MenuAdapter;
import com.shuaijie.codenote.bean.BmobNote;
import com.shuaijie.codenote.bean.Note;

import com.shuaijie.codenote.R;
import com.shuaijie.codenote.adapter.NoteAdapter;
import com.shuaijie.codenote.bean.User;
import com.shuaijie.codenote.utils.CommonUtils;
import com.shuaijie.codenote.utils.NoteDatabaseUtils;
import com.shuaijie.codenote.views.CircleImageView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends ActionBarActivity {
    private NoteDatabaseUtils noteDatabaseUtils;
    private ListView lvNote;
    private ListView lvMenu;
    private TextView noteTip, tv_user, tv_login;
    public NoteAdapter noteAdapter;
    public ArrayList<Note> notes;
    private CircleImageView avatar;
    private DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("好记");
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        lvMenu = (ListView) findViewById(R.id.lv_menu);
        lvMenu.setAdapter(new MenuAdapter(this));
        lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    BmobUser currentUser = BmobUser.getCurrentUser();
                    if (CommonUtils.isNetworkConnected(getApplicationContext())) {
                        if (currentUser != null) {
                            uploadClouds();
                        } else {
                            CommonUtils.showTip(getApplicationContext(), "请先登录");
                        }
                    } else {
                        CommonUtils.showTip(getApplicationContext(), "网络不可用");
                    }
                } else if (i == 1) {
                    BmobUser currentUser = BmobUser.getCurrentUser();
                    if (CommonUtils.isNetworkConnected(getApplicationContext())) {
                        if (currentUser != null) {
                            downloadClouds();
                        } else {
                            CommonUtils.showTip(getApplicationContext(), "请先登录");
                        }
                    } else {
                        CommonUtils.showTip(getApplicationContext(), "网络不可用");
                    }
                } else if (i == 2) {
                    Intent intent = new Intent(MainActivity.this, FeedbackActivity.class);
                    startActivity(intent);
                } else if (i == 3) {
                    Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                    startActivity(intent);
                }
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNote();
            }
        });
        noteDatabaseUtils = new NoteDatabaseUtils(this);
        lvNote = (ListView) findViewById(R.id.lvNote);
        noteTip = (TextView) findViewById(R.id.noteTip);
        tv_user = (TextView) findViewById(R.id.tv_user);
        tv_login = (TextView) findViewById(R.id.tv_login);
        avatar = (CircleImageView) findViewById(R.id.avatar);
        notes = noteDatabaseUtils.findAll();
        registerForContextMenu(lvNote);
        lvNote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.tvNoteId);
                Intent intent = new Intent(MainActivity.this, ShowNoteActivity.class);
                intent.putExtra("id", textView.getText().toString().trim());
                startActivity(intent);
            }
        });
        if (notes.isEmpty()) {
            lvNote.setVisibility(View.GONE);
            noteTip.setVisibility(View.VISIBLE);
        } else {
            noteAdapter = new NoteAdapter(this, notes);
            lvNote.setAdapter(noteAdapter);
        }
        updateInfo();
    }
    private ActionBarDrawerToggle toggle;
    private boolean isFirst = true;

    private long lastTime;


    private synchronized void updateInfo() {
        final User currentUser = BmobUser.getCurrentUser(User.class);
        if (currentUser != null && CommonUtils.isNetworkConnected(getApplicationContext())) {
            tv_login.setVisibility(View.GONE);
            tv_user.setVisibility(View.VISIBLE);
            tv_user.setText(currentUser.getUsername());
            final String avatorPath = CommonUtils.getSpData(getApplicationContext(), "avatorPath");
            if (avatorPath == null) {
                avatar.setImageResource(R.mipmap.app_logo);
            } else {
                File file = new File(avatorPath);
                if (file.exists()) {
                    Bitmap oldImage = BitmapFactory.decodeFile(CommonUtils.getSpData(getApplicationContext(), "avatorPath"));
                    avatar.setImageBitmap(oldImage);
                } else {
                    BmobFile downFile = new BmobFile(currentUser.getFileName(), "", currentUser.getFileUrl());
                    System.out.println(downFile);
                    downFile.download(new DownloadFileListener() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                //将下载的缓存头像复制到指定路径中
                                CommonUtils.copyFile(s, avatorPath);
                                Bitmap oldImage = BitmapFactory.decodeFile(CommonUtils.getSpData(getApplicationContext(), "avatorPath"));
                                avatar.setImageBitmap(oldImage);
                            }
                        }

                        @Override
                        public void onProgress(Integer value, long l) {
                        }
                    });
                }
            }
            avatar.setClickable(true);
        } else {
            tv_login.setVisibility(View.VISIBLE);
            tv_user.setVisibility(View.GONE);
            avatar.setImageResource(R.mipmap.app_logo);
            avatar.setClickable(false);
        }
    }

    /**
     * 跳转到个人设置界面
     *
     * @param view
     */
    public void startPerson(View view) {
        Intent intent = new Intent(this, PersonInfoActivity.class);
        startActivity(intent);
    }

    /**
     * 跳转到增加笔记界面
     */
    public void addNote() {
        Intent intent = new Intent(this, AddNoteActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (isFirst) {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_LONG).show();
                lastTime = System.currentTimeMillis();
                isFirst = false;
            } else {
                if ((System.currentTimeMillis() - lastTime) < 2000) {
                    this.finish();
                } else {
                    Toast.makeText(this, "再按一次退出", Toast.LENGTH_LONG).show();
                    lastTime = System.currentTimeMillis();
                }
            }
        }
    }

    /**
     * 跳转到登录界面
     *
     * @param view
     */
    public void login(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * 创建上下文菜单
     *
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_note, menu);
    }

    /**
     * 创建选项菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_actionbar, menu);
        MenuItem searchItem = menu.findItem(R.id.searchNote);
        if (searchItem != null) {
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(final String query) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.obj = query;
                            msg.what = 200;
                            myHandler.sendMessage(msg);
                        }
                    }).start();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(final String newText) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.obj = newText;
                            msg.what = 200;
                            myHandler.sendMessage(msg);
                        }
                    }).start();
                    return true;
                }
            });
        }

        return true;
    }

    /**
     * 选项菜单点击事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.share) {
            final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                    {
                            SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA,
                            SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
                    };
            UMImage image = new UMImage(MainActivity.this,
                    BitmapFactory.decodeResource(getResources(), R.mipmap.app_logo));
            new ShareAction(this).setDisplayList(displaylist)
                    .withText("一款高大上的网络记事本")
                    .withTitle("好记")
                    .withTargetUrl("http://codenote.bmob.cn/")
                    .withMedia(image)
                    .setListenerList(new UMShareListener() {
                        @Override
                        public void onResult(SHARE_MEDIA share_media) {

                        }

                        @Override
                        public void onError(SHARE_MEDIA share_media, Throwable throwable) {

                        }

                        @Override
                        public void onCancel(SHARE_MEDIA share_media) {

                        }
                    })
                    .open();
        }
        return toggle.onOptionsItemSelected(item) | super.onOptionsItemSelected(item);
    }

    /**
     * 上下文点击事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        View view = info.targetView;
        final TextView tvId = (TextView) view.findViewById(R.id.tvNoteId);
        switch (item.getItemId()) {
            case R.id.delNote:
                final int delId = Integer.parseInt(tvId.getText().toString().trim());
                BmobUser user = BmobUser.getCurrentUser();
                if (user != null && CommonUtils.isNetworkConnected(getApplicationContext())) {
                    BmobQuery<BmobNote> eq1 = new BmobQuery<BmobNote>();
                    eq1.addWhereEqualTo("userId", user.getObjectId());
                    BmobQuery<BmobNote> eq2 = new BmobQuery<BmobNote>();
                    eq2.addWhereEqualTo("localId", delId + "");
                    List<BmobQuery<BmobNote>> queries = new ArrayList<BmobQuery<BmobNote>>();
                    queries.add(eq1);
                    queries.add(eq2);
                    BmobQuery<BmobNote> mainQuery = new BmobQuery<BmobNote>();
                    mainQuery.and(queries);
                    mainQuery.findObjects(new FindListener<BmobNote>() {
                        @Override
                        public void done(List<BmobNote> list, BmobException e) {
                            if (e == null) {
                                if (list.size() > 0) {
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setTitle("提示!");
                                    final boolean[] isDel = {false};
                                    builder.setIcon(R.mipmap.app_logo);
                                    builder.setMultiChoiceItems(new String[]{"将其从云端删除"}, new boolean[]{false}, new DialogInterface.OnMultiChoiceClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                            isDel[0] = isChecked;
                                        }
                                    });
                                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (isDel[0]) {
                                                noteDatabaseUtils.delete(delId);
                                                notifyListData();
                                                delNoteFromBmob(delId);
                                            } else {
                                                noteDatabaseUtils.delete(delId);
                                                notifyListData();
                                            }
                                        }
                                    });
                                    builder.show();
                                } else {
                                    noteDatabaseUtils.delete(Integer.parseInt(tvId.getText().toString().trim()));
                                    notifyListData();
                                }
                            }
                        }
                    });
                } else {
                    noteDatabaseUtils.delete(Integer.parseInt(tvId.getText().toString().trim()));
                    notifyListData();
                }
                break;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * 根据localId来删除云端的数据
     *
     * @param delId
     */
    private void delNoteFromBmob(int delId) {
        BmobUser user = BmobUser.getCurrentUser();
        BmobQuery<BmobNote> eq1 = new BmobQuery<BmobNote>();
        eq1.addWhereEqualTo("userId", user.getObjectId());
        BmobQuery<BmobNote> eq2 = new BmobQuery<BmobNote>();
        eq2.addWhereEqualTo("localId", delId + "");
        List<BmobQuery<BmobNote>> queries = new ArrayList<BmobQuery<BmobNote>>();
        queries.add(eq1);
        queries.add(eq2);
        BmobQuery<BmobNote> mainQuery = new BmobQuery<BmobNote>();
        mainQuery.and(queries);
        mainQuery.findObjects(new FindListener<BmobNote>() {
            @Override
            public void done(List<BmobNote> list, BmobException e) {
                if (e == null) {
                    BmobNote delNote = list.get(0);
                    System.out.println(delNote.getObjectId());
                    BmobNote bmobNote = new BmobNote();
                    bmobNote.setObjectId(delNote.getObjectId());
                    bmobNote.delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Log.i("云端单条数据", "删除成功");
                            }
                            {
                                Log.i("云端单条数据", "删除失败：" + e.getMessage() + "," + e.getErrorCode());
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 将笔记上传到云端
     */
    private void uploadClouds() {
        final User currentUser = BmobUser.getCurrentUser(User.class);
        final ArrayList<Note> notes = noteDatabaseUtils.findAll();
        if (notes.isEmpty()) {
            CommonUtils.showTip(getApplicationContext(), "没有可上传的笔记");
        } else {
            final int[] addFileCount = {0, 0};
            addFileCount[0] = 0;
            addFileCount[1] = 0;
            CommonUtils.showTip(getApplicationContext(), "正在连接服务器......");
            for (final Note note : notes) {
                BmobQuery<BmobNote> eq1 = new BmobQuery<BmobNote>();
                eq1.addWhereEqualTo("userId", currentUser.getObjectId());
                BmobQuery<BmobNote> eq2 = new BmobQuery<BmobNote>();
                eq2.addWhereEqualTo("localId", note.getId() + "");
                List<BmobQuery<BmobNote>> queries = new ArrayList<BmobQuery<BmobNote>>();
                queries.add(eq1);
                queries.add(eq2);
                BmobQuery<BmobNote> mainQuery = new BmobQuery<BmobNote>();
                mainQuery.and(queries);
                mainQuery.findObjects(new FindListener<BmobNote>() {
                    @Override
                    public void done(List<BmobNote> list, BmobException e) {
                        addFileCount[0]++;
                        if (e == null) {
                            if (list.size() == 0) {
                                addFileCount[1]++;
                                BmobNote bmobNote = new BmobNote();
                                bmobNote.setTitle(note.getTitle());
                                bmobNote.setContent(note.getContent());
                                bmobNote.setTime(note.getTime());
                                bmobNote.setUserId(currentUser.getObjectId());
                                bmobNote.setLocalId(note.getId() + "");
                                bmobNote.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {

                                    }
                                });
                            }
                        } else {
                            Log.i("code笔记", "失败：" + e.getMessage() + "," + e.getErrorCode());
                        }
                        if (addFileCount[0] == notes.size()) {

                            if (addFileCount[1] > 0) {
                                CommonUtils.showTip(getApplicationContext(), "同步到云端成功");
                            } else {
                                CommonUtils.showTip(getApplicationContext(), "没有数据可同步到云端");
                            }
                        }
                    }
                });
            }

        }
    }

    /**
     * 从云端下载笔记
     */
    private void downloadClouds() {
        User currentUser = BmobUser.getCurrentUser(User.class);
        BmobQuery<BmobNote> queryNotes = new BmobQuery<>();
        queryNotes.addWhereEqualTo("userId", currentUser.getObjectId());
        CommonUtils.showTip(getApplicationContext(), "正在连接服务器......");
        queryNotes.findObjects(new FindListener<BmobNote>() {
            @Override
            public void done(List<BmobNote> list, BmobException e) {
                if (e == null) {
                    if (list.isEmpty()) {
                        CommonUtils.showTip(getApplicationContext(), "没有可下载的笔记");
                    } else {
                        int result = noteDatabaseUtils.addNotes(list);
                        if (result > 0) {
                            notifyListData();
                            CommonUtils.showTip(getApplicationContext(), "下载成功");
                        } else {
                            CommonUtils.showTip(getApplicationContext(), "没有可下载的笔记");
                        }
                    }
                } else {
                    CommonUtils.showTip(getApplicationContext(), "下载失败");
                }
            }
        });
    }

    private MyHandler myHandler = new MyHandler(this);

    private class MyHandler extends Handler {
        private WeakReference<MainActivity> weakReference;

        public MyHandler(MainActivity mainActivity) {
            this.weakReference = new WeakReference<MainActivity>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity mainActivity = weakReference.get();
            switch (msg.what) {
                case 100:
                    mainActivity.notes = mainActivity.noteDatabaseUtils.findAll();
                    if (mainActivity.notes != null && !mainActivity.notes.isEmpty()) {
                        mainActivity.lvNote.setVisibility(View.VISIBLE);
                        mainActivity.noteTip.setVisibility(View.GONE);
                        if (mainActivity.noteAdapter == null) {
                            mainActivity.noteAdapter = new NoteAdapter(mainActivity, mainActivity.notes);
                            mainActivity.lvNote.setAdapter(mainActivity.noteAdapter);
                        } else {
                            mainActivity.noteAdapter.refreshData(mainActivity.notes);
                        }
                    } else {
                        mainActivity.lvNote.setVisibility(View.GONE);
                        mainActivity.noteTip.setVisibility(View.VISIBLE);
                    }
                    break;
                case 200:
                    mainActivity.notes = noteDatabaseUtils.findLike((String) msg.obj);
                    if (mainActivity.noteAdapter == null) {
                        mainActivity.noteAdapter = new NoteAdapter(mainActivity, mainActivity.notes);
                        mainActivity.lvNote.setAdapter(mainActivity.noteAdapter);
                    } else {
                        mainActivity.noteAdapter.refreshData(mainActivity.notes);
                    }
                    break;
            }
        }

    }

    /**
     * 通知更新数据
     */
    private void notifyListData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                myHandler.sendEmptyMessage(100);
            }
        }).start();
    }

    /**
     * 当再次可见的时候重新刷新数据
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateInfo();
        notifyListData();
    }
}
