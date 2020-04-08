package com.soundboards.alexanderpowell.sopranossoundboard;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import java.io.FileDescriptor;
import java.util.HashSet;
import java.util.Set;

import static com.soundboards.alexanderpowell.sopranossoundboard.MainActivity.BUTTON_HEIGHT_PIXELS;
import static com.soundboards.alexanderpowell.sopranossoundboard.MainActivity.BUTTON_MARGIN_LARGE;
import static com.soundboards.alexanderpowell.sopranossoundboard.MainActivity.BUTTON_MARGIN_SMALL;
import static com.soundboards.alexanderpowell.sopranossoundboard.MainActivity.TABLE_ROW_HEIGHT;
import static com.soundboards.alexanderpowell.sopranossoundboard.MainActivity.TABLE_ROW_WEIGHT;
import static com.soundboards.alexanderpowell.sopranossoundboard.MainActivity.TABLE_ROW_WIDTH;

public class FavoritesTab extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private MediaPlayer mediaPlayer;
    private String[] sounds;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorites_tab_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        TextView empty_favorites_message = requireView().findViewById(R.id.empty_favorites_message);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        Set set = sharedPreferences.getStringSet("favorites", new HashSet<String>());
        sounds = new String[set.size()];
        set.toArray(sounds);

        if (set.size() == 0) {
            empty_favorites_message.setVisibility(View.VISIBLE);
        } else {
            empty_favorites_message.setVisibility(View.INVISIBLE);
        }

        TableLayout tableLayout = requireView().findViewById(R.id.tableLayout2);
        tableLayout.setStretchAllColumns(true);
        tableLayout.setShrinkAllColumns(true);

        TableRow tableRow;
        MaterialButton buttonLeft, buttonRight;

        for (int i = 0; i < sounds.length; i+=2) {
            tableRow = new TableRow(getContext());
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(params);
            tableRow.setGravity(Gravity.CENTER_HORIZONTAL);

            // Construct 2 buttons
            buttonLeft = new MaterialButton(requireContext(), null, R.attr.materialButtonStyle);
            buttonRight = new MaterialButton(requireContext(), null, R.attr.materialButtonStyle);

            // Left hand buttons
            TableRow.LayoutParams buttonLayoutParamsLeft = new TableRow.LayoutParams(TABLE_ROW_WIDTH, TABLE_ROW_HEIGHT, TABLE_ROW_WEIGHT);
            buttonLayoutParamsLeft.height = BUTTON_HEIGHT_PIXELS;
            buttonLayoutParamsLeft.setMarginStart(BUTTON_MARGIN_LARGE);
            buttonLayoutParamsLeft.setMarginEnd(BUTTON_MARGIN_SMALL);

            // Right hand buttons
            TableRow.LayoutParams buttonLayoutParamsRight = new TableRow.LayoutParams(TABLE_ROW_WIDTH, TABLE_ROW_HEIGHT, TABLE_ROW_WEIGHT);
            buttonLayoutParamsRight.height = BUTTON_HEIGHT_PIXELS;
            buttonLayoutParamsRight.setMarginStart(BUTTON_MARGIN_SMALL);
            buttonLayoutParamsRight.setMarginEnd(BUTTON_MARGIN_LARGE);

            buttonLeft.setLayoutParams(buttonLayoutParamsLeft);
            buttonRight.setLayoutParams(buttonLayoutParamsRight);
            buttonLeft.setOnClickListener(this);
            buttonRight.setOnClickListener(this);
            buttonLeft.setOnLongClickListener(this);
            buttonRight.setOnLongClickListener(this);

            // Left
            buttonLeft.setId(i);
            buttonLeft.setText(MainActivity.formatFileString(sounds[i]));
            tableRow.addView(buttonLeft);

            // Right
            if ((i + 1) < sounds.length) {
                buttonRight.setId(i + 1);
                buttonRight.setText(MainActivity.formatFileString(sounds[i + 1]));
                tableRow.addView(buttonRight);
            }

            // Add the row to the table
            tableLayout.addView(tableRow);
        }
    }

    @Override
    public void onClick(View view) {
        playSound(sounds[view.getId()]);
    }

    @Override
    public boolean onLongClick(final View view) {

        final SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage("Remove from favorites");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Set<String> favoritesSet = sharedPreferences.getStringSet("favorites", new HashSet<String>());
                favoritesSet.remove(sounds[view.getId()]);
                editor.clear();
                editor.putStringSet("favorites", favoritesSet);
                editor.apply();

                view.setVisibility(View.GONE);
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        return true;
    }

    private void playSound(String filename) {
        //
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        //

        mediaPlayer = new MediaPlayer();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        try {
            if (getActivity() != null) {
                AssetFileDescriptor assetFileDescriptor = getActivity().getAssets().openFd("sounds/" + filename);
                FileDescriptor fileDescriptor = assetFileDescriptor.getFileDescriptor();
                long startOffset = assetFileDescriptor.getStartOffset();
                long length = assetFileDescriptor.getLength();
                mediaPlayer.setDataSource(fileDescriptor, startOffset, length);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
