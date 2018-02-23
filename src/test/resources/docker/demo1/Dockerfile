FROM java:7
                    COPY . /usr/src/myapp
                    WORKDIR /usr/src/myapp
                    RUN javac Hello.java
                    CMD ["java", "Hello"]