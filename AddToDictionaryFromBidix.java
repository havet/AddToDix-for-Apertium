//AddToDictionaryFromBidix.java

// ADD TO DICTIONARY FROM BIDIX: 

// Skapa enspr�kig ordlista f�r Apertium
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

// v. 0.2 Comments etc mainly in Swedish.
//			Removed direction (not used any more for lexical selection)
//			Added some hints to the user.
//			Stem suggested from the lemma.
// 			File names displayed after saving for easy retrieval.
//			Stop list with existing words of the actual type, e.g. nouns.
// v. 0.3 User has to admit GPL license for the dix.
// v. 0.4 More helpful questions.
// v. 0.5 Better algo for finding lemma.
//			Bugfix: correction of a word

// v. 0.6 Don't ask for paradigm when adding interjections and abbreviations
//			Encourgement: number of added words displayed.
//			Nicer output.
//
// v. 0.7
// v. 0.8 Doublet words in input are discarded.
// v. 0.9 
// v. 0.95
// v. 0.96 Lists the most frequent paradigms
//			Checks input for paradigms
//			Possibility to quit after each 5 added words.
//			Fixed bugs:
//			List of words to translate from the correct language.
// v. 0.97 List of words to translate now only contains new words.
//			The number of remaining words is displayed after each 5 treated.
//			Unused large variables are nulled.
//			Nicer output. Fixed bug: now stops nicely after last entry.
// v. 0.98 Quicker and more intuitive to change stem.
//			Fixed bugs:
//			Stops nicely if all words in bidix already are present in monodix.
//			Algo for finding lemmas now resistant to extra spaces in monodix.
//			Doesn't try to create a list of paradigms for abbr and ij.

// -----------------------------------------------------------------------

// Skapa enspr�kig ordlista f�r Apertium
// Maskin�vers�ttning
// www.apertium.org
// ====================================== 

// Author: Per Tunedal 2012

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

