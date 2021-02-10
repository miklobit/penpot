;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.
;;
;; This Source Code Form is "Incompatible With Secondary Licenses", as
;; defined by the Mozilla Public License, v. 2.0.
;;
;; Copyright (c) 2020 UXBOX Labs SL

(ns app.config
  "A configuration management."
  (:refer-clojure :exclude [get])
  (:require
   [clojure.core :as c]
   [app.common.spec :as us]
   [app.common.version :as v]
   [app.util.time :as dt]
   [clojure.spec.alpha :as s]
   [cuerdas.core :as str]
   [environ.core :refer [env]]))

(def defaults
  {:http-server-port 6060

   :database-uri "postgresql://127.0.0.1/penpot"
   :database-username "penpot"
   :database-password "penpot"

   :default-blob-version 1

   :asserts-enabled false

   :public-uri "http://localhost:3449"
   :redis-uri "redis://localhost/0"

   :srepl-host "127.0.0.1"
   :srepl-port 6062

   :storage-backend :fs

   :storage-fs-directory "resources/public/assets"
   :storage-s3-region :eu-central-1
   :storage-s3-bucket "penpot-devenv-assets-pre"

   :feedback-destination "info@example.com"
   :feedback-enabled false

   :assets-path "/internal/assets/"

   :rlimits-password 10
   :rlimits-image 2

   :smtp-enabled false
   :smtp-default-reply-to "Penpot <no-reply@example.com>"
   :smtp-default-from "Penpot <no-reply@example.com>"

   :profile-complaint-threshold 2 ; in a week
   :profile-bounce-threshold 10   ; in a week

   :allow-demo-users true
   :registration-enabled true
   :registration-domain-whitelist ""

   :telemetry-enabled false
   :telemetry-uri "https://telemetry.penpot.app/"

   ;; LDAP auth disabled by default. Set ldap-auth-host to enable
   ;:ldap-auth-host "ldap.mysupercompany.com"
   ;:ldap-auth-port 389
   ;:ldap-bind-dn "cn=admin,dc=ldap,dc=mysupercompany,dc=com"
   ;:ldap-bind-password "verysecure"
   ;:ldap-auth-ssl false
   ;:ldap-auth-starttls false
   ;:ldap-auth-base-dn "ou=People,dc=ldap,dc=mysupercompany,dc=com"

   :ldap-auth-user-query "(|(uid=$username)(mail=$username))"
   :ldap-auth-username-attribute "uid"
   :ldap-auth-email-attribute "mail"
   :ldap-auth-fullname-attribute "displayName"
   :ldap-auth-avatar-attribute "jpegPhoto"

   ;; :initial-data-file "resources/initial-data.json"
   ;; :initial-data-project-name "Penpot Oboarding"
   })

(s/def ::http-server-port ::us/integer)
(s/def ::database-username (s/nilable ::us/string))
(s/def ::database-password (s/nilable ::us/string))
(s/def ::database-uri ::us/string)
(s/def ::redis-uri ::us/string)


(s/def ::storage-backend ::us/keyword)
(s/def ::storage-fs-directory ::us/string)
(s/def ::assets-path ::us/string)
(s/def ::storage-s3-region ::us/keyword)
(s/def ::storage-s3-bucket ::us/string)

(s/def ::media-uri ::us/string)
(s/def ::media-directory ::us/string)
(s/def ::asserts-enabled ::us/boolean)

(s/def ::feedback-enabled ::us/boolean)
(s/def ::feedback-destination ::us/string)

(s/def ::profile-complaint-threshold ::us/integer)
(s/def ::profile-bounce-threshold ::us/integer)

(s/def ::error-report-webhook ::us/string)

(s/def ::smtp-enabled ::us/boolean)
(s/def ::smtp-default-reply-to ::us/string)
(s/def ::smtp-default-from ::us/string)
(s/def ::smtp-host ::us/string)
(s/def ::smtp-port ::us/integer)
(s/def ::smtp-username (s/nilable ::us/string))
(s/def ::smtp-password (s/nilable ::us/string))
(s/def ::smtp-tls ::us/boolean)
(s/def ::smtp-ssl ::us/boolean)
(s/def ::allow-demo-users ::us/boolean)
(s/def ::registration-enabled ::us/boolean)
(s/def ::registration-domain-whitelist ::us/string)
(s/def ::public-uri ::us/string)

(s/def ::srepl-host ::us/string)
(s/def ::srepl-port ::us/integer)

(s/def ::rlimits-password ::us/integer)
(s/def ::rlimits-image ::us/integer)

