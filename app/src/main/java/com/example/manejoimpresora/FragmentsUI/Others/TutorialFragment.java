package com.example.manejoimpresora.FragmentsUI.Others;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.manejoimpresora.Onboarding.OnboardingAdapter;
import com.example.manejoimpresora.Onboarding.OnboardingItem;
import com.example.manejoimpresora.R;

import java.util.ArrayList;

public class TutorialFragment extends Fragment {
    private Button buttonOnboardingAction;
    private LinearLayout layoutOnboardingIndicators;
    /* access modifiers changed from: private */
    public OnboardingAdapter onboardingAdapter;
    private Button txTurial;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_tutorial, viewGroup, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        this.layoutOnboardingIndicators = (LinearLayout) inflate.findViewById(R.id.layoutOnboardingIndicators);
        this.buttonOnboardingAction = (Button) inflate.findViewById(R.id.buttonOnboardingAction);
        this.txTurial = (Button) inflate.findViewById(R.id.textViewTutorial);
        setupOnboardingItems();
        final ViewPager2 viewPager2 = (ViewPager2) inflate.findViewById(R.id.onboardingViewPager);
        viewPager2.setAdapter(this.onboardingAdapter);
        setupOnboardingIndicators();
        setCurrentIndicator(0);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            public void onPageSelected(int i) {
                super.onPageSelected(i);
                TutorialFragment.this.setCurrentIndicator(i);
            }
        });
        this.buttonOnboardingAction.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (viewPager2.getCurrentItem() + 1 < TutorialFragment.this.onboardingAdapter.getItemCount()) {
                    viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
                    return;
                }
                Navigation.findNavController(view).navigate((int) R.id.nav_home);
            }
        });
        this.txTurial.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                TutorialFragment.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://www.youtube.com/watch?v=WbD5rJdTq4k")));
            }
        });
        return inflate;
    }

    private void setupOnboardingItems() {
        ArrayList arrayList = new ArrayList();
        OnboardingItem onboardingItem = new OnboardingItem();
        onboardingItem.setTitle(getString(R.string.functions));
        onboardingItem.setDescription(getString(R.string.functions2));
        onboardingItem.setImage(R.drawable.fotofeatures);
        OnboardingItem onboardingItem2 = new OnboardingItem();
        onboardingItem2.setTitle(getString(R.string.Persoadd1));
        onboardingItem2.setDescription(getString(R.string.Persoadd2));
        onboardingItem2.setImage(R.drawable.fotosettings2);
        OnboardingItem onboardingItem3 = new OnboardingItem();
        onboardingItem3.setTitle(getString(R.string.addProdu));
        onboardingItem3.setDescription(getString(R.string.addProdu2));
        onboardingItem3.setImage(R.drawable.fotproducts);
        OnboardingItem onboardingItem4 = new OnboardingItem();
        onboardingItem4.setTitle(getString(R.string.inventories));
        onboardingItem4.setDescription(getString(R.string.inventories2));
        onboardingItem4.setImage(R.drawable.fotoinventory);
        OnboardingItem onboardingItem5 = new OnboardingItem();
        onboardingItem5.setTitle(getString(R.string.printOnbo));
        onboardingItem5.setDescription(getString(R.string.printOnbo2));
        onboardingItem5.setImage(R.drawable.fotoprint);
        OnboardingItem onboardingItem6 = new OnboardingItem();
        onboardingItem6.setTitle(getString(R.string.ReportOn));
        onboardingItem6.setDescription(getString(R.string.ReportOn2));
        onboardingItem6.setImage(R.drawable.fotoreport);
        arrayList.add(onboardingItem);
        arrayList.add(onboardingItem2);
        arrayList.add(onboardingItem3);
        arrayList.add(onboardingItem4);
        arrayList.add(onboardingItem5);
        arrayList.add(onboardingItem6);
        this.onboardingAdapter = new OnboardingAdapter(arrayList);
    }

    private void setupOnboardingIndicators() {
        int itemCount = this.onboardingAdapter.getItemCount();
        ImageView[] imageViewArr = new ImageView[itemCount];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < itemCount; i++) {
            imageViewArr[i] = new ImageView(getContext());
            imageViewArr[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.onboarding_indicator_inactive));
            imageViewArr[i].setLayoutParams(layoutParams);
            this.layoutOnboardingIndicators.addView(imageViewArr[i]);
        }
    }

    /* access modifiers changed from: private */
    public void setCurrentIndicator(int i) {
        int childCount = this.layoutOnboardingIndicators.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            ImageView imageView = (ImageView) this.layoutOnboardingIndicators.getChildAt(i2);
            if (i2 == i) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.onboarding_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.onboarding_indicator_inactive));
            }
        }
        if (i == this.onboardingAdapter.getItemCount() - 1) {
            this.buttonOnboardingAction.setText(getString(R.string.Start));
            this.txTurial.setVisibility(View.VISIBLE);
            return;
        }
        this.buttonOnboardingAction.setText(getString(R.string.nextto));
    }

    public static TutorialFragment newInstance(String str, String str2) {
        TutorialFragment tutorialFragment = new TutorialFragment();
        tutorialFragment.setArguments(new Bundle());
        return tutorialFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getArguments();
    }
}
