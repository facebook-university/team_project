package com.example.dine_and_donate;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadUtil {

    final public static int GALLERY_REQUEST_CODE = 100;

    private Activity mActivity;

    public UploadUtil(Activity currActivity) {
        mActivity = currActivity;
    }


    public void inOnClick(View v, Uri selectedImage, final Uri[] downloadUri, StorageReference mStorageRef, Task<Uri> urlTask) {
        if(selectedImage != null) {
            final StorageReference ref = mStorageRef.child("images/"+selectedImage.getLastPathSegment());
            UploadTask uploadTask = ref.putFile(selectedImage);

            urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        downloadUri[0] = task.getResult();
                        String s = downloadUri[0].toString();
                    }
                }
            });
        }
    }

    public void pickFromGallery(Intent intent){
        intent.setAction(Intent.ACTION_PICK);
        Log.d("helper method", "gets here");
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        mActivity.startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }
}