export function updateProgressBar() {
    var progressBar = document.getElementById('progress-bar');
    let totalAnswers = 7;
    var correctPercentage = (correctAnswers / totalAnswers) * 100;
    console.log(correctPercentage)
    progressBar.style.width = correctPercentage + '%';
}