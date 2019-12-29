package vn.ecpay.ewallet.ui.startApp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import vn.ecpay.ewallet.R;

public class SlidePager extends PagerAdapter {
    private Context context;

    public SlidePager(Context context) {
        this.context = context;
    }

    /*
    This callback is responsible for creating a page. We inflate the layout and set the drawable
    to the ImageView based on the position. In the end we add the inflated layout to the parent
    container .This method returns an object key to identify the page view, but in this example page view
    itself acts as the object key
    */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.pager_item, null);
        ImageView imageView = view.findViewById(R.id.image);
        ImageView imvFull = view.findViewById(R.id.image_full);
        TextView tvTitle = view.findViewById(R.id.tv_chess);
        switch (position) {
            case 0:
                imageView.setVisibility(View.VISIBLE);
                imvFull.setVisibility(View.GONE);
                imageView.setImageDrawable(context.getResources().getDrawable(getImageAt(position)));
                tvTitle.setText("Tài lộc dồi dào");
                break;
            case 1:
                imageView.setVisibility(View.VISIBLE);
                imvFull.setVisibility(View.GONE);
                imageView.setImageDrawable(context.getResources().getDrawable(getImageAt(position)));
                tvTitle.setText("An khang thịnh vượng");
                break;
            case 2:
                imageView.setVisibility(View.VISIBLE);
                imvFull.setVisibility(View.GONE);
                imageView.setImageDrawable(context.getResources().getDrawable(getImageAt(position)));
                tvTitle.setText("Vạn sự như ý");
                break;
            case 3:
                imvFull.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                imvFull.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_4));
                break;

        }
        container.addView(view);
        return view;
    }

    /*
    This callback is responsible for destroying a page. Since we are using view only as the
    object key we just directly remove the view from parent container
    */
    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);
    }

    /*
    Returns the count of the total pages
    */
    @Override
    public int getCount() {
        return 4;
    }

    /*
    Used to determine whether the page view is associated with object key returned by instantiateItem.
    Since here view only is the key we return view==object
    */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }

    private int getImageAt(int position) {
        switch (position) {
            case 0:
                return R.drawable.ic_1;
            case 1:
                return R.drawable.ic_2;
            case 2:
                return R.drawable.ic_3;
            default:
                return R.drawable.ic_4;
        }
    }
}