class AddToDictionaryFromBidix

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
	// ======================
	// till�ten input
	allowed = pair;
	allowed = allowed.replaceAll ("-", "|");

	String lang = "";
	
	Utskrift.rubrik("Ordlistan du ska skapa");
	
	while (true)
		{
		lang = Inmatning.rad("Ordlistans spr�k (tex sv):", allowed);		
		if (lang.length() != 0) break;
		}
		
	// Typ av ord
	// ==========
	// Lingvistisk kategori
	// dvs ordklass
	
	// Skapar lista p� ordklasser
	TreeMap<String, String> tmKlass = partOfSpeach ();
	
	String klass = lasKlass (pair);
	Utskrift.skrivText("Orden h�mtade fr�n den tv�spr�kiga ordlistan tillh�r ordklass: " + tmKlass.get(klass) + " ("+ klass + ")\n");	
	
	// Lista p� ord som redan finns
	Utskrift.skrivText("V�nta - g�ra en lista p� ord som redan finns");
	String[] stop = OrdSomFinns (pair, lang, klass);
	
	//Utskrift.skrivVektor (stop);
	
	String mall = "";
	String topParadigms = "";
	
	// Interjektioner och f�rkortningar ob�jliga i sv-da
	if (klass.equals("ij") || klass.equals("abbr"))
	{}
	else
	{
		// G�r en paradigm-lista f�r vald ordklass
		// =======================================
		
		Utskrift.skrivText("V�nta - g�ra en lista p� paradigm (b�jningsm�nster)");
		//ArrayList<String> paradigmer = new ArrayList<String>(); // anv�nds ej??
			
		// Lista ord som karakteriserar resp. paradigm
		// dvs m�nster/mall f�r b�jningen

		mall = paradigmOrd(pair, lang, klass);
		
		// L�ser statisk frekvenslista p� paradigm
		//String topParadigms = Textfil.laesText ("paradigms-da-nouns.txt", "utf-8", 6);
		topParadigms = Textfil.laesText ("paradigms-" + lang + "-" + tmKlass.get(klass) + ".txt", "utf-8", 10);
		topParadigms = topParadigms.replaceAll("\n"," |");
		//Utskrift.skrivText("TopParadigms: " + topParadigms);
	}
	
	// Ber�kna spr�kets position
	// =========================
	// dvs. �r spr�ket till v�nster (L)
	// eller till h�ger (R)
	// Beh�vs f�r att l�sa fr�n bidix
		
	String pos = languagePosition (lang, pair);
	
	// L�ser in lemma fr�n bidix
	// ---------------------------
	Utskrift.skrivText("V�nta - g�ra en lista p� ord att beskriva");
	// Listan rensad fr�n ord som redan finns i monodix.
	ArrayList<String> WordsToDefine = lasBidix (pair, lang, pos, klass, stop);
	
	//Utskrift.rubrik ("Antal ord att beskriva: " + WordsToDefine.size());
	int ant = WordsToDefine.size();
	Utskrift.rubrik ("Antal ord att beskriva: " + ant);
	if (ant == 0) System.exit(0);
		
	
			
	// Samla in ordbeskrivningarna
	// ===========================
		
	// Sparas i en ArrayList
	// s� att det �r l�tt att l�gga till rader.
		
	ArrayList<String> alDix = new ArrayList<String>();
		
		
	// Inmatning av ord
	// ================
	String rad = "";
	int added = 0; // f�r uppmuntran mm
	
	// Jfr AddToBidixFromMonodix.java : elegantare l�sning!
	// "added" anv�nds ist.f. check-vektor!
	// G�r ej h�r pga koll mot befintliga ord :-(
	// Jo, det g�r om listan kollas f�rst:
	// inneh�ller d� bara ord som inte finns!
	
	//int[] check = {0}; // antalet genomg�ngna ord
	// L�gg till en rad (str�ng) i taget
	// avsluta med ENTER
	Utskrift.rubrik ("Nytt ord (klass: " + klass + " spr�k: " + lang + ")");
	while (true && added < WordsToDefine.size())
		{
		// mall: paradigm-stam, klass: ordklass
		// stop: ord som finns: redan kollat!
		//rad = defineWord(WordsToDefine, author, mall, klass, stop, check, topParadigms);
		//rad = defineWord(added, WordsToDefine, author, mall, klass, check, topParadigms);
		rad = defineWord(added, WordsToDefine, author, mall, klass, topParadigms);
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
				Utskrift.rubrik("N�sta ord");
				
				// g�r backup f�r var 10:e rad
				//if (0 == Math.round(alDix.size()%10))
				if (0 == Math.round(added%10))
				{
					String filNamn = pair + "." + lang + "." + "dix.txt.back";
					dixBackup (alDix, filNamn); // Backup till fil
				}
				
				// M�jlighet att avsluta efter var 5:e ord.
				if (0 == Math.round(added%5))
				{	
					Utskrift.skrivText("Du har matat in " + added + " ord. �terst�r: " + (WordsToDefine.size() - added));
					allowed = "j|J|ja|JA|Ja|n|N|nej|NEJ|Nej|^$"; // Till�tna svar (^$ = tomt)
					String sluta = Inmatning.rad("Sluta (j/N)?", allowed);	
					if (!sluta.equals("") && sluta.charAt(0)=='j') break;
				}
			}
		else 
			{
				Utskrift.skrivText("Mata in ordet p� nytt!");
				//check[0] = check[0] - 1; // ordets index
				//added dvs. ordets index i WordsToDefine of�r�ndrat.
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
			r--; // r �ndras till korrekt index f�r raden
			andraRad(added, WordsToDefine, alDix, r, author, mall, klass, topParadigms); // �ndras p� plats
		}
	}
	
	// Formatera listan dvs. l�gg till HTML-taggar
	// ===========================================
	
	String monodix = "";
	String line = "====================================================" + "\n";
	monodix = monodix + "Monolingual dictionnary for " + pair + ", language: " + lang + "\n";
	monodix = monodix + "Part of speach: " + tmKlass.get(klass) + " (" + klass + ")\n" + line + "\n";
	
	monodix = monodix + makeXMLmonodix (alDix);
	
	String lic = "Jag till�ter att ordlistan licensieras enl. GPL version 2 eller senare.";
	monodix = monodix + "\n" + lic + "\n" + namn + "\n" + epost; //avs�ndare	


	if (monodix.length() != 0) Utskrift.skrivText ("\n" + monodix + "\n");
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
		String filnamn = pair + "." + lang + "." + "dix.txt";
		//String lic = "Jag till�ter att ordlistan licensieras enl. GPL version 2 eller senare.";
		//monodix = monodix + "\n" + lic + "\n" + namn + "\n" + epost; //avs�ndare
		Textfil.skrivText (filnamn, monodix, "UTF-8");
		Utskrift.rubrik ("Sparat ordlistan " +  filnamn);
		
		}
	else Utskrift.rubrik ("Sparar ingenting");	
	}
	
	// Konstruerar en lista �ver tillg�ngliga
	// paradigmer (b�jningsm�nster) f�r vald ordklass.		
	// Listar orden som �r m�nster/mall f�r b�jningen

	public static String paradigmOrd (String pair, String lang, String klass) throws Exception
	
	{
	
		String p = "";
	
		// Konstruerar en lista �ver tillg�ngliga
		// paradigmer utifr�n spr�kets monodix!
		// =======================================
		
		String fil = "apertium-" + pair + "." + lang + ".dix";
		
		FileInputStream fs = new FileInputStream(fil); // byte till character
		Scanner fin = new Scanner (fs, "UTF-8"); // r�tt tecken!

		String rad= "";
		int i = 0;
		int j = 0;
	
		while (true)
	
			{	

			rad = fin.nextLine();

			if (rad.contains("<e lm=")) // Word definitions: too far down
				break;
			if (!rad.contains("<pardef") || !rad.contains("__" + klass + "\""))
				continue;
			else
				{
				// Modellordet i paradigmet/paradigm-namnet, inkl. ev till�gg
				i = rad.indexOf('\"'); // f�rsta "
				j = rad.indexOf("__" + klass); //sista delen av paradigmet

				p = p + rad.substring(i+1, j) + " ";
				}		
			}
			
		fin.close();
		
		//}
		
		return p;	
	}
		
		
	// Inmatning av ett ord:
	// lemma, stam, paradigm etc
		
	public static String defineWord (int added, ArrayList<String> WordsToDefine, String author, String mall, String klass, String topParadigms) throws Exception
	{
	// <e lm="dagis" c="domain:family style:fam" a="PT">
	
	String ord = "";
	String paradigm = "";
				
	// uppgifterna skiljs �t med tecknet |
	
	// Obs! Lemma redan inl�st fr�n bidix!
	// Forts�tta till listan p� ord �r slut.

	//Utskrift.rubrik ("Nytt ord");
	Utskrift.skrivText ("(danska/norska tecken: � � � svenska tecken: � � �)");
	// String rad (String ask, String[] stop, String meddelande)

	//String lemma = lasWordsToDefine(WordsToDefine, stop, check); // l�ser fr�n ordlista skapad fr�n bidix
	//String lemma = lasWordsToDefine(WordsToDefine, check); // l�ser fr�n ordlista skapad fr�n bidix
	String lemma = WordsToDefine.get(added); // antalet ord = index f�r n�sta!
	
	if (lemma.length() == 0) return ord = "";

	else
		{
		//Utskrift.skrivText("Lemma: " + lemma);
		Utskrift.rubrik("Lemma (grundform): " + lemma);
		ord = ord + lemma;
		
		// <e lm="dagis" c="domain:family style:fam" a="PT">		
		Utskrift.skrivText ("I vilket sammanhang anv�nds ordet?");
		// Inga blanka! De �r gr�ns mot n�sta "attribute":
		// 'space' as a separator of pairs and ':' to separate key:value pairs
		ord = ord + "|" +  Inmatning.rad("Dom�n:").replaceAll("\\s","");
				
		Utskrift.skrivText ("genre: sol neu fam pej vulg old dial");
		ord = ord + "|" +  Inmatning.rad("Genre:", "sol|neu|fam|pej|vulg|old|dial|^$").trim();
					
		ord = ord + "|" + author;
		
		Utskrift.skrivText ("flerordsuttryck: <b/> ist.f. mellanslag");

		// ij|abbr|n|vblex till�tna
		if (klass.equals("n")||klass.equals("ij") ||klass.equals("abbr"))
		{
			String stam = lemma;
			stam = stam.replaceAll(" ","<b/>");
			Utskrift.skrivText("stam: " + stam);
			//String change = Inmatning.rad("�ndra (j/N) ?");
			//if (change.equals("") || !(change.charAt(0)=='j'))
			String change = Inmatning.rad("�ndra (j/N) ?");
			if (change.equals("") || change.equals("n") || change.equals("N"))
			{
			ord = ord + "|" + stam;
			}
			else
			{
				if (!(change.charAt(0)=='j'))
				{
				ord = ord + "|" + change; // antar att man skrivit nya stammen
				}
				else
				{
				ord = ord + "|" +  Inmatning.rad("stam:").trim();
				}
			}
		}
		
		else
		{
			ord = ord + "|" +  Inmatning.rad("stam:").trim();
		}
					
		Utskrift.rubrik ("Paradigmer");

		// interjektioner ob�jliga i sv-da-no
		if(klass.equals("ij"))
		{
			Utskrift.skrivText("(f�r interjektioner v�ljs paradigm automatiskt)");
			paradigm = mall.trim();
		}

		// f�rkortningar normalt ob�jliga i sv-da-no
		// annars b�r de behandlas som tex egennamn (np) eller substantiv (n)
		else if(klass.equals("abbr"))
		{
			Utskrift.skrivText("(f�r f�rkortningar v�ljs paradigm automatiskt.");
			Utskrift.skrivText("De �r normalt ob�jliga, annars b�r de behandlas som tex egennamn (np).)");
			paradigm = mall.trim();
		}
		else
		{
			// Skriver ut de vanligaste paradigmen
			Utskrift.skrivText("De vanligaste: " + topParadigms);
			// Skriver ut ord som karakteriserar resp. paradigm
			Utskrift.skrivText(mall);
			
			while (true)
			{
				paradigm = Inmatning.rad("paradigm:", mall.replaceAll(" ","|")).trim();	
				if (paradigm.length() != 0) break;
			}
		}
		
		ord = ord + "|" +  paradigm;
		//ord = ord + "|" +  Inmatning.rad("paradigm:").trim();
		ord = ord + "__" + klass;
				
		return ord;
		}
	}
		
	// XML-taggar l�ggs till en monodix
		
	public static String makeXMLmonodix (ArrayList alDix) throws Exception
	{
	// <e lm="dagis" c="domain:family style:fam" a="PT">
	// c="domain:sj� och hav style:neu" FEL: inga mellanslag!!
	String monodix = "";
	String lemma = "";

	String domain = "";
	String genre = "";
	
	String author = "";
	String stam = "";
	String paradigm = "";
		
	String rad = "";
		
	for (int r = 0; r < alDix.size(); r++)
		{		
		rad = (String) alDix.get(r);
				
		Scanner	sc = new Scanner (rad);
		// Obs! Delimiter m�ste anges som regex! [] �r teckenklass
		sc.useDelimiter ("[|]"); // tv� i f�ljd ger TOM str�ng
								
		lemma = sc.next();
		monodix = monodix + "<e lm=\"" + lemma;
				
		// Comment: dom�n och genre
		// c="domain:family style:fam"
		domain = sc.next(); // dom�n
		
		if (domain.length() == 0)
			{}
		else
			monodix = monodix + "\" c=\"domain:" + domain;
		
		genre = sc.next(); // genre dvs. style
		
		if (genre.length() == 0)
			{}
		else if (domain.length() == 0)
			{
			monodix = monodix + "\" c=\"style:" + genre;
			}
		else // dvs. !(domain.length() == 0)
			{
			monodix = monodix + " style:" + genre;
			}		
				
		author = sc.next();
		if (author.length() == 0)
			{}
		else
			monodix = monodix + "\" a=\"" + author + "\"";	
				
		stam = sc.next();
				
		paradigm = sc.next();
				
		monodix = monodix + ">              <i>" + stam + "</i><par n=\"" + paradigm + "\"/></e>\n";

		}
			
	return monodix; // XML-taggad
		
	}

	// Backup av ordlistan
	public static void dixBackup (ArrayList alDix, String fileName) throws Exception
	{
		Utskrift.skrivText ("V�nta! Sparar backkup av ordlistan.");
		// formaterar ordlistan
		String back = makeXMLmonodix (alDix);
		// skriver till fil
		Textfil.skrivText (fileName, back, "UTF-8");
		Utskrift.skrivText ("Backup sparad: " + fileName);
		System.out.println();
	}
	
	// �ndra rad dvs. ett ord (f�re taggning)
	public static void andraRad (int added, ArrayList<String> WordsToDefine, ArrayList<String> alDix, int index, String author, String mall, String klass, String topParadigms) throws Exception
	{
		// Skriv ut den gamla raden
		String rad = (String) alDix.get(index);
		/****************************************************
		Scanner	sc = new Scanner (rad);
		// Obs! Delimiter m�ste anges som regex! [] �r teckenklass
		sc.useDelimiter ("[|]"); // tv� i f�ljd ger TOM str�ng
		String lemma = "";
		lemma = sc.next();
		WordsToDefine.add(lemma); // l�gg till lemmat sist
		check[0] = WordsToDefine.size() - 1; // peka p� den sista (nya!) raden!
		******************************************************/
		Utskrift.rubrik ("Ordet du vill �ndra");
		Utskrift.skrivText(rad);
		// Mata in den nya raden
		// =====================
		while (true)
		{
			// mall = paradigm-stam, klass = ordklass
			//rad = defineWord(WordsToDefine, author, mall, klass, stop, check, topParadigms);
			//rad = defineWord(WordsToDefine, author, mall, klass, check, topParadigms);
			//rad = defineWord(index, WordsToDefine, author, mall, klass, check, topParadigms);
			rad = defineWord(index, WordsToDefine, author, mall, klass, topParadigms);
					
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
				//check[0] = check[0] - 1; // ordets index
			}
		}	
	}
	
	// Str�ng-vektor med ord som redan finns i monodix
	// (f�r att kunna utesluta dem)
		
	public static String[] OrdSomFinns (String pair, String lang, String klass) throws Exception
	{			
		String p = "";
		
		String fil = "apertium-" + pair + "." + lang + ".dix";
		
		FileInputStream fs = new FileInputStream(fil); // byte till character
		Scanner fin = new Scanner (fs, "UTF-8"); // r�tt tecken!

		String rad= "";
		int i = 0;
		int j = 0;
		int j1 = 0;
	
		while (fin.hasNextLine())	
		{	
			rad = fin.nextLine();
			
			// Definition av ord och r�tt ordklass
			if (rad.contains("<e lm=") && rad.contains("__" + klass))
			
			{
				// Ord som finns i ordlistan
				// Exempel:
				// <e lm="ack" a="PT">              <i>ack</i><par n="ack__ij"/></e>
				// <e lm="aha" c="style:neu" a="PT">              <i>aha</i><par n="ack__ij"/></e>
				// <e lm="t.ex."><i>t.ex.</i><par n="t.ex.__abbr"/></e>
/********************************************
				// b�rjan av ordet
				i = rad.indexOf('=');
				
				rad = rad.substring(i+2);
				
				// slutet av ordet
				j = -1 + rad.indexOf('>'); // end of e-tag
				j1 = -3 + rad.indexOf('='); // comment or author
				
				if (j1<j) j = j1;  // comment or author
				
				Utskrift.skrivText ("ord som finns: " + rad.substring(0, j));
				p = p + rad.substring(0, j) + "|";
*********************************************************/
				// b�rjan av ordet
				i = rad.indexOf('=');
				
				rad = rad.substring(i+2);
				
				// slutet av ordet
				//j = -1 + rad.indexOf('>'); // end of e-tag
				//j1 = -3 + rad.indexOf('='); // comment or author
				j = -1 + rad.indexOf('>'); // end of e-tag
				j1 = -3 + rad.indexOf('='); // comment or author
				
				if (j1<j) j = j1;  // comment or author
				
				rad = rad.substring(0, j);
				//rad = rad.replaceAll("", "") etc! �ven "
				rad = rad.replaceAll("\"", "");
				rad = rad.trim();
				
				//Utskrift.skrivText ("ord som finns: " + rad);
				p = p + rad + "|";
			}		
		}
		
		fin.close();
		
	// g�r om str�ngen till vektor
	String[] stop = stringToVektor (p);
	Arrays.sort(stop); // sorteras p� plats
	return stop;
	}
	
	//Ord kopieras  fr�n str�ng till vektor
	public static String[] stringToVektor (String text) throws Exception
	{
	// pga flerordsuttryck med blanka mellan orden
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
	public static ArrayList<String> lasBidix (String pair, String lang, String languagePosition, String klass, String[] stop) throws Exception
	{
		String fil = pair + "." + pair + "." + "dix.txt";
		FileInputStream fs = new FileInputStream(fil); // byte till character
		Scanner fin = new Scanner (fs, "UTF-8"); // r�tt tecken!
	
		ArrayList<String> ordlista = new ArrayList<String>();
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
			if (rad.contains("<p><l>")) // Definition av ord
						
			{
				if (languagePosition.equals("L")) // spr�k att �vers�tta fr�n dvs givna ord
				{
					// Ord som finns i ordlistan
					// <e>       <p><l>j�vlar<s n="ij"/></l>
					// <e a="PT">       <p><l>lycka<b/>till<s n="ij"/></l>

					// b�rjan av ordet
					i = rad.indexOf("<p><l>");
					
					// slutet av ordet
					j = rad.indexOf("<s");
					
					nyttOrd = rad.substring(i+6, j);

					nyttOrd = nyttOrd.replaceAll("<b/>", " ");
					
					if (Arrays.binarySearch (stop, nyttOrd) < 0) // ordet finns inte
					{
						nyttOrd = nyttOrd + "|";
						text = text + nyttOrd;
					}
					else  //ordet finns redan
					{}
				}
				
				if (languagePosition.equals("R")) // spr�k att �vers�tta fr�n dvs givna ord
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

					if (Arrays.binarySearch (stop, nyttOrd) < 0) // ordet finns inte
					{
						nyttOrd = nyttOrd + "|";
						text = text + nyttOrd;
					}
					else  //ordet finns redan
					{}
				}
			}		
		}
		
		fin.close();
		
	if (!(text.length() == 0))
	{		
			
		// StringTokenizer l�mnar inte extra blanka
		// mellan ord.
			
		StringTokenizer st = new StringTokenizer(text,"|");
	  
		// Orden sparas i en str�ng-vektor
		// med r�tt l�ngd
		String [] vOrd = new String[st.countTokens()];
		text = null; // Anv�nds inte mer.
		
		i = 0;
		
		while (st.hasMoreTokens())
			{
				vOrd[i++] = st.nextToken();
			}
		
		Arrays.sort(vOrd); // sorteras p� plats

		String ordet = vOrd[0];
						
		for (i=1;i<vOrd.length;i++)
			{		
				nyttOrd = vOrd[i];
				if (!ordet.equals(nyttOrd)) // ej dubletter
				{
					ordlista.add (ordet);
					ordet = nyttOrd;
				}			
			}
		ordlista.add(ordet);
	}
	return ordlista;
	}
	
	// Ber�kna spr�kets position i paret
	// dvs. om det �r till v�nster eller h�ger					
	public static String languagePosition (String lang, String pair) throws Exception
	{
	String pos = "R";					
	if (pair.startsWith (lang) ) pos = "L";	
	return pos;
	}
	
	/*********************************************************
	// L�ser fr�n Ordlista skapad fr�n bidix
	public static String lasWordsToDefine(ArrayList<String> WordsToDefine, int[] check) throws Exception
	{
	String lemma = "";
	String ord = "";
	// L�s orden fr�n listan
	for (int i=check[0];i<WordsToDefine.size();i++)
	{
		ord = WordsToDefine.get(i);

		// Om ej tr�ff dvs ej finns i stop-listan, returnera det l�sta ordet.
		if (Arrays.binarySearch (stop, ord) < 0)
			{
			lemma = ord;
			check[0] = i+1; // eller ++i (ej i++ !!)
			break;
			}
		lemma = ord;
		check[0] = i+1; // eller ++i (ej i++ !!)
	}
	
	return lemma;
	}
	****************************************************************/
	
	// L�ser in ordklass fr�n en monodix-fil
	// s� man slipper f� en fr�ga
	public static String lasKlass (String pair) throws Exception
	{
		String fil = pair + "." + pair + "." + "dix.txt";
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

			if (rad.contains("<e")) // �vers�ttning av ord	
			{
				// Ord som finns i ordlistan
				// Exempel fr�n bidix:
				// <e a="PT">       <p><l>nov.<s n="abbr"/></l>                <r>nov.<s n="abbr"/></r></p></e>
				//Utskrift.skrivText("&" + rad + "&");
				
				// b�rjan av ordklass
				i = rad.indexOf("n=\"");
				rad = rad.substring(i+3);
				
				//Utskrift.skrivText("&" + rad + "&");
				
				// slutet av ordklassen
				j = rad.indexOf("\"/></l>");

				klass = rad.substring(0, j);
				break; // r�cker med att hitta f�rsta ordet!
			}		
		}
		
		fin.close();
	return klass;
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
}