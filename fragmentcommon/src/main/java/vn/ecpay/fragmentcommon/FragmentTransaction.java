package vn.ecpay.fragmentcommon;

import android.os.Bundle;
import androidx.annotation.IdRes;

public class FragmentTransaction {

    String presentFragmentName;
    BaseFragment presentFragment;
    Bundle arguments;
    @IdRes
    int containerId = R.id.fragment_container;
    boolean animated = true;
    boolean isClearTop;
    boolean removeLast;
    boolean isClearStack;
    int requestCode;
    boolean addToBackStack = true;

    public void setPresentFragmentName(String presentFragmentName) {
        this.presentFragmentName = presentFragmentName;
    }

    public void setPresentFragment(BaseFragment presentFragment) {
        this.presentFragment = presentFragment;
    }

    public void setArguments(Bundle arguments) {
        this.arguments = arguments;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public void setContainerId(int containerId) {
        this.containerId = containerId;
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    public void setClearTop(boolean clearTop) {
        isClearTop = clearTop;
    }

    public void setAddToBackStack(boolean addToBackStack) {
        this.addToBackStack = addToBackStack;
    }

    public void setRemoveLast(boolean removeLast) {
        this.removeLast = removeLast;
    }

    public void setClearStack(boolean clearStack) {
        isClearStack = clearStack;
    }
}
