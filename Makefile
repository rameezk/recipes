.PHONY: build

build:
	clojure -M:build

repl:
	clojure -M:nREPL -m nrepl.cmdline
