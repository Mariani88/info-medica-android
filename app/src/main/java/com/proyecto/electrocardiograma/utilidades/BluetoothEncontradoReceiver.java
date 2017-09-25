package com.proyecto.electrocardiograma.utilidades;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by martin on 9/18/17.
 */

public class BluetoothEncontradoReceiver extends BroadcastReceiver {

    private ControladorBluetooth bluetooh;

    public BluetoothEncontradoReceiver(ControladorBluetooth controladorBluetooth) {
        this.bluetooh = controladorBluetooth;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice dispositivoEncontrado = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            bluetooh.detenerBusqueda();
            bluetooh.conectar(dispositivoEncontrado.getAddress());
        }
    }
}
