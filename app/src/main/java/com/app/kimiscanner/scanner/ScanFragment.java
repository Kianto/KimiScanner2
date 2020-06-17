package com.app.kimiscanner.scanner;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public abstract class ScanFragment extends Fragment {

    protected IFragmentInteractionListener activityListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IFragmentInteractionListener) {
            activityListener = (IFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IFragmentInteractionListener");
        }
    }

    protected abstract View.OnClickListener getViewListener();

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface IFragmentInteractionListener {
        void onCameraFragmentInteraction();
        void onProcessFragmentInteraction(boolean isFromCamera);
        void onCloseFragmentInteraction(ScanFragment fragment);
        void onDoneAllWorkInteraction();
    }
}
