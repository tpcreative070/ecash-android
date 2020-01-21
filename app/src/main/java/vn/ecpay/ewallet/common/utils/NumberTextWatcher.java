package vn.ecpay.ewallet.common.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class NumberTextWatcher implements TextWatcher {
    private EditText et;

    public NumberTextWatcher(EditText et)
    {
        this.et = et;

    }

    @SuppressWarnings("unused")
    private static final String TAG = "NumberTextWatcher";

    public void afterTextChanged(Editable s)
    {
        et.removeTextChangedListener(this);

        try {
            String originalString = s.toString();

            Long longval;
            if (originalString.contains(",")) {
                originalString = originalString.replaceAll(",", "");
            }
            longval = Long.parseLong(originalString);

            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            formatter.applyPattern("#,###,###,###");
            String formattedString = formatter.format(longval);

            //setting text after format to EditText
            et.setText(formattedString);
            et.setSelection(et.getText().length());
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }

        et.addTextChangedListener(this);

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
}