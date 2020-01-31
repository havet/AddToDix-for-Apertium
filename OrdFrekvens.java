// OrdFrekvens.java

// ORDFREKVENS: 

// Lingvistik.
// Ber�knar frekvens f�r ord i en text
// ===================================
// Anv�ndbart t.ex. vid konstruktion av ett nytt
// spr�kpar f�r �vers�ttning med Apertium
// www. apertium.org

// Version: 0.98

//      Copyright (c) 2012-2013 Per Tunedal, Stockholm, Sweden
//       Author: Per Tunedal <info@tunedal.nu>

//       This program is free software: you can redistribute it and/or modify
//       it under the terms of the GNU General Public License as published by
//       the Free Software Foundation, either version 3 of the License, or
//       (at your option) any later version.

//       This program is distributed in the hope that it will be useful,
//       but WITHOUT ANY WARRANTY; without even the implied warranty of
//       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//       GNU General Public License for more details.

//       You should have received a copy of the GNU General Public License
//       along with this program.  If not, see <http://www.gnu.org/licenses/>.


// Notes
// =====

// v. 0.1 Comments etc mainly in Swedish.
// v. 0.2 Some bugs fixed.
// v. 0.3 Non existent.
// v. 0.4 More useful output.
// v. 0.5
// v. 0.6
// v. 0.7
// v. 0.8 Bugfix: now the last word is included too.
// v. 0.9 Added a language specific stop list for
//			pronouns, auxiliary verbs and adjectives,
//			and finally, some frequent nouns and
//			ordinary verbs.
// v. 0.95 Improved calculation of percentage for frequency intervals.
// v. 0.96 Number of read lines is printed 
//			for every 1000nd line, when reading from file.
//			Joins splitted words.
// v. 0.97 Removal of unwanted signs improved.
//			Unused large variables are nulled.
// v. 0.98
// -----------------------------------------------------
// Stopplista fr�n textfil (Windows dvs. ANSI Cp1252)
// Du kan sj�lv l�gga till irriterande "ord" till stopplistan!
// Den heter "stoplist.txt";
// ------------------------

// Demonstrerar:
// Flera metoder i ett program
// Anv�nder metoder fr�n flera standardklasser
// 
// Anv�nder metoder fr�n egna klasser f�r inmatning
// och utskrift med korrekta svenska tecken.

// Inmatningskontroll

// L�sning fr�n en textfil
// Skrivning till en textfil

import java.io.*; // Filhantering
import java.util.*; // ArrayList, Scanner, sort, Collections sort, binarySearch
import per.edu.*; // Egna funktioner f�r inmatning, utskrift och filhantering.

class OrdFrekvens

