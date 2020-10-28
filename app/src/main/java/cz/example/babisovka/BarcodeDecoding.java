package cz.example.babisovka;

import android.content.Context;

import java.util.List;

public class BarcodeDecoding {

    static List<FirmaData> seznamFirem13;
    static List<FirmaData> seznamFirem8;
    static List<FirmaData> privatniZnackaAlbert;
    static List<FirmaData> privatniZnackaBilla;
    static List<FirmaData> privatniZnackaGlobus;
    static List<FirmaData> privatniZnackaKaufland;
    static List<FirmaData> privatniZnackaLidl;
    static List<FirmaData> privatniZnackaNorma;
    static List<FirmaData> privatniZnackaPenny;
    static List<FirmaData> privatniZnackaTesco;
    static List<FirmaData> privatniZnacka20;

    // nacteni dat o firmach do seznamu
    // musi se zavolat pred prvnim hledanim vyrobce
    public void init(Context cont, boolean resetData) {
        if (resetData) {
            seznamFirem13 = null;
            seznamFirem8 = null;
            privatniZnackaAlbert = null;
            privatniZnackaBilla = null;
            privatniZnackaGlobus = null;
            privatniZnackaKaufland = null;
            privatniZnackaLidl = null;
            privatniZnackaNorma = null;
            privatniZnackaPenny = null;
            privatniZnackaTesco = null;
            privatniZnacka20 = null;
        }

        if (seznamFirem13 == null && seznamFirem8 == null) {
            seznamFirem13 = Companies.loadEans13(cont);
            seznamFirem8 = Companies.loadEans8(cont);
            // privatni znacky se plni az pri prvnim pouziti
        }
    }

    // podle caroveho kodu najde a vrati dostupne informace o vyrobci
    public ResultData getResultData(Context cont, String barcode) {
        FirmaData firmaData = getCompanyData(cont, barcode);
        ZemeData zemeData = Countries.getCompanyCountry(barcode);

        ResultData rslt = new ResultData();

        // neznama firma
        if (firmaData == null) {
            // CZ
            if (zemeData.kategorie == Countries.Kategorie.CZ) {
                rslt.nazev = "nezjištěná firma z České republiky";
                rslt.dodatek = "Kód výrobce není v seznamu firem holdingu. Pro jistotu se můžete pokusit najít jméno výrobce na obalu.";
                rslt.holding = Companies.Kategorie.NEJASNE;
            }
            else if (zemeData.kategorie == Countries.Kategorie.ZAHRANICNI) {
                rslt.nazev = "nezjištěná firma ".concat(zemeData.nazevSklonovany);
                rslt.dodatek = "";
                rslt.holding = Companies.Kategorie.MIMOHOLDING;
            }
            else if (zemeData.kategorie == Countries.Kategorie.PRIVATNI) {
                rslt.nazev = "privární značka obchodního řetězce";
                rslt.dodatek = "Pod privátní značkou dodávají řetězcům zboží různí výrobci. Pokuste se výrobce najít na obalu nebo se zkuste zeptat prodavače.";
                rslt.holding = Companies.Kategorie.NEJASNE;
            }
            else if (zemeData.kategorie == Countries.Kategorie.VAHOVE) {
                rslt.nazev = "váhový/kusový kód, neznámý dodavatel";
                rslt.dodatek = "Váhové/kusové kódy si tiskne každá prodejna sama, podle hmotnosti nebo variabilní ceny zboží. Pokuste se výrobce najít na obalu nebo se zkuste zeptat prodavače.";
                rslt.holding = Companies.Kategorie.NEJASNE;
            }
            else {
                rslt.nazev = "podle kódu nelze identifikovat";
                rslt.dodatek = "Tento typ kódu neumožňuje identifikaci výrobce ani země.";
                rslt.holding = Companies.Kategorie.NEJASNE;
            }
        }
        // znama firma
        else {
            // retezec
            if (firmaData.retezec == Companies.Retezec.RETEZEC) {
                rslt.nazev = "Obchodní řetězec ".concat(firmaData.nazev);
                rslt.dodatek = "Řetězce využívají různých dodavatelů. Pokuste se výrobce najít na obalu nebo se zkuste zeptat prodavače.";
                rslt.holding = Companies.Kategorie.NEJASNE;
            }
            // dodavatel retezce (privatni znacky) HOLDING
            else if (firmaData.retezec == Companies.Retezec.DODAVATELRETEZCE && firmaData.holding == Companies.Kategorie.HOLDING) {
                rslt.nazev = "AGROFERT - ".concat(firmaData.nazev);
                rslt.dodatek = firmaData.pozn;
                rslt.holding = firmaData.holding;
            }
            // dodavatel retezce (privatni znacky) NEHOLDING
            else if (firmaData.retezec == Companies.Retezec.DODAVATELRETEZCE && firmaData.holding == Companies.Kategorie.MIMOHOLDING) {
                rslt.nazev = firmaData.nazev;
                rslt.dodatek = firmaData.pozn;
                rslt.holding = firmaData.holding;
            }
            // se stejným kódem dodává více dodavatelů (napr. EAN-8, zacinajici "20")
            else if (firmaData.retezec == Companies.Retezec.VICEDODAVATELU) {
                rslt.nazev = "Privátní značka řetězce";
                rslt.dodatek = firmaData.pozn.concat("\n\nPozor, pod stejným kódem mohou mít ostatní řetězce jiné dodavatele.");
                rslt.holding = firmaData.holding;
            }
            // HOLDING
            else if (firmaData.holding == Companies.Kategorie.HOLDING) {
                rslt.nazev = "AGROFERT - ".concat(firmaData.nazev);
                rslt.dodatek = firmaData.pozn;
                rslt.holding = firmaData.holding;
            }
            // NEHOLDING
            else {
                rslt.nazev = firmaData.nazev.concat(", ").concat(zemeData.nazev);
                rslt.dodatek = firmaData.pozn;
                rslt.holding = firmaData.holding;
            }
        }

        return rslt;
    }


