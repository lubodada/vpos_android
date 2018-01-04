package com.ven.pos.control;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.cloudpos.scanserver.aidl.IScanService;

public class AidlController {
	
	public static final String TAG = "AidlController";
	private IAIDLListener aidlListener ;
	private ServiceConnection connection;
	
	public static final String DESC_SCAN_SERVICE ="com.cloudpos.scanserver.aidl.IScanService";
	
	private AidlController() {
	}
	
	private static AidlController instance;
	
	public static AidlController getInstance(){
		if(instance == null){
			instance = new AidlController();
		}
		return instance;
	}
	
	protected class ServiceConnectionImpl implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			try {
				String temp = service.getInterfaceDescriptor();
				Log.d(TAG, "onServiceConnected : " + temp);
				Object objService = null;
				if(temp.equals(DESC_SCAN_SERVICE)){
					objService = IScanService.Stub.asInterface(service);
				}else{
					//TODO 英文
					Log.e(TAG, "不能识别对应的AIDL接口");
				}
				if(objService != null){
					aidlListener.serviceConnected(objService, connection);
				}
				
			} catch (RemoteException e) {
				e.printStackTrace();
			}
//			IAgentService.Stub.asInterface(service);
//			IModifyAdminPwdService.Stub.asInterface(service);
//			INetworkManageService.Stub.asInterface(service);
//			IPosSystemService.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			connection = null;
		}

	}
	
	protected void setListener(IAIDLListener aidlListener ){
		this.aidlListener = aidlListener;
	}
	
	protected boolean startConnectService(Context host, String packageName , String className, IAIDLListener aidlListener) {
		boolean isSuccess =startConnectService(host, new ComponentName(packageName,className),aidlListener);
		return isSuccess;
	}
	protected boolean startConnectService(Context host, ComponentName comp, IAIDLListener aidlListener) {
		setListener(aidlListener);
		Intent intent = new Intent();
		intent.setComponent(comp);
		connection = new ServiceConnectionImpl();
		boolean isSuccess = host.bindService(intent, connection, Context.BIND_AUTO_CREATE);
		host.startService(intent);
		return isSuccess;
	}
	
	public boolean startScanService(Context host, IAIDLListener aidlListener) {
		ComponentName comp = new ComponentName(
				"com.cloudpos.scanserver",
				"com.cloudpos.scanserver.service.ScannerService");
		boolean isSuccess = startConnectService(host,comp,aidlListener);
		//TODO 英文
		Log.d("DEBUG", "bind scanner service");
		return isSuccess;
	}
	
}
