# lm.arpa is built on the first 1,000 lines of Europarl

java -Djava.library.path=$JOSHUA/lib -cp ../class joshua.phrase.decode.Decode
