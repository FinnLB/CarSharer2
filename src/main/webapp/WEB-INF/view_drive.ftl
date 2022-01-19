<html xmlns="http://www.w3.org/1999/html">
<head>
	<title>CarSharer</title>
	<link rel="stylesheet" href="/res?template.css">
	<link rel="stylesheet" href="/res?view_drive.css">
</head>

<body>
	<div id="wrapper">
		<div id="header">
			<h1>Fahrt ansehen</h1>
		</div>
		<div id="subheader">
		<h1> Informationen </h1>
		</div>
	   
		<div id="data">
		<p>
			<ul>
				<li>Anbieter: ${anbieter}</li>
				<li>Fahrtdatum und -uhrzeit: ${datetime}</li>
				<li>Von: ${start}</li>
				<li>Nach: ${ziel}</li>
				<li>Anzahl freier Plätze: ${nfreiePlaetze}</li>
				<li>Status: ${status}</li>
				<li>Beschreibung: ${description}</li>
			</ul>
		</p>
		</div>

		<div id = "subheader"><h1>Aktionsleiste</h1></div>
		<div id="data">
			<p>
				Anzahl Plaetze fuer Reservierung: <input type="number" id="resplaetze" name="resplaetze" min="1" max="2"> <br>
				<button> Fahrt reservieren</button>   <button> Fahrt löschen</button>
			</p>
		</div>

		<div id = "subheader"><h1>Bewertungen</h1>  Durchschnittsrating: ${averageRating}</div>
		<div id="data">
			<p>
				<table>
				<tr>
					<th>email</th>
					<th>beschreibung</th>
					<th>rating</th>
				</tr>
				<tr>
					<th>n.fuhr@carSharer.de</th>
					<th>Sehr angenehme Fahrt!</th>
					<th>5</th>
				</tr>
				<!-- insert date from TODO here, in as many rows as needed -->
				</table>
			</p>
		</div>
	</div>
</body>
</html>
