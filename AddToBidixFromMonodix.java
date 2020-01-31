//AddToBidixFromMonodix.java

// ADD TO BIDIX FROM MONODIX:

// Skapa tv�spr�kig ordlista f�r Apertium
// Utnyttjar egen enspr�kig ordlista
// skapad med AddToDictionary.
// Maskin�vers�ttning
// www.apertium.org
// ====================================== 

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
// v. 0.2 Lots of bugs fixed.
// Some new features like e.g.
// 			File names displayed after saving for easy retrieval.
// v. 0.3 User has to admit GPL license for the dix.
// v. 0.4 More helpful questions.
// v. 0.5 Better algo for finding lemma.
// v. 0.6 Encourgement: number of added words displayed
//			Nicer output.
// v. 0.7 Reads part of speach from monodix
//			Bugs fixed.
// v. 0.8 
// v. 0.9
// v. 0.95
// v. 0.96 Possibility to quit after each 5 added words.
// v. 0.97 List of words to translate now only contains new words.
// 			The number of remaining words is displayed after each 5 treated.
//			Nicer output.  Fixed bug: now stops nicely after last entry.
// v. 0.98
//			Fixed bugs:
//			Stops nicely if all words in monodix already are present in bidix.
//			Stop-words from correct language: left or right language in bidix.
//
// ----------------------------------------------------------------

// Demonstrerar:
// Flera metoder i ett program
// Anv�nder metoder fr�n flera standardklasser
// 
// Anv�nder metoder fr�n egna klasser f�r inmatning
// och utskrift med korrekta svenska tecken.

// Inmatningskontroll

// L�sning fr�n en textfil
// Skrivning till en textfil

import java.util.*; // ArrayList, Scanner
import java.io.*; // f�r att skapa/�ppna en fil (File)
import per.edu.*;

class AddToBidixFromMonodix

