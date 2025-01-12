let matchedSpot=0;
document.addEventListener("DOMContentLoaded", () => {
    const cityList = document.getElementById("cityList");
    const streetList = document.getElementById("streetList");
    const addressList = document.getElementById("addressList");
    const parkingSpotList = document.getElementById("parkingSpotList");

    const streetInput = document.getElementById("street");
    const addressInput = document.getElementById("address");
    const parkingSpotInput = document.getElementById("parkingSpot");

    // Initially hide dependent inputs
    streetInput.parentElement.style.display = "none";
    addressInput.parentElement.style.display = "none";
    parkingSpotInput.parentElement.style.display = "none";

    fetch('http://localhost:8080/api/parkings')
        .then(response => response.json())
        .then(apiResponse => {
            console.log(apiResponse);

            // Transform data for easier usage
            const transformedData = apiResponse.flatMap(parking =>
                parking.spots.map(spot => ({
                    city: parking.city,
                    street: parking.street,
                    address: parking.address,
                    spot: spot.spaceCode,
                    spotID: spot.id
                }))
            );

            function addOptionsToList(listElement, options) {
                listElement.innerHTML = '';
                options.forEach(option => {
                    const optionElement = document.createElement("option");
                    optionElement.value = option;
                    listElement.appendChild(optionElement);
                });
            }

            const cities = [...new Set(transformedData.map(entry => entry.city))];
            addOptionsToList(cityList, cities);

            document.getElementById("city").addEventListener("input", function () {
                const selectedCity = this.value;

                if (selectedCity) {
                    const streets = [...new Set(transformedData.filter(entry => entry.city === selectedCity).map(entry => entry.street))];
                    addOptionsToList(streetList, streets);

                    // Show street input and reset below inputs
                    streetInput.parentElement.style.display = "block";
                    addressInput.parentElement.style.display = "none";
                    parkingSpotInput.parentElement.style.display = "none";
                } else {
                    // Hide all dependent inputs
                    streetInput.parentElement.style.display = "none";
                    addressInput.parentElement.style.display = "none";
                    parkingSpotInput.parentElement.style.display = "none";
                }
            });

            document.getElementById("street").addEventListener("input", function () {
                const selectedStreet = this.value;
                const selectedCity = document.getElementById("city").value;

                if (selectedStreet) {
                    const addresses = [...new Set(transformedData.filter(entry => entry.city === selectedCity && entry.street === selectedStreet).map(entry => entry.address))];
                    addOptionsToList(addressList, addresses);

                    // Show address input and reset parking spot input
                    addressInput.parentElement.style.display = "block";
                    parkingSpotInput.parentElement.style.display = "none";
                } else {
                    // Hide address and parking spot inputs
                    addressInput.parentElement.style.display = "none";
                    parkingSpotInput.parentElement.style.display = "none";
                }
            });

            document.getElementById("address").addEventListener("input", function () {
                const selectedAddress = this.value;
                const selectedCity = document.getElementById("city").value;
                const selectedStreet = document.getElementById("street").value;

                if (selectedAddress) {
                    const parkingSpots = [...new Set(transformedData.filter(entry => entry.city === selectedCity && entry.street === selectedStreet && entry.address === selectedAddress).map(entry => entry.spot))];
                    addOptionsToList(parkingSpotList, parkingSpots);

                    // Show parking spot input
                    parkingSpotInput.parentElement.style.display = "block";
                } else {
                    // Hide parking spot input
                    parkingSpotInput.parentElement.style.display = "none";
                }
            });

            document.getElementById("parkingSpot").addEventListener("input", function () {
                const selectedSpot = this.value;
                const selectedCity = document.getElementById("city").value;
                const selectedStreet = document.getElementById("street").value;
                const selectedAddress = document.getElementById("address").value;

                // Resolve spotID
                 matchedSpot = transformedData.find(entry =>
                    entry.city === selectedCity &&
                    entry.street === selectedStreet &&
                    entry.address === selectedAddress &&
                    entry.spot === selectedSpot
                );

                if (matchedSpot) {
                    console.log("Resolved Spot ID:", matchedSpot.spotID);

                } else {
                    console.log("No matching spot ID found.");
                }
            });
        })
        .catch(error => {
            console.error('Error fetching data:', error);
        });
});
document.getElementById("SpotSelectionForm").addEventListener("submit", function (event) {
    event.preventDefault();
    console.log("Form submitted: Spot Selection");


    const spotSelectionForm = document.getElementById("SpotSelection");
    const paymentForm = document.getElementById("PaymentSection");
    const timeS = document.getElementById("reservationStartInput").value;
    const timeE = document.getElementById("reservationEndInput").value;

    console.log(timeE)
    console.log(timeS)
    let timeSConverted = new Date(timeS)
    let timeEConverted = new Date(timeE)


    if (timeEConverted < timeSConverted) {
        console.log("imposible timerange selected")
        return
    }

    spotSelectionForm.style.display = "none";
    paymentForm.style.display = "block";

    console.log("Navigated to Payment Form");
    console.log(matchedSpot.spotID+ "id");
    console.log(timeSConverted+ "start");
    console.log(timeEConverted+ "end");
});