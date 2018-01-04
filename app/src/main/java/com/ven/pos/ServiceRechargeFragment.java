package com.ven.pos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cnyssj.pos.R;
import com.ven.pos.Payment.PaymentMainActivity;
import com.ven.pos.Payment.PaymentResultActivity;
import com.ven.pos.Util.HttpConnection;

public class ServiceRechargeFragment extends Fragment implements
        OnClickListener {

    private Spinner service_list_spinner = null;
    private Button service_amount_minus_btn = null;
    private Button service_amount_plus_btn = null;
    private EditText service_amount_edit = null;
    private TextView service_total_money_txt = null;
    private Button service_confirm_btn = null;
    private Context context;
    private List<ServersActive> serversActives;
    private static final int REFRESH_SERVICE_LIST = 100;
    private int nSelectPos = 0;

    // {\"id\":\"1\",\"weid\":\"2\",\"displayorder\":\"0\",\"name\":\"\\u4e0a\\u95e8\\u4fdd\\u6d01\\u670d\\u52a1\",\"price\":\"200.00\",\"units\":\"\\u6b21\",\"status\":\"1\",\"comment\":\"\\u4f7f\\u7528\\u524d\\u8bf7\\u5148\\u9884\\u7ea6\\uff01\",\"end\":\"\",\"timetype\":\"0\"}
    private class ServersActive {
        public int id;
        public String name;
        public float price;
    }

    /**
     * 用Handler来更新UI
     */
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case REFRESH_SERVICE_LIST: {
                    // 第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。

                    List<String> list = new ArrayList<String>();

                    for (int iPos = 0; iPos < serversActives.size(); iPos++) {

                        list.add(serversActives.get(iPos).name);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            context, android.R.layout.simple_spinner_item, list);
                    // 第三步：为适配器设置下拉列表下拉时的菜单样式。
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // 第四步：将适配器添加到下拉列表上
                    service_list_spinner.setAdapter(adapter);
                    // 第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
                    service_list_spinner
                            .setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                                public void onItemSelected(AdapterView<?> arg0,
                                                           View arg1, int arg2, long arg3) {
                                    // TODO Auto-generated method stub
                                /* 将所选mySpinner 的值带入myTextView 中 */
                                    // myTextView.setText("您选择的是："+
                                    // adapter.getItem(arg2));
                                    ForegroundColorSpan clsp = new ForegroundColorSpan(
                                            Color.parseColor("#fda737"));
                                    SpannableString spanString = new SpannableString(
                                            String.valueOf(serversActives.get(arg2).price));
                                    spanString.setSpan(clsp, 0,
                                            spanString.length(),
                                            spanString.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    service_total_money_txt.setText(spanString);
                                    nSelectPos = arg2;
                                /* 将mySpinner 显示 */
                                    arg0.setVisibility(View.VISIBLE);
                                }

                                public void onNothingSelected(AdapterView<?> arg0) {
                                    // TODO Auto-generated method stub
                                    // myTextView.setText("NONE");
                                    arg0.setVisibility(View.VISIBLE);
                                }
                            });
                    break;
                }
            }
        }
    };

    public ServiceRechargeFragment(Context context) {
        super();

        this.context = context;
        this.serversActives = new ArrayList<ServersActive>();
    }

    /**
     * 覆盖此函数，先通过inflater inflate函数得到view最后返回
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.member_charge_service, container,
                false);

        initView(v);

        return v;

    }

    private void initView(View v) {
        service_list_spinner = (Spinner) v
                .findViewById(R.id.service_list_spinner);
        service_amount_minus_btn = (Button) v
                .findViewById(R.id.service_amount_minus);
        service_amount_plus_btn = (Button) v
                .findViewById(R.id.service_amount_plus);
        service_amount_edit = (EditText) v
                .findViewById(R.id.service_amount_edit);
        service_total_money_txt = (TextView) v
                .findViewById(R.id.service_total_money);
        service_confirm_btn = (Button) v.findViewById(R.id.service_confirm);

        service_amount_edit.setText("1");

        service_amount_minus_btn.setOnClickListener(this);
        service_amount_plus_btn.setOnClickListener(this);
        service_confirm_btn.setOnClickListener(this);

        (new QueryActiveThread()).start();
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        if (arg0.getId() == service_list_spinner.getId()) {

        } else if (arg0.getId() == service_amount_minus_btn.getId()) {
            onChangeAmount(-1);
        } else if (arg0.getId() == service_amount_plus_btn.getId()) {
            onChangeAmount(1);
        } else if (arg0.getId() == service_confirm_btn.getId()) {
            onBuyService();
        }
    }

    private void onChangeAmount(int value) {
        String strServiceAmount = service_amount_edit.getText().toString();
        int nServiceAmount = Integer.valueOf(strServiceAmount);

        nServiceAmount += value;
        if (nServiceAmount < 0) {
            return;
        }
        if (nServiceAmount > 99) {
            return;
        }

        service_amount_edit.setText(String.valueOf(nServiceAmount));

        if (serversActives.size() <= nSelectPos) {
            return;
        }

        ForegroundColorSpan clsp = new ForegroundColorSpan(
                Color.parseColor("#fda737"));
        SpannableString spanString = new SpannableString("总价"
                + String.valueOf(serversActives.get(nSelectPos).price
                * nServiceAmount) + "元");
        spanString.setSpan(clsp, 2, spanString.length() - 1,
                spanString.SPAN_EXCLUSIVE_EXCLUSIVE);
        service_total_money_txt.setText(spanString);
    }

    private void onBuyService() {

        // JSONArray shopsJson = new JSONArray();
        // JSONObject shopJson = new JSONObject();
        try {
            // shopJson.put(ConsumeListView.list_id_name, "服务充值");
            //
            // shopJson.put(ConsumeListView.list_id_amount, "1");
            // shopJson.put(ConsumeListView.list_id_money, str1);
            // shopJson.put("price", str1);
            // shopJson.put("id", "0");
            //
            // shopsJson.put(shopJson);
            Activity ac = ((Activity) context);
            String uid = ac.getIntent().getExtras().getString("uid");

            if (serversActives.size() <= nSelectPos) {
                return;
            }

            String strServiceAmount = service_amount_edit.getText().toString();
            int nServiceAmount = Integer.valueOf(strServiceAmount);

            if (nServiceAmount <= 0) {
                return;
            }

            JSONObject json = new JSONObject();
            json.put("uid", uid);
            json.put("sid", serversActives.get(nSelectPos).id);
            json.put("num", service_amount_edit.getText().toString());

            Intent localIntent = new Intent(context, PaymentMainActivity.class);

            String str1 = this.service_total_money_txt.getText().toString();

            localIntent.putExtra(
                    "TotalMoney",
                    String.valueOf(serversActives.get(nSelectPos).price
                            * nServiceAmount));
            localIntent.putExtra(PaymentMainActivity.INTENT_CONSUME_HIDE, true);
            localIntent.putExtra(PaymentResultActivity.IntentOperater, 3);
            localIntent.putExtra(PaymentResultActivity.IntentPayDetails,
                    json.toString());

            startActivity(localIntent);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void onParseJson(String jsonStr) throws JSONException {
        // JSONObject jsonContent = new JSONObject(jsonStr);
        JSONArray jsonArray = new JSONArray(jsonStr);
        if (null == jsonArray) {
            return;
        }

        List<String> list = new ArrayList<String>();
        for (int iPos = 0; iPos < jsonArray.length(); iPos++) {
            JSONObject jsonObject = jsonArray.getJSONObject(iPos);
            if (null == jsonObject) {
                continue;
            }

            // {\"type\":\"2\",\"charge_num\":200,\"coupon_id\":3,\"charge_back\":1,\"title\":\"9\\u6298\\u4f18\\u60e0\\u5238\"}
            ServersActive ra = new ServersActive();
            ra.id = jsonObject.getInt("id");
            ra.price = Float.parseFloat(jsonObject.getString("price"));
            ra.name = jsonObject.getString("name");

            // rechargeReturn.put(ra.charge_num, ra);
            serversActives.add(ra);
            list.add(ra.name);
        }

        Message localMessage = new Message();
        localMessage.what = REFRESH_SERVICE_LIST;

        handler.sendMessage(localMessage);
    }

    class QueryActiveThread extends Thread {

        public QueryActiveThread() {
        }

        public void run() {
            Looper.prepare();
            // this.serverUrl = GlobalContant.instance().serverUrl;

            String serverUrl = GlobalContant.instance().mainUrl
                    + getResources().getString(R.string.query_recharge_active);

            Map<String, String> sParaTemp = new HashMap<String, String>();
            sParaTemp.put("token", GlobalContant.instance().token);

            String str1 = "";
            try {
                str1 = HttpConnection.buildRequest(serverUrl, sParaTemp);

                Log.v("member recv", str1);

                if (str1 == null) {
                    Toast.makeText(context, "连接失败!", Toast.LENGTH_LONG).show();

                    return;
                }

                JSONObject json = new JSONObject(str1);
                if (json.isNull("servers")) {
                    Toast.makeText(context, "查询失败!", Toast.LENGTH_LONG).show();

                    return;
                }

                onParseJson(json.getString("servers"));
            } catch (Exception localException1) {
                localException1.printStackTrace();
                // Toast.makeText(context, str1, Toast.LENGTH_LONG).show();
            }

            Looper.loop();
        }
    }
}
