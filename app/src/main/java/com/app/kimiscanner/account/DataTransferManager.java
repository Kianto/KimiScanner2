package com.app.kimiscanner.account;

import com.app.kimiscanner.R;
import com.app.util.LanguageManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataTransferManager {
    Map<String, DataTransfer> dataMap = new HashMap<>();
    List<DataListener> listenerList = new ArrayList<>();

    private DataTransferManager() {}
    private static DataTransferManager manager;
    public static DataTransferManager getInstance() {
        if (null == manager) manager = new DataTransferManager();
        return manager;
    }

    public void registerListen(DataListener listener) {
        listenerList.add(listener);
    }

    public void unRegisterListen(DataListener listener) {
        listenerList.remove(listener);
    }

    public void addProgress(String id, String folderName, int fileNumber, boolean isUpload) {
        dataMap.put(id, new DataTransfer(folderName, fileNumber, isUpload));
    }

    public void updateProgress(String id, int donePackage) {
        if (dataMap.containsKey(id))
            notifyListeners(id, dataMap.get(id).getAction(), dataMap.get(id).update(donePackage));
    }

    void notifyListeners(String id, String action, double percent) {
        for (DataListener listener : listenerList) {
            if (0 > percent) {
                listener.onFail(id, action);
                dataMap.remove(id);
            }
            else if (1 > percent)
                listener.onUpdateProgress(id, action, percent);
            else {
                listener.onDone(id, action);
                dataMap.remove(id);
            }
        }
    }

    static class DataTransfer {
        DataTransfer(String folderName, int total, boolean isUpload) {
            this.folderName = folderName;
            this.total = total;
            this.transferred = 0;
            this.isUpload = isUpload;
        }

        String folderName;
        int transferred;
        int total;
        boolean isUpload;

        double update(int pack) {
            transferred += pack;
            return getPercent();
        }

        double getPercent() {
            return transferred * 1.0 / total;
        }

        String getAction() {
            return isUpload
                    ? LanguageManager.getInstance().getString(R.string.upload)
                    : LanguageManager.getInstance().getString(R.string.download);
        }
    }

    public interface DataListener {
        void onUpdateProgress(String folderName, String action, double percent);
        void onDone(String folderName, String action);
        void onFail(String folderName, String action);
    }

}

