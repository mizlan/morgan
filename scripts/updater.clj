#!/usr/bin/env bb

(require '[bencode.core :as b])

(def *port* (Integer/parseInt (slurp "/Users/michaellan/code/morgan/.nrepl-port")))

(def base (slurp "/Users/michaellan/code/morgan/scripts/base.md"))
(def gh-repo-path "/Users/michaellan/code/mizlan/")
(def output-path (str gh-repo-path "/README.md"))

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


(let [ranks (morgan-eval "(map format-entry (get-most-recent conn 5))")]
  (spit output-path
        (str
         base
         "\n"
         "| program | elapsed | finished |\n"
         "| :---    | :---    | :---     |\n"
         (str/join "\n" (read-string ranks)))))

(shell/with-sh-dir gh-repo-path
  (shell/sh "bash" "-c" "git add README.md")
  (shell/sh "bash" "-c" "git commit -m'update'")
  (shell/sh "bash" "-c" "git push"))
