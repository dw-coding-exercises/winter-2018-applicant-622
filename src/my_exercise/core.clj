(ns my-exercise.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [my-exercise.home :as home]
            [hiccup.page :refer [html5]]
            [clj-http.client :as client]))

; Returns a county name based on address, requires ZIP
(defn retrieve-county [params]
  )

; Builds query string based on form inputs
(defn build-ocd-ids [params]
  (let [city (clojure.string/replace (clojure.string/lower-case (:city params)) #"\s+" "_")
        state (clojure.string/lower-case (:state params))
        prefix (str "ocd-division/country:us/state:" state)]
    (if (not= city "") (str prefix "," prefix "/place:" city) prefix)))

(defn call-api [ocd-ids]
  (str 
    (:body
      (client/get 
        (str "https://api.turbovote.org/elections/upcoming?district-divisions=" ocd-ids)))))

(defn find-elections [params]
  (call-api 
    (build-ocd-ids params)))

(defn display-elections [result]
  (html5 [:p result]))

(defroutes app
  (GET "/" [] home/page)
  (POST "/search" req
    (display-elections (find-elections (:params req))))
  (route/resources "/")
  (route/not-found "Not found"))

(def handler
  (-> app
      (wrap-defaults site-defaults)
      wrap-reload))


