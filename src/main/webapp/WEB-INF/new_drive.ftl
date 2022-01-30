<html xmlns="http://www.w3.org/1999/html">
<head>
	<title>CarSharer</title>
	<link rel="stylesheet" href="/res?template.css">
</head>
<body>
	<div id="wrapper">
		<div id="header">
		<h1>Fahrt erstellen</h1>
		</div>
	   
		<div id="site">
			<!-- Zur Hauptseite Button -->
			<form method="get" action="view_main">
				<input type="hidden" value="${kunden_id}" name="kunden_id">
				<input type="submit" value="Zur Hauptseite">
			</form><br>
		<form method="post" action="new_drive">
			<input type="hidden" value="${kunden_id}" name="kunden_id">
			<label>
				Von:
				<input type="text" name="von">
			</label><br>
			<label>
				Bis:
				<input type="text" name="bis">
			</label><br>
			<label>
				Maximale Kapazität:
				<input type="number" name="kapazitaet" min="0" max="10" required>
			</label><br>
			<label>
				Fahrtkosten:
				<input type="number" name="kosten" min="0" required>
			</label><br>
			Transportmittel:
			<#list vehicles as vehicle>
				<label>
					<input type="radio" value="${vehicle.tid}" name="transportmittel">
					${vehicle.name}
				</label>
			</#list>
			<br>
			<label>
				Fahrtdatum:
				<input type="datetime-local" name="fahrtdatumzeit" id="fahrtdatum" required>
			</label><br>
			<label>
				Beschreibung:
				<textarea name="beschreibung" maxlength="50"></textarea>
			</label><br>
			<input type="submit" value="Fahrt erstellen">
		</form>
		</div>
	</div>
<script>
	console.log(new Date().toISOString().split(".")[0].substring(0, 16));
	document.getElementById("fahrtdatum").min = new Date().toISOString().split(".")[0].substring(0, 16);
</script>
</body>
</html>
