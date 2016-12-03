/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ManagerBean;

import java.util.Map;

/**
 *
 * @author Jonathan
 */
public interface EncryptFile {

    enum EncryptType {
        Reversible,
        NonReversible
    }

    //public Map<String, EncryptType> fieldsEncryptType;
    public Map<String, EncryptType> GetFieldsEncryptTypeList();

    public String GetFilePath();

    public boolean IsRecordKeyEquals(Object[] curRecord);

    public Object CloneObject() throws CloneNotSupportedException;
}
