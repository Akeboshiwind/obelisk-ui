(ns obelisk-ui.api
  (:require [org.httpkit.client :as http]
            [cheshire.core :refer [generate-string parse-string]]))

;;;; --- Private API --- ;;;;

(defn- make-address
  "Makes the address for the given endpoint"
  [server-address endpoint]
  (str server-address endpoint))

(defn- get
  "Make a GET request to the given endpoint
  Supports basic auth by using the :basic-auth key
  Returns the response"
  [endpoint
   {:keys [server-address
           basic-auth
           cookie]}]
  (let [response @(http/get (make-address server-address endpoint)
                            {:basic-auth basic-auth
                             :keep-alive 3000
                             :headers {"Cookie" cookie}})]
    (when (= (:status response) 200)
      response)))

(defn- get-json
  "Makes a GET request to the given endpoint
  Returns the body of the response parsed to json"
  [endpoint opts]
  (let [response (get endpoint opts)]
    (-> response
        :body
        (parse-string true))))

;;;; --- Public API --- ;;;;

(defn login
  "Returns the cookie that is set after logging into the obelisk ui
  Supports basic auth by using the :basic-auth key"
  [username password {:keys [server-address
                             basic-auth]}]
  (get-in
   @(http/post (make-address server-address "/api/login")
               (merge
                {:body (generate-string {:username username
                                         :password password})}
                (when basic-auth
                  {:basic-auth basic-auth})))
   [:headers
    :set-cookie]))

(defn curr-user
  "Returns information about the currently logged in user"
  [{:keys [server-address
           basic-auth
           cookie]}]
  (get-json "/api/currUser" opts))

(defn versions
  "Returns the versions of the software running on the obelisk"
  [{:keys [server-address
           basic-auth
           cookie]}]
  (get-json "/api/inventory/versions" opts))

(defn dashboard
  "Returns the information displayed on the dashboard of the obelisk ui"
  [{:keys [server-address
           basic-auth
           cookie]}]
  (get-json "/api/status/dashboard" opts))

(defn pools
  "Returns information about the pools that are currently setup on the obelisk"
  [{:keys [server-address
           basic-auth
           cookie]}]
  (get-json "/api/config/pools" opts))

(defn mining
  "Returns the currently set mining config"
  [{:keys [server-address
           basic-auth
           cookie]}]
  (get-json "/api/config/mining" opts))

(defn system
  "Returns the currently set system config"
  [{:keys [server-address
           basic-auth
           cookie]}]
  (get-json "/api/config/system" opts))

(defn network
  "Returns the currently set network config"
  [{:keys [server-address
           basic-auth
           cookie]}]
  (get-json "/api/config/network" opts))

(defn diagnostics
  "Returns diagnostics information"
  [{:keys [server-address
           basic-auth
           cookie]}]
  (-> (get "/api/status/diagnostics" opts)
      :body
      slurp))

(comment

  (def opts {:server-address "http://localhost"
             :basic-auth ["username" "password"]})

  (def cookie (login "admin" "admin" opts))

  (def opts (assoc opts :cookie cookie))

  (curr-user opts)
; => {:username "admin", :deviceModel "SC1|DCR1"
  (versions opts)
;=> {:firmwareVersion "SC1 v1.2.0", :cgminerVersion "4.10.0"

  (keys (dashboard opts))
;=> (:diagnostics :hashrateData :systemInfo :poolStatus :hashboardStatus)

  (pools opts)
;=> ({:password "my-secure-pw", :url "pool-url", :worker "my-worker"

  (mining opts)
;=> {:disableGeneticAlgo false, :rebootMinHashrate 150, :rebootIntervalMins 0, :maxHotChipTempC 105, :optimizationMode 2, :minFanSpeedPercent 10}

  (system opts)
;=> {:timezone "Europe/London"

  (network opts)
;=> {:macAddress "my-mac-address", :ipAddress "192.168.1.2", :subnetMask "255.255.255.0", :hostname "Obelisk", :dnsServer "8.8.8.8", :gateway "192.168.1.1", :dhcpEnabled false}

  (count (diagnostics opts))
;=> 10315

  [])
