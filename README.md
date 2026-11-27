# College Canteen Management System

## Project Overview

The College Canteen Management System is a Java-based console application designed to manage food ordering and order processing in a college canteen.

The application provides separate dashboards for students and administrators. Students can view the menu, select food items, enter quantities, place orders and generate bills. Administrators can view received orders, mark orders as completed, check total earnings and manage food availability.

Java File Handling is used to store order information so that orders remain available even after the application is closed and restarted.

## Features

### Student Dashboard
- View available food items and their prices.
- Select multiple food items.
- Enter quantities for selected dishes.
- View selected dishes.
- Calculate the total bill automatically.
- Confirm or cancel an order.
- Receive a unique order ID.

### Admin Dashboard
- View received (pending) orders.
- View completed orders.
- Mark pending orders as completed.
- Check total amount earned from completed orders.
- Check and update food availability.

### File Handling
- Save orders in a text file.
- Retrieve previously saved orders.
- Preserve order details and completion status after restarting the application.

## Technologies Used

- **Programming Language:** Java
- **Concepts:** Object-Oriented Programming (OOP), Classes, Objects, Inheritance, Encapsulation, Arrays, ArrayList, Loops and Switch Statements.
- **File Handling:** FileWriter, FileReader, BufferedReader and BufferedWriter.
- **Development Environment:** VS Code / IntelliJ IDEA / Eclipse.

## Menu Items

### Breakfast
- Idli – Rs. 30
- Dosa – Rs. 35
- Vada Pav – Rs. 12
- Vada – Rs. 15

### Snacks
- Pani Puri – Rs. 20
- Bhel – Rs. 20

### Drinks
- Tea – Rs. 5
- Coffee – Rs. 10

## How to Run the Project

1. Download or clone this repository.
2. Open the project folder in your Java IDE.
3. Open the Main.java file.
4. Compile the program:

   javac Main.java

5. Run the program:

   java Main

6. Enter the appropriate passkey:
   - student – To access the Student Dashboard.
   - admin – To access the Admin Dashboard.
   - exit – To close the application.

## Data Storage

The application uses a text file named `orders.txt` to store order information.

The file is automatically created when an order is saved. Previously stored orders can be retrieved when the application starts again.

Note: The order file is generated locally and is not included in the GitHub repository.

## Project Learning Outcomes

Through this project, I learned and practised:

Implementing Object-Oriented Programming concepts in Java.

Creating separate functionalities for different users.

Managing food selection and calculating bills.

Using arrays and ArrayLists to manage order information.

Implementing Java File Handling for persistent data storage.

Managing order status and validating user input.

Developing a menu-driven console application.

## problems faced
During development of this project I wrote the basic structure of the program(pseudo code) easily but in later stages I found difficulties in placing the order , calculating the bill , viewing orders from admin's perspectives , etc.
So I tried my best to solve it by myself and apply my all existing knowledge . 
Then some of the part I solved and the part in which I was facing problems , I learned the new concepts and then applied it in my project and after that I took some help from ai which gave me many unique solution ,which I learned and applied it to my project.
## Future Improvements

Implement a graphical user interface using Java Swing.

Integrate MySQL for database management.

Add student registration and login functionality.

Add order history and search functionality.

Generate daily and monthly sales reports.

Support simultaneous access by students and administrators.

## Author
Developed as a learning project to practise Java programming, Object-Oriented Programming and File Handling.

## License

This project is available for educational and learning purposes.
