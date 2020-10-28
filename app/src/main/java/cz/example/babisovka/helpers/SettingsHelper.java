package cz.example.babisovka.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsHelper {


    public enum Preference {
        BARCODE_BEEP,
        BARCODE_VIBRATE,
        DISPLAY_ORIENTATION
    }

    public static boolean getSettingBoolean (Context cont, Preference pref) {
        // otevreme soubor pro cteni preferenci
        SharedPreferences sharedPreferences = cont.getSharedPreferences("preferences.xml", Context.MODE_PRIVATE);

        // nacteme ulozenou hodnotu
        if (pref == Preference.BARCODE_BEEP)
            return sharedPreferences.getBoolean("barcodeBeep", true);
        else if (pref == Preference.BARCODE_VIBRATE)
            return sharedPreferences.getBoolean("barcodeVibrate", false);
        else
            return false;
        //throw new Exception("Chyba - pokus o čtení neexistujícího nastavení.");

    }

    public static int getSettingInt (Context cont, Preference pref) {
        // otevreme soubor pro cteni preferenci
        SharedPreferences sharedPreferences = cont.getSharedPreferences("preferences.xml", Context.MODE_PRIVATE);

        // nacteme ulozenou hodnotu
        if (pref == Preference.DISPLAY_ORIENTATION)
            return sharedPreferences.getInt("displayOrientation", 0);
        else
            return 0;

    }


}
