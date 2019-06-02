<#include "shared.ftl">
google.charts.load('current', {'packages':['bar', 'corechart']});
<#list data.charts as chart>
  <@chartFunction chart />
  google.charts.setOnLoadCallback(<@chartFunctionName chart />);
</#list>
