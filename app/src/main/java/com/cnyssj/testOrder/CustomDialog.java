package com.cnyssj.testOrder;
 
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.cnyssj.pos.R;

public class CustomDialog extends Dialog {
 
    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }
 
    public CustomDialog(Context context) {
        super(context);
    }
 
    /**
     * 时间选择弹框
     */
    public static class Builder implements  OnDateChangedListener,OnTimeChangedListener{
 
        private Context context;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private String dateTime;
    	private String initDateTime;
      //开始日期时间UI
    	private DatePicker DatePicker;
    	private TimePicker TimePicker;
 
        private DialogInterface.OnClickListener 
                        positiveButtonClickListener,
                        negativeButtonClickListener;
 
        public Builder(Context context) {
            this.context = context;
        }
 
 
        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }
 
        public Builder setPositiveButton(int positiveButtonText,
                DialogInterface.OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }
 
        public Builder setPositiveButton(String positiveButtonText,
                DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }
 
        public Builder setNegativeButton(int negativeButtonText,
                DialogInterface.OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }
 
        public Builder setNegativeButton(String negativeButtonText,
                DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }
 
        public void init(DatePicker datePicker,TimePicker timePicker)
    	{
    		Calendar calendar= Calendar.getInstance();
    		initDateTime=calendar.get(Calendar.YEAR)+"-"+calendar.get(Calendar.MONTH)+"-"+
    				calendar.get(Calendar.DAY_OF_MONTH)+" "+
    				calendar.get(Calendar.HOUR_OF_DAY)+":"+
    				calendar.get(Calendar.MINUTE)+":"+
    				calendar.get(Calendar.SECOND);
    		datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH), this);
    		timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
    		timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
    	}
        
        public CustomDialog create(final Button dateTimeTextEdite) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
            final CustomDialog dialog = new CustomDialog(context, R.style.Dialog);
            
            dialog.setCanceledOnTouchOutside(false);
            View layout = inflater.inflate(R.layout.custom_dialog_layout, null);
            dialog.addContentView(layout, new LayoutParams(
            		LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            
            DatePicker = (DatePicker) layout.findViewById(R.id.DatePicker);
			TimePicker = (TimePicker) layout.findViewById(R.id.TimePicker);
			init(DatePicker,TimePicker);
			TimePicker.setIs24HourView(true);
			TimePicker.setOnTimeChangedListener(this);
			
            // set the confirm button
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.positiveButton))
                        .setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.positiveButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                	
                                	dateTimeTextEdite.setText(dateTime);
                                    positiveButtonClickListener.onClick(
                                    		dialog, 
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
               
                layout.findViewById(R.id.positiveButton).setVisibility(
                        View.GONE);
            }
            
            if (negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.negativeButton))
                        .setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.negativeButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick(
                                    		dialog, 
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            } else {
                
                layout.findViewById(R.id.negativeButton).setVisibility(
                        View.GONE);
            }
            dialog.setContentView(layout);
            onDateChanged(null, 0, 0, 0);
            return dialog;
        }

		@Override
		public void onTimeChanged(android.widget.TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			onDateChanged(null, 0, 0, 0);
		}

		@Override
		public void onDateChanged(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			// TODO Auto-generated method stub
			Calendar calendar = Calendar.getInstance();
			calendar.set(DatePicker.getYear(), DatePicker.getMonth(),
					DatePicker.getDayOfMonth(), TimePicker.getCurrentHour(),
					TimePicker.getCurrentMinute());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateTime=sdf.format(calendar.getTime());
//			builder.setTitle(dateTime);
		}
 
    }
 
}