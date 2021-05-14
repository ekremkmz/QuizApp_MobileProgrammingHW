package tr.edu.yildiz.ekremkamaz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {
    private ArrayList<String> assetNames;
    private GridView gridView;
    int[] drawables = {R.drawable.menuadd, R.drawable.menulist, R.drawable.menuexam, R.drawable.menusettings};
    String[] names = {"Soru Ekle", "Soruları Listele", "Sınavlar", "Sınav Ayarları"};
    private User _user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        defineVariables();
        defineListeners();
    }

    private void defineListeners() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0: {
                        Intent _intent = new Intent(MenuActivity.this, AddQuestionActivity.class);
                        _intent.putExtra("user_id", _user.getId());
                        startActivity(_intent);
                    }
                        break;
                    case 1: {
                        Intent _intent = new Intent(MenuActivity.this, ListQuestionsActivity.class);
                        _intent.putExtra("user_id", _user.getId());
                        startActivity(_intent);
                    }
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void defineVariables() {
        _user = getIntent().getParcelableExtra("user");
        gridView = findViewById(R.id.gridView2);
        GridAdapter gridAdapter = new GridAdapter(names, drawables, this);
        gridView.setAdapter(gridAdapter);
        gridView.setGravity(Gravity.CENTER);
        gridView.setVerticalSpacing(40);
        gridView.setHorizontalSpacing(20);
    }

    private class GridAdapter extends BaseAdapter {
        String[] imageNames;
        int[] imagesPhoto;
        Context context;
        LayoutInflater layoutInflater;

        public GridAdapter(String[] imageNames, int[] imagesPhoto, Context context) {
            this.imageNames = imageNames;
            this.imagesPhoto = imagesPhoto;
            this.context = context;
            this.layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return imagesPhoto.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = layoutInflater.inflate(R.layout.menu_item, viewGroup, false);
            }
            TextView textView = view.findViewById(R.id.menuText);
            ImageView imageView = view.findViewById(R.id.menuImage);
            textView.setText(imageNames[i]);
            imageView.setImageResource(imagesPhoto[i]);
            return view;
        }
    }
}

