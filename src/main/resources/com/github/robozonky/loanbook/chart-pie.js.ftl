<#macro pieChart chart>
    function <@chartFunctionName chart />() {
        var data = google.visualization.arrayToDataTable([
            <#list chart.getData()>
                ['${chart.getLabelForX()}', '${chart.getLabelForY()}'],
                <#items as point>
                    ['${point._1()}', ${point._2()?c}]<#if point_has_next>,</#if>
                </#items>
            </#list>
        ]);
        var options = {
            title: '${chart.getTitle()}',
            subtitle: '${chart.getSubtitle()}'
        };
        var chart_div = document.getElementById('<@htmlIdInteractive chart />');
        var chart = new google.visualization.PieChart(chart_div);

        google.visualization.events.addListener(chart, 'ready', function () {
            document.getElementById('<@htmlIdPng chart />').outerHTML =
            '<a href="' + chart.getImageURI() + '">Ke stažení</a>';
        });

        chart.draw(data, options);
    }
</#macro>
