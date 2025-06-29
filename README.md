# Orbital-Backend

**BudgetBuddy [Orbital 2025]**

---

## About
A web application to manage your university finances.

---

## Details
- **Level of Achievement**: Apollo  
- **Developers**:  
  - Chaudhari Moushami Sujeet  
  - Bawa Amarnath Sania  

---

## Instructions

1. **Ensure that you have Java and Maven installed** 

    Maven installation version - 3.9.9
    Link for windows : https://maven.apache.org/install.html

    Java JDK used - Java - 21.0.5
    Link for windows : https://www.oracle.com/ae/java/technologies/downloads/#java21

## Option 1 :  Instructions for Github clone

1. **Clone the repository**  
    ```bash
    git clone <repository-url>
    ```
2. **See step 3 onwards**

## Option 2 :  Download and unzip project folder

1. **Select “Extract All” and extract the contents completely**
   
2. **Open terminal and navigate inside the project folder**
   ```bash
    cd Orbital-Backend
    ```
3. **Install the required maven packages**  
    ```bash
    mvn install
    ```

4. **Run the spring-boot server**  
    ```bash
    mvn spring-boot:run
    ```
5. **Open another terminal and navigate to** 
     ```bash
    cd Orbital-Backend\src\ml-api
    ```
6. **Run FASTAPI server using**
    ```bash
    uvicorn ml_api:app --reload
    ```
7. **Open your browser and visit:  (Ensure frontend setup is also completed)**
   ```bash
    http://localhost:8081
    ``` 
     
8. **For frontend setup**  
   Please refer to the frontend repository’s README for further instructions.

---

Happy budgeting! 🎉
