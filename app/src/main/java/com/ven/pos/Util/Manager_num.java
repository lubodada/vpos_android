package com.ven.pos.Util;

import android.os.RemoteException;

import com.basewin.services.ServiceManager;

public class Manager_num {
	public String manager() {
		try {
			return ServiceManager.getInstence().getDeviceinfo().getSN();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
}
