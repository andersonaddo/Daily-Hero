package com.lumberjackapps.dailyhero;


import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutPage extends Fragment {


    public AboutPage() {
        // Required empty public constructor
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

    /**
     * This method allows XML images to receive compressed images instead of full memory images.
     * Prevents sluggish scrolling.
     * @param res
     * @param resId resource id of the image
     * @param reqWidth width of image
     * @param reqHeight height of image
     * @return
     */
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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_page, container, false);

        ImageView logo = (ImageView)view.findViewById(R.id.logo);
        logo.setImageBitmap(decodeSampledBitmapFromResource(getActivity().getResources(), R.drawable.lumberjackappslogoempty, 300, 300));

        Button contactButton = (Button) view.findViewById(R.id.contactUsButton);
        //Setting listener to send email intent
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"lumberjackapps72@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Primus Suggestion or Issue");
                intent.setType("text/plain");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    getActivity().startActivity(Intent.createChooser(intent, "Send Email using:"));
                }
            }
        });

        //Setting listener to send browser intent
        Button websiteButton = (Button) view.findViewById(R.id.ourWebsiteButton);
        websiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://lumberjacksapps.wixsite.com/home";
                Intent browser = new Intent(Intent.ACTION_VIEW);
                browser.setData(Uri.parse(url));
                startActivity(browser);
            }
        });

        //Setting listener for legal disclaimer button
        Button legalbutton = (Button)view.findViewById(R.id.legal_button);
        legalbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LegalDisc.class);
                getActivity().startActivity(intent);
            }
        });

        return view;
    }

}
