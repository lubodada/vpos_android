/**
 * 
 */
package com.ven.pos;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.cnyssj.pos.R;
import com.ven.pos.Util.UtilTool;

/**
 * @author Administrator
 *
 */
public class MemberRechargeActivity extends FragmentActivity  implements OnClickListener  {
	
	private ViewPager viewPager;
	private Button remianRechargeBtn;
	private Button serviceRechargeBtn;
	 private MemberRechargePagerAdapter mAdapter;
	 private Fragment fg;
	
	List<Fragment> fragmentList = new ArrayList<Fragment>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		com.ven.pos.Util.ActivityManager.getInstance().addActivity(this);
		TitleBar.setTitleBar(this, "后退", " 会员充值", "", null);
		
		setContentView(R.layout.member_charge);
		initView();
		UtilTool.checkLoginStatusAndReturnLoginActivity(this, null);
	}
	
	private void initView(){
		remianRechargeBtn = (Button)findViewById(R.id.remain_recharge_btn);
		serviceRechargeBtn= (Button)findViewById(R.id.serivce_recharge_btn);
		
		remianRechargeBtn.setOnClickListener(this);
		serviceRechargeBtn.setOnClickListener(this);
		
		viewPager = (ViewPager)findViewById(R.id.service_viewpager);
		fragmentList.add(new RemainRechargeFragment(this));
		fragmentList.add(new ServiceRechargeFragment(this));


		mAdapter = new MemberRechargePagerAdapter(getSupportFragmentManager(), fragmentList);
		viewPager.setAdapter(mAdapter);

	}
	
	@Override
	public void onStart() {

		TitleBar.setActivity(this);

		super.onStart();
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		// OpenIntegralWall.getInstance().onUnbind();
		super.onDestroy();
		com.ven.pos.Util.ActivityManager.getInstance().removeActivity(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(arg0.getId() == remianRechargeBtn.getId()){
			viewPager.setCurrentItem(0);
			remianRechargeBtn.setBackgroundResource(R.drawable.fc_blue_left);
			serviceRechargeBtn.setBackgroundResource(R.drawable.fc_white_right);
			
		}
		else if(arg0.getId() == serviceRechargeBtn.getId()){
			viewPager.setCurrentItem(1);
			serviceRechargeBtn.setBackgroundResource(R.drawable.fc_blue_right);
			remianRechargeBtn.setBackgroundResource(R.drawable.fc_white_left);
		}
	}
	
	 class MemberRechargePagerAdapter extends FragmentPagerAdapter {

	        private List<Fragment> fragmentList;

	        public MemberRechargePagerAdapter(FragmentManager fm, List<Fragment> fragmentList){

	            super(fm);
	            this.fragmentList = fragmentList;
	
	        }

	        /**
	         * 得到每个页面
	         */

	        @Override
	        public Fragment getItem(int arg0) {
	            return (fragmentList == null || fragmentList.size() == 0) ? null : fragmentList.get(arg0);
	        }

	        /**
	         * 页面的总个数
	         */
	        @Override
	        public int getCount() {
	           return fragmentList == null ? 0 : fragmentList.size();
	        }
	    }
	 
	
}
