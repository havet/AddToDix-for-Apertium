//CollectText.java

// COLLECT TEXT: 

// L�ser textfiler fr�n en katalog
// och sparar texterna i en textfil
// 
// f�r Apertium
// Maskin�vers�ttning
// www.apertium.org
// ====================================== 

// Version: 0.98

//      Copyright (c) 2013 Per Tunedal, Stockholm, Sweden
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
// v. 0.96 Added cleaning of html and xml (Wordpress) files.
// v. 0.97 Added cleaning of php, and some cleaning of doc and rtf
//			Unused large variables are nulled.
// v. 0.98
// 

// -----------------------------------------------------------------------


// TODO:
// V�lja Windows- eller utf-8 fil!
// Rensa �ven doc- och rtf-filer.
// ev. senare g� �ver till att ist�llet v�lja ut texten.

import java.util.*; // ArrayList, Scanner, Comparator
import java.io.*; // f�r att skapa/�ppna en fil (File)
import per.edu.*;

class CollectText

{	
	public static void main (String[] args) throws Exception
	{
	// Inmatning katalognamn
	// C:\Users\Per\tmp\corpustest
	
	String katalog = "";
		
	while (true)
		{
		katalog = Inmatning.rad("Katalog: ");
		if (katalog.length() != 0) break;
		}
	
	Utskrift.rubrik("Angiven katalog: " + katalog);
	Utskrift.skrivText("Obs! Om n�got dokument inneh�ller bilder h�nger sig programmet!");
	
	// Skapar filobjekt av katalogen
	File kat = new File (katalog);

	// Skapar filobjekt fr�n katalogen
	
	String[] katalogerFiler = kat.list();
	
	File[] fv = new File[katalogerFiler.length];
	
	int html = 0; // r�knar html-filer
	int xml = 0; // r�knar xml-filer
	int php = 0; // r�knar php-filer
	int doc = 0; // r�knar doc-filer
	int rtf = 0; // r�knar rtf-filer
	
	for (int i = 0;i < fv.length; i++)
	{
		fv[i] = new File (kat, katalogerFiler[i]);
		if (FileExtension(fv[i]).matches("html")) html++;
		else if (FileExtension(fv[i]).matches("htm")) html++;
		else if (FileExtension(fv[i]).matches("xml")) xml++;
		else if (FileExtension(fv[i]).matches("php")) php++;
		else if (FileExtension(fv[i]).matches("doc")) doc++;
		else if (FileExtension(fv[i]).matches("rtf")) rtf++;
	}
	
	// Bygger doc-stop-lista om det finns rtf-filer
	String[] docStop = {""};
	if (doc > 0)
	{
	String stopDoc = makeStopList ("stop.doc.txt");
	docStop = stringToVektor (stopDoc);
	stopDoc = null; // St�dar
	Arrays.sort(docStop); // sorteras p� plats
	}
	
	// Bygger rtf-stop-lista om det finns rtf-filer
	String[] rtfStop = {""};
	if (rtf > 0)
	{
	String stopRtf = makeStopList ("stop.rtf.txt");
	rtfStop = stringToVektor (stopRtf);
	stopRtf = null; // St�dar
	Arrays.sort(rtfStop); // sorteras p� plats
	}
	
	//Utskrift.skrivText("html: " + (html > 0)  );

	// Bygger htlm-stop-lista om det finns html-filer
	// eller php-filer
	String[] htmlStop = {""};
	if (html > 0 || php > 0)
	{
	String stopHTML = makeStopList ("stop.html.txt");
	htmlStop = stringToVektor (stopHTML);
	stopHTML = null; // St�dar
	Arrays.sort(htmlStop); // sorteras p� plats
	}

	// Bygger xml-stop-lista om det finns xml-filer
	// (f�r Wordpress-filer)
	String[] xmlStop = {""};
	if (xml > 0 )
	{
	String stopXML = makeStopList ("stop.xml.txt");
	xmlStop = stringToVektor (stopXML);
	stopXML = null; // St�dar
	Arrays.sort(xmlStop); // sorteras p� plats
	}
	
	// Bygger php-stop-lista om det finns php-filer
	String[] phpStop = {""};
	if (php > 0)
	{
	String stopPHP = makeStopList ("stop.php.txt");
	phpStop = stringToVektor (stopPHP);
	stopPHP = null; // St�dar
	Arrays.sort(phpStop); // sorteras p� plats
	}
	
	// �ppna en ny fil f�r skrivning
	// namn = katalognamn + .txt
	
	// M�ste rensa bort s�kv�gen!
	katalog = removePath(katalog);
	String utfil = katalog + ".txt";
	
	Utskrift.skrivText("Utfil: " + utfil);

	
	// L�s en fil i taget och
	// l�gg till texten i den nya filen.
	// St�ng den nya filen.
	
	String text = "";
	// Till�tna fil�ndelser
	// bara textfiler (�ven Word etc!)
	// f�r html och xml m�ste taggar rensas bort!
	// sxw, odt och docx �r komprimerade!
	String allowed = "txt|rtf|doc|htm|html|php|xml";
	//String allowed = "txt|rtf|doc";
	
	// Rensa utfilen, ifall man pr�vat flera g�nger
	Textfil.skrivText(utfil,"");
	
	for (int i = 0;i < fv.length; i++)
	{
		if(fv[i].isFile() && FileExtension(fv[i]).matches(allowed))
		{
		Utskrift.skrivText(fv[i].getName());
		text = laesText(fv[i]);
		text = text.replaceAll("-\n", "").replaceAll("-\r\n", ""); // s�tter ihop avdelade ord
	//Utskrift.skrivText("f�re: " + text);
		if (FileExtension(fv[i]).matches("htm") || FileExtension(fv[i]).matches("html"))
			{
				Utskrift.skrivText("Rensar html-fil");
				text = text.replaceAll("&ndash;", " - ").replaceAll("&mdash;", " - ").replaceAll("&nbsp;", " ").replaceAll("&copy;", "").replaceAll("&reg;", "");
				// l�sg�r ord fr�n taggar
				//text = text.replaceAll(">", "> ").replaceAll("<", " <").replaceAll("&lt;", " ").replaceAll("&gt;", " ");
				text = text.replaceAll(">", "> ").replaceAll("<", " <").replaceAll("&lt;", " < ").replaceAll("&gt;", " > ");
				text = text.replaceAll("&lsquo;", "'").replaceAll("&rsquo;", "'").replaceAll("&quot;", "\"").replaceAll("&raquo;", "").replaceAll("&laquo;", "");
				text = text.replaceAll("&auml;", "�").replaceAll("&aring;", "�").replaceAll("&ouml;", "�");
				text = text.replaceAll("&aelig;", "�").replaceAll("&oslash;", "�");
				text = text.replaceAll("&agrave;", "�").replaceAll("&aacute;", "�").replaceAll("&acirc;", "�");
				text = text.replaceAll("&egrave;", "�").replaceAll("&eacute;", "�").replaceAll("&ecirc;", "�").replaceAll("&euml;", "�");
				text = text.replaceAll("&igrave;", "�").replaceAll("&iacute;", "�").replaceAll("&icirc;", "�").replaceAll("&iuml;", "�");
				text = text.replaceAll("&ntilde;", "�");
				text = text.replaceAll("&ograve;", "�").replaceAll("&oacute;", "�").replaceAll("&ocirc;", "�");
				text = text.replaceAll("&ugrave;", "�").replaceAll("&uacute;", "�").replaceAll("&ucirc;", "�").replaceAll("&uuml;", "�");
				text = text.replaceAll("&Auml;", "�").replaceAll("&Aring;", "�").replaceAll("&Ouml;", "�");
				text = text.replaceAll("&AElig;", "�").replaceAll("&Oslash;", "�");
				text = text.replaceAll("&Agrave;", "�").replaceAll("&Aacute;", "�").replaceAll("&Acirc;", "�");
				text = text.replaceAll("&Egrave;", "�").replaceAll("&Eacute;", "�").replaceAll("&Ecirc;", "�").replaceAll("&Euml;", "�");
				text = text.replaceAll("&Igrave;", "�").replaceAll("&Iacute;", "�").replaceAll("&Icirc;", "�").replaceAll("&Iuml;", "�");
				text = text.replaceAll("&Ograve;", "�").replaceAll("&Oacute;", "�").replaceAll("&Ocirc;", "�");
				text = text.replaceAll("&Ugrave;", "�").replaceAll("&Uacute;", "�").replaceAll("&Ucirc;", "�").replaceAll("&uuml;", "�");
				text = text.replaceAll("&ccedil;", "�").replaceAll("&Ccedil;", "�");
				
				text = text.replaceAll("&#8211;", "-").replaceAll("&#8212;", "-").replaceAll("&#160;", " ").replaceAll("&#169", "").replaceAll("&#174", "");
				text = text.replaceAll("&#039;", "'").replaceAll("&#39;", "'").replaceAll("&#60;", " ").replaceAll("&#62", " ");
				text = text.replaceAll("&#8216;", "'").replaceAll("&#8217;", "'").replaceAll("&#34", "").replaceAll("&#187", "").replaceAll("&#171", "");
				text = text.replaceAll("&#228;", "�").replaceAll("&#229;", "�").replaceAll("&#246;", "�");
				text = text.replaceAll("&#230;", "�").replaceAll("&#248;", "�");
				text = text.replaceAll("&#224;", "�").replaceAll("&#225;", "�").replaceAll("&#226;", "�");
				text = text.replaceAll("&#232;", "�").replaceAll("&#233;", "�").replaceAll("&#234;", "�").replaceAll("&#235;", "�");
				text = text.replaceAll("&#236;", "�").replaceAll("&#237;", "�").replaceAll("&#238;", "�").replaceAll("&#239;", "�");
				text = text.replaceAll("&Ntilde;", "�");
				text = text.replaceAll("&#242;", "�").replaceAll("&#243;", "�").replaceAll("&#244;", "�");
				text = text.replaceAll("&#249;", "�").replaceAll("&#250;", "�").replaceAll("&#251;", "�").replaceAll("&uuml;", "�");
				text = text.replaceAll("&#196;", "�").replaceAll("&#197;", "�").replaceAll("&#214;", "�");
				text = text.replaceAll("&#198;", "�").replaceAll("&#216;", "�");
				text = text.replaceAll("&#192;", "�").replaceAll("&#193;", "�").replaceAll("&#194;", "�");
				text = text.replaceAll("&#200", "�").replaceAll("&#201;", "�").replaceAll("&#202;", "�").replaceAll("&#203;", "�");
				text = text.replaceAll("&#204;", "�").replaceAll("&#205;", "�").replaceAll("&#206;", "�").replaceAll("&#207;", "�");
				text = text.replaceAll("&#210;", "�").replaceAll("&#211;", "�").replaceAll("&#212;", "�");
				text = text.replaceAll("&#217;", "�").replaceAll("&#250;", "�").replaceAll("&#251;", "�").replaceAll("&#252;", "�");
							
				text = text.replaceAll("alt=\"", " "); // tar vara p� alt-texter, men ej content.
				//text = text.replaceAll("-\n", "").replaceAll("-\r\n", "").replaceAll("&amp;", "").replaceAll("\";", "\"");
				//text = text.replaceAll("-\n", "").replaceAll("-\r\n", ""); // sl� ihop avdelade ord
				text = text.replaceAll("&amp;", "&").replaceAll("&#38", "&").replaceAll("&#038", "&");
				
				text = rensaStringHTML (text, htmlStop); // slutrensning mha stopp-lista
				text = text.replaceAll("- ", ""); // s�tter ihop avdelade ord
				
				//Utskrift.skrivText("rensad HTML: " + text);
			}
			
					if (FileExtension(fv[i]).matches("php"))
			{
				Utskrift.skrivText("Rensar php-fil");
				text = text.replaceAll("&ndash;", " - ").replaceAll("&mdash;", " - ").replaceAll("&nbsp;", " ").replaceAll("&copy;", "").replaceAll("&reg;", "");
				// l�sg�r ord fr�n taggar
				//text = text.replaceAll(">", "> ").replaceAll("<", " <").replaceAll("&lt;", " ").replaceAll("&gt;", " ");
				text = text.replaceAll(">", "> ").replaceAll("<", " <").replaceAll("&lt;", " < ").replaceAll("&gt;", " > ");
				text = text.replaceAll("&lsquo;", "'").replaceAll("&rsquo;", "'").replaceAll("&quot;", "\"").replaceAll("&raquo;", "").replaceAll("&laquo;", "");
				text = text.replaceAll("&auml;", "�").replaceAll("&aring;", "�").replaceAll("&ouml;", "�");
				text = text.replaceAll("&aelig;", "�").replaceAll("&oslash;", "�");
				text = text.replaceAll("&agrave;", "�").replaceAll("&aacute;", "�").replaceAll("&acirc;", "�");
				text = text.replaceAll("&egrave;", "�").replaceAll("&eacute;", "�").replaceAll("&ecirc;", "�").replaceAll("&euml;", "�");
				text = text.replaceAll("&igrave;", "�").replaceAll("&iacute;", "�").replaceAll("&icirc;", "�").replaceAll("&iuml;", "�");
				text = text.replaceAll("&ntilde;", "�");
				text = text.replaceAll("&ograve;", "�").replaceAll("&oacute;", "�").replaceAll("&ocirc;", "�");
				text = text.replaceAll("&ugrave;", "�").replaceAll("&uacute;", "�").replaceAll("&ucirc;", "�").replaceAll("&uuml;", "�");
				text = text.replaceAll("&Auml;", "�").replaceAll("&Aring;", "�").replaceAll("&Ouml;", "�");
				text = text.replaceAll("&AElig;", "�").replaceAll("&Oslash;", "�");
				text = text.replaceAll("&Agrave;", "�").replaceAll("&Aacute;", "�").replaceAll("&Acirc;", "�");
				text = text.replaceAll("&Egrave;", "�").replaceAll("&Eacute;", "�").replaceAll("&Ecirc;", "�").replaceAll("&Euml;", "�");
				text = text.replaceAll("&Igrave;", "�").replaceAll("&Iacute;", "�").replaceAll("&Icirc;", "�").replaceAll("&Iuml;", "�");
				text = text.replaceAll("&Ograve;", "�").replaceAll("&Oacute;", "�").replaceAll("&Ocirc;", "�");
				text = text.replaceAll("&Ugrave;", "�").replaceAll("&Uacute;", "�").replaceAll("&Ucirc;", "�").replaceAll("&uuml;", "�");
				text = text.replaceAll("&ccedil;", "�").replaceAll("&Ccedil;", "�");
				
				text = text.replaceAll("&#8211;", "-").replaceAll("&#8212;", "-").replaceAll("&#160;", " ").replaceAll("&#169", "").replaceAll("&#174", "");
				text = text.replaceAll("&#039;", "'").replaceAll("&#39;", "'").replaceAll("&#60;", " ").replaceAll("&#62", " ");
				text = text.replaceAll("&#8216;", "'").replaceAll("&#8217;", "'").replaceAll("&#34", "").replaceAll("&#187", "").replaceAll("&#171", "");
				text = text.replaceAll("&#228;", "�").replaceAll("&#229;", "�").replaceAll("&#246;", "�");
				text = text.replaceAll("&#230;", "�").replaceAll("&#248;", "�");
				text = text.replaceAll("&#224;", "�").replaceAll("&#225;", "�").replaceAll("&#226;", "�");
				text = text.replaceAll("&#232;", "�").replaceAll("&#233;", "�").replaceAll("&#234;", "�").replaceAll("&#235;", "�");
				text = text.replaceAll("&#236;", "�").replaceAll("&#237;", "�").replaceAll("&#238;", "�").replaceAll("&#239;", "�");
				text = text.replaceAll("&Ntilde;", "�");
				text = text.replaceAll("&#242;", "�").replaceAll("&#243;", "�").replaceAll("&#244;", "�");
				text = text.replaceAll("&#249;", "�").replaceAll("&#250;", "�").replaceAll("&#251;", "�").replaceAll("&uuml;", "�");
				text = text.replaceAll("&#196;", "�").replaceAll("&#197;", "�").replaceAll("&#214;", "�");
				text = text.replaceAll("&#198;", "�").replaceAll("&#216;", "�");
				text = text.replaceAll("&#192;", "�").replaceAll("&#193;", "�").replaceAll("&#194;", "�");
				text = text.replaceAll("&#200", "�").replaceAll("&#201;", "�").replaceAll("&#202;", "�").replaceAll("&#203;", "�");
				text = text.replaceAll("&#204;", "�").replaceAll("&#205;", "�").replaceAll("&#206;", "�").replaceAll("&#207;", "�");
				text = text.replaceAll("&#210;", "�").replaceAll("&#211;", "�").replaceAll("&#212;", "�");
				text = text.replaceAll("&#217;", "�").replaceAll("&#250;", "�").replaceAll("&#251;", "�").replaceAll("&#252;", "�");
					
				text = text.replaceAll("'", " ' "); // frig�r taggar
				text = text.replaceAll(",", " , "); // frig�r taggar
				//text = text.replaceAll(".", ".  "); // frig�r meningar FEL ger bara punkter!
				
				text = text.replaceAll("alt=\"", " "); // tar vara p� alt-texter, men ej content.
				//text = text.replaceAll("-\n", "").replaceAll("-\r\n", "").replaceAll("&amp;", "").replaceAll("\";", "\"");
				//text = text.replaceAll("-\n", "").replaceAll("-\r\n", ""); // sl� ihop avdelade ord
				text = text.replaceAll("&amp;", "&").replaceAll("&#38", "&").replaceAll("&#038", "&");
				
				text = rensaStringPHP (text, phpStop); // PHP-specific rensning
				
				text = rensaStringHTML (text, htmlStop); // slutrensning mha stopp-lista
				text = text.replaceAll("- ", ""); // s�tter ihop avdelade ord
				
				text = text.replaceAll(" ,", ","); // placerar komman r�tt
				
				//Utskrift.skrivText("rensad PHP: " + text);
			}
			
		if (FileExtension(fv[i]).matches("xml"))
			{
				Utskrift.skrivText("Rensar xml-fil");
				text = text.replaceAll("&ndash;", " - ").replaceAll("&mdash;", " - ").replaceAll("&nbsp;", " ").replaceAll("&copy;", "").replaceAll("&reg;", "");
				// l�sg�r ord fr�n taggar
				//text = text.replaceAll(">", "> ").replaceAll("<", " <").replaceAll("&lt;", " ").replaceAll("&gt;", " ");
				text = text.replaceAll(">", "> ").replaceAll("<", " <").replaceAll("&lt;", " < ").replaceAll("&gt;", " > ");

				text = text.replaceAll("alt=\"", " "); // tar vara p� alt-texter, men ej content.
				//text = text.replaceAll("-\n", "").replaceAll("-\r\n", "").replaceAll("&amp;", "").replaceAll("\";", "\"");
				//text = text.replaceAll("-\n", "").replaceAll("-\r\n", ""); // sl� ihop avdelade ord
				text = text.replaceAll("&amp;", "&").replaceAll("&#38", "&").replaceAll("&#038", "&");
				
				text = rensaStringXML (text, xmlStop); // slutrensning mha stopp-lista
				text = text.replaceAll("- ", ""); // s�tter ihop avdelade ord
				
				//Utskrift.skrivText("rensad XML: " + text);
			}
			
			if (FileExtension(fv[i]).matches("doc"))
			{
				// OBS! Denna rensning funkar INTE av n�gon anledning!
				// Stopplistan funkar inte.
				// ===================================================
				Utskrift.skrivText("Rensar doc-fil");
				//text = text.replaceAll("&ndash;", " - ").replaceAll("&mdash;", " - ").replaceAll("&nbsp;", " ").replaceAll("&copy;", "").replaceAll("&reg;", "");
				// l�sg�r ord fr�n taggar
				//text = text.replaceAll(">", "> ").replaceAll("<", " <").replaceAll("&lt;", " ").replaceAll("&gt;", " ");
				//text = text.replaceAll(">", "> ").replaceAll("<", " <").replaceAll("&lt;", " < ").replaceAll("&gt;", " > ");

				//text = text.replaceAll("alt=\"", " "); // tar vara p� alt-texter, men ej content.
				//text = text.replaceAll("-\n", "").replaceAll("-\r\n", "").replaceAll("&amp;", "").replaceAll("\";", "\"");
				//text = text.replaceAll("-\n", "").replaceAll("-\r\n", ""); // sl� ihop avdelade ord
				//text = text.replaceAll("&amp;", "&").replaceAll("&#38", "&").replaceAll("&#038", "&");
				
				//text = rensaStringXML (text, docStop); // slutrensning mha stopp-lista
				text = text.replace("\\'f6","�").replace("\\'e4","�").replace("\\'e5","�"); // �, �, �
				text = text.replace("\\'c4","�").replace("\\'c5","�"); // �, �, �
				text = text.replace("\\'e9","�"); // mosk\'e9n
				text = text.replace("}"," }"); // frig�r ord
				//text = text.replace("\-}","\- }"); // frig�r ord
				
				text = text.replace("\\-", ""); // s�tter ihop avdelade ord \-
				text = rensaStringXML (text, docStop); // slutrensning mha stopp-lista
				//Utskrift.skrivText("rensad: " + text);
			}
			
			if (FileExtension(fv[i]).matches("rtf"))
			{
				// OBS! Denna rensning funkar N�STAN perfekt!
				// Stopplistan funkar med n�gra f� undantag.
				// ==========================================
				Utskrift.skrivText("Rensar rtf-fil");
				//text = text.replaceAll("&ndash;", " - ").replaceAll("&mdash;", " - ").replaceAll("&nbsp;", " ").replaceAll("&copy;", "").replaceAll("&reg;", "");
				// l�sg�r ord fr�n taggar
				//text = text.replaceAll(">", "> ").replaceAll("<", " <").replaceAll("&lt;", " ").replaceAll("&gt;", " ");
				//text = text.replaceAll(">", "> ").replaceAll("<", " <").replaceAll("&lt;", " < ").replaceAll("&gt;", " > ");

				//text = text.replaceAll("alt=\"", " "); // tar vara p� alt-texter, men ej content.
				//text = text.replaceAll("-\n", "").replaceAll("-\r\n", "").replaceAll("&amp;", "").replaceAll("\";", "\"");
				//text = text.replaceAll("-\n", "").replaceAll("-\r\n", ""); // sl� ihop avdelade ord
				//text = text.replaceAll("&amp;", "&").replaceAll("&#38", "&").replaceAll("&#038", "&");

				text = text.replace("\\endash","�"); // 1590\endash 1600 dvs. 1590�1600
				text = text.replace("\\'f6","�").replace("\\'e4","�").replace("\\'e5","�"); // �, �, �
				text = text.replace("\\'c4","�").replace("\\'c5","�"); // �, �, �
				text = text.replace("\\'e9","�"); // mosk\'e9n
				text = text.replace("\\'94"," "); // frig�r formattering fr�n ord.
				text = text.replace("\\"," \\"); // frig�r formattering fr�n ord.
				text = text.replace("}"," }").replace("{","{ "); // frig�r ord
				

				//text = rensaStringXML (text, rtfStop); // slutrensning mha stopp-lista
				text = text.replace("\\-", ""); // s�tter ihop avdelade ord \-
				text = rensaStringXML (text, rtfStop); // slutrensning mha stopp-lista
				
				//Utskrift.skrivText("rensad RTF: " + text);
			}
			
		//Utskrift.skrivText("efter: " + text);
		Textfil.addText(utfil, text);
		}
	}
	
	// Texterna nu samlade i den nya filen
	Utskrift.skrivText("KLART\nAll text har skrivits till filen: " + utfil);

	}
	
