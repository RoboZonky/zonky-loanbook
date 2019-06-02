<#-- Basic macros to be used in charts. -->
<#macro chartFunctionName chart>draw${chart.getType()}${chart.getAxisCount()}Chart${chart.getId()}</#macro>
<#macro htmlId chart>chart-${chart.getType()}-${chart.getId()}</#macro>
<#macro htmlIdInteractive chart><@htmlId chart />-interactive</#macro>

<#-- Support for individual charts. -->
<#include "chart-pie.js.ftl">
<#include "chart-bar3.js.ftl">

<#-- Rendering of the individual charts from a shared macro. -->
<#macro chartFunction chart>
    <#assign type>${chart.getType().toString()}</#assign>
    <#switch type>
      <#case "PIE">
        <@pieChart chart />
        <#break>
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
    </div>
</#macro>

<#-- And this, finally, is the HTML source. -->
<html>
    <head>
        <title>Vizualizace Zonky loanbooku</title>
        <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
        <script>
            function getImgData(chartContainer) {
                var chartArea = chartContainer.getElementsByTagName('iframe')[0]
                                    .contentDocument.getElementById('chartArea');
                var svg = chartArea.innerHTML;
                var doc = chartContainer.ownerDocument;
                var canvas = doc.createElement('canvas');
                canvas.setAttribute('width', chartArea.offsetWidth);
                canvas.setAttribute('height', chartArea.offsetHeight);
                canvas.setAttribute('style',
                    'position: absolute; ' +
                    'top: ' + (-chartArea.offsetHeight * 2) + 'px;' +
                    'left: ' + (-chartArea.offsetWidth * 2) + 'px;');
                doc.body.appendChild(canvas);
                canvg(canvas, svg);
                var imgData = canvas.toDataURL("image/png");
                canvas.parentNode.removeChild(canvas);
                return imgData;
            }

            function saveAsImg(chartContainer) {
                var imgData = getImgData(chartContainer);

                // Replacing the mime-type will force the browser to trigger a download
                // rather than displaying the image in the browser window.
                window.location = imgData.replace("image/png", "image/octet-stream");
            }

            function toImg(chartContainer, imgContainer) {
                var doc = chartContainer.ownerDocument;
                var img = doc.createElement('img');
                img.src = getImgData(chartContainer);

                while (imgContainer.firstChild) {
                  imgContainer.removeChild(imgContainer.firstChild);
                }
                imgContainer.appendChild(img);
            }
        </script>
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
            <hr />
        </#list>
    </body>
</html>
