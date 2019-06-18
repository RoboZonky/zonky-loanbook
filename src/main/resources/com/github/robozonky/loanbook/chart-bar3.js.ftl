<#macro barChart3 chart>
    function <@chartFunctionName chart />() {
        var data = google.visualization.arrayToDataTable([
            <#list chart.getAdaptedAxisLabels()>
                [<#items as point>'${point}'<#if point_has_next>, </#if></#items>],
            </#list>
            <#list chart.getAdaptedData()>
                <#items as point>
                   ['${point._1()}',
                    <#list point._2()><#items as point2>${point2?c}<#if point2_has_next>, </#if></#items></#list>
                    <#if point_has_next>], <#else>]</#if>
                </#items>
            </#list>
        ]);
        var options = {
            chart: {
                title: '${chart.getTitle()}',
                subtitle: '${chart.getSubtitle()}'
            },
            <#if chart.getType().toString() == "BAR">bars: 'horizontal',</#if>
            axes: {
                <#if chart.getType().toString() == "BAR">x<#else>y</#if>: {
                  0: {label: '${chart.getLabelForZ()}'}
                },
            },
            <#if chart.isRatingsAsSeries()><#include "ratings.js.ftl">,</#if>
            isStacked: ${chart.isStacked()?c}
        };
        var chart_div = document.getElementById('<@htmlIdInteractive chart />');
        var chart = new google.charts.Bar(chart_div);

        chart.draw(data, google.charts.Bar.convertOptions(options));
    }
</#macro>
