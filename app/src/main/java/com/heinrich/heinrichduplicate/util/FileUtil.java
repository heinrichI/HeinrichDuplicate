package com.heinrich.heinrichduplicate.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileUtil {
    public static ArrayList<File> GetFiles(String[] params) {
        ArrayList<File> files = new ArrayList<File>();
        for (String path: params) {
            File file = new File(path);
            File[] allFiles = file.listFiles();
            if (allFiles != null) {
                for (File f : allFiles) {
                    if (f.isDirectory()) {
                        continue;
                    }
                    files.add(f);
                }
            }
        }
        return files;
    }

    public static List<File> GetFilesWithSameSize(List<File> inputList) {
        long currentFileSize;
        List<File> completeFileList = new ArrayList<File>();
        List<File> fileSizeMatchList = new ArrayList<File>();

        if (inputList.isEmpty())
            return null;

        Collections.sort(inputList, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                return Long.compare(lhs.length(),rhs.length());
                //return lhs.length() - rhs.length();
            }
        });

        currentFileSize = inputList.get(0).length();
        fileSizeMatchList.add(inputList.get(0));

        // Enumerate through all of the files in the listForCompare
        for (int i = 1; i < inputList.size(); i++)
        {
            if (currentFileSize == inputList.get(i).length())
            {
                fileSizeMatchList.add(inputList.get(i));  //содержит файлы с одинаковым размером
            }
            else
            {
                completeFileList.addAll(fileSizeMatchList);
                fileSizeMatchList.clear();
                fileSizeMatchList.add(inputList.get(i));
            }
            currentFileSize = inputList.get(i).length();
        }
        completeFileList.addAll(fileSizeMatchList);
        return completeFileList;
    }
}
