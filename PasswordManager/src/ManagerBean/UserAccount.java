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
public class UserAccount implements EncryptFile, Cloneable {

    public String userName;
    private String password;

    public UserAccount(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String GetPassword() {
        return password;
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
        return "data/UserAccount";
    }

    public boolean IsRecordKeyExists(String[] curRecord) {
        if (curRecord[0].equals(this.userName)) {
            return true;
        }
        return false;
    }

    public Object CloneObject()throws CloneNotSupportedException
    {
        return this.clone();
    }
    
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