    // najde odpovidajici EAN a vrati data o firme
    private FirmaData getCompanyData(Context cont, String barcode) {
        // https://www.gs1cz.org/nabizime/nastroje/gepir/vyhledavani-podle-gln
        // https://www.gs1cz.org/nabizime/nastroje/gepir/vyhledavani-podle-nazvu-spolecnosti

        FirmaData nalezenaFirma = null;

        if (barcode.substring(0,2).equals("20")) {
            nalezenaFirma = getPrivatniZnacka20(cont, barcode);
        } else if (barcode.length() > 8) {
            // 13 mistne barcode
            for (FirmaData firma : seznamFirem13) {
                if (compLeft(barcode, firma.kod)) {
                    nalezenaFirma = new FirmaData(firma);
                    continue;
                }
            }
        } else {
            // 8 mistne barcode
            for (FirmaData firma : seznamFirem8) {
                if (compLeft(barcode, firma.kod)) {
                    nalezenaFirma = new FirmaData(firma);
                    continue;
                }
            }
        }

        // jestli jde o kod retezce, dohledame dodavatele v seznamu privatnich znacek
        if (nalezenaFirma != null && nalezenaFirma.retezec == Companies.Retezec.RETEZEC) {
            FirmaData privZnacka = getPrivatniZnacka(cont, nalezenaFirma.nazev, barcode);
            if (privZnacka != null){
                nalezenaFirma.holding = privZnacka.holding;
                nalezenaFirma.pozn = privZnacka.pozn.concat(" (").concat(nalezenaFirma.nazev.concat(")"));
                nalezenaFirma.nazev = privZnacka.nazev;
                nalezenaFirma.retezec = Companies.Retezec.DODAVATELRETEZCE;
            }
        }

        return nalezenaFirma;
    }

