// Globals
const selected = {}; // format: { USERNAME: { usernameOptionEl: Node, cancelEl: Node, hiddenInputEl: NodeList } }
const multiselect = document.querySelector('.multiselect');
const form = document.querySelector('.multiselect-form');
const selectedWrapper = document.querySelector('.multiselect-selected');
const options = form.querySelectorAll('.friend-option');
const inviteFriendsForms = document.querySelectorAll('.invite-friends-form');

onLoad();

function onLoad() {
    multiselect.addEventListener('change', onMultiselectChange);

    const urlSearchParams = new URLSearchParams(window.location.search);
    const usernames = [...options].map(option => {
        return option.value;
    });

    if (urlSearchParams.has('friends')) {
        const query = urlSearchParams.getAll('friends');
        query.forEach(param => {
            if (usernames.includes(param)) {
                const usernameOptionEl = disabledUsernameOption(param);
                const hiddenInputEl = createHiddenInput(param);
                const cancelEl = createCancelElement(param);
                setSelected(param, usernameOptionEl, cancelEl, hiddenInputEl);
            }
        });
    }
}

function onMultiselectChange(e) {
    const username = e.target.value;
    const usernameOptionEl = disabledUsernameOption(username);
    const hiddenInputEl = createHiddenInput(username);
    const cancelEl = createCancelElement(username);
    setSelected(username, usernameOptionEl, cancelEl, hiddenInputEl);
}

function onSelectedCancel(selectedUserName) {
    const selectElement = selected[selectedUserName];

    if (selectElement) {
        const {usernameOptionEl, cancelEl, hiddenInputEl} = selectElement;
        usernameOptionEl.removeAttribute('disabled');
        cancelEl.remove();
        hiddenInputEl.forEach(node => {
            node.remove();
        });
        delete selected[selectedUserName];
        multiselect.removeAttribute('disabled');
    }
}

function createHiddenInput(username) {
    const inputElements = [];

    const createHiddenInputEl = (inputUsername) => {
        const hiddenInputEl = document.createElement('input');
        hiddenInputEl.setAttribute('type', 'hidden');
        hiddenInputEl.setAttribute('name', 'friends');
        hiddenInputEl.setAttribute('value', inputUsername);
        return hiddenInputEl;
    };

    const formInput = createHiddenInputEl(username);
    inputElements.push(formInput);
    form.insertAdjacentElement('beforeend', formInput);

    inviteFriendsForms.forEach(node => {
        const hiddenInputExisting = node.querySelector(`input[value="${username}"]`);
        if (!hiddenInputExisting) {
            const inviteFormInput = createHiddenInputEl(username);
            inputElements.push(inviteFormInput);
            node.insertAdjacentElement('beforeend', inviteFormInput);
        } else {
            inputElements.push(hiddenInputExisting)
        }
    });
    return inputElements;
}

function createCancelElement(username) {
    const cancelEl = document.createElement('div');
    cancelEl.setAttribute('onclick', `onSelectedCancel('${username}')`);
    cancelEl.textContent = `${username} x`;
    selectedWrapper.insertAdjacentElement('beforeend', cancelEl);
    return cancelEl;
}

function disabledUsernameOption(username) {
    const usernameOptionEl = form.querySelector(`option[value="${username}"]`);
    usernameOptionEl.setAttribute('disabled', '');
    return usernameOptionEl;
}

function setSelected(username, usernameOptionEl, cancelEl, hiddenInputEl) {
    selected[username] = {usernameOptionEl, cancelEl, hiddenInputEl};
    multiselect.value = "";
    if (options.length === Object.keys(selected).length) {
        multiselect.setAttribute('disabled', '');
    }
}