{	
	public static void main (String[] args) throws Exception	
	{
		
	// Inmatning av namn
	// =================

	String namn = "";
		
	while (true)
		{
		namn = Inmatning.rad("Ditt namn:");	
		if (namn.length() != 0) break;
		}

	// Inmatning av signatur
	// =====================

	String author = "";
		
	while (true)
		{
		author = Inmatning.rad("Din signatur:");	
		if (author.length() != 0) break;
		}
		
	// Inmatning av e-post
	// ===================

	String epost = "";
		
	while (true)
		{
		epost = Inmatning.rad("Din e-postadress:");	
		if (epost.length() != 0) break;
		}	
	
	// Till�ta att dix licensieras under GPL
	
	String gpl = "";
	String allowed = "ja|JA|Ja|nej|NEJ|Nej"; // Till�tna svar

	Utskrift.rubrik ("Apertium inkl. ordlistorna �r licensierat enl. GPL v.2");
	Utskrift.skrivText("Du m�ste till�ta att den ordlista du bidrar med licensieras enl. GPL version 2 eller senare,\n" 
	+ "f�r att ditt bidrag ska komma till nytta. https://www.gnu.org/licenses/old-licenses/gpl-2.0.html\n");
	while (true)
		{
		gpl = Inmatning.rad("Ja, jag till�ter att ordlistan licensieras enl. GPL version 2 eller senare (JA/NEJ):", allowed);	
		if (gpl.length() != 0) break;
		}
	if (gpl.matches("nej|NEJ|Nej")) System.exit(0); // Quit the program
	
	// Inmatning av spr�kpar
	// =====================
		
	// till�ten input
	allowed = "sv-da|sv-nb";
	String pair = "";
		
	while (true)
		{
		pair = Inmatning.rad("Spr�kpar (tex sv-da):", allowed);		
		if (pair.length() != 0) break;
		}
		
	// Val av spr�k (monodix)
	// ----------------------
	// till�ten input
	allowed = pair;
	allowed = allowed.replaceAll ("-", "|");

	String lang = "";
	
	Utskrift.rubrik("K�llspr�k");
	while (true)
		{
		lang = Inmatning.rad("Ordlistan du ska �vers�tta FR�N �r p� spr�ket (tex sv):", allowed);		
		if (lang.length() != 0) break;
		}
		
	// Typ av ord
	// ==========
	// Lingvistisk kategori
	// dvs ordklass
	
	// Skapar lista p� ordklasser
	TreeMap<String, String> tmKlass = partOfSpeach ();
	
	String klass = lasKlass (pair, lang);

	//Utskrift.skrivText("Orden i ordlistan tillh�r ordklass: " + klass);
	Utskrift.skrivText("Orden i ordlistan tillh�r ordklass: " + tmKlass.get(klass) + " ("+ klass + ")\n");

	// Ber�kna spr�kets position
	// =========================
	// dvs. �r spr�ket till v�nster (L)
	// eller till h�ger (R)
	// Beh�vs f�r att l�sa fr�n bidix
		
	String pos = languagePosition (lang, pair);
	
	// Lista p� ord som redan finns
	// jfr AddToDictionaryFromBidix!
	// Listan m�ste rensas n�r den skapas.
	Utskrift.skrivText("V�nta - g�ra en lista p� ord som redan finns");
	String[] stop = lasBidix (pair, klass, pos);

	Utskrift.skrivVektor(stop);
		
	// Utg� fr�n egen ny monodix
	// =========================
	
	// L�ser in lemma fr�n monodix
	// ---------------------------
	// Rensas fr�n ord som finns (stop) vid inl�sningen!
	ArrayList<String> WordsToTranslate = lasMonodix (pair, lang, stop);
	
	int ant = WordsToTranslate.size();
	Utskrift.rubrik ("Antal ord att �vers�tta: " + ant);
	if (ant == 0) System.exit(0);
		
	// L�ser v�nster och h�ger spr�k
	// fr�n pair
	
	int len = pair.length();
	int ind = pair.indexOf("-");
	String left = pair.substring(0, ind);
	String right = pair.substring(ind+1);
	//Utskrift.skrivText("V�nster spr�k: " + left + " H�ger spr�k: " + right);
	
	// Ber�kna spr�kets position  (monodix)
	// =========================
	// dvs. �r spr�ket till v�nster (L)
	// eller till h�ger (R)
	// Beh�vs f�r att utnyttja monodix
			
/**************************************
	// position p� nytt s�tt
	String pos = "R";
	if (left.equals(lang)) pos = "L";
*****************************************/
	
	// Skapar lista p� spr�k
	TreeMap<String, String> tmLang = languages ();
	//Utskrift.skrivText (tmLang);
	
	// Samla in ordbeskrivningarna
	// ===========================
		
	// Sparas i en ArrayList
	// s� att det �r l�tt att l�gga till rader.
		
	ArrayList<String> alDix = new ArrayList<String>();
		
		
	// Inmatning av ord
	// ================
	String rad = "";
	int added = 0;
		
	// L�gg till en rad (str�ng) i taget
	// avsluta med ENTER
	Utskrift.rubrik ("Nytt ord (klass: " + klass + ")");
	while (true && added < WordsToTranslate.size())
		{
		// mall: paradigm-stam, klass: ordklass
		//pos: v�nster eller h�ger spr�k , stop: ord som finns
		rad = defineWordFromMonodix (added, WordsToTranslate, pos, left, right, tmLang, author, klass);
		if (rad == "") break; // avsluta med ENTER
				
		// Skriv ut raden och ge m�jlighet att �ndra
		Utskrift.rubrik ("Nya ordet");
		Utskrift.skrivText (rad);
		String change = Inmatning.rad("�ndra (j/N) ?");

		if (change.equals("") || !(change.charAt(0)=='j'))
			{
				alDix.add (rad);
				added++; //r�knar antalet tillagda ord
				Utskrift.skrivText("Antal ord: " + added); // Uppmuntran!
				
				if (added == WordsToTranslate.size()) break;
				
				Utskrift.rubrik("N�sta ord");
				
				// g�r backup f�r var 10:e rad			
				//if (0 == Math.round(alDix.size()%10))
				if (0 == Math.round(added%10))
				{
					String filNamn = pair + "." + pair + "." + "dix.txt.back";
					dixBackup (alDix, filNamn); // Backup till fil
				}
				
				// M�jlighet att avsluta efter var 5:e ord.
				if (0 == Math.round(added%5))
				{	
					//Utskrift.skrivText("Du har matat in " + added + " ord");
					Utskrift.skrivText("Du har matat in " + added + " ord. �terst�r: " + (WordsToTranslate.size() - added));
					allowed = "j|J|ja|JA|Ja|n|N|nej|NEJ|Nej|^$"; // Till�tna svar (^$ = tomt)
					String sluta = Inmatning.rad("Sluta (j/N)?", allowed);	
					if (!sluta.equals("") && sluta.charAt(0)=='j') break;
				}
			}
		else 
			{
				Utskrift.skrivText("Mata in ordet p� nytt!");
				//added dvs. ordets index i WordsToTranslate of�r�ndrat.
			}
		}
		
	// Skriv ut hela listan med radnr
	// ==============================
	String raden = "";
	
	//Utskrift.skrivText ("Antal inmatade ord: " + alDix.size());
	
	for (int i=0; i<alDix.size(); i++)
	
	{
		// F�rsta raden har nr 1, men index 0!
		// ===================================
		raden = Integer.toString(i+1) + " " + (String) alDix.get(i);
		Utskrift.skrivText (raden);
	}
	// M�jlighet att �ndra viss rad.
	// Avsluta med 0
	int r = 0;
	
	while (true)
	{
		Utskrift.rubrik ("Vill du �ndra n�gon rad (1, 2, 3 ...) ?");
		r = Inmatning.heltal ("Ange radnummer (avsluta med 0):", 1 + alDix.size()); //maxv�rde!
		
		if (r == 0) break;
		else
		{		
			r--; // r �ndras till korrekt index
			andraRad(alDix, r, WordsToTranslate, pos, tmLang, left, right, author, klass); // �ndras p� plats
		}
	}
	
	// Formatera listan dvs. l�gg till HTML-taggar
	// ===========================================

	String bidix = "";
	String line = "====================================================" + "\n";
	bidix = bidix + "Bilingual dictionnary for " + pair + "\n";
	//bidix = bidix + "Part of speach: " + klass + "\n" + line + "\n";
	bidix = bidix + "Part of speach: " + tmKlass.get(klass) + " (" + klass + ")\n" + line + "\n";
	
	bidix = bidix + makeXMLbidix (alDix);
	
	String lic = "Jag till�ter att ordlistan licensieras enl. GPL version 2 eller senare.";
	bidix = bidix + "\n" + lic + "\n" + namn + "\n" + epost; //avs�ndare

	if (bidix.length() != 0) Utskrift.skrivText ("\n" + bidix + "\n");
	else Utskrift.skrivText ("Inget inmatat");
	
	
	// Fr�ga om spara till fil
	// =======================
	
	// regex ^$ betyder tom str�ng
	allowed = "j|J|ja|JA|Ja|n|N|nej|NEJ|Nej|^$"; // Till�tna svar
	String save = Inmatning.rad("Spara (J/n)?", allowed);	
	
	if (save.equals("") || save.matches("j|J|ja|JA|Ja"))
		{
		Utskrift.skrivText ("V�nta! Sparar ordlistan.");
		// Texten redan klar!
		// H�r anrop av metod f�r att skriva till fil.
		String filnamn = pair + "." + pair + "." + "dix.txt";
		Textfil.skrivText (filnamn, bidix, "UTF-8");
		Utskrift.rubrik ("Sparat ordlistan: " + filnamn);
		
		}
	else Utskrift.rubrik ("Sparar ingenting");	
	}
				
	
	public static String defineWordFromMonodix (int rad, ArrayList<String> WordsToTranslate, String languagePosition, String left, String right, TreeMap<String, String> tmLang, String author, String klass) throws Exception

