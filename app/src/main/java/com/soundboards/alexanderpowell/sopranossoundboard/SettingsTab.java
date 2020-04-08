package com.soundboards.alexanderpowell.sopranossoundboard;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import java.io.FileDescriptor;
import java.util.Random;

public class SettingsTab extends Fragment implements View.OnClickListener {

    private static String[] sound_file_names;
    private MediaPlayer mediaPlayer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_tab_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        sound_file_names = MainActivity.filenames;

        MaterialButton shareButton = requireActivity().findViewById(R.id.shareButton);
        MaterialButton suggestSoundButton = requireActivity().findViewById(R.id.suggestNewSoundButton);
        MaterialButton randomSoundButton = requireActivity().findViewById(R.id.randomSoundButton);

        shareButton.setOnClickListener(this);
        suggestSoundButton.setOnClickListener(this);
        randomSoundButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.shareButton: shareApp(); break;
            case R.id.suggestNewSoundButton: suggestNewSound(); break;
            case R.id.randomSoundButton: playRandomSound(); break;
        }
    }

    private void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBody = getString(R.string.share_app_body);
        shareBody = shareBody + "\n" + getString(R.string.share_app_url);
        String shareSubject = getString(R.string.share_subject_text);
        intent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(intent, getString(R.string.share_using_text)));
    }

    private void suggestNewSound() {
        Uri uri = Uri.parse(getString(R.string.suggest_new_sound_url));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void playRandomSound() {
        int random = new Random().nextInt(sound_file_names.length);
        playSound(sound_file_names[random]);
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
