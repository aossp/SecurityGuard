package com.beiluoshimen.securityguard.moe;


import java.util.ArrayList;
import java.util.List;

import com.beiluoshimen.securityguard.taskmanager.ProcessInfo;
import com.beiluoshimen.securityguard.taskmanager.ProcessInfoProvider;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * An service used to kill All app that is currently running. 
 * Those Activities want to use this class as tool to kill app, need to bind this Service.
 * and this service will return MyBinder, which has our Callback to use.
 * 
 * @author Hsieh Yu-Hua
 */

public class CleanService extends Service{
	private MyBinder binder;
	private List<ProcessInfo> processInfos; // all infos
	private List<ProcessInfo> userInfos;// which need to be clean
	private ProcessInfoProvider provider;
	private int count;
	private long memsize;
	private MemInfo memInfo;
	
	
	private static final String TAG = "CleanService";
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "CleanService on create.");
		
		provider = new ProcessInfoProvider(getApplicationContext());
		userInfos = new ArrayList<ProcessInfo>();
		count = 0;
		memsize = 0;
	}
	
	class MyBinder extends Binder implements CleanInterface{

		@Override
		public MemInfo onCallClean() {
			Log.d(TAG, "Cleanservice onCallClean");
			//get all the users info
			processInfos = provider.getProcessInfos();
			for (ProcessInfo info : processInfos) {
				if (info.isUserProcess()) {
					userInfos.add(info);
				}
			}
			//Interact with the overall activities running in the system.
			// kill all the user process...
			ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			count = 0;	
			memsize = 0;
			for(ProcessInfo info: userInfos){
				count++;
				memsize+=info.getMemory();
				am.killBackgroundProcesses(info.getPackName());
			}
			userInfos.clear();
			processInfos = null;
			
			memInfo = new MemInfo();
			memInfo.setCount(count);
			memInfo.setSize(memsize);
			return memInfo;
			
		}
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "Cleanservice onBind");
		binder = new MyBinder();
		return binder;
	}

}
