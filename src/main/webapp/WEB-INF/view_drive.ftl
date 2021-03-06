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
		<!-- Zur Hauptseite Button -->
		<form method="get" action="view_main">
			<input type="hidden" value="${kunden_id}" name="kunden_id">
			<input type="submit" value="Zur Hauptseite">
		</form><br>
		<div id="subheader">
		<h1> Informationen </h1>
		</div>
	   
		<div id="data">
		<p>
			<img src="${transportmittel_icon_path}" alt="image of an ${transportmittel}">
			<ul>
				<li>Anbieter: ${anbieter}</li>
				<li>email: ${anbieter_email}</li>
				<li>Fahrtdatum und -uhrzeit: ${datetime}</li>
				<li>Von: ${start}</li>
				<li>Nach: ${ziel}</li>
				<li>Anzahl freier Plätze: ${nfreiePlaetze}</li>
				<li>Status: ${status}</li>
				<li>Transportmittel: ${transportmittel}</li>
				<li>kosten: ${kosten} EUR</li>
				<li>Beschreibung: ${description}</li>
			</ul>
		</p>
		</div>

		<div id = "subheader"><h1>Aktionsleiste</h1></div>
		<div id="data">
			<form method="post" action="view_drive?kunden_id=${kunden_id}&fahrt_id=${fahrt_id}">
				<input type="hidden", name="type", value="reserve">
				<input type="hidden" name="kunden_id" value="${kunden_id}">
				<input type="hidden" name="fahrt_id" value="${fahrt_id}">
				${aktion_res}

			</form>
			<form method="post" action="view_drive?kunden_id=${kunden_id}&fahrt_id=${fahrt_id}">
				<input type="hidden", name="type", value="delete">
				<input type="hidden" name="kunden_id" value="${kunden_id}">
				<input type="hidden" name="fahrt_id" value="${fahrt_id}">
				${delete_drive}
			</form>

		</div>

		<div id = "subheader"><h1>Bewertungen</h1>
			Durchschnittsrating: ${averageRating}<br>
			<a href="new_rating?fid=${fahrt_id}&nid=${kunden_id}">BEWERTEN</a>
		</div>
		<div id="data">
			<p>
				<table>
				<tr>
					<th>email</th>
					<th>textnachricht</th>
					<th>rating</th>
				</tr>
				${ratings_tabledata}
				</table>
			</p>
		</div>
	</div>
</body>
</html>
