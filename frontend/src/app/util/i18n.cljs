;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.
;;
;; Copyright (c) 2015-2016 Juan de la Cruz <delacruzgarciajuan@gmail.com>
;; Copyright (c) 2015-2019 Andrey Antukh <niwi@niwi.nz>

(ns app.util.i18n
  "A i18n foundation."
  (:require
   [beicon.core :as rx]
   [cuerdas.core :as str]
   [goog.object :as gobj]
   [okulary.core :as l]
   [rumext.alpha :as mf]
   [app.config :as cfg]
   [app.util.storage :refer [storage]]
   [app.util.transit :as t]))

(defonce locale (l/atom (or (get storage ::locale)
                            cfg/default-language)))
(defonce translations #js {})

;; The traslations `data` is a javascript object and should be treated
;; with `goog.object` namespace functions instead of a standart
;; clojure functions. This is for performance reasons because this
;; code is executed in the critical part (application bootstrap) and
;; used in many parts of the application.

(defn init!
  [data]
  (set! translations data))

(defn set-current-locale!
  [v]
  (swap! storage assoc ::locale v)
  (reset! locale v))

(defn set-default-locale!
  []
  (set-current-locale! cfg/default-language))

(deftype C [val]
  IDeref
  (-deref [o] val))

(defn ^boolean c?
  [r]
  (instance? C r))

;; A main public api for translate strings.

;; A marker type that is used just for mark
;; a parameter that reprsentes the counter.

(defn c
  [x]
  (C. x))

(defn empty-string?
  [v]
  (or (nil? v) (empty? v)))

(defn t
  ([locale code]
   (let [code  (name code)
         value (gobj/getValueByKeys translations code locale)]
     (if (empty-string? value)
       (if (= cfg/default-language locale)
         code
         (t cfg/default-language code))
       (if (array? value)
         (aget value 0)
         value))))
  ([locale code & args]
   (let [code   (name code)
         value  (gobj/getValueByKeys translations code locale)]
     (if (empty-string? value)
       (if (= cfg/default-language locale)
         code
         (apply t cfg/default-language code args))
       (let [plural (first (filter c? args))
             value  (if (array? value)
                      (if (= @plural 1) (aget value 0) (aget value 1))
                      value)]
         (apply str/fmt value (map #(if (c? %) @% %) args)))))))

(defn tr
  ([code] (t @locale code))
  ([code & args] (apply t @locale code args)))

;; DEPRECATED
(defn use-locale
  []
  (mf/deref locale))

