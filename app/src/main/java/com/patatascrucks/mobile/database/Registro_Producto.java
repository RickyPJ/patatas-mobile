package com.patatascrucks.mobile.database;

import android.content.Context;
import java.util.ArrayList;

public class Registro_Producto extends DBHelperController {
	private final String TABLE_NAME = "Registro_Producto";
	public static final String idRegistro = "idRegistro";
	public static final String idDocumento = "idDocumento";
	public static final String idPrefijo = "idPrefijo";
	public static final String fecha = "fecha";
	private static final String idProducto = "idProducto";
	public static final String cantidad = "cantidad";
	public static final String valor = "valor";
	private Context context;

	public Registro_Producto(Context context) {
		this.context = context;
	}

	public void insert(Double idRegistro, String idDocumento, String idPrefijo, String fecha, Integer idProducto, Integer cantidad, Double valor) {
		idDocumento = idDocumento != null ? "\"" + idDocumento + "\"" : null;
		idPrefijo = idPrefijo != null ? "\"" + idPrefijo + "\"" : null;
		fecha = fecha != null ? "\"" + fecha + "\"" : null;
		
		Object[] values_ar = {idRegistro, idDocumento, idPrefijo, fecha, idProducto, cantidad, valor};
		String[] fields_ar = {Registro_Producto.idRegistro, Registro_Producto.idDocumento, Registro_Producto.idPrefijo, Registro_Producto.fecha, Registro_Producto.idProducto, Registro_Producto.cantidad, Registro_Producto.valor};
		StringBuilder values = new StringBuilder();
		StringBuilder fields = new StringBuilder();
		for (int i = 0; i < values_ar.length; i++) {
			if (values_ar[i] != null) {
				values.append(values_ar[i]).append(", ");
				fields.append(fields_ar[i]).append(", ");
			}
		}
		if (values.length() > 0) {
			values = new StringBuilder(values.substring(0, values.length() - 2));
			fields = new StringBuilder(fields.substring(0, fields.length() - 2));
			super.execute(context, "INSERT INTO " + TABLE_NAME + "(" + fields + ") values(" + values + ");");
		}
	}

	public void delete(String whatField, String whatValue) {
		super.delete(context, TABLE_NAME, whatField + " = " + whatValue);
	}

	public void update(String whatField, String whatValue, String whereField, String whereValue) {
		super.execute(context, "UPDATE " + TABLE_NAME + " set " + whatField + " = \"" + whatValue + "\" where " + whereField + " = \"" + whereValue + "\";");
	}

	public ArrayList<ArrayList<String>> select(String fields, String whatField, String whatValue, String sortField, String sort) {
		String query = "SELECT ";
		query += fields == null ? " * FROM " + TABLE_NAME : fields + " FROM " + TABLE_NAME;
		query += whatField != null && whatValue != null ? " WHERE " + whatField + " = \"" + whatValue + "\"" : "";
		query += sort != null && sortField != null ? " order by " + sortField + " " + sort : "";
		return super.executeQuery(context, query);
	}

	public ArrayList<ArrayList<String>> getExecuteResult(String query) {
		return super.executeQuery(context, query);
	}

	public void execute(String query) {
		super.execute(context, query);
	}

}