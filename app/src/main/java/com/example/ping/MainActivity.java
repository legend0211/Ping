package com.example.ping;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    EditText ipAddressEditText;
    EditText numberEditText;
    Button pingButton;
    TextView resultTextView;

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipAddressEditText = findViewById(R.id.editTextIpAddress);
        numberEditText = findViewById(R.id.editTextNumber);
        pingButton = findViewById(R.id.buttonPing);
        resultTextView = findViewById(R.id.textViewResult);

        pingButton.setOnClickListener(v -> {
            if(TextUtils.isEmpty(ipAddressEditText.getText())) {
                Toast.makeText(this, "Please enter an IP", Toast.LENGTH_SHORT).show();
            }
            else {
                String ipAddress = ipAddressEditText.getText().toString();
                int n;
                if (TextUtils.isEmpty(numberEditText.getText())) {
                    n = 4;
                } else {
                    n = Integer.parseInt(numberEditText.getText().toString());
                }
                executorService.execute(() -> performPing(ipAddress, n));
            }
        });
    }

    private void performPing(String ipAddress, int n) {
        try {
            Process process = Runtime.getRuntime().exec("/system/bin/ping -c " + n + " " + ipAddress);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                runOnUiThread(() -> resultTextView.setText(output.toString()));
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println("Error: " + e.getMessage());
            Toast.makeText(this, "Unreachable IP", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
