document.addEventListener("DOMContentLoaded", () => {
    console.log("loaded popup")
})

document.getElementById("returnButton").addEventListener("click", () => {
    // if (eventSource!= undefined) {
    //     eventSource.close()
    // }
    window.location.href = "../index.html"; // Replace with the actual file for spot selection
});
