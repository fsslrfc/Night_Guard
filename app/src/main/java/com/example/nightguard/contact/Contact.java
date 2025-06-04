package com.example.nightguard.contact;

public class Contact {
    private int id;
    private String name;
    private String phone;
    private String note;

    public Contact(int id, String name, String phone, String note) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.note = note;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getNote() { return note; }

    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setNote(String note) { this.note = note; }
}
