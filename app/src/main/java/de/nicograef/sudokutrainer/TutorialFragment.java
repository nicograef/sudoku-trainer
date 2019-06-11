package de.nicograef.sudokutrainer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TutorialFragment extends Fragment {

    public TutorialFragment() {}


    public static TutorialFragment newInstance(int position) {
        TutorialFragment fragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View helpFragment = inflater.inflate(R.layout.fragment_tutorial, container, false);
        int position = getArguments().getInt("position", 0) + 1;

        TextView helpTitle = (TextView) helpFragment.findViewById(R.id.helpTitle);
        TextView helpBody = (TextView) helpFragment.findViewById(R.id.helpBody);

        switch (position) {
            case 1:
            default:
                helpTitle.setText(R.string.help_title_1);
                helpBody.setText(R.string.help_body_1);
                break;
            case 2:
                helpTitle.setText(R.string.help_title_2);
                helpBody.setText(R.string.help_body_2);
                break;
            case 3:
                helpTitle.setText(R.string.help_title_3);
                helpBody.setText(R.string.help_body_3);
                break;
            case 4:
                helpTitle.setText(R.string.help_title_4);
                helpBody.setText(R.string.help_body_4);
                break;
        }

        return helpFragment;
    }
}
