<html xmlns="http://www.w3.org/1999/html">
<head>
    <title>CarSharer</title>
    <link rel="stylesheet" href="/res?template.css">
</head>
<body>
<div id="wrapper">
    <div id="header">
        <h1>Offene Fahrten des "besten Fahrers"</h1>
    </div>

    <div id="site">
        <!-- Zur Hauptseite Button -->
        <form action="view_main" method="get">
            <input type="submit" value="Zur Hauptseite">
        </form><br>

        Fahrer: ${driver}<br>
        Durchschnittsrating: ${average_rating}<br><hr>
        <#list drives as drive>
            <div class="drive">
                Fahrt-Id: ${drive.fid}<br>
                Von: ${drive.startort}<br>
                Nach: ${drive.zielort}
            </div>
        </#list>
    </div>
</div>
</body>
</html>
