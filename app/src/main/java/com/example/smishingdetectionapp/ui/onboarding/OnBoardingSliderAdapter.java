package com.example.smishingdetectionapp.ui.onboarding;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.R;

import java.util.List;
public class OnBoardingSliderAdapter extends RecyclerView.Adapter<OnBoardingSliderAdapter.IntroSlideViewHolder> {

    private Context context;
    private List<OnBoardingSlide> slides;

    public OnBoardingSliderAdapter(Context context, List<OnBoardingSlide> slides) {
        this.context = context;
        this.slides = slides;
    }

    @NonNull
    @Override
    public IntroSlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IntroSlideViewHolder(
                LayoutInflater.from(context).inflate(R.layout.onboarding_slide_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull IntroSlideViewHolder holder, int position) {
        holder.bind(slides.get(position));
    }

    @Override
    public int getItemCount() {
        return slides.size();
    }

    public class IntroSlideViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView titleTextView;
        private TextView descriptionTextView;

        public IntroSlideViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.introImage);
            titleTextView = itemView.findViewById(R.id.introTitle);
            descriptionTextView = itemView.findViewById(R.id.introDescription);

        }

        void bind(OnBoardingSlide slide) {
            imageView.setImageResource(slide.getImage());
            titleTextView.setText(slide.getTitle());
            descriptionTextView.setText(slide.getDescription());

        }
    }




}
