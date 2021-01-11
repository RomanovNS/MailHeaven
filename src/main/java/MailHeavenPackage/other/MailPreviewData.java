package MailHeavenPackage.other;

import javax.mail.Address;
import java.util.Date;

public class MailPreviewData {
    private int number;
    private Address sender;
    private String senderStr;
    private String subject;
    private Date date;
    private boolean seen;
    private boolean attachments;
    private boolean important;

    public MailPreviewData(){
        number = 0;
        sender = null;
        subject = null;
        date = null;
        seen = false;
        attachments = false;
        important = false;
    }

    public MailPreviewData(int number, Address sender, String subject, Date date, boolean seen, boolean attachments, boolean important){
        this.number = number;
        this.sender = sender;
        this.senderStr = EmailAddressConverter.decode(sender);
        this.subject = subject;
        this.date = date;
        this.seen = seen;
        this.attachments = attachments;
        this.important = important;
    }

    public int getNumber() {
        return number;
    }

    public Address getSender() {
        return sender;
    }

    public String getSenderStr() {
        return senderStr;
    }

    public String getSubject() {
        return subject;
    }

    public Date getDate() {
        return date;
    }

    public boolean isSeen() {
        return seen;
    }

    public boolean isAttachments() {
        return attachments;
    }

    public boolean isImportant() {
        return important;
    }

    @Override
    public String toString() {
        return "MailPreviewData{" +
                "number='" + number + '\'' +
                ", sender='" + sender + '\'' +
                ", senderStr='" + senderStr + '\'' +
                ", subject='" + subject + '\'' +
                ", date=" + date +
                ", seen=" + seen +
                ", attachments=" + attachments +
                ", important=" + important +
                '}';
    }
}