    private FirmaData getPrivatniZnacka(Context cont, String znacka, String barcode) {
        List<FirmaData> privatniZnacka = null;

        if (znacka.contains("Albert")) {
            if (privatniZnackaAlbert == null)
                privatniZnackaAlbert = Companies.loadPrivateLabels(cont, "Albert");
            privatniZnacka = privatniZnackaAlbert;
        }
        else if (znacka.contains("Billa")) {
            if (privatniZnackaBilla == null)
                privatniZnackaBilla = Companies.loadPrivateLabels(cont, "Billa");
            privatniZnacka = privatniZnackaBilla;
        }
        else if (znacka.contains("Globus")) {
            if (privatniZnackaGlobus == null)
                privatniZnackaGlobus = Companies.loadPrivateLabels(cont, "Globus");
            privatniZnacka = privatniZnackaGlobus;
        }
        else if (znacka.contains("Kaufland")) {
            if (privatniZnackaKaufland == null)
                privatniZnackaKaufland = Companies.loadPrivateLabels(cont, "Kaufland");
            privatniZnacka = privatniZnackaKaufland;
        }
        else if (znacka.contains("Lidl")) {
            if (privatniZnackaLidl == null)
                privatniZnackaLidl = Companies.loadPrivateLabels(cont, "Lidl");
            privatniZnacka = privatniZnackaLidl;
        }
        else if (znacka.contains("Norma")) {
            if (privatniZnackaNorma == null)
                privatniZnackaNorma = Companies.loadPrivateLabels(cont, "Norma");
            privatniZnacka = privatniZnackaNorma;
        }
        else if (znacka.contains("Penny")) {
            if (privatniZnackaPenny == null)
                privatniZnackaPenny = Companies.loadPrivateLabels(cont, "Penny");
            privatniZnacka = privatniZnackaPenny;
        }
        else if (znacka.contains("Tesco")) {
            if (privatniZnackaTesco == null)
                privatniZnackaTesco = Companies.loadPrivateLabels(cont, "Tesco");
            privatniZnacka = privatniZnackaTesco;
        }
        else {
            return null;
        }

        for (FirmaData firma : privatniZnacka) {
            if (compLeft(barcode, firma.kod))
                return firma;
        }

        return null;
    }

    private FirmaData getPrivatniZnacka20(Context cont, String barcode) {

        if (privatniZnacka20 == null)
        {
            privatniZnacka20 = Companies.loadPrivateLabels20(cont, "Globus");
            privatniZnacka20.addAll(Companies.loadPrivateLabels20(cont, "Lidl"));
            privatniZnacka20.addAll(Companies.loadPrivateLabels20(cont, "Norma"));
            privatniZnacka20.addAll(Companies.loadPrivateLabels20(cont, "Penny"));
        }

        FirmaData nalezenaFirma = null;
        for (FirmaData firma : privatniZnacka20) {
            if (compLeft(barcode, firma.kod)) {

                if (nalezenaFirma == null) {
                    nalezenaFirma = new FirmaData(firma);
                    nalezenaFirma.pozn = firma.pozn.concat(", ").concat(nalezenaFirma.nazev);
                }
                else  {
                    nalezenaFirma.pozn = nalezenaFirma.pozn.concat("\n").concat(firma.pozn).concat(", ").concat(nalezenaFirma.nazev);
                    nalezenaFirma.holding = nalezenaFirma.holding.equals(firma.holding) ? firma.holding : Companies.Kategorie.NEJASNE;
                }
                nalezenaFirma.pozn = nalezenaFirma.pozn.concat(firma.holding == Companies.Kategorie.HOLDING ? " (AGROFERT)" : "");
                nalezenaFirma.retezec = Companies.Retezec.VICEDODAVATELU;
            }
        }

        return nalezenaFirma;
    }
    // Compare left - srovna dva stringy, jestli se shoduji od zacatku zleva
    private boolean compLeft(String s1, String s2) {
        if (s2.length() > s1.length()) return false;

        int pos = s2.length();
        while (pos-- > 0) {
            if (s2.charAt(pos) != s1.charAt(pos)) return false;
        }
        return true;
    }
}


class ResultData {
    String nazev;
    String dodatek;
    Companies.Kategorie holding;

    public ResultData() {
    }
    public ResultData(String _nazev, String _dodatek, Companies.Kategorie _holding) {
        nazev = _nazev;
        dodatek = _dodatek;
        holding = _holding;
    }
}