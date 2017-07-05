# Spring Boot example for http/2 cleartext

This small Spring Boot application is showcasing how to setup an application to serve
both HTTP/1.1 and unencrypted HTTP/2 cleartext, known as **h2c** over the same port.

```bash
$ mvn clean package spring-boot:run
```

Then, install an http/2 test tool, like [nghttp](https://nghttp2.org/documentation/package_README.html) for example.

Use it like this:

```bash
$ nghttp -vua http://localhost:8080/hello-world
```

As you can see, nghttp performs a protocol upgrade from http/1.1 to h2c (http/2 cleartex), the unencrypted variant of http/2.

```
[  0.002] Connected
[  0.002] HTTP Upgrade request
GET /hello-world?name=Jan HTTP/1.1
host: localhost:8080
connection: Upgrade, HTTP2-Settings
upgrade: h2c
http2-settings: AAMAAABkAAQAAP__
accept: */*
user-agent: nghttp2/1.17.0

[  0.003] HTTP Upgrade response
HTTP/1.1 101 Switching Protocols

[  0.003] HTTP Upgrade success
[  0.005] recv HEADERS frame <length=67, flags=0x04, stream_id=1>
          ; END_HEADERS
          (padlen=0)
          ; First response header
{"id":19,"content":"Hello, Jan!"}[  0.005] recv DATA frame <length=33, flags=0x00, stream_id=1>

```