package MailHeavenPackage.other;

import java.util.List;

public class Letter {
    private String date;
    private String time;
    private String senderName;
    private String senderEmail;
    private String recipientName;
    private String recipientEmail;
    private String title;
    private String html;
    private boolean seen;
    private boolean important;
    private List<String> attachements;

    public Letter() {
    }

    public Letter(String date, String time, String senderName, String senderEmail, String recipientName,
                  String recipientEmail, String title, String html, boolean seen, boolean important, List<String> attachements) {
        this.date = date;
        this.time = time;
        this.senderName = senderName;
        this.senderEmail = senderEmail;
        this.recipientName = recipientName;
        this.recipientEmail = recipientEmail;
        this.title = title;
        this.html = html;
        this.seen = seen;
        this.important = important;
        this.attachements = attachements;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public List<String> getAttachements() {
        return attachements;
    }

    public void setAttachements(List<String> attachements) {
        this.attachements = attachements;
    }
}
