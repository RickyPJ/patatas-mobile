package com.patatascrucks.mobile.model;

import android.content.Context;
import android.os.Bundle;
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
import android.widget.TextView;

import com.patatascrucks.mobile.R;
import com.patatascrucks.mobile.database.Cliente;
import com.patatascrucks.mobile.database.Documento;
import com.patatascrucks.mobile.database.Registro;
import com.patatascrucks.mobile.database.Registro_Producto;
import com.patatascrucks.mobile.database.Registro_Vendedor_Cliente;
import com.patatascrucks.mobile.database.Vendedor;
import com.patatascrucks.mobile.templates.AdditionalTransactionsFragment;
import com.patatascrucks.mobile.util.SingleSelectionListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Charge extends AppCompatActivity implements View.OnClickListener, AdditionalTransactionsFragment.OnFragmentInteractionListener {
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private GestureDetectorCompat mDetector;
    private List<ArrayList<String>> myDataList;
    private TextView mTxtTotal;
    private String mTitle;
    private ArrayList<String> mVendedor;
    private String mCurrentDate;
    private int currentPos;
    private boolean isSelected;

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
                + "AND Registro_Vendedor_Cliente." + Registro_Vendedor_Cliente.idDocumento + " = 'factCred' "
                + "GROUP BY " +  Documento.documento + ", Registro_Vendedor_Cliente." + Registro_Vendedor_Cliente.idRegistro + ", " + Cliente.cliente
        );

        mAdapter = new DiaryListAdapter(getApplicationContext(), myDataList);
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

        mTxtTotal = (TextView) findViewById(R.id.txtTotal);
        assert mTxtTotal != null;
        mTxtTotal.setText(String.format(Locale.getDefault(), "$ %.2f", 0.0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        SingleSelectionListAdapter adapter = (SingleSelectionListAdapter) mAdapter;

        if (view.getId() == R.id.diary_list_item) {
            currentPos = mRecyclerView.getChildAdapterPosition(view);
            isSelected = adapter.toggleSelection(currentPos);
            AdditionalTransactionsFragment additionalTransaction = AdditionalTransactionsFragment.newInstance("Cobro");
            additionalTransaction.show(getSupportFragmentManager(), getString(R.string.PatatasApp));
        }
    }

    @Override
    public void onItemSelected(int i) {

    }

    @Override
    public void onCompleted(String value) {
        isSelected = ((SingleSelectionListAdapter) mAdapter).toggleSelection(currentPos);
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
