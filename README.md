# Xarda

This project was developed in the course "Blockchain technology for public sector innovation" in cooperation with [Fortiss](https://www.fortiss.org) and [Agata](https://agatatechnology.es). 
Xarda is used to optimize the Less than a Container Load (LCL) process using a DLT-based solution. 
It consists of a CorDapp, an API accessing it and a demonstration frontend. 

## Getting Started

The following steps will help you run a local instance of the project. 


### Prerequisites

The following points must be observed in order to start the individual services correctly:
- Java 8 is required to start the Corda nodes
- Docker is necessary to start the containers for the API and the frontend
- Postman is helpful to test a sample flow using the attached collection and the steps listed below


### Installing

#### Start Corda nodes

- Download the configurations packed as a ZIP for the test network: https://1drv.ms/u/s!Am0z09x-pM7Cgv4S7nCxF7T6xc6V9A?e=C05wRA
- Execute `./runnodes` or use `runnodes.bat` based on your operating system
- Check that all 5 nodes are booted up by the respective terminal window. If something like "Node for "..." started up and registered in 76.45 sec" appears and then an interactive shell can be seen, everything is fine.
- If one or more nodes did not start as expected, re-run the command from step 2

If the configuration in the ZIP leads to errors, this can also be created manually. 
To do this, switch to `/cordapp-lcl` and run `./gradlew build -x test` and then `./gradlew deployNodes`.


### Start API and frontend per node

In this step, APIs are started that connect to the Corda node of buyer, supplier, LCL company and shipping line, respectively.
Additionally, we will launch a demonstration frontend for buyer, supplier and LCL company (which differ by ports). 
To do this, follow the steps below:

- Switch to the project root level
- Execute `docker-compose up` to 
- If something like `started application in X seconds` appears for all APIs, they are ready to use. The frontend containers usually do not output anything.

## Exemplary walk-through

Below is a guide on how to run our business process from start to finish using the Postman collection (`xarda.postman_collection`) provided.
This requires knowledge of the associated report. 
We assume a simplified example for one buyer and a single container, because otherwise the process and the description becomes even more complex. 
However, the steps can be adapted and for the same buyer node e.g. several packages can be created, which are then consolidated.  

Here are a few more points to consider after importing the `xarda.postman_collection` file into Postman:
- In "" is the name of the Postman request and in parentheses is the API to use. Unless otherwise specified, no further changes are required to the prebuilt requests in terms of their bodies.
- The URLs used in the requests are matched to the provided configuration, but the IDs must be changed as needed.
- The first time a request to a node is started, it might take longer than usual.
- With the GET-Requests in the folders further information can be requested at any time
- Everything that is done here via the copied IDs could be read by machine and passed via the respective endpoints if a software integrates our APImight be helpful

After each final step of the groups below, it might be helpful to look at the change in tracking. 
Use "Get tracking state" with the corresponding ID as URL path variable.

** Buyer assigns the LCL company (folder "Assign LCL company")**
- Propose LCL assignment" (LCL company): Initiates a proposal to the buyer with the data received off-chain from the customer. Then copy the Id of the created proposal from linearId.id in the response.
- Accept LCL assignment (Buyer): Use the copied ID from the previous step as URL path variable for the proposal id. This way the customer accepts the created agreement. Copy linearId.id (the ID of this assignment) and trackingStateId.id (the ID of the related tracking) from the response to a text document for further use.

** LCL company requests a container from the shipping line (folder "Request container") **
- "Initiate container request" (LCL company): Execute the request without any further changes to start a negotiation between LCL company and Shipping Line. Copy linearId.id from the response to the clipboard.
- "Assign container" (Shipping Line): Use the proposal ID from step 1 as URL path variable. Execute the request to assign the container defined in the body to the LCL company.
- "Accept proposal" (LCL company): Use the ID from step 1 as URL parameter and copy the Tracking State Id from one of the first steps into the field in the request body. This way the LCL company accepts the assigned container. Store the ID of this accepted container request (linearId.id in the response) in the temporary text document. 

** LCL company picks up the goods from each supplier (folder "Pickup goods") **
- "Propose pickup" (LCL company): Use the stored ID of the LCL assignment (order) as value in the request body. Copy the ID of the generated proposal (linearId.id) from the response to the clipboard.
- "Set picked up goods" (Supplier): Use the ID of the proposal copied in the previous step as URL parameter to set the picked up goods with the request.
- "Accept proposal (LCL company): Use the proposal ID as URL parameter and enter the container request ID from the previous process step and the known tracking ID in the request body. Store the ID (linearId.id in the response) of the issued House Bill Of Lading.

** LCL company transfers the packed container to the Shipping Line (folder "Load ship") **
- "Propose loading" (LCL company): Set in the request body the stored ID of the container request and the House Bill Of Lading ID. After creating the loading proposal, save the generated proposal ID (linearId.id in the response) to the clipboard.
- "Accept proposal (shipping line): use the copied proposal ID as URL parameter and paste the saved tracking ID into the array in the request body to update the status for each package of the container. After executing the request, store the ID of the Master Bill of Lading issued by the Shipping Line (linearId.id in the response) in the text document.
- "Get tracking state: Use the saved tracking ID to check at this point for example for the buyer which information has been tracked over time.

** LCL company releases the arrived container from the Shipping Line (folder "Deconsolidation") **
- "Propose deconsolidation" (LCL company): Set the ID of the stored Master Bill of Lading in the Request Body, which the LCL company will hand over to the Shipping Line to receive the goods. After executing the request, save the received proposal ID (linearId.id of the response) to the clipboard.
- "Release container" (Shipping Line): Use the copied ID as URL parameter and execute the request so that the Shipping Line proposes the container release.
- Accept deconsolidation" (LCL company): Use the proposal ID as URL parameter and add the tracking id to the array in the request body to update the status for each package of the container. Execute the request.

** Shipped goods are delivered to the customer (folder "Delivery") ** 
- "Propose delivery" (Buyer): Insert the known ID of the House Bill of Lading into the request body (for which the customer wants to receive his goods). After executing the request, save the received proposal ID from the response.
- "Set delivered goods" (LCL company): Use the copied proposal ID as URL parameter and start the request to propose the delivered goods.
- "Accept delivery (buyer): Use the copied proposal ID as URL parameter and copy the known tracking ID into the request body.

Finally, a call to "Get tracking state" shows the history of all the steps executed above.





## Built With

* [https://www.corda.net](http://www.dropwizard.io/1.0.2/docs/) - Used DLT
* [Spring Boot](https://spring.io/projects/spring-boot) - API
* [Angular](https://angular.io) - Frontend
* [Docker](https://www.docker.com) - Virtualization of API and frontend

## Authors

* **Adrian Mitter **
* **Maximillian Henneberg  **
* **Jesco Melzer  **
* **Marco Mielenz  ** 

## License

This project is licensed under the MIT License.
