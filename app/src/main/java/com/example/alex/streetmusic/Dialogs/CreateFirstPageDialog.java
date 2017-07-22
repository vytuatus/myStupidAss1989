package com.example.alex.streetmusic.Dialogs;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alex.streetmusic.R;
import com.example.alex.streetmusic.Utils.Constants;
import com.example.alex.streetmusic.ViewPagerFragments.ViewPagerHandler;
import com.google.android.gms.vision.text.Text;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.net.URISyntaxException;

public class CreateFirstPageDialog extends DialogFragment {
    private View rootView;
    private ImageView imageHolder;
    private Uri selectedImageUri;
    private TextView addImageInformational;
    private TextView next;
    private TextView cancel;
    private SharedPreferences sp;

    public static CreateFirstPageDialog newInstance(Uri imageUri) {
        CreateFirstPageDialog createFirstPageDialog = new CreateFirstPageDialog();
        Bundle bundle = new Bundle();

        bundle.putParcelable(Constants.KEY_IMAGE_URI, imageUri);
        createFirstPageDialog.setArguments(bundle);
        return createFirstPageDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedImageUri = getArguments().getParcelable(Constants.KEY_IMAGE_URI);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.activity_create_first_page_dialog, null);
        next = (TextView) rootView.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = CreateSecondPageDialog.newInstance(selectedImageUri);
                dialog.show(CreateFirstPageDialog.this.getFragmentManager(), "AddListDialogFragment");
                dismiss();

            }
        });
        cancel = (TextView) rootView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        //imageHolder = (ImageView) rootView.findViewById(R.id.image_view_holder);
        //Dialog d = builder.setView(rootView).create();
        Dialog d = new Dialog(getActivity(), R.style.FirstPageDialog);
        d.setContentView(rootView);
        imageHolder = (ImageView) rootView.findViewById(R.id.imageHolder);
        addImageInformational = (TextView) rootView.findViewById(R.id.textView_add_image);
        if (selectedImageUri != null)
            Picasso.with(getActivity()).load(selectedImageUri).into(imageHolder);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        //d.getWindow().getAttributes().windowAnimations = R.style.FirstPageDialogAnimation;

        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;


        d.show();

        imageHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());

                // 2. Chain together various setter methods to set the dialog characteristics
                builder1.setTitle("Select Picture").
                        setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, 1);
                    }
                }).setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 2);
                    }
                }).show();


            }
        });

        return d;


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 1:
                if(resultCode == Activity.RESULT_OK){
                    selectedImageUri = data.getData();
                    Picasso.with(getActivity()).load(selectedImageUri).into(imageHolder);
                    addImageInformational.setText("Select a different Image");
                }
                break;
            case 2:
                if(resultCode == Activity.RESULT_OK){
                    selectedImageUri = data.getData();
                    Picasso.with(getActivity()).load(selectedImageUri).into(imageHolder);
                    addImageInformational.setText("Select a different Image");
                }
                break;
            default:
                if (resultCode == Activity.RESULT_CANCELED) {
                    //Write your code if there's no result
                }
                break;
        }
    }
    //Save instance state variables after screen rotation
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){

        savedInstanceState.putParcelable("image", selectedImageUri);
        Toast.makeText(getActivity(), "onsavedinstancestate", Toast.LENGTH_SHORT).show();


        // Call at the end
        super.onSaveInstanceState(savedInstanceState);
    }

    //Recover variables after the screen rotation
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //probably orientation change

            selectedImageUri = savedInstanceState.getParcelable("image");
            Log.d("Code", "did get here");
            if (selectedImageUri != null) {
                Picasso.with(getActivity()).load(selectedImageUri).into(imageHolder);
            }
            Toast.makeText(getActivity(), "onActivityCreatedCalled", Toast.LENGTH_SHORT).show();


        }

    }

}
