package com.ven.pos.Payment;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.device.scanner.configuration.Triggering;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.cnyssj.db.DBManager;
import com.cnyssj.db.GoodsOrder;
import com.cnyssj.pos.R;
import com.jhj.Agreement.ZYB.CryptTool;
import com.jhj.Agreement.ZYB.SharedPreferences_util;
import com.jhj.print.Print_goods;
import com.ven.pos.GoodSearchActivity;
import com.ven.pos.TitleBar;
import com.ven.pos.BarcodeScan.BarcodeScan;
import com.ven.pos.Util.BaseActivity;
import com.ven.pos.Util.Snippet;
import com.ven.pos.Util.UtilTool;
import com.ven.pos.qscanbar.ShopCameraActivity;

public class PaymentCashierMainAct extends BaseActivity {

	public static PaymentCashierMainAct context;
	public static List<Map<String, Object>> list;
	ConsumeListView consumeListViewAdapter;
	public static double totalPoint;

	private Button btnAdd;
	private ImageButton CameraAdd;
	private TextView total_money_txt;
	private EditText edtBarcode;
	private TextView txtTotalMoney;
	private Button shop_search, item_delete_btn;
	private Button bill_pay;
	private ListView consume_list;
	private SoundPool soundpool = null;
	private int soundid = 0;
	BarcodeScan barcodeScan; // 扫码枪
	// 声音
	public static SoundPool soundPool;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	//
	SharedPreferences_util su = new SharedPreferences_util();
	boolean on_off, on_off_goods;
	String clientId;
	DBManager DBM;
	private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			barcodeScan.PlaySound();
			soundpool.play(soundid, 1, 1, 0, 0, 1);
			byte[] barcode = intent.getByteArrayExtra("barocode");
			int barocodelen = intent.getIntExtra("length", 0);
			// byte temp = intent.getByteExtra("barcodeType", (byte) 0);
			String barcodeStr = new String(barcode, 0, barocodelen);
			
