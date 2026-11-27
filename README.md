# College-Canteen-Management-System
A Java-based College Canteen Management System featuring student and admin dashboards, food selection, cart management, automatic billing, order tracking, and file-based data persistence using Java File Handling.
College Canteen Management System
A console-based College Canteen Management System developed in Java. The application provides separate student and admin dashboards. Students can browse the menu, add dishes to a cart, calculate a bill, and place orders. Admins can view pending orders, mark orders as completed, review completed orders, see a sales summary, and change dish availability. The base of the program is developed by me and for the additional features and logical expression I have used a little bit of ai.
Features
Student
View the canteen menu, grouped into breakfast, snacks, and drinks.
Select multiple dishes and enter quantities.
View selected dishes and the calculated bill.
Confirm or cancel checkout.
Enter a student name and student ID/USN when placing an order.
Receive a unique order ID after a successful order.
Admin
View all pending orders with student details, items, quantities, totals, and timestamps.
Mark an order as completed using its order ID.
View completed orders.
View revenue from completed orders and the value of pending orders separately.
Toggle a dish's availability during the current application session.
File-based order persistence
Confirmed orders are saved in `orders.txt`.
The application loads saved orders when it starts.
Order IDs continue from the highest ID already saved.
When an admin marks an order completed, the updated status is saved to the file.
Order records remain available after the application is closed and reopened.
Technologies
Java (Java 8 or later)
Java I/O and NIO file APIs
`ArrayList`, arrays, and object-oriented programming
`LocalDateTime` for order timestamps
No external libraries or database are required.
Project structure
```text
College-Canteen-Management-System/
├── Main.java       # Application entry point and all Java classes
├── README.md       # Project documentation
├── .gitignore      # Files that should not be committed
└── orders.txt      # Created automatically when the first order is placed
```
How to run
Install a JDK (Java 8 or later).
Open a terminal in the project folder.
Compile the program:
```bash
   javac Main.java
   ```
Run it:
```bash
   java Main
   ```
At the passkey prompt, enter `student` or `admin`. Type `exit` to close the application.
The application allows users to switch between student and admin dashboards without restarting the program. Select the dashboard's "Back to Main Menu" option to return to the passkey prompt.
How order storage works
The program creates `orders.txt` in the current working directory when the first order is placed. It appends new orders to the file and reads existing records at startup. When an order's status changes, the application rewrites the file with the updated order records.
The file is generated automatically; it does not need to be created manually. The file is excluded from Git by `.gitignore` because it can contain student names and IDs. Each user should generate their own local order file when testing the application.
Important limitations
This is a learning project using a local text file, not a database-backed or networked application.
Student and admin dashboards are used sequentially in the same running application. This version does not support simultaneous access from separate computers or multiple application processes.
The demo uses the text passkeys `student` and `admin`; these are role selectors, not secure user-account authentication. Do not use this authentication approach in a real deployed system.
Dish availability changes are held in memory and reset when the application is restarted.
The order file is a local data file. Do not commit real student information to a public repository.
Possible future improvements
Persist menu availability and prices in a file.
Add cart quantity update and item removal options.
Add student order history and order cancellation.
Add automated tests and stronger input validation.
Move order storage from a text file to MySQL using JDBC.
Add a graphical interface using Java Swing.
Learning outcomes
This project demonstrates Java classes and objects, arrays, collections, loops, conditional statements, methods, exception handling, file reading and writing, and basic persistence of application data.
