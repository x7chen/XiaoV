package com.cfk.xiaov.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import okhttp3.ResponseBody;

/**
 * Created by cfk on 2017/10/31.
 */

public class ResponseBodyStore {

    private static String SAVEADDRESS = UIUtils.getContext().getFilesDir().getPath();// /data/data/<application package>/files
    private static final String SCHEMA = "file://";

    public static String writeResponseBodyToDisk(ResponseBody body){

        File f = new File(SAVEADDRESS, UUID.randomUUID().toString());
        try {
            FileOutputStream out = new FileOutputStream(f);
            out.write(body.bytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return SCHEMA + f.getPath();
    }
}
