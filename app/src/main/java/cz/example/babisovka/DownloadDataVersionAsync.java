package cz.example.babisovka;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadDataVersionAsync extends AsyncTask<Void, Void, Void> {

    private String address;
    private String nactenaData="";
    private Object obj;

    public DownloadDataVersionAsync(String address, Object obj) {
        this.address = address;
        this.obj = obj;
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
        }
        return  null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        DownloadDataActivity.onDownloadedVersion(nactenaData, obj);
    }
}
