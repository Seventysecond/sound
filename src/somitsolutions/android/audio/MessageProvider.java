package somitsolutions.android.audio;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import somitsolutions.android.audio.Constant;

public class MessageProvider extends ContentProvider {
	private static final UriMatcher mUriMatcher;
	private static final int URI_TYPE_user = 1;
	static {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(Constant.AUTHORITY, DatabaseHelper.usertable,
				URI_TYPE_user);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		private static final String DataBase_Name = "Message";
		public static final String usertable = "user";

		public DatabaseHelper(Context context) {
			super(context, DataBase_Name, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) { // Table name
			String sqluser = "CREATE TABLE user ("
					+ "id INTEGER primary key autoincrement," + "url TEXT ,"
					+ "time TEXT);";

			db.execSQL(sqluser);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS" + usertable);
		}

	}

	static DatabaseHelper databaseHelper;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch (mUriMatcher.match(uri)) {

		case URI_TYPE_user:

			SQLiteDatabase db = databaseHelper.getWritableDatabase();
			db.delete("user", selection, null);
			db.close();
			break;
		}
		return 0;
	}

	@Override
	public String getType(Uri uri) {

		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		switch (mUriMatcher.match(uri)) {

		case URI_TYPE_user:

			SQLiteDatabase db = databaseHelper.getWritableDatabase();
			db.insert("user", null, values);
			db.close();
			break;

		}
		return null;
	}

	@Override
	public boolean onCreate() {

		databaseHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		switch (mUriMatcher.match(uri)) {
		case URI_TYPE_user:
			SQLiteDatabase db = databaseHelper.getReadableDatabase();
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables("user");
			Cursor c = qb.query(db, projection, selection, selectionArgs, null,
					null, null);
			return c;
		}
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		switch (mUriMatcher.match(uri)) {

		case URI_TYPE_user:

			SQLiteDatabase db = databaseHelper.getWritableDatabase();
			db.update("user", values, selection, null);
			db.close();
			break;
		}

		return 0;
	}

}
