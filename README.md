# Orbital-Backend

**BudgetBuddy [Orbital 2025]**

---

## About

A web application to manage your university finances.

Website: [http://ec2-18-141-203-5.ap-southeast-1.compute.amazonaws.com](http://ec2-18-141-203-5.ap-southeast-1.compute.amazonaws.com)

---

## Details

- **Level of Achievement**: Apollo  
- **Developers**:  
  - Chaudhari Moushami Sujeet  
  - Bawa Amarnath Sania  

---

## Instructions

### Prerequisites

- **Java JDK**: 21.0.5 ([Download for Windows](https://www.oracle.com/ae/java/technologies/downloads/#java21))
- **Maven**: 3.9.9 ([Download for Windows](https://maven.apache.org/install.html))

---

### Option 1: Clone from GitHub

1. **Clone the repository:**
    ```bash
    git clone <repository-url>
    ```
2. **Continue from Step 3 below.**

---

### Option 2: Download and Unzip the Project Folder

1. **Extract the contents completely using "Extract All".**
2. **Open a terminal and navigate to the project folder:**
    ```bash
    cd Orbital-Backend
    ```
3. **Install required Maven packages:**
    ```bash
    mvn install
    ```
4. **Run the Spring Boot server:**
    ```bash
    mvn spring-boot:run
    ```
5. **Open another terminal and navigate to the ML API:**
    ```bash
    cd Orbital-Backend/src/ml-api
    ```
6. **Run the FastAPI server:**
    ```bash
    uvicorn ml_api:app --reload
    ```
7. **Download the Email Server:**

    [Download Fake SMTP Server](https://drive.google.com/file/d/1mzDdd5oCsMtE_6Wrbo-pNWUYUuRxx6so/view?usp=sharing)

8. **Extract all contents from your downloads.**
9. **Open another terminal and run:**
    ```bash
    cd <path-to-MailServer>
    java -jar fake-smtp-server-2.4.2.jar --spring.config.location="C:\Downloads\MailServer\config\"
    ```
10. **Visit the Email Server in your browser:**
    ```
    http://localhost:9080
    ```

11. **Visit the main application in your browser (ensure frontend setup is complete):**
    ```
    http://localhost:8081
    ```

12. **To run unit tests:**
    ```bash
    mvn test
    ```

13. **For frontend setup:**  
    Please refer to the frontend repository’s README for further instructions.

---

Happy budgeting! 🎉
