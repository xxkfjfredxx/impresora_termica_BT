package com.example.manejoimpresora.FragmentsUI.Others;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.manejoimpresora.R;

public class rewardFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private int a;
    private Button butRewarded;
    private String mParam1;
    private String mParam2;
    private TextView numePrints;
    private int numeroImpres;

    public static rewardFragment newInstance(String str, String str2) {
        rewardFragment rewardfragment = new rewardFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        rewardfragment.setArguments(bundle);
        return rewardfragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.mParam1 = getArguments().getString(ARG_PARAM1);
            this.mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_reward, viewGroup, false);
        this.butRewarded = (Button) inflate.findViewById(R.id.butReward);
        this.numePrints = (TextView) inflate.findViewById(R.id.txtcantPrin);
        return inflate;
    }
}
