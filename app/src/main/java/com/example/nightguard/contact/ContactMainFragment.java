package com.example.nightguard.contact;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nightguard.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ContactMainFragment extends Fragment {
    public ContactMainFragment() {
        super();
    }

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private ContactAdapter adapter;
    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewContacts);
        fabAdd = view.findViewById(R.id.fabAddContact);
        dbHelper = new DBHelper(requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        loadContacts();

        fabAdd.setOnClickListener(v -> showAddContactDialog());

        return view;
    }

    private void loadContacts() {
        List<Contact> contactList = dbHelper.getAllContacts();
        adapter = new ContactAdapter(contactList);
        recyclerView.setAdapter(adapter);
    }

    private void showAddContactDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_contact, null);
        EditText editName = dialogView.findViewById(R.id.editName);
        EditText editPhone = dialogView.findViewById(R.id.editPhone);
        EditText editNote = dialogView.findViewById(R.id.editNote);

        new AlertDialog.Builder(requireContext())
                .setTitle("添加紧急联系人")
                .setView(dialogView)
                .setPositiveButton("保存", (dialog, which) -> {
                    String name = editName.getText().toString().trim();
                    String phone = editPhone.getText().toString().trim();
                    String note = editNote.getText().toString().trim();

                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
                        Toast.makeText(requireContext(), "姓名和电话不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean success = dbHelper.insertContact(name, phone, note);
                    if (success) {
                        Toast.makeText(requireContext(), "联系人已添加", Toast.LENGTH_SHORT).show();
                        loadContacts();
                    } else {
                        Toast.makeText(requireContext(), "添加失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadContacts();  // 刷新数据
    }

}
