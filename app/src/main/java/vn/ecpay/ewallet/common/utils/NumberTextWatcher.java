package vn.ecpay.ewallet.common.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class NumberTextWatcher implements TextWatcher {
    private EditText amount;
    private EditText content;
    private Button confirm;
    private Context context;

    public NumberTextWatcher(EditText amount)
    {
        this.amount = amount;

    }
    public NumberTextWatcher(Context context,EditText amount, EditText content, Button confirm)
    {
        this.context = context;
        this.amount = amount;
        this.content = content;
        this.confirm = confirm;

    }

    @SuppressWarnings("unused")
    private static final String TAG = "NumberTextWatcher";

    public void afterTextChanged(Editable s)
    {
        amount.removeTextChangedListener(this);

        try {
            String originalString = s.toString();
            checkButtonConfirm(originalString);
            Long longval;
            if (originalString.contains(".")) {
               // originalString = originalString.replaceAll(",", "");
                originalString = originalString.replace(".", "");
            }
            longval = Long.parseLong(originalString);

        //   DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("vi","VN"));
            formatter.applyPattern("#,###,###,###");
            String formattedString = formatter.format(longval);

            //setting text after format to EditText
            amount.setText(formattedString);
            amount.setSelection(amount.getText().length());


        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }

        amount.addTextChangedListener(this);

    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count)
    {

    }
    public String format(String number){
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###");
        if(number==null||number.length()==0){
            number="0";
        }
        long longval = Long.parseLong(number);
        return formatter.format(longval);
    }

    private void checkButtonConfirm(String text){
            if(context!=null&&content!=null&&confirm!=null){
                if(text.length()>0){
                    //Log.e("edtContent","1");
                    if(content.getText().toString().length()>0){
                        Utils.disableButtonConfirm(context,confirm,false);
                    }else{
                        Utils.disableButtonConfirm(context,confirm,true);
                    }
                }else{
                    Utils.disableButtonConfirm(context,confirm,true);
                }
            }
    }
}