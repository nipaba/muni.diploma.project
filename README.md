# Návod pro použití pluginu CCT

Pro podrobnější návod včetně obrázků [zde](https://github.com/nipaba/muni.diploma.project/blob/master/N%C3%A1vod.pdf)

## Instalace
Pro plugin Connected Component Tree (CCT) je nutné mít nainstalováno program ImageJ 2.0 (https://imagej.net/Welcome) a Javu ve verzi 8. Pro nainstalování pluginu je nutné soubor CCT-plugin.jar nakopírovat do složky plugins v adresáři s programem ImageJ. 

## Kompilace zdrojových kódů
Program je vytvořený jako $maven$ projekt. Je možné jej importovat do kteréhokoliv vývojového nástroje (Eclipse, Netbeans, IdeaJ) pro vývoj Java aplikací. Pro automatické nasazení je pak možné změnou následujícího parametru v souboru $pom.xml$:
' 	<outputDirectory>cesta k ImageJ/plugins</outputDirectory>'
docílit nasazení přímo do složky plugins aplikace ImageJ. Toho můžeme dosáhnout maven příkazem clean install.

## Používání pluginu
Plugin CCT je možné spustit až po otevření obrazu určeného pro analýzu. Po otevření obrazu je nejdříve nutné převést obraz na 8-bitový typ pomocí příkazu programu ImageJ (Image –> Type –> 8-bit) a v případě velkého obrazu (větší než 1024 x 800) spustit mediánový nebo Gaussův filtr (aspoň v minimální velikosti). Následně je možné spustit plugin CCT (Process –> CCT), který nejprve sestaví maximový, minimový strom a strom tvarů. Průběh sestavování je možné sledovat na ukazateli ve spodní části okna, který zobrazuje procenta zpracování sestavení stromů.
Okno pluginu CCT umožňuje uživateli nastavovat filtrovací kritéria, volit formu analýzy, sledovat parametry testovaného obrazu a výsledky provedené analýzy.


Po sestavení stromů může uživatel pomocí myši vytvářet ohraničující obdélníky, v nichž program segmentuje komponenty/buňky v obraze (výpočet segmentace u prvního obdélníku je delší a trvá déle, protože program v tomto kroku dopočítává další parametry komponent). Pomocí filtrovacích kritérií jako je velikost komponent (Size), výška stromu (Height), protáhlost komponent (Elongation), průměrná intenzita jasu (AVG intensity) či kulatost komponent (Roundness) lze nastavovat parametry pro segmentaci a lze také přepínat mezi minimovým stromem (Min Tree), maximovým stromem (Max Tree) a stromem tvarů (Shape Tree). Pro aplikaci filtrovacího kritéria či stromu je třeba použít tlačítko Filter. Pokud chceme smazat zobrazenou segmentaci a vrátit se k původnímu obrazu, použijeme tlačítko Orig img (Original image).
Výše popsaným postupem dosahujeme segmentace buněk, ale abychom byli schopni exaktně zhodnotit míru úspěšnosti segmentace, je potřeba v programu ImageJ otevřít ještě masku analyzovaného obrazu. Masku otevřeme pomocí příkazu Open GT mask. U masky obrazu je nutné, aby obsahovala 8-bitový obraz, kde jednotlivé komponenty jsou rozlišitelné pomocí 4-okolí a mají hodnotu intenzity 255 a pozadí s hodnotou intenzity 0.

## Masky 
Masky obrazů otevíráme v programu pomocí tlačítka Open GT mask (ground truth). Pro lepší orientaci byly masky v pluginu ImageJ upraveny tak, aby byla každá buňka souvislá v čtyř-okolí označena jinou značkou/číselnou hodnotou – „mask value“. Hodnotu komponenty zjistíme a orientujeme se podle ní při automatické analýze „Mask Jaccard“, kdy je obsažena v exportovaném csv souboru (zjišťujeme, které komponenty se týká zjištěný výpočet). Číselnou hodnotu komponenty v masce sledujeme i při manuálním výpočtu koeficientu J, kdy je hodnota komponenty zobrazena v programu v řádku Jaccard koeficient (mask value) v závorce.

## Analýzy přesnosti segmentace
Po otevření masky můžeme spouštět analýzy segmentace pomocí Jaccardova koeficientu J či pomocí koeficientu přesnosti A, které jsou schopny zhodnotit přesnost segmentace nebo vyhodnotit, jaké nastavení programu vede k nejpřesnější segmentaci.
Analýzy lze provádět automaticky či manuálně. Pro automatické analýzy se po otevření masky zpřístupní tlačítka „Mask Jaccard“ (počítá koeficient J) a Mask Accuracy (počítá koeficient A). Manuální analýza probíhá na základě ohraničujících obdélníků, které vytváří sám uživatel kurzorem myši. Při zakliknutí tlačítka "Selection only" analýza zahrnuje vždy jednu největší buňku v ohraničujícím obdélníku a počítá koeficient J, pokud není tlačítko "Selection only" zakliknuto, provádí se analýza všech komponent v obdélníku a počítá se koeficient A.

## Automatická analýza - tlačítko Mask Jaccard
Tlačítko Mask Jaccard vyhodnocuje, jaký strom je nejvhodnější pro segmentaci jednotlivé buňky. 
Při výpočtu je vzata každá buňka v masce, je kolem ní vytvořen ohraničující obdélník (tím je simulován vstup uživatele) a tyto ohraničující obdélníky jsou použity jako filtrovací kritéria nad všemi třemi stromy. Po vypočtení výsledků program nabídne jejich vyexportování do csv souboru. V csv souboru pak pomocí Jaccardova koeficientu můžeme vyfiltrovat a identifikovat nejvhodnější strom pro segmentaci dané buňky. To, které buňky se dané nastavení stromu týká, zjistíme podle číselného označení konkrétní buňky v masce, které je v csv souboru zobrazeno ve sloupci "maskvalue". 

Popis sloupců souboru csv:

- maskValue - označení konkrétní buňky v masce
- treeType - použitý typ stromu: strom tvarů (SHAPE), max. strom (MAX), min. strom (MIN)
- tp - množina hodnot pravdivě pozitivních
- fp - množina hodnot nepravdivě pozitivních
- fn - množina hodnot nepravdivě negativních
- jaccardCoef - výsledná hodnota Jaccardova koeficientu


Po zvolení vhodného stromu je dále možné segmentaci upravovat pomocí nastavení filtrovacích kritérií.

## Automatická analýza - tlačítko Mask Accuracy
Analýza Mask Accuracy vyhodnocuje, který strom a jaké hodnoty filtrovacích kritérií (či jejich kombinace) vedou k nejpřesnější segmentaci obrazu jako celku.
Pro analýzu zvolíme příkaz Mask Accuracy, který spustí opakované segmentování v cyklech. Funkce porovná každý výsledek segmentace vůči masce. Jakmile jsou výsledky porovnání spočítány, nabídne program jejich exportování do souboru csv, kde pomocí seřazení dle koeficientu A identifikujeme nejvhodnější nastavení stromu a filtrů pro segmentaci, tedy takové, při jejichž použití vede dle koeficientu k nejpřesnějším výsledkům.
Popis sloupců:


- tree - použitý strom (MAX, MIN, SHAPE)
- measureType1	- použité kritérium 1
- param1 - hodnota kritéria 1
- measureType2 - použité kritérium 1 (empty značí, že nebylo použité)
- param2 - hodnota kritéria 2 (-1 značí, že nebylo použité)
- filteredSize - počet získaných komponent
- filteredMiss - počet komponent které kompletně minuly komponenty masky
- maskHits	- počet komponent masky které byly nalezeny
- maskMiss	- počet komponent masky které nebyly nalezeny
- tp - množina hodnot pravdivě pozitivní (porovnání pixelů v celé masce)
- fp - množina hodnot nepravdivě pozitivní (porovnání pixelů v celé masce)
- fn - množina hodnot nepravdivě negativní (porovnání pixelů v celé masce)
- jaccardPixels	- hodnota Jaccardova koeficientu pixelů (porovnání pixelů v celé masce)
- acuracy - přesnost segmentace


## Manuální analýza obrazu pomocí Jaccardova koeficientu

Manuální analýza pomocí Jaccardova koeficientu vyhodnocuje procentuální úspěšnost segmentace jednotlivé buňky.
Provádíme ji po výběru tlačítka "Selection only". Poté můžeme na obrazu ručně vytvořit ohraničující obdélník a program je schopen srovnat přesnost segmentace největší buňky v obdélníku vůči masce. Po zmáčknutí tlačítka "Compare" se v řádku Jaccard koeficient (mask value) zobrazí procento úspěšně segmentované plochy. Přesnost se dá ještě zvýšit použitím filtrovacích kritérií. Po jejich změně je třeba použít tlačítko "Filter" a opět "Compare". Pro lepší orientaci je za procentuálním vyjádřením přesnosti segmentace v závorce uvedeno číslo komponenty v masce (mask value).
Pokud ohraničující obdélník manuálně nevytvoříme a přejdeme rovnou k filtrovacím kritériím a tlačítku "Compare", je za ohraničující obdélník považován celý obraz a program analyzuje největší buňku dle nastavených filtrujících kritérií.

## Manuální analýza přesnosti
Manuální analýza pomocí koeficientu A vyhodnocuje, kolik buněk ve vytvořeném ohraničujícím obdélníku bylo segmentací správně zasaženo.
Pro tuto manuální analýzu není vybráno "Selection only" a jsou analyzovány všechny buňky ve vytvořeném ohraničujícím obdélníku. Výsledek segmentace upravujeme volením hodnot filtrovacích kritérií. Když jsou filtrovací kritéria navolena, můžeme zvolit příkaz "Filter" a "Compare" a v řádku "Accuracy" zjistíme, kolik procent komponent v daném obdélníku jsme pravdivě zasáhli.
Pokud ohraničující obdélník manuálně nevytvoříme a přejdeme rovnou k filtrovacím kritériím a tlačítku "Compare", je za ohraničující obdélník považován celý obraz a program analyzuje všechny buňky v obraze.

## Vlastnosti aplikace
Při používání aplikace je nutné brát v potaz velikost analyzovaného obrazu. Doba tvorby stromu je závislá na počtu úrovní intenzit v obraze a velikosti obrazu. Nejnáročnějšími částmi výpočtu je tvorba stromů a první filtrování nad obrazem, kdy se dopočítávají některé vlastnosti komponent.
