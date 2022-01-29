<html xmlns="http://www.w3.org/1999/html">
<head>
	<title>CarSharer Search ${query}</title>
	<link rel="stylesheet" href="/res?template.css">
	<link rel="stylesheet" href="/res?search.css">
</head>

<body>
	<div id="wrapper">
		<div id="header">
		<h1>CarSharer Search</h1>
		</div>
		<form method="get" action="view_main"><input type="submit" value="Go to main"></form><br>
	   
		<div id="site">
			<div id="search">
				<form action="view_search" method="get">
					<table style="min-width: 40%">
						<tr>
							<td><label>Von:</label></td>
							<td><input type="text" name="from" value="${from}"></td>
						</tr>
						<tr>
							<td><label>Nach:</label></td>
							<td><input type="text" name="to" value="${to}"></td>
						</tr>
						<tr>
							<td><label>Fahrtdatum:</label></td>
							<td><input type="date" name="date" value="${date}"></td>
						</tr>
						<tr>
							<td><input type="submit" style="width: 200%" value="Suchen"></td>
						</tr>
					</table>
				</form>
				<br>
			</div>
			<div id="results">
				<div class="separator">
					<h2>${query}</h2>
				</div>
				${results}
			</div>
		</div>
	</div>
</body>
</html>
