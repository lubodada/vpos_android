package com.cloudpos.scanserver.aidl;
import com.cloudpos.scanserver.aidl.ScanParameter;
import com.cloudpos.scanserver.aidl.ScanResult;
import com.cloudpos.scanserver.aidl.IScanCallBack;
interface IScanService{
   com.cloudpos.scanserver.aidl.ScanResult scanBarcode(in ScanParameter parameter);
   void startScan(in ScanParameter parameter, in IScanCallBack callBack);
   boolean stopScan();
}