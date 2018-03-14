package com.patatascrucks.mobile.print;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.support.v7.app.AlertDialog;

import com.patatascrucks.mobile.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Project PatatasApp
 * Created by Ricardo on 8/19/2016.
 */
public abstract class CustomPrintAdapter extends PrintDocumentAdapter {
    private Context mContext;
    private PrintedPdfDocument mDocument;
    private String mTitle;
    private String mHeader;
    private List<ArrayList<String>> mData;
    private String mFooter;

    protected CustomPrintAdapter(Context context) {
        mContext = context;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback layoutResultCallback, Bundle bundle) {
        mDocument = new PrintedPdfDocument(mContext, newAttributes);

        if (cancellationSignal.isCanceled()) {
            layoutResultCallback.onLayoutCancelled();
            return;
        }

        int pages = 1;

        PrintDocumentInfo info = new PrintDocumentInfo
                .Builder("temp_file.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(pages)
                .build();

        layoutResultCallback.onLayoutFinished(info, true);
    }

    @Override
    public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor parcelFileDescriptor, CancellationSignal cancellationSignal, WriteResultCallback writeResultCallback) {
        if (mDocument == null) {
            new AlertDialog.Builder(mContext)
                    .setTitle(R.string.title_activity_transaction)
                    .setMessage("El documento está vacío")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }

        PdfDocument.Page page = mDocument.startPage(0);
        if (cancellationSignal.isCanceled()) {
             writeResultCallback.onWriteCancelled();
            mDocument.close();
            mDocument = null;
            return;
        }

        onDraw(page.getCanvas());
        mDocument.finishPage(page);

        try {
            mDocument.writeTo(new FileOutputStream(parcelFileDescriptor.getFileDescriptor()));
        } catch (IOException e) {
            writeResultCallback.onWriteFailed(e.toString());
            return;
        } finally {
            mDocument.close();
            mDocument = null;
        }
        writeResultCallback.onWriteFinished(pageRanges);
    }

    public abstract void onDraw(Canvas canvas);
}
