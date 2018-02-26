function showElement(id) {
	var divElement = document.getElementById(id);
	v = divElement.style.display;
	divElement.style.display = v == "inline" ? "none" : "inline"
}

function showElements(elemIds) {
	var atLeastOneCollapsed = false;
	for(var i = 0; i < elemIds.length; i++) {
		if(document.getElementById(elemIds[i]).style.display == "none") {
			atLeastOneCollapsed = true;
		}
	}

	if(atLeastOneCollapsed == true) {
		for(var i = 0; i < elemIds.length; i++) {
			document.getElementById(elemIds[i]).style.display = "inline";
		}
	} else {
		for(var i = 0; i < elemIds.length; i++) {
			document.getElementById(elemIds[i]).style.display = "none";
		}
	}
}