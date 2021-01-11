package MailHeavenPackage.other;

import javax.mail.internet.MimeUtility;

import javax.security.auth.Subject;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Base64;

public class FileNameConverter {
    public static String decode(String filename) {
        String decoded = filename;
        try {
            decoded = MimeUtility.decodeText(filename);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decoded;
    }
    public static String encode(String filename){
        String encoded = filename;
        try {
            encoded = MimeUtility.encodeText(filename, "iso-8859-5", "Q");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return encoded;
    }

}