{
	public static void main (String[] args) throws Exception
	{
	// Analyserar en given dansk text
	// K�lla: https://da.wikipedia.org/wiki/Krypteringsalgoritme
	
	// Inmatning av spr�kpar
	// =====================
		
	// till�ten input
	String allowed = "sv-da|sv-nb";
	String pair = "";
		
	while (true)
		{
		pair = Inmatning.rad("Spr�kpar (tex sv-da):", allowed);		
		if (pair.length() != 0) break;
		}		
		
	// Val av spr�k (monodix)
	// ======================
	// till�ten input
	allowed = pair;
	allowed = allowed.replaceAll ("-", "|");

	String lang = "";
		
	while (true)
		{
		lang = Inmatning.rad("Textfilens spr�k (tex sv):", allowed);		
		if (lang.length() != 0) break;
		}
	
	// Stopplista fr�n textfil (Windows dvs. ANSI (Cp1252)
	// Du kan sj�lv l�gga till irriterande "ord" till stopplistan!
	String stopWords = makeStopList ("stoplist.txt");
	
	// Vanliga ord med varianter fr�n resp. ordlista
	// pronomen, hj�lpverb och adjektiv
	stopWords = stopWords + makeStopList ("stoplist." + lang +".txt");
		
	// Lista p� ord som redan finns	
	String finns = OrdSomFinns (pair, lang);
	
	Utskrift.skrivText("V�nta - l�gger ord som finns till listan med stopp-ord");
	// L�gg ihop stoplistan med ord som finns
	stopWords = stopWords + finns;
	finns = null; // Anv�nds inte mer.
	
	// g�r om str�ngen till vektor
	String[] stop = stringToVektor (stopWords);
	stopWords = null; // Anv�nds inte mer.
	Arrays.sort(stop); // sorteras p� plats
		
	//Utskrift.rubrik ("Samtliga stopp-ord");
	//Utskrift.skrivVektor (stop);
	
	// Inmatning av textfil att analysera

	String filnamn = "";
		
	while (true)
		{
		filnamn = Inmatning.rad("Textfil att analysera (t.ex. DanishTowns.txt) :");		
		if (filnamn.length() != 0) break;
		}		
	
	// Inmatning av textfilens kodning (Windows dvs. ANSI (Cp1252), 
	// eller UTF-8 utan BOM)	
	allowed = "win|Win|WIN|utf-8|UTF-8"; // till�ten inmatning
	String kodning = "";
		
	while (true)
		{
		kodning = Inmatning.rad("�r det en vanlig Windows-fil (win)" + 
		" eller en UTF-8 kodad fil (UTF-8)?", allowed);
		kodning.toUpperCase();
		if (kodning.length() != 0) break;
		}	
	// L�ser in en fil med text fr�n aktuell katalog.
	// ==============================================
	// Visar att n�got h�nder:
	// skriver antal skrivna rader f�r varje 1000 l�sta.
	
	Utskrift.skrivText ("V�nta - l�ser fr�n fil!");
		
	String text = "";
	
	if (kodning.toUpperCase().equals ("WIN"))
	{
		text = Textfil.laesText (filnamn, "Cp1252", true);
	}
	else text = Textfil.laesText (filnamn, "UTF-8", true);
	
	Utskrift.rubrik ("Text");
	Utskrift.skrivText (text);
		
	// Texten rensas fr�n on�diga tecken
	// och orden g�rs gemena
	// =================================
	
	// Objekt som �r immutable, tex str�ngar,
	// m�ste �ndras genom tilldelning.
	// H�r: Str�ng text
	
	// Metoderna replaceAll och toLowerCase
	// tar str�ng-objektet "text" som argument
	// och returnerar ett str�ng-objekt som resultat.
	
	// Eftersom metoderna levererar objekt som resultat,
	// kan de staplas p� varandra i en kedja med punkt emellan.
	
	//  replaceAll("\\<.*?>","") // p�st�s ta bort html
	// b�ttre rensa html-, php-, word- och rtf-filer vid k�llan!
	text = text.replaceAll("[\"/(),.:;#~���]", "").replaceAll("\t", " ").replaceAll("\n", " ").replaceAll("\r\n", " ").toLowerCase();
	text = text.replaceAll("<>",""); // tar bort fler skr�ptecken, men !?* funkar ej
	text = text.replaceAll("- ", ""); // s�tt ihop avdelade ord
	
	// Orden kopieras till en vektor
	// =============================
	
	String[] ord = stringToVektor (text);
	text = null; // Anv�nds inte mer
	
	// Sortering
	// =========
	
	// Orden sorteras f�r att enkelt
	// kunna ber�kna frekvensen
	// -----------------------------
	
	// Objekt som �r vektorer
	// (explicit eller implicit)
	// kan f�r�ndras utan tilldelning.
	// H�r: ArrayList ordFrekvens

	Arrays.sort(ord); // sorteras p� plats
		
	// Ber�kning av ordfrekvens
	// ========================
	
	// Ord med frekvens sparas som str�ngar
	// i en ArrayList. Det �r en vektor med str�ng-objekt.
	
	ArrayList<String> ordFrekvens = new ArrayList<String>();
	
	// En ArrayList kan manipuleras direkt,
	// utan tilldelning.	

	frekvensOrd (ord, stop, ordFrekvens); // �ndras p� plats!
	ord = null; // Anv�nds inte mer
	stop = null; // Anv�nds inte mer
		
	// Sortera listan efter frekvensen
	// ===============================
	// Omv�nd sortering
	
	// Objekt som �r vektorer
	// (explicit eller implicit)
	// kan f�r�ndras utan tilldelning.
	// H�r: ArrayList ordFrekvens
	
	Collections.sort(ordFrekvens, Collections.reverseOrder());// Omv�nd
		
	// Skriv ut ett ord per rad, inkl frekvens
	// =======================================
	
	// Allt �r redan f�rberett:
	// Orden �r sparade som str�ngar,
	// med frekvensen f�rst, i
	// ArrayList ordFrekvens
	
	Utskrift.rubrikVersal("ordfrekvens");
			
	for (int i=0;i<ordFrekvens.size();i++)
		Utskrift.skrivText(ordFrekvens.get(i));
		
	// Fr�ga om spara till fil
	// =======================
	
	// regex ^$ betyder tom str�ng
	allowed = "j|J|ja|JA|Ja|n|N|nej|NEJ|Nej|^$"; // Till�tna svar
	String save = Inmatning.rad("Spara (J/n)?", allowed);	
	
	if (save.equals("") || save.matches("j|J|ja|JA|Ja"))
		{
		
		Utskrift.skrivText ("V�nta - skriver till fil!");
		
		// Texten redan klar!
		// H�r anrop av metod!		
				
		filnamn = "Frekvens." + filnamn;
		
		skrivFrekvensTillFil (filnamn, ordFrekvens);
		
		Utskrift.skrivText ("Sparat frekvensordlistan: " + filnamn);
		
		}
	else Utskrift.rubrik ("Sparar ingenting");	

	}
				
