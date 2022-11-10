package com.evanemran.wolclient.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.evanemran.wolclient.model.Device;
import com.evanemran.wolclient.R;
import com.evanemran.wolclient.listener.AddListener;
import com.google.android.material.textfield.TextInputEditText;

public class AddDeviceDialog extends DialogFragment {
    private TextInputEditText editTextDeviceName, editTextDeviceIP, editTextDeviceMAC;
    private final AddListener listener;

    public AddDeviceDialog(AddListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_add_device, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextDeviceName = view.findViewById(R.id.editTextDeviceName);
        editTextDeviceIP = view.findViewById(R.id.editTextDeviceIP);
        editTextDeviceMAC = view.findViewById(R.id.editTextDeviceMAC);

        view.findViewById(R.id.button_add_device).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextDeviceName.getText().toString();
                String ip = editTextDeviceIP.getText().toString();
                String mac = editTextDeviceMAC.getText().toString();

                if(isValidate(name, ip, mac)) {
                    Device device = new Device();
                    device.setDeviceName(name);
                    device.setDeviceIp(ip);
                    device.setDeviceMac(mac);
                    listener.onAddClicked(device);
                    dismiss();
                }
            }
        });

        view.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private boolean isValidate(String name, String ip, String mac) {
        return !name.isEmpty() && !ip.isEmpty() && !mac.isEmpty();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            d.getWindow().setLayout(width, height);
        }
    }
}
