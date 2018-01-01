package com.lumberjackapps.dailyhero;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SavedPages extends Fragment {



    public SavedPages() {
        // Required empty public constructor
    }

    public boolean fileExistance(String fname) {
        File file = getActivity().getFileStreamPath(fname);
        return file.exists();
    }


    public List<Data> fill_with_data() {
        String filename2 ="savedPages";
        List<Data> data = new ArrayList<>();
        if (fileExistance(filename2)) {
            FileInputStream fileInputStream;
            ObjectInputStream objectInputStream;
            try {
                fileInputStream = getActivity().openFileInput(filename2);
                objectInputStream = new ObjectInputStream(fileInputStream);
                data = (ArrayList<Data>) objectInputStream.readObject();
                objectInputStream.close();



            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        return data;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_saved_pages, container, false);
        List<Data> data = fill_with_data();

        //For the card recycler view
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview);
        Recycler_View_Adapter adapter = new Recycler_View_Adapter(data, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }



}
