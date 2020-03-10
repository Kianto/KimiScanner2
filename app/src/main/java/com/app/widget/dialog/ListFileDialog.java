package com.app.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.kimiscanner.R;
import com.app.kimiscanner.main.FolderCollector;
import com.app.kimiscanner.model.FolderInfo;

import java.util.ArrayList;
import java.util.List;

public class ListFileDialog extends Dialog {

    private List<FolderInfo> folderList;
    private List<Boolean> checkList;

    public ListFileDialog(Context context, Callback callback) {
        super(context, callback);

        folderList = FolderCollector.getLocalFolders();
        checkList = new ArrayList<>();
        for (short i = 0; i < folderList.size(); i++) {
            checkList.add(false);
        }
    }

    public ListFileDialog addList(List<FolderInfo> folderList) {
        this.folderList = folderList;
        checkList = new ArrayList<>();
        for (short i = 0; i < folderList.size(); i++) {
            checkList.add(false);
        }
        return this;
    }

    @Override
    public void show() {
        final View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_list_folder, null);

        final RecyclerView recyclerView = inflate.findViewById(R.id.dialog_list_folder);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflate.getContext()));
        recyclerView.setAdapter(new CheckFolderAdapter(folderList, checkList));

        final ConstraintLayout allCheckLayout = inflate.findViewById(R.id.dialog_list_all_layout);
        final CheckBox checkAllBox = inflate.findViewById(R.id.dialog_list_check);

        allCheckLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAllBox.isChecked()) {
                    for (int i = 0; i < checkList.size(); i++) {
                        checkList.set(i, false);
                    }
                    checkAllBox.setChecked(false);
                } else {
                    for (int i = 0; i < checkList.size(); i++) {
                        checkList.set(i, true);
                    }
                    checkAllBox.setChecked(true);
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
                    for (int i = 0; i < folderList.size(); i++) {
                        if (checkList.get(i)) {
                            folderPaths.add(folderList.get(i).folderPath);
                        }
                    }
                    callback.onSucceed(folderPaths.toArray());
                })
                .setNegativeButton(context.getString(R.string.action_cancel), (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    protected class CheckFolderAdapter extends RecyclerView.Adapter<CheckFolderAdapter.ViewHolder> {

        private final List<FolderInfo> mValues;
        private final List<Boolean> mCheckedList;

        public CheckFolderAdapter(List<FolderInfo> folders, List<Boolean> checkeds) {
            mValues = folders;
            mCheckedList = checkeds;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_check_folder, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.setItemView(mValues.get(position), mCheckedList.get(position), position);
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

            public void setItemView(FolderInfo folder, boolean isChecked, int position) {
                mCheck.setChecked(isChecked);

                mName.setText(folder.folderName);
                mPage.setText("Page: " + String.valueOf(folder.pageNumber));

                mView.findViewById(R.id.list_check_folder_layout).setOnClickListener(view -> {
                    mCheckedList.set(position, !mCheck.isChecked());
                    mCheck.setChecked(!mCheck.isChecked());
                });
            }

        }
    }

}
