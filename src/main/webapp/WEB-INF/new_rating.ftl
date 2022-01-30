<html xmlns="http://www.w3.org/1999/html">
<head><title>CarSharer</title>
	<link rel="stylesheet" href="/res?template.css">
</head>

<body>
	<div id="wrapper">
		<div id="header">
		<h1> CarSharer Website </h1>
		</div>
	   
		<div id="site">
			<h3>Bewertung für ${fahrer}:</h3>
			<form action="new_rating" method="post">
				<input type="hidden" name="fid" value="${fid}">
				<input type="hidden" name="nid" value="${nid}">
				<table>
					<tr>
						<td><label>Textnachricht:</label></td>
						<td>
							<textarea name="msg" maxlength="50" minlength="1" required></textarea>
						</td>
					</tr>
					<tr>
						<td><label>Bewertung:</label></td>
						<td>
							<input type="radio" name="rating" value="1">1
							<input type="radio" name="rating" value="2">2
							<input type="radio" name="rating" value="3">3
							<input type="radio" name="rating" value="4">4
							<input type="radio" name="rating" value="5">5
						</td>
					</tr>
					<tr>
						<td><input type="submit" value="Bewertung abgeben"></td>
					</tr>
				</table>
			</form>
		</div>
	</div>
</body>
</html>
