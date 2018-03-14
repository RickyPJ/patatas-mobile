package com.patatascrucks.mobile.reports;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.print.PrintManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.patatascrucks.mobile.R;
import com.patatascrucks.mobile.database.Cliente;
import com.patatascrucks.mobile.database.Documento;
import com.patatascrucks.mobile.database.Registro;
import com.patatascrucks.mobile.database.Registro_Adicional;
import com.patatascrucks.mobile.database.Registro_Producto;
import com.patatascrucks.mobile.database.Registro_Vendedor_Cliente;
import com.patatascrucks.mobile.database.Vendedor;
import com.patatascrucks.mobile.print.CustomPrintAdapter;
import com.patatascrucks.mobile.util.SingleSelectionListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Diary extends AppCompatActivity {
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<ArrayList<String>> myDataList;
    private TextView mTxtTotal;
    private String mTitle;
    private ArrayList<String> mVendedor;
    private String mCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        ActionBar appBar = getSupportActionBar();
        assert appBar != null;
        appBar.setDisplayHomeAsUpEnabled(true);

        mTitle = getIntent().getStringExtra(getString(R.string.app_name));
        setTitle(mTitle);

        mVendedor = new Vendedor(getApplicationContext()).select(Vendedor.idVendedor + ", " + Vendedor.vendedor, null, null, null, null).get(0);

        mCurrentDate = String.format(Locale.getDefault(), "%tF", Calendar.getInstance());

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_list);
        assert mRecyclerView != null;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        myDataList = new Registro(this).getExecuteResult(
                "SELECT "
                + Documento.documento + ", "
                + Registro_Producto.idRegistro + ", "
                + "\"\" AS " + Cliente.cliente + ", "
                + "SUM(" + Registro_Producto.cantidad + " * " + Registro_Producto.valor + ") AS Total, "
                + Registro_Producto.fecha + " "
                + "FROM Registro_Producto "
                + "JOIN Documento "
                + "ON Registro_Producto." + Registro_Producto.idDocumento + " = Documento." + Documento.idDocumento + " "
                + "WHERE " + Registro_Producto.idPrefijo + " = '" + mVendedor.get(1) + "' "
                + "AND " + Registro_Producto.fecha + " = '" + mCurrentDate + "' "
                + "AND Registro_Producto." + Registro_Producto.idDocumento + " NOT LIKE 'fact%' "
                + "GROUP BY " +  Documento.documento + ", " + Registro_Producto.idRegistro + ", " + Cliente.cliente
                + " UNION " +
                "SELECT "
                + Documento.documento + ", "
                + "Registro_Vendedor_Cliente." + Registro_Vendedor_Cliente.idRegistro + ", "
                + Cliente.cliente + ", "
                + "SUM(" + Registro_Producto.cantidad + " * " + Registro_Producto.valor + ") AS Total, "
                + "Registro_Vendedor_Cliente." + Registro_Vendedor_Cliente.fecha + " "
                + "FROM Registro_Vendedor_Cliente "
                + "JOIN Documento "
                + "ON Registro_Vendedor_Cliente." + Registro_Vendedor_Cliente.idDocumento + " = Documento." + Documento.idDocumento + " "
                + "JOIN Cliente "
                + "ON Registro_Vendedor_Cliente." + Registro_Vendedor_Cliente.idCliente + " = Cliente." + Cliente.idCliente + " "
                + "JOIN Registro_Producto "
                + "ON Registro_Vendedor_Cliente." + Registro_Vendedor_Cliente.fecha + " = Registro_Producto." + Registro_Producto.fecha + " "
                + "AND Registro_Vendedor_Cliente." + Registro_Vendedor_Cliente.idDocumento + " = Registro_Producto." + Registro_Producto.idDocumento + " "
                + "AND Registro_Vendedor_Cliente." + Registro_Vendedor_Cliente.idPrefijo + " = Registro_Producto." + Registro_Producto.idPrefijo + " "
                + "AND Registro_Vendedor_Cliente." + Registro_Vendedor_Cliente.idRegistro + " = Registro_Producto." + Registro_Producto.idRegistro + " "
                + "WHERE Registro_Vendedor_Cliente." + Registro_Vendedor_Cliente.idPrefijo + " = '" + mVendedor.get(1) + "' "
                + "AND " + Registro_Vendedor_Cliente.idVendedor + " = '" + mVendedor.get(0) + "' "
                + "AND Registro_Vendedor_Cliente." + Registro_Vendedor_Cliente.fecha + " = '" + mCurrentDate + "' "
                + "AND Registro_Vendedor_Cliente." + Registro_Vendedor_Cliente.idDocumento + " LIKE 'fact%' "
                + "GROUP BY " +  Documento.documento + ", Registro_Vendedor_Cliente." + Registro_Vendedor_Cliente.idRegistro + ", " + Cliente.cliente
                + " UNION " +
                "SELECT "
                + Documento.documento + ", "
                + "Registro_Vendedor_Cliente." + Registro_Vendedor_Cliente.idRegistro + ", "
                + Cliente.cliente + ", "
                + Registro_Adicional.valor + " AS Total, "
                + "Registro_Vendedor_Cliente." + Registro_Vendedor_Cliente.fecha + " "
                + "FROM Registro_Vendedor_Cliente "
                + "JOIN Documento "
                + "ON Registro_Vendedor_Cliente." + Registro_Vendedor_Cliente.idDocumento + " = Documento." + Documento.idDocumento + " "
                + "JOIN Cliente "
                + "ON Registro_Vendedor_Cliente." + Registro_Vendedor_Cliente.idCliente + " = Cliente." + Cliente.idCliente + " "
                + "JOIN Registro_Adicional "
                + "ON Registro_Vendedor_Cliente." + Registro_Vendedor_Cliente.fecha + " = Registro_Adicional." + Registro_Adicional.fecha + " "
                + "AND Registro_Vendedor_Cliente." + Registro_Vendedor_Cliente.idDocumento + " = Registro_Adicional." + Registro_Adicional.idDocumento + " "
                + "AND Registro_Vendedor_Cliente." + Registro_Vendedor_Cliente.idPrefijo + " = Registro_Adicional." + Registro_Adicional.idPrefijo + " "
                + "AND Registro_Vendedor_Cliente." + Registro_Vendedor_Cliente.idRegistro + " = Registro_Adicional." + Registro_Adicional.idRegistro + " "
                + "WHERE Registro_Vendedor_Cliente." + Registro_Vendedor_Cliente.idPrefijo + " = '" + mVendedor.get(1) + "' "
                + "AND " + Registro_Vendedor_Cliente.idVendedor + " = '" + mVendedor.get(0) + "' "
                + "AND Registro_Adicional." + Registro_Adicional.fecha2 + " = '" + mCurrentDate + "' "
                + "AND Registro_Adicional." + Registro_Adicional.idDocumentoAdicional + " = 'cob' "
        );

        mAdapter = new DiaryListAdapter(getApplicationContext(), myDataList);
        mRecyclerView.setAdapter(mAdapter);

        mTxtTotal = (TextView) findViewById(R.id.txtTotal);
        assert mTxtTotal != null;
        mTxtTotal.setText(String.format(Locale.getDefault(), "$ %.2f", 0.0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.diary, menu);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_print:
                doPrint();
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                        int bodyLine = 120;

                        paint.setColor(Color.BLACK);
                        paint.setTypeface(Typeface.MONOSPACE);
                        paint.setTextSize(22);
                        canvas.drawText(mTitle, margin, titleLine, paint);
                        paint.setTextSize(18);
                        canvas.drawText(String.format(Locale.getDefault(), "Vendedor: %s", mVendedor.get(1)), margin, headerLine, paint);
                        paint.setTextSize(16);
                        for (ArrayList<String> item : myDataList) {
                            if (item.get(0).length() > 0) canvas.drawText(
                                    String.format(
                                            Locale.getDefault(),
                                            "%-15s #: %-6s %-17s %7.2f",
                                            item.get(0),
                                            item.get(1),
                                            item.get(2),
                                            Double.parseDouble(item.get(3))),
                                    margin,
                                    bodyLine += 20,
                                    paint);
                        }
                        canvas.drawText("\n", margin, bodyLine += 20, paint);
                        canvas.drawText(
                                String.format(
                                        Locale.getDefault(),
                                        "%4s %-30s %-7s %7.2f",
                                        "",
                                        "",
                                        "TOTAL",
                                        Double.parseDouble(mTxtTotal.getText().toString().replace("$", "").trim())),
                                margin,
                                bodyLine + 20, paint);
                    }
                },
                null);
    }

    class DiaryListAdapter extends SingleSelectionListAdapter {

        public DiaryListAdapter(Context context, List<ArrayList<String>> dataset) {
            super(context, dataset);
        }

        @Override
        protected View createView(Context context, ViewGroup parent, int viewType) {
            return LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_row, parent, false);
        }

        @Override
        protected void bindView(ArrayList<String> item, ViewHolder holder, boolean isSelected) {
            TextView lblDocument = (TextView) holder.getView(R.id.lblDocument);
            TextView lblDate = (TextView) holder.getView(R.id.lblDate);
            TextView lblRegistry = (TextView) holder.getView(R.id.lblRegistry);
            TextView lblClient = (TextView) holder.getView(R.id.lblClient);
            TextView lblTotal = (TextView) holder.getView(R.id.lblTotal);

            lblDocument.setText(item.get(0));
            lblDate.setText(item.get(4));
            lblRegistry.setText(String.format(Locale.getDefault(), "#%010d", ((Number)Double.parseDouble(item.get(1))).longValue()));
            lblClient.setText(item.get(2));
            lblTotal.setText(String.format(Locale.getDefault(), "$ %.2f", Double.parseDouble(item.get(3))));

            holder.itemView.setSelected(isSelected);
        }
    }
}
