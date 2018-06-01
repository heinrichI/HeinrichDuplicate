package com.heinrich.heinrichduplicate;

public class FileInfo {
    public String Path;
    public Boolean Checked;
    public DuplGroup Group;

    public FileInfo(String path, boolean checked, DuplGroup group) {
        Path = path;
        Checked = checked;
        Group = group;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj))
            return false;
        else {
            FileInfo other = (FileInfo) obj;
            if (Path != other.Path)
                return false;
            return true;
        }
    }
}