(s/def ::google-client-id ::us/string)
(s/def ::google-client-secret ::us/string)

(s/def ::gitlab-client-id ::us/string)
(s/def ::gitlab-client-secret ::us/string)
(s/def ::gitlab-base-uri ::us/string)

(s/def ::github-client-id ::us/string)
(s/def ::github-client-secret ::us/string)

(s/def ::ldap-auth-host ::us/string)
(s/def ::ldap-auth-port ::us/integer)
(s/def ::ldap-bind-dn ::us/string)
(s/def ::ldap-bind-password ::us/string)
(s/def ::ldap-auth-ssl ::us/boolean)
(s/def ::ldap-auth-starttls ::us/boolean)
(s/def ::ldap-auth-base-dn ::us/string)
(s/def ::ldap-auth-user-query ::us/string)
(s/def ::ldap-auth-username-attribute ::us/string)
(s/def ::ldap-auth-email-attribute ::us/string)
(s/def ::ldap-auth-fullname-attribute ::us/string)
(s/def ::ldap-auth-avatar-attribute ::us/string)

(s/def ::telemetry-enabled ::us/boolean)
(s/def ::telemetry-with-taiga ::us/boolean)
(s/def ::telemetry-uri ::us/string)
(s/def ::telemetry-server-enabled ::us/boolean)
(s/def ::telemetry-server-port ::us/integer)

(s/def ::initial-data-file ::us/string)
(s/def ::initial-data-project-name ::us/string)

(s/def ::default-blob-version ::us/integer)

(s/def ::config
  (s/keys :opt-un [::allow-demo-users
                   ::asserts-enabled
                   ::database-password
                   ::database-uri
                   ::database-username
                   ::default-blob-version
                   ::error-report-webhook
                   ::feedback-enabled
                   ::feedback-destination
                   ::github-client-id
                   ::github-client-secret
                   ::gitlab-base-uri
                   ::gitlab-client-id
                   ::gitlab-client-secret
                   ::google-client-id
                   ::google-client-secret
                   ::http-server-port
                   ::ldap-auth-avatar-attribute
                   ::ldap-auth-base-dn
                   ::ldap-auth-email-attribute
                   ::ldap-auth-fullname-attribute
                   ::ldap-auth-host
                   ::ldap-auth-port
                   ::ldap-auth-ssl
                   ::ldap-auth-starttls
                   ::ldap-auth-user-query
                   ::ldap-auth-username-attribute
                   ::ldap-bind-dn
                   ::ldap-bind-password
                   ::public-uri
                   ::profile-complaint-threshold
                   ::profile-bounce-threshold
                   ::redis-uri
                   ::registration-domain-whitelist
                   ::registration-enabled
                   ::rlimits-password
                   ::rlimits-image
                   ::smtp-default-from
                   ::smtp-default-reply-to
                   ::smtp-enabled
                   ::smtp-host
                   ::smtp-password
                   ::smtp-port
                   ::smtp-ssl
                   ::smtp-tls
                   ::smtp-username
                   ::storage-backend
                   ::storage-fs-directory
                   ::srepl-host
                   ::srepl-port
                   ::local-assets-uri
                   ::storage-s3-bucket
                   ::storage-s3-region
                   ::telemetry-enabled
                   ::telemetry-with-taiga
                   ::telemetry-server-enabled
                   ::telemetry-server-port
                   ::telemetry-uri
                   ::initial-data-file
                   ::initial-data-project-name]))

(defn- env->config
  [env]
  (reduce-kv
   (fn [acc k v]
     (cond-> acc
       (str/starts-with? (name k) "penpot-")
       (assoc (keyword (subs (name k) 7)) v)

       (str/starts-with? (name k) "app-")
       (assoc (keyword (subs (name k) 4)) v)))
   {}
   env))

(defn- read-config
  [env]
  (->> (env->config env)
       (merge defaults)
       (us/conform ::config)))

(defn- read-test-config
  [env]
  (merge {:redis-uri "redis://redis/1"
          :database-uri "postgresql://postgres/penpot_test"
          :storage-fs-directory "/tmp/app/storage"
          :migrations-verbose false}
         (read-config env)))

(def version (v/parse "%version%"))
(def config (read-config env))
(def test-config (read-test-config env))

(def default-deletion-delay
  (dt/duration {:hours 48}))

(defn get
  "A configuration getter. Helps code be more testable."
  ([key]
   (c/get config key))
  ([key default]
   (c/get config key default)))
