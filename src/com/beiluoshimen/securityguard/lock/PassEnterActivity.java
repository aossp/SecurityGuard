package com.beiluoshimen.securityguard.lock;

import com.beiluoshimen.securityguard.R;
import com.beiluoshimen.securityguard.tools.MD5Tools;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * This class is used to verify the password if the App-lock service is turn on.
 * 
 * 
 * this Activity will be created by LockService.
 * if LockService detect the app that is locked is open.
 * 
 * !!!Note:This class is now useless and substitute by " GestureLockAty2"
 * since we no longer verify user's password but user's gesture.
 * 
 * 
 * @see "" Must see ""
 * !!Important!!: this project co-author "jeffrey40501" is in charge of the whole package of
 * com.beiluoshimen.securityguard.lockui, which comprises of GestureLockAty2.
 * (That package is related to AppLock Gesture UI )
 * 
 * But it seems like that >> his code is not written on his own << and
 * it is "copy-paste" from other unknown author.
 * 
 * and the code in "com.beiluoshimen.securityguard.lockui" is totally disastrous.
 * So, it is recommend to ""re-write the whole code"" in
 * com.beiluoshimen.securityguard.lockui package.
 * 
 * Caveat : NEVER COLLABORATE WITH SOMEONE WHO IS TOTALLY NOOB IN YOUR PROJECT.
 * HE WILL MEES UP YOUR WHOLE PROJECT.  :(  
 * 
 * @author Hsieh Yu-Hua
 */
public class PassEnterActivity extends Activity implements OnClickListener{
	private EditText et_pass;
	private TextView tv_name;
	private ImageView iv_icon;
	private Button btn_sumbit;
	
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
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_pass_enter);
		tv_name = (TextView) findViewById(R.id.tv_name);
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		et_pass = (EditText) findViewById(R.id.et_pass);
		btn_sumbit = (Button) findViewById(R.id.btn_pass_confirm);
		btn_sumbit.setOnClickListener(this);
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
		
		try {
			//use packName to get the details of package
			PackageInfo info = getPackageManager().getPackageInfo(packName, 0);
			tv_name.setText(info.applicationInfo.loadLabel(getPackageManager()));
			iv_icon.setImageDrawable(info.applicationInfo.loadIcon(getPackageManager()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(connection);
	}

	@Override
	public void onClick(View v) {
		if (R.id.btn_pass_confirm == v.getId()) {
			String pwd = MD5Tools.MD5(et_pass.getText().toString().trim());
			
//			System.out.println("the pass input is "+pwd);
			if (TextUtils.isEmpty(pwd)) {
				Toast.makeText(this, "Password is empty.", Toast.LENGTH_SHORT).show();
				return;
			}
			SharedPreferences sp = getSharedPreferences(getString(R.string.sp_config), MODE_PRIVATE);
//			System.out.println("sp pass is "+sp.getString(getString(R.string.sp_password), ""));
			if (sp.getString(getString(R.string.sp_password), "").equals(pwd)) {
				Toast.makeText(this, "Password is correct", Toast.LENGTH_SHORT).show();
				iService.callTempStopLock(packName);
				finish();
			}else {
				Toast.makeText(this, "Password is wrong", Toast.LENGTH_SHORT).show();
				return;
			}
		}
	}
	//don't let the user to use "back-key"
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
