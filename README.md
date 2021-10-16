# TTN REST API

This is an independent Java implementation of The Things Network (TTN) REST API.

In order to communicate with TTN you need a valid username and API access key from TTN. To create an API key, select API keys menu in The Things Stack console and then click on + Add API key.


## Example: TtnTest

As a very simple example of usage you can see the **test.TtnTest** class (in */src/test* folder). It is a command line program that uses TTN API for listing your applications and devices on TTN, and for retrieving data payload sent to TTN from a given device.

In order to run **test.TtnTest** you have to pass a valid TTN username and API key to the program as arguments. e.g.:
```
java -cp lib/nemo.jar;lib/ttnapi.jar ttn.TtnTest -u userYYY -k NNSXS.XXXX [-a appid] [-d devid] [-v] [-h]
```

Optional arguments are:

| option | description |
| ------------- | ------------- |
| -a appid | specifies the application ID; if not present, the first application is used; |
| -d devid | specifies the device ID; if not present, the first device of the given application is used; |
| -v | verbose mode; |
| -h | prints a help message. |


## Dependancies

The code uses the *nemo.jar* library from the Nemo project (https://netsec.unipr.it/project/nemo). The *nemo.jar* file is included in the */lib* folder. 
