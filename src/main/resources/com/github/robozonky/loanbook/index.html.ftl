<#include "chart-pie.js.ftl">
<html>
    <head>
        <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
        <script type="text/javascript">
          google.charts.load('current', {'packages':['corechart']});
          google.charts.setOnLoadCallback(drawCharts);

          <#list data.charts as chart>
            <@pieChart chart />
          </#list>

          function drawCharts() {
              <#list data.charts as chart>
                  drawPieChart${chart.getId()}();
              </#list>
          }
        </script>
    </head>
    <body>
        <#list data.charts as chart>
            <@pieChartMeta chart />
        </#list>
    </body>
</html>
