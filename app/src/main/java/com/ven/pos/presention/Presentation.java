package com.ven.pos.presention;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Display;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cnyssj.pos.R;


/**
 * Created by Administrator on 2017/12/15 0015.
 */

public class Presentation extends android.app.Presentation {


    private MsgReceiver receiver;
    public final static String PRESENTATION_ACTION = "PRESENTATION_ACTION";
    private Context context;

    ListView vs_listView;
    TextView tv_total_money_vs;

    public Presentation(Context outerContext, Display display) {
        super(outerContext, display);
        this.context = outerContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vicescreen);
        vs_listView = (ListView) findViewById(R.id.vs_listView);
        tv_total_money_vs = (TextView) findViewById(R.id.tv_total_money_vs);
    }

    private void registMyReceiver() {
        receiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PRESENTATION_ACTION);
        context.registerReceiver(receiver, intentFilter);
    }

    class MsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String value = intent.getStringExtra("value");

                switch (intent.getIntExtra("type", -1)) {
                    case 1:

                        break;
                    case 2:
                        break;

                }
            }
        }
    }

    public ListView getVs_listView() {
        return vs_listView;
    }

    public TextView getTv_total_money_vs() {
        return tv_total_money_vs;
    }

}
