# TTN and All-Things-Talk APIs and utilities

This project provides an independent Java implementation of the following APIs:
* [The Things Network (TTN)](https://www.thethingsindustries.com/docs/integrations/) API;
* [All-Things-Talk](https://api.allthingstalk.io/swagger/ui/index) API;
* [OpenWeather](https://openweathermap.org) API.

Based on these API, some virtual devices and server webhooks have been implemented.

For handling CBOR data, the third party [JACOB](https://github.com/jawi/jacob) code has been included.



# TTN

In order to communicate with TTN you need a valid username and API access key from TTN. To create an API key, select API keys menu in The Things Stack console and then click on + Add API key.


## Example: TtnTest

As a very simple example of usage you can see the **test.TtnTest** class (in */src/test* folder). It is a command line program that uses TTN API for listing your applications and devices on TTN, and for retrieving data payload sent to TTN from a given device.

In order to run **test.TtnTest** you have to pass a valid TTN username and API key to the program as arguments. e.g.:
```
java -cp lib/* ttn.TtnTest -u userYYY -k NNSXS.XXXX [-a appid] [-d devid] [-v] [-h]
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
