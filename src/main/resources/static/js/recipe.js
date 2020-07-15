console.log("loaded recipe.js!")

insertCopyToClipboardButton()

//copyInstructionsToClipboard()

function insertCopyToClipboardButton() {
    document.querySelector("#recipe-card-container").insertAdjacentHTML("afterbegin",
        `
        <div class='copy-to-clipboard-wrapper' onclick='document.execCommand("copy")' title="Copy instructions to clipboard">
            <ion-icon name="copy-outline"></ion-icon>
        </div>
        `);

    document.addEventListener('copy', overrideCopyEvent)
}

function overrideCopyEvent(event) {
    event.clipboardData.setData('text/plain', 'Hello, world!');
    displayCopiedSymbol()
    event.preventDefault();
}

function displayCopiedSymbol() {
    console.log("display copy symbol called")
}