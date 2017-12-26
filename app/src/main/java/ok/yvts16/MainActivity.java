package ok.yvts16;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private MyDB db = null;
    Button btnadd, btnedit, btndelete, btntel,btnemail;
    EditText etName, etTel, etEmail;
    ListView lst;
    Cursor cursor;
    long myid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* 進入畫面時當游標在輸入欄位上時,不帶出虛擬鍵盤。*/
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        /*---------------------------------------------------------------------------*/
        btnadd = (Button) findViewById(R.id.btnAdd);
        btnedit = (Button) findViewById(R.id.btnEdit);
        btndelete = (Button) findViewById(R.id.btnDelete);
        btntel = (Button) findViewById(R.id.btnTel);
        btnemail = (Button) findViewById(R.id.btnEmail);

        lst = (ListView) findViewById(R.id.listView);
        etName = (EditText) findViewById(R.id.etName);
        etTel = (EditText) findViewById(R.id.etTel);
        etEmail = (EditText) findViewById(R.id.etEmail);

        /* 所有的Button物件共用mylistener監聽器。*/
        btnadd.setOnClickListener(mylistener);
        btnedit.setOnClickListener(mylistener);
        btndelete.setOnClickListener(mylistener);
        btntel.setOnClickListener(mylistener);
        btnemail.setOnClickListener(mylistener);
        /* 設定ListView監聽器。*/
        lst.setOnItemClickListener(listviewListener);

        db = new MyDB(this);
        db.open();
        cursor = db.getAll();
        UpdateAdapter(cursor);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("確定要離開本程式嗎?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                    }
                });
        AlertDialog about_dialog = builder.create();
        about_dialog.show();
    }

    private ListView.OnItemClickListener listviewListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            showdata(id);
            cursor.moveToFirst();
        }
    };

    private Button.OnClickListener mylistener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                switch (v.getId()) {
                    case R.id.btnAdd: {
                        String name = etName.getText().toString();
                        String tel = etTel.getText().toString();
                        String email = etEmail.getText().toString();

                        if (db.append(name, tel, email) > 0) {
                            cursor = db.getAll();  /* 重新讀取所有資料一次。*/
                            UpdateAdapter(cursor); /* 更新在ListView中的顯示資料。*/
                            ClearEdit();           /* 清除輸入欄位上的資料內容。*/
                        }
                        break;
                    }
                    /* 傳送電子郵件 */
                    case R.id.btnEmail: {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setData(Uri.parse("mailto:" +etEmail.getText().toString()));
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                        intent.putExtra(Intent.EXTRA_TEXT, "body");
                        startActivity(intent);
                        break;
                    }
                    /* 傳送簡訊 */
                    case R.id.btnTel:{
                        Uri uri = Uri.parse("smsto:" + etTel.getText().toString());
                        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                        it.putExtra("sms_body", " The SMS text");
                        startActivity(it);
                        break;
                    }
                    case R.id.btnEdit: {
                        String name = etName.getText().toString();
                        String tel = etTel.getText().toString();
                        String email = etEmail.getText().toString();

                        if (db.update(myid, name, tel, email)) {
                            cursor = db.getAll();
                            UpdateAdapter(cursor);
                        }
                        break;
                    }
                    case R.id.btnDelete: {
                        if (cursor != null && cursor.getCount() >= 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("刪除確認:");
                            builder.setMessage("您確認要刪除[" + etName.getText() + "]此筆資料嗎?");
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                /* 按下[取消]不做任何事。*/
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            builder.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                                /* 按下[確認]則真正執行刪除動作。*/
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (db.delete(myid)) {
                                        Toast.makeText(MainActivity.this,
                                                "刪除 ID=" + myid + "成功!",
                                                Toast.LENGTH_SHORT).show();
                                        cursor = db.getAll();
                                        UpdateAdapter(cursor);
                                        ClearEdit();
                                    }
                                }
                            });
                            builder.show();
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void showdata(long Id) {
        Cursor cur = db.get(Id);
        myid = Id;
        etName.setText(cur.getString(1));
        etTel.setText("" + cur.getString(2));
        etEmail.setText("" + cur.getString(3));
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Toast.makeText(this,"昌昌",Toast.LENGTH_LONG).show();
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setTitle("離開確認:");
//        builder.setMessage("您確認要離開嗎?");
//
//        builder.setPositiveButton("確認", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });

//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("確定要離開本程式嗎?")
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // TODO Auto-generated method stub
//                        MainActivity.this.finish();
//                    }
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // TODO Auto-generated method stub
//                        dialog.cancel();
//                    }
//                });
//        AlertDialog about_dialog = builder.create();
//        about_dialog.show();
//
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    public void ClearEdit() {
        etName.setText("");
        etTel.setText("");
        etEmail.setText("");
    }

    public void UpdateAdapter(Cursor cursor) {
        if (cursor != null && cursor.getCount() >= 0) {
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    this,
                    R.layout.simple_list_item_1,
                    cursor,
                    new String[]{"name", "tel","email"},
                    new int[]{R.id.text0, R.id.text1, R.id.text2},
                    0
            );
            lst.setAdapter(adapter);
        }
    }
}
