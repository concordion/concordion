window.addEventListener('load', ready, false);

// Uncomment any embedded elements which were commented out so as not to interfere with execution of the Concordion commands
function ready(){
  var tutorialElements = document.querySelectorAll('.toggle.rendered');
  uncomment(tutorialElements);
  hide(tutorialElements);
}

function toggle(b) {
  var tutorialElements = document.querySelectorAll('.toggle.rendered');
  var specElements = document.querySelectorAll('.toggle.source');
  if (b.value == 'View as Rendered') {
    b.value='View as HTML';
    show(tutorialElements);
    hide(specElements);
  } else {
    b.value='View as Rendered';
    show(specElements);
    hide(tutorialElements);
  }
}

function uncomment(elements) {
  for (i = 0; i < elements.length; i++) {
	var element = elements[i];
	var text = element.innerHTML;
	if (text) {
	  newText = text.replace('<!--', '').replace('-->', '');
	  element.innerHTML = newText;
	}
  }
}

function show(elements) {
  for (i = 0; i < elements.length; i++) {
	var element = elements[i];
    element.className = element.className.replace("hide", "");
    element.className += " show";
  }
}

function hide(elements) {
  for (i = 0; i < elements.length; i++) {
	var element = elements[i];
    element.className = element.className.replace("show", "");
    element.className += " hide";
  }
};
