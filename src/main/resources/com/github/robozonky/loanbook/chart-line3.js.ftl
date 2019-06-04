<#macro lineChart3 chart>
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
            axes: {
                y: {
                  0: {label: '${chart.getLabelForZ()}'}
                },
            }
            <#if chart.isRatingsAsSeries()>,<#include "ratings.js.ftl"></#if>
        };
        var chart_div = document.getElementById('<@htmlIdInteractive chart />');
        var chart = new google.charts.Line(chart_div);

        chart.draw(data, google.charts.Line.convertOptions(options));
    }
</#macro>
