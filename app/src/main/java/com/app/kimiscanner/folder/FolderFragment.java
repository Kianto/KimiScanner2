package com.app.kimiscanner.folder;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.kimiscanner.R;
import com.app.kimiscanner.model.FolderInfo;

/**
 * A placeholder fragment containing a simple view.
 */
public class FolderFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "folder-column-count";
    private static final int GRID_COLUMN = 2;

    private int mColumnCount = 1;
    private boolean isLinear = true;

    private RecyclerView listLayout;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;

    private FolderInfo folder;

    public FolderFragment() {
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, 1);
        this.setArguments(args);

        this.folder = FolderStore.getInstance().folder;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder, container, false);

        setListAdapter(view);

        return view;
    }

    private void setListAdapter(View view) {
        listLayout = view.findViewById(R.id.folder_list);

        Context context = getContext();
        linearLayoutManager = new LinearLayoutManager(context);
        gridLayoutManager = new GridLayoutManager(context, GRID_COLUMN);

        if (mColumnCount <= 1) {
            listLayout.setLayoutManager(linearLayoutManager);
        } else {
            listLayout.setLayoutManager(gridLayoutManager);
        }
        listLayout.setAdapter(new FileViewAdapter(folder.filePaths));
    }

    public void changeView() {
        if (isLinear) {
            listLayout.setLayoutManager(gridLayoutManager);
        } else {
            listLayout.setLayoutManager(linearLayoutManager);
        }
        listLayout.setAdapter(new FileViewAdapter(folder.filePaths));

        isLinear = !isLinear;
    }


}
