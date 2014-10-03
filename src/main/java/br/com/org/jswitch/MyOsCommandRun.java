package br.com.org.jswitch;
 
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
 
public class MyOsCommandRun {
 
    public static void main(String a[]){
         
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        ProcessBuilder pb = new ProcessBuilder("cmd","/C","for %i in (javac.exe) do @echo.   %~$PATH:i");
        try {
            Process prs = pb.start();
            is = prs.getInputStream();
            byte[] b = new byte[1024];
            int size = 0;
            baos = new ByteArrayOutputStream();
            while((size = is.read(b)) != -1){
                baos.write(b, 0, size);
            }
            System.out.println(new String(baos.toByteArray()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally{
            try {
                if(is != null) is.close();
                if(baos != null) baos.close();
            } catch (Exception ex){}
        }
    }
}