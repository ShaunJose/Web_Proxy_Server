# Web Proxy Server


### Description
An implementation of a web proxy server (acting as an intermediary between a client and a host) in Java.


### Tools used
1. JVM (for Java code)

2. curl


### Instructions:
1. Compile ManagementConsole.java
```
javac ManagementConsole.java
```

2. Run ManagementConsole.java
```
java ManagementConsole
```

3. Block sites you want to by following instructions which will we displayed

4. Open new tab on terminal and use curl to send a request to the proxy
```
curl -x localhost:4000 <InterestedSiteUrl>
```
