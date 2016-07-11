package nachar.serial.cl.serialportdevlab;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button start = (Button) findViewById(R.id.startButton);
        if (start != null) {
            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    connection();
                }
            });
        }



    }

    private void connection(){
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        UsbDevice device;
        UsbDeviceConnection connection;
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();

        if(!usbDevices.isEmpty())
        {
            boolean keep = true;
            for(Map.Entry<String, UsbDevice> entry : usbDevices.entrySet())
            {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                int devicePID = device.getProductId();
                if(deviceVID != 0x1d6b || (devicePID != 0x0001 || devicePID != 0x0002 || devicePID != 0x0003))
                {
                    //Pedir permisos para usar la conección USB
                    if(!usbManager.hasPermission(device))
                    {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                        alertDialogBuilder.setMessage("Solicitando Acceso");
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(
                                "ANDROID.USB"), 0);
                        usbManager.requestPermission(device, pi);
                    }
                    connection = usbManager.openDevice(device);
                    keep = false;

                    int iface = 0;

                    if (connection != null){
                        iniciarConnection(device, connection, iface);
                    }


                }else
                {
                    connection = null;
                    device = null;
                }

                if(!keep)
                    break;
            }
        }else {
            toastMaker("No Reconoce el USB");
        }
    }

    private UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback()
    {
        @Override
        public void onReceivedData(byte[] arg0)
        {
            toastMaker("Callback!!!!!!");
        }
    };

    private void iniciarConnection (UsbDevice device, UsbDeviceConnection connection, int iface){
        //toastMaker("TEST 1: "+ connection);
        UsbSerialDevice serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection, iface);
        if(serialPort != null) {
            //toastMaker("TEST 2");
            if(serialPort.open()) {
                serialPort.setBaudRate(115200);
                toastMaker("TEST 4");
                serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                toastMaker("TEST 5");
                serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                toastMaker("TEST 6");
                serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                toastMaker("TEST 7");
                serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                toastMaker("TEST 8");
                serialPort.read(mCallback);
            }else {
                // Serial port could not be opened, maybe an I/O error or it CDC driver was chosen it does not really fit
            }
        }else{
            // No driver for given device, even generic CDC driver could not be loaded
        }
    }



    private void toastMaker(String toastString){
        Toast toast2 = Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_SHORT);
        //toast2.setGravity(Gravity.CENTER|Gravity.LEFT,0,0);
        toast2.show();
    }



}