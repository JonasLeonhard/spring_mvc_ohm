let selected = false;
let onCopyTimeout = false;
let instructionsToClipboard = "";

insertCopyToClipboardButton();

function insertCopyToClipboardButton() {
    document.querySelector("#recipe-card-container").insertAdjacentHTML("afterbegin",
        `
        <div class='copy-to-clipboard-wrapper' onclick='onCopyClick()' title="Copy instructions to clipboard">
            <ion-icon id="copy-symbol" name="copy-outline"></ion-icon>
        </div>
        `);
    setInstructionsText();
}

function onCopyClick() {
    const textarea = createCopyTextarea();
    document.body.appendChild(textarea);

    storeSelection();

    textarea.select();
    document.execCommand('copy');
    document.body.removeChild(textarea);

    restoreSelection();
    displayCopiedSymbol();
}

function setInstructionsText() {
    instructionsToClipboard += document.querySelector(".recipe-card-info-title").innerText + ":\n"
    document.querySelectorAll(".ingredient").forEach(node => {
        instructionsToClipboard += `${node.querySelector(".ingredient-stats").innerText}\n`
    });
}

function createCopyTextarea() {
    const textarea = document.createElement('textarea');
    textarea.value = instructionsToClipboard;
    textarea.setAttribute('readonly', '');
    textarea.style.position = 'absolute';
    textarea.style.left = '-9999px';
    return textarea;
}

function storeSelection() {
    selected = document.getSelection().rangeCount > 0
        ? document.getSelection().getRangeAt(0)
        : false;
}

function restoreSelection() {
    if (selected) {
        document.getSelection().removeAllRanges();
        document.getSelection().addRange(selected);
    }
}

function displayCopiedSymbol() {
    if (!onCopyTimeout) {
        console.log("display copy symbol called")
        const copySymbol = document.querySelector("#copy-symbol");
        copySymbol.setAttribute("name", "checkbox-outline");
        onCopyTimeout = true;
        setTimeout(function () {
            copySymbol.setAttribute("name", "copy-outline");
            onCopyTimeout = false;
        }, 5000);
    }
}