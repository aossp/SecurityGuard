package com.beiluoshimen.securityguard.antitheft;

import java.util.Iterator;

import com.beiluoshimen.securityguard.R;
import com.beiluoshimen.securityguard.tools.MD5Tools;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.Theme;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;
/**
 * This class can be used to receive message and according to this message,
 * wipe out the content of this phone.
 * 
 * @author Hsieh Yu-Hua
 * @date 2014年11月20日下午9:42:05
 */



public class BrSMS extends BroadcastReceiver{
	private static final String TAG = "BrSMS";
	SharedPreferences sp;
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "receive message");
		sp = context.getSharedPreferences(context.getString(R.string.sp_config),Context.MODE_PRIVATE);
		
		//once the message is received , the message content is in the array of "pdus"
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		
		//create one instance of the device policy manager
		DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName admin = new ComponentName(context, Admin.class);
		for(Object obj:objs){
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
			String body = smsMessage.getMessageBody();
			Log.d(TAG, "this is sms body:"+body);
			//check if the password match the message body
			if (MD5Tools.MD5(body).equals(sp.getString(context.getString(R.string.sp_password),""))) {
				Log.i(TAG, "收到符合信息，手機資料即將全部抹處！！!!");
				Toast.makeText(context, "機器即將回到原廠", Toast.LENGTH_SHORT).show();
				//check if we have the admin authority to wipe out data on devices....
				if (dpm.isAdminActive(admin)) {
					//TODO  TEST TEST  wipe out all data
//					dpm.wipeData(0);//reboot the device
					Log.i(TAG, "收到符合信息，手機資料即將全部抹處！！!!");
					Log.i(TAG, "收到符合信息，手機資料即將全部抹處！！!!");
					Log.i(TAG, "收到符合信息，手機資料即將全部抹處！！!!");
					Log.i(TAG, "收到符合信息，手機資料即將全部抹處！！!!");
					Log.i(TAG, "收到符合信息，手機資料即將全部抹處！！!!");
					Log.i(TAG, "收到符合信息，手機資料即將全部抹處！！!!");
					Log.i(TAG, "收到符合信息，手機資料即將全部抹處！！!!");
					Log.i(TAG, "收到符合信息，手機資料即將全部抹處！！!!");
					Log.i(TAG, "收到符合信息，手機資料即將全部抹處！！!!");
					Log.i(TAG, "收到符合信息，手機資料即將全部抹處！！!!");
					Log.i(TAG, "收到符合信息，手機資料即將全部抹處！！!!");
					Log.i(TAG, "收到符合信息，手機資料即將全部抹處！！!!");
					System.err.println("i have the authority!!!!");
				}else {
					System.err.println("no active adimin");
				}
				abortBroadcast(); //don't show this wipe out password message . hide it....
			}else {
				Log.d(TAG, "not match"+"this message body is :"+body+"the md5 is "+MD5Tools.MD5(body)+"the store pass is "+sp.getString(context.getString(R.string.sp_password),"")+"the md5 is :"+MD5Tools.MD5(sp.getString(context.getString(R.string.sp_password),"")));
			}
		}
		
	}

}