	{
		String ord = "";
		String toTranslate = "";
					
		// uppgifterna skiljs �t med tecknet |
		// <e a="PT">       <p><l>godk�nnande<s n="n"/></l>              <r>accept<s n="n"/></r></p><par n="_nt_ut"/></e>
		// bidix: author, L lemma, L paradigm, R lemma, R paradigm
		if (languagePosition.equals("L")) // V�nster lemma givet
		{
			//Utskrift.skrivText("lemma att �vers�tta fr�n " + tmLang.get(left) + ": " + WordsToTranslate.get(rad));
			toTranslate = WordsToTranslate.get(rad);
			Utskrift.rubrik("Lemma (grundform) att �vers�tta fr�n " + tmLang.get(left) + ": " + toTranslate);
			Utskrift.skrivText ("(danska/norska tecken: � � � svenska tecken: � � �)");
			String lemma = Inmatning.rad("lemma " + tmLang.get(right) + ":").trim();
			if (lemma.length() == 0) return ord = "";

			else
			{
				ord = ord + "|" + author;
				//ord = ord + "|" + lemma;
				//toTranslate
				//stam = stam.replaceAll(" ","<b/>");
				toTranslate = toTranslate.replaceAll(" ","<b/>");
				ord = ord + "|" + toTranslate;
				//ord = ord + "|" + WordsToTranslate.get(rad);
				ord = ord + "|" + klass;
				
				// bidix: riktning, author, L lemma, L paradigm, R lemma, R paradigm
				// Riktning ska EJ anv�ndas f�r att v�lja ord!
				lemma = lemma.replaceAll(" ","<b/>");
				ord = ord + "|" + lemma;
				
				ord = ord + "|" + klass;
				
				if (klass.equals("n"))
					{
					Utskrift.rubrik ("Paradigmer");
					Utskrift.skrivText ("Ange ordens genus");
					ord = ord + "|" + askParadigm (tmLang, left, right)  + "|";
					}
					
				else
				
					{
						ord = ord +"||";
					}
					
				Utskrift.skrivText(ord);
				return ord;
			}
		}
		
		else //languagePosition = R: H�ger ord givet.
		{
			toTranslate = WordsToTranslate.get(rad);
			Utskrift.rubrik("Lemma (grundform) att �vers�tta fr�n " + tmLang.get(right) + ": " + toTranslate);
			Utskrift.skrivText ("(danska/norska tecken: � � � svenska tecken: � � �)");
			String lemma = Inmatning.rad("lemma " + tmLang.get(left) + ":").trim();
			if (lemma.length() == 0) return ord = "";

			else
			{
				ord = ord + "|" + author;
				lemma = lemma.replaceAll(" ","<b/>");
				ord = ord + "|" + lemma;
				ord = ord + "|" + klass;
				
				// bidix: riktning, author, L lemma, L paradigm, R lemma, R paradigm
				// Riktning ska EJ anv�ndas f�r att v�lja ord!
				
				toTranslate = toTranslate.replaceAll(" ","<b/>");
				ord = ord + "|" + toTranslate;
				
				ord = ord + "|" + klass;
								
				if (klass.equals("n"))
					{
					Utskrift.rubrik ("Paradigmer");
					Utskrift.skrivText ("Ange ordens genus");
					ord = ord + "|" + askParadigm (tmLang, left, right)  + "|";
					}
					
				else
				
					{
						ord = ord +"||";
					}
				
				Utskrift.skrivText(ord);
				return ord;
			}
		}
	}
		
