package net.ivanvega.basededatoslocalconrooma.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.ivanvega.basededatoslocalconrooma.data.AppDatabase;
import net.ivanvega.basededatoslocalconrooma.data.User;
import net.ivanvega.basededatoslocalconrooma.data.UserDao;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MiContentProvider extends ContentProvider {
    /**
     * Estructura de la uri:
     * authority -> net.ivanvega.basededatoslocanlconrooma.provider
     * content://authority/user -> insert/query
     * content://authority/user/# -> update/delete
     * content://authority/user/* -> query/update/delete
    */


    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        String authority = "net.ivanvega.basededatoslocalconrooma.provider";
        uriMatcher.addURI(authority, "/user",1);
        uriMatcher.addURI(authority, "/user/#",2);
        uriMatcher.addURI(authority, "/user/*",3);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        String typeMime =  "";
        int match = uriMatcher.match(uri);
        switch (match){
            case 1:
            case 3:
                typeMime = "vnd.android.cursor.dir/vnd.net.ivanvega.basededatoslocalconrooma.provider";
                break;
            case 2:
                typeMime = "vnd.android.cursor.item/vnd.net.ivanvega.basededatoslocalconrooma.provider";
                break;
        }
        return typeMime;
    }


    Cursor listUserToCursor(List<User> usuarios){
        String[] columns = new String[]{"uid", "first_name","last_name"};
        MatrixCursor cursor = new MatrixCursor(columns);
        for(User u : usuarios){
            String[] row = new String[]{Integer.toString(u.uid), u.firstName, u.lastName};
            cursor.addRow(row);
        }
        return cursor;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] columns,
                        @Nullable String condition,
                        @Nullable String[] conditionData,
                        @Nullable String orderColumn) {
        UserDao dao = getDao();
        Log.d("MiContentProvider", "Got query with uri: " + uri.toString());

        int match = uriMatcher.match(uri);
        Log.d("MiContentProvider", "Match: " + match);
        switch (match){
            case 1:
                return listUserToCursor(dao.getAll());
            case 2:
                int id = Integer.parseInt(uri.getLastPathSegment());
                List<User> usuarios = dao.loadAllByIds(new int[]{id});
                return listUserToCursor(usuarios);
            case 3:
                break;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        UserDao dao = getDao();
        User usuario = null;
        switch (uriMatcher.match(uri)){
            case 1:
                usuario = new User();
                usuario.firstName = contentValues.getAsString(UsuarioContrato.COLUMN_FIRST_NAME);
                usuario.lastName = contentValues.getAsString(UsuarioContrato.COLUMN_LAST_NAME);
                usuario.uid = dao.insertUser(usuario).intValue();
                break;
        }
        if(usuario == null) return null;
        return Uri.withAppendedPath(uri, String.valueOf(usuario.uid));
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        UserDao dao = getDao();
        int id = Integer.parseInt(uri.getLastPathSegment());
        List<User> usuarios = dao.loadAllByIds(new int[]{id});
        if(usuarios.size() == 0) return 0;
        dao.delete(usuarios.get(0));
        return 1;
    }

    private UserDao getDao() {
        AppDatabase db = AppDatabase.getDatabaseInstance(getContext());
        return db.userDao();
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        UserDao dao = getDao();

        int id = Integer.parseInt(uri.getLastPathSegment());
        User usuario = dao.loadAllByIds(new int[]{id}).get(0);

        String nombre = contentValues.getAsString(UsuarioContrato.COLUMN_FIRST_NAME);
        String apellido = contentValues.getAsString(UsuarioContrato.COLUMN_LAST_NAME);

        usuario.firstName = nombre;
        usuario.lastName = apellido;

        dao.update(usuario);
        return 1;
    }
}
