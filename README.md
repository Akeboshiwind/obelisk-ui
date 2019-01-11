# obelisk-ui

A clojure wrapper for the api calls that the [obelisk](https://obelisk.tech/) miner ui makes.

## Installation

Include the following in your list of dependencies:

[![Clojars Project](https://img.shields.io/clojars/v/obelisk-ui.svg)](https://clojars.org/obelisk-ui)

## Usage

The entirety of the api is in the `api` namespace:

```clojure
(require '[obelisk-ui.api :as api])
```

First step is to get a cookie from the ui:

```clojure
(def opts {:server-address "http://<your-obelisk-address>"
           :basic-auth ["username" "password"]}) ;; This key is optional

(def cookie (api/login "admin" "admin" opts))

(def opts (assoc opts :cookie cookie))
```

After getting a cookie you can get your outputs:

```clojure
(api/curr-user opts)
; => {:username "admin", :deviceModel "SC1|DCR1"

(api/versions opts)
;=> {:firmwareVersion "SC1 v1.2.0", :cgminerVersion "4.10.0"

(keys (api/dashboard opts))
;=> (:diagnostics :hashrateData :systemInfo :poolStatus :hashboardStatus)

(api/pools opts)
;=>({:password "my-secure-pw", :url "pool-url", :worker "my-worker"

(api/mining opts)
;=> {:disableGeneticAlgo false, :rebootMinHashrate 150, :rebootIntervalMins 0, :maxHotChipTempC 105, :optimizationMode 2, :minFanSpeedPercent 10}

(api/system opts)
;=> {:timezone "Europe/London"

(api/network opts)
;=> {:macAddress "my-mac-address", :ipAddress "192.168.1.2", :subnetMask "255.255.255.0", :hostname "Obelisk", :dnsServer "8.8.8.8", :gateway "192.168.1.1", :dhcpEnabled false}

(count (api/diagnostics opts))
;=> 10315
```

## License

Copyright © 2019 Oliver Marshall

Distributed under the MIT License.
