/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ManagerBean;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jonathan
 */
public class UserApplicationPassword implements EncryptFile, Cloneable {

    public String applicationName;
    public String url;
    public String accountName;
    private String password;

    public String GetPasswrod() {
        return this.password;
    }

    public void SetPassword(String password) {
        this.password = password;
    }

    public Map<String, EncryptType> GetFieldsEncryptTypeList() {
        HashMap<String, EncryptType> fieldsEncryptType = new HashMap<String, EncryptType>();
        //fieldsEncryptType.put("userName", EncryptType.None);
        fieldsEncryptType.put("password", EncryptType.NonReversible);
        return fieldsEncryptType;
    }

    public String GetFilePath() {
        return "data/AccountManager";
    }

    public boolean IsRecordKeyEquals(Object[] curRecord) {
        if (curRecord[0].equals(this.applicationName)
                && curRecord[1].equals(this.url)
                && curRecord[2].equals(this.accountName)) {
            return true;
        }
        return false;
    }

    public Object CloneObject() throws CloneNotSupportedException {
        return this.clone();
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
