package com.app.widget.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.kimiscanner.R;
import com.app.kimiscanner.main.FolderCollector;
import com.app.kimiscanner.model.FolderInfo;
import com.app.kimiscanner.model.FolderInfoChecker;

import java.util.ArrayList;
import java.util.List;

public class ListFileDialog extends Dialog {

    private List<FolderInfoChecker> folderList;

    // Init with default folder list from local
    public ListFileDialog(Context context, Callback callback) {
        super(context, callback);

        folderList = new ArrayList<>();
        for (FolderInfo folder : FolderCollector.getLocalFolders()) {
            folderList.add(new FolderInfoChecker(folder));
        }
    }

    // Init with a custom folder list
    public ListFileDialog(Context context, Callback callback, List<FolderInfoChecker> folderList) {
        super(context, callback);
        this.folderList = folderList;
    }

    public ListFileDialog setList(List<FolderInfoChecker> folderList) {
        this.folderList = folderList;
        return this;
    }

    @Override
    public void show() {
        if (preCheckIfEmpty(folderList)) {
            Toast toast = Toast.makeText(context, "There is no folder available!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        final View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_list_folder, null);

        final RecyclerView recyclerView = inflate.findViewById(R.id.dialog_list_folder);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflate.getContext()));
        recyclerView.setAdapter(new CheckFolderAdapter(folderList));

        inflate.findViewById(R.id.dialog_select_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (FolderInfoChecker checker : folderList) {
                    if (!checker.isExisted) checker.isChecked = true;
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });

        new AlertDialog
                .Builder(context)
                .setTitle(context.getString(R.string.action_select_folder))
                .setView(inflate)
                .setPositiveButton(context.getString(R.string.action_ok), (dialog, which) -> {
                    List<String> folderPaths = new ArrayList<>();
                    for (FolderInfoChecker checker : folderList) {
                        if (checker.isChecked) {
                            folderPaths.add(checker.getPath());
                        }
                    }
                    callback.onSucceed(folderPaths.toArray());
                    Toast.makeText(context, R.string.processing, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                })
                .setNegativeButton(context.getString(R.string.action_cancel), (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private boolean preCheckIfEmpty(List<FolderInfoChecker> folderList) {
        if (null == folderList || folderList.isEmpty())
            return true;

        int countAvailable = 0;
        for (FolderInfoChecker checker : folderList) {
            if (!checker.isExisted) countAvailable++;
        }
        if (0 == countAvailable)
            return true;

        return false;
    }

    protected class CheckFolderAdapter extends RecyclerView.Adapter<CheckFolderAdapter.ViewHolder> {

        private final List<FolderInfoChecker> mValues;

        CheckFolderAdapter(List<FolderInfoChecker> folders) {
            mValues = folders;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_check_folder, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.setItemView(mValues.get(position));
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final View mView;
            private final CheckBox mCheck;
            private final TextView mName, mPage;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mCheck = (CheckBox) view.findViewById(R.id.list_check_folder_check);
                mName = (TextView) view.findViewById(R.id.list_check_folder_name);
                mPage = (TextView) view.findViewById(R.id.list_check_folder_page);
            }

            public void setItemView(FolderInfoChecker folderChecker) {
                mCheck.setChecked(folderChecker.isChecked);

                mName.setText(folderChecker.getName());
                mPage.setText((folderChecker.getPage().equals("-1")
                        ? ""
                        : "Page: " + folderChecker.getPage())
                );

                if (folderChecker.isExisted) {
                    mName.setPaintFlags(mName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    mView.setBackgroundColor(Color.GRAY);
                } else {
                    mName.setPaintFlags(0);
                    mView.setBackgroundColor(Color.WHITE);

                    mView.findViewById(R.id.list_check_folder_layout).setOnClickListener(view -> {
                        folderChecker.isChecked = !mCheck.isChecked();
                        mCheck.setChecked(!mCheck.isChecked());
                    });
                }
            }

        }
    }

}
