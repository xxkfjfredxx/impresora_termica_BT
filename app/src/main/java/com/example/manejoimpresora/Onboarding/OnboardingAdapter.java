package com.example.manejoimpresora.Onboarding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.manejoimpresora.R;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {
    private List<OnboardingItem> onboardingItems;

    public OnboardingAdapter(List<OnboardingItem> list) {
        this.onboardingItems = list;
    }

    public OnboardingViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new OnboardingViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_container_onboarding, viewGroup, false));
    }

    public void onBindViewHolder(OnboardingViewHolder onboardingViewHolder, int i) {
        onboardingViewHolder.setOnboardingData(this.onboardingItems.get(i));
    }

    public int getItemCount() {
        return this.onboardingItems.size();
    }

    class OnboardingViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageOnboarding;
        private TextView textDescription;
        private TextView textTitle;

        public OnboardingViewHolder(View view) {
            super(view);
            this.textTitle = (TextView) view.findViewById(R.id.textTitle);
            this.textDescription = (TextView) view.findViewById(R.id.textDescription);
            this.imageOnboarding = (ImageView) view.findViewById(R.id.imageOnboarding);
        }

        /* access modifiers changed from: package-private */
        public void setOnboardingData(OnboardingItem onboardingItem) {
            this.textTitle.setText(onboardingItem.getTitle());
            this.textDescription.setText(onboardingItem.getDescription());
            this.imageOnboarding.setImageResource(onboardingItem.getImage());
        }
    }
}
