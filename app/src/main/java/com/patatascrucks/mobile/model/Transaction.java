package com.patatascrucks.mobile.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.print.PrintManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.patatascrucks.mobile.R;
import com.patatascrucks.mobile.database.Cliente;
import com.patatascrucks.mobile.database.Grupo;
import com.patatascrucks.mobile.database.Producto;
import com.patatascrucks.mobile.database.Registro;
import com.patatascrucks.mobile.database.Registro_Adicional;
import com.patatascrucks.mobile.database.Registro_Producto;
import com.patatascrucks.mobile.database.Registro_Vendedor_Cliente;
import com.patatascrucks.mobile.database.Vendedor;
import com.patatascrucks.mobile.database.Vendedor_Cliente;
import com.patatascrucks.mobile.templates.AdditionalTransactionsFragment;
import com.patatascrucks.mobile.print.CustomPrintAdapter;
import com.patatascrucks.mobile.util.SingleSelectionListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Transaction extends AppCompatActivity implements View.OnClickListener, AdditionalTransactionsFragment.OnFragmentInteractionListener {

    static final int CLIENT_REQUEST_CODE = 1;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private GestureDetectorCompat mDetector;
    private List<ArrayList<String>> myDataList;
    private List<ArrayList<String>> mClientsList;
    private GridLayout mNumpadLayout;
    private BottomSheetBehavior mNumpad;
    private ArrayList<String> mVendedor;
    private String mCliente;
    private String mCurrentDate;
    private double mReg;
    private Spinner mClientes;
    private ArrayAdapter mClientsAdapter;
    private CheckBox mIsCredit;
    private TextView mTxtTotal;
    private AdditionalTransactionsFragment mAdditionalTransaction;
    private String mTitle;
    private String mDocument;
    private String mAdditional;
    private double mRet = 0;
    private double mDesc = 0;
    private int currentPos;
    private boolean isSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        mTitle = getIntent().getStringExtra(getString(R.string.app_name));
        setTitle(mTitle);
        if (mTitle.equals(getString(R.string.bill))) {
            mDocument = "factCont";
        } else if (mTitle.equals(getString(R.string.note))) {
            mDocument = "nota";
        } else if (mTitle.equals(getString(R.string.dispatch))) {
            mDocument = "desp";
        } else if (mTitle.equals(getString(R.string.returns))) {
            mDocument = "dev";
        } else if (mTitle.equals(getString(R.string.change))) {
            mDocument = "camb";
        } else if (mTitle.equals(getString(R.string.returns))) {
            mDocument = "obseq";
        }

        if (mDocument.contains("desp") || mDocument.contains("dev")) findViewById(R.id.client_data).setVisibility(View.GONE);

        mVendedor = new Vendedor(getApplicationContext()).select(Vendedor.idVendedor + ", " + Vendedor.vendedor, null, null, null, null).get(0);

        mCliente = "9999999999";
        mClientes = findViewById(R.id.clients);
        mClientsList = new Cliente(getApplicationContext()).getExecuteResult("SELECT Cliente." + Cliente.idCliente + ", " + Cliente.cliente + " FROM Cliente JOIN Vendedor_Cliente ON Cliente.idCliente = Vendedor_Cliente.idCliente WHERE " + Vendedor_Cliente.idVendedor + " = '" + mVendedor.get(0) + "'");
        mClientsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mClientsList);
        mClientes.setAdapter(mClientsAdapter);

        if (mClientes.getAdapter().getCount() > 0) mClientes.setSelection(0);
        mClientes.requestFocus();

        mCurrentDate = String.format(Locale.getDefault(), "%tF", Calendar.getInstance());

        ArrayList<ArrayList<String>> registro = new Registro(getApplicationContext()).getExecuteResult("SELECT " + Registro.idRegistro + " FROM Registro WHERE " + Registro.idPrefijo + " = '" + mVendedor.get(1) + "' AND " + Registro.idDocumento + " = '" + mDocument + "'");
        mReg = registro.size() > 0 ? registro.size() + 1 : 0;
        ((EditText) findViewById(R.id.transaction_number)).setText(String.format(Locale.getDefault(), "%d", (long) mReg));

        mIsCredit = findViewById(R.id.transaction_credit);
        if (!mDocument.equals("factCont")) mIsCredit.setVisibility(View.INVISIBLE);

        ActionBar appBar = getSupportActionBar();
        assert appBar != null;
        appBar.setDisplayHomeAsUpEnabled(true);

        mNumpadLayout = findViewById(R.id.numpad);
        assert mNumpadLayout != null;
        mNumpad = BottomSheetBehavior.from(mNumpadLayout);
        mNumpad.setState(BottomSheetBehavior.STATE_HIDDEN);
        for (int i = 0; i < mNumpadLayout.getChildCount(); i++) {
            Button btn = (Button) mNumpadLayout.getChildAt(i);
            btn.setOnClickListener(this);
        }
        mNumpad.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset == 0 && isSelected) {
                    isSelected = ((SingleSelectionListAdapter) mAdapter).toggleSelection(currentPos);
                }
            }
        });

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView = findViewById(R.id.recycler_list);
        assert mRecyclerView != null;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        myDataList = new Producto(this).getExecuteResult("SELECT \"\", "
                    + Producto.producto + ", "
                    + Producto.precioU + ", "
                    + "\"0.00\", "
                    + "Producto." + Producto.idGrupo + ", "
                    + Grupo.color + " "
                + "FROM Producto "
                + "JOIN Grupo "
                    + "ON Producto.idGrupo = Grupo.idGrupo "
                + "ORDER BY "
                    +  Producto.id + " ASC");

        mAdapter = new TransactionListAdapter(getApplicationContext(), myDataList);
        mRecyclerView.setAdapter(mAdapter);

        mDetector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                onClick(mRecyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY()));
                return super.onSingleTapConfirmed(motionEvent);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (isSelected) {
                    isSelected = ((SingleSelectionListAdapter) mAdapter).toggleSelection(currentPos);
                    mNumpad.setState(isSelected ? BottomSheetBehavior.STATE_EXPANDED : BottomSheetBehavior.STATE_HIDDEN);
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                mDetector.onTouchEvent(e);
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        mTxtTotal = findViewById(R.id.txtTotal);
        assert mTxtTotal != null;
        mTxtTotal.setText(String.format(Locale.getDefault(), "$ %.2f", 0.0));

        ImageButton mAddClient = findViewById(R.id.add_client);
        mAddClient.setOnClickListener(this);

        FloatingActionButton mAddTransaction = findViewById(R.id.fab);
        mAddTransaction.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trans, menu);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (mIsCredit.getVisibility() == View.VISIBLE) {
            mDocument = mIsCredit.isChecked() ? "factCred" : "factCont";
        }

        if (id != android.R.id.home) {
            mCliente = ((ArrayList<String>) mClientes.getSelectedItem()).get(0);
        }

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_print:
                doPrint();
                return true;
            case R.id.action_save:
                doSave();
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isSelected) {
            isSelected = ((SingleSelectionListAdapter) mAdapter).toggleSelection(currentPos);
            mNumpad.setState(isSelected ? BottomSheetBehavior.STATE_EXPANDED : BottomSheetBehavior.STATE_HIDDEN);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        SingleSelectionListAdapter adapter = (SingleSelectionListAdapter) mAdapter;

        if (view.getId() == R.id.transaction_list_item) {
            currentPos = mRecyclerView.getChildAdapterPosition(view);
            isSelected = adapter.toggleSelection(currentPos);
        } else if (view.getId() == R.id.add_client) {
            Intent i = new Intent(getApplicationContext(), Client.class);
            startActivityForResult(i, CLIENT_REQUEST_CODE);
        } else if (view.getId() == R.id.fab) {
            mAdditionalTransaction = AdditionalTransactionsFragment.newInstance(R.id.fab);
            mAdditionalTransaction.show(getSupportFragmentManager(), getString(R.string.PatatasApp));
        } else if (((GridLayout) view.getParent()).getId() == mNumpadLayout.getId()) {
            String num = ((Button) view).getText().toString();
            if (isSelected) {
                if (view.getId() == R.id.btnR) {
                    isSelected = adapter.toggleSelection(currentPos);
                } else if (view.getId() == R.id.btnX) {
                    adapter.setValueAt("", currentPos);
                } else {
                    adapter.setValueAt(adapter.getValueAt(currentPos, 0) + num, currentPos);
                }
            }
        }
        mNumpad.setState(isSelected ? BottomSheetBehavior.STATE_EXPANDED : BottomSheetBehavior.STATE_HIDDEN);
        mTxtTotal.setText(String.format(Locale.getDefault(), "$ %.2f", adapter.getTotalValue()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CLIENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mClientsList = new Cliente(getApplicationContext()).getExecuteResult("SELECT Cliente." + Cliente.idCliente + ", " + Cliente.cliente + " FROM Cliente JOIN Vendedor_Cliente ON Cliente.idCliente = Vendedor_Cliente.idCliente WHERE " + Vendedor_Cliente.idVendedor + " = '" + mVendedor.get(0) + "'");
                mClientsAdapter.notifyDataSetChanged();
                mClientes.invalidate();
            }
        }
    }

    private void doPrint() {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        final Paint paint = new Paint();

        printManager.print(
            getString(R.string.app_name) + " Document",
            new CustomPrintAdapter(getApplicationContext()) {
                @Override
                public void onDraw(Canvas canvas) {
                    int margin = 54;
                    int titleLine = 72;
                    int headerLine = 95;
                    int bodyLine = 150;

                    paint.setColor(Color.BLACK);
                    paint.setTypeface(Typeface.MONOSPACE);
                    paint.setTextSize(22);
                    canvas.drawText(mTitle, margin, titleLine, paint);
                    paint.setTextSize(18);
                    canvas.drawText(String.format(Locale.getDefault(), "#: %s", mReg), margin, headerLine, paint);
                    canvas.drawText(String.format(Locale.getDefault(), "Cliente: %s", mCliente), margin, headerLine + 15, paint);
                    canvas.drawText(String.format(Locale.getDefault(), "Fecha: %s", mCurrentDate), margin, headerLine + 30, paint);
                    paint.setTextSize(16);
                    for (ArrayList<String> item : myDataList) {
                        if (item.get(0).length() > 0) canvas.drawText(
                                String.format(
                                        Locale.getDefault(),
                                        "%4s %-30s %-7.2f %7.2f",
                                        item.get(0),
                                        item.get(4) + " " + item.get(1),
                                        Double.parseDouble(item.get(2)) / (mTitle.equals(getString(R.string.bill)) ? 1.14 : 1),
                                        Double.parseDouble(item.get(3)) / (mTitle.equals(getString(R.string.bill)) ? 1.14 : 1)),
                                margin,
                                bodyLine += 20,
                                paint);
                    }
                    canvas.drawText("\n", margin, bodyLine += 20, paint);
                    paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
                    if (mDesc != 0) {
                        paint.setTypeface(Typeface.MONOSPACE);
                        canvas.drawText(
                                String.format(
                                        Locale.getDefault(),
                                        "%4s %-30s %-7s %7.2f",
                                        "",
                                        "",
                                        "SUB",
                                        Double.parseDouble(mTxtTotal.getText().toString().replace("$", "").trim()) / 1.14),
                                margin,
                                bodyLine += 20, paint);
                        paint.setTypeface(Typeface.MONOSPACE);
                        canvas.drawText(
                                String.format(
                                        Locale.getDefault(),
                                        "%4s %-30s %-7s %7.2f",
                                        "",
                                        "",
                                        "DESC " + mDesc * 100 + "%%",
                                        Double.parseDouble(mTxtTotal.getText().toString().replace("$", "").trim()) * mDesc),
                                margin,
                                bodyLine += 20, paint);
                    }
                    if (mTitle.equals(getString(R.string.bill))) {
                        paint.setTypeface(Typeface.MONOSPACE);
                        canvas.drawText(
                                String.format(
                                        Locale.getDefault(),
                                        "%4s %-30s %-7s %7.2f",
                                        "",
                                        "",
                                        "SUB",
                                        Double.parseDouble(mTxtTotal.getText().toString().replace("$", "").trim()) * (1 - mDesc) / 1.14),
                                margin,
                                bodyLine += 20, paint);
                        paint.setTypeface(Typeface.MONOSPACE);
                        canvas.drawText(
                                String.format(
                                        Locale.getDefault(),
                                        "%4s %-30s %-7s %7.2f",
                                        "",
                                        "",
                                        "IVA",
                                        Double.parseDouble(mTxtTotal.getText().toString().replace("$", "").trim()) * (1 - mDesc) * 0.14),
                                margin,
                                bodyLine += 20, paint);
                    }
                    canvas.drawText(
                            String.format(
                                    Locale.getDefault(),
                                    "%4s %-30s %-7s %7.2f",
                                    "",
                                    "",
                                    "TOTAL",
                                    Double.parseDouble(mTxtTotal.getText().toString().replace("$", "").trim()) * (1 - mDesc)),
                            margin,
                            bodyLine + 20, paint);
                    if (mRet != 0) {
                        canvas.drawText(
                                String.format(
                                        Locale.getDefault(),
                                        "%4s %-30s %-7s %7.2f",
                                        "",
                                        "",
                                        "RETENIDO",
                                        mRet),
                                margin,
                                bodyLine + 20, paint);
                    }
                }
            },
        null);
    }

    private void doSave() {
        try {
            EditText txtTransactionNumber = findViewById(R.id.transaction_number);

            if (validateEntry(txtTransactionNumber, (TextInputLayout) findViewById(R.id.lblTransaction_number))) {

                mReg = Double.parseDouble(txtTransactionNumber.getText().toString());

                new Registro(getApplicationContext()).insert(mDocument, mReg, mVendedor.get(1), mCurrentDate, mDocument.equals("factCred") ? "Pendiente" : "");

                try {
                    new Registro_Vendedor_Cliente(getApplicationContext()).insert(mReg, mDocument, mVendedor.get(1), mCurrentDate, mVendedor.get(0), mCliente);

                    try {
                        Registro_Producto registro_producto = new Registro_Producto(getApplicationContext());
                        for (ArrayList<String> item : myDataList) {
                            if (item.get(0).length() > 0) {
                                registro_producto.insert(mReg, mDocument, mVendedor.get(1), mCurrentDate, myDataList.indexOf(item) + 1, Integer.parseInt(item.get(0)), Double.parseDouble(item.get(2)));
                            }
                        }
                        if (mDesc != 0) new Registro_Adicional(getApplicationContext()).insert(mDocument, mReg, mVendedor.get(1), mCurrentDate, mCurrentDate, "desc", Double.parseDouble(mTxtTotal.getText().toString().replace("$", "").trim()) * mDesc);
                        if (mRet != 0) new Registro_Adicional(getApplicationContext()).insert(mDocument, mReg, mVendedor.get(1), mCurrentDate, mCurrentDate, "ret", mRet);
                        Toast.makeText(this, String.format(Locale.getDefault(), mTitle + " #%d grabado correctamente!", (long) mReg), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(this, String.format(Locale.getDefault(), mTitle + " #%d no pudo ser grabado!\n%s", (long) mReg, e.getMessage()), Toast.LENGTH_LONG).show();
                        new Registro_Vendedor_Cliente(getApplicationContext()).execute("DELETE FROM Registro_Vendedor_Cliente " +
                                "WHERE " + Registro_Vendedor_Cliente.idRegistro + " = " + mReg + " " +
                                "AND " + Registro_Vendedor_Cliente.fecha + " = '" + mCurrentDate + "' " +
                                "AND " + Registro_Vendedor_Cliente.idDocumento + " = '" + mDocument + "' " +
                                "AND " + Registro_Vendedor_Cliente.idVendedor + " = '" + mVendedor.get(0) + "' " +
                                "AND " + Registro_Vendedor_Cliente.idCliente + " = '" + mCliente + "' ");
                        new Registro(getApplicationContext()).execute("DELETE FROM Registro " +
                                "WHERE " + Registro.idRegistro + " = " + mReg + " " +
                                "AND " + Registro.fecha + " = '" + mCurrentDate + "' " +
                                "AND " + Registro.idDocumento + " = '" + mDocument + "' " +
                                "AND " + Registro.idPrefijo + " = '" + mVendedor.get(1) + "' ");
                    }
                } catch (Exception e) {
                    Toast.makeText(this, String.format(Locale.getDefault(), mTitle + " #%.0f no pudo ser grabado!\n%s", mReg, e.getMessage()), Toast.LENGTH_LONG).show();
                    new Registro(getApplicationContext()).execute("DELETE FROM Registro " +
                            "WHERE " + Registro.idRegistro + " = " + mReg + " " +
                            "AND " + Registro.fecha + " = '" + mCurrentDate + "' " +
                            "AND " + Registro.idDocumento + " = '" + mDocument + "' " +
                            "AND " + Registro.idPrefijo + " = '" + mVendedor.get(1) + "' ");
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, String.format(Locale.getDefault(), mTitle + " #%.0f no pudo ser grabado!\n%s", mReg, e.getMessage()), Toast.LENGTH_LONG).show();
        }
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

    @Override
    public void onItemSelected(int i) {
        mAdditional = getResources().getStringArray(R.array.additional_transactions)[i];
        mAdditionalTransaction.dismiss();
        mAdditionalTransaction = AdditionalTransactionsFragment.newInstance(mAdditional);
        mAdditionalTransaction.show(getSupportFragmentManager(), getString(R.string.PatatasApp));
    }

    @Override
    public void onCompleted(String value) {
        if (mAdditional != null) {
            if (mAdditional.equals(getResources().getStringArray(R.array.additional_transactions)[0])) {
                mDesc = Double.parseDouble(value) / 100;
            } else if (mAdditional.equals(getResources().getStringArray(R.array.additional_transactions)[1])) {
                mRet = Double.parseDouble(value);
            }
        }

        mAdditionalTransaction.dismiss();
    }

    private class TransactionListAdapter extends SingleSelectionListAdapter {

        TransactionListAdapter(Context context, List<ArrayList<String>> dataset) {
            super(context, dataset);
        }

        @Override
        protected View createView(Context context, ViewGroup parent, int viewType) {
            return LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_row, parent, false);
        }

        @Override
        protected void bindView(ArrayList<String> item, ViewHolder holder, boolean isSelected) {
            TextView txtUnits = (TextView) holder.getView(R.id.txtUnits);
            TextView lblGrupo = (TextView) holder.getView(R.id.lblGroup);
            TextView lblDescription = (TextView) holder.getView(R.id.lblDescription);
            TextView lblUnitPrice = (TextView) holder.getView(R.id.lblUnitPrice);
            TextView lblProduct = (TextView) holder.getView(R.id.lblProduct);

            txtUnits.setText(item.get(0));
            lblGrupo.setText(item.get(4));
            lblGrupo.setTextColor(Color.parseColor(String.format(Locale.getDefault(), "#%06X", (0xFFFFFF & Integer.parseInt(item.get(5))))));
            lblDescription.setText(item.get(1));
            lblUnitPrice.setText(String.format(Locale.getDefault(), "$ %.2f", Double.parseDouble(item.get(2))));
            lblProduct.setText(String.format(Locale.getDefault(), "$ %.2f", Double.parseDouble(item.get(3))));

            holder.itemView.setSelected(isSelected);
        }
    }
}