package tr.edu.yildiz.ekremkamaz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ExamSettingsActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    EditText defaultExamTime;
    EditText defaultExamPoint;
    SeekBar defaultExamLevel;
    TextView defaultExamLevelText;
    FloatingActionButton save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_settings);
        defineVariables();
        defineListeners();
    }

    private void defineListeners() {
        defaultExamLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                defaultExamLevelText.setText(String.valueOf(2 + i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("defaultExamTime", defaultExamTime.getText().toString());
                editor.putString("defaultExamPoint", defaultExamPoint.getText().toString());
                editor.putInt("defaultExamLevel", defaultExamLevel.getProgress() + 2);
                editor.commit();
                finish();
            }
        });
    }

    private void defineVariables() {
        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        defaultExamTime = findViewById(R.id.defaultExamTime);
        defaultExamPoint = findViewById(R.id.defaultExamPoint);
        defaultExamLevel = findViewById(R.id.defaultExamLevel);
        defaultExamLevelText = findViewById(R.id.defaultExamLevelText);
        save = findViewById(R.id.save);
        defaultExamTime.setText(sharedPreferences.getString("defaultExamTime", "120"));
        defaultExamPoint.setText(sharedPreferences.getString("defaultExamPoint", "10"));
        int progress = sharedPreferences.getInt("defaultExamLevel", 4);
        defaultExamLevel.setProgress(progress - 2);
        defaultExamLevelText.setText(String.valueOf(progress));

    }
}