	// XML-taggar l�ggs till en bidix
		
	public static String makeXMLbidix (ArrayList alDix) throws Exception
	{
	// <e a="PT">       <p><l>passa<s n="vblex"/></l>                <r>passe<s n="vblex"/></r></p></e>
	String bidix = "";
	String author = "";
	String vLemma = ""; // left lemma
	String vKlass = ""; // left part of speach
	String hLemma = ""; // right lemma
	String hKlass = ""; // right part of speach
	String paradigm = ""; //  paradigm
		
	String rad = "";
		
	for (int r = 0; r < alDix.size(); r++)
		{		
		rad = (String) alDix.get(r);
				
		Scanner	sc = new Scanner (rad);
		// Obs! Delimiter m�ste anges som regex! [] �r teckenklass
		sc.useDelimiter ("[|]"); // tv� i f�ljd ger TOM str�ng

		author = sc.next();
		bidix = bidix + "<e a=\"" + author + "\">       <p><l>";
	
		vLemma = sc.next();
		bidix = bidix + vLemma + "<s n=\"";
		
		vKlass = sc.next();
		bidix = bidix + vKlass + "\"/></l>                <r>";

		hLemma = sc.next();
		bidix = bidix + hLemma + "<s n=\"";
		
		hKlass = sc.next();
		bidix = bidix + hKlass + "\"/></r></p>";		
		
		paradigm = sc.next();
		if (paradigm.length() == 0)
			{
				bidix = bidix + "</e>\n";
			}
		else
			bidix = bidix + "<par n=\"" + paradigm + "\"/></e>\n";		
		}
			
	return bidix; // XML-taggad
		
	}