	// Rensar bort s�kv�gen
	//String katalog = removePath(katalog)
	
	public static String removePath (String katalogEllerFil) throws Exception
	{
	// StringTokenizer l�mnar inte extra blanka
	// mellan ord.
		
	StringTokenizer st = new StringTokenizer(katalogEllerFil,"\\");
  
	// S�ker sista delen av str�ngen
	
	int len = st.countTokens();
	
  String [] parts = new String[len];
	
	int i = 0;
	
	while (st.hasMoreTokens())
		{
			parts[i++] = st.nextToken();
		}
		
	String s = parts[len - 1];
	
	return s;	
		
	}
	
	// L�s fr�n en textfil (filobjekt)	
	public static String laesText (File fil) throws Exception
	
	{
	
	//File fil = new File (filnamn); // �ppnar en fil
	
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
	
	// Ta fram fil�ndelsen f�r ett filobject	
	public static String FileExtension(File fil) throws Exception
	{
		String filnamn = fil.getName();
		
		// StringTokenizer l�mnar inte extra blanka
		// mellan ord.
			
		StringTokenizer st = new StringTokenizer(filnamn,".");
	  
		String extension = "";
		
		while (st.hasMoreTokens())
			{
				extension = st.nextToken();
			}
			
		return extension;
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
	
		//Str�ng rensas fr�n ord i stopp-lista (f�r HTML)
	public static String rensaStringHTML (String text, String[] stop) throws Exception
	{
	// StringTokenizer l�mnar inte extra blanka
	// mellan ord.
	
	//text.replaceAll("\n", " ");
		
	//StringTokenizer st = new StringTokenizer(text," ");
	StringTokenizer st = new StringTokenizer(text);
  
			
	String rensad = "";
	String ord = "";
	String forbid2 = "a=|b=|c=|d=|e="; // regexp
	String forbid3 = "MM_|li.|<!-|<w:|<o:|<m:"; // regexp
	String forbid4 = "rel=|src=|id=.|for=|mso-|top:|url=|div.|DefQ|DefP|DefL|</w:|</o:|</m:|dir="; // regexp
	String forbid5 = "href=|type=|face=|size=|http:|rows=|lang=|name=|Name=|text=|cols=|span.|p.Mso|Math.|goon="; // regexp
	String forbid6 = "title=|align=|value=|style=|scope=|color=|\"http:|class=|width=|vlink=|m:val=|media=|color:|v:ext=|xmlns=|clear=|alink=|xmlns:|float:|width:|shape=|wmode="; // regexp
	String forbid7 = "valign=|target=|border=|height=|locked=|Locked=|method=|margin:|panose-|format:|DefSemi|params.|action=|'splash|dateEnd|scheme=|onblur=|filter:"; // regexp
	String forbid8 = "colspan=|summary=|bgcolor=|onclick=|qformat=|QFormat=|charset=|padding:|version=|content=|Strict//|classid=|onfocus=|enctype=|rowspan="; // regexp
	String forbid9 = "itemtype=|itemprop=|priority=|Priority=|onsubmit=|language=|encoding=|this.href|onSubmit=|document.|DefUnhide|overflow:|position:|accesskey="; // regexp
	String forbid10 = "font-size:|trackview=|@font-face|navigator.|swfobject.|newwindow=|accesskey=|resizable="; // regexp
	String forbid11 = "semihidden=|SemiHidden=|http-equiv=|pageTracker|scrollbars="; // regexp
	String forbid12 = "cellpadding=|cellspacing=|font-weight:|font-family:|ispermalink=|ispermalink|isPermaLink=|margin-right|margin-left:|margin-botto|marginwidth=|bordercolor=|marginheight"; // regexp
	String forbid14 = "maximum-scale=|initial-scale=|onselectstart=|marginheight=\""; // regexp
	String forbid15 = "unhidewhenused=|UnhideWhenUsed=|text-underline:|text-decoration|splashScreen.lo|navigator.userA|text-transform:|ChangeDetection|background-imag"; // regexp
	
	// B�ttre : startsWith och endsWith!!
	
	while (st.hasMoreTokens())
		{
			ord = st.nextToken();
			int len = ord.length();
			//Utskrift.skrivText("Ord: " + ord);
			
			if (len>= 2 && ord.substring(0,2).matches(forbid2))
			{}
			if (len>= 3 && ord.substring(0,3).matches(forbid3))
			{}
			else if (len>= 4 && ord.substring(0,4).matches(forbid4))
			{}
			else if (len>= 5 && ord.substring(0,5).matches(forbid5))
			{}
			else if (len >= 6 && ord.substring(0,6).matches(forbid6))
			{}
			else if (len >= 7 && ord.substring(0,7).matches(forbid7))
			{}
			else if (len >= 8 && ord.substring(0,8).matches(forbid8))
			{}
			else if (len >= 9 && ord.substring(0,9).matches(forbid9))
			{}
			else if (len >= 10 && ord.substring(0,10).matches(forbid10))
			{}
			else if (len >= 11 && ord.substring(0,11).matches(forbid11))
			{}
			else if (len >= 12 && ord.substring(0,12).matches(forbid12))
			{}
			else if (len >= 14 && ord.substring(0,14).matches(forbid14))
			{}
			else if (len >= 15 && ord.substring(0,15).matches(forbid15))
			{}
			else if (len>= 3 && ord.contains("twttr.")) // twttr.
			{}
			else if (len>= 3 && ord.contains("]]>"))
			{}
			else if (len>= 3 && ord.contains("px;"))
			{}
			else if (len>= 3 && ord.contains("\"/>"))
			{}
			else if (len>= 3 && ord.contains("\"'>"))
			{}
			else if (len>= 2 && ord.contains("<!"))
			{}
			else if (len>= 2 && ord.contains("+="))
			{}
			else if (len>= 2 && ord.contains("\"\"")) // ""
			{}
			else if (len>= 2 && ord.contains("&&")) // &&
			{}
			else if (len>= 2 && ord.contains("}}")) // }}
			{}
			else if (len>= 2 && ord.contains("()")) // ()
			{}
			else if (len>= 2 && ord.contains("._")) // ._
			{}
			else if (len>= 2 && ord.startsWith("<"))
			{}
			else if (ord.startsWith("#"))
			{}
			else if (ord.startsWith("\"#"))
			{}
			else if (ord.startsWith("{behavior:"))
			{}
			else if (ord.startsWith("{mso-style"))
			{}
			else if (ord.startsWith("{size:"))
			{}
			else if (ord.startsWith("{page:"))
			{}
			else if (ord.startsWith("{font-family:"))
			{}
			else if (ord.startsWith("flashFile["))
			{}
			else if (ord.startsWith("Flash\"]"))
			{}
			else if (ord.startsWith("(navigator."))
			{}
			else if (ord.startsWith("'splash"))
			{}
			else if (ord.startsWith("'http:"))
			{}
			else if (ord.startsWith("(url."))
			{}
			else if (ord.startsWith("((navigator."))
			{}
			else if (ord.startsWith("url("))
			{}
			else if (ord.startsWith("categ_"))
			{}
			else if (ord.startsWith("data["))
			{}
			else if (ord.startsWith("$(")) // $('
			{}
			else if (ord.startsWith("RegExp(")) // RegExp(
			{}
			else if (ord.startsWith("rgb(")) // rgb(
			{}
			else if (ord.startsWith("rgba(")) // rgba(
			{}
			else if (ord.startsWith("alpha(")) // alpha(
			{}
			else if (ord.startsWith("s.parent")) // s.parent
			{}
			else if (ord.startsWith("twttr.")) // twttr.
			{}
			else if (ord.startsWith("str.")) // str.
			{}
			else if (ord.startsWith("parseInt(")) // parseInt(
			{}
			else if (ord.startsWith("//")) // //
			{}
			else if (ord.endsWith("]>")) // ">
			{}
			else if (ord.endsWith("\">")) // ">
			{}
			else if (ord.endsWith("\";")) // ";
			{}
			else if (ord.endsWith("'};")) // '};
			{}
			else if (ord.endsWith("});")) // });
			{}
			else if (ord.endsWith(" );")) //  ); (blank f�rst)
			{}
			else if (ord.endsWith("');")) //  ');
			{}
			else if (ord.endsWith(");\"")) //  );"
			{}
			else if (ord.endsWith(");\"")) //  );"
			{}
			else if (ord.endsWith("\");")) //  ");
			{}
			else if (ord.endsWith("();")) //  ();;
			{}
			else if (ord.endsWith("url);")) //  url);
			{}
			else if (ord.endsWith("));")) //  ));
			{}
			else if (ord.endsWith(");var")) //  );var
			{}
			else if (ord.endsWith(");return")) //  );return
			{}
			//else if (ord.endsWith(");") && !ord.contains(".")) // );
			//{}
			else if (ord.endsWith(";}")) // ;}
			{}
			else if (ord.endsWith(".jpg\"")) // .jpg"
			{}
			else if (ord.endsWith(".gif\"")) // .gif"
			{}
			else if (ord.endsWith("//EN\"")) // //EN"
			{}
			else if (Arrays.binarySearch (stop, ord) >= 0)
			{}
			else
			rensad = rensad + ord + " ";
		}
		
	return rensad;	
		
	}

	//Str�ng rensas fr�n taggar (f�r PHP)
	public static String rensaStringPHP (String text, String[] stop) throws Exception
	{
	// Mesta rensningen sker med rensningen f�r HTML!
	// StringTokenizer l�mnar inte extra blanka
	// mellan ord.
	
	//text.replaceAll("\n", " ");
		
	//StringTokenizer st = new StringTokenizer(text," ");
	StringTokenizer st = new StringTokenizer(text);
  
			
	String rensad = "";
	String ord = "";
	
	String forbid5 = "TYPE=|NAME=|SIZE=|FACE="; // regexp
	String forbid6 = "VALUE="; // regexp
	String forbid7 = "METHOD=|ACTION="; // regexp
	
	/***********************************************
	String forbid2 = "b="; // regexp
	String forbid3 = "MM_|li.|<!-|<w:|<o:|<m:"; // regexp
	String forbid4 = "rel=|src=|id=.|for=|mso-|top:|url=|div.|DefQ|DefP|DefL|</w:|</o:|</m:|dir="; // regexp
	String forbid5 = "href=|type=|face=|size=|http:|rows=|lang=|name=|Name=|text=|cols=|span.|p.Mso|Math.|goon="; // regexp
	String forbid6 = "title=|align=|value=|style=|scope=|color=|\"http:|class=|width=|vlink=|m:val=|media=|color:|v:ext=|xmlns=|clear=|alink=|xmlns:|float:|width:|shape=|wmode="; // regexp
	String forbid7 = "valign=|target=|border=|height=|locked=|Locked=|method=|margin:|panose-|format:|DefSemi|params.|action=|'splash|dateEnd|scheme=|onblur="; // regexp
	String forbid8 = "colspan=|summary=|bgcolor=|onclick=|qformat=|QFormat=|charset=|padding:|version=|content=|Strict//|classid=|onfocus=|enctype=|rowspan="; // regexp
	String forbid9 = "itemtype=|itemprop=|priority=|Priority=|onsubmit=|language=|encoding=|this.href|onSubmit=|document.|DefUnhide|overflow:|position:|accesskey="; // regexp
	String forbid10 = "font-size:|trackview=|@font-face|navigator.|swfobject.|newwindow=|accesskey="; // regexp
	String forbid11 = "semihidden=|SemiHidden=|http-equiv=|pageTracker"; // regexp
	String forbid12 = "cellpadding=|cellspacing=|font-weight:|font-family:|ispermalink=|ispermalink|isPermaLink=|margin-right|margin-left:|margin-botto|marginwidth=|bordercolor=|marginheight"; // regexp
	String forbid14 = "maximum-scale=|initial-scale=|onselectstart=|marginheight=\""; // regexp
	String forbid15 = "unhidewhenused=|UnhideWhenUsed=|text-underline:|text-decoration|splashScreen.lo|navigator.userA|text-transform:"; // regexp
	**************************************************/
	
	// B�ttre : startsWith och endsWith!!
	
	while (st.hasMoreTokens())
		{
			ord = st.nextToken();
			int len = ord.length();
			
			if (len >= 5 && ord.substring(0,5).matches(forbid5))
			{}
			else if (len >= 6 && ord.substring(0,6).matches(forbid6))
			{}
			else if (len >= 7 && ord.substring(0,7).matches(forbid7))
			{}

			/****************************************************
			if (len >= 2 && ord.substring(0,2).matches(forbid2))
			{}
			if (len >= 3 && ord.substring(0,3).matches(forbid3))
			{}
			else if (len >= 4 && ord.substring(0,4).matches(forbid4))
			{}
			else if (len >= 5 && ord.substring(0,5).matches(forbid5))
			{}
			else if (len >= 6 && ord.substring(0,6).matches(forbid6))
			{}
			else if (len >= 7 && ord.substring(0,7).matches(forbid7))
			{}
			else if (len >= 8 && ord.substring(0,8).matches(forbid8))
			{}
			else if (len >= 9 && ord.substring(0,9).matches(forbid9))
			{}
			else if (len >= 10 && ord.substring(0,10).matches(forbid10))
			{}
			else if (len >= 11 && ord.substring(0,11).matches(forbid11))
			{}
			else if (len >= 12 && ord.substring(0,12).matches(forbid12))
			{}
			else if (len >= 14 && ord.substring(0,14).matches(forbid14))
			{}
			else if (len >= 15 && ord.substring(0,15).matches(forbid15))
			{}
			else if (len>= 3 && ord.contains("]]>"))
			{}
			else if (len>= 3 && ord.contains("px;"))
			{}
			******************************************************/
			
			else if (ord.startsWith("<?"))
			{}
			else if (ord.startsWith("include"))
			{}
			else if (ord.startsWith("$meta"))
			{}
			else if (ord.startsWith(");"))
			{}
			else if (ord.startsWith("$meny"))
			{}
			else if (ord.startsWith("$validated"))
			{}
			else if (ord.startsWith("start(\""))
			{}
			else if (ord.startsWith("?>"))
			{}
			else if (ord.startsWith("method="))
			{}
			else if (ord.startsWith("onClick="))
			{}
			else if (ord.startsWith(");return"))
			{}
			else if (ord.startsWith("slut();"))
			{}
			else if (ord.startsWith("array("))
			{}
			else if (ord.startsWith("ruta("))
			{}

/***************************************************************
			else if (ord.endsWith("]>")) // ">
			{}
			else if (ord.endsWith("\">")) // ">
			{}
			else if (ord.endsWith("\";")) // ";
			{}
			else if (ord.endsWith(");")) // );
			{}
			else if (ord.endsWith(";}")) // ;}
			{}
			else if (ord.endsWith(".jpg\"")) // .jpg"
			{}
			else if (ord.endsWith(".gif\"")) // .gif"
			{}
			else if (ord.endsWith("//EN\"")) // //EN"
			{}
*******************************************************************/
			else if (Arrays.binarySearch (stop, ord) >= 0)
			{}

			else
			rensad = rensad + ord + " ";
		}
		
	return rensad;	
		
	}
		
	//Str�ng rensas fr�n ord i stopp-lista (f�r XML)
	public static String rensaStringXML (String text, String[] stop) throws Exception
	{
	// StringTokenizer l�mnar inte extra blanka
	// mellan ord.
	
	//text.replaceAll("\n", " ");
		
	//StringTokenizer st = new StringTokenizer(text," ");
	StringTokenizer st = new StringTokenizer(text);
  
			
	String rensad = "";
	String ord = "";
	//String forbid2 = "b="; // regexp
	String forbid3 = "_wp|id="; // regexp
	String forbid4 = "<wp:|</wp|rel="; // regexp
	String forbid5 = "sort=|href=|role=|alpha=|list="; // regexp
	String forbid6 = "title=|xmlns:|width="; // regexp
	String forbid7 = "search=|target=|domain="; // regexp
	String forbid8 = "created=|version=|message="; // regexp
	String forbid9 = "nicename=|encoding="; // regexp
	String forbid10 = "_MailPress|generator="; // regexp
	String forbid11 = "pagination="; // regexp
	String forbid12 = "isPermaLink=|pagination2="; // regexp
	/********************************************
	String forbid14 = "maximum-scale=|initial-scale=|onselectstart=|marginheight=\""; // regexp
	String forbid15 = "unhidewhenused=|UnhideWhenUsed=|text-underline:|text-decoration|splashScreen.lo|navigator.userA|text-transform:"; // regexp
	*****************************************/
	
	// B�ttre : startsWith och endsWith!!
	
	while (st.hasMoreTokens())
		{
			ord = st.nextToken();
			int len = ord.length();
			
			//if (len>= 2 && ord.substring(0,2).matches(forbid2))
			{}
			if (len>= 3 && ord.substring(0,3).matches(forbid3))
			{}
			else if (len>= 4 && ord.substring(0,4).matches(forbid4))
			{}
			else if (len>= 5 && ord.substring(0,5).matches(forbid5))
			{}
			else if (len >= 6 && ord.substring(0,6).matches(forbid6))
			{}
			else if (len >= 7 && ord.substring(0,7).matches(forbid7))
			{}
			else if (len >= 8 && ord.substring(0,8).matches(forbid8))
			{}
			else if (len >= 9 && ord.substring(0,9).matches(forbid9))
			{}
			else if (len >= 10 && ord.substring(0,10).matches(forbid10))
			{}
			else if (len >= 11 && ord.substring(0,11).matches(forbid11))
			{}
			else if (len >= 12 && ord.substring(0,12).matches(forbid12))
			{}
			/****************************************		
			else if (len >= 14 && ord.substring(0,14).matches(forbid14))
			{}
			else if (len >= 15 && ord.substring(0,15).matches(forbid15))
			{}
			else if (len>= 3 && ord.contains("px;"))
			{}
			else if (len>= 3 && ord.contains("\"/>"))
			{}
			else if (len>= 3 && ord.contains("\"'>"))
			{}
			else if (len>= 3 && ord.contains("<!"))
			{}
			else if (ord.startsWith("{behavior:"))
			{}
			else if (ord.startsWith("{mso-style"))
			{}
			else if (ord.startsWith("{size:"))
			{}
			else if (ord.startsWith("{page:"))
			{}
			else if (ord.startsWith("{font-family:"))
			{}
			else if (ord.startsWith("flashFile["))
			{}
			else if (ord.startsWith("Flash\"]"))
			{}
			else if (ord.startsWith("(navigator."))
			{}
			else if (ord.startsWith("'splash"))
			{}
			else if (ord.startsWith("'http:"))
			{}
			else if (ord.startsWith("(url."))
			{}
			else if (ord.startsWith("((navigator."))
			{}
			else if (ord.startsWith("url("))
			{}
			else if (ord.startsWith("categ_"))
			{}
			else if (ord.startsWith("data["))
			{}
			else if (ord.endsWith("]>")) // ">
			{}
			else if (ord.endsWith("\">")) // ">
			{}
			else if (ord.endsWith("\";")) // ";
			{}
			else if (ord.endsWith(");")) // );
			{}
			else if (ord.endsWith(";}")) // ;}
			{}
			else if (ord.endsWith(".jpg\"")) // .jpg"
			{}
			else if (ord.endsWith(".gif\"")) // .gif"
			{}
			else if (ord.endsWith("//EN\"")) // //EN"
			{}
			************************************/
			else if (len>= 3 && ord.contains("]]>"))
			{}
			else if (len>= 3 && ord.contains("<!"))
			{}
			else if (ord.endsWith(".gif")) // .gif"
			{}
			else if (ord.endsWith(".jpg")) // .jpg"
			{}
			else if (Arrays.binarySearch (stop, ord) >= 0)
			{}
			else
			rensad = rensad + ord + " ";
		}
		
	return rensad;	
		
	}
}