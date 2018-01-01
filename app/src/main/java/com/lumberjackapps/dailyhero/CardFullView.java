package com.lumberjackapps.dailyhero;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by addoa on 03-Jul-16.
 */
public class CardFullView extends AppCompatActivity{


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
     * Prevents sluggism scrolling.
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_full_view);
        Intent intent = getIntent();
        final Data message = (Data) intent.getSerializableExtra(MainActivity.EXTRA_MESSAGE);
        TextView heroName = (TextView) findViewById(R.id.character_title);
        heroName.setText(message.name);
        TextView heroSummary = (TextView) findViewById(R.id.character_summary);
        heroSummary.setText(message.summary);
        TextView heroTrivia = (TextView) findViewById(R.id.character_trivia);
        heroTrivia.setText(message.trivia);
        TextView heroAbilities = (TextView) findViewById(R.id.character_abilities);
        heroAbilities.setText(message.abilities);
        TextView heroNum = (TextView) findViewById(R.id.character_number);
        heroNum.setText(message.number);
        ImageView heroImage = (ImageView)findViewById(R.id.character_image);
        heroImage.setImageBitmap(decodeSampledBitmapFromResource(getResources(), message.imageId, 300, 300));

        Button linkButton = (Button)findViewById(R.id.link_button);
        linkButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = message.link;
                Intent browser = new Intent(Intent.ACTION_VIEW);
                browser.setData(Uri.parse(url));
                startActivity(browser);
            }
        });
    }
    }

