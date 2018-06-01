package com.heinrich.heinrichduplicate;

import java.util.ArrayList;
import java.util.List;

public class DuplGroup {
    // Не рекомендуется: private HashMap<Integer, Boolean> mCheckedMap = new HashMap<>();
    //public SparseBooleanArray CheckedMap;
    public String Header;
    public List<FileInfo> Files;

    public DuplGroup(String header, List<FileInfo> files) {
        Header = header;
        Files = files;
    }

    public DuplGroup(String header) {
        Header = header;
    }

    public boolean IsAllChecked(FileInfo willChecked) {
        boolean allChecked = true;
        for (FileInfo current: Files) {
            if (!current.Checked
                    && !willChecked.equals(current))
            {
                allChecked = false;
                break;
            }
        }
        return allChecked;
    }
}