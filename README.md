# TTN and All-Things-Talk APIs and Utilities

This project provides an independent Java implementation of the following APIs:
* [The Things Network (TTN)](https://www.thethingsindustries.com/docs/integrations/) API;
* [All-Things-Talk](https://api.allthingstalk.io/swagger/ui/index) API;
* [OpenWeather](https://openweathermap.org) API.

Based on these API, some virtual devices and server webhooks have been implemented.

For handling CBOR data, the third party [JACOB](https://github.com/jawi/jacob) code has been included.



# TTN

In order to communicate with TTN you need a valid username and API access key from TTN. To create an API key, select API keys menu in The Things Stack console and then click on + Add API key.


## Examples

As a very simple example of usage you can see the **test.TtnTest** class (in */src/test* folder). It is a command line program that uses TTN API for listing your applications and devices on TTN, and for retrieving data payload sent to TTN from a given device.

In order to run **test.TtnTest** you have to pass a valid TTN username and API key to the program as arguments. e.g.:
```
java -cp lib/* ttn.TtnTest -u userYYY -k NNSXS.XXXX [-app appid] [-dev devid] [-v] [-h]
```

Optional arguments are:

| option | description |
| ------------- | ------------- |
| -app appid | specifies the application ID; if not present, the first application is used; |
| -dev devid | specifies the device ID; if not present, the first device of the given application is used; |
| -v | verbose mode; |
| -h | prints a help message. |


A second example is the scheduling of downlink data trasmissions using TTN.
In order to instruct the TTN application server to send data to a device (using the downlink interface), you have to first create a Webhook in TTN (if not already present) and [generate an API key](https://www.thethingsindustries.com/docs/integrations/webhooks/scheduling-downlinks/).

As an example, you can schedule a downlink transmission using **test.TtnTest** with the following command:
```
java -cp lib/* ttn.TtnTest -u userUUUU -k NNSXS.XXXX -app appidZZZZ -wh webhookidWWWW -dev devidYYYY -fb 1 -dl hexdataHHHH -vvv
```



## Dependancies

The code uses the *nemo.jar* library from the Nemo project (https://netsec.unipr.it/project/nemo). All jar files are included in the */lib* folder. 
