package com.patatascrucks.mobile.model;

import android.database.SQLException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.patatascrucks.mobile.R;
import com.patatascrucks.mobile.database.Cliente;
import com.patatascrucks.mobile.database.Vendedor;
import com.patatascrucks.mobile.database.Vendedor_Cliente;

import java.util.Locale;

public class Client extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        ActionBar appBar = getSupportActionBar();
        assert appBar != null;
        appBar.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText txtID = findViewById(R.id.txtID);
                EditText txtName = findViewById(R.id.txtName);
                EditText txtPhone = findViewById(R.id.txtPhone);
                EditText txtEmail = findViewById(R.id.txtEmail);
                EditText txtLocation = findViewById(R.id.txtLocation);

                boolean hasError = false;

                if (!validateEntry(txtID, (TextInputLayout) findViewById(R.id.lblID))) {
                    hasError = true;
                }
                if (!validateEntry(txtName, (TextInputLayout) findViewById(R.id.lblName))) {
                    hasError = true;
                }
                if (!validateEntry(txtPhone, (TextInputLayout) findViewById(R.id.lblNumber))) {
                    hasError = true;
                }
                if (!validateEntry(txtEmail, (TextInputLayout) findViewById(R.id.lblEmail))) {
                    hasError = true;
                }
                if (!validateEntry(txtLocation, (TextInputLayout) findViewById(R.id.lblLocation))) {
                    hasError = true;
                }

                if (!hasError) {
                    try {
                        new Cliente(getApplicationContext()).insert(
                                txtID.getText().toString(),
                                txtName.getText().toString(),
                                txtLocation.getText().toString(),
                                txtEmail.getText().toString(),
                                txtPhone.getText().toString());
                        new Vendedor_Cliente(getApplicationContext()).insert(new Vendedor(getApplicationContext()).select(Vendedor.idVendedor, null, null, null, null).get(0).get(0), txtID.getText().toString());
                        Toast.makeText(getApplicationContext(), String.format(Locale.getDefault(), "Cliente %s grabado correctamente!", txtID.getText()), Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } catch (SQLException e) {
                        Toast.makeText(getApplicationContext(), String.format(Locale.getDefault(), "Error al grabar el cliente: %s !", txtID.getText()), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private boolean validateEntry(EditText editText, TextInputLayout layout) {
        if (editText.getText().length() > 0) {
            layout.setErrorEnabled(false);
            return true;
        } else {
            layout.setError("El campo no puede estar vacío.");
            return false;
        }
    }
}