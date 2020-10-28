package cz.example.babisovka;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        // nastaveni toolbaru
        ActionBar supportActionBar=getSupportActionBar();
        if (supportActionBar!=null) {
            supportActionBar.setTitle("Nastavení");
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
        }


        // naplnit formular daty
        nacistData();
    }

    // sipka zpet v toolbaru
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void nacistData()
    {
        // otevreme soubor pro cteni preferenci
        SharedPreferences sharedPreferences = this.getSharedPreferences("preferences.xml", Context.MODE_PRIVATE);
        // nacteme ulozenou hodnotu
        boolean barcodeBeep = sharedPreferences.getBoolean("barcodeBeep", true);
        boolean barcodeVibrate = sharedPreferences.getBoolean("barcodeVibrate", false);
        int displayOrientation = sharedPreferences.getInt("displayOrientation", 0);
        // String text2 = Integer.toString(sharedPreferences.getInt("text2", 0));

        // ulozime hodnoty do formulare
        ((Switch)findViewById(R.id.swPipnout)).setChecked(barcodeBeep);
        ((Switch)findViewById(R.id.swZavibrovat)).setChecked(barcodeVibrate);

        if(displayOrientation == 0)
            ((RadioButton) findViewById(R.id.rb_otaceni_neblokovat)).setChecked(true);
        else if(displayOrientation == 1)
            ((RadioButton) findViewById(R.id.rb_otaceni_portrait)).setChecked(true);
        else if(displayOrientation == 2)
            ((RadioButton) findViewById(R.id.rb_otaceni_landscape)).setChecked(true);


        // EditText editText2 = (EditText)findViewById(R.id.editText2);
        // editText2.setText(text2);
    }

    public void ulozitZmenu(View sender)
    {
        // otevreme soubor pro zapis preferenci
        SharedPreferences sharedPreferences = this.getSharedPreferences("preferences.xml", Context.MODE_PRIVATE);
        // vytvorime objekt editor preferenci
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // pipnuti/vibrace
        if (sender.getId()==R.id.swPipnout) {
            editor.putBoolean("barcodeBeep", ((Switch)sender).isChecked());
        }
        else if (sender.getId()==R.id.swZavibrovat) {
            editor.putBoolean("barcodeVibrate", ((Switch)sender).isChecked());
        }

        // otaceni displeje
        if (sender.getId()==R.id.rb_otaceni_neblokovat) {
            editor.putInt("displayOrientation", 0);
        }
        else if (sender.getId()==R.id.rb_otaceni_portrait) {
            editor.putInt("displayOrientation", 1);
        }
        else if (sender.getId()==R.id.rb_otaceni_landscape) {
            editor.putInt("displayOrientation", 2);
        }

        /*
        // nacteme hodnoty z formulare
        EditText editText1 = (EditText)findViewById(R.id.editText1);
        String inputText = editText1.getText().toString();

        EditText editText2 = (EditText)findViewById(R.id.editText2);
        String text2 = editText2.getText().toString();
        int inputInt = Integer.parseInt(text2);
        // do editoru ulozime klic a hodnotu z formulare
        editor.putString("text1", inputText);
        editor.putInt("text2", inputInt);
*/

        // data ulozime
        editor.apply();
    }

/*
    public boolean getSettingBoolean (String name) {
        // otevreme soubor pro cteni preferenci
        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        // nacteme ulozenou hodnotu
        Boolean barcodeBeep = sharedPreferences.getBoolean("barcodeBeep", true);
        if (name.equals("barcodeBeep"))
            return sharedPreferences.getBoolean("barcodeBeep", true);
        else if (name.equals("barcodeVibrate"))
            return sharedPreferences.getBoolean("barcodeVibrate", false);
        else
            return false;
            //throw new Exception("Chyba - pokus o čtení meexistujícího nastavení.");

    }
 */
}