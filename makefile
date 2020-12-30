.ONESHELL: # Only applies to all target
textpdf:
	cd public
	pandoc -o ../snb_text.pdf -V geometry:margin=3cm ../src/snb/presentation.md
	evince ../snb_text.pdf


kafka:
	clojure -M:db:kafka:web-backend:repl
