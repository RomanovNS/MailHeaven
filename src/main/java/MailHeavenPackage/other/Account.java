package MailHeavenPackage.other;

import java.util.List;

public class Account {
    private String eMail;
    private List<Folder> folders;

    public Account(){

    }
    public Account(String eMail, List<Folder> folders) {
        this.eMail = eMail;
        this.folders = folders;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public List<Folder> getFolders() {
        return folders;
    }

    public void setFolders(List<Folder> folders) {
        this.folders = folders;
    }
}
