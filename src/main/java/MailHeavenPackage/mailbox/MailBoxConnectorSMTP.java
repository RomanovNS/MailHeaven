package MailHeavenPackage.mailbox;

import MailHeavenPackage.other.EmailAddressConverter;
import MailHeavenPackage.other.Mail;
import javafx.util.Pair;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MailBoxConnectorSMTP {
    protected Properties properties;
    protected String email;
    protected String emailAccount;
    protected String emailHost;
    protected String password;
    protected Authenticator authenticator;

    public MailBoxConnectorSMTP(String email, String password){
        this.email = email;
        emailAccount = email.split("@")[0];
        emailHost = email.split("@")[1];
        this.password = password;

        properties = new Properties();
        properties.setProperty("mail.smtp.host", "smtp." + emailHost);
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.port", "465");
        properties.setProperty("mail.smtp.socketFactory.port", "465");
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");


        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.starttls.required", "true");
        properties.setProperty("mail.smtp.ssl.enable", "true");
        properties.setProperty("mail.smtp.user", emailAccount);

        authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailAccount, password);
            }
        };
    }

    public MimeMessage sendMail(String[] recipientEmails, Address senderAddress, String subject, String textPlain, String textHTML, LinkedList<Pair<String, byte[]>> files){
        Session session = Session.getDefaultInstance(properties, authenticator);
        MimeMessage message = null;
        try {
            InternetAddress[] recipientAddresses = new InternetAddress[recipientEmails.length];
            for (int  i = 0, size = recipientEmails.length; i < size; i++){
                recipientAddresses[i] = new InternetAddress(recipientEmails[i]);
            }
            InternetAddress replyTo = new InternetAddress(email);
            Address from = senderAddress;

            MimeBodyPart mimeTextPlain = null;
            if (textPlain != null){
                if (textPlain.length() > 0){
                    mimeTextPlain = new MimeBodyPart();
                    mimeTextPlain.setContent(textPlain, "text/plain; charset=UTF-8");
                }
            }
            MimeBodyPart mimeTextHTML = null;
            if (textHTML != null){
                if (textHTML.length() > 0){
                    mimeTextHTML = new MimeBodyPart();
                    mimeTextHTML.setContent(textHTML, "text/html; charset=UTF-8");
                }
            }

            message = new MimeMessage(session);
            message.setFrom(from);
            message.setReplyTo(new Address[]{replyTo});
            message.setRecipients(Message.RecipientType.TO, recipientAddresses);
            message.setSubject(subject);

            int fileCount = 0;
            if (files != null){
                if (files.size() > 0){
                    fileCount = files.size();
                }
            }

            if (fileCount > 0){
                if (mimeTextPlain == null && mimeTextHTML == null){
                    if (fileCount == 1){
                        message.setContent(files.get(0).getValue(), "file");
                        message.setFileName(files.get(0).getKey());
                    }
                    else{
                        MimeMultipart mimeMultipart = new MimeMultipart();
                        for (Pair<String, byte[]> file : files){
                            MimeBodyPart mimeBodyPartFile = new MimeBodyPart();
                            mimeBodyPartFile.setContent(file.getValue(), "file");
                            mimeBodyPartFile.setFileName(file.getKey());
                            mimeMultipart.addBodyPart(mimeBodyPartFile);
                        }
                        message.setContent(mimeMultipart);
                    }
                }
                else{
                    if (mimeTextPlain != null && mimeTextHTML != null){
                        MimeMultipart mimeMultipartAlternative = new MimeMultipart();
                        mimeMultipartAlternative.addBodyPart(mimeTextPlain);
                        mimeMultipartAlternative.addBodyPart(mimeTextHTML);
                        mimeMultipartAlternative.setSubType("alternative");
                        MimeBodyPart mimeBodyPartAltermative = new MimeBodyPart();
                        mimeBodyPartAltermative.setContent(mimeMultipartAlternative);

                        MimeMultipart mimeMultipart = new MimeMultipart();
                        mimeMultipart.addBodyPart(mimeBodyPartAltermative);
                        for (Pair<String, byte[]> file : files){
                            MimeBodyPart mimeBodyPartFile = new MimeBodyPart();
                            mimeBodyPartFile.setContent(file.getValue(), "file");
                            mimeBodyPartFile.setFileName(file.getKey());
                            mimeMultipart.addBodyPart(mimeBodyPartFile);
                        }
                        message.setContent(mimeMultipart);
                    }
                    else {
                        if (mimeTextPlain != null){
                            MimeMultipart mimeMultipart = new MimeMultipart();
                            mimeMultipart.addBodyPart(mimeTextPlain);
                            for (Pair<String, byte[]> file : files){
                                File file1 = new File(file.getKey());
                                MimeBodyPart mimeBodyPartFile = new MimeBodyPart();
                                mimeBodyPartFile.setContent(file.getValue(), "application/txt");
                                mimeBodyPartFile.setFileName(file.getKey());
                                mimeMultipart.addBodyPart(mimeBodyPartFile);
                            }
                            message.setContent(mimeMultipart);
                        }
                        if (mimeTextHTML != null){
                            MimeMultipart mimeMultipart = new MimeMultipart();
                            mimeMultipart.addBodyPart(mimeTextHTML);
                            for (Pair<String, byte[]> file : files){
                                MimeBodyPart mimeBodyPartFile = new MimeBodyPart();
                                mimeBodyPartFile.setContent(file.getValue(), "file");
                                mimeBodyPartFile.setFileName(file.getKey());
                                mimeMultipart.addBodyPart(mimeBodyPartFile);
                            }
                            message.setContent(mimeMultipart);
                        }
                    }
                }
            }
            else {
                if (mimeTextPlain != null && mimeTextHTML != null){
                    MimeMultipart mimeMultipart = new MimeMultipart();
                    mimeMultipart.addBodyPart(mimeTextPlain);
                    mimeMultipart.addBodyPart(mimeTextHTML);
                    mimeMultipart.setSubType("alternative");
                    message.setContent(mimeMultipart);
                }
                else {
                    if (mimeTextPlain != null){
                        message.setContent(mimeTextPlain.getContent(), mimeTextPlain.getContentType());
                    }
                    if (mimeTextHTML != null){
                        message.setContent(mimeTextHTML.getContent(), mimeTextHTML.getContentType());
                    }
                }
            }
            System.out.println("получаю транспорт");
            Transport transport = session.getTransport("smtp");
            System.out.println("коннекчусь");
            transport.connect("smtp." + emailHost, emailAccount, password);
            System.out.println("отправляю");
            transport.sendMessage(message, message.getAllRecipients());
            System.out.println("отправлено");
            transport.close();

        }catch (Exception e){
            System.out.println("ошибка");
            e.printStackTrace();
            return null;
        }
        return message;
    }


}
