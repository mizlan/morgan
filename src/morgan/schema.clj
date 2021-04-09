(ns morgan.schema)

(def entry
  [{:db/ident :entry/program
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The name of the program"}
   {:db/ident :entry/elapsed
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/many
    :db/doc "The time elapsed in milliseconds during for a read"}
   {:db/ident :entry/finish
    :db/valueType :db.type/instant
    :db/cardinality :db.cardinality/many
    :db/doc "The end time of a read"}])
