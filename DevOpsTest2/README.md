# Project Title: DevOps Test 2

## DevOps Test 1: 
- Build the docker container (for DevOpsTest_2 )
- Deploy the service with docker
- Build the docker container
- Use Host network
- Restart on failure
- Maximum CPU usage 5 %
- RAM = as minimum as possible
- API URL : http://127.0.0.1:5000/
- Monitor API health (If your API failed to response at any moment, You can use any kind of monitoring tools)
- Analyze API response failure with your diagnosis in the container
- If you are not familiar with that kind of API Exceptions then do some R&D and
explain the failure to the team as much as you can
<p>&nbsp;</p>
Please be noted that 2 task are completely independent. 
<p>&nbsp;</p>

## Solution:

### Step 1:
Modify the dockerfile to expose the service in 5000 port.

```
# start by pulling the python image
FROM python:3.8-slim-buster
# copy the requirements file into the image
COPY ./requirements.txt /app/requirements.txt

# switch working directory
WORKDIR /app

RUN apt-get update
RUN apt install snapd -y
RUN apt-get install -y ca-certificates wget
RUN apt-get install curl -y
RUN snap install redis || true

RUN apt-get update
RUN systemctl unmask snapd.service
RUN systemctl enable snapd.service
RUN systemctl start snapd.service; exit 0
# install the dependencies and packages in the requirements file
RUN pip install -r requirements.txt

# copy every content from the local file to the image
COPY . /app

# configure the container to run in an executed manner
EXPOSE 5000
ENTRYPOINT [ "python" ]

CMD ["main.py" ]

```

### Step 2: 
To deploy the service, in this case, use a docker-compose file to make the deployment. Write a docker-compose file
```
version: "3"
services:
   redis: 
     image: redislabs/redismod
     deploy:
      resources:
        limits:
          cpus: '2'
          memory: 256M
        reservations:
          cpus: '0.05'
          memory: 128M
     ports:
       - '6379:6379'
     restart: on-failure
     network_mode: "host" 
   redisinsight:
    image: redislabs/redisinsight:latest
    ports:
    - '8001:8001'
    network_mode: "host"
   web:
    build: .
    network_mode: "host"
    deploy:
     resources:
      limits:
       cpus: '2'
       memory: 128M
      reservations:
       cpus: '0.05'
       memory: 64M
    ports:
      - "5000:5000"
    volumes:
      - .:/code
    depends_on:
     - redis
    healthcheck:
          test: curl --fail http://localhost:5000/ || exit 1
          interval: 40s
          timeout: 30s
          retries: 5
          start_period: 60s
    restart: on-failure 
```

### Step 3: 
To use host network on containers, set the ```network_mode: "host"``` for all services

### Step 4:
To make sure the Restart on failure add this ```restart: on-failure ``` in web service. It will automatically restarts the service whenever it will fail for any reason. 
```
docker build -t nayeemh67/devopstask1:v1 .
```

### Step 5:
For setting up the maximum cpu usage 5% set the resources limit and reservation in deploy sections.

```
    deploy:
     resources:
      limits:
       cpus: '2'
       memory: 128M
      reservations:
       cpus: '0.05'
       memory: 64M

```
### Step 6:
For minimun ram, set the memory 64M on resources section.

### Step 7:
For api url, http://127.0.0.1:5000/ , the web service should be exposed on 5000 port of host network.
```
    ports:
      - "5000:5000"
```
### Step 8:
To monitor api health for failed response after every 5 request, set a health checker on web service section.
```
    healthcheck:
          test: curl --fail http://localhost:5000/ || exit 1
          interval: 40s
          timeout: 30s
          retries: 5
          start_period: 60s
```

### Step 9:
To analyze api response failure at first check the logs of the container.
```
Traceback (most recent call last):
  File "/usr/local/lib/python3.10/site-packages/redis/connection.py", line 611, in connect
    sock = self.retry.call_with_retry(
  File "/usr/local/lib/python3.10/site-packages/redis/retry.py", line 46, in call_with_retry
    return do()
  File "/usr/local/lib/python3.10/site-packages/redis/connection.py", line 612, in <lambda>
    lambda: self._connect(), lambda error: self.disconnect(error)
  File "/usr/local/lib/python3.10/site-packages/redis/connection.py", line 677, in _connect
    raise err
  File "/usr/local/lib/python3.10/site-packages/redis/connection.py", line 665, in _connect
    sock.connect(socket_address)
ConnectionRefusedError: [Errno 61] Connection refused

During handling of the above exception, another exception occurred:

Traceback (most recent call last):
  File "/usr/local/lib/python3.10/site-packages/flask/app.py", line 2525, in wsgi_app
    response = self.full_dispatch_request()
  File "/usr/local/lib/python3.10/site-packages/flask/app.py", line 1822, in full_dispatch_request
    rv = self.handle_user_exception(e)
  File "/usr/local/lib/python3.10/site-packages/flask/app.py", line 1820, in full_dispatch_request
    rv = self.dispatch_request()
  File "/usr/local/lib/python3.10/site-packages/flask/app.py", line 1796, in dispatch_request
    return self.ensure_sync(self.view_functions[rule.endpoint])(**view_args)
  File "/Users/macbookpro/projects/career/DevOps-assignment/DevOpsTest2/main.py", line 25, in hello_geek
    REDIS_CLIENT.rpush('QUEUE', 1)
  File "/usr/local/lib/python3.10/site-packages/redis/commands/core.py", line 2695, in rpush
    return self.execute_command("RPUSH", name, *values)
  File "/usr/local/lib/python3.10/site-packages/redis/client.py", line 1235, in execute_command
    conn = self.connection or pool.get_connection(command_name, **options)
  File "/usr/local/lib/python3.10/site-packages/redis/connection.py", line 1387, in get_connection
    connection.connect()
  File "/usr/local/lib/python3.10/site-packages/redis/connection.py", line 617, in connect
    raise ConnectionError(self._error_message(e))
redis.exceptions.ConnectionError: Error 61 connecting to localhost:6379. Connection refused.
127.0.0.1 - - [26/Nov/2022 18:37:00] "GET / HTTP/1.1" 500 -
```
This exceptions are given by redis.
That is ```ConnectionRefusedError: [Errno 61] Connection refuse```
This can occure for many reason such as:
- ### Redis-Server is not started : 
    The most common reason for the connection refused error is that the Redis-Server is not started.
- ### Firewall restriction: 
    Firewall restriction is another common reason that can trigger the “could not connect to Redis connection refused”.

    By default Redis server listen to the TCP port 6379. If another application is using the port or if the firewall restrictions blocks the port, it can trigger the connection refused error.

    Thus it is important to check the status of the port in the firewall configuration while troubleshooting the could not connect to Redis error.
- ### Resource usage:
    Redis uses the main memory to store the data. Thus if the resource in the server is not sufficient for the process to run, it may get terminated abruptly.


