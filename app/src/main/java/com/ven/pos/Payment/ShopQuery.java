package com.ven.pos.Payment;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cnyssj.pos.R;
import com.ven.pos.GlobalContant;
import com.ven.pos.Util.HttpConnection;

public class ShopQuery {

	private static final String TAG = "WxPayQuery";
	public static ShopQuery inst;

	public static ShopQuery instance() {
		if (null == inst) {
			inst = new ShopQuery();
		}
		return inst;
	}

	private Context parentContext;
	private QueryShopThread queryShopThread;

	public void init(Context c) {
		parentContext = c;
	}

	class QueryShopThread extends Thread {
		private List<Integer> allPage;
		private int nTotalPage = 10;
		private int size = 50;

		public QueryShopThread() {
			allPage = new ArrayList<Integer>();
		}

		public Bitmap returnBitMap(String url) {
			URL myFileUrl = null;
			Bitmap bitmap = null;
			try {
				myFileUrl = new URL(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			try {
				HttpURLConnection conn = (HttpURLConnection) myFileUrl
						.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 16;

				bitmap = BitmapFactory.decodeStream(is, null, options);
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return bitmap;
		}

		public boolean QueryShop(int nPage) {
			String serverUrl = GlobalContant.instance().mainUrl
					+ parentContext.getResources().getString(
							R.string.shop_query_url);

			Map<String, String> sParaTemp = new HashMap<String, String>();
			sParaTemp.put("page", String.valueOf(nPage));
			sParaTemp.put("token", GlobalContant.instance().token);
			sParaTemp.put("size", String.valueOf(size));
			String str1;
			try {
				str1 = HttpConnection.buildRequest(serverUrl, sParaTemp);
				if (str1 == null) {
					return false;
				}

				JSONTokener jsonParser = new JSONTokener(str1);
				JSONObject json = (JSONObject) jsonParser.nextValue();

				if (json.getString("status").equals("1")) {

					nTotalPage = Integer.valueOf(json.getString("total"));
					int nCurrentPage = Integer
							.valueOf(json.getString("pindex"));

					JSONArray goodJson = json.getJSONArray("goods");
					if (null != goodJson) {
						int nGoodSize = goodJson.length();
						for (int i = 0; i < nGoodSize; i++) {
							JSONObject oj = goodJson.getJSONObject(i);
							ShopItem shopItem = new ShopItem();
							shopItem.itemNo = oj.getString("id");
							shopItem.itemProductsn = oj.getString("productsn");
							shopItem.itemName = oj.getString("title");
							shopItem.price_100 = oj.getInt("price");
							shopItem.price = shopItem.price_100 / 100.0d;
							ShopItemMgr.instance().InsertItem(shopItem);
						}
					}
					allPage.add(nCurrentPage);
					return true;
				}
			} catch (Exception localException1) {
				localException1.printStackTrace();
				return false;
			}
			return false;
		}

		public void run() {
			// 查看是不是所有页都有加载
			while (true) {
				boolean bAllLoad = true;
				for (int i = 1; i <= nTotalPage; i++) {
					if (!allPage.contains(i)) {
						bAllLoad = false;
						if (!QueryShop(i))
							return;
						break;
					}
				}
				if (bAllLoad) {
					return;
				}
			}
		}
	}

	public void QueryShop() {
		queryShopThread = new QueryShopThread();
		queryShopThread.start();
	}
}
