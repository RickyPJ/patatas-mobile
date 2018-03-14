package com.patatascrucks.mobile.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DBHelper extends SQLiteOpenHelper {
	private static String DATABASE_PATH;
	private static final String DATABASE_NAME = "PatatasDB.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TAG = DBHelper.class.getSimpleName();
	private final Context context;

	DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
		DATABASE_PATH = context.getFilesDir().getParentFile().getPath() + "/databases/";
		if (!dbExists()) {
			try {
				getReadableDatabase();
				copyDatabase();
			} catch (IOException e) {
				Log.d(TAG, "Error copying database");
				onCreate(getWritableDatabase());
			}
		}

		Log.d(TAG, "Constructor");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createDatabase(db);

		Log.d(TAG, "onCtreate ocurred");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Update database from version  " + oldVersion
				+ " to " + newVersion + ", which remove all old records");
		onCreate(db);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);

		if (!db.isReadOnly()) {
			db.execSQL("PRAGMA foreign_keys=ON");
		}
	}

	private boolean dbExists() {
		SQLiteDatabase db = null;

		try {
			db = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READONLY);
		} catch (SQLException e) {
			Log.d(TAG, "Database doesn't exist");
		}

		if (db != null) {
			db.close();
		}

		return db != null;
	}

	private void createDatabase(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS Documento ( `idDocumento` TEXT NOT NULL, `documento` TEXT NOT NULL, `establecimiento` INTEGER NOT NULL, `serie` INTEGER NOT NULL, PRIMARY KEY(idDocumento))");
		db.execSQL("CREATE TABLE IF NOT EXISTS Grupo ( `idGrupo` TEXT NOT NULL, `color` INTEGER NOT NULL, PRIMARY KEY(idGrupo) )");
		db.execSQL("CREATE TABLE IF NOT EXISTS \"Vendedor_Cliente\" (	`idVendedor`	TEXT NOT NULL,	`idCliente`	TEXT NOT NULL,	PRIMARY KEY(idVendedor,idCliente),	FOREIGN KEY(`idVendedor`) REFERENCES `Vendedor`(`idVendedor`),	FOREIGN KEY(`idCliente`) REFERENCES `Cliente`(`idCliente`))");
		db.execSQL("CREATE TABLE IF NOT EXISTS \"Cliente\" (	`idCliente`	TEXT NOT NULL,	`cliente`	TEXT NOT NULL,	`localizacion`	TEXT NOT NULL,	`email`	TEXT,	`celular`	TEXT,	PRIMARY KEY(idCliente))");
		db.execSQL("CREATE TABLE IF NOT EXISTS \"Vendedor\" (	`idVendedor`	TEXT NOT NULL,	`vendedor`	TEXT NOT NULL,	`email`	TEXT,	`celular`	TEXT,	PRIMARY KEY(idVendedor))");
		db.execSQL("CREATE TABLE IF NOT EXISTS \"Producto\" (	`id`	INTEGER NOT NULL,	`idGrupo`	TEXT NOT NULL,	`producto`	TEXT NOT NULL,	`precioU`	REAL NOT NULL,	PRIMARY KEY(id),	FOREIGN KEY(`idGrupo`) REFERENCES `Grupo`(`idGrupo`))");
		db.execSQL("CREATE TABLE IF NOT EXISTS \"Registro\" (	`idDocumento`	TEXT NOT NULL,	`idRegistro`	REAL NOT NULL,	`idPrefijo`	TEXT NOT NULL,	`fecha`	TEXT NOT NULL,	`comentario`	TEXT,	PRIMARY KEY(idDocumento,idRegistro,idPrefijo,fecha),	FOREIGN KEY(`idDocumento`) REFERENCES `Documento`(`idDocumento`))");
		db.execSQL("CREATE TABLE IF NOT EXISTS \"Registro_Adicional\" (	`idDocumento`	TEXT NOT NULL,	`idRegistro`	REAL NOT NULL,	`idPrefijo`	TEXT NOT NULL,	`fecha`	TEXT NOT NULL,	`idDocumentoAdicional`	TEXT NOT NULL,	`valor`	REAL NOT NULL,	PRIMARY KEY(idDocumento,idRegistro,idPrefijo,fecha,idDocumentoAdicional),	FOREIGN KEY(`idDocumentoAdicional`) REFERENCES `Documento`(`idDocumento`))");
		db.execSQL("CREATE TABLE IF NOT EXISTS \"Registro_Producto\" (	`idRegistro`	REAL NOT NULL,	`idDocumento`	TEXT NOT NULL,	`idPrefijo`	TEXT NOT NULL,	`fecha`	TEXT NOT NULL,	`idProducto`	INTEGER NOT NULL,	`cantidad`	INTEGER NOT NULL,	`valor`	REAL NOT NULL,	PRIMARY KEY(idRegistro,idDocumento,idPrefijo,fecha,idProducto),	FOREIGN KEY(`idProducto`) REFERENCES `Producto`(`id`))");
		db.execSQL("CREATE TABLE IF NOT EXISTS \"Registro_Vendedor_Cliente\" (	`idRegistro`	REAL NOT NULL,	`idDocumento`	TEXT NOT NULL,	`idPrefijo`	TEXT NOT NULL,	`fecha`	TEXT NOT NULL,	`idVendedor`	TEXT NOT NULL,	`idCliente`	TEXT NOT NULL,	PRIMARY KEY(idRegistro,idDocumento,idPrefijo,fecha,idVendedor,idCliente))");
	}

	private void copyDatabase() throws IOException {
		InputStream is = context.getAssets().open(DATABASE_NAME);

		OutputStream os = new FileOutputStream(DATABASE_PATH + DATABASE_NAME);

		byte[] buffer = new byte[1024];

		int length;
		while ((length = is.read(buffer)) > 0) {
			os.write(buffer, 0, length);
		}

		os.flush();
		os.close();
		is.close();
	}
}