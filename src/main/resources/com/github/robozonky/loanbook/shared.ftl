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
