package MailHeavenPackage.other;

import javax.mail.Address;
import javax.mail.Multipart;
import javax.xml.crypto.Data;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Mail {
    private int number;
    private Address sender;
    private String senderStr;
    private String subject;
    private List<Address> recipients;
    private List<String> recipientsStr;
    private Date date;
    private String body;
    private boolean seen;
    private boolean important;
    private List<String> fileNames;

    public Mail(){
        number = 0;
        sender = null;
        senderStr = null;
        subject = null;
        recipients = null;
        recipientsStr = null;
        date = null;
        body = null;
        seen = false;
        important = false;
        fileNames = null;
    }

    public Mail(int number, Address sender, List<Address> recipients, String subject, Date date, String body, boolean seen, boolean important, List<String> fileNames){
        this.number = number;
        this.sender = sender;
        this.recipients = recipients;
        this.senderStr = EmailAddressConverter.decode(sender);
        this.recipientsStr = new LinkedList<String>();
        for (Address recipient : recipients){
            recipientsStr.add(EmailAddressConverter.decode(recipient));
        }
        this.subject = subject;
        this.date = date;
        this.body = body;
        this.fileNames = fileNames;
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

    public List<Address> getRecipients() {
        return recipients;
    }

    public List<String> getRecipientsStr() {
        return recipientsStr;
    }

    public String getSubject() {
        return subject;
    }

    public Date getDate() {
        return date;
    }

    public String getBody() {
        return body;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    @Override
    public String toString() {
        return "Mail{" +
                "number=" + number +
                ", sender=" + sender +
                ", senderStr='" + senderStr + '\'' +
                ", subject='" + subject + '\'' +
                ", recipients=" + recipients +
                ", recipientsStr=" + recipientsStr +
                ", date=" + date +
                ", body='" + body + '\'' +
                ", seen=" + seen +
                ", important=" + important +
                ", fileNames=" + fileNames +
                '}';
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
}
