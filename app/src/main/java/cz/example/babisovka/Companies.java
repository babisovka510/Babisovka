package cz.example.babisovka;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Companies {

    public static List<FirmaData> loadEans13 (Context cont) {

        // nacist data ze souboru do promenne tridy typu List<FirmaData>
        List<FirmaData> seznamFirem = new ArrayList<FirmaData>();
        try {
            // firmy z holdingu
            JSONObject jObject = new JSONObject(getJsonFromFile(cont, cont.getString(R.string.ean13agro_soubor)));
            JSONArray firmy = jObject.getJSONArray("firmy");
            for(int i = 0; i < firmy.length(); i++) {
                JSONObject c = firmy.getJSONObject(i);
                if (c.isNull("nazev") || c.isNull("kod"))
                    continue;
                String nazev = c.getString("nazev");
                String kod = c.getString("kod");
                Kategorie holding = Kategorie.HOLDING;
                Retezec retezec = (!c.isNull("retezec") && c.getInt("retezec") > 0) ? Retezec.RETEZEC : Retezec.NENIRETEZEC;
                String pozn = c.isNull("pozn") ? null : c.getString("pozn");
                seznamFirem.add(new FirmaData(nazev, kod, holding, retezec, pozn));
            }

            // firmy mimo holding
            jObject = new JSONObject(getJsonFromFile(cont, cont.getString(R.string.ean13neagro_soubor)));
            firmy = jObject.getJSONArray("firmy");
            for(int i = 0; i < firmy.length(); i++) {
                JSONObject c = firmy.getJSONObject(i);
                if (c.isNull("nazev") || c.isNull("kod"))
                    continue;
                String nazev = c.getString("nazev");
                String kod = c.getString("kod");
                Kategorie holding = Kategorie.MIMOHOLDING;
                Retezec retezec = (!c.isNull("retezec") && c.getInt("retezec") > 0) ? Retezec.RETEZEC : Retezec.NENIRETEZEC;
                String pozn = c.isNull("pozn") ? null : c.getString("pozn");
                seznamFirem.add(new FirmaData(nazev, kod, holding, retezec, pozn));
            }
        }
        catch (Exception ex) {
            Log.e("test", "Unable to parse JSON.", ex);
        }
        return seznamFirem;
    }

    public static List<FirmaData> loadEans8 (Context cont) {

        // nacist data ze souboru do promenne tridy typu List<FirmaData>
        List<FirmaData> seznamFirem = new ArrayList<FirmaData>();
        try {
            // firmy z holdingu
            JSONObject jObject = new JSONObject(getJsonFromFile(cont, cont.getString(R.string.ean8agro_soubor)));
            JSONArray firmy = jObject.getJSONArray("firmy");
            for(int i = 0; i < firmy.length(); i++) {
                JSONObject c = firmy.getJSONObject(i);
                if (c.isNull("nazev") || c.isNull("kod"))
                    continue;
                String nazev = c.getString("nazev");
                String kod = c.getString("kod");
                Kategorie holding = Kategorie.HOLDING;
                String pozn = c.isNull("pozn") ? null : c.getString("pozn");
                int pocet = c.isNull("pocet") ? 1 : c.getInt("pocet");

                int iKod = Integer.parseInt(kod);
                for(int j = 0; j < pocet; j++) {
                    String sKod=String.valueOf(iKod+j);
                    seznamFirem.add(new FirmaData(nazev, sKod, holding, Retezec.NENIRETEZEC, pozn));
                }
            }

            // firmy mimo holding
            jObject = new JSONObject(getJsonFromFile(cont, cont.getString(R.string.ean8neagro_soubor)));
            firmy = jObject.getJSONArray("firmy");
            for(int i = 0; i < firmy.length(); i++) {
                JSONObject c = firmy.getJSONObject(i);
                if (c.isNull("nazev") || c.isNull("kod"))
                    continue;
                String nazev = c.getString("nazev");
                String kod = c.getString("kod");
                Kategorie holding = Kategorie.MIMOHOLDING;
                String pozn = c.isNull("pozn") ? null : c.getString("pozn");
                int pocet = c.isNull("pocet") ? 1 : c.getInt("pocet");

                int iKod = Integer.parseInt(kod);
                for(int j = 0; j < pocet; j++) {
                    String sKod=String.valueOf(iKod+j);
                    seznamFirem.add(new FirmaData(nazev, sKod, holding, Retezec.NENIRETEZEC, pozn));
                }
            }
        }
        catch (Exception ex) {
            Log.e("test", "Unable to parse JSON.", ex);
        }
        return seznamFirem;
    }

    public static List<FirmaData> loadPrivateLabels (Context cont, String label) {

        // nacist data ze souboru do promenne tridy typu List<FirmaData>
        List<FirmaData> seznamFirem = new ArrayList<FirmaData>();
        try {
            // firmy z holdingu
            JSONObject jObject = new JSONObject(getJsonFromFile(cont, cont.getString(R.string.privatelabel_soubor).replace("{0}",label)));
            JSONArray firmy = jObject.getJSONArray("firmy");
            for(int i = 0; i < firmy.length(); i++) {
                JSONObject c = firmy.getJSONObject(i);
                if (c.isNull("vyrobce") || c.isNull("kod"))
                    continue;
                String kod = c.getString("kod");
                if (kod.charAt(0)=='2')  // pouze kody ktere nejsou 2xxxx..
                    continue;
                String nazev = c.getString("vyrobce");
                Kategorie holding = !c.isNull("holding") && c.getInt("holding") > 0 ? Kategorie.HOLDING : Kategorie.MIMOHOLDING;
                String pozn = c.isNull("produkt") ? null : c.getString("produkt");
                seznamFirem.add(new FirmaData(nazev, kod, holding, Retezec.NENIRETEZEC, pozn));
            }
        }
        catch (Exception ex) {
            Log.e("test", "Unable to parse JSON.", ex);
        }
        return seznamFirem;
    }

    // type20 - pouze typy kodu zacinajici 20 az 29
    public static List<FirmaData> loadPrivateLabels20 (Context cont, String label) {

        // nacist data ze souboru do promenne tridy typu List<FirmaData>
        List<FirmaData> seznamFirem = new ArrayList<FirmaData>();
        try {
            // firmy z holdingu
            JSONObject jObject = new JSONObject(getJsonFromFile(cont, cont.getString(R.string.privatelabel_soubor).replace("{0}",label)));
            JSONArray firmy = jObject.getJSONArray("firmy");
            for(int i = 0; i < firmy.length(); i++) {
                JSONObject c = firmy.getJSONObject(i);
                if (c.isNull("vyrobce") || c.isNull("kod"))
                    continue;
                String kod = c.getString("kod");
                if (kod.charAt(0)!='2')  // pouze kody 2xxxx..
                    continue;
                String nazev = c.getString("vyrobce");
                Kategorie holding = !c.isNull("holding") && c.getInt("holding") > 0 ? Kategorie.HOLDING : Kategorie.MIMOHOLDING;
                String pozn = label.concat(": ").concat(c.isNull("produkt") ? "" : c.getString("produkt"));
                seznamFirem.add(new FirmaData(nazev, kod, holding, Retezec.NENIRETEZEC, pozn));
            }
        }
        catch (Exception ex) {
            Log.e("test", "Unable to parse JSON.", ex);
        }
        return seznamFirem;
    }

    // nacte ze souboru JSON data
    private static String getJsonFromFile (Context cont, String fileName) {

        try {
            // otevreme soubor pro cteni
            FileInputStream inStream = cont.openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inStream, "UTF_16");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // soubor postupne, po radcich, nacteme
            // String line;

            return bufferedReader.readLine();

            /*
            while ((line = bufferedReader.readLine()) != null) {
                // sb.append(line);
                lstStanice.add(line);
            }
            */
        }
        catch (IOException e) {
            // zobrazi text, v pripade chyby
            String textChyby = String.format("Nepodařilo se načíst soubor z interního úložiště&#8230;\\n%1$s", e.getMessage());
            Toast.makeText(cont, textChyby, Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public enum Kategorie {
        HOLDING,
        MIMOHOLDING,
        NEJASNE
    }

    public enum Retezec {
        NENIRETEZEC,
        RETEZEC,
        DODAVATELRETEZCE,
        VICEDODAVATELU      // pod stejnym EAN dodava vice dodavatelu (napr EAN-8 zacinajici "20")
    }

}

class FirmaData{
    String nazev;
    String kod;
    Companies.Kategorie holding;
    Companies.Retezec retezec;
    String pozn;

    public FirmaData (FirmaData data) {
        nazev=data.nazev;
        kod=data.kod;
        holding=data.holding;
        retezec=data.retezec;
        pozn=data.pozn;
    }

    public FirmaData (String _nazev, String _kod, Companies.Kategorie _holding, Companies.Retezec _retezec, String _pozn) {
        nazev=_nazev;
        kod=_kod;
        holding=_holding;
        retezec=_retezec;
        pozn=_pozn;
    }
}
