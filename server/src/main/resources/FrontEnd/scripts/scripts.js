let matchedSpot=0;
let cost=0;
let reservationId=0;
let procesInstanceId;
let eventSource;
document.addEventListener("DOMContentLoaded", () => {
    const cityList = document.getElementById("cityList");
    const streetList = document.getElementById("streetList");
    const addressList = document.getElementById("addressList");
    const parkingSpotList = document.getElementById("parkingSpotList");

    const streetInput = document.getElementById("street");
    const addressInput = document.getElementById("address");
    const parkingSpotInput = document.getElementById("parkingSpot");


    streetInput.parentElement.style.display = "none";
    addressInput.parentElement.style.display = "none";
    parkingSpotInput.parentElement.style.display = "none";

    fetch('http://localhost:8080/api/parkings')
        .then(response => response.json())
        .then(apiResponse => {
            console.log(apiResponse);


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


                    streetInput.parentElement.style.display = "block";
                    addressInput.parentElement.style.display = "none";
                    parkingSpotInput.parentElement.style.display = "none";
                } else {

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


                    parkingSpotInput.parentElement.style.display = "block";
                } else {

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


    console.log("Navigated to Payment Form");
    console.log(matchedSpot.spotID+ "id");
    console.log(timeSConverted+ "start");
    console.log(timeEConverted+ "end");

    function SendReservation(start,stop,SpotID) {
        const body=JSON.stringify({
            spotId:SpotID,
            startTime:timeS,
            endTime:timeE,
        })
        console.log("test formatu"+body)

        fetch('http://localhost:8080/api/start',{
            method:'POST',
            headers:{'Content-Type': 'application/json'},
            body:body
        }).then(response => {
            if (!response.ok) {
                throw new Error('Failed to make reservation: ' + response.statusText);
            }
            return response.json();
        })
            .then(data => {
                console.log('Reservation successful:', data);
              //  cost=data.totalCost;
               // reservationId=data.reservationId;
                procesInstanceId=data.processInstanceKey;
                console.log('data  recieved:'+procesInstanceId);
                eventSource= new EventSource(`http://localhost:8080/api/subscribe?processInstanceKey=${procesInstanceId}`);

                eventSource.onmessage = async function (event) {
                    try {
                        console.log("Camunda response: ", event.data);
                        const data = JSON.parse(event.data);
                        console.log("Parsed data: ", data);
                        if (data.hasOwnProperty('isSpaceAvaliable')){
                            if (data.isSpaceAvaliable===true)
                            {
                                const goPaymentButton=document.getElementById('goToPaymentButton').style.display='block'
                            }
                            else {
                                alert("this spot is already taken on this time, select another one")
                                const goPaymentButton=document.getElementById('goToPaymentButton').style.display='none'
                            }

                        }
                        if (data.hasOwnProperty('totalCost')){
                          cost=data.totalCost;
                          document.getElementById("price").innerHTML=cost/100;
                        }
                        if (data.hasOwnProperty('reservationId')){
                            reservationId=data.reservationId;
                            document.getElementById("reservationId").innerHTML=reservationId;

                        }
                    } catch (error) {
                        console.error("Error in onmessage handler: ", error);
                    }
                };
                // function SetPayment() {
                //     document.getElementById("price").innerHTML=cost/100;
                // }
                // SetPayment();
            })
            .catch(error => {
                console.error('Error during reservation:', error);
                //dubluje inie z response!=200 pewnie do wywalenia
            });
    }
    SendReservation(timeS,timeE,matchedSpot.spotID)

});
document.getElementById("goToPaymentButton").addEventListener("click", function (event) {
    event.preventDefault();
    const spotSelectionForm = document.getElementById("SpotSelection");
    const paymentForm = document.getElementById("PaymentSection");
    spotSelectionForm.style.display = "none";
    paymentForm.style.display = "block";

})
document.getElementById("PaymentForm").addEventListener("submit", function (event) {
    event.preventDefault();
    const validBankingData = [
        {
            name: "Piotr",
            surname: "Dawid",
            cardNumber: "1231232132132131",
            cvv: "123",
            expireDate: "2025-02",
        },
        {
            name: "Anna",
            surname: "Kowalska",
            cardNumber: "9876543210987654",
            cvv: "456",
            expireDate: "2026-05",
        },
        {
            name: "John",
            surname: "Doe",
            cardNumber: "1111222233334444",
            cvv: "789",
            expireDate: "2024-12",
        },
    ];
   // console.log('abc test');

    const name = document.getElementById('firstName').value;
    const surname = document.getElementById('lastName').value;
    const cardNumber = document.getElementById('cardNumber').value;
    const cvv = document.getElementById('cvv').value;
    const expireDate = document.getElementById('expiryDate').value;
    const mail= document.getElementById("email").value;

   // console.log(`Name: ${name}\nSurname: ${surname}\nCard Number: ${cardNumber}\nCVV: ${cvv}\nExpire Date: ${expireDate}\nEmail: ${mail}`);

    // const isPaymentValid = validBankingData.some(entry =>
    //     entry.name === name &&
    //     entry.surname === surname &&
    //     entry.cardNumber === cardNumber &&
    //     entry.cvv === cvv &&
    //     entry.expireDate === expireDate
    // );

    // if (isPaymentValid) {
    //     console.log("Payment successful!");
    //     alert("Payment processed successfully!");
    //     window.location.href = "html-templates/success-popup.html";
    //
    // } else {
    //     console.error("Payment failed! Invalid details.");
    //     alert("Payment failed! Please check your details and try again.");
    // }

    const  body={
        reservationId:reservationId,
        card: {
            number: cardNumber,
            name: name + ' ' + surname,
            expire: expireDate,
            cvc: cvv,
        }
    }
    console.log(body)

    fetch('http://localhost:8080/api/payment',{
        method:'POST',
        headers:{'Content-Type': 'application/json'},
        body:body
    }).then(response => {
        if (!response.ok) {
            throw new Error('payment failed' + response.statusText);
        }
        return response.json();
    }).then(response=>{
        console.log("payment succesfull"+response)
    })
});