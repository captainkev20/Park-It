package com.rocks.kevinwalker.parkit.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

import static android.support.v4.app.ActivityCompat.startActivityForResult;
import static android.support.v4.app.ActivityCompat.startIntentSenderForResult;

public class PhotoCaptureHelper {

    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private Context context;

    public PhotoCaptureHelper(Context context) {
        this.context = context;
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Verify a camera activity can handle the intent
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(takePictureIntent);
        }
    }

    public void setPhotoWithPicasso(Uri photoUri, int width, int height, float rotateDegrees, ImageView photoImage) {
        Picasso.get().load(photoUri).centerCrop().resize(width, height).rotate(rotateDegrees).into(photoImage);
    }
}
