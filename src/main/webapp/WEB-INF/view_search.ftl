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
				<table>
					<tr>
						<td><label>Von:</label></td>
						<td><input type="text" id="from"></td>
					</tr>
					<tr>
						<td><label>Nach:</label></td>
						<td><input type="text" id="to"></td>
					</tr>
					<tr>
						<td><label>Fahrtdatum:</label></td>
						<td><input type="date" id="date"></td>
					</tr>
					<tr>
						<td><button onclick="run_search()" style="width: 100%">Search</button></td>
					</tr>
				</table>
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
<script>
	var from = document.getElementById("from")
	var to = document.getElementById("to")
	var date = document.getElementById("date")

	function run_search(){
		location.href = location.protocol + "//" + location.hostname + ":" + location.port + "/view_search?from=" + from.value + "&to=" + to.value + "&date=" + date.value; // redirect to link
	}

	function watch(k_id, f_id){
		location.href = location.protocol + "//" + location.hostname + ":" + location.port + "/view_drive?kunden_id=" + parseInt(k_id, 10) +"&fahrt_id=" + parseInt(f_id, 10);
	}
</script>
</body>
</html>
