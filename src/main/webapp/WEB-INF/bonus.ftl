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
        Fahrer: ${driver}<br>
        Durchschnittsrating: ${average_rating}<br>
        <#list drives as drive>

        </#list>
    </div>
</div>
</body>
</html>
