package MailHeavenPackage.controllers;

import MailHeavenPackage.mailbox.MailBoxConnectorIMAP;
import MailHeavenPackage.mailbox.MailBoxConnectorSMTP;
import MailHeavenPackage.models.MailBoxAccount;
import MailHeavenPackage.models.User;
import MailHeavenPackage.other.EmailAddressConverter;
import MailHeavenPackage.other.Letter;
import MailHeavenPackage.other.Mail;
import MailHeavenPackage.repos.MailBoxAccountRepository;
import MailHeavenPackage.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.Message;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

@Controller
public class MainController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailBoxAccountRepository mailBoxAccountRepository;

    @GetMapping("/main")
    public String main(Model model){
        return "mainPage";
    }

    @PostMapping("/addAccount")
    public String addAccount(Principal principal, MailBoxAccount mailBoxAccount, Model model){
        User user = userRepository.findByUsername(principal.getName());
        mailBoxAccount.setUserid(user);
        mailBoxAccountRepository.save(mailBoxAccount);
        return "redirect:/main";
    }

    @PostMapping("/sendLetter")
    public String sendLetter(Principal principal, @RequestParam("senderEmail") String senderEmail,
                             @RequestParam("recipientEmail") String recipientEmail, @RequestParam("title") String title,
                             @RequestParam("letterText") String letterText, Model model){
        User user = userRepository.findByUsername(principal.getName());
        Set<MailBoxAccount> mailBoxAccountSet = user.getAccounts();

        mailBoxAccountSet.forEach( mailBoxAccount -> {
            if (mailBoxAccount.getEmail().equals(senderEmail)){

                MailBoxConnectorSMTP mailBoxConnectorSMTP = new MailBoxConnectorSMTP(mailBoxAccount.getEmail(), mailBoxAccount.getPassword());
                try {
                    String[] recp = new String[1];
                    recp[0] = recipientEmail;
                    MimeMessage message = mailBoxConnectorSMTP.sendMail(recp, new InternetAddress(mailBoxAccount.getEmail()),
                            title, null, letterText, null);
                    if (message != null){
                        System.out.println("Закидываю письмо к отправленным");
                        MailBoxConnectorIMAP mailBoxConnectorIMAP = new MailBoxConnectorIMAP(mailBoxAccount.getEmail(), mailBoxAccount.getPassword());
                        mailBoxConnectorIMAP.saveSendedMessage(message);
                    }

                } catch (AddressException e) {
                    e.printStackTrace();
                }
            }
        });
        return "redirect:/main";
    }
}
