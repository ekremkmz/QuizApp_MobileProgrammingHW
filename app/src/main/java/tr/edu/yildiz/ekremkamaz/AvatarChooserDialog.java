package tr.edu.yildiz.ekremkamaz;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class AvatarChooserDialog extends Dialog {
    private Activity a;
    private ArrayList<String> avatarNames;
    private GridView gridView;
    private DisplayMetrics metrics;


    public AvatarChooserDialog(Activity a, DisplayMetrics metrics) {
        super(a);
        this.a = a;
        avatarNames = new ArrayList<String>();
        this.metrics = metrics;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_avatar_chooser);
        Resources resources = getContext().getResources();
        gridView = (GridView) findViewById(R.id.gridView);
        for (int i = 110; i <= 149; i++) {
            String s = new String("avatar_" + String.valueOf(i));
            avatarNames.add(s);
        }
        GridAdapter gridAdapter = new GridAdapter(a, this, avatarNames, metrics.widthPixels / 6);
        gridView.setAdapter(gridAdapter);
        gridView.setGravity(Gravity.CENTER);
        gridView.setVerticalSpacing(40);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((SignUpActivity) a).setImage((String) adapterView.getItemAtPosition(i));
                dismiss();
            }
        });

    }
    private class GridAdapter extends BaseAdapter {
        Context context;
        Dialog d;
        ArrayList<String> assetNames;
        int size;

        public GridAdapter(Context context, Dialog d, ArrayList<String> assetNames, int size) {
            this.context = context;
            this.d = d;
            this.assetNames = assetNames;
            this.size = size;
        }

        @Override
        public int getCount() {
            return assetNames.size();
        }

        @Override
        public Object getItem(int i) {
            return assetNames.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(d.getContext().getResources().getIdentifier(assetNames.get(i), "drawable", d.getContext().getPackageName()));

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(size, size));
            return imageView;
        }
    }
}