	//Ord kopieras  fr�n str�ng till vektor
		
	public static String[] stringToVektor (String text) throws Exception
	{
	// StringTokenizer l�mnar inte extra blanka
	// mellan ord.
		
	StringTokenizer st = new StringTokenizer(text," ");
  
	// Orden sparas i en str�ng-vektor
	// med r�tt l�ngd
	String [] ord = new String[st.countTokens()];
	
	int i = 0;
	
	while (st.hasMoreTokens())
		{
			ord[i++] = st.nextToken();
		}
		
	return ord;	
		
	}
	
	// Ber�kning av ordfrekvens
	// ========================
	// input: Str�ng-vektor och Str�ng-ArrayList
	// output: sparas i bef. ArrayList!
	// En ArrayList kan manipuleras direkt,
	// utan tilldelning.
	
	public static void frekvensOrd (String[] ord, String[] stop, ArrayList<String> ordFrekvens) throws Exception
	
	{	
	int f = 1; // frekvensr�knare
	int len = 0; // l�ngd av frekvensstr�ng
	int antal = 1; // ordr�knare, alla f�rekomster
	int unik = 0; // ordr�knare, unika f�rekomster
	int few5p = 0; // r�knar antal ord med frekvens c:a 5 %
	int few10p = 0; // r�knar antal ord med frekvens c:a 10 %
	int few15p = 0; // r�knar antal ord med frekvens c:a 15 %
	int few20p = 0; // r�knar antal ord med frekvens c:a 20 %
	int[] rareWordCount = new int[10]; // r�knar antal ord med f� tr�ffar
	int few5 = 0; // r�knar unika ord med frekvens c:a 5 %
	int few10 = 0; // r�knar unika ord med frekvens c:a 10 %
	int few15 = 0; // r�knar unika ord med frekvens c:a 15 %
	int few20 = 0; // r�knar unika ord med frekvens c:a 20 %
	int[] rareWord = new int[10]; // r�knar unika ord med f� tr�ffar

	
	String ordet = ord[0];
	String nyttOrd = "";
	
	// Anv�nder Stopplista!
	// �ven f�rsta ordet m�ste kollas
	for (int i=1;i<ord.length;i++)
	{
		 if (ordet.matches("\\d+")) //siffror
			{
			ordet = ord[i];
			}
			
		// J�mf�r med uteslutningsordlista
		else if (Arrays.binarySearch (stop, ordet) >= 0)
			{
			ordet = ord[i];
			}	
	}
				
	for (int i=1;i<ord.length;i++)
	{	
		nyttOrd = ord[i];
		antal++;  // �ven uteslutna r�knas
		
		// j�mf�r med n�sta ord
		if (ordet.equals(nyttOrd))
			{
			f++;
			}
		else if (nyttOrd.matches("\\d+")) //siffror
			{}
			
		// J�mf�r med uteslutningsordlista
		else if (Arrays.binarySearch (stop, nyttOrd) >= 0)
			{}
		else 
			{
			// padding av frekvens till 6 tecken	
			// f�r sortering			
			String fs = Integer.toString(f);
			fs = "000000" + fs;
			len = fs.length();
			String fsp = fs.substring(len-6, len);
			
			ordFrekvens.add(fsp + " " + ordet);
			unik++; // ingen h�nsyn till uteslutna ord
			
			if (f < 2)
			{
			rareWordCount[0] = rareWordCount[0] + f;
			rareWord[0]++;
			}
			if (f < 3)
			{
			rareWordCount[1] = rareWordCount[1] + f;
			rareWord[1]++;
			}
			if (f < 4)
			{
			rareWordCount[2] = rareWordCount[2] + f;
			rareWord[2]++;
			}
			if (f < 5)
			{
			rareWordCount[3] = rareWordCount[3] + f;
			rareWord[3]++;
			}
			if (f < 6)
			{
			rareWordCount[4] = rareWordCount[4] + f;
			rareWord[4]++;
			}
			if (f < 7)
			{
			rareWordCount[5] = rareWordCount[5] + f;
			rareWord[5]++;
			}
			if (f < 8)
			{
			rareWordCount[6] = rareWordCount[6] + f;
			rareWord[6]++;
			}
			if (f < 9)
			{
			rareWordCount[7] = rareWordCount[7] + f;
			rareWord[7]++;
			}
			if (f < 10)
			{
			rareWordCount[8] = rareWordCount[8] + f;
			rareWord[8]++;
			}
			if (f < 11)
			{
			rareWordCount[9] = rareWordCount[9] + f;
			rareWord[9]++;
			}
			
			f=1;
			ordet = nyttOrd;
			}
	}

	// L�gger till sista ordet
		if (ordet.matches("\\d+")) //siffror
			{}
			
			// J�mf�r med uteslutningsordlista
		else if (Arrays.binarySearch (stop, ordet) >= 0)
			{}
		else
			{
				// padding av frekvens till 6 tecken	
				// f�r sortering			
				String fs = Integer.toString(f);
				fs = "000000" + fs;
				len = fs.length();
				String fsp = fs.substring(len-6, len);
				
				ordFrekvens.add(fsp + " " + ordet);
				unik++; // ingen h�nsyn till uteslutna ord
				
				if (f < 2)
				{
				rareWordCount[0] = rareWordCount[0] + f;
				rareWord[0]++;
				}
				if (f < 3)
				{
				rareWordCount[1] = rareWordCount[1] + f;
				rareWord[1]++;
				}
				if (f < 4)
				{
				rareWordCount[2] = rareWordCount[2] + f;
				rareWord[2]++;
				}
				if (f < 5)
				{
				rareWordCount[3] = rareWordCount[3] + f;
				rareWord[3]++;
				}
				if (f < 6)
				{
				rareWordCount[4] = rareWordCount[4] + f;
				rareWord[4]++;
				}
				if (f < 7)
				{
				rareWordCount[5] = rareWordCount[5] + f;
				rareWord[5]++;
				}
				if (f < 8)
				{
				rareWordCount[6] = rareWordCount[6] + f;
				rareWord[6]++;
				}
				if (f < 9)
				{
				rareWordCount[7] = rareWordCount[7] + f;
				rareWord[7]++;
				}
				if (f < 10)
				{
				rareWordCount[8] = rareWordCount[8] + f;
				rareWord[8]++;
				}
				if (f < 11)
				{
				rareWordCount[9] = rareWordCount[9] + f;
				rareWord[9]++;
				}
			}
	//Utskrift.rubrik("rareWordCount: antal ord med f� tr�ffar");
	//Utskrift.skrivVektor(rareWordCount);
	//Utskrift.rubrik("rareWord: unika ord med f� tr�ffar");
	//Utskrift.skrivVektor(rareWord);
		
	// Passningsber�kning:	
	System.out.println("Antal: :" + antal);
	
	// F�rs�ker hitta ungef�r 5 %:
	//int t5 = 2; // b�rjar p� f�rre �n 2
	int t5 = 0;
	for (int i=0;i < rareWordCount.length;i++) // i = t-2
	{
		//t5 = t5 + i;
		t5 = 2 + i;
		few5 = rareWord[i];
		few5p = 100 * rareWordCount[i]/antal;
		if (few5p >= 5) break;
	}
	
	// F�rs�ker hitta ungef�r 10 %:
	few10 = few5;
	few10p = few5p;
	//int t10 = t5; // forts�tter
	int t10 = 0;
	for (int i=t5-2;i < rareWordCount.length;i++)
	{
		//t10 = t10 + i;
		t10 = 2 + i;
		few10 = rareWord[i];
		few10p = 100 * rareWordCount[i]/antal;
		if (few10p >= 10) break;
	}
	
	// F�rs�ker hitta ungef�r 15 %:
	few15 = few10;
	few15p = few10p;
	//int t15 = t10; // forts�tter
	int t15 = 0;
	for (int i=t10-2;i < rareWordCount.length;i++)
	{
		//t15 = t15 + i;
		t15 = 2 + i;
		few15 = rareWord[i];
		few15p = 100 * rareWordCount[i]/antal;
		if (few15p >= 15) break;
	}
	
	// F�rs�ker hitta ungef�r 20 %:
	few20 = few15;
	few20p = few15p;
	//int t20 = t15;
	int t20 = 0;
	for (int i=t15-2;i < rareWordCount.length;i++)
	{
		//Utskrift.skrivText("Ber�kn t20, i: " + i);
		//t20 = t20 + i;
		t20 = 2 + i;
		few20 = rareWord[i];
		few20p = 100 * rareWordCount[i]/antal;
		if (few20p >= 20) break;
	}
	
	// Obs! �ven nedanst�ende sorteras omv�nt f�re skrivning till fil!
	ordFrekvens.add ("Unika nya ord: " + unik);
	ordFrekvens.add ("Totala antalet ord, inkl. uteslutna: " + antal);

	ordFrekvens.add ("A: ======================================================================================");
	//if (few15 != few20 && t15 != t20)
	if (t15 != t20)
	ordFrekvens.add ("B: " + (unik - few20) + " ord med " + t20 + " eller fler f�rekomster utg�r " + Math.round(100 - few20p) + " % av alla ord.");
	//if (few10 != few15 && t10 != t15)
	if (t10 != t15)
	ordFrekvens.add ("C: " + (unik - few15) + " ord med " + t15 + " eller fler f�rekomster utg�r " + Math.round(100 - few15p) + " % av alla ord.");
	//if (few5 != few10 && t5 != t10)
	if (t5 != t10)
	ordFrekvens.add ("D: " + (unik - few10) + " ord med " + t10 + " eller fler f�rekomster utg�r " + Math.round(100 - few10p) + " % av alla ord.");

	ordFrekvens.add ("E: " + (unik - few5) + " ord med " + t5 + " eller fler f�rekomster utg�r " + Math.round(100 - few5p) + " % av alla ord.");
	}

