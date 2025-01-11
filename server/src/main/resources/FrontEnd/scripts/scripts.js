document.addEventListener("DOMContentLoaded", () => {
    const cityList = document.getElementById("cityList");
    const streetList = document.getElementById("streetList");
    const addressList = document.getElementById("addressList");
    const parkingSpotList = document.getElementById("parkingSpotList");

    // Fetch the parking data from your API
    fetch('http://localhost:8080/api/parkings')
        .then(response => response.json()) // Parse the response as JSON
        .then(apiResponse => {

            // Transform the API response to the required format
            const transformedData = apiResponse.flatMap(parking =>
                parking.spots.map(spot => ({
                    city: parking.city,
                    street: parking.street,
                    address: parking.address,
                    spot: spot.spaceCode
                }))
            );

            // Add options to list function
            function addOptionsToList(listElement, options) {
                listElement.innerHTML = '';
                options.forEach(option => {
                    const optionElement = document.createElement("option");
                    optionElement.value = option;
                    listElement.appendChild(optionElement);
                });
            }

            // Populate the city list
            const cities = [...new Set(transformedData.map(entry => entry.city))];
            addOptionsToList(cityList, cities);

            // Handle city input change
            document.getElementById("city").addEventListener("input", function () {
                const selectedCity = this.value;
                const streets = [...new Set(transformedData.filter(entry => entry.city === selectedCity).map(entry => entry.street))];
                addOptionsToList(streetList, streets);

                // Reset address and parking spot lists
                addressList.innerHTML = '';
                parkingSpotList.innerHTML = '';
            });

            // Handle street input change
            document.getElementById("street").addEventListener("input", function () {
                const selectedStreet = this.value;
                const selectedCity = document.getElementById("city").value;

                const addresses = [...new Set(transformedData.filter(entry => entry.city === selectedCity && entry.street === selectedStreet).map(entry => entry.address))];
                addOptionsToList(addressList, addresses);

                // Reset parking spot list
                parkingSpotList.innerHTML = '';
            });

            // Handle address input change
            document.getElementById("address").addEventListener("input", function () {
                const selectedAddress = this.value;
                const selectedCity = document.getElementById("city").value;
                const selectedStreet = document.getElementById("street").value;

                const parkingSpots = [...new Set(transformedData.filter(entry => entry.city === selectedCity && entry.street === selectedStreet && entry.address === selectedAddress).map(entry => entry.spot))];
                addOptionsToList(parkingSpotList, parkingSpots);
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
    let timeSConverted = new Date(timeS).getTime()
    let timeEConverted = new Date(timeE).getTime()


    if (timeEConverted < timeSConverted) {
        console.log("imposible timerange selected")
        return
    }

    spotSelectionForm.style.display = "none";
    paymentForm.style.display = "block";

    console.log("Navigated to Payment Form");
});