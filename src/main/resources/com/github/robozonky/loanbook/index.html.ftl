<#-- Basic macros to be used in charts. -->
<#macro chartFunctionName chart>draw${chart.getType()}${chart.getAxisCount()}Chart${chart.getId()}</#macro>
<#macro htmlId chart>chart-${chart.getType()}-${chart.getId()}</#macro>
<#macro htmlIdInteractive chart><@htmlId chart />-interactive</#macro>
<#macro htmlIdPng chart><@htmlId chart />-png</#macro>

<#-- Support for individual charts. -->
<#include "chart-bar3.js.ftl">

<#-- Rendering of the individual charts from a shared macro. -->
<#macro chartFunction chart>
    <#assign type>${chart.getType().toString()}</#assign>
    <#switch type>
      <#case "COLUMN">
      <#case "BAR">
        <@barChart3 chart />
        <#break>
      <#default>
        // nothing
    </#switch>
</#macro>
<#macro chartMeta chart>
    <div id="<@htmlId chart />">
        <div id="<@htmlIdInteractive chart />" style="width: 800px; height: 600px;"></div>
        <div id='<@htmlIdPng chart />'>
            <input type='button' value='Stáhnout jako PNG' onclick="generate('<@htmlId chart />.png', document.getElementById('<@htmlIdInteractive chart />').getElementsByTagName('svg')[0]);" />
        </div>
    </div>
    <hr />
</#macro>

<#-- And this, finally, is the HTML source. -->
<html>
    <head>
        <title>Vizualizace Zonky loanbooku</title>
        <script src="https://www.gstatic.com/charts/loader.js"></script>
        <!-- Store this locally as the updated versions available online throw all kinds of errors. -->
        <script src="rgbcolor.js"></script>
        <script src="canvg.js"></script>
        <!-- And here starts our own code. -->
        <script src="svgprint.js"></script>
        <script type="text/javascript">
          google.charts.load('current', {'packages':['bar', 'corechart']});
          <#list data.charts as chart>
              <@chartFunction chart />
              google.charts.setOnLoadCallback(<@chartFunctionName chart />);
          </#list>
        </script>
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