	// Skriv frekvenstabellen till fil
	
	public static void skrivFrekvensTillFil (String filnamn, ArrayList<String> ordFrekvens) throws Exception
	
	{
	
	// Skriver f�rst �ver
	Textfil.skrivText(filnamn, "Ordfrekvens\r\n");
	
	// L�gger sedan till en massa g�nger
	
	// Binder en textfil till en variabel
	// Filen skapas om den inte finns.
	// Anv�nder paketet java.io
	
	File   fil = new File (filnamn); // skapas i aktuell katalog
	
	// Skapar ett FileWriter-objekt f�r skrivning till filen
	// och binder objektet till filen
	// Filen �ppnas samtidigt f�r skrivning.
	// Anv�nder paketet java.io
	
	// L�gger till i slutet p� filen
	// (argumentet "true")
	
	FileWriter fw = new FileWriter (fil, true);
	
	// Skapar ett PrintWriter-objekt f�r 
	// bekv�m skrivning till filen.
	// (Automatisk omvandling till tkn-str�ng.)
	// och binder det till FileWriter-objektet
	// Anv�nder paketet java.util	
	
	PrintWriter fout = new PrintWriter (fw);
	
	// Buffrad utskrift
	// skapar ett BufferedWriter-objekt f�r skrivning
	// och binder det till FileWriter-objektet.
	
	//BufferedWriter fout = new BufferedWriter (fw);
	
	// Skriver till filen med radbrytning
	
	fout.println ("==========="); // Understrykning
	
	// Skriver frekvenslistan
			
	for (int i=0;i<ordFrekvens.size();i++)
	
		fout.println (ordFrekvens.get(i));
		
	// St�nger filen
	// framtvingar skrivning av de sista buffrade tecken,
	
	fout.close();  // St�nger
	
	}
	
