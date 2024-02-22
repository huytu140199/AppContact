package com.example.contact;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends  RecyclerView.Adapter<ContactAdapter.ContactViewHodler> implements Filterable {

    private Context mContext;
    private List<Contact> mListContact, mListContactOld;


    public ContactAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<Contact> list){
        this.mListContact = list;
        this.mListContactOld = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ContactViewHodler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHodler holder, int position) {
        Contact contact = mListContact.get(position);
        if(contact == null){
            return;
        }

        holder.itemName.setText(contact.getName());
        holder.itemPhone.setText(contact.getPhone());
        holder.itemEmail.setText(contact.getEmail());
        File imageFile  = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),contact.getImage());
        String imagePath = imageFile.getAbsolutePath();
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        holder.itemImg.setImageBitmap(bitmap);
        holder.contactItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, contact.getPhone(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("contact", contact);
                intent.putExtra("mode", 2);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mListContact != null){
            return mListContact.size();
        }
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString();
                if(strSearch.isEmpty()){
                    mListContact = mListContactOld;
                }
                else{
                    List<Contact> list = new ArrayList<>();
                    for (Contact contact : mListContactOld){
                        if(contact.getName().toLowerCase().contains(strSearch.toLowerCase())){
                            list.add(contact);
                        }
                    }

                    mListContact = list;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mListContact;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mListContact = (List<Contact>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ContactViewHodler extends RecyclerView.ViewHolder{

        private ImageView itemImg;
        private TextView itemName, itemPhone, itemEmail;
        private FrameLayout contactItem;

        LinearLayout layoutForeground;

        public ContactViewHodler(@NonNull View itemView) {
            super(itemView);

            itemImg = itemView.findViewById(R.id.itemImg);
            itemName = itemView.findViewById(R.id.itemName);
            contactItem = itemView.findViewById(R.id.contactItem);
            itemPhone = itemView.findViewById(R.id.itemPhone);
            itemEmail = itemView.findViewById(R.id.itemEmail);
            layoutForeground = itemView.findViewById(R.id.layout_foreground);
        }
    }

    public void removeItem(int index){
        mListContact.remove(index);
        notifyItemRemoved(index);
    }

    public void undoItem(Contact contact, int index){
        mListContact.add(index, contact);
        notifyItemInserted(index);
    }

}
