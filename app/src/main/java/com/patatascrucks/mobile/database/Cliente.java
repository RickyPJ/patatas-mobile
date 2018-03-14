package com.patatascrucks.mobile.database;

import android.content.Context;
import java.util.ArrayList;

public class Cliente extends DBHelperController {
	private final String TABLE_NAME = "Cliente";
	public static final String idCliente = "idCliente";
	public static final String cliente = "cliente";
	private static final String localizacion = "localizacion";
	private static final String email = "email";
	private static final String celular = "celular";
	private Context context;

	public Cliente(Context context) {
		this.context = context;
	}

	public void insert(String idCliente, String cliente, String localizacion, String email, String celular) {
		idCliente = idCliente != null ? "\"" + idCliente + "\"" : null;
		cliente = cliente != null ? "\"" + cliente + "\"" : null;
		localizacion = localizacion != null ? "\"" + localizacion + "\"" : null;
		email = email != null ? "\"" + email + "\"" : null;
		celular = celular != null ? "\"" + celular + "\"" : null;
		
		Object[] values_ar = {idCliente, cliente, localizacion, email, celular};
		String[] fields_ar = {Cliente.idCliente, Cliente.cliente, Cliente.localizacion, Cliente.email, Cliente.celular};
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
		query += sort != null && sortField != null ? " ORDER BY " + sortField + " " + sort : "";
		return super.executeQuery(context, query);
	}

	public ArrayList<ArrayList<String>> getExecuteResult(String query) {
		return super.executeQuery(context, query);
	}

	public void execute(String query) {
		super.execute(context, query);
	}

}