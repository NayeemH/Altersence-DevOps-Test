# Project Title: DevOps Test 1

## DevOps Test 1: 
A source code of a test program written in java is provided here. We would like you to dockerize this java project. It is a very simple java program to detect prime number. There is a file named input.txt inside the DevOpsTest_1 folder where 10 numbers are given. When you run the project, in the output.txt file you should find the identification of those 10 numbers weather it is prime or not. After running if you see the identification of those 10 numbers then everything is good but if not, then your job is to find out the problem and make a report about which is causing the problem. 

## Solution:

### Step 1:
There was a logical error on Main.java that is, the numebrs in input.txt file are not similiar. There were both integer and double values. So to remove the error, firstly need to parse all the data to Double and then cast the vaules to int. 

```
String data = myReader.nextLine();
double xyz = Double.parseDouble(data);
int value = ((int) xyz);
```

### Step 2: 
For project building, set the build option Maven from Intellij IdE. This will generate a pom.xml file.

### Step 3: 
Then create a Dockerfile. 

```
#Build Stage
FROM maven:3.8.6-amazoncorretto-17 AS build
WORKDIR /app
COPY . .
RUN mvn -f /app/pom.xml clean package

#check directories
RUN ls -al

#package stage
FROM openjdk:20-ea-14-jdk-oraclelinux8
COPY --from=build /app/target/*SNAPSHOT.jar .
COPY --from=build /app/input.txt .
COPY --from=build /app/out/artifacts/DevOpsTest1_jar/*.jar .
EXPOSE 80
ENTRYPOINT exec java $JAVA_OPTS -jar DevOpsTest1.jar
```

Make the jar from artifacts so the entrypoint will execute the jar from out folder. Also copy the input.txt file into the work directory. This will successfully run the container.

### Step 4:
Then build the docker image by running the following command from the same directory where dockerdfile stored
```
docker build -t nayeemh67/devopstask1:v1 .
```

### Step 5:
Run the container of this image nayeemh67/devopstask1 by running
```
docker run nayeemh67/devopstask1:v1

output:
5 true
7 true
8 false
9 false
10 false
6 false
88 false
91 false
20 false
21 false
```
### Step 6:
To push the docker image, run the following
```
docker push nayeemh67/devopstask1:v1
```