package ok.yvts16;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;

/* 建立自己資料庫類別 */
public class MyDB {
    public SQLiteDatabase db = null;
    private final static String DATABASE_NAME = "mydb.db";
    private final static String TABLE_NAME = "mytable";
    private final static String ID = "_id";
    private final static String NAME = "name";
    private final static String TEL = "tel";
    private final static String EMAIL = "email";

    /* create databse 字串 */
    private final static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
            " (" + ID + " INTEGER PRIMARY KEY," + NAME + " varchar not null," + TEL + " varchar," + EMAIL + " varchar )";
    private Context mCtx = null;

    /* 建構式,必須傳入Context,傳入建立物件的Activity。*/
    public MyDB(Context ctx) {
        this.mCtx = ctx;
    }

    // 開啟資料庫
    public void open() throws SQLException {
        db = mCtx.openOrCreateDatabase(DATABASE_NAME, 0, null);
        try {
            db.execSQL(CREATE_TABLE);
        } catch (Exception e) {
        }
    }

    // 查詢所有資料
    public Cursor getAll() {
        return db.query(TABLE_NAME, new String[]{ID, NAME, TEL, EMAIL}, null, null, null, null, null);
    }

    // 查詢指定ID的資料
    public Cursor get(long rowId) throws SQLException {
        Cursor mCursor = db.query(TABLE_NAME, new String[]{ID, NAME, TEL, EMAIL}, ID + "=" + rowId, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //插入資料
    public long append(String name, String tel, String email) {
        ContentValues args = new ContentValues();
        args.put(NAME, name);
        args.put(TEL, tel);
        args.put(EMAIL, email);
        return db.insert(TABLE_NAME, null, args);
    }

    // 刪除指定ID的資料
    public boolean delete(long rowId) {
        return db.delete(TABLE_NAME, ID + "=" + rowId, null) > 0;
    }

    // 更改指定ID的資料
    public boolean update(long rowId, String name, String tel, String email) {
        ContentValues args = new ContentValues();
        args.put(NAME, name);
        args.put(TEL, tel);
        args.put(EMAIL, email);
        return db.update(TABLE_NAME, args, ID + "=" + rowId, null) > 0;
    }

    public void close() {
        db.close();
    }
}
