const challengeTimeObj = document.querySelector('#challenge-time');
let timeLeft = Number(challengeTimeObj.dataset.time);

setInterval(() => {
    timeLeft += 1;
    const hhmmss = new Date(timeLeft * 1000).toISOString().substr(11, 8)
    challengeTimeObj.innerHTML = hhmmss;
}, 1000);