package MailHeavenPackage.controllers;

import MailHeavenPackage.mailbox.MailBoxConnectorIMAP;
import MailHeavenPackage.models.MailBoxAccount;
import MailHeavenPackage.models.User;
import MailHeavenPackage.other.*;
import MailHeavenPackage.repos.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@RestController
public class AjaxController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/getAccounts")
    public String getAccounts(Principal principal){
        if (principal == null) return "[]";
        User user = userRepository.findByUsername(principal.getName());
        if (user == null) return "[]";
        Set<MailBoxAccount> mailBoxAccountSet = user.getAccounts();

        List<Account> accounts = new LinkedList<>();
        mailBoxAccountSet.forEach( mailBoxAccount -> {
            MailBoxConnectorIMAP mailBoxConnectorIMAP = new MailBoxConnectorIMAP(mailBoxAccount.getEmail(), mailBoxAccount.getPassword());
            List<String> folderNames = mailBoxConnectorIMAP.getFolderNames();
            LinkedList<Folder> folders = new LinkedList<>();
            folderNames.forEach(folderName -> {
                folders.add(new Folder(folderName, mailBoxConnectorIMAP.getFolderCount(folderName)));
            });
            accounts.add(new Account(mailBoxAccount.getEmail(), folders));
        });

        String json = "[]";
        try {
            json = new ObjectMapper().writeValueAsString(accounts);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return json;
    }

    @GetMapping("/getFolder")
    public String getFolders(Principal principal, @RequestParam("accountEmail") String email,
                             @RequestParam("folderName") String folderName, @RequestParam("fromIndex") int from,
                             @RequestParam("toIndex") int to){

        if (principal == null) return "{\"lettersTotalCount\": 0, \"letters\": []}";
        User user = userRepository.findByUsername(principal.getName());
        if (user == null) return "{\"lettersTotalCount\": 0, \"letters\": []}";
        Set<MailBoxAccount> mailBoxAccountSet = user.getAccounts();
        if (mailBoxAccountSet.size() == 0) return "{\"lettersTotalCount\": 0, \"letters\": []}";
        LinkedList<Letter> letters = new LinkedList<Letter>();
        int[] count = new int[1];
        int c;
        mailBoxAccountSet.forEach( mailBoxAccount -> {
            if (mailBoxAccount.getEmail().equals(email)){
                MailBoxConnectorIMAP mailBoxConnectorIMAP = new MailBoxConnectorIMAP(mailBoxAccount.getEmail(), mailBoxAccount.getPassword());
                count[0] = mailBoxConnectorIMAP.getFolderCount(folderName);
                int newFrom = count[0] - to + 1;
                int newTo = count[0] - from + 1;
                if (newFrom < 1) newFrom = 1;
                if (newTo < 1) newTo = 1;
                if (newFrom > count[0]) newFrom = count[0];
                if (newTo > count[0]) newTo = count[0];
                List<Mail> mailList = mailBoxConnectorIMAP.getMessagesAsMail(folderName, newFrom, newTo);
                for (int i = 0; i < mailList.size(); i++){
                    Mail mail = mailList.get(i);
                    String date = new SimpleDateFormat("DD.MM.YYYY").format(mail.getDate());
                    String time = new SimpleDateFormat("hh:mm").format(mail.getDate());
                    String senderName = EmailAddressConverter.onlySenderName(mail.getSenderStr());
                    String senderEmail = EmailAddressConverter.onlyEmail(mail.getSenderStr());
                    String recipientName = EmailAddressConverter.onlySenderName(mail.getRecipientsStr().get(0));
                    String recipientEmail = EmailAddressConverter.onlyEmail(mail.getRecipientsStr().get(0));
                    String title = mail.getSubject();
                    String html = mail.getBody();
                    boolean seen = mail.isSeen();
                    boolean important = mail.isImportant();
                    List<String> files = mail.getFileNames();
                    letters.addFirst(new Letter(date, time, senderName, senderEmail, recipientName, recipientEmail, title, html, seen, important, files));
                }
            }
        });

        String json = "{\"lettersTotalCount\": 0, \"letters\": []}";
        try {
            json = new ObjectMapper().writeValueAsString(new LettersContainer(count[0], letters));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }




}
