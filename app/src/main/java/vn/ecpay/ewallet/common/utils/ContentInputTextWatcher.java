package vn.ecpay.ewallet.common.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

public class ContentInputTextWatcher implements TextWatcher {
    private EditText amount;
    private EditText content;
    private Button confirm;
    private Context context;
    public ContentInputTextWatcher(Context context, EditText amount,EditText content, Button confirm)
    {
        this.context = context;
        this.amount = amount;
        this.content = content;
        this.confirm = confirm;

    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        content.removeTextChangedListener(this);
        if(context!=null&&amount!=null&&confirm!=null){
            if(s.toString().length()>0){
                if(amount.getText().toString().length()>0){
                    Utils.disableButtonConfirm(context,confirm,false);
                }else{
                    Utils.disableButtonConfirm(context,confirm,true);
                }
            }else{
                Utils.disableButtonConfirm(context,confirm,true);
            }
        }
        content.addTextChangedListener(this);
    }
}
