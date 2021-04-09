#!/usr/bin/env bb

(ns man-shim
  (:require [bencode.core :as b]))

;; TODO: have a global file for manpages
(def *port* 55901)

(defn interactive-prog [args]
  (let [process-builder (java.lang.ProcessBuilder. args)
        inherit (java.lang.ProcessBuilder$Redirect/INHERIT)]
    (.redirectOutput process-builder inherit)
    (.redirectError process-builder inherit)
    (.redirectInput process-builder inherit)
    (.waitFor (.start process-builder))))

(defn get-stats [args]
  (let [start-time (java.util.Date.)]
    ;; TODO: catch an error, or malformed input
    (interactive-prog args)
    (let [end-time (java.util.Date.)
          elapsed (- (.getTime end-time) (.getTime start-time))]
      {:finish end-time
       :elapsed elapsed})))

(defn run [query]
  (let [s (get-stats ["man" query])]
    ;; HACK: use 5000 as a way to check if it was not a real
    ;;       manpage
    (if (< (:elapsed s) 5000)
      nil
      (assoc s :program query))))

;; adapted from https://book.babashka.org/#_interacting_with_an_nrepl_server
(defn nrepl-eval [port expr ns]
  (let [s (java.net.Socket. "localhost" port)
        out (.getOutputStream s)
        in (java.io.PushbackInputStream. (.getInputStream s))
        _ (b/write-bencode out {"op" "eval" "code" expr "ns" ns})
        stuff (b/read-bencode in)
        bytes (get stuff "value")]
    (String. bytes)))

(defn morgan-eval [expr]
  (nrepl-eval *port* expr "morgan.main"))

(some->> *command-line-args*
         first
         run
         (conj '[add-entry conn])
         (apply list)
         pr-str
         morgan-eval)
