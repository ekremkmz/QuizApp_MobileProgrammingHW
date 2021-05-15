package tr.edu.yildiz.ekremkamaz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ListQuestionsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Question> questions;
    private DatabaseHelper DBHelper;
    //TODO fix at the end
    private int user_id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_questions);
        defineVariables();
    }

    private void defineVariables() {
        //TODO fix at the end
        //user_id = getIntent().getExtras().getInt("user_id");
        recyclerView = (RecyclerView) findViewById(R.id.questionsRecyclerView);
        DBHelper = DatabaseHelper.getInstance(getApplicationContext());
        questions = DBHelper.getQuestions(user_id);
        CustomAdapter customAdapter = new CustomAdapter(questions);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
        ArrayList<Question> questions;

        public CustomAdapter(ArrayList<Question> questions) {
            this.questions = questions;
        }

        public void changeData(Question question, int position) {
            questions.set(position, question);
            notifyItemChanged(position);
        }

        @NonNull
        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View question = inflater.inflate(R.layout.list_question_item, parent, false);

            ViewHolder viewHolder = new ViewHolder(question);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

            Question question = questions.get(position);
            holder.titleTextView.setText(question.getTitle());

            LinearLayout choices = holder.choices;
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

            choices.removeAllViewsInLayout();
            for (int i = 0; i < question.getLevel(); i++) {
                View view = inflater.inflate(R.layout.list_question_choice, null);
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

        public void deleteQuestion(int position) {
            Question question = questions.get(position);
            DBHelper.deleteQuestion(question);
            questions.remove(question);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, questions.size());
        }

        @Override
        public int getItemCount() {
            return questions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView titleTextView;
            public MaterialButton expandButton;
            public MaterialButton editButton;
            public MaterialButton removeButton;
            public LinearLayout choices;


            public ViewHolder(@NonNull @NotNull View itemView) {
                super(itemView);
                titleTextView = (TextView) itemView.findViewById(R.id.listQuestionItemTitle);
                choices = (LinearLayout) itemView.findViewById(R.id.listQuestionItemChoices);
                expandButton = (MaterialButton) itemView.findViewById(R.id.listQuestionItemExpandButton);
                editButton = (MaterialButton) itemView.findViewById(R.id.listQuestionItemEditButton);
                removeButton = (MaterialButton) itemView.findViewById(R.id.listQuestionItemRemoveButton);
                expandButton.setOnClickListener(this);
                editButton.setOnClickListener(this);
                removeButton.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                switch (view.getId()) {
                    case R.id.listQuestionItemEditButton: {
                        Intent _intent = new Intent(ListQuestionsActivity.this, AddQuestionActivity.class);
                        _intent.putExtra("question", questions.get(position));
                        _intent.putExtra("position", position);
                        startActivityForResult(_intent, 666);
                    }
                    break;
                    case R.id.listQuestionItemExpandButton: {
                        Question question = questions.get(position);
                        question.setExpanded(!question.getExpanded());
                        notifyItemChanged(position);
                    }
                    break;
                    case R.id.listQuestionItemRemoveButton: {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ListQuestionsActivity.this);
                        builder.setTitle("Uyarı");
                        builder.setMessage("Soru silinecek. Emin misiniz?");
                        builder.setNegativeButton("Hayır", null);
                        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteQuestion(position);
                            }
                        });
                        builder.show();
                    }
                    break;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 666) {
            CustomAdapter adapter = ((CustomAdapter) recyclerView.getAdapter());
            Bundle bundle = data.getExtras();
            int position = bundle.getInt("position");
            Question question = data.getParcelableExtra("question");

            adapter.changeData(question, position);
        }
    }
}