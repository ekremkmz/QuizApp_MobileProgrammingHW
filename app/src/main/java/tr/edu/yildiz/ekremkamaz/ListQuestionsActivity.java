package tr.edu.yildiz.ekremkamaz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.icu.util.Measure;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
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
    private int user_id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_questions);
        defineVariables();
    }

    private void defineVariables() {
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
            if(choices.getChildCount()==0){
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
            }
            choices.setVisibility(question.getExpanded() ? View.VISIBLE : View.GONE);
        }

        @Override
        public int getItemCount() {
            return questions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView titleTextView;
            public MaterialButton expandButton;
            public LinearLayout choices;


            public ViewHolder(@NonNull @NotNull View itemView) {
                super(itemView);
                titleTextView = (TextView) itemView.findViewById(R.id.listQuestionItemTitle);
                choices = (LinearLayout) itemView.findViewById(R.id.listQuestionItemChoices);
                expandButton = (MaterialButton) itemView.findViewById(R.id.listQuestionItemButton);
                expandButton.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.listQuestionItemButton) {
                    LinearLayout choices = ((View) view.getParent().getParent()).findViewById(R.id.listQuestionItemChoices);
                    Question question = questions.get(getAdapterPosition());
                    question.setExpanded(!question.getExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            }
            /*
            public void expand(final View v, int duration, int targetHeight) {

                int prevHeight = v.getHeight();

                ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        v.getLayoutParams().height = (int) animation.getAnimatedValue();
                        v.requestLayout();
                    }
                });
                valueAnimator.setInterpolator(new DecelerateInterpolator());
                valueAnimator.setDuration(duration);
                valueAnimator.start();
            }

            public void collapse(final View v, int duration, int targetHeight) {
                int prevHeight = v.getHeight();
                ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
                valueAnimator.setInterpolator(new DecelerateInterpolator());
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        v.getLayoutParams().height = (int) animation.getAnimatedValue();
                        v.requestLayout();
                    }
                });
                valueAnimator.setInterpolator(new DecelerateInterpolator());
                valueAnimator.setDuration(duration);
                valueAnimator.start();
            }*/
        }
    }
}