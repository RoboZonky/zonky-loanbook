<#include "shared.ftl">
<html>
    <head>
        <title>Vizualizace Zonky loanbooku</title>
        <script src="https://www.gstatic.com/charts/loader.js" defer></script>
        <!-- Store this locally as the updated versions available online throw all kinds of errors. -->
        <script src="rgbcolor.js" defer></script>
        <script src="canvg.js" defer></script>
        <!-- And here starts our own code. -->
        <script src="svgprint.js" defer></script>
        <script src="charts.js" defer></script>
    </head>
    <body>
        <h1>Vizualizace Zonky loanbooku</h1>
        <ul>
            <li>Tato stránka poskytuje vizualizaci dat z loanbooku investiční platformy Zonky. Jako každá jiná data, i tato vyžadují interpretaci.</li>
            <li>Je aktualizována nahodile, zpravidla jednou za měsíc. Poslední aktualizace proběhla ${data.now?date} v ${data.now?time}.</li>
            <li>Zdrojová data jsou ke stažení na stránce <a href="https://zonky.cz/risk/">Jak se splácí půjčky na Zonky</a>.</li>
            <li>Provozovatelem této stránky je <a href="https://www.petrovicky.net/">Lukáš Petrovický</a>, autor <a href="https://www.robozonky.cz/">investičního robota RoboZonky</a>. Nejsem žádný velký grafik a hlavně mě uživatelské rozhraní nebaví. <strong>Chcete-li pomoci tuto stránku nastylovat, ozvěte se.</strong></li>
        </ul>
        <h2>A teď už grafy</h2>
        <#list data.charts as chart>
            <@chartMeta chart />
        </#list>
    </body>
</html>
