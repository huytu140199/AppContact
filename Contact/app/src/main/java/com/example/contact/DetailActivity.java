package com.example.contact;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class DetailActivity extends AppCompatActivity {
    TextInputEditText txtName, txtPhone, txtEmail;
    Button btnAdd;
    Uri selectedImageUri;

    ImageView imgContact;

    private int mode = 1;

    private String fileName;

    private Contact contact;

    private static final int REQUEST_IMAGE_PICK = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Thêm mới liên hệ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addData();
        addMethods();
        getIntentData();
    }

    private void getIntentData() {
        if(getIntent().hasExtra("contact")){
            contact = (Contact) getIntent().getSerializableExtra("contact");
            txtName.setText(contact.getName());
            txtPhone.setText(contact.getPhone());
            txtEmail.setText(contact.getEmail());
            File imageFile  = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),contact.getImage());
            String imagePath = imageFile.getAbsolutePath();
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imgContact.setImageBitmap(bitmap);
        }
        this.mode = getIntent().getIntExtra("mode", 1);
        if(mode == 1){
            getSupportActionBar().setTitle("Thêm mới liên hệ");
            btnAdd.setText("Thêm mới");
        }
        else if(mode == 2){
            getSupportActionBar().setTitle("Chỉnh sửa liên hệ");
            btnAdd.setText("Chỉnh sửa");
        }
    }

    private void addMethods() {

        //btnImage
        imgContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE_PICK);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contact.setName(txtName.getText().toString());
                contact.setPhone(txtPhone.getText().toString());
                contact.setEmail(txtEmail.getText().toString());
                if(selectedImageUri != null){
                    // Lưu ảnh vào thư mục ứng dụng
                    saveImageToAppDirectory(selectedImageUri);
                    contact.setImage(fileName);
                }
                else{
                    contact.setImage("default.jpg");
                }
                if(contact.validate(DetailActivity.this)){
                    SQLiteHelper myDB = new SQLiteHelper(DetailActivity.this);
                    //thêm mới
                    if(mode == 1){
                        myDB.addContact(contact);
                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                    //cập nhật
                    else if(mode == 2){
                        myDB.updateContact(contact);
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();

            // hiển thị ảnh trên ImageView:
            ImageView imgHike = findViewById(R.id.imgContact);
            imgHike.setImageURI(selectedImageUri);
        }
    }

    private void saveImageToAppDirectory(Uri imageUri) {
        try {
            // Lấy đường dẫn thư mục lưu trữ của ứng dụng
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            // Tạo tên tệp tin
            fileName = generateUniqueFileName("jpg");
            // Tạo tệp tin cho ảnh
            File imageFile = new File(storageDir, fileName);
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            OutputStream outputStream = new FileOutputStream(imageFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi lưu ảnh.", Toast.LENGTH_LONG).show();
        }
    }

    private static String generateUniqueFileName(String extension) {
        // Lấy thời gian hiện tại dưới dạng timestamp
        long timestamp = System.currentTimeMillis();

        // Tạo định dạng ngày tháng giờ phút giây
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        // Lấy ngày tháng giờ phút giây hiện tại
        String formattedDate = dateFormat.format(new Date(timestamp));

        // Tạo một số ngẫu nhiên để đảm bảo tính duy nhất
        Random random = new Random();
        int randomInt = random.nextInt(10000);

        // Kết hợp thông tin để tạo tên tập tin duy nhất
        return formattedDate + "_" + randomInt + "." + extension;
    }

    private void addData() {
        contact = new Contact();
        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtEmail = findViewById(R.id.txtEmail);
        imgContact = findViewById(R.id.imgContact);
        btnAdd = findViewById(R.id.btnAdd);
    }
}