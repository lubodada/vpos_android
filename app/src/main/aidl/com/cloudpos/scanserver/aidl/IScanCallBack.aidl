package com.cloudpos.scanserver.aidl;
import com.cloudpos.scanserver.aidl.ScanResult;
interface IScanCallBack{
	void foundBarcode(in com.cloudpos.scanserver.aidl.ScanResult result);
}