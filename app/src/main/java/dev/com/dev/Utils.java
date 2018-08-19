package dev.com.dev;

import android.content.Context;
import android.os.Environment;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

  public Utils() {
  }


  public static void copyFile(InputStream in,OutputStream out) throws IOException {
    try {
      byte [] buffer = new byte[1024];
      int len;
      while ((len = in.read(buffer)) > 0){
        out.write(buffer,0,len);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

}
