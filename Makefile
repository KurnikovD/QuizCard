DT = $(shell date "+%Y-%m-%d_%H-%M-%s")
BUILD_SRC = build/src_$(DT)

.load_xlsx:
	poetry run python gs.py

build: .load_xlsx
	rm -f builds/dr-quiz.zip
	cd content && zip -x */.DS_Store -x **/*/.DS_Store -r ../builds/dr-quiz.zip .

.PHONY: build