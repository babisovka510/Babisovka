package cz.example.babisovka;

public class Countries {

    //
    // Zjisti nazev zeme ve ktere je vyrobce registrovany, podle prvnich tri znaku v barcode
    //
    public static ZemeData getCompanyCountry(String barcode)
    {
        String strCountryCode = barcode.length()>2 ? barcode.substring(0, 3) : "000";
        int iCode = Integer.parseInt(strCountryCode);

        if ( (iCode >= 0 && iCode <= 19) || (iCode >= 30 && iCode <= 39) || (iCode >= 60 && iCode <= 139) )
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"USA/Kanada", "z USA/Kanady");
        else if ( (iCode >= 20 && iCode <= 29) || (iCode >= 40 && iCode <= 49) )
            return new ZemeData(iCode,  Kategorie.PRIVATNI,"neznámá, lokální užití", "neznámá, lokální užití");
        else if ( (iCode >= 200 && iCode <= 209) )
            return new ZemeData(iCode,  Kategorie.PRIVATNI,"privátní značka", "privátní značka");
        else if ( (iCode >= 210 && iCode <= 299) )
            return new ZemeData(iCode,  Kategorie.VAHOVE,"váhový kód", "váhový kód");
        else if (iCode >= 300 && iCode <= 379)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Francie", "z Francie");
        else if (iCode == 380)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Bulharsko", "z Bulharsko");
        else if (iCode == 383)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Slovinsko", "ze Slovinska");
        else if (iCode == 385)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Chorvatsko", "z Chorvatska");
        else if (iCode == 387)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Bosna a Hercegovina", "z Bosny a Hercegoviny");
        else if (iCode == 389)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Černá Hora", "z Černé Hory");
        else if (iCode >= 400 && iCode <= 440)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Německo", "z Německa");
        else if ( (iCode >= 450 && iCode <= 459) || (iCode >= 490 && iCode <= 499) )
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Japonsko", "z Japonska");
        else if (iCode >= 460 && iCode <= 469)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Rusko", "z Ruska");
        else if (iCode == 470)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Kyrgyzstán", "z Kyrgyzstánu");
        else if (iCode == 471)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Tchaj-wan", "z Tchaj-wanu");
        else if (iCode == 474)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Estonsko", "z Estonska");
        else if (iCode == 475)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Lotyšsko", "z Lotyšska");
        else if (iCode == 476)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Ázerbajdžán", "z Ázerbajdžánu");
        else if (iCode == 477)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Litva", "z Litvy");
        else if (iCode == 478)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Uzbekistán", "z Uzbekistánu");
        else if (iCode == 479)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Srí Lanka", "ze Srí Lanky");
        else if (iCode == 480)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Filipíny", "z Filipín");
        else if (iCode == 481)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Bělorusko", "z Běloruska");
        else if (iCode == 482)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Ukrajina", "z Ukrajiny");
        else if (iCode == 484)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Moldavsko", "z Moldavska");
        else if (iCode == 485)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Arménie", "z Arménie");
        else if (iCode == 486)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Gruzie", "z Gruzie");
        else if (iCode == 487)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Kazachstán", "z Kazachstánu");
        else if (iCode == 488)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Tádžikistán", "z Tádžikistánu");
        else if (iCode == 489)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Hong Kong", "z Hong Kongu");
        else if (iCode >= 500  && iCode <= 509)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Velká Británie", "z Velké Británie");
        else if (iCode >= 520 && iCode <= 521)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Řecko", "z Řecka");
        else if (iCode == 528)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Libanon", "z Libanonu");
        else if (iCode == 529)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Kypr", "z Kypru");
        else if (iCode == 530)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Albánie", "z Albánie");
        else if (iCode == 531)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Makedonie", "z Makedonie");
        else if (iCode == 535)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Malta", "z Malty");
        else if (iCode == 539)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Irsko", "z Irska");
        else if (iCode >= 540 && iCode <= 549)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Belgie a Lucembursko", "z Belgie/Lucemburska");
        else if (iCode == 560)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Portugalsko", "z Portugalska");
        else if (iCode == 569)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Island", "z Islandu");
        else if (iCode >= 570 && iCode <= 579)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Dánsko", "z Dánska");
        else if (iCode == 590)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Polsko", "z Polska");
        else if (iCode == 594)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Rumunsko", "z Rumunska");
        else if (iCode == 599)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Maďarsko", "z Maďarska");
        else if (iCode >= 600 && iCode <= 601)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Jižní Afrika", "z Jižní Afriky");
        else if (iCode == 603)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Ghana", "z Ghany");
        else if (iCode == 604)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Senegal", "ze Senegalu");
        else if (iCode == 608)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Bahrajn", "z Bahrajnu");
        else if (iCode == 609)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Mauricius", "z Mauricia");
        else if (iCode == 611)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Maroko", "z Maroka");
        else if (iCode == 613)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Alžír", "z Alžíru");
        else if (iCode == 615)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Nigérie", "z Nigérie");
        else if (iCode == 616)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Keňa", "z Keni");
        else if (iCode == 618)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Pobřeží slonoviny", "z Pobřeží slonoviny");
        else if (iCode == 619)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Tunisko", "z Tuniska");
        else if (iCode == 620)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Tanzánie", "z Tanzánie");
        else if (iCode == 621)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Sýrie", "ze Sýrie");
        else if (iCode == 622)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Egypt", "z Egypta");
        else if (iCode == 623)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Brunej", "z Bruneje");

