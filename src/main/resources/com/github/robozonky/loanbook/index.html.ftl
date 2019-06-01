<#include "charts.js.ftl">
<html>
    <head>
        <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
        <script type="text/javascript">
          google.charts.load('current', {'packages':['corechart']});
          google.charts.setOnLoadCallback(drawCharts);

          <#list data.charts as chart>
            <@chartFunction chart />
          </#list>

          function drawCharts() {
              <#list data.charts as chart>
                  <@chartFunctionName chart />();
              </#list>
          }
        </script>
    </head>
    <body>
        <#list data.charts as chart>
            <@chartMeta chart />
        </#list>
    </body>
</html>
