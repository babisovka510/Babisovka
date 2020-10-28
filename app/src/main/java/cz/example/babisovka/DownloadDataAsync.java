package cz.example.babisovka;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadDataAsync extends AsyncTask<Void, Void, Void> {

    private Context cont;
    private String address;
    private String filename;
    private String nactenaData="";
    private String err="";

    public DownloadDataAsync(Context cont, String address, String filename) {
        this.cont = cont;
        this.address = address;
        this.filename = filename;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try
        {
            URL url= new URL(this.address);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestProperty("Cache-Control", "no-cache");
            InputStream stream = conn.getInputStream();
            BufferedReader buff = new BufferedReader(new InputStreamReader(stream));
            String radka = "";
            do {
                nactenaData = nactenaData.concat(radka);
                radka = buff.readLine();
            } while (radka != null);
        }
        catch(Exception e) {
            nactenaData = "";
            err=String.format("%s: %s", filename, e.getMessage());
        }
        return  null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (nactenaData!=null && nactenaData.length()>0) {

            try {
                FileOutputStream outStream = cont.openFileOutput(filename, Context.MODE_PRIVATE);
                outStream.write(nactenaData.getBytes("UTF_16"));
                outStream.close();
            } catch (Exception e) {
                err=String.format("%s: %s", filename, e.getMessage());
            }
        }
        DownloadDataActivity.onDownloaded(cont, err);
    }
}


