<#macro chartFunctionName chart>draw${chart.getType()}Chart${chart.getId()}</#macro>
<#macro htmlId chart>chart-${chart.getType()}-${chart.getId()}</#macro>
<#macro htmlIdInteractive chart><@htmlId chart />-interactive</#macro>
<#macro htmlIdPng chart><@htmlId chart />-png</#macro>

<#include "chart-pie.js.ftl">
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

