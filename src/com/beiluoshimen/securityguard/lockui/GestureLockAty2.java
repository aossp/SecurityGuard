package com.beiluoshimen.securityguard.lockui;

/**
 * Created by shiangchih on 2015/1/2.
 */

import com.beiluoshimen.securityguard.R;
import com.beiluoshimen.securityguard.lock.AtyLock;
import com.beiluoshimen.securityguard.lock.IService;
import com.beiluoshimen.securityguard.lock.LockService;
import com.beiluoshimen.securityguard.lockui.GestureLockView.OnGestureFinishListener;
import com.beiluoshimen.securityguard.tools.UtilTools;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class GestureLockAty2 extends Activity implements OnClickListener {
    // 底部
    private RelativeLayout rel_botton;
    // 提示信息
    private TextView tv_message;
    // 绘制手势的控件
    private GestureLockView gv;
    // 绘制错误时候的动画
    private Animation animation;

    private String drawKey;// 绘制图形的key

    private boolean isSetting;

    private SharedPreferences preferences = null;
    private Editor editor;
    private Button btn_onDraw, btn_finishDraw;
    //忘记密码
    private TextView tv_forget_password;

    private int index = 0;// 标记当前绘制了几次

    private Context context;
    //当前已经绘制的手势 提示
    private GridView gv_lock;

    private LockAdapter lockAdapter;

    private Intent serviceIntent;
	private IService iService;
	private MyConnection connection;
	private String packName;
	private class MyConnection implements ServiceConnection{
// called when we successfully connect to service
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// upward type transfer
			iService = (IService) service;
		}
//	Called when a connection to the Service has been lost. 
//	This typically happens when the process hosting the service has crashed or been killed. 
		@Override
		public void onServiceDisconnected(ComponentName name) {
			finish();
		}
		
	}
	
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_gesturelock);
        context = GestureLockAty2.this;
        setTitle("設置圖形鎖");
        
        
		//get the name of the app need to be locked from caller (lock service)
		Intent intent = getIntent();
		packName = intent.getStringExtra("packname");
		
		// Bind the lock service, execute the method of onBind.
		// provides you with access to the service object while the service is created.
		// thus we get the pointer to "iService" in lock service
		// and we can call stopTempProtect in lock service
		
		// Connect to an application service, creating it if needed. 
		//This defines a dependency between your application and the service.
		serviceIntent = new Intent(this,LockService.class);
		connection = new MyConnection();
		bindService(serviceIntent, connection, BIND_AUTO_CREATE);
        
        preferences = getSharedPreferences(getString(R.string.sp_config), Context.MODE_PRIVATE);
        drawKey = preferences.getString("drawKey", null);
        isSetting = false;

       
        
        editor = preferences.edit();
        InitView();
        SetOnClickListener();
    }

    private void InitView() {
        rel_botton = (RelativeLayout) findViewById(R.id.rel_botton);
        rel_botton.setVisibility(View.GONE);
        tv_message = (TextView) findViewById(R.id.tv_message);
        gv = (GestureLockView) findViewById(R.id.gv);
        btn_finishDraw = (Button) findViewById(R.id.btn_finishDraw);
        btn_onDraw = (Button) findViewById(R.id.btn_onDraw);

        gv_lock = (GridView) findViewById(R.id.gv_lock);
        lockAdapter = new LockAdapter();
        gv_lock.setAdapter(lockAdapter);
        tv_forget_password = (TextView) findViewById(R.id.tv_forget_password);
        if (drawKey == null && "".equals(drawKey)) {
            gv.setResult(true);
        }
        //如果是从设置揭界面进来的隐藏掉忘记密码
        if (isSetting) {
            gv.setKey("");
            tv_forget_password.setVisibility(View.GONE);
            gv_lock.setVisibility(View.VISIBLE);
        } else {
            gv.setKey(drawKey);
            tv_forget_password.setVisibility(View.GONE);
            gv_lock.setVisibility(View.GONE);
        }
        animation = new TranslateAnimation(-10, 10, 0, 0);
        animation.setDuration(50);
        animation.setRepeatCount(10);
        animation.setRepeatMode(Animation.REVERSE);
    }

    private void SetOnClickListener() {
        btn_onDraw.setOnClickListener(this);
        btn_finishDraw.setOnClickListener(this);
        tv_forget_password.setOnClickListener(this);
        //绘制手势的监听
        gv.setOnGestureFinishListener(new OnGestureFinishListener() {
            //success绘制状态 key绘制的密码
            @Override
            public void OnGestureFinish(boolean success, String key) {
                Log.i("Log", success+"    "+key);
                if (!success) {
                    gv.setDrawSucceed(false);
                    if (isSetting) {
                        if (index == 1) {
                            tv_message.setText("與上次繪製的不一樣！");
                            index =1;
                        } else if (index == 1){
                            tv_message.setText("請重新繪製！");
                            gv.setKey("");
                            index = 0;
                        }
                    } else {
                        tv_message.setText("密碼錯誤！");
                    }
                    tv_message.startAnimation(animation);
                    gv.setKey(drawKey);
                } else {
                    if (isSetting) {
                        if (index == 0) {
                            tv_message.setText("請再次繪製！");
                            index =1;
                            lockAdapter.setKey(key);
                        } else if(index == 1){
                            tv_message.setText("繪製成功！");
                            rel_botton.setVisibility(View.VISIBLE);
                            gv.setDrawSucceed(true);
                        }
                    } else {
                        tv_message.setText("密碼正確");
                        iService.callTempStopLock(packName);
                        finish();
                    }
                    drawKey = key;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            //重绘时把所有数据还原
            case R.id.btn_onDraw:
                rel_botton.setVisibility(View.GONE);
                drawKey = "";
                gv.setKey(drawKey);
                tv_message.setText("請重新繪製圖形鎖！");
                index = 0;
                lockAdapter.setKey(drawKey);
                gv_lock.setVisibility(View.VISIBLE);
                gv.setResult(false);
                gv.setDrawSucceed(false);
                gv.clearDraw();
                break;

            case R.id.btn_finishDraw:
                editor.putString("drawKey", drawKey);
                editor.commit();
                finish();
                break;
            case R.id.tv_forget_password:
                gv.setKey("");
                isSetting = true;
                tv_forget_password.setVisibility(View.GONE);
                gv_lock.setVisibility(View.VISIBLE);
                tv_message.setText("請繪製圖形鎖！");
                break;
        }
    }

    class LockAdapter extends BaseAdapter{

        private char keys[];
        public void setKey(String key){
            if (key != null) {
                this.keys = key.toCharArray();
                this.notifyDataSetChanged();
            }
        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 9;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(R.drawable.eqe);
            if (keys != null) {
                for (int i = 0; i < keys.length; i++) {
                    if ((keys[i]-48)==position) {
                        imageView.setImageResource(R.drawable.eqd);
                        continue;
                    }
                }

            }
            return imageView;
        }

    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() ==KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return true; // waste this keydown (which is keycode_back!)
		}
//		else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_HOME) {
//			finish();
//		}
		//TODO waht if the user is HOME and two times open app?????
		//TODO waht if the user is HOME and two times open app?????
		//TODO waht if the user is HOME and two times open app?????
		//TODO waht if the user is HOME and two times open app?????
		return super.onKeyDown(keyCode, event);
	}

}