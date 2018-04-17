package com.company.newScheme;

//import redis.clients.jedis.Jedis;

import com.company.AES;
import com.company.SchemeInterface;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.*;

public class newSchemeV1SHAMD5 implements SchemeInterface {

    private HashSet<String> users;
    private HashSet<String> usernames;

    private static String sha1key;
    private final static String sep = "|%";
    private final static String aeskey = "q4t7w!z%C*F-JaNd";

    private int count;
    private final static int finalcount = 1000000;

    private final static String fileName = "newSchemeUsers.txt";
    private final static String fileUserNames = "newSchemeUserNames.txt";
    private final static String chipheredDB = "cipherdb.txt";
    private final static String KEYfile = "KEY.txt";

    private ArrayList<ArrayList<String>> log;
    private AES aes;
    private byte[] cipherdb;

    public newSchemeV1SHAMD5() {
        count = 0;

        users = new HashSet<String>();
        usernames = new HashSet<String>();
        cipherdb = null;
        users = (HashSet<String>) readData(fileName);
        usernames = (HashSet<String>) readData(fileUserNames);
        cipherdb = (byte[]) readData(chipheredDB);
        sha1key = (String) readData(KEYfile);

        if(sha1key == null)
            sha1key = "a";
        //readRedis();
        log = new ArrayList<>();
        aes = new AES();
       /*  new Thread(() -> {
            try {
                while(2+2==4) {
                    Thread.sleep(20000);
                    dumpLog();
                    changeKey();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        */
    }


    private static final String HMAC_SHA1_ALGORITHM = "HmacMD5";

    public boolean login(String username, String password) {
        return users.contains(applyFunction(username, password));
    }

    public boolean register(String username, String password) {
        if (!usernames.add(username))
            return false;
        else {
            usernames.add(username);
            return users.add(applyFunction(username, password));}
    }

    public boolean changePassword(String username, String password1, String password2) {
        if (!users.remove(applyFunction(username, password1)))
            return false;
        else {
            log.add(new ArrayList<>() {{add("cp");add(username);add(password1);add(password2);}});
            return users.add(applyFunction(username, password2));
        }
    }

    public boolean changeUsername(String username1, String username2, String password) {
        if (!users.remove(applyFunction(username1, password)))
            return false;
        else {
            usernames.remove(username1);
            usernames.add(username2);
            log.add(new ArrayList<>() {{add("cu");add(username1);add(username2);add(password);}});
            return users.add(applyFunction(username2, password));
        }
    }

    public boolean deleteUser(String username, String password) {
        if (!usernames.remove(username))
            return false;
        else {
            log.add(new ArrayList<>() {{add("del");add(username);add(password);}});
            return users.remove(applyFunction(username, password));
        }

    }
    
    public String applyFunction(String username, String password) {
        String user = null;
        try {
            SecretKeySpec key = new SecretKeySpec((sha1key).getBytes("UTF-8"), HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(key);

            byte[] bytes = mac.doFinal((username + sep + password).getBytes("ASCII"));

            StringBuffer hash = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            user = hash.toString();
        } catch (Exception e){
            e.printStackTrace();
        }

        return user;
    }

    private OutputStream ops = null;
    private ObjectOutputStream objOps = null;

    private void writeData(Object users, String fileName) {
        //txts no explorer
        try {
            ops = new FileOutputStream(fileName);
            objOps = new ObjectOutputStream(ops);
            objOps.writeObject(users);
            objOps.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (objOps != null) objOps.close();
            } catch (Exception ex) {

            }
        }
    }
    private Object readData(String file) {
        InputStream fileIs = null;
        ObjectInputStream objIs = null;
        Object users;
        try {
            fileIs = new FileInputStream(file);
            objIs = new ObjectInputStream(fileIs);
            users = objIs.readObject();
            //System.out.println("");
            //System.out.println(Collections.singletonList(users)); // method 2
            return users;
        } catch (FileNotFoundException e) {

            if(file.equals(fileUserNames) || file.equals(fileName))
                return new HashSet<String>();
            else
                return null;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (objIs != null) objIs.close();
            } catch (Exception ex) {

            }
        }
        return null;
    }


    private void dumpLog(){
        try {
            HashMap<String, String> dbpw;
            if (cipherdb != null) {
                //decipher old ciphered db and put it in String
                dbpw = aes.decipher(cipherdb, aeskey);

                //String to Map
            }
            else
                dbpw = new HashMap<>();

            //update the hashmap according to the log
            for(ArrayList<String> entry : log){
                switch(entry.get(0)){
                    case "add":
                        dbpw.put(entry.get(1), entry.get(2));
                        break;
                    case "cp":
                        dbpw.replace(entry.get(1), entry.get(3));
                        break;
                    case "cu":
                        dbpw.remove(entry.get(1));
                        dbpw.put(entry.get(2), entry.get(3));
                        break;
                    case "del":
                        dbpw.remove(entry.get(1));
                        break;
                    default: System.out.println("Something went terribly wrong");
                }
            }

            log = new ArrayList<>();

            //encrypt that bitch back
            cipherdb = aes.cipher(dbpw, aeskey);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeKey(){
        long startTime = System.nanoTime();

        sha1key = UUID.randomUUID().toString();
        users = new HashSet<>();
        try {
                HashMap<String, String> db = aes.decipher(cipherdb, aeskey);
                Iterator it = db.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    String user = applyFunction(  (String) pair.getKey(), (String) pair.getValue());
                    users.add(user);
                    it.remove();
                }

        } catch (Exception e) {
            e.printStackTrace(); 
        }

        long endTime = System.nanoTime();
        System.out.println("Changed hash set with key " + sha1key + " and it took " + (int) ((endTime - startTime)/(1000000)) + " ms!");

    }

}
