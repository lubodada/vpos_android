package com.ven.pos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cnyssj.pos.R;
import com.ven.pos.Payment.PaymentMainActivity;
import com.ven.pos.Payment.PaymentResultActivity;
import com.ven.pos.Util.HttpConnection;

public class RemainRechargeFragment extends Fragment implements OnClickListener {

    private EditText member_recharge_money = null;
    private TextView member_recharge_return = null;
    private TextView member_charge_service_charge = null;
    private TextView member_recharge_oper = null;
    private TextView member_charge_money_active_info = null;
    private Button remain_recharge_confirm_btn = null;
    private Context context;

    // {\"type\":\"2\",\"charge_num\":200,\"coupon_id\":3,\"charge_back\":1,\"title\":\"9\\u6298\\u4f18\\u60e0\\u5238\"}
    public class RechargeActive {
        public int type = 0;
        public int charge_num = 0;
        public int coupon_id = 0;
        public int charge_back = 0;
        public String title = "";
    }

    private Map<Integer, RechargeActive> rechargeReturn;

    public RemainRechargeFragment(Context context) {

        super();

        this.context = context;
        rechargeReturn = new HashMap<Integer, RechargeActive>();
    }

    /**
     * 覆盖此函数，先通过inflater inflate函数得到view最后返回
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.member_charge_money, container,
                false);

        initView(v);

        (new QueryActiveThread()).start();

        return v;

    }

    private void initView(View v) {
        member_recharge_money = (EditText) v
                .findViewById(R.id.member_recharge_money);
        member_recharge_return = (TextView) v
                .findViewById(R.id.member_recharge_return);
        member_charge_service_charge = (TextView) v
                .findViewById(R.id.member_charge_service_charge);
        member_recharge_oper = (TextView) v
                .findViewById(R.id.member_recharge_oper);
        member_charge_money_active_info = (TextView) v
                .findViewById(R.id.member_charge_money_active_info);
        member_charge_money_active_info.setFocusable(false);

        remain_recharge_confirm_btn = (Button) v
                .findViewById(R.id.remain_recharge_confirm);
        remain_recharge_confirm_btn.setOnClickListener(this);

        member_recharge_money.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // Log.d(TAG, "afterTextChanged");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // Log.d(TAG, "beforeTextChanged:" + s + "-" + start + "-" +
                // count + "-" + after);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // Log.d(TAG, "onTextChanged:" + s + "-" + "-" + start + "-" +
                // before + "-" + count);
                // mTextView.setText(s);

                String strMoneyInput = member_recharge_money.getText()
                        .toString();
                if (strMoneyInput.length() <= 0)
                    return;

                // strMoneyInput = strMoneyInput.substring(0,
                // strMoneyInput.length());
                // if( strMoneyInput.length() <= 0)
                // return ;

                boolean bFind = false;
                int nMoney = Integer.parseInt(strMoneyInput);

                List<Map.Entry<Integer, RechargeActive>> mHashMapEntryList = new ArrayList<Map.Entry<Integer, RechargeActive>>(
                        rechargeReturn.entrySet());

                Collections.sort(mHashMapEntryList,
                        new Comparator<Map.Entry<Integer, RechargeActive>>() {

                            @Override
                            public int compare(
                                    Map.Entry<Integer, RechargeActive> firstMapEntry,
                                    Map.Entry<Integer, RechargeActive> secondMapEntry) {
                                return (firstMapEntry.getKey() - secondMapEntry
                                        .getKey());
                            }
                        });
                for (Map.Entry<Integer, RechargeActive> entry : mHashMapEntryList) {
                    if (nMoney >= entry.getKey()) {
                        bFind = true;
                        ForegroundColorSpan clsp = new ForegroundColorSpan(
                                Color.parseColor("#ff3939"));

                        if (entry.getValue().type == 1) {
                            SpannableString spanString = new SpannableString(
                                    "返："
                                            + String.valueOf(entry.getValue().charge_back)
                                            + "元");
                            spanString.setSpan(clsp, 2,
                                    spanString.length() - 1,
                                    spanString.SPAN_EXCLUSIVE_EXCLUSIVE);
                            member_recharge_return.setText(spanString);
                        } else {
                            SpannableString spanString = new SpannableString(
                                    "返：" + entry.getValue().title);
                            spanString.setSpan(clsp, 2, spanString.length(),
                                    spanString.SPAN_EXCLUSIVE_EXCLUSIVE);
                            member_recharge_return.setText(spanString);
                        }
                    }
                }

                if (!bFind)
                    member_recharge_return.setText("");
            }

        });
        member_recharge_money.setFocusable(true);
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        if (arg0.getId() == remain_recharge_confirm_btn.getId()) {
            onRemainRechargeConfirm();
        }
    }

    private void onRemainRechargeConfirm() {

        try {

            JSONObject json = new JSONObject();

            Activity ac = ((Activity) context);
            String uid = ac.getIntent().getExtras().getString("uid");

            json.put("uid", uid);
            json.put("price", member_recharge_money.getText().toString());
            json.put("fprice", member_charge_service_charge.getText()
                    .toString());
            json.put("remake", member_recharge_oper.getText().toString());

            Intent localIntent = new Intent(context, PaymentMainActivity.class);

            String str1 = this.member_recharge_money.getText().toString();

            Pattern p = Pattern.compile("[0-9]*");
            Matcher m = p.matcher(str1);
            if (str1.isEmpty() || !m.matches()) {
                Toast.makeText(this.context, "输入有误", Toast.LENGTH_LONG).show();
                return;
            }

            localIntent.putExtra("TotalMoney", str1);
            localIntent.putExtra(PaymentMainActivity.INTENT_CONSUME_HIDE, true);
            localIntent.putExtra(PaymentResultActivity.IntentOperater, 2);

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

        String activeInfos = "";

        for (int iPos = 0; iPos < jsonArray.length(); iPos++) {
            JSONObject jsonObject = jsonArray.getJSONObject(iPos);
            if (null == jsonObject) {
                continue;
            }

            // {\"type\":\"2\",\"charge_num\":200,\"coupon_id\":3,\"charge_back\":1,\"title\":\"9\\u6298\\u4f18\\u60e0\\u5238\"}
            RechargeActive ra = new RechargeActive();
            if (!jsonObject.isNull("type"))
                ra.type = Integer.parseInt(jsonObject.getString("type"));
            if (!jsonObject.isNull("charge_num"))
                ra.charge_num = Integer.parseInt(jsonObject
                        .getString("charge_num"));
            if (!jsonObject.isNull("coupon_id"))
                ra.coupon_id = jsonObject.getInt("coupon_id");
            if (!jsonObject.isNull("charge_back"))
                ra.charge_back = jsonObject.getInt("charge_back");
            if (!jsonObject.isNull("title"))
                ra.title = jsonObject.getString("title");

            rechargeReturn.put(ra.charge_num, ra);

            activeInfos += ra.title + "   充" + String.valueOf(ra.charge_num)
                    + "返" + String.valueOf(ra.charge_back);
            if (iPos != jsonArray.length() - 1)
                activeInfos += "\n";
            ;
        }

        member_charge_money_active_info.setText(activeInfos);
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
                if (json.isNull("chargers")) {
                    Toast.makeText(context, "查询失败!", Toast.LENGTH_LONG).show();

                    return;
                }

                onParseJson(json.getString("chargers"));
            } catch (Exception localException1) {

                localException1.printStackTrace();

                // Toast.makeText(context, str1, Toast.LENGTH_LONG).show();
            }

            Looper.loop();
        }
    }
}
