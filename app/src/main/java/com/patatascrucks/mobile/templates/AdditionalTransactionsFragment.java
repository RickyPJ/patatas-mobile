package com.patatascrucks.mobile.templates;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.widget.EditText;

import com.patatascrucks.mobile.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdditionalTransactionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class AdditionalTransactionsFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private final static String ARG = "ARG";
    private EditText txtValue;
    private OnFragmentInteractionListener mListener;

    public static AdditionalTransactionsFragment newInstance(Object arg) {
        AdditionalTransactionsFragment additionalTransactionsFragment = new AdditionalTransactionsFragment();
        Bundle bundle = new Bundle();
        if (arg.equals(R.id.fab)) {
            bundle.putInt(ARG, (int)arg);
        } else {
            bundle.putString(ARG, arg.toString());
        }
        additionalTransactionsFragment.setArguments(bundle);

        return additionalTransactionsFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        assert args != null;
        Object arg = args.get(ARG);
        assert arg != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (arg.equals(R.id.fab)) {
            builder.setTitle("Seleccione una opción:")
                    .setSingleChoiceItems(R.array.additional_transactions, 0, this);
        } else {
            txtValue = new EditText(getActivity());
            txtValue.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            txtValue.setPadding(10, 10, 10, 10);
            builder.setTitle(arg.toString())
                    .setMessage("Ingrese el valor:")
                    .setView(txtValue)
                    .setPositiveButton("OK", this)
                    .setNegativeButton("Cancelar", null);
        }
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (mListener != null) {
            if (i >= 0) {
                mListener.onItemSelected(i);
            } else {
                mListener.onCompleted(txtValue.getText().toString());
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onItemSelected(int i);
        void onCompleted(String value);
    }
}
