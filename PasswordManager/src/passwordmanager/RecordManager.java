/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import ManagerBean.EncryptFile;
import ManagerBean.EncryptFile.EncryptType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Jonathan
 */
public class RecordManager {

    public EncryptFile objectStruct;
    protected File file;

    public RecordManager(EncryptFile encryptFile) {
        this.objectStruct = encryptFile;
        this.file = new File(encryptFile.GetFilePath());

        String path = file.getParent();
        File directory = new File(path);

        //create directory
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    public boolean CheckRecordEquals(EncryptFile curRecord) {
        Field[] fields = this.objectStruct.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                fields[i].setAccessible(true);
                //check all value is equals
                if (!fields[i].get(this.objectStruct).equals(fields[i].get(curRecord))) {
                    return false;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
        return true;
    }

    public boolean SaveFile(EncryptFile encryptFile) {
        List<EncryptFile> list = new ArrayList<EncryptFile>();
        list.add(encryptFile);

        return this.SaveFile(list);
    }

    public boolean SaveFile(List<EncryptFile> encryptFileList) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.file, true));
            PrintWriter out = new PrintWriter(writer);
            for (int i = 0; i < encryptFileList.size(); i++) {
                out.println(this.ConvertObjectToString(encryptFileList.get(i)));
            }
            out.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return true;
    }

    //criteria -> pass in record
    public boolean CheckRecordKeyExists(EncryptFile encryptFile) {
        if (this.file.exists()) {
            BufferedReader br;
            try {
                br = new BufferedReader(new FileReader(this.file));
                String line = "";
                while ((line = br.readLine()) != null) {
                    String[] record = line.split(this.GetFileSeparater());
                    //check record exists
                    if (encryptFile.IsRecordKeyExists(record)) {
                        return true;
                    }
                }
                br.close();

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return false;
    }

    //get record in file storage
    //critera -> same as curret record key
    public EncryptFile GetRecordInFile(EncryptFile encryptFile) {
        EncryptFile curFile = null;
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(this.file));
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] record = line.split(this.GetFileSeparater());
                //check record exists
                if (encryptFile.IsRecordKeyExists(record)) {
                    curFile = this.ConvertStringToObject(line);
                }
            }
            br.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return curFile;
    }

    public List<EncryptFile> GetAllRecordsInFile() {
        List<EncryptFile> allRecords = new ArrayList<EncryptFile>();

        EncryptFile curFile = null;
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(this.file));
            String line = "";
            while ((line = br.readLine()) != null) {
                curFile = this.ConvertStringToObject(line);
                allRecords.add(curFile);
            }
            br.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return allRecords;
    }

    //out string for export
    public String ConvertObjectToString(EncryptFile encryptFile) {
        //convert file to string
        Field[] fields = encryptFile.getClass().getDeclaredFields();
        String curValue;
        String output = "";
        for (int i = 0; i < fields.length; i++) {
            try {
                fields[i].setAccessible(true);
                curValue = (String) fields[i].get(encryptFile);
                output += curValue + this.GetFileSeparater();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        if (output.length() > 0) {
            output = output.substring(0, output.length() - 1);
        }
        System.out.println(output);
        return output;
    }

    //out object
    public EncryptFile ConvertStringToObject(String record) {
        EncryptFile curFile = null;
        try {
            //clone object
            curFile = (EncryptFile) this.objectStruct.CloneObject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

        String[] recordArray = record.split(this.GetFileSeparater());
        Field[] fields = this.objectStruct.getClass().getDeclaredFields();
        String curValue;
        for (int i = 0; i < recordArray.length; i++) {
            try {
                fields[i].setAccessible(true);
                fields[i].set(curFile, recordArray[i]);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        return curFile;
    }

    //reocrd to encrypted message
    public void ConvertToEncryptedObject(EncryptFile encryptFile) {
        Field[] fields = encryptFile.getClass().getDeclaredFields();
        Map<String, EncryptType> encryptMap = encryptFile.GetFieldsEncryptTypeList();
        String curValue;
        for (int i = 0; i < fields.length; i++) {
            try {
                fields[i].setAccessible(true);
                curValue = (String) fields[i].get(encryptFile);
                if (encryptMap.containsKey(fields[i].getName())) {
                    curValue = this.EncryptValue(curValue, encryptMap.get(fields[i].getName()));
                }
                fields[i].set(encryptFile, curValue);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    //decrypt object
    public void ConvertToDecryptedObject(EncryptFile encryptFile) {
        Field[] fields = encryptFile.getClass().getDeclaredFields();
        Map<String, EncryptType> encryptMap = encryptFile.GetFieldsEncryptTypeList();
        String curValue;
        for (int i = 0; i < fields.length; i++) {
            try {
                fields[i].setAccessible(true);
                curValue = (String) fields[i].get(encryptFile);
                if (encryptMap.containsKey(fields[i].getName())) {
                    curValue = this.DecryptValue(curValue, encryptMap.get(fields[i].getName()));
                }
                fields[i].set(encryptFile, curValue);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    protected String GetFileSeparater() {
        return ";";
    }

    public String EncryptValue(String value, EncryptType encryptType) {
        if (encryptType == EncryptType.None) {
            return value;
        }
        return value;
    }

    public String DecryptValue(String value, EncryptType encryptType) {
        return value;
    }
}
