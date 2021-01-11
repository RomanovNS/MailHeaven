package MailHeavenPackage.other;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.Base64;

public class EmailAddressConverter {
    public static String decode(String address){
        String decoded = address;
        try {
            decoded = MimeUtility.decodeText(address);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decoded;
    }
    public static String decode(Address address){
        String strAddr = address.toString();
        return decode(strAddr);
    }
    public static String encode(String address){
        String addr = address.substring(address.lastIndexOf("<"), address.lastIndexOf(">")+1);
        String toEncode = address.substring(0, address.lastIndexOf("<")-1);
        String encoded = toEncode;
        try {
            encoded = MimeUtility.encodeText(toEncode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encoded + " " + addr;
    }
    public static String onlyEmail(String address){
        if (address.contains("<") && address.contains(">")){
            return address.substring(address.lastIndexOf("<") + 1, address.lastIndexOf(">"));
        }
        return address;
    }
    public static String onlySenderName(String address){
        if (address.lastIndexOf("<") - 1 > 0 && (address.contains("<") && address.contains(">"))){
            return address.substring(0, address.lastIndexOf("<")-1);
        }
        else {
            String em = onlyEmail(address);
            return em.substring(0, em.lastIndexOf("@"));
        }
    }
}
