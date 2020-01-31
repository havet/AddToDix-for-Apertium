Read me p� Svenska

Instruktioner
=============

Dessa sm� enkla Java-program �r t�nkta
att underl�tta f�r dig som vill l�gga till
nya ord till ett Apertium-spr�kpar,
tex Svenska - Danska (sv-da).

Den st�rsta f�rdelen �r att du slipper skriva xml-kod
och g�ra triviala misstag, som inte alls har med spr�k
och �vers�ttning att g�ra. Dessutom underl�ttar programmen
genom att automatiskt kontrollera olika saker och presentera
alternativ n�r du t.ex. ska ange paradigm (b�jningsm�nster).

Anv�nd dessa program f�r att g�ra till�ggsf�rslag till
ordlistorna. Sedan skickar du dem till resp. spr�kutvecklare,
som l�gger till orden till den officiella ordlistan. 
Eller g�r det sj�lv om du blivit utvecklare f�r spr�ket.

Utg�ngspunkt:
Det �r sv�rt och kr�ver 10 000-tals nya ord om man ska
t�cka in alla t�nkbara sorters texter. N�r man totalt har kanske
50 - 60 000 ord i ordlistorna kanske man n�r c:a 80 % t�ckning
n�r man �vers�tter.

Det �r mycket l�ttare att f� en h�g t�ckning, ja t.o.m. �ver
90 % f�r en viss typ av texter, l�t oss kalla det en dom�n.
Om du �r intresserad av n�got visst omr�de, 
det m� vara daggmaskar, n�versl�jd, filosofi, knyppling, 
f�rskolepedagogik eller n�got annat, 
kan du l�gga till ord inom det omr�det och snabbt f�
en fungerande �vers�ttningshj�lp.

Principen �r att l�gga in de vanligaste orden f�rst. Det ger
snabbt en f�rb�ttring av �vers�ttningen. D�rf�r b�rjar 
man med att g�ra en frekvenslista utifr�n en korpus dvs. en samling 
texter. Sedan l�gger man till orden i frekvensordning.

G�r s� h�r:

1. Installera programmen genom att packa upp dem i en katalog 
d�r du har skrivr�ttigheter, t.ex. n�gonstans under 
"Mina dokument" eller under ditt anv�ndarnamn.
Du k�r programmen genom att �ppna kommandotolken ("DOS-f�nstret"),
bl�ddra till programkatalogen och skriva "java" + programmets namn, tex:
java OrdFrekvens

2. Med programmen f�ljer de officiella ordlistorna:
sv-da.da.dix
sv-da.sv-da.dix
sv-da.sv.dix

De beh�ver sannolikt uppdateras. Ladda d�rf�r ned de senast fr�n Apertium.
Se Apertiums hemsida: www.apertium.org
Utgivna spr�kpar finns under apertium-trunk.
Kopiera sedan den senaste versionen till mappen d�r du packat upp
AddToDix, s� att de gamla versionerna skrivs �ver.

3. �vers�tt FR�N det spr�k du kan s�mst TILL det spr�k du kan b�st 
(t.ex. ditt modersm�l). Jag kallar nedan spr�ket du �vers�tter 
fr�n f�r k�llspr�k och spr�ket du �vers�tter till f�r m�lspr�k. 

4. Samla f�rst s� m�nga texter som m�jligt (skrivna p� k�llspr�ket) 
inom den dom�n du vill arbeta med. L�gg dem sedan i en textfil, 
och spara den i samma mapp som programmen. (Anv�nd t.ex. programmet 
Anteckningar: Start - Alla program - Tillbeh�r - Anteckningar)
Har du filer med l�mpliga texter? D� kan du kopiera textfiler, 
rtf-filer och gamla wordfiler (.doc) till en mapp och sedan k�ra programmet 
CollectText f�r att samla texterna i en textfil. Filen f�r samma namn 
som mappen och �ndelsen ".txt".

5. K�r programmet OrdFrekvens.java f�r att f� en lista p� alla ord
 i textfilen, listade i frekvensordning dvs. de vanligaste orden f�rst.
Ord som redan finns i ordlistorna ska i stort sett vara bortrensade, 
liksom skr�ptecken. Listan heter "Frekvens. + DinTextfil"
(t.ex. Frekvens.DanishTowns.txt) och finns i samma katalog som programmen.

6. Du ska nu l�gga in orden i frekvensordning. Genom att du l�gger 
in de vanligaste orden f�rst, f�r du snabbt nytta av ditt arbete.

7. B�rja med att l�gga in ord i den enspr�kiga ordlistan f�r k�llspr�ket 
med programmet AddToDictionnary.java
Programmet kollar att orden inte redan finns i ordlistan, s� du inte 
g�r n�got on�digt arbete. (Du beh�ver allts� inte heller s�ka igenom 
ordlistorna f�r se om ordet finns - programmet g�r jobbet!) 
Spara filen n�r du �r klar.

8. L�gg sedan in �vers�ttningen av ordet i den tv�spr�kiga ordlistan 
med programmet AddToBidixFromMonodix.java Programmet l�ser in orden fr�n den 
enspr�kiga ordlistan du skapat tidigare, s� att du bara beh�ver
l�gga till information om �vers�ttningen mm. Spara filen n�r du �r klar.

9. L�gg slutligen till ord i den enspr�kiga ordlistan f�r m�lspr�ket 
med programmet AddToDictionaryFromBidix.java Programmet l�ser in orden fr�n 
den tv�spr�kiga ordlistan du nyss skapat, s� att du bara beh�ver l�gga 
till information om t.ex. paradigm (b�jningsm�nster).
Spara filen.

10. N�r du �r klar skickar du de tre filerna till resp. spr�kutvecklare, 
g�rna komprimerade i en zip-fil. Utvecklaren kontrollerar dina filer, 
�ndrar eventuellt lite grand, och l�gger sedan till de nya orden till 
Apertium-parets officiella ordlistor.

11. Filerna heter:

Ordfrekvens
-----------
Frekvens.DinTextfil.txt (t.ex. Frekvens.DanishTowns.txt) Skicka inte den!

Ordlistor (Skicka dessa!)
-------------------------

spr�kpar.spr�k1.dix.txt (t.ex. sv-da.da.dix.txt)

spr�kpar.spr�kpar.dix.txt (t.ex. sv-da.sv-da.dix.txt)

spr�kpar.spr�k2.dix.txt(t.ex. sv-da.sv.dix.txt)