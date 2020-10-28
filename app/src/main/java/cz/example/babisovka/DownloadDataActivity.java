package cz.example.babisovka;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import cz.example.babisovka.helpers.DatafilesHelper;

public class DownloadDataActivity extends AppCompatActivity {

    private static ProgressBar progressbar;
    private static TextView txtVerzionApp;
    // private static TextView txtVerzionNet;
    private static int justDownloaded  = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaddata);

        // nastaveni toolbaru
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Aktualizace dat");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        progressbar = (ProgressBar)findViewById(R.id.progressBar);

        // aktualizovat info o verzich dat na displeji
        txtVerzionApp = (TextView)findViewById(R.id.txtVerzeApp);
        refreshDateVersionApp(this, txtVerzionApp);
        TextView txtVerzionNet = (TextView)findViewById(R.id.txtVerzeNet);
        if (txtVerzionNet.getText().length() < 1 && isDatovePripojeniOk()>0) {
            askForDateVersionNet(txtVerzionNet);
        }
    }

    // Zjisti z datoveho souboru verzi dat (resp. datum, verzujeme datumem)
    private static void refreshDateVersionApp (Context cont, TextView textView) {
        String jsonFromFile = "";
        String verzeDatum = "";
        try {
            // otevreme soubor pro cteni
            FileInputStream inStream = cont.openFileInput(cont.getString(R.string.eanverze_soubor));
            InputStreamReader inputStreamReader = new InputStreamReader(inStream, "UTF_16");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            jsonFromFile = bufferedReader.readLine();

            JSONObject jObject = new JSONObject(jsonFromFile);
            String datum = jObject.getString("datum");

            textView.setText(String.format("Verze dat v aplikaci: %s", datum));
        }
        catch (Exception e) {
            Log.d("JSON parser", String.format("Chyba během zjišťování verze dat v souborech.\n\n%s", e.getMessage()));
        }
    }

    // Zjisti z datoveho souboru verze dat, jestli tam neni zprava
    private static String getDateVersionMessage (Context cont) {
        String jsonFromFile = "";
        String verzeDatum = "";
        try {
            // otevreme soubor pro cteni
            FileInputStream inStream = cont.openFileInput(cont.getString(R.string.eanverze_soubor));
            InputStreamReader inputStreamReader = new InputStreamReader(inStream, "UTF_16");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            jsonFromFile = bufferedReader.readLine();

            JSONObject jObject = new JSONObject(jsonFromFile);
            return jObject.getString("zprava");
        }
        catch (Exception e) {
            Log.d("JSON parser", String.format("Chyba během zjišťování textove zpravy ze souboru s verzi dat.\n\n%s", e.getMessage()));
            return "";
        }
    }

    // Zjisti ze serveru datum posledni verze dat (nemame cisla verzi, verzujeme datumem)
    private void askForDateVersionNet (TextView textView) {
        DownloadDataVersionAsync dataSync = new DownloadDataVersionAsync(getString(R.string.eanverze_url), textView);
        dataSync.execute();
    }
    // udalost, volana z DownloadDataVersionAsync po nacteni verze ze serveru
    public static void onDownloadedVersion (String data, Object obj) {
        if (data.length()<1)
            return;
        try {
            JSONObject jObject = new JSONObject(data);
            String datum = jObject.getString("datum");
            ((TextView) obj).setText(String.format("Verze dat ke stažení: %s", datum));
        }
        catch (JSONException e) {
            Log.d("JSON parser", String.format("Chyba během parsování JSON.\n\n%s", e.getMessage()));
        }
    }


    public void onStahnoutDataClick (View view) {
        if (isDatovePripojeniOk()<=0) {
            Toast.makeText(getBaseContext(), "Není dostupné datové připojení.\nZapněte prosím WiFi nebo mobilní data.", Toast.LENGTH_LONG).show();
            return;
        }

        downloadManager(this, true, "");
    }

    public void onDefaultDataClick (View view) {
        boolean ok = DatafilesHelper.replaceDataFromDefault(this);
        if(ok) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.app_name);
            alert.setMessage("Data byla přepsána starší verzí.\nMůžete je kdykoliv znovu přehrát aktuální verzí.");
            alert.setPositiveButton(R.string.ok, null);
            alert.create().show();
        }
        else
            Toast.makeText(this, "Data se nepodařilo přepsat starší verzí.", Toast.LENGTH_LONG).show();

        // pregenerovani seznamu barcodu
        BarcodeDecoding barcodeDecoding = new BarcodeDecoding();
        barcodeDecoding.init(this, true);

        // info o nove verzi
        TextView txtVerzionApp = (TextView)findViewById(R.id.txtVerzeApp);
        refreshDateVersionApp(this, txtVerzionApp);
    }

    // udalost, volana z DownloadDataAsync po nacteni dat ze serveru
    public static void onDownloaded (Context cont, String err) {
        downloadManager(cont, false, err);
    }

    private static String lastError = "";

    // manager, ktery ridi postupne asynchronni stahovani jednotlivych datovych souboru z netu
    private static void downloadManager(Context cont, boolean init, String err) {
        if (init) {
            justDownloaded = -1;
            lastError = "";
        }
        if (err != null && err.length()>0)
            lastError=lastError.concat("\n").concat(err);

        justDownloaded ++;

        progressbar.setProgress(justDownloaded);

        Resources res = cont.getResources();
        DownloadDataAsync dataSync = null;
        if (justDownloaded == 0)
            dataSync = new DownloadDataAsync(cont, res.getString(R.string.ean13agro_url), res.getString(R.string.ean13agro_soubor));
        else if (justDownloaded == 1)
            dataSync = new DownloadDataAsync(cont, res.getString(R.string.ean13neagro_url), res.getString(R.string.ean13neagro_soubor));
        else if (justDownloaded == 2)
            dataSync = new DownloadDataAsync(cont, res.getString(R.string.ean8agro_url), res.getString(R.string.ean8agro_soubor));
        else if (justDownloaded == 3)
            dataSync = new DownloadDataAsync(cont, res.getString(R.string.ean8neagro_url), res.getString(R.string.ean8neagro_soubor));
        else if (justDownloaded == 4)
            dataSync = new DownloadDataAsync(cont, res.getString(R.string.eanverze_url), res.getString(R.string.eanverze_soubor));
        else if (justDownloaded == 5)
            dataSync = new DownloadDataAsync(cont, res.getString(R.string.privatelabelAlbert_url), res.getString(R.string.privatelabel_soubor).replace("{0}", "Albert"));
        else if (justDownloaded == 6)
            dataSync = new DownloadDataAsync(cont, res.getString(R.string.privatelabelBilla_url), res.getString(R.string.privatelabel_soubor).replace("{0}", "Billa"));
        else if (justDownloaded == 7)
            dataSync = new DownloadDataAsync(cont, res.getString(R.string.privatelabelGlobus_url), res.getString(R.string.privatelabel_soubor).replace("{0}", "Globus"));
        else if (justDownloaded == 8)
            dataSync = new DownloadDataAsync(cont, res.getString(R.string.privatelabelKaufland_url), res.getString(R.string.privatelabel_soubor).replace("{0}", "Kaufland"));
        else if (justDownloaded == 9)
            dataSync = new DownloadDataAsync(cont, res.getString(R.string.privatelabelLidl_url), res.getString(R.string.privatelabel_soubor).replace("{0}", "Lidl"));
        else if (justDownloaded == 10)
            dataSync = new DownloadDataAsync(cont, res.getString(R.string.privatelabelNorma_url), res.getString(R.string.privatelabel_soubor).replace("{0}", "Norma"));
        else if (justDownloaded == 11)
            dataSync = new DownloadDataAsync(cont, res.getString(R.string.privatelabelPenny_url), res.getString(R.string.privatelabel_soubor).replace("{0}", "Penny"));
        else if (justDownloaded == 12)
            dataSync = new DownloadDataAsync(cont, res.getString(R.string.privatelabelTesco_url), res.getString(R.string.privatelabel_soubor).replace("{0}", "Tesco"));

        if (justDownloaded <= 12 && dataSync!= null) {
            dataSync.execute();
        }
        else {
            justDownloaded = -1;

            // pregenerovani seznamu barcodu
            BarcodeDecoding barcodeDecoding = new BarcodeDecoding();
            barcodeDecoding.init(cont, true);

            // aktualizace labelu verze dat v zarizeni
            refreshDateVersionApp(cont, txtVerzionApp);

            progressbar.setProgress(13);

            // AlertDialog s vysledkem
            String msg;
            if (lastError.length()<1) {
                msg = "Data byla aktualizována.\nNyní máte poslední dostupnou verzi dat.";
                String dataMsg = getDateVersionMessage(cont);
                if (dataMsg.length() > 0)
                    msg=msg.concat("\n\n").concat(dataMsg);
            }
            else {
                msg = String.format("Během aktualizace dat došlo k chybě.\n%s", lastError);
            }

            AlertDialog.Builder alert = new AlertDialog.Builder(cont);
            alert.setTitle(R.string.app_name);
            alert.setMessage(msg);
            alert.setPositiveButton(R.string.ok, null);
            alert.create().show();
        }
    }

    // Overi dostupnost datoveho pripojeni
    private byte isDatovePripojeniOk() {
        Context context = getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return 0;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
                    return 1;
                else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                    return 2;
                else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                    return 3;
            }
        }
        else {
            return cm.getActiveNetworkInfo() == null ? 0 : (byte)10;
        }
        return 0;
    }

    // sipka zpet v toolbaru
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}