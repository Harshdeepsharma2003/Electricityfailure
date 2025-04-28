
#Project Title: MPEB Electricity Fault Monitoring System

Description: The MPEB Electricity Fault Monitoring System is a comprehensive application designed to monitor and manage electricity failures in real-time. This system leverages the power of Spring Boot and Apache Kafka to provide a robust solution for tracking electricity outages, scheduled maintenance, and notifications to affected customers.

Key Features:

Real-Time Monitoring: Automatically detects and reports electricity failures, providing immediate updates to users.
WebSocket Integration: Utilizes WebSocket for real-time communication, allowing users to receive live updates on electricity faults and resolutions.
Kafka Messaging: Employs Apache Kafka for efficient message handling and processing, ensuring reliable communication between different components of the system.
User -Friendly Interface: A clean and intuitive web interface that allows users to easily navigate through active faults and scheduled maintenance.
Search Functionality: Users can check the status of electricity faults in their area by entering their PIN code, zone, and city.
Notification System: Sends automatic notifications via SMS and email to keep users informed about outages and maintenance schedules.
Technologies Used:

Backend: Spring Boot, Spring Data JPA, Apache Kafka
Frontend: HTML, CSS, JavaScript
Database: MySQL
WebSocket: For real-time updates
Installation:

Clone the repository: git clone https://github.com/yourusername/Electricityfailure.git
Navigate to the project directory: cd Electricityfailure
Install dependencies using Maven: ./mvnw install
Configure your database settings in application.properties.
Run the application: ./mvnw spring-boot:run
Usage:

Access the application through your web browser at http://localhost:8080.
Use the search feature to check the status of electricity faults in your area.
Monitor live updates on electricity failures and resolutions.
Contributing: Contributions are welcome! Please feel free to submit a pull request or open an issue for any enhancements or bug fixes.
