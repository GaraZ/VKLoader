package com.github.garaz.vkloader;

import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.dom4j.Element;

/**
 *
 * @author GaraZ
 */
public class ProfileObj {
    private String login;
    private String token;
    private boolean saveToken;
    
    public ProfileObj() {
        this.login = null;
    }
    
    public ProfileObj(String login) {
        this.login = login;
    }
      
    public String getLogin() {
        return login;
    }
    
    public String getToken() {
        return token;
    }
    
    public boolean getSaveToken() {
        return saveToken;
    }
    
    public void setLogin(String login) {
        this.login = login;
    }
    
    public void setToken(String token, boolean saveToken) {
        this.token = token;
        this.saveToken = saveToken;
    }
    
    public void setSaveToken(boolean saveToken) {
        this.saveToken = saveToken;
    }
    
    ProfileObj readFromXML(Element locRoot) throws Exception {
            login = locRoot.valueOf("@Login");
        try {
            token = Crypt.dencrypt(locRoot.valueOf("@Token"));
        } catch(NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | 
                IllegalBlockSizeException | BadPaddingException | IllegalArgumentException |
                UnsupportedEncodingException | UnknownHostException | SocketException e) {
            throw new Exception("Profile "
                    .concat(String.valueOf(login))
                    .concat(" is incorrect")
                    .concat(" Error: ")
                    .concat(e.getMessage()));
        }
        return this;
    }
    
    void writeToXML(Element locRoot) throws Exception {
        try {
            String tokenVal;
            if (saveToken == true) {
                tokenVal = Crypt.encrypt(token);
            } else {
                tokenVal = new String();
            }
            locRoot.addElement("Profile")
                .addAttribute("Login", String.valueOf(login))
                .addAttribute("Token", tokenVal);
        } catch(UnsupportedEncodingException | NoSuchAlgorithmException | 
                NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | 
                BadPaddingException | UnknownHostException | SocketException e) {
            throw new Exception("Profile "
                    .concat(String.valueOf(login))
                    .concat(" is incorrect")
                    .concat(" Error: ")
                    .concat(e.getMessage()));
        }
    }
    
    @Override
    public String toString() {
        return String.valueOf(login);
    }
}
