package knf.kuma.preferences;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import knf.kuma.BottomFragment;
import knf.kuma.R;

/**
 * Created by Jordy on 08/01/2018.
 */

public class BottomPreferencesFragment extends BottomFragment {
    public static BottomPreferencesFragment get(){
        return new BottomPreferencesFragment();
    }
    public BottomPreferencesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preferences,container,false);
    }

    @Override
    public void onReselect() {

    }
}
