package com.example.alex.streetmusic.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alex.streetmusic.R;
import com.example.alex.streetmusic.Utils.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by vytuatus on 11/18/16.
 */

public class CreateSecondPageDialog extends DialogFragment {
    private View rootView;
    private Button selectMusicStyle;
    private EditText writeBandName;
    private TextView pickedMusicStyle;
    private TextView bandName;
    private TextView next;
    private TextView previous;
    private Uri imageUri;
    private SharedPreferences myPrefs;
    private String musicStyle;
    private SharedPreferences.Editor myPrefsEditor;
    private String writtenBandName;
    private ArrayList selectedItemsIdexList = new ArrayList();
    private ArrayList<String> selectedItemsList = new ArrayList<String>();
    private boolean[] isSelectedArray = {false, false, false, false};

    //For multiSpinnerselector
    private PopupWindow pw;
    private boolean expanded; 		//to  store information whether the selected values are displayed completely or in shortened representatn
    public static boolean[] checkSelected;
    private static final String LOG_TAG = CreateSecondPageDialog.class.getSimpleName();

    public static CreateSecondPageDialog newInstance(Uri imageUri) {
        CreateSecondPageDialog createSecondPageDialog = new CreateSecondPageDialog();
        Bundle bundle = new Bundle();

        bundle.putParcelable(Constants.KEY_IMAGE_URI, imageUri);
        createSecondPageDialog.setArguments(bundle);
        return createSecondPageDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageUri = getArguments().getParcelable(Constants.KEY_IMAGE_URI);
        myPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        myPrefsEditor = myPrefs.edit();
        

    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.create_second_page_dialogfragment, null);
        initializeVariables(rootView);

        //pickedMusicStyle.setText(myPrefs.getString(Constants.KEY_MUSIC_STYLE, "Music Style"));
        bandName.setText(myPrefs.getString(Constants.KEY_WRITTEN_BAND_NAME, "Band Name"));

        selectMusicStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder multiChoiceBuilder = new AlertDialog.Builder(getActivity());
                multiChoiceBuilder.setTitle("Choose the Music Style");
                multiChoiceBuilder.setMultiChoiceItems(R.array.music_styles_array, isSelectedArray,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                                if (isChecked){
                                    //if the user added the item, put it in the selectedItems list
                                    selectedItemsIdexList.add(which);
                                    Log.d(LOG_TAG, String.valueOf(which));
                                }else if (selectedItemsIdexList.contains(which)){
                                    //if item is already in the list, then remove it instead
                                    selectedItemsIdexList.remove(Integer.valueOf(which));
                                }

                            }
                        });
                multiChoiceBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onOkay(selectedItemsIdexList);
                    }
                });

                multiChoiceBuilder.show();

            }
        });


        //
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = CreateFirstPageDialog.newInstance(imageUri);
                dialog.show(CreateSecondPageDialog.this.getFragmentManager(), "CreateFirstPageDialog");
                dismiss();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = CreateThirdPageDialog.newInstance(imageUri);
                dialog.show(CreateSecondPageDialog.this.getFragmentManager(), "CreateThirdPageDialog");
                dismiss();

            }
        });



        writeBandName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE){
                    writtenBandName = writeBandName.getText().toString();
                    bandName.setText(writtenBandName);
                    myPrefsEditor.putString(Constants.KEY_WRITTEN_BAND_NAME, writtenBandName).apply();

                }
                return false;
            }
        });

        /*
         * section for the multiSpinner (in progress...)
         */
        Dialog d = new Dialog(getActivity(), R.style.FirstPageDialog);

        d.setContentView(rootView);

        d.show();


        return d;

    }


    /**
     *
    Related to multiChoise funtionality
    *
    * */
    private void onOkay(ArrayList<Integer> arrayList){
        StringBuilder stringBuilder = new StringBuilder();
        if (arrayList.size() != 0){
            selectedItemsList.clear();
            for (int i=0; i < arrayList.size(); i++){
                String musicStyle = getResources().getStringArray(R.array.music_styles_array)[arrayList.get(i)];
                selectedItemsList.add(musicStyle);
                stringBuilder = stringBuilder.append(musicStyle + ", ");
            }
            Set<String> set = new HashSet<String>();
            set.addAll(selectedItemsList);
            myPrefsEditor.putStringSet(Constants.KEY_MUSIC_STYLE, set).apply();
            pickedMusicStyle.setText(stringBuilder.toString());
        }
    }

    private void initializeVariables(View rootView){
        selectMusicStyle = (Button) rootView.findViewById(R.id.button_select_music_style);
        writeBandName = (EditText) rootView.findViewById(R.id.editText_add_band_name);
        pickedMusicStyle = (TextView) rootView.findViewById(R.id.textView_picked_music_style);
        bandName = (TextView) rootView.findViewById(R.id.textView_band_name);
        next = (TextView) rootView.findViewById(R.id.second_page_next);
        previous = (TextView) rootView.findViewById(R.id.second_page_previous);
    }

}