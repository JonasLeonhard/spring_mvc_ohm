let selected = false;
let onCopyTimeout = false;
let instructionsToClipboard = "";

insertCopyToClipboardButton();

function insertCopyToClipboardButton() {
    document.querySelector("#buyList-copy-wrapper").insertAdjacentHTML("afterbegin",
        `
        <div class='copy-to-clipboard-wrapper' onclick='onCopyClick()' title="Copy ingredients to clipboard">
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
    document.querySelectorAll(".buyList-card").forEach(node => {
        instructionsToClipboard += node.querySelector(".recipe-title").innerText + ":\n";

        node.querySelectorAll(".recipe-ingredient").forEach(node => {
            if (!node.querySelector("input").checked) {
                const labelDiv = node.querySelector("label");
                const amount = labelDiv.querySelector(".amount").innerText;
                const unit = labelDiv.querySelector(".unit").innerText;
                const name = labelDiv.querySelector(".name").innerText;

                instructionsToClipboard += `${amount} ${unit} ${name}\n`
            }
        });
        instructionsToClipboard += "\n\n"
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