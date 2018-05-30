package com.heinrich.heinrichduplicate;

public class DirInfo {
    public String Path;
    public Boolean Checked;

    public DirInfo(String folderLocation, boolean checked) {
        Path = folderLocation;
        Checked = checked;
    }
}
