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
		<form method="post" action="new_drive">
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
				<input type="number" name="kapazitaet">
			</label><br>
			<label>
				Fahrtkosten:
				<input type="text" name="kosten">
			</label><br>
			Transportmittel:
			<label>
				<input type="radio" value="Auto" name="transportmittel">
				Auto
			</label>
			<label>
				<input type="radio" value="Bus" name="transportmittel">
				Bus
			</label>
			<label>
				<input type="radio" value="Kleintransporter" name="transportmittel">
				Kleintransporter
			</label>
			<br>
			<label>
				Fahrtdatum:
				<input type="datetime-local" name="fahrtdatum"> - <input type="time" name="fahrtuhrzeit">
			</label><br>
			<label>
				Beschreibung:
				<textarea></textarea>
			</label><br>
			<input type="submit" value="Fahrt erstellen">
		</form>
		</div>
	</div>
</body>
</html>
