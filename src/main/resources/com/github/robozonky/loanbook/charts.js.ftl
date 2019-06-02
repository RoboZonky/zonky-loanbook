<#include "shared.ftl">
google.charts.load('current', {'packages':['bar', 'line', 'corechart']});
<#list data.charts as chart>
  <@chartFunction chart />
  google.charts.setOnLoadCallback(<@chartFunctionName chart />);
</#list>
