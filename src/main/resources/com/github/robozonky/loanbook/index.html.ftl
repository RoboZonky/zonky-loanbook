<#-- Basic macros to be used in charts. -->
<#macro chartFunctionName chart>draw${chart.getType()}Chart${chart.getId()}</#macro>
<#macro htmlId chart>chart-${chart.getType()}-${chart.getId()}</#macro>
<#macro htmlIdInteractive chart><@htmlId chart />-interactive</#macro>
<#macro htmlIdPng chart><@htmlId chart />-png</#macro>

<#-- Support for individual charts. -->
<#include "chart-pie.js.ftl">

<#-- Rendering of the individual charts from a shared macro. -->
<#macro chartFunction chart>
    <#assign type>${chart.getType().toString()}</#assign>
    <#switch type>
      <#case "PIE">
        <@pieChart chart />
        <#break>
      <#default>
        // nothing
    </#switch>
</#macro>
<#macro chartMeta chart>
    <div id="<@htmlId chart />">
        <div id="<@htmlIdInteractive chart />" style="width: 640px; height: 480px;"></div>
        <div id='<@htmlIdPng chart />'></div>
    </div>
</#macro>

<#-- And this, finally, is the HTML source. -->
<html>
    <head>
        <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
        <script type="text/javascript">
          google.charts.load('current', {'packages':['corechart']});
          <#list data.charts as chart>
              <@chartFunction chart />
              google.charts.setOnLoadCallback(<@chartFunctionName chart />);
          </#list>
        </script>
    </head>
    <body>
        <#list data.charts as chart>
            <@chartMeta chart />
        </#list>
    </body>
</html>
