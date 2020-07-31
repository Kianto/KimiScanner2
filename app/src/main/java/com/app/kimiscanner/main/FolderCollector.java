package com.app.kimiscanner.main;

import com.app.kimiscanner.model.FolderInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.app.kimiscanner.LocalPath.ROOT_PATH;

public abstract class FolderCollector {

    public static List<FolderInfo> getLocalFolders() {
        List<FolderInfo> resList = new ArrayList<>();

        File[] documents = new File(ROOT_PATH).listFiles();
        if (null != documents) {
            for (File file : documents) {
                if (file.isDirectory()) {
                    if (file.getName().contains(".")) continue;
                    if (null == file.list() || 0 == Objects.requireNonNull(file.list()).length)
                        continue;

                    FolderInfo folder = new FolderInfo(file);
                    resList.add(folder);
                }
            }
        }
        Collections.sort(resList, new Comparator<FolderInfo>() {
            @Override
            public int compare(FolderInfo o1, FolderInfo o2) {
                return o2.lastModified.compareTo(o1.lastModified);
            }
        });

        return resList;
    }

}
