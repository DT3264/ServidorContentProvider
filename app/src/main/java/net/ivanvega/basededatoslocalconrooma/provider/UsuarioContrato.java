package net.ivanvega.basededatoslocalconrooma.provider;

import android.net.Uri;

public class UsuarioContrato {
    public static final Uri CONTENT_URI = Uri.parse("content://net.ivanvega.basededatoslocalconrooma.provider/user");

    public static final String COLUMN_ID = "uid";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";


    public static final String[] COLUMNS = new String[]{
            COLUMN_ID,
            COLUMN_FIRST_NAME,
            COLUMN_LAST_NAME
    };
}