	// Backup av ordlistan
	public static void dixBackup (ArrayList alDix, String fileName) throws Exception
	{
		Utskrift.skrivText ("V�nta! Sparar backkup av ordlistan.");
		// formaterar ordlistan
		String back = makeXMLbidix (alDix);
		// skriver till fil
		Textfil.skrivText (fileName, back, "UTF-8");
		Utskrift.skrivText ("Backup sparad: " + fileName);
		System.out.println();
	}
	
	// �ndra rad dvs. ett ord (f�re taggning)
	public static void andraRad (ArrayList<String> alDix, int index, ArrayList<String> WordsToTranslate, String languagePosition, TreeMap<String, String> tmLang, String left, String right, String author, String klass) throws Exception
	{
		// H�mta den gamla raden
		String rad = (String) alDix.get(index);

		// Skriv ut den gamla raden
		Utskrift.rubrik ("Ordet du vill �ndra");
		Utskrift.skrivText(rad);
		// Mata in den nya raden
		// =====================
		while (true)
		{
			// klass = ordklass
			// languagePosition = v�nster eller h�ger spr�k
			rad = defineWordFromMonodix (index, WordsToTranslate, languagePosition, left, right, tmLang, author, klass);
					
			// Skriv ut raden och ge m�jlighet att �ndra
			Utskrift.rubrik ("Nya ordet");
			Utskrift.skrivText (rad);
			String change = Inmatning.rad("�ndra (j/N) ?");

			if (change.equals("") || !(change.charAt(0)=='j'))
				{
					// Skriv den nya raden
					alDix.set (index, rad);
					break;
				}
			else 
				{
					Utskrift.skrivText("Mata in ordet p� nytt!");
					//added dvs. ordets index i WordsToTranslate of�r�ndrat
				}
		}	
	}
	
	// Skapar en lista som �vers�tter spr�kkod till spr�k
	public static TreeMap<String, String> languages ()
	{
		// ger sorterad lista
		// med kod (nyckel) och klartext (�vers�ttning/v�rde)
		TreeMap<String, String> tm = new TreeMap<String, String>();
		
		// l�gger till spr�k
		// blir en mycket l�ng lista!
		// l�sa fr�n fil i en loop? On�digt l�ngsamt?
		tm.put("sv", "svenska");
		tm.put("da", "danska");
		tm.put("nb", "norska bokm�l");
		tm.put("nn", "norska nynorsk");
		
		return tm;
	}
	
	// Paradigm f�r substantiv
	public static String askParadigm (TreeMap<String, String> tmLang, String left, String right) throws Exception
	{
		// par n="_ut_ut"
		String p = "_";
		String ask = tmLang.get(left) + ": �r ordet ett \"den-ord\" (ut: utrium) eller ett \"det-ord\" (nt: neutrum)";
		p = p + Inmatning.rad(ask,"nt|ut").trim();
		ask = tmLang.get(right) + ": �r ordet ett \"den-ord\" (ut: utrium) eller ett \"det-ord\" (nt: neutrum)";
		p = p + "_" + Inmatning.rad(ask,"nt|ut").trim();
		return p;
	}
	
