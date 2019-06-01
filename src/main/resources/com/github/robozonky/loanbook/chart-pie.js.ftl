<#macro pieChart chart>
    function drawPieChart${chart.getId()}() {
        var data = google.visualization.arrayToDataTable([
            <#list chart.getData()>
                ['${chart.getLabelForX()}', '${chart.getLabelForY()}'],
                <#items as point>
                    ['${point._1()}', ${point._2()?c}]<#if point_has_next>,</#if>
                </#items>
            </#list>
        ]);
        var options = {
            title: '${chart.getTitle()}'
        };
        var chart_div = document.getElementById('chart-pie-${chart.getId()}-interactive');
        var chart = new google.visualization.PieChart(chart_div);

        google.visualization.events.addListener(chart, 'ready', function () {
            document.getElementById('chart-pie-${chart.getId()}-png').outerHTML =
            '<a href="' + chart.getImageURI() + '">Ke stažení</a>';
        });

        chart.draw(data, options);
    }
</#macro>
<#macro pieChartMeta chart>
    <div id="chart-pie-${chart.getId()}">
        <div id="chart-pie-${chart.getId()}-interactive" style="width: 640px; height: 480px;"></div>
        <div id='chart-pie-${chart.getId()}-png'></div>
    </div>
</#macro>

