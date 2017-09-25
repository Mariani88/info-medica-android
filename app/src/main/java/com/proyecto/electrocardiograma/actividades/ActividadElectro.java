package com.proyecto.electrocardiograma.actividades;

import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.proyecto.electrocardiograma.R;
import com.proyecto.electrocardiograma.utilidades.VistaListener;
import com.proyecto.electrocardiograma.utilidades.BluetoothEncontradoReceiver;
import com.proyecto.electrocardiograma.utilidades.ControladorBluetooth;
import com.proyecto.electrocardiograma.utilidades.Graficador;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActividadElectro extends AppCompatActivity implements VistaListener {

    @BindView(R.id.grafico_electro)
    LineChart graficoElectro;
    @BindView(R.id.btn_empezar)
    Button btnPausar;

    private boolean enCurso = false;

    private Graficador graficador;

    private ControladorBluetooth bluetooth;
    private BluetoothEncontradoReceiver bluetoothEncontradoReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.actividad_electro);
        ButterKnife.bind(this);

        graficador = new Graficador(this, graficoElectro);
        bluetooth = new ControladorBluetooth(this, this);
        registrarBroadcastReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_terminal, menu);
        MenuItem menuConectar = menu.findItem(R.id.accion_conectar);
        MenuItem menuDesconectar = menu.findItem(R.id.accion_desconectar);
        if (bluetooth.estaConectado()) {
            menuConectar.setVisible(false);
            menuDesconectar.setVisible(true);
        } else {
            menuConectar.setVisible(true);
            menuDesconectar.setVisible(false);
        }
        return true;
    }

    private void registrarBroadcastReceiver() {
        bluetoothEncontradoReceiver = new BluetoothEncontradoReceiver(bluetooth);
        registerReceiver(bluetoothEncontradoReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(bluetoothEncontradoReceiver);
        bluetooth.detenerServicio();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.accion_conectar) {
            bluetooth.configurarBluetooth();
            bluetooth.buscarDispositivos();
            return true;
        } else if (id == R.id.accion_desconectar) {
            bluetooth.detenerServicio();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_empezar)
    public void listenerPausar() {

        if (bluetooth.estaConectado()) {
            if (enCurso) {
                btnPausar.setText(getString(R.string.reanudar));
                btnPausar.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_play), null, null, null);
                bluetooth.enviarComando("1");
                enCurso = false;
            } else {
                btnPausar.setText(getString(R.string.pausar));
                btnPausar.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_pausa), null, null, null);
                if (graficoElectro.getData() != null) {
                    graficoElectro.moveViewToX(graficoElectro.getData().getEntryCount());
                }
                bluetooth.enviarComando("0");
                enCurso = true;
            }
        } else {
            Toast.makeText(this, "Debes emparejar el bluetooth primero", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void actualizar(int valor) {
        if (enCurso) {
            graficador.agregarValor(valor);
            bluetooth.enviarComando("0");
        }
    }

    @Override
    public void actualizarToolbar(String nombre) {
        invalidateOptionsMenu();
        getSupportActionBar().setSubtitle(nombre);
    }
}