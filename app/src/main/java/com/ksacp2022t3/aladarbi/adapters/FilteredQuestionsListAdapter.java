package com.ksacp2022t3.aladarbi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ksacp2022t3.aladarbi.ChatBotActivity;
import com.ksacp2022t3.aladarbi.R;
import com.ksacp2022t3.aladarbi.models.Question;

import java.util.List;

public class FilteredQuestionsListAdapter extends RecyclerView.Adapter<FilteredQuestionItem> {
    List<Question> questions;
    Context context;

    public FilteredQuestionsListAdapter(List<Question> questions,Context context) {
        this.context=context;
        this.questions = questions;
    }

    @NonNull
    @Override
    public FilteredQuestionItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filtered_question,parent,false);
        return new FilteredQuestionItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilteredQuestionItem holder, int position) {
        Question question=questions.get(position);
            holder.txt_question.setText(question.getText());

        holder.question_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ChatBotActivity)context).add_question(question);
            }
        });

    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
}

class FilteredQuestionItem extends RecyclerView.ViewHolder {
        TextView txt_question ;
        LinearLayoutCompat question_card;

    public FilteredQuestionItem(@NonNull View itemView) {
        super(itemView);
        txt_question=itemView.findViewById(R.id.txt_question);
        question_card=itemView.findViewById(R.id.question_card);
    }
}
