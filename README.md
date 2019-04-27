# tflapp

A [re-frame](https://github.com/Day8/re-frame) application designed to use TFL (Transport for London) api.
A basic app written in ClojureScript.

Using this app, a user should be able to search for all bike points for an arbitrary location (string).
The search yields a list of all bike points in the list format and also maps it on google map.


## Development Mode

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

