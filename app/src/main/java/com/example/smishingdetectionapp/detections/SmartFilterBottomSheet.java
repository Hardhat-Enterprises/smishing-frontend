package com.example.smishingdetectionapp.detections;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.smishingdetectionapp.R;

public class SmartFilterBottomSheet extends BottomSheetDialogFragment {

    private RadioGroup sortGroup;
    private Button buttonApply, buttonReset;

    public interface FilterListener {
        void onFilterApplied(boolean newestFirst);
    }

    private FilterListener listener;

    public void setFilterListener(FilterListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_smart_filter_sheet, container, false);

        sortGroup = view.findViewById(R.id.sortGroup);
        buttonApply = view.findViewById(R.id.button_apply);
        buttonReset = view.findViewById(R.id.button_reset);

        buttonApply.setOnClickListener(v -> {
            boolean newestFirst = sortGroup.getCheckedRadioButtonId() == R.id.radio_newest;
            if (listener != null) {
                listener.onFilterApplied(newestFirst);
            }
            dismiss();
        });

        buttonReset.setOnClickListener(v -> {
            sortGroup.check(R.id.radio_oldest);
        });

        return view;
    }
}
