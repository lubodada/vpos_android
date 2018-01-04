package com.ven.pos.control;

import android.content.ServiceConnection;

public interface IAIDLListener {
    
    public static final int STATE_UNKNOW = -1;
//	onServiceConnected
	public  void  serviceConnected(Object objService, ServiceConnection connection);

}
