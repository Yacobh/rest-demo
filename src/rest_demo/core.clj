(ns rest-demo.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json])
  (:gen-class))
; Scramble
; Construct a map, every letter has a prime number assigned
(def prime-abc (zipmap (map str "abcdefghijklmnopqrstuvwxyz") '(2 3 5 7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73 79 83 89 97 101)))
; every string has a product of prime factors
(defn str-value [x y] (*' x (get prime-abc y 0.1)))
(defn scramble [s1 s2]
  ; if the division of the products is integer then it can be match
  (integer? ( / (reduce str-value 1 (map str s1)) (reduce str-value 1 (map str s2)))) 
)

; Simple Body Page
(defn simple-body-page [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "Hello World"})

; Scramblie-service handler
(defn scramble-service [req] ;(3)
     {:status  200
      :headers {"Content-Type" "text/html"}
      :body    (->
                (pp/pprint req)
                (str (scramble (:s1 (:params req)) (:s2 (:params req)) )  ))})


; Our main routes
(defroutes app-routes
  (GET "/" [] simple-body-page)
  (GET "/scramble" [] scramble-service)
  (route/not-found "Error, page not found!"))

(defn -main
  "This is our main entry point"
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    ; Run the server with Ring.defaults middleware
    (server/run-server (wrap-defaults #'app-routes site-defaults) {:port port})
    ; Run the server without ring defaults
    ;(server/run-server #'app-routes {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))