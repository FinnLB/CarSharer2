<html xmlns="http://www.w3.org/1999/html">
<head><title>CarSharer</title>
	<link rel="stylesheet" href="/res?template.css">
	<link rel="stylesheet" href="/res?view_drive.css">
	<style>
		.border {
			border: 5px solid black;
			padding-bottom: 2px;
			padding-left: 5px;
			padding-top: 2px;
			margin: 20px;
		}
	</style>
</head>

<body>
	<div id="wrapper">
		<input type="hidden" name="kunden_id" value="${kunden_id}">
		<div id="header">
		<h1>CarSharer Website</h1>
		</div>

		<div id="subheader">
			<h1>Aktionen</h1>
		</div>
		<div id="site">
			<!-- Neue Fahrt erstellen Button -->
			<form method="get" action="new_drive">
				<input type="hidden" name="kunden_id" value="${kunden_id}">
				<input type="submit" value="Neue Fahrt erstellen">
			</form><br>

			<!-- Suche anzeigen Button -->
			<form method="get" action="view_search">
				<input type="hidden" name="kunden_id" value="${kunden_id}">
				<input type="submit" value="Suche anzeigen">
			</form><br>

			<!-- Bonus anzeigen Button -->
			<form method="get" action="bonus">
				<input type="hidden" name="kunden_id" value="${kunden_id}">
				<input type="submit" value="Bonus anzeigen">
			</form>

			<div id="subheader">
				<h1>Meine Reservierten Fahrten</h1>
			</div>
			<div id="reservierte_fahrten">
				${res_list}
			</div>
			<div id="subheader">
				<h1>Offene Fahrten</h1>
			</div>
			<div id="offene_fahrten">
				${open_list}
			</div>
		</div>
	</div>
</body>
</html>
