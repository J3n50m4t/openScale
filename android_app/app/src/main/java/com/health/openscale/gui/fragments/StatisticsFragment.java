/* Copyright (C) 2014  olie.xdev <olie.xdev@googlemail.com>
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

package com.health.openscale.gui.fragments;

import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.health.openscale.R;
import com.health.openscale.core.OpenScale;
import com.health.openscale.core.datatypes.ScaleMeasurement;
import com.health.openscale.core.datatypes.ScaleUser;
import com.health.openscale.core.utils.Converters;
import com.health.openscale.core.utils.DateTimeHelpers;
import com.health.openscale.gui.views.BMIMeasurementView;
import com.health.openscale.gui.views.BoneMeasurementView;
import com.health.openscale.gui.views.FatMeasurementView;
import com.health.openscale.gui.views.HipMeasurementView;
import com.health.openscale.gui.views.LBMMeasurementView;
import com.health.openscale.gui.views.MeasurementView;
import com.health.openscale.gui.views.MeasurementViewSettings;
import com.health.openscale.gui.views.MuscleMeasurementView;
import com.health.openscale.gui.views.WaistMeasurementView;
import com.health.openscale.gui.views.WaterMeasurementView;
import com.health.openscale.gui.views.WeightMeasurementView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.health.openscale.gui.views.MeasurementView.MeasurementViewMode.STATISTIC;

public class StatisticsFragment extends Fragment implements FragmentUpdateListener {

    private View statisticsView;

    private TextView txtGoalWeight;
    private TextView txtGoalDiff;
    private TextView txtGoalDayLeft;

    private TextView txtLabelGoalWeight;
    private TextView txtLabelGoalDiff;
    private TextView txtLabelDayLeft;

    private ScaleUser currentScaleUser;
    private ScaleMeasurement lastScaleMeasurement;

    private ArrayList <MeasurementView> viewMeasurementsListWeek;
    private ArrayList <MeasurementView> viewMeasurementsListMonth;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        statisticsView = inflater.inflate(R.layout.fragment_statistics, container, false);

        // Set android:tint="?attr/editTextColor" programmatically as setting it in xml layout
        // throws an exception on API 19.
        int color = new EditText(getContext()).getCurrentTextColor();
        for (int id : new int[]{R.id.imageGoalWeight, R.id.imageGoalDiff, R.id.imageDayLeft}) {
            ImageView image = statisticsView.findViewById(id);
            image.setColorFilter(color);
        }

        txtGoalWeight = statisticsView.findViewById(R.id.txtGoalWeight);
        txtGoalDiff = statisticsView.findViewById(R.id.txtGoalDiff);
        txtGoalDayLeft = statisticsView.findViewById(R.id.txtGoalDayLeft);

        txtLabelGoalWeight = statisticsView.findViewById(R.id.txtLabelGoalWeight);
        txtLabelGoalDiff = statisticsView.findViewById(R.id.txtLabelGoalDiff);
        txtLabelDayLeft = statisticsView.findViewById(R.id.txtLabelDayLeft);

        TableLayout tableWeekAveragesLayoutColumnA = statisticsView.findViewById(R.id.tableWeekAveragesLayoutColumnA);
        TableLayout tableWeekAveragesLayoutColumnB = statisticsView.findViewById(R.id.tableWeekAveragesLayoutColumnB);
        TableLayout tableMonthAveragesLayoutColumnA = statisticsView.findViewById(R.id.tableMonthAveragesLayoutColumnA);
        TableLayout tableMonthAveragesLayoutColumnB = statisticsView.findViewById(R.id.tableMonthAveragesLayoutColumnB);

        viewMeasurementsListWeek = new ArrayList<>();

        viewMeasurementsListWeek.add(new WeightMeasurementView(statisticsView.getContext()));
        viewMeasurementsListWeek.add(new WaterMeasurementView(statisticsView.getContext()));
        viewMeasurementsListWeek.add(new MuscleMeasurementView(statisticsView.getContext()));
        viewMeasurementsListWeek.add(new LBMMeasurementView(statisticsView.getContext()));
        viewMeasurementsListWeek.add(new FatMeasurementView(statisticsView.getContext()));
        viewMeasurementsListWeek.add(new BoneMeasurementView(statisticsView.getContext()));
        viewMeasurementsListWeek.add(new WaistMeasurementView(statisticsView.getContext()));
        viewMeasurementsListWeek.add(new HipMeasurementView(statisticsView.getContext()));

        final int paddingBottom = 10;

        int i=0;

        for (MeasurementView measurement : viewMeasurementsListWeek) {
            measurement.setEditMode(STATISTIC);

            if (measurement.getSettings().isEnabled()) {
                measurement.setVisible(true);
                measurement.setPadding(-1, -1, -1, paddingBottom);
                if ((i % 2) == 0) {
                    tableWeekAveragesLayoutColumnA.addView(measurement);
                } else {
                    tableWeekAveragesLayoutColumnB.addView(measurement);
                }
                i++;
            }
        }

        viewMeasurementsListMonth = new ArrayList<>();

        viewMeasurementsListMonth.add(new WeightMeasurementView(statisticsView.getContext()));
        viewMeasurementsListMonth.add(new WaterMeasurementView(statisticsView.getContext()));
        viewMeasurementsListMonth.add(new MuscleMeasurementView(statisticsView.getContext()));
        viewMeasurementsListMonth.add(new LBMMeasurementView(statisticsView.getContext()));
        viewMeasurementsListMonth.add(new FatMeasurementView(statisticsView.getContext()));
        viewMeasurementsListMonth.add(new BoneMeasurementView(statisticsView.getContext()));
        viewMeasurementsListMonth.add(new WaistMeasurementView(statisticsView.getContext()));
        viewMeasurementsListMonth.add(new HipMeasurementView(statisticsView.getContext()));

        i=0;

        for (MeasurementView measurement : viewMeasurementsListMonth) {
            measurement.setEditMode(STATISTIC);

            if (measurement.getSettings().isEnabled()) {
                measurement.setVisible(true);
                measurement.setPadding(-1, -1, -1, paddingBottom);
                if ((i % 2) == 0) {
                    tableMonthAveragesLayoutColumnA.addView(measurement);
                } else {
                    tableMonthAveragesLayoutColumnB.addView(measurement);
                }
                i++;
            }
        }

        OpenScale.getInstance().registerFragment(this);

        return statisticsView;
    }

    @Override
    public void onDestroyView() {
        OpenScale.getInstance().unregisterFragment(this);
        super.onDestroyView();
    }

    @Override
    public void updateOnView(List<ScaleMeasurement> scaleMeasurementList) {
        currentScaleUser = OpenScale.getInstance().getSelectedScaleUser();

        if (scaleMeasurementList.isEmpty()) {
            lastScaleMeasurement = new ScaleMeasurement();
            lastScaleMeasurement.setUserId(currentScaleUser.getId());
            lastScaleMeasurement.setWeight(currentScaleUser.getInitialWeight());
        } else {
            lastScaleMeasurement = scaleMeasurementList.get(0);
        }

        updateStatistics(scaleMeasurementList);
        updateGoal();
    }

    private void updateGoal() {
        final Converters.WeightUnit unit = currentScaleUser.getScaleUnit();

        ScaleMeasurement goalScaleMeasurement = new ScaleMeasurement();
        goalScaleMeasurement.setUserId(currentScaleUser.getId());
        goalScaleMeasurement.setWeight(currentScaleUser.getGoalWeight());

        txtGoalWeight.setText(String.format("%.1f %s",
                Converters.fromKilogram(goalScaleMeasurement.getWeight(), unit),
                unit.toString()));

        txtGoalDiff.setText(String.format("%.1f %s",
                Converters.fromKilogram(goalScaleMeasurement.getWeight() - lastScaleMeasurement.getWeight(), unit),
                unit.toString()));

        Calendar goalCalendar = Calendar.getInstance();
        goalCalendar.setTime(currentScaleUser.getGoalDate());
        int days = Math.max(0, DateTimeHelpers.daysBetween(Calendar.getInstance(), goalCalendar));
        txtGoalDayLeft.setText(getResources().getQuantityString(R.plurals.label_days, days, days));

        boolean isBmiEnabled = new MeasurementViewSettings(
                PreferenceManager.getDefaultSharedPreferences(getActivity()), BMIMeasurementView.KEY)
                .isEnabled();
        final float goalBmi = goalScaleMeasurement.getBMI(currentScaleUser.getBodyHeight());

        txtLabelGoalWeight.setText(
                isBmiEnabled
                        ? Html.fromHtml(String.format(
                                "%s<br><font color='grey'><small>%s: %.1f</small></font>",
                                getResources().getString(R.string.label_goal_weight),
                                getResources().getString(R.string.label_bmi),
                                goalBmi))
                        : getResources().getString(R.string.label_goal_weight));

        txtLabelGoalDiff.setText(
                isBmiEnabled
                        ? Html.fromHtml(String.format(
                                "%s<br><font color='grey'><small>%s: %.1f</small></font>",
                                getResources().getString(R.string.label_weight_difference),
                                getResources().getString(R.string.label_bmi),
                                lastScaleMeasurement.getBMI(currentScaleUser.getBodyHeight()) - goalBmi))
                        : getResources().getString(R.string.label_weight_difference));

        txtLabelDayLeft.setText(
                Html.fromHtml(String.format(
                        "%s<br><font color='grey'><small>%s %s</small></font>",
                        getResources().getString(R.string.label_days_left),
                        getResources().getString(R.string.label_goal_date_is),
                        DateFormat.getDateInstance(DateFormat.LONG).format(currentScaleUser.getGoalDate()))));
    }

    private void updateStatistics(List<ScaleMeasurement> scaleMeasurementList) {

        Calendar histDate = Calendar.getInstance();
        Calendar weekPastDate = Calendar.getInstance();
        Calendar monthPastDate = Calendar.getInstance();

        weekPastDate.setTime(lastScaleMeasurement.getDateTime());
        weekPastDate.add(Calendar.DATE, -7);

        monthPastDate.setTime(lastScaleMeasurement.getDateTime());
        monthPastDate.add(Calendar.DATE, -30);

        int weekSize = 0;
        int monthSize = 0;

        ScaleMeasurement averageWeek = new ScaleMeasurement();
        ScaleMeasurement averageMonth = new ScaleMeasurement();

        for (ScaleMeasurement measurement : scaleMeasurementList) {
            histDate.setTime(measurement.getDateTime());

            if (weekPastDate.before(histDate)) {
                averageWeek.add(measurement);
                weekSize++;
            }

            if (monthPastDate.before(histDate)) {
                averageMonth.add(measurement);
                monthSize++;
            }
        }

        if (weekSize > 0) {
            averageWeek.divide(weekSize);
        }
        if (monthSize > 0) {
            averageMonth.divide(monthSize);
        }

        for (MeasurementView measurement : viewMeasurementsListWeek) {
            measurement.loadFrom(averageWeek, null);
        }

        for (MeasurementView measurement : viewMeasurementsListMonth) {
            measurement.loadFrom(averageMonth, null);
        }
    }
}
