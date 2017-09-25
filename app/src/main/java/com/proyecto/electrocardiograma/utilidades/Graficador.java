package com.proyecto.electrocardiograma.utilidades;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.proyecto.electrocardiograma.R;

/**
 * Created by martin on 9/14/17.
 */

public class Graficador implements OnChartValueSelectedListener {

    private LineChart graficoElectro;
    private Context context;

    public Graficador(Context context, LineChart graficoElectro) {
        this.graficoElectro = graficoElectro;
        this.context = context;
        inicializarGraficoElectro();
    }

    private void inicializarGraficoElectro() {

        graficoElectro.setOnChartValueSelectedListener(this);
        graficoElectro.getDescription().setEnabled(false);
        graficoElectro.setTouchEnabled(true);
        graficoElectro.setDragEnabled(true);
        graficoElectro.setScaleEnabled(true);
        graficoElectro.setDrawGridBackground(true);
        graficoElectro.setPinchZoom(true);
        graficoElectro.setGridBackgroundColor(ContextCompat.getColor(context, R.color.fondo));

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        graficoElectro.setData(data);
        graficoElectro.getLegend().setEnabled(false);

        XAxis xl = graficoElectro.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);
        xl.setGridColor(Color.RED);
//        xl.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                return value + "ms";
//            }
//        });

        YAxis leftAxis = graficoElectro.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaximum(1100f);
        leftAxis.setAxisMinimum(-10f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.RED);

        YAxis rightAxis = graficoElectro.getAxisRight();
        rightAxis.setEnabled(false);
    }

    public void agregarValor(float valor) {

        LineData data = graficoElectro.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = crearConjuntoValores();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), valor), 0);
            data.notifyDataChanged();

            graficoElectro.notifyDataSetChanged();
            graficoElectro.setVisibleXRangeMaximum(100);
            graficoElectro.moveViewToX(data.getEntryCount());
        }
    }

    private LineDataSet crearConjuntoValores() {

        LineDataSet set = new LineDataSet(null, null);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setColor(Color.BLACK);
        set.setLineWidth(1f);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setCubicIntensity(0.1f);
        return set;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
