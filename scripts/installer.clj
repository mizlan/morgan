#!/usr/bin/env bb

;; TODO: write an automation script for the daemon
(require '[babashka.fs :as fs])

(let [opts {:replace-existing true}]
  (fs/copy "./man_shim.clj" "/usr/local/bin/m" opts)
  (fs/copy "./updater.clj" "/usr/local/bin/morgan-update" opts))
