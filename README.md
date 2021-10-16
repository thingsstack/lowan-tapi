# TTN REST API

This is an independent Java implementation of The Things Network (TTN) REST API.

The only dependence is on the Nemo libray (https://netsec.unipr.it/project/nemo), included in in the /lib folder. 

As a very simple of usage you can see the test.TtnTest class (in /src/test folder). It is a command line program that uses TTN API for listing your applications and devices on TTN, and for retrieving data payload sent to TTN from a given device.

In order to run test.TtnTest you have to pass a TTN username and API authentication key as arguments. e.g.:
```
java -cp lib/nemo.jar;lib/ttnapi.jar ttn.TtnTest -u user -k NNSXS.XXXX
```

An verbose output can be obtained by adding the '-v' option.
