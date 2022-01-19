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
	   
		<div id="site">
			<div id="search">
				<label>
					Von:
					<input type="text">
				</label>
				<label>
					Bis:
					<input type="text">
				</label>
				<label>
					Fahrtdatum:
					<input type="date">
				</label>
				<br>
				<button onclick="run_search">Search</button>
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