	// L�ser in en monodix-fil
	// medger att man bara l�gger till �vers�ttningen
	public static ArrayList<String> lasMonodix (String pair, String lang, String[] stop) throws Exception
	{
		String fil = pair + "." + lang + "." + "dix.txt";
		FileInputStream fs = new FileInputStream(fil); // byte till character
		Scanner fin = new Scanner (fs, "UTF-8"); // r�tt tecken!

		ArrayList<String> ordlista = new ArrayList<String>();
		String rad= "";

		int i = 0;
		int j = 0;
		int j1 = 0;
		String nyttOrd = "";
			
		while (fin.hasNextLine())	
		{	
			rad = fin.nextLine();
			
			if (rad.contains("<e lm=")) // Definition av ord			
			{
				// Ord som finns i ordlistan
				// Exempel:
				// <e lm="ack" a="PT">              <i>ack</i><par n="ack__ij"/></e>
				// <e lm="aha" c="style:neu" a="PT">              <i>aha</i><par n="ack__ij"/></e>
				// <e lm="t.ex."><i>t.ex.</i><par n="t.ex.__abbr"/></e>
				// <e lm="AB"  c="Aktiebolag/AftonBladet" a="PT"><i>AB</i><par n="t.ex.__abbr"/></e>
				// <e lm="andelsboligforening" r="RL"  c="Danska!" a="PT"><i>andelsboligforeningak</i><par n="t.ex.__abbr"/></e>
				// <e lm="adr." a="PT"><i>adr.</i><par n="t.ex.__abbr"/></e>

				//Utskrift.skrivText("monodix-rad: " + rad);

				// b�rjan av ordet
				i = rad.indexOf('=');
				
				rad = rad.substring(i+2);
				
				// slutet av ordet
				j = -1 + rad.indexOf('>'); // end of e-tag
				j1 = -3 + rad.indexOf('='); // comment or author
				
				if (j1<j) j = j1;  // comment or author
				
				nyttOrd = rad.substring(0, j);
				
					if (Arrays.binarySearch (stop, nyttOrd) < 0) // ordet finns inte
					{
						ordlista.add (nyttOrd);
					}
					else  //ordet finns redan
					{}
				
				//Utskrift.skrivText ("ord: " + rad.substring(0, j));
				//ordlista.add (rad.substring(0, j));			
			}		
		}
		
		fin.close();
	//Utskrift.skrivText (ordlista);
	return ordlista;
	}
	
	// L�ser in ordklass fr�n en monodix-fil
	// s� man slipper f� en fr�ga
	public static String lasKlass (String pair, String lang) throws Exception
	{
		String fil = pair + "." + lang + "." + "dix.txt";
		FileInputStream fs = new FileInputStream(fil); // byte till character
		Scanner fin = new Scanner (fs, "UTF-8"); // r�tt tecken!

		// Skulle kunna l�sa fr�n rubriken inst�llet,
		// men det �r l�tt h�nt att den �ndras!

		String rad= "";
		String klass = "";

		int i = 0;
		int j = 0;
			
		while (fin.hasNextLine())	
		{	
			rad = fin.nextLine();
			
			if (rad.contains("<e lm=")) // Definition av ord			
			{
				// Ord som finns i ordlistan
				// Exempel:
				// <e lm="okt" a="PT"><i>okt</i><par n="t.ex.__abbr"/></e>
		
				// b�rjan av ordklass (slutet av paradigm)
				i = rad.indexOf("__");
				rad = rad.substring(i+2);
								
				// slutet av ordklassen
				j = rad.indexOf("\"/></e>");

				klass = rad.substring(0, j);
				break; // r�cker med att hitta f�rsta ordet!
			}		
		}
		
		fin.close();
	return klass;
	}	
	
	//Ord kopieras  fr�n str�ng till vektor
		
