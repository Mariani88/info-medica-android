package com.proyecto.electrocardiograma.utilidades;

import android.content.Context;
import android.util.Log;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

/**
 * Created by martin on 9/18/17.
 */

public class ControladorBluetooth extends BluetoothSPP implements BluetoothSPP.OnDataReceivedListener, BluetoothSPP.BluetoothConnectionListener,
        BluetoothSPP.BluetoothStateListener {

    private static final String TAG = ControladorBluetooth.class.getSimpleName();

    private int reintentosConexion = 0;
    private boolean conectado = false;
    private boolean buscandoDispositivos = false;
    private VistaListener listener;

    public ControladorBluetooth(Context context, VistaListener listener) {
        super(context);
        this.listener = listener;
    }

    public boolean estaConectado() {
        return conectado;
    }

    public void buscarDispositivos() {
        Log.d(TAG, "Buscando dispositivos bluetooth...");
        buscandoDispositivos = true;
        startDiscovery();
    }

    public void detenerBusqueda() {
        Log.d(TAG, "Cancelando búsqueda de dispositivos bluetooth...");
        buscandoDispositivos = false;
        cancelDiscovery();
    }

    public void conectar(String direccion) {
        connect(direccion);
    }

    private void desconectar() {

        Log.e(TAG, "Desconectando...");

        disconnect();

        setBluetoothConnectionListener(null);
        setBluetoothStateListener(null);
        stopService();
        getBluetoothAdapter().disable();

        conectado = false;
        Log.e(TAG, "DESCONECTADO");
    }

    public void enviarComando(String datos) {
        send(datos, true);
    }

    public void configurarBluetooth() {

        if (isBluetoothAvailable()) {

            if (!isBluetoothEnabled()) {
                enable();
            }

            disconnect();

            setBluetoothConnectionListener(this);
            setBluetoothStateListener(this);
            setOnDataReceivedListener(null);
            setOnDataReceivedListener(this);

            setupService();
            startService(BluetoothState.DEVICE_OTHER);
        }
    }

    @Override
    public void onDataReceived(byte[] data, String mensajeRecibido) {
        Log.d(TAG, "onDataReceived: " + mensajeRecibido);
        listener.actualizar(Integer.valueOf(mensajeRecibido));
    }

    @Override
    public void onDeviceConnected(String nombre, String direccion) {
        Log.d(TAG, "Dispositivo conectado -> Nombre: " + nombre + ", MAC: " + direccion);
    }

    @Override
    public void onDeviceDisconnected() {
        desconectar();
    }

    @Override
    public void onDeviceConnectionFailed() {

        reintentosConexion++;
        Log.d(TAG, "Reintentos: " + reintentosConexion);

        if (reintentosConexion == 3) {
            reintentosConexion = 0;
            desconectar();
        } else {
            Log.d(TAG, "Reintentando...");
            buscarDispositivos();
        }
    }

    @Override
    public void onServiceStateChanged(int estado) {
        switch (estado) {
            case BluetoothState.STATE_NONE:
                Log.e(TAG, "DESCONECTADO");
                listener.actualizarToolbar("DESCONECTADO");
                break;
            case BluetoothState.STATE_LISTEN:
                Log.e(TAG, "BUSCANDO...");
                listener.actualizarToolbar("BUSCANDO..");
                break;
            case BluetoothState.STATE_CONNECTING:
                listener.actualizarToolbar("CONECTANDO..");
                Log.e(TAG, "CONECTANDO...");
                break;
            case BluetoothState.STATE_CONNECTED:
                listener.actualizarToolbar("CONECTADO");
                Log.e(TAG, "CONECTADO");
                conectado = true;
                break;
        }
    }

    public void detenerServicio() {
        stopService();
    }
}
