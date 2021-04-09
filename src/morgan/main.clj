(ns morgan.main
  (:require [datomic.client.api :as d]
            [morgan.schema :as schema]
            [morgan.example :as ex]))

(def client (d/client {:server-type :dev-local
                       :system "morgan"}))

(comment
  (d/delete-database client {:db-name "man"})
  (d/create-database client {:db-name "man"})
  )

(def conn (d/connect client {:db-name "man"}))

(comment
  (def db (d/db conn))
  (d/transact conn {:tx-data schema/entry})
  (d/transact conn {:tx-data ex/data})
  )

(defn get-db-data [conn]
  (let [db (d/db conn)]
    (->> (d/q '[:find ?name ?time ?elapsed
                :keys program endtime elapsed
                :where
                [?e :entry/program ?name]
                [?e :entry/finish ?time]
                [?e :entry/elapsed ?elapsed]]
              db)
         (sort-by :endtime))))

(defn get-most-recent [conn n]
  (->> (get-db-data conn)
       reverse
       (take n)))

(defn add-entry [conn {:keys [program elapsed finish]}]
  (d/transact
   conn
   {:tx-data
    [{:entry/program program
      :entry/elapsed elapsed
      :entry/finish finish}]}))

(defn current-time []
  (java.util.Date.))

(defn format-date [date]
  (.format (java.text.SimpleDateFormat. "hh:mma MM/dd") date))

(defn format-elapsed [ms]
  (let [secs (quot ms 1000)]
    (format "%ds" secs)))

(defn format-entry [{:keys [program elapsed endtime]}]
  (format "| `%s` | %s | %s |" program (format-elapsed elapsed) (format-date endtime)))

(comment
  (get-most-recent conn 2)
  (format-date (current-time))
  (add-entry conn {:program "emacs" :elapsed 234829342 :finish (current-time)})
  )
