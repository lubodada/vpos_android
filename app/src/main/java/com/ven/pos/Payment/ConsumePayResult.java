package com.ven.pos.Payment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;

public class ConsumePayResult {
	

	
	public static void CosumePayRet(Activity ac, String ret, String sPostData /*发送给服务器的数据*/){
		
		try{
		Map<String,String> postDataArray = new HashMap<String,String>(); 
	    String[] paramArray = sPostData.split("&");
	    for(int i =0; i<paramArray.length; i++){
	    	if(paramArray[i].length()<=0){
	    		continue;
	    	}
	    	
	    	String[] subParam = paramArray[i].split("=");
	    	if(subParam.length>=2&&null!=subParam[0]&& null!= subParam[1]){
	    		postDataArray.put( subParam[0],  subParam[1]);
	    	}
	    }
	    
		String[] paramPP = ret.split("\\?");		
	    String[] retArray = paramPP[1].split("&");
	    for(int i =0; i<retArray.length; i++){
	    	String[] subParam = retArray[i].split("=");
	    	if(subParam.length>=2&&null!=subParam[0]&& null!= subParam[1]){
	    		postDataArray.put( subParam[0],  subParam[1]);
	    	}
	    }
		
//		SimpleDateFormat sdf = new SimpleDateFormat(
//				"yyyyMMDDHHMMSS");
//		String timeEnd = sdf.format(new java.util.Date());
		   
	    String test = postDataArray.get("items");
	    
		Intent localIntent = new Intent(ac,PaymentResultActivity.class);
		localIntent.putExtra(PaymentResultActivity.IntentPayResult, true);
		localIntent.putExtra(PaymentResultActivity.IntentPayErrMsg, "");
		localIntent.putExtra(PaymentResultActivity.IntentPayMoney, postDataArray.get("money"));
		localIntent.putExtra(PaymentResultActivity.IntentPayTradeNo, postDataArray.get("orderid"));
		localIntent.putExtra(PaymentResultActivity.IntentPayType, PaymentMainActivity.CONSUME_PAYWAY);
		localIntent.putExtra(PaymentResultActivity.IntentPayDetails, postDataArray.get("items"));
		localIntent.putExtra(PaymentResultActivity.IntentDiscount, postDataArray.get("dis"));
		localIntent.putExtra(PaymentResultActivity.IntentSncodeid, postDataArray.get("sncodeid"));
		localIntent.putExtra(PaymentResultActivity.IntentUid, postDataArray.get("uid"));
		localIntent.putExtra(PaymentResultActivity.IntentBeizhu, postDataArray.get("beizhu"));
//		localIntent.putExtra(PaymentResultActivity.IntentPayDetails, 1);
		
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy年MM月dd日   HH:mm:ss:SSS    ");
		long curS = System.currentTimeMillis();
		Date curDate = new Date(curS);
		String timeEnd = formatter.format(curDate);
		
		localIntent.putExtra(PaymentResultActivity.IntentPayTime, timeEnd);
		
		ac.finish();
		ac.startActivity(localIntent);
		
	}catch(Exception ex){
		ex.printStackTrace();
	}
	}
}
