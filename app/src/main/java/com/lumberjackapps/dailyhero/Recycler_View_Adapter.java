package com.lumberjackapps.dailyhero;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by addoa on 03-Jul-16.
 */
public class Recycler_View_Adapter extends RecyclerView.Adapter<View_Holder> {

    List<Data> list = Collections.emptyList();
    Context context;

    public Recycler_View_Adapter(List<Data> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;

    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                  int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    public void setButtonState(boolean newValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("value_key", newValue);
        editor.commit();
    }

    @Override
    public void onBindViewHolder(final View_Holder holder, final int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.name.setText(list.get(position).name);
        holder.number.setText("Character # " + list.get(position).number);
        holder.imageView.setImageBitmap(decodeSampledBitmapFromResource(context.getResources(), (list.get(position).imageId), 60, 60));

        holder.fullbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CardFullView.class);
                intent.putExtra(MainActivity.EXTRA_MESSAGE,(Serializable) list.get(position));
                context.startActivity(intent);
            }
        });

        holder.deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Data pos = list.get(position);
                new AlertDialog.Builder(context)
                        .setTitle("Delete Profile")
                        .setMessage("Are you sure you want to delete Saved Character #" + pos.number + " (" + pos.name +  ") ?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                remove(list.get(position));

                                //Also removing it from internal memory
                                ArrayList<Data> savedPages = new ArrayList<Data>();
                                String filename2 ="savedPages";
                                FileInputStream fileInputStream;
                                ObjectInputStream objectInputStream;
                                try {
                                    fileInputStream = context.openFileInput(filename2);
                                    objectInputStream = new ObjectInputStream(fileInputStream);
                                    savedPages = (ArrayList<Data>)objectInputStream.readObject();
                                    objectInputStream.close();

                                } catch (FileNotFoundException e){
                                    e.printStackTrace();
                                } catch (StreamCorruptedException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                                savedPages.remove(position);
                                FileOutputStream fileOutputStream;
                                ObjectOutputStream objectOutputStream;
                                try{
                                    fileOutputStream = context.openFileOutput(filename2, Context.MODE_PRIVATE);
                                    objectOutputStream = new ObjectOutputStream(fileOutputStream);
                                    objectOutputStream.writeObject(savedPages);
                                    setButtonState(false);
                                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                                    objectOutputStream.close();
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.mipmap.ic_launcher)
                        .show();

            }
        });

        holder.sharebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Daily Hero Character # " + list.get(position).number +":" + list.get(position).name +"\n" + "Find out more at " + list.get(position).link);
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(Intent.createChooser(intent, "Share this profile using:"));
                }
                else {
                    Toast.makeText(context, "You don't have any apps to share this profile.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //animate(holder);

    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, Data data) {
        list.add(position, data);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(Data data) {
        int position = list.indexOf(data);
        list.remove(position);
        notifyItemRemoved(position);
    }

}