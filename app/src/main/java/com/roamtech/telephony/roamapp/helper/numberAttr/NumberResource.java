package com.roamtech.telephony.roamapp.helper.numberAttr;

import android.content.Context;
import android.os.Environment;

import com.roamtech.telephony.roamapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by caibinglong
 * on 2017/2/14.
 */

public class NumberResource {

    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transfering bytestream.
     */
    public static void copyDataBase(Context context, String dbPath) throws IOException {
        if (!(new File(dbPath).exists())) {
            // Open your local db as the input stream
            InputStream myInput = context.getResources().openRawResource(R.raw.telocation);
            // Path to the just created empty db
            FileOutputStream fos = new FileOutputStream(dbPath);
            // Open the empty db as the output stream
            // transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024 * 18];
            int count;
            while ((count = myInput.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            // Close the streams
            fos.flush();
            fos.close();
            myInput.close();
        }
    }
}
