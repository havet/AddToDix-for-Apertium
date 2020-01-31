//Textfil.java

// TEXTFIL:

// Metoder f�r att skriva och l�sa textfiler bekv�mt
// och med r�tt tecken (�, �, �).
// ====================================== 

// Version: 0.98

//      Copyright (c) 2012-2013 Per Tunedal, Stockholm, Sweden
//       Author: Per Tunedal <info@tunedal.nu>

	// Optimized code for line counting heavily inspired by:
	// http://stackoverflow.com/questions/453018/number-of-lines-in-a-file-in-java
	// with the kind consent of Martin Ankerl martin.ankerl@gmail.com
	// "It's not completely my code, it has been modified by one or more of the stackoverflow community. But you are of course free to use it anywhere, as far as I can say. Just don't sue me or anyone because of it ;)"
	// Martin

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
// v. 0.98 Added line counting
// --------------------------------------

package per.edu;

import java.io.*; // f�r att skapa/�ppna en fil (File)
import java.util.*; // f�r att skriva/l�sa en fil (PrintWriter resp. Scanner)

public class Textfil

// �ppna och skriv till en textfil
// Skriver �ver om filen finns

{

	public static void skrivText (String filnamn, String text) throws Exception
	{
	// Binder en textfil till en variabel
	// Filen skapas om den inte finns.
	// Anv�nder paketet java.io
	
	File   fil = new File (filnamn); // skapas i aktuell katalog
	
	// Skapar ett FileWriter-objekt f�r skrivning till filen
	// och binder objektet till filen
	// Filen �ppnas samtidigt f�r skrivning.
	// Anv�nder paketet java.io
	
	// Skriver �ver filen om den finns
	// (fil, true) l�gger till p� slutet
	
	FileWriter fw = new FileWriter (fil); // Java 1.4?
	
	// Skapar ett PrintWriter-objekt f�r 
	// bekv�m skrivning till filen.
	// (Automatisk omvandling till tkn-str�ng.)
	// och binder objektet till filen
	// Anv�nder paketet java.util	
	
	PrintWriter fout = new PrintWriter (fw); // Java 1.4?
		
	// Skriver till filen
	
	fout.print (text);
	
	// St�nger filen
	// framtvingar skrivning av de sista buffrade tecken,
	// g�rs annars med flush().
	
	fout.close();  // St�nger �ven fw!

	}

	// �ppna och skriv till en textfil
	// L�gger till om filen finns

	public static void addText (String filnamn, String text) throws Exception
	{

	// Binder en textfil till en variabel
	// Filen skapas om den inte finns.
	// Anv�nder paketet java.io
	
	File   fil = new File (filnamn); // skapas i aktuell katalog
	
	// Skapar ett FileWriter-objekt f�r skrivning till filen
	// och binder objektet till filen
	// Filen �ppnas samtidigt f�r skrivning.
	// Anv�nder paketet java.io
	
	// L�gger till i slutet p� filen
	// om den finns
	// (argumentet "true")
	
	FileWriter fw = new FileWriter (fil, true);
		
	// Buffrad utskrift
	// skapar ett BufferedWriter-objekt f�r skrivning
	// och binder det till FileWriter-objektet.
	
	BufferedWriter fout = new BufferedWriter (fw);
		
	// L�gger till radbrytning.
	fout.write (text + "\n"); // BufferedWriter
	
	// St�nger filen
	// framtvingar skrivning av de sista buffrade tecken,
	// g�rs annars med flush().
	
	fout.close();  // St�nger �ven fw!
	
	}
	
	
// �ppna och skriv till en textfil med angiven kodning
// Skriver �ver om filen finns

	public static void skrivText (String filnamn, String text, String encoding) throws Exception
	{
	// Binder en textfil till en variabel
	// Filen skapas om den inte finns.
	// Anv�nder paketet java.io
	
	File   fil = new File (filnamn); // skapas i aktuell katalog
	
	// Skapar ett FileWriter-objekt f�r skrivning till filen
	// och binder objektet till filen
	// Filen �ppnas samtidigt f�r skrivning.
	// Anv�nder paketet java.io
	
	// Skriver �ver filen om den finns
	// (fil, true) l�gger till p� slutet
	
	 // Skriver byte-str�m till fil
	FileOutputStream fos = new FileOutputStream (fil);
		
	// Character till byte
	OutputStreamWriter fout = new OutputStreamWriter (fos, encoding); // Java 1.6
	
	// Skriver till filen
	
	//Obs! L�gger till!
	fout.append (text); // Java 1.6
	
	// St�nger filen
	// framtvingar skrivning av de sista buffrade tecken,
	// g�rs annars med flush().
	
	fout.close(); // St�nger �ven fw!

	}
	
// �ppna och skriv till en textfil med angiven kodning
// L�gger till om filen finns och append = true
public static void addText (String filnamn, String text, String encoding) throws Exception
	{
	// Binder en textfil till en variabel
	// Filen skapas om den inte finns.
	// Anv�nder paketet java.io
	
	File   fil = new File (filnamn); // skapas i aktuell katalog
	
	// Skapar ett FileWriter-objekt f�r skrivning till filen
	// och binder objektet till filen
	// Filen �ppnas samtidigt f�r skrivning.
	// Anv�nder paketet java.io
	
	// Skriver �ver filen om den finns
	// (fil, true) l�gger till p� slutet
	
	 // Skriver byte-str�m till fil
	 // boolean true anger append! Annars: skriv �ver.
	 // --------------------------
	FileOutputStream fos = new FileOutputStream (fil, true); // true ger append
		
	// Character till byte
	OutputStreamWriter fout = new OutputStreamWriter (fos, encoding); // Java 1.6
	
	// Skriver till filen
	
	//Obs! L�gger till!
	fout.append (text); // Java 1.6
	
	// St�nger filen
	// framtvingar skrivning av de sista buffrade tecken,
	// g�rs annars med flush().
	
	fout.close(); // St�nger �ven fw!

	}
	
	// L�s fr�n en textfil
	
	public static String laesText (String filnamn) throws Exception
	
	{
	
	File fil = new File (filnamn); // �ppnar en fil
	
	Scanner fin = new Scanner (fil); // Binder till l�sare
	
	String text = "";
	
	String rad = "";
	
	while (fin.hasNextLine())
		{
		rad = fin.nextLine() + "\n";
		text = text + rad;		
		}
	
	fin.close(); // St�nger filen
		
	return text;
	}
	
	// L�s fr�n en texfil med angiven kodning
	public static String laesText (String filnamn, String encoding) throws Exception
	
	{
	
	File fil = new File (filnamn); // �ppnar en fil
	
	// l�ser byte fr�n fil
	FileInputStream fis = new FileInputStream (fil);
	
	// L�sare f�r rader etc
	// avkodar fr�n angiven kodning
	
	Scanner fin = new Scanner (fis, encoding);
	
	String text = "";
	
	String rad = "";
	
	while (fin.hasNextLine())
		{
		rad = fin.nextLine() + "\n";
		text = text + rad;
		}
	
	fin.close(); // St�nger filen
	
	return text;	
	}
	
	// L�s ett visst antal rader fr�n en texfil med angiven kodning
	public static String laesText (String filnamn, String encoding, int lines) throws Exception
	
	{
	
	File fil = new File (filnamn); // �ppnar en fil
	
	// l�ser byte fr�n fil
	FileInputStream fis = new FileInputStream (fil);
	
	// L�sare f�r rader etc
	// avkodar fr�n angiven kodning
	
	Scanner fin = new Scanner (fis, encoding);
	
	String text = "";
	
	String rad = "";
	
	int antal = 0;
	
	while (fin.hasNextLine())
		{
		rad = fin.nextLine() + "\n";
		text = text + rad;
		antal++;
		if (antal == lines) break;
		}
	
	fin.close(); // St�nger filen
	
	return text;
	}
	
	// L�s angivna rader fr�n en texfil med angiven kodning
	public static String laesText (String filnamn, String encoding, int[] lines) throws Exception
	
	{
	// Kr�ver att vektorn med radnummer �r sorterad.
	// BufferedReader �r c:a 25 % snabbare.
	BufferedReader br = new BufferedReader(new InputStreamReader(new
	FileInputStream(filnamn), encoding));

	String text = "";
	String rad = "";
	int antal = 0;
	int hittade = 0;

	while ((hittade < lines.length) && ((rad = br.readLine()) != null))
	{
		//System.out.println(rad);
		antal++;
		if (0 == Math.round(antal%100000)) System.out.println ("Tusental rader hittills: "+ antal/1000);
		if (Arrays.binarySearch(lines, antal) >= 0)
		{
			text = text + rad + "\n";
			hittade++;
		}
	}
	
	br.close();
	return text;
	}
	
	// L�s rader fr�n en texfil, s�tt angiven avgr�nsare
	public static String laesText (String filnamn, String encoding, String delimiter) throws Exception
	
	{
	// BufferedReader l�r vara snabbare. Ja, c:a 25 %!
	BufferedReader br = new BufferedReader(new InputStreamReader(new
	FileInputStream(filnamn), encoding));

	String text = "";
	String rad = "";
	int antal = 0;

	while ((rad = br.readLine()) != null)
	{
		//System.out.println(rad);
		antal++;
		if (0 == Math.round(antal%100000)) System.out.println ("Tusental rader hittills: "+ antal/1000);
		//if (0 == Math.round(antal%1000)) System.out.println ("Tusental rader hittills: "+ antal/1000);
		{
			text = text + rad + delimiter;
		}
	}
	
	br.close();
	return text;
	}
	
	// L�s visst antal rader fr�n en textfil till en str�ngvektor
	public static String[] laesText (int lines, String filnamn, String encoding) throws Exception
	
	{
	// BufferedReader l�r vara snabbare. Ja, c:a 25 %!
	BufferedReader br = new BufferedReader(new InputStreamReader(new
	FileInputStream(filnamn), encoding));

	String[] text = new String[lines];
	String rad = "";
	//int antal = 0;

	//while ((rad = br.readLine()) != null)
	for (int i = 0;i < lines; i++)
	{
		rad = br.readLine();

		if (0 == Math.round(i%100000)) System.out.println ("Tusental rader hittills: "+ i/1000);
		{
			text[i] = rad;
		}
	}
	
	br.close();
	return text;
	}
	
	// L�s en textfil till en 2-dim str�ngvektor med varje ord i en cell
	public static String[][] laesText (int lines, int words, String filnamn, String encoding) throws Exception
	
	{
	// BufferedReader l�r vara snabbare. Ja, c:a 25 %!
	BufferedReader br = new BufferedReader(new InputStreamReader(new
	FileInputStream(filnamn), encoding));

	String[][] text = new String[lines][words];
	String rad = "";
	//int antal = 0;

	//while ((rad = br.readLine()) != null)
	for (int i = 0;i < lines; i++)
	{
		rad = br.readLine();

		if (0 == Math.round(i%100000)) System.out.println ("Tusental rader hittills: "+ i/1000);
		{
			StringTokenizer st = new StringTokenizer(rad);
			
			int j = 0;
			
			while (st.hasMoreTokens())
				{
					text[i][j++] = st.nextToken();
				}
		}
	}
	
	br.close();
	return text;
	}
	
	// L�ser fr�n en textfil och visar att n�got h�nder
	public static String laesText (String filnamn, String encoding, boolean showActivity) throws Exception
	
	{
	
	File fil = new File (filnamn); // �ppnar en fil
	
	// l�ser byte fr�n fil
	FileInputStream fis = new FileInputStream (fil);
	
	// L�sare f�r rader etc
	// avkodar fr�n angiven kodning
	
	Scanner fin = new Scanner (fis, encoding);
	
	String text = "";	
	String rad = "";
	
	if (showActivity)
	{
		int antal = 0;
		
		while (fin.hasNextLine())
			{
			antal++;
			rad = fin.nextLine() + "\n";
			text = text + rad;
			if (0 == Math.round(antal%1000)) System.out.println ("Rader hittills: "+ antal);
			}
	}
	else
	{
		while (fin.hasNextLine())
			{
			rad = fin.nextLine() + "\n";
			text = text + rad;
			}
	}
	
	fin.close(); // St�nger filen
	
	return text;	
	}
	
	// R�knar rader i en textfil
	public static int rader (String filnamn) throws Exception
	{
	
	/**********************************************
	
	// Denna algoritm ger en p� tok f�r l�g siffra!!
	// =============================================
	File fil = new File (filnamn); // �ppnar en fil
	
	Scanner fin = new Scanner (fil); // Binder till l�sare
	
	int antal = 0;
	
	while (fin.hasNextLine())
		{
		antal++;
		fin.nextLine();
		}
	
	fin.close(); // St�nger filen
	
	*************************************************/
	
	/************************************************************
	// Nedanst�ende kod �r l�ngsam, men ger korrekt resultat
	// =====================================================
	BufferedReader reader = new BufferedReader(new FileReader(filnamn));
	int antal = 0;
	while (reader.readLine() != null) antal++;
	reader.close();
	*********************************************************/
	
	/*******************************************
	// Denna kod �r lite snabbare
	FileReader fr = new FileReader(filnamn);
	LineNumberReader lnr = new LineNumberReader(fr);
 
	int antal = 0;
 
	while (lnr.readLine() != null)
	{
		antal++;
	}
  
	lnr.close();
	
	return antal;
	**********************************************/
	
	// Nedanst�ende kod �r blixtsnabb!!
	// Heavily inspired by:
	// http://stackoverflow.com/questions/453018/number-of-lines-in-a-file-in-java
	// with the kind consent of Martin Ankerl martin.ankerl@gmail.com
	// "It's not completely my code, it has been modified by one or more of 
	// the stackoverflow community. But you are of course free to use it anywhere, 
	// as far as I can say. Just don't sue me or anyone because of it ;)"
	// Martin
	InputStream is = new BufferedInputStream(new FileInputStream(filnamn));
	
	//int antal = 0;
    try {
        byte[] c = new byte[1024];
        int antal = 0;
        int readChars = 0;
        boolean empty = true;
        while ((readChars = is.read(c)) != -1) {
            empty = false;
            for (int i = 0; i < readChars; ++i) {
                if (c[i] == '\n')
                    ++antal;
            }
        }
        return (antal == 0 && !empty) ? 1 : antal;
    } finally {
        is.close();
    }
	//return antal;
	}
}