		// Lista p� ord som redan finns i monodix
		// (f�r att kunna utesluta dem)
		
	public static String OrdSomFinns (String pair, String lang) throws Exception
	{			
		String p = "";
		
		String fil = "apertium-" + pair + "." + lang + ".dix";
		
		FileInputStream fs = new FileInputStream(fil); // byte till character
		Scanner fin = new Scanner (fs, "UTF-8"); // r�tt tecken!

		String rad= "";
		int i = 0;
		int j = 0;
	
		while (fin.hasNextLine())	
		{	
			rad = fin.nextLine();
			
			if (rad.contains("<e lm=")) // Definition av ord
			
			{
				// Ord som finns i ordlistan
				// Exempel:
				// <e lm="deres">
				// <e lm="dens" a="PT">

				// b�rjan av ordet
				i = rad.indexOf('=');
				
				// slutet av ordet
				j = rad.indexOf("\" ");
				
				if (j > 0)
					{}
				else
					j = rad.indexOf('>') - 1;
					//j = rad.indexOf("\">"); // funkar ocks�
				
				p = p + rad.substring(i+2, j) + " ";			
			}		
		}
		
		fin.close();
		
		return p; // Str�ng
		
	}
	
	// L�s en stoppliste-fil till en str�ng
	public static String makeStopList (String filename) throws Exception
	{
		Utskrift.skrivText("V�nta - g�r lista p� stopp-ord");
		
		String rad = "";
		int antal = 0;
				
		FileInputStream fs = new FileInputStream(filename); // byte till character
		//Windowsfil
		Scanner fin = new Scanner (fs, "Cp1252"); // r�tt tecken!
		
		while (fin.hasNextLine())
		{
			rad = rad + fin.nextLine() + " ";
			//System.out.println(".");
			antal++;
			//System.out.println(antal);
			if (0 == Math.round(antal%1000)) Utskrift.skrivText ("L�st "+ antal);
		}
	
		fin.close(); // St�nger filen
				
		return rad;
	}
}
