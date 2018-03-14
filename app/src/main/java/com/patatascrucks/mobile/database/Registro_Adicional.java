package com.patatascrucks.mobile.database;

import android.content.Context;
import java.util.ArrayList;

public class Registro_Adicional extends DBHelperController {
	private final String TABLE_NAME = "Registro_Adicional";
	public static final String idDocumento = "idDocumento";
	public static final String idRegistro = "idRegistro";
	public static final String idPrefijo = "idPrefijo";
	public static final String fecha = "fecha";
	public static final String fecha2 = "fecha2";
	public static final String idDocumentoAdicional = "idDocumentoAdicional";
	public static final String valor = "valor";
	private Context context;

	public Registro_Adicional(Context context) {
		this.context = context;
	}

	public void insert(String idDocumento, Double idRegistro, String idPrefijo, String fecha, String fecha2, String idDocumentoAdicional, Double valor) {
		idDocumento = idDocumento != null ? "\"" + idDocumento + "\"" : null;
		idPrefijo = idPrefijo != null ? "\"" + idPrefijo + "\"" : null;
		fecha = fecha != null ? "\"" + fecha + "\"" : null;
		fecha2 = fecha2 != null ? "\"" + fecha2 + "\"" : null;
		idDocumentoAdicional = idDocumentoAdicional != null ? "\"" + idDocumentoAdicional + "\"" : null;
		
		Object[] values_ar = {idDocumento, idRegistro, idPrefijo, fecha, fecha2, idDocumentoAdicional, valor};
		String[] fields_ar = {Registro_Adicional.idDocumento, Registro_Adicional.idRegistro, Registro_Adicional.idPrefijo, Registro_Adicional.fecha, Registro_Adicional.fecha2, Registro_Adicional.idDocumentoAdicional, Registro_Adicional.valor};
		String values = "", fields = "";
		for (int i = 0; i < values_ar.length; i++) {
			if (values_ar[i] != null) {
				values += values_ar[i] + ", ";
				fields += fields_ar[i] + ", ";
			}
		}
		if (!values.isEmpty()) {
			values = values.substring(0, values.length() - 2);
			fields = fields.substring(0, fields.length() - 2);
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