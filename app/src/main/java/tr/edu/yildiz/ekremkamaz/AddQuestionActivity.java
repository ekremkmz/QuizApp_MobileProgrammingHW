package tr.edu.yildiz.ekremkamaz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class AddQuestionActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout choices;
    FloatingActionButton addItem;
    FloatingActionButton save;
    EditText editTextTitle;
    DatabaseHelper DBHelper;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        userId = getIntent().getExtras().getInt("user_id");
        defineVariables();
        defineListeners();
        addNewItem();
        addNewItem();
    }

    private void defineListeners() {
        addItem.setOnClickListener(this);
        save.setOnClickListener(this);
    }

    private void defineVariables() {
        choices = (LinearLayout) findViewById(R.id.choices);
        addItem = (FloatingActionButton) findViewById(R.id.addItem);
        save = (FloatingActionButton) findViewById(R.id.save);
        editTextTitle = findViewById(R.id.editTextTitle);
        DBHelper = DatabaseHelper.getInstance(getApplicationContext());
    }

    void addNewItem() {
        View view = getLayoutInflater().inflate(R.layout.add_question_item, choices, false);
        view.findViewById(R.id.removeItem).setOnClickListener(this);
        view.findViewById(R.id.radio).setOnClickListener(this);
        choices.addView(view);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                saveQuestion();
                break;
            case R.id.addItem:
                addNewItem();
                if (choices.getChildCount() >= 5) {
                    addItem.setEnabled(false);
                }
                break;
            case R.id.removeItem:
                choices.removeView((View) view.getParent());
                if (choices.getChildCount() < 5) {
                    addItem.setEnabled(true);
                }
                break;
            case R.id.radio:
                for (int i = 0; i < choices.getChildCount(); i++) {
                    RadioButton rd = (RadioButton) choices.getChildAt(i).findViewById(R.id.radio);
                    if (rd != view) {
                        rd.setChecked(false);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void saveQuestion() {
        String title = editTextTitle.getText().toString();
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < choices.getChildCount(); i++) {
            String s = ((EditText) choices.getChildAt(i).findViewById(R.id.choice)).getText().toString();
            list.add(s);
        }
        String choicesStr = String.join("$$$", list);
        int answer = -1;
        for (int i = 0; i < choices.getChildCount(); i++) {
            if (((RadioButton) choices.getChildAt(i).findViewById(R.id.radio)).isChecked()) {
                answer = i;
            }
        }
        if (answer == -1) {
            Toast.makeText(this, "Doğru şıkkı işaretleyiniz", Toast.LENGTH_LONG).show();
            return;
        }
        String content_type = "";
        String content_path = "";
        if (DBHelper.addQuestion(userId, title, list.size(), choicesStr, answer, content_type, content_path)) {
            Toast.makeText(this, "Soru kaydedildi", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Soru kaydedilemedi!!!", Toast.LENGTH_LONG).show();
        }
    }

}