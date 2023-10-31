("use strict");
//Global variables
var copyValue = "";
var pasteElement = null;
var isInfoShowed = false;
var button = null;
var textarea = null;
$(document).ready(function () {
  button = document.getElementById("showsaveinfo");
  pasteElement = document.getElementById("paste");
  const listContainer = document.getElementById("listContainer");
  textarea = document.getElementById("feedtextdata");

  // this is used  to load saved text file from system
  $("#viewFolder").on("click", "#chooseFolder", function () {
    var folderName = $(this).text();
    if (!folderName.includes(".txt")) {
      $.ajax({
        url: "/inside/" + folderName,
        success: function (cObj) {
          $("#viewFolder").html(cObj);
        },
        error: function (data) {},
      });
    } else {
      var encodedURL = encodeURIComponent(folderName);
      $.ajax({
        url: "/feedtext/" + encodedURL,
        success: function (cObj) {
          $("#feedtextdata").val(cObj);
          document.getElementById("closeButton").click();
        },
        error: function (data) {},
      });
    }
  });

  //used in file system to move back to parent folder
  $("#backButton").on("click", function () {
    $.ajax({
      url: "/back",
      success: function (cObj) {
        $("#viewFolder").html(cObj);
      },
      error: function (data) {},
    });
  });

  //save typed data to backend
  $("#saveButton").on("click", saveToBackend);

  //able to get which option is slected inside of list of choice
  listContainer.addEventListener("click", (event) => {
    if (event.target.classList.contains("list-item")) {
      const selected = event.target.getAttribute("data-option");
      getCursorPosition(selected, textarea);
      listContainer.style.display = "none";
    }
  });

  document.addEventListener("contextmenu", (event) => {
    event.preventDefault(); // Prevent the default context menu
    // Position the context div at the mouse click location
    if (isInfoShowed) {
      hideActionInfo();
    }
    listContainer.style.display = "block";
    listContainer.focus();
    listContainer.style.top = `${event.clientY}px`;
    listContainer.style.left = `${event.clientX}px`;
    listContainer.scrollTop = 0;
  });

  document.addEventListener("click", () => {
    listContainer.style.display = "none";
  });

  document.addEventListener("keydown", function (event) {
    switch (event.key) {
      case "s":
      case "S":
        if (event.ctrlKey) {
          event.preventDefault();
          saveToBackend();
        }
        break;
      case "c":
      case "C":
        if (event.ctrlKey) {
          showActionInfo("copied");
        }
        break;
      case "a":
      case "A":
        if (event.ctrlKey) {
          showActionInfo("Selected All");
        }
        break;
      case "v":
      case "V":
        if (event.ctrlKey) {
          showActionInfo("pasted");
        }
        break;

      default:
        if (isInfoShowed) {
          hideActionInfo();
        }
        break;
    }
  });
});

function getCursorPosition(value, textarea) {
  var textToInsert = "";
  textarea.focus();
  switch (value) {
    case "d": //date
      textToInsert = getdate();
      break;
    case "t": //time
      textToInsert = getTime();
      break;
    case "b": //bold possible available to textara tag
      textToInsert = getBold(textarea);
      break;
    case "s": // sybols
      textToInsert = addSymbolsONSelectedText("* ", textarea);
      break;
    case "n": //add number
      textToInsert = addNumbersONSelectedText(textarea);
      break;
    case "m": //move next
      textToInsert = addSymbolsONSelectedText("  ", textarea);
      break;
    case "f": //tommorow
      textToInsert = " [ WORKED SHIFTED TO TOMMOROW ] ";
      break;
    case "o": //small task
      textToInsert = "Topic <-> [TASK INFO - ]*";
      break;
    case "os": //big task
      textToInsert = "Topic <-> [TASK INFO - ] [DAY - ]*";
      break;
    case "cl": //clear
      textarea.value = "";
      return;
    case "co": //copy
      copyValue = copy(textarea);
      pasteElement.style.display = "block";
      showActionInfo("Copied");
      return;
    case "pa": //paste
      textToInsert = copyValue;
      showActionInfo("Pasted");
      break;
    case "sa": //save
      saveToBackend();
      break;
    case "lp": //load previous data
      loacPreviousData();
      break;
    case "br": //bracket
      textToInsert = " [  ] ";
      break;

    default:
      break;
  }

  // Get the current cursor position
  const startPos = textarea.selectionStart;
  const endPos = textarea.selectionEnd;
  const currentValue = textarea.value;

  const newValue =
    currentValue.substring(0, startPos) +
    `${textToInsert}` +
    currentValue.substring(endPos);

  // Update the textarea value
  textarea.value = newValue;

  // Set the cursor position after the inserted text
  textarea.selectionStart = startPos + textToInsert.length;
  textarea.selectionEnd = startPos + textToInsert.length;
}

