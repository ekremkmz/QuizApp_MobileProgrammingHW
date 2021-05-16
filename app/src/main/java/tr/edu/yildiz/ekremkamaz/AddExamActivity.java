package tr.edu.yildiz.ekremkamaz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import tr.edu.yildiz.ekremkamaz.model.Exam;
import tr.edu.yildiz.ekremkamaz.model.Question;

public class AddExamActivity extends AppCompatActivity {
    RecyclerView addExamRecyclerView;
    TextView selectedCount;
    TextView addExamDifficulty;
    FloatingActionButton save;
    DatabaseHelper DBHelper;
    EditText addExamTime;
    EditText addExamPoint;
    SeekBar addExamSeekBar;
    ArrayList<Question> questionArrayList;
    int user_id;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exam);
        defineVariables();
        defineListeners();
    }

    private void defineListeners() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomAdapter customAdapter = (CustomAdapter) addExamRecyclerView.getAdapter();
                ArrayList<Question> questions = customAdapter.getQuestions();
                ArrayList<Boolean> selected = customAdapter.getSelected();
                ArrayList<String> selectedQuestions = new ArrayList<>();
                for (int i = 0; i < selected.size(); i++) {
                    if (selected.get(i)) {
                        selectedQuestions.add(String.valueOf(questions.get(i).getId()));
                    }
                }
                Exam exam = new Exam("Sınav", addExamSeekBar.getProgress() + 2, Integer.parseInt(addExamTime.getText().toString()),
                        Integer.parseInt(addExamPoint.getText().toString()), selectedQuestions, 0, user_id);
                DBHelper.addExam(exam);
                Intent _intent = new Intent();
                _intent.putExtra("exam", exam);
                setResult(RESULT_OK, _intent);
                finish();
            }
        });
        addExamSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                addExamDifficulty.setText("Zorluk " + String.valueOf(2 + i));
                addExamRecyclerView.setAdapter(createAdapter());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void defineVariables() {
        user_id = getIntent().getIntExtra("user_id", 1);
        addExamRecyclerView = findViewById(R.id.addExamRecyclerView);
        selectedCount = findViewById(R.id.selectedCount);
        save = findViewById(R.id.save);
        DBHelper = DatabaseHelper.getInstance(AddExamActivity.this);
        questionArrayList = DBHelper.getQuestions(user_id);
        addExamTime = findViewById(R.id.addExamTime);
        addExamPoint = findViewById(R.id.addExamPoint);
        addExamSeekBar = findViewById(R.id.addExamSeekBar);
        addExamDifficulty = findViewById(R.id.addExamDifficulty);
        selectedCount = findViewById(R.id.selectedCount);
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        addExamTime.setText(sharedPreferences.getString("defaultExamTime", "120"));
        addExamPoint.setText(sharedPreferences.getString("defaultExamPoint", "10"));
        int progress = sharedPreferences.getInt("defaultExamLevel", 4);
        addExamSeekBar.setProgress(progress - 2);
        addExamDifficulty.setText("Zorluk " + String.valueOf(progress));
        addExamRecyclerView.setAdapter(createAdapter());
        addExamRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private CustomAdapter createAdapter() {
        return new CustomAdapter((ArrayList<Question>) questionArrayList.stream().filter(x -> x.getLevel() >= addExamSeekBar.getProgress() + 2).collect(Collectors.<Question>toList()));
    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        ArrayList<Question> questions;
        ArrayList<Boolean> selected;

        public CustomAdapter(ArrayList<Question> questions) {
            this.questions = questions;
            selected = new ArrayList<Boolean>();
            for (int i = 0; i < questions.size(); i++) {
                selected.add(false);
            }
        }

        public ArrayList<Question> getQuestions() {
            return questions;
        }

        public ArrayList<Boolean> getSelected() {
            return selected;
        }

        @NonNull
        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View exam = inflater.inflate(R.layout.item_add_exam, parent, false);

            ViewHolder viewHolder = new ViewHolder(exam);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
            Question question = questions.get(position);
            holder.addExamItemTitle.setText(question.getTitle());

            LinearLayout choices = holder.choices;
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

            choices.removeAllViewsInLayout();
            for (int i = 0; i < question.getLevel(); i++) {
                View view = inflater.inflate(R.layout.item_list_question_choice, null);
                RadioButton radioButton = view.findViewById(R.id.listQuestionItemRadio);
                if (question.getAnswer() == i) {
                    radioButton.setChecked(true);
                }
                radioButton.setEnabled(false);
                TextView choice = view.findViewById(R.id.listQuestionItemChoice);
                choice.setText(question.getChoices().get(i));
                choices.addView(view);
            }

            choices.setVisibility(question.getExpanded() ? View.VISIBLE : View.GONE);
        }

        @Override
        public int getItemCount() {
            return questions.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView addExamItemTitle;
            MaterialButton addExamItemAddButton;
            MaterialButton addExamItemExpandButton;
            LinearLayout choices;

            public ViewHolder(@NonNull @NotNull View itemView) {
                super(itemView);
                addExamItemTitle = itemView.findViewById(R.id.addExamItemTitle);
                addExamItemAddButton = itemView.findViewById(R.id.addExamItemAddButton);
                addExamItemExpandButton = itemView.findViewById(R.id.addExamItemExpandButton);
                choices = itemView.findViewById(R.id.addExamItemChoices);
                addExamItemAddButton.setOnClickListener(this);
                addExamItemExpandButton.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                switch (view.getId()) {
                    case R.id.addExamItemAddButton: {
                        selected.set(position, !selected.get(position));
                        if (selected.get(position)) {
                            addExamItemAddButton.setIcon(getDrawable(R.drawable.remove_icon));
                            addExamItemAddButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                            count++;

                        } else {
                            addExamItemAddButton.setIcon(getDrawable(R.drawable.add_button));
                            addExamItemAddButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.expandable_button_color)));
                            count--;
                        }

                        selectedCount.setText(count + " soru seçildi");
                    }
                    break;
                    case R.id.addExamItemExpandButton: {
                        Question question = questions.get(position);
                        question.setExpanded(!question.getExpanded());
                        notifyItemChanged(position);
                    }
                    break;
                    default:
                        break;
                }
            }
        }
    }
}