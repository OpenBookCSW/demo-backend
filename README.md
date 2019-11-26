# Open Book Backend

A simple chat application written with [Ktor](https://ktor.io) using websockets and sessions.

## Running

Execute this command in the repository's root directory to run this sample:

```bash
./gradlew :run
```
 
And navigate to [http://localhost:8080/](http://localhost:8080/) to see the sample home page.

And/Or if you want to connet the IntelliJ debugger then use a run configuration of:

> Gradle with the argument `run` for the project

Was using [ngrok](https://ngrok.com/) to forward this local port for the iOS app. See instances of `3ygun.ngrok.io` in the iOS code.

Was using the final stats when running this:

```shell
$ gradle --version

------------------------------------------------------------
Gradle 5.5.1
------------------------------------------------------------

Build time:   2019-07-10 20:38:12 UTC
Revision:     3245f748c7061472da4dc184991919810f7935a5

Kotlin:       1.3.31
Groovy:       2.5.4
Ant:          Apache Ant(TM) version 1.9.14 compiled on March 12 2019
JVM:          1.8.0_202-ea (Oracle Corporation 25.202-b03)
OS:           Mac OS X 10.15.1 x86_64
```
