package com.ven.pos;

import java.util.HashMap;
import java.util.Map;

public class GlobalContant {

	public static final int CloseProgressDialog = 10;
	public static final int ShowProgressDialog = 20;
	public static final int ShowErrorDialog = 30;

	public static final String SharedPreferenceUseInfo = "UserInfo";
	public static final String SharedPreferencesUserAccount = "userAccount";
	public static final String SharedPreferencesPassword = "password";
	public static final String SharedPreferencesRememberPwd = "RememberPwd";
	public static final String SharedPreferencesAutoLogin = "AutoLogin";

	public static final String SECRET_KEY = "qbR0qPghaXzxiJkBKsRr47K64bzIeZEI";

	public static final int MACHINE_PHONE = 1; // 普通手机
	public static final int MACHINE_I9000S = 2; // I9000S 机型

	public static int MACHINE = MACHINE_I9000S; // 默认机器

	public static boolean bClose9000SMemberCard = false; // 关闭会员卡
	public static boolean bClose9000SScan = true; // 关闭扫描枪

	public String companyName = "";
	public String token = "";
	public String userName = "";
	public String mainUrl = "cnyssj.net";

	public class AliConfig {
		public boolean status = false;// 后台是否配置了数据
		// ↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
		// 合作身份者ID，以2088开头由16位纯数字组成的字符串
		// public String partner = "2088021074616801";
		// public String partner = "2088511924290201"; //力凯的信息
		public String partner = "";

		public String seller_id = partner;

		public String seller_email;

		public String notify_url = "http://niuka.656.so/vpos/alipay/notify_url.php";

		public String log_path = "";

		public String input_charset = "utf-8";

		public String sign_type = "RSA";

		public String ALIPAY_GATEWAY = "https://openapi.alipay.com/gateway.do?charset=utf-8";


	}

	public class WxConfig {
		public boolean status = true;// 后台是否配置了数据

		public String APP_ID;

		public String MCH_ID;

		public String API_KEY;

		public String NOTIFY_URL = "https://test/";

		public String WX_ORDER_QUIERY_URL = "https://api.mch.weixin.qq.com/pay/orderquery";

		public String WX_PAY_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

		public String WX_COLLECTION_PAY = "https://api.mch.weixin.qq.com/pay/micropay";
	}

	public class YzfConfig {
		public final String PAY_INTFARCE = "app/index.php?i=uniacid&c=entry&do=yunpay&m=niuka";
	}

	public class ErrorMsg {
		public Map<String, String> errors = new HashMap<String, String>();

		ErrorMsg() {
			errors.put("-1", "帐号名不存在");
			errors.put("0", "密码错误");
		}
	}

	public AliConfig aliConfig = new AliConfig();
	public WxConfig wxConfig = new WxConfig();
	public ErrorMsg errorMsg = new ErrorMsg();
	// public String serverUrl =
	// "http://niuka.656.so/vpos/class/function.php?do=login";

	private static GlobalContant inst = new GlobalContant();

	public static GlobalContant instance() {
		return inst;
	}
}
