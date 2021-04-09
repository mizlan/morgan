#!/usr/bin/env bb

(require '[babashka.fs :as fs])

(fs/copy "./man_shim.clj" "/usr/local/bin/m")
(fs/copy "./updater.clj" "/usr/local/bin/morgan-update")
