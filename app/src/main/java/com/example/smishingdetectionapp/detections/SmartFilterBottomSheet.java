package com.example.smishingdetectionapp.detections;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smishingdetectionapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SmartFilterBottomSheet extends BottomSheetDialogFragment {

    private RadioGroup sortGroup;
    private CheckBox checkboxLink, checkboxToday, checkboxLast7Days;
    private LinearLayout yearContainer;
    private final List<CheckBox> yearCheckBoxes = new ArrayList<>();
    private Button buttonApply, buttonReset, buttonStartDate, buttonEndDate;

    private String selectedStartDate = null;
    private String selectedEndDate = null;

    public interface FilterListener {
        void onFilterApplied(boolean newestFirst, boolean containsLink, boolean todayOnly, boolean last7DaysOnly, List<String> selectedYears, String startDate, String endDate);
    }

    private FilterListener listener;

    public void setFilterListener(FilterListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_smart_filter_sheet, container, false);

        sortGroup = view.findViewById(R.id.sortGroup);
        checkboxLink = view.findViewById(R.id.checkbox_link);
        checkboxToday = view.findViewById(R.id.checkbox_today);
        checkboxLast7Days = view.findViewById(R.id.checkbox_last7days);
        yearContainer = view.findViewById(R.id.year_filter_container);
        buttonApply = view.findViewById(R.id.button_apply);
        buttonReset = view.findViewById(R.id.button_reset);
        buttonStartDate = view.findViewById(R.id.button_start_date);
        buttonEndDate = view.findViewById(R.id.button_end_date);

        populateYearsFromDatabase();

        buttonStartDate.setOnClickListener(v -> showDatePicker(true));
        buttonEndDate.setOnClickListener(v -> showDatePicker(false));

        buttonApply.setOnClickListener(v -> {
            boolean newestFirst = sortGroup.getCheckedRadioButtonId() == R.id.radio_newest;
            boolean containsLink = checkboxLink.isChecked();
            boolean todayOnly = checkboxToday.isChecked();
            boolean last7DaysOnly = checkboxLast7Days.isChecked();

            List<String> selectedYears = new ArrayList<>();
            for (CheckBox cb : yearCheckBoxes) {
                if (cb.isChecked()) selectedYears.add(cb.getText().toString());
            }

            if (listener != null) {
                listener.onFilterApplied(newestFirst, containsLink, todayOnly, last7DaysOnly, selectedYears, selectedStartDate, selectedEndDate);
            }
            dismiss();
        });

        buttonReset.setOnClickListener(v -> {
            sortGroup.check(R.id.radio_oldest);
            checkboxLink.setChecked(false);
            checkboxToday.setChecked(false);
            checkboxLast7Days.setChecked(false);
            for (CheckBox cb : yearCheckBoxes) cb.setChecked(false);
            buttonStartDate.setText("From: ");
            buttonEndDate.setText("To: ");
        });

        return view;
    }

    private void showDatePicker(boolean isStart) {
        Calendar calendar = Calendar.getInstance();

        Context themedContext = new ContextThemeWrapper(requireContext(), R.style.CustomDatePickerDialogTheme);
        DatePickerDialog dialog = new DatePickerDialog(themedContext, (view, year, month, dayOfMonth) -> {
            String selected = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            if (isStart) {
                selectedStartDate = selected;
                buttonStartDate.setText("From: " + selected);
            } else {
                selectedEndDate = selected;
                buttonEndDate.setText("To: " + selected);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        dialog.show();
    }

    private void populateYearsFromDatabase() {
        try {
            DatabaseAccess databaseAccess = new DatabaseAccess(getContext());
            databaseAccess.open();

            Cursor cursor = DatabaseAccess.db.rawQuery(
                    "SELECT DISTINCT SUBSTR(Date, 1, 4) AS year FROM Detections " +
                            "WHERE LENGTH(Date) >= 10 AND SUBSTR(Date, 1, 4) GLOB '[0-9][0-9][0-9][0-9]' " +
                            "ORDER BY year DESC", null);

            Set<String> years = new HashSet<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String year = cursor.getString(cursor.getColumnIndexOrThrow("year"));
                    if (year != null && year.length() == 4) {
                        years.add(year);
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }

            for (String year : years) {
                if (isAdded()) {
                    CheckBox cb = new CheckBox(requireContext());
                    cb.setText(year);
                    cb.setTextSize(14f);
                    cb.setPadding(10, 4, 10, 4);
                    yearContainer.addView(cb);
                    yearCheckBoxes.add(cb);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}