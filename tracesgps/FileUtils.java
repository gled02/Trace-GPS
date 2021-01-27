package tracesgps;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;

/**
 * Classe d'outil permet de récupérer le chemin du fichier par URI.
 * @author Ning Shi.
 */
public class FileUtils {

    /**
     * Récupérer le chemin du fichier par URI.
     */
    public static String getFilePathByUri(Context context, Uri uri) {
        String path;
        // commence par file://
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            path = uri.getPath();
            return path;
        }
        // pour android 4.4+, commence par content://
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    // ExternalStorageProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":", 2);
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        path = Environment.getExternalStorageDirectory()
                                + "/" + split[1];
                        return path;
                    }
                } else if (isDownloadsDocument(uri)) {
                    // DownloadsProvider
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            Long.parseLong(id));
                    path = getDataColumn(context, contentUri
                    );
                    return path;
                }
            }
        }
        return null;
    }

    // OUTILS

    /**
     * Récupère les données de la colonne "_data".
     */
    private static String getDataColumn(Context context, Uri uri) {
        final String column = "_data";
        final String[] projection = {column};
        try (Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        }
        return null;
    }

    /**
     * Teste si Uri représente un fichier de stockage externe.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents"
                .equals(uri.getAuthority());
    }

    /**
     * Teste si Uri représente un fichier dans "Téléchargement".
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents"
                .equals(uri.getAuthority());
    }
}