			edtBarcode.setText(barcodeStr);
			ConsumeItemAdd(barcodeStr, 1);
		}
	};

	public static final int UpdateListMsg = 1;
	// 更新list列表
	public Handler updateList = new Handler() {
		@SuppressWarnings("rawtypes")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UpdateListMsg:
				consume_list.setAdapter(getAdapter());
				int nTotalMoney = 0;
				HashMap<String, HashMap<String, String>> listArray = ConsumeListView
						.instance().consumelistArray;
				Iterator iter = listArray.keySet().iterator();
				while (iter.hasNext()) {
					Object key = iter.next();
					HashMap<String, String> val = listArray.get(key);
					if (String.valueOf(key).length() == 18) {
						int monery = Integer
								.parseInt(new DecimalFormat("##0").format(Double.parseDouble(val
										.get(ConsumeListView.list_id_money)) * 100));
						nTotalMoney += monery;
					} else {
						int nAmount = Integer.parseInt(val
								.get(ConsumeListView.list_id_amount));

						ShopItem shopItem = ShopItemMgr.instance().GetItem(
								String.valueOf(key));
						nTotalMoney += (shopItem.price_100) * nAmount;
					}
				}
				DecimalFormat fnum = new DecimalFormat("##0.00");
				total_money_txt.setText(fnum.format(nTotalMoney / 100.0f));

				break;

			}
			super.handleMessage(msg);
		}
	};

	@SuppressWarnings("unused")
	private void AssignGoodsCommission(int paramInt) {

	}

	/* Error */
	@SuppressWarnings("unused")
	private void AsyncConsumeAllItem(Object paramObject,
			Object[] paramArrayOfObject) {
	}

	@SuppressWarnings("unused")
	private void AsyncConsumeAllPackage(final Object paramObject,
			final Object[] paramArrayOfObject) {
	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		TitleBar.setTitleBar(this, "返回", "消费收银", "", null);
		setContentView(R.layout.memberconsume_main);
		// setTitle("消费收银");
		initView();
		setListeners();

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;

		UtilTool.checkLoginStatusAndReturnLoginActivity(this, null);
		showmessage();

	}

	// 发送handle
	public void showmessage() {
		Message message = new Message();
		message.what = UpdateListMsg;
		PaymentCashierMainAct.this.updateList.sendMessage(message);
	}

	public void onStart() {
		if (null != txtTotalMoney)
			txtTotalMoney.setText("0.00");
		TitleBar.setActivity(this);
		super.onStart();
	}

	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		barcodeScan.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		barcodeScan.onResume();
	}

	private SimpleAdapter getAdapter() {
		SimpleAdapter adapter = new SimpleAdapter(this, ConsumeListView
				.instance().getAllShop(), R.layout.consume_list_view,
				new String[] { ConsumeListView.list_id_name,
						ConsumeListView.list_id_amount,
						ConsumeListView.list_id_money }, new int[] {
						R.id.good_name, R.id.good_amount, R.id.money });
		return adapter;

	}

	@SuppressWarnings("static-access")
	private void initView() {

		context = this;
		on_off = su.getPrefboolean(PaymentCashierMainAct.this, "on_off", false);
		on_off_goods = su.getPrefboolean(PaymentCashierMainAct.this,
				"on_off_goods", false);
		clientId = su.getPrefString(PaymentCashierMainAct.this, "clientId",
				null);
		this.edtBarcode = (EditText) findViewById(R.id.MemberConsume_Barcode_edt);
		edtBarcode.setInputType(InputType.TYPE_NULL);
		edtBarcode.requestFocus();
		// 商品编码输入框
		edtBarcode.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					shopnum();
					return true;
				}
				return false;
			}
		});
		edtBarcode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});
		edtBarcode.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {

				}
				return true;
			}
		});
		this.btnAdd = (Button) findViewById(R.id.MemberConsume_AddBarcode_add_btn);
		this.CameraAdd = (ImageButton) findViewById(R.id.Camera_btn);

		this.total_money_txt = (TextView) findViewById(R.id.MemberConsume_TotalMoney_txt);
		this.shop_search = (Button) findViewById(R.id.item_search_btn);
		this.item_delete_btn = (Button) findViewById(R.id.item_delete_btn);

		this.bill_pay = (Button) findViewById(R.id.bill_pay_btn);
		this.consume_list = (ListView) findViewById(R.id.consume_list);

		// 实现列表的显示
		this.consume_list.setAdapter(getAdapter());
		barcodeScan = new BarcodeScan(this, mScanReceiver);
		barcodeScan.setScanType(Triggering.CONTINUOUS);
		// this.inputEdit= new EditText(this);
	}
	 @Override  
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
	       
	        // 根据上面发送过去的请求吗来区别  
	        switch (requestCode) {  
	        case 0:  
	          
	            break;  
	          
	        default:  
	            break;  
	        }  
	    }  
	@SuppressLint("DefaultLocale")
	private void setListeners() {

		this.btnAdd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				ConsumeItemAdd();
				// barcodeScan.PlaySound();
			}
		});

		this.CameraAdd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				ScanCode();
			}
		});
		// 商品搜索
		this.shop_search.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {

				Intent intent = new Intent(context, GoodSearchActivity.class);
				startActivityForResult(intent, 0);
			}
		});
		
		// 删除商品
		this.item_delete_btn.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("unchecked")
			public void onClick(View paramAnonymousView) {

				HashMap<String, Object> map = (HashMap<String, Object>) consume_list
						.getItemAtPosition(consume_list.getCount() - 1);
				if (null != map) {
					String name = (String) map.get("name");
					ShopItem item1 = ShopItemMgr.instance().GetItemByName(name);
					if (item1 != null) {

						if (String.valueOf(item1.itemProductsn).length() != 7) {
							ConsumeListView.instance().removeByItemNo(
									item1.itemProductsn);
							showmessage();

						} else {
							ConsumeListView.instance().removeByItemNo(
									item1.itemProductsn);

							showmessage();
							String monery = new DecimalFormat("#0")
									.format(Double.valueOf(map.get("money")
											.toString()) * 100);
							String amount = new DecimalFormat("#0")
									.format(Double.valueOf(map.get("amount")
											.toString()) * 1000);
							String num_shop = item1.itemProductsn
									+ String.format("%05d",
											Integer.parseInt(monery))
									+ String.format("%05d",
											Integer.parseInt(amount));
							ConsumeListView.instance().removeByItemNo(
									num_shop + checkdigit(num_shop));

							showmessage();
						}

					}
				}
			}
		});
		/**
		 * 商品订单相关
		 * */
		this.bill_pay.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("unchecked")
			public void onClick(View paramAnonymousView) {
				int cunsume = consume_list.getCount();
				try {
					String strTotalMoney = total_money_txt.getText().toString();
					if (strTotalMoney.equals("")) {
						return;
					}
					Double dTotalMoney = Double.valueOf(strTotalMoney);
					if (dTotalMoney <= 0.0d) {
						return;
					}
					Intent localIntent = new Intent(context,
							PaymentMainActivity.class);
					JSONArray shopsJson = new JSONArray();

					for (int iPos = 0; iPos < consume_list.getCount(); iPos++) {
						HashMap<String, Object> map = (HashMap<String, Object>) consume_list
								.getItemAtPosition(iPos);
						if (null == map) {
							continue;
						}
						// 订单信息封装
						JSONObject shopJson = new JSONObject();
						// 商品名称
						shopJson.put(ConsumeListView.list_id_name,
								map.get(ConsumeListView.list_id_name));
						// 商品数量
						shopJson.put(ConsumeListView.list_id_amount,
								map.get(ConsumeListView.list_id_amount));
						// 商品金额
						shopJson.put(ConsumeListView.list_id_money,
								map.get(ConsumeListView.list_id_money));
						String name = (String) map
								.get(ConsumeListView.list_id_name);
						ShopItem item1 = ShopItemMgr.instance().GetItemByName(
								name);
						// 商品单价
						shopJson.put("price", item1.price.toString());
						//
						shopJson.put("id", item1.itemNo);
						shopsJson.put(shopJson);

					}
					localIntent.putExtra("TotalMoney", total_money_txt
							.getText().toString());
					// 总商品信息
					localIntent.putExtra(
							PaymentResultActivity.IntentPayDetails,
							shopsJson.toString());
					try {
						String data_print = "";
						for (int i = 0; i < shopsJson.length(); i++) {
							String deta = shopsJson.getString(i);
							JSONObject sJson = new JSONObject(deta);
							String data_name, data_amount, data_money, data_name_more1, data_name_more2;
							data_name = sJson.getString("name");
							if (Snippet.getLength(data_name) > 20) {
								data_name_more1 = data_name.substring(0, 10);
								while (Snippet.getLength(data_name_more1) < 20) {
									data_name_more1 = data_name_more1 + " ";
								}
								data_name_more2 = data_name.substring(10,
										data_name.length());
							} else {
								data_name_more1 = data_name;
								int ii = 20 - Snippet
										.getLength(data_name_more1);
								for (int i1 = 0; i1 < ii; i1++) {
									data_name_more1 = data_name_more1 + " ";
								}
								data_name_more2 = "";
							}
							data_amount = String.format("%-3s",
									sJson.getString("amount"));
							data_money = String.format("%-8s",
									sJson.getString("money"));
							if (data_name_more2 == "") {
								data_print = data_print + data_name_more1 + " "
										+ data_amount + " " + data_money + "\n";
							} else {
								data_print = data_print + data_name_more1 + " "
										+ data_amount + " " + data_money + "\n"
										+ data_name_more2 + "\n";
							}

						}
						if (Build.MANUFACTURER.contains("BASEWIN")
								|| Build.MANUFACTURER.contains("basewin")) {
							if (on_off) {
								if (on_off_goods) {
									Print_goods pg = new Print_goods();
									pg.prilay(PaymentCashierMainAct.this,
											data_print);
								} else {
									// toast("打印商品详单功能未开启！");
								}

							} else {
								toast("打印机未开启!");
							}
						} else {
							toast("未检测到打印机！无法进行打印打印");
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
					// 操作类型 1:消费 2:充值
					localIntent.putExtra(PaymentResultActivity.IntentOperater,
							1);
					// 标记
					localIntent.putExtra("mark_activity", false);
					String outOrderId = CryptTool.getCurrentDate()
							+ clientId.substring(clientId.length() - 4,
									clientId.length());
					localIntent.putExtra("outOrderId", outOrderId);
					SimpleDateFormat sDateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");

					// 添加数据库
					save_shop_items(outOrderId, "", total_money_txt.getText()
							.toString(), 0, shopsJson.toString(), 1,
							sDateFormat.format(new java.util.Date()).toString());
					updata_order_goods(outOrderId, 1);
					startActivity(localIntent);
					// 遍历json

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		// 条目点击事件
		this.consume_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});
		// 添加长按点击
		this.consume_list
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						menu.setHeaderTitle("操作");
						menu.add(0, 0, 0, "删除");
						menu.add(0, 1, 0, "修改数量");
					}
				});
	}

	// 长按菜单响应函数
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		try {
			// setTitle("点击了长按菜单里面的第"+item.getItemId()+"个项目");
			int nPos = ((AdapterContextMenuInfo) item.getMenuInfo()).position;
			if (nPos < 0 || nPos >= consume_list.getCount()) {
				return false;
			}
			@SuppressWarnings("unchecked")
			HashMap<String, Object> map = (HashMap<String, Object>) consume_list
					.getItemAtPosition(nPos);
			if (null == map) {
				return false;
			}
			switch (item.getItemId()) {
			// 长安删除商品
			case 0: {
				String name = (String) map.get("name");

				ShopItem item1 = ShopItemMgr.instance().GetItemByName(name);

				if (item1 != null) {
					ConsumeListView.instance().removeByItemNo(
							item1.itemProductsn);

					showmessage();
				}
			}
				break;
			case 1: {

				String name = (String) map.get("name");
				final ShopItem item1 = ShopItemMgr.instance().GetItemByName(
						name);
				LayoutInflater inflater = LayoutInflater.from(this);
				final View textEntryView = inflater.inflate(R.layout.msg_box,
						null);
				final EditText edtInput = (EditText) textEntryView
						.findViewById(R.id.edtInput);

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("请输入数量");
				builder.setIcon(android.R.drawable.ic_dialog_info);

				edtInput.setText("");
				//showmessage();
				builder.setView(textEntryView);
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								int amount = 0;
								try {
									amount = Integer.parseInt(edtInput
											.getText().toString());
								} catch (NumberFormatException e) {
									e.printStackTrace();
								}
								if (amount != 0) {
									if (item1 != null) {
										ConsumeListView.instance()
												.ModifyAmount(
														item1.itemProductsn,
														amount);

										showmessage();
									} else {
										Toast.makeText(
												PaymentCashierMainAct.this,
												"请输入正确的数量!", Toast.LENGTH_SHORT)
												.show();
									}
								} else {
									Toast.makeText(PaymentCashierMainAct.this,
											"请输入正确的数量!", Toast.LENGTH_SHORT)
											.show();
								}
							}
						});
				builder.setNegativeButton("取消", null);
				builder.show();
			}
				break;

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return super.onContextItemSelected(item);
	}

	// 添加商品
	public void shopnum() {
		DBM = new DBManager(getApplicationContext());
		int amount = 1;
		DecimalFormat fnum = new DecimalFormat("##0.00");
		String itemNo = PaymentCashierMainAct.context.edtBarcode.getText()
				.toString().trim();
		if (itemNo.isEmpty()) {
			return;
		}
		// 获取商品信息
		if (String.valueOf(itemNo).length() != 18) {

			ShopItem item = ShopItemMgr.instance().GetItem(itemNo);
			PaymentCashierMainAct.context.edtBarcode.setText(null);
			if (item != null) {
				int nTotalMoney = item.price_100 * amount;
				ConsumeListView.instance().insert(itemNo,
						Integer.toString(amount),
						fnum.format(nTotalMoney / 100.0f));

				showmessage();
			} else {
				shopnum_null();
			}

		} else {
			PaymentCashierMainAct.context.edtBarcode.setText(null);
			// 商品码
			String itemNoJ = itemNo.subSequence(0, 7).toString();
			// 商品价格
			int monery = Integer.parseInt(itemNo.subSequence(7, 12).toString());
			ShopItem item_dn = ShopItemMgr.instance().GetItem(itemNoJ);
			if (item_dn != null) {
				// 商品重量
				String weight = Double.toString(Double.parseDouble(itemNo
						.subSequence(12, 17).toString()) / 1000);
				// (.substring(0, itemNo.length() - 1)).toString()
				ConsumeListView.instance().insert(itemNo, weight,
						fnum.format(monery / 100.0f));

				showmessage();
			} else {
				shopnum_null();
			}
		}
	}

	// 未识别到商品
	private void shopnum_null() {
		toast("该商品没有找到");
		playBeepSoundAndVibrate();
		Dialog alertDialog = new AlertDialog.Builder(this)
				.setTitle("系统提示")
				.setMessage("未找到对应的商品，请核实后重新扫描！")
				.setOnKeyListener(new DialogInterface.OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode,
							KeyEvent event) {
						if (keyCode == KeyEvent.KEYCODE_ENTER) {
							playBeepSoundAndVibrate();
							return true;
						}
						return false;
					}
				})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						System.out.println(1);
					}
				}).create();
		alertDialog.show();
	}

	private void ScanCode() {
		// Intent localIntent =new Intent( context, BarCodeActivity.class);
		// startActivity( localIntent);
		Intent openCameraIntent = new Intent(PaymentCashierMainAct.this,
				ShopCameraActivity.class);

		startActivity(openCameraIntent);
	}

	// 添加商品
	private void ConsumeItemAdd() {
		try {
			String itemNo = this.edtBarcode.getText().toString().trim();

			if (String.valueOf(itemNo).length() == 18) {
				PaymentCashierMainAct.context.edtBarcode.setText(null);
				// 商品码
				String itemNoJ = itemNo.subSequence(0, 7).toString();
				// 商品价格
				int monery = Integer.parseInt(itemNo.subSequence(7, 12)
						.toString());
				ShopItem item_dn = ShopItemMgr.instance().GetItem(itemNoJ);
				if (item_dn != null) {
					// 商品重量
					String weight = Double.toString(Double.parseDouble(itemNo
							.subSequence(12, 17).toString()) / 1000);
					ConsumeListView.instance()
							.insert(itemNo,
									weight,
									new DecimalFormat("##0.00")
											.format(monery / 100.0f));

					showmessage();
				} else {
					shopnum_null();
				}
			} else {
				// ShopItem item =getgoodsItems(itemNo);
				if (ShopItemMgr.instance().FindItem(itemNo)) {
					// if (item!=null) {
					LayoutInflater inflater = LayoutInflater.from(this);
					final View textEntryView = inflater.inflate(
							R.layout.msg_box, null);
					final EditText edtInput = (EditText) textEntryView
							.findViewById(R.id.edtInput);
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("请输入数量");
					builder.setIcon(android.R.drawable.ic_dialog_info);
					builder.setView(textEntryView);
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									DecimalFormat fnum = new DecimalFormat(
											"##0.00");
									String itemNo = PaymentCashierMainAct.context.edtBarcode
											.getText().toString().trim();
									//
									ShopItem item = ShopItemMgr.instance()
											.GetItem(itemNo);

									int amount = Integer.parseInt(edtInput
											.getText().toString());
									if (item != null) {
										int nTotalMoney = item.price_100
												* amount;
										ConsumeListView
												.instance()
												.insert(itemNo,
														Integer.toString(amount),
														fnum.format(nTotalMoney / 100.0f));

										showmessage();
									} else {
										shopnum_null();
									}
								}
							});
					builder.setNegativeButton("取消", null);
					builder.show();

				} else {
					shopnum_null();
					toast("该商品没有找到");
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void ConsumeItemAdd(String strItemNo, int amount) {
		try {
			// String itemNo = this.edtBarcode.getText().toString().trim();
			if (ShopItemMgr.instance().FindItem(strItemNo)) {

				ShopItem item = ShopItemMgr.instance().GetItem(strItemNo);

				// int amount = 1;
				if (item != null && amount > 0) {
					int nTotalMoney = item.price_100 * amount;
					DecimalFormat fnum = new DecimalFormat("##0.00");
					ConsumeListView.instance().insert(strItemNo,
							Integer.toString(amount),
							fnum.format(nTotalMoney / 100.0f));
					showmessage();
				}

			} else {
				toast("该商品没有找到");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	public void toast(String data) {
		Toast.makeText(PaymentCashierMainAct.this, data, Toast.LENGTH_LONG)
				.show();
	}

	private String checkdigit(String str) {
		int sum = 0;
		for (int i = 0; i < str.length() - 1; i++) {
			if (i % 2 == 0) {
				sum += Integer.parseInt(str.substring(i, i + 1)) * 3;
			} else {
				sum += Integer.parseInt(str.substring(i, i + 1));
			}
		}
		return Integer.toString((10 - (sum % 10)) % 10);
	}

	@SuppressWarnings("static-access")
	private void save_shop_items(String myorderid, String order,
			String str_monery, int PaymentMethod, String PayDetails,
			int paymentstatus, String time) {
		DBM = new DBManager(getApplicationContext());
		GoodsOrder goodsorder = new GoodsOrder(getApplicationContext());
		DBM.addOrder(goodsorder.addOrder(myorderid, order, str_monery,
				PaymentMethod, PayDetails, paymentstatus, time, "", 0));
	}

	// 更新商品订单状态
	private void updata_order_goods(String outOrderId, int paytype) {
		DBM = new DBManager(getApplicationContext());
		if (DBM.updateOrderstatus(outOrderId, paytype)) {
			// showToast("更新状态成功");
		} else {
			toast("更新状态失败");
		}
	}

}
