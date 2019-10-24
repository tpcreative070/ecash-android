package vn.ecpay.fragmentcommon;

import android.view.View;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

public abstract class FragmentContainer {
    @Nullable
    public abstract View findViewById(@IdRes int id);
}
