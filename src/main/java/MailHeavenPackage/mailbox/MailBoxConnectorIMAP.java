package MailHeavenPackage.mailbox;

import MailHeavenPackage.other.EmailAddressConverter;
import MailHeavenPackage.other.FileNameConverter;
import MailHeavenPackage.other.Mail;
import MailHeavenPackage.other.MailPreviewData;
import javafx.util.Pair;
import org.thymeleaf.standard.expression.MessageExpression;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.*;
import javax.mail.util.SharedByteArrayInputStream;
import java.io.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class MailBoxConnectorIMAP {
    protected Properties properties;
    protected String email;
    protected String emailAccount;
    protected String emailHost;
    protected String password;
    protected Authenticator authenticator;
    protected Session session;
    protected Store store;

    public MailBoxConnectorIMAP(String email, String password){
        this.email = email;
        this.emailAccount = email.split("@")[0];
        this.emailHost = email.split("@")[1];
        this.password = password;

        properties = new Properties();
        properties.setProperty("mail.imap.ssl.enable", "true");
        properties.setProperty("mail.imap.host", "imap."+emailHost);
        properties.setProperty("mail.imap.user", emailAccount);
        properties.setProperty("mail.imap.port", "993");
        properties.setProperty("mail.store.protocol", "imap");

        authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailAccount, password);
            }
        };

        System.out.println("Пытаюсь подключиться к ящику " + email);
        session = Session.getDefaultInstance(properties, authenticator);
        try {
            store = session.getStore("imaps");
            store.connect("imap."+emailHost, emailAccount, password);
            System.out.println("Успешно подключилчся");
        } catch (Exception e) {
            System.out.println("Подключиться не получилось");
            e.printStackTrace();
        }
    }
    public List<String> getFolderNames(){
        List<String> folderNames = new LinkedList<String>();
        //Session session = Session.getDefaultInstance(properties, authenticator);
        try {
            //Store store = session.getStore("imaps");
            //store.connect("imap."+emailHost, emailAccount, password);
            Folder[] folders = store.getDefaultFolder().list("*");
            for (javax.mail.Folder folder : folders) {
                if ((folder.getType() & javax.mail.Folder.HOLDS_MESSAGES) != 0) {
                    folderNames.add(folder.getName());
                }
            }
            //store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return folderNames;
    }
    public int getFolderCount(String folderName){
        //Session session = Session.getDefaultInstance(properties, authenticator);
        int folderCount = 0;
        try {
            //Store store = session.getStore("imaps");
            //store.connect("imap."+emailHost, emailAccount, password);

            Folder[] folders = store.getDefaultFolder().list("*");
            for (javax.mail.Folder folder : folders) {
                if (folder.getName().equals(folderName)) {
                    folderCount = folder.getMessageCount();
                }
            }

            //Folder folder = store.getFolder(folderName);
            //folderCount = folder.getMessageCount();

            //store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return folderCount;
    }
    public int getFolderCountUnread(String folderName){
        //Session session = Session.getDefaultInstance(properties, authenticator);
        int folderCount = 0;
        try {
            //Store store = session.getStore("imaps");
            //store.connect("imap."+emailHost, emailAccount, password);
            Folder folder = store.getFolder(folderName);
            folderCount = folder.getUnreadMessageCount();
            //store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return folderCount;
    }
    public int getFolderCountNew(String folderName){
        //Session session = Session.getDefaultInstance(properties, authenticator);
        int folderCount = 0;
        try {
            //Store store = session.getStore("imaps");
            //store.connect("imap."+emailHost, emailAccount, password);
            Folder folder = store.getFolder(folderName);
            folderCount = folder.getNewMessageCount();
            //store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return folderCount;
    }
    public MailPreviewData getMessagePreview(String folderName, int id){
        //Session session = Session.getDefaultInstance(properties, authenticator);
        MailPreviewData mailPreviewData = new MailPreviewData();
        try {
            //Store store = session.getStore("imaps");
            //store.connect("imap."+emailHost, emailAccount, password);

            Folder folder = store.getFolder(folderName);
            folder.open(Folder.READ_ONLY);
            MimeMessage message = (MimeMessage)folder.getMessage(id);

            boolean hasAttachments = false;
            if (message.getContentType().contains("multipart")){
                Multipart multipart = (Multipart) message.getContent();
                for (int i = 0, size = multipart.getCount(); i < size; i++){
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (bodyPart.getFileName() != null)
                        hasAttachments = true;
                }
            }

            mailPreviewData = new MailPreviewData(id, message.getSender(), message.getSubject(), message.getReceivedDate(),
                    message.getFlags().contains(Flags.Flag.SEEN), hasAttachments,
                    message.getFlags().contains(Flags.Flag.FLAGGED));

            folder.close(false);
            //store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mailPreviewData;
    }
    public List<MailPreviewData> getMessagePreviews(String folderName, int start, int end){
        //Session session = Session.getDefaultInstance(properties, authenticator);
        LinkedList<MailPreviewData> mailPreviewDataList = new LinkedList<MailPreviewData>();
        try {
            //Store store = session.getStore("imaps");
            //store.connect("imap."+emailHost, emailAccount, password);

            Folder folder = store.getFolder(folderName);
            folder.open(Folder.READ_ONLY);

            Message[] messages = folder.getMessages(start, end);
            for (Message message : messages){
                boolean hasAttachments = false;
                if (message.getContentType().contains("multipart")){
                    Multipart multipart = (Multipart) message.getContent();
                    for (int i = 0, size = multipart.getCount(); i < size; i++){
                        BodyPart bodyPart = multipart.getBodyPart(i);
                        if (bodyPart.getFileName() != null)
                            hasAttachments = true;
                    }
                }
                MimeMessage mimeMessage = (MimeMessage)message;
                mailPreviewDataList.add(new MailPreviewData(mimeMessage.getMessageNumber(), mimeMessage.getFrom()[0], mimeMessage.getSubject(),
                        mimeMessage.getReceivedDate(), mimeMessage.getFlags().contains(Flags.Flag.SEEN), hasAttachments,
                        mimeMessage.getFlags().contains(Flags.Flag.FLAGGED)));

            }

            folder.close(false);
            //store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mailPreviewDataList;
    }
    public MimeMessage getOfflineMessage(String folderName, int id){
        //Session session = Session.getDefaultInstance(properties, authenticator);
        MimeMessage offlineMessage = null;
        try {
            //Store store = session.getStore("imaps");
            //store.connect("imap."+emailHost, emailAccount, password);

            Folder folder = store.getFolder(folderName);
            folder.open(Folder.READ_ONLY);
            MimeMessage message = (MimeMessage)folder.getMessage(id);

            offlineMessage = new MimeMessage(message);

            folder.close(true);
            //store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return offlineMessage;
    }
    public List<MimeMessage> getOfflineMessages(String folderName, int from, int to){
        //Session session = Session.getDefaultInstance(properties, authenticator);
        LinkedList<MimeMessage> offlineMessages = new LinkedList<>();
        try {
            //Store store = session.getStore("imaps");
            //store.connect("imap."+emailHost, emailAccount, password);

            Folder[] folders = store.getDefaultFolder().list("*");
            for (javax.mail.Folder folder : folders) {
                if (folder.getName().equals(folderName)) {
                    folder.open(Folder.READ_ONLY);
                    Message[] messages = folder.getMessages(from, to);
                    for (Message message : messages){
                        offlineMessages.add(new MimeMessage((MimeMessage) message));
                    }
                    folder.close(true);
                }
            }


            //Folder folder = store.getFolder(folderName);
            //folder.open(Folder.READ_ONLY);
            //Message[] messages = folder.getMessages(from, to);

            //for (Message message : messages){
            //    offlineMessages.add(new MimeMessage((MimeMessage) message));
            //}

            //folder.close(true);
            //store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return offlineMessages;
    }
    public void changeMessageFlag(String folderName, Flags.Flag flag, int id, boolean value){
        //Session session = Session.getDefaultInstance(properties, authenticator);
        try {
            //Store store = session.getStore("imaps");
            //store.connect("imap."+emailHost, emailAccount, password);

            Folder folder = store.getFolder(folderName);
            folder.open(Folder.READ_WRITE);
            MimeMessage message = (MimeMessage)folder.getMessage(id);
            message.setFlag(flag, value);

            folder.close(true);
            //store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void changeMessagesFlag(String folderName, Flags.Flag flag, int[] ids, boolean value){
        //Session session = Session.getDefaultInstance(properties, authenticator);
        try {
            //Store store = session.getStore("imaps");
            //store.connect("imap."+emailHost, emailAccount, password);

            Folder folder = store.getFolder(folderName);
            folder.open(Folder.READ_WRITE);

            Message[] messages = folder.getMessages(ids);
            for (Message message : messages){
                message.setFlag(flag, value);
            }

            folder.close(true);
            //store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void changeMessageFlagImportant(String folderName, int id, boolean value){
        changeMessageFlag(folderName, Flags.Flag.FLAGGED, id, value);
    }
    public void changeMessagesFlagImportant(String folderName, int[] ids, boolean value){
        changeMessagesFlag(folderName, Flags.Flag.FLAGGED, ids, value);
    }
    public void changeMessageFlagSeen(String folderName, int id, boolean value){
        changeMessageFlag(folderName, Flags.Flag.SEEN, id, value);
    }
    public void changeMessagesFlagSeen(String folderName, int[] ids, boolean value){
        changeMessagesFlag(folderName, Flags.Flag.SEEN, ids, value);
    }
    public void deleteMessage(String folderName, int id){
        changeMessageFlag(folderName, Flags.Flag.DELETED, id, true);
    }
    public void deleteMessages(String folderName, int[] ids){
        changeMessagesFlag(folderName, Flags.Flag.DELETED, ids, true);
    }
    public void moveMessage(String srcFolderName, String dstFolderName, int id){
        //Session session = Session.getDefaultInstance(properties, authenticator);
        try {
            //Store store = session.getStore("imaps");
            //store.connect("imap."+emailHost, emailAccount, password);

            Folder srcFolder = store.getFolder(srcFolderName);
            Folder dstFolder = store.getFolder(dstFolderName);
            srcFolder.open(Folder.READ_WRITE);
            MimeMessage message = (MimeMessage)srcFolder.getMessage(id);

            srcFolder.copyMessages(new Message[]{message}, dstFolder);
            message.setFlag(Flags.Flag.DELETED, true);

            srcFolder.close(true);
            //store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void moveMessages(String srcFolderName, String dstFolderName, int[] ids){
        //Session session = Session.getDefaultInstance(properties, authenticator);
        try {
            //Store store = session.getStore("imaps");
            //store.connect("imap."+emailHost, emailAccount, password);

            Folder srcFolder = store.getFolder(srcFolderName);
            Folder dstFolder = store.getFolder(dstFolderName);
            srcFolder.open(Folder.READ_WRITE);
            Message[] messages = srcFolder.getMessages(ids);
            srcFolder.copyMessages(messages, dstFolder);
            for (Message message : messages){
                message.setFlag(Flags.Flag.DELETED, true);
            }

            srcFolder.close(true);
            //store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void appendMessage(String folderName, MimeMessage mimeMessage){
        //Session session = Session.getDefaultInstance(properties, authenticator);
        try {
            //Store store = session.getStore("imaps");
            //store.connect("imap."+emailHost, emailAccount, password);

            Folder folder = store.getFolder(folderName);
            folder.open(Folder.READ_WRITE);
            folder.appendMessages(new Message[]{mimeMessage});

            folder.close(true);
            //store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<MailPreviewData> searchMessagePreviewsBySearchTerm(String folderName, SearchTerm searchTerm){
        //Session session = Session.getDefaultInstance(properties, authenticator);
        LinkedList<MailPreviewData> mailPreviewDataList = new LinkedList<MailPreviewData>();
        try {
            //Store store = session.getStore("imaps");
            //store.connect("imap."+emailHost, emailAccount, password);

            Folder folder = store.getFolder(folderName);
            folder.open(Folder.READ_ONLY);

            Message[] messages = folder.search(searchTerm);

            for (Message message : messages){
                boolean hasAttachments = false;
                if (message.getContentType().contains("multipart")){
                    Multipart multipart = (Multipart) message.getContent();
                    for (int i = 0, size = multipart.getCount(); i < size; i++){
                        BodyPart bodyPart = multipart.getBodyPart(i);
                        if (bodyPart.getFileName() != null)
                            hasAttachments = true;
                    }
                }
                MimeMessage mimeMessage = (MimeMessage)message;
                mailPreviewDataList.add(new MailPreviewData(message.getMessageNumber(), mimeMessage.getFrom()[0], mimeMessage.getSubject(),
                        mimeMessage.getReceivedDate(), mimeMessage.getFlags().contains(Flags.Flag.SEEN), hasAttachments,
                        mimeMessage.getFlags().contains(Flags.Flag.FLAGGED)));

            }
            folder.close(false);
            //store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mailPreviewDataList;
    }
    public List<MailPreviewData> searchMessagePreviewsByFlag(String folderName, Flags.Flag flag, boolean flagValue){
        SearchTerm searchTerm = new FlagTerm(new Flags(flag), flagValue);
        return searchMessagePreviewsBySearchTerm(folderName, searchTerm);
    }
    public List<MailPreviewData> searchMessagePreviewsByFlagSeen(String folderName, boolean flagValue){
        return searchMessagePreviewsByFlag(folderName, Flags.Flag.SEEN, flagValue);
    }
    public List<MailPreviewData> searchMessagePreviewsByFlagImportant(String folderName, boolean flagValue){
        return searchMessagePreviewsByFlag(folderName, Flags.Flag.FLAGGED, flagValue);
    }
    public List<MailPreviewData> searchMessagePreviewsBySender(String folderName, String senderEmail){
        List<MailPreviewData> mailPreviewDataList = new LinkedList<MailPreviewData>();
        try{
            SearchTerm searchTerm = new FromTerm(new InternetAddress(senderEmail));
            mailPreviewDataList = searchMessagePreviewsBySearchTerm(folderName, searchTerm);
        } catch (Exception e){
            e.printStackTrace();
        }
        return mailPreviewDataList;
    }
    public List<MailPreviewData> searchMessagePreviewsByWords(String folderName, String words){
        List<MailPreviewData> mailPreviewDataList = new LinkedList<MailPreviewData>();
        try{
            SearchTerm searchTerm = new OrTerm( new SubjectTerm(words), new BodyTerm(words));
            mailPreviewDataList = searchMessagePreviewsBySearchTerm(folderName, searchTerm);
        } catch (Exception e){
            e.printStackTrace();
        }
        return mailPreviewDataList;
    }
    public String getMessageAsString(String folderName, int id){
        String messageStr = "";
        //Session session = Session.getDefaultInstance(properties, authenticator);
        try {
            //Store store = session.getStore("imaps");
            //store.connect("imap."+emailHost, emailAccount, password);

            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            MimeMessage message = (MimeMessage)folder.getMessage(id);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            message.writeTo(byteArrayOutputStream);
            byteArrayOutputStream.close();
            messageStr = byteArrayOutputStream.toString("UTF-8");

            folder.close(true);
            //store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageStr;
    }
    private String searchTextPlain(Multipart multipart) throws MessagingException, IOException {
        String textPlain = null;
        int partsCount = multipart.getCount();
        for (int i = 0; i < partsCount; i++){
            BodyPart bodyPart = multipart.getBodyPart(i);
            String temp = null;
            if (bodyPart.getContentType().contains("multipart")){
                temp = searchTextPlain((Multipart)bodyPart.getContent());
            }
            else {
                if (bodyPart.getContentType().contains("text/plain")){
                    temp = (String) bodyPart.getContent();
                }
            }
            if (temp != null){
                textPlain = temp;
            }
        }
        return textPlain;
    }
    private String searchTextHTML(Multipart multipart) throws MessagingException, IOException {
        String textHTML = null;
        int partsCount = multipart.getCount();
        for (int i = 0; i < partsCount; i++){
            BodyPart bodyPart = multipart.getBodyPart(i);
            String temp = null;
            if (bodyPart.getContentType().contains("multipart")){
                temp = searchTextHTML((Multipart)bodyPart.getContent());
            }
            else {
                String contentType = bodyPart.getContentType();
                if (contentType.contains("text/") && contentType.contains("html")){
                    temp = (String) bodyPart.getContent();
                }
            }
            if (temp != null){
                textHTML = temp;
            }
        }
        return textHTML;
    }
    private String getTextFromMultipart(Multipart multipart) throws MessagingException, IOException {
        int partsCount = multipart.getCount();
        String bodyText = searchTextHTML(multipart);
        if (bodyText == null)
            bodyText = searchTextPlain(multipart);
        if (bodyText == null)
            bodyText = "";
        return bodyText;
    }
    private LinkedList<String> getAttachmentsFileNames(Multipart multipart) throws MessagingException, IOException {
        LinkedList<String> fileNames = new LinkedList<>();
        int partsCount = multipart.getCount();
        for (int i = 0; i < partsCount; i++){
            BodyPart bodyPart = multipart.getBodyPart(i);
            if (bodyPart.getContentType().contains("multipart")){
                fileNames.addAll(getAttachmentsFileNames((Multipart)bodyPart.getContent()));
            }
            else {
                String fileName = bodyPart.getFileName();
                if (fileName != null){
                    fileNames.add(FileNameConverter.decode(fileName));
                }
            }
        }
        return fileNames;
    }
    private byte[] getFileBytes(Multipart multipart, String fileName) throws MessagingException, IOException {
        byte[] fileBytes = null;
        int partsCount = multipart.getCount();
        for (int i = 0; i < partsCount; i++){
            BodyPart bodyPart = multipart.getBodyPart(i);
            if (bodyPart.getContentType().contains("multipart")){
                fileBytes = getFileBytes((Multipart)bodyPart.getContent(), fileName);
            }
            else {
                String currentFileName = bodyPart.getFileName();
                if (currentFileName != null){
                    String decodedFileName = FileNameConverter.decode(currentFileName);
                    if (decodedFileName.equals(fileName)){
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        InputStream inputStream = bodyPart.getInputStream();
                        byte[] buffer = new byte[4096];
                        int bytesRead = 0;
                        while ((bytesRead = inputStream.read(buffer)) != -1){
                            byteArrayOutputStream.write(buffer, 0, bytesRead);
                        }
                        byteArrayOutputStream.close();
                        fileBytes = byteArrayOutputStream.toByteArray();
                    }
                }
            }
        }
        return fileBytes;
    }
    public Mail getMessageAsMail(String folderName, int id){
        //Session session = Session.getDefaultInstance(properties, authenticator);
        Mail mail = null;
        try {
            //Store store = session.getStore("imaps");
            //store.connect("imap."+emailHost, emailAccount, password);

            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            MimeMessage message = (MimeMessage)folder.getMessage(id);

            int number = message.getMessageNumber();
            Address sender = message.getFrom()[0];
            List<Address> recipients = new LinkedList<Address>();
            for (Address recipient : message.getAllRecipients()){
                recipients.add(recipient);
            }
            String subject = message.getSubject();
            Date date = message.getReceivedDate();

            boolean seen = message.getFlags().contains(Flags.Flag.SEEN);
            boolean important = message.getFlags().contains(Flags.Flag.FLAGGED);

            String body = "";
            List<String> fileNames = new LinkedList<String>();

            if (message.getContentType().contains("multipart")){
                Multipart multipart = (Multipart) message.getContent();
                body = getTextFromMultipart(multipart);
                fileNames = getAttachmentsFileNames(multipart);
            }
            else{
                if (message.getContentType().contains("text")){
                    body = (String) message.getContent();
                }
                if (message.getFileName() != null){
                    fileNames.add(FileNameConverter.decode(message.getFileName()));
                }
            }

            mail = new Mail(number, sender, recipients, subject, date, body, seen, important, fileNames);

            folder.close(true);
            //store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mail;
    }
    public List<Mail> getMessagesAsMail(String folderName, int from, int to){
        List<MimeMessage> mimeMessages = getOfflineMessages(folderName, from, to);

        List<Mail> mailList = new LinkedList<>();
        for (int i = 0; i < mimeMessages.size(); i++){
            try{
                MimeMessage message = mimeMessages.get(i);
                int number = from + i;
                Address sender = message.getFrom()[0];
                List<Address> recipients = new LinkedList<Address>();
                for (Address recipient : message.getAllRecipients()){
                    recipients.add(recipient);
                }
                String subject = message.getSubject();
                Date date = message.getReceivedDate();
                if (date == null) date = message.getSentDate();

                boolean seen = message.getFlags().contains(Flags.Flag.SEEN);
                boolean important = message.getFlags().contains(Flags.Flag.FLAGGED);

                String body = "";
                List<String> fileNames = new LinkedList<String>();

                if (message.getContentType().contains("multipart")){
                    Multipart multipart = (Multipart) message.getContent();
                    body = getTextFromMultipart(multipart);
                    fileNames = getAttachmentsFileNames(multipart);
                }
                else{
                    if (message.getContentType().contains("text")){
                        body = (String) message.getContent();
                    }
                    if (message.getFileName() != null){
                        fileNames.add(FileNameConverter.decode(message.getFileName()));
                    }
                }

                mailList.add(new Mail(number, sender, recipients, subject, date, body, seen, important, fileNames));
            } catch (Exception e){
                e.printStackTrace();
            }

        }

        return mailList;
    }
    public byte[] getMessageAttachedFile(String folderName, int id, String filename){
        byte[] fileBytes = new byte[]{};
        //Session session = Session.getDefaultInstance(properties, authenticator);
        try {
            //Store store = session.getStore("imaps");
            //store.connect("imap."+emailHost, emailAccount, password);

            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            MimeMessage message = (MimeMessage)folder.getMessage(id);

            if (message.getContentType().contains("multipart")){
                Multipart multipart = (Multipart) message.getContent();
                fileBytes = getFileBytes(multipart, filename);
            }
            else {
                if (message.getFileName() != null){
                    if (filename.equals(FileNameConverter.decode(message.getFileName()))){
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        InputStream inputStream = message.getInputStream();
                        byte[] buffer = new byte[4096];
                        int bytesRead = 0;
                        while ((bytesRead = inputStream.read(buffer)) != -1){
                            byteArrayOutputStream.write(buffer, 0, bytesRead);
                        }
                        byteArrayOutputStream.close();
                        fileBytes = byteArrayOutputStream.toByteArray();
                    }
                }
            }

            folder.close(true);
            //store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileBytes;
    }
    public boolean createFolder(String folderName){
        //Session session = Session.getDefaultInstance(properties, authenticator);
        boolean isCreated = false;
        try {
            //Store store = session.getStore("imaps");
            //store.connect("imap."+emailHost, emailAccount, password);

            Folder folder = store.getFolder(folderName);

            if (!folder.exists()){
                if (folder.create(Folder.HOLDS_MESSAGES)) {
                    folder.setSubscribed(true);
                    isCreated = true;
                }
            }

            //store.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return isCreated;
    }
    public boolean deleteFolder(String folderName){
        //Session session = Session.getDefaultInstance(properties, authenticator);
        boolean isDeleted = false;
        try {
            //Store store = session.getStore("imaps");
            //store.connect("imap."+emailHost, emailAccount, password);

            Folder folder = store.getFolder(folderName);

            if (folder.exists()){
                folder.setSubscribed(false);
                folder.delete(true);
                isDeleted = true;
            }

            //store.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return isDeleted;
    }
    public boolean saveSendedMessage(MimeMessage message){
        //Session session = Session.getDefaultInstance(properties, authenticator);
        try {
            //Store store = session.getStore("imaps");
            //store.connect("imap."+emailHost, emailAccount, password);
            Folder[] folders = store.getDefaultFolder().list("*");
            for (javax.mail.Folder folder : folders) {
                if ((folder.getType() & javax.mail.Folder.HOLDS_MESSAGES) != 0) {
                    String folderName = folder.getName();
                    if (folderName.toLowerCase().equals("sentbox") || folderName.toLowerCase().equals("sent") || folderName.toLowerCase().equals("отправленные")){
                        folder.open(Folder.READ_WRITE);
                        folder.appendMessages(new Message[]{message});
                        folder.close(true);
                    }
                }
            }
            //store.close();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