        else if (iCode == 624)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Libye", "z Libye");
        else if (iCode == 625)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Jordánsko", "z Jordánska");
        else if (iCode == 626)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Irán", "z Iránu");
        else if (iCode == 627)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Kuvajt", "z Kuvajtu");
        else if (iCode == 628)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Saudská Arábie", "ze Saudské Arábie");
        else if (iCode == 629)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Spojené arabské emiráty", "ze Spojených arabských emirátů");
        else if (iCode >= 640 && iCode <= 649)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Finsko", "z Finska");
        else if (iCode >= 690 && iCode <= 699)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Čína", "z Číny");
        else if (iCode >= 700 && iCode <= 709)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Norsko", "z Norska");
        else if (iCode == 729)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Izrael", "z Izraele");
        else if (iCode >= 730 && iCode <= 739)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Švédsko", "ze Švédska");
        else if (iCode == 740)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Guatemala", "z Guatemaly");
        else if (iCode == 741)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Salvádor", "ze Salvádoru");
        else if (iCode == 742)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Honduras", "z Hondurasu");
        else if (iCode == 743)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Nikaragua", "z Nikaragui");
        else if (iCode == 744)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Kostarika", "z Kostariky");
        else if (iCode == 745)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Panama", "z Panamy");
        else if (iCode == 746)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Dominikánská republika", "z Dominikánské republiky");
        else if (iCode == 750)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Mexiko", "z Mexika");
        else if (iCode >= 754 && iCode <= 755)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Kanada", "z Kanady");
        else if (iCode == 759)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Venezuela", "z Venezuely");
        else if (iCode >= 760 && iCode <= 769)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Švýcarsko", "ze Švýcarska");
        else if (iCode >= 770 && iCode <= 771)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Kolumbie", "z Kolumbie");
        else if (iCode == 773)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Uruguay", "z Uruguaye");
        else if (iCode == 775)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Peru", "z Peru");
        else if (iCode == 777)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Bolívie", "z Bolívie");
        else if (iCode >= 778 && iCode <= 779)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Argentina", "z Argentiny");
        else if (iCode == 780)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Chile", "z Chile");
        else if (iCode == 784)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Paraguay", "z Paraguaye");
        else if (iCode == 786)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Ekvádor", "z Ekvádoru");
        else if (iCode >= 789 && iCode <= 790)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Brazílie", "z Brazílie");
        else if (iCode >= 800 && iCode <= 839)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Itálie", "z Itálie");
        else if (iCode >= 840 && iCode <= 849)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Španělsko", "ze Španělska");
        else if (iCode == 850)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Kuba", "z Kuby");
        else if (iCode == 858)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Slovensko", "ze Slovenska");
        else if (iCode == 859)
            return new ZemeData(iCode,  Kategorie.CZ,"Česká republika", "z České republiky");
        else if (iCode == 860)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Srbsko", "ze Srbska");
        else if (iCode == 865)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Mongolsko", "z Mongolska");
        else if (iCode == 867)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Severní Korea", "ze Severní Koreje");
        else if (iCode >= 868 && iCode <= 869)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Turecko", "z Turecka");
        else if (iCode >= 870 && iCode <= 879)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Holandsko", "z Holandska");
        else if (iCode == 880)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Jižní Korea", "z Jižní Koreje");
        else if (iCode == 884)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Kambodža", "z Kambodže");
        else if (iCode == 885)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Thajsko", "z Thajska");
        else if (iCode == 888)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Singapur", "ze Singapuru");
        else if (iCode == 890)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Indie", "z Indie");
        else if (iCode == 893)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Vietnam", "z Vietnamu");
        else if (iCode == 896)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Pákistán", "z Pákistánu");
        else if (iCode == 899)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Indonésie", "z Indonésie");
        else if (iCode >= 900 && iCode <= 919)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Rakousko", "z Rakouska");
        else if (iCode >= 930 && iCode <= 939)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Austrálie", "z Austrálie");
        else if (iCode >= 940 && iCode <= 949)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Nový Zéland", "z Nového Zélandu");
        else if (iCode >= 950 && iCode <= 951)
            return new ZemeData(iCode,  Kategorie.JINA,"EPCglobal", "EPCglobal");
        else if (iCode == 955)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Malajsie", "z Malajsie");
        else if (iCode == 958)
            return new ZemeData(iCode,  Kategorie.ZAHRANICNI,"Makao", "z Makaa");
        else if (iCode >= 960 && iCode <= 969)
            return new ZemeData(iCode,  Kategorie.JINA,"GS1", "GS1");
        else if (iCode == 977)
            return new ZemeData(iCode,  Kategorie.JINA,"seriálové publikace (ISSN)", "seriálové publikace (ISSN)");
        else if (iCode >= 978 && iCode <= 979)
            return new ZemeData(iCode,  Kategorie.JINA,"knihy (ISBN)", "knihy (ISBN)");
        else if (iCode == 980)
            return new ZemeData(iCode,  Kategorie.JINA,"vratná účtenka", "vratná účtenka");
        else if (iCode >= 981 && iCode <= 984)
            return new ZemeData(iCode,  Kategorie.JINA,"platební poukázka", "platební poukázka");
        else if (iCode >= 990 && iCode <= 999)
            return new ZemeData(iCode,  Kategorie.JINA,"poukázka", "poukázka");
        else
            return new ZemeData(iCode,  Kategorie.JINA,"neznámý kód země", "neznámý kód země");
    }

    public enum Kategorie {
        CZ,
        ZAHRANICNI,
        PRIVATNI,
        VAHOVE,
        JINA
    }
}

class ZemeData {
    int kod;
    Countries.Kategorie kategorie;
    String nazev;
    String nazevSklonovany;

    public ZemeData(int _kod, Countries.Kategorie _kategorie, String _nazev, String _nazevSklonovany) {
        kod = _kod;
        kategorie=_kategorie;
        nazev = _nazev;
        nazevSklonovany = _nazevSklonovany;
    }
}
