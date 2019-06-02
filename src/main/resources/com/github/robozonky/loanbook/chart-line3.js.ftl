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
            },
            series: {
                0: { color: '#c0498b'},
                1: { color: '#8b59be'},
                2: { color: '#596abe'},
                3: { color: '#599ebe'},
                4: { color: '#5abfa0'},
                5: { color: '#67cd75'},
                6: { color: '#91c95a'},
                7: { color: '#cebe5a'},
                8: { color: '#d7954b'},
                9: { color: '#e75637'},
                10: { color: '#d12f2f'}
            }
        };
        var chart_div = document.getElementById('<@htmlIdInteractive chart />');
        var chart = new google.charts.Line(chart_div);

        chart.draw(data, google.charts.Line.convertOptions(options));
    }
</#macro>
