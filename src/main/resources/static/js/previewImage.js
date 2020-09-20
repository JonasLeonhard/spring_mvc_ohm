document.querySelector("#preview-img-input").addEventListener('change', onChangePreviewImg);

function onChangePreviewImg(e) {
    const reader = new FileReader();
    reader.readAsDataURL(document.querySelector("#preview-img-input").files[0]);

    reader.onload = function (e) {
        document.querySelector("#preview-img").src = e.target.result;
    };
}