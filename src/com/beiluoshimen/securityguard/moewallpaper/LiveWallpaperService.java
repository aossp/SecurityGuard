/**
 *
 *  You can modify and use this source freely
 *  only for the development of application related Live2D.
 *
 *  (c) Live2D Inc. All rights reserved.
 */
package com.beiluoshimen.securityguard.moewallpaper;

import com.beiluoshimen.securityguard.moe.CleanInterface;
import com.beiluoshimen.securityguard.moe.CleanService;
import com.beiluoshimen.securityguard.moe.MemInfo;
import com.beiluoshimen.securityguard.moe.MoeApplication;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import net.rbgrn.android.glwallpaperservice.*;

public class LiveWallpaperService extends GLWallpaperService {

	// this is used to implement double click
	private static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds

    long lastClickTime = 0;

	private static String TAG = "Live2dWallPaper";
	// service
	private static Intent serviceIntent;
	public CleanInterface iService;
	private static MyConnection connection;

	private class MyConnection implements ServiceConnection {
		// called when we successfully connect to service
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// upward type transfer
			Log.d(TAG, "connected.");
			iService = (CleanInterface) service;

		}

		// Called when a connection to the Service has been lost.
		// This typically happens when the process hosting the service has
		// crashed or been killed.
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG, "dis connected.");
		}

	}

	public LiveWallpaperService() {
		super();
	}

	public Engine onCreateEngine() {

		serviceIntent = new Intent(this, CleanService.class);
		connection = new MyConnection();
		bindService(serviceIntent, connection, BIND_AUTO_CREATE);

		MyEngine engine = new MyEngine();
		return engine;

	}

	class MyEngine extends GLEngine {
		Live2DRenderer renderer;

		public MyEngine() {
			super();
			// handle prefs, other initialization
			renderer = new Live2DRenderer(getApplicationContext());
			setRenderer(renderer);
			setRenderMode(RENDERMODE_CONTINUOUSLY);
		}

		@Override
		public void onTouchEvent(MotionEvent event) {
			
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:

				long clickTime = System.currentTimeMillis();
				if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
					MemInfo mem = iService.onCallClean();
					Toast.makeText(
							getApplicationContext(),
							"Kill"
									+ mem.getCount()
									+ "process(s),release"
									+ Formatter.formatFileSize(
											getApplicationContext(),
											mem.getSize()) + "memory",
							Toast.LENGTH_SHORT).show();
				} else {
					// single click
				}
				lastClickTime = clickTime;
				break;
			default:
				break;
			}

		}

		// @Override
		// public void onTouchEvent (MotionEvent event) {
		// switch (event.getAction()) {
		// case MotionEvent.ACTION_DOWN:
		//
		//
		//
		//
		// Log.d(TAG, "dis connected.");
		// MemInfo mem =iService.onCallClean();
		// Toast.makeText(getApplicationContext(),"Kill" + mem.getCount() +
		// "process(s),release" +
		// Formatter.formatFileSize(getApplicationContext(), mem.getSize()) +
		// "memory", Toast.LENGTH_SHORT).show();
		// //TODO BUG??????
		// System.out.println("call clean");
		// break;
		// case MotionEvent.ACTION_UP:
		// renderer.resetDrag();
		// break;
		// case MotionEvent.ACTION_MOVE:
		// renderer.drag(event.getX(), event.getY());
		// break;
		// case MotionEvent.ACTION_CANCEL:
		// break;
		// }
		//
		//
		//
		// }

		public void onDestroy() {
			super.onDestroy();
			if (renderer != null) {
				renderer.release();
			}
			renderer = null;
		}
	}
}
