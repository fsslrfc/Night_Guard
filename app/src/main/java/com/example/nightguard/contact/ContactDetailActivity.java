package com.example.nightguard.contact;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nightguard.R;

public class ContactDetailActivity extends AppCompatActivity {

    private EditText editName, editPhone, editNote;
    private Button buttonSave, buttonDelete, buttonCall;

    private DBHelper dbHelper;
    private int contactId = -1;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        dbHelper = new DBHelper(this);
        contactId = getIntent().getIntExtra("contact_id", -1);
        setContentView(R.layout.activity_contact_detail);

        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        editNote = findViewById(R.id.editNote);
        buttonSave = findViewById(R.id.buttonSave);
        buttonDelete = findViewById(R.id.buttonDelete);
        buttonCall = findViewById(R.id.buttonCall);

        if (contactId != -1) {
            contact = dbHelper.getContactById(contactId);
            if (contact != null) {
                editName.setText(contact.getName());
                editPhone.setText(contact.getPhone());
                editNote.setText(contact.getNote());
            }
        }

        buttonSave.setOnClickListener(v -> saveChanges());
        buttonDelete.setOnClickListener(v -> confirmDelete());
        buttonCall.setOnClickListener(v -> callContact());
    }

    private void saveChanges() {
        String name = editName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String note = editNote.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "姓名和电话不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = dbHelper.updateContact(contactId, name, phone, note);
        if (success) {
            Toast.makeText(this, "修改已保存", Toast.LENGTH_SHORT).show();
            finish(); // 返回上个页面
        } else {
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除这个联系人吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    dbHelper.deleteContact(contactId);
                    Toast.makeText(this, "已删除联系人", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void callContact() {
        String phone = editPhone.getText().toString().trim();
        if (!TextUtils.isEmpty(phone)) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            startActivity(intent);
        } else {
            Toast.makeText(this, "号码为空", Toast.LENGTH_SHORT).show();
        }
    }
}