//used to show warning before reload
window.onbeforeunload = function (e) {
  var dialogText = "Are you sure you want to close the Window?";
  e.returnValue = dialogText;
  return dialogText;
};

function getdate() {
  const today = new Date();
  const year = today.getFullYear();
  const month = today.getMonth() + 1; // Months are zero-based (0 = January, 11 = December)
  const day = today.getDate();
  const formattedDate = ` [ ${year}-${month.toString().padStart(2, "0")}-${day
    .toString()
    .padStart(2, "0")} ] `;
  return formattedDate;
}

function getTime() {
  const today = new Date();
  const hours = today.getHours();
  const minutes = today.getMinutes();
  const ampm = hours >= 12 ? "PM" : "AM";
  // Convert 24-hour time to 12-hour time
  const twelveHourFormat = hours % 12 || 12;
  const formattedTime = ` [ ${twelveHourFormat}:${minutes
    .toString()
    .padStart(2, "0")} ${ampm} ] `;
  return formattedTime;
}

function addSymbolsONSelectedText(symbol, textarea) {
  const selectedText = textarea.value.substring(
    textarea.selectionStart,
    textarea.selectionEnd
  );
  // Split the selected text into lines
  const lines = selectedText.split("\n");
  // Add a bullet point to each line
  const bulletedLines = lines
    .map((line) => {
      return `${symbol}${line}`; // Add a bullet symbol to subsequent lines
    })
    .join("\n");
  return bulletedLines;
}

function addNumbersONSelectedText(textarea) {
  const selectedText = textarea.value.substring(
    textarea.selectionStart,
    textarea.selectionEnd
  );
  // Split the selected text into lines
  const lines = selectedText.split("\n");
  // Add a bullet point to each line
  const bulletedLines = lines
    .map((line, index) => `${index + 1}. ${line}`)
    .join("\n");
  return bulletedLines;
}

function getBold(textarea) {
  const selectedText = textarea.value.substring(
    textarea.selectionStart,
    textarea.selectionEnd
  );
  const boldText = `<b>${selectedText}</b>`;
  return boldText;
}

function copy(textarea) {
  const selectedText = textarea.value.substring(
    textarea.selectionStart,
    textarea.selectionEnd
  );
  return selectedText;
}

function showActionInfo(message) {
  button.textContent = message;
  button.style.opacity = `1`;
  isInfoShowed = true;
}

function hideActionInfo() {
  button.style.opacity = `0.8`;
  button.style.opacity = `0`;
  isInfoShowed = false;
}

function loacPreviousData() {
  $.ajax({
    url: "/create",
    success: function (cObj) {
      $("#feedtextdata").val(cObj);
      showActionInfo("Loaded Previous File");
    },
    error: function (data) {},
  });
}

function saveToBackend() {
  var saveClassObject = {
    savingDirectory: $("#filelocationinput").val(),
    parentFolderName: $("#parentFolder").val(),
    subFolderName: $("#childFolder").val(),
    fileName: $("#fileNameinput").val(),
    textData: $("#feedtextdata").val(),
    save: $("#SaveORnotinput option:selected").val(),
  };
  $.ajax({
    url: "/save",
    method: "POST",
    contentType: "application/json",
    data: JSON.stringify(saveClassObject),
    success: function (cObj) {
      $("#feedtextdata").val(cObj);
      showActionInfo("Saved");
    },
    error: function (data) {},
  });
}
