<#include "shared.ftl">
<html>
    <head>
        <title>Zonky loanbook v barvách (${data.period})</title>
        <!-- Global site tag (gtag.js) - Google Analytics -->
        <script async src="https://www.googletagmanager.com/gtag/js?id=UA-121920303-2"></script>
        <script>
          window.dataLayer = window.dataLayer || [];
          function gtag(){dataLayer.push(arguments);}
          gtag('js', new Date());
        
          gtag('config', 'UA-121920303-2');
        </script>
        <script src="https://www.gstatic.com/charts/loader.js" defer></script>
        <!-- Store this locally as the updated versions available online throw all kinds of errors. -->
        <script src="rgbcolor.js" defer></script>
        <script src="canvg.js" defer></script>
        <!-- And here starts our own code. -->
        <script src="svgprint.js" defer></script>
        <script src="charts.js" defer></script>
    </head>
    <body>
        <h1>Zonky loanbook v barvách (${data.period})</h1>
        <ul>
            <li>Tato stránka poskytuje vizualizaci dat z loanbooku investiční platformy Zonky. Mějte na paměti, že <strong>může obsahovat chyby</strong>, nikdo není neomylný. Na konci stránky najdete <a href="#changes">seznam změn</a>.</li>
            <li>Stránka aktualizována nahodile, zpravidla jednou za měsíc. Poslední aktualizace proběhla ${data.now?date} v ${data.now?time}.</li>
            <li>Zdrojová data jsou ke stažení na stránce <a href="https://zonky.cz/risk/">Jak se splácí půjčky na Zonky</a>.</li>
            <li>Provozovatelem této stránky jsem já, <a href="https://www.petrovicky.net/">Lukáš Petrovický</a>, autor <a href="https://www.robozonky.cz/">investičního robota RoboZonky</a>. Nejsem žádný velký grafik a hlavně mě uživatelská rozhraní nebaví. <strong>Chcete-li pomoci tuto stránku nastylovat, ozvěte se.</strong></li>
            <li>Ozvěte se také pokud si myslíte, že tu ještě nějaký graf chybí. Občas ve volném čase nějaký přidám.</li>
        </ul>

        <h2>Dostupné grafy</h2>
        <ul>
        <#list data.charts as chart>
            <li><a href="#<@htmlId chart />">${chart.getTitle()}</a></li>
        </#list>
        </ul>

        <h2>A teď už vážně</h2>
        <#list data.charts as chart>
            <@chartMeta chart />
        </#list>

        <h2 id="changes">Změny a novinky</h2>
        <dl>
            <dt>6. 3. 2020: Normalizovaný graf zesplatněnosti</dt>
            <dt>5. 3. 2020: Informativní osy</dt>
                <dd>Upravil jsem intervaly na osách délky a výše půjčky tak, aby bylo zřejmé, ze které strany jsou tyto intervaly uzavřené.</dd>
            <dt>7. 2. 2020: Nové grafy</dt>
                <dd>Přidal jsem grafy k objemu a počtu půjček v čase, podle úrokové míry.</dd>
            <dt>6. 1. 2020: Změna grafů o zesplatněnosti</dt>
                <dd>Většina grafů týkajících se zesplatněnosti nově jako základ používá pouze půjčky ukončené. Vyhneme se tím zkreslení, kdy rostoucí tempo přírůstku nových zdravých půjček mohlo zakrývat potenciálně vzrůstající zesplatněnost.</dd>
                <dd>Kromě toho jsem nově rozdělil délky úvěru po 6 měsících namísto původních 12.</dd>
        </dl>
        <!-- Github Corner from http://tholman.com/github-corners/ -->
        <a href="https://github.com/RoboZonky/zonky-loanbook" class="github-corner" aria-label="Zdrojový kód na Github"><svg width="80" height="80" viewBox="0 0 250 250" style="fill:#151513; color:#fff; position: absolute; top: 0; border: 0; right: 0;" aria-hidden="true"><path d="M0,0 L115,115 L130,115 L142,142 L250,250 L250,0 Z"></path><path d="M128.3,109.0 C113.8,99.7 119.0,89.6 119.0,89.6 C122.0,82.7 120.5,78.6 120.5,78.6 C119.2,72.0 123.4,76.3 123.4,76.3 C127.3,80.9 125.5,87.3 125.5,87.3 C122.9,97.6 130.6,101.9 134.4,103.2" fill="currentColor" style="transform-origin: 130px 106px;" class="octo-arm"></path><path d="M115.0,115.0 C114.9,115.1 118.7,116.5 119.8,115.4 L133.7,101.6 C136.9,99.2 139.9,98.4 142.2,98.6 C133.8,88.0 127.5,74.4 143.8,58.0 C148.5,53.4 154.0,51.2 159.7,51.0 C160.3,49.4 163.2,43.6 171.4,40.1 C171.4,40.1 176.1,42.5 178.8,56.2 C183.1,58.6 187.2,61.8 190.9,65.4 C194.5,69.0 197.7,73.2 200.1,77.6 C213.8,80.2 216.3,84.9 216.3,84.9 C212.7,93.1 206.9,96.0 205.4,96.6 C205.1,102.4 203.0,107.8 198.3,112.5 C181.9,128.9 168.3,122.5 157.7,114.1 C157.9,116.9 156.7,120.9 152.7,124.9 L141.0,136.5 C139.8,137.7 141.6,141.9 141.8,141.8 Z" fill="currentColor" class="octo-body"></path></svg></a><style>.github-corner:hover .octo-arm{animation:octocat-wave 560ms ease-in-out}@keyframes octocat-wave{0%,100%{transform:rotate(0)}20%,60%{transform:rotate(-25deg)}40%,80%{transform:rotate(10deg)}}@media (max-width:500px){.github-corner:hover .octo-arm{animation:none}.github-corner .octo-arm{animation:octocat-wave 560ms ease-in-out}}</style>
    </body>
</html>