	public static String[] stringToVektor (String text) throws Exception
	{
	// StringTokenizer l�mnar inte extra blanka
	// mellan ord.
		
	StringTokenizer st = new StringTokenizer(text,"|");
  
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
	
	// L�ser in en bidix-fil till en vektor som sorteras,
	// s� att dubletter kan rensas bort. Ut: ArrayList
	// Medger att man bara l�gger till �vers�ttningen
	// till varje unikt nytt ord.
	// OBS! Utg�r fr�n V�NSTER spr�k!
	public static String[] lasBidix (String pair, String klass, String pos) throws Exception
	{
		String fil = "apertium-" + pair + "." + pair + "." + "dix";
		FileInputStream fs = new FileInputStream(fil); // byte till character
		Scanner fin = new Scanner (fs, "UTF-8"); // r�tt tecken!
	
		//ArrayList<String> ordlista = new ArrayList<String>();
		String text = "";		
		String rad= "";

		int i = 0;
		int j = 0;
	//String ordet = " ";
	String nyttOrd = "";	
		while (fin.hasNextLine())	
		{	
			rad = fin.nextLine();
			
			// <e>       <p><l>jod�<s n="ij"/></l>                    <r>jod�<s n="ij"/></r></p></e>
			// <e>       <p><l>j�vlar<s n="ij"/></l>                  <r>for<b/>fanden<s n="ij"/></r></p></e>
			// <e a="PT">       <p><l>lycka<b/>till<s n="ij"/></l>                      <r>tillykke<s n="ij"/></r></p></e>
			
			if (rad.contains("<l>") && rad.contains("n=\"" + klass)) // Definition av ord i r�tt klass
						
			{
				if (pos.equals("L")) // spr�k att �vers�tta fr�n dvs givna ord
				{
					// Ord som finns i ordlistan
					// <e>       <p><l>j�vlar<s n="ij"/></l>
					// <e a="PT">       <p><l>lycka<b/>till<s n="ij"/></l>

					// b�rjan av ordet
					i = rad.indexOf("<l");// V�nster spr�k!
					
					nyttOrd = rad.substring(i+3);
					
					// slutet av ordet
					j = nyttOrd.indexOf("<s");
					
					nyttOrd = nyttOrd.substring(0, j);

					nyttOrd = nyttOrd.replaceAll("<b/>", " ");
					
					Utskrift.skrivText("Bef. ord: " + nyttOrd);
					
					nyttOrd = nyttOrd + "|";
					text = text + nyttOrd;
				}
				if (pos.equals("R")) // spr�k att �vers�tta fr�n dvs givna ord
				{
					// Ord som finns i ordlistan
					// <r>jod�<s n="ij"/></r></p></e>
					// <r>for<b/>fanden<s n="ij"/></r></p></e>
					
					// b�rjan av ordet
					i = rad.indexOf("<r>");
					
					// slutet av ordet
					rad = rad.substring(i+3);
					j = rad.indexOf("<s n=");
					
					nyttOrd = rad.substring(0, j);
					nyttOrd = nyttOrd.replaceAll("<b/>", " ");

					nyttOrd = nyttOrd + "|";
					text = text + nyttOrd;
				}
				
			}
		}
		
		fin.close();
		
	// g�r om str�ngen till vektor
	String[] vOrd = stringToVektor (text);
	text = null; // Anv�nds inte mer.

	Arrays.sort(vOrd); // sorteras p� plats
	
	// tar bort dubletter
	// ------------------
	
	String unika = "";

	String ordet = vOrd[0];

	for (i=1;i<vOrd.length;i++)
	{		
		nyttOrd = vOrd[i];
		if (!ordet.equals(nyttOrd)) // ej dubletter
		{
			//ordlista.add (ordet);
			unika = unika + nyttOrd+ "|";
			ordet = nyttOrd;
		}			
	}
	unika = unika + nyttOrd+ "|";
	//ordlista.add(ordet);
	vOrd = stringToVektor (unika);
	
	return vOrd;
	}
	
	// Skapar en lista som �vers�tter klasskod till ordklass
	public static TreeMap<String, String> partOfSpeach ()
	{
		// ger sorterad lista
		// med kod (nyckel) och klartext (�vers�ttning/v�rde)
		TreeMap<String, String> tm = new TreeMap<String, String>();
		
		tm.put("ij", "interjections"); // Interjektion
		tm.put("abbr", "abbreviations"); // F�rkortning
		tm.put("n", "nouns"); // Substantiv/Navneord
		tm.put("vblex", "verbs"); // Verb
		tm.put("adj", "adjectives"); // Adjektiv/Till�gsord
		tm.put("adv", "adverbs"); // Adverb/Biord
		
		return tm;
	}
	
	// Ber�kna spr�kets position i paret
	// dvs. om det �r till v�nster eller h�ger					
	public static String languagePosition (String lang, String pair) throws Exception
	{
	String pos = "R";					
	if (pair.startsWith (lang) ) pos = "L";	
	return pos